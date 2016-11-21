package migway.core.config;

/**
 * Factory class for building configuration file
 * 
 * @author Sébastien Tissier
 *
 */
public class ConfigFactory {
    /**
     * Create a Class Structure
     * 
     * @param name
     *            name of the class
     * @param interfaceList
     *            list of interfaces supported by this structure
     * @param remoteList
     *            list of name as this structure is know by the interface
     * @return
     */
    public ClassStructure createClass(String name, String[] interfaceList, String[] remoteList) {
        return new ClassStructure(name, interfaceList, remoteList);
    }

    public ClassStructure createClass(String name) {
        return new ClassStructure(name);
    }

    /**
     * Create a Union structure
     * 
     * @param name
     *            name of the Java class that defines this structure
     * @param interfaceList
     *            list of interfaces supported by this structure
     * @param remoteList
     *            list of name as this structure is know by the interface
     * @return
     */
    public UnionStructure createUnion(String name, String[] interfaceList, String[] remoteList) {
        return new UnionStructure(name, interfaceList, remoteList);
    }

    public UnionStructure createUnion(String name) {
        return new UnionStructure(name);
    }

    /**
     * Create an enumeration
     * 
     * @param name
     * @param interfaceList
     *            list of interfaces supported by this structure
     * @param remoteList
     *            list of name as this structure is know by the interface
     * @return
     */
    public EnumStructure createEnum(String name, String[] interfaceList, String[] remoteList) {
         return new EnumStructure(name, interfaceList, remoteList);
    }

    public EnumStructure createEnum(String name) {
        return new EnumStructure(name);
    }

    /**
     * Create an element that is basic type
     * 
     * @param name
     * @param type
     * @return
     */
    public Element createBasic(String name, BasicTypeEnum type) {
        Element e = new Element(name, null, null);
        e.setBasic(type);
        return e;
    }

    public Element createArray(String name, ElementType type, int size) {
        Element e = new Element(name, null, null);
        e.setArray(type, 0, size);
        return e;
    }

    public Element createRef(String name, Structure ref) {
        Element e = new Element(name, null, null);
        e.setPojo(ref);
        return e;
    }

    public Element createElement(String name, ElementType type) {
        Element element = new Element(name, null, null);
        element.setElementType(type);
        return element;
    }

    /**
     * Get a basic element type
     * 
     * @param type
     *            this string must exist as a value of the {@link BasicTypeEnum}
     *            enum
     * @return
     */
    public ElementType getBasicType(String type) {
        BasicTypeEnum basicEnum = BasicTypeEnum.valueOf(type);
        return ElementType.getPojoType(basicEnum);
    }

    /**
     * Create a new type of array
     * 
     * @param arrayType
     *            is the type of the nested element (type of an element of the
     *            array)
     * @param size
     * @return
     */
    public ElementType createArrayType(ElementType arrayType, int size) {
        return new ArrayType(arrayType, 0, size);
    }

    /**
     * Get a reference to an existing structure. This reference is read as a
     * type
     * 
     * @param ref
     * @return
     */
    public ElementType getReferenceType(String ref) {
        return ElementType.getPojoType(ref);
    }


}
