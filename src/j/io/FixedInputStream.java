/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an input stream with a predetermined size.
 * The size of this input stream is limited to a predefined size,
 * regardless of the size of the underlying stream.
 */
public class FixedInputStream extends FilterInputStream
{
    /** Number of bytes left. */
    private int curLeft;

    /**
     * @exception IllegalArgumentException 
     *      if inner is null or size <= 0
     */
    public FixedInputStream(InputStream inner, int size)
    {
        super(inner);

        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        if (size <= 0)
            throw new IllegalArgumentException("size must > 0");

        this.curLeft = size;
    }
    
    /**
     * @return false. Not supported.
     */
    @Override
    public boolean markSupported()
    {
        return false;
    }

    /**
     * @exception IOException always thrown since not supported.
     */
    @Override 
    public void reset() throws IOException
    {
        throw new IOException("not supported");
    }
    
    /**
     * Does nothing since not supported.
     */
    @Override 
    public void mark(int k) 
    {
        // nothing
    }

    private InputStream getIn() throws IOException
    {
        final InputStream local = this.in;
        if (local == null)
            throw new IOException("stream closed");
        return local;
    }

    @Override
    public synchronized int read() throws IOException
    {
        final InputStream local = getIn();

        if (this.curLeft > 0)
        {
            final int ret = local.read();
            if (ret >= 0)
                this.curLeft --;

            return ret;
        }

        return -1;
    }
    
    @Override
    public synchronized int read(byte[] b, int off, int len)
        throws IOException
    {
        final InputStream local = getIn();

        if (this.curLeft <= 0)
            return -1;

        final int toRead = Math.min(len, this.curLeft);
        final int read = local.read(b, off, toRead);
        if (read >= 0)
        {
            this.curLeft -= read;
        }

        return read;
    }
 
    @Override
    public synchronized long skip(long n) throws IOException
    {
        final InputStream local = getIn();

        n = Math.min(n, this.curLeft);

        final long nskip = local.skip(n);
        if (nskip >= 0) this.curLeft -= (int)nskip;
        return nskip;
    }

    @Override
    public void close() throws IOException
    {
        // don't use getIn() since we allow closing
        // of a closed stream
        final InputStream local = this.in;

        if (local == null) return;

        local.close();
        this.in = null;
    }
}

