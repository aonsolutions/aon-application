package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EmployeeTree implements EntryPoint, Employees.Listener {
	
	
	

	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();

		@Source("warn.png")
		ImageResource warn();

		@Source("aon-menuBar.png")
		ImageResource menuBar();

		@Source("checkyes.png")
		ImageResource checkYes();

		@Source("button.png")
		ImageResource button();

		@Source("public.png")
		ImageResource publiC();
		
		@Source("private.png")
		ImageResource privatE();

		@Source("protected.png")
		ImageResource protecteD();
	}

	class NewEmployeeCommand implements ScheduledCommand{
		@Override
		public void execute() {
			// TODO Auto-generated method stub
			
		}
	}
	
	class WorkplaceContextMenu extends ContextMenu {
		public WorkplaceContextMenu() {
			super();
			addItem("Nuevo Contrato", new NewEmployeeCommand());
		}
	}
	
	interface Binder extends UiBinder<Widget, EmployeeTree> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Employees employees;
	@UiField
	DetailPanel employeeDetail;

	private JSF jsf;
	private Documents documents;
	private Cost cost;
	private Salary salary;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	
	private ContextMenu workplaceContextMenu;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {

		// Window.alert("This method constructs the application user interface by instantiating controls and hooking up event handler.");

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		// Window.enableScrolling(false);
		// Window.setMargin("0px");

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		// RootPanel root = RootPanel.get("rootPanel");
		root.add(ui);
		
		jsf = new JSF();
		cost = new Cost();
		salary = new Salary();
		documents = new Documents();
		salaryDraft = new SalaryDraft();
		salaryPreview = new SalaryPreview();
		workplaceContextMenu = new WorkplaceContextMenu();
		
		employees.addListener(this);

	}
	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		employeeDetail.setWidget(jsf);
		jsf.enterpriseSelected();
	}

	@Override
	public void onWorkplaceSelected(Workplace workplace) {
		employeeDetail.setWidget(jsf);
		jsf.workplaceSelected(workplace.getId());
	}

	@Override
	public void onEmployeeSelected(Employee employee) {
		employeeDetail.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
	}
	
	@Override
	public void onActivitySelected(Activity activity) {
		employeeDetail.setWidget(jsf);
		jsf.activitySelected(activity.getId());
	}

	@Override
	public void onSalariesSelected(SalaryDocuments docs) {
		employeeDetail.setWidget(salary);
		salary.setSalaryDocuments(docs);
	}	
	
	@Override
	public void onCostsSelected(CostDocuments docs) {
		employeeDetail.setWidget(cost);
		cost.setCostDocuments(docs);
	}
	
	@Override
	public void onDocumentsSelected(ISpinnable<IDocument> docs) {
		employeeDetail.setWidget(documents);
		documents.setDocuments(docs);
	}

	@Override
	public void onSalaryDraftSelected(SalaryDraftObject salaryDraftObject) {
		employeeDetail.setWidget(salaryDraft);
		salaryDraft.setSalaryDraftObject(salaryDraftObject);
	}
	
	@Override
	public void onSalaryPreviewSelected( SalaryPreviewDocument salaryPreviewDocument) {
		employeeDetail.setWidget(salaryPreview);
		salaryPreview.setSalaryPreviewDocument(salaryPreviewDocument);
	}
	
	
	@Override
	public void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		workplaceContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		workplaceContextMenu.show();
	}
	
	

}
