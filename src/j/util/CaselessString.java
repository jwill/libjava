package j.util;

import java.util.*;
import java.text.*;
import java.io.*;

/**
 * A wrapper class for strings that does case-insensitive,
 * locale-neutral comparisons and hashing by default.
 *
 * This is primarily for use in hash tables where case-insensitive
 * key comparisons are required. 
 * When instantiating a copy of this class with a string, 
 * the actual string value, including the case, will be preserved for
 * later retrieval. Note that varying accented forms of a character 
 * will be generally considered different.
 * 
 * All instances of this class are immutable, just like
 * all instances of the String class are.
 *
 * This class is thread-safe.
 * @author Lucas Tan
 */
public final class CaselessString
    implements Comparable<CaselessString>, CharSequence, Serializable
{
    private static final Collator NEUTRAL_COLLATOR = 
        Collator.getInstance(Locale.ROOT);
    
    static
    {
        // Make it case-insensitive.
        NEUTRAL_COLLATOR.setStrength(Collator.SECONDARY);
        NEUTRAL_COLLATOR.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
    }
    
    private final String value;
    private final int hashCode;
    
    /**
     * @param value String value to be used as the underlying value.
     * @exception IllegalArgumentException if value is null
     */ 
    public CaselessString(String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("value cannot be null");
        }
        
        this.value = value;

        // We can cache the hashCode here since string is immutable
        this.hashCode = this.value.toLowerCase(Locale.ROOT).hashCode();
    }
    
    /**
     * Gets the underlying string value stored in this object, 
     * with the case preserved (as passed to the constructor)
     */
    public String getValue()
    {
        return this.value;
    }
    
    /**
     * Gets the underlying string value stored in this object,
     * with the case preserved (as passed to the constructor)
     */
    @Override
    public String toString()
    {
        return this.value;
    }
    
    /**
     * Performs an equality test in a case-insensitive and locale-neutral
     * manner.
     * @param obj This can be a String or CaselessString instance.
     *        Instances of other classes will result in false being returned.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof String)
        {
            return NEUTRAL_COLLATOR.equals((String)obj, this.value);
        }
        
        if (obj instanceof CaselessString)
        {
            CaselessString cis = (CaselessString)obj; 
            return cis.hashCode == this.hashCode &&
                NEUTRAL_COLLATOR.equals(cis.value, this.value);
        }
        
        return false;
    }
    
    @Override
    public int hashCode()
    {
        return this.hashCode;
    }

    /**
     * Compares this string object to another in a case-insensitive 
     * and locale-neutral manner.
     * @param o Cannot be null.
     */
    @Override
    public int compareTo(CaselessString o)
    {
        return NEUTRAL_COLLATOR.compare(this.value, o.value);
    }

    /**
     * This behaves exactly the same as the substring() method for the
     * underlying string, 
     * except that an instance of CaselessString is returned.
     */
    @Override
    public CharSequence subSequence(int start, int end)
    {
        return new CaselessString(this.value.substring(start, end));
    }

    /**
     * Gets the character at a specified index in the underlying string.
     * The actual case of the character (as passed to the constructor)
     * is preserved.
     */
    @Override 
    public char charAt(int index)
    {
        return this.value.charAt(index);
    }

    /**
     * Gets the length of the underlying string.
     */
    @Override
    public int length()
    {
        return this.value.length();
    }
}

