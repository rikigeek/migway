package migway.test.benchmark;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import edu.cyc14.essais.pojo.MyPojo;

public class CamelBenchmarkTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return Main.getRoutes();
    }

    @Override
    protected void startCamelContext() throws Exception {
        context.addInterceptStrategy(new InterceptStrategy() {

            @Override
            public Processor wrapProcessorInInterceptors(CamelContext context, ProcessorDefinition<?> definition, Processor target,
                    Processor nextTarget) throws Exception {
                //
                return new DelegateAsyncProcessor(new Processor() {

                    @Override
                    public void process(Exchange exchange) throws Exception {
                        log.debug("Before " + target.toString() + " " + exchange.getIn().getBody().getClass().getName());
                        target.process(exchange);
                        log.debug("After... done");

                    }
                });
            }
        });
        super.startCamelContext();

    }

    @Test
    public void testProcessor() throws Exception {
        Main main = new Main();

        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                from("direct:out").log("direct:out");
                from("direct:test1").to("direct:testout");
                from("direct:outhlapojo").to("mock:outhlapojo");
                from("direct:outddsbyte").to("mock:outddsbyte");
            }
        });

        // consumer.
        // template.sendBody("direct:test1", "toto");

        NotifyBuilder notify;
        boolean matches ;
        notify = new NotifyBuilder(context).from("direct:in").whenAllDoneMatches(body().isInstanceOf(ByteBuffer.class)).create();

        template.sendBodyAndHeader("direct:in", main.createHlaMessage(), "ObjectClass", "edu.cyc14.essais.pojo.MyPojo");
        matches = notify.matches(1, TimeUnit.SECONDS);
        assertTrue(matches);

        notify = new NotifyBuilder(context).from("direct:inhlainterface").whenAllDoneMatches(body().isInstanceOf(MyPojo.class)).create();
        template.sendBodyAndHeader("direct:inhlainterface", main.createHlaMessage(), "ObjectClass", "edu.cyc14.essais.pojo.MyPojo");
        matches = notify.matches(1, TimeUnit.SECONDS);
        assertTrue(matches);
        
        notify = new NotifyBuilder(context).from("direct:inhla2").whenAllDoneMatches(body().isInstanceOf(ByteBuffer.class)).create();
        template.sendBodyAndHeader("direct:inhla2", main.createHlaMessage(), "ObjectClass", "edu.cyc14.essais.pojo.MyPojo");
        matches = notify.matches(1, TimeUnit.SECONDS);
        assertTrue(matches);

    }
}
