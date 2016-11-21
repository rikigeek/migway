package migway.core.mapper;

import static migway.core.TestUtils.IN_HLA;
import static migway.core.TestUtils.MAX_BUFFER_SIZE;
import static migway.core.TestUtils.OUT_DDS;
import static migway.core.TestUtils.hlaName;
import static migway.core.TestUtils.hlaOk;
import static migway.core.TestUtils.hlaValue;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import migway.core.TestUtils;
import migway.core.config.ConfigHelper;
import migway.core.interfaces.FakeHlaInterface;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ConfigMapperTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from(IN_HLA).process(new FakeHlaInterface())
                        .process(new Mapper(ConfigHelper.loadConfig("migway:sample").appendConfig("migway:map-sample"))).to(OUT_DDS);
            }
        };
    }

    @Test
    public void testMapper() throws ClassNotFoundException, InterruptedException, InstantiationException, IllegalAccessException,
            NoSuchFieldException, SecurityException {
        MockEndpoint ep = getMockEndpoint(OUT_DDS);

        ep.expectedMessageCount(1);
        ep.message(0).body().isNotNull();

        // Fail to test using isInstanceOf. Probably because Class<?> cannot be
        // checked with instanceof
        // Class<?> expectedType = new
        // PojoClassLoader().loadClass("edu.cyc14.essais.pojo.DdsPojo");
        // Object expectedObjectType = expectedType.newInstance();
        // Why doesn't it work?
        // ep.message(0).body().isInstanceOf(expectedObjectType.getClass());
        // ep.message(0).body().isInstanceOf(expectedType);

        template.sendBodyAndHeader(IN_HLA, TestUtils.fillHlaBuffer(ByteBuffer.allocate(MAX_BUFFER_SIZE)), "ObjectClass",
                "edu.cyc14.essais.pojo.MyPojo");

        Object ddsPojo = ep.getExchanges().get(0).getIn().getBody();
        Class<?> ddsPojoClass = ddsPojo.getClass();
        assertEquals("edu.cyc14.essais.pojo.DdsPojo", ddsPojoClass.getName());
        ep.assertIsSatisfied();

        // Check POJO content
        Field numberField = ddsPojoClass.getField("number");
        Field descriptionField = ddsPojoClass.getField("description");
        Field acknowledgedField = ddsPojoClass.getField("acknowledged");

        assertEquals(hlaName, descriptionField.get(ddsPojo));
        assertEquals(hlaValue, numberField.get(ddsPojo));
        assertEquals(hlaOk, acknowledgedField.get(ddsPojo));

    }

}
