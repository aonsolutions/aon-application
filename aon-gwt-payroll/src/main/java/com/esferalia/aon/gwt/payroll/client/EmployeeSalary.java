package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class EmployeeSalary extends Composite {
	
	// --------------------------------------------- Salary Table Impl
	
	private class SalaryTableImpl extends SalaryTable {

		@Override
		protected void onSelectionSalaryChange(boolean isSomethingSelected, boolean hasSettleSelected) {
			enableDisableButtons(isSomethingSelected, hasSettleSelected);
			fireEnableDisableButtons(isSomethingSelected, hasSettleSelected);
		}
		
	}
	
	// --------------------------------------------- UiBinder
	
	private static EmployeeSalaryUiBinder uiBinder = GWT.create(EmployeeSalaryUiBinder.class);

	interface EmployeeSalaryUiBinder extends UiBinder<Widget, EmployeeSalary> {}
	
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
	
	private void sendEmail(com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type type) {
		Integer enterpriseID = ((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId();
		
		HashMap<String, String> params = new HashMap<>();
		params.put("url", GWT.getModuleBaseURL()+ "salary_connor_macleod/");
		params.put("type", "salary");
		params.put("name", "salaries.pdf");
		params.put("enterprise", String.valueOf(enterpriseID));
		params.put("domain", Wnd.getCurrentDomainNameURL());
		params.put("user", Wnd.getCurrentUser());
		
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
			params.put("id" + i, ""+((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
		
		
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
						AonMessagePanel.showSuccess(messagePanel, employeeSalaryObject.getEmailStatus());
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
		
		public EmailContextMenu() {
			
			newEmailEmployees = addItem("Email empleados", new NewEmailEmployeesCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newEmailEmployees.ensureDebugId("newEmailEmployees");
			
			newEmailEnterprise = addItem("Email empresa", new NewEmailEnterpriseCommand(), 
					AON.CSS.aonIconEmail(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newEmailEnterprise.ensureDebugId("newEmailEnterprise");
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
	
	private EmployeeSalaryObject employeeSalaryObject;
	
	private List<Listener> listeners;
	
	private SalaryTable salaryTable;
	
	private EmailContextMenu contextMenu;
	
	private AonToolbarButton deleteButton;
	private AonToolbarButton pdfButton;
	private AonToolbarButton pdfSettleButton;
//	private AonToolbarButton publishButton;
	private AonToolbarButton bidoqPublishButton;
	private AonToolbarButton email;

	// --------------------------------------------- Constructor

	protected EmployeeSalary() {
		this.toolbar = new AonToolbar("N\u00f3minas");
		this.toolbarPDFViewer = new AonToolbar("N\u00f3minas");
		
		salaryTable = new SalaryTableImpl();
		initWidget(uiBinder.createAndBindUi(this)); 
		
		// Init toolbar
		getToolbarPanel();
		getToolbarPDFViewerPanel();
		
		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 200.00, Unit.PX);
		
		contextMenu = new EmailContextMenu();
		
		mainContainer.add(salaryTable);
		
		salaryTable.setEmployeeView();
		salaryTable.sortTableByStartDate();
		
		listeners = new LinkedList<>();
		
		initFilterPanel();

		showSalary();
	}
	
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title );
		toolbarPDFViewer.setTitle(title );
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
		dateFilterList.addItem("\u00DAltimo trimestre");
		dateFilterList.addItem("\u00DAltimo semestre");
		dateFilterList.addItem("A\u00F1o actual");
		dateFilterList.addItem("A\u00F1o anterior");
		dateFilterList.setSelectedIndex(0);
	}
	

	// --------------------------------------------- setEmployeeSalaryObject

	public void setEmployeeSalaryObject(EmployeeSalaryObject employeeSalaryObject) {
		this.employeeSalaryObject = employeeSalaryObject;
		this.employeeSalaryObject.getSalariesDates(
				s -> {
					showSalary();
					initDatesListBox();
					filterCurrentYearSalaries();
				}, 
				f -> {}
		);
	}
	

	private void initDatesListBox() {
		// Add year to listboxes
		yearTillT.clear();
		yearTTo.clear();
		
		Integer firstPayrollYear = DateUtils.getYear(this.employeeSalaryObject.getMinDate());
		Integer lastPayrollYear = DateUtils.getYear(this.employeeSalaryObject.getMaxDate());
		Integer diffYears = lastPayrollYear - firstPayrollYear;
		
		for(int i = 0; i <= diffYears; i++) {
			Integer year = lastPayrollYear - i;
			String yearStr = year.toString();
			yearTillT.addItem(yearStr, yearStr);
			yearTTo.addItem(yearStr, yearStr);
		}
	}
	

	private void filterCurrentYearSalaries() {
		SalaryInfoFilter filter = employeeSalaryObject.getFilter();
		
		Date startDate = DateUtils.addMonths2Date(DateUtils.getFirstDayOfMonth(), -1);
		Date endDate = DateUtils.getLastDayOfMonth(employeeSalaryObject.getMaxDate());
		
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
		
		this.employeeSalaryObject.getSalaries(
				s -> {
					setSettlePDFVisibility();
					salaryTable.setSalariesList(employeeSalaryObject.getEmployeeSalaries());
					initSalariesTable();
				}, f -> { }
		);
	}
	
	
	// --------------------------------------------- Init SalaryTable

	private void initSalariesTable() {
		//Show buttons
//		this.publishButton.setVisible(!Wnd.getCurrentDomainNameURL().contains("ayudat"));
		this.bidoqPublishButton.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));
		
		//Disable buttons till any salary selected
		enableDisableButtons(false, false);
		
		salaryTable.initSalariesTable();
	}
	
	
	// --------------------------------------------- UI Handlers

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
		} else
			showHideDatesMessage(true);
	}

	// --------------------------------------------- Filter Salaries
	
	private void filterSalaries() {
		SalaryInfoFilter filter = employeeSalaryObject.getFilter();
		
		// Date Filter
		filter.setDateTillT(getDateTillT());
		filter.setDateTTo(getDateTTo());
		
		setSelectedValueLB(monthTillT, DateUtils.getMonth(filter.getDateTillT()) + "");
		setSelectedValueLB(monthTTo, DateUtils.getMonth(filter.getDateTTo()) + "");
		
		setSelectedValueLB(yearTillT, DateUtils.getYear(filter.getDateTillT()) + "");
		setSelectedValueLB(yearTTo, DateUtils.getYear(filter.getDateTTo()) + "");
		
		// Salary Types
		List<Integer> salaryTypes = new ArrayList<>();
		
		if(salaryCB.getValue()) salaryTypes.add(0);
		if(extraCB.getValue()) salaryTypes.add(1);
		if(settleCB.getValue()) salaryTypes.add(2);
		if(delayCB.getValue()) salaryTypes.add(3);
		
		filter.setSalaryTypes(salaryTypes);
		
		this.employeeSalaryObject.getSalaries(
				s -> {
					setSettlePDFVisibility();
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
	
	private void enableDisableButtons(boolean isSomethingSelected, boolean hasSettleSelected) {
		deleteButton.setEnabled(isSomethingSelected);
    	pdfButton.setEnabled(isSomethingSelected);
    	pdfSettleButton.setEnabled(hasSettleSelected);
//    	publishButton.setEnabled(isSomethingSelected);
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
		for(SalaryInfo salaryInfo : this.employeeSalaryObject.getEmployeeSalaries())
			if(salaryInfo.getType() == Type.SETTLE) 
				return true;
		
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
	
	// --------------------------------------------- Toolbar
	
	private void getToolbarPanel() {

		deleteButton = new AonToolbarButton( AON.MSG.deleteAction(), AON.CSS.aonIconDelete() );
		deleteButton.addClickHandler(e -> onDelete());	
		toolbar.add(deleteButton);
		
		pdfButton = new AonToolbarButton( AON.MSG.printPDF(), AON.CSS.aonIconPdf());
		pdfButton.addClickHandler(e -> onPDF());	
		toolbar.add(pdfButton);
		
		pdfSettleButton = new AonToolbarButton( "Carta Finiquito", AON.CSS.aonIconPdf());
		pdfSettleButton.addClickHandler(e -> onPDFSettle());	
		pdfSettleButton.setVisible(false);
		toolbar.add(pdfSettleButton);
		
//		publishButton = new AonToolbarButton( "Drive", AON.CSS.aonIconDrive());
//		publishButton.addClickHandler(e -> onPublish());	
//		toolbar.add(publishButton);
		
		bidoqPublishButton = new AonToolbarButton( "Bidoq", "aon-icon-bidoq");
		bidoqPublishButton.addClickHandler(e -> onBidoqPublish());	
		bidoqPublishButton.setVisible(Wnd.getCurrentDomainNameURL().contains("ayudat"));
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
		onSalaryShow();
	}

	private void showPdf() {
		toolbarDeckPanel.showWidget(1);
		mainDeckPanel.showWidget(1);
		onPDFShow();
	}
	
	public void onClosePDF() {
		showSalary();
	}
	
	// --------------------------------------------- Toolbar Methods

	public void onDelete() {
		AonDialog deleteDialog = new AonDialog("Eliminar n\u00F3minas", new HTML("\u00BFDesea eliminar la n\u00F3minas seleccionadas\u003F"));
		deleteDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				List<SalaryInfo> alcatrazSalaries = salaryTable.getSelectedSalaries().stream().filter(salary -> salary.isAlcatraz()).collect(Collectors.toList());
				if(alcatrazSalaries.isEmpty()) {
					employeeSalaryObject.deleteSalaries(
							salaryTable.getSelectedSalaries(), 
							s -> 
								reloadTable(success -> {
									Map<String, String> successMap = new HashMap<>();
									successMap.put("Borrado", "La(s) n\u00F3minas han sido eliminadas correctamente");
									AonMessagePanel.showSuccess(messagePanel, successMap);
								})
							, f -> {}
					);
				} else
					createAlcatrazWarning(alcatrazSalaries);
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
	
	private void reloadTable(Consumer<List<SalaryInfo>> success) {
		this.employeeSalaryObject.getSalaries(
				s -> {
					initDatesListBox();
					setSettlePDFVisibility();
					filterCurrentYearSalaries();
					success.accept(s);
				}, 
				f -> {}
		);
	}

	public void onPDFSettle() {
		Optional<SalaryInfo> settle = salaryTable.getSelectedSalaries().stream().filter(salary -> salary.getType().equals(Type.SETTLE)).findFirst();
		
		if(settle.isPresent()) {
			AonMessagePanel.showLoading(messagePanel, "Cargando carta finiquito...");
			
			employeeSalaryObject.getPDFSettle(settle.get().getId(), 
					dataURI -> {
						AonMessagePanel.hideMessage(messagePanel);
						showPdf();
						pdfViewer.open(dataURI);
					},
					f -> AonMessagePanel.hideMessage(messagePanel));
		}
	}
	
	public void onPDF() {
		AonMessagePanel.showLoading(messagePanel, "Cargando n\u00f3mina(s)...");
		
		List<Integer> salaryIds = new ArrayList<>();
		for(int i=0; i<salaryTable.getSelectedSalaries().size(); i++)
			salaryIds.add(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[i]).getId());
		
		employeeSalaryObject.getPDFSalaries(((SalaryInfo)salaryTable.getSelectedSalaries().toArray()[0]).getEnterpriseId(), salaryIds,
				dataURI -> {
					AonMessagePanel.hideMessage(messagePanel);
					showPdf();
					pdfViewer.open(dataURI);
				},
				f -> AonMessagePanel.hideMessage(messagePanel));
		
	}

//	public void onPublish() {
//		onPublish("drive");
//	}

	public void onBidoqPublish() {
		onPublish("bidoq");
	}

	public void onEmail(ClickEvent e) {
		NativeEvent nativeEvent = e.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}

	// -------------------------------------------------- ContrataEmployee.Methods
	
	public void hideToolbar(){
		dockLayoutPanel.remove(toolbarDeckPanel);
		filterSalaryPanel.getElement().getStyle().setMarginTop(0, Unit.PX);
		mainContainer.getElement().getStyle().setMarginTop(0, Unit.PX);
	}

	public void removeMainMT() {
		mainContainer.getElement().getStyle().setMarginTop(0, Unit.PX);
	}
	
	public void setContrataView() {
		salaryTable.salaryDG.removeColumn(7);
	}
	
	// -------------------------------------------------- AbstractMethods
	
	protected abstract void fireEnableDisableButtons(boolean isSomethingSelected, boolean hasSettleSelected);

	protected abstract void onSalaryShow();

	protected abstract void onPDFShow();

	
}
