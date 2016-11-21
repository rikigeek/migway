package migway.core.stuff;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExceptionTest {

    private boolean bFinal;
    private boolean bCatch;

    @Test
    public void testExceptions() throws Exception {

        int i = 2;
        try {
            System.out.println("Try");
            if (i == 1)
                throw new Exception("Test exception");
        } catch (Exception e) {
            System.out.println("catched " + e.getMessage());
            // throw new Exception("toto", e);
            return;

        } finally {
            System.out.println("finally");
        }

        assertTrue(true);
    }

    @Test
    public void testFinallyException() {
        bFinal = false;
        bCatch = false;
        try {
            myTest(1);
            // No exception (so no catch) but finally always run
            assertTrue(bFinal);
            assertFalse(bCatch);
        } catch (Exception e) {
            assertFalse("Shouldn't get an exception here", true);
        }

        bFinal = false; // reset the final flag
        try {
            // Flags still false
            assertFalse(bFinal);
            assertFalse(bCatch);
            myTest(0);
            // Never reach this code
            assertFalse("An exception should have been thrown before", true);
        } catch (Exception e) {
            // Both flags are set (even the finally one)
            assertTrue(bFinal);
            assertTrue(bCatch);
        }
    }

    private boolean myTest(int i) throws Exception {
        try {
            if (i == 0)
                throw new Exception("i is 0");
        } catch (Exception e) {
            bCatch = true;
            // Even with this exception thrown, finally code is *always* run
            throw new Exception("myTest exception", e);
        } finally {
            bFinal = true;
        }
        return true;
    }

    @Test
    public void testFinallyReturn() {
        bFinal = false;
        bCatch = false;

        assertFalse(bFinal);
        assertFalse(bCatch);

        assertTrue(noException(1)); 
        // Catch block must not be reached, but finally must be
        // and must return true;
        assertTrue(bFinal);
        assertFalse(bCatch);

        bFinal = false;

        assertFalse(noException(0));
        // both block are reached, and false is returned (because i = 0)
        assertTrue(bFinal);
        assertTrue(bCatch);

    }

    /**
     * i = 0 generate an exception. bFinal and bCatch are updated depending on
     * catch and finally block reached
     * 
     * @param i
     * @return true if i != 0, and false if i == 0
     */
    private boolean noException(int i) {
        try {
            if (i == 0)
                throw new Exception("i is 0");
        } catch (Exception e) {
            bCatch = true;
            return false;
        } finally {
            bFinal = true;
        }
        return true;
    }

}
