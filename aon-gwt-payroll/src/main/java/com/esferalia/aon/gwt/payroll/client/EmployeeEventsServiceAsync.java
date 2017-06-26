package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;

import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EmployeeEventsServiceAsync {

	void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables, AsyncCallback<EmployeeEventsData> callback); 
	
	void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo, AsyncCallback<EmployeeEventsUpdate> callback);
	
}
