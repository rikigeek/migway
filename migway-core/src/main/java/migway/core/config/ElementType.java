package migway.core.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlSeeAlso;

//@XmlType(name="type" )
@XmlSeeAlso({
    StructureRef.class,
    ArrayType.class,
    BasicType.class
})
public abstract class ElementType {
    private static Map<BasicTypeEnum, ElementType> mapBasic = new HashMap<BasicTypeEnum, ElementType>();
    private static Map<String, ElementType> mapRef = new HashMap<String, ElementType>();
    
    /**
     * GetNativeType
     * 
     * @param type
     * @return
     */
    public static ElementType getPojoType(BasicTypeEnum type) {
        if (mapBasic.containsKey(type))
            return mapBasic.get(type);
        ElementType retVal = create(type);
        mapBasic.put(type, retVal);
        return retVal;
    }
    
    public static ElementType getPojoType(String refName) {
        if (mapRef.containsKey(refName)) 
            return mapRef.get(refName);
        ElementType retVal = create(refName);
        mapRef.put(refName, retVal);
        return retVal;
    }

    public static ElementType BOOLEAN = getPojoType(BasicTypeEnum.BOOLEAN);
    public static ElementType BYTE = getPojoType(BasicTypeEnum.BYTE);
    public static ElementType SHORT = getPojoType(BasicTypeEnum.SHORT);
    public static ElementType INT = getPojoType(BasicTypeEnum.INT);
    public static ElementType LONG = getPojoType(BasicTypeEnum.LONG);
    public static ElementType UNSIGNED_BYTE = getPojoType(BasicTypeEnum.UNSIGNED_BYTE);
    public static ElementType UNSIGNED_SHORT = getPojoType(BasicTypeEnum.UNSIGNED_SHORT);
    public static ElementType UNSIGNED_INT = getPojoType(BasicTypeEnum.UNSIGNED_INT);
    public static ElementType FLOAT = getPojoType(BasicTypeEnum.FLOAT);
    public static ElementType DOUBLE = getPojoType(BasicTypeEnum.DOUBLE);
    public static ElementType STRING = getPojoType(BasicTypeEnum.STRING);

    private static ElementType create(BasicTypeEnum type) {
        ElementType t = new BasicType(type);
        return t;
    }
    private static ElementType create(String refname) {
        ElementType t = new StructureRef(refname);
        return t;
    }
}
