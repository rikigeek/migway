package migway.core.config.rules;

import java.util.HashMap;
import java.util.Map;

import migway.core.config.ConfigHelper;
import migway.core.config.Element;
import migway.core.config.Structure;
import migway.core.helper.PojoLoaderHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompletionRuleLoader {
    private static Logger LOG = LoggerFactory.getLogger(CompletionRuleLoader.class);

    public final ICompletionRule NO_RULE = new ICompletionRule() {
        @Override
        public boolean validate(Object outputPojo, String[] args) {
            return true;
        }
    };
    public final ICompletionRule ALL_ELEMENTS_RULE = new ICompletionRule() {
        @Override
        public boolean validate(Object outputPojo, String[] args) {
            // lookup into Structure and list all elements. Only if all
            // elements are not null, return true
            String pojoClassName = outputPojo.getClass().getName();
            Structure structure = config.getPojoStructure(pojoClassName);
            PojoLoaderHelper loader = PojoLoaderHelper.INSTANCE;
            for (int idxElement = 0; idxElement < structure.size(); idxElement++) {
                Element element = structure.getElement(idxElement);
                if (element.name() == null) {
                    LOG.warn("Error in configuration: Element #" + idxElement + " of structure " + structure.getName() + " has no name");
                }
                try {
                    if (loader.getField(pojoClassName, element.name()).get(outputPojo) == null) {
                        return false;
                    }
                } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
                        | SecurityException e) {
                    LOG.warn("Failed to load element " + structure.getName() + "." + element.name() + ":\n" + e.getStackTrace().toString());
                    return false;
                }
            }
            return true;
        }
    };
    public final ICompletionRule SELECTED_ELEMENTS_RULE = new ICompletionRule() {
        @Override
        public boolean validate(Object outputPojo, String[] args) {
            if (args == null) {
                // No element to check. It means it's true
                return true;
            }
            // lookup into Structure and list all elements. If elements in
            // args array are not null, return true
            String pojoClassName = outputPojo.getClass().getName();
            Structure structure = config.getPojoStructure(pojoClassName);
            PojoLoaderHelper loader = PojoLoaderHelper.INSTANCE;
            for (String elementName : args) {
                Element element = structure.getElement(elementName);
                if (element == null) {
                    LOG.warn("Configuration error: element " + elementName + " is not an element of the structure " + structure.getName());
                    return false;
                }
                try {
                    if (loader.getField(pojoClassName, element.name()).get(outputPojo) == null) {
                        return false;
                    }
                } catch (IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
                        | SecurityException e) {
                    LOG.warn("Failed to load element " + structure.getName() + "." + element.name() + ":\n" + e.getStackTrace().toString());
                    return false;
                }
            }
            return true;
        }
    };

    private Map<String, ICompletionRule> registry = new HashMap<String, ICompletionRule>();
    private ConfigHelper config;

    public CompletionRuleLoader(ConfigHelper config) {
        this.config = config;
        // init default rules
        addRule("NO_RULE", NO_RULE);
        addRule("ALL_ELEMENTS", ALL_ELEMENTS_RULE);
        addRule("SELECTED_ELEMENTS", SELECTED_ELEMENTS_RULE);
    }

    /**
     * Fill in the registry of all CompletionRules
     * 
     * @param registry
     */
    public void setRegistry(Map<String, ICompletionRule> registry) {
        this.registry.putAll(registry);
    }

    public void addRule(String name, ICompletionRule rule) {
        if (rule == null || name == null)
            throw new IllegalArgumentException("Argument (name or rule) can't be null");
        registry.put(name, rule);
    }

    public ICompletionRule find(String name) {
        ICompletionRule rule = registry.get(name);
        if (rule != null)
            return rule;
        // Default rule is NO_RULE
        return NO_RULE;
    }

}
