package j.util;

import java.awt.datatransfer.*;
import java.awt.Toolkit;
import java.awt.HeadlessException;
import java.io.*;

/**
 * Provides methods to modify and retrieve data from the system clipboard.
 * @author Lucas Tan
 *
 */
public final class SystemClipboard
{
    private static class DefaultOwner implements ClipboardOwner
    {
        /**
         * Empty implementation of the ClipboardOwner interface.
         */
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) 
        {
            // the default instance does nothing.
        }
    }
    
    /**
     * The default instance to be used as the owner when users do not
     * supply their own owners. 
     */
    private static final ClipboardOwner DEFAULT_OWNER = new DefaultOwner();
    
    private static final Toolkit DEFAULT_TOOLKIT = 
        Toolkit.getDefaultToolkit();

    /**
     * Private constructor so that no one can create an instance of this class.
     */
    private SystemClipboard()
    {
        // nothing
    }
   
    /**
     * Puts a string on the system clipboard, and changes the clipboard owner.
     * @param string String to put on the clipboard.
     * @param owner The new owner.
     * @exception IllegalStateException if clipboard is currently unavailable
     * @exception HeadlessException 
     */
    public static void setString(String string, ClipboardOwner owner)
    {
        final StringSelection stringSelection = new StringSelection(string);

        // We should not put the system clipboard in a static final var
        // as getSystemClipboard() can throw a HeadlessException.
        // So we should get the sys clipboard only when we need to.
        final Clipboard clip = DEFAULT_TOOLKIT.getSystemClipboard();
        clip.setContents(stringSelection, owner);
    }
    
    /**
     * Puts a string on the system clipboard, and uses a default owner
     * for the clipboard.
     * @param string String to put on the clipboard.
     * @exception IllegalStateException if clipboard is currently unavailable
     * @exception HeadlessException 
     */
    public static void setString(String string)
    {
        setString(string, DEFAULT_OWNER);
    }
    
    /**
     * Gets any text found on the system clipboard.
     *
     * @return If none found, returns an empty string. 
     *         If there is an error, returns null. 
     */
    public static String getString() 
    {
        try
        {
            final Clipboard clipboard = DEFAULT_TOOLKIT.getSystemClipboard();
            
            // the param of getContents is not currently used
            final Transferable contents = clipboard.getContents(null);
        
            final boolean isTransferable =
                (contents != null) &&
                contents.isDataFlavorSupported(DataFlavor.stringFlavor);
      
            if (isTransferable)
            {
                return (String)
                    contents.getTransferData(DataFlavor.stringFlavor);
            }

            return "";
        }
        // Could be IOException or UnsupportedFlavorException
        catch (Exception e)
        {
            return null;
        }
    }
}


