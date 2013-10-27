package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.esferalia.aon.gwt.payroll.client.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptEvent;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.gps.ReportConstants;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EmployeeTree implements EntryPoint, Employees.Listener, MetaData.Listener {

	static String CALC_URL = URL.encode(GWT.getModuleBaseURL() + "calculate");

	static DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(CalculateService.DATE_FORMAT_PATTERN);

	static class EmployeeCalcDialog extends CalcDialog<Employee> {

		public EmployeeCalcDialog() {

			// Full name.
			Column<Employee, String> fullNameColumn = new Column<Employee, String>(
					new TextCell()) {
				@Override
				public String getValue(Employee employee) {
					return employee.getFullname();
				}
			};

			addColumn(fullNameColumn, "Empleado");
		}

	}

	static class WorkPlaceCalcDialog extends CalcDialog<Workplace> {

		public WorkPlaceCalcDialog() {

			// Full name.
			Column<Workplace, String> descriptionColumn = new Column<Workplace, String>(
					new TextCell()) {
				@Override
				public String getValue(Workplace workplace) {
					return workplace.getDescription();
				}
			};

			addColumn(descriptionColumn, "Centro");
		}

	}

	static class WorkPlaceReportDialog extends ReportDialog<Workplace> {

		public WorkPlaceReportDialog() {

			// Full name.
			Column<Workplace, String> descriptionColumn = new Column<Workplace, String>(
					new TextCell()) {
				@Override
				public String getValue(Workplace workplace) {
					return workplace.getDescription();
				}
			};

			addColumn(descriptionColumn, "Hotel");
		}

	}

	class ShowResultsCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showResultsPanel();
		}

	}

	class NewEmployeeCommand implements ScheduledCommand {
		@Override
		public void execute() {
			// TODO Auto-generated method stub

		}
	}

	class CalcEnterpriseCommand implements ScheduledCommand, AcceptHandler,
			ReadyStateChangeHandler, CalculateService {

		WorkPlaceCalcDialog calcDialog;

		public CalcEnterpriseCommand() {
			calcDialog = new WorkPlaceCalcDialog();
			calcDialog.addAcceptHandler(this);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();

		}

		public void setEnterprise(Enterprise enterprise) {
			calcDialog.setData(enterprise.getWorkplaces());
			// TODO : Start date is January 1, 1970, 00:00:00 GMT ?.
			calcDialog.setMonth(new Date(0), null,
					DateUtils.getFirstDayOfMonth());
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();

			StringBuffer requestDataBuffer = new StringBuffer();
			requestDataBuffer.append(START_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getFirstDayOfMonth(month)));
			requestDataBuffer.append("&" + END_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));
			requestDataBuffer.append("&" + ISSUE_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));

			for (Workplace workplace : calcDialog.getSelectedData())
				requestDataBuffer.append("&" + WORKPLACES + "="
						+ workplace.getId());
			if (calcDialog.isSaveSelected())
				requestDataBuffer.append("&" + SAVE + "=true");

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", CALC_URL);
			xhr.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(this);
			xhr.send(requestDataBuffer.toString());

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
		}

		// --------------------------------------
		// ReadyStateChangeHandler
		// --------------------------------------

		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
				EmployeeTree.this.resultsPanel.setHTML(xhr.getResponseText());
			}
		}

	}

	class CalcEmployeeCommand implements ScheduledCommand, AcceptHandler,
			CalculateService, ReadyStateChangeHandler {

		private CalcDialog<Employee> calcDialog;

		public CalcEmployeeCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();
		}

		public void setEmployee(Employee employee) {
			calcDialog.setData(Collections.singletonList(employee));
			Date actualDate = DateUtils.before(
					DateUtils.after(new Date(), employee.getStartDate()),
					employee.getEndDate());
			calcDialog.setMonth(employee.getStartDate(), employee.getEndDate(),
					actualDate);
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();

			StringBuffer requestDataBuffer = new StringBuffer();
			requestDataBuffer.append(START_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getFirstDayOfMonth(month)));
			requestDataBuffer.append("&" + END_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));
			requestDataBuffer.append("&" + ISSUE_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));

			for (Employee employee : calcDialog.getSelectedData())
				requestDataBuffer.append("&" + EMPLOYEES + "="
						+ employee.getId());

			if (calcDialog.isSaveSelected())
				requestDataBuffer.append("&" + SAVE + "=true");

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", CALC_URL);
			xhr.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(this);
			xhr.send(requestDataBuffer.toString());

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
		}

		// --------------------------------------
		// ReadyStateChangeHandler
		// --------------------------------------

		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
				EmployeeTree.this.resultsPanel.setHTML(xhr.getResponseText());
			}
		}

	}

	class CalcWorkplaceCommand implements ScheduledCommand, AcceptHandler,
			ReadyStateChangeHandler, CalculateService {

		private CalcDialog<Employee> calcDialog;

		public CalcWorkplaceCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();

		}

		public void setWorkplace(final Workplace workplace) {
			AsyncEmployeeProvider employeeProvider = new AsyncEmployeeProvider() {

				@Override
				Date getEndDate() {
					return calcDialog.getMonth();
				}

				@Override
				Workplace getWorkplace() {
					return workplace;
				}

			};

			// TODO : Start date is January 1, 1970, 00:00:00 GMT ?.
			calcDialog.setMonth(new Date(0), null,
					DateUtils.getFirstDayOfMonth());

			calcDialog.setDataProvider(employeeProvider);
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();

			StringBuffer requestDataBuffer = new StringBuffer();
			requestDataBuffer.append(START_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getFirstDayOfMonth(month)));
			requestDataBuffer.append("&" + END_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));
			requestDataBuffer.append("&" + ISSUE_DATE + "="
					+ DATE_FORMAT.format(DateUtils.getLastDayOfMonth(month)));

			for (Employee employee : calcDialog.getSelectedData())
				requestDataBuffer.append("&" + EMPLOYEES + "="
						+ employee.getId());

			if (calcDialog.isSaveSelected())
				requestDataBuffer.append("&" + SAVE + "=true");

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", CALC_URL);
			xhr.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(this);
			xhr.send(requestDataBuffer.toString());

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
		}

		// --------------------------------------
		// ReadyStateChangeHandler
		// --------------------------------------

		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
				EmployeeTree.this.resultsPanel.setHTML(xhr.getResponseText());
			}
		}

	}

	static abstract class GPSReportEnterpriseCommand implements
			ScheduledCommand, AcceptHandler {

		WorkPlaceReportDialog reportDialog;

		public GPSReportEnterpriseCommand(String caption) {
			reportDialog = new WorkPlaceReportDialog();
			reportDialog.setCaption(caption);
			reportDialog.addAcceptHandler(this);
		}

		@Override
		public void execute() {
			reportDialog.center();
			reportDialog.show();

		}

		public void setEnterprise(Enterprise enterprise) {
			reportDialog.setData(enterprise.getWorkplaces());
		}

		Date getStartDate() {
			return reportDialog.getStartDate();
		}

		Date getEndDate() {
			return reportDialog.getEndDate();
		}

		boolean isAllSelected() {
			return reportDialog.isAllSelected();
		}

		Set<Workplace> getSelected() {
			return reportDialog.getSelectedData();
		}

		void submit(String fileName, Map<String, String> params) {

			StringBuffer query = new StringBuffer("?");
			for (Entry<String, String> param : params.entrySet())
				query.append(param.getKey() + "=" + param.getValue() + "&");

			String url = URL.encode(GWT.getModuleBaseURL() + "gps/" + fileName
					+ query);

			Window.open(url, "_blank", null);
		}

	}

	static class FTEReportEnterpriseCommand extends GPSReportEnterpriseCommand
			implements ReportConstants {

		public FTEReportEnterpriseCommand(String caption) {
			super(caption);
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(WorkPlaceReportDialog.AcceptEvent event) {
			DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");
			String fileName = "Informe FTE(" + format.format(getStartDate())
					+ ".." + format.format(getStartDate()) + ").csv";

			Map<String, String> params = new HashMap<String, String>();
			DateTimeFormat paramFormat = DateTimeFormat
					.getFormat(DATE_FORMAT_PATTERN);
			params.put(START_DATE_PARAM, paramFormat.format(getStartDate()));
			params.put(END_DATE_PARAM, paramFormat.format(getEndDate()));

			for (Workplace workplace : getSelected()) {
				params.put(WORKPLACE_PARAM, workplace.getId().toString());
			}

			params.put(REPORT_FTE_PARAM, Boolean.toString(true));

			submit(fileName, params);
		}

	}

	static class A3ReportEnterpriseCommand extends GPSReportEnterpriseCommand
			implements ReportConstants {

		public A3ReportEnterpriseCommand(String caption) {
			super(caption);
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(WorkPlaceReportDialog.AcceptEvent event) {
			DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");
			String fileName = "Informe A3(" + format.format(getStartDate())
					+ ".." + format.format(getStartDate()) + ").csv";

			Map<String, String> params = new HashMap<String, String>();
			DateTimeFormat paramFormat = DateTimeFormat
					.getFormat(DATE_FORMAT_PATTERN);
			params.put(START_DATE_PARAM, paramFormat.format(getStartDate()));
			params.put(END_DATE_PARAM, paramFormat.format(getEndDate()));

			for (Workplace workplace : getSelected()) {
				params.put(WORKPLACE_PARAM, workplace.getId().toString());
			}

			params.put(REPORT_A3_PARAM, Boolean.toString(true));

			submit(fileName, params);
		}

	}

	static class CTRLReportEnterpriseCommand extends GPSReportEnterpriseCommand
			implements ReportConstants {

		public CTRLReportEnterpriseCommand(String caption) {
			super(caption);
		}

		// --------------------------------------
		// AcceptHandler
		// --------------------------------------
		@Override
		public void onAccept(WorkPlaceReportDialog.AcceptEvent event) {
			DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd");
			String fileName = "Informe Control Festivos, Libres y Vacaciones ("
					+ format.format(getStartDate()) + ".."
					+ format.format(getStartDate()) + ").csv";

			DateTimeFormat paramFormat = DateTimeFormat
					.getFormat(DATE_FORMAT_PATTERN);
			Map<String, String> params = new HashMap<String, String>();
			params.put(START_DATE_PARAM, paramFormat.format(getStartDate()));
			params.put(END_DATE_PARAM, paramFormat.format(getEndDate()));

			for (Workplace workplace : getSelected()) {
				params.put(WORKPLACE_PARAM, workplace.getId().toString());
			}

			params.put(REPORT_CTRL_PARAM, Boolean.toString(true));

			submit(fileName, params);

		}

	}

	class WorkplaceContextMenu extends ContextMenu {

		CalcWorkplaceCommand calcCmd;

		public WorkplaceContextMenu() {

			MenuBar newPopup = new MenuBar(true);
			MenuItem newEmployeeItem = newPopup.addItem(
					getHTML("Contrato", AON.AON_ICON_EMPLOYEE,
							AON.AON_ICON_CMD_BUTTON), true,
					new NewEmployeeCommand());
			newEmployeeItem.setEnabled(false);

			MenuItem newItem = addItem("Nuevo", newPopup, AON.AON_ICON_RESET,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(),
					AON.AON_ICON_ACCEPT, AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			MenuItem runItem = addItem("Calcular",
					calcCmd = new CalcWorkplaceCommand(),
					AON.AON_ICON_TASK_START, AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME,
					AON.AON_ICON_CMD_BUTTON);

		}

		public void setWorkplace(Workplace workplace) {
			calcCmd.setWorkplace(workplace);
		}
	}

	class EnterpriseContextMenu extends ContextMenu {

		CalcEnterpriseCommand calcCmd;
		A3ReportEnterpriseCommand a3ReportCmd;
		FTEReportEnterpriseCommand fteReportCmd;
		CTRLReportEnterpriseCommand ctrlReportCmd;

		public EnterpriseContextMenu() {

			MenuBar newPopup = new MenuBar(true);

			MenuItem newWorkPlaceItem = newPopup.addItem(
					getHTML("Centro", AON.AON_ICON_WORKPLACE,
							AON.AON_ICON_CMD_BUTTON), true,
					new NewEmployeeCommand());
			newWorkPlaceItem.setEnabled(false);

			MenuItem newActivityItem = newPopup.addItem(
					getHTML("Actividad", AON.AON_ICON_INE,
							AON.AON_ICON_CMD_BUTTON), true,
					new NewEmployeeCommand());
			newActivityItem.setEnabled(false);

			addItem("Nuevo", newPopup, AON.AON_ICON_RESET,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(),
					AON.AON_ICON_ACCEPT, AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			addItem("Calcular", calcCmd = new CalcEnterpriseCommand(),
					AON.AON_ICON_TASK_START, AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME,
					AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Informe FTE",
					fteReportCmd = new FTEReportEnterpriseCommand(
							"Informe FTE..."), AON.AON_ICON_EXCEL,
					AON.AON_ICON_CMD_BUTTON);
			addItem("Informe Control Festivos, Libres y Vacaciones",
					ctrlReportCmd = new CTRLReportEnterpriseCommand(
							"Informe Control Festivos, Libres y Vacaciones..."),
					AON.AON_ICON_EXCEL, AON.AON_ICON_CMD_BUTTON);
			addItem("Informe A3",
					a3ReportCmd = new A3ReportEnterpriseCommand(
							"Informe A3..."), AON.AON_ICON_EXCEL,
					AON.AON_ICON_CMD_BUTTON);
		}

		void setEnterprise(Enterprise enterprise) {
			calcCmd.setEnterprise(enterprise);
			fteReportCmd.setEnterprise(enterprise);
			ctrlReportCmd.setEnterprise(enterprise);
		}

	}

	class EmployeeContextMenu extends ContextMenu {

		private CalcEmployeeCommand calcCmd;

		public EmployeeContextMenu() {

			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(),
					AON.AON_ICON_ACCEPT, AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			MenuItem runItem = addItem("Calcular",
					calcCmd = new CalcEmployeeCommand(),
					AON.AON_ICON_TASK_START, AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME,
					AON.AON_ICON_CMD_BUTTON);

		}

		public void setEmployee(Employee employee) {
			calcCmd.setEmployee(employee);
		}

	}

	abstract class AsyncEmployeeProvider extends AsyncDataProvider<Employee> {

		abstract Date getEndDate();

		abstract Workplace getWorkplace();

		/**
		 * {@link #onRangeChanged(HasData)} is called when the table requests a
		 * new range of data. You can push data back to the displays using
		 * {@link #updateRowData(int, List)}.
		 */
		@Override
		protected void onRangeChanged(HasData<Employee> display) {
			// Get the new range.
			final Range range = display.getVisibleRange();
			// Query the data asynchronously (RPC call).
			getServiceAsync().getEmployees(getWorkplace().getId(),
					getEndDate(), null, range.getStart(), range.getLength(),
					new AsyncCallback<List<Employee>>() {

						@Override
						public void onSuccess(List<Employee> result) {
							// Push the data to the displays. AsyncDataProvider
							// will only update
							// displays that are within range of the data.
							updateRowData(range.getStart(), result);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}
					});
		}

		EmployeesServiceAsync getServiceAsync() {
			return EmployeeTree.this.employees.getEmployeesService();
		}
	}

	private static EmployeeTree singlenton;

	interface Binder extends UiBinder<Widget, EmployeeTree> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Employees employees;
	@UiField
	MetaData metaData;
	@UiField
	DetailPanel employeeDetail;
	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	MinimizePanel footPanel;
	@UiField
	TabLayoutPanel footTabPanel;

	private JSF jsf;
	private Documents documents;
	private Cost cost;
	private Irpf irpf;
	private Salary salary;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	private EventsDraft eventsDraft;
	private AgreementDraft agreementDraft;

	private ResultsPanel resultsPanel;
	
	private EmployeeContextMenu employeeContextMenu;
	private WorkplaceContextMenu workplaceContextMenu;
	private EnterpriseContextMenu enterpriseContextMenu;

	private Enterprise enterprise;


	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<MainEntryPoint.GWTResources> create(
				MainEntryPoint.GWTResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.AonResources> create(
				MainEntryPoint.AonResources.class).css().ensureInjected();

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
		irpf = new Irpf();
		salary = new Salary();
		documents = new Documents();
		eventsDraft = new EventsDraft();
		salaryDraft = new SalaryDraft();
		salaryPreview = new SalaryPreview();
		agreementDraft = new AgreementDraft();

		resultsPanel = new ResultsPanel();

		employees.addListener(this);
		metaData.addListener(this);

		employeeContextMenu = new EmployeeContextMenu();
		enterpriseContextMenu = new EnterpriseContextMenu();
		workplaceContextMenu = new WorkplaceContextMenu();

		singlenton = this;

		export2JS();

	}

	// ---------------------------------------------- Employees.Listener methods

	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		employeeDetail.setWidget(jsf);
		jsf.enterpriseSelected(enterprise.getId());
		this.enterprise = enterprise;
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
	public void onIrpfsSelected(IrpfDocuments docs) {
		employeeDetail.setWidget(irpf);
		irpf.setIrpfDocuments(docs);
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
	public void onSalaryPreviewSelected(
			SalaryPreviewDocument salaryPreviewDocument) {
		employeeDetail.setWidget(salaryPreview);
		salaryPreview.setSalaryPreviewDocument(salaryPreviewDocument);
	}

	@Override
	public void onWorkplaceContextMenu(Workplace workplace,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		workplaceContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		workplaceContextMenu.setWorkplace(workplace);
		workplaceContextMenu.show();
	}

	@Override
	public void onEnterpriseContextMenu(Enterprise enterprise,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		enterpriseContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		enterpriseContextMenu.setEnterprise(enterprise);
		enterpriseContextMenu.show();

	}

	@Override
	public void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		employeeContextMenu.setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		employeeContextMenu.setEmployee(employee);
		employeeContextMenu.show();
	}

	@Override
	public void onAgreementDraftSelected(
			AgreementDraftObject agreementDraftObject) {
		employeeDetail.setWidget(agreementDraft);
		agreementDraft.setAgreementDraftObject(agreementDraftObject);
	}

	@Override
	public void onEventsDraftSelected(EventsDraftObject eventsDraftObject) {
		employeeDetail.setWidget(eventsDraft);
		eventsDraft.setEventsDraftObject(eventsDraftObject);

	}
	// ---------------------------------------------- MetaData.Listener methods
	
	@Override
	public void onBonusConceptsSelected() {
		employeeDetail.setWidget(jsf);
		jsf.bonusConceptsSelected();
	}
	
	@Override
	public void onDeductionConceptsSelected() {
		employeeDetail.setWidget(jsf);
		jsf.deductionConceptsSelected();
	}
	
	@Override
	public void onPaymentConceptsSelected() {
		employeeDetail.setWidget(jsf);
		jsf.paymentConceptsSelected();
	}

	// ------------------------------------------------------- UiHandler methods

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {

	}

	// --------------------------------------------------------- Private methods

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void showResultsPanel() {
		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		EmployeeTree.this.footTabPanel.add(EmployeeTree.this.resultsPanel, tab);
		EmployeeTree.this.splitLayoutPanel.setWidgetSize(
				EmployeeTree.this.footPanel, Window.getClientHeight() / 4);
	}

	private static void showSalaryDraft(int employeeId, String startDateString,
			String endDateString) {
		EmployeeTree employeeTree = getEmployeeTree();
		DateTimeFormat dateTimeFormat = DateTimeFormat
				.getFormat(CalculateService.DATE_FORMAT_PATTERN);

		com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = new com.esferalia.aon.gwt.payroll.shared.SalaryDraft();
		Employee employee = new Employee();
		employee.setId(employeeId);
		salaryDraft.setEmployee(employee);
		Date startDate = dateTimeFormat.parse(startDateString);
		salaryDraft.setStartDate(startDate);
		Date endDate = dateTimeFormat.parse(endDateString);
		salaryDraft.setEndDate(endDate);
		salaryDraft.setIssueDate(endDate);
		salaryDraft.setType(Type.SALARY);

		EmployeesServiceAsync employeesServiceAsync = employeeTree.employees
				.getEmployeesService();
		SalaryDraftObject draftObject = new SalaryDraftObject(salaryDraft,
				employeesServiceAsync);

		employeeTree.employeeDetail.setWidget(employeeTree.salaryDraft);
		employeeTree.salaryDraft.setSalaryDraftObject(draftObject);
	}

	private static void showEmployee(int employeeId) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employeeDetail.setWidget(employeeTree.jsf);
		employeeTree.jsf.employeeSelected(employeeId);
	}

	private static EmployeeTree getEmployeeTree() {
		return singlenton;
	}

	private static void a3Report() {
		singlenton.enterpriseContextMenu.setEnterprise(singlenton.enterprise);
		singlenton.enterpriseContextMenu.a3ReportCmd.execute();
	}

	private static void fteReport() {
		singlenton.enterpriseContextMenu.setEnterprise(singlenton.enterprise);
		singlenton.enterpriseContextMenu.fteReportCmd.execute();
	}

	private static void ctrlReport() {
		singlenton.enterpriseContextMenu.setEnterprise(singlenton.enterprise);
		singlenton.enterpriseContextMenu.ctrlReportCmd.execute();
	}

	private static native void export2JS() /*-{
											$wnd.a3Report = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::a3Report());
											$wnd.fteReport = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::fteReport());
											$wnd.ctrlReport = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::ctrlReport());
											$wnd.showEmployee = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::showEmployee(I));
											$wnd.showSalaryDraft = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::showSalaryDraft(ILjava/lang/String;Ljava/lang/String;));
											}-*/;

}
