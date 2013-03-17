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
     * Only the amperstand, angle brackets and double and single quote
     * characters are encoded.
     * @exception NullPointerException if s is null
     */
    public static String htmlEncode(String s)
    {
        return htmlEncodeInternal(s).toString();
    }

    private static StringBuilder htmlEncodeInternal(String s)
    {
        final int len = s.length();
        final StringBuilder w = new StringBuilder(len);

        for (int i = 0; i < len; i++)
        {
            final char c = s.charAt(i);
            switch(c)
            {
            case '<': w.append("&lt;"); break;
            case '>': w.append("&gt;"); break;
            case '&': w.append("&amp;"); break;
            case '"': w.append("&quot;"); break;
            case '\'': w.append("&apos;"); break;
            default:  w.append(c);
            }
        }

        return w;
    }

    /**
     * Writes the HTML encoded version of a string to a writer
     * output stream.
     * Only the amperstand, angle brackets and double quote
     * characters are encoded.
     * @exception NullPointerException if s or w is null
     */
    public static void htmlEncode(String s, Writer w)
        throws IOException
    {
        w.append(htmlEncodeInternal(s));
    }
}

