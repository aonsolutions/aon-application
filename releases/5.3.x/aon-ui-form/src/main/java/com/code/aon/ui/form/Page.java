package com.code.aon.ui.form;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.code.aon.common.ITransferObject;

/**
 * Page represents a part of the model that will be loaded.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 31-may-2005
 * @since 1.0
 */
public class Page implements Serializable {

	
	private static final long serialVersionUID = -591644494125219121L;

	/** EMPTY_PAGE. */
	public static final Page EMPTY_PAGE;
	
	/** Index from which the page is loaded */
	private int start;
	
	/** Loaded objects. */
	private List<ITransferObject> objects;

	/**
	 * Constructor.
	 * 
	 * @param start the first index
	 * @param l the list
	 */
	public Page(List<ITransferObject> l, int start) {
		setList(l);
	    this.start = start;
	}

	/**
	 * Gets the list.
	 * 
	 * @return the list
	 */
	public List<ITransferObject> getList() {
	    return objects;
	}

	/**
	 * Sets the list.
	 * 
	 * @param l the list
	 */
	public void setList(List<ITransferObject> l) {
	    objects = l;
	}

	/**
	 * Gets the starting index of the next page.
	 * 
	 * @return the starting index of the next page
	 */
	public int getStartOfNextPage()	{
	    return start + objects.size();
	}
	
	/**
	 * Gets the starting index of the previous page.
	 * 
	 * @return the starting index of the previous page
	 */
	public int getStartOfPreviousPage()	{
	    return Math.max(start - getSize(), 0);
	}
	
	/**
	 * Gets size.
	 * 
	 * @return the size
	 */
	public int getSize() {
	    return objects.size();
	}
	
	/**
	 * Gets the starting index of the page.
	 * 
	 * @return the starting index of the
	 */
	public int getStart() {
	    return start;
	}

	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return (objects == null) || (getSize() == 0);
	}
	
	static {
	    EMPTY_PAGE = new Page(Collections.<ITransferObject>emptyList(), 0);
	}
}