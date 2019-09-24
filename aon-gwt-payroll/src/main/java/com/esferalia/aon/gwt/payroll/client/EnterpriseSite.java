/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;


import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ContextMenuEvent;
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
	private CalendarDraft calendarDraft;
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
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();

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
		calendarDraft = new CalendarDraft() {
			
			@Override
			protected void onEnterKeyPressAction(Date date) {

			}
			
			@Override
			protected void onSuprKeyPressAction(Date date) {				
				
			}
		};
		calendarDraft.deshabilitGestionCalendar();
		
		it = new ITEditor(){
			@Override
			protected void showContractActiveTooltip(Tooltip tooltip,
					int clientX, int clientY) {
				showContractEndedTooltip(tooltip, clientX, clientY);
			}
			
			@Override
			protected void showLeaveActiveTooltip(Tooltip tooltip,
					final int clientX, final int clientY) {
				showLeaveEndedTooltip(tooltip, clientX, clientY);
			}
			
			
			@Override
			protected boolean isLeaveEmployee(String pElement) {
				return false;
			}
		};
		salary.hideDeleteButton();
		documents = new Documents();
		employees.getOptionsToolbar().setVisibleCopyButton(false);
		employees.getOptionsToolbar().setVisibleDraftButton(false);
		employees.getOptionsToolbar().setVisibleNewButton(false);
		employees.getOptionsToolbar().setVisiblePasteButton(false);		
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
	public void onITDataSelected(ITDataObject dataObject) {
		detailPanel.setWidget(it);
		it.setITEditor(dataObject);		
	}
	

	@Override
	public void onCalendarSelected(CalendarDraftObjectData calendarObjectData) {		
		detailPanel.setWidget(calendarDraft);				
		calendarDraft.setCalendarDraftObject(null, calendarObjectData);
	}
	
	@Override
	public void onEmployeeCalendarSelected(EmployeeCalendarDraftObjectData calendar) {
		detailPanel.setWidget(calendarDraft);				
	}
	
	@Override
	public void onEmployeeSalarySelected(EmployeeSalaryObject employeeSalary) {

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
	public void onCCCSelected(CCC ccc) {
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
	public void onCCCContextMenu(CCC ccc, ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onWorkplaceContextMenu(Workplace workplace,
			ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		
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
	public void onCategoryDraftSelected(CategoryDraftObject agreementDraftObject) {
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

	@Override
	public void onEmployeeCopy(Employee employee) {
		// TODO Apéndice de método generado automáticamente		
	}

	@Override
	public void onEmployeePaste(Workplace workplace) {
		// TODO Apéndice de método generado automáticamente		
	}

	@Override
	public void onEmployeeCut(Employee employee) {
		// TODO Apéndice de método generado automáticamente		
	}

	@Override
	public void onSuprPress(Employee employee) {
		// TODO Apéndice de método generado automáticamente
		
	}
	
	@Override
	public void onLoadAvaiableEmployees(Map<String, String> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEmployeeEventsDraftSelected(
			EmployeeEventsDraftObject employeeEventsDraft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEmployeeDraftSelected(EmployeeDraftObject employeeDraft) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void onSSBonusDraftSelected(SSBonusDraftObject ssBonus) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onEmployeeNewDraftSelected(EmployeeNewDraftObject employeeNewDraft) {
		// TODO Auto-generated method stub
		
	}

	
}
