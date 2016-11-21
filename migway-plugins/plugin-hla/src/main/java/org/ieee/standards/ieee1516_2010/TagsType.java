//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.05.30 à 11:09:47 AM CEST 
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
 * <p>Classe Java pour tagsType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="tagsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="updateReflectTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="sendReceiveTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="deleteRemoveTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="divestitureRequestTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="divestitureCompletionTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="acquisitionRequestTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
 *         &lt;element name="requestUpdateTag" type="{http://standards.ieee.org/IEEE1516-2010}tagType" minOccurs="0"/>
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
@XmlType(name = "tagsType", propOrder = {
    "updateReflectTag",
    "sendReceiveTag",
    "deleteRemoveTag",
    "divestitureRequestTag",
    "divestitureCompletionTag",
    "acquisitionRequestTag",
    "requestUpdateTag",
    "any"
})
public class TagsType {

    protected TagType updateReflectTag;
    protected TagType sendReceiveTag;
    protected TagType deleteRemoveTag;
    protected TagType divestitureRequestTag;
    protected TagType divestitureCompletionTag;
    protected TagType acquisitionRequestTag;
    protected TagType requestUpdateTag;
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
     * Obtient la valeur de la propriété updateReflectTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getUpdateReflectTag() {
        return updateReflectTag;
    }

    /**
     * Définit la valeur de la propriété updateReflectTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setUpdateReflectTag(TagType value) {
        this.updateReflectTag = value;
    }

    /**
     * Obtient la valeur de la propriété sendReceiveTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getSendReceiveTag() {
        return sendReceiveTag;
    }

    /**
     * Définit la valeur de la propriété sendReceiveTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setSendReceiveTag(TagType value) {
        this.sendReceiveTag = value;
    }

    /**
     * Obtient la valeur de la propriété deleteRemoveTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getDeleteRemoveTag() {
        return deleteRemoveTag;
    }

    /**
     * Définit la valeur de la propriété deleteRemoveTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setDeleteRemoveTag(TagType value) {
        this.deleteRemoveTag = value;
    }

    /**
     * Obtient la valeur de la propriété divestitureRequestTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getDivestitureRequestTag() {
        return divestitureRequestTag;
    }

    /**
     * Définit la valeur de la propriété divestitureRequestTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setDivestitureRequestTag(TagType value) {
        this.divestitureRequestTag = value;
    }

    /**
     * Obtient la valeur de la propriété divestitureCompletionTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getDivestitureCompletionTag() {
        return divestitureCompletionTag;
    }

    /**
     * Définit la valeur de la propriété divestitureCompletionTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setDivestitureCompletionTag(TagType value) {
        this.divestitureCompletionTag = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionRequestTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getAcquisitionRequestTag() {
        return acquisitionRequestTag;
    }

    /**
     * Définit la valeur de la propriété acquisitionRequestTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setAcquisitionRequestTag(TagType value) {
        this.acquisitionRequestTag = value;
    }

    /**
     * Obtient la valeur de la propriété requestUpdateTag.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getRequestUpdateTag() {
        return requestUpdateTag;
    }

    /**
     * Définit la valeur de la propriété requestUpdateTag.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setRequestUpdateTag(TagType value) {
        this.requestUpdateTag = value;
    }

    /**
     * Obtient la valeur de la propriété any.
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
     * Définit la valeur de la propriété any.
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
     * Obtient la valeur de la propriété idtag.
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
     * Définit la valeur de la propriété idtag.
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
