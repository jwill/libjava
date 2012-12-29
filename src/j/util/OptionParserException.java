/**
 * @author Lucas Tan
 */

package j.util;

import java.lang.reflect.*;

/**
 * An immutable class representing the exception that 
 * may occur when parsing an option.
 */
public class OptionParserException 
    extends OptionException
{
    /** can be null */
    private final Field field;

    /** can be null */
    private final Option opt;

    public OptionParserException(String msg)
    {
        this(null, msg, null);
    }

    public OptionParserException(Field field, String msg)
    {
        this(field, msg, null);
    }

    public OptionParserException(Field field, String msg, Throwable cause)
    {
        super(msg, cause);

        this.field = field;
        this.opt = (field != null ? field.getAnnotation(Option.class)
                    : null);
    }

    public final Field getField()
    {
        return this.field;
    }

    public final Option getOption()
    {
        return this.opt;
    }
}

