package migway.plugins.dds;

import java.nio.ByteBuffer;

import migway.plugins.dds.DdsiProcessor;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Benchmark for DdsiProcessor
 * @author Sébastien Tissier
 *
 */
public class DdsMessageTest extends CamelTestSupport {
    private DdsiProcessor ddsiProcessor = new DdsiProcessor();

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        // TODO Auto-generated method stub
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:in").process(ddsiProcessor).to("mock:result");

            }
        };
    }

    /**
     * Test sending a ByteBuffer into DdsiProcessor, and check the result.
     */
    @Test
    public void testDdsiProcessor() {
        byte[] byteArray = DdsSamples.toByteBuffer(DdsSamples.PLATFORM_CAPABILITY_BUFFER_1);
        int iterations = 100;
        ByteBuffer body = ByteBuffer.wrap(byteArray);
        MockEndpoint ep = getMockEndpoint("mock:result");
        ep.expectedMessageCount(iterations);
        String headerValue = "LDM.platform.PlatformCapability_T";
        String headerName = "CamelDdsiTypeName";
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            template.sendBodyAndHeader("direct:in", body, headerName, headerValue);
        }
        long end = System.nanoTime();
        long delta = end - start;
        System.out.printf("%d iterations in %.6f s = %.3f ms per iteration, %.2f messages per second", iterations, delta/(float)1_000_000_000, ((float)delta/(float)iterations)/1_000_000, (float)iterations/(float)delta*1_000_000_000);
        try {
            ep.assertIsSatisfied();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
