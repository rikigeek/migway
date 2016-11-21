package migway.test.benchmark;

import java.util.Date;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to consume camel route
 * @author Sébastien Tissier
 *
 */
public class PerfTestConsumer extends Thread {
	// default TIMEOUT for a consumer
	private final int CONSUMER_TIMEOUT = 5000;
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	// The Camel Context
	private CamelContext camel;
	private ConsumerTemplate template;
	// The number of awaited messages
	private int iterations;
	
	// The storage of the result
	private PerfResult result;
	
	private String endPointUri;
	

	/** 
	 * Initialize a consumer thread
	 * @param camel The camel context
	 * @param endPointUri what to consume 
	 * @param iterations expected number of message to receive
	 * @param result Object to store performance results
	 */
	public PerfTestConsumer(CamelContext camel, String endPointUri, int iterations, PerfResult result) {
		this.camel = camel;
		this.template = camel.createConsumerTemplate();
		this.iterations = iterations;
		this.endPointUri = endPointUri;
		this.result = result;

	}

	/**
	 * Get the camel context
	 * @return the camel context
	 */
	public CamelContext getCamelContext() {
		return camel;
	}

	/**
	 * Thread entry point.
	 * It consume "iterations" number of message from the "endPointUri" end point
	 */
	@Override
	public void run() {
		// Starting the thread
		
		// initialize message to receive and some timers
		Date receivedFirst = new Date();
		Date sentFirst = null;
		Date receivedLast = null;
		Date sentLast = null;
		Object response = new Object();
		int i = 0;

		// START : store the start timer
		Date start = new Date();
		log.info("Start Consumer : " + start.getTime());
		// if we receive a null message, or if expected iterations is reached, we stop the loop 
		for (i = 0; i < iterations && response != null; i++) {
			try {
				// Receive the message from the End Point
				response = template.receiveBody(endPointUri, CONSUMER_TIMEOUT);
			} catch (Exception e) {
				log.warn("Iteration {} - Exception {}", i, e.getMessage());
				// Stop the loop if error
				response = null;
			}
			
			// Store reception time of the first message
			if (i == 0) {
				receivedFirst = new Date();
//				sentFirst = response.getSending();
				sentFirst = receivedFirst;
			}
			// Store reception time of the last message
			if (i == iterations - 1) {
				receivedLast = new Date();
//				sentLast = response.getSending();
				sentLast = receivedLast;
			}
		}
		// STOP : store stop time
		Date stop = new Date();
		log.debug("Consumer is ending @" + stop.getTime());
		// The test has been aborted if a response is null
		if (response == null) {
			result.aborted = true;
			log.warn("Aborted");
		}
		// Stop the consumer
		try {
			template.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Saving the measures
		result.startReceive = start;
		result.stopReceive = stop;
		result.totalIterations = i;
		result.firstMessageReceived = receivedFirst;
		result.firstMessageSent = sentFirst;
		result.lastMessageReceived = receivedLast;
		result.lastMessageSent = sentLast;
	}
}
