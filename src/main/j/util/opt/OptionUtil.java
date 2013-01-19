package j.util.opt;

import java.util.*;

/**
 * A utility class containing methods for dealing with options.
 * @author Lucas Tan
 */
public final class OptionUtil
{
    // Cannot instantiate
    private OptionUtil(){}

    /**
     * Parses a string into a boolean value.
     * A string value of "0", "no", "false" and "off" will be interpreted
     * as false. A value of "1", "yes", "true" and "on" will be
     * interpreted as true.
     * An IllegalArgumentException will be thrown for any other value.
     * @exception NullPointerException if val is null
     * @exception IllegalArgumentException 
     *            if val has an unrecognizable value.
     */
    public static boolean parseBoolean(String val)
    {
        String s = val.trim().toLowerCase(Locale.ROOT);
        
        if (s.equals("0") || 
            s.equals("no") || 
            s.equals("false") ||
            s.equals("off")
        )
            return false;

        if (s.equals("1") || 
            s.equals("yes") || 
            s.equals("true") ||
            s.equals("on")
        )
            return true;

        throw new IllegalArgumentException("invalid boolean option: "+val);
    }
}

