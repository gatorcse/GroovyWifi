package com.linkedsenior.silvercrome.wifitest

class NetshManager {

    static final def NETWORK_PATTERN = /SSID \d+ : (.*)\n\W+Network type\W+:\W+(.*)\n\W*Authentication\W+:\W+(.*)\n\W+Encryption\W+:\W+(.*)/
    static final def PROFILES_PATTERN = /.*: (.*)/
    static  def KEY_PATTERN(key) { /\W+ $key \W+: (.*)/ }

    public static void main(String[] args) {
        extractFromTextWithRegex(Network, testNetworksData, NETWORK_PATTERN)
            .each { println("$it.SSID :: $it.networkType :: $it.security :: $it.encryption") }

        println('===')

        extractFromTextWithRegex(String, testProfilesData, PROFILES_PATTERN)
            .each { println(it) }

        println('===')

        extractFromTextWithRegex(String, testInterfacesData, KEY_PATTERN("Profile"))
            .each {println(it) }

        println('===')

        extractFromTextWithRegex(String, testProfile1776, KEY_PATTERN("SSID name"))
            .each {println(it.drop(1).reverse().drop(1).reverse())}

        def net = extractFromTextWithRegex(Network, testNetworksData, NETWORK_PATTERN)
            .find {it.SSID == "1776 Member WiFi"}

        mkProfile(net, "Franklin1776")
    }

    static def listNetworks() {
        extractFromTextWithRegex(
                Network,
                "netsh wlan show networks".execute().text,
                NETWORK_PATTERN)
    }

    static def listProfiles() {
        extractFromTextWithRegex(
                String,
                "netsh wlan show profiles".execute().text,
                PROFILES_PATTERN)
    }

    static def connected() {
        extractFromTextWithRegex(
                String,
                "netsh wlan show interfaces".execute().text,
                KEY_PATTERN("SSID")
        ).head()
    }

    static def connect(String profile) {
        def message = /netsh wlan connect name=\"$profile"/.execute().text
        if (message.contains("successfully")) {
            true
        } else {
            // TODO: Error logging
            false
        }
    }

    static def disconnect() {
        def message = "netsh wlan disconnect".execute().text
        if (message.contains("successfully")) {
            true
        } else {
            // TODO: Error logging
            false
        }
    }

    static def mkProfile(String ssid, String password = "") {
        def network = listNetworks().find {it.SSID == ssid}
        def xml = ProfileManager.buildProfile(network, password)
        File.createTempFile("profile", ".xml") with {
            deleteOnExit()
            write xml
            /netsh wlan add profile filename="$absolutePath"/.execute().waitForOrKill(2000)
            delete()
        }
        ssid
    }

    private static def profileInfo(String profile) {
        extractFromTextWithRegex(
                String,
                "netsh wlan show profiles ${profile}".execute().text,
                KEY_PATTERN("SSID name")
        ).head().drop(1).reverse().drop(1).reverse()
    }

    private static def <T> List<T> extractFromTextWithRegex(Class<T> clazz, String text, String pattern) {
        (text =~ pattern).collect {clazz.newInstance(it[1..it.size() - 1].toArray())}
    }

    private static String testNetworksData = """

Interface name : Wi-Fi
There are 8 networks currently visible.

SSID 1 : 1776 Member WiFi
    Network type            : Infrastructure
    Authentication          : WPA2-Personal
    Encryption              : CCMP

SSID 2 : 1776 Guest WiFi
    Network type            : Infrastructure
    Authentication          : WPA2-Personal
    Encryption              : CCMP

SSID 3 : HP7B9E43
    Network type            : Adhoc
    Authentication          : Open
    Encryption              : None

SSID 4 : FW's MacBook
    Network type            : Infrastructure
    Authentication          : Open
    Encryption              : None

SSID 5 :
    Network type            : Infrastructure
    Authentication          : WPA-Personal
    Encryption              : TKIP

SSID 6 : nfguest
    Network type            : Infrastructure
    Authentication          : WPA-Personal
    Encryption              : TKIP

SSID 7 : TheFort1
    Network type            : Infrastructure
    Authentication          : WPA2-Personal
    Encryption              : CCMP

SSID 8 : HP7B9E3D
    Network type            : Adhoc
    Authentication          : Open
    Encryption              : None


"""

    private static String testProfilesData = """
Profiles on interface Wireless Network Connection:

Group policy profiles (read only)
---------------------------------
    <None>

User profiles
-------------
    All User Profile     : 1776 Member WiFi

"""

    private static String testInterfacesData = """
There is 1 interface on the system:

    Name                   : Wireless Network Connection
    Description            : 802.11n Wireless LAN Card
    GUID                   : abfdb36f-7c9c-4e4d-8cba-55010aa31d6b
    Physical address       : 00:26:82:c0:8d:99
    State                  : connected
    SSID                   : 1776 Member WiFi
    BSSID                  : 08:ea:44:9b:dd:15
    Network type           : Infrastructure
    Radio type             : 802.11g
    Authentication         : WPA2-Personal
    Cipher                 : CCMP
    Connection mode        : Auto Connect
    Channel                : 11
    Receive rate (Mbps)    : 130
    Transmit rate (Mbps)   : 130
    Signal                 : 70%
    Profile                : 1776 Member WiFi

    Hosted network status  : Not started

"""

    private static String testProfile1776 = """
Profile 1776 Member WiFi on interface Wireless Network Connection:
=======================================================================

Applied: All User Profile

Profile information
-------------------
    Version                : 1
    Type                   : Wireless LAN
    Name                   : 1776 Member WiFi
    Control options        :
        Connection mode    : Connect automatically
        Network broadcast  : Connect only if this network is broadcasting
        AutoSwitch         : Do not switch to other networks

Connectivity settings
---------------------
    Number of SSIDs        : 1
    SSID name              : "1776 Member WiFi"
    Network type           : Infrastructure
    Radio type             : [ Any Radio Type ]
    Vendor extension          : Not present

Security settings
-----------------
    Authentication         : WPA2-Personal
    Cipher                 : CCMP
    Security key           : Present

"""
}
