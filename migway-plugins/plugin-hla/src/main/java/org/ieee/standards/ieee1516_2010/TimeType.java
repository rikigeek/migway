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
 * <p>Classe Java pour timeType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="timeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="timeStamp" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dataType" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" minOccurs="0"/>
 *                   &lt;element name="semantics" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *                   &lt;any namespace='##other' minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="lookahead" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="dataType" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" minOccurs="0"/>
 *                   &lt;element name="semantics" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *                   &lt;any namespace='##other' minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
@XmlType(name = "timeType", propOrder = {
    "timeStamp",
    "lookahead",
    "any"
})
public class TimeType {

    protected TimeType.TimeStamp timeStamp;
    protected TimeType.Lookahead lookahead;
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
     * Obtient la valeur de la propriété timeStamp.
     * 
     * @return
     *     possible object is
     *     {@link TimeType.TimeStamp }
     *     
     */
    public TimeType.TimeStamp getTimeStamp() {
        return timeStamp;
    }

    /**
     * Définit la valeur de la propriété timeStamp.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeType.TimeStamp }
     *     
     */
    public void setTimeStamp(TimeType.TimeStamp value) {
        this.timeStamp = value;
    }

    /**
     * Obtient la valeur de la propriété lookahead.
     * 
     * @return
     *     possible object is
     *     {@link TimeType.Lookahead }
     *     
     */
    public TimeType.Lookahead getLookahead() {
        return lookahead;
    }

    /**
     * Définit la valeur de la propriété lookahead.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeType.Lookahead }
     *     
     */
    public void setLookahead(TimeType.Lookahead value) {
        this.lookahead = value;
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
     *         &lt;element name="dataType" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" minOccurs="0"/>
     *         &lt;element name="semantics" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
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
        "dataType",
        "semantics",
        "any"
    })
    public static class Lookahead {

        protected ReferenceType dataType;
        protected org.ieee.standards.ieee1516_2010.String semantics;
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
         * Obtient la valeur de la propriété dataType.
         * 
         * @return
         *     possible object is
         *     {@link ReferenceType }
         *     
         */
        public ReferenceType getDataType() {
            return dataType;
        }

        /**
         * Définit la valeur de la propriété dataType.
         * 
         * @param value
         *     allowed object is
         *     {@link ReferenceType }
         *     
         */
        public void setDataType(ReferenceType value) {
            this.dataType = value;
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
     *         &lt;element name="dataType" type="{http://standards.ieee.org/IEEE1516-2010}ReferenceType" minOccurs="0"/>
     *         &lt;element name="semantics" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
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
        "dataType",
        "semantics",
        "any"
    })
    public static class TimeStamp {

        protected ReferenceType dataType;
        protected org.ieee.standards.ieee1516_2010.String semantics;
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
         * Obtient la valeur de la propriété dataType.
         * 
         * @return
         *     possible object is
         *     {@link ReferenceType }
         *     
         */
        public ReferenceType getDataType() {
            return dataType;
        }

        /**
         * Définit la valeur de la propriété dataType.
         * 
         * @param value
         *     allowed object is
         *     {@link ReferenceType }
         *     
         */
        public void setDataType(ReferenceType value) {
            this.dataType = value;
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
