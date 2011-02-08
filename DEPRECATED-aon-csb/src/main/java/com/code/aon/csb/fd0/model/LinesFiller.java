package com.code.aon.csb.fd0.model;

import java.util.Map;

import com.code.aon.csb.fd0.core.Register;


/**
 * Lines filler interface 
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public interface LinesFiller {

	/**
	 * Fills the line structure defined in the register with the the data contained in properties objects  
	 * 
	 * @param register structure of the line
	 * @param properties the data objects
	 * @throws Fd0Exception exception
	 */
	public void fillLine(Register register, Map properties);

	/**
	 * Returns the line
	 * 
	 * @return the line string
	 */
	public String getLine();

	/**
	 * Adds a listener
	 * 
	 * @param listener the LinesFillerListener
	 */
	public void addLinesFillerListener(LinesFillerListener listener);

	/**
	 * Removes a listener
	 * 
	 * @param listener the LinesFillerListener
	 */
	public void removeLinesFillerListener(LinesFillerListener listener);

}
