package j.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

public class WebUtilTest
{
    @Test
    public void htmlEncodeTest() throws IOException
    {
        assertEquals(WebUtil.htmlEncode(""), "");
        assertEquals(WebUtil.htmlEncode("<"), "&lt;");
        assertEquals(WebUtil.htmlEncode(">"), "&gt;");
        assertEquals(WebUtil.htmlEncode("&"), "&amp;");
        assertEquals(WebUtil.htmlEncode("\""), "&quot;");

        StringWriter w = new StringWriter();
        WebUtil.htmlEncode("", w); assertEquals(w.toString(), "");

        w = new StringWriter();
        WebUtil.htmlEncode("<", w); assertEquals(w.toString(), "&lt;");

        w = new StringWriter();
        WebUtil.htmlEncode(">", w); assertEquals(w.toString(), "&gt;");
        
        w = new StringWriter();
        WebUtil.htmlEncode("&", w); assertEquals(w.toString(), "&amp;");
        
        w = new StringWriter();
        WebUtil.htmlEncode("\"", w); assertEquals(w.toString(), "&quot;");
        

        // Randomly generate a string for encoding
        StringBuilder sb = new StringBuilder();
        StringBuilder encoded = new StringBuilder();
        Random r = new Random();
        String chars = "0123456789abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < 1000; i++)
        {
            int k = r.nextInt(5);
            char c = chars.charAt(r.nextInt(chars.length()));
            switch(k)
            {
            case 0: encoded.append("&lt;"); sb.append("<"); break;
            case 1: encoded.append("&gt;"); sb.append(">"); break;
            case 2: encoded.append("&amp;"); sb.append("&"); break;
            case 3: encoded.append("&quot;"); sb.append("\""); break;
            default: encoded.append(c); sb.append(c); break;
            }
        }

        assertEquals(encoded.toString(), 
            WebUtil.htmlEncode(sb.toString()));
    }

    @Test(expected = NullPointerException.class)
    public void htmlEncodeNullTest1() throws IOException
    {
        WebUtil.htmlEncode(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void htmlEncodeNullTest2() throws IOException
    {
        WebUtil.htmlEncode(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void htmlEncodeNullTest3() throws IOException
    {
        WebUtil.htmlEncode("", null);
    }
    
    @Test(expected = NullPointerException.class)
    public void htmlEncodeNullTest4() throws IOException
    {
        WebUtil.htmlEncode(null, new StringWriter());
    }
}

