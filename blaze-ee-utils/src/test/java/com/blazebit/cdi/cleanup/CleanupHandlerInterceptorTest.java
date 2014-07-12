package com.blazebit.cdi.cleanup;

import com.blazebit.cdi.SimpleInvocationContext;
import com.blazebit.cdi.cleanup.annotation.Cleanup;
import com.blazebit.cdi.cleanup.annotation.CleanupHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


public class CleanupHandlerInterceptorTest {
    
    private final CleanupHandlerInterceptor interceptor = new CleanupHandlerInterceptor();

    @CleanupHandler(cleanup = Cleanup.class)
    public static class Cleanupable1 {
        
        private int cleanedUp = 0;
        
        @Cleanup(Cleanup.class)
        public void cleanup() {
            cleanedUp++;
        }
        
        public void run() { }
        
        public int getCleanedUp() {
            return cleanedUp;
        }
    }
    
    @Test
    public void testSimpleCleanup() throws Exception {
        final Cleanupable1 c = new Cleanupable1();
        final Method m = c.getClass().getMethod("run");
        final Exception e = new Exception();

        try {
            interceptor.cleanup(new SimpleInvocationContext(e, c, m));
            fail("Exception not thrown!");
        } catch (Exception ex) {
            assertEquals(e, ex);
        }
        
        assertEquals(1, c.getCleanedUp());
        
        try {
            interceptor.cleanup(new SimpleInvocationContext(e, c, m));
            fail("Exception not thrown!");
        } catch (Exception ex) {
            assertEquals(e, ex);
        }
        
        assertEquals(2, c.getCleanedUp());
    }

    @CleanupHandler(cleanup = Cleanup.class)
    public static class Cleanupable2 {
        
        private List<Throwable> cleanedUp = new ArrayList<Throwable>();
        
        @Cleanup(Cleanup.class)
        public void cleanup(Throwable t) {
            cleanedUp.add(t);
        }
        
        public void run() { }
        
        public List<Throwable> getCleanedUp() {
            return cleanedUp;
        }
    }
    
    @Test
    public void testExceptionParameterCleanup() throws Exception {
        final Cleanupable2 c = new Cleanupable2();
        final Method m = c.getClass().getMethod("run");
        final Exception e = new Exception();

        try {
            interceptor.cleanup(new SimpleInvocationContext(e, c, m));
            fail("Exception not thrown!");
        } catch (Exception ex) {
            assertEquals(e, ex);
        }
        
        assertEquals(1, c.getCleanedUp().size());
        assertEquals(e, c.getCleanedUp().get(0));
        
        try {
            interceptor.cleanup(new SimpleInvocationContext(e, c, m));
            fail("Exception not thrown!");
        } catch (Exception ex) {
            assertEquals(e, ex);
        }
        
        assertEquals(2, c.getCleanedUp().size());
        assertEquals(e, c.getCleanedUp().get(1));
    }
    
    @CleanupHandler(cleanup = Cleanup.class)
    public static class Cleanupable3 {

        public void run() { }
        
        @Cleanup(Cleanup.class)
        public void cleanup(IllegalArgumentException t) {
            
        }
    }
    @Test(expected=IllegalArgumentException.class)
    public void testInvalidExceptionParameterCleanup() throws Exception {
        final Cleanupable3 c = new Cleanupable3();
        final Method m = c.getClass().getMethod("run");
        final Exception e = new Exception();

        interceptor.cleanup(new SimpleInvocationContext(e, c, m));
    }
}
