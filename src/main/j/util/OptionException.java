/**
 * @author Lucas Tan
 */

package j.util;

/**
 * An immutable class representing a generic option 
 * exception.
 */
public class OptionException extends Exception
{
    public OptionException(String msg)
    {
        this(msg,null);
    }

    public OptionException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

