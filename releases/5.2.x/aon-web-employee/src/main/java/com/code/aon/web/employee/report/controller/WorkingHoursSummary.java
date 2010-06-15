/**
 * 
 */
package com.code.aon.web.employee.report.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.PeriodList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Duration;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.CalendarUtil;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.planner.core.IncidenceType;
import com.code.aon.registry.Registry;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.planner.util.PlannerUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/01/2007
 *
 */
@SuppressWarnings("unchecked")
public class WorkingHoursSummary {

	private List employees;
	private Employee employee;
	private Integer year;
	private double hours;
	private double incidenceEqual;
	private double incidencePlus;
	private double incidenceMinus;

	/**
	 * @return the employee
	 */
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * @param employee the employee to set
	 */
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the hours
	 */
	public double getHours() {
		return hours;
	}

	/**
	 * @param hours the hours to set
	 */
	public void setHours(double hours) {
		this.hours = hours;
	}

	/**
	 * @return the incidenceEqual
	 */
	public double getIncidenceEqual() {
		return incidenceEqual;
	}

	/**
	 * @param incidenceEqual the incidenceEqual to set
	 */
	public void setIncidenceEqual(double incidenceEqual) {
		this.incidenceEqual = incidenceEqual;
	}

	/**
	 * @return the incidenceMinus
	 */
	public double getIncidenceMinus() {
		return incidenceMinus;
	}

	/**
	 * @param incidenceMinus the incidenceMinus to set
	 */
	public void setIncidenceMinus(double incidenceMinus) {
		this.incidenceMinus = incidenceMinus;
	}

	/**
	 * @return the incidencePlus
	 */
	public double getIncidencePlus() {
		return incidencePlus;
	}

	/**
	 * @param incidencePlus the incidencePlus to set
	 */
	public void setIncidencePlus(double incidencePlus) {
		this.incidencePlus = incidencePlus;
	}

	/**
	 * @return the employee list
	 * @throws ManagerBeanException 
	 */
	public List<SelectItem> getEmployees() {
		List<SelectItem> l = new ArrayList<SelectItem>();
		l.add( new SelectItem( 0, PlannerUtil.EMPTY_STRING ) );
		for (Iterator iter = this.employees.iterator(); iter.hasNext();) {
			Employee e = (Employee) iter.next();
			String name = e.getRegistry().getName() + " " + e.getRegistry().getSurname();
			SelectItem item = new SelectItem( e.getId(), name );
			l.add(item);
		}
		return l;
	}

	public void onLoad(MenuEvent event) throws ManagerBeanException {
		this.employee = new Employee();
		this.employee.setRegistry( new Registry() );
		initHours();
		IManagerBean bean = BeanManager.getManagerBean(Employee.class);
		this.employees = bean.getList(null);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		this.year = calendar.get( Calendar.YEAR );
	}

	/**
     * @param event
     */
	public void employeeChanged(ValueChangeEvent event) {
		initHours();
		Integer index = (Integer) event.getNewValue();
		for (Iterator iter = this.employees.iterator(); iter.hasNext();) {
			Employee e = (Employee) iter.next();
			if ( e.getId().equals( index ) ) {
				this.employee =  e;
				return;
			}
		}
	}

	public void onReportHours(ActionEvent event) throws ManagerBeanException, CalendarException {
		AonCalendar aonCalendar = CalendarHelper.getCalendar( this.employee.getCalendar() );
		if ( aonCalendar != null ) {
			loadIncidenceTypes();
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime( date );
			if ( cal.get( Calendar.YEAR ) != this.year ) {
				cal.set( this.year, 11, 31 );
				date = cal.getTime();
			}
			cal.add( Calendar.DATE, 1);
			hours = 
				PlannerUtil.calcYearlyHours( PlannerUtil.getYearlyComponentList( aonCalendar ), cal.getTime() );
			computeIncidences( aonCalendar, date );
		}
	}

	private void computeIncidences(AonCalendar aonCalendar, Date date) throws ManagerBeanException {
		ComponentList list = aonCalendar.getVEvents( EventCategory.INCIDENCE );
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			VEvent event = (VEvent) iter.next();
			double duration = 0.0;
        	String desc = event.getProperties().getProperty(Property.DESCRIPTION).getValue();
	    	Date start = CalendarUtil.getDate( event.getStartDate().getDate() );
	    	Duration dur = (Duration) event.getProperties().getProperty(Property.DURATION);
	    	if ( dur != null ) {
	    		if ( dur.getDuration().getDays() > 0 )
	    			duration = PlannerUtil.calcWorkingHours( aonCalendar, start );
	    		else
	    			duration = dur.getDuration().getHours() + (dur.getDuration().getMinutes()/60);
	    	} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime( start );
				int startDate = calendar.get( Calendar.DATE ) - 1;
				calendar.setTime( date );
				int days = calendar.get( Calendar.DATE ) - startDate;
				Period period = 
					new Period( (DateTime) event.getStartDate().getDate(), new Dur( days,0,0,0 ) );
				PeriodList pl = aonCalendar.getWorkingTime( period );
				for (Iterator plIter = pl.iterator(); plIter.hasNext();) {
					Period p = (Period) plIter.next();
	    			duration += p.getDuration().getHours() + (p.getDuration().getMinutes()/60);
				}
	    	}
        	int index = desc.indexOf( ')' );
        	Integer id = Integer.parseInt( desc.substring( 1, index ) );
			switch ( this.incidenceTypes[ id ] ) {
			case 1:
				this.incidencePlus += duration;
				break;
			case -1:
				this.incidenceMinus += duration;
				break;
			default:
				this.incidenceEqual += duration;
				break;
			}
		}
	}

	Integer[] incidenceTypes;
	private void loadIncidenceTypes() throws ManagerBeanException {
		if ( incidenceTypes == null ) {
			IManagerBean bean = BeanManager.getManagerBean( IncidenceType.class );
			List list = bean.getList(null);
			Integer minCapacity = ( (IncidenceType) list.get( list.size() - 1 ) ).getId();
			this.incidenceTypes = new Integer[minCapacity + 1];
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				IncidenceType type = (IncidenceType) iter.next();
				this.incidenceTypes[ type.getId() ] = type.getCompute();
			}
		}
	}

    private void initHours() {
		this.hours = 0;
		this.incidenceEqual = 0;
		this.incidencePlus = 0;
		this.incidenceMinus = 0;
	}

}
