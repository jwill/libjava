package j.util.opt;

import java.lang.annotation.*;
import java.lang.reflect.*;
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
    
    private static void printUsage(Object obj)
        throws Exception
    {
        System.err.println("Options :");
        
        Class cls = obj.getClass();
        Field[] fields = cls.getFields();
        for (Field f : fields)
        {
            Option opt = f.getAnnotation(Option.class);
        
            if (opt == null) continue;

            System.err.print(" -"+opt.name()+" : "+
                opt.description());

            if (opt.required())
            {
                System.err.print(" [Required]");
            }
            else
            {
                Object value = f.get(obj);
                if (value == null)
                    System.err.print(" [Default=]");
                else
                    System.err.print(" [Default="+value+"]");
            }

            System.err.println();
        }

    }

    private static boolean checkSupported(Field f)
    {
        Class cls = f.getType();
        return (cls.equals(Integer.class) ||
                cls.equals(Integer.TYPE) ||
                cls.equals(Boolean.class) ||
                cls.equals(Boolean.TYPE) ||
                cls.equals(String.class) );
    }

    private static boolean useNext(Field f)
    {
        if (f.getType().equals(Boolean.class) ||
            f.getType().equals(Boolean.TYPE))
            return false;
        return true;
    }

    private static void checkConstraint(Field f, Object val)
        throws OptionConstraintException, OptionFieldException
    {
        OptionConstraintRange cRange = 
            f.getAnnotation(OptionConstraintRange.class);
        OptionConstraintNonEmpty cNonEmpty = 
            f.getAnnotation(OptionConstraintNonEmpty.class);
        Option opt = 
            f.getAnnotation(Option.class);
        Class cls = f.getType();

        if (cls.equals(Integer.class) ||
            cls.equals(Integer.TYPE))
        {
            if (cNonEmpty != null)
                throw new OptionFieldException(f,
                    "constraint type non-empty not allowed");

            if (cRange == null) return;
        
            int v = (int)(Integer)val;
            if (v < cRange.min() || v > cRange.max())
                throw new OptionConstraintException(f, cRange);
        }
        else if (cls.equals(String.class))
        {
            if (cRange != null)
                throw new OptionFieldException(f, 
                    "constraint type range not allowed");

            if (cNonEmpty == null) return;

            String v = (String)val;
            if (v == null || v.isEmpty())
            {
                throw new OptionConstraintException(f,cNonEmpty);
            }
        }
    }

    /**
     * Map options from command line arguments to the respective
     * fields of a specified object.
     *
     * Parsing errors will be printed to stderr.
     * The usage message will also be printed when an error occurs.
     *
     * @param obj Object whose class must be public. The fields
     *        to be filled in must also be public. The default
     *        value for an option is specified in the respective
     *        field declaration.
     *        
     * @param args Command line arguments as received by main()
     *
     * @return Returns null on encountering an internal error or 
     *         parsing error.
     *         Otherwise, returns an array of unparsed args 
     *         which can be empty if there is none.
     *
     * @exception OptionException if an unexpected error/exception
     *            occurred. The inner exception will be the actual 
     *            exception that caused the error.
     */
    public static String[] parse(Object obj, String[] args)
        throws OptionException
    {
        boolean wantUsage = false;
        Throwable unexpected = null;

        try
        {
            return parseInternal(obj, args);
        }
        catch (OptionParserException e)
        {
            if (e.getOption() != null)    
                System.err.println("-"+e.getOption().name()+": "+
                    e.getMessage());
            else
                System.err.println(e.getMessage());

            wantUsage = true;
        }
        catch (OptionConstraintException e)
        {
            System.err.println("-"+e.getOption().name()+": "+
                    e.getMessage());
            wantUsage = true;
        }
        catch (OptionFieldException e)
        {
            // internal exception, no need to print usage
            System.err.println("field "+e.getField().getName()+": "+
                e.getMessage());
        }
        catch (OptionException e)
        {
            // internal exception, no need to print usage
            System.err.println(e.getMessage());
        }
        catch (Throwable t)
        {
            unexpected = t;
        }

        if (wantUsage)
        {
            try
            {
                printUsage(obj);
            }
            catch (Exception e){unexpected = e;}
        }

        if (unexpected != null)
            throw new OptionException("Unexpected error/exception", 
                unexpected);

        return null;
    }

    private static Object parseValue(Field f, String val)
        throws OptionParserException, OptionFieldException
    {
        Class cls = f.getType();
        
        if (cls.equals(Boolean.class) || 
            cls.equals(Boolean.TYPE))
        {
            // ignore val since presence indicates truth
            return true;
        }

        final String trimmedVal = val.trim();
        
        if (cls.equals(Integer.class)||
            cls.equals(Integer.TYPE))
        {
            try
            {
                // decode recognizes hexa and octal prefix
                return Integer.decode(trimmedVal);
            }
            catch (NumberFormatException e)
            {
                throw new OptionParserException(f, 
                    "Unrecognizable number: "+trimmedVal, e);
            }
        }

        if (cls.equals(String.class))
            return trimmedVal;

        throw new OptionFieldException(f, 
            "unexpected field type "+
            cls.getName());
    }

    private static String[] parseInternal
        (Object obj, String args[]) 
        throws OptionConstraintException, 
            OptionParserException, 
            OptionException,
            OptionFieldException,
            Exception
    {
        // maps from switch name to field
        Map<String, Field> mapping = new
            HashMap<String, Field>();

        // Perform sanity checking for option fields
        // and to populate mapping
        Class cls = obj.getClass();

        // We require the class to be public so that we can get
        // and set the fields
        final int mods = cls.getModifiers();
        if (! Modifier.isPublic(mods))
            throw new OptionException("class "+cls.getName()+" must be public");

        // get public fields
        Field[] fields = cls.getFields();
        for (Field f : fields)
        {
            Option opt = f.getAnnotation(Option.class);
            if (opt == null)
            {
                continue;
            }
            
            if (!checkSupported(f))
            {
                throw new OptionFieldException(f,"unsupported type");
            }

            final String name = opt.name() == null ? "" : opt.name();
            if (name.isEmpty())
            {
                throw new OptionFieldException
                    (f, "empty or null option name");
            }

            if (opt.description() == null ||
                opt.description().trim().isEmpty())
            {
                throw new OptionFieldException(f,"empty or null description");
            }

            final boolean exists = mapping.containsKey(name);
            if (exists)
            {
                throw new OptionFieldException(f,"duplicate option name '"
                    + name+"'");
            }
            
            mapping.put(name, f);
        }

        List<String> extras = new ArrayList<String>();
        Set<String> specified = new HashSet<String>();

        for (int i = 0; i < args.length; i++)
        {
            String cur = args[i];
            String next = (i != args.length - 1 ? 
                            args[1+i] : null);

            if (cur.startsWith("-"))
            {
                final String name = cur.substring(1);
                if (name.isEmpty())
                    throw new OptionParserException("empty switch");

                Field f = mapping.get(name);
                if (f == null)
                {
                    throw new OptionParserException("unknown option: -"+
                        name);
                }

                if (useNext(f)) 
                {
                    if (next == null)
                        throw new OptionParserException(f,"value not "
                            +"specified");
                    i++;
                }

                Object val = parseValue(f, next);
                checkConstraint(f, val);
                f.set(obj, val);
                specified.add(name);
            }
            else
            {
                extras.add(cur);
            }
        }

        for (Field f:fields)
        {
            Option opt = f.getAnnotation(Option.class);
            if (opt == null) continue;

            if (opt.required() && 
                !specified.contains(opt.name()))
                throw new OptionParserException(f,"required");
        }

        return extras.toArray(new String[extras.size()]);
    }
}


