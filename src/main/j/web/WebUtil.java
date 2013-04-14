package j.web;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import j.str.*;
import j.io.*;

/**
 * Web utilities.
 * @author Lucas Tan
 */
public final class WebUtil
{
    private WebUtil(){}

    private static final Pattern ATTRIB_PATTERN = 
        // group 1: attrib name (allowing namespace)
        // group 2: value including quote chars if any
        Pattern.compile("([\\w:]+)\\s*=\\s*(\"[^\"]*\"|'[^']*'|\\S*)",
            Pattern.CASE_INSENSITIVE);

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

    /**
     * Parses an HTML fragment containing attribute pairs without the
     * tag name, in to a map.
     * This is a forgiving parser that does not adhere strictly to XML rules,
     * but well-formed XML are guaranteed to be parsed correctly.
     * @param html This must be in the format: attrib1="value" attrib2="value"
     */
    public static Map<String,String> parseAttrib(String html)
    {
        Map<String,String> map = new HashMap<String,String>();

        Matcher m = ATTRIB_PATTERN.matcher(html);
        while(m.find())
        {
            map.put(m.group(1), StrUtil.unquote(m.group(2)));
        }

        return map;
    }
}

