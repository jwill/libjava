package j.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Random;

import j.collections.ArrayStack;

public class ArrayStackTest 
{
    /**
     * Test creating on stack, and make sure that there are no elements
     * in there for a new, blank one.  Make sure:
     *      1) size() == 0
     *      2) peek() == null
     *      3) poll() == null
     */
    @Test (timeout = 1000)
    public void createStackTest ()
    {
        ArrayStack<Object> q = new ArrayStack<Object> ();
        assertEquals ("Create new stack, call size()", 0, q.size ());
        assertEquals ("Create new stack, call peek()", null, q.peek ());
        assertEquals ("Create new stack, call poll()", null, q.poll ());
    }

    @Test(timeout = 1000)
    public void removeAndPeekTest()
    {
        ArrayStack<Object> q = new ArrayStack<Object>();
        assertTrue(q.isEmpty());
        Object obj = (new Object());
        assertTrue(q.push(obj));
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
        ArrayStack<Object> q = new ArrayStack<Object>();
        assertTrue(q.isEmpty());
        q.remove();
    }

    @Test(timeout = 1000, expected = NoSuchElementException.class)
    public void topTest2()
    {
        ArrayStack<Object> q = new ArrayStack<Object>();
        try{
            assertTrue(q.isEmpty());
            Object obj = new Object();
            assertTrue(q.push(obj));
            assertEquals(obj, q.top());
            assertEquals(obj, q.peek());
            assertEquals(obj, q.poll());
        }
        catch(AssertionError e){throw e;}
        catch(Exception e){assertFalse("Unexpected excep", true);}
    
        q.top();
    }

    @Test(expected = NoSuchElementException.class)
    public void topTest()
    {
        new ArrayStack<Object>().top();
    }

    /**
     * Tests basic offer functionality to make sure it returns true
     * and changes the size
     */
    @Test (timeout = 1000)
    public void offerTest ()
    {
        ArrayStack <Object> q = new ArrayStack <Object> ();
        assertEquals ("Create stack, call push(null)", true, q.push (null));
        assertEquals ("Call size() after push(null)", 1, q.size ());
        assertEquals ("Create stack, call push(null)", null, q.top ());
    }

    @Test(timeout = 10000)
    public void clearAndAddTest()
    {
        ArrayStack<Integer> q = new ArrayStack<Integer>();
        
        for (int i = 0; i < 100; i++)
        {
            for (int j = 0; j < i; j ++)
                assertTrue(q.add(j));

            assertEquals(i, q.size());

            for (int j = 0; j < i; j++)
                assertEquals((int)(i-1-j), (int)q.poll());

            for (int j = 0; j < i; j ++)
                assertTrue(q.add(j));
            
            q.clear();
            assertEquals(0, q.size()); 
        }
    }

    /**
     * Test corner case for iterator
     */
    @Test (timeout = 1000, expected = NoSuchElementException.class)
    public void iterateNSEExceptionCornerCase ()
    {
        ArrayStack<Object> q = new ArrayStack <Object>();
        Iterator<Object> itr = q.iterator ();
        assertTrue ("Call hasNext() on iterator returned by new stack", 
            false == itr.hasNext());

        // the following line should throw the NSEException
        itr.next ();
    }
    
    /**
     * Test whether iterator.remove throws an UnsupportedOperationException
     */
    @Test (timeout = 1000, expected = UnsupportedOperationException.class)
    public void testIteratorRemove ()
    {
        ArrayStack<Object> h = new ArrayStack<Object> ( );
        Iterator<Object> it = h.iterator();
        it.remove();
    }
    
    
    /**
     * This is an example of a randomized stress test. The goal here is to bring
     * out corner cases. It's also important to do more targeted testing. Try to
     * test odd things that might happen. For example, the circular behavior of
     * the array stack is a good place to find issues.
     */
    @Test
    public void randomAddRemoveTest ()  
    {
        Random r = new Random ();
        ArrayStack <Object> mine = new ArrayStack <Object> ();
        LinkedList <Object> compare = new LinkedList <Object> ();
        
        int NTESTS = 10000;
        
        for (int i = 0; i < NTESTS; i ++) 
        {
            if (compare.size() == 0 || r.nextBoolean()) 
            {
                Object o = new Object ();
                mine.push (o);
                compare.offer (o);
            } 
            else 
            {
                assertEquals ("object from peek()", compare.getLast(), mine.peek());
                assertEquals ("size after peek()",  compare.size(), mine.size());
                assertEquals ("object from poll()", compare.removeLast(), mine.poll());
            }
            
            assertEquals ("size after operations", compare.size(), mine.size());
        }
    }
    
    /** A method that tests for equality of two stacks
     * @param stdStack an instance of the 'standard' implementation of the stack.
     * @param myStack an instance of my implementation of the stack object.
     * @returns true if they are both equal; false otherwise.*/
    private static boolean equals(LinkedList<Object> stdStack,
        ArrayStack<Object> myStack)
    
    {
        //LinkedList's iterator is forward while ArrayStack's
        // is backwards
        Iterator<Object> myIt = myStack.iterator();
        Object[] objs = stdStack.toArray(new Object[stdStack.size()]);

        for (int i = 0; i < objs.length; i++)
        {
            if (! myIt.hasNext())
            {
                return false;
            }
            
            Object std = objs[(objs.length-1-i)];
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
        
        return false == myIt.hasNext();
    }
    
    @Test
    public void resizeTest () 
    {
        Random rnd = new Random();
        
        for (int i = 0; i < 20; i ++)
        {
            LinkedList<Object> stdStack = new LinkedList<Object>();
            ArrayStack<Object> myStack = new ArrayStack<Object> ();
            int nullObjCount = 0;
            
            // inner loop that tests wrapping around feature.
            for (int k = 0; k < 4; k++)
            {
                int toAdd = 1000;
                Object objs[] = new Object[toAdd];
                
                for (int j = 0; j < toAdd; j ++)
                {
                    if (stdStack.size() > 0)
                        assertEquals(
                            "during adding: stdStack.peek== myStack.peek ?", 
                            stdStack.getLast(), myStack.peek());
                    else
                        assertEquals(null, myStack.peek());

                    assertEquals("during adding: stdStack.size() == myStack.size()? ", stdStack.size(), myStack.size());
                    
                    objs[j] = (rnd.nextBoolean() ? new Object() : null);
                    if (objs[j] == null)
                    {
                        nullObjCount++;
                    }

                    stdStack.addLast(objs[j]);
                    myStack.push(objs[j]);
                    
                    assertEquals("during adding: stdStack.peek== myStack.peek ?", stdStack.getLast(), myStack.peek());
                    assertEquals("during adding: stdStack.size() == myStack.size()? ", stdStack.size(), myStack.size());
                }
    
                for (int j = 0; j < toAdd; j ++)
                {
                    assertEquals("after adding: myStack.contains?", true, myStack.contains(objs[j]));
                }
                
                Iterator<Object> it = myStack.iterator();
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
                
                // Test for equality against the stdStack after we run through 
                // the iterator, to check whether the iterator has modified anything.
                assertEquals("after adding: myStack == stdStack ? ", true, equals(stdStack, myStack));
                assertEquals("after adding: myStack.size() == stdStack.size() ? ", stdStack.size(), myStack.size());
                
                assertEquals("after adding: iterator null count", nullObjCount, nullCount);
                assertEquals("after adding: iterator elem count", stdStack.size(), count);
                assertEquals("after adding: iterator no such element exception", true, exception);
                
                // 'toRemove' can be zero.
                int toRemove = Math.min(toAdd, rnd.nextInt(toAdd + 1));
                for (int j = 0; j < toRemove; j ++)
                {
                    Object std = stdStack.removeLast();
                    Object my = myStack.poll();
                    if (std == null)
                    {
                        nullObjCount--;
                    }
                    assertEquals("during removing: stdStack.poll == myStack.poll", std, my);
                }
                
                for (int j = 0; j < toAdd; j ++)
                {
                    assertEquals("after removing:  myStack.contains", 
                            stdStack.contains(objs[j]), myStack.contains(objs[j]));
                }
                
                it = myStack.iterator();
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
                
                // Test for equality against the stdStack after we run through 
                // the iterator, to check whether the iterator has modified anything.
                assertEquals("after removing: myStack == stdStack ? ", true, equals(stdStack, myStack));
                assertEquals("after removing: myStack.size() == stdStack.size() ? ", stdStack.size(), myStack.size());
                
                assertEquals("after removing: iterator null count", nullObjCount, nullCount);
                assertEquals("after removing: iterator elem count", stdStack.size(), count);
                assertEquals("after removing: iterator no such element exception", true, exception);
                
            }
        }
    }
}
