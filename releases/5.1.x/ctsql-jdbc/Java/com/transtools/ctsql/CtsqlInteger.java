package com.transtools.ctsql;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlInteger extends CtsqlType {
	/**
	 *  Gets the AsInt attribute of the CtsqlInteger object
	 *
	 *@return    The AsInt value
	 */
	public int getAsInt();


	/**
	 *  Gets the AsShort attribute of the CtsqlInteger object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort();


	/**
	 *  Gets the AsString attribute of the CtsqlInteger object
	 *
	 *@return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Gets the AsDouble attribute of the CtsqlInteger object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble();


	/**
	 *  Sets the AsInt attribute of the CtsqlInteger object
	 *
	 *@param  in  The new AsInt value
	 *@return     Description of the Returned Value
	 */
	public int setAsInt(int in);


	/**
	 *  Sets the AsShort attribute of the CtsqlInteger object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public int setAsShort(short sh);


	/**
	 *  Sets the AsString attribute of the CtsqlInteger object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public int setAsString(String cad);


	/**
	 *  Sets the AsDouble attribute of the CtsqlInteger object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public int setAsDouble(double d);


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlInteger object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public int setAsBigDecimal(java.math.BigDecimal bd);
}
