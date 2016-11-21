package migway.core.config;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class StructureIterator implements Iterator<HierarchicalElement> {

    private int pos = -1;
    private List<Element> elements;
    private StructureIterator nestedStructure = null;
    private Structure currentStructure = null;

    public StructureIterator(List<Element> elements, Structure structure) {
        this.elements = elements;
        this.currentStructure = structure;
    }

    @Override
    public boolean hasNext() {
        boolean nestedHasNext = false;
        // If nestedStructure hasNext, then we continue with the nestedStructure
        if (nestedStructure != null)
            nestedHasNext = nestedStructure.hasNext();
        if (nestedHasNext)
            return true;
        // If nestedStructure is ended, we move to next element
        int next = pos + 1;
        if (next < elements.size())
            return true;
        else
            return false;
    }

    @Override
    public HierarchicalElement next() {
        // Objective: get the next element

        // First check if we are in a nested structure.
        if (nestedStructure != null) {
            // If we are in a nested structure, we check if this structure has a
            // next element
            if (nestedStructure.hasNext()) {
                // Nested structure has another element. Return it
                return nestedStructure.next().add(currentStructure, elements.get(pos));
            }
            // We reached the end of the nestedStructure
            // indicate we are out of a nested structure
            nestedStructure = null;
        }
        // if no next element is available in the nestedStructure, or we were
        // not in a nested structure
        // So we can increment position in our list
        pos++;
        // Throw an exception if we exceed size of local list
        if (pos >= elements.size())
            throw new NoSuchElementException();
        // Check if the element is a structure
        Element e = elements.get(pos);
        if (e.elementType() instanceof StructureRef) {
            // The element is a structureRef, but first check if we can access
            // to this referenced structure
            StructureRef ref = (StructureRef) e.elementType();
            Structure referencedStructure = Structure.structureCatalog.get(ref.getStructureName());
            if (referencedStructure == null) {
                // Check if it's a simple name. In this case, name is relative
                // to currentStructure package name
                String packageName = currentStructure.getPackageName();
                if (!packageName.isEmpty()) {
                    packageName += ".";
                    referencedStructure = Structure.structureCatalog.get(packageName + ref.getStructureName());
                } //No need to check again if there is no package name
            }
            // Throw an exception if the structure cannot be found
            if (referencedStructure == null)
                throw new NoSuchElementException("Structure referenced with name " + ref.getStructureName()
                        + " is not found in structureCatalog");
            // Structure is found.
            // Is this a ClassStructure? Then get the iterator, store it, and
            // return next element from this element
            if (referencedStructure instanceof ClassStructure) {
                nestedStructure = referencedStructure.iterator();
                return nestedStructure.next().add(currentStructure, elements.get(pos));
            } else {
                // Not a ClassStructure. So we simply return the element.
                return new HierarchicalElement(e);
            }
        } else {
            // Element is not a structure. So return it
            // TODO : special case for array?
            return new HierarchicalElement(e);
        }
    }

}
