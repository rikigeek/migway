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
 * <p>Classe Java pour objectModelType complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="objectModelType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="modelIdentification" type="{http://standards.ieee.org/IEEE1516-2010}modelIdentificationType" minOccurs="0"/>
 *         &lt;element name="serviceUtilization" type="{http://standards.ieee.org/IEEE1516-2010}serviceUtilizationType" minOccurs="0"/>
 *         &lt;element name="objects" type="{http://standards.ieee.org/IEEE1516-2010}objectsType" minOccurs="0"/>
 *         &lt;element name="interactions" type="{http://standards.ieee.org/IEEE1516-2010}interactionsType" minOccurs="0"/>
 *         &lt;element name="dimensions" type="{http://standards.ieee.org/IEEE1516-2010}dimensionsType" minOccurs="0"/>
 *         &lt;element name="time" type="{http://standards.ieee.org/IEEE1516-2010}timeType" minOccurs="0"/>
 *         &lt;element name="tags" type="{http://standards.ieee.org/IEEE1516-2010}tagsType" minOccurs="0"/>
 *         &lt;element name="synchronizations" type="{http://standards.ieee.org/IEEE1516-2010}synchronizationsType" minOccurs="0"/>
 *         &lt;element name="transportations" type="{http://standards.ieee.org/IEEE1516-2010}transportationsType" minOccurs="0"/>
 *         &lt;element name="switches" type="{http://standards.ieee.org/IEEE1516-2010}switchesType" minOccurs="0"/>
 *         &lt;element name="updateRates" type="{http://standards.ieee.org/IEEE1516-2010}updateRatesType" minOccurs="0"/>
 *         &lt;element name="dataTypes" type="{http://standards.ieee.org/IEEE1516-2010}dataTypesType" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://standards.ieee.org/IEEE1516-2010}notesType" minOccurs="0"/>
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
@XmlType(name = "objectModelType", propOrder = {
    "modelIdentification",
    "serviceUtilization",
    "objects",
    "interactions",
    "dimensions",
    "time",
    "tags",
    "synchronizations",
    "transportations",
    "switches",
    "updateRates",
    "dataTypes",
    "notes",
    "any"
})
public class ObjectModelType {

    protected ModelIdentificationType modelIdentification;
    protected ServiceUtilizationType serviceUtilization;
    protected ObjectsType objects;
    protected InteractionsType interactions;
    protected DimensionsType dimensions;
    protected TimeType time;
    protected TagsType tags;
    protected SynchronizationsType synchronizations;
    protected TransportationsType transportations;
    protected SwitchesType switches;
    protected UpdateRatesType updateRates;
    protected DataTypesType dataTypes;
    protected NotesType notes;
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
     * Obtient la valeur de la propri�t� modelIdentification.
     * 
     * @return
     *     possible object is
     *     {@link ModelIdentificationType }
     *     
     */
    public ModelIdentificationType getModelIdentification() {
        return modelIdentification;
    }

    /**
     * D�finit la valeur de la propri�t� modelIdentification.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelIdentificationType }
     *     
     */
    public void setModelIdentification(ModelIdentificationType value) {
        this.modelIdentification = value;
    }

    /**
     * Obtient la valeur de la propri�t� serviceUtilization.
     * 
     * @return
     *     possible object is
     *     {@link ServiceUtilizationType }
     *     
     */
    public ServiceUtilizationType getServiceUtilization() {
        return serviceUtilization;
    }

    /**
     * D�finit la valeur de la propri�t� serviceUtilization.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceUtilizationType }
     *     
     */
    public void setServiceUtilization(ServiceUtilizationType value) {
        this.serviceUtilization = value;
    }

    /**
     * Obtient la valeur de la propri�t� objects.
     * 
     * @return
     *     possible object is
     *     {@link ObjectsType }
     *     
     */
    public ObjectsType getObjects() {
        return objects;
    }

    /**
     * D�finit la valeur de la propri�t� objects.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectsType }
     *     
     */
    public void setObjects(ObjectsType value) {
        this.objects = value;
    }

    /**
     * Obtient la valeur de la propri�t� interactions.
     * 
     * @return
     *     possible object is
     *     {@link InteractionsType }
     *     
     */
    public InteractionsType getInteractions() {
        return interactions;
    }

    /**
     * D�finit la valeur de la propri�t� interactions.
     * 
     * @param value
     *     allowed object is
     *     {@link InteractionsType }
     *     
     */
    public void setInteractions(InteractionsType value) {
        this.interactions = value;
    }

    /**
     * Obtient la valeur de la propri�t� dimensions.
     * 
     * @return
     *     possible object is
     *     {@link DimensionsType }
     *     
     */
    public DimensionsType getDimensions() {
        return dimensions;
    }

    /**
     * D�finit la valeur de la propri�t� dimensions.
     * 
     * @param value
     *     allowed object is
     *     {@link DimensionsType }
     *     
     */
    public void setDimensions(DimensionsType value) {
        this.dimensions = value;
    }

    /**
     * Obtient la valeur de la propri�t� time.
     * 
     * @return
     *     possible object is
     *     {@link TimeType }
     *     
     */
    public TimeType getTime() {
        return time;
    }

    /**
     * D�finit la valeur de la propri�t� time.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeType }
     *     
     */
    public void setTime(TimeType value) {
        this.time = value;
    }

    /**
     * Obtient la valeur de la propri�t� tags.
     * 
     * @return
     *     possible object is
     *     {@link TagsType }
     *     
     */
    public TagsType getTags() {
        return tags;
    }

    /**
     * D�finit la valeur de la propri�t� tags.
     * 
     * @param value
     *     allowed object is
     *     {@link TagsType }
     *     
     */
    public void setTags(TagsType value) {
        this.tags = value;
    }

    /**
     * Obtient la valeur de la propri�t� synchronizations.
     * 
     * @return
     *     possible object is
     *     {@link SynchronizationsType }
     *     
     */
    public SynchronizationsType getSynchronizations() {
        return synchronizations;
    }

    /**
     * D�finit la valeur de la propri�t� synchronizations.
     * 
     * @param value
     *     allowed object is
     *     {@link SynchronizationsType }
     *     
     */
    public void setSynchronizations(SynchronizationsType value) {
        this.synchronizations = value;
    }

    /**
     * Obtient la valeur de la propri�t� transportations.
     * 
     * @return
     *     possible object is
     *     {@link TransportationsType }
     *     
     */
    public TransportationsType getTransportations() {
        return transportations;
    }

    /**
     * D�finit la valeur de la propri�t� transportations.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportationsType }
     *     
     */
    public void setTransportations(TransportationsType value) {
        this.transportations = value;
    }

    /**
     * Obtient la valeur de la propri�t� switches.
     * 
     * @return
     *     possible object is
     *     {@link SwitchesType }
     *     
     */
    public SwitchesType getSwitches() {
        return switches;
    }

    /**
     * D�finit la valeur de la propri�t� switches.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchesType }
     *     
     */
    public void setSwitches(SwitchesType value) {
        this.switches = value;
    }

    /**
     * Obtient la valeur de la propri�t� updateRates.
     * 
     * @return
     *     possible object is
     *     {@link UpdateRatesType }
     *     
     */
    public UpdateRatesType getUpdateRates() {
        return updateRates;
    }

    /**
     * D�finit la valeur de la propri�t� updateRates.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateRatesType }
     *     
     */
    public void setUpdateRates(UpdateRatesType value) {
        this.updateRates = value;
    }

    /**
     * Obtient la valeur de la propri�t� dataTypes.
     * 
     * @return
     *     possible object is
     *     {@link DataTypesType }
     *     
     */
    public DataTypesType getDataTypes() {
        return dataTypes;
    }

    /**
     * D�finit la valeur de la propri�t� dataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link DataTypesType }
     *     
     */
    public void setDataTypes(DataTypesType value) {
        this.dataTypes = value;
    }

    /**
     * Obtient la valeur de la propri�t� notes.
     * 
     * @return
     *     possible object is
     *     {@link NotesType }
     *     
     */
    public NotesType getNotes() {
        return notes;
    }

    /**
     * D�finit la valeur de la propri�t� notes.
     * 
     * @param value
     *     allowed object is
     *     {@link NotesType }
     *     
     */
    public void setNotes(NotesType value) {
        this.notes = value;
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
