/**
 * @author Lucas Tan
 */

package j.util;

import java.lang.annotation.*;

/**
 * Represents the constraint that an option value be limited
 * to a range.
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

