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
public class FixedInputStream extends InputStream
{
    private final int size;
    private final InputStream inner;
    private int curRead;

    public FixedInputStream(InputStream inner, int size)
    {
        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        this.curRead = 0;
        this.size = size;
        this.inner = inner;
    }
    
    @Override
    public boolean markSupported()
    {
        return false;
    }

    @Override
    public int read(byte[]b)
        throws IOException
    {
        return read(b, 0, b.length);
    }

    @Override
    public int read() throws IOException
    {
        byte[] b = {0};
        final int read = this.read(b, 0, 1);
        if (read < 0) return read;
        return b[0];
    }
    
    @Override
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        if (this.curRead >= this.size)
            return -1;

        final int toRead = Math.min(len, this.size - this.curRead);
        final int read = this.inner.read(b, off, toRead);
        if (read < 0) return -1;

        this.curRead += read;
        return read;
    }

    @Override
    public void close() throws IOException
    {
        this.inner.close();
    }
}

