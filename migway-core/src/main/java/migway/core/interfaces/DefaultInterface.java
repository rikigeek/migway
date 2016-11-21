package migway.core.interfaces;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import migway.core.config.ConfigHelper;
import migway.core.config.ElementType;

import org.apache.camel.Exchange;

/**
 * Default implementation of a ComponentInterface.
 * This implementation use a ByteBuffer as a buffer. Data are written directly
 * to the buffer.
 * Arrays are encoded first with the size of the array. Every element of the
 * array is encoded after the size, one by one.
 * 
 * Message Headers: ClassName is the class (java binary name) of the POJO
 * contained in the
 * ByteBuffer.
 * 
 * This interface name is "Generic".
 * Default buffer size is 1500 bytes. But it should depends on the POJO size
 * 
 * @author Sébastien Tissier
 *
 */
public class DefaultInterface extends ComponentInterface {

    private ByteBuffer buffer = null;

    public DefaultInterface(ConfigHelper configHelper) {
        super(configHelper);
    }

    public DefaultInterface() {
        super();
    }

    @Override
    protected String setHeaderClassName() {
        return "ClassName";
    }

    @Override
    protected String setInterfaceTypeName() {
        return "Generic";
    }

    @Override
    protected int maxBufferSize(Object pojo) {
        return 1500;
    }

    @Override
    protected boolean initializeInputBuffer(Object buffer) {
        if (buffer instanceof ByteBuffer) {
            this.buffer = (ByteBuffer) buffer;
            return true;
        }
        return false;
    }

    @Override
    protected boolean initializeOutputBuffer(Object rootPojo) {
        this.buffer = ByteBuffer.allocate(maxBufferSize(rootPojo));
        return true;
    }

    @Override
    protected ByteBuffer getFinalizedOutputBuffer() {
        return buffer;
    }

    @Override
    protected boolean readBoolean() {
        return buffer.get() != 0;
    }

    @Override
    protected byte readByte() {
        return buffer.get();
    }

    @Override
    protected short readShort() {
        return buffer.getShort();
    }

    @Override
    protected int readInt() {
        return buffer.getInt();
    }

    @Override
    protected long readLong() {
        return buffer.getLong();
    }

    @Override
    protected float readFloat() {
        return buffer.getFloat();
    }

    @Override
    protected double readDouble() {
        return buffer.getDouble();
    }

    @Override
    protected char readChar() {
        return buffer.getChar();
    }

    @Override
    protected String readString() {
        int sSize = buffer.getInt();
        StringBuilder sValue = new StringBuilder();
        for (int pos = 0; pos < sSize; pos++) {
            sValue.append(buffer.getChar());
        }
        return sValue.toString();
    }

    @Override
    protected short readUnsignedByte() {
        // short val = (short) buffer.get();
        // if (val < 0) {
        // return (short) (127 - val);
        // } else
        // return val;
        return buffer.getShort();
    }

    @Override
    protected int readUnsignedShort() {
        // int val = (int) buffer.getShort();
        // if (val < 0) {
        // return (int) ((128 * 256) - 1 - val);
        // } else
        // return val;
        return buffer.getInt();
    }

    @Override
    protected long readUnsignedInt() {
        // long val = (long) buffer.getInt();
        // if (val < 0) {
        // val = val ^ (1 << 63);
        // val = val | (1 << 31);
        // return val;
        // } else
        // return val;
        return buffer.getLong();
    }

    @Override
    protected double readUnsignedFloat() {
        return buffer.getDouble();
    }

    @Override
    protected Object readObject(String objectType) {
        throw new UnsupportedOperationException("DefaultInterface doesn't support Object encoding");
    }

    @Override
    protected void writeBoolean(boolean value) {
        buffer.put((byte) (value ? 1 : 0));
    }

    @Override
    protected void writeShort(short value) {
        buffer.putShort(value);
    }

    @Override
    protected void writeByte(byte value) {
        buffer.put(value);
    }

    @Override
    protected void writeInt(int value) {
        buffer.putInt(value);
    }

    @Override
    protected void writeLong(long value) {
        buffer.putLong(value);
    }

    @Override
    protected void writeFloat(float value) {
        buffer.putFloat(value);
    }

    @Override
    protected void writeDouble(double value) {
        buffer.putDouble(value);
    }

    @Override
    protected void writeUnsignedByte(short value) {
        buffer.putShort(value);
    }

    @Override
    protected void writeUnsignedShort(int value) {
        buffer.putInt(value);
    }

    @Override
    protected void writeUnsignedInt(long value) {
        buffer.putLong(value);
    }

    @Override
    protected void writeUnsignedFloat(double value) {
        buffer.putDouble(value);
    }

    @Override
    protected void writeString(String value) {
        buffer.putInt(value.length());
        for (int pos = 0; pos < value.length(); pos++) {
            buffer.putChar(value.charAt(pos));
        }
    }

    @Override
    protected void writeChar(char value) {
        buffer.putChar(value);
    }

    @Override
    protected void writeObject(Object value, String typeName) {
        throw new UnsupportedOperationException("DefaultInterface doesn't support Object encoding");
    }

    @Override
    protected boolean validate(Exchange exchange) {
        return true;
    }

    @Override
    protected void writePreArray(Object arrayObject, Class<?> fieldType) {
        // should write the size of the array
        int size = Array.getLength(arrayObject);
        writeInt(size);
    }

    @Override
    protected void writePostArray(Object arrayObject, Class<?> fieldType) {
        // nothing is written after the array body
    }

    @Override
    protected boolean isABuffer(Object content) {
        return expectedBodyClass().isInstance(content);
    }

    @Override
    protected Object readPreArray(ElementType elementType, Class<?> elementClass) {
        int size = readInt();
        return Array.newInstance(elementClass, size);
    }

    @Override
    protected Object readPostArray(Object arrayObject) {
        // TODO Auto-generated method stub
        return arrayObject;
    }

    @Override
    protected Class<?> expectedBodyClass() {
        return ByteBuffer.class;
    }
}
