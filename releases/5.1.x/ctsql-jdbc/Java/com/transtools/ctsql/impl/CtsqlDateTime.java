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
class CtsqlDateTime implements ProtocolType, com.transtools.ctsql.CtsqlDateTime {
	private int[] timeAsArray = {0, 0, 0};
	private int[] dateAsArray = {0, 0, 0};
	private byte[] buffer = {0, 0, 0, 0, 0, 0, 0, 0};
	private CtsqlDate ctsqlDate = null;
	private CtsqlTime ctsqlTime = null;

	private final static int DAYSECS = 86400;
	private static int NULL = CtsqlInteger.NULL;
	private static String NULL_DATETIME = "NULL DATETIME";


	/**
	 *  Constructor for the null CtsqlDateTime object
	 */
	public CtsqlDateTime() {
		ctsqlDate = new CtsqlDate();
		ctsqlTime = new CtsqlTime();
	}


	/**
	 *  Constructor for the CtsqlDateTime object
	 *
	 *@param  calendar  Description of Parameter
	 */
	public CtsqlDateTime(Calendar calendar) {
		ctsqlDate = new CtsqlDate();
		ctsqlTime = new CtsqlTime();
		ctsqlDate.setValue(ctsqlDate.dmyToInt(calendar));
		ctsqlTime.setValue(ctsqlTime.hmsToInt(calendar));
	}


	/**
	 *  Constructor for the CtsqlDateTime object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlDateTime(byte[] buffer, int offset) {
		ctsqlDate = new CtsqlDate();
		ctsqlTime = new CtsqlTime();
		load(buffer, offset);
	}

	/**
	 *  Constructor for the CtsqlDateTime object
	 *
	 *@param  date  the date value
	 */
	public CtsqlDateTime(CtsqlDate date) {
		ctsqlDate = new CtsqlDate(date);
		ctsqlTime = new CtsqlTime();
	}

	/**
	 *  Constructor for the CtsqlDateTime object
	 *
	 *@param  date  the date value
	 *@param  time  the time value
	 */
	public CtsqlDateTime(CtsqlDate date, CtsqlTime time) {
		ctsqlDate = new CtsqlDate(date);
		ctsqlTime = new CtsqlTime(time);
	}

	/**
	 *  Sets the AsString attribute of the CtsqlDateTime object
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
	 *  Gets the AsCalendar attribute of the CtsqlDateTime object
	 *
	 *@return    The AsCalendar value
	 */
	public Calendar getAsCalendar() {
		if (isNull()) {
			return null;
		}
		Calendar calendar = Calendar.getInstance(Locale.ITALY);
		calendar.clear();
		ctsqlTime.intToHms(ctsqlTime.getValue(), timeAsArray);
		calendar.set(Calendar.HOUR_OF_DAY, timeAsArray[0]);
		calendar.set(Calendar.MINUTE, timeAsArray[1]);
		calendar.set(Calendar.SECOND, timeAsArray[2]);
		ctsqlDate.intToDmy(ctsqlDate.getValue(), dateAsArray);
		calendar.set(Calendar.DATE, dateAsArray[0]);
		calendar.set(Calendar.MONTH, dateAsArray[1] - 1);
		calendar.set(Calendar.YEAR, dateAsArray[2]);

		return calendar;
	}


	/**
	 *  Gets the Type attribute of the CtsqlDateTime object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.DATETIME_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlDateTime object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return 2 * CtsqlInteger.SQL_LENGTH;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlDateTime object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return 2 * CtsqlInteger.STORE_LENGTH;
	}


	/**
	 *  Gets the Null attribute of the CtsqlDateTime object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return (ctsqlTime.isNull() && ctsqlDate.isNull());
	}


	/**
	 *  Gets the AsString attribute of the CtsqlDateTime object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		if (isNull()) {
			return NULL_DATETIME;
		}
		ctsqlTime.intToHms(ctsqlTime.getValue(), timeAsArray);
		ctsqlDate.intToDmy(ctsqlDate.getValue(), dateAsArray);
		return Integer.toString(dateAsArray[0]) + "/" +
				Integer.toString(dateAsArray[1]) + "/" +
				Integer.toString(dateAsArray[2]) +
				" " +
				Integer.toString(timeAsArray[0]) + ":" +
				Integer.toString(timeAsArray[1]) + ":" +
				Integer.toString(timeAsArray[2]);
	}

	public CtsqlDate getAsDate() {
		if(this.isNull() || this.ctsqlDate == null)
			return new CtsqlDate();
		else
			return this.ctsqlDate;
	}


	public CtsqlTime getAsTime() {
		if(this.isNull() || this.ctsqlTime == null)
			return new CtsqlTime();
		else
			return this.ctsqlTime;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Returned Value
	 */
	public byte[] store() {
		byte[] storeBuffer = {0, 0, 0, 0};
		int counter;
		CtsqlInteger.storeInt(ctsqlDate.getValue(), buffer);
		CtsqlInteger.storeInt(ctsqlTime.getValue(), storeBuffer);

		System.arraycopy(storeBuffer, 0, buffer, CtsqlInteger.STORE_LENGTH, storeBuffer.length);
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
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 */
	private void load(byte buf[], int off) {
		int valueTime;
		int valueDate;

		valueDate = CtsqlInteger.loadInt(buf, off);

		ctsqlDate.setValue(valueDate);
		ctsqlTime.setValue(CtsqlInteger.loadInt(buf, off + 4));
		valueTime = ctsqlTime.getValue();
		valueTime = (valueTime > DAYSECS) || (valueTime < 1) ? NULL : valueTime;
		ctsqlTime.setValue(valueTime);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  seconds      Description of Parameter
	 *@param  timeAsArray  Description of Parameter
	 */
	private void intToHms(int seconds, int[] timeAsArray) {
		if (isNull()) {
			timeAsArray[0] = timeAsArray[1] = timeAsArray[2] = 0;
			return;
		}
		timeAsArray[0] = seconds / 3600;
		timeAsArray[1] = seconds / 60 % 60;
		timeAsArray[2] = seconds % 60;
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
		if (hour == 0 && minute == 0 && second == 0) {
			return NULL;
		}
		return (hour * 3600) + (minute * 60) + second;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  calendar  Description of Parameter
	 *@return           Description of the Returned Value
	 */
	private int hmsToInt(Calendar calendar) {
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
}
