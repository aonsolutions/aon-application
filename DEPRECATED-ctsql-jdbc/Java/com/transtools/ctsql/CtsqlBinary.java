package com.transtools.ctsql;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
public interface CtsqlBinary extends CtsqlType {
	/**
	 *  Gets the AsString attribute of the CtsqlBinary object
	 *
	 *@return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Sets the AsString attribute of the CtsqlBinary object
	 *
	 *@param  cad                 The new AsString value
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 */
	public byte[] setAsString(String cad) throws CtsqlException;


	/**
	 *  Gets the AsBytes attribute of the CtsqlBinary object
	 *
	 *@return    The AsBytes value
	 */
	public byte[] getAsBytes();
}
