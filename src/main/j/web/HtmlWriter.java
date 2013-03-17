package j.web;

import java.io.*;

public class HtmlWriter
{
    private static final String indentStr;
    private static final int INDENT_STR_LEN;

    static
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++)
        {
            sb.append("        "); // 8 spaces
        }

        indentStr = sb.toString();
        INDENT_STR_LEN = indentStr.length();
    }

    private final Writer w;
    
    private int indent;
    private String newLine;
    private int indentIncre;

    /**
     * Initializes an instance with the default indentation increment of 
     * 2 spaces, and the platform's line termination convention.
     */
    public HtmlWriter(Writer w)
    {
        this(w, 2, System.getProperty("line.separator"));
    }

    /**
     * Initializes an instance with the platform's line termination convention.
     */
    public HtmlWriter(Writer w, int indentIncre)
    {
        this(w, indentIncre, System.getProperty("line.separator"));
    }

    /**
     * Initializes an instance.
     * @param indentIncre Indentation increment in terms of spaces. Can be zero.
     * @param newLine Line termination sequence. Can be empty.
     * @exception IllegalArgumentException if w or newLine is null, indentIncre
     *            is negative.
     */
    public HtmlWriter(Writer w, int indentIncre, String newLine)
    {
        if (w == null) 
            throw new IllegalArgumentException("w is null");
        
        if (indentIncre < 0) 
            throw new IllegalArgumentException("indentIncre is negative");

        if (newLine == null)
            throw new IllegalArgumentException("newLine is null");

        this.w = w;
        this.indentIncre = indentIncre;
        this.newLine = newLine;
    }

    private void writeIndent()
        throws IOException
    {
        int left = this.indent;

        while (left >= INDENT_STR_LEN)
        {
            this.w.append(indentStr, 0, INDENT_STR_LEN);
            left -= INDENT_STR_LEN;
        }
        
        this.w.append(indentStr, 0, left);
    }

    /**
     * Writes a self-closing tag.
     * @param rawTag Must be of the form <tagName attr="value" /> or
     *        <tagName attr="value" ....></tagName>
     */
    public void tagRaw(String rawTag)
        throws IOException
    {
        writeIndent();
        this.w.append(rawTag.trim()).append(this.newLine);
    }

    /**
     * Writes a self-closing tag.
     * @param name Name of the tag.
     * @param attrs Attribute names and values in alternating order.
     */
    public void tag(String name, Object... attrs)
        throws IOException
    {
        writeIndent();
        this.w.append("<").append(name);
    
        for (int i = 0; i < attrs.length; i += 2)
        {
            this.w.write(' ');
            this.w.append(attrs[i].toString());
            this.w.append("=\"");
            WebUtil.htmlEncode(attrs[i+1].toString(), this.w);
            this.w.write('"');
        }

        this.w.append(" />").append(this.newLine);
    }

    /**
     * Writes an open tag.
     *
     */
    public void openTag(String name, Object... attrs)
        throws IOException
    {
        writeIndent();
        this.w.append("<").append(name);
    
        for (int i = 0; i < attrs.length; i += 2)
        {
            this.w.write(' ');
            this.w.append(attrs[i].toString());
            this.w.append("=\"");
            WebUtil.htmlEncode(attrs[i+1].toString(), this.w);
            this.w.write('"');
        }

        this.w.append(">").append(this.newLine);
        this.indent += this.indentIncre;
    }

    /**
     * @param rawTag Must be of the form <tagName attr="value" ...>
     */
    public void openTagRaw(String rawTag)
        throws IOException
    {
        writeIndent();
        this.w.append(rawTag.trim()).append(this.newLine);
        this.indent += this.indentIncre;
    }

    public void closeTag(String name)
        throws IOException
    {
        if (this.indent > 0)
        {
            writeIndent();
            this.w.append("</").append(name).append(">").append(this.newLine);
            this.indent -= this.indentIncre;
        }
        else
            throw new IllegalStateException("invalid closing of tag");
    }

    /**
     * Writes raw HTML.
     * @param html Can be empty but not null.
     */
    public void raw(String html)
        throws IOException
    {
        if (html.length() > 0)
        {
            writeIndent();
            this.w.append(html);
        }
    }

    /**
     * Writes text that will be HTML encoded.
     * @param text Can be empty but not null. 
     */
    public void text(String text)
        throws IOException
    {
        if (text.length() > 0)
        {
            writeIndent();
            WebUtil.htmlEncode(text, this.w);
        }
    }
}

