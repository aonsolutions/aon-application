package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlCursor;
import com.transtools.ctsql.CtsqlFactory;
import com.transtools.ctsql.CtsqlServer;

import java.util.Calendar;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
public class Factory extends CtsqlFactory {

	/**
	 *  Gets the NewServer attribute of the Factory object
	 *
	 *@return    The NewServer value
	 */
	public CtsqlServer getNewServer() {
		return new CtsqlServerImpl();
	}


	/**
	 *  Gets the NewCursor attribute of the Factory object
	 *
	 *@param  server  Description of Parameter
	 *@return         The NewCursor value
	 */
	public CtsqlCursor getNewCursor(CtsqlServer server) {
		return new CtsqlStmt((CtsqlServerImpl) server);
	}


	/**
	 *  Gets the NewDecimal attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDecimal value
	 */
	public com.transtools.ctsql.CtsqlDecimal getNewDecimal(double value) {
		return new CtsqlDecimal(value);
	}


	/**
	 *  Gets the NewInteger attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewInteger value
	 */
	public com.transtools.ctsql.CtsqlInteger getNewInteger(int value) {
		return new CtsqlInteger(value);
	}


	/**
	 *  Gets the NewSmallint attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewSmallint value
	 */
	public com.transtools.ctsql.CtsqlSmallint getNewSmallint(short value) {
		return new CtsqlSmallint(value);
	}


	/**
	 *  Gets the NewChar attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewChar value
	 */
	public com.transtools.ctsql.CtsqlChar getNewChar(String value) {
		return new CtsqlChar(value);
	}


	/**
	 *  Gets the NewDate attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDate value
	 */
	public com.transtools.ctsql.CtsqlDate getNewDate(Calendar value) {
		return new CtsqlDate(value);
	}


	/**
	 *  Gets the NewDecimal attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDecimal value
	 */
	public com.transtools.ctsql.CtsqlDecimal getNewDecimal(Double value) {
		return new CtsqlDecimal(value);
	}


	/**
	 *  Gets the NewInteger attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewInteger value
	 */
	public com.transtools.ctsql.CtsqlInteger getNewInteger(Integer value) {
		return new CtsqlInteger(value);
	}


	/**
	 *  Gets the NewSmallint attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewSmallint value
	 */
	public com.transtools.ctsql.CtsqlSmallint getNewSmallint(Short value) {
		return new CtsqlSmallint(value);
	}


	/**
	 *  Gets the NewTime attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewTime value
	 */
	public com.transtools.ctsql.CtsqlTime getNewTime(Calendar value) {
		return new CtsqlTime(value);
	}


// New Eva 18-04-2001

	/**
	 *  Gets the NewDateTime attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewDateTime value
	 */
	public com.transtools.ctsql.CtsqlDateTime getNewDateTime(Calendar value) {
		return new CtsqlDateTime(value);
	}


// END Eva 18-04-2001

	/**
	 *  Gets the NewBinary attribute of the Factory object
	 *
	 *@param  value  Description of Parameter
	 *@return        The NewBinary value
	 */
	public com.transtools.ctsql.CtsqlBinary getNewBinary(byte[] value) {
		return new CtsqlBinary(value);
	}
}

