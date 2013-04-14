package j.str;

public final class StrUtil
{
    private StrUtil(){}
    
    private static final char[] HEX = "0123456789ABCDEF".toCharArray();

    public static String toHex(byte[] b)
    {
        char [] c = new char[b.length*2];
        for (int i = 0; i < b.length; i++)
        {
            int v = (int)b[i];
            c[i*2]   = HEX[(v >> 4) & 15];
            c[i*2+1] = HEX[v & 15];
        }

        return new String(c);
    }

    /**
     * Parses a hex string in to a byte array.
     * @param hex This is handled in a case-insensitive manner. It must
     *        consists of only hex chars, not even spaces.
     * @exception IllegalArgumentException if it contains a non-hex char.
     */
    public static byte[] fromHex(String hex)
    {
        final int hexLen = hex.length();
        byte[] b = new byte[hexLen / 2];

        for (int i = 0; i < hexLen; i += 2)
        {
            int v = 0;
            char c = hex.charAt(i);
            if ('0' <= c && c <= '9')
                v = (c - '0') << 4;
            else if ('A' <= c && c <= 'F')
                v = (c - 'A' + 10) << 4;
            else if ('a' <= c && c <= 'f')
                v = (c - 'a' + 10) << 4;
            else
                throw new IllegalArgumentException();

            c = hex.charAt(i+1);
            if ('0' <= c && c <= '9')
                v |= (c - '0');
            else if ('A' <= c && c <= 'F')
                v |= (c - 'A' + 10);
            else if ('a' <= c && c <= 'f')
                v |= (c - 'a' + 10);
            else
                throw new IllegalArgumentException();
            
            b[i / 2] = (byte)v;
        }
        
        return b;
    }

    /**
     * Removes surrounding quote chars (" or ') from a string.
     * Any whitespace surrounded by the quote chars are preserved but 
     * whitespaces outside of the quote chars are removed. If no quote chars
     * are present the entire string is trimmed of whitespaces.
     * The string is processed adhering to the following rules:
     * - the starting and ending quote chars must be the same.
     * - if the starting or ending quote char is present, the other must also
     *    be present, that is, there must be no unmatched quote char.
     * <pre>
     * Examples: 
     * String s = "  ' hello '  "; // unquote(s) returns "' hello '"
     * String s = "  'hello   ";   // unquote(s) will throw an exception
     * String s = " hello ";       // unquote(s) returns "hello"
     * </pre> 
     * @param str 
     * @exception IllegalArgumentException if at least one 
     * of the rules is violated. 
     */
    public static String unquote(String str)
    {
        str = str.trim();
        
        final int len = str.length();
        
        if (len >= 2)
        {
            char start = str.charAt(0);
            char end = str.charAt(len-1);
            
            boolean isQuote = 
               (start == '"' || start == '\'' ||
                end   == '"' || end   == '\'');
                
            if (isQuote)
            {
                if (start != end)
                    throw new IllegalArgumentException("different quote chars");
            
                return str.substring(1, len-1);
            }
        }
        else if (len >= 1)
        {
            char start = str.charAt(0);

            if (start == '"' || start == '\'')
                throw new IllegalArgumentException("unmatched starting quote");
        }

        return str;
    }
}