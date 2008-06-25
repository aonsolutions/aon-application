package com.code.aon.web.employee.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.schedule.model.ScheduleEntry;
import org.apache.myfaces.custom.schedule.model.ScheduleModel;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.calendar.enumeration.EventCategory;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.company.resources.Resource;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.employee.util.Utils;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.planner.core.CalendarScheduleModel;
import com.code.aon.ui.util.AonUtil;

public class EmployeeController extends com.code.aon.ui.employee.controller.EmployeeController {
	
	private static final long serialVersionUID = 4312327540171952885L;

	/** Employee calendar */
	private AonCalendar calendar;

    /**
     * Return Employee calendar.
     * @return
     */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove( event );
        CalendarsController.getCalendarsController().setDirty( true );
	}

	@Override
	public void accept(ActionEvent event) {
		super.accept(event);
		CalendarsController.getCalendarsController().setDirty( true );
		super.setResourceAllowed( false );
		try {
			getCalendarScheduleModel();
		} catch (CalendarException e) {
			Utils.addMessage( "aon_employee_calendar_creation_exception", true );
            throw new AbortProcessingException(e.getMessage(), e);
		} catch (ManagerBeanException e) {
			Utils.addMessage( "aon_employee_calendar_creation_exception", true );
            throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel();
		csm.setMode( ScheduleModel.WEEK );
		planner.setMode( ScheduleModel.WEEK );
		planner.setDate( new Date() );
		planner.setScheduleModel(csm);
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> categories = new LinkedList<SelectItem>();
		for (int i = 0; i < EventCategory.values().length; i++) {
			if ( EventCategory.values()[i] != null 
					&& EventCategory.values()[i] != EventCategory.WORK 
					&& EventCategory.values()[i] != EventCategory.INCIDENCE) {
				String name = EventCategory.values()[i].getName(locale);
				SelectItem si = new SelectItem( EventCategory.values()[i], name );
				categories.add(si);
			}
		}
		planner.setCategories(categories);
		planner.setOutcome( MANAGER_BEAN_NAME );
		planner.setSpreadable( false );
		planner.setEvents( new ListDataModel( new ArrayList<ScheduleEntry>() ) );
		ControllerUtil.getWorkingTime().initialize( this.calendar, ( (Employee) getTo() ).getAgreementTime() );
		ControllerUtil.getIncidences().initialize( super.getResource(), false );
	}

    /**
     * Return Employee schedule model.
     * 
     * @return
     * @throws CalendarException 
     * @throws ManagerBeanException 
     * @throws ExpressionException 
     */
	public CalendarScheduleModel getCalendarScheduleModel() throws CalendarException, ManagerBeanException {
		Employee employee = (Employee) getTo();
		if ( employee.getCalendar() == null ) {
			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
			Resource r = super.getResource();
			if ( r.getWorkActivity() != null ) {
//	Ask for working activity calendar, if not exist creates it and returns to the employee.
				WorkActivityController wac = 
					(WorkActivityController) AonUtil.getController( WorkActivityController.MANAGER_BEAN_NAME );
				AonCalendar waCalendar = 
					wac.getCalendarScheduleModel( r.getWorkActivity() ).getCalendar();
				this.calendar = cmb.cloneCalendar( waCalendar );
			} else {
//	Ask for working place calendar, if not exist creates it and returns to the employee.
				WorkPlaceController wpc = 
					(WorkPlaceController) AonUtil.getController( WorkPlaceController.MANAGER_BEAN_NAME );
				AonCalendar wpCalendar = 
					wpc.getCalendarScheduleModel( r.getWorkPlace() ).getCalendar();
				this.calendar = cmb.cloneCalendar( wpCalendar );
			}
			String description = 
				employee.getRegistry().getName() + " " + employee.getRegistry().getSurname(); 
			this.calendar.setDescription( description );
			this.calendar.setAddSpreadEventAllowed( true );
			cmb.updateCalendar( this.calendar );
			employee.setCalendar( this.calendar.getPrimaryKey() );
			update();
		} else {
			this.calendar = CalendarHelper.getCalendar( employee.getCalendar() );
		}
        return new CalendarScheduleModel( this.calendar );
	}

}