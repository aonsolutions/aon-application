package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CostDocuments extends AbstractSpinnable<IDocument> implements
		IDocument {

	private List<Cost> costs;
	private List<Salary.Type> types;
	private DomainEmployeesServiceAsync employeesService;

	public CostDocuments(List<Cost> costs,
			DomainEmployeesServiceAsync employeesServiceAsync) {
		this(costs, new ArrayList<Salary.Type>(Arrays.asList(Salary.Type
				.values())), employeesServiceAsync);
	}

	CostDocuments(List<Cost> costs, List<Salary.Type> types,
			DomainEmployeesServiceAsync employeesServiceAsync) {
		this.costs = costs;
		this.types = types;
		this.employeesService = employeesServiceAsync;
		last();
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
		String printURL = URL.encode(GWT.getModuleBaseURL() + "cost_pdf/"
				+ cost.getMonth() + "_" + cost.getYear() + "_"
				+ cost.getEnterpriseId() + "_" + cost.getWorkplaceId() + "."
				+ format);
		Window.open(printURL, "_blank", null);
	}

	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Cost cost = costs.get(getCurrentIndex());
		employeesService.getCostReceiptHTML(cost,
				getSalaryTypes(), zoom, callback);
	}

	public void getSLDAsHTML(int zoom, AsyncCallback<String> callback) {
		Cost cost = costs.get(getCurrentIndex());
		employeesService.getSLDCalcReceiptHTML(cost,
				getSalaryTypes(), zoom, callback);
	}

	@Override
	public String[] getSupportedFormats() {
		return new String[] { "xls" };
	}

	public List<Cost> getCosts() {
		return costs;
	}
	
	public Cost geCurrentCost() {
		return costs.get(getCurrentIndex());
	}

	public void addType(Salary.Type type) {
		types.add(type);
	}

	public void removeType(Salary.Type type) {
		types.remove(type);
	}

	public boolean containsType(Salary.Type type) {
		return types.contains(type);
	}

	// ------------------------------------------------------ protected methods
	
	Salary.Type [] getSalaryTypes() {
		return types.toArray(new Salary.Type[types.size()]);
	}
	
	protected DomainEmployeesServiceAsync getEmployeesService() {
		return employeesService;
	}

}