package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlType;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlDecimal implements ProtocolType, com.transtools.ctsql.CtsqlDecimal {

	private int length = DEFAULT_LENGTH;
	private int precision = DEFAULT_PRECISION;
	private CtsqlDecimalSupport support;

	private final static int DEFAULT_LENGTH = CtsqlDecimalSupport.DBLPRECISION;
	private final static int DEFAULT_PRECISION = CtsqlDecimalSupport.FLOATPREC;


	/**
	 *  Constructor for the CtsqlDecimal object
	 *
	 *@param  value  Description of Parameter
	 */
	public CtsqlDecimal(Double value) {
		support = new CtsqlDecimalSupport(value);
	}


	/**
	 *  Constructor for the CtsqlDecimal object
	 *
	 *@param  value  Description of Parameter
	 */
	public CtsqlDecimal(double value) {
		support = new CtsqlDecimalSupport(value);
	}

	/**
	 *  Constructor for the null CtsqlDecimal object
	 *
	 */
	public CtsqlDecimal() {
		support = new CtsqlDecimalSupport(null);
	}

	/**
	 *  Constructor for the CtsqlDecimal object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 *@param  pack    Description of Parameter
	 */
	public CtsqlDecimal(byte[] buffer, int offset, int pack) {
		support = new CtsqlDecimalSupport();
		this.length = lengthFromPack(pack);
		this.precision = precisionFromPack(pack);
		load(buffer, offset, decLen(length, precision));
	}


	/**
	 *  Sets the AsDouble attribute of the CtsqlDecimal object
	 *
	 *@param  d  The new AsDouble value
	 *@return    Description of the Returned Value
	 */
	public double setAsDouble(double d) {
		return d;
	}


	/**
	 *  Sets the AsShort attribute of the CtsqlDecimal object
	 *
	 *@param  sh  The new AsShort value
	 *@return     Description of the Returned Value
	 */
	public double setAsShort(short sh) {
		return (double) sh;
	}


	/**
	 *  Sets the AsInteger attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsInteger value
	 *@return     Description of the Returned Value
	 */
	public double setAsInteger(int in) {
		return (double) in;
	}

	/* NEW Paco 28-1-2003 */
	/**
	 *  Sets the AsLong attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsLong value
	 *@return     Description of the Returned Value
	 */
	public double setAsLong(long in) {
		return (double) in;
	}

	/**
	 *  Sets the AsFloat attribute of the CtsqlDecimal object
	 *
	 *@param  in  The new AsFloat value
	 *@return     Description of the Returned Value
	 */
	public double setAsFloat(float in) {
		return (double) in;
	}
	/* END-NEW Paco 28-1-2003 */

	/**
	 *  Sets the AsString attribute of the CtsqlDecimal object
	 *
	 *@param  cad  The new AsString value
	 *@return      Description of the Returned Value
	 */
	public double setAsString(String cad) {
		return Double.parseDouble(cad);
	}


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlDecimal object
	 *
	 *@param  bd  The new AsBigDecimal value
	 *@return     Description of the Returned Value
	 */
	public double setAsBigDecimal(java.math.BigDecimal bd) {
		String cad = bd.toString();
		return setAsString(cad);
	}


	/**
	 *  Gets the AsDouble attribute of the CtsqlDecimal object
	 *
	 *@return    The AsDouble value
	 */
	public double getAsDouble() {
		return (isNull() ? 0 : support.tdectodbl());
	}


	/**
	 *  Gets the Type attribute of the CtsqlDecimal object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return CtsqlType.DECIMAL_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlDecimal object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return precMake(length, precision);
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlDecimal object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return getDecimalStoreLength() + CtsqlSmallint.STORE_LENGTH;
	}


	/**
	 *  Gets the Null attribute of the CtsqlDecimal object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return support.isNull();
	}


	/**
	 *  Gets the AsString attribute of the CtsqlDecimal object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		return support.toString();
	}


	/**
	 *  Gets the AsShort attribute of the CtsqlDecimal object
	 *
	 *@return    The AsShort value
	 */
	public short getAsShort() {
		return (short) getAsDouble();
	}


	/**
	 *  Gets the AsInteger attribute of the CtsqlDecimal object
	 *
	 *@return    The AsInteger value
	 */
	public int getAsInteger() {
		return (int) getAsDouble();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf   Description of Parameter
	 *@param  off   Description of Parameter
	 *@param  size  Description of Parameter
	 */
	public void load(byte buf[], int off, int size) {
		support.lddec(buf, off, size);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		byte[] buffer = new byte[getStoreLength()];

		CtsqlSmallint.storeShort((short) getDecimalStoreLength(), buffer);
		support.stdec(buffer, CtsqlSmallint.STORE_LENGTH, getDecimalStoreLength());

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
	 *  Gets the DecimalStoreLength attribute of the CtsqlDecimal object
	 *
	 *@return    The DecimalStoreLength value
	 */
	private int getDecimalStoreLength() {
		return support.getNumberOfDigits() + 1;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  lenprec  Description of Parameter
	 *@return          Description of the Returned Value
	 */
	protected static int lengthFromPack(int lenprec) {
		return (((lenprec) >> 8) & 0xff);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  lenprec  Description of Parameter
	 *@return          Description of the Returned Value
	 */
	protected static int precisionFromPack(int lenprec) {
		return ((lenprec) & 0xff);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  len   Description of Parameter
	 *@param  prec  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	private static int decLen(int len, int prec) {
		return (((len) + ((prec) & 1) + 3) / 2);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  len   Description of Parameter
	 *@param  prec  Description of Parameter
	 *@return       Description of the Returned Value
	 */
	private static int precMake(int len, int prec) {
		return ((len << 8) + prec);
	}
}
