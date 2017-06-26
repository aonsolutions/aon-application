package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;

public interface EmployeeEventsService {

	EmployeeEventsData getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables); 
	
	void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo);
	
	
	
}
