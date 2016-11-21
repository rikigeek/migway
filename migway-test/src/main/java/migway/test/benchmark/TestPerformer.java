package migway.test.benchmark;

import java.util.Date;
import java.util.Hashtable;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.impl.DefaultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPerformer {
    private static final Logger log = LoggerFactory.getLogger(TestPerformer.class);

    private CamelContext camel;
    public TestPerformer(CamelContext camel) {
        this.camel = camel;
    }

    /**
     * Run a benchmark
     * @param inputEndpoint is the endpoint to send the messages to
     * @param outputEndpoint is the endpoint that will receive messages (the endpoint to consume message)
     * @param iterations is the number of messages to send (to load the application)
     * @param messagesBody is an array of object to send
     * @param messageHeaders is the table of headers to send with the body
     * @param result is the object that will store results (this object must be created, and will be passed to the consumer)
     * @return
     */
    public PerfResult run(String inputEndpoint, String outputEndpoint, int iterations, Object messagesBody[], Hashtable<String, Object> messageHeaders, PerfResult result) {
        log.info("Run Test : " + result.description + " - " + iterations + " iterations");
        // Prepare to receive. It's important to consume the output. Otherwise,
        // there is a timeout that make really poor performances)
        PerfTestConsumer consumer = new PerfTestConsumer(camel, outputEndpoint, iterations, result);
        consumer.start(); // Start the consumer thread

        // Prepare to send
        ProducerTemplate producer = camel.createProducerTemplate();

        // Wait a little for the producer to be started
        try {
            Thread.sleep(200);
        } catch (InterruptedException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        
        // START : store the start timer
        Date start = new Date();
        log.trace("@{} ms - Starting", start.getTime());
        start = new Date();
        for (int i = 0; i < iterations; i++) {
            Exchange exchange = new DefaultExchange(camel, ExchangePattern.InOnly);
            Message message = new DefaultMessage();
            message.setHeaders(messageHeaders);
            message.setBody(messagesBody[i]);
            exchange.setIn(message);
            // Send the Exchange (with a unique message into it)
            producer.send(inputEndpoint, exchange);
        }
        // STOP : store the stop timer
        Date stop = new Date();

        // Store performance timer in result
        result.stopSend = stop;
        result.startSend = start;

        // Wait for the consumer to be stopped : everything must be consumed
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        while (consumer.isAlive())
            consumer.interrupt();
        // Sleep until consumer thread is stopped
        try {
            consumer.join();
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // Free the resources : stop the producer
        try {
            producer.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
        
    }
}
