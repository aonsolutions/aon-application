/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author rtrepiana
 * 
 */
public class EnterpriseSite implements EntryPoint, Employees.Listener {

	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();

		@Source("aon-menuBar.png")
		ImageResource menuBar();

		@Source("checkyes.png")
		ImageResource checkYes();
		
		@Source("button.png")
		ImageResource button();
		
	}

	interface Binder extends UiBinder<Widget, EnterpriseSite> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Salaries salaries;
	@UiField
	Employees employees;
	@UiField
	StackLayoutPanel explorer;
	@UiField
	DetailPanel detailPanel;
	@UiField
	SplitLayoutPanel splitLayoutPanel;

	private JSF jsf;
	private Cost cost;
	private Salary salary;
	private Documents documents;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	@Override
	public void onModuleLoad() {
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		salaries.setDetailPanel(detailPanel);

		jsf = new JSF();
		cost = new Cost();
		salary = new Salary();
		documents = new Documents();
		employees.addListener(this);
	}

	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/enterprise.jsf");
		detailPanel.setWidget(jsf);
	}

	@Override
	public void onWorkplaceSelected(Workplace workplace) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/workplace.jsf"
				+ "?controller=payrollWorkPlace&payrollWorkPlace_id="
				+ workplace.getId());
		detailPanel.setWidget(jsf);
	}

	@Override
	public void onEmployeeSelected(Employee employee) {
		jsf.setUrl(GWT.getHostPageBaseURL()
				+ "/com/esferalia/aon/gwt/payroll/facelet/enterprise/contract.jsf"
				+ "?controller=contract&contract_id=" + employee.getId()
				+ "&controller=person&person_id=" + employee.getPerson());
		detailPanel.setWidget(jsf);
	}

	
	@Override
	public void onSalariesSelected(SalaryDocuments docs) {
		detailPanel.setWidget(salary);
		salary.setSalaryDocuments(docs);
	}
	
	@Override
	public void onCostsSelected(CostDocuments docs) {
		detailPanel.setWidget(cost);
		cost.setCostDocuments(docs);
	}

	@Override
	public void onDocumentsSelected(ISpinnable<IDocument> docs) {
		detailPanel.setWidget(documents);
		documents.setDocuments(docs);
	}
	
	@Override
	public void onActivitySelected(Activity activity) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSalaryDratSelected( SalaryDraftDocument salaryDraftDocument) {
		// TODO Auto-generated method stub
	}
}
