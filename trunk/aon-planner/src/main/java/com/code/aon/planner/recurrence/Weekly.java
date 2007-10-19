/**
 * 
 */
package com.code.aon.planner.recurrence;

import java.util.Iterator;

import com.code.aon.planner.util.PlannerUtil;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-ene-2006
 * @since 1.0
 *
 */

public class Weekly implements IRecurrence {

	/** Indica el intervalo de repetición en número de semanas. */
	private int interval = 1;
	/** Indica los días dentro de la semana. */
    private String[] days;

    public Weekly() {}

    public Weekly(Recur recur) {
		this.interval = (recur.getInterval() > 0)? recur.getInterval(): 1;
		this.days = getDayList(recur);
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
	 * @return Returns the days.
	 */
	public String[] getDays() {
		return days;
	}
	/**
	 * @param days The days to set.
	 */
	public void setDays(String[] days) {
		this.days = days;
	}
	/* (non-Javadoc)
	 * @see com.code.aon.planner.recurrence.IRecurrence#toRRULEString()
	 */
	public String toRRULEString() {
		String[] params = {Recur.WEEKLY, PlannerUtil.EMPTY_STRING + this.interval, getByDay()};
		return RRULE.format(params);
	}

	/**
	 * Transforma la lista de días del atributo <code>days</code> en un String separado por comas.
	 * 
	 * @return
	 */
	public String getByDay() {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < days.length; i++) {
            b.append(days[i]);
            if (i+1 < days.length) {
                b.append(',');
            }
        }
        return b.toString();
	}

	/**
	 * Transforma la lista de días en el objeto <code>Recur</code> a una lista que entienden las 
	 * Java ServerFaces.
	 * 
	 * @param recur
	 * @return
	 */
	private String[] getDayList(Recur recur) {
		String[] dayList = new String[recur.getDayList().size()]; 
		Iterator iter = recur.getDayList().iterator();
		int i = 0;
		while (iter.hasNext()) {
			WeekDay element = (WeekDay) iter.next();
			dayList[i++] = element.getDay();
		}
		return dayList;
	}

}
