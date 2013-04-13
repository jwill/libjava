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
}