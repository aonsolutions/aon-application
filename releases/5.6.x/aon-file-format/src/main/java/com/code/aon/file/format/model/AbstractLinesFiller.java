package com.code.aon.file.format.model;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * An Abstract lines filler implementing LinesFiller
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public abstract class AbstractLinesFiller implements LinesFiller {

	/**
	 * The listeners registered
	 */
	private ArrayList<LinesFillerListener> listeners = new ArrayList<LinesFillerListener>();
	/**
	 * The Line string
	 */
	protected String line = "";

	public void addLinesFillerListener(LinesFillerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeLinesFillerListener(LinesFillerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fires an event containing itself
	 * 
	 * @param event the line filled event
	 */
	protected void fireLineFilled(EventObject event) {
		for (LinesFillerListener listener: listeners) {
			if (event == null) {
				event = new EventObject(this);
			}
			listener.lineFilled(event);
		}
	}

	public String getLine() {
		return line;
	}

}
