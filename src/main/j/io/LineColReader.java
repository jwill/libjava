package j.io;

import java.io.*;

/**
 * A reader that keeps track of the line and column numbers.
 * It accepts the following line termination convention: \n, \r and \r\n.
 * However, it will standardize line endings to a single line feed, \n,
 * and will only return a \n to indicate the end of a line.
 * @author Lucas Tan
 */
public class LineColReader extends Reader
{
    private final PushbackReader in;
    
    /** One-based line number of last char read. */
    private int lineNum;

    /** One-based column number of last char read. */
    private int colNum;

    private int lineInc;
    private int colInc;

    /**
     * @exception IllegalArgumentException if in is null
     */
    public LineColReader(Reader in)
    {
        if (in == null) throw new IllegalArgumentException("in is null");

        this.in = new PushbackReader(in);
        this.lineNum = 0; 
        this.colNum = 0; 
        this.lineInc = 1;
        this.colInc = 1;
    }

    /**
     * Gets the next character without consuming it, blocking if necessary.
     * @return The next character if successful; -1 if end of stream has already
     *         been reached.
     */
    public int peek() throws IOException
    {
        int ch = this.in.read();
        if (ch != '\r')
        {
            if (ch >= 0) this.in.unread(ch);
            return ch;
        }

        this.in.unread('\n');
        
        int nextCh = this.in.read();
        if (nextCh >= 0 && nextCh != '\n')
        {
            this.in.unread(nextCh);
        }

        return '\n';
    }

    /**
     * Gets the one-based line number of the char last returned by any of
     * the read methods.
     * @return The line number if any of the read methods has been called;
     *         0 otherwise.
     */
    public int getLineNum() 
    {
        return this.lineNum;
    }

    /**
     * Gets the one-based column number of the char last returned by any of
     * the read methods.
     * @return The column number if any of the read methods has been called;
     *         0 otherwise.
     */
    public int getColumnNum() 
    {
        return this.colNum;
    }

    @Override
    public boolean ready() throws IOException
    {
        return this.in.ready();
    }

    @Override
    public void close() throws IOException
    {
        this.in.close();
    }

    /**
     * Reads an array of chars, blocking if necessary.
     * This method will still keep track of the line and column numbers.
     * @return The number of chars read. 0 may be returned. -1 is returned
     *         if end of stream has been reached.
     */
    @Override
    public int read(char chars[], int off, int len) throws IOException
    {
        if (off < 0) throw new IllegalArgumentException("off is negative");

        if (len > Integer.MAX_VALUE - off)
            len = Integer.MAX_VALUE - off;

        int i = off;
        for (; i < off + len; i++)
        {
            int ch = read();
            if (ch >= 0)
            {
                chars[i] = (char)ch;
                continue;
            }

            break;
        }

        return i-off;
    }

    /**
     * Reads the next character, blocking if necessary.
     * The line ending will be standardized to a single line feed character, \n.
     * @return The character read if successful; -1 if end of stream has already
     *         been reached.
     */
    @Override
    public int read() throws IOException
    {
        int ch = this.in.read();
        
        if (ch >= 0) 
        {
            this.lineNum += this.lineInc;
            this.colNum += this.colInc;
        }

        this.lineInc = 0;
        this.colInc = 1;

        if (ch != '\r' && ch != '\n')
            return ch;

        if (ch == '\r')
        {
            int nextCh = this.in.read();
            
            if (nextCh >= 0 && nextCh != '\n')
                this.in.unread(nextCh);
        }

        this.lineInc = 1;
        this.colInc = 1 - this.colNum;

        // standardize line ending to \n
        return '\n';
    }
}
