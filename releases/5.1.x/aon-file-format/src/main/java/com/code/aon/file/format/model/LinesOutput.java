package com.code.aon.file.format.model;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EventObject;

/**
 * The lines writer
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class LinesOutput implements LinesFillerListener {

	private static final String CRCL = "\r\n";
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
		for (String line: lines) {
			out.print(line);
            out.print(CRCL);
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
