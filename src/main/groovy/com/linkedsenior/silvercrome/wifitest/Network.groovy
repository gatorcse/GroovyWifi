package com.linkedsenior.silvercrome.wifitest

class Network {
    String SSID
    WLANNetworkType networkType
    WLANAuthType security
    WLANEncryptionType encryption

    Network(String SSID, WLANNetworkType networkType, WLANAuthType security, WLANEncryptionType encryption) {
        this.SSID = SSID
        this.networkType = networkType
        this.security = security
        this.encryption = encryption
    }

    Network(String SSID, String networkType, String security, String encryption) {
        this.SSID = SSID

        switch (networkType) {
            case "Infrastructure": this.networkType = WLANNetworkType.ESS; break
            case "Adhoc": this.networkType = WLANNetworkType.IBSS; break
        }

        switch (security) {
            case "WPA-Personal": this.security = WLANAuthType.WPAPSK; break
            case "WPA-Enterprise": this.security = WLANAuthType.WPA; break
            case "WPA2-Personal": this.security = WLANAuthType.WPA2PSK; break
            case "WPA2-Enterprise": this.security = WLANAuthType.WPA2; break
            default: this.security = security.toLowerCase() as WLANAuthType
        }

        switch (encryption) {
            case "None": this.encryption = WLANEncryptionType.none; break
            case "CCMP": this.encryption = WLANEncryptionType.AES; break
            case { String it -> it.startsWith("WEP") }: this.encryption = WLANEncryptionType.WEP; break
            default: this.encryption = encryption as WLANEncryptionType
        }
    }
}

public enum WLANNetworkType {
    ESS, IBSS
}

public enum WLANAuthType {
    open, shared, WPA, WPAPSK, WPA2, WPA2PSK
}

public enum WLANEncryptionType {
    none, WEP, TKIP, AES, CCMP
}
