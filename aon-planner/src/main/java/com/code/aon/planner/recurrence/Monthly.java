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

public class Monthly implements IRecurrence {

	/** Indica el identificador de la repetición. */
	private String id = EACH;
	private int interval = 1, rangeInterval = 1, dayMonthLocation;
	private String day = "MO";

	public Monthly() {}

	public Monthly(Recur recur) {
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
	 * @return Returns the rangeInterval.
	 */
	public int getRangeInterval() {
		return rangeInterval;
	}
	/**
	 * @param rangeInterval The rangeInterval to set.
	 */
	public void setRangeInterval(int rangeInterval) {
		this.rangeInterval = rangeInterval;
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

	/* (non-Javadoc)
	 * @see com.code.aon.planner.recurrence.IRecurrence#toRRULEString()
	 */
	public String toRRULEString() {
		if (id.equals(EACH)) {
			String[] params = {Recur.MONTHLY, ";INTERVAL=" + this.interval, EMPTY_STRING};
			return RRULE.format(params);
		} else {
			int index = (this.dayMonthLocation == DayMonthLocation.getList().size() - 1)? -1: this.dayMonthLocation + 1;
			String[] params = {Recur.MONTHLY, ";INTERVAL=" + this.rangeInterval, ";BYDAY=" + index + this.day};
			return RRULE.format(params);
		}
	}

}
