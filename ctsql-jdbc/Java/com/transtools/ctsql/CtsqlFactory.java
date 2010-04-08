package com.transtools.ctsql;

import java.util.Calendar;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public abstract class CtsqlFactory {

	private final static CtsqlFactory factory = new com.transtools.ctsql.impl.Factory();


	/**
	 *  Gets the NewServer attribute of the CtsqlFactory object
	 *
	 *@return    The NewServer value
	 */
	public abstract CtsqlServer getNewServer();


	/**
	 *  Gets the NewCursor attribute of the CtsqlFactory object
	 *
	 *@param  server  Description of Parameter
	 *@return         The NewCursor value
	 */
	public abstract CtsqlCursor getNewCursor(CtsqlServer server);


	/**
	 *  Gets the NewChar attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewChar value
	 */
	public abstract com.transtools.ctsql.CtsqlChar getNewChar(String value);


	/**
	 *  Gets the NewDate attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDate value
	 */
	public abstract com.transtools.ctsql.CtsqlDate getNewDate(Calendar value);


	/**
	 *  Gets the NewDateTime attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDateTime value
	 */
	public abstract com.transtools.ctsql.CtsqlDateTime getNewDateTime(Calendar value);


	/**
	 *  Gets the NewTime attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewTime value
	 */
	public abstract com.transtools.ctsql.CtsqlTime getNewTime(Calendar value);


	/**
	 *  Gets the NewDecimal attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDecimal value
	 */
	public abstract com.transtools.ctsql.CtsqlDecimal getNewDecimal(Double value);


	/**
	 *  Gets the NewInteger attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewInteger value
	 */
	public abstract com.transtools.ctsql.CtsqlInteger getNewInteger(Integer value);


	/**
	 *  Gets the NewSmallint attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewSmallint value
	 */
	public abstract com.transtools.ctsql.CtsqlSmallint getNewSmallint(Short value);


	/**
	 *  Gets the NewDecimal attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDecimal value
	 */
	public abstract com.transtools.ctsql.CtsqlDecimal getNewDecimal(double value);


	/**
	 *  Gets the NewInteger attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewInteger value
	 */
	public abstract com.transtools.ctsql.CtsqlInteger getNewInteger(int value);


	/**
	 *  Gets the NewSmallint attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewSmallint value
	 */
	public abstract com.transtools.ctsql.CtsqlSmallint getNewSmallint(short value);


	/**
	 *  Gets the NewBinary attribute of the CtsqlFactory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewBinary value
	 */
	public abstract com.transtools.ctsql.CtsqlBinary getNewBinary(byte[] value);


	/**
	 *  Gets the Factory attribute of the CtsqlFactory class
	 *
	 *@return    The Factory value
	 */
	public final static CtsqlFactory getFactory() {
		return factory;
	}
}
