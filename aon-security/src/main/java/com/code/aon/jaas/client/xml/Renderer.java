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

    /**
     * Etiqueta de comienzo del fichero xml.
     */
    static final String DECLARATION = "<?xml version='1.0' encoding='ISO-8859-1' ?>";

    /**
     * Etiqueta de comienzo.
     */
    static final String LT = "<";

    /**
     * Etiqueta final.
     */
    static final String GT = ">";

    /**
     * Etiqueta final.
     */
    static final String END = "/";

	/** Etiqueta de aplicación. */
	static final String APPLICATION = "application";

	/** Etiqueta de domain. */
	static final String DOMAIN = "domain";

    /** Etiqueta de id. */
	static final String ID = "id";

	/**
     * Stream de Salida.
     */
    PrintWriter out;

    /**
     * Profundidad de la linea.
     */
    int deep;

    /**
     * Construye la salida a fichero.
     * 
     * @param obj INode
     * @param out OutputStream
     */
    public void render(INode obj, OutputStream out) {
        render(obj, new PrintWriter(out));
    }

    /**
     * Construye la salida a fichero.
     * 
     * @param obj INode
     * @param out Writer
     */
    public void render(INode obj, Writer out) {
        render(obj, new PrintWriter(out));
    }

    /**
     * Construye la salida a fichero.
     * 
     * @param obj INode
     * @param out PrintWriter
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
     * @param line String
     * @param deep int
     */
    void println(String line, int deep) {
        while (deep-- > 0) {
            out.print('\t');
        }
        out.println(line);
    }

	/**
     * Print a String.
     * 
     * @param line String
     * @param deep int
     */
    void print(String line, int deep) {
        while (deep-- > 0) {
            out.print('\t');
        }
        out.print(line);
    }

    /**
     * Escribe la etiqueta de inicio.
     * 
     * @param name String
     * @return String
     */
    String startElement(String name) {
        return (LT + name + GT);
    }

    /**
     * Escribe la etiqueta de inicio y le añade los atributos dados.
     * 
     * @param name String
     * @param attrs Properties
     * @return String
     */
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
     * Escribe la etiqueta de fin.
     * 
     * @param name String
     * @return String
     */
    String endElement(String name) {
        return (LT + END + name + GT);
    }

}