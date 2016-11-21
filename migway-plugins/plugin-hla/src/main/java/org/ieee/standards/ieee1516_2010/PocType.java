//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.05.30 à 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour pocType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="pocType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pocType" type="{http://standards.ieee.org/IEEE1516-2010}pocTypeType" minOccurs="0"/>
 *         &lt;element name="pocName" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element name="pocOrg" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element name="pocTelephone" type="{http://standards.ieee.org/IEEE1516-2010}String" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pocEmail" type="{http://standards.ieee.org/IEEE1516-2010}String" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pocType", propOrder = {
    "pocType",
    "pocName",
    "pocOrg",
    "pocTelephone",
    "pocEmail"
})
@XmlSeeAlso({
    org.ieee.standards.ieee1516_2010.ModelIdentificationType.Poc.class
})
public class PocType {

    protected PocTypeType pocType;
    protected String pocName;
    protected String pocOrg;
    protected List<String> pocTelephone;
    protected List<String> pocEmail;

    /**
     * Obtient la valeur de la propriété pocType.
     * 
     * @return
     *     possible object is
     *     {@link PocTypeType }
     *     
     */
    public PocTypeType getPocType() {
        return pocType;
    }

    /**
     * Définit la valeur de la propriété pocType.
     * 
     * @param value
     *     allowed object is
     *     {@link PocTypeType }
     *     
     */
    public void setPocType(PocTypeType value) {
        this.pocType = value;
    }

    /**
     * Obtient la valeur de la propriété pocName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPocName() {
        return pocName;
    }

    /**
     * Définit la valeur de la propriété pocName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPocName(String value) {
        this.pocName = value;
    }

    /**
     * Obtient la valeur de la propriété pocOrg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPocOrg() {
        return pocOrg;
    }

    /**
     * Définit la valeur de la propriété pocOrg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPocOrg(String value) {
        this.pocOrg = value;
    }

    /**
     * Gets the value of the pocTelephone property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pocTelephone property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPocTelephone().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPocTelephone() {
        if (pocTelephone == null) {
            pocTelephone = new ArrayList<String>();
        }
        return this.pocTelephone;
    }

    /**
     * Gets the value of the pocEmail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pocEmail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPocEmail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPocEmail() {
        if (pocEmail == null) {
            pocEmail = new ArrayList<String>();
        }
        return this.pocEmail;
    }

}
