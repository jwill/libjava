/**
 * @author Lucas Tan
 */

package j.util.opt;

import java.lang.annotation.*;

/**
 * Represents the constraint that an integral option value 
 * be within a range.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OptionConstraintRange 
{
    /** Minimum value, inclusive. */
    int min() default Integer.MIN_VALUE;

    /** Maximum value, inclusive. */
    int max() default Integer.MAX_VALUE;
}

