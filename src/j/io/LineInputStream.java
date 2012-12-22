/**
 * @author Lucas Tan
 */

package j.io;

import java.io.*;
import java.util.*;

/**
 * This stream allows reading of a line in the UTF-8 charset, as well as, 
 * reading of binary data from an underlying binary stream.
 */
public class LineInputStream extends InputStream
{
    private final InputStream ist;
    private final ByteArrayOutputStream bout;

    private int lastByte;
    private boolean hasLastByte;

    public LineInputStream(InputStream ist)
    {
        if (ist == null)
            throw new IllegalArgumentException("input stream is null");

        this.ist = ist;
        this.hasLastByte = false;
        this.bout = new ByteArrayOutputStream();
    }

    @Override
    public boolean markSupported()
    {
        return false;
    }

    public String readLine() throws IOException
    {
        this.bout.reset();

        if (this.hasLastByte)
        {
            this.hasLastByte = false;
            this.bout.write(this.lastByte);
        }

        boolean cont = true;
        while (cont)
        {
            int v = this.ist.read();
            if (v < 0) break;
            switch(v)
            {
            case '\r':
                this.lastByte = this.ist.read();
                if (this.lastByte >= 0)
                {
                    if (this.lastByte != '\n')
                    {
                        this.hasLastByte = true;
                    }
                }
                cont = false;
                break;

            case '\n':
                cont = false;
                break;

            default:
                this.bout.write(v);
                break;
            }
        }

        return new String(this.bout.toByteArray(), "UTF-8");
    }

    @Override
    public int read(byte [] b) throws IOException
    {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        if (this.hasLastByte && len > 0)
        {
            b[off] = (byte)(this.lastByte & 255);
            this.hasLastByte = false;
            int read = this.ist.read(b, off+1, len-1);
            if (read < 0) return 1;
            return read+1;
        }

        int read = this.ist.read(b, off, len);
        return read;
    }

    @Override
    public int read() throws IOException
    {
        if (this.hasLastByte)
        {
            this.hasLastByte = false;
            return this.lastByte;
        
        }

        return this.ist.read();
    }

    @Override
    public void close() throws IOException
    {
        this.ist.close();
    }
}

