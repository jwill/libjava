/**
 * @author Lucas Tan
 */

package j.util.opt;

import java.lang.reflect.*;
import java.lang.annotation.*;

/**
 * An immutable class representing an option constraint check
 * failure.
 */
public class OptionConstraintException 
    extends OptionFieldException
{
    private final Annotation con;

    private static String getMessage(Annotation con)
    {
        if (con instanceof OptionConstraintRange)
        {
            OptionConstraintRange r = (OptionConstraintRange) con;
            return "must be between "+r.min()+ " and "+
                   r.max()+" inclusively";
        }
        else if (con instanceof OptionConstraintNonEmpty)
        {
            return "must be non-empty and non-null";
        }
        else
        {
            return "unknown constraint failure";
        }
    }

    /**
     * @exception IllegalArgumentException if con is null
     */
    public OptionConstraintException(Field field, Annotation con)
    {
        super(field, getMessage(con));

        if (con == null) throw new IllegalArgumentException("con is null");
        this.con = con;
    }

    public final Annotation getConstraint()
    {
        return this.con;
    }
}

