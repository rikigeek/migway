package migway.plugins.dds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.omg.CORBA.Any;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;

import com.prismtech.gateway.camelddsi.DdsiException;


/**
 * Simulated DDS Byte Buffer
 * @author Sébastien Tissier
 *
 */
public class DdsByteBufferReader extends InputStream {

    private ByteBuffer savedBuffer;
    private int bufferStart;

    public DdsByteBufferReader(ByteBuffer buffer) throws DdsiException {
        this.savedBuffer = buffer.duplicate();

        buffer = this.savedBuffer;
        // Get encoding flag (byte 0 & 1)
        int encodingFlag = (short) (this.savedBuffer.get() << 8 | buffer.get() & 0xFF);
        // skip next 2 bytes: reserved for future use
        buffer.get();
        buffer.get();

        // select encoding Endianess following encoding flag
        if (encodingFlag == 0) {
            buffer.order(ByteOrder.BIG_ENDIAN);
        } else if (encodingFlag == 1) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            // only CDR are actually supported
            throw new DdsiException("Unsupported encoding flag (" + encodingFlag + ") for CacheChange payload.");
        }
        // Save start position of datas
        this.bufferStart = this.savedBuffer.position();
    }

    public DdsByteBufferReader(byte[] array) throws DdsiException {
        this(ByteBuffer.wrap(array));
    }

    public final void close() throws IOException {
        super.close();
        this.savedBuffer = null;
        this.bufferStart = 0;
    }

    /**
     * Fonction de décallage pour se positionner au bon endroit, afin de lire un
     * bloc entier:
     * si c'est un bloc de 2, se positionner sur un offset multiple de 2
     * si c'est un bloc de 4, se positionner sur un offset multiple de 4
     * si c'est un bloc de 8, se positionner sur un offset multiple de 8
     * 
     * En plus simple, fait un position(size - (offset % size)) _si_
     * offset % size != 0, mais en certainement plus optimisé pour des sizes en
     * puissance de 2
     * 
     * NB: ne fonctionne pas avec des valeurs non puissance de 2.
     * 
     * @param move
     */
    private void alignOnBoundary(int size) {
        if (size > 1) {
            // nombre d'octets à décaler pour être synchro
            int move = -this.savedBuffer.position() + this.bufferStart & size - 1;
            if (move != 0) {
                this.savedBuffer.position(this.savedBuffer.position() + move);
            }
        }
    }

    public final boolean read_boolean() {
        return this.savedBuffer.get() != 0;
    }

    public final char read_char() {
        return (char) (this.savedBuffer.get() & 0xFF);
    }

    public final char read_wchar() {
        throw new NO_IMPLEMENT("wchar not yet supported");
    }

    public final byte read_octet() {
        return this.savedBuffer.get();
    }

    public final short read_short() {
        alignOnBoundary(2);
        return this.savedBuffer.getShort();
    }

    public final short read_ushort() {
        alignOnBoundary(2);
        if (this.savedBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            return (short) ((this.savedBuffer.get() & 0xFF) + ((this.savedBuffer.get() & 0xFF) << 8));
        }
        return (short) (((this.savedBuffer.get() & 0xFF) << 8) + (this.savedBuffer.get() & 0xFF));
    }

    public final int read_long() {
        alignOnBoundary(4);
        return this.savedBuffer.getInt();
    }

    public final int read_ulong() {
        alignOnBoundary(4);
        if (this.savedBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            return (this.savedBuffer.get() & 0xFF) + ((this.savedBuffer.get() & 0xFF) << 8) + ((this.savedBuffer.get() & 0xFF) << 16)
                    + ((this.savedBuffer.get() & 0xFF) << 24);
        }
        return ((this.savedBuffer.get() & 0xFF) << 24) + ((this.savedBuffer.get() & 0xFF) << 16) + ((this.savedBuffer.get() & 0xFF) << 8)
                + (this.savedBuffer.get() & 0xFF);
    }

    public final long read_longlong() {
        alignOnBoundary(8);
        return this.savedBuffer.getLong();
    }

    public final long read_ulonglong() {
        alignOnBoundary(8);
        if (this.savedBuffer.order() == ByteOrder.LITTLE_ENDIAN) {
            return (this.savedBuffer.getInt() & 0xFFFFFFFF) + (this.savedBuffer.getInt() << 32);
        }
        return (this.savedBuffer.getInt() << 32) + (this.savedBuffer.getInt() & 0xFFFFFFFF);
    }

    public final float read_float() {
        alignOnBoundary(4);
        return this.savedBuffer.getFloat();
    }

    public final double read_double() {
        alignOnBoundary(8);
        return this.savedBuffer.getDouble();
    }

    public final String read_string() {
        int i;
        if ((i = read_long()) <= 0) {
            throw new MARSHAL("Try to read a string, but the length is " + i);
        }
        try {
            this.savedBuffer.position(this.savedBuffer.position() + i);
            String str = new String(this.savedBuffer.array(), this.savedBuffer.position() - i, i - 1, "UTF-8");
            return str;
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            throw new MARSHAL("Error trying to read a string: " + localUnsupportedEncodingException);
        }
    }

    public final String read_wstring() {
        throw new NO_IMPLEMENT("wstring not yet supported");
    }

    public final void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfBoolean[(i + paramInt1)] = read_boolean();
        }
    }

    public final void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfChar[(i + paramInt1)] = read_char();
        }
    }

    public final void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfChar[(i + paramInt1)] = read_wchar();
        }
    }

    public final void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfByte[(i + paramInt1)] = read_octet();
        }
    }

    public final void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfShort[(i + paramInt1)] = read_short();
        }
    }

    public final void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfShort[(i + paramInt1)] = read_ushort();
        }
    }

    public final void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfInt[(i + paramInt1)] = read_long();
        }
    }

    public final void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfInt[(i + paramInt1)] = read_ulong();
        }
    }

    public final void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfLong[(i + paramInt1)] = read_longlong();
        }
    }

    public final void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfLong[(i + paramInt1)] = read_ulonglong();
        }
    }

    public final void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfFloat[(i + paramInt1)] = read_float();
        }
    }

    public final void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
        for (int i = 0; i < paramInt2; i++) {
            paramArrayOfDouble[(i + paramInt1)] = read_double();
        }
    }

    public final Object read_Object() {
        throw new NO_IMPLEMENT("CORBA::Object not possible in DDS structure");
    }

    public final TypeCode read_TypeCode() {
        throw new NO_IMPLEMENT("CORBA::TypeCode not possible in DDS structure");
    }

    public final Any read_any() {
        throw new NO_IMPLEMENT("CORBA::Any not possible in DDS structure");
    }
}