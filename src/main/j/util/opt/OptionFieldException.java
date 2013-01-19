/**
 * @author Lucas Tan
 */

package j.util.opt;

import java.lang.reflect.*;

/**
 * An immutable class representing an exception
 * associated with a field.
 */
public class OptionFieldException extends OptionException
{
    private final Field field;
    private final Option opt;

    public OptionFieldException(Field field, String msg)
    {
        super(msg);

        if (field == null) 
            throw new IllegalArgumentException("field is null");

        this.opt = field.getAnnotation(Option.class);
        this.field = field;
        
        // this should not occur
        if (this.opt == null) 
            throw new RuntimeException(
                "field "+field.getName()+" has no option!");
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

