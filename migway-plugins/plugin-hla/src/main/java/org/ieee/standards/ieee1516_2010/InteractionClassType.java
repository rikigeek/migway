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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour interactionClassType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="interactionClassType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://standards.ieee.org/IEEE1516-2010}IdentifierType"/>
 *         &lt;element name="sharing" type="{http://standards.ieee.org/IEEE1516-2010}sharingType" minOccurs="0"/>
 *         &lt;element name="dimensions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dimension" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;any namespace='##other' minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="transportation" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" minOccurs="0"/>
 *         &lt;element name="order" type="{http://standards.ieee.org/IEEE1516-2010}orderType" minOccurs="0"/>
 *         &lt;element name="semantics" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element ref="{http://standards.ieee.org/IEEE1516-2010}parameter" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://standards.ieee.org/IEEE1516-2010}interactionClass" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "interactionClassType", propOrder = {
    "name",
    "sharing",
    "dimensions",
    "transportation",
    "order",
    "semantics",
    "parameter",
    "interactionClass",
    "any"
})
@XmlSeeAlso({
    InteractionClass.class
})
public class InteractionClassType {

    @XmlElement(required = true)
    protected IdentifierType name;
    protected SharingType sharing;
    protected InteractionClassType.Dimensions dimensions;
    protected ReferenceType transportation;
    protected OrderType order;
    protected org.ieee.standards.ieee1516_2010.String semantics;
    protected List<Parameter> parameter;
    protected List<InteractionClass> interactionClass;
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
     * Obtient la valeur de la propriété name.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setName(IdentifierType value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété sharing.
     * 
     * @return
     *     possible object is
     *     {@link SharingType }
     *     
     */
    public SharingType getSharing() {
        return sharing;
    }

    /**
     * Définit la valeur de la propriété sharing.
     * 
     * @param value
     *     allowed object is
     *     {@link SharingType }
     *     
     */
    public void setSharing(SharingType value) {
        this.sharing = value;
    }

    /**
     * Obtient la valeur de la propriété dimensions.
     * 
     * @return
     *     possible object is
     *     {@link InteractionClassType.Dimensions }
     *     
     */
    public InteractionClassType.Dimensions getDimensions() {
        return dimensions;
    }

    /**
     * Définit la valeur de la propriété dimensions.
     * 
     * @param value
     *     allowed object is
     *     {@link InteractionClassType.Dimensions }
     *     
     */
    public void setDimensions(InteractionClassType.Dimensions value) {
        this.dimensions = value;
    }

    /**
     * Obtient la valeur de la propriété transportation.
     * 
     * @return
     *     possible object is
     *     {@link ReferenceType }
     *     
     */
    public ReferenceType getTransportation() {
        return transportation;
    }

    /**
     * Définit la valeur de la propriété transportation.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenceType }
     *     
     */
    public void setTransportation(ReferenceType value) {
        this.transportation = value;
    }

    /**
     * Obtient la valeur de la propriété order.
     * 
     * @return
     *     possible object is
     *     {@link OrderType }
     *     
     */
    public OrderType getOrder() {
        return order;
    }

    /**
     * Définit la valeur de la propriété order.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderType }
     *     
     */
    public void setOrder(OrderType value) {
        this.order = value;
    }

    /**
     * Obtient la valeur de la propriété semantics.
     * 
     * @return
     *     possible object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public org.ieee.standards.ieee1516_2010.String getSemantics() {
        return semantics;
    }

    /**
     * Définit la valeur de la propriété semantics.
     * 
     * @param value
     *     allowed object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public void setSemantics(org.ieee.standards.ieee1516_2010.String value) {
        this.semantics = value;
    }

    /**
     * Gets the value of the parameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Parameter }
     * 
     * 
     */
    public List<Parameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<Parameter>();
        }
        return this.parameter;
    }

    /**
     * Gets the value of the interactionClass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the interactionClass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInteractionClass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InteractionClass }
     * 
     * 
     */
    public List<InteractionClass> getInteractionClass() {
        if (interactionClass == null) {
            interactionClass = new ArrayList<InteractionClass>();
        }
        return this.interactionClass;
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


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="dimension" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" maxOccurs="unbounded" minOccurs="0"/>
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
    @XmlType(name = "", propOrder = {
        "dimension",
        "any"
    })
    public static class Dimensions {

        protected List<ReferenceType> dimension;
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
         * Gets the value of the dimension property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dimension property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDimension().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ReferenceType }
         * 
         * 
         */
        public List<ReferenceType> getDimension() {
            if (dimension == null) {
                dimension = new ArrayList<ReferenceType>();
            }
            return this.dimension;
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

}
