/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

/**
 *  Description of the Class
 *
 * @author     eva
 * @created    May 21, 2001
 * @version    $Revision: 1.13 $
 */
class CtsqlChar implements ProtocolType, com.transtools.ctsql.CtsqlChar {
	private static String NULL = "Null Char";

	private String value;
	private byte[] buffer;
	private boolean isOem;
	private boolean rTrimChar;


	/**
	 *  Constructor for the CtsqlChar object
	 *
	 * @param  buffer  Description of Parameter
	 * @param  offset  Description of Parameter
	 * @param  length  Description of Parameter
	 * @param  isOem   <OJO - Put here the parameter description>
	 */
	public CtsqlChar(byte[] buffer, int offset, int length, boolean isOem, boolean rTrimChar) {
		this.isOem = isOem;
		this.rTrimChar = rTrimChar;
		load(buffer, offset, length);
	}


	/**
	 *  Constructor for the CtsqlChar object
	 *
	 * @param  aValue string value, null value is represented with null string
	 */
	public CtsqlChar(String aValue) {
		/*
		 *  New MA 23-10-2001 el string vacio no es nulo en JDBC,
		 *  sustituyo el string vacio por un espacio para que el sql lo 'padee'
		 */
		if ((aValue != null) && (aValue.compareTo("") == 0)) {
			aValue = " ";
		}
		/*
		 *  End New MA 23-10-2001
		 */
		value = aValue;
	}

	/**
	 *  Constructor for the null CtsqlChar object
	 */
	public CtsqlChar() {
		/*
		 *  Mod MA 23-10-2001 el nulo se representa con null
		 *  value = NULL;
		 */
		value = null;
		/*
		 *  End Mod. MA 23-10-2001
		 */
	}

	/**
	 *  Sets the AsString attribute of the CtsqlChar object
	 *
	 * @param  string  The new AsString value
	 * @return         Description of the Returned Value
	 */
	public String setAsString(String string) {
		return string;
	}


	/**
	 *  Sets the AsShort attribute of the CtsqlChar object
	 *
	 * @param  sh  The new AsShort value
	 * @return     Description of the Returned Value
	 */
	public String setAsShort(short sh) {
		return Short.toString(sh);
	}


	/**
	 *  Sets the AsInteger attribute of the CtsqlChar object
	 *
	 * @param  in  The new AsInteger value
	 * @return     Description of the Returned Value
	 */
	public String setAsInteger(int in) {
		return Integer.toString(in);
	}


	/**
	 *  Sets the AsDouble attribute of the CtsqlChar object
	 *
	 * @param  d  The new AsDouble value
	 * @return    Description of the Returned Value
	 */
	public String setAsDouble(double d) {
		return Double.toString(d);
	}


	/**
	 *  Sets the AsDateCalendar attribute of the CtsqlChar object
	 *
	 * @param  dt  The new AsDateCalendar value
	 * @return     Description of the Returned Value
	 */
	public String setAsDateCalendar(java.sql.Date dt) {
		return dt.toString();
	}


	/**
	 *  Sets the AsTimeCalendar attribute of the CtsqlChar object
	 *
	 * @param  tm  The new AsTimeCalendar value
	 * @return     Description of the Returned Value
	 */
	public String setAsTimeCalendar(java.sql.Time tm) {
		return tm.toString();
	}


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlChar object
	 *
	 * @param  bd  The new AsBigDecimal value
	 * @return     Description of the Returned Value
	 */
	public String setAsBigDecimal(java.math.BigDecimal bd) {
		return bd.toString();
	}


	/**
	 *  Gets the AsString attribute of the CtsqlChar object
	 *
	 * @return    The AsString value
	 */
	public String getAsString() {
		return value;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlChar object
	 *
	 * @return    The StoreLength value
	 */
	public int getStoreLength() {
		return CtsqlSmallint.STORE_LENGTH + getSqlLength();
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlChar object
	 *
	 * @return    The SqlLength value
	 */
	public int getSqlLength() {
		return (value != null) ? value.length() : 0;
	}


	/**
	 *  Gets the Null attribute of the CtsqlChar object
	 *
	 * @return    The Null value
	 */
	public boolean isNull() {
		return value == null;
	}


	/**
	 *  Gets the Type attribute of the CtsqlChar object
	 *
	 * @return    The Type value
	 */
	public int getType() {
		return ProtocolType.CHAR_TYPE;
	}


	/**
	 *  Gets the AsShort attribute of the CtsqlChar object
	 *
	 * @return    The AsShort value
	 */
	public short getAsShort() {
		return (value != null) ? Short.parseShort(value) : 0;
	}


	/**
	 *  Gets the AsInteger attribute of the CtsqlChar object
	 *
	 * @return    The AsInteger value
	 */
	public int getAsInteger() {
		return (value != null) ? Integer.parseInt(value.trim()) : 0;
	}


	/**
	 *  Gets the AsDateCalendar attribute of the CtsqlChar object
	 *
	 * @return                     The AsDateCalendar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public Calendar getAsDateCalendar() throws CtsqlException {
		try {
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
			java.util.Date dt = df.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			return (value != null) ? cal : null;
		} catch (ParseException ex) {
			CtsqlExceptionManager.getManager().throwException(ex);
			return null;
		}
	}


	/**
	 *  Gets the AsTimeCalendar attribute of the CtsqlChar object
	 *
	 * @return                     The AsTimeCalendar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public Calendar getAsTimeCalendar() throws CtsqlException {
		try {
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ITALY);
			java.util.Date dt = df.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			return (value != null) ? cal : null;
		} catch (ParseException ex) {
			CtsqlExceptionManager.getManager().throwException(ex);
			return null;
		}
	}


// New Eva 18-04-2001
	/**
	 *  Gets the AsDateTimeCalendar attribute of the CtsqlChar object
	 *
	 * @return                     The AsDateTimeCalendar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public Calendar getAsDateTimeCalendar() throws CtsqlException {
		try {
			DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ITALY);
			java.util.Date dt = df.parse(value);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dt);
			return (value != null) ? cal : null;
		} catch (ParseException ex) {
			CtsqlExceptionManager.getManager().throwException(ex);
			return null;
		}
	}


// END Eva 18-04-2001

	/**
	 *  Gets the AsByte attribute of the CtsqlChar object
	 *
	 * @return    The AsByte value
	 */
	public byte[] getAsByte() {
		byte[] bytes = null;
		if(value != null){
			int len = value.length();
			bytes = new byte[len];
			for(int i = 0; i < len; i++){
				bytes[i] = (byte)value.charAt(i);
			}
		}
		return bytes;
	}


	/**
	 *  Gets the AsDouble attribute of the CtsqlChar object
	 *
	 * @return    The AsDouble value
	 */
	public double getAsDouble() {
		return (value != null) ? Double.parseDouble(value) : 0;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 */
	public byte[] store() {
		char chr;
		int idx = 0;
		int off;
		int len = ((value != null) ? value.length() : 0);
		int size;

		size = len + CtsqlSmallint.STORE_LENGTH;

		if ((buffer == null) || (buffer.length < len)) {
			buffer = new byte[size];
		}

		off = CtsqlSmallint.STORE_LENGTH;

		CtsqlSmallint.storeShort((short) (len), buffer);

		for (idx = 0; idx < len; idx++) {
			buffer[idx + off] = (byte) (value.charAt(idx));
		}

		if (isOem) {
			buffer = OemConverter.AnsiToOem(buffer);
		}
		return buffer;
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Returned Value
	 */
	public String toString() {
		return (value != null) ? value : NULL;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  buf  Description of Parameter
	 * @param  off  Description of Parameter
	 * @param  len  Description of Parameter
	 */
	private void load(byte buf[], int off, int len) {
		/*
		 *  Mod. MA-PACO 11-10-2001 Tratamiento correcto del null
		 *  value = (len == 0) ? null : new String(buf, off, len).trim();
		 */
		if (len > 0) {
			int lastNoBlank = 0;
			boolean eos = false;
			if (isOem) {
				buf = OemConverter.OemToAnsi(buf);
			}
			char[] byteBuf = new char[len];
			for(int i = 0; i < len; i++){
				byteBuf[i] = (char) buf[i + off];
				// Para evitar la propagación del negativo.
				byteBuf[i] &= 0x00ff;
				if(byteBuf[i] == '\0'){
					eos = true;
				}
				if(!eos && byteBuf[i] != ' '){
					lastNoBlank = i;
				}
			}
			if(rTrimChar && lastNoBlank >= 0 && lastNoBlank < len){
				value = new String(byteBuf, 0, lastNoBlank + 1);;
			}else{
				value = new String(byteBuf);
			}
			if (value.length() == 0 || value.charAt(0) == '\0') {
				value = null;
			}
			/*
			 *  Quitado MA 23-10-2001 Esta linea asi no sirve para nada, además
			 *  el valor ha de respetarse sin trimar (pag. 632 libro JDBC)
			 *  else{
			 *  value.trim();
			 *  }
			 */
		} else {
			value = null;
		}

		/*
		 *  End Mod. MA-PACO 11-10-2001
		 */
	}
}
