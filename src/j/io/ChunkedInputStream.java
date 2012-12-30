/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;
import java.util.*;

/**
 * Represents an HTTP chunked input stream.
 * This class is not thread-safe.
 */
public class ChunkedInputStream extends FilterInputStream
{
    private static final int SKIP_BUF_SIZE = 2048;
    private static byte[] skipBuf;

    // Some values for curLeft
    private static final int CHUNK_NONE = -2;
    private static final int CHUNK_FIRST = -1;

    private volatile LineInputStream inner;

    /** No. of unread bytes left in current chunk */
    private int curLeft;

    /** Trailing headers after the last chunk */
    private List<String> trailingHeaders;

    /**
     * @param inner Underlying input stream.
     * @exception IllegalArgumentException if inner is null
     */
    public ChunkedInputStream(LineInputStream inner)
    {
        super(inner);

        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        this.curLeft = CHUNK_FIRST;
        this.inner = inner;
        this.trailingHeaders = null;
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
     * does nothing since not supported
     */
    @Override
    public void mark(int k) 
    {
        // nothing
    }

    @Override
    public int available() throws IOException
    {
        final InputStream local = getIn();

        return Math.max(0, Math.min(this.curLeft, local.available()));
    }

    /**
     * @exception IOException always thrown since not supported.
     */
    @Override
    public void reset() throws IOException
    {
        throw new IOException("not supported");
    }
    
    private LineInputStream getIn() throws IOException
    {
        final LineInputStream localIn = this.inner;
        if (localIn == null)
            throw new IOException("stream closed");

        return localIn;
    }
    
    private void readChunk(LineInputStream local) throws IOException
    {
        final int localCurLeft = this.curLeft;
        switch (localCurLeft)
        {
        case CHUNK_NONE:
            // If no more chunk, then return
            return;

        default:
            // if this is not the first chunk,
            // then read the terminating CRLF of the current chunk.
            local.readLine();

            // fall thru

        case CHUNK_FIRST: {
            final String line = local.readLine();
            final int lineLen = line.length();

            // line is of the form:
            // <chunk-size> [; chunk-extension]
            // where <chunk-size> is in hexadec without any prefix
            // and chunk-extension is optional

            // we ignore chunk extension for now
            
            // Parse the chunk size
            long size = 0;
            int idx = 0;
            for (; idx < lineLen; idx++)
            {
                final int n = Character.digit(line.charAt(idx), 16);
                if (n >= 0)
                {
                    size = (size << 4) + n;
                    if (size <= Integer.MAX_VALUE)
                        continue;
                    
                    throw new IOException("chunk size too large");
                }

                // stop when we hit a non-hexadec digit
                break;
            }
        
            // there must be at least one valid hexadec digit
            if (idx == 0) throw new IOException("malformed chunk size");
            
            if (size > 0)
            {
                this.curLeft = (int)size;
                return;
            }

            if (size == 0)
            {
                this.curLeft = CHUNK_NONE;

                // This last chunk contains OPTIONAL "Trailing HTTP headers"
                // with each on a separate line. 
                // The format of the last chunk is :
                // 0 CRLF
                // [trailing header] CRLF
                // [...] CRLF
                // CRLF
            
                this.trailingHeaders = new ArrayList<String>();

                // read until we hit a blank line
                String tmp = null;
                while(!(tmp = local.readLine()).isEmpty())
                {
                    this.trailingHeaders.add(tmp);
                }
                this.trailingHeaders = 
                    Collections.unmodifiableList(this.trailingHeaders);
                return;
            }

            throw new IOException("negative chunk size: "+size);

        } } // switch
    }

    /**
     * Gets the trailing headers that appear after the last chunk.
     * @return Before the last chunk is read, returns null.
     *         After the last chunk is read, i.e., read() returns -1, returns 
     *         an unmodifiable list of headers which can be empty if there
     *         is none.
     */
    public List<String> getTrailingHeaders()
    {
        return this.trailingHeaders;
    }

    @Override
    public int read() throws IOException
    {
        byte[] b = {0};
        final int read = this.read(b, 0, 1);
        if (read >= 0) return ((int)b[0]) & 255;
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        final LineInputStream local = getIn(); 

        // finished reading the current chunk,
        // so let's move on to the next one.
        if (this.curLeft <= 0)
        {
            readChunk(local);

            // no more chunk?
            if (this.curLeft <= 0) return  -1;
        }
    
        final int toRead = Math.min(len, this.curLeft);
        final int read = local.read(b, off, toRead);
        if (read >= 0)
        {
            this.curLeft -= read;
        }

        return read;
    }

    @Override
    public long skip(long n)
        throws IOException
    {
        if (n <= 0) return 0;
        
        long left = n;
        
        // note: there might be contention here...
        // multiple buffers might be created but
        // it should be harmless, just that some memory will
        // be allocated for nothing.
        if (this.skipBuf == null)
            this.skipBuf = new byte[SKIP_BUF_SIZE];

        final byte[] localBuf = this.skipBuf;
        while(left > 0)
        {
            final int nread = read(localBuf, 0, 
                (int) Math.min(left, localBuf.length));
            if (nread < 0) break;

            left -= nread;
        }

        return n - left;
    }

    @Override
    public void close() throws IOException
    {
        // Don't use getIn() since we allow closing
        // of a closed stream.
        final LineInputStream local = this.inner;

        if (local == null) return;

        local.close();
        this.inner = null;
    }
}

