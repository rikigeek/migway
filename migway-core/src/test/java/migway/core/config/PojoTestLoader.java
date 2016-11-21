package migway.core.config;

import java.util.ArrayList;
import java.util.List;

import migway.core.config.ElementMapping.ElementKey;

public class PojoTestLoader {
    public static void fillConfig(ConfigHelper config) {
        ClassStructure s = new ClassStructure("migway.core.mapper.InPojo");
        s.addClass("inA", null, null, "migway.core.mapper.InPojo$InStruct");
        s.addBasic("inB", null, null, BasicTypeEnum.INT);
        s.addBasic("inC", null, null, BasicTypeEnum.INT);
        config.addStructure(s);
        s = new ClassStructure("migway.core.mapper.OutPojo");
        s.addClass("outA", null, null, "migway.core.mapper.OutPojo$OutStruct");
        s.addBasic("outB", null, null, BasicTypeEnum.INT);
        s.addBasic("outC", null, null, BasicTypeEnum.INT);
        config.addStructure(s);
        s = new ClassStructure("migway.core.mapper.OutPojo$OutStruct");
        s.addBasic("sA", null, null, BasicTypeEnum.INT);
        s.addBasic("sB", null, null, BasicTypeEnum.INT);
        config.addStructure(s);
        s = new ClassStructure("migway.core.mapper.InPojo$InStruct");
        s.addBasic("sA", null, null, BasicTypeEnum.INT);
        s.addBasic("sB", null, null, BasicTypeEnum.INT);
        config.addStructure(s);
    }

    /**
     * Create the following configuration mapping (InPojo -> OutPojo): <code>
     * <ul>
     * <li>inB -> outA.sA
     * <li>inA.sB -> outA.sB  
     * <li>inA.s1 -> outA.sC.sD
     * <li>inA.sC.sA -> outA.sD
     * <li>inA.sA -> outB  
     * <li>inC -> outC
     * <li>inA -> outD
     * </ul></code> {@code outA} and {@code inD} are {@code OutStruct} type.<br>
     * {@code inA} and {@code outD} are {@code InStruct} type.<br>
     * {@code OutStruct.sC} is {@code OutStruct} type.<br>
     * {@code InStruct.sC} is {@code InStruct} type.<br>
     * 
     * @param config
     */
    public static void setMappingElementsLevel(ConfigHelper config) {
        // Mapping
        List<Mapping> list = new ArrayList<Mapping>();
        Mapping m = new Mapping();
        m.setDestination("migway.core.mapper.OutPojo");
        m.addSource("migway.core.mapper.InPojo", null);

        ElementMapping em = new ElementMapping();
        ElementKey keyIn = new ElementKey();
        ElementKey keyOut = new ElementKey();

        keyIn.elementName = "inB";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outA.sA";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inA.sA";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outB";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inA.sB";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outA.sB";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inC";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outC";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inA.sA";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outA.sC.sD";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inA.sC.sA";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outA.sD";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inA";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "outD";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        list.add(m);

        config.setMappings(list);
    }

    /**
     * Configure a mapping of Structure {@code InPojo} into {@code OutPojo},
     * without element assignment. A TypeConverter will then be used.
     * 
     * @param config
     */
    public static void setMappingGenericStructureConfig1(ConfigHelper config) {
        // Mapping
        List<Mapping> list = new ArrayList<Mapping>();
        Mapping m = new Mapping();
        ElementMapping em;
        ElementKey keyIn;
        ElementKey keyOut;

        m.setDestination("migway.core.mapper.OutPojo");
        m.addSource("migway.core.mapper.InPojo", null);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "*";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        keyOut.elementName = "*";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);

        list.add(m);

        m = new Mapping();
        m.setDestination("migway.core.mapper.InPojo");
        m.addSource("migway.core.mapper.OutPojo", null);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "*";
        keyIn.structureName = "migway.core.mapper.OutPojo";
        em.setSource(keyIn);
        keyOut.elementName = "inE";
        keyOut.structureName = "migway.core.mapper.InPojo";
        em.setDestination(keyOut);

        m.add(em);

        list.add(m);

        config.setMappings(list);
    }

    public static void setMappingGenericStructureConfig2(ConfigHelper config) {
        List<Mapping> list = new ArrayList<Mapping>();
        Mapping m = new Mapping();
        ElementMapping em;
        ElementKey keyIn;
        ElementKey keyOut;

        m.setDestination("migway.core.mapper.InPojo");
        m.addSource("migway.core.mapper.OutPojo", null);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "outE";
        keyIn.structureName = "migway.core.mapper.OutPojo";
        em.setSource(keyIn);
        keyOut.elementName = "*";
        keyOut.structureName = "migway.core.mapper.InPojo";
        em.setDestination(keyOut);

        m.add(em);

        list.add(m);

        config.setMappings(list);
    }

    /**
     * InPojo(inB) -> OutPojo(outB)
     * inC -> outArrayB
     * inD(sA) -> outArrayA(sA)
     * @param config
     */
    public static void setMappingArrayStructureConfig(ConfigHelper config) {
        List<Mapping> list = new ArrayList<Mapping>();
        Mapping m = new Mapping();
        ElementMapping em;
        ElementKey keyIn;
        ElementKey keyOut;
        
        KeyModel key = new KeyModel();

        // Premier mapping, int vers un int[]. Tous les entiers sont ajoutés
        key.addKeyName("outB");
        m.setDestination("migway.core.mapper.OutPojo");
        m.setDestinationKey(key);

        key = new KeyModel();
        key.addKeyName("inB");
        m.addSource("migway.core.mapper.InPojo", key);

        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();

        keyIn.elementName = "inC";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        
        keyOut.elementName = "outArrayB";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);

        m.add(em);
        
        em = new ElementMapping();
        keyIn = new ElementKey();
        keyOut = new ElementKey();
        
        keyIn.elementName = "inD";
        keyIn.structureName = "migway.core.mapper.InPojo";
        em.setSource(keyIn);
        key = new KeyModel();
        key.addKeyName("sA");
        em.setSourceKey(key);
        
        keyOut.elementName = "outArrayA";
        keyOut.structureName = "migway.core.mapper.OutPojo";
        em.setDestination(keyOut);
        key = new KeyModel();
        key.addKeyName("sA");
        em.setDestinationKey(key);
        
        m.add(em);

        list.add(m);
        
        config.setMappings(list);
    }

}
