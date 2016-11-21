package migway.plugins.hla;

public interface IGenerator {

    public void writeHeader();
    
    public void openClass(String name, String parent, String description);

    public void addField(String className, String fieldName, String fieldType, String description);

    public void closeClass(String name);

    public void openEnum(String name, String enumType, String description);

    public void addEnumValue(String enumName, String enumValue, String enumId, String description);

    public void closeEnum(String name);

    public void openUnion(String name, String discName, String discType, String description);

    public void addAlternative(String unionName, String name, String altType, String discValue, String description);

    public void closeUnion(String name);
    
    public void writeFooter();
    
    public void addNativeType(String name, int iSize);

}
