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
 * <p>Classe Java pour variantRecordEncodingEnumerator.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="variantRecordEncodingEnumerator">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="HLAvariantRecord"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "variantRecordEncodingEnumerator")
@XmlEnum
public enum VariantRecordEncodingEnumerator {

    @XmlEnumValue("HLAvariantRecord")
    HL_AVARIANT_RECORD("HLAvariantRecord");
    private final java.lang.String value;

    VariantRecordEncodingEnumerator(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static VariantRecordEncodingEnumerator fromValue(java.lang.String v) {
        for (VariantRecordEncodingEnumerator c: VariantRecordEncodingEnumerator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
