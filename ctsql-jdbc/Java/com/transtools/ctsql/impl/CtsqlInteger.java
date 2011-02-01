package com.transtools.ctsql.impl;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlInteger implements ProtocolType, com.transtools.ctsql.CtsqlInteger {

	private int value;
	private byte[] buffer = {0, 0, 0, 0};

	/**
	 *  Description of the Field
	 */
	public final static int NULL = (int) 0x80000000;
	/**
	 *  Description of the Field
	 */
	public final static int STORE_LENGTH = 4;
	/**
	 *  Description of the Field
	 */
	public final static int SQL_LENGTH = 4;


	/**
	 *  Constructor for the CtsqlInteger object
	 *
	 *@param  aValue  Description of Parameter
	 */
	public CtsqlInteger(int aValue) {
		value = aValue;
	}


	/**
	 *  Constructor for the CtsqlInteger object
	 *
	 *@param  aValue  Description of Parameter
	 */
	public CtsqlInteger(Integer aValue) {
		value = (aValue == null ? NULL : aValue.intValue());
	}

	/**
	 *  Constructor for the null CtsqlInteger object
	 */
	public CtsqlInteger() {
		value = NULL;
	}

	/**
	 *  Constructor for the CtsqlInteger object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlInteger(byte[] buffer, int offset) {
		load(buffer, offset);
	}


	/**
	 *  Sets the AsInt attribute of the CtsqlInteger object
	 *
	 *@param  in  The new AsInt value
	 *@return     Description of the Returned Value
	 */
	public int setAsInt(int in) {
		return in;
	}


	/**
	 *  Sets the AsShort attribute of the CtsqlInteger object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public int setAsShort(short sh) {
		return (int) sh;
	}


	/**
	 *  Sets the AsString attribute of the CtsqlInteger object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public int setAsString(String cad) {
		return Integer.parseInt(cad);
	}


	/**
	 *  Sets the AsDouble attribute of the CtsqlInteger object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public int setAsDouble(double d) {
		return (int) d;
	}


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlInteger object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public int setAsBigDecimal(java.math.BigDecimal bd) {
		String cad = bd.toString();
		return setAsString(cad);
	}


	/**
	 *  Gets the AsInt attribute of the CtsqlInteger object
	 *
	 *@return    The AsInt value
	 */
	public int getAsInt() {
		return getValue();
	}


	/**
	 *  Gets the Type attribute of the CtsqlInteger object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.INTEGER_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlInteger object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return SQL_LENGTH;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlInteger object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return STORE_LENGTH;
	}


	/**
	 *  Gets the Null attribute of the CtsqlInteger object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return value == NULL;
	}


	/**
	 *  Gets the AsString attribute of the CtsqlInteger object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		return Integer.toString(getValue());
	}


	/**
	 *  Gets the AsShort attribute of the CtsqlInteger object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort() {
		return (short) getValue();
	}


	/**
	 *  Gets the AsDouble attribute of the CtsqlInteger object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble() {
		Integer i = new Integer(getValue());
		return i.doubleValue();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		storeInt(value, buffer);
		return buffer;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		return getAsString();
	}


	/**
	 *  Gets the Value attribute of the CtsqlInteger object
	 *
	 *@return    The Value value
	 */
	private int getValue() {
		return (isNull() ? 0 : value);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 */
	private void load(byte buf[], int off) {
		value = loadInt(buf, off);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  aValue  Description of Parameter
	 *@param  buf     Description of Parameter
	 */
	public static void storeInt(int aValue, byte buf[]) {
		buf[0] = (byte) ((aValue & 0xFF000000) >> 24);
		buf[1] = (byte) ((aValue & 0x00FF0000) >> 16);
		buf[2] = (byte) ((aValue & 0x0000FF00) >> 8);
		buf[3] = (byte) ((aValue & 0x000000FF));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 *@return      Description of the Returned Value
	 */
	public static int loadInt(byte buf[], int off) {
		int res;
		int i;
		res = ((byte) (buf[off + 3])) & 0xff;
		i = res;
		res = ((byte) (buf[off + 2])) & 0xff;
		i += res << 8;
		res = ((byte) (buf[off + 1])) & 0xff;
		i += res << 16;
		res = ((byte) (buf[off])) & 0xff;
		i += res << 24;
		return i;
	}
}
