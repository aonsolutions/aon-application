package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlType;

/**
 *  Description of the Interface
 *
 *@author     eva
 *@created    May 21, 2001
 */
interface ProtocolType extends CtsqlType {
	//	public void load(byte buf[], int off);
	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store();


	/**
	 *  Gets the StoreLength attribute of the ProtocolType object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength();


	/**
	 *  Gets the SqlLength attribute of the ProtocolType object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength();
}
