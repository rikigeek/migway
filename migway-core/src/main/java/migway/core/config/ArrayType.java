package migway.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="array")
public class ArrayType extends ElementType {
    @XmlElement
    private int first;
    @XmlElement
    private int last;
    @XmlElementRef(name="arrayElement")
    private ElementType arrayElement;

    /**
     * Set this element as an array
     * 
     * @param elementType
     *            the object type contained in the array
     * @param minCapacity
     *            minimal size of the array. 0 means no minimal limit
     * @param maxCapacity
     *            maximal size of the array. 0 means no maximal limit
     */
    ArrayType(ElementType elementType, int minCapacity, int maxCapacity) {
        this.arrayElement = elementType;
        this.first = minCapacity;
        this.last = maxCapacity;
    }

    // used for XML marshaling
    @SuppressWarnings("unused")
    private ArrayType() {}
    public ElementType getElementType() {
        return arrayElement;
    }
    
    public int getMin() {
        return first;
    }
    
    public int getMax() {
        return last;
    }

}
