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
    private static final byte[] LAST_CHUNK = 
        // no trailing headers after 0\r\n
        {'0', '\r', '\n', '\r', '\n'};

    private boolean closed;

    public ChunkedOutputStream(OutputStream out)
    {
        super(out);
        this.closed = false;
        if (out == null)
            throw new IllegalArgumentException("out is null");
    }

    @Override
    public void write(int i) throws IOException
    {
        if (this.closed) throw new IOException("stream closed");

        final byte[] header = {'1', '\r', '\n', (byte)i, '\r', '\n'};
        this.out.write(header, 0, header.length);
    }
    
    private synchronized void writeChunk(
        byte[] header, byte[] b, int off, int len)
        throws IOException
    {
        this.out.write(header, 0, header.length);
        this.out.write(b, off, len);
        this.out.write(NEW_LINE);
    }

    @Override
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        if (this.closed) throw new IOException("stream closed");

        if (len > 0)
        {
            final byte[] header = 
                String.format("%x\r\n", len).getBytes("US-ASCII");
            writeChunk(header, b, off, len);
        }
        else
        {
            // this should throw an exception if len < 0
            // or do nothing if len == 0
            this.out.write(b, off, len);
        }
    }

    private synchronized void closeInternal() throws IOException
    {
        // We only write this once.
        if (!this.closed)
            this.out.write(LAST_CHUNK);

        this.closed = true;
    }

    @Override
    public void close() throws IOException
    {
        closeInternal();
        try
        {
            flush();
        }
        catch (IOException e){/*nothing*/}

        this.out.close(); 
    }

    @Override
    public void flush() throws IOException
    {
        this.out.flush();
    }
}

