/**
 * 
 */
package com.code.aon.planner.recurrence;

import net.fortuna.ical4j.model.Recur;

import com.code.aon.planner.IRecurrence;
import com.code.aon.planner.enumeration.DayMonthLocation;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-ene-2006
 * @since 1.0
 *
 */

public class Yearly implements IRecurrence {

	/** Indica el identificador de la repetición. */
	private String id = EACH;
	private int interval = 1, dayMonthLocation, month;
	private String day = "MO";

	public Yearly() {}

	public Yearly(Recur recur) {
		this.interval = (recur.getInterval() > 0)? recur.getInterval(): 1;
		if (this.interval > 1) {
			this.id = RANGE;
		}
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the interval.
	 */
	public int getInterval() {
		return interval;
	}
	/**
	 * @param interval The interval to set.
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
	/**
	 * @return Returns the day.
	 */
	public String getDay() {
		return day;
	}
	/**
	 * @param day The day to set.
	 */
	public void setDay(String day) {
		this.day = day;
	}
	/**
	 * @return Returns the dayMonthLocation.
	 */
	public int getDayMonthLocation() {
		return dayMonthLocation;
	}
	/**
	 * @param dayMonthLocation The dayMonthLocation to set.
	 */
	public void setDayMonthLocation(int dayMonthLocation) {
		this.dayMonthLocation = dayMonthLocation;
	}
	/**
	 * @return Returns the month.
	 */
	public int getMonth() {
		return month;
	}
	/**
	 * @param month The month to set.
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.planner.recurrence.IRecurrence#toRRULEString()
	 */
	public String toRRULEString() {
		if (id.equals(EACH)) {
			String[] params = {Recur.YEARLY, ";INTERVAL=" + this.interval, EMPTY_STRING};
			return RRULE.format(params);
		} else {
			int index = (this.dayMonthLocation == DayMonthLocation.getList().size() - 1)? -1: this.dayMonthLocation + 1;
			String[] params = {Recur.MONTHLY, ";BYMONTH=" + this.month, ";BYDAY=" + index + this.day};
			return RRULE.format(params);
		}
	}

}
