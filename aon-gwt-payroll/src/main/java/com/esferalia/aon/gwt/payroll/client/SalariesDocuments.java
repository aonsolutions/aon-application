package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalariesDocuments extends CostDocuments {

	public SalariesDocuments(List<Cost> costs,
			DomainEmployeesServiceAsync employeesServiceAsync) {
		super(costs);
	}
	
	@Override
	public void download(String format) {
		Cost cost = getCosts().get(getCurrentIndex());
		String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/"
				+ cost.getMonth() + "_" + cost.getYear() + "_"
				+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId()
				+ "." + format);
		Window.open(printURL, "_blank", null);
	}
	
	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Cost cost = getCosts().get(getCurrentIndex());
		getEmployeesService().getSalaryReceiptHTML(cost, getSalaryTypes(), zoom, callback);
	}
	
	@Override
	public String[] getSupportedFormats() {
		return new String[] {};
	}
	
}
