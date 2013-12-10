package com.linkedsenior.silvercrome.wifitest

import groovy.xml.MarkupBuilder

import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class ProfileManager {
    static def buildProfile(Network network, String password) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.WLANProfile(xmlns: "http://www.microsoft.com/networking/WLAN/profile/v1") {
            name(network.SSID)
            SSIDConfig(){
                SSID() {
                    name(network.SSID)
                }
            }
            connectionType(network.networkType.toString())
            connectionMode('auto')
            autoSwitch('false')
            MSM() {
                security() {
                    authEncryption() {
                        authentication(network.security.toString())
                        encryption(network.encryption.toString())
                    }
                    if (network.security != WLANAuthType.open) {
                        sharedKey() {
                            if (network.encryption == WLANEncryptionType.WEP)
                                keyType('networkKey')
                            else
                                keyType('passPhrase')
                            'protected'('false')
                            keyMaterial(password)
                        }

                    }
                }
            }
        }

        writer.toString()
    }

    /**
     * Validates the xml file against Microsoft's xsd. Throws exception if verification fails. Not used currently since
     * it fails verification if the password is included in the xml file (Microsoft's .xsd needs some love).
     * @param xml
     */
    private static def validateXml(String xml){

        String xsd = "/profile.v1.xsd"

        def factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
        def schema = factory.newSchema(new StreamSource(System.getResourceAsStream(xsd)))
        def validator = schema.newValidator()
        validator.validate(new StreamSource(new StringReader(xml)))
    }
}
