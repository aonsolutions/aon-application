package com.transtools.ctsql;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlSmallint extends CtsqlType {
	/**
	 *  Gets the AsShort attribute of the CtsqlSmallint object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort();


	/**
	 *  Gets the AsString attribute of the CtsqlSmallint object
	 *
	 *@return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Gets the AsInteger attribute of the CtsqlSmallint object
	 *
	 *@return    The AsInteger value
	 */
	public int getAsInteger();


	/**
	 *  Gets the AsDouble attribute of the CtsqlSmallint object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble();


	/**
	 *  Sets the AsShort attribute of the CtsqlSmallint object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public short setAsShort(short sh);


	/**
	 *  Sets the AsString attribute of the CtsqlSmallint object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public short setAsString(String cad);


	/**
	 *  Sets the AsInteger attribute of the CtsqlSmallint object
	 *
	 *@param  in  The new AsInteger value
	 *@return     Description of the Returned Value
	 */
	public short setAsInteger(int in);


	/**
	 *  Sets the AsDouble attribute of the CtsqlSmallint object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public short setAsDouble(double d);


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlSmallint object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public short setAsBigDecimal(java.math.BigDecimal bd);
}
