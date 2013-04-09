package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.CalcDialog.AcceptEvent;
import com.esferalia.aon.gwt.payroll.client.CalcDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.client.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
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
public class EmployeeTree implements EntryPoint, Employees.Listener {

	interface GWTResources extends ClientBundle {
		@NotStrict
		@Source("gwt.css")
		CssResource css();

		@Source("warn.png")
		ImageResource warn();

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
	}

	static String CALC_URL = URL.encode(GWT.getModuleBaseURL() + "calculate");

	static DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(CalculateService.DATE_FORMAT_PATTERN);

	/**
	 * A custom {@link Cell} used to render a string that contains the name of a
	 * fullname.
	 */

	static class MyTextCell extends AbstractCell<String> {

		/**
		 * The HTML templates used to render the cell.
		 */
		interface Templates extends SafeHtmlTemplates {
			/**
			 * The template for this Cell, which includes styles and a value.
			 * 
			 * @param styles
			 *            the styles to include in the style attribute of the
			 *            div
			 * @param value
			 *            the safe value. Since the value type is
			 *            {@link SafeHtml}, it will not be escaped before
			 *            including it in the template. Alternatively, you could
			 *            make the value type String, in which case the value
			 *            would be escaped.
			 * @return a {@link SafeHtml} instance
			 */
			@SafeHtmlTemplates.Template("<span style=\"{0}\">{1}</span>")
			SafeHtml cell(SafeStyles styles, SafeHtml value);
		}

		/**
		 * Create a singleton instance of the templates used to render the cell.
		 */
		private static Templates templates = GWT.create(Templates.class);

		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			/*
			 * Always do a null check on the value. Cell widgets can pass null
			 * to cells if the underlying data contains a null, or if the data
			 * arrives out of order.
			 */
			if (value == null) {
				return;
			}

			// If the value comes from the user, we escape it to avoid XSS
			// attacks.
			SafeHtml safeValue = SafeHtmlUtils.fromString(value);

			// Use the template to create the Cell's html.
			SafeStyles styles = SafeStylesUtils.forWhiteSpace(WhiteSpace.NOWRAP);
			
			SafeHtml rendered = templates.cell(styles, safeValue);
			sb.append(rendered);
		}

	}

	static class EmployeeCalcDialog extends CalcDialog<Employee> {

		public EmployeeCalcDialog() {

			// Full name.
			Column<Employee, String> fullNameColumn = new Column<Employee, String>(
					new MyTextCell()) {
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
					new MyTextCell()) {
				@Override
				public String getValue(Workplace workplace) {
					return workplace.getDescription();
				}
			};

			addColumn(descriptionColumn, "Centro");
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
		}

		void setEnterprise(Enterprise enterprise) {
			calcCmd.setEnterprise(enterprise);
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

	interface Binder extends UiBinder<Widget, EmployeeTree> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Employees employees;
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
	private Salary salary;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	private AgreementDraft agreementDraft;

	private ResultsPanel resultsPanel;

	private EmployeeContextMenu employeeContextMenu;
	private WorkplaceContextMenu workplaceContextMenu;
	private EnterpriseContextMenu enterpriseContextMenu;

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
		agreementDraft = new AgreementDraft();

		resultsPanel = new ResultsPanel();

		employees.addListener(this);

		employeeContextMenu = new EmployeeContextMenu();
		enterpriseContextMenu = new EnterpriseContextMenu();
		workplaceContextMenu = new WorkplaceContextMenu();

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

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {

	}

	// ------------------------------------------
	//
	// ------------------------------------------

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
}
