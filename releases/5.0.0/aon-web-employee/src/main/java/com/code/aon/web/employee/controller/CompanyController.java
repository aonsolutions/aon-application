package com.code.aon.web.employee.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.IPlanner;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.planner.core.CalendarScheduleModel;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public class CompanyController 
	extends com.code.aon.ui.company.controller.CompanyController implements IPlanner {

	/** The calendar. */
	private AonCalendar calendar;

	public CompanyController() throws ManagerBeanException {
		super();
	}

	/**
	 * Gets the calendar.
	 * 
	 * @return the calendar
	 */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

//    @Override
	public CalendarScheduleModel getCalendarScheduleModel(Object widget)
			throws CalendarException, ManagerBeanException {
		if ( getTo() == null ) {
			super.getModel().setRowIndex(0);
			onSelect(null);
		}
		if ( this.calendar == null ) {
			Company company = (Company) getTo(); 
			this.calendar = 
				ControllerUtil.getCalendarManagerBean().getCalendar( company.getCalendar() );
			company.setCalendar( this.calendar.getPrimaryKey() );
			update();
		}
	    return new CalendarScheduleModel(this.calendar);
	}

//    @Override
	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		if ( super.model == null ) {
			super.onLoad( event );
		}
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel( null );
		planner.initialize( csm, COMPANY_NAME, false );
	}

}
