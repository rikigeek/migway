package migway.core.mapper;

import migway.core.config.ConfigHelper;
import migway.core.config.PojoTestLoader;
import migway.core.mapper.InPojo.InStruct;
import migway.core.mapper.OutPojo.OutStruct;

import org.apache.camel.Message;
import org.apache.camel.TypeConverter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MapperTest extends CamelTestSupport {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // Special way to load the type converter
        context.getTypeConverterRegistry().addTypeConverter(OutPojo.class, InPojo.class, new PojoConverter());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // Create a configuration for element mapping
                ConfigHelper elementMappingConfig = new ConfigHelper();
                PojoTestLoader.fillConfig(elementMappingConfig);
                PojoTestLoader.setMappingElementsLevel(elementMappingConfig);

                from("direct:pojoIn").process(new Mapper(elementMappingConfig)).to("mock:pojoOut");

                // create a configuration for structure mapping
                ConfigHelper structureMappingConfig = new ConfigHelper();
                PojoTestLoader.fillConfig(structureMappingConfig);
                PojoTestLoader.setMappingGenericStructureConfig1(structureMappingConfig);

                from("direct:structIn").process(new Mapper(structureMappingConfig)).to("mock:structOut");

                // create a configuration for structure mapping
                ConfigHelper genericMappingConfig = new ConfigHelper();
                PojoTestLoader.fillConfig(genericMappingConfig);
                PojoTestLoader.setMappingGenericStructureConfig2(genericMappingConfig);

                from("direct:genericIn").process(new Mapper(genericMappingConfig)).to("mock:genericOut");

                // create a configuration for structure mapping
                ConfigHelper arrayMappingConfig = new ConfigHelper();
                PojoTestLoader.fillConfig(arrayMappingConfig);
                PojoTestLoader.setMappingArrayStructureConfig(arrayMappingConfig);

                from("direct:arrayIn").process(new Mapper(arrayMappingConfig)).to("mock:arrayOut");
            }
        };
    }

    @Test
    public void testTypeConverterLoading() {
        TypeConverter conv = context.getTypeConverterRegistry().lookup(OutPojo.class, InPojo.class);
        // List<Class<?>[]> list =
        // context.getTypeConverterRegistry().listAllTypeConvertersFromTo();
        assertNotNull(conv);
    }

    /**
     * Test "classic" mapping (mapping between elements of a structure).
     * Mapping support referencing element through hierarchical name ("inA.sA")
     * 
     * @throws InterruptedException
     */
    @Test
    public void testRecursiveMapping() throws InterruptedException {
        InPojo i = new InPojo();
        i.inA = new InStruct();
        i.inA.sA = 3;
        i.inA.sB = 4;
        i.inA.sC = new InStruct();
        i.inA.sC.sA = 5;
        i.inB = 1;
        i.inC = 2;

        MockEndpoint ep = getMockEndpoint("mock:pojoOut");

        ep.expectedMessageCount(1);

        template.sendBody("direct:pojoIn", i);

        ep.assertIsSatisfied();

        // Test message body is an OutputPojo
        Message msg = ep.getExchanges().get(0).getIn();
        assertNotNull(msg);
        assertTrue(msg.getBody() instanceof OutPojo);

        OutPojo o = ep.getReceivedExchanges().get(0).getIn().getBody(OutPojo.class);
        assertNotNull(o);

        assertEquals(1, o.outA.sA);
        assertEquals(2, o.outC);
        assertEquals(3, o.outB);
        assertEquals(4, o.outA.sB);
        assertEquals(3, o.outA.sC.sD);
        assertEquals(5, o.outA.sD);
        assertEquals(3, o.outD.sA);
        assertEquals(4, o.outD.sB);
        assertEquals(5, o.outD.sC.sA);
    }

    @Test
    public void testPojoToPojoMapping() throws InterruptedException {
        InPojo i = new InPojo();
        i.inA = new InStruct();
        i.inA.sA = 3;
        i.inA.sB = 4;
        i.inA.sC = new InStruct();
        i.inA.sC.sA = 5;
        i.inA.sC.sB = 6;
        i.inA.sC.sC = null;
        i.inA.sC.sD = 7;
        i.inA.sD = 8;
        i.inB = 1;
        i.inC = 2;
        i.inD = new OutStruct();
        i.inD.sA = 101;
        i.inD.sB = 102;

        MockEndpoint ep = getMockEndpoint("mock:structOut");
        // First try: Mapping entire structure into entire structure by sending
        // InPojo
        ep.expectedMessageCount(1);

        template.sendBody("direct:structIn", i);
        ep.assertIsSatisfied();
        Message msg = ep.getExchanges().get(0).getIn();
        assertNotNull(msg);
        assertTrue(msg.getBody() instanceof OutPojo);
        OutPojo o = ep.getExchanges().get(0).getIn().getBody(OutPojo.class);

        assertEquals(i.inB + i.inC, o.outB);
        assertEquals(i.inB - i.inC, o.outC);

    }

    @Test
    public void testStructToPojoMapping() throws InterruptedException {
        MockEndpoint epGeneric = getMockEndpoint("mock:genericOut");
        // This part is ignored
        OutPojo o = new OutPojo();
        o.outB = 12;
        o.outC = 15;
        o.outA = new OutStruct();
        o.outA.sA = 18;
        o.outA.sB = 22;
        // only outE is mapped to InPojo object
        o.outE = new InPojo();
        o.outE.inB = 35;
        o.outE.inC = 99;
        o.outE.inA = new InStruct();
        o.outE.inA.sA = 1256;
        o.outE.inA.sB = 26;

        epGeneric.expectedMessageCount(1);

        template.sendBody("direct:genericIn", o);

        epGeneric.assertIsSatisfied();

        InPojo i = epGeneric.getExchanges().get(0).getIn().getBody(InPojo.class);

        assertNotNull(i);
        assertEquals(35, i.inB);
        assertEquals(99, i.inC);
        assertNotNull(i.inA);
        assertEquals(1256, i.inA.sA);
        assertEquals(26, i.inA.sB);
        assertNull(i.inD);
        assertNull(i.inE);
        assertNull(i.inA.sC);
    }

    @Test
    public void testPojoToStructMapping() throws InterruptedException {
        MockEndpoint ep = getMockEndpoint("mock:structOut");

        // Second try: sending OutPojo that will be converted into inE of InPojo
        ep.expectedMessageCount(1);

        OutPojo o = new OutPojo();
        o.outB = 12;
        o.outC = 15;
        o.outA = new OutStruct();
        o.outA.sA = 18;
        o.outA.sB = 22;

        template.sendBody("direct:structIn", o);

        ep.assertIsSatisfied();

        InPojo i = ep.getExchanges().get(0).getIn().getBody(InPojo.class);

        // only inE field is set
        assertNotNull(i.inE);
        assertEquals(18, i.inE.outA.sA);
        assertEquals(22, i.inE.outA.sB);
        assertEquals(12, i.inE.outB);
        assertEquals(15, i.inE.outC);
        assertNull(i.inA);

    }

    @Test
    public void testPojoToArrayMapping() throws Exception {
        /*
         * InPojo(inB) -> OutPojo(outB)
         * inC -> outArrayB
         * inD(sA) -> outArrayA(sA)
         */
        MockEndpoint ep = getMockEndpoint("mock:arrayOut");

        // Second try: sending OutPojo that will be converted into inE of InPojo
        ep.expectedMessageCount(1);

        InPojo i = new InPojo();
        i.inA = new InStruct();
        i.inA.sA = 3;
        i.inA.sB = 4;
        i.inA.sC = new InStruct();
        i.inA.sC.sA = 5;
        i.inA.sC.sB = 6;
        i.inA.sC.sC = null;
        i.inA.sC.sD = 7;
        i.inA.sD = 8;
        i.inB = 1; // pojo key
        i.inC = 2; // -> outArrayB
        i.inD = new OutStruct(); // -> outArrayA 
        i.inD.sA = 101; // element key
        i.inD.sB = 102;

        template.sendBody("direct:arrayIn", i);
        ep.assertIsSatisfied();
        OutPojo o = ep.getExchanges().get(0).getIn().getBody(OutPojo.class);
        // only inE field is set
        assertNull(o.outA);
        assertEquals(0, o.outB);
        assertEquals(0, o.outC);
        assertNull(o.outD);
        assertNull(o.outE);
        assertNotNull(o.outArrayA);
        assertNotNull(o.outArrayB);
        assertEquals(1, o.outArrayA.length);
        assertEquals(101, o.outArrayA[0].sA);
        assertEquals(102, o.outArrayA[0].sB);
        assertEquals(1, o.outArrayB.length);
        assertEquals(2, o.outArrayB[0]);

        i = new InPojo();
        // keep same pojo key
        i.inB = 1; // pojo key
        i.inC = 2; // -> outArrayB
        i.inD = new OutStruct(); // -> outArrayA
        i.inD.sA = 109; // change element key
        i.inD.sB = 909; 

        template.sendBody("direct:arrayIn", i);

        ep.expectedMessageCount(2);
        ep.assertIsSatisfied();
        o = ep.getExchanges().get(1).getIn().getBody(OutPojo.class);

        assertNull(o.outA);
        assertEquals(0, o.outB);
        assertEquals(0, o.outC);
        assertNull(o.outD);
        assertNull(o.outE);
        assertNotNull(o.outArrayA);
        assertNotNull(o.outArrayB);
        assertEquals(2, o.outArrayA.length);
        assertEquals(101, o.outArrayA[0].sA);
        assertEquals(102, o.outArrayA[0].sB);
        assertEquals(109, o.outArrayA[1].sA);
        assertEquals(909, o.outArrayA[1].sB);
        assertEquals(2, o.outArrayB.length);
        assertEquals(2, o.outArrayB[0]);
        assertEquals(2, o.outArrayB[1]);
        
        i = new InPojo();
        // keep same pojo key
        i.inB = 1; // pojo key
        i.inC = 2; // -> outArrayB
        i.inD = new OutStruct(); // -> outArrayA
        i.inD.sA = 101; // change element key
        i.inD.sB = 908; 

        template.sendBody("direct:arrayIn", i);

        ep.expectedMessageCount(3);
        ep.assertIsSatisfied();
        o = ep.getExchanges().get(2).getIn().getBody(OutPojo.class);

        assertNull(o.outA);
        assertEquals(0, o.outB);
        assertEquals(0, o.outC);
        assertNull(o.outD);
        assertNull(o.outE);
        assertNotNull(o.outArrayA);
        assertNotNull(o.outArrayB);
        assertEquals(2, o.outArrayA.length);
        assertEquals(101, o.outArrayA[0].sA);
        assertEquals(908, o.outArrayA[0].sB);
        assertEquals(109, o.outArrayA[1].sA);
        assertEquals(909, o.outArrayA[1].sB);
        assertEquals(3, o.outArrayB.length);
        assertEquals(2, o.outArrayB[0]);
        assertEquals(2, o.outArrayB[1]);
        assertEquals(2, o.outArrayB[2]);
    }
}
