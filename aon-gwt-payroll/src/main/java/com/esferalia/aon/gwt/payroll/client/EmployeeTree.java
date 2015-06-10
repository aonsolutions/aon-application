package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.CalculateService.DUPLICATE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.END_DATE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.ISSUE_DATE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.OVERWRITE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.SAVE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.START_DATE;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.WORKPLACES;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.metrics.StatsEventLogger;
import com.esferalia.aon.gwt.common.client.widget.Calendar;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel.ClearEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel.ClearHandler;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptEvent;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.storage.client.Storage;
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
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class EmployeeTree implements EntryPoint, Employees.Listener,
		MetaData.Listener, Cost.Listener, Salary.Listener {

	static final byte SAVE_OPTION = 0x01;
	static final byte OVERWRITE_OPTION = 0x02;
	static final byte DUPLICATE_OPTION = 0x04;

	private static final int RESULTS_LIMIT = 100;

	static String SHARE_URL = URL.encode(GWT.getModuleBaseURL() + "share");
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

	class ShowResultsCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showResultsPanel();
		}

	}

	class NewEmployeeCommand implements ScheduledCommand {
		@Override
		public void execute() {

		}
	}

	class CopyEmployeeCommand implements ScheduledCommand {

		private Employee employeeCopy;

		@Override
		public void execute() {
			setEmployeeCopy(singlenton.employee);			
			String item = employee2Json(employee);
			storage.setItem(EMPLOYEE, item);
			setPasteItemVisible(true);
		}

		private void setEmployeeCopy(Employee employee) {
			this.employeeCopy = employee;
		}

		private void setPasteItemVisible(boolean visible) {
			pasteItem.setVisible(visible);
		}

		public Employee getEmployeeCopy() {
			return this.employeeCopy;
		}
	}

	class PasteEmployeeCommand implements ScheduledCommand,
			EmployeePopupCopy.Listener {

		@Override
		public void execute() {
			paste = new EmployeePopupCopy();
			paste.addListener(this);
			Employee aux = singlenton.getEmployeeContextMenu().getEmployeeCopy();
			if(singlenton.avaiableEmployees.containsKey(aux.getDocument()) == false)
				existPerson(aux);
			else
				showPopUpPanel();
		}

		private void showPopUpPanel() {
			setEmployeePaste(singlenton.getEmployeeContextMenu().getEmployeeCopy());
			setMapAvaiableEmployees(singlenton.avaiableEmployees);
			paste.showPopUpPanel();
		}

		@Override
		public void onAcceptClick(Employee pasteEmployee, boolean value) {
			int workplaceId = workplace.getId();
			int contractId = singlenton.getEmployeeContextMenu().getEmployeeCopy()
					.getId();
			String document = pasteEmployee.getDocument();
			Date startDate = pasteEmployee.getStartDate();
			Date endDate = (pasteEmployee.getEndDate() != null) ? pasteEmployee
					.getEndDate() : null;

			pasteContract(workplaceId, contractId, document, startDate,
					endDate, value, null);
			paste.hide();
		}

		private void setEmployeePaste(Employee employee) {			
			paste.setEmployee(employee);
		}

		private void setMapAvaiableEmployees(Map<String, String> map) {	
			paste.setMapAvaiableEmployees(map);
		}

		private void pasteContract(int workplaceId, int contractId,
				String document, Date startDate, Date endDate, boolean check,
				final AsyncCallback<Employee> callback) {

			employees.getEmployeesService().pasteContract(workplaceId,
					contractId, document, startDate, endDate, check,
					new AsyncCallback<Employee>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Employee result) {
							employees.onEnterprise(enterprise);
							callback.onSuccess(result);
						}
					});
		}
		
		private void existPerson(final Employee employee) {	
			
			if(Window.confirm(employee.getFullname() + 
					" no se encuentra en el dominio. \u00BFDesea insertar "
					+ "el registro\u003F")) {
				
				singlenton.employees.getEmployeesService().insertPerson(employee, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(Void result) {
						singlenton.avaiableEmployees.put(employee.getDocument(), 
								employee.getFullname());
						PasteEmployeeCommand.this.showPopUpPanel();
					}
				});
			}
		}
	}

	class DeleteEmployeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			deleteContract(singlenton.employee, null);
		}

		private void deleteContract(Employee employee,
				final AsyncCallback<Void> callback) {

			employees.getEmployeesService().moveContractId(employee,
					new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Void result) {
							employees.onEnterprise(enterprise);
							callback.onSuccess(result);
						}
					});
		}

	}

	class CalcEnterpriseCommand implements ScheduledCommand, AcceptHandler,
			AsyncCallback<JsSalaryResult>, SelectionHandler<JsSalaryResult>,
			ClearHandler, Handler {

		SalaryResultsGrid resultsGrid;
		WorkPlaceCalcDialog calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcEnterpriseCommand() {
			resultsGrid = new SalaryResultsGrid();
			calcDialog = new WorkPlaceCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			resultsGrid.addSelectionHandler(this);
			resultsGrid.addAttachHandler(this);
			resultsGrid.setPageSize(RESULTS_LIMIT);
			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			resultsDataProvider.addDataDisplay(resultsGrid);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();

		}

		public void setEnterprise(Enterprise enterprise) {
			calcDialog.setData(enterprise.getWorkplaces());
		}

		// ------------------------------------------------------ AcceptHandler
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();
			Date startDate = DateUtils.getFirstDayOfMonth(month);
			Date endDate = DateUtils.getLastDayOfMonth(month);

			Set<Workplace> workplaces = calcDialog.getSelectedData();

			resultsPanel.setWidget(resultsGrid);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= DUPLICATE_OPTION;

			EmployeeTree.calculate(startDate, endDate, WORKPLACES, workplaces,
					optionsBits, this);

			clear();

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
		}

		// --------------------------------------- AsyncCallback<JsSalaryResult>

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(JsSalaryResult result) {
			resultsDataProvider.getList().add(result);
		}

		// ----------------------------------------- SelectionHandler<TreeItem>
		@Override
		public void onSelection(SelectionEvent<JsSalaryResult> event) {
			onSalaryResultSelected(event.getSelectedItem());
		}

		// ------------------------------------------------------------ Handler

		@Override
		public void onAttachOrDetach(AttachEvent event) {
			if (event.isAttached())
				registration = resultsPanel.addClearHandler(this);
			else
				registration.removeHandler();
		}

		// ------------------------------------------------------- ClearHandler

		@Override
		public void onClear(ClearEvent event) {
			clear();
		}

		// ---------------------------------------------------- Private methods

		private void clear() {
			resultsDataProvider.getList().clear();
		}

		private void onSalaryResultSelected(JsSalaryResult salaryResult) {
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
		}

	}

	class CalcEmployeeCommand implements ScheduledCommand, AcceptHandler,
			CalculateService, SelectionHandler<JsSalaryResult>, ClearHandler,
			Handler, AsyncCallback<JsSalaryResult> {

		private SalaryResultsGrid resultsGrid;
		private CalcDialog<Employee> calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcEmployeeCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			resultsGrid = new SalaryResultsGrid();
			resultsGrid.addAttachHandler(this);
			resultsGrid.setPageSize(RESULTS_LIMIT);
			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			resultsDataProvider.addDataDisplay(resultsGrid);
			resultsGrid.addSelectionHandler(this);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();
		}

		public void setEmployee(Employee employee) {
			calcDialog.setData(Collections.singletonList(employee));

			Date actualDate = DateUtils.after(new Date(),
					employee.getStartDate());
			actualDate = DateUtils.before(actualDate, employee.getEndDate());

			calcDialog.setStartMonth(employee.getStartDate());
			calcDialog.setEndMonth(employee.getEndDate());
			calcDialog.setMonth(actualDate);
		}

		// ------------------------------------------------------ AcceptHandler
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();

			Date startDate = DateUtils.getFirstDayOfMonth(month);
			Date endDate = DateUtils.getLastDayOfMonth(month);

			Set<Employee> employees = calcDialog.getSelectedData();

			resultsPanel.setWidget(resultsGrid);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= DUPLICATE_OPTION;

			EmployeeTree.calculate(startDate, endDate, EMPLOYEES, employees,
					optionsBits, this);
			clear();

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'

		}

		// ------------------------------------------------------------ Handler

		@Override
		public void onAttachOrDetach(AttachEvent event) {
			if (event.isAttached())
				registration = resultsPanel.addClearHandler(this);
			else
				registration.removeHandler();
		}

		// ------------------------------------------------------- ClearHandler

		@Override
		public void onClear(ClearEvent event) {
			clear();
		}

		// ----------------------------------------- SelectionHandler<TreeItem>
		@Override
		public void onSelection(SelectionEvent<JsSalaryResult> event) {
			onSalaryResultSelected(event.getSelectedItem());
		}

		// --------------------------------------- AsyncCallback<JsSalaryResult>

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(JsSalaryResult result) {
			resultsDataProvider.getList().add(result);
		}

		// ---------------------------------------------------- Private methods

		private void clear() {
			resultsDataProvider.getList().clear();
			resultsDataProvider.flush();
		}

		private void onSalaryResultSelected(JsSalaryResult salaryResult) {
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
		}
	}

	class CalcWorkplaceCommand implements ScheduledCommand, AcceptHandler,
			CalculateService, AsyncCallback<JsSalaryResult>,
			SelectionHandler<JsSalaryResult>, ClearHandler, Handler {

		private SalaryResultsGrid resultsGrid;
		private CalcDialog<Employee> calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcWorkplaceCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			resultsGrid = new SalaryResultsGrid();
			resultsGrid.addAttachHandler(this);
			resultsGrid.setPageSize(RESULTS_LIMIT);
			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			resultsDataProvider.addDataDisplay(resultsGrid);
			resultsGrid.addSelectionHandler(this);

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

			calcDialog.setDataProvider(employeeProvider);
		}

		// ------------------------------------------------------ AcceptHandler
		@Override
		public void onAccept(AcceptEvent event) {
			Date month = calcDialog.getMonth();
			Date startDate = DateUtils.getFirstDayOfMonth(month);
			Date endDate = DateUtils.getLastDayOfMonth(month);

			Set<Employee> employees = calcDialog.getSelectedData();

			resultsPanel.setWidget(resultsGrid);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= DUPLICATE_OPTION;

			EmployeeTree.calculate(startDate, endDate, EMPLOYEES, employees,
					optionsBits, this);
			clear();
			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
		}

		// ------------------------------------------------------------ Handler

		@Override
		public void onAttachOrDetach(AttachEvent event) {
			if (event.isAttached())
				registration = resultsPanel.addClearHandler(this);
			else
				registration.removeHandler();
		}

		// ------------------------------------------------------- ClearHandler

		@Override
		public void onClear(ClearEvent event) {
			clear();
		}

		// ----------------------------------------- SelectionHandler<TreeItem>
		@Override
		public void onSelection(SelectionEvent<JsSalaryResult> event) {
			onSalaryResultSelected(event.getSelectedItem());
		}

		// --------------------------------------- AsyncCallback<JsSalaryResult>

		@Override
		public void onFailure(Throwable caught) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(JsSalaryResult result) {
			resultsDataProvider.getList().add(result);

		}

		// ---------------------------------------------------- Private methods

		private void clear() {
			resultsDataProvider.getList().clear();
		}

		private void onSalaryResultSelected(JsSalaryResult salaryResult) {
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
		}
	}

	class WorkplaceContextMenu extends ContextMenu {

		CalcWorkplaceCommand calcCmd;
		PasteEmployeeCommand pasteCmd;

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
			pasteItem = addItem("Pegar", pasteCmd = new PasteEmployeeCommand(),
					AON.AON_CSS.aonIconPaste(), AON.AON_ICON_CMD_BUTTON);
			pasteItem.setVisible(false);
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

		private void setEmployee(Employee employee) {
			pasteCmd.setEmployeePaste(employee);
		}

		private void setMapAvaiableEmployees(Map<String, String> map) {
			pasteCmd.setMapAvaiableEmployees(map);
		}

		private void showPastePanel() {
			pasteCmd.showPopUpPanel();
		}
		
		public void pasteContract () {
			pasteCmd.execute();
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
		private CopyEmployeeCommand copyCmd;
		private DeleteEmployeeCommand deleteCmd;

		public EmployeeContextMenu() {

			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(),
					AON.AON_ICON_ACCEPT, AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			addItem("Copiar", copyCmd = new CopyEmployeeCommand(),
					AON.AON_ICON_COPY, AON.AON_ICON_CMD_BUTTON);
			addItem("Eliminar", deleteCmd = new DeleteEmployeeCommand(),
					AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
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

		public void setCopyEmployee(Employee employee) {
			copyCmd.setEmployeeCopy(employee);
		}

		public Employee getEmployeeCopy() {
			return copyCmd.getEmployeeCopy();
		}

		public void deleteContract() {
			deleteCmd.execute();
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
	
	private static final String EMPLOYEE = "C-EMPLOYEE";
	
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
	private Statistics stats;
	private Reports reports;
	private ITEditor it;
	private CalendarDraft calendarDraft;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	private EventsDraft eventsDraft;
	private EmployeeEventsDraft employeeEventsDraft;
	private CategoryDraft categoryDraft;
	private AgreementDraft agreementDraft;
	private BonusEditor bonusEditor;
	private PaymentEditor paymentEditor;
	private DeductionEditor deductionEditor;
	private EmployeePopupCopy paste;

	private ResultsPanel resultsPanel;

	private EmployeeContextMenu employeeContextMenu;
	private WorkplaceContextMenu workplaceContextMenu;
	private EnterpriseContextMenu enterpriseContextMenu;

	private Employee employee;
	private Workplace workplace;
	private Enterprise enterprise;

	private ShareResultsGrid shareResultsGrid;
	private ListDataProvider<JsShareResult> shareResultsProvider;

	private Map<String, String> avaiableEmployees;

	private MenuItem pasteItem;

	

	private Storage storage;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		logEvent("start");
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(
				MainEntryPoint.CodeMirrorResources.class).css()
				.ensureInjected();
		logEvent("richStylesInjected");

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		logEvent("uiCreatedAndBound");

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		// Window.enableScrolling(false);
		// Window.setMargin("0px");

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		// RootPanel root = RootPanel.get("rootPanel");
		root.add(ui);
		logEvent("addedToRootPanel");
		
		//LocalStorage Items
		storage = Storage.getLocalStorageIfSupported();
		
		jsf = new JSF();
		logEvent("jsfWidgetCreated");


		employees.addListener(this);
		metaData.addListener(this);

		resultsPanel = new ResultsPanel();
		shareResultsGrid = new ShareResultsGrid();
		shareResultsProvider = new ListDataProvider<JsShareResult>();
		shareResultsProvider.addDataDisplay(shareResultsGrid);		
		logEvent("resultsWidgetsCreated");

		singlenton = this;
		
		export2JS();
		logEvent("end");

	}

	// --------------------------------------------------- Cost.Listener methods

	@Override
	public void onPublish(CostDocuments documents) {
		class Callback implements AsyncCallback<JsShareResult> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(JsShareResult result) {
				shareResultsProvider.getList().add(result);
			}
		}

		shareResultsProvider.getList().clear();
		resultsPanel.setWidget(shareResultsGrid);

		com.esferalia.aon.gwt.payroll.shared.Cost cost = documents.getCosts()
				.get(documents.getCurrentIndex());
		share(cost, new Callback());

		showResultsPanel();

	}

	// ------------------------------------------------- Salary.Listener methods

	@Override
	public void onPublis(SalaryDocuments documents) {
		class Callback implements AsyncCallback<JsShareResult> {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(JsShareResult result) {
				shareResultsProvider.getList().add(result);
			}
		}

		shareResultsProvider.getList().clear();
		resultsPanel.setWidget(shareResultsGrid);

		com.esferalia.aon.gwt.payroll.shared.Salary salary = documents
				.getSalaries().get(documents.getCurrentIndex());
		share(salary, new Callback());
		showResultsPanel();

	}

	// ---------------------------------------------- Employees.Listener methods

	@Override
	public void onLoadAvaiableEmployees(Map<String, String> map) {
		avaiableEmployees = (map != null) ? map : new HashMap<String, String>();
	}

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
		this.workplace = workplace;
	}

	@Override
	public void onEmployeeSelected(Employee employee) {
		employeeDetail.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
		this.employee = employee;
	}

	@Override
	public void onActivitySelected(Activity activity) {
		employeeDetail.setWidget(jsf);
		jsf.activitySelected(activity.getId());
	}

	@Override
	public void onSalariesSelected(SalaryDocuments docs) {
		employeeDetail.setWidget(getSalary());
		getSalary().setSalaryDocuments(docs);
	}

	@Override
	public void onIrpfsSelected(IrpfDocuments docs) {
		employeeDetail.setWidget(getIrpf());
		getIrpf().setIrpfDocuments(docs);
	}

	@Override
	public void onCostsSelected(CostDocuments docs) {
		getCost().setTitle("Costes");
		employeeDetail.setWidget(getCost());
		getCost().setCostDocuments(docs);

	}

	@Override
	public void onReportsSelected(ReportsObject reportsObject) {
		employeeDetail.setWidget(getReports());
		getReports().setReportsObject(reportsObject);

	}
	
	@Override
	public void onStatisticsSelected(
			com.esferalia.aon.gwt.payroll.shared.Statistics statistics) {
		employeeDetail.setWidget(getStats());
		getStats().setStatistics(statistics);
	}

	@Override
	public void onITDataSelected(ITDataObject dataObject) {
		employeeDetail.setWidget(getIt());
		getIt().setITEditor(dataObject);
	}
	
	@Override
	public void onCalendarSelected(CalendarDraftObjectData calendarDraftObjectData) {		
		employeeDetail.setWidget(getCalendarDraft());
		getCalendarDraft().setCalendarDraftObject(null, calendarDraftObjectData);
	}

	@Override
	public void onSalariesSelected(SalariesDocuments docs) {
		getCost().setTitle("N\u00F3minas");
		employeeDetail.setWidget(getCost());
		getCost().setCostDocuments(docs);
	}

	@Override
	public void onDocumentsSelected(ISpinnable<IDocument> docs) {
		employeeDetail.setWidget(getDocuments());
		getDocuments().setDocuments(docs);
	}

	@Override
	public void onSalaryDraftSelected(SalaryDraftObject salaryDraftObject) {
		employeeDetail.setWidget(getSalaryDraft());
		getSalaryDraft().setSalaryDraftObject(salaryDraftObject);
	}

	@Override
	public void onSalaryPreviewSelected(
			SalaryPreviewDocument salaryPreviewDocument) {
		employeeDetail.setWidget(getSalaryPreview());
		getSalaryPreview().setSalaryPreviewDocument(salaryPreviewDocument);
	}

	@Override
	public void onWorkplaceContextMenu(Workplace workplace,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getWorkplaceContextMenu().setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		getWorkplaceContextMenu().setWorkplace(workplace);
		getWorkplaceContextMenu().show();
	}

	@Override
	public void onEnterpriseContextMenu(Enterprise enterprise,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getEnterpriseContextMenu().setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		getEnterpriseContextMenu().setEnterprise(enterprise);
		getEnterpriseContextMenu().show();

	}

	@Override
	public void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getEmployeeContextMenu().setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		getEmployeeContextMenu().setEmployee(employee);
		getEmployeeContextMenu().show();
	}

	@Override
	public void onCategoryDraftSelected(CategoryDraftObject categoryDraftObject) {
		employeeDetail.setWidget(getCategoryDraft());
		getCategoryDraft().setCategoryDraftObject(categoryDraftObject);

	}

	@Override
	public void onAgreementDraftSelected(
			AgreementDraftObject agreementDraftObject) {
		employeeDetail.setWidget(getAgreementDraft());
		getAgreementDraft().setAgreementDraftObject(agreementDraftObject);
	}

	@Override
	public void onEventsDraftSelected(EventsDraftObject eventsDraftObject) {
		employeeDetail.setWidget(getEventsDraft());
		getEventsDraft().setEventsDraftObject(eventsDraftObject);
	}
	

	@Override
	public void onEmployeeEventsDraftSelected(
			EmployeeEventsDraftObject employeeEventsDraft) {
		employeeDetail.setWidget(getEmployeeEventsDraft());
		getEmployeeEventsDraft().setEventsDraftObject(employeeEventsDraft);
	}


	@Override
	public void onEmployeeCopy(Employee employee) {
		singlenton.getEmployeeContextMenu().setCopyEmployee(employee);		
		storage.setItem(EMPLOYEE, employee2Json(employee));
		pasteItem.setVisible(true);
	}

	@Override
	public void onEmployeePaste(Workplace workplace) {
		
		singlenton.getWorkplaceContextMenu().pasteContract();
		
	}

	@Override
	public void onEmployeeCut(Employee employee) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuprPress(Employee employee) {
		getEmployeeContextMenu().deleteContract();
	}

	// ---------------------------------------------- MetaData.Listener methods

	@Override
	public void onBonusConceptSelected(Bonus bonus) {
		getBonusEditor().setBonus(bonus);
		employeeDetail.setWidget(getBonusEditor());
	}

	@Override
	public void onPaymentConceptSelected(Payment payment) {
		getPaymentEditor().setPayment(payment);
		employeeDetail.setWidget(getPaymentEditor());
	}

	@Override
	public void onDeductionConceptSelected(Deduction deduction) {
		getDeductionEditor().setDeduction(deduction);
		employeeDetail.setWidget(getDeductionEditor());
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
	
	private ITEditor getIt() {
		if ( it == null )
			it = new ITEditor();
		return it;
	}
	
	private Cost getCost() {
		if ( cost == null )
			(cost = new Cost()).addListener(this);
			
		return cost;
	}
	
	private Irpf getIrpf() {
		if ( irpf == null )
			irpf = new Irpf();
		return irpf;
	}
	
	private Statistics getStats() {
		if ( stats == null )
			stats = new Statistics();
		return stats;
	}

	private Reports getReports() {
		if ( reports == null )
			reports = new Reports();
		return reports;
	}
	
	public Salary getSalary() {
		if ( salary == null )
			(salary = new Salary()).addListener(this);
		return salary;
	}
		
	private Documents getDocuments() {
		if ( documents == null )
			documents = new Documents();
		return documents;
	}
	
	private BonusEditor getBonusEditor() {
		if ( bonusEditor == null )
			bonusEditor = new BonusEditor();
		return bonusEditor;
	}
	
	private PaymentEditor getPaymentEditor() {
		if ( paymentEditor == null )
			paymentEditor = new PaymentEditor();
		return paymentEditor;
	}
	
	private DeductionEditor getDeductionEditor() {
		if ( deductionEditor == null )
			deductionEditor = new DeductionEditor();
		return deductionEditor;
	}
	
	private SalaryPreview getSalaryPreview() {
		if ( salaryPreview == null )
			salaryPreview = new SalaryPreview();
		return salaryPreview;
	}
	
	public SalaryDraft getSalaryDraft() {
		if ( salaryDraft == null )
			salaryDraft = new SalaryDraft();
		return salaryDraft;
	}
	
	private AgreementDraft getAgreementDraft() {
		if ( agreementDraft == null )
			agreementDraft = new AgreementDraft();
		return agreementDraft;
	}
	
	private EventsDraft getEventsDraft() {
		if ( eventsDraft == null )
			eventsDraft = new EventsDraft();
		return eventsDraft;
	}
	
	private EmployeeEventsDraft getEmployeeEventsDraft() {
		if (employeeEventsDraft == null)
			employeeEventsDraft = new EmployeeEventsDraft();
		return employeeEventsDraft;
	}
	
	private CategoryDraft getCategoryDraft() {
		if ( categoryDraft == null )
			categoryDraft = new CategoryDraft();
		return categoryDraft;
	}
	
	private CalendarDraft getCalendarDraft() {
		if ( calendarDraft == null )
			calendarDraft = new CalendarDraft();
		return calendarDraft;
	}
	
	private EmployeeContextMenu getEmployeeContextMenu() {
		if ( employeeContextMenu == null ) {
			employeeContextMenu = new EmployeeContextMenu();
		}
		return employeeContextMenu;
	}
	
	private EnterpriseContextMenu getEnterpriseContextMenu() {
		if ( enterpriseContextMenu == null )
			enterpriseContextMenu = new EnterpriseContextMenu();
		return enterpriseContextMenu;
	}
	
	private WorkplaceContextMenu getWorkplaceContextMenu() {
		
		if ( workplaceContextMenu == null )
			workplaceContextMenu = new WorkplaceContextMenu();
		
		Employee employee = getClipboardEmployee();
		if (employee != null) {
			
			if(employeeContextMenu == null)			
				employeeContextMenu = getEmployeeContextMenu();
			
			employeeContextMenu.setCopyEmployee(employee);
			this.pasteItem.setVisible(true);
		}
		
		return workplaceContextMenu;
	}
	
	private Employee getClipboardEmployee(){
		
		if(storage.getItem(EMPLOYEE) != null) {		
			return JSON2Employee(storage.getItem(EMPLOYEE).toString());
		}
		return null;
		
	}

	// --------------------------------------------------------- Private methods

	private static void showSalaryDraft(int employeeId, int workplaceId,
			Date startDate, Date endDate) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employees.selectSalaryDraft(employeeId, workplaceId, true);
	}

	private static EmployeeTree getEmployeeTree() {
		return singlenton;
	}

	private static <T extends HasId<?>> void calculate(Date startDate,
			Date endDate, String itemClass, Set<T> items, int optionsBits,
			final AsyncCallback<JsSalaryResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + START_DATE + "="
				+ DATE_FORMAT.format(startDate));
		requestDataBuffer.append("&" + END_DATE + "="
				+ DATE_FORMAT.format(endDate));
		requestDataBuffer.append("&" + ISSUE_DATE + "="
				+ DATE_FORMAT.format(endDate));

		for (T item : items)
			requestDataBuffer.append("&" + itemClass + "=" + item.getId());

		if ((optionsBits & SAVE_OPTION) > 0)
			requestDataBuffer.append("&" + SAVE + "=" + Boolean.toString(true));
		if ((optionsBits & OVERWRITE_OPTION) > 0)
			requestDataBuffer.append("&" + OVERWRITE + "="
					+ Boolean.toString(true));
		else if ((optionsBits & DUPLICATE_OPTION) > 0)
			requestDataBuffer.append("&" + DUPLICATE + "="
					+ Boolean.toString(true));

		// Send request to server and catch any errors.

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", CALC_URL);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state == XMLHttpRequest.LOADING
						|| state == XMLHttpRequest.DONE) {

					String text = xhr.getResponseText();

					try {
						for (JsSalaryResult result = read(text); text != null; result = read(text))
							callback.onSuccess(result);
					} catch (IndexOutOfBoundsException e) {
					}
				}

			}

			private JsSalaryResult read(String text) {
				for (int begin = loaded; begin < text.length(); begin++) {
					if (text.charAt(begin) == '{') {
						loaded = findEnd(text, begin + 1) + 1;
						String json = text.substring(begin, loaded);
						return JsonUtils.safeEval(json);
					}
				}
				throw new IndexOutOfBoundsException();
			}

			private int findEnd(String text, int start) {
				for (int end = start; end < text.length(); end++) {
					switch (text.charAt(end)) {
					case '}':
						return end;
					case '{':
						end = findEnd(text, end + 1);
					}
				}
				throw new IndexOutOfBoundsException();
			}

		});

		xhr.send(requestDataBuffer.toString());
	}

	private static <T extends HasId<?>> void share(
			com.esferalia.aon.gwt.payroll.shared.Salary salary,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.SALARY + "="
				+ salary.getId());

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);

	}

	private static <T extends HasId<?>> void share(
			com.esferalia.aon.gwt.payroll.shared.Cost cost,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.MONTH + "="
				+ cost.getMonth());
		requestDataBuffer
				.append("&" + ShareService.YEAR + "=" + cost.getYear());
		int workplaceId = cost.getWorkplaceId();
		if (workplaceId != 0)
			requestDataBuffer.append("&" + ShareService.WORKPLACE + "="
					+ workplaceId);
		else
			requestDataBuffer.append("&" + ShareService.ENTERPRISE + "="
					+ cost.getEnterpriseId());

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);
	}

	private static void share(String requestData,
			final AsyncCallback<JsShareResult> callback) {
		// Send request to server and catch any errors.

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SHARE_URL);
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state == XMLHttpRequest.LOADING
						|| state == XMLHttpRequest.DONE) {

					String text = xhr.getResponseText();

					try {
						for (JsShareResult result = read(text); text != null; result = read(text))
							callback.onSuccess(result);
					} catch (IndexOutOfBoundsException e) {
					}
				}

			}

			private JsShareResult read(String text) {
				for (int begin = loaded; begin < text.length(); begin++) {
					if (text.charAt(begin) == '{') {
						loaded = findEnd(text, begin + 1) + 1;
						String json = text.substring(begin, loaded);
						return JsonUtils.safeEval(json);
					}
				}
				throw new IndexOutOfBoundsException();
			}

			private int findEnd(String text, int start) {
				for (int end = start; end < text.length(); end++) {
					switch (text.charAt(end)) {
					case '}':
						return end;
					case '{':
						end = findEnd(text, end + 1);
					}
				}
				throw new IndexOutOfBoundsException();
			}

		});

		xhr.send(requestData);

	}

	private static void viewResults() {
		singlenton.showResultsPanel();
	}

	private static void enterpriseCalc() {
		singlenton.getEnterpriseContextMenu().setEnterprise(singlenton.enterprise);
		singlenton.getEnterpriseContextMenu().calcCmd.execute();
	}

	private static void workplaceCalc() {
		singlenton.getWorkplaceContextMenu().setWorkplace(singlenton.workplace);
		singlenton.getWorkplaceContextMenu().calcCmd.execute();
	}

	private static void employeeCalc() {
		singlenton.getEmployeeContextMenu().setEmployee(singlenton.employee);
		singlenton.getEmployeeContextMenu().calcCmd.execute();
	}

	private static native void export2JS() /*-{
		$wnd.viewResults = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::viewResults());
		$wnd.employeeCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::employeeCalc());
		$wnd.workplaceCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::workplaceCalc());
		$wnd.enterpriseCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::enterpriseCalc());
	}-*/;

	private static String employee2Json(Employee employee) {

		JSONObject json = new JSONObject();
		try {

			json.put("id", new JSONNumber(employee.getId()));
			json.put("person", new JSONNumber(employee.getPerson()));
			json.put("name", new JSONString(employee.getName()));
			json.put("first", new JSONString(employee.getFirstSurname()));
			
			json.put("startDate", new JSONString(employee.getStartDate()					
					.toString()));
			
			if(employee.getSecondSurName() != null)
				json.put("second", new JSONString(employee.getSecondSurName()));
			
			if (employee.getEndDate() != null)
				json.put("endDate", new JSONString(employee.getEndDate()
						.toString()));
			
			//Si Document es nulo se va a la BD a por el campo
			if(employee.getDocument() != null)
				json.put("document", new JSONString(employee.getDocument()));			
			
			if(employee.getSocialSecurity() != null)
				json.put("ss", new JSONString(employee.getSocialSecurity()));		
					
			return json.toString();

		} catch (Exception ex) {			
			ex.printStackTrace();
			return null;
		}
	}
	
	private static Employee JSON2Employee(String jsonEmployee) {
		
		try {
			
			JSONObject json = new JSONObject(parseJson(jsonEmployee));
			
			String id = json.get("id").toString();
			String person = json.get("person").toString().replaceAll("\"", "");
			String name = json.get("name").toString().replaceAll("\"", "");			
			String firstSurname = json.get("first").toString().replaceAll("\"", "");
			
			String secondSurname = null;
			if(json.get("second") != null)
				secondSurname = json.get("second").toString().replaceAll("\"", "");
			
			String start = json.get("startDate").toString().replaceAll("\"", "");			
			Date startDate = getDate(start);		
			
			Date endDate = null;
			if(json.get("endDate") != null) {
				String end = json.get("endDate").toString().replaceAll("\"", "");
				endDate = getDate(end);
			}
			
			String document = "";
			if(json.get("document") != null)			
				document = json.get("document").toString().replaceAll("\"", "");
			
			String ss = "";
			if(json.get("ss") != null)
				ss = json.get("ss").toString().replaceAll("\"", "");
			
			
			
			Employee employee = new Employee();			
			employee.setId(Integer.parseInt(id));			
			employee.setName(name);			
			employee.setFirstSurname(firstSurname);			
			employee.setSecondSurName(secondSurname);			
			employee.setStartDate(startDate);
			employee.setEndDate(endDate);
			employee.setDocument(document);
			employee.setPerson(Integer.parseInt(person));
			employee.setSocialSecurity(ss);
			
			return employee;
			
			
		} catch (Exception ex) {
			ex.printStackTrace(); 
			return null;
		}
		
	}
	
	private static Date getDate(String date) {
		return DateTimeFormat.getFormat("yyyy-MM-dd").parse(date);
	}
	
	private static <T extends JavaScriptObject> T parseJson(String json) {
		return JsonUtils.safeEval(json);
	}
	
	private boolean personExistInDomain(String document) {
		return singlenton.avaiableEmployees.containsKey(document);
	}
	
	private static void logEvent(String type){
		StatsEventLogger.logEvent("aon", "EmployeeTree", type);
	}
	
}
