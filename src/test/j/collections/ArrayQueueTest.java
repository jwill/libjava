package j.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Random;

import j.collections.ArrayQueue;

public class ArrayQueueTest 
{
    /**
     * Test creating on queue, and make sure that there are no elements
     * in there for a new, blank one.  Make sure:
     *      1) size() == 0
     *      2) peek() == null
     *      3) poll() == null
     */
    @Test (timeout = 1000)
    public void createQueueTest ()
    {
        Queue<Object> q = new ArrayQueue<Object> ();
        assertEquals ("Create new queue, call size()", 0, q.size ());
        assertEquals ("Create new queue, call peek()", null, q.peek ());
        assertEquals ("Create new queue, call poll()", null, q.poll ());
    }

    /**
     * Tests basic offer functionality to make sure it returns true
     * and changes the size
     */
    @Test (timeout = 1000)
    public void offerTest ()
    {
        Queue <Object> q = new ArrayQueue <Object> ();
        assertEquals ("Create queue, call offer(null)", true, q.offer (null));
        assertEquals ("Call size() after offer(null)", 1, q.size ());

    }

    @Test(timeout = 1000)
    public void removeAndPeekTest()
    {
        Queue<Object> q = new ArrayQueue<Object>();
        assertTrue(q.isEmpty());
        Object obj = (new Object());
        assertTrue(q.offer(obj));
        assertEquals(1, q.size());
        assertEquals(obj, q.peek());
        assertEquals(1, q.size());
        assertFalse(q.isEmpty());
        
        assertEquals(obj, q.remove());
        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
        assertEquals(null, q.peek());
        assertEquals(null, q.poll());
    }
    
    @Test(timeout = 1000, expected = NoSuchElementException.class)
    public void removeTest()
    {
        Queue<Object> q = new ArrayQueue<Object>();
        assertTrue(q.isEmpty());
        q.remove();
    }

    @Test(timeout = 1000, expected = NoSuchElementException.class)
    public void elementTest()
    {
        Queue<Object> q = new ArrayQueue<Object>();
        assertTrue(q.isEmpty());
        q.element();
    }
    
    @Test(timeout = 1000, expected = NoSuchElementException.class)
    public void elementTest2()
    {
        Queue<Object> q = new ArrayQueue<Object>();
        try{
            assertTrue(q.isEmpty());
            Object obj = new Object();
            assertTrue(q.offer(obj));
            assertEquals(obj, q.element());
            assertEquals(obj, q.peek());
            assertEquals(obj, q.poll());
        }
        catch(AssertionError e){throw e;}
        catch(Exception e){assertFalse("Unexpected excep", true);}

        q.element();
    }
    
    @Test(timeout = 10000)
    public void clearAndAddTest()
    {
        Queue<Integer> q = new ArrayQueue<Integer>();
        
        for (int i = 0; i < 100; i++)
        {
            for (int j = 0; j < i; j ++)
                assertTrue(q.add(j));

            assertEquals(i, q.size());

            for (int j = 0; j < i; j++)
            {
                assertEquals((int)j, (int)q.poll());
                assertTrue(q.add(j));
            }

            q.clear();
            assertEquals(0, q.size()); 
        }
    }

    @Test(timeout = 1000)
    public void pollAndPeekTest()
    {
        Queue<Object> q = new ArrayQueue<Object>();
        assertTrue(q.isEmpty());
        Object obj = new Object();
        assertTrue(q.offer(obj));
        assertEquals(1, q.size());
        assertEquals(obj, q.peek());
        assertEquals(1, q.size());
        assertFalse(q.isEmpty());
        
        assertEquals(obj, q.poll());
        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
        assertEquals(null, q.peek());
        assertEquals(null, q.poll());
    }

    /**
     * Test corner case for iterator
     */
    @Test (timeout = 1000, expected = NoSuchElementException.class)
    public void iterateNSEExceptionCornerCase ()
    {
        Queue<Object> q = new ArrayQueue <Object>();
        Iterator<Object> itr = null;
        
        try // to ensure the expected exception comes from .next()
        {
            itr = q.iterator ();
            assertFalse ("Call hasNext() on iterator returned by new queue", itr.hasNext());
        }
        catch(AssertionError e){throw e;}
        catch(Exception e){assertFalse("Unexpected excep", true);}

        // the following line should throw the NSEException
        itr.next ();
    }
    
    /**
     * Test whether iterator.remove throws an UnsupportedOperationException
     */
    @Test (timeout = 1000, expected = UnsupportedOperationException.class)
    public void testIteratorRemove ()
    {
        ArrayQueue<Object> h = new ArrayQueue<Object> ( );
        Iterator<Object> it = null;
        
        try
        {
            it = h.iterator();
        }
        catch(AssertionError e){throw e;}
        catch(Exception e){assertFalse("Unexpected excep", true);}

        it.remove();
    }
    
    
    /**
     * This is an example of a randomized stress test. The goal here is to bring
     * out corner cases. It's also important to do more targeted testing. Try to
     * test odd things that might happen. For example, the circular behavior of
     * the array queue is a good place to find issues.
     */
    @Test
    public void randomAddRemoveTest ()  
    {
        Random r = new Random ();
        Queue <Object> mine = new ArrayQueue <Object> ();
        Queue <Object> compare = new LinkedList <Object> ();
        
        int NTESTS = 10000;
        
        for (int i = 0; i < NTESTS; i ++) 
        {
            if (compare.size() == 0 || r.nextBoolean()) 
            {
                Object o = new Object ();
                mine.offer (o);
                compare.offer (o);
            } 
            else 
            {
                assertEquals ("object from peek()", compare.peek(), mine.peek());
                assertEquals ("size after peek()",  compare.size(), mine.size());
                assertEquals ("object from poll()", compare.poll(), mine.poll());
            }
            
            assertEquals ("size after operations", compare.size(), mine.size());
        }
    }
    
    /** A method that tests for equality of two queues
     * @param stdQueue an instance of the 'standard' implementation of the queue object.
     * @param myQueue an instance of my implementation of the queue object.
     * @returns true if they are both equal; false otherwise.*/
    private static boolean equals(Queue<Object> stdQueue, Queue<Object> myQueue)
    {
        Iterator <Object> stdIt = stdQueue.iterator();
        Iterator<Object> myIt = myQueue.iterator();
        
        while (true)
        {
            if (stdIt.hasNext() != myIt.hasNext())
            {
                return false;
            }
            
            if (! stdIt.hasNext())
            {
                break;
            }
            
            Object std = stdIt.next();
            Object my  = myIt.next();
            
            if (my == null && std != null)
            {
                return false;
            }
            
            if (my != null && !my.equals(std))
            {
                return false;
            }
        }
        
        return stdIt.hasNext() == myIt.hasNext();
    }
    
    @Test
    public void resizeTest () 
    {
        Random rnd = new Random();
        
        for (int i = 0; i < 20; i ++)
        {
            Queue<Object> stdQueue = new LinkedList<Object>();
            ArrayQueue<Object> myQueue = new ArrayQueue<Object> ();
            int nullObjCount = 0;
            
            // inner loop that tests wrapping around feature.
            for (int k = 0; k < 4; k++)
            {
                int toAdd = 1000;
                Object objs[] = new Object[toAdd];
                
                for (int j = 0; j < toAdd; j ++)
                {
                    assertEquals("during adding: stdQueue.peek== myQueue.peek ?", stdQueue.peek(), myQueue.peek());
                    assertEquals("during adding: stdQueue.size() ==  myQueue.size()? ", stdQueue.size(), myQueue.size());
                    
                    objs[j] = (rnd.nextBoolean() ? new Object() : null);
                    if (objs[j] == null)
                    {
                        nullObjCount++;
                    }
                    stdQueue.offer(objs[j]);
                    myQueue.offer(objs[j]);
                    
                    assertEquals("during adding: stdQueue.peek== myQueue.peek ?", stdQueue.peek(), myQueue.peek());
                    assertEquals("during adding: stdQueue.size() ==  myQueue.size()? ", stdQueue.size(), myQueue.size());
                }
    
                for (int j = 0; j < toAdd; j ++)
                {
                    assertEquals("after adding: myQueue.contains?", true, myQueue.contains(objs[j]));
                }
                
                Iterator<Object> it = myQueue.iterator();
                boolean exception = false;
                int nullCount = 0;
                int count = 0;
                while (it.hasNext())
                {
                    count ++;
                    if (it.next () == null)
                    {
                        nullCount ++;
                    }
                }
                
                // Test for NoSuchElementException
                try
                {
                    it.next();
                }
                catch (NoSuchElementException e)
                {
                    exception = true;
                }
                
                // Test for equality against the stdQueue after we run through 
                // the iterator, to check whether the iterator has modified anything.
                assertEquals("after adding: myQueue == stdQueue ? ", true, equals(stdQueue, myQueue));
                assertEquals("after adding: myQueue.size() == stdQueue.size() ? ", stdQueue.size(), myQueue.size());
                
                assertEquals("after adding: iterator null count", nullObjCount, nullCount);
                assertEquals("after adding: iterator elem count", stdQueue.size(), count);
                assertEquals("after adding: iterator no such element exception", true, exception);
                
                // 'toRemove' can be zero.
                int toRemove = Math.min(toAdd, rnd.nextInt(toAdd + 1));
                for (int j = 0; j < toRemove; j ++)
                {
                    Object std = stdQueue.poll();
                    Object my = myQueue.poll();
                    if (std == null)
                    {
                        nullObjCount--;
                    }
                    assertEquals("during removing: stdQueue.poll == myQueue.poll", std, my);
                }
                
                for (int j = 0; j < toAdd; j ++)
                {
                    assertEquals("after removing:  myQueue.contains", 
                            stdQueue.contains(objs[j]), myQueue.contains(objs[j]));
                }
                
                it = myQueue.iterator();
                exception = false;
                nullCount = 0;
                count = 0;
                while (it.hasNext())
                {
                    count ++;
                    if (it.next () == null)
                    {
                        nullCount ++;
                    }
                }
                
                // Test for NoSuchElementException
                try
                {
                    it.next();
                }
                catch (NoSuchElementException e)
                {
                    exception = true;
                }
                
                // Test for equality against the stdQueue after we run through 
                // the iterator, to check whether the iterator has modified anything.
                assertEquals("after removing: myQueue == stdQueue ? ", true, equals(stdQueue, myQueue));
                assertEquals("after removing: myQueue.size() == stdQueue.size() ? ", stdQueue.size(), myQueue.size());
                
                assertEquals("after removing: iterator null count", nullObjCount, nullCount);
                assertEquals("after removing: iterator elem count", stdQueue.size(), count);
                assertEquals("after removing: iterator no such element exception", true, exception);
                
            }
        }
    }
}
