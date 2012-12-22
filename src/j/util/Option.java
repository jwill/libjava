/**
 * @author Lucas Tan
 */

package j.util;

import java.lang.annotation.*;

/**
 * Represents an option field
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Option
{
    /** Name of the switch. For e.g., if the switch is "-t",
     * then the name would be "t". This can have more than
     * one character. */
    String name();

    /** Description of this option. 
     * For e.g., "number of threads" */
    String description();

    /** Whether this option must be specified. */
    boolean required();
}

