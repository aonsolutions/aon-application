/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;


import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
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

		@Source("warn.png")
		ImageResource warn();

		@Source("changed.png")
		ImageResource changed();

		@Source("aon-menuBar.png")
		ImageResource menuBar();

		@Source("aon-tabBar.png")
		ImageResource tabBar();

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

		@Source("ine.png")
		ImageResource ine();

		@Source("workplace.png")
		ImageResource workplace();

		@Source("employee.png")
		ImageResource employee();
		
		@Source("card.png")
		ImageResource card();

		@Source("agreement.png")
		ImageResource agreement();
	}

	interface Binder extends UiBinder<Widget, EnterpriseSite> {
	}

	private static final Binder binder = GWT.create(Binder.class);

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
	private Statistics stats;
	private ITEditor it;
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
		GWT.<MainEntryPoint.AonResources> create(
				MainEntryPoint.AonResources.class).css().ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		jsf = new JSF();
		cost = new Cost();
		salary = new Salary();
		stats = new Statistics();
		it = new ITEditor(){
			@Override
			protected void showContractActiveTooltip(Tooltip tooltip,
					int clientX, int clientY) {
				showContractEndedTooltip(tooltip, clientX, clientY);
			}
			
			@Override
			protected void showContractLeaveActiveTooltip(Tooltip tooltip,
					int clientX, int clientY) {
				showContractLeaveEndedTooltip(tooltip, clientX, clientY);
			}
			
			@Override
			protected boolean isLeaveEmployee(String pElement) {
				return false;
			}
		};
		salary.hideDeleteButton();
		documents = new Documents();
		employees.addListener(this);
	}

	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		detailPanel.setWidget(jsf);
		jsf.enterpriseSelected(enterprise.getId());
	}

	@Override
	public void onWorkplaceSelected(Workplace workplace) {
		detailPanel.setWidget(jsf);
		jsf.workplaceSelected(workplace.getId());
	}

	@Override
	public void onEmployeeSelected(Employee employee) {
		detailPanel.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
	}

	@Override
	public void onSalariesSelected(SalaryDocuments docs) {
		detailPanel.setWidget(salary);
		salary.setSalaryDocuments(docs);
	}

	@Override
	public void onCostsSelected(CostDocuments docs) {
		cost.setTitle("Costes");
		detailPanel.setWidget(cost);
		cost.setCostDocuments(docs);
	}
	
	@Override
	public void onStatisticsSelected(com.esferalia.aon.gwt.payroll.shared.Statistics statistics) {
		detailPanel.setWidget(stats);
		stats.setStatistics(statistics);
	}	

	@Override
	public void onITDataSelected(com.esferalia.aon.gwt.payroll.shared.ITData itData) {
		detailPanel.setWidget(it);
		it.setITEditor(itData);		
	}
	
	@Override
	public void onSalariesSelected(SalariesDocuments docs) {
		cost.setTitle("N\u00F3minas");
		detailPanel.setWidget(cost);
		cost.setCostDocuments(docs);
	}

	@Override
	public void onDocumentsSelected(ISpinnable<IDocument> docs) {
		detailPanel.setWidget(documents);
		documents.setDocuments(docs);
	}
	
	@Override
	public void onIrpfsSelected(IrpfDocuments docs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivitySelected(Activity activity) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSalaryPreviewSelected(
			SalaryPreviewDocument salaryPreviewDocument) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onWorkplaceContextMenu(Workplace workplace,
			ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onEnterpriseContextMenu(Enterprise enterprise,
			ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSalaryDraftSelected(SalaryDraftObject salaryDraftDocument) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAgreementDraftSelected(
			AgreementDraftObject agreementDraftDocument) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onEventsDraftSelected(EventsDraftObject eventsDraftObject) {
		// TODO Auto-generated method stub
		
	}

	
}
