package migway.core.config;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * A POJO structure define how the POJO is serialized by an interface: order
 * of its elements is important.
 * This structure is also used to fill the POJO with values from the interface
 * (bus or remote application)
 * 
 * A structure is defined by:
 * <ul>
 * <li><i>A Java binary name</i> is the name of the POJO class</li>
 * <li><i>A remote name</i> is the name of this POJO as known by the interface
 * (bus or remote application)
 * </ul>
 * 
 * @author Sébastien Tissier
 *
 */
@XmlType(propOrder = { "name", "globalRemoteName", "interfaceList", "remoteName", "elements" })
public abstract class Structure extends AbstractList<HierarchicalElement> {
    // Constructor for XML parser
    protected Structure() {
    };

    protected ElementFactory factory = ElementFactory.getInstance();
    /**
     * Static catalog that contains all loaded structures. Every Structure,
     * at initialization, call "catalogMe()" to add itself to this catalog
     */
    @XmlTransient
    protected static Map<String, Structure> structureCatalog = new HashMap<String, Structure>();
    @XmlElementWrapper(name = "elements")
    @XmlElement(name = "element")
    protected ArrayList<Element> elements = new ArrayList<Element>();

    /**
     * Java binary name of the POJO
     */
    @XmlAttribute
    @XmlID
    private String name;
    /**
     * List of remote name, one by interface
     */
    @XmlElement
    private String[] remoteName;
    /**
     * If this element is set, all interfaces are supported. Interface with no
     * specified remote name use this value as the remote name
     */
    @XmlElement
    private String globalRemoteName;
    /**
     * List of interfaces supported by this POJO
     */
    @XmlElement
    private String[] interfaceList;

    /**
     * Create a structure with one supported interface. Remote name is the
     * remote name of this supported interface only. No other interfaces are
     * supported
     * Create a new POJO structure supported only by interface in the list. If
     * the interface list is empty, all interfaces are supported.
     * Create a structure supported only by the interfaces in the list. Name
     * used by those interfaces (remoteName) are all the same :
     * <code>remoteName</code>.
     * Not specified interfaces are not supported *
     * Each
     * interface use the remote name specified at the same index in remoteName
     * array. If this array contains null values or empty string, then the
     * "pojo" name is used instead
     * 
     * @param name
     *            is a mandatory element (can't be null). It's the Java Binary
     *            name of the POJO
     * @param interfaceList
     *            is a list of interfaces supported by this POJO. If null or
     *            empty, it means all interfaces are allowed
     * @param remoteName
     *            is a list of remote name as know by each interface (bus or
     *            remote application). Size should be the same as interfaces
     *            list.
     *            If size is bigger, array is truncated. If size is smaller,
     *            missing name are filled with the shared remote name.
     *            It can be empty or null. In this case, default remote name is
     *            used by all interfaces.
     * 
     */
    protected Structure(String name, String[] interfaceList, String[] remoteName) {
        this(name);
        setRemoteName(interfaceList, remoteName);
    }

    /**
     * Create a structure with this name
     * 
     * @param name
     *            is the java binary name of the class
     */
    protected Structure(String name) {
        // name must not be null
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("name cannot be null");
        this.name = name;

        setGlobalRemoteName(this.name);

        catalogMe();
    }

    public void setGlobalRemoteName(String name) {
        this.globalRemoteName = this.name;
    }

    public void setRemoteName(String[] interfaceList, String[] remoteNameList) {
        // Reset global remote name
        this.globalRemoteName = null;

        // Duplicate interfaceList array, and replace null with empty value.
        this.interfaceList = duplicateArray(interfaceList);

        // duplicate remoteName array, and replace null with empty value
        this.remoteName = duplicateArray(remoteNameList);

        String defaultRemoteName;
        if (this.remoteName.length > 0 && !this.remoteName[0].isEmpty())
            defaultRemoteName = this.remoteName[0];
        else
            defaultRemoteName = this.name;

        // define a globalRemote name is no interface list is set
        if (this.interfaceList.length == 0)
            this.globalRemoteName = defaultRemoteName;

        // Replace empty values with sharedRemoteName if defined, name otherwise
        for (int i = 0; i < this.remoteName.length; i++) {
            if (this.remoteName[i].isEmpty())
                this.remoteName[i] = defaultRemoteName;
        }

        // Set both list the same size
        int nbInterfaces = this.interfaceList.length;
        int nbRemoteNames = this.remoteName.length;
        if (nbInterfaces > 0) {
            // First - make both list the same size
            if (nbRemoteNames > nbInterfaces) {
                // Truncate array
                this.remoteName = Arrays.copyOf(this.remoteName, nbInterfaces);
            }
            if (nbRemoteNames < nbInterfaces) {
                // Extend array
                this.remoteName = Arrays.copyOf(this.remoteName, nbInterfaces);
                // And fill empty values
                for (int i = nbRemoteNames; i < nbInterfaces; i++) {
                    this.remoteName[i] = defaultRemoteName;
                }
            }
        }
    }

    /**
     * Add itself into the global structure catalog.
     * 
     * It's used after loading from XML config file
     */
    protected void catalogMe() {
        // set the owner of each element
        for (Element element : this.elements) {
            element.setPojo(this);
        }
        // Structure catalog is used by the hierarchical navigation (Iterator on
        // Structure)
        if (structureCatalog.containsKey(this.getName()))
            return;
        structureCatalog.put(this.getName(), this);
    }

    /**
     * Duplicate a String array.
     * Always return a String array. The array can be empty.
     * Elements of the array are all initialized. They could be empty, but not
     * null.
     * 
     * @param array
     * @return
     */
    private String[] duplicateArray(String[] array) {
        if (array == null) {
            String[] ret = new String[0];
            return ret;
        }
        // Array is not null
        String[] ret = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null)
                ret[i] = "";
            else
                ret[i] = array[i].trim();
        }
        return ret;
    }

    /**
     * Check if this POJO is supported by the interface
     * 
     * @param interfaceName
     * @return
     */
    public boolean isSupported(String interfaceName) {
        // Check parameter value
        if (interfaceName == null || interfaceName.trim().isEmpty())
            return false;
        // A shared remote name defined means all interfaces are supported
        if (globalRemoteName != null && !globalRemoteName.isEmpty())
            return true;
        // default is all interfaces are supported
        if (interfaceList.length == 0)
            return true;
        // no share remote name and interface list exist mean only interface
        // listed are supported
        for (String s : interfaceList) {
            if (s.equalsIgnoreCase(interfaceName))
                return true;
        }
        return false;
    }

    /**
     * The type name as known by the remote bus or remote application
     * 
     * @return
     */
    public String getRemoteName(String interfaceName) {
        if (interfaceName == null || interfaceName.trim().isEmpty())
            return null;
        if (interfaceList.length == 0)
            return globalRemoteName;
        // If interface is null, the loop will never return anything
        for (int i = 0; i < interfaceList.length; i++) {
            if (interfaceList[i].equalsIgnoreCase(interfaceName)) {
                if (remoteName.length > i)
                    return remoteName[i];
                else
                    return globalRemoteName;
            }
        }
        // Not found
        return null;
    }

    /**
     * Java class name (binary name) of the POJO to load
     * 
     * @return the Java binary name
     */
    public String getName() {
        return name;
    }

    /**
     * Get package name of the class of the structure
     * 
     * @return
     */
    public String getPackageName() {
        int lpos = name.lastIndexOf('.');
        if (lpos < 0)
            return ""; // No package
        return name.substring(0, lpos);
    }

    /**
     * Get the 'n'th element from the structure.
     * 
     * @param n
     * @return the element, or null if there is no 'n'th element
     */
    public Element getElement(int n) {
        try {
            return elements.get(n);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /* *********************************************
     * 
     * Implementation of AbstractList & Iterable
     * 
     * ********************************************
     */

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public HierarchicalElement get(int n) {
        return new HierarchicalElement(getElement(n));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#set(int, java.lang.Object)
     */
    @Override
    public HierarchicalElement set(int index, HierarchicalElement hElement) {
        return new HierarchicalElement(elements.set(index, hElement.element()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size() {
        int size = 0;
        for (Element e : elements) {
            if (e.elementType() instanceof StructureRef) {
                // Get the remote structure to find its size
                // FIXME NullPointerException if reference is not found
                size += Structure.structureCatalog.get(((StructureRef) e.elementType()).getStructureName()).size();
            } else {
                size++;
            }
        }
        return size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.AbstractList#iterator()
     */
    @Override
    public StructureIterator iterator() {
        return new StructureIterator(elements, this);
    }

    /**
     * Recursive method to get an element of a structure from its name.
     * The name can be hierarchical name: a dot must separate each name level
     * 
     * @param elementName
     * @return
     */
    public Element getElement(String elementName) {
        for (Element e : elements) {
            if (e.name().equals(elementName))
                return e;
        }
        // from full name, not found. Let's check if it's a hierarchical name
        int sepPos = elementName.indexOf('.');
        if (sepPos > 0) {
            // found a hierarchical name
            String parent = elementName.substring(0, sepPos);
            Element parentElement = getElement(parent);
            if (parentElement != null) {
                // the name of the parent element is really an element
                if (parentElement.elementType() instanceof StructureRef) {
                    // More over, the element is a reference to another
                    // structure
                    // Let's get this referenced structure name
                    String subType = ((StructureRef) parentElement.elementType()).getStructureName();
                    // And try to find it in the structure catalog. If found,
                    // recursive call to this method, but from the subType
                    // structure
                    if (structureCatalog.containsKey(subType)) {
                        return structureCatalog.get(subType).getElement(elementName.substring(sepPos + 1));
                    }
                }
                // it could be an array
                if (parentElement.elementType() instanceof ArrayType) {
                    ArrayType t = (ArrayType) parentElement.elementType();
                    if (t.getElementType() instanceof StructureRef) {
                        String subType = ((StructureRef) t.getElementType()).getStructureName();
                        if (structureCatalog.containsKey(subType)) {
                            return structureCatalog.get(subType).getElement(elementName.substring(sepPos + 1));
                        }
                    }
                }
            }
        }
        // if nothing is found, return null
        return null;
    }

    /**
     * Get the number of elements in this structure
     * 
     * @return
     */
    public int getElementsSize() {
        return elements.size();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Structure))
            return false;
        Structure s = (Structure) o;
        if (this.globalRemoteName != null)
            if (!this.globalRemoteName.equals(s.globalRemoteName))
                return false;
            else if (s.globalRemoteName != null)
                return false;
        if (!this.elements.equals(s.elements))
            return false;
        if (!Arrays.equals(this.interfaceList, s.interfaceList))
            return false;
        if (!Arrays.equals(this.remoteName, s.remoteName))
            return false;
        if (!this.name.equals(s.name))
            return false;
        return true;
        // return super.equals(o);
    }
}
