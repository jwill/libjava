/**
 * @author Lucas Tan
 */

package j.util;

import java.lang.annotation.*;

/**
 * Represents the constraint that the option value be non-empty.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OptionConstraintNonEmpty
{
    // nothing
}

