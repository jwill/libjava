/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an HTTP chunked output stream 
 */
public class ChunkedOutputStream extends FilterOutputStream
{
    private static final byte[] NEW_LINE = {'\r', '\n'};

    public ChunkedOutputStream(OutputStream out)
    {
        super(out);

        if (out == null)
            throw new IllegalArgumentException("out is null");
    }

    @Override
    public void write(int i) throws IOException
    {
        byte[] b = {(byte)i};
        write(b, 0, 1);
    }
    
    @Override
    public synchronized void write(byte[] b, int off, int len)
        throws IOException
    {
        if (len > 0)
        {
            this.out.write(String.format("%x", len).getBytes("US-ASCII"));
            this.out.write(NEW_LINE);
            this.out.write(b, off, len);
            this.out.write(NEW_LINE);
        }
        else
        {
            // this should throw an exception if len < 0
            // or do nothing if len == 0
            this.out.write(b, off, len);
        }
    }

    @Override
    public void close() throws IOException
    {
        // last chunk is empty
        this.out.write('0');
        this.out.write(NEW_LINE);

        // no trailing headers
        this.out.write(NEW_LINE);

        flush();
        this.out.close(); 
    }

    @Override
    public void flush() throws IOException
    {
        this.out.flush();
    }
}

