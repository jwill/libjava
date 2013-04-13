package j.io;

import java.io.*;
import java.util.*;

public final class FileUtil
{
    private FileUtil(){}

    public static final String DEFAULT_CHARSET = "utf-8";
    
    public static final String LINE_SEP = System.getProperty("line.separator");

    /**
     * Reads an entire UTF-8 text file into memory as a {@link String}.
     * This can handle various line ending conventions, but line endings 
     * will be converted into the underlying platform's default.
     */
    public static String readAll(File file)
        throws IOException
    {
        return readAll(file, DEFAULT_CHARSET);
    }

    /**
     * Reads an entire UTF-8 text file into memory as a {@link String}.
     * This can handle various line ending conventions, but line endings 
     * will be converted into the underlying platform's default.
     */
    public static String readAll(String filePath)
        throws IOException
    {
        return readAll(new File(filePath), DEFAULT_CHARSET);
    }

    /**
     * Reads an entire text file into memory as a {@link String}.
     * This can handle various line ending conventions, but line endings 
     * will be converted into the underlying platform's default.
     */
    public static String readAll(String filePath, String charset)
        throws IOException
    {
        return readAll(new File(filePath), charset);
    }

    /**
     * Reads an entire text file into memory as a {@link String}.
     * This can handle various line ending conventions, but line endings 
     * will be converted into the underlying platform's default.
     */
    public static String readAll(File file, String charset)
        throws IOException
    {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
    
        try
        {
            String line;
            String lineSep = "";

            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), charset));

            while ((line = br.readLine()) != null)
            {
                sb.append(lineSep);
                sb.append(line);
                lineSep = LINE_SEP;
            }

            return sb.toString();
        }
        finally
        {
            if (br != null) br.close();
        }
    }
}