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
 * <p>Classe Java pour resignActionType.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="resignActionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UnconditionallyDivestAttributes"/>
 *     &lt;enumeration value="DeleteObjects"/>
 *     &lt;enumeration value="CancelPendingOwnershipAcquisitions"/>
 *     &lt;enumeration value="DeleteObjectsThenDivest"/>
 *     &lt;enumeration value="CancelThenDeleteThenDivest"/>
 *     &lt;enumeration value="NoAction"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "resignActionType")
@XmlEnum
public enum ResignActionType {

    @XmlEnumValue("UnconditionallyDivestAttributes")
    UNCONDITIONALLY_DIVEST_ATTRIBUTES("UnconditionallyDivestAttributes"),
    @XmlEnumValue("DeleteObjects")
    DELETE_OBJECTS("DeleteObjects"),
    @XmlEnumValue("CancelPendingOwnershipAcquisitions")
    CANCEL_PENDING_OWNERSHIP_ACQUISITIONS("CancelPendingOwnershipAcquisitions"),
    @XmlEnumValue("DeleteObjectsThenDivest")
    DELETE_OBJECTS_THEN_DIVEST("DeleteObjectsThenDivest"),
    @XmlEnumValue("CancelThenDeleteThenDivest")
    CANCEL_THEN_DELETE_THEN_DIVEST("CancelThenDeleteThenDivest"),
    @XmlEnumValue("NoAction")
    NO_ACTION("NoAction");
    private final java.lang.String value;

    ResignActionType(java.lang.String v) {
        value = v;
    }

    public java.lang.String value() {
        return value;
    }

    public static ResignActionType fromValue(java.lang.String v) {
        for (ResignActionType c: ResignActionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
