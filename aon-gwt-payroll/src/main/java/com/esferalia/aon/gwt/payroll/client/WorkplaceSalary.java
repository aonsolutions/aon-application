package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceSalary extends Composite {
	
	// --------------------------------------------- Salary Table Impl ----------------------------------------------
	
	private class SalaryTableImpl extends SalaryTable {

		@Override
		protected void onSelectionSalaryChange(boolean isSomethingSelected) {
			enableDisableButtons(isSomethingSelected);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, WorkplaceSalary> {}
	
	//Listener to Publish Salaries
	static interface Listener {
		void onPublishSalaries(SalaryInfo salary, String type);
	}
	
	// ----------------------------------------------- ScheduledCommand ---------------------------------------------
	
	class NewEmailEmployeesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEmployees();
		}
	}
	
	class NewEmailEnterpriseCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEnterprise();
		}
	}
	
	class NewContextMenu extends ContextMenu {
				
		private MenuItem newEmailEmployees = null;
		private MenuItem newEmailEnterprise = null;
		
		public NewContextMenu() {
			
			newEmailEmployees = addItem("Email empleados", new NewEmailEmployeesCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			newEmailEmployees.ensureDebugId("newEmailEmployees");
			
			newEmailEnterprise = addItem("Email empresa", new NewEmailEnterpriseCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			newEmailEnterprise.ensureDebugId("newEmailEnterprise");
		}
	}
	
	// -------------------------------------------------- UiFields --------------------------------------------------
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	VerticalPanel mainContainer;
	
	@UiField
	HTMLPanel filterSalaryPanel;
		
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	ListBox typeList;
	
	@UiField
	RadioButton noDateRB;
	
	@UiField
	RadioButton dateTTRB;
	
	@UiField
	ListBox monthTillT;
	
	@UiField
	ListBox yearTillT;
	
	@UiField
	ListBox monthTTo;
	
	@UiField
	ListBox yearTTo;
	
	// -------------------------------------------------- Variables -------------------------------------------------
	
	private WorkplaceSalaryObject workplaceSalaryObject;
	
	private List<Listener> listeners;
	
	private List<SalaryInfo> salaries = Collections.emptyList();
	
	private SalaryTable salaryTable;
	
	private NewContextMenu contextMenu;
	
	private AonToolbar toolbar;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton publishButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;

	// ------------------------------------------------- Constructor ------------------------------------------------

	public WorkplaceSalary() {
		toolbar = getToolbarPanel();
		salaryTable = new SalaryTableImpl();
		initWidget(uiBinder.createAndBindUi(this)); 
		
		contextMenu = new NewContextMenu();
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		mainContainer.add(salaryTable);
		
		salaryTable.setWorkplaceView();
		
		listeners = new LinkedList<Listener>();
		
		initFilterPanel();
	}
	
	private void initFilterPanel() {
		filterSalaryPanel.addStyleName(AON.CSS.aonSearchPanel());
		filterSalaryPanel.addStyleName(AON.CSS.aonScrollArea());
		filterSalaryPanel.addStyleName(AON.CSS.aonMarginBottom());
		filterSalaryPanel.addStyleName(AON.CSS.aonMarginLeft());
		filterSalaryPanel.addStyleName(AON.CSS.aonMarginRight());
		filterSalaryPanel.addStyleName(AON.CSS.aonBlockCenter());
		
		initListBox();
	}
	
	private void initListBox() {
		// Clear listboxies
		typeList.clear();
		monthTillT.clear();
		yearTillT.clear();
		monthTTo.clear();
		yearTTo.clear();
		
		// Add types to typeList
		typeList.addItem("Todas", "-1");
		typeList.addItem("Nomina", "0");
		typeList.addItem("Extra", "1");
		typeList.addItem("Atraso", "3");
		typeList.addItem("Finiquito", "2");
		
		// Add months to listboxes
		String[] monthList = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(int i=0; i<monthList.length; i++) {
			monthTillT.addItem(monthList[i], i+"");
			monthTTo.addItem(monthList[i], i+"");
		}
	}
	
	// ------------------------------------------ Set WorkplaceSalaryObject -----------------------------------------

	public void setWorkplaceSalaryObject(WorkplaceSalaryObject workplaceSalaryObject) {
		this.workplaceSalaryObject = workplaceSalaryObject;
		this.workplaceSalaryObject.getWorkplaceSalariesDB(
				s -> {
					initDatesListBox();
					initSuggestBox();
					filterCurrentYearSalaries();
					setNewToolbarTitle();
				}, 
				f -> {}
		);
	}

	private void initDatesListBox() {
		// Add year to listboxes
		yearTillT.clear();
		yearTTo.clear();
		
		List<SalaryInfo> workplaceSalaries = this.workplaceSalaryObject.getWorkplaceSalaries();
		
		Integer actualYear = DateUtils.getYear();
		Integer firstPayroll = (null == workplaceSalaries || workplaceSalaries.isEmpty()) ? DateUtils.getYear() : DateUtils.getYear(workplaceSalaries.get(workplaceSalaries.size()-1).getStartDate());
		Integer diffYears = actualYear - firstPayroll;
		
		for(int i = 0; i <= diffYears; i++) {
			Integer year = actualYear - i;
			String yearStr = year.toString();
			yearTillT.addItem(yearStr, yearStr);
			yearTTo.addItem(yearStr, yearStr);
		}
	}

	private void initSuggestBox() {
		//NAMES
		List<String> employeesNames = this.workplaceSalaryObject.getWorkplaceEmployees().getWorkplaceEmployeesName();
		List<String> employeesNamesSuggest = new ArrayList<String>();
		for(String name : employeesNames)
			employeesNamesSuggest.add(name+"");
		
		MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
		orclNames.addAll(employeesNamesSuggest);
		this.employeeSB.setAutoSelectEnabled(true);
		this.employeeSB.setValue("");
	}
	
	private void filterCurrentYearSalaries() {
		SalaryInfoFilter filter = workplaceSalaryObject.getFilter();
		
		filter.setNoDateFilter(false);
		filter.setDateMYFilter(false);
		filter.setDateTTFilter(true);
		
		Date firstDayOfYear = DateUtils.getFirstDayOfYear();
		Date lastDayOfYear = DateUtils.getLastDayOfYear(DateUtils.getFirstDayOfYear());
		
		dateTTRB.setValue(true);
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(firstDayOfYear) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(lastDayOfYear) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(firstDayOfYear) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(lastDayOfYear) + "");
		
		filter.setDateTillT(firstDayOfYear);
		filter.setDateTTo(lastDayOfYear);
		
		// Salary Type
		Integer salaryType = Integer.parseInt(typeList.getSelectedValue());
		filter.setSalaryType(salaryType);
		
		filter.setEmployeeId(null);
		
		this.workplaceSalaryObject.getFilterSalariesDB(
				s -> {
					salaryTable.setSalariesList(workplaceSalaryObject.getWorkplaceSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	private void setNewToolbarTitle() {
		List<SalaryInfo> workplaceSalaries = this.workplaceSalaryObject.getWorkplaceSalaries();
		String workplaceName = null;
		
		if(null != workplaceSalaries && !workplaceSalaries.isEmpty())
			workplaceName = workplaceSalaries.get(0).getWorkplaceName();
		
		if(AonStringUtils.isNotBlank(workplaceName)) toolbar.setTitle("N" + String.valueOf("\u00F3") + "minas : " + workplaceName);
	}

	// ---------------------------------------------- Init SalaryTable ----------------------------------------------

	private void initSalariesTable() {
		//Show buttons
		this.publishButton.setVisible(!Wnd.getCurrentDomainNameURL().contains("ayudat"));
		this.bidoqPublishButton.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));
		
		//Disable buttons till any salary selected
		enableDisableButtons(false);
		
		salaryTable.initSalariesTable();
	}
	
	// ------------------------------------------------ Ui Handlers -------------------------------------------------

	@UiHandler("employeeSB")
	public void onFilterEmployee(ValueChangeEvent<String> event) {
		filterSalaries();
	}
	
	@UiHandler("typeList")
	public void onFilterTypeChange(ChangeEvent event) {
		filterSalaries();
	}
	
	@UiHandler("noDateRB")
	public void onNoDateRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthTillT.setEnabled(false);
			yearTillT.setEnabled(false);
			monthTTo.setEnabled(false);
			yearTTo.setEnabled(false);
			filterSalaries();
		}
	}
	
	@UiHandler("dateTTRB")
	public void onDateTTRBCahnge(ValueChangeEvent<Boolean> event) {
		if(event.getValue()) {
			monthTillT.setEnabled(true);
			yearTillT.setEnabled(true);
			monthTTo.setEnabled(true);
			yearTTo.setEnabled(true);
			filterSalaries();
		}
	}
	
	@UiHandler({"monthTillT", "yearTillT", "monthTTo", "yearTTo"})
	public void onFilterDatesChange(ChangeEvent event) {
		filterSalaries();
	}
	
	// ---------------------------------------------- Filter Salaries ------------------------------------------------
	
	private void filterSalaries() {
		SalaryInfoFilter filter = workplaceSalaryObject.getFilter();
		if(noDateRB.getValue()) {
			filter.setNoDateFilter(true);
			filter.setDateMYFilter(false);
			filter.setDateTTFilter(false);
		} else if(dateTTRB.getValue()) {
			filter.setNoDateFilter(false);
			filter.setDateMYFilter(false);
			filter.setDateTTFilter(true);
			
			Integer yearTillTValue = Integer.parseInt(yearTillT.getSelectedValue());
			Integer monthTillTValue = Integer.parseInt(monthTillT.getSelectedValue());
			filter.setDateTillT(DateUtils.getDate(monthTillTValue, yearTillTValue));
			
			Integer yearTToValue = Integer.parseInt(yearTTo.getSelectedValue());
			Integer monthTToValue = Integer.parseInt(monthTTo.getSelectedValue());
			filter.setDateTTo(DateUtils.getDate(monthTToValue, yearTToValue));
		}
		
		// Salary Type
		Integer salaryType = Integer.parseInt(typeList.getSelectedValue());
		filter.setSalaryType(salaryType);
		
		
		//Check if exist employee filter
		String nameSurname = employeeSB.getValue();
		if(nameSurname.length() > 0) {
			EmployeeInfo employeeInfo = workplaceSalaryObject.getEmployeeDataByNameSurname(nameSurname);
			filter.setEmployeeId(employeeInfo.getEmployeeId());
			filter.setWorkplaceId(null);
			filter.setEnterpriseId(null);
		}else
			filter.setEmployeeId(null);
		
		this.workplaceSalaryObject.getFilterSalariesDB(
				s -> {
					salaryTable.setSalariesList(workplaceSalaryObject.getWorkplaceSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	// --------------------------------------------- Auxiliar Methods ------------------------------------------------
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	void onPublish(String type) {
		for (Listener listener : listeners)
			for(SalaryInfo salary : salaryTable.getSelectedSalaries())
			listener.onPublishSalaries(salary, type);
	}

	public void hideEnterpriseSiteButtons() {
		deleteButton.getElement().getStyle().setDisplay(Display.NONE);
		email.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void enableDisableButtons(boolean isSomethingSelected) {
		deleteButton.setEnabled(isSomethingSelected);
    	pdfButton.setEnabled(isSomethingSelected);
    	publishButton.setEnabled(isSomethingSelected);
    	bidoqPublishButton.setEnabled(isSomethingSelected);
    	email.setEnabled(isSomethingSelected);
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------------------- Toolbar -----------------------------------------------------
	
	private AonToolbar getToolbarPanel() {

		AonToolbar toolbar = new AonToolbar("N" + String.valueOf("\u00F3") + "minas");

		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.addClickHandler(e -> {
			onDelete(e);
		});	
		toolbar.add(deleteButton);
		
		pdfButton = new AonToolbarButton( AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> {
			onPDF(e);
		});	
		toolbar.add(pdfButton);
		
		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> {
			onPublish(e);
		});	
		toolbar.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> {
			onBidoqPublish(e);
		});	
		toolbar.add(bidoqPublishButton);
		
		email = new AonToolbarButton(AON.MSG.email(), AON.CSS.aonIconEmail());
		email.addClickHandler(e -> {
			onEmail(e);
		});	
		toolbar.add(email);
		
		return toolbar;

	}

	private void onDelete(ClickEvent e) {
		workplaceSalaryObject.delete(
				salaryTable.getSelectedSalaries(), 
				s -> {
					setWorkplaceSalaryObject(workplaceSalaryObject);
				}, 
				f -> {}
		);
	}

	private void onPDF(ClickEvent e) {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "salary_exporter/";
		String query = "?type=salary&selectedSalaries=" + salaryTable.getSelectedSalaries().size()
	            + "&enterprise=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId()
		        ;
			
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		Window.open(fileDownloadURL+paramsBase64, "_blank", null);
	}
	
	private static native String b64decode(String a) /*-{
	  return window.btoa(a);
	}-*/;

	private void onPublish(ClickEvent e) {
		onPublish("drive");
	}

	private void onBidoqPublish(ClickEvent e) {
		onPublish("bidoq");
	}

	private void onEmail(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}

	private void onEmailEnterprise() {
		Integer enterpriseID = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		// PARAMS TO DOWNLOAD PAYROLLS
		String url = GWT.getModuleBaseURL()+ "salary_exporter/";
		String query = "?type=salary&selectedSalaries=" + salaryTable.getSelectedSalaries().size()
	            + "&enterprise=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
			
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		//Complete URL
		url += paramsBase64;
		
		// DIALOG TO SEND EMAIL
		PayrollEmailToEnterpriseDialog dialog = new PayrollEmailToEnterpriseDialog(enterpriseID, url) {
			
			@Override
			protected void onAccept() {
				if(null == this.getFromMAilAccount()) {
					WarningDialog warning = new WarningDialog("AVISO", "No existe cuenta de correo desde la que enviar este mensaje.");
					warning.center();
					warning.show();
				} else {
					String from = this.getFromMAilAccount().getId().toString();
					String to = this.getSendTo();
					String cc = this.getCC();
					String cco = this.getCCO();
					String bodyHTML = this.getBody();
					
					workplaceSalaryObject.sendPayrollEmail(from, to, cc, cco, bodyHTML,
						s -> {
							WarningDialog warning = new WarningDialog("AVISO", workplaceSalaryObject.getEmailStatus());
							warning.center();
							warning.show();
							hide();
						},f -> {}
					);
				}
			}
		};
		
		dialog.center();
		dialog.show();
	}

	private void onEmailEmployees() {
		Integer enterpriseID = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		// PARAMS TO DOWNLOAD PAYROLLS
		String query = "?type=salary&selectedSalaries=" + salaryTable.getSelectedSalaries().size()
	            + "&enterprise=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
			
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			query += "&salary"+i+"Id=" + ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId();
		}
		
		query += "&name=salaries.pdf";
		
		String paramsBase64 = b64decode(query);
		
		final String url = GWT.getModuleBaseURL()+ "salary_exporter/" + paramsBase64;
		
		// CHECK SELECTED EMPLOYEES EMAILS
		workplaceSalaryObject.checkEmployeesEmails(
				salaryTable.getSelectedSalaries(), 
				s -> {
					if(workplaceSalaryObject.getCheckEmailEmployeesStatus().length() != 0) {
						WarningDialog warningDialog = new WarningDialog("REVISAR EMAILS", workplaceSalaryObject.getCheckEmailEmployeesStatus());
						warningDialog.center();
						warningDialog.show();
					} else {
						PayrollEmailToEmployeesDialog dialog = new PayrollEmailToEmployeesDialog(enterpriseID, url) {
							
							@Override
							protected void onAccept() {
								
								if(null == this.getFromMAilAccount()) {
									WarningDialog warning = new WarningDialog("AVISO", "No existe cuenta de correo desde la que enviar este mensaje.");
									warning.center();
									warning.show();
								} else {
									String from = this.getFromMAilAccount().getId().toString();
									String cc = this.getCC();
									String cco = this.getCCO();
									String bodyHTML = this.getBody();
									
									workplaceSalaryObject.sendPayrollEmailToEmployees(from, cc, cco, bodyHTML, url,
										s -> {
											WarningDialog warning = new WarningDialog("AVISO", workplaceSalaryObject.getEmailStatus());
											warning.center();
											warning.show();
											hide();
										},f -> {}
									);
								}
							}
						}; 
						
						dialog.center();
						dialog.show();
					}
				}, 
				f -> {}
		);
	}

}
