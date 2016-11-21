//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.05.30 à 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour OMTypeEnumerations.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="OMTypeEnumerations">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FOM"/>
 *     &lt;enumeration value="SOM"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OMTypeEnumerations")
@XmlEnum
public enum OMTypeEnumerations {

    FOM,
    SOM;

    public java.lang.String value() {
        return name();
    }

    public static OMTypeEnumerations fromValue(java.lang.String v) {
        return valueOf(v);
    }

}
