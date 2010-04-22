/**
 * 
 */
package com.code.aon.web.employee.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.calendar.AonCalendar;
import com.code.aon.calendar.CalendarException;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.planner.calendar.CalendarHelper;
import com.code.aon.ui.planner.CalendarManagerBean;
import com.code.aon.ui.planner.ControllerUtil;
import com.code.aon.ui.planner.IPlanner;
import com.code.aon.ui.planner.PlannerController;
import com.code.aon.ui.planner.core.CalendarScheduleModel;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 09/11/2007
 *
 */
public class WorkPlaceController 
	extends com.code.aon.ui.company.controller.WorkPlaceController implements IPlanner {

	/** The calendar. */
	private AonCalendar calendar;
	
    /**
     * Gets the calendar.
     * 
     * @return the calendar
     */
    public AonCalendar getAonCalendar() {
    	return this.calendar;
    }

    /* (non-Javadoc)
	 * @see com.code.aon.ui.planner.IPlanner#getCalendarScheduleModel(java.lang.Object)
	 */
	public CalendarScheduleModel getCalendarScheduleModel(Object widget)
			throws CalendarException, ManagerBeanException {
		WorkPlace workPlace = (WorkPlace) widget;
//		Ask for Calendar to CalendarHelper instead of CalendarManagerBean 
		if ( workPlace.getCalendar() == null ) {
			CompanyController cc = 
				(CompanyController) AonUtil.getController( CompanyController.COMPANY_NAME );
			AonCalendar cCalendar = cc.getCalendarScheduleModel( null ).getCalendar();
			CalendarManagerBean cmb = ControllerUtil.getCalendarManagerBean();
			this.calendar = cmb.cloneCalendar( cCalendar );
			this.calendar.setDescription( workPlace.getAddress().getAddress() );
			this.calendar.setAddSpreadEventAllowed( true );
			cmb.updateCalendar( this.calendar );
			workPlace.setCalendar( this.calendar.getPrimaryKey() );
			getManagerBean().update( workPlace );
		} else {
			this.calendar = CalendarHelper.getCalendar( workPlace.getCalendar() );
		}
		return new CalendarScheduleModel(this.calendar);    
	}

	public void onSchedule(ActionEvent event) throws CalendarException, ManagerBeanException {
		PlannerController planner = ControllerUtil.getPlannerController();
		CalendarScheduleModel csm = getCalendarScheduleModel( (WorkPlace) getTo() );
		planner.initialize( csm, MANAGER_BEAN_NAME, false );
	}

}
