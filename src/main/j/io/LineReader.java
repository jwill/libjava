package j.io;

import java.io.*;

/**
 * A line reader that keeps track of the line number. It has the exact
 * same functionality as BufferedReader except that it has the additional
 * ability to keep track of the current line number.
 * In order for the class to accurately keep track of the line number,
 * callers must not use the read, skip, reset and mark methods.
 * @author Lucas Tan
 */
public class LineReader extends BufferedReader
{
    private int lineNum;

    public LineReader(Reader r)
    {
        super(r);
        this.lineNum = 0;
    }

    public LineReader(Reader r, int bufSize)
    {
        super(r, bufSize);
        this.lineNum = 0;
    }

    @Override
    public String readLine()
        throws IOException
    {
        String s = super.readLine();
        if (s != null) this.lineNum++;
        return s;
    }

    /** Gets the one-based line number of the line last returned by
     *  readLine()
     *  @return the line number if readLine() has been called; 0 otherwise.
     */
    public int getLineNum(){ return this.lineNum; }
}

