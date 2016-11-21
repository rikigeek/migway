package migway.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Mapping {

    public static class Source {
        @XmlAttribute
        public String name;
        @XmlElement(name = "sourceKey", required = false)
        public KeyModel key;

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Source))
                return false;
            Source s = (Source) obj;
            if (!Equals.test(this.name, s.name))
                return false;
            if (!Equals.test(this.key, s.key))
                return false;
            return true;
        }
    }

    // Structure to create
    @XmlAttribute
    private String destination;

    @XmlElement
    private KeyModel destinationKey;

    // Structures that are used to fill in the output
    @XmlElements({ @XmlElement(name = "source", type = Source.class) })
    private List<Source> sources = new ArrayList<Source>();

    // list of Element mapping
    @XmlElement
    private List<ElementMapping> elementMappings = new ArrayList<ElementMapping>();

    // Rule to fullfill if we want to send the instance. Default rule is there
    // is no rule.
    @XmlElement
    private CompletionRule completionRule = new CompletionRule("NO_RULE", null);

    void add(ElementMapping em) {
        elementMappings.add(em);
    }

    /**
     * check if this Mapping is made from a specific POJO class name
     * 
     * @param pojoTypeName
     * @return true if pojoTypeName is one the input POJO type of this mapping
     */
    public boolean containsInput(String pojoTypeName) {
        return getSource(pojoTypeName) != null;
    }

    void resetSources() {
        sources.clear();
    }

    Source getSource(String pojoTypeName) {
        for (Source s : sources) {
            if (s.name.equals(pojoTypeName))
                return s;
        }
        return null;
    }

    List<Source> getSources() {
        return sources;
    }

    void addSource(String source, KeyModel key) {
        Source s = new Source();
        s.name = source;
        s.key = key;
        sources.add(s);
    }

    public KeyModel getSourceKey(String pojoTypeName) {
        Source s = getSource(pojoTypeName);
        if (s == null)
            return null;
        return s.key;
    }

    /**
     * get the output POJO type of this mapping
     * 
     * @return the java binary name of the destination object class. Can be
     *         empty if no destination is set or if destination has no name;
     */
    public String getDestination() {
        if (destination == null)
            return "";
        return destination;
    }

    void setDestination(String destination) {
        this.destination = destination;
    }

    public KeyModel getDestinationKey() {
        return this.destinationKey;
    }

    void setDestinationKey(KeyModel key) {
        this.destinationKey = key;
    }

    /**
     * CompletionRule is a definition that must be fulfilled to be able to send
     * a POJO instance.
     * 
     * @return never null. Default is to return the NO_RULE
     *         implementation
     */
    public CompletionRule getCompletionRule() {
        return completionRule;
    }

    /**
     * Obtain the element mapping information (one element is transformed into
     * one another element)
     * 
     * @return safe array (no references, you can modify the array if necessary)
     */
    public ElementMapping[] getElementMapping() {
        return elementMappings.toArray(new ElementMapping[1]);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Mapping))
            return false;
        Mapping m = (Mapping) obj;
        if (!Equals.test(this.completionRule, m.completionRule))
            return false;
        if (!Equals.test(this.destination, m.destination))
            return false;
        if (!Equals.test(this.destinationKey, m.destinationKey))
            return false;
        if (!Equals.test(this.elementMappings, m.elementMappings))
            return false;
        if (!Equals.test(this.sources, m.sources))
            return false;
        return true;
    }

}
