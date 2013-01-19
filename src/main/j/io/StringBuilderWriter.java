package j.io;

import java.io.*;

/**
 * Behaves like StringWriter except that it is not thread-safe,
 * hence offering a higher performance.
 * @author Lucas Tan
 */
public class StringBuilderWriter extends Writer
{
    private final  StringBuilder sb;

    public StringBuilderWriter()
    {
        this.sb = new StringBuilder();
    }

    public StringBuilderWriter(int initialCap)
    {
        this.sb = new StringBuilder(initialCap);
    }

    public StringBuilderWriter append(char c)
    {
        this.sb.append(c);
        return this;
    }

    public StringBuilderWriter append(CharSequence cs)
    {
        if (cs == null) 
            this.sb.append("null");
        else
            this.sb.append(cs);
        return this;
    }

    public StringBuilderWriter append(CharSequence cs, int s, int e)
    {
        CharSequence real = (cs == null ? "null" : cs);
        this.sb.append(real.subSequence(s, e));
        return this;
    }

    public StringBuilder getBuffer()
    {
        return this.sb;
    }

    public void flush(){/*nothing*/}
    
    public void close(){/*nothing*/}
    
    public void write(int c)
    {
        this.sb.append((char)c);
    }

    public void write(char[] c)
    {
        this.sb.append(c);
    }

    public void write(String s)
    {
        this.sb.append(s);
    }

    public void write(String str, int s, int e)
    {
        this.sb.append(str, s, e);
    }

    public void write(char[] c, int s, int e)
    {
        this.sb.append(c, s, e);
    }
    
    @Override
    public String toString()
    {
        return this.sb.toString();
    }
}

