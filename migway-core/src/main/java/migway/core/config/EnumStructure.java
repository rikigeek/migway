package migway.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "enumStructure")
public class EnumStructure extends Structure {
    protected EnumStructure() {
    }

    @XmlElementRef(type = ElementType.class)
    private ElementType enumType = ElementType.INT;

    @XmlElement(name = "enumValue")
    @XmlElementWrapper(name = "enumElements")
    private List<String> enumElements = new ArrayList<String>();

    /**
     * Set this element as an enumeration entry
     * 
     * @param enumType
     *            the object type of the enumeration. Default is an INTEGER
     */

    EnumStructure(String enumName) {
        super(enumName);
    }

    EnumStructure(String enumName, String[] interfaces, String[] remoteNames) {
        super(enumName, interfaces, remoteNames);
    }

    ElementType getEnumType() {
        return enumType;
    }

    public void addEnumValue(String string) {
        enumElements.add(string);
    }

    public int elementPosition(String elementValue) {
        return enumElements.indexOf(elementValue);
    }

    @Override
    public int size() {
        return enumElements.size();
    }
}
