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
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour modelIdentificationType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="modelIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://standards.ieee.org/IEEE1516-2010}NonEmptyString" minOccurs="0"/>
 *         &lt;element name="type" type="{http://standards.ieee.org/IEEE1516-2010}modelType" minOccurs="0"/>
 *         &lt;element name="version" type="{http://standards.ieee.org/IEEE1516-2010}NonEmptyString" minOccurs="0"/>
 *         &lt;element name="modificationDate" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>date">
 *                 &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="securityClassification" type="{http://standards.ieee.org/IEEE1516-2010}securityClassificationType" minOccurs="0"/>
 *         &lt;element name="releaseRestriction" type="{http://standards.ieee.org/IEEE1516-2010}String" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="purpose" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element name="applicationDomain" type="{http://standards.ieee.org/IEEE1516-2010}applicationDomainType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://standards.ieee.org/IEEE1516-2010}NonEmptyString" minOccurs="0"/>
 *         &lt;element name="useLimitation" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element name="useHistory" type="{http://standards.ieee.org/IEEE1516-2010}String" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="keyword" type="{http://standards.ieee.org/IEEE1516-2010}keywordType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="poc" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://standards.ieee.org/IEEE1516-2010}pocType">
 *                 &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="reference" type="{http://standards.ieee.org/IEEE1516-2010}idReferenceType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="other" type="{http://standards.ieee.org/IEEE1516-2010}String" minOccurs="0"/>
 *         &lt;element name="glyph" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://standards.ieee.org/IEEE1516-2010>glyphType">
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/simpleContent>
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
@XmlType(name = "modelIdentificationType", propOrder = {
    "name",
    "type",
    "version",
    "modificationDate",
    "securityClassification",
    "releaseRestriction",
    "purpose",
    "applicationDomain",
    "description",
    "useLimitation",
    "useHistory",
    "keyword",
    "poc",
    "reference",
    "other",
    "glyph",
    "any"
})
public class ModelIdentificationType {

    protected NonEmptyString name;
    protected ModelType type;
    protected NonEmptyString version;
    protected ModelIdentificationType.ModificationDate modificationDate;
    protected SecurityClassificationType securityClassification;
    protected List<org.ieee.standards.ieee1516_2010.String> releaseRestriction;
    protected org.ieee.standards.ieee1516_2010.String purpose;
    protected ApplicationDomainType applicationDomain;
    protected NonEmptyString description;
    protected org.ieee.standards.ieee1516_2010.String useLimitation;
    protected List<org.ieee.standards.ieee1516_2010.String> useHistory;
    protected List<KeywordType> keyword;
    protected List<ModelIdentificationType.Poc> poc;
    protected List<IdReferenceType> reference;
    protected org.ieee.standards.ieee1516_2010.String other;
    protected ModelIdentificationType.Glyph glyph;
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
     *     {@link NonEmptyString }
     *     
     */
    public NonEmptyString getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link NonEmptyString }
     *     
     */
    public void setName(NonEmptyString value) {
        this.name = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     * 
     * @return
     *     possible object is
     *     {@link ModelType }
     *     
     */
    public ModelType getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelType }
     *     
     */
    public void setType(ModelType value) {
        this.type = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     * 
     * @return
     *     possible object is
     *     {@link NonEmptyString }
     *     
     */
    public NonEmptyString getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     * 
     * @param value
     *     allowed object is
     *     {@link NonEmptyString }
     *     
     */
    public void setVersion(NonEmptyString value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété modificationDate.
     * 
     * @return
     *     possible object is
     *     {@link ModelIdentificationType.ModificationDate }
     *     
     */
    public ModelIdentificationType.ModificationDate getModificationDate() {
        return modificationDate;
    }

    /**
     * Définit la valeur de la propriété modificationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelIdentificationType.ModificationDate }
     *     
     */
    public void setModificationDate(ModelIdentificationType.ModificationDate value) {
        this.modificationDate = value;
    }

    /**
     * Obtient la valeur de la propriété securityClassification.
     * 
     * @return
     *     possible object is
     *     {@link SecurityClassificationType }
     *     
     */
    public SecurityClassificationType getSecurityClassification() {
        return securityClassification;
    }

    /**
     * Définit la valeur de la propriété securityClassification.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityClassificationType }
     *     
     */
    public void setSecurityClassification(SecurityClassificationType value) {
        this.securityClassification = value;
    }

    /**
     * Gets the value of the releaseRestriction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the releaseRestriction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReleaseRestriction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.ieee.standards.ieee1516_2010.String }
     * 
     * 
     */
    public List<org.ieee.standards.ieee1516_2010.String> getReleaseRestriction() {
        if (releaseRestriction == null) {
            releaseRestriction = new ArrayList<org.ieee.standards.ieee1516_2010.String>();
        }
        return this.releaseRestriction;
    }

    /**
     * Obtient la valeur de la propriété purpose.
     * 
     * @return
     *     possible object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public org.ieee.standards.ieee1516_2010.String getPurpose() {
        return purpose;
    }

    /**
     * Définit la valeur de la propriété purpose.
     * 
     * @param value
     *     allowed object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public void setPurpose(org.ieee.standards.ieee1516_2010.String value) {
        this.purpose = value;
    }

    /**
     * Obtient la valeur de la propriété applicationDomain.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationDomainType }
     *     
     */
    public ApplicationDomainType getApplicationDomain() {
        return applicationDomain;
    }

    /**
     * Définit la valeur de la propriété applicationDomain.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationDomainType }
     *     
     */
    public void setApplicationDomain(ApplicationDomainType value) {
        this.applicationDomain = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     * 
     * @return
     *     possible object is
     *     {@link NonEmptyString }
     *     
     */
    public NonEmptyString getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     * 
     * @param value
     *     allowed object is
     *     {@link NonEmptyString }
     *     
     */
    public void setDescription(NonEmptyString value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété useLimitation.
     * 
     * @return
     *     possible object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public org.ieee.standards.ieee1516_2010.String getUseLimitation() {
        return useLimitation;
    }

    /**
     * Définit la valeur de la propriété useLimitation.
     * 
     * @param value
     *     allowed object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public void setUseLimitation(org.ieee.standards.ieee1516_2010.String value) {
        this.useLimitation = value;
    }

    /**
     * Gets the value of the useHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the useHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUseHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.ieee.standards.ieee1516_2010.String }
     * 
     * 
     */
    public List<org.ieee.standards.ieee1516_2010.String> getUseHistory() {
        if (useHistory == null) {
            useHistory = new ArrayList<org.ieee.standards.ieee1516_2010.String>();
        }
        return this.useHistory;
    }

    /**
     * Gets the value of the keyword property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keyword property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeyword().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeywordType }
     * 
     * 
     */
    public List<KeywordType> getKeyword() {
        if (keyword == null) {
            keyword = new ArrayList<KeywordType>();
        }
        return this.keyword;
    }

    /**
     * Gets the value of the poc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the poc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPoc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ModelIdentificationType.Poc }
     * 
     * 
     */
    public List<ModelIdentificationType.Poc> getPoc() {
        if (poc == null) {
            poc = new ArrayList<ModelIdentificationType.Poc>();
        }
        return this.poc;
    }

    /**
     * Gets the value of the reference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdReferenceType }
     * 
     * 
     */
    public List<IdReferenceType> getReference() {
        if (reference == null) {
            reference = new ArrayList<IdReferenceType>();
        }
        return this.reference;
    }

    /**
     * Obtient la valeur de la propriété other.
     * 
     * @return
     *     possible object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public org.ieee.standards.ieee1516_2010.String getOther() {
        return other;
    }

    /**
     * Définit la valeur de la propriété other.
     * 
     * @param value
     *     allowed object is
     *     {@link org.ieee.standards.ieee1516_2010.String }
     *     
     */
    public void setOther(org.ieee.standards.ieee1516_2010.String value) {
        this.other = value;
    }

    /**
     * Obtient la valeur de la propriété glyph.
     * 
     * @return
     *     possible object is
     *     {@link ModelIdentificationType.Glyph }
     *     
     */
    public ModelIdentificationType.Glyph getGlyph() {
        return glyph;
    }

    /**
     * Définit la valeur de la propriété glyph.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelIdentificationType.Glyph }
     *     
     */
    public void setGlyph(ModelIdentificationType.Glyph value) {
        this.glyph = value;
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
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://standards.ieee.org/IEEE1516-2010>glyphType">
     *       &lt;anyAttribute namespace='##other'/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Glyph
        extends GlyphType
    {


    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>date">
     *       &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
     *       &lt;anyAttribute namespace='##other'/>
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class ModificationDate {

        @XmlValue
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar value;
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
         * Obtient la valeur de la propriété value.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValue() {
            return value;
        }

        /**
         * Définit la valeur de la propriété value.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValue(XMLGregorianCalendar value) {
            this.value = value;
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
     *     &lt;extension base="{http://standards.ieee.org/IEEE1516-2010}pocType">
     *       &lt;attGroup ref="{http://standards.ieee.org/IEEE1516-2010}commonAttributes"/>
     *       &lt;anyAttribute namespace='##other'/>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Poc
        extends PocType
    {

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
