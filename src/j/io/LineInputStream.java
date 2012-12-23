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
public class LineInputStream extends FilterInputStream
{
    private final ByteArrayOutputStream bout;

    private int lastByte;
    private boolean hasLastByte;

    /**
     * @param ist Underlying binary stream.
     * @exception IllegalArgumentException if ist is null
     */
    public LineInputStream(InputStream ist)
    {
        super(ist);

        if (ist == null)
            throw new IllegalArgumentException("input stream is null");

        this.hasLastByte = false;
        this.bout = new ByteArrayOutputStream();
    }

    /**
     * @return false, not supported.
     */
    @Override
    public boolean markSupported()
    {
        return false;
    }

    /**
     * Reads a line starting from the current position until
     * it is terminated by either a line feed (LF), 
     * a carriage return (CR)
     * or a CRLF sequence.
     * The line will be interpreted using the UTF-8 charset.
     * @return The line read, excluding the line terminator.
     */
    public synchronized String readLine() throws IOException
    {
        final InputStream localIn = getIn();

        this.bout.reset();

        if (this.hasLastByte)
        {
            this.hasLastByte = false;
            this.bout.write(this.lastByte);
        }

        boolean cont = true;
        while (cont)
        {
            int v = localIn.read();
            if (v < 0) break;

            switch(v)
            {
            case '\r':
                this.lastByte = localIn.read();
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

    /**
     * Does nothing since not supported.
     */
    @Override
    public void mark(int k) 
    {
        // nothing
    }
    
    /**
     * @exception IOException always thrown since not supported.
     */
    @Override
    public void reset() throws IOException
    {
        throw new IOException("not supported");
    }

    @Override
    public synchronized int read(byte[] b, int off, int len)
        throws IOException
    {
        final InputStream localIn = getIn();

        if (this.hasLastByte && len > 0)
        {
            b[off] = (byte)(this.lastByte & 255);
            this.hasLastByte = false;
            int read = localIn.read(b, off+1, len-1);
            if (read <= 0) return 1;
            return read+1;
        }

        int read = localIn.read(b, off, len);
        return read;
    }

    @Override
    public synchronized int read() throws IOException
    {
        final InputStream localIn = getIn();

        if (this.hasLastByte)
        {
            this.hasLastByte = false;
            return this.lastByte;
        }

        return localIn.read();
    }

    private InputStream getIn() throws IOException
    {
        final InputStream localIn = this.in;
        if (localIn == null)
            throw new IOException("stream closed");

        return localIn;
    }

    @Override
    public synchronized long skip(long n) throws IOException
    {
        final InputStream localIn = getIn();

        if (n >= 1 && this.hasLastByte)
        {
            n--;
            this.hasLastByte = false;
        }

        return localIn.skip(n);
    }

    @Override
    public void close() throws IOException
    {
        // don't use getIn() since we allow closing
        // of a closed stream.
        final InputStream localIn = this.in;

        if (localIn == null) return;

        localIn.close();
        this.in = null;
    }
}

