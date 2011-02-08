package com.transtools.ctsql;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlType {

	/**
	 *  Description of the Field
	 */
	public final static short UNKNOWN_TYPE = -1;
	/**
	 *  Description of the Field
	 */
	public final static short CHAR_TYPE = 0;
	/**
	 *  Description of the Field
	 */
	public final static short SMALLINT_TYPE = 1;
	/**
	 *  Description of the Field
	 */
	public final static short INTEGER_TYPE = 2;
	/**
	 *  Description of the Field
	 */
	public final static short TIME_TYPE = 3;
	/**
	 *  Description of the Field
	 */
	public final static short DECIMAL_TYPE = 5;
	/**
	 *  Description of the Field
	 */
	public final static short SERIAL_TYPE = 6;
	/**
	 *  Description of the Field
	 */
	public final static short DATE_TYPE = 7;
	/**
	 *  Description of the Field
	 */
	public final static short MONEY_TYPE = 8;
	/**
	 *  Description of the Field
	 */
	public final static short NULL_TYPE = 9;
	/**
	 *  Description of the Field
	 */
	public final static short DATETIME_TYPE = 10;
	/**
	 *  Description of the Field
	 */
	public final static short BINARY_TYPE = 11;


	/**
	 *  Gets the Null attribute of the CtsqlType object
	 *
	 *@return    The Null value
	 */
	public boolean isNull();


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString();


	/**
	 *  Gets the Type attribute of the CtsqlType object
	 *
	 *@return    The Type value
	 */
	public int getType();

	public String getAsString();
}
