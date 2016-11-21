package migway.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="basic")

public class BasicType extends ElementType {
    
    @XmlElement
    private BasicTypeEnum basicType;
    
    // used for XML marshaling
    @SuppressWarnings("unused")
    private BasicType() {}
    BasicType(BasicTypeEnum type) {
        this.basicType = type; 
    }

    public BasicTypeEnum getBasicTypeEnum() {
        return basicType;
    }
    
    public boolean isSigned() {
        return basicType.isSigned();
    }

}
