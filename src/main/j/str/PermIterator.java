package j.str;

import java.util.*;
import j.util.Util;

/**
 * An iterator that generates random permutations of a given string.
 * 
 * The given string must not have any duplicate characters, otherwise
 * duplicate permutations will be generated.
 * 
 * This algorithm uses O(n) memory space but each call to the next() method
 * takes O(n^2) time.
 *
 * @author Lucas Tan
 */
public class PermIterator 
  implements Iterator<String>
{
    private static class State
    {
        int index;
        boolean isBefore;
    }
    
    /** used[i] == true iff chars[i] is used */
    private final boolean[] used;

    /** the characters in the string */
    private final char[] chars;

    /** Buffer to hold the next permutation */
    private final char[] buf;

    private final State[] states;

    /** next string to return; null if none. */
    private String nextStr;

    /**
     * @param str Must not have any duplicate characters.
     * @exception NullPointerException if str is null.
     * @exception IllegalArgumentException if str is empty.
     */
    public PermIterator(String str)
    {
        if (str.isEmpty()) throw new IllegalArgumentException();

        this.chars = str.toCharArray();
        this.buf = new char[chars.length];
        this.used = new boolean[chars.length];
        this.states = new State[chars.length];

        Util.shuffle(this.chars);
       
        // Initialize state.
        int i;
        for (i = 0; i < this.states.length; i++)
        {
            this.buf[i] = this.chars[i];
            this.used[i] = true;
            this.states[i] = new State();
            this.states[i].isBefore = false;
            this.states[i].index = i;
        }

        this.used[i-1] = false;
        this.states[i-1].isBefore = true;

        this.nextStr = nextInternal();
    }

    /**
     * Not supported.
     * @exception UnsupportedOperationException always thrown.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext()
    {
        return this.nextStr != null;
    }

    @Override
    public String next()
    {
        if (this.nextStr != null)
        {
            final String ret = this.nextStr;
            this.nextStr = nextInternal();
            return ret;
        }

        throw new NoSuchElementException();
    }
    
    /** Returns the next one. null if none */
    private String nextInternal()
    {

    /*
    recursion:
    for (int a = 0; a < ; a++)
        if (used[a]) continue;
        used[a] = true;
        buf[0] = chars[a];

        for (int b = 0; b <
            if (used[b]) continue;
            used[b] = true;
            buf[1] = chars[b];

            ... for ...

                buf[n-1]=chars[..];
                output buf
                used[..]=false;
                

            used[b]=false;

        used[a] = false;
    */

    // iterative version of recursion
    mainLoop:
        for (int charPos = this.buf.length - 1; charPos < this.buf.length;)
        {
            State s = this.states[charPos];
            if (s.isBefore)
            {
                for (int i = s.index; i < this.buf.length ; i++)
                {
                    if (this.used[i]) continue;
                    this.used[i] = true;
                    this.buf[charPos] = this.chars[i];
                    s.index = i;
                    s.isBefore = false;
                    if (charPos == this.buf.length-1)
                        break mainLoop;

                    charPos ++;
                    continue mainLoop;
                }

                s.index = 0;
                charPos --;
                if (charPos >= 0) continue;
                return null;
            }
            else
            {
                this.used[s.index] = false;
                s.index++;
                s.isBefore = true;
            }
        }

        return new String(this.buf);
    }
}


