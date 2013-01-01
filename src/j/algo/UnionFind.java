package j.algo;

import java.util.*;
import java.io.*;

/**
 * An implementation of the union find solution with ranking and 
 * path compression.
 *
 * The elements are modeled as a forest with elements in each tree being
 * in the same set. Each tree has a canonical root.
 *
 * This class is not thread-safe.
 * @author Lucas Tan
 */
public class UnionFind implements Serializable
{
    /** rank[i] = Rank of element i. 
     * A negative value indicates that the element is a canonical root of 
     * the membership set (or tree). The magnitude of the value is the 
     * height of the tree (starting from 1). Note that the height is only 
     * an upper bound due to path compression. The max possible height is 
     * log_2(Integer.MAX_VALUE+1) due to ranking.
     * A non-negative value indicates that the element is a non-root. The value
     * is the zero-based index of its parent in the tree. */
    private final int[] rank;
    
    /** Number of disjoint sets (or trees). */
    private int numDisjoint;

    /**
     * Constructs an instance with a specified number of elements. All 
     * elements are initially disjoint, that is, each element exists in a 
     * unique set by itself.
     * @param numElems The total number of elements.
     * @exception IllegalArgumentException if numElems is non-positive.
     */
    public UnionFind(int numElems)
    {
        if (numElems <= 0)
        {
            throw new IllegalArgumentException("numElems must > 0");
        }
        
        this.numDisjoint = numElems;
        this.rank = new int[numElems];
    }
    
    /**
     * Checks whether two elements are in the same set.
     * The order of specifying the element indices does not matter.
     * @param a The zero-based index of the first element.
     * @param b The zero-based index of the other element.
     * @return true if a and b are in the same set or a and b are the same; 
     *         false otherwise.
     * @exception IndexOutOfBoundsException 
     *            if a or b is negative or out of bound.
     */
    public boolean connected(int a, int b)
    {
        if ((a | b) >= 0 && a < this.rank.length && b < this.rank.length)
        {
            return a == b || find(a) == find(b);
        }
        
        throw new IndexOutOfBoundsException();
    }
    
    /**
     * Union the sets that contain two specified elements.
     * The order of specifying the elements does not matter.
     * @param a Zero-based index of the first element.
     * @param b Zero-based index of the other element.
     * @return true if the two sets are  disjoint prior to union; 
     *         false otherwise.
     * @exception IndexOutOfBoundsException 
     *            if a or b is negative or out of bound.
     */
    public boolean union(int a, int b)
    {
        if ((a | b) >= 0 && a < this.rank.length && b < this.rank.length)
        {
            if (a == b) return false;
        
            int x = find(a);
            int y = find(b);
        
            // If they are already connected, then do not
            // union the sets.
            if (x == y) return false;
        
            // If root x has a greater height than root y,
            // then make y a child of x.
            if (this.rank[x] < this.rank[y])
            {
                // swap x and y
                int t = x;
                x = y;
                y = t;
            } 
            else if (this.rank[x] == this.rank[y])
            {
                this.rank[y] --;
            } 
        
            // make x a child of y
            this.rank[x] = y;
            this.numDisjoint --;
            return true;
        }

        throw new IndexOutOfBoundsException();
    }
    
    /**
     * Gets the number of disjoint sets.
     * @return Returns a number between 1 and the
     *         total number of elements, inclusively.
     */
    public int getDistinctCount()
    {
        return this.numDisjoint;
    }
    
    /**
     * Checks whether all the elements are connected together, that is,
     * they are all in the same set.
     */
    public boolean areAllConnected()
    {
        return this.numDisjoint <= 1;
    }
    
    /**
     * Gets the zero-based index of the canonical root of an element.
     * @param idx The zero-based index of the element.
     */
    private int find(int idx)
    {
        final int theRank = this.rank[idx];

        // If this elem is a root, then return its index.
        if (theRank < 0) 
            return idx;
       
        // Path compression.
        // This recursion shouldn't cause a stack overflow
        // since the maximum length of a path (due to ranking),
        // is log_2(Integer.MAX_VALUE + 1)
        return this.rank[idx] = find(theRank);
    }
}

