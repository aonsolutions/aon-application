package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlException;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlBinary implements ProtocolType, com.transtools.ctsql.CtsqlBinary {

	private byte[] buffer;

	private static String NULL_BINARY = "NULL BINARY";


	/**
	 *  Constructor for the CtsqlBinary object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlBinary(byte[] buffer, int offset) {
		load(buffer, offset);
	}


	/**
	 *  Constructor for the CtsqlBinary object
	 *
	 *@param  buffer  Description of Parameter
	 */
	public CtsqlBinary(byte[] buffer) {
		this.buffer = buffer;
	}

	/**
	 *  Constructor for the null CtsqlBinary object
	 */
	public CtsqlBinary() {
		this.buffer = null;
	}

	/**
	 *  Sets the AsString attribute of the CtsqlBinary object
	 *
	 *@param  cad                 The new AsString value
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 */
	public byte[] setAsString(String cad) throws CtsqlException {
		int len = cad.length();
		byte[] bytes = new byte[len];
		for (int i=0; i<len; i++) {
			bytes[i] = (byte) cad.charAt(i);
		}
		return bytes;
	}


	/**
	 *  Gets the Type attribute of the CtsqlBinary object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.BINARY_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlBinary object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return (buffer != null) ? buffer.length : 0;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlBinary object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return CtsqlSmallint.STORE_LENGTH + getSqlLength();
	}


	/**
	 *  Gets the Null attribute of the CtsqlBinary object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return (buffer == null);
	}


	/**
	 *  Gets the AsBytes attribute of the CtsqlBinary object
	 *
	 *@return    The AsBytes value
	 */
	public byte[] getAsBytes() {
		return buffer;
	}


	/**
	 *  Gets the AsString attribute of the CtsqlBinary object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		if (isNull()) {
			return NULL_BINARY;
		}

		StringBuffer value = new StringBuffer();

		for (int idx = 0; idx < buffer.length; idx++) {
			int number = (buffer[idx] & 0xFF);
			value.append(Integer.toHexString(number));
		}
		return value.toString();
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		byte[] storeBuffer = new byte[getStoreLength()];
		if (isNull()) {
			CtsqlSmallint.storeShort((short) -1, storeBuffer);
		}
		else {
			CtsqlSmallint.storeShort((short) getSqlLength(), storeBuffer);
			System.arraycopy(buffer, 0, storeBuffer, CtsqlSmallint.STORE_LENGTH, getSqlLength());
		}
		return storeBuffer;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public String toString() {
		char[] charBuf = new char[buffer.length];
		
		for (int i=0; i<buffer.length; i++) {
			charBuf[i] = (char) (((char) buffer[i]) & 0x00ff);
		}
		return new String(charBuf);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  loadBuffer  Description of Parameter
	 *@param  off         Description of Parameter
	 */
	private void load(byte loadBuffer[], int off) {
		int size = CtsqlInteger.loadInt(loadBuffer, off);
		if (size == -1) {
			buffer = null;
		}
		else {
			this.buffer = new byte[size];
			System.arraycopy(loadBuffer, CtsqlInteger.STORE_LENGTH + off, buffer, 0, size);
		}
	}
}
