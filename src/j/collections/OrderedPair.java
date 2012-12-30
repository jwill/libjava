package j.collections;

/**
 * Represents an ordered 2-tuple, either one or both of the elements 
 * can be null.
 *
 * This class is immutable if and only if both underlying elements 
 * are immutable.
 *
 * In this class, a null element is considered to be "lesser" 
 * than a non-null element. 
 * For example, the pair (null, "Hello") is lesser than ("Hello", "World").
 *
 * @author Lucas Tan
 *
 * @param <A> Type of the first element
 * @param <B> Type of the second element
 */
public final class OrderedPair<A extends Comparable<A>,B extends Comparable<B>>
    implements Comparable<OrderedPair<A,B>>
{
    private final A first;
    private final B second;

    /**
     * Either one or both of the elements can be null.
     * @param first First element in the pair.
     * @param second Second element in the pair.
     * 
     */
    public OrderedPair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }
   
    public A getFirst()
    {
        return this.first;
    }

    public B getSecond()
    {
        return this.second;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof OrderedPair)
        {
            OrderedPair<A,B> p = (OrderedPair<A,B>)o;
            return compareTo(p) == 0;
        }

        return false;
    }
    
    @Override
    public int hashCode()
    {
        int hashCode = 0;
        
        if (this.first != null)
        {
            hashCode = this.first.hashCode();
        }
        
        if (this.second != null)
        {
            hashCode ^= this.second.hashCode();
        }
        
        return hashCode;
    }        
    
    @Override 
    public String toString()
    {
        return "("+this.first+","+this.second+")";
    }

    /**
     * Compares two comparables.
     * Null comes first.
     */
    private static <T> int compare(Comparable<T> a, Comparable<T> b)
    {
        if (a == null)
            if (b == null) return 0;
            else return -1;

        return a.compareTo(b);
    }
    
    /**
     * Compares this pair to another. 
     * 
     * @param o Cannot be null.
     * @return If this pair is "lesser", then returns -1.
     *         If both pairs are equal, then returns 0.
     *         If this pair is "greater", then returns 1.
     */
    @Override
    public int compareTo(OrderedPair<A, B> o)
    {
        int k = compare(this.first, o.first);
        if (k != 0) return k;

        return compare(this.second, o.second);
    }              
}

