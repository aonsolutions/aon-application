package com.transtools.ctsql;

import java.util.Calendar;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlTime extends CtsqlType {
	/**
	 *  Gets the AsCalendar attribute of the CtsqlTime object
	 *
	 *@return    The AsCalendar value
	 */
	public Calendar getAsCalendar();


	/**
	 *  Gets the AsString attribute of the CtsqlTime object
	 *
	 *@return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Sets the AsString attribute of the CtsqlTime object
	 *
	 *@param  cad                 The new AsString value
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 */
	public Calendar setAsString(String cad) throws CtsqlException;
}
