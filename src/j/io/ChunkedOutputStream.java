/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an HTTP chunked output stream 
 */
public class ChunkedOutputStream extends OutputStream
{
    private static final byte[] NEW_LINE = {'\r', '\n'};

    private final OutputStream os;

    public ChunkedOutputStream(OutputStream os)
    {
        this.os = os;
    }
    
    @Override
    public void write(byte[]b)
        throws IOException
    {
        write(b, 0, b.length);
    }

    @Override
    public void write(int i) throws IOException
    {
        byte[] b = {(byte)i};
        write(b, 0, 1);
    }
    
    @Override
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        if (len > 0)
        {
            this.os.write(String.format("%x", len).getBytes("US-ASCII"));
            this.os.write(NEW_LINE);
            this.os.write(b, off, len);
            this.os.write(NEW_LINE);
        }
        else
        {
            // this should throw an exception if len < 0
            // or do nothing if len == 0
            this.os.write(b, off, len);
        }
    }

    @Override
    public void close() throws IOException
    {
        // last chunk is empty
        this.os.write('0');
        this.os.write(NEW_LINE);

        // no trailing headers
        this.os.write(NEW_LINE);

        flush();
        this.os.close(); 
    }

    @Override
    public void flush() throws IOException
    {
        this.os.flush();
    }
}

