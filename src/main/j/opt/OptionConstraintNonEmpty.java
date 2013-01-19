/**
 * @author Lucas Tan
 */

package j.opt;

import java.lang.annotation.*;

/**
 * Represents the constraint that a string option value 
 * be non-empty and non-null.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OptionConstraintNonEmpty 
{
    // nothing
}

