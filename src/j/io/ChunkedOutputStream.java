/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an HTTP chunked output stream.
 * There is a built-in internal buffering mechanism.
 * This class is thread-safe.
 */
public class ChunkedOutputStream extends FilterOutputStream
{
    private static final int DEFAULT_BUF_SIZE = 8192;

    private static final byte[] NEW_LINE = {'\r', '\n'};
    private static final byte[] LAST_CHUNK = 
        // no trailing headers after 0\r\n
        {'0', '\r', '\n', '\r', '\n'};

    private static final String HEX = "0123456789ABCDEF";

    /** Buffer */
    private final byte[] buf;

    /** No. of valid bytes in buffer */
    private int count;

    /** Whether the stream is closed. */
    private boolean closed;

    public ChunkedOutputStream(OutputStream out)
    {
        this(out, DEFAULT_BUF_SIZE);
    }

    /**
     * @exception IllegalArgumentException if out is null or
     *            size is non-positive.
     */
    public ChunkedOutputStream(OutputStream out, int size)
    {
        super(out);
        
        if (out == null)
            throw new IllegalArgumentException("out is null");

        if (size <= 0)
            throw new IllegalArgumentException("size must > 0");

        this.buf = new byte[size];
        this.count = 0;
        this.closed = false;
    }

    private void checkClose() throws IOException
    {
        if (this.closed) throw new IOException("stream closed");
    }

    private void writeChunk(byte[] header, int headerCount,
        byte[] b, int off, int len)
        throws IOException
    {
        this.out.write(header, 0, headerCount);
        this.out.write(b, off, len);
        this.out.write(NEW_LINE);
    }

    private static int fillHeader(int count, byte[] b)
    {
        int tmp = count;
        int ndigit = 0;

        do
        {
            ndigit++;
            tmp >>>= 4;
        }
        while(tmp != 0);

        int idx = ndigit-1;
        do
        {
            b[idx] = (byte)HEX.charAt(count & 0xf);
            idx--;
            count >>>= 4;
        }
        while(count != 0);

        b[ndigit] = '\r';
        b[ndigit+1] = '\n';
        return ndigit+2;
    }

    /**
     * Writes the buffer as a chunk.
     */
    private void flushBuffer() throws IOException
    {
        // Only flush if count > 0, otherwise
        // flushBuffer will output the last chunk format
        // which indicates the end of the stream.
        if (this.count > 0)
        {
            byte [] header = {0,1,2,3,4,5,6,7,8,9,10};
            int headerCount = fillHeader(this.count, header);
            writeChunk(header, headerCount, this.buf, 0, this.count);
            this.count = 0;
        }
    }

    @Override
    public void write(int i) throws IOException
    {
        synchronized(this)
        {
            checkClose();
            
            if (this.count >= this.buf.length)
                flushBuffer();

            this.buf[this.count ++] = (byte)i;
        }
    }

    @Override
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        if (len >= this.buf.length)
        {
            byte [] header = {0,1,2,3,4,5,6,7,8,9,10};
            int headerCount = fillHeader(len, header);

            synchronized(this)
            {
                checkClose();
                
                // Writes directly to underlying stream
                // if len is larger than buffer size.
                flushBuffer();
                writeChunk(header, headerCount, b, off, len);
            }
            return;
        }

        synchronized(this)
        {
            checkClose();

            // flush buffer if necessary to make space for incoming
            if (len > this.buf.length - this.count)
                flushBuffer();

            System.arraycopy(b, off, this.buf, this.count, len);
            this.count += len;
        }
    }

    @Override
    public void close() throws IOException
    {
        // close() can be called multiple times.
        // but only the first time would be effective.

        synchronized(this)
        {
            if (this.closed) return;
            
            this.closed = true;
        }
      
        flushBuffer();

        // We must only write this exactly once.
        this.out.write(LAST_CHUNK, 0, LAST_CHUNK.length);

        try
        {
            // must flush so the last chunk can be written properly.
            // This is necessary since this.out.close() is not required
            // to do a flush() first.
            this.out.flush();
        }
        catch (IOException e){/*nothing*/}

        this.out.close(); 
    }

    @Override
    public void flush() throws IOException
    {
        synchronized(this)
        {
            checkClose();
            flushBuffer();
        }

        this.out.flush();
    }
}

