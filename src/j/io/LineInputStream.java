/**
 * @author Lucas Tan
 */

package j.io;

import java.io.*;
import java.util.*;

/**
 * This stream allows reading of data in the form of lines as well as
 * in bytes from an underlying binary stream.
 *
 * This class is not thread-safe
 */
public class LineInputStream extends FilterInputStream
{
    private final ByteArrayOutputStream bout;

    /** -1 indicates there is no last byte; else
     * it will be from 0 to 255. */
    private int lastByte;

    /**
     * @param ist Underlying binary stream.
     * @exception IllegalArgumentException if ist is null
     */
    public LineInputStream(InputStream ist)
    {
        super(ist);

        if (ist == null)
            throw new IllegalArgumentException("input stream is null");

        this.lastByte = -1;
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
     * Reads bytes starting from the current position until
     * it is terminated by either a line feed (LF), 
     * a carriage return (CR), a CRLF sequence or the end-of-stream 
     * condition.
     *
     * The bytes in the line will be interpreted using the UTF-8 charset.
     *
     * @return If end of stream and nothing is read, returns
     *         null; else returns the line read, 
     *         excluding the line terminator.
     *
     * @exception IOException if the stream is closed or an I/O error occurs.
     */
    public String readLine() throws IOException
    {
        final InputStream localIn = getIn();

        this.bout.reset();

        if (this.lastByte >= 0)
        {
            this.bout.write(this.lastByte);
            this.lastByte = -1;
        }

    loop:
        while(true)
        {
            final int cur = localIn.read();
            if (cur >= 0)
            {
                switch(v)
                {
                case '\r':
                    this.lastByte = localIn.read();
                    if (this.lastByte == '\n')
                        this.lastByte = -1;

                    break;

                case '\n':
                    break;

                default:
                    this.bout.write(v);
                    continue loop;
                }
            }
            else if (this.bout.size() == 0)
                // nothing is read, so return null.
                return null;

            final byte[] buf = this.bout.toByteArray();
            
            return new String(buf, "UTF-8");
        } // while
    } // method

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
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        final InputStream localIn = getIn();

        if (this.lastByte < 0 || len <= 0)
        {
            return localIn.read(b, off, len);
        }

        b[off] = (byte)(this.lastByte & 255);
        this.lastByte = -1;
        final int read = localIn.read(b, off+1, len-1);
        if (read >= 0) return read+1;
        return 1;
    }

    @Override
    public int read() throws IOException
    {
        final InputStream localIn = getIn();

        if (this.lastByte < 0)
            return localIn.read();

        final int ret = this.lastByte;
        this.lastByte = -1;
        return ret;
    }

    private InputStream getIn() throws IOException
    {
        final InputStream localIn = this.in;
        if (localIn == null)
            throw new IOException("stream closed");

        return localIn;
    }

    @Override
    public long skip(long n) throws IOException
    {
        final InputStream localIn = getIn();

        if (this.lastByte < 0 || n <= 0)
        {
            return localIn.skip(n);
        }

        this.lastByte = -1;
        final long nskip = localIn.skip(n-1);
        if (nskip >= 0) return nskip+1;
        return 1;
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

