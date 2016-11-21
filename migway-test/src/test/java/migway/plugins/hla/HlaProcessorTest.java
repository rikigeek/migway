package migway.plugins.hla;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import migway.core.config.ConfigHelper;
import migway.core.samples.HlaSamples;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class HlaProcessorTest extends CamelTestSupport {
    HlaProcessor hlaProcessor;



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // TODO Auto-generated method stub
        return new RouteBuilder() {

            HlaTestProcessor hlaProcessor = new HlaTestProcessor(ConfigHelper.loadConfig("migway:hla-RPRFOM.edu"), new File(HlaTestProcessor.DEFAULT_FOM_FILE));
            @Override
            public void configure() throws Exception {
                from("direct:testin").setHeader("HlaClass", constant("BaseEntity"))
                        .process(hlaProcessor).to("mock:out");
                from ("direct:testpojo").process(hlaProcessor).to("mock:pojoout");
                
            }
        };
    }

    @Test
    public void testDecodeBaseEntity() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:out");
        mock.expectedMessageCount(1);
        
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        map.put("Spatial", HlaSamples.getHlaBufferSpatialVariantStruct());
        assertNotNull(map.keySet());
        template.sendBody("direct:testin", map);
        mock.assertIsSatisfied();
        List<Exchange> receives =  mock.getReceivedExchanges();
        assertEquals(1, receives.size());
        assertEquals("edu.cyc14.essais.pojo.rprfom.BaseEntity", receives.get(0).getIn().getBody().getClass().getName());
        Object baseEntity = receives.get(0).getIn().getBody();
        Field spatial = baseEntity.getClass().getField("spatial");
        Field relativeSpatial = baseEntity.getClass().getField("relativeSpatial");
        Object spatialValue = spatial.get(baseEntity);
        Object relativeSpatialValue = relativeSpatial.get(baseEntity);
        assertNull(relativeSpatialValue);
        assertNotNull(spatialValue);
    }
    
    @Test
    public void testEncodeBaseEntity() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:pojoout");
        mock.expectedMessageCount(1);
        
        Object pojo = HlaSamples.getHlaBaseEntity();
        template.sendBody("direct:testpojo", pojo);
        mock.assertIsSatisfied();
        
        List<Exchange> receives =  mock.getReceivedExchanges();
        assertEquals(1, receives.size());
        assertTrue(receives.get(0).getIn().getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, byte[]> content = (Map<String, byte[]>) receives.get(0).getIn().getBody();
        
        assertEquals(1, content.size());
        assertTrue(content.containsKey("Spatial"));
        byte[] buf = content.get("Spatial");
        assertTrue(Arrays.equals(buf, HlaSamples.getHlaBufferSpatialVariantStruct()));
    }

}
