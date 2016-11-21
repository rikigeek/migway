package migway.test.benchmark;

import java.util.Date;

/**
 * Object to store performance result
 * 
 * @author Sébastien Tissier
 *
 */
public class PerfResult {
    /**
     * Description of the test (from where to where, computer where the test run
     * on)
     */
    public String description;
    /**
     * Total iterations used for the test (number of messages)
     */
    public int totalIterations = 1;

    /**
     * Time for the first message to go through CAMEL (from sending to
     * receiving)
     */
    public long firstMessageDuration() {
        if (firstMessageSent != null && firstMessageReceived != null)
            return firstMessageReceived.getTime() - firstMessageSent.getTime();
        return -1;
    };

    public Date firstMessageSent;
    public Date firstMessageReceived;

    /**
     * Time for the last message to go through CAMEL (from sending to receiving)
     */
    public long lastMessageDuration() {
        if (lastMessageSent != null && lastMessageReceived != null)
            return lastMessageReceived.getTime() - lastMessageSent.getTime();
        return -1;
    };

    public Date lastMessageSent;
    public Date lastMessageReceived;
    /**
     * Time the sending thread started (to calculate the offset between the two
     * threads = the time the receiving/sending thread had to wait)
     */
    public Date startSend;
    public Date stopSend;

    public long startSendTime() {
        return (startSend != null) ? startSend.getTime() : -1;
    }

    public long stopSendTime() {
        return (stopSend != null) ? stopSend.getTime() : -1;
    }

    /**
     * Duration of the sending thread
     */
    public long totalSendDuration() {
        if (stopSend != null && startSend != null)
            return stopSend.getTime() - startSend.getTime();
        return -1;
    };

    /**
     * Time the receiving thread started (to calculate the offset between the
     * two threads = the time the receiving/sending thread had to wait)
     */
    public Date startReceive;
    public Date stopReceive;

    public long startReceiveTime() {
        if (startReceive != null)
            return startReceive.getTime();
        return -1;
    }

    public long stopReceiveTime() {
        if (stopReceive != null)
            return stopReceive.getTime();
        return -1;
    }

    /**
     * Duration of the receiving thread
     */
    public long totalReceiveDuration() {
        if (stopReceive != null && startReceive != null)
            return stopReceive.getTime() - startReceive.getTime();
        return -1;
    };

    /**
     * The average time for a message to be sent
     */
    public double averageMessageTime() {
        return (double) totalSendDuration() / (double) totalIterations;
    };

    /**
     * Was the test aborted ?
     */
    public boolean aborted = false;
    /**
     * Eventual exception thrown during the test
     */
    public Exception exception;

    /**
     * Default result printout
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(description).append(" : ").append(System.lineSeparator());
        sb.append(String.format("Start : SND = %d ms | RCV = %d ms", startSendTime(), startReceiveTime())).append(System.lineSeparator());
        sb.append(String.format("Stop  : SND = %d ms | RCV = %d ms", stopSendTime(), stopReceiveTime())).append(System.lineSeparator());
        sb.append(
                String.format("Time  : SND = %d ms | RCV = %d ms | OFFSET = %d ms", totalSendDuration(), totalReceiveDuration(),
                        startSendTime() - startReceiveTime())).append(System.lineSeparator());
        sb.append(String.format("Iterations : %d | Average : %.3f ms | %.3f msg/s SENT | %.3f msg/s RECEIVED", totalIterations, averageMessageTime(), rateMessages(), rateReceivedMessages()))
                .append(System.lineSeparator());
        sb.append(String.format("Message : FIRST = %d ms | LAST = %d ms", firstMessageDuration(), lastMessageDuration())).append(
                System.lineSeparator());

        return sb.toString();
    }

    /**
     * Rate of message send per second
     * 
     * @return
     */
    private double rateMessages() {
        return totalIterations / totalSendDuration() *1000;
    }
    
    /**
     * Rate of message received per second
     * 
     * @return
     */
    private double rateReceivedMessages() {
        return totalIterations / totalReceiveDuration() * 1000;
    }

    /**
     * Get the CSV header
     * 
     * @return
     */
    static public String toCsvHeader() {
        return "Description;Aborted;Iterations;SNDStart;SNDStop;RCVStart;RCVStop;SNDTime;RCVTime;FIRSTTime;LASTTime;AVGTime";
    }

    /**
     * Export the result as CSV row
     * 
     * @return
     */
    public String toCsv() {
        return String.format("%s;%s;%d;%d;%d;%d;%d;%d;%d;%d;%d;%.3f", description, aborted, totalIterations, startSendTime(),
                stopSendTime(), startReceiveTime(), stopReceiveTime(), totalSendDuration(), totalReceiveDuration(), firstMessageDuration(),
                lastMessageDuration(), averageMessageTime());
    }

}
