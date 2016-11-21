package migway.core.config;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum BasicTypeEnum {
    BOOLEAN(false),        // bool
    BYTE(true),            // signed 8 bits
    SHORT(true),           // signed 16 bits
    INT(true),             // signed 32 bits
    LONG(true),            // signed 64 bits
    UNSIGNED_BYTE(false),  // unsigned 8 bits
    UNSIGNED_SHORT(false), // unsigned 16 bits
    UNSIGNED_INT(false),   // unsigned 32 bits
    FLOAT(true),           // float single precision 32 bits (IEEE 754)
    DOUBLE(true),          // float double precision 64 bits (IEEE 754)
    UNSIGNED_FLOAT(false), // unsigned single precision 32 bits floating
                           // point
                           // ??
                           // TODO Unsigned float Really necessary?
    CHAR(false),           // unicode character
    STRING(false)          // unicode character array (string)
    ;

    private boolean signed;

    BasicTypeEnum(boolean signed) {
        this.signed = signed;
    }

    boolean isSigned() {
        return signed;
    }
}
