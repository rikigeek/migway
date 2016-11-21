package migway.plugins.hla;

import java.util.ArrayList;
import java.util.List;

public class JavaNormalizer {
    private List<String> reservedWords;
    private List<String> nativeTypes;

    public JavaNormalizer() {
        reservedWords = new ArrayList<String>();
        for (String c : "abstract continue for new switch assert default if package synchronized boolean do goto private this break double implements protected throw byte else import public throws case enum instanceof return transient catch extends int short try char final interface static void class finally long strictfp volatile const float native super while"
                .split(" ")) {
            reservedWords.add(c);
        }
        nativeTypes = new ArrayList<String>();
        for (String c : "byte boolean char double float int long short".split(" ")) {
            nativeTypes.add(c);
        }
    }

    public String normalizeField(String fieldName) {
        boolean isArray = false;
        // Remove [] in array definition
        if (fieldName.endsWith("[]")) {
            isArray = true;
            fieldName = fieldName.substring(0, fieldName.length() - 2);
        }

        String f = normalizeIdent(fieldName);
        if (Character.isUpperCase(f.charAt(0))) {
            // First letter must be lower case
            String t = f.substring(0, 1).toLowerCase();
            f = t.concat(f.substring(1));
        }
        if (reservedWords.contains(f)) {
            f = "f".concat(f);
        }

        // Array finish with []
        if (isArray)
            f = f.concat("[]");
        return f;
    }

    public String normalizeEnum(String enumerator) {
        // keep only mandatory changes 
        String e = normalizeIdent(enumerator);
        // everything in upper case 
//        e = e.toUpperCase();

        return e;
    }

    public String normalizeClass(String className) {
        String c = normalizeIdent(className);
        if (!nativeTypes.contains(c)) { // ignore first letter case if it's a
                                        // native type
            if (Character.isLowerCase(c.charAt(0))) {
                // First letter must be upper case
                String t = c.substring(0, 1).toLowerCase();
                c = t.concat(c.substring(1));
            }
        }
        return c;
    }

    public String normalizeIdent(String ident) {
        String i = "";
        for (int idx = 0; idx < ident.length(); idx++) {
            if (Character.isJavaIdentifierPart(ident.charAt(idx))) {
                i = i.concat(ident.substring(idx, idx + 1));
            }
        }
        return i;
    }

}
