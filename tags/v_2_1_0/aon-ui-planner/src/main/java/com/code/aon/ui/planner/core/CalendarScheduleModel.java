/*
 * Created on 10-nov-2005
 *
 */
package com.code.aon.ui.planner.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.myfaces.custom.schedule.model.AbstractScheduleModel;
import org.apache.myfaces.custom.schedule.model.Day;
import org.apache.myfaces.custom.schedule.model.ScheduleEntry;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;

import com.code.aon.planner.core.EventCategoryFilter;
import com.code.aon.ui.planner.util.PlannerUtil;

public class CalendarScheduleModel extends AbstractScheduleModel {

    /** Conjunto de Calendario(s) con los eventos y alarmas. */
	protected Set<AonCalendar> calendars = new HashSet<AonCalendar>();
    /** Filtro donde se indican las categorias a visualizar en el Planificador. */
    private EventCategoryFilter filter;
    /** Intervalo de tiempo destinado a la solicitud de horas libres. Por defecto 1 hora. */
    private Dur interval = new Dur(0, 1, 0, 0);
//    /** Clave primaria del calendario seleccionado. */
//    protected Integer selected;
    
	public CalendarScheduleModel() {
		super();
		this.filter = new EventCategoryFilter();
		this.filter.add(EventCategory.APPOINTMENT);
		this.filter.add(EventCategory.INCIDENCE);
	}

    /**
     * 
     * @param calendar
     */
	public CalendarScheduleModel(AonCalendar calendar) {
		this();
		calendars.add(calendar);
//		this.selected = calendar.getOwnerId();
	}

    /**
     * Recupera el calendario en curso.
     * 
     * @return Returns the calendar.
     */
    public AonCalendar getCalendar() {
//    	Iterator iter = this.calendars.iterator();
//    	while (iter.hasNext()) {
//			AonCalendar calendar = (AonCalendar) iter.next();
//			if (calendar.getOwnerId().equals(this.selected))
//				return calendar;
//		}
        return (this.calendars.size() > 0)? (AonCalendar)calendars.iterator().next(): null;
    }

    /**
     * @param calendar The calendar to set.
     */
    public void setCalendar(Set<AonCalendar> calendars) {
        this.calendars = calendars;
    }

    /**
     * Añade un calendario al conjunto.
     * 
     * @param calendar
     */
    public void addAonCalendar(AonCalendar calendar) {
    	calendars.add(calendar);
//    	this.selected = calendar.getOwnerId();
    }

    /**
     * @return Returns the filter.
     */
    public EventCategoryFilter getFilter() {
        return filter;
    }

    /**
     * @param filter The filter to set.
     */
    public void setFilter(EventCategoryFilter filter) {
        this.filter = filter;
    }

    /**
     * @return Returns the interval.
     */
    public Dur getInterval() {
        return interval;
    }

    /**
     * @param interval The interval to set.
     */
    public void setInterval(Dur interval) {
        this.interval = interval;
    }

	/**
     * Creates a list of periods representing the time consumed by the specified
     * list of components.
     * 
     * @param components
     * @param rangeStart
     * @param rangeEnd
     * @return
     */
    private Collection getConsumedTime(final ComponentList components, final DateTime rangeStart, final DateTime rangeEnd) {
        ArrayList<ScheduleEntry> entries = new ArrayList<ScheduleEntry>();
        for (Iterator i = components.iterator(); i.hasNext();) {
            Component component = (Component) i.next();
            // only events consume time..
            if (component instanceof VEvent) {
            	VEvent event = (VEvent) component;
            	Iterator iter = event.getConsumedTime(rangeStart, rangeEnd).iterator();
            	int recurrence = 0;
            	while (iter.hasNext()) {
					Period period = (Period) iter.next();
					Date start = CalendarUtil.getDate(period.getStart());
					Date end = CalendarUtil.getDate(period.getEnd());
					entries.add(PlannerUtil.getScheduleEntry(event, start, end));
					recurrence++;
				}
            }
        }
        return entries;
    }

    public void addEntry(ScheduleEntry entry) {
        // TODO Auto-generated method stub
        
    }

    public void removeEntry(ScheduleEntry entry) {
        // TODO Auto-generated method stub
        
    }

    public void removeSelectedEntry() {
        // TODO Auto-generated method stub
        
    }

//	******* AbstractScheduleModel class methods *********************************************** 
	@SuppressWarnings("unchecked")
	protected Collection loadEntries(Date startDate, Date endDate) {
//	TODO Permitir un filtro de EventCategory con los eventos que cada aplicación requiera.
		ComponentList list = new ComponentList();
		for( EventCategory _enum : EventCategory.values() ) {
			if ( filter.accept( _enum.ordinal() ) ) {
				ComponentList events = getCalendar().getVEvents( _enum ); 
				list.addAll(events.subList(0, events.size()));
			}
    	}
		DateTime start = CalendarUtil.getICalDateTime(startDate, true);
		DateTime end = CalendarUtil.getICalDateTime(endDate, true);
		return getConsumedTime(list, start, end);
    }

    protected void loadDayAttributes(Day day) {
        if (day == null)
            return;

        Component c = PlannerUtil.isSpecialDay( getCalendar().getEventsInDay(day.getDate()) ); 
        if ( c != null ) {
            day.setSpecialDayName( c.getProperties().getProperty(Property.SUMMARY).getValue() );
            day.setWorkingDay(false);
        } else {
            day.setSpecialDayName(null);
            day.setWorkingDay(true);
        }
    }
//	******* AbstractScheduleModel class methods *********************************************** 
}
