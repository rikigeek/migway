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
 * <p>Classe Java pour POCTypeEnumeration.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="POCTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Primary author"/>
 *     &lt;enumeration value="Contributor"/>
 *     &lt;enumeration value="Proponent"/>
 *     &lt;enumeration value="Sponsor"/>
 *     &lt;enumeration value="Release authority"/>
 *     &lt;enumeration value="Technical POC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "POCTypeEnumeration")
@XmlEnum
public enum POCTypeEnumeration {

    @XmlEnumValue("Primary author")
    PRIMARY_AUTHOR("Primary author"),
    @XmlEnumValue("Contributor")
    CONTRIBUTOR("Contributor"),
    @XmlEnumValue("Proponent")
    PROPONENT("Proponent"),
    @XmlEnumValue("Sponsor")
    SPONSOR("Sponsor"),
    @XmlEnumValue("Release authority")
    RELEASE_AUTHORITY("Release authority"),
    @XmlEnumValue("Technical POC")
    TECHNICAL_POC("Technical POC");
    private final java.lang.String value;

    POCTypeEnumeration(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static POCTypeEnumeration fromValue(java.lang.String v) {
        for (POCTypeEnumeration c: POCTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
