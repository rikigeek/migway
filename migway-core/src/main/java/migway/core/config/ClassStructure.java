package migway.core.config;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="classStructure")
public class ClassStructure extends Structure {

    protected ClassStructure() {};
    ClassStructure(String objectClassName) {
        super(objectClassName);
    }

    ClassStructure(String objectClassName, String[] interfaces, String[] remoteNames) {
        super(objectClassName, interfaces, remoteNames);
    }

    /**
     * Add a basic type element
     * 
     * @param fieldName
     * @param elementType
     * @param getter
     * @param setter
     */

    public void addBasic(String fieldName, String getter, String setter, BasicTypeEnum basicType) {
        Element element = new Element(fieldName, getter, setter);
        element.setBasic(basicType);
        addElement(element);
    }

    public void addClass(String fieldName, String getter, String setter, String objectClassName) {
        Element element = new Element(fieldName, getter, setter);
        element.setClass(objectClassName);
        addElement(element);
    }

    public void addArray(String fieldName, String getter, String setter, ElementType elementType, int minCapacity, int maxCapacity) {
        Element element = new Element(fieldName, getter, setter);
        element.setArray(elementType, minCapacity, maxCapacity);
        addElement(element);
    }

    public void addArray(String fieldName, String getter, String setter, BasicTypeEnum arrayType, int minCapacity, int maxCapacity) {
        addArray(fieldName, getter, setter, ElementType.getPojoType(arrayType), minCapacity, maxCapacity);
    }

    public void addArray(String fieldName, String getter, String setter, String arrayType, int minCapacity, int maxCapacity) {
        addArray(fieldName, getter, setter, ElementType.getPojoType(arrayType), minCapacity, maxCapacity);
    }

    public void addElement(Element element) {
        element.setPojo(this);
        elements.add(element);
    }

}
