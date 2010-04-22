package com.code.aon.csb.fd0.core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Contains and manages a list of fillers
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class Register {

	/**
	 * The list of fillers
	 */
	private ArrayList<Filler> fillers = new ArrayList<Filler>();

	/**
	 * Adds a filler
	 * 
	 * @param filler the filler
	 */
	public void add(Filler filler) {
		fillers.add(filler);
	}

	/**
	 * Returns the iterator of the fillers
	 * 
	 * @return the fillers iterator
	 */
	public Iterator iterator() {
		return fillers.iterator();
	}

	/**
	 * Returns last filler
	 * 
	 * @return last filler
	 */
	public Filler getLastFiller() {
		return (Filler)fillers.get(fillers.size() - 1);
	}

}
