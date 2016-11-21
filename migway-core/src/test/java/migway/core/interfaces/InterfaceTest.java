package migway.core.interfaces;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import migway.core.interfaces.FakeDdsInterface;
import migway.core.interfaces.FakeHlaInterface;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static migway.core.TestUtils.*;

public class InterfaceTest extends CamelTestSupport {



    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from(IN_HLA).process(new FakeHlaInterface()).to(OUT_HLA);
                from(IN_DDS).process(new FakeDdsInterface()).to(OUT_DDS);
            }
        };
    }

    @Test
    public void testDdsPojo() throws NoSuchFieldException, SecurityException, InterruptedException, IllegalArgumentException,
            IllegalAccessException {
        ByteBuffer ddsStruct = ByteBuffer.allocate(MAX_BUFFER_SIZE);

        ddsStruct = fillDdsBuffer(ddsStruct);

        MockEndpoint ep = getMockEndpoint(OUT_DDS);

        ep.expectedMessageCount(1);

        template.sendBodyAndHeader(IN_DDS, ddsStruct, "TopicClass", "edu.cyc14.essais.pojo.DdsPojo");

        ep.assertIsSatisfied();

        // check result is a DdsPojo class
        Object result = ep.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals("edu.cyc14.essais.pojo.DdsPojo", result.getClass().getName());

        // test content of the pojo
        Class<?> resultClass = result.getClass();

        Field numberField = resultClass.getField("number");
        Field descriptionField = resultClass.getField("description");
        Field precisionField = resultClass.getField("precision");
        Field acknowledgedField = resultClass.getField("acknowledged");

        assertEquals(42, numberField.getInt(result));
        assertEquals("Answer", descriptionField.get(result));
        assertEquals(0.95f, precisionField.getFloat(result), 0.0f);
        assertFalse(acknowledgedField.getBoolean(result));

    }

    @Test
    public void testHlaPojo() throws InterruptedException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
            NoSuchFieldException, SecurityException {
        // Larger than needed
        ByteBuffer hlaStruct = ByteBuffer.allocate(MAX_BUFFER_SIZE); 
        hlaStruct = fillHlaBuffer(hlaStruct);

        MockEndpoint ep = getMockEndpoint(OUT_HLA);

        ep.expectedMessageCount(1);
        // This method can't work because the pojos are
        // ep.message(0).body().isInstanceOf(Class.forName("edu.cyc14.essais.pojo.MyPojo"));

        template.sendBodyAndHeader(IN_HLA, hlaStruct, "ObjectClass", "edu.cyc14.essais.pojo.MyPojo");
        // template.sendBody("direct:simhla", hlaStruct);
        ep.assertIsSatisfied();
        // Check result is a MyPojo class
        Object result = ep.getReceivedExchanges().get(0).getIn().getBody();
        assertEquals("edu.cyc14.essais.pojo.MyPojo", result.getClass().getName());

        // Test content of the pojo
        Class<?> resultClass = result.getClass();

        Field nameField = resultClass.getField("name");
        Field valueField = resultClass.getField("value");
        Field okField = resultClass.getField("ok");

        assertEquals("Command", nameField.get(result));
        assertEquals(66, valueField.getInt(result));
        assertTrue(okField.getBoolean(result));
    }


}
