package migway.core.mapper;

import java.io.File;
import java.nio.ByteBuffer;

import migway.core.config.ConfigHelper;
import migway.core.interfaces.FakeDdsInterface;
import migway.core.interfaces.FakeHlaInterface;
import migway.core.mapper.Mapper;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import static migway.core.TestUtils.*;

public class FullRouteMapperTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            
            @Override
            public void configure() throws Exception {
                from (IN_HLA)
                    .process(new FakeHlaInterface())
                    .process(new Mapper(ConfigHelper.loadConfig(new File("migway:sample")).appendConfig("migway:map-sample")))
                    .process(new FakeDdsInterface())
                    .to(OUT_DDS);
            }
        };
    }
    
    @Test
    public void testFullRouteMapper() throws InterruptedException {
        ByteBuffer buf = ByteBuffer.allocate(MAX_BUFFER_SIZE);
        buf = fillHlaBuffer(buf);
        
        MockEndpoint ep = getMockEndpoint(OUT_DDS);
        
        ep.expectedMessageCount(1);
        ep.message(0).body().isInstanceOf(ByteBuffer.class);
        
        template.sendBodyAndHeader(IN_HLA, buf, "ObjectClass", HLA_POJO_CLASS);
        
        ep.assertIsSatisfied();
        
        Message m = ep.getExchanges().get(0).getIn();
        ByteBuffer receivedBuf = m.getBody(ByteBuffer.class);
        receivedBuf.rewind();
        assertEquals(hlaValue, receivedBuf.getInt());
        assertEquals(hlaOk, receivedBuf.get()!=0);
        StringBuilder sb = new StringBuilder();
        int descriptionSize = receivedBuf.getInt();
        for (int i = 0; i < descriptionSize; i++) {
            sb.append(receivedBuf.getChar());
        }
        assertEquals(hlaName, sb.toString());
    }
}
