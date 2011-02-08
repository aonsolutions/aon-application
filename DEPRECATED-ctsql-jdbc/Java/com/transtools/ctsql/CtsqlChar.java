/*
 *  Copyright 2001
 *
 *  TransTOOLs, S.A.
 *  All rights reserved
 */
package com.transtools.ctsql;

import java.util.Calendar;

/**
 *  Description of the Interface
 *
 * @author     eva
 * @created    May 21, 2001
 * @version    $Revision: 1.3 $
 */
public interface CtsqlChar extends CtsqlType {
	/**
	 *  Gets the AsString attribute of the CtsqlChar object
	 *
	 * @return    The AsString value
	 */
	public String getAsString();


	/**
	 *  Gets the AsShort attribute of the CtsqlChar object
	 *
	 * @return    The AsShort value
	 */
	public short getAsShort();


	/**
	 *  Gets the AsInteger attribute of the CtsqlChar object
	 *
	 * @return    The AsInteger value
	 */
	public int getAsInteger();


	/**
	 *  Gets the AsDateCalendar attribute of the CtsqlChar object
	 *
	 * @return                     The AsDateCalendar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public Calendar getAsDateCalendar() throws CtsqlException;


	/**
	 *  Gets the AsTimeCalendar attribute of the CtsqlChar object
	 *
	 * @return                     The AsTimeCalendar value
	 * @exception  CtsqlException  Description of Exception
	 */
	public Calendar getAsTimeCalendar() throws CtsqlException;

	/**
	 *  Gets the CtsqlChar object value as a DateTime Calendar.
	 *
	 * @return                     The Calendar value
	 * @exception  CtsqlException  if a conversion error occurs
	 */
	public Calendar getAsDateTimeCalendar() throws CtsqlException;

	/**
	 *  Gets the AsDouble attribute of the CtsqlChar object
	 *
	 * @return    The AsDouble value
	 */
	public double getAsDouble();

	/**
	 *  Gets the CtsqlChar object value as a byte array.
	 *
	 * @return    The byte array value
	 */
	public byte[] getAsByte();


	/**
	 *  Sets the AsString attribute of the CtsqlChar object
	 *
	 * @param  string  The new AsString value
	 * @return         Description of the Returned Value
	 */
	public String setAsString(String string);


	/**
	 *  Sets the AsShort attribute of the CtsqlChar object
	 *
	 * @param  sh  The new AsShort value
	 * @return     Description of the Returned Value
	 */
	public String setAsShort(short sh);


	/**
	 *  Sets the AsInteger attribute of the CtsqlChar object
	 *
	 * @param  in  The new AsInteger value
	 * @return     Description of the Returned Value
	 */
	public String setAsInteger(int in);


	/**
	 *  Sets the AsDouble attribute of the CtsqlChar object
	 *
	 * @param  d  The new AsDouble value
	 * @return    Description of the Returned Value
	 */
	public String setAsDouble(double d);


	/**
	 *  Sets the AsDateCalendar attribute of the CtsqlChar object
	 *
	 * @param  dt  The new AsDateCalendar value
	 * @return     Description of the Returned Value
	 */
	public String setAsDateCalendar(java.sql.Date dt);


	/**
	 *  Sets the AsTimeCalendar attribute of the CtsqlChar object
	 *
	 * @param  tm  The new AsTimeCalendar value
	 * @return     Description of the Returned Value
	 */
	public String setAsTimeCalendar(java.sql.Time tm);


	/**
	 *  Sets the AsBigDecimal attribute of the CtsqlChar object
	 *
	 * @param  bd  The new AsBigDecimal value
	 * @return     Description of the Returned Value
	 */
	public String setAsBigDecimal(java.math.BigDecimal bd);

}
