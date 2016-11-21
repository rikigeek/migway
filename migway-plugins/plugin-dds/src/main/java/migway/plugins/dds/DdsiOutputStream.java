package migway.plugins.dds;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.camel.RuntimeCamelException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.omg.CORBA.Any;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import com.prismtech.gateway.camelddsi.DdsiException;


public final class DdsiOutputStream extends OutputStream {
    private ChannelBuffer channelBuffer;

    public DdsiOutputStream() {
        this.channelBuffer = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN, 256);

        // Padding options bytes 
        this.channelBuffer.writeShort(0);
        this.channelBuffer.writeShort(0);
    }

    public final ChannelBuffer getChannelBuffer() {
        return this.channelBuffer;
    }

    private void alignOnBoundary(int size) {
        if (size > 1) {
            int move = -this.channelBuffer.writerIndex() + 4 & size - 1;
            if (move > 0) {
                this.channelBuffer.writeZero(move);
            }
        }
    }

    public final InputStream create_input_stream() {
        try {
            return new DdsiInputStream(ByteBuffer.wrap(this.channelBuffer.array(), this.channelBuffer.arrayOffset(),
                    this.channelBuffer.capacity()));
        } catch (DdsiException localDdsiException) {
            throw new RuntimeCamelException(localDdsiException);
        }
    }

    public final void write_boolean(boolean paramBoolean) {
        this.channelBuffer.writeByte(paramBoolean ? 1 : 0);
    }

    public final void write_char(char paramChar) {
        if (paramChar > 0xFF) {
            throw new DATA_CONVERSION("Cannot encode char '" + paramChar + "' (value > 0xFF)");
        }
        this.channelBuffer.writeByte(paramChar);
    }

    public final void write_wchar(char paramChar) {
        throw new NO_IMPLEMENT("wchar not yet supported");
    }

    public final void write_octet(byte paramByte) {
        this.channelBuffer.writeByte(paramByte);
    }

    public final void write_short(short paramShort) {
        alignOnBoundary(2);
        this.channelBuffer.writeShort(paramShort);
    }

    public final void write_ushort(short paramShort) {
        alignOnBoundary(2);
        this.channelBuffer.writeShort(paramShort);
    }

    public final void write_long(int paramInt) {
        alignOnBoundary(4);
        this.channelBuffer.writeInt(paramInt);
    }

    public final void write_ulong(int paramInt) {
        alignOnBoundary(4);
        this.channelBuffer.writeInt(paramInt);
    }

    public final void write_longlong(long paramLong) {
        alignOnBoundary(8);
        this.channelBuffer.writeLong(paramLong);
    }

    public final void write_ulonglong(long paramLong) {
        alignOnBoundary(8);
        this.channelBuffer.writeLong(paramLong);
    }

    public final void write_float(float paramFloat) {
        alignOnBoundary(4);
        this.channelBuffer.writeFloat(paramFloat);
    }

    public final void write_double(double paramDouble) {
        alignOnBoundary(8);
        this.channelBuffer.writeDouble(paramDouble);
    }

    public final void write_string(String paramString) {
        write_long(paramString.length() + 1);

        char[] paramString_ = paramString.toCharArray();
        for (int i = 0; i < paramString_.length; i++) {
            write_char(paramString_[i]);
        }
        write_octet((byte) 0);
    }

    public final void write_wstring(String paramString) {
        throw new NO_IMPLEMENT("wstring not yet supported");
    }

    public final void write_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_boolean(paramArrayOfBoolean[i]);
        }
    }

    public final void write_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_char(paramArrayOfChar[i]);
        }
    }

    public final void write_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_wchar(paramArrayOfChar[i]);
        }
    }

    public final void write_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_octet(paramArrayOfByte[i]);
        }
    }

    public final void write_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_short(paramArrayOfShort[i]);
        }
    }

    public final void write_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_ushort(paramArrayOfShort[i]);
        }
    }

    public final void write_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_long(paramArrayOfInt[i]);
        }
    }

    public final void write_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_ulong(paramArrayOfInt[i]);
        }
    }

    public final void write_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_longlong(paramArrayOfLong[i]);
        }
    }

    public final void write_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_ulonglong(paramArrayOfLong[i]);
        }
    }

    public final void write_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_float(paramArrayOfFloat[i]);
        }
    }

    public final void write_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
        for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
            write_double(paramArrayOfDouble[i]);
        }
    }

    public final void write_Object(Object paramObject) {
        throw new NO_IMPLEMENT("CORBA::Object not possible in DDS structure");
    }

    public final void write_TypeCode(TypeCode paramTypeCode) {
        throw new NO_IMPLEMENT("CORBA::TypeCode not possible in DDS structure");
    }

    public final void write_any(Any paramAny) {
        throw new NO_IMPLEMENT("CORBA::TypeCode not possible in DDS structure");
    }
}

/*
 * Location:
 * C:\Users\seb\.m2\repository\com\prismtech\gateway\camel-ddsi\2.2.1\camel
 * -ddsi-2.2.1.jar!\com\prismtech\gateway\camelddsi\obfuscated\j.class Java
 * compiler version: 6 (50.0) JD-Core Version: 0.7.1
 */