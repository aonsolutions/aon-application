package com.transtools.ctsql.impl;

import com.transtools.ctsql.CtsqlException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

/**
 *  Description of the Class
 *
 *@author     eva
 *@created    May 21, 2001
 */
class CtsqlTime implements ProtocolType, com.transtools.ctsql.CtsqlTime {
	private byte[] buffer = {0, 0, 0, 0};
	private int[] timeAsArray = {0, 0, 0};
	private int value;

	private static int NULL = CtsqlInteger.NULL;
	private static String NULL_TIME = "NULL TIME";

	private final static int DAYSECS = 86400;


	/**
	 *  Constructor for the null CtsqlTime object
	 */
	public CtsqlTime() {
		value = NULL;
	}

	/**
	 *  Constructor for  CtsqlDate object
	 */
	public CtsqlTime(CtsqlTime time) {
		if(time.isNull())
			value = NULL;
		else
			value = time.getValue();
	}

	/**
	 *  Constructor for the CtsqlTime object
	 *
	 *@param  calendar  Description of Parameter
	 */
	public CtsqlTime(Calendar calendar) {
		value = hmsToInt(calendar);
	}


	/**
	 *  Constructor for the CtsqlTime object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlTime(byte[] buffer, int offset) {
		load(buffer, offset);
	}


// New Eva 18-04-2001

	/**
	 *  Sets the Value attribute of the CtsqlTime object
	 *
	 *@param  value  The new Value value
	 */
	public void setValue(int value) {
		this.value = value;
	}


	/**
	 *  Sets the AsString attribute of the CtsqlTime object
	 *
	 *@param  cad                 The new AsString value
	 *@return                     Description of the Returned Value
	 *@exception  CtsqlException  Description of Exception
	 */
	public Calendar setAsString(String cad) throws CtsqlException {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
		Calendar calendar = Calendar.getInstance(Locale.ITALY);

		try {
			java.util.Date date = df.parse(cad);
			calendar.setTime(date);
		}
		catch (ParseException pe) {
			CtsqlExceptionManager.getManager().throwException(pe);
		}
		return calendar;
	}


	/**
	 *  Gets the AsCalendar attribute of the CtsqlTime object
	 *
	 *@return    The AsCalendar value
	 */
	public Calendar getAsCalendar() {
		if (isNull()) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(Locale.ITALY);
		calendar.clear();
		intToHms(value, timeAsArray);
		calendar.set(Calendar.HOUR_OF_DAY, timeAsArray[0]);
		calendar.set(Calendar.MINUTE, timeAsArray[1]);
		calendar.set(Calendar.SECOND, timeAsArray[2]);
		return calendar;
	}


	/**
	 *  Gets the Type attribute of the CtsqlTime object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.TIME_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlTime object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return CtsqlInteger.SQL_LENGTH;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlTime object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return CtsqlInteger.STORE_LENGTH;
	}


	/**
	 *  Gets the Value attribute of the CtsqlTime object
	 *
	 *@return    The Value value
	 */
	public int getValue() {
		return value;
	}


	/**
	 *  Gets the Null attribute of the CtsqlTime object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return value == NULL;
	}


	/**
	 *  Gets the AsString attribute of the CtsqlTime object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		if (isNull()) {
			return NULL_TIME;
		}
		intToHms(value, timeAsArray);
		return Integer.toString(timeAsArray[0]) + ":" +
				Integer.toString(timeAsArray[1]) + ":" +
				Integer.toString(timeAsArray[2]);
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		CtsqlInteger.storeInt(value, buffer);
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


//	private void intToHms(int seconds, int[] timeAsArray) {
	/**
	 *  Description of the Method
	 *
	 *@param  seconds      Description of Parameter
	 *@param  timeAsArray  Description of Parameter
	 */
	public void intToHms(int seconds, int[] timeAsArray) {
		if (isNull()) {
			timeAsArray[0] = timeAsArray[1] = timeAsArray[2] = 0;
			return;
		}

		timeAsArray[0] = seconds / 3600;
		timeAsArray[1] = seconds / 60 % 60;
		timeAsArray[2] = seconds % 60;
	}


//	private int hmsToInt( Calendar calendar ) {
	/**
	 *  Description of the Method
	 *
	 *@param  calendar  Description of Parameter
	 *@return           Description of the Returned Value
	 */
	public int hmsToInt(Calendar calendar) {
		int value = NULL;

		if (calendar != null) {
			value = hmsToInt
					(
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND)
					);
		}

		return value;
	}


// END New Eva 18-04-2001

	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 */
	private void load(byte buf[], int off) {
		value = CtsqlInteger.loadInt(buf, off);
		value = (value > DAYSECS) || (value < 0) ? NULL : value;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  hour    Description of Parameter
	 *@param  minute  Description of Parameter
	 *@param  second  Description of Parameter
	 *@return         Description of the Returned Value
	 */
	private int hmsToInt(int hour, int minute, int second) {
	/* MOD Paco 4-11-2002. las 00:00:00 ya no son nulo
		if (hour == 0 && minute == 0 && second == 0) {
	*/
		if (hour == CtsqlInteger.NULL && minute == CtsqlInteger.NULL && second == CtsqlInteger.NULL) {
	/* END-MOD Paco 4-11-2002 */
			return NULL;
		}
		return (hour * 3600) + (minute * 60) + second;
	}

}
