package com.transtools.jdbc;

import com.transtools.ctsql.CtsqlChannel;

import java.io.IOException;

/**
 *@author          eva
 *@created         April 25, 2001
 *@jdbc.pending    hay que repasar todo lo referente al lanzamiento de
 *      excepciones en esta clase.
 */

public class CtsqlCommBuffer {

	private CtsqlChannel ctsqlChannel;
	private byte[] buf4bytes = {0, 0, 0, 0};
	private int count = 0;
	private boolean isUsed = false;
	private int convert = 0;

	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTANSITOOEM = 1;
	/**
	 *  Description of the Field
	 */
	public final static int CNT_CONVERTOEMTOANSI = 2;

	private static String DATABASE = "database";
	private static byte[] EVEN_BYTE = {0};
	private static byte[] DUMB_BUFFER = {0};


	/**
	 *  Constructor for the CtsqlCommBuffer object
	 *
	 *@param  aCtsqlChannel  Description of Parameter
	 */
	public CtsqlCommBuffer(CtsqlChannel aCtsqlChannel) {
		this.ctsqlChannel = aCtsqlChannel;
	}


	/**
	 *  Sets the Convert attribute of the CtsqlCommBuffer object
	 *
	 *@param  convert  The new Convert value
	 */
	public void setConvert(int convert) {
		this.convert = convert;
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of Exception
	 */
	public void close() throws IOException {
		ctsqlChannel.close();
	}


	/**
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void flush() throws IOException {
		ctsqlChannel.flush();
	}


	/**
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public short readSmallint() throws IOException {
		ctsqlChannel.readData(buf4bytes, 0, 2);
		return loadShort(buf4bytes, 0);
	}


	/**
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public int readInteger() throws IOException {
		ctsqlChannel.readData(buf4bytes, 0, 4);
		return loadInt(buf4bytes, 0);
	}


	/**
	 *@param  data             Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void writeSmallint(short data) throws IOException {
		storeShort(data, buf4bytes);
		ctsqlChannel.writeData(buf4bytes, 2);
	}


	/**
	 *@return                  Description of the Returned Value
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public String readString() throws IOException {
		int len;

		len = readSmallint();
		if (len > 0) {
			byte buffer[] = new byte[len];
			ctsqlChannel.readData(buffer, 0, len);
			evenizeRead(len);
			return new String(buffer);
		}
		return "";
	}


	/**
	 *@param  aString          Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void writeString(String aString) throws IOException {
		int length = aString.length();
		writeSmallint((short) length);
		ctsqlChannel.writeData(aString.getBytes(), length);
		evenizeWrite(length);
	}


	/**
	 *@param  data             Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void writeData(byte[] data, int len) throws IOException {
		ctsqlChannel.writeData(data, len);
		evenizeWrite(len);
	}


	/**
	 *@param  buffer           Description of Parameter
	 *@param  off              Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void readData(byte buffer[], int off, int len) throws IOException {
		ctsqlChannel.readData(buffer, off, len);
	}


	/**
	 *@param  buffer           Description of Parameter
	 *@param  off              Description of Parameter
	 *@param  len              Description of Parameter
	 *@exception  IOException  Description of Exception
	 *@jdbc.pending            hay que repasar todo lo referente al lanzamiento de
	 *      excepciones en este método.
	 */
	public void readDataEven(byte buffer[], int off, int len) throws IOException {
		readData(buffer, off, len);
		evenizeRead(len);
	}


	/**
	 *  NEW Paco 21-12-2001 Returns a byte array readed from the channel
	 *
	 *@return                  byte array
	 *@exception  IOException  Description of Exception
	 */
	public byte[] readByteArray() throws IOException {
		int len;

		len = readSmallint();
		if (len > 0) {
			byte buffer[] = new byte[len];
			ctsqlChannel.readData(buffer, 0, len);
			evenizeRead(len);
			return buffer;
		}
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  length           Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private void evenizeWrite(int length) throws IOException {
		if ((length & 1) != 0) {
			ctsqlChannel.writeData(EVEN_BYTE, 1);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  length           Description of Parameter
	 *@exception  IOException  Description of Exception
	 */
	private void evenizeRead(int length) throws IOException {
		if ((length & 1) != 0) {
			ctsqlChannel.readData(DUMB_BUFFER, 0, 1);
		}
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

