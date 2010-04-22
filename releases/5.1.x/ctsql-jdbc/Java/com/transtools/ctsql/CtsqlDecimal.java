package com.transtools.ctsql;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlDecimal extends CtsqlType {
	/**
	 *  Gets the AsDouble attribute of the CtsqlDecimal object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble();


	/**
	 *  Gets the AsShort attribute of the CtsqlDecimal object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort();


	/**
	 *  Gets the AsInteger attribute of the CtsqlDecimal object
	 *
	 *@return    The AsInteger value
	 */
	public int getAsInteger();


	/**
	 *  Gets the AsString attribute of the CtsqlDecimal object
	 *
	 *@return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Sets the AsDouble attribute of the CtsqlDecimal object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public double setAsDouble(double d);


	/**
	 *  Sets the AsShort attribute of the CtsqlDecimal object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public double setAsShort(short sh);


	/**
	 *  Sets the AsInteger attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsInteger value
	 *@return     Description of the Returned Value
	 */
	public double setAsInteger(int in);


	/* NEW Paco 28-1-2003 */
	/**
	 *  Sets the AsInteger attribute of the CtsqlDecimal object
	 *
	 *  Sets the AsLong attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsLong value
	 */
	public double setAsLong(long in);

	/**
	 *  Sets the AsFloat attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsFloat value
	 *@return     Description of the Returned Value
	 */
	public double setAsFloat(float in);
	/* END-NEW Paco 28-1-2003 */

	/**
	 *  Sets the AsString attribute of the CtsqlDecimal object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public double setAsString(String cad);


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlDecimal object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public double setAsBigDecimal(java.math.BigDecimal bd);
}
