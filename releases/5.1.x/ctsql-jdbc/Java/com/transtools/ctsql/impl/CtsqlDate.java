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
class CtsqlDate implements ProtocolType, com.transtools.ctsql.CtsqlDate {

	private int value;
	private byte[] buffer = {0, 0, 0, 0};
	private int[] dateAsArray = {0, 0, 0};

	private static int NULL = CtsqlInteger.NULL;
	private static String NULL_DATE = "NULL DATE";

	private static int DAYS_4Y = 1461;
	private static int DAYS_4C = 146097;
	private static int DAYSBEGIN = 693595;
	private static int days[] = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


	/**
	 *  Constructor for the CtsqlDate object
	 *
	 *@param  buffer  Description of Parameter
	 *@param  offset  Description of Parameter
	 */
	public CtsqlDate(byte[] buffer, int offset) {
		load(buffer, offset);
	}


	/**
	 *  Constructor for the CtsqlDate object
	 *
	 *@param  calendar  Description of Parameter
	 */
	public CtsqlDate(Calendar calendar) {
		value = dmyToInt(calendar);
	}


	/**
	 *  Constructor for the null CtsqlDate object
	 */
	public CtsqlDate() {
		value = NULL;
	}


	/**
	 *  Constructor for CtsqlDate object
	 *
	 *@param  date  Description of Parameter
	 */
	public CtsqlDate(CtsqlDate date) {
		if (date.isNull()) {
			value = NULL;
		}
		else {
			value = date.getValue();
		}
	}


	/**
	 *  Sets the AsString attribute of the CtsqlDate object
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


// New Eva 18-04-2001

	/**
	 *  Sets the Value attribute of the CtsqlDate object
	 *
	 *@param  value  The new Value value
	 */
	public void setValue(int value) {
		this.value = value;
	}


	/**
	 *  Gets the AsCalendar attribute of the CtsqlDate object
	 *
	 *@return    The AsCalendar value
	 */
	public Calendar getAsCalendar() {
		if (isNull()) {
			return null;
		}

		Calendar calendar = Calendar.getInstance(Locale.ITALY);
		calendar.clear();
		intToDmy(value, dateAsArray);
		calendar.set(Calendar.DATE, dateAsArray[0]);
		calendar.set(Calendar.MONTH, dateAsArray[1] - 1);
		calendar.set(Calendar.YEAR, dateAsArray[2]);

		return calendar;
	}


	/**
	 *  Gets the Null attribute of the CtsqlDate object
	 *
	 *@return    The Null value
	 */
	public boolean isNull() {
		return value == NULL;
	}


	/**
	 *  Gets the AsString attribute of the CtsqlDate object
	 *
	 *@return    The AsString value
	 */
	public String getAsString() {
		if (isNull()) {
			return NULL_DATE;
		}
		intToDmy(value, dateAsArray);
		return Integer.toString(dateAsArray[0]) + "/" +
				Integer.toString(dateAsArray[1]) + "/" +
				Integer.toString(dateAsArray[2]);
	}


	/**
	 *  Gets the Type attribute of the CtsqlDate object
	 *
	 *@return    The Type value
	 */
	public int getType() {
		return ProtocolType.DATE_TYPE;
	}


	/**
	 *  Gets the SqlLength attribute of the CtsqlDate object
	 *
	 *@return    The SqlLength value
	 */
	public int getSqlLength() {
		return CtsqlInteger.SQL_LENGTH;
	}


	/**
	 *  Gets the StoreLength attribute of the CtsqlDate object
	 *
	 *@return    The StoreLength value
	 */
	public int getStoreLength() {
		return CtsqlInteger.STORE_LENGTH;
	}


	/**
	 *  Gets the Value attribute of the CtsqlDate object
	 *
	 *@return    The Value value
	 */
	public int getValue() {
		return value;
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


// END New Eva 18-04-2001

//	private int dmyToInt( Calendar calendar ) {
	/**
	 *  Description of the Method
	 *
	 *@param  calendar  Description of Parameter
	 *@return           Description of the Returned Value
	 */
	public int dmyToInt(Calendar calendar) {
		int value = NULL;

		if (calendar != null) {
			value = dmyToInt
					(
					calendar.get(Calendar.DATE),
					calendar.get(Calendar.MONTH) + 1,
					calendar.get(Calendar.YEAR)
					);
		}

		return value;
	}


//	private void intToDmy(int aDate, int [] dmy)	{
	/**
	 *  Description of the Method
	 *
	 *@param  aDate  Description of Parameter
	 *@param  dmy    Description of Parameter
	 */
	public void intToDmy(int aDate, int[] dmy) {
		int ncents;
		int nyears;
		int dd;
		int yy;
		int mm;

		if (aDate == NULL) {
			dmy[0] = dmy[1] = dmy[2] = 0;
			return;
		}
		aDate += DAYSBEGIN;
		aDate -= 1;
		/*
		 *  Para evitar truncado
		 */
		ncents = (aDate * 4 + 3) / DAYS_4C;
		/*
		 *  numero de siglos en aDate
		 */
		aDate -= ncents * DAYS_4C / 4;
		/*
		 *  resta dias de siglos pasados
		 */
		nyears = (aDate * 4 + 3) / DAYS_4Y;
		/*
		 *  numero de a#os del siglo
		 */
		aDate -= nyears * DAYS_4Y / 4;
		/*
		 *  resta dias del siglo
		 */
		yy = (int) (ncents * 100 + nyears + 1);
		/*
		 *  a#o en curso
		 */
		dd = (int) (aDate + 1);
		/*
		 *  dias restantes
		 */
		days[2] = ((isLeapYear(yy) == true) ? 29 : 28);
		mm = 1;
		while (dd > days[mm]) {
			dd -= days[mm++];
			/*
			 *  meses del a#o y dias que sobran
			 */
			if (mm > 12) {
				break;
			}
		}

		dmy[0] = dd;
		dmy[1] = mm;
		dmy[2] = yy;
	}


	/**
	 *  Gets the LeapYear attribute of the CtsqlDate object
	 *
	 *@param  year  Description of Parameter
	 *@return       The LeapYear value
	 */
	private boolean isLeapYear(int year) {
		if ((year & 3) != 0) {
			return false;
		}
		if (((year % 400) != 0) && ((year % 100) == 0)) {
			return false;
		}
		return true;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  buf  Description of Parameter
	 *@param  off  Description of Parameter
	 */
	private void load(byte buf[], int off) {
		value = CtsqlInteger.loadInt(buf, off);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  day    Description of Parameter
	 *@param  month  Description of Parameter
	 *@param  year   Description of Parameter
	 *@return        Description of the Returned Value
	 */
	private int dmyToInt(int day, int month, int year) {
		/*
		 *  throws CtsqlDateException
		 */
		int n;
		int l;
		int l1;

		if (day == 0 && month == 0 && year == 0) {
			return NULL;
		}

		days[2] = isLeapYear(year) == true ? 29 : 28;
//		if (year < 1 || year > 9999)
//			throw new CtsqlDateException("Bad year", EXCEPTION_YEARDATE);
//		if (month < 1 || month > 12)
//			throw new CtsqlDateException("Bad month", EXCEPTION_MONTHDATE);
//		if (day < 1 || day > days[month])
//			throw new CtsqlDateException("Bad day", EXCEPTION_DAYDATE);
		l = year - 1;
		l1 = l / 100 * DAYS_4C / 4 + l % 100 * DAYS_4Y / 4 - DAYSBEGIN;

		for (n = 1; n < month; n++) {
			l1 += days[n];
		}
		/*
		 *  numero de dias de los meses de ese a#o
		 */
		l1 += day;
		/*
		 *  numero de dias de ese mes
		 */
		return l1;
	}

}
