//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2016.05.30 � 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour glyphTypeEnumerations.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="glyphTypeEnumerations">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="BITMAP"/>
 *     &lt;enumeration value="JPG"/>
 *     &lt;enumeration value="GIF"/>
 *     &lt;enumeration value="PNG"/>
 *     &lt;enumeration value="TIFF"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "glyphTypeEnumerations")
@XmlEnum
public enum GlyphTypeEnumerations {

    BITMAP,
    JPG,
    GIF,
    PNG,
    TIFF;

    public java.lang.String value() {
        return name();
    }

    public static GlyphTypeEnumerations fromValue(java.lang.String v) {
        return valueOf(v);
    }

}
