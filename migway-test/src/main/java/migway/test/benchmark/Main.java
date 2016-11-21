package migway.test.benchmark;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import migway.core.config.ConfigHelper;
import migway.core.interfaces.FakeDdsInterface;
import migway.core.interfaces.FakeHlaInterface;
import migway.core.mapper.Mapper;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

public class Main {

    public static void main(String[] args) {
        Main main = new Main();
        try {
            main.runBenchmarks(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Route definition
     * 
     * @return
     */
    static RouteBuilder getRoutes() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                ConfigHelper config = ConfigHelper.loadConfig("migway:sample").appendConfig("migway:map-sample");
                // First route: straight forward route. ('etalonage')
                from("direct:in-straight").to("direct:out-straight");
                // Add couple of straight routes : no transformation, only
                // endpoint to
                // endpoint
                // add a Direct route (from in to out)
                from("direct:in-process").process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().getBody();
                    }
                }).to("direct:out-process");
                // Route with only buffer->pojo conversion
                from("direct:in-hlainterface").process(new FakeHlaInterface(config)).to("direct:out-hlainterface");
                // Route with Mapper and the pojo conversion to Byte buffer
                from("direct:in-fullroute").process(new FakeHlaInterface(config)).process(new Mapper(config))
                        .process(new FakeDdsInterface(config)).to("direct:out-fullroute");

                from("direct:in").process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().getBody();
                    }
                }).to("direct:out");
                from("direct:inhlainterface").process(new FakeHlaInterface(config)).to("direct:outhlapojo");
                from("direct:inhla2").process(new FakeHlaInterface(config)).process(new Mapper(config))
                        .process(new FakeDdsInterface(config)).to("direct:outddsbyte");
            }
        };
    }

    /**
     * function to start the benchmark process
     * 
     * @param iterationRate
     *            a rate to easily decrease or increase the total duration of
     *            the test
     * @throws Exception
     */
    public void runBenchmarks(double iterationRate) throws Exception {
        // Initialize Camel environment
        CamelBenchmark camelTest = new CamelBenchmark();

        // Add the routes
        camelTest.addRoutes(getRoutes());

        // start Camel
        camelTest.startCamel();

        // and do the tests
        ArrayList<PerfResult> camelResults = new ArrayList<PerfResult>();

        // First test: sending 1M Objects to straight route
        for (float i = 0.1F; i <= 10; i = i * 2) { // Progressive increase of
                                                   // the load
            // Calculate real number of messages to send. Depends on
            // iterationRate and the progressive increase of the load
            int nbIterations = (int) (1000000 * i * iterationRate);
            Object[] message = new Object[1000000];
            for (int j = 0; j < message.length; j++)
                message[j] = new Object();
            Object[] messages = new Object[nbIterations];
            for (int j = 0; j < messages.length; j++)
                messages[j] = message; // Simply copy reference object (no need
                                       // to duplicate it)
            PerfResult result = camelTest.execute("direct:in-straight", "direct:out-straight", nbIterations, messages,
                    new Hashtable<String, Object>());
            camelResults.add(result);
            result.description = "STRAIGHT 1M Objects";
        }

        // Second test: Sending A HLA Object
        // Header map
        Hashtable<String, Object> headers = new Hashtable<String, Object>();
        headers.put("ObjectClass", "edu.cyc14.essais.pojo.MyPojo");
        for (float i = 0.1F; i <= 10; i = i * 2) {
            ByteBuffer message = createHlaMessage();
            int nbIterations = (int) (100_000 * i * iterationRate);
            ByteBuffer[] messages = new ByteBuffer[nbIterations];
            for (int j = 0; j < nbIterations; j++)
                messages[j] = message.duplicate();
            PerfResult result = camelTest.execute("direct:in-fullroute", "direct:out-fullroute", nbIterations, messages, headers);
            camelResults.add(result);
            result.description = "HLA Objects through full route";
        }
        // Byte buffers don't need to be closed

        System.out.println(PerfResult.toCsvHeader());
        // Display tests results for Camel
        for (PerfResult result : camelResults) {
            if (result != null && result.description != null)
                System.out.println(result.toCsv());
        }
    }

    /**
     * Create a default FakeHlaMessage
     * 
     * @return a ByteBuffer
     */
    ByteBuffer createHlaMessage() {
        // Build the testingMessage that will receives all data
        ByteBuffer hlaMessage = ByteBuffer.allocate(150);
        // Store some data in the message
        hlaMessage.rewind();
        hlaMessage.put((byte) 1); // ok = true
        hlaMessage.putInt(42); // value = 42
        String name = "The answer";
        hlaMessage.putInt(name.length());
        for (int i = 0; i < name.length(); i++) {
            hlaMessage.putChar(name.charAt(i));
        } // name = "The answer";
        return hlaMessage;
    }
}
