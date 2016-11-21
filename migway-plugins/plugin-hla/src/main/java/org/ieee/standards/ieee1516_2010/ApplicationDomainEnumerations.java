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
 * <p>Classe Java pour ApplicationDomainEnumerations.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="ApplicationDomainEnumerations">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Analysis"/>
 *     &lt;enumeration value="Training"/>
 *     &lt;enumeration value="Test and Evaluation"/>
 *     &lt;enumeration value="Engineering"/>
 *     &lt;enumeration value="Acquisition"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ApplicationDomainEnumerations")
@XmlEnum
public enum ApplicationDomainEnumerations {

    @XmlEnumValue("Analysis")
    ANALYSIS("Analysis"),
    @XmlEnumValue("Training")
    TRAINING("Training"),
    @XmlEnumValue("Test and Evaluation")
    TEST_AND_EVALUATION("Test and Evaluation"),
    @XmlEnumValue("Engineering")
    ENGINEERING("Engineering"),
    @XmlEnumValue("Acquisition")
    ACQUISITION("Acquisition");
    private final java.lang.String value;

    ApplicationDomainEnumerations(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static ApplicationDomainEnumerations fromValue(java.lang.String v) {
        for (ApplicationDomainEnumerations c: ApplicationDomainEnumerations.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
