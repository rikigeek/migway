package migway.core.config;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "unionStructure")
public class UnionStructure extends ClassStructure {
    protected UnionStructure(){};
    @XmlElement
    private Element discriminant = null;
    @XmlElement
    private Map<String, String> alternatives = new HashMap<String, String>();

    UnionStructure(String unionName) {
        super(unionName);
    }

    UnionStructure(String unionName, String[] interfaces, String[] remoteName) {
        super(unionName, interfaces, remoteName);
    }

    @Override
    public int size() {
        return 1;
    }

    public void setDiscrimant(String disc) {
        this.discriminant = this.getElement(disc);
    }

    public Element getDiscrimant() {
        return discriminant;
    }

    public UnionStructure addAlternative(String discriminantValue, String structure) {
        alternatives.put(discriminantValue, structure);
        return this;
    }

    public Map<String, String> getAlternatives() {
        return alternatives;
    }

    public String getAlternative(String discriminantValue) {
        return alternatives.get(discriminantValue);
    }
}
