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
 * <p>Classe Java pour dataTypesType complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="dataTypesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="basicDataRepresentations" type="{http://standards.ieee.org/IEEE1516-2010}basicDataRepresentationsType" minOccurs="0"/>
 *         &lt;element name="simpleDataTypes" type="{http://standards.ieee.org/IEEE1516-2010}simpleDataTypesType" minOccurs="0"/>
 *         &lt;element name="enumeratedDataTypes" type="{http://standards.ieee.org/IEEE1516-2010}enumeratedDataTypesType" minOccurs="0"/>
 *         &lt;element name="arrayDataTypes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://standards.ieee.org/IEEE1516-2010}arrayDataTypesType">
 *                 &lt;anyAttribute namespace='##other'/>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="fixedRecordDataTypes" type="{http://standards.ieee.org/IEEE1516-2010}fixedRecordDataTypesType" minOccurs="0"/>
 *         &lt;element name="variantRecordDataTypes" type="{http://standards.ieee.org/IEEE1516-2010}variantRecordDataTypesType" minOccurs="0"/>
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
@XmlType(name = "dataTypesType", propOrder = {
    "basicDataRepresentations",
    "simpleDataTypes",
    "enumeratedDataTypes",
    "arrayDataTypes",
    "fixedRecordDataTypes",
    "variantRecordDataTypes",
    "any"
})
public class DataTypesType {

    protected BasicDataRepresentationsType basicDataRepresentations;
    protected SimpleDataTypesType simpleDataTypes;
    protected EnumeratedDataTypesType enumeratedDataTypes;
    protected DataTypesType.ArrayDataTypes arrayDataTypes;
    protected FixedRecordDataTypesType fixedRecordDataTypes;
    protected VariantRecordDataTypesType variantRecordDataTypes;
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
     * Obtient la valeur de la propriété basicDataRepresentations.
     * 
     * @return
     *     possible object is
     *     {@link BasicDataRepresentationsType }
     *     
     */
    public BasicDataRepresentationsType getBasicDataRepresentations() {
        return basicDataRepresentations;
    }

    /**
     * Définit la valeur de la propriété basicDataRepresentations.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicDataRepresentationsType }
     *     
     */
    public void setBasicDataRepresentations(BasicDataRepresentationsType value) {
        this.basicDataRepresentations = value;
    }

    /**
     * Obtient la valeur de la propriété simpleDataTypes.
     * 
     * @return
     *     possible object is
     *     {@link SimpleDataTypesType }
     *     
     */
    public SimpleDataTypesType getSimpleDataTypes() {
        return simpleDataTypes;
    }

    /**
     * Définit la valeur de la propriété simpleDataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleDataTypesType }
     *     
     */
    public void setSimpleDataTypes(SimpleDataTypesType value) {
        this.simpleDataTypes = value;
    }

    /**
     * Obtient la valeur de la propriété enumeratedDataTypes.
     * 
     * @return
     *     possible object is
     *     {@link EnumeratedDataTypesType }
     *     
     */
    public EnumeratedDataTypesType getEnumeratedDataTypes() {
        return enumeratedDataTypes;
    }

    /**
     * Définit la valeur de la propriété enumeratedDataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link EnumeratedDataTypesType }
     *     
     */
    public void setEnumeratedDataTypes(EnumeratedDataTypesType value) {
        this.enumeratedDataTypes = value;
    }

    /**
     * Obtient la valeur de la propriété arrayDataTypes.
     * 
     * @return
     *     possible object is
     *     {@link DataTypesType.ArrayDataTypes }
     *     
     */
    public DataTypesType.ArrayDataTypes getArrayDataTypes() {
        return arrayDataTypes;
    }

    /**
     * Définit la valeur de la propriété arrayDataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link DataTypesType.ArrayDataTypes }
     *     
     */
    public void setArrayDataTypes(DataTypesType.ArrayDataTypes value) {
        this.arrayDataTypes = value;
    }

    /**
     * Obtient la valeur de la propriété fixedRecordDataTypes.
     * 
     * @return
     *     possible object is
     *     {@link FixedRecordDataTypesType }
     *     
     */
    public FixedRecordDataTypesType getFixedRecordDataTypes() {
        return fixedRecordDataTypes;
    }

    /**
     * Définit la valeur de la propriété fixedRecordDataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link FixedRecordDataTypesType }
     *     
     */
    public void setFixedRecordDataTypes(FixedRecordDataTypesType value) {
        this.fixedRecordDataTypes = value;
    }

    /**
     * Obtient la valeur de la propriété variantRecordDataTypes.
     * 
     * @return
     *     possible object is
     *     {@link VariantRecordDataTypesType }
     *     
     */
    public VariantRecordDataTypesType getVariantRecordDataTypes() {
        return variantRecordDataTypes;
    }

    /**
     * Définit la valeur de la propriété variantRecordDataTypes.
     * 
     * @param value
     *     allowed object is
     *     {@link VariantRecordDataTypesType }
     *     
     */
    public void setVariantRecordDataTypes(VariantRecordDataTypesType value) {
        this.variantRecordDataTypes = value;
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
     *     &lt;extension base="{http://standards.ieee.org/IEEE1516-2010}arrayDataTypesType">
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
    public static class ArrayDataTypes
        extends ArrayDataTypesType
    {


    }

}
