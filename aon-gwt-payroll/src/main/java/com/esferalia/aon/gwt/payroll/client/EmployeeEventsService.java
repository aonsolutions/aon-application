package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;

public interface EmployeeEventsService {

	EmployeeEventsData getEmployeeEvents(int contract); 
	
	void setEmployeeEvents(int contract, EmployeeCalendarUpdate updateInfo);
	
	
	
}
