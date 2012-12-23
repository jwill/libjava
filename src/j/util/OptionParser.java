/**
 * @author Lucas Tan
 */

package j.util;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class OptionParser
{
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
        throws Exception
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
                throw new Exception("invalid constraint type non-empty "
                 + "for field "+f.getName());

            if (cRange == null) return;
        
            int v = (int)(Integer)val;
            if (v < cRange.min() || v > cRange.max())
                throw new Exception("option -"+opt.name()+" must"
                +" be between "+cRange.min()+" and "+cRange.max()
                +" inclusively");
        }
        else if (cls.equals(String.class))
        {
            if (cRange != null)
                throw new Exception("invalid constraint type range "
                 + "for field "+f.getName());

            if (cNonEmpty == null) return;

            String v = (String)val;
            if (v == null || v.isEmpty())
            {
                throw new Exception("option -"+opt.name()+" must"
                +" be non-empty");
            }
        }
    }

    /**
     * Parses options and fills in the relevant fields in
     * the specified object.
     * @return Returns null on error.
     *         Otherwise, returns an array of extra unparsed args 
     *         or an array if there is none.
     */
    public static String[] parse(Object obj, String[] args)
    {
        try
        {
            return parseInternal(obj, args);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            try{printUsage(obj);}
            catch(Exception e2){ /*nothing*/ }
            System.err.flush();
            return null;
        }
    }

    private static Object parseValue(Field f, String val)
        throws Exception
    {
        Class cls = f.getType();
        
        if (cls.equals(Boolean.class) || 
            cls.equals(Boolean.TYPE))
            return true;
        
        if (cls.equals(Integer.class)||
            cls.equals(Integer.TYPE))
            return Integer.parseInt(val.trim());
        
        if (cls.equals(String.class) )
            return val.trim();

        throw new Exception("unexpected field type "+
            cls.getName());
    }

    private static String[] parseInternal
        (Object obj, String args[]) throws Exception
    {
        // maps from switch name to field
        Map<String, Field> mapping = new
            HashMap<String, Field>();

        // Perform sanity checking for option fields
        // and to populate mapping
        Class cls = obj.getClass();
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
                throw new Exception("unsupported type for "
                    +"field "+f.getName());
            }

            final String name = opt.name() == null ? "" : opt.name();
            if (name.isEmpty())
            {
                throw new Exception
                    ("empty or null option name for field "+
                     f.getName());
            }

            if (opt.description() == null ||
                opt.description().trim().isEmpty())
            {
                throw new Exception("empty or null description for field "
                 + f.getName());
            }

            final boolean exists = mapping.containsKey(name);
            if (exists)
            {
                throw new Exception("duplicate option name '"
                    + name + "' for field "+f.getName());
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
                    throw new Exception("empty switch");

                Field f = mapping.get(name);
                if (f == null)
                {
                    throw new Exception("unknown option: -"+
                        name);
                }

                if (useNext(f)) 
                {
                    if (next == null)
                        throw new Exception("value not "
                            +"specified for option -"+name);
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
                throw new Exception("option -"+opt.name()+
                    " required");
        }

        return extras.toArray(new String[]{});
    }
}


