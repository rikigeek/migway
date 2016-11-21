package migway.plugins.dds;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Map.Entry;

import migway.core.config.ConfigHelper;
import migway.core.config.ElementType;
import migway.core.interfaces.ComponentInterface;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.prismtech.gateway.camelddsi.DdsiException;


/**
 * Interface with DDSI component.
 * TODO this implementation is not finished. All output are wrong
 * 
 * @author Sébastien Tissier
 *
 */
public class DdsiProcessor extends ComponentInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(DdsiProcessor.class);
    // Input buffer
    private DdsiInputStream is;
    // Output buffer
    private DdsiOutputStream os;
    @SuppressWarnings("unused")
    private Exchange savedExchange;

    /* *********************
     * Utils debug functions
     * 
     * ********************
     */
    private void debugMessageHeader(Message msg) {
        for (Entry<String, Object> entry : msg.getHeaders().entrySet()) {
            LOGGER.debug(String.format("Header %s : %s (%s)\n", entry.getKey(), entry.getValue().getClass().toString(), entry.getValue()
                    .toString()));
        }
    }

    public void debugBuffer(ByteBuffer buffer) {
        debugBuffer(buffer, false);
    }

    /**
     * Dump out the content of the buffer. Doesn't change the buffer position.
     * 
     * @param buffer
     * @param toString
     *            when true output can be reused in a byte array.
     *            If false, byte are output as hexadecimal values in a field of
     *            brackets
     *            [00][00]...
     *            More human readable
     */
    public void debugBuffer(ByteBuffer buffer, boolean toString) {
        buffer.mark();
        StringBuffer sb = new StringBuffer("Buffer content: ");
        if (toString) {
            while (buffer.hasRemaining()) {
                sb.append(String.format("0x%02x,", buffer.get()));
            }
        } else {
            int offset = 0;
            while (buffer.hasRemaining()) {
                if (offset % 8 == 0) {
                    sb.append(String.format("\n  %03d: ", offset));
                }
                sb.append(String.format("[%02x]", buffer.get()));
                offset++;
            }

        }
        LOGGER.debug(sb.toString());

        buffer.reset();
    }

    /* *************************
     * End utils debug functions
     * 
     * ************************
     */

    public DdsiProcessor() {
        super(ConfigHelper.loadConfig(new File("migway:dds-GVA.LDM")));
    }

    public DdsiProcessor(ConfigHelper config) {
        super(config);
    }

    @Override
    protected boolean validate(Exchange exchange) {
        savedExchange = exchange;
        debugMessageHeader(exchange.getIn());
        Boolean isBody = exchange.getIn().getHeader("CamelDdsiIsBodyValid", Boolean.TRUE, Boolean.class);
        return isBody;
    }

    @Override
    protected String setHeaderClassName() {
        return "CamelDdsiTypeName";
    }

    @Override
    protected String setInterfaceTypeName() {
        return "DDS";
    }

    @Override
    protected int maxBufferSize(Object pojo) {
        return 3000;
    }

    @Override
    protected boolean initializeInputBuffer(Object buf) {
        ByteBuffer buffer = (ByteBuffer) buf;
        // Dump out the buffer content, to be reused in source code
        debugBuffer(buffer, true);
        // this one is for debugging
        debugBuffer(buffer, false);
//        buffer.rewind();

        // Data d = buffer.getData_value();
        // d.

        is = null;
        try {
            is = new DdsiInputStream(buffer);
        } catch (DdsiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (is != null);
    }

    @Override
    protected boolean readBoolean() {
        return is.read_boolean();
    }

    @Override
    protected byte readByte() {
        return is.read_octet();
    }

    @Override
    protected short readShort() {
        return is.read_short();
    }

    @Override
    protected int readInt() {
        return is.read_long();
    }

    @Override
    protected long readLong() {
        return is.read_longlong();
    }

    @Override
    protected float readFloat() {
        return is.read_float();
    }

    @Override
    protected double readDouble() {
        return is.read_double();
    }

    @Override
    protected char readChar() {
        return is.read_char();
    }

    @Override
    protected String readString() {
        return is.read_string();
    }

    @Override
    protected short readUnsignedByte() {
        return is.read_octet();
    }

    @Override
    protected int readUnsignedShort() {
        return is.read_ushort();
    }

    @Override
    protected long readUnsignedInt() {
        return is.read_ulong();
    }

    @Override
    protected double readUnsignedFloat() {
        return is.read_float();
    }

    @Override
    protected Object readObject(String objectType) {
        return is.read_Object();
    }

    @Override
    protected boolean initializeOutputBuffer(Object rootPojo) {
        // this.buffer = ByteBuffer.allocate(maxBufferSize(rootPojo));
        // return true;
        os = new DdsiOutputStream();
        LOGGER.warn("Output to DDS is not implemented yet");
        // throw new
        // UnsupportedOperationException("Output to DDS is not implemented");
        return true;
    }

    @Override
    protected Object getFinalizedOutputBuffer() {
        ChannelBuffer cb = os.getChannelBuffer();
        LOGGER.warn("Output to DDS is not implemented yet");
        // throw new
        // UnsupportedOperationException("Output to DDS is not implemented");
        return cb;
    }

    @Override
    protected void writeBoolean(boolean value) {
        os.write_boolean(value);
    }

    @Override
    protected void writeShort(short value) {
        os.write_short(value);
    }

    @Override
    protected void writeByte(byte value) {
        os.write_octet(value);
    }

    @Override
    protected void writeInt(int value) {
        os.write_long(value);
    }

    @Override
    protected void writeLong(long value) {
        os.write_longlong(value);
    }

    @Override
    protected void writeFloat(float value) {
        os.write_float(value);
    }

    @Override
    protected void writeDouble(double value) {
        os.write_double(value);
    }

    @Override
    protected void writeUnsignedByte(short value) {
        os.write_ushort(value);
    }

    @Override
    protected void writeUnsignedShort(int value) {
        os.write_ushort((short) value);
    }

    @Override
    protected void writeUnsignedInt(long value) {
        os.write_ulong((int) value);
    }

    @Override
    protected void writeUnsignedFloat(double value) {
        os.write_float((float) value);
    }

    @Override
    protected void writeString(String value) {
        os.write_string(value);
    }

    @Override
    protected void writeChar(char value) {
        os.write_char(value);
    }

    @Override
    protected void writeObject(Object value, String typeName) {
        throw new UnsupportedOperationException("DDSI Interface doesn't support Object serialization");
    }

    @Override
    protected boolean isABuffer(Object content) {
        return expectedBodyClass().isInstance(content);
    }

    @Override
    protected Class<?> expectedBodyClass() {
        // TODO Auto-generated method stub
        return ByteBuffer.class;
    }

    @Override
    protected Object readPreArray(ElementType elementType, Class<?> elementClass) {
        // Read size of the array, from the buffer
        int size = readInt();
        // instantiate a new array of size elements of class elementClass
        return Array.newInstance(elementClass, size);
    }

    @Override
    protected Object readPostArray(Object arrayObject) {
        // do nothing. Return the Array received in parameter
        return arrayObject;
    }

    @Override
    protected void writePreArray(Object arrayObject, Class<?> fieldType) {
        // Get the size of the array object
        int size = Array.getLength(arrayObject);
        // write the size into the buffer
        writeInt(size);

    }

    @Override
    protected void writePostArray(Object arrayObject, Class<?> fieldType) {
        // Nothing
    }

}
