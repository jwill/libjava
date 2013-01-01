package j.collections;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.AbstractQueue;
import java.util.Queue;
import java.util.Collection;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.io.Serializable;


/**
 * A queue implemented using a circular array that doubles in size when
 * run of space.
 *
 * This queue implementation permits null elements.
 *
 * This class is not thread-safe.
 * @author Lucas Tan
 * @param <E> Class of the element.
 */
public class ArrayQueue<E> extends AbstractQueue<E>
    implements Iterable<E>, Queue<E>, Collection<E>, Serializable
{
    private static final long serialVersionUID = -2024744406713321676L;

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] elems;

    /** Zero-based index of first enqueued elem. */
    private int startIdx;
    
    /** No. of elems in array */
    private int size;

    /** modification counter */
    private int modCount;

    private class Iter implements Iterator<E>
    {
        private int cur; // cursor index
        private int left; // num elems left
        private int expectedModCount;

        public Iter()
        {
            this.cur = startIdx;
            this.left = size;
            this.expectedModCount = modCount;
        }

        @Override
        public boolean hasNext()
        {
            return this.left > 0;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public E next()
        {
            if (this.left > 0)
            {
                if (modCount == this.expectedModCount)
                {
                    final E ret = (E) elems[this.cur];
                    this.left --;
                    this.cur = (this.cur + 1) % elems.length;
                    return ret;
                }

                throw new  ConcurrentModificationException();
            }
            throw new NoSuchElementException();
        }
    }

    public ArrayQueue()
    {
        this(DEFAULT_CAPACITY);
    }

    public ArrayQueue(int initialCapacity)
    {
        if (initialCapacity <= 0)
            throw new IllegalArgumentException("initial capacity must > 0");
        this.elems = new Object[initialCapacity];
        this.size = 0;
        this.startIdx = 0;
        this.modCount = 0;
    }

    private void copyTo(Object[] dest)
    {
        final int leftOverSize = this.elems.length - this.startIdx;

        // If not wrapped around, ...
        if (leftOverSize >= this.size)
        {
                System.arraycopy(
                    this.elems, this.startIdx, 
                    dest, 0, 
                    this.size);
        }
        else
        {
                System.arraycopy(
                    this.elems, this.startIdx, 
                    dest, 0, 
                    leftOverSize);
                
                System.arraycopy(
                    this.elems, 0, 
                    dest, leftOverSize,
                    this.size - leftOverSize);
        }
    }

    private void ensureCapacity()
    {
        if (this.elems.length > this.size)
            return;

        Object[] newElems = new Object[this.elems.length*2];
    
        copyTo(newElems);

        this.elems = newElems;
        this.startIdx = 0;
    }

    @Override
    public void clear()
    {
        this.modCount++;

        final int leftOverSize = this.elems.length - this.startIdx;
        
        // Zero out the array so GC can work ...

        // If not wrapped around, ...
        if (leftOverSize >= this.size)
        {
            // args: fill(arr, from, to, value)
            // where from is included but to is excluded
            Arrays.fill(this.elems, this.startIdx, 
                this.startIdx+this.size, null);
        }
        else
        {
            Arrays.fill(this.elems, this.startIdx,
                this.elems.length, null);

            Arrays.fill(this.elems, 0,
                this.size - leftOverSize, null);
        }
        
        this.size = this.startIdx = 0;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size == 0;
    }

    @Override
    public int size()
    {
        return this.size;
    }

    /**
     * Behaves like ArrayList.toArray()
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a)
    {
        if (a.length < this.size)
        {
            // Make a new array of a's runtime type, but my contents:
            final Class newType = a.getClass();
            
            a = newType.equals(Object[].class)
              ? (T[]) new Object[this.size]
              : (T[]) Array.newInstance(newType.getComponentType(), this.size);
        
            copyTo(a);
            return a;
        }

        copyTo(a);
        
        if (a.length > this.size)
            a[this.size] = null;
        
        return a;
    }

    /**
     * Gets the elements in this queue in an array, with the first
     * element in the array being the element at the front of the queue.
     * The caller is free to modify the returned array without affecting
     * the underlying array in this queue.
     */ 
    @Override
    public Object[] toArray()
    {
        Object[] newElems = new Object[this.size];
        copyTo(newElems);
        return newElems;
    }

    /**
     * Adds an element to the back of the queue.
     * This will resize the underlying array if insufficient space is
     * available. The only error this method can throw is OutOfMemoryError.
     * @param elem Can be null.
     * @exception OutOfMemoryError if unable to expand the underlying array
     * @return always true.
     */
    @Override
    public boolean offer(E elem)
    {
        return add(elem);
    }
    
    /**
     * Adds an element to the back of the queue.
     * This will resize the underlying array if insufficient space is
     * available. The only error this method can throw is OutOfMemoryError.
     * @param elem Can be null.
     * @exception OutOfMemoryError if unable to expand the underlying array
     * @return always true.
     */
    @Override
    public boolean add(E elem)
    {
        this.modCount++;
        ensureCapacity();

        // use long to prevent overflow
        final int idx = 
            (int)((this.startIdx + (long)this.size) % this.elems.length);
        this.elems[idx] = elem;
        this.size++;
        return true;
    }

    /**
     * Gets but does not remove the element at the front.
     * @exception NoSuchElementException if the queue is empty
     */
    @Override
    @SuppressWarnings("unchecked")
    public E element()
    {
        if (this.size == 0)
            throw new NoSuchElementException();
        
        return (E) this.elems[this.startIdx];
    }

    /**
     * Gets but does not remove the element at the front.
     * @return the element if the queue is non-empty; else null.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E peek()
    {
        if (this.size == 0)
            return null;
        
        return (E) this.elems[this.startIdx];
    }

    
    /**
     * Gets and removes the element at the front.
     * @return the element if the queue is non-empty; else null.
     */
    @Override
    @SuppressWarnings("unchecked")
    public E poll()
    {
        this.modCount++;
        
        if (this.size == 0)
            return null;
        
        final E ret = (E) this.elems[this.startIdx];
        this.elems[this.startIdx] = null;
        this.startIdx = (this.startIdx + 1) % this.elems.length;
        this.size--;
        return ret;
    }

    
    /**
     * Gets and removes the element at the front.
     * @exception NoSuchElementException if the queue is empty
     */
    @Override
    @SuppressWarnings("unchecked")
    public E remove()
    {
        this.modCount++;
        
        if (this.size == 0)
            throw new NoSuchElementException();
        
        final E ret = (E) this.elems[this.startIdx];
        this.elems[this.startIdx] = null;
        this.startIdx = (this.startIdx + 1) % this.elems.length;
        this.size--;
        return ret;
    }

    /**
     * Gets an iterator that can enumerate the elements in the queue order.
     * The iterator returned does not support removal of elements from
     * the queue. The iterator will throw a ConcurrentModificationException
     * exception when it detects any modification
     * being done to the queue concurrently while iterating through.
     */
    @Override
    public Iterator<E> iterator()
    {
        return new Iter();
    }
}


