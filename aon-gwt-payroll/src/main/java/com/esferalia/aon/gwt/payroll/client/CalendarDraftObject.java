package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CalendarDraftObject {
	
	private EmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private Calendar calendar;
	
	public CalendarDraftObject(Integer workplaceId, EmployeesServiceAsync employeesService) {
		
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;
		
	}
	
	public void load(final AsyncCallback<CalendarDraftObject> cb) {
		if(calendar != null)
			cb.onSuccess(CalendarDraftObject.this);
		else
			getHolidayCaledar(cb);
	}
	
	private void getHolidayCaledar(final AsyncCallback<CalendarDraftObject> cb) {
		
	}

}
