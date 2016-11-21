package migway.plugins.dds;


public class DdsSamples {
    /**
     * A sample of byte array as received on DDSI BUS.
     * First 4 bytes are header (see DDSI specifications)
     * 
     * Values are these one:
     * ID 125881207 - RESOURCE_TYPE__NAV_UNIT (#15)
     * PN 'DDSSimulated'
     * DS 'A DDS simulated vehicule'
     * MF 'Sebi'
     * IS 'none'
     * ST 'MIGWAY'
     * NS ''
     * SN '42'
     * SW 'core' 'v0.1'
     * true true true true true
     * GEN 2016-05-24 44257.000000s: 1464007231 seconds and 0 nanoseconds
     * 
     * 
     * More details:
     * PlatformCapability_T Topic
     * offset 0 is ByteBuffer.mark (116)
     * offset 4 is ByteBuffer.bufferStart (120)
     * limit is 276 (160 + mark)
     * 116 000: [00][01][00][00][77][cb][80][07] // header : 4 Bytes / resource
     * Id (4B)
     * 124 008: [0f][00][00][00][05][00][00][00] // resourceIdType (4B) / Lenght
     * of string = 5 (4B)
     * 132 016: [53][65][62][69][00][00][00][00] // 'Sebi\0' manufacturer
     * String: 5 * 1B + padding 3B
     * 140 024: [0d][00][00][00][44][44][53][53] // 'DDSS' productName length 4B
     * / 13 * 1B (4B here)
     * 148 032: [69][6d][75][6c][61][74][65][64] // 'imulated' (8B here)
     * 156 040: [00][02][00][00][19][00][00][00] // '\0' (Last B here) + 3B
     * padding / String Length = 25 (4B)
     * 164 048: [41][20][44][44][53][20][73][69] // 'A DDS si'
     * 172 056: [6d][75][6c][61][74][65][64][20] // 'mulated '
     * 180 064: [76][65][68][69][63][75][6c][65] // 'vehicule'
     * 188 072: [00][00][00][00][03][00][00][00] // '\0' (1B) + 3B padding /
     * string length = 3 (4B)
     * 196 080: [34][32][00][00][05][00][00][00] // '42\0' (3B) + 1B padding /
     * string length = 5 (4B)
     * 204 088: [6e][6f][6e][65][00][00][00][00] // 'none\0' (5B) + 3B padding
     * 212 096: [07][00][00][00][4d][49][47][57] // string length = 7 (4B) /
     * 'MIGW' (4B)
     * 220 104: [41][59][00][00][01][00][00][00] // 'AY\0' (3B) + 1B padding /
     * string length = 1 (4B)
     * 228 112: [00][00][00][00][01][00][00][00] // '\0' + 3B padding / array
     * lenght = 1 (4B)
     * 236 120: [05][00][00][00][63][6f][72][65] // string length = 5 (4B) /
     * 'core'
     * 244 128: [00][00][00][00][04][00][00][00] // '\0' (1B) + 3B padding /
     * string length = 4 (4B)
     * 252 136: [30][2e][31][00][01][01][01][01] // '0.1\0' (4B) / bool = 1 (1B)
     * / bool = 1 (1B) / bool = 1 (1B) / bool = 1 (1B)
     * 260 144: [01][00][00][00][3f][fa][42][57] // bool = 1 (1B) + 3B padding /
     * long (java int 4B) = 1464007231 = 0x5742FA3F
     * 268 152: [00][00][00][00][00][00][00][00] //
     * 276
     */
    public static short[] PLATFORM_CAPABILITY_BUFFER_1 = { 0x00, 0x01, 0x00, 0x00, 0x77, 0xcb, 0x80, 0x07, 0x0f, 0x00, 0x00, 0x00, 0x05,
            0x00, 0x00, 0x00, 0x53, 0x65, 0x62, 0x69, 0x00, 0x9f, 0xd8, 0x14, 0x0d, 0x00, 0x00, 0x00, 0x44, 0x44, 0x53, 0x53, 0x69, 0x6d,
            0x75, 0x6c, 0x61, 0x74, 0x65, 0x64, 0x00, 0x00, 0x00, 0x00, 0x19, 0x00, 0x00, 0x00, 0x41, 0x20, 0x44, 0x44, 0x53, 0x20, 0x73,
            0x69, 0x6d, 0x75, 0x6c, 0x61, 0x74, 0x65, 0x64, 0x20, 0x76, 0x65, 0x68, 0x69, 0x63, 0x75, 0x6c, 0x65, 0x00, 0x4e, 0x75, 0x42,
            0x03, 0x00, 0x00, 0x00, 0x34, 0x32, 0x00, 0x30, 0x05, 0x00, 0x00, 0x00, 0x6e, 0x6f, 0x6e, 0x65, 0x00, 0x37, 0x00, 0x00, 0x07,
            0x00, 0x00, 0x00, 0x4d, 0x49, 0x47, 0x57, 0x41, 0x59, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00,
            0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x63, 0x6f, 0x72, 0x65, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x30, 0x2e, 0x31,
            0x00, 0x01, 0x01, 0x01, 0x01, 0x01, 0x6d, 0x65, 0x3e, 0x61, 0x46, 0x44, 0x57, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    public static byte[] toByteBuffer(short[] array) {
        byte[] bb = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            bb[i] =  (byte) array[i];
        }
        return bb;
    }
    
    
}
