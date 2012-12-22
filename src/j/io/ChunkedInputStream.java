/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * Represents an HTTP chunked input stream.
 */
public class ChunkedInputStream extends InputStream
{
    private final LineInputStream inner;

    /** size of current chunk */
    private int curSize;

    /** No. of bytes already read for current chunk */
    private int curRead;

    public ChunkedInputStream(LineInputStream inner)
    {
        if (inner == null)
            throw new IllegalArgumentException("inner is null");

        this.curSize = 0;
        this.curRead = 0;
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

    private void readChunk() throws IOException
    {
        // if this is not the first chunk,
        // then read the terminating CRLF of the current chunk.
        if (this.curSize > 0)
            this.inner.readLine();

        String line = this.inner.readLine();
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

        String sizeStr = line.substring(0, idx);
        int size = Integer.parseInt(sizeStr, 16);
        
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
            while(!this.inner.readLine().isEmpty());
            return;
        }

        throw new IOException("negative chunk size: "+size);
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
        // finished reading the current chunk,
        // so let's move on to the next one.
        if (this.curRead == this.curSize)
        {
            //  are we already at the last chunk?
            if (this.curSize < 0) return -1;

            readChunk();

            // no more chunk?
            if (this.curSize <= 0) return  -1;
        }
    
        final int toRead = Math.min(len, this.curSize - this.curRead);
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

