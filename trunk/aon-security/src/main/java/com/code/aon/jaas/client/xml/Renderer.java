package com.code.aon.jaas.client.xml;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Properties;

import com.code.aon.jaas.client.ast.INode;
import com.code.aon.jaas.client.ast.INodeVisitor;

/**
 * Escribe el fichero XML asociado al Stream de salida.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-may-2004
 * @since 1.0
 *  
 */
public abstract class Renderer implements INodeVisitor {

    /** xml definition tag. */
    static final String DECLARATION = "<?xml version='1.0' encoding='ISO-8859-1' ?>";

    /** < tag. */
    static final String LT = "<";

    /** > tag. */
    static final String GT = ">";

    /** / tag. */
    static final String END = "/";

	/** application tag. */
	static final String APPLICATION = "application";

	/** domain tag. */
	static final String DOMAIN = "domain";

    /** id tag. */
	static final String ID = "id";

	/** description tag. */
    static final String DESCRIPTION = "description";

	/** output stream. */
    PrintWriter out;

    /** line depth. */
    int deep;

    /**
     * Renders file output.
     * 
     * @param INode
     * @param OutputStream
     */
    public void render(INode obj, OutputStream out) {
        render(obj, new PrintWriter(out));
    }

    /**
     * Renders file output.
     * 
     * @param INode
     * @param Writer
     */
    public void render(INode obj, Writer out) {
        render(obj, new PrintWriter(out));
    }

    /**
     * Renders file output.
     * 
     * @param INode
     * @param PrintWriter
     */
    public void render(INode obj, PrintWriter out) {
        this.out = out;
        obj.accept(this);
        this.out.flush();
        this.out.close();
    }

	/**
     * Print a String and then terminate the line. 
     * 
     * @param String
     * @param int
     */
    void println(String line, int deep) {
        while (deep-- > 0) {
            out.print('\t');
        }
        out.println(line);
    }

	/**
     * Prints a String.
     * 
     * @param String
     * @param int
     */
    void print(String line, int deep) {
        while (deep-- > 0) {
            out.print('\t');
        }
        out.print(line);
    }

    /**
     * Prints start tag.
     * 
     * @param String
     * @return String
     */
    String startElement(String name) {
        return (LT + name + GT);
    }

    /**
     * Prints start element with attributes.
     * 
     * @param String
     * @param Properties
     * @return String
     */
    @SuppressWarnings("unchecked")
    String startElement(String name, Properties attrs) {
    	StringBuffer label = new StringBuffer(name);
    	Enumeration enumeration = attrs.keys();
    	while (enumeration.hasMoreElements()) {
    		String key = (String) enumeration.nextElement();
			label.append( " " + key + "=\"" + attrs.getProperty(key) + "\"");
		}
        return (LT + label.toString() + GT);
    }

    /**
     * Prints end tag.
     * 
     * @param String
     * @return String
     */
    String endElement(String name) {
        return (LT + END + name + GT);
    }

    /**
     * Convert <b>null</b> value to empty <code>String</code>
     * 
     * @param value
     * @return
     */
	String null2Empty(String value) {
		return ( value == null )? "": value;
	}

}