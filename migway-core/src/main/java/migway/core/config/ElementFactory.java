package migway.core.config;

public class ElementFactory {
    private static ElementFactory INSTANCE = new ElementFactory();
    
    public static ElementFactory getInstance() {
        return INSTANCE;
    }
    
//    public Element newBasic(String fieldName, ElementType type, String getter, String setter) {
//        return newBasic(fieldName, PojoType.getPojoType(type), getter, setter); 
//    }
//    public Element newBasic(String fieldName, PojoType type, String getter, String setter) {
//        Element el = new Element(fieldName, getter, setter);
//        return el;
//    }
//    
//    public Element newArray(String fieldName, PojoType arrayType, String getter, String setter, int minCapacity, int maxCapacity) {
//        Element el = new Element(fieldName, getter, setter);
//        return el;
//    }
//    
//    public Element newClass(String fieldName, String objectClassName, String getter, String setter) {
//        Element el = new Element(fieldName, getter, setter);
//        return el;
//    }
//    
//    public Element newClass(String fieldName, PojoType elementType, String getter, String setter) {
//        Element el = new Element(fieldName, getter, setter);
//        return el;
//    }
//
//    public Element newElement(String fieldName, PojoType elementType, String getter, String setter) {
//        if (elementType instanceof BasicType)
//            return newBasic(fieldName, elementType, getter, setter);
//        if (elementType instanceof ArrayType) // in this case, elementType is the array
//            return newArray(fieldName, elementType, getter, setter);
//        if (elementType instanceof StructureRef)
//            return newClass(fieldName, elementType, getter, setter);
//        return null;
//    }
}
