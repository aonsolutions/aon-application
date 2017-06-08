package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;

public interface EmployeeEventsService {

	EmployeeEventsData getEmployeeEvents(int contract); 
	
	void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo);
	
	
	
}
