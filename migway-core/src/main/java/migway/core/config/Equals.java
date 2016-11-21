package migway.core.config;

public class Equals {
    public static boolean test(Object o1, Object o2) {
        if (o1 == null) {
            if (o2 == null)
                return true;
            else
                return false;
        }
        return o1.equals(o2);
    }
}
