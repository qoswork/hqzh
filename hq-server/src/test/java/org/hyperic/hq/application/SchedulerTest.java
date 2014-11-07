/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 *
 * Copyright (C) [2004-2008], Hyperic, Inc.
 * This file is part of HQ.
 *
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.application;

import java.util.concurrent.ScheduledFuture;

import org.hyperic.util.SpinBarrier;
import org.hyperic.util.SpinBarrierCondition;

import junit.framework.TestCase;

/**
 * Tests the Scheduler class.
 */
public class SchedulerTest extends TestCase {

    private Scheduler scheduler;
    
    public void tearDown() throws Exception{
        super.tearDown();
        if(this.scheduler != null) {
            this.scheduler.shutdown();
        }
    }
        
    public void testIllegalPoolSize() {
        try {
            new Scheduler(-1);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected outcome
        } catch (Exception e) {
            fail("Expected IllegalArgumentException instead of:"+e);
        }
        
        try {
            new Scheduler(0);
            fail("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // expected outcome
        } catch (Exception e) {
            fail("Expected IllegalArgumentException instead of:"+e);
        }
    }
    
    public void testExecuteAtFixedRate() throws Exception {
        this.scheduler = new Scheduler(1);
                
        final RunnableCounter counter = new RunnableCounter(50, false);
        
        ScheduledFuture future = 
            scheduler.scheduleAtFixedRate(counter, Scheduler.NO_INITIAL_DELAY, 100);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                return counter.numRuns() >= 2;
            }
        });
        assertTrue(counterUpdated.waitFor());
        
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());
        
    }
    
    public void testExecuteWithFixedDelay() throws Exception {
        this.scheduler = new Scheduler(1);
        
        final RunnableCounter counter = new RunnableCounter(100, false);
        
        // the execution period is about 200 msec (delay+runtime)
        ScheduledFuture future = 
            scheduler.scheduleWithFixedDelay(counter, Scheduler.NO_INITIAL_DELAY, 100);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                return counter.numRuns() >= 2;
            }
        });
        assertTrue(counterUpdated.waitFor());
        
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());      
    }
    
    public void testCancellingScheduledTask() throws Exception {
        this.scheduler = new Scheduler(1);
        
        final RunnableCounter counter = new RunnableCounter(50, false);
        
        ScheduledFuture future = 
            scheduler.scheduleAtFixedRate(counter, Scheduler.NO_INITIAL_DELAY, 100);
        
        SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                return counter.numRuns() >= 2;
            }
        });
        assertTrue(counterUpdated.waitFor());
        
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());
        
        future.cancel(true);
        
        assertTrue(future.isDone());
        assertTrue(future.isCancelled());     
       
    }
    
    public void testExecuteAtFixedRateWithInitialDelay() throws Exception {
        this.scheduler = new Scheduler(1);
        
        final RunnableCounter counter = new RunnableCounter(50, false);
        
        ScheduledFuture future = 
            scheduler.scheduleAtFixedRate(counter, 100, 100);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                return counter.numRuns() >= 2;
            }
        });
        assertTrue(counterUpdated.waitFor());
        
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());        
        
    }
    
    public void testExecuteWithFixedDelayWithInitialDelay() throws Exception {
        this.scheduler = new Scheduler(1);
        
        final RunnableCounter counter = new RunnableCounter(50, false);
        
        ScheduledFuture future = 
            scheduler.scheduleWithFixedDelay(counter, 100, 50);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                return counter.numRuns() >= 2;
            }
        });
        assertTrue(counterUpdated.waitFor());
        
        assertFalse(future.isDone());
        assertFalse(future.isCancelled());

    }
    
    /**
     * Test executing one task at a fixed rate and one task with a fixed delay.
     * 
     * @throws Exception
     */
    public void testExecuteConcurrentTasks() throws Exception {
        this.scheduler = new Scheduler(2);
        
        final RunnableCounter counter1 = new RunnableCounter(10, false);

        final RunnableCounter counter2 = new RunnableCounter(10, false);
                
        ScheduledFuture future1 = 
            scheduler.scheduleAtFixedRate(counter1, Scheduler.NO_INITIAL_DELAY, 50);
        
        // the execution period is about 60 msec (delay+runtime)
        ScheduledFuture future2 = 
            scheduler.scheduleWithFixedDelay(counter2, Scheduler.NO_INITIAL_DELAY, 50);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            
            public boolean evaluate() {
                return ((counter1.numRuns() >= 5) && (counter2.numRuns() >= 4));
            }
        });
        
        assertTrue(counterUpdated.waitFor());
        assertFalse(future1.isDone());
        assertFalse(future1.isCancelled());

        assertFalse(future2.isDone());
        assertFalse(future2.isCancelled());
           
    }
    
    /**
     * Test that a scheduled task will stop executing if it throws an 
     * unchecked exception.
     */
    public void testUncheckedExceptionInTask() throws Exception {
        this.scheduler = new Scheduler(1);
        
        final RunnableCounter counter = new RunnableCounter(50, true);
        
        final ScheduledFuture future = 
            scheduler.scheduleAtFixedRate(counter, Scheduler.NO_INITIAL_DELAY, 100);
        
        final SpinBarrier counterUpdated = new SpinBarrier(new SpinBarrierCondition() {
            public boolean evaluate() {
                // only should have run once since a runtime exception was thrown
                return ((counter.numRuns() == 1) && (future.isDone()) && !(future.isCancelled()));
            }
        });
        assertTrue(counterUpdated.waitFor());
    }
    
    private class RunnableCounter implements Runnable {

        private final long _sleepTime;
        private int _numRuns;
        private final boolean _throwException;
        private final Object _lock = new Object();
        
        public RunnableCounter(long sleepTime, boolean throwUncheckedException) {
            _sleepTime = sleepTime;
            _throwException = throwUncheckedException;
        }
        
        public void run() {
            synchronized (this) {
                _numRuns++;
            }
            
            try {
                Thread.sleep(_sleepTime);
            } catch (InterruptedException e) {
            }
            
            if (_throwException) {
                throw new RuntimeException("unchecked exception");
            }
        }
        
        public synchronized int numRuns() {
            return _numRuns;
        }
        
    }
    
}
