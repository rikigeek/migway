//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.05.30 à 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour capabilityEnumerations.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="capabilityEnumerations">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Register"/>
 *     &lt;enumeration value="Achieve"/>
 *     &lt;enumeration value="RegisterAchieve"/>
 *     &lt;enumeration value="NoSynch"/>
 *     &lt;enumeration value="NA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "capabilityEnumerations")
@XmlEnum
public enum CapabilityEnumerations {

    @XmlEnumValue("Register")
    REGISTER("Register"),
    @XmlEnumValue("Achieve")
    ACHIEVE("Achieve"),
    @XmlEnumValue("RegisterAchieve")
    REGISTER_ACHIEVE("RegisterAchieve"),
    @XmlEnumValue("NoSynch")
    NO_SYNCH("NoSynch"),
    NA("NA");
    private final java.lang.String value;

    CapabilityEnumerations(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static CapabilityEnumerations fromValue(java.lang.String v) {
        for (CapabilityEnumerations c: CapabilityEnumerations.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
