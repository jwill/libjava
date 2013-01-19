package j.web;

import java.io.*;
import j.io.*;

/**
 * Web utilities.
 * @author Lucas Tan
 */
public final class WebUtil
{
    private WebUtil(){}

    /**
     * Gets the HTML encoded version of a string.
     * Only the amperstand, angle brackets and double quote
     * characters are encoded.
     */
    public static String htmlEncode(String s)
    {
        final StringBuilderWriter w = 
            new StringBuilderWriter(s.length());
        try
        {
            htmlEncode(s, w);
        }
        catch (IOException e)
        {
            // do nothing since should not happen
        }

        return w.toString();
    }

    /**
     * Writes the HTML encoded version of a string to a writer
     * output stream.
     * Only the amperstand, angle brackets and double quote
     * characters are encoded.
     */
    public static void htmlEncode(String s, Writer w)
        throws IOException
    {
        final int len = s.length();
        for (int i = 0; i < len; i++)
        {
            final char c = s.charAt(i);
            switch(c)
            {
            case '<': w.append("&lt;"); break;
            case '>': w.append("&gt;"); break;
            case '&': w.append("&amp;"); break;
            case '"': w.append("&quot;"); break;
            default:  w.append(c);
            }
        }
    }
}

