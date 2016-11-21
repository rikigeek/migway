package migway.core.config;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Base class of structure elements. This class is extendend to implement some
 * basic element (native, string...), enum element, or complex element
 * (reference to a structure)
 * 
 * In the same way a structure reflects a class, an element reflects a field of
 * this class.
 * 
 * An element is defined by the {@link name} of the field it reflects, its
 * {@link getter} and {@link setter} method (can be null). The reflected getter
 * method never takes
 * argument, and always returns the object.
 * The setter method never returns anything, and always takes only one argument:
 * the object value to set to the field (the element).
 * 
 * If there is no getter and no setter, then the field is directly accessed with
 * its name. You can check this by using {@link isField} method.
 * 
 * You can access the structure that contains this element with the method
 * {@link getPojo}
 * 
 * Because this model is designed to reflect objects that are encoded in an
 * unknown way, then it also contains informations as the endianness (
 * {@link isLittleEndian} and {@link isBigEndian}), and the remote name of the
 * object {@link getRemoteName}. Finally, it contains {@link signed} information
 * to indicate if numerical value is signed or not.
 * 
 * 
 * @author Sébastien Tissier
 *
 */
@XmlType
public class Element {
    @XmlElement(name = "getter")
    private String _getter;
    @XmlElement(name = "setter")
    private String _setter;
    @XmlAttribute(name = "field")
    @XmlID
    private String _name;
    // @XmlElement(name="pojo")
    @XmlTransient
    private Structure _pojo;
    @XmlElementRef(type = ElementType.class)
    private ElementType _type;
    @XmlElement(name = "bigEndian")
    private boolean _bigEndian;
    /**
     * List of remote name, one by interface
     */
    @XmlElement(name = "remoteNames")
    private String[] _remoteName;
    /**
     * A default remote name, if not all interfaces have a specific remote name
     */
    @XmlElement(name = "sharedRemoteName")
    private String _sharedRemoteName;
    /**
     * List of interfaces supported by this POJO
     */
    @XmlElement(name = "interfaces")
    private String[] _interfaceList;

    public void setRemoteName(String remoteName) {
        this._sharedRemoteName = remoteName;
    }

    public void setRemoteName(String remoteName, String interfaceName) {
        if (remoteName == null)
            remoteName = "";
        if (interfaceName == null)
            interfaceName = "";
        int index = -1;
        int nbInterfaces = _interfaceList.length;
        for (int i = 0; i < nbInterfaces; i++) {
            if (interfaceName.equalsIgnoreCase(_interfaceList[i])) {
                // already exist
                index = i;
            }
        }
        if (index == -1) {
            // need to create
            nbInterfaces++;
            _interfaceList = Arrays.copyOf(_interfaceList, nbInterfaces);
            _remoteName = Arrays.copyOf(_remoteName, nbInterfaces);
            _interfaceList[nbInterfaces - 1] = interfaceName;
            _remoteName[nbInterfaces - 1] = remoteName;
        } else {
            // existing interface
            _remoteName[index] = remoteName;
        }
    }

    /**
     * 
     * @return the name of method to use on the POJO instance to get the value
     *         of the field.
     */
    public String getter() {
        return _getter;
    }

    /**
     * 
     * @return the name of the method to use on the POJO instance to set the
     *         value of the field
     */
    public String setter() {
        return _setter;
    }

    /**
     * The field is accessible only if there is no setter or getter.
     * 
     * @return the name of the field used to store the value
     */
    public String name() {
        return _name;
    }

    /**
     * 
     * @return the structure instance that reflect the POJO object that contains
     *         this element
     */
    public Structure getPojo() {
        return _pojo;
    }

    /**
     * 
     * @return true if the field can be directly acceded. If false, you have to
     *         use getter and setter methods
     * 
     */
    public boolean isField() {
        return (_getter == null && _setter == null) ? true : false;
    }

    protected Element() {
        this._remoteName = new String[0];
        this._interfaceList = new String[0];
    }

    Element(String fieldName, String getter, String setter) {
        this();
        this._name = fieldName;
        this._sharedRemoteName = fieldName;
        this._getter = getter;
        this._setter = setter;
    }

    void setPojo(Structure pojoStructure) {
        this._pojo = pojoStructure;
    }

    /**
     * Set this element as an array of 'arrayElement' elements
     * 
     * @param arrayElement
     * @param minCapacity
     * @param maxCapacity
     */
    void setArray(ElementType arrayElement, int minCapacity, int maxCapacity) {
        this._type = new ArrayType(arrayElement, minCapacity, maxCapacity);
    }

    /**
     * Set this element as a basic type 'basicType'
     * 
     * @param basicType
     */
    void setBasic(BasicTypeEnum basicType) {
        this._type = ElementType.getPojoType(basicType);
    }

    /**
     * Set this element as reference to another structure named
     * 'objectClassName'
     * 
     * @param objectClassName
     */
    void setClass(String objectClassName) {
        this._type = ElementType.getPojoType(objectClassName);
    }

    /**
     * 
     * @return
     *         type of the element (enum)
     */
    public ElementType elementType() {
        return _type;
    }

    /**
     * Set the element type
     * 
     * @param type
     *            type of the element
     */
    void setElementType(ElementType type) {
        this._type = type;
    }

    /**
     * Get endianness information
     * 
     * @return true if information is encoded using BE (returns
     *         <code>!isLitlleEndian()</code>)
     */
    public boolean isBigEndian() {
        return _bigEndian;
    }

    /**
     * Get endianness information
     * 
     * @return true if information is encoded using LE (returns
     *         <code>!isBigEndian()</code>)
     */
    public boolean isLittleEndian() {
        return !_bigEndian;
    }

    /**
     * This information is not relevant is element doesn't reflect a numerical
     * value
     * 
     * @return true if element accept negative values
     */
    public boolean isSigned() {
        if (_type instanceof BasicType)
            return ((BasicType) _type).isSigned();
        return false;
    }

    /**
     * Because {@link name} store the field name used in the class, we sometime
     * need to know what is the name of this element from the 'other side'.
     * This method returns this information
     * 
     * @return
     *         a string containing the name used by the remote system, before
     *         information has been encoded.
     */
    /**
     * The type name as known by the remote bus or remote application
     * 
     * @return
     */
    public String getRemoteName(String interfaceName) {
        if (interfaceName == null || interfaceName.trim().isEmpty())
            return null;
        if (_interfaceList.length == 0)
            return _sharedRemoteName;
        // If interface is null, the loop will never return anything
        for (int i = 0; i < _interfaceList.length; i++) {
            if (_interfaceList[i].equalsIgnoreCase(interfaceName)) {
                if (_remoteName.length > i)
                    return _remoteName[i];
                else
                    return _sharedRemoteName;
            }
        }
        // Not found
        return null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Element)) return false;
        Element e = (Element) obj;
        
        if(!this._bigEndian == e._bigEndian) return false;
        if(!this._getter.equals(e._getter)) return false;
        if(!Arrays.equals(this._interfaceList, e._interfaceList)) return false;
        if(!Arrays.equals(this._remoteName, e._remoteName)) return false;
        if(!this._name.equals(e._name)) return false;
        if(!this._pojo.equals(e._pojo)) return false;
        if(!this._setter.equals(e._setter)) return false;
        if(!this._sharedRemoteName.equals(e._sharedRemoteName)) return false;
        // FIXME implement equals for elementType
//        if(!this._type.equals(e._type)) return false;
        
        return true;
    }
}