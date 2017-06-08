package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EmployeeEventsServiceAsync {

	void getEmployeeEvents(int contract, AsyncCallback<EmployeeEventsData> callback); 
	
	void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo, AsyncCallback<EmployeeEventsUpdate> callback);
	
}
