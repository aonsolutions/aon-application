package com.code.aon.ui.planner;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.calendar.CalendarUtil;

import com.code.aon.planner.enumeration.Day;
import com.code.aon.planner.enumeration.DayMonthLocation;
import com.code.aon.planner.enumeration.Month;
import com.code.aon.planner.recurrence.Daily;
import com.code.aon.planner.recurrence.IRecurrence;
import com.code.aon.planner.recurrence.Monthly;
import com.code.aon.planner.recurrence.Weekly;
import com.code.aon.planner.recurrence.Yearly;
import com.code.aon.planner.util.PlannerUtil;

import net.fortuna.ical4j.model.Recur;

public class Recurrence implements Serializable {

	private Recur recur;
	private String untilType = IRecurrence.WITHOUT;

	private Daily daily = new Daily();
	private Weekly weekly = new Weekly();
	private Monthly monthly = new Monthly();
	private Yearly yearly = new Yearly();
	
	/**
	 * @param recur
	 */
	public Recurrence(Recur recur) {
		this.recur = recur;
		init();
	}

	/**
	 * @return Returns the recur.
	 */
	public Recur getRecur() {
		return recur;
	}
	/**
	 * @param recur The recur to set.
	 */
	public void setRecur(Recur recur) {
		this.recur = recur;
	}

	/**
	 * @return Returns the untilType.
	 */
	public String getUntilType() {
		return this.untilType = (recur.getCount() == -1 && recur.getUntil() == null)? IRecurrence.WITHOUT: (recur.getCount() > -1)? IRecurrence.COUNT: IRecurrence.UNTILDATE;
	}
	/**
	 * @param untilType The untilType to set.
	 */
	public void setUntilType(String untilType) {
		this.untilType = untilType;
		if (this.untilType.equals(IRecurrence.WITHOUT)) {
			setCount(-1);
			setUntil(null);
		}
	}

	/**
	 * @return Returns the count.
	 */
	public int getCount() {
		return this.recur.getCount();
	}

	/**
	 * @param until The until to set.
	 */
	public void setCount(int count) {
		this.recur.setCount(count);
	}

	/**
	 * @return Returns the until.
	 */
	public Date getUntil() {
		return (this.recur.getUntil() != null)? CalendarUtil.getDate(this.recur.getUntil()): null;
	}

	/**
	 * @param until The until to set.
	 */
	public void setUntil(Date until) {
		if (until != null && this.untilType.equals(IRecurrence.UNTILDATE))
			this.recur.setUntil(CalendarUtil.getICalDate(until));
	}

    /**
	 * @return Returns the daily.
	 */
	public Daily getDaily() {
		return daily;
	}
	/**
	 * @param daily The daily to set.
	 */
	public void setDaily(Daily daily) {
		this.daily = daily;
	}
	/**
	 * @return Returns the monthly.
	 */
	public Monthly getMonthly() {
		return monthly;
	}
	/**
	 * @param monthly The monthly to set.
	 */
	public void setMonthly(Monthly monthly) {
		this.monthly = monthly;
	}
	/**
	 * @return Returns the weekly.
	 */
	public Weekly getWeekly() {
		return weekly;
	}
	/**
	 * @param weekly The weekly to set.
	 */
	public void setWeekly(Weekly weekly) {
		this.weekly = weekly;
	}
	/**
	 * @return Returns the yearly.
	 */
	public Yearly getYearly() {
		return yearly;
	}
	/**
	 * @param yearly The yearly to set.
	 */
	public void setYearly(Yearly yearly) {
		this.yearly = yearly;
	}

	/**
     * Devuelve una lista con los días de la semana.
     * 
     * @return
     */
    public List getDays() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        List c = Day.getList();
        Iterator iter = c.iterator();
        while (iter.hasNext()){
            Day type = (Day) iter.next();
            SelectItem item = new SelectItem(type.getAcronym(), type.getName(locale));
            types.add( item );
        }
        return types;
    }

    /**
     * Devuelve una lista con la posición del día de la semana dentro del mes.
     * 
     * @return
     */
    public List getDayMonthLocations() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        List c = DayMonthLocation.getList();
        Iterator iter = c.iterator();
        while (iter.hasNext()){
            DayMonthLocation type = (DayMonthLocation) iter.next();
            SelectItem item = new SelectItem(type.getValue(), type.getName(locale));
            types.add( item );
        }
        return types;
    }

    /**
     * Devuelve una lista con los meses del año.
     * 
     * @return
     */
    public List getMonths() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        List c = Month.getList();
        Iterator iter = c.iterator();
        while (iter.hasNext()){
            Month type = (Month) iter.next();
            SelectItem item = new SelectItem(type.getValue(), type.getName(locale));
            types.add( item );
        }
        return types;
    }

    /**
     * Devuelve la fecha de finalización del Evento.
     * 
     * @param start
     * @return
     * @throws ParseException
     */
    public Date getEndDate(Date start, Date end) throws ParseException {
		Calendar cal = Calendar.getInstance();
    	cal.setTime(end);
		if (getUntilType().equals(IRecurrence.COUNT)) {
			cal.setTime(start);
			cal.add( Calendar.DATE, getCount() );
			return cal.getTime();
		}
		if (getUntilType().equals(IRecurrence.UNTILDATE)) {
			Calendar untilCalendar = Calendar.getInstance();
			untilCalendar.setTime( getUntil() );
			untilCalendar.set( Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) );
			untilCalendar.set( Calendar.MINUTE, cal.get(Calendar.MINUTE) );
			return untilCalendar.getTime();
		}
		Calendar sc = Calendar.getInstance();
		sc.setTime(start);
		cal.set( sc.get(Calendar.YEAR), sc.get(Calendar.MONTH), sc.get(Calendar.DATE));
		return cal.getTime();
    }

    //TODO Generar la RRULE de cada periodicidad.
    public String getRRule() {
    	String recurrenceInterval = PlannerUtil.EMPTY_STRING;
    	if (getUntilType().equals(IRecurrence.COUNT))
    		recurrenceInterval = ";COUNT=" + this.recur.getCount();
    	if (getUntilType().equals(IRecurrence.UNTILDATE))
    		recurrenceInterval = ";UNTIL=" + CalendarUtil.getICalDate(this.recur.getUntil());
    	if (this.recur.getFrequency().equals(Recur.DAILY)) {
    		String[] params = {Recur.DAILY, 
    							";INTERVAL=" + this.daily.getInterval(), 
    							recurrenceInterval, 
    							PlannerUtil.EMPTY_STRING, PlannerUtil.EMPTY_STRING};
    		return IRecurrence.RRULE.format(params);
    	}
    	if (this.recur.getFrequency().equals(Recur.WEEKLY)) {
    		String[] params = {Recur.WEEKLY, 
    							";INTERVAL=" + this.weekly.getInterval(), 
    							recurrenceInterval, 
    							PlannerUtil.EMPTY_STRING, 
    							";BYDAY=" + this.weekly.getByDay()};
    		return IRecurrence.RRULE.format(params);
    	}
    	if (this.recur.getFrequency().equals(Recur.MONTHLY)) {
    		if (this.monthly.getId().equals(IRecurrence.EACH)) {
        		String[] params = {Recur.MONTHLY, 
							";INTERVAL=" + this.monthly.getInterval(), 
							recurrenceInterval, 
							PlannerUtil.EMPTY_STRING, PlannerUtil.EMPTY_STRING};
    			return IRecurrence.RRULE.format(params);
    		} else {
    			int index = (this.monthly.getDayMonthLocation() == DayMonthLocation.getList().size() - 1)? -1: this.monthly.getDayMonthLocation() + 1;
    			String[] params = {Recur.MONTHLY, 
    								";INTERVAL=" + this.monthly.getRangeInterval(), 
    								recurrenceInterval, 
    								PlannerUtil.EMPTY_STRING, ";BYDAY=" + index + this.monthly.getDay()};
    			return IRecurrence.RRULE.format(params);
    		}
    	}
		if (this.yearly.getId().equals(IRecurrence.EACH)) {
    		String[] params = {Recur.YEARLY, 
								";INTERVAL=" + this.yearly.getInterval(), 
								recurrenceInterval, 
								PlannerUtil.EMPTY_STRING, PlannerUtil.EMPTY_STRING};
			return IRecurrence.RRULE.format(params);
		} else {
			int index = (this.yearly.getDayMonthLocation() == DayMonthLocation.getList().size() - 1)? -1: this.yearly.getDayMonthLocation() + 1;
			String[] params = {Recur.YEARLY, 
								";INTERVAL=" + this.yearly.getInterval(), 
								recurrenceInterval, 
								";BYMONTH=" + this.yearly.getMonth(), 
								";BYDAY=" + index + this.yearly.getDay()};
			return IRecurrence.RRULE.format(params);
		}
    }

    //TODO
    private void init() {
    	if (this.recur.getFrequency().equals(Recur.DAILY)) {
    		this.daily = new Daily(this.recur);
    	}
    	if (this.recur.getFrequency().equals(Recur.WEEKLY)) {
    		this.weekly = new Weekly(this.recur);
    	}
    	if (this.recur.getFrequency().equals(Recur.MONTHLY)) {
    		this.monthly = new Monthly(this.recur);
    	}
    	if (this.recur.getFrequency().equals(Recur.YEARLY)) {
    		this.yearly = new Yearly(this.recur);
    	}
    	// Construye la frecuencia asociada al atributo Recur, para despues visualizarlo.
    }
}
