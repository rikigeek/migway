package migway.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "ref")
@XmlType(name="reference")

public class StructureRef extends ElementType {
    @XmlElement(name="structureName")
    private String structureName; 
    
    // used for XML marshaling
    @SuppressWarnings("unused")
    private StructureRef() {};
    StructureRef(String structureName) {
        this.structureName = structureName;
    }
    
    public String getStructureName() {
        return this.structureName;
    }

}
