package migway.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import migway.core.config.rules.CompletionRuleLoader;

/**
 * Configuration to use in the Migway gateway
 * This configuration contains the following informations:
 * 1/ the structure of the POJO.
 * The structure is an ordered list of elements. It is mainly used by the
 * component interface.
 * 2/ the mapping information.
 * It indicates which field in a POJO is
 * 
 * @author Sébastien Tissier
 *
 */
@XmlRootElement(name = "configuration")
// @XmlType (name = "configuration")
public class ConfigHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigHelper.class);

    // @XmlElementRef(name="s")
    // @XmlTransient
    @XmlElements({ @XmlElement(name = "classStructure", type = ClassStructure.class),
            @XmlElement(name = "enumStructure", type = EnumStructure.class),
            @XmlElement(name = "unionStructure", type = UnionStructure.class) })
    private ArrayList<Structure> structures = new ArrayList<Structure>();
    @XmlTransient
    private Map<String, Structure> pojoList = new HashMap<String, Structure>();
    @XmlElement
    private ArrayList<Mapping> mappings = new ArrayList<Mapping>();

    private CompletionRuleLoader completionRules = new CompletionRuleLoader(this);

    /**
     * Create a blank configuration.
     */
    public ConfigHelper() {
        // this(new File("migway:sample"));
    }

    /**
     * Load the configuration from a file
     * Supports also the default samples:
     * <ul>
     * <li><code>'migway:sample'</code> - sample POJO (MyPojo & DdsPojo)
     * <li><code>'migway:hla.tanklab'</code> - HLA Tanklab object from Tanklab
     * FOM (tanklab.Tank & tanklab.Fire)
     * <li><code>'migway:dds-GVA.LDM'</code> - DDS subset of GVA IDL (LDM.*,
     * LDM.platform.*, LDM.navigation.*)
     * <li><code>'migway:hla-RPRFOM.edu'</code> - HLA subset of RPRFOM
     * (namespace is edu.cyc14.essais.pojo.rprfom). Define BaseEntity Class and
     * Spatial attribute types
     * <li><code>'migway:testingconfig'</code> - structure for unit test
     * (contains config that use all available type and element combination)
     * </ul>
     * 
     * @param filename
     *            the file to load
     */
    public static ConfigHelper loadConfig(String filename) {
        return loadConfig(new File(filename));
    }

    /**
     * Load the configuration from a file
     * Supports also the default samples:
     * <ul>
     * <li><code>'migway:sample'</code> - sample POJO (MyPojo & DdsPojo)
     * <li><code>'migway:hla.tanklab'</code> - HLA Tanklab object from Tanklab
     * FOM (tanklab.Tank & tanklab.Fire)
     * <li><code>'migway:dds-GVA.LDM'</code> - DDS subset of GVA IDL (LDM.*,
     * LDM.platform.*, LDM.navigation.*)
     * <li><code>'migway:hla-RPRFOM.edu'</code> - HLA subset of RPRFOM
     * (namespace is edu.cyc14.essais.pojo.rprfom). Define BaseEntity Class and
     * Spatial attribute types
     * <li><code>'migway:testingconfig'</code> - structure for unit test
     * (contains config that use all available type and element combination)
     * </ul>
     * 
     * @param f
     *            the file to load
     */
    public static ConfigHelper loadConfig(File f) {
        ConfigHelper config;
        // Translate, if necessary, resource name with the File reference
        config = translateResource(f);
        // null return value indicates the resource is not available
        if (config == null) {
            // Get the ConfigHelper from this configuration
            config = loadFile(f);
        }
        // set the internal Map with the loaded structure list
        config.loadFromList();
        return config;
    }

    /**
     * Convert the file reference into an internal resource reference.
     * If file refer to a "migway:*" URI, then this method tries to convert the
     * name into an internal file resource name
     * 
     * @param f
     *            the file to open
     * @return the internal resource when f is a "migway:" reference. The
     *         original f value if not a migway reference. it can return null if
     *         an error occured
     */
    private static ConfigHelper translateResource(File f) {
        ConfigHelper config = null;
        // Try to convert specific uri (migway:*) with internal resource
        if (f.equals(new File("migway:sample"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-sample.xml"));
        } else if (f.equals(new File("migway:hla.tanklab"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-hla.tanklab.xml"));
        } else if (f.equals(new File("migway:dds-GVA.LDM"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-dds-GVA.LDM.xml"));
        } else if (f.equals(new File("migway:hla-RPRFOM.edu"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-hla-RPRFOM.edu.xml"));
        } else if (f.equals(new File("migway:testingconfig"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-testingconfig.xml"));
        } else if (f.equals(new File("migway:map-demo-tanklab"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-map-demo-tanklab.xml"));
        } else if (f.equals(new File("migway:key-demo-tanklab"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-key-demo-tanklab.xml"));
        } else if (f.equals(new File("migway:map-sample"))) {
            config = loadFile(ConfigHelper.class.getResourceAsStream("/mig-map-sample.xml"));
        } else {
            config = loadFile(f);
        }
        return config;
    }

    public ConfigHelper appendConfig(String filename) {
        return appendConfig(new File(filename));
    }

    public ConfigHelper appendConfig(File f) {
        ConfigHelper config; // the config to import (append)

        // translate into internal resource, if neccessary
        config = translateResource(f);
        // return null when error occurs
        if (config == null) {
            // get the config from the file to load
            config = loadFile(f);
        }
        if (config == null)
            return null;
        // import the new config into the actual config
        for (Structure s : config.structures) {
            // Append only if the structure doesn't already exist
            if (!this.pojoList.containsKey(s.getName())) {
                this.structures.add(s); // also append to the list
                this.pojoList.put(s.getName(), s); // append to the hashmap
                s.catalogMe(); // catalog the structure
            }
        }
        for (Mapping m : config.mappings) {
            this.mappings.add(m);
        }
        return this;
    }

    private static ConfigHelper loadFile(File file) {
        try {
            return loadFile(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File is not correct", e);
        }
    }

    private static ConfigHelper loadFile(InputStream inputStream) {
        JAXBContext jaxb;
        ConfigHelper config;
        try {
            jaxb = JAXBContext.newInstance("migway.core.config");
            config = (ConfigHelper) jaxb.createUnmarshaller().unmarshal(inputStream);
            return config;
        } catch (JAXBException e) {
            throw new InternalError("Unmarshalling migway.core.config failed", e);
        }
    }

    /**
     * Save this configHelper into XML file
     * 
     * @param f
     *            the file to store information into
     * @return true if file has been created or updated. false in any other case
     */
    public boolean saveConfig(File f) {
        // first thing to do is to store structure from HashMap into the List
        // (only List is marshaled into XML)
        loadFromMap();
        JAXBContext jaxb;
        try {
            jaxb = JAXBContext.newInstance("migway.core.config");
            jaxb.createMarshaller().marshal(this, new FileOutputStream(f));
            return true;
        } catch (JAXBException e) {
            e.printStackTrace();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Copy Structures from HashMap to the ArrayList. Only the array list can is
     * marshaled to XML.
     * 
     * Used by the saveConfig method
     */
    void loadFromMap() {
        this.structures.clear();
        this.structures.addAll(this.pojoList.values());
    }

    /**
     * Copy structures from ArrayList to the HashMap. Only the array list can be
     * unmarshaled from XML
     * 
     * Used by the loadConfig method
     */
    void loadFromList() {
        this.pojoList.clear();
        for (Structure s : structures) {
            this.pojoList.put(s.getName(), s);
            s.catalogMe();
        }
        // loadFromMap();
    }

    /**
     * Add a POJO structure to the configuration
     * 
     * @param structure
     *            the POJO structure
     */
    public void addStructure(Structure structure) {
        pojoList.put(structure.getName(), structure);
    }

    /**
     * Get the POJO Structure list actually configured
     * 
     * @return
     */
    public List<Structure> getPojos() {
        return new ArrayList<Structure>(pojoList.values());
    }

    /**
     * Get the list of POJO actually supported for the named interface
     * 
     * @param interfaceName
     *            the interface to list supported POJO in this configuration
     * @return
     */
    public List<String> supportedPojos(String interfaceName) {
        ArrayList<String> result = new ArrayList<String>();
        for (Structure pojo : pojoList.values()) {
            if (pojo.isSupported(interfaceName))
                result.add(pojo.getName());
        }
        return result;
    }

    /**
     * Get the structure of the specific POJO
     * 
     * @param pojoClassName
     * @return
     */
    public Structure getPojoStructure(String pojoClassName) {
        return pojoList.get(pojoClassName);
    }

    /**
     * Set the config mappings list with the content of the mappings list in
     * parameter (Caution, a clone of the parameter is store, not the
     * reference!)
     * 
     * @param mappings
     */
    public void setMappings(List<Mapping> mappings) {
        // Duplicate mapping list
        this.mappings = new ArrayList<Mapping>(mappings.size());
        this.mappings.addAll(mappings);
    }

    public List<Mapping> getMappings() {
        return this.mappings;
    }

    /**
     * Find a mapping for the destination object type in parameter
     * 
     * @param pojoTypeName
     * @return null if parameter is null or if no mapping is found
     */
    public Mapping getMappingForDestination(String pojoTypeName) {
        if (pojoTypeName == null)
            return null;
        for (Mapping m : mappings) {
            if (pojoTypeName.equals(m.getDestination()))
                return m;
        }
        return null;
    }

    /**
     * Find a mapping for the source object type in parameter
     * 
     * @param inputPojoType
     * @return null if parameter is null or if no mapping found for this type
     */
    public Mapping getMappingForSource(String inputPojoType) {
        if (inputPojoType == null)
            return null;
        // Look for the mapping configuration to use
        for (Mapping m : getMappings()) {
            LOG.trace("Looking into mapping {}", m.toString());
            // Find the correct mapping
            if (m.containsInput(inputPojoType)) {
                LOG.debug("Mapping found in mapping {}", m.toString());
                return m;
            }
        }
        return null;
    }

    /**
     * Return the Java binary name of the POJO that is equal to remote name of
     * interface
     * 
     * @param remoteObjectName
     * @param interfaceTypeName
     * @return
     */
    public String getPojoRemoteName(String remoteObjectName, String interfaceTypeName) {
        for (Structure s : pojoList.values()) {
            if (s.getRemoteName(interfaceTypeName).equals(remoteObjectName))
                return s.getName();
        }
        return null;
    }

    public boolean validatePojo(Mapping mapping, Object pojo) {
        // TODO Fix Null exception
        return completionRules.find(mapping.getCompletionRule().getName()).validate(pojo, mapping.getCompletionRule().getArgs());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConfigHelper)) return false;
        ConfigHelper c = (ConfigHelper)obj;
        // TODO Test also structures. Actually test only Mapping
        if (!this.mappings.equals(c.mappings)) return false;
//        if (!this.pojoList.equals(c.pojoList)) return false;
//        if (!this.structures.equals(c.structures)) return false;
        return true;
    }
}
