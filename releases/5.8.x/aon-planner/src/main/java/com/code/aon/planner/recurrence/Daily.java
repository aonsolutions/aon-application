/**
 * 
 */
package com.code.aon.planner.recurrence;

import com.code.aon.planner.IRecurrence;

import net.fortuna.ical4j.model.Recur;

/**
 * Periodicidad Diaria.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-ene-2006
 * @since 1.0
 *
 */

public class Daily implements IRecurrence {

	/** Indica el identificador de la repetición. */
	private String id = EACH;
	/** Indica el intervalo de repetición en número de semanas. */
	private int interval = 1;

	public Daily() {}

	public Daily(Recur recur) {
		this.interval = (recur.getInterval() > 0)? recur.getInterval(): 1;
		if (this.interval > 1) {
			this.id = RANGE;
		}
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
	public void setInterval(int count) {
		this.interval = count;
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

	/* (non-Javadoc)
	 * @see com.code.aon.planner.recurrence.IRecurrence#toRRULEString()
	 */
	public String toRRULEString() {
		String[] params = {Recur.DAILY, EMPTY_STRING + this.interval};
		return RRULE.format(params);
	}
	
}
