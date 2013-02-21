package j.io;

import java.io.*;

/**
 * A utility class for manipulating paths.
 * An abstract path in this class refers to a path that might not exist
 * in the filesystem, and if it exists, it might not be canonicalized
 * according to the underlying filesystem naming conventions.
 * @author Lucas Tan
 */
public final class PathUtil
{
    // cannot instantiate
    private PathUtil(){}

    /**
     * Combine two paths together in an abstract manner.
     * This method accepts the forward or back slash as the
     * directory separator when dealing with the arguments.
     * However, if the parent path does not end with a directory separator,
     * this method will use the underlying system's
     * directory separator char to perform the combination. 
     * The returned combined path will not
     * be canonicalized, that is, soft/hard/junction links and the 
     * components .. and . will not be resolved.
     * @param parent Abstract path to the parent directory. Can end with
     *        a directory separator.
     * @param child Abstract name of a child directory or file.
     * @exception NullPointerException if parent or child is null.
     */
    public static String combine(String parent, String child)
    {
        if (child != null)
        {
            if ( parent.endsWith("/")
              || parent.endsWith("\\"))
                return parent + child;
        
            return parent + File.separatorChar + child;
        }

        throw new NullPointerException("child is null");
    }

    /**
     * This is the extended version of the two-argument method.
     * If only one argument is specified, the combined path is the argument
     * itself.
     * @exception IllegalArgumentException if no argument is specified.
     * @exception NullPointerException if at least one argument is null.
     */
    public static String combine(String... s)
    {
        if (s != null && s.length > 0)
        {
            StringBuilder sb = new StringBuilder(s[0]);
            String last = s[0];

            for (int i = 1; i < s.length; i++)
            {
                if (!last.endsWith("/") && !last.endsWith("\\"))
                    sb.append(File.separatorChar);

                sb.append(last = s[i]);
            }

            return sb.toString();
        }

        throw new IllegalArgumentException("specify one or more arguments");
    }
}

