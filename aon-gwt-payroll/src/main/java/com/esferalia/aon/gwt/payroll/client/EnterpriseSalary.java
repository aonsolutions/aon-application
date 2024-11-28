package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Mail;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class EnterpriseSalary extends Composite {
	
	// --------------------------------------------- Salary Table Impl
	
	private class SalaryTableImpl extends SalaryTable {

		@Override
		protected void onSelectionSalaryChange(boolean isSomethingSelected, boolean hasSettleSelected) {
			enableDisableButtons(isSomethingSelected);
		}
		
	}

	// --------------------------------------------- UiBinder
	
	private static EnterpriseSalaryUiBinder uiBinder = GWT.create(EnterpriseSalaryUiBinder.class);

	interface EnterpriseSalaryUiBinder extends UiBinder<Widget, EnterpriseSalary> {}
	
	// --------------------------------------------- Listener to Publish Salaries
	
	static interface Listener {
		void onPublishSalaries(SalaryInfo salary, String type);
	}
	
	// --------------------------------------------- EmailContextMenu
	
	class NewEmailEmployeesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEmployees();
		}
		
		private void onEmailEmployees() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.EMPLOYEE);
		}
	}
	
	class NewEmailEnterpriseCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEnterprise();
		}
		
		private void onEmailEnterprise() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE);
		}
	}
	
	class NewEmailEnterpriseManagementCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onEmailEnterpriseManagement();
		}
		
		private void onEmailEnterpriseManagement() {
			sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE_MANAGEMENT);
		}
	}

	private void sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type) {
		Integer enterpriseID = null;
		if(!type.equals(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type.ENTERPRISE_MANAGEMENT))
			enterpriseID = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		HashMap<String, String> params = new HashMap<>();
		params.put("url", GWT.getModuleBaseURL()+ "salary_connor_macleod/");
		params.put("type", "salary");
		params.put("name", "salaries.pdf");
		params.put("enterprise", String.valueOf(enterpriseID));
		params.put("domain", Wnd.getCurrentDomainNameURL());
		params.put("user", Wnd.getCurrentUser());
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++) {
			params.put("id" + i, ""+((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
			params.put("enterpriseId" + i, ""+((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getEnterpriseId());
		}
		
		new PayrollEmailDialog(type, params) {
			
			@Override
			protected void onAccept() {
				Mail mail = new Mail()
						.setFrom(this.getFromMAilAccount().getId().toString())
						.setTo(this.getSendTo())
						.setCc(this.getCC())
						.setCco(this.getCCO())
						.setBodyHTML(this.getBody())
						.setPassword(this.isPassword());
				
				enterpriseSalaryObject.sendPayrollEmail(type, params, mail,
					s -> {
						AonMessagePanel.showSuccess(messagePanel, enterpriseSalaryObject.getEmailStatus());
						hide();
					},f -> {
						AonMessagePanel.showError(messagePanel, f.getMessage());
						hide();
					}
				);
			}
		};
	}
	
	class EmailContextMenu extends ContextMenu {
				
		private MenuItem newEmailEmployees = null;
		private MenuItem newEmailEnterprise = null;
		private MenuItem newEmailEnterpriseManagement = null;
		
		public EmailContextMenu() {
			
			newEmailEmployees = addItem("Email empleados", new NewEmailEmployeesCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newEmailEmployees.ensureDebugId("newEmailEmployees");
			
			newEmailEnterprise = addItem("Email empresa", new NewEmailEnterpriseCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newEmailEnterprise.ensureDebugId("newEmailEnterprise");
			
			newEmailEnterpriseManagement = addItem("Email empresa", new NewEmailEnterpriseManagementCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newEmailEnterpriseManagement.ensureDebugId("newEmailEnterprise");
			newEmailEnterpriseManagement.setVisible(false);
		}
		
		private void setEnterpriseManagement() {
			newEmailEnterprise.setVisible(false);
			newEmailEnterpriseManagement.setVisible(true);
		}
		
		private void setEnterprise() {
			newEmailEnterprise.setVisible(true);
			newEmailEnterpriseManagement.setVisible(false);
		}
	}	

	// --------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckPanel toolbarDeckPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	AonToolbar toolbarPDFViewer;
	
	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	VerticalPanel mainContainer;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel filterSalaryPanel;
	
	@UiField
	HTMLPanel enterprisesPanel;
	
	@UiField
	SuggestBox enterpriseSB;
	
	@UiField
	HTMLPanel workplacesPanel;
	
	@UiField
	SuggestBox workplaceSB;
	
	@UiField
	SuggestBox employeeSB;
	
	@UiField
	CheckBox salaryCB;
	
	@UiField
	CheckBox extraCB;
	
	@UiField
	CheckBox delayCB;
	
	@UiField
	CheckBox settleCB;
	
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
	
	@UiField
	SimpleLayoutPanel scrolledPDFPanel;
	
	@UiField
	FullViewer pdfViewer;
	
	// --------------------------------------------- Variables
	
	private EnterpriseSalaryObject enterpriseSalaryObject;
	
	private List<Listener> listeners = new LinkedList<>();
	
	private SalaryTable salaryTable;
	
	private EmailContextMenu contextMenu;
	
	private AonToolbarButton backButton;
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
//	private AonToolbarButton publishButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;
	
	private DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();
	private DomainUserRoles dur;

	// --------------------------------------------- Constructor

	protected EnterpriseSalary() {
		this.toolbar = new AonToolbar("N\u00f3minas");
		this.toolbarPDFViewer = new AonToolbar("N\u00f3minas");
		salaryTable = new SalaryTableImpl();
		initWidget(uiBinder.createAndBindUi(this)); 
		
		initView();
		service.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				dur = result;
				bidoqPublishButton.setVisible(null != dur && dur.isBidoq());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error DUR: " + caught.getMessage());
			}
			
		});
		
	}

	private void initView() {
		// Init toolbar
		getToolbarPanel();
		getToolbarPDFViewerPanel();
		
		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 200.00, Unit.PX);
		
		contextMenu = new EmailContextMenu();
		
		mainContainer.add(salaryTable);
		
		salaryTable.sortTableByName();
		
		listeners = new LinkedList<>();
		
		initFilterPanel();

		showSalary();
	}
	
	// --------------------------------------------- Filter Panel
	
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
		dateFilterList.clear();
		monthTillT.clear();
		yearTillT.clear();
		monthTTo.clear();
		yearTTo.clear();
		
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
		dateFilterList.addItem("A\u00F1o actual");
		dateFilterList.addItem("A\u00F1o anterior");
		dateFilterList.setSelectedIndex(0);
	}
	
	// --------------------------------------------- setEnterpriseSalaryObject

	public void setEnterpriseSalaryObject(EnterpriseSalaryObject enterpriseSalaryObject) {
		this.enterpriseSalaryObject = enterpriseSalaryObject;
		this.enterpriseSalaryObject.getSalariesDates(
				s -> {
					showSalary();
					initIsParentDomainView();
					initDatesListBox();
					initSuggestBox();
					filterCurrentYearSalaries();
				}, 
				f -> {}
		);
	}
	
	public void setEnterprisesView() {
		employeeSB.setEnabled(false);
	}
	
	public void hideEditSalaryButton() {
		salaryTable.setEnteprisesView();
	}

	public void setEnterpriseView() {
		employeeSB.setEnabled(true);
		salaryTable.setEntepriseView();
	}
	
	private void initIsParentDomainView() {
		List<Enterprise> enterprises = this.enterpriseSalaryObject.getEnterprises();
		if(null == enterprises || enterprises.isEmpty()) {
			this.enterprisesPanel.setVisible(false);
			this.workplacesPanel.setVisible(true);
			this.contextMenu.setEnterprise();
		} else {
			this.enterprisesPanel.setVisible(true);
			this.workplacesPanel.setVisible(false);
			this.contextMenu.setEnterpriseManagement();
		}
	}

	private void initDatesListBox() {
		// Add year to listboxes
		yearTillT.clear();
		yearTTo.clear();
		
		Integer firstPayrollYear = DateUtils.getYear(this.enterpriseSalaryObject.getMinDate());
		Integer lastPayrollYear = DateUtils.getYear(this.enterpriseSalaryObject.getMaxDate());
		Integer diffYears = lastPayrollYear - firstPayrollYear;
		
		for(int i = 0; i <= diffYears; i++) {
			Integer year = lastPayrollYear - i;
			String yearStr = year.toString();
			yearTillT.addItem(yearStr, yearStr);
			yearTTo.addItem(yearStr, yearStr);
		}
	}

	private void initSuggestBox() {
		List<Enterprise> enterprises = this.enterpriseSalaryObject.getEnterprises();
		if(null == enterprises || enterprises.isEmpty()) {
			
			//NAMES
			List<String> employeesNames = this.enterpriseSalaryObject.getEnterpriseEmployeesName();
			List<String> employeesNamesSuggest = new ArrayList<>();
			employeesNames.forEach(name -> employeesNamesSuggest.add(name+""));
			
			MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
			orclNames.addAll(employeesNamesSuggest);
			this.employeeSB.setAutoSelectEnabled(true);
			this.employeeSB.setValue("");
			
			//WORKPLACES
			List<String> workplaceNames = this.enterpriseSalaryObject.getWorkplacesNames();
			List<String> workplaceNamesSuggest = new ArrayList<>();
			workplaceNames.forEach(workplace -> workplaceNamesSuggest.add(workplace+""));
			
			MultiWordSuggestOracle orclWorkplaceNames = (MultiWordSuggestOracle) this.workplaceSB.getSuggestOracle();
			orclWorkplaceNames.addAll(workplaceNamesSuggest);
			this.workplaceSB.setAutoSelectEnabled(true);
			this.workplaceSB.setValue("");
			
		} else {
			//ENTERPRISES
			List<String> enterprisesNames = this.enterpriseSalaryObject.getEnterprisesName();
			List<String> enterprisesNamesSuggest = new ArrayList<>();
			enterprisesNames.forEach(name -> enterprisesNamesSuggest.add(name+""));
			
			MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.enterpriseSB.getSuggestOracle();
			orclNames.addAll(enterprisesNamesSuggest);
			this.enterpriseSB.setAutoSelectEnabled(true);
			this.enterpriseSB.setValue("");
		}
	}
	
	private void filterCurrentYearSalaries() {
		SalaryInfoFilter filter = enterpriseSalaryObject.getFilter();
		
		Date startDate = DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -1);
		Date endDate = DateUtils.getLastDayOfMonth(enterpriseSalaryObject.getMaxDate());
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(startDate) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(endDate) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(startDate) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(endDate) + "");
		
		filter.setDateTillT(startDate);
		filter.setDateTTo(endDate);
		
		// Salary Types
		List<Integer> salaryTypes = new ArrayList<>();
		
		if(salaryCB.getValue()) salaryTypes.add(0);
		if(extraCB.getValue()) salaryTypes.add(1);
		if(settleCB.getValue()) salaryTypes.add(2);
		if(delayCB.getValue()) salaryTypes.add(3);
		
		filter.setSalaryTypes(salaryTypes);
		
		filter.setEmployeeId(null);
		filter.setWorkplaceId(null);
		filter.setEnterpriseId(null);
		
		this.enterpriseSalaryObject.getSalaries(
				s -> {
					salaryTable.setSalariesList(enterpriseSalaryObject.getEnterpriseSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	public void setNewToolbarTitle() {
		String entepriseName = this.enterpriseSalaryObject.getEnterpriseName();
		if(AonStringUtils.isNotBlank(entepriseName)) {
			toolbar.setTitle("N\u00F3minas : " + entepriseName);
			toolbarPDFViewer.setTitle("N\u00F3minas : " + entepriseName);
		}
	}
	
	// --------------------------------------------- Init SalaryTable

	private void initSalariesTable() {
		//Disable buttons till any salary selected
		enableDisableButtons(false);
		
		salaryTable.initSalariesTable();
	}

	// --------------------------------------------- UI Handlers
	
	@UiHandler("enterpriseSB")
	public void onFilterEnteprise(ValueChangeEvent<String> event) {
		if(AonStringUtils.isNotBlank(event.getValue())) {
			workplaceSB.setValue("");
			employeeSB.setValue("");
			employeeSB.setEnabled(true);
		} else {
			employeeSB.setValue("");
			employeeSB.setEnabled(false);
			enterpriseSalaryObject.setEnterprise(null);
			filterSalaries();
		}
	}
	
	@UiHandler("enterpriseSB")
	public void onFilterEntepriseSelection(SelectionEvent<Suggestion> event) {
		Enterprise enterprise = enterpriseSalaryObject.getEnterpriseByName(enterpriseSB.getValue());
		enterpriseSalaryObject.setEnterprise(enterprise, s -> {
			//NAMES
			List<String> employeesNames = this.enterpriseSalaryObject.getEnterpriseEmployeesName();
			List<String> employeesNamesSuggest = new ArrayList<>();
			employeesNames.forEach(name -> employeesNamesSuggest.add(name+""));
			
			MultiWordSuggestOracle orclNames = (MultiWordSuggestOracle) this.employeeSB.getSuggestOracle();
			orclNames.addAll(employeesNamesSuggest);
			this.employeeSB.setAutoSelectEnabled(true);
			this.employeeSB.setValue("");
		}, f ->{});
		filterSalaries();
	}

	@UiHandler("workplaceSB")
	public void onFilterWorkplace(ValueChangeEvent<String> event) {
		if(AonStringUtils.isNotBlank(event.getValue())) {
			enterpriseSB.setValue("");
			employeeSB.setValue("");
		} else
			filterSalaries();
	}
	
	@UiHandler("workplaceSB")
	public void onFilterWorkplaceSelection(SelectionEvent<Suggestion> event) {
		filterSalaries();
	}
	
	@UiHandler("employeeSB")
	public void onFilterEmployee(ValueChangeEvent<String> event) {
		if(AonStringUtils.isNotBlank(event.getValue()))
			workplaceSB.setValue("");
		else
			filterSalaries();
	}
	
	@UiHandler("employeeSB")
	public void onFilterEmployeeSelection(SelectionEvent<Suggestion> event) {
		filterSalaries();
	}
	
	@UiHandler({"salaryCB", "extraCB", "delayCB", "settleCB"})
	public void onFilterSalaryCB(ValueChangeEvent<Boolean> event) {
		filterSalaries();
	}
	
	@UiHandler("dateFilterList")
	public void onDateFilterListCahnge(ChangeEvent event) {
		int dateFilterType = dateFilterList.getSelectedIndex();
		enableDisableDatesListBox(dateFilterType == 0);
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

	// --------------------------------------------- Filter Salaries
	
	private void filterSalaries() {
		SalaryInfoFilter filter = enterpriseSalaryObject.getFilter();
		
		// Date Filter
		filter.setDateTillT(getDateTillT());
		filter.setDateTTo(getDateTTo());
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(filter.getDateTillT()) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(filter.getDateTTo()) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(filter.getDateTillT()) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(filter.getDateTTo()) + "");
		
		// Salary Type
		List<Integer> salaryTypes = new ArrayList<>();
		
		if(salaryCB.getValue()) salaryTypes.add(0);
		if(extraCB.getValue()) salaryTypes.add(1);
		if(settleCB.getValue()) salaryTypes.add(2);
		if(delayCB.getValue()) salaryTypes.add(3);
		
		filter.setSalaryTypes(salaryTypes);
		
		//Check if exist enterprise filter
		String enterpsieName = enterpriseSB.getValue();
		if(AonStringUtils.isNotBlank(enterpsieName)) {
			Enterprise enterprise = enterpriseSalaryObject.getEnterpriseByName(enterpsieName);
			filter.setEmployeeId(null);
			filter.setWorkplaceId(null);
			filter.setEnterpriseId(enterprise.getId());
		}else
			filter.setEnterpriseId(null);
		
		//Check if exist workplace filter
		String workplaceName = workplaceSB.getValue();
		if(AonStringUtils.isNotBlank(workplaceName)) {
			Workplace workplace = enterpriseSalaryObject.getWorkplaceByDescription(workplaceName);
			filter.setEmployeeId(null);
			filter.setWorkplaceId(workplace.getId());
			filter.setEnterpriseId(null);
		}else
			filter.setWorkplaceId(null);
		
		//Check if exist employee filter
		String nameSurname = employeeSB.getValue();
		if(AonStringUtils.isNotBlank(nameSurname)) {
			EmployeeInfo employeeInfo = enterpriseSalaryObject.getEmployeeDataByNameSurname(nameSurname);
			filter.setEmployeeId(employeeInfo.getEmployeeId());
			filter.setWorkplaceId(null);
			filter.setEnterpriseId(null);
		}else
			filter.setEmployeeId(null);
		
		this.enterpriseSalaryObject.getSalaries(
				s -> {
					salaryTable.setSalariesList(enterpriseSalaryObject.getEnterpriseSalaries());
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

	// --------------------------------------------- Auxiliar Methods
	
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
//    	publishButton.setEnabled(isSomethingSelected);
    	
    	if(null != this.dur && this.dur.isBidoq())
    		bidoqPublishButton.setEnabled(isSomethingSelected && null != this.dur && this.dur.isBidoq());
    	
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
	
	public void setBackButtonVisible() {
		this.backButton.setVisible(true);
	}
	
	public void setSalaryPrintView() {
		this.backButton.setVisible(false);
		this.deleteButton.setVisible(false);
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
	
	// --------------------------------------------- Toolbar
	
	private void getToolbarPanel() {

		backButton = new AonToolbarButton( "Atras", AON.CSS.aonIconBack() );
		backButton.addClickHandler(e -> onBackClick());	
		backButton.setVisible(false);
		toolbar.add(backButton);
		
		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.addClickHandler(e -> onDelete());	
		toolbar.add(deleteButton);
		
		pdfButton = new AonToolbarButton( AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> onPDF());	
		toolbar.add(pdfButton);
		
//		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
//		publishButton.addClickHandler(e -> onPublish());	
//		toolbar.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidoq", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> onBidoqPublish());	
		bidoqPublishButton.setVisible(null != this.dur && this.dur.isBidoq());
		toolbar.add(bidoqPublishButton);
		
		email = new AonToolbarButton(AON.MSG.email(), AON.CSS.aonIconEmail());
		email.addClickHandler(this::onEmail);	
		toolbar.add(email);
	}
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private AonToolbar getToolbarPDFViewerPanel() {

		AonToolbarButton closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.addClickHandler(e -> onClosePDF());
		toolbarPDFViewer.add(closePDF);

		return toolbarPDFViewer;
	}
	
	// ------------------------------------------------- Show/Hide Employee/PDF

	private void showSalary() {
		toolbarDeckPanel.showWidget(0);
		mainDeckPanel.showWidget(0);
	}

	private void showPdf() {
		toolbarDeckPanel.showWidget(1);
		mainDeckPanel.showWidget(1);
	}
	
	private void onClosePDF() {
		showSalary();
	}
	
	// --------------------------------------------- Toolbar Methods

	private void onDelete() {
		AonDialog deleteDialog = new AonDialog("Eliminar n\u00F3minas", new HTML("\u00BFDesea eliminar la n\u00F3minas seleccionadas\u003F"));
		deleteDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				List<SalaryInfo> alcatrazSalaries = salaryTable.getSelectedSalaries().stream().filter(salary -> salary.isAlcatraz()).collect(Collectors.toList());
				List<SalaryInfo> financeSalaries = salaryTable.getSelectedSalaries().stream().filter(salary -> salary.isFinance()).collect(Collectors.toList());
				
				if(!alcatrazSalaries.isEmpty()) 
					createAlcatrazWarning(alcatrazSalaries);
				else if(!financeSalaries.isEmpty()) {
					createFinanceWarning(financeSalaries);
				} else {
					enterpriseSalaryObject.deleteSalaries(
							salaryTable.getSelectedSalaries(), 
							s -> 
								reloadTable(success -> {
									Map<String, String> successMap = new HashMap<>();
									successMap.put("Borrado", "La(s) n\u00F3minas han sido eliminadas correctamente");
									AonMessagePanel.showSuccess(messagePanel, successMap);
								})
							, f -> {}
					);
				}
			}
		});
	}
	
	private void createAlcatrazWarning(List<SalaryInfo> alcatrazSalaries) {
		DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		String message = "No se pueden eliminar la n&oacute;minas que est&aacute;n presentadas en el <b>Modelo 111</b>. Estas n&oacute;minas son:<br><br>";
		for(SalaryInfo salary : alcatrazSalaries) {
			message += "&emsp;" + salary.getAlcatrazTerritory().getDescription() + ", " + salary.getAlcatrazYear() + " " + salary.getAlcatrazPeriod().getDescription()  + ".  " + salary.getEmployeeName() + " (" + formatDate.format(salary.getStartDate()) + " - " + formatDate.format(salary.getEndDate()) + ")<br>";
		}
		
		message += "<br>Para poder eliminar dichas n&oacute;minas, deber&aacute; eliminar primero el <b>Modelo 111</b> asociado.";
		
		AonDialog dialog = new AonDialog("Borraro", new HTML(message));
		dialog.info();
	}
	
	private void createFinanceWarning(List<SalaryInfo> financeSalaries) {
		DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
		
		String message = "No se pueden eliminar la n&oacute;minas que ya tienen <b>vencimientos</b> creados. Estas n&oacute;minas son:<br><br>";
		for(SalaryInfo salary : financeSalaries) {
			message += "&emsp;" + salary.getEmployeeName() + " (" + formatDate.format(salary.getStartDate()) + " - " + formatDate.format(salary.getEndDate()) + ")<br>";
		}
		
		message += "<br>Para poder eliminar dichas n&oacute;minas, deber&aacute; eliminar primero los <b>vencimientos</b> asociados.";
		
		AonDialog dialog = new AonDialog("Borraro", new HTML(message));
		dialog.info();
	}
	
	private void reloadTable(Consumer<List<SalaryInfo>> success) {
		this.enterpriseSalaryObject.getSalaries(
				s -> {
					initDatesListBox();
					initSuggestBox();
					filterCurrentYearSalaries();
					success.accept(s);
				}, 
				f -> {}
		);
	}

	private void onPDF() {
		AonMessagePanel.showLoading(messagePanel, "Cargando n\u00f3mina(s)...");
		
		List<Integer> salaryIds = new ArrayList<>();
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
			salaryIds.add(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
		
		enterpriseSalaryObject.getPDFSalaries(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId(), salaryIds,
				dataURI -> {
					AonMessagePanel.hideMessage(messagePanel);
					showPdf();
					pdfViewer.open(dataURI);
				},
				f -> AonMessagePanel.hideMessage(messagePanel));
	}

//	private void onPublish() {
//		onPublish("drive");
//	}

	private void onBidoqPublish() {
		onPublish("bidoq");
	}

	private void onEmail(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}

	// --------------------------------------------- Abstract Methods

	protected abstract void onBackClick();
	
}
