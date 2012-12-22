/**
 * @author Lucas Tan
 */ 

package j.io;

import java.io.*;

/**
 * An output stream that discards all data written to it.
 */
public class NullOutputStream extends OutputStream
{
    public NullOutputStream()
    {
        // nothing
    }
    
    @Override
    public void write(byte[]b)
        throws IOException
    {
        // nothing
    }

    @Override
    public void write(int i) throws IOException
    {
        // nothing
    }
    
    @Override
    public void write(byte[] b, int off, int len)
        throws IOException
    {
        // nothing
    }

    @Override
    public void close() throws IOException
    {
    
        // nothing
    }

    @Override
    public void flush() throws IOException
    {
        // nothing
    }
}

