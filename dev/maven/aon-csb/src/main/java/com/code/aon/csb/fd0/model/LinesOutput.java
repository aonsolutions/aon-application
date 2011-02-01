package com.code.aon.csb.fd0.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;

/**
 * The lines writer
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class LinesOutput implements LinesFillerListener {

	/**
	 * The writer
	 */
	private PrintWriter out;
	/**
	 * Lines array
	 */
	public ArrayList<String> lines = new ArrayList<String>();
	/**
	 * line numbers
	 */
	private int numLines = 0;

	/**
	 * Constructor with writer assigned
	 * 
	 * @param out the writer
	 */
	public LinesOutput(PrintWriter out) {
		this.out = new PrintWriter(out);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.model.LinesFillerListener#lineFilled(java.util.EventObject)
	 */
	public void lineFilled(EventObject event) {
		LinesFiller lineFiller = (LinesFiller)event.getSource();
		lines.add(lineFiller.getLine());
		++numLines;
	}

	/**
	 * Returns lines number
	 * 
	 * @return the lines number
	 */
	public int getNumLines() {
		return numLines;
	}

	/**
	 * Flush the writer
	 */
	public void flush() {
		Iterator iter = lines.iterator();
		while (iter.hasNext()) {
			String line = (String)iter.next();
			out.print(line);
            out.print("\r\n");
		}
		out.flush();
		out.close();
	}

	/**
	 * Closes the writer
	 */
	public void close() {
		out.println();
		out.close();
	}

}
