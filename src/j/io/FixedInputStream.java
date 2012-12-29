/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an input stream with a predetermined size.
 * The size of this input stream is the minimum of
 * a predefined size and the actual size of the underlying stream.
 *
 * This class is thread-safe.
 */
public class FixedInputStream extends FilterInputStream
{
    /** Number of bytes left in the stream for reading. 
     * Invariant: non-negative */
    private int curLeft;

    /**
     * @param size if zero, then the end of stream is reached immediately,
     *        and no byte can be read from this stream at all.
     * @exception IllegalArgumentException 
     *      if inner is null or size is negative
     */
    public FixedInputStream(InputStream inner, int size)
    {
        super(inner);

        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        if (size < 0)
            throw new IllegalArgumentException("size must >= 0");

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

    /**
     * Gets the number of bytes that can be read without blocking.
     * This method might not be very useful when more than one thread
     * reads from this stream, as the return value could become inaccurate
     * immediately upon returning.
     * See InputStream.available() for a detailed description of this 
     * method.
     * @exception IOException when this stream is closed or an error occurs.
     */
    @Override
    public int available() throws IOException
    {
        // reading this.curLeft is not thread-safe
        // but it doesn't matter since we can never be 100% accurate
        // when this class is used by multiple threads.
        return Math.min(getIn().available(), this.curLeft);
    }

    @Override
    public int read() throws IOException
    {
        final InputStream local = getIn();

        int ret = -1;

        synchronized(this)
        {
            if (this.curLeft > 0)
            {
                ret = local.read();
                // it is ok to decrement even if ret == -1.
                // This just means we have prematurely reached
                // the end of the stream, and curLeft will no longer
                // be a counter of the number of bytes left.
                // Future attempts to read from the underlying stream
                // will just get -1.
                this.curLeft --;
            }
        }

        return ret;
    }
    
    @Override
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        final InputStream local = getIn();

        int nread = -1;

        synchronized(this)
        {
            if (this.curLeft > 0)
            {
                // this block works even if len <= 0
                final int toRead = Math.min(len, this.curLeft);
                nread = local.read(b, off, toRead);
                if (nread >= 0)
                {
                    this.curLeft -= nread;
                }
            }
        }

        return nread;
    }
 
    @Override
    public long skip(long n) throws IOException
    {
        final InputStream local = getIn();

        long nskip = 0;
        
        synchronized(this)
        {
            // this block works even if n <= 0
            n = Math.min(n, this.curLeft);

            nskip = local.skip(n);
            if (nskip >= 0) this.curLeft -= (int)nskip;
        }

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

