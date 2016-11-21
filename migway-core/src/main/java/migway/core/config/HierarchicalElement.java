package migway.core.config;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A hierarchical information of an element.
 * It contains an Element, and the list of Structure object we went through
 * 
 * @author Sébastien Tissier
 *
 */
@XmlType
public class HierarchicalElement {
    @XmlElement
    private Element element;
    @XmlElement
    private List<Structure> structurePath = new ArrayList<Structure>();
    @XmlElement
    private List<Element> elementPath = new ArrayList<Element>();

    HierarchicalElement(Element element) {
        this.element = element;
    }

    HierarchicalElement setElement(Element element) {
        this.element = element;
        return this;
    }

    /**
     * Is the element from a sub object?
     * 
     * @return
     */
    public boolean isNested() {
        return (structurePath.size() > 0 || elementPath.size() > 0);
    }

    /**
     * Get the leaf element
     * 
     * @return
     */
    public Element element() {
        return this.element;
    }

    /**
     * Get an element from the tree. Index is based on Attributes name.
     * e.g. If element 'name' is accessed with 'Person.address.city.name', where
     * Person is the object whose structure was iterated,
     * then 'address' index is 1, 'city' index is 0, and there is no index 2.
     * 'name' is accessed with element() method.
     * The tree is indexed from leaf to root.
     * 
     * @param index
     *            the position from the parent of the leaf.
     * @return
     */
    public Element getElement(int index) {
        return elementPath.get(index);
    }

    public Structure getStructure(int index) {
        return structurePath.get(index);
    }

    /**
     * Get the number of element between root and the current element.
     * 
     * @return
     */
    public int size() {
        return elementPath.size();
    }

    HierarchicalElement add(Structure struct, Element element) {
        elementPath.add(element);
        structurePath.add(struct);
        return this;
    }

    List<Structure> getStructurePath() {
        return structurePath;
    }

    List<Element> getElementPath() {
        return elementPath;
    }

    /**
     * Get the element path to go to the leaf element.
     * e.g. address.city.
     * 
     * @return the path with a final dot (.)
     *         String is empty if there is no hierarchical information (in this
     *         case, there is no final dot)
     */
    public String path() {
        StringBuilder sb = new StringBuilder();
        int pathSize = elementPath.size();
        for (int i = pathSize; i > 0; i--) {
            sb.append(elementPath.get(i - 1).name());
            sb.append(".");
        }
        return sb.toString();
    }

    /**
     * Get the name of the leaf element
     * e.g. 'name'
     * 
     * @return
     */
    public String name() {
        return element.name();
    }

    /**
     * Get the hierarchical name of the element.
     * e.g. address.city.name
     * 
     * @return
     */
    public String fullName() {
        return this.path() + element.name();
    }
}
