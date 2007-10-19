package com.code.aon.calendar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VFreeBusy;
import net.fortuna.ical4j.model.property.FreeBusy;
import net.fortuna.ical4j.model.property.Transp;

import com.code.aon.calendar.enumeration.EventCategory;

public class AonCalendar {

	private Calendar calendar;
	/** Año del calendario. */
	private int year;
	/** Identificador del calendario en la Base de Datos.*/
	private Integer primaryKey;
	/** Descripción del calendario en la Base de Datos.*/
	private String description;
	/** Indica si se pueden añadir eventos propagados desde el padre.*/
	private boolean addSpreadEventAllowed;

	public AonCalendar(Calendar calendar, int year) {
		this.calendar = calendar;
		this.year = year;
	}

	/**
	 * Devuelve el identificador único.
	 * 
	 * @return Devuelve el identificador único.
	 */
	public Integer getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Asigna el identificador único.
	 * 
	 * @param primaryKey, identificador único.
	 */
	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the addSpreadEventAllowed
	 */
	public boolean isAddSpreadEventAllowed() {
		return addSpreadEventAllowed;
	}

	/**
	 * @param addSpreadEventAllowed the addSpreadEventAllowed to set
	 */
	public void setAddSpreadEventAllowed(boolean addSpreadEventAllowed) {
		this.addSpreadEventAllowed = addSpreadEventAllowed;
	}

	/**
	 * @return Returns the year.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @return Returns the calendar.
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	@SuppressWarnings("unchecked")
	public void merge(Calendar calendar) {
		this.calendar.getComponents().addAll(calendar.getComponents());
	}

	public ComponentList getEventsInDay( java.util.Date date ) {
    	ComponentList result = new ComponentList();
    	Date rangeStart = CalendarUtil.getICalDate(date);
		java.util.Calendar cal = java.util.Calendar.getInstance();
    	cal.setTime( date );
    	cal.add( java.util.Calendar.DATE, 1 );
    	Date rangeEnd = CalendarUtil.getICalDate(cal.getTime());
    	ComponentList components = this.calendar.getComponents();
    	for( int i = 0; i < components.size(); i++ ) {
    		Component component = (Component) components.get(i);
    		if ( Component.VEVENT.equals(component.getName()) ) {
    			VEvent event = (VEvent) component;
    			PeriodList periodList = event.getConsumedTime(rangeStart, rangeEnd );
    			if (! periodList.isEmpty() ) {
    				result.add( event );
    			}
    		}
    	}
    	return result;
    }

	public boolean hasEvents( java.util.Date from, java.util.Date to, EventCategory[] ec, String id ) {
    	Date rangeStart = CalendarUtil.getICalDateTime(from, false);
    	Date rangeEnd = CalendarUtil.getICalDateTime(to, false);
    	ComponentList components = this.calendar.getComponents();
    	if (ec != null)
    		components = getVEvents(ec);
    	for( int i = 0; i < components.size(); i++ ) {
    		Component component = (Component) components.get(i);
			Property prodId = component.getProperties().getProperty(Property.PRODID);
    		if ( Component.VEVENT.equals(component.getName()) && prodId != null && !prodId.getValue().equals(id) ) {
    			VEvent event = (VEvent) component;
    			PeriodList periodList = event.getConsumedTime(rangeStart, rangeEnd );
    			if ( !periodList.isEmpty() ) {
    				return true;
    			}
    		}
    	}
    	return false;
    }

	public ComponentList getVEvents( java.util.Date date, EventCategory ec ) {
    	ComponentList result = new ComponentList();
    	Date rangeStart = CalendarUtil.getICalDate(date);
		java.util.Calendar cal = java.util.Calendar.getInstance();
    	cal.setTime( date );
    	cal.add( java.util.Calendar.DATE, 1 );
    	Date rangeEnd = CalendarUtil.getICalDate(cal.getTime());
    	ComponentList components = this.calendar.getComponents();
    	for( int i = 0; i < components.size(); i++ ) {
    		Component component = (Component) components.get(i);
    		if ( Component.VEVENT.equals(component.getName()) 
    				&& ec == CalendarUtil.getCategory((VEvent) component) ) {
    			VEvent event = (VEvent) component;
    			PeriodList periodList = event.getConsumedTime(rangeStart, rangeEnd );
    			if (! periodList.isEmpty() ) {
    				result.add( event );
    			}
    		}
    	}
    	return result;
    }

	public ComponentList getVEvents( java.util.Date startDate, java.util.Date endDate, EventCategory ec ) {
    	ComponentList result = new ComponentList();
    	Date rangeStart = CalendarUtil.getICalDate( startDate );
    	Date rangeEnd = CalendarUtil.getICalDate( endDate );
    	ComponentList components = this.calendar.getComponents();
    	for( int i = 0; i < components.size(); i++ ) {
    		Component component = (Component) components.get(i);
    		if ( Component.VEVENT.equals(component.getName()) 
    				&& ec == CalendarUtil.getCategory((VEvent) component) ) {
    			VEvent event = (VEvent) component;
    			PeriodList periodList = event.getConsumedTime(rangeStart, rangeEnd );
    			if (! periodList.isEmpty() ) {
    				result.add( event );
    			}
    		}
    	}
    	return result;
    }

	public ComponentList getVEvents( EventCategory ec ) {
    	ComponentList list = new ComponentList();
    	Iterator i = calendar.getComponents().iterator();
    	while ( i.hasNext() ) {
    		Component component = (Component) i.next();
    		if ( Component.VEVENT.equals(component.getName()) ) {
    			if ( ec == CalendarUtil.getCategory((VEvent) component) ) {
    				list.add(component);
    			}
    		}
    	}
    	return list;
    }
    
	public ComponentList getVEvents( EventCategory[] ec ) {
    	ComponentList list = new ComponentList();
    	Iterator i = calendar.getComponents().iterator();
    	while ( i.hasNext() ) {
    		Component component = (Component) i.next();
    		if ( Component.VEVENT.equals(component.getName()) &&
    				ec[ CalendarUtil.getCategory((VEvent) component).ordinal() ] != null) {
   				list.add(component);
    		}
    	}
    	return list;
    }

	public static EventCategory[] getEventCategory( ComponentList list ) {
		EventCategory[] result = null;
		if (! list.isEmpty() ) {
			ArrayList<EventCategory> categories = new ArrayList<EventCategory>();
			for( int i = 0; i < list.size(); i++ ) {
				VEvent vevent = (VEvent) list.get(i);
				EventCategory ec = CalendarUtil.getCategory(vevent);
				if ( ec != null ) {
					categories.add( ec );
				}
			}
			if (! categories.isEmpty() ) {
				result = new EventCategory[categories.size()];
				categories.toArray( result );
			}
		}
		return result;
	}

	public PeriodList getWorkingTime( Period period ) {
		PeriodList result;
		ComponentList list = getVEvents( EventCategory.WORK );
		CalendarUtil.changeProperty( list, Transp.OPAQUE );

		VFreeBusy request = new VFreeBusy( period.getStart(), period.getEnd() );
		VFreeBusy busyTime = new VFreeBusy( request, list );
        FreeBusy fg = (FreeBusy) busyTime.getProperties().getProperty( Property.FREEBUSY );
        if ( fg != null ) {
        	result = fg.getPeriods();
        } else {
        	result = new PeriodList();
        }
        
		CalendarUtil.changeProperty( list, Transp.TRANSPARENT );
		
		return result;
	}

	public PeriodList getBusyTime( Period period ) {
		PeriodList time = null;
		
        VFreeBusy request = new VFreeBusy( period.getStart(), period.getEnd() );

        VFreeBusy freeBusy = new VFreeBusy(request, this.calendar.getComponents() );
        
        FreeBusy fg = (FreeBusy) freeBusy.getProperties().getProperty( Property.FREEBUSY );
        if ( fg != null ) {
        	time = fg.getPeriods();
        }

		return time;
	}

	public PeriodList getFreeTime( Period period, Dur duration ) {
		PeriodList time = null;
		
        VFreeBusy requestFree = new VFreeBusy( period.getStart(), period.getEnd(), duration );

        VFreeBusy freeBusy = new VFreeBusy(requestFree, this.calendar.getComponents() );
        
        FreeBusy fg = (FreeBusy) freeBusy.getProperties().getProperty( Property.FREEBUSY );
        if ( fg != null ) {
        	time = fg.getPeriods();
        }

		return time;
	}

    public boolean removeEventsInDay( java.util.Date date ) {
        Date rangeStart = CalendarUtil.getICalDate(date);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime( date );
        cal.add( java.util.Calendar.DATE, 1 );
        Date rangeEnd = CalendarUtil.getICalDate(cal.getTime());
        ComponentList components = this.calendar.getComponents();
        List<Component> removalComponents = new ArrayList<Component>();
        for( int i = 0; i < components.size(); i++ ) {
            Component component = (Component) components.get(i);
            if ( Component.VEVENT.equals(component.getName()) ) {
                VEvent event = (VEvent) component;
                PeriodList periodList = event.getConsumedTime(rangeStart, rangeEnd );
                if (!periodList.isEmpty()) {
                	removalComponents.add(component);
                }
            }
        }
        Iterator iter = removalComponents.iterator();
        while (iter.hasNext()) {
			Component component = (Component) iter.next();
			components.remove(component);
		}
        return true;
    }

    /**
     * Updates event.
     * 
     * @param event
     * @return
     */
	public VEvent updateEvent(VEvent event) {
//		int index = -1;
//		String eId = event.getProperties().getProperty(Property.PRODID).getValue();
//		ComponentList components = this.calendar.getComponents();
//		for( int i = 0; i < components.size(); i++ ) {
//			Component component = (Component) components.get(i);
//			Property cId = component.getProperties().getProperty(Property.PRODID);
//			if ( Component.VEVENT.equals(component.getName()) 
//					&& cId != null && cId.getValue().equals(eId) ) {
//				index = i;
//			}
//		}
		String id = event.getProperties().getProperty(Property.PRODID).getValue();
		int index = indexOf( id );
		return updateEvent( index, event );
	}

    /**
     * Updates event at the specified position.
     * 
     * @param event
     * @return
     */
	@SuppressWarnings("unchecked")
	public VEvent updateEvent(int index, VEvent event) {
		return (index > -1)? (VEvent) this.calendar.getComponents().set( index, event ): null;
	}

	/**
     * Removes a single instance of the specified event identifier from this list.
	 * 
	 * @param id
	 * @return
	 */
	public VEvent removeEvent(String id) {
//		int index = -1;
//		ComponentList components = this.calendar.getComponents();
//		for( int i = 0; i < components.size(); i++ ) {
//			Component component = (Component) components.get(i);
//			Property cId = component.getProperties().getProperty(Property.PRODID);
//			if ( Component.VEVENT.equals(component.getName()) 
//					&& cId != null && cId.getValue().equals(id) ) {
//				index = i;
//			}
//		}
		return removeEvent( indexOf( id ) );
	}

    /**
     * Removes event at the specified position.
     * 
     * @param event
     * @return
     */
	@SuppressWarnings("unchecked")
	public VEvent removeEvent(int index) {
		return (index > -1)? (VEvent) this.calendar.getComponents().remove( index ): null;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 * @return
	 */
	public VEvent getVEvent(int index) {
		return (VEvent) this.calendar.getComponents().get( index );
	}

	/**
     * Searches for the first occurence of the given event identifier. 
	 * 
	 * @param id
	 * @return
	 */
	public int indexOf(String id) {
		ComponentList components = this.calendar.getComponents();
		for( int i = 0; i < components.size(); i++ ) {
			Component component = (Component) components.get(i);
			Property cId = component.getProperties().getProperty(Property.PRODID);
			if ( Component.VEVENT.equals(component.getName()) && cId != null && cId.getValue().equals(id) ) {
				return i;
			}
		}
		return -1;
	}

}
