package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CostDocuments extends AbstractSpinnable<IDocument> implements
		IDocument {

	private List<Cost> costs;
	private EmployeesServiceAsync employeesService;

	public CostDocuments(List<Cost> costs, EmployeesServiceAsync employeesServiceAsync) {
		this.costs = costs;
		this.employeesService = employeesServiceAsync;
		first();
	}

	@Override
	public int size() {
		return costs.size();
	}

	@Override
	public IDocument current() {
		return this;
	}

	@Override
	public void print() {
		download();
	}

	@Override
	public void download() {
		download("pdf");
	}

	@Override
	public void download(String format) {
		Cost cost = costs.get(getCurrentIndex());
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost/"
				+ cost.getMonth() + "_" + cost.getYear() + "_"
				+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId()
				+ "." + format);
		Window.open(printURL, "_blank", null);
	}

	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Cost cost = costs.get(getCurrentIndex());
		employeesService.getCostReceiptHTML(cost, zoom, callback);
	}

	@Override
	public String[] getSupportedFormats() {
		return new String[] { "xls" };
	}
	
	
	public List<Cost> getCosts() {
		return costs;
	}
	
	public void setCurrent(Cost cost){
		setCurrentIndex(costs.indexOf(cost));
	}
}