package com.code.aon.csb.fd0.model;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;

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

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.model.LinesFiller#addLinesFillerListener(com.code.aon.csb.fd0.model.LinesFillerListener)
	 */
	public void addLinesFillerListener(LinesFillerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.model.LinesFiller#removeLinesFillerListener(com.code.aon.csb.fd0.model.LinesFillerListener)
	 */
	public void removeLinesFillerListener(LinesFillerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fires an event containing itself
	 * 
	 * @param event the line filled event
	 */
	protected void fireLineFilled(EventObject event) {
		Iterator iter = listeners.iterator();
		while (iter.hasNext()) {
			LinesFillerListener listener = (LinesFillerListener)iter.next();
			if (event == null) {
				event = new EventObject(this);
			}
			listener.lineFilled(event);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.model.LinesFiller#getLine()
	 */
	public String getLine() {
		return line;
	}

}
