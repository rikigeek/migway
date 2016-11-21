package migway.core.samples;

import static org.junit.Assert.*;

import org.junit.Test;

public class HlaSamplesTest {

    @Test
    public void testConvertPitchRecorderStringToByteArray() {
        byte[] r = HlaSamples.convertPitchRecorderStringToByteArray("00010203 FFA00A0B");
        assertEquals(8, r.length);
        assertEquals(0, r[0]);
        assertEquals(3, r[3]);
        assertEquals((byte)0xFF, r[4]);
        assertEquals((byte)0xA0, r[5]);
        assertEquals((byte)0x0B, r[7]);
        assertEquals(10, r[6]);
    }

}
