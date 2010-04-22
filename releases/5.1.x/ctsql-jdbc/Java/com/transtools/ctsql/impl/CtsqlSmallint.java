package com.transtools.ctsql.impl;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlSmallint implements ProtocolType, com.transtools.ctsql.CtsqlSmallint {

	private short value;
	private byte[] buffer = {0, 0};

	/**
	 *  Description of the Field
	 */
	public final static short NULL = (short) 0x8000;
	/**
	 *  Description of the Field
	 */
	public final static int STORE_LENGTH = 2;
	/**
	 *  Description of the Field
	 */
	public final static int SQL_LENGTH = 2;


	/**
	 *  Constructor for the null CtsqlSmallint object
	 */
	public CtsqlSmallint() {
		value = NULL;
	}


	/**
	 *  Constructor for the CtsqlSmallint object
	 *
	 *@param  aValue  Description of Parameter
	 */
	public CtsqlSmallint(short aValue) {
		value = aValue;
	}


	/**
	 *  Constructor for the CtsqlSmallint object
	 *
	 *@param  aValue  Description of Parameter
	 */
	public CtsqlSmallint(Short aValue) {
		value = (aValue == null ? NULL : aValue.shortValue());
	}


	/**
	 *  Constructor for the CtsqlSmallint object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlSmallint(byte[] buffer, int offset) {
		load(buffer, offset);
	}


	/**
	 *  Sets the AsShort attribute of the CtsqlSmallint object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public short setAsShort(short sh) {
		return sh;
	}


	/**
	 *  Sets the AsString attribute of the CtsqlSmallint object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public short setAsString(String cad) {
		return Short.parseShort(cad);
	}


	/**
	 *  Sets the AsInteger attribute of the CtsqlSmallint object
	 *
	 *@param  in  The new AsInteger value
	 *@return     Description of the Returned Value
	 */
	public short setAsInteger(int in) {
		return (short) in;
	}


	/**
	 *  Sets the AsDouble attribute of the CtsqlSmallint object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public short setAsDouble(double d) {
		return (short) d;
	}


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlSmallint object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public short setAsBigDecimal(java.math.BigDecimal bd) {
		String cad = bd.toString();
		return setAsString(cad);
	}


	/**
	 *  Gets the AsShort attribute of the CtsqlSmallint object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort() {
		return getValue();
	}


	/**
	 *  Gets the Type attribute of the CtsqlSmallint object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.SMALLINT_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlSmallint object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return SQL_LENGTH;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlSmallint object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return STORE_LENGTH;
	}


	/**
	 *  Gets the Null attribute of the CtsqlSmallint object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return value == NULL;
	}


	/**
	 *  Gets the AsString attribute of the CtsqlSmallint object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		return Short.toString(value);
	}


	/**
	 *  Gets the AsInteger attribute of the CtsqlSmallint object
	 *
	 *@return    The AsInteger value
	 */
	public int getAsInteger() {
		return getValue();
	}


	/**
	 *  Gets the AsDouble attribute of the CtsqlSmallint object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble() {
		Short sh = new Short(getValue());
		return sh.doubleValue();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		storeShort(value, buffer);
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
	 *  Gets the Value attribute of the CtsqlSmallint object
	 *
	 *@return    The Value value
	 */
	private short getValue() {
		return (isNull() ? 0 : value);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 */
	private void load(byte buf[], int off) {
		value = loadShort(buf, off);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  aValue  Description of Parameter
	 *@param  buf     Description of Parameter
	 */
	public static void storeShort(short aValue, byte buf[]) {
		buf[0] = (byte) ((aValue & 0xFF00) >> 8);
		buf[1] = (byte) ((aValue & 0x00FF));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 *@return      Description of the Returned Value
	 */
	public static short loadShort(byte buf[], int off) {
		short res;
		short i;
		res = (short) (((byte) (buf[off + 1])) & 0xff);
		i = res;
		res = (short) (((byte) (buf[off])) & 0xff);
		i += res << 8;
		return i;
	}
}
