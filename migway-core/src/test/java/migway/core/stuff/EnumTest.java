package migway.core.stuff;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnumTest {
    
    public enum Toto {
        ONE(1), TWO(2), FREE(-1);
        
        private int value;
        private Toto(int v) {
            this.value = v;
        }
        public String toString() {
            return "" + value + "";
        }
    }

    @Test
    public void test() {
        Toto t = Toto.ONE;
        assertEquals("1", t.toString());
    }

}
