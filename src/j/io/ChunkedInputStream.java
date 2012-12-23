/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an HTTP chunked input stream.
 * Currently, the available() method is not overriden and
 * will give an overly optimistic number of bytes available
 * since some of those bytes are metadata, not actual content.
 */
public class ChunkedInputStream extends FilterInputStream
{
    private static final int SKIP_BUF_SIZE = 2048;
    private static byte[] skipBuf;
    
    private volatile LineInputStream inner;

    /** size of current chunk */
    private int curSize;

    /** No. of bytes already read for current chunk */
    private int curRead;

    /**
     * @param inner Underlying input stream.
     * @exception IllegalArgumentException if inner is null
     */
    public ChunkedInputStream(LineInputStream inner)
    {
        super(inner);

        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        this.curSize = 0;
        this.curRead = 0;
        this.inner = inner;
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
        // if this is not the first chunk,
        // then read the terminating CRLF of the current chunk.
        if (this.curSize > 0)
            local.readLine();

        final String line = local.readLine();
        // line is of the form:
        // <chunk-size> [; chunk-extension]
        // where <chunk-size> is in hexadec without any prefix
        // and chunk-extension is optional

        int idx = line.indexOf(';');
        if (idx < 0) 
        {
            idx = line.indexOf(' ');
            if (idx < 0) idx = line.length();
        }

        // we ignore chunk extension for now

        final String sizeStr = line.substring(0, idx);
        final int size = Integer.parseInt(sizeStr, 16);
        
        if (size > 0)
        {
            this.curSize = size;
            this.curRead = 0;
            return;
        }

        if (size == 0)
        {
            this.curSize = -1;
            this.curRead = -1;

            // This last chunk contains OPTIONAL "Trailing HTTP headers"
            // with each on a separate line. 
            // The format of the last chunk is :
            // 0 CRLF
            // [trailing header] CRLF
            // [...] CRLF
            // CRLF
            
            // For now, we ignore such headers.

            // read until we hit a blank line
            while(!local.readLine().isEmpty());
            return;
        }

        throw new IOException("negative chunk size: "+size);
    }

    @Override
    public int read() throws IOException
    {
        byte[] b = {0};
        final int read = this.read(b, 0, 1);
        if (read >= 0) return b[0];
        return read;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len)
        throws IOException
    {
        final LineInputStream local = getIn(); 

        // finished reading the current chunk,
        // so let's move on to the next one.
        if (this.curRead == this.curSize)
        {
            //  are we already at the last chunk?
            if (this.curSize < 0) return -1;

            readChunk(local);

            // no more chunk?
            if (this.curSize <= 0) return  -1;
        }
    
        final int toRead = Math.min(len, this.curSize - this.curRead);
        final int read = local.read(b, off, toRead);
        if (read >= 0)
        {
            this.curRead += read;
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

