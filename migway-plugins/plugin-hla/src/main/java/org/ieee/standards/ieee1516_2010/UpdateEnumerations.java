//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2016.05.30 � 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour updateEnumerations.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="updateEnumerations">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Static"/>
 *     &lt;enumeration value="Periodic"/>
 *     &lt;enumeration value="Conditional"/>
 *     &lt;enumeration value="NA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "updateEnumerations")
@XmlEnum
public enum UpdateEnumerations {

    @XmlEnumValue("Static")
    STATIC("Static"),
    @XmlEnumValue("Periodic")
    PERIODIC("Periodic"),
    @XmlEnumValue("Conditional")
    CONDITIONAL("Conditional"),
    NA("NA");
    private final java.lang.String value;

    UpdateEnumerations(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static UpdateEnumerations fromValue(java.lang.String v) {
        for (UpdateEnumerations c: UpdateEnumerations.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
