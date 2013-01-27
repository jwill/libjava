package j.str;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.*;
import j.str.PermIterator;

public class PermIteratorTest
{
    @Test(expected = IllegalArgumentException.class)
    public void emptyString()
    {
        new PermIterator("");
    }

    @Test(expected = NullPointerException.class)
    public void nullString()
    {
        new PermIterator(null);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void removeTest()
    {
        new PermIterator("1").remove();
    }

    @Test
    public void test()
    {
        String str = "01234"; // DO NOT CHANGE

        List<String> list = new ArrayList<String>();

        final int len = str.length();
        for (int i = 1; i <= len; i++)
        {
            list.clear();
            String sub = str.substring(0, i);
            PermIterator p = new PermIterator(sub);
            while (p.hasNext())
                list.add(p.next());
 
            verify(i, list);

            boolean thrown =false;
            try
            {
                p.next();
            }
            catch (NoSuchElementException e)
            {
                thrown = true;
            }

            assertTrue(thrown);
        }
    }

    private static int factorial(int n)
    {
        int f = 1;
        while (n >= 2) f *= n--;
        return f;
    }

    private static void verify(int size, List<String> list)
    {
        assertEquals(factorial(size), list.size());

        Set<String> all = new HashSet<String>();
        boolean used[] = new boolean[size];
        for (String s : list)
        {
            assertTrue(s != null);
            assertEquals(s.length(), size);
            assertTrue(! all.contains(s));

            all.add(s);

            for (int idx = 0; idx < size; idx++)
            {
                int k = s.charAt(idx)-'0';
                if (used[k]) assertTrue(false); // failed!
                used[k] = true;
            }

            for (int i = 0; i < used.length; i++)
                used[i] = false;
        }
    }
}

