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
 * <p>Classe Java pour SecurityClassificationEnumeration.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="SecurityClassificationEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unclassified"/>
 *     &lt;enumeration value="Confidential"/>
 *     &lt;enumeration value="Secret"/>
 *     &lt;enumeration value="Top Secret"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SecurityClassificationEnumeration")
@XmlEnum
public enum SecurityClassificationEnumeration {

    @XmlEnumValue("Unclassified")
    UNCLASSIFIED("Unclassified"),
    @XmlEnumValue("Confidential")
    CONFIDENTIAL("Confidential"),
    @XmlEnumValue("Secret")
    SECRET("Secret"),
    @XmlEnumValue("Top Secret")
    TOP_SECRET("Top Secret");
    private final java.lang.String value;

    SecurityClassificationEnumeration(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static SecurityClassificationEnumeration fromValue(java.lang.String v) {
        for (SecurityClassificationEnumeration c: SecurityClassificationEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
