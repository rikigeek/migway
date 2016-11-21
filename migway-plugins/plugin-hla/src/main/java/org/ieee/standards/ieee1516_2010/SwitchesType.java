//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2016.05.30 � 11:09:47 AM CEST 
//


package org.ieee.standards.ieee1516_2010;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour switchesType complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="switchesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="autoProvide" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="conveyRegionDesignatorSets" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="conveyProducingFederate" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="attributeScopeAdvisory" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="attributeRelevanceAdvisory" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="objectClassRelevanceAdvisory" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="interactionRelevanceAdvisory" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="serviceReporting" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="exceptionReporting" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="delaySubscriptionEvaluation" type="{http://standards.ieee.org/IEEE1516-2010}switchType" minOccurs="0"/>
 *         &lt;element name="automaticResignAction" type="{http://standards.ieee.org/IEEE1516-2010}resignSwitchType" minOccurs="0"/>
 *         &lt;any namespace='##other' minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *       &lt;anyAttribute namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "switchesType", propOrder = {
    "autoProvide",
    "conveyRegionDesignatorSets",
    "conveyProducingFederate",
    "attributeScopeAdvisory",
    "attributeRelevanceAdvisory",
    "objectClassRelevanceAdvisory",
    "interactionRelevanceAdvisory",
    "serviceReporting",
    "exceptionReporting",
    "delaySubscriptionEvaluation",
    "automaticResignAction",
    "any"
})
public class SwitchesType {

    protected SwitchType autoProvide;
    protected SwitchType conveyRegionDesignatorSets;
    protected SwitchType conveyProducingFederate;
    protected SwitchType attributeScopeAdvisory;
    protected SwitchType attributeRelevanceAdvisory;
    protected SwitchType objectClassRelevanceAdvisory;
    protected SwitchType interactionRelevanceAdvisory;
    protected SwitchType serviceReporting;
    protected SwitchType exceptionReporting;
    protected SwitchType delaySubscriptionEvaluation;
    protected ResignSwitchType automaticResignAction;
    @XmlAnyElement(lax = true)
    protected Object any;
    @XmlAttribute(name = "notes")
    @XmlIDREF
    @XmlSchemaType(name = "IDREFS")
    protected List<Object> attrNotes;
    @XmlAttribute(name = "idtag")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected java.lang.String idtag;
    @XmlAnyAttribute
    private Map<QName, java.lang.String> otherAttributes = new HashMap<QName, java.lang.String>();

    /**
     * Obtient la valeur de la propri�t� autoProvide.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getAutoProvide() {
        return autoProvide;
    }

    /**
     * D�finit la valeur de la propri�t� autoProvide.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setAutoProvide(SwitchType value) {
        this.autoProvide = value;
    }

    /**
     * Obtient la valeur de la propri�t� conveyRegionDesignatorSets.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getConveyRegionDesignatorSets() {
        return conveyRegionDesignatorSets;
    }

    /**
     * D�finit la valeur de la propri�t� conveyRegionDesignatorSets.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setConveyRegionDesignatorSets(SwitchType value) {
        this.conveyRegionDesignatorSets = value;
    }

    /**
     * Obtient la valeur de la propri�t� conveyProducingFederate.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getConveyProducingFederate() {
        return conveyProducingFederate;
    }

    /**
     * D�finit la valeur de la propri�t� conveyProducingFederate.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setConveyProducingFederate(SwitchType value) {
        this.conveyProducingFederate = value;
    }

    /**
     * Obtient la valeur de la propri�t� attributeScopeAdvisory.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getAttributeScopeAdvisory() {
        return attributeScopeAdvisory;
    }

    /**
     * D�finit la valeur de la propri�t� attributeScopeAdvisory.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setAttributeScopeAdvisory(SwitchType value) {
        this.attributeScopeAdvisory = value;
    }

    /**
     * Obtient la valeur de la propri�t� attributeRelevanceAdvisory.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getAttributeRelevanceAdvisory() {
        return attributeRelevanceAdvisory;
    }

    /**
     * D�finit la valeur de la propri�t� attributeRelevanceAdvisory.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setAttributeRelevanceAdvisory(SwitchType value) {
        this.attributeRelevanceAdvisory = value;
    }

    /**
     * Obtient la valeur de la propri�t� objectClassRelevanceAdvisory.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getObjectClassRelevanceAdvisory() {
        return objectClassRelevanceAdvisory;
    }

    /**
     * D�finit la valeur de la propri�t� objectClassRelevanceAdvisory.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setObjectClassRelevanceAdvisory(SwitchType value) {
        this.objectClassRelevanceAdvisory = value;
    }

    /**
     * Obtient la valeur de la propri�t� interactionRelevanceAdvisory.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getInteractionRelevanceAdvisory() {
        return interactionRelevanceAdvisory;
    }

    /**
     * D�finit la valeur de la propri�t� interactionRelevanceAdvisory.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setInteractionRelevanceAdvisory(SwitchType value) {
        this.interactionRelevanceAdvisory = value;
    }

    /**
     * Obtient la valeur de la propri�t� serviceReporting.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getServiceReporting() {
        return serviceReporting;
    }

    /**
     * D�finit la valeur de la propri�t� serviceReporting.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setServiceReporting(SwitchType value) {
        this.serviceReporting = value;
    }

    /**
     * Obtient la valeur de la propri�t� exceptionReporting.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getExceptionReporting() {
        return exceptionReporting;
    }

    /**
     * D�finit la valeur de la propri�t� exceptionReporting.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setExceptionReporting(SwitchType value) {
        this.exceptionReporting = value;
    }

    /**
     * Obtient la valeur de la propri�t� delaySubscriptionEvaluation.
     * 
     * @return
     *     possible object is
     *     {@link SwitchType }
     *     
     */
    public SwitchType getDelaySubscriptionEvaluation() {
        return delaySubscriptionEvaluation;
    }

    /**
     * D�finit la valeur de la propri�t� delaySubscriptionEvaluation.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchType }
     *     
     */
    public void setDelaySubscriptionEvaluation(SwitchType value) {
        this.delaySubscriptionEvaluation = value;
    }

    /**
     * Obtient la valeur de la propri�t� automaticResignAction.
     * 
     * @return
     *     possible object is
     *     {@link ResignSwitchType }
     *     
     */
    public ResignSwitchType getAutomaticResignAction() {
        return automaticResignAction;
    }

    /**
     * D�finit la valeur de la propri�t� automaticResignAction.
     * 
     * @param value
     *     allowed object is
     *     {@link ResignSwitchType }
     *     
     */
    public void setAutomaticResignAction(ResignSwitchType value) {
        this.automaticResignAction = value;
    }

    /**
     * Obtient la valeur de la propri�t� any.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getAny() {
        return any;
    }

    /**
     * D�finit la valeur de la propri�t� any.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setAny(Object value) {
        this.any = value;
    }

    /**
     * Gets the value of the attrNotes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attrNotes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttrNotes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getAttrNotes() {
        if (attrNotes == null) {
            attrNotes = new ArrayList<Object>();
        }
        return this.attrNotes;
    }

    /**
     * Obtient la valeur de la propri�t� idtag.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getIdtag() {
        return idtag;
    }

    /**
     * D�finit la valeur de la propri�t� idtag.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setIdtag(java.lang.String value) {
        this.idtag = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, java.lang.String> getOtherAttributes() {
        return otherAttributes;
    }

}
