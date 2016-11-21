package migway.core.config;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * How to know when a POJO instance is complete.
 * A POJO is complete when it contains enough information (enough data) to not
 * screw up the bus where it's sent.
 * 
 * There is multiple way of defining a {@link completionRule}. It can be defined
 * by an external bean, or when a list of elements are not null.
 * When there is no completion rule, then a POJO is always complete and can
 * always be sent.
 * 
 * @author Sébastien Tissier
 *
 */
@XmlType
public class CompletionRule {
    @XmlElement
    protected String name;
    @XmlElement
    protected String[] args;

    private static final String DEFAULT_RULE = "NO_RULE";

    public CompletionRule() {
        // Necessary for JAXB
    }

    public CompletionRule(String name, String[] arg) {
        this();
        this.name = name;
        if (arg != null)
            this.args = Arrays.copyOf(arg, arg.length);
        else
            this.args = null;
    }

    public String getName() {
        if (name == null)
            return DEFAULT_RULE;
        return name;
    }

    public String[] getArgs() {
        if (args == null)
            return new String[0];
        return args;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof CompletionRule))
            return false;
        CompletionRule rule = (CompletionRule) obj;
        if (!this.getName().equals(rule.getName())) {
                return false;
        }
        return Arrays.equals(this.getArgs(), rule.getArgs());
    }

}
