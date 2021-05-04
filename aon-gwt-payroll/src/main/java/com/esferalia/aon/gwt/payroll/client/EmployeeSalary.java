package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EmployeeSalary extends Composite {
	
	// --------------------------------------------- Salary Table Impl ----------------------------------------------
	
	private class SalaryTableImpl extends SalaryTable {

		@Override
		protected void onSelectionSalaryChange(boolean isSomethingSelected) {
			enableDisableButtons(isSomethingSelected);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, EmployeeSalary> {}
	
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
	ListBox typeList;
	
	@UiField
	ListBox dateFilterList;
	
	@UiField
	ListBox monthTillT;
	
	@UiField
	ListBox yearTillT;
	
	@UiField
	ListBox monthTTo;
	
	@UiField
	ListBox yearTTo;
	
	@UiField
	Label datesMessage;
	
	// -------------------------------------------------- Variables -------------------------------------------------
	
	private EmployeeSalaryObject employeeSalaryObject;
	
	private List<Listener> listeners;
	
	private List<SalaryInfo> salaries = Collections.emptyList();
	
	private SalaryTable salaryTable;
	
	private NewContextMenu contextMenu;
	
	private Integer settleId = null; // Only used for setlle letter assign on hasSettleSalary()
	private Integer settleEnterpriseId = null; // Only used for setlle letter assign on hasSettleSalary()
	
	private AonToolbar toolbar;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton pdfSettleButton;
	private AonToolbarButton publishButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;

	// ------------------------------------------------- Constructor ------------------------------------------------

	public EmployeeSalary() {
		toolbar = getToolbarPanel();
		salaryTable = new SalaryTableImpl();
		initWidget(uiBinder.createAndBindUi(this)); 
		
		contextMenu = new NewContextMenu();
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		mainContainer.add(salaryTable);
//		mainContainer.setHeight((Window.getClientHeight() - 150) + "px");
		
		salaryTable.setEmployeeView();
		salaryTable.sortTableByStartDate();
		
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
		
		showHideDatesMessage(false);
		
		initListBox();
	}
	
	private void initListBox() {
		// Clear listboxies
		typeList.clear();
		dateFilterList.clear();
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
		
		// Date Filter list
		dateFilterList.addItem("Personalizado");
		dateFilterList.addItem("Mes actual");
		dateFilterList.addItem("Mes anterior");
		dateFilterList.addItem(String.valueOf("\u00DA") + "ltimo trimestre");
		dateFilterList.addItem(String.valueOf("\u00DA") + "ltimo semestre");
		dateFilterList.addItem("A" + String.valueOf("\u00F1") + "o actual");
		dateFilterList.addItem("A" + String.valueOf("\u00F1") + "o anterior");
		dateFilterList.setSelectedIndex(0);
	}

	// ------------------------------------------ Set EmployeeSalaryObject -----------------------------------------

	public void setEmployeeSalaryObject(EmployeeSalaryObject employeeSalaryObject) {
		this.employeeSalaryObject = employeeSalaryObject;
		setNewToolbarTitle();
		this.employeeSalaryObject.getSalaries(
				s -> {
					initDatesListBox();
					setSettlePDFVisibility();
					filterCurrentYearSalaries();
				}, 
				f -> {}
		);
	}

	private void initDatesListBox() {
		// Add year to listboxes
		yearTillT.clear();
		yearTTo.clear();
		
		List<SalaryInfo> workplaceSalaries = this.employeeSalaryObject.getEmployeeSalaries();
		
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

	private void filterCurrentYearSalaries() {
		SalaryInfoFilter filter = employeeSalaryObject.getFilter();
		
		Date startDate = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -1);
		Date endDate = DateUtils.getLastDayOfYear(new Date());
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(startDate) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(endDate) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(startDate) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(endDate) + "");
		
		filter.setDateTillT(startDate);
		filter.setDateTTo(endDate);
		
		// Salary Type
		Integer salaryType = Integer.parseInt(typeList.getSelectedValue());
		filter.setSalaryType(salaryType);
		
		filter.setEmployeeId(null);
		
		this.employeeSalaryObject.getSalaries(
				s -> {
					salaryTable.setSalariesList(employeeSalaryObject.getEmployeeSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	private void setNewToolbarTitle() {
		String employeeName = this.employeeSalaryObject.getEmployeeName();
		if(AonStringUtils.isNotBlank(employeeName)) toolbar.setTitle("N" + String.valueOf("\u00F3") + "minas : " + employeeName);
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

	@UiHandler("typeList")
	public void onFilterTypeChange(ChangeEvent event) {
		filterSalaries();
	}
	
	@UiHandler("dateFilterList")
	public void onDateFilterListCahnge(ChangeEvent event) {
		int dateFilterType = dateFilterList.getSelectedIndex();
		if(dateFilterType == 0)
			enableDisableDatesListBox(true);
		else
			enableDisableDatesListBox(false);
		
		showHideDatesMessage(false);
		
		filterSalaries();
	}
	
	@UiHandler({"monthTillT", "yearTillT", "monthTTo", "yearTTo"})
	public void onFilterDatesChange(ChangeEvent event) {
		if(checkFilterDates()) {
			showHideDatesMessage(false);
			filterSalaries();
		} else {
			showHideDatesMessage(true);
		}
	}

	// ---------------------------------------------- Filter Salaries ------------------------------------------------
	
	private void filterSalaries() {
		SalaryInfoFilter filter = employeeSalaryObject.getFilter();
		
		// Date Filter
		filter.setDateTillT(getDateTillT());
		filter.setDateTTo(getDateTTo());
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(filter.getDateTillT()) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(filter.getDateTTo()) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(filter.getDateTillT()) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(filter.getDateTTo()) + "");
		
		// Salary Type
		Integer salaryType = Integer.parseInt(typeList.getSelectedValue());
		filter.setSalaryType(salaryType);
		
		this.employeeSalaryObject.getSalaries(
				s -> {
					salaryTable.setSalariesList(employeeSalaryObject.getEmployeeSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	private Date getDateTillT() {
		int dateFilterType = dateFilterList.getSelectedIndex();
		switch (dateFilterType) {
		case 1: // Mes actual
			return DateUtils.getFirstDayOfMonth();
		case 2: // Mes anterior
			return DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -1);
		case 3: // Tres meses
			return DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -2);
		case 4: // Seis meses
			return DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -5);
		case 5: // Año actual
			return DateUtils.getFirstDayOfYear();
		case 6: // Año anterior
			return DateUtils.addYears2Date(DateUtils.getFirstDayOfYear(), -1);
		default:
			Integer yearTillTValue = Integer.parseInt(yearTillT.getSelectedValue());
			Integer monthTillTValue = Integer.parseInt(monthTillT.getSelectedValue());
			return DateUtils.getFirstDayOfMonth(DateUtils.getDate(monthTillTValue, yearTillTValue));
		}
	}

	private Date getDateTTo() {
		int dateFilterType = dateFilterList.getSelectedIndex();
		if(0 == dateFilterType) {
			Integer yearTToValue = Integer.parseInt(yearTTo.getSelectedValue());
			Integer monthTToValue = Integer.parseInt(monthTTo.getSelectedValue());
			return DateUtils.getLastDayOfMonth(DateUtils.getDate(monthTToValue, yearTToValue));
		} else if(2 == dateFilterType) { // Mes anterior
			return DateUtils.getLastDayOfMonth(DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -1));
		} else if(6 == dateFilterType) { // Año anterior
			return DateUtils.getLastDayOfYear(DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -1));
		}
		
		return DateUtils.getLastDayOfMonth();
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
	
	private void enableDisableDatesListBox(boolean isEnabled) {
		monthTillT.setEnabled(isEnabled);
		yearTillT.setEnabled(isEnabled);
		monthTTo.setEnabled(isEnabled);
		yearTTo.setEnabled(isEnabled);
	}
	
	private void showHideDatesMessage(boolean isVisible) {
		datesMessage.setVisible(isVisible);
	}
	
	private void setSettlePDFVisibility() {
		pdfSettleButton.setVisible(hasSettleSalary());
	}
	
	private boolean checkFilterDates() {
		Integer yearTillTValue = Integer.parseInt(yearTillT.getSelectedValue());
		Integer monthTillTValue = Integer.parseInt(monthTillT.getSelectedValue());
		Date startDate = DateUtils.getDate(monthTillTValue, yearTillTValue);
		
		Integer yearTToValue = Integer.parseInt(yearTTo.getSelectedValue());
		Integer monthTToValue = Integer.parseInt(monthTTo.getSelectedValue());
		Date endDate = DateUtils.getDate(monthTToValue, yearTToValue);
		
		return startDate.before(endDate) || startDate.equals(endDate);
	}
	
	private boolean hasSettleSalary() {
		for(SalaryInfo salaryInfo : this.employeeSalaryObject.getEmployeeSalaries()) {
			if(salaryInfo.getType() == Type.SETTLE) {
				settleId = salaryInfo.getId();
				settleEnterpriseId = salaryInfo.getEnterpriseId();
				return true;
			}
		}
		settleId = null;
		settleEnterpriseId = null;
		return false;
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
		
		pdfSettleButton = new AonToolbarButton( "Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> {
			onPDFSettle(e);
		});	
		pdfSettleButton.setVisible(false);
		toolbar.add(pdfSettleButton);
		
		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
		publishButton.addClickHandler(e -> {
			onPublish(e);
		});	
		toolbar.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidow", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> {
			onBidoqPublish(e);
		});	
		bidoqPublishButton.setVisible(false);
		toolbar.add(bidoqPublishButton);
		
		email = new AonToolbarButton(AON.MSG.email(), AON.CSS.aonIconEmail());
		email.addClickHandler(e -> {
			onEmail(e);
		});	
		toolbar.add(email);
		
		return toolbar;

	}

	public void onDelete(ClickEvent e) {
		employeeSalaryObject.deleteSalaries(
				salaryTable.getSelectedSalaries(), 
				s -> {
					setEmployeeSalaryObject(employeeSalaryObject);
				}, 
				f -> {}
		);
	}

	public void onPDF(ClickEvent e) {
		onPDF("salary", "salaries");
	}
	
	public void onPDFSettle(ClickEvent e) {
		onPDF("settle", "settleLetter");
	}
	
	private void onPDF(String type, String name ) {
		String fileDownloadURL = GWT.getModuleBaseURL()+ "salary_exporter/";

		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(fileDownloadURL);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden(PayrollPrintService.Parameter.TYPE.getName(), type));
		flowPanel.add(new Hidden(PayrollPrintService.Parameter.ENTERPRISE.getName(), String.valueOf(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId())));
		flowPanel.add(new Hidden(PayrollPrintService.Parameter.DOMAIN.getName(), Wnd.getCurrentDomainNameURL()));
		flowPanel.add(new Hidden(PayrollPrintService.Parameter.USER.getName(), Wnd.getCurrentUser()));
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			flowPanel.add(new Hidden(PayrollPrintService.Parameter.ID.getName(), ""+((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId()));
		}
		flowPanel.add(new Hidden(PayrollPrintService.Parameter.NAME.getName(), name + ".pdf"));
		
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> {
			mainContainer.remove(formPanel);
		});
		mainContainer.add(formPanel);

		formPanel.submit();
		
	}
	
	private static native String b64decode(String a) /*-{
	  return window.btoa(a);
	}-*/;

	public void onPublish(ClickEvent e) {
		onPublish("drive");
	}

	public void onBidoqPublish(ClickEvent e) {
		onPublish("bidoq");
	}

	public void onEmail(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}

	private void onEmailEnterprise() {
		sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE);
	}

	private void onEmailEmployees() {
		sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.EMPLOYEE);
	}
	
	private void sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type) {
		Integer enterpriseID = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("url", GWT.getModuleBaseURL()+ "salary_exporter/");
		params.put("type", "salary");
		params.put("name", "salaries.pdf");
		params.put("enterprise", String.valueOf(enterpriseID));
		params.put("domain", Wnd.getCurrentDomainNameURL());
		params.put("user", Wnd.getCurrentUser());
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			params.put("id" + i, ""+((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
		}
		
		new PayrollEmailDialog(type, params) {
			
			@Override
			protected void onAccept() {
				String from = this.getFromMAilAccount().getId().toString();
				String to = this.getSendTo();
				String cc = this.getCC();
				String cco = this.getCCO();
				String bodyHTML = this.getBody();
				
				employeeSalaryObject.sendPayrollEmail(type, params, from, to, cc, cco, bodyHTML,
					s -> {
						AonDialog dialog = new AonDialog("AVISO", new HTML(employeeSalaryObject.getEmailStatus()));
						dialog.warning();
						hide();
					},f -> {}
				);
			}
		};
	}
	
	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbar);
		filterSalaryPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
}
