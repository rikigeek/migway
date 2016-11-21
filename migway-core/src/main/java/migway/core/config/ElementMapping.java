package migway.core.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Mapping information on element level (the lowest level)
 * 
 * Transformation is always done from one to one
 * 
 * @author Sébastien Tissier
 *
 */
public class ElementMapping {
    /** 
     * Identification of an element in a Mapping
     * 
     * @author Sébastien Tissier
     *
     */
    @XmlType(propOrder = {"structureName", "elementName", "key"})
    public static class ElementKey {
        @XmlAttribute(name = "name")
        public String elementName;
        @XmlElement
        public String structureName; 
        @XmlElement
        public KeyModel key;
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ElementKey)) return false;
            ElementKey e = (ElementKey) obj;
            if (!Equals.test(this.elementName, e.elementName)) return false;
            if (!Equals.test(this.structureName, e.structureName)) return false;
            if (!Equals.test(this.key, e.key)) return false;
            return true;
        }
    }
    @XmlElement
    private ElementKey destination;
    @XmlElement
    private ElementKey source;

    /**
     * Get the destination element
     * 
     * @return
     */
    public ElementKey getDestination() {
        return destination;
    }
    
    public KeyModel getDestinationKey() {
        return destination.key;
    }

    void setDestination(ElementKey destination) {
        this.destination = destination;
    }

    void setDestination(Element destination) {
        this.destination = new ElementKey();
        this.destination.elementName = destination.name();
        this.destination.structureName = destination.getPojo().getName();
    }

    void setDestinationKey(KeyModel key) {
        this.destination.key = key;
    }
    /**
     * Get the source element
     * 
     * @return
     */
    public ElementKey getSource() {
        return source;
    }
    
    public KeyModel getSourceKey() {
        return source.key;
    }

    void setSource(ElementKey source) {
        this.source = source;
    }
    
    void setSource(Element source) {
        this.source = new ElementKey();
        this.source.elementName = source.name();
        this.source.structureName = source.getPojo().getName();
    }
    
    void setSourceKey(KeyModel key) {
        this.source.key = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ElementMapping)) return false;
        ElementMapping e = (ElementMapping) obj;
        if (!Equals.test(this.destination, e.destination)) return false;
        if (!Equals.test(this.source, e.source)) return false;
        return true;
    }
}
