package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.Constants.EMPLOYEE_SEARCH_PARAM;
import static com.esferalia.aon.gwt.payroll.client.MainEntryPoint.getParameter;
import static com.esferalia.aon.gwt.payroll.shared.CalculateService.WORKPLACES;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.TextCell;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.metrics.StatsEventLogger;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.Task;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.TimeTask;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonEmployeesToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.client.MainCreta.AbstractBaseCretaDetail;
import com.esferalia.aon.gwt.payroll.client.MainCreta.AbstractCCCCretaRequestCommand;
import com.esferalia.aon.gwt.payroll.client.MainCreta.SyncCallback;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptEvent;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.CalculateService;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.File;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBases;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsBasesResult;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEmployee;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsFile;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsRespuesta;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsTrabajadoresYTramos;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedCCC;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedContractType;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedOccupation;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedPartialFactor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedQuoteGroup;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.MismatchedStartDate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus.Visitor;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Province;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class EmployeeTree implements EntryPoint, Employees.Listener, MetaData.Listener, Cost.Listener, Salary.Listener,
		EmployeeSalary.Listener, WorkplaceSalary.Listener, EnterpriseSalary.Listener, SalaryDraft.Listener {
	public static String SHARE_URL = URL.encode(GWT.getModuleBaseURL() + "share");
	
	

	static class EmployeeCalcDialog extends CalcDialog<Employee> {

		DomainEmployeesServiceAsync employeesService;

		public EmployeeCalcDialog() {

			employeesService = DomainEmployeesServiceAsync.newInstance();

			// Full name.
			Column<Employee, String> fullNameColumn = new Column<Employee, String>(new TextCell()) {
				@Override
				public String getValue(Employee employee) {
					return employee.getFullname();
				}
			};

			addColumn(fullNameColumn, "Empleado");
		}

		@Override
		protected void getExtras(Set<Employee> employeesSet, AsyncCallback<List<Extra>> callback) {
			List<Employee> employeesList = new ArrayList<Employee>(employeesSet);
			employeesService.getExtras(employeesList, callback);
		}

	}

	static class WorkPlaceCalcDialog extends CalcDialog<Workplace> {

		DomainEnterprisesServiceAsync enterpriseService;

		public WorkPlaceCalcDialog() {

			enterpriseService = DomainEnterprisesServiceAsync.newInstance();

			// Full name.
			Column<Workplace, String> descriptionColumn = new Column<Workplace, String>(new TextCell()) {
				@Override
				public String getValue(Workplace workplace) {
					return workplace.getDescription();
				}
			};

			addColumn(descriptionColumn, "Centro");
		}

		@Override
		protected void getExtras(Set<Workplace> workplaces, AsyncCallback<List<Extra>> callback) {
			List<Integer> workplaceIds = new ArrayList<Integer>(workplaces.size());
			for (Workplace workplace : workplaces)
				workplaceIds.add(workplace.getId());
			enterpriseService.getWorkplacesExtras(workplaceIds, callback);
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
			EmployeeDialog employeeDialog = new EmployeeDialog(true) {
				@Override
				protected void onAccept(Integer contractId) {
				}
			};
			EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(workplace);
			employeeDialog.setEmployeeDialogObject(employeeDialogObject);
			workplaceContextMenu.hide();
		}
	}

	class NewActivityCommand implements ScheduledCommand {
		@Override
		public void execute() {
			ActivityDialog activityDialog = new ActivityDialog();
			ActivityDialogObject activityDialogObject = new ActivityDialogObject(enterprise);
			activityDialog.setActivityDialogObject(activityDialogObject);
		}
	}

	class NewWorkplaceCommand implements ScheduledCommand {

		@Override
		public void execute() {
			WorkplaceDialog workplaceDialog = new WorkplaceDialog();
			WorkplaceDialogObject workplaceDialogObject = new WorkplaceDialogObject(enterprise);
			getEnterpriseContext(enterpriseCtx -> workplaceDialogObject.setAgreements(enterpriseCtx.getAgreements()) );
			;
			workplaceDialog.setWorkplaceDialogObject(workplaceDialogObject);
			enterpriseContextMenu.hide();
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

	class PasteEmployeeCommand implements ScheduledCommand, EmployeePopupCopy.Listener {

		@Override
		public void execute() {
			paste = new EmployeePopupCopy();
			paste.addListener(this);
			Employee aux = singlenton.getEmployeeContextMenu().getEmployeeCopy();

			if (singlenton.avaiableEmployees.containsKey(aux.getDocument()) == false)
				existPerson(aux);
			else
				showPopUpPanel();
		}

		private void showPopUpPanel() {
			setEmployeePaste(singlenton.getEmployeeContextMenu().getEmployeeCopy());

			setMapAvaiableEmployees(singlenton.avaiableEmployees);
			// TODO: FIX FIRST THIS, DONT KNOW HOW REALLY WORKS
			paste.showPopUpPanel();
		}

		@Override
		public void onAcceptClick(Employee pasteEmployee, boolean value) {
			int workplaceId = workplace.getId();
			int contractId = singlenton.getEmployeeContextMenu().getEmployeeCopy().getId();
			String document = pasteEmployee.getDocument();
			Date startDate = pasteEmployee.getStartDate();
			Date endDate = (pasteEmployee.getEndDate() != null) ? pasteEmployee.getEndDate() : null;

			pasteContract(workplaceId, contractId, document, startDate, endDate, value, null);
			paste.hide();
		}

		private void setEmployeePaste(Employee employee) {
			paste.setEmployee(employee);
		}

		private void setMapAvaiableEmployees(Map<String, String> map) {
			paste.setMapAvaiableEmployees(map);
		}

		private void pasteContract(int workplaceId, int contractId, String document, Date startDate, Date endDate,
				boolean check, final AsyncCallback<Employee> callback) {

			employees.getEmployeesService().pasteContract(workplaceId, contractId, document, startDate, endDate, check,
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

			if (Window.confirm(employee.getFullname() + " no se encuentra en el dominio. \u00BFDesea insertar "
					+ "el registro\u003F")) {

				singlenton.employees.getEmployeesService().insertPerson(employee, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(Void result) {
						singlenton.avaiableEmployees.put(employee.getDocument(), employee.getFullname());
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

		private void deleteContract(Employee employee, final AsyncCallback<Void> callback) {

			if (employee.hasSalries()) {
				Window.alert("No se puede borrar un empleado con nominas.");
				callback.onSuccess(null);
			} else {
				employees.getEmployeesService().moveContractId(employee, new AsyncCallback<Void>() {

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

	}

	class CalcEnterpriseCommand implements ScheduledCommand, AcceptHandler, AsyncCallback<JsSalaryResult>,
			SelectionHandler<JsSalaryResult> {

		SalaryResults results;
		WorkPlaceCalcDialog calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcEnterpriseCommand() {
			results = new SalaryResults();
			calcDialog = new WorkPlaceCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			results.addSelectionHandler(this);
			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			results.setDataProvider(resultsDataProvider);
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
			Date startDate = calcDialog.getStartDate();
			Date endDate = calcDialog.getEndDate();
			Date issueDate = calcDialog.getIssueDate();
			Date chargeDate = calcDialog.getChargeDate();

			Set<Workplace> workplaces = calcDialog.getSelectedData();

			resultsPanel.setWidget(results);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= MainCalculator.SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= MainCalculator.OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= MainCalculator.DUPLICATE_OPTION;

			Integer extra = calcDialog.getExtra();
			com.esferalia.aon.gwt.payroll.shared.Salary.Type salaryType = calcDialog.getType();

			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, chargeDate, WORKPLACES, workplaces, extra,
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

		// ---------------------------------------------------- Private methods

		private void clear() {
			resultsDataProvider.getList().clear();
		}

		private void onSalaryResultSelected(JsSalaryResult salaryResult) {
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(), salaryResult.getWorkplaceId(),
					salaryResult.getStartDate(), salaryResult.getEndDate());
		}

	}

	interface EmployeeCommand extends ScheduledCommand {
		void setEmployee(Employee employee);
	}

	class RefreshEmployeeCommand implements EmployeeCommand {

		private Employee employee;

		@Override
		public void execute() {
			EmployeeTree.this.employees.refresh(employee);
		}

		// --------------------------------------------------------------------

		@Override
		public void setEmployee(Employee employee) {
			this.employee = employee;
		}
	}

	class CalcEmployeeCommand implements EmployeeCommand, AcceptHandler, CalculateService,
			SelectionHandler<JsSalaryResult>, AsyncCallback<JsSalaryResult> {

		private SalaryResults results;
		private CalcDialog<Employee> calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcEmployeeCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			results = new SalaryResults();

			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			results.setDataProvider(resultsDataProvider);
			results.addSelectionHandler(this);
		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();
		}

		public void setEmployee(Employee employee) {
			calcDialog.setData(Collections.singletonList(employee));

			Date actualDate = DateUtils.after(new Date(), employee.getStartDate());
			actualDate = DateUtils.before(actualDate, employee.getEndDate());

			calcDialog.setStartMonth(employee.getStartDate());
			calcDialog.setEndMonth(employee.getEndDate());
			calcDialog.setMonth(actualDate);
		}

		// ------------------------------------------------------ AcceptHandler
		@Override
		public void onAccept(AcceptEvent event) {

			Date startDate = calcDialog.getStartDate();
			Date endDate = calcDialog.getEndDate();
			Date issueDate = calcDialog.getIssueDate();
			Date chargeDate = calcDialog.getChargeDate();

			Set<Employee> employees = calcDialog.getSelectedData();

			resultsPanel.setWidget(results);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= MainCalculator.SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= MainCalculator.OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= MainCalculator.DUPLICATE_OPTION;

			Integer extra = calcDialog.getExtra();
			com.esferalia.aon.gwt.payroll.shared.Salary.Type salaryType = calcDialog.getType();

			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, chargeDate, EMPLOYEES, employees, extra,
					optionsBits, this);
			clear();

			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'

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
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(), salaryResult.getWorkplaceId(),
					salaryResult.getStartDate(), salaryResult.getEndDate());
		}
	}

	public static abstract class CretaCommand implements ScheduledCommand, CretaService {

		protected File file;
		protected FileEditor fileEditor;
		protected DetailPanel detailPanel;

		public CretaCommand(File file, DetailPanel detailPanel) {
			this(file, detailPanel, new FileEditor());
		}

		public CretaCommand(File file, DetailPanel detailPanel, FileEditor fileEditor) {
			this.file = file;
			this.fileEditor = fileEditor;
			this.detailPanel = detailPanel;
		}

		protected void send(long autorizado, final int fromMonth, final int fromYear, final int toMonth,
				final int toYear, final int ctrlMonth, final int ctrlYear, final String tipo,
				final Collection<CCC> cccs, final boolean basesMesAnterior, final boolean calcsDetailed,
				final String i54, final boolean reftificativa, final boolean solicitudRecepcionRNT) {
			StringBuffer requestDataBuffer = new StringBuffer();

			requestDataBuffer.append("&" + Parameter.TIPO + "=" + tipo);
			requestDataBuffer.append("&" + Parameter.DESDE_MES + "=" + fromMonth);
			requestDataBuffer.append("&" + Parameter.DESDE_ANHO + "=" + fromYear);
			requestDataBuffer.append("&" + Parameter.HASTA_MES + "=" + toMonth);
			requestDataBuffer.append("&" + Parameter.HASTA_ANHO + "=" + toYear);
			requestDataBuffer.append("&" + Parameter.CTRL_MES + "=" + ctrlMonth);
			requestDataBuffer.append("&" + Parameter.CTRL_ANHO + "=" + ctrlYear);
			requestDataBuffer.append("&" + Parameter.AUTORIZADO + "=" + autorizado);

			requestDataBuffer.append("&" + Parameter.USER + "=" + Wnd.getCurrentUser());
			requestDataBuffer.append("&" + Parameter.DOMAIN + "=" + Wnd.getCurrentDomainNameURL());

			for (CCC ccc : cccs)
				requestDataBuffer.append("&" + Parameter.CCC + "=" + ccc.getRegime() + ccc.getCode());

			if (basesMesAnterior)
				requestDataBuffer.append("&" + Parameter.ACEPTAR_BASES_ANTERIORES + "=on");

			if (calcsDetailed)
				requestDataBuffer.append("&" + Parameter.CALCULOS_DESGLOSADOS + "=on");

			if (reftificativa)
				requestDataBuffer.append("&" + Parameter.INDICADOR_RECTIFICACION + "=on");

			if (solicitudRecepcionRNT)
				requestDataBuffer.append("&" + Parameter.SOLICITUD_RECEPCION_RNT + "=on");

			requestDataBuffer.append("&" + Parameter.I54 + "=" + i54);

			for (CCC ccc : cccs)
				for (Employee employee : ccc.getEmployees())
					requestDataBuffer.append("&" + Parameter.NAFS + "=" + employee.getSocialSecurity());

			// Send request to server and catch any errors.

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", CretaService.CRETA_URL + "/" + file.name());
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

				private int loaded = 0;

				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();

					if (state != XMLHttpRequest.DONE)
						return;

					onRequestDone(xhr.getResponseText(), fromMonth, fromYear, toMonth, toYear, tipo, cccs);
				}

			});

			xhr.send(requestDataBuffer.toString());

		}

		protected void onRequestDone(String response, int fromMonth, int fromYear, int toMonth, int toYear, String tipo,
				Collection<CCC> cccs) {
			fileEditor.setMode("xml");
			fileEditor.setText(response);
			fileEditor.setFoldGutter(true);
			fileEditor.setLineNumbers(true);
			fileEditor.setTitle(file.getFilename());
			// TODO: from ?
			fileEditor.setFilename(getFileName(toMonth, toYear, tipo, cccs));
			detailPanel.setWidget(fileEditor);
			fileEditor.autoRefresh();
		}

		// --------------------------------------------------------------------

		private String getFileName(int month, int year, String tipo, Collection<CCC> cccs) {

			StringBuffer buffer = new StringBuffer();

			buffer.append(cleanDiacritics(file.getFilename()));

			for (CCC ccc : cccs) {
				buffer.append('-');
				buffer.append(ccc.getRegime());
				buffer.append(ccc.getCode());
			}
			buffer.append('-');
			buffer.append(tipo);
			buffer.append('-');
			buffer.append(year);
			buffer.append('-');
			if (month < 10)
				buffer.append('0');
			buffer.append(month);
			buffer.append(".xml");

			return buffer.toString();
		}

		// --------------------------------------------------------------------

	}

	public static abstract class CreateResponseCommand extends CretaCommand implements CretaResponseDialog.Handler {

		ResultsPanel resultsPanel;
		CretaResponseDialog dialog;

		public CreateResponseCommand(File outFile, File inFile, DetailPanel detailPanel, ResultsPanel resultsPanel) {
			super(outFile, detailPanel);
			this.resultsPanel = resultsPanel;
			dialog = new CretaResponseDialog(outFile, inFile, this) {
				@Override
				public String getDescription(String ccc) {
					return CreateResponseCommand.this.getDescription(ccc);
				}

				@Override
				public boolean accept(JsFile f) {
					return CreateResponseCommand.this.accept(f.getCCC());
				}
			};
		}

		// --------------------------------------------------------------------
		@Override
		public void execute() {
			sync();
			dialog.onTrabajadoresYTramos();
			dialog.center();
			dialog.show();
		}

		// --------------------------------------------------------------------

		@Override
		public void onBases(CretaService.JsBasesResult result) {
			dialog.hide();

			showBases(result, detailPanel);

			showResults(result, dialog.getSelectedData(), resultsPanel, CreateResponseCommand.this::onBases,
					r -> showResultsPanel());
		}

		// --------------------------------------------------------------------
		protected abstract void showResultsPanel();

		protected abstract boolean accept(String ccc);

		protected abstract String getDescription(String ccc);

		// --------------------------------------------------------------------

		private void sync() {
			MainCreta.sync(new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					CreateResponseCommand.this.dialog.onTrabajadoresYTramos();
				}

				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				};
			});
		}

	}

	public static abstract class WorkplaceCreateResponseCommand extends CreateResponseCommand {

		public WorkplaceCreateResponseCommand(File outFile, File inFile, DetailPanel detailPanel,
				ResultsPanel resultsPanel) {
			super(outFile, inFile, detailPanel, resultsPanel);
		}

		private Workplace workplace;

		protected void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
		}

		@Override
		protected String getDescription(String ccc) {
			String province = ccc.substring(4, 6);
			Activity activity = workplace.getActivity();
			return activity.getDescription() + "," + Province.getName(province);
		}

		@Override
		protected boolean accept(String fullCcc) {
			Activity activity = workplace.getActivity();
			for (CCC ccc : activity.getCccs())
				if (fullCcc.endsWith(ccc.getCode()))
					return true;

			return false;
		}

	}

	public static abstract class CreateRequestCommand extends CretaCommand implements CretaRequestDialog.Callback<CCC> {

		CretaRequestDialog<CCC> dialog;

		public CreateRequestCommand(File file, DetailPanel detailPanel) {
			super(file, detailPanel);
			dialog = new CretaRequestDialog.CretaCCCRequestDialog(this) {
				@Override
				void onMonthChanged(ChangeEvent e) {
				}
				
				@Override
				void onMonthsChanged(ChangeEvent e) {
				}

				@Override
				public String getDescription(CCC ccc) {
					return CreateRequestCommand.this.getDescription(ccc);
				}
				
			};
			setUpDialog(file, dialog);
		}

		public CreateRequestCommand(File file, DetailPanel detailPanel, FileEditor fileEditor) {
			super(file, detailPanel, fileEditor);
			dialog = new CretaRequestDialog.CretaCCCRequestDialog(this) {
				
				@Override
				void onMonthChanged(ChangeEvent e) {
				}
				
				@Override
				void onMonthsChanged(ChangeEvent e) {
				}

				@Override
				public String getDescription(CCC ccc) {
					return CreateRequestCommand.this.getDescription(ccc);
				}
			};
			setUpDialog(file, dialog);
		}

		// --------------------------------------------------------------------
		@Override
		public void execute() {
			dialog.center();
			dialog.show();
		}

		// --------------------------------------------------------------------
		@Override
		public boolean onAccept(CretaRequestDialog<CCC> dialog) {

			String tipo = dialog.getType();
			Date fromMonth = dialog.getFromMonth();
			Date toMonth = dialog.getToMonth();
			//TODO: Check.
			Date ctrlMonth = dialog.getCtrlMonth();
			int desdeMes = fromMonth.getMonth() + 1;
			int desdeAnyo = fromMonth.getYear() + 1900;
			int hastaMes = toMonth.getMonth() + 1;
			int hastaAnyo = toMonth.getYear() + 1900;
			int ctrlMes = ctrlMonth.getMonth() + 1;
			int ctrlAnyo = ctrlMonth.getYear() + 1900;
			long autorizado = dialog.getAuthorized();
			Set<CCC> ccs = dialog.getSelectedData();
			boolean basesMesAnterior = dialog.previousBases();
			boolean calcsDetailed = dialog.calcsDetailed();
			String i54 = dialog.getI54();
			boolean reftificationMark = dialog.reftificationMark();
			boolean solicitudRecepcionRNT = dialog.solicitudRecepcionRNT();

			send(autorizado, desdeMes, desdeAnyo, hastaMes, hastaAnyo, ctrlMes, ctrlAnyo, tipo, checkCCCs(ccs),
					basesMesAnterior, calcsDetailed, i54, reftificationMark, solicitudRecepcionRNT);

			return true;
		}

		// --------------------------------------------------------------------

		public void reexecute(Consumer<CretaRequestDialog<CCC>> consumer) {
			consumer.accept(dialog);
			onAccept(dialog);
		}

		// --------------------------------------------------------------------
		protected void setSelected(Collection<CCC> cccs) {
			dialog.setSelectedData(cccs);

		}
		// --------------------------------------------------------------------

		protected abstract String getDescription(CCC ccc);

		protected abstract void onCCCError(CCC ccc, String message);

		// --------------------------------------------------------------------

		private Set<CCC> checkCCCs(Set<CCC> cccs) {
			return cccs.stream().filter(ccc -> {
				String code = ccc.getCode();
				if (AonStringUtils.isBlank(code)) {
					onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n vac\u00EDo");
					return false;
				}
				if (AonStringUtils.trim(code).length() < 11) {
					onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '" + code
							+ "'. Recuerde dos d\u00EDgitos para la provincia y nueve d\u00EDgitos para el n\u00FAmero de cotizaci\u00F3n");
					return false;
				}
				try {
					int provincia = Integer.parseInt(AonStringUtils.substring(code, 0, 2));
					if (provincia < 1 || provincia > 52) {
						onCCCError(ccc,
								"C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '" + code
										+ "'. C\u00F3digo de provincia '" + AonStringUtils.substring(code, 0, 2)
										+ "' desconocido. Recuerde debe estar entre ( 01 y 52 )");
						return false;
					}

				} catch (Exception e) {
					onCCCError(ccc,
							"C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '" + code
									+ "'. C\u00F3digo de provincia '" + AonStringUtils.substring(code, 0, 2)
									+ "' desconocido. Recuerde debe estar entre ( 01 y 52 )");
					return false;
				}

				String numero = AonStringUtils.substring(code, 2);
				String control = AonStringUtils.substring(code, -2);

				return true;
			}).collect(Collectors.toSet());
		}

		private static void setUpDialog(File file, final CretaRequestDialog dialog) {
			file.accept(new File.Visitor<Void, Void, RuntimeException>() {

				@Override
				public void visitBases(Void t, Void l) throws RuntimeException {
					// TODO: add TypeChangeHadler to CretaRequestDialog?
					dialog.typeListBox.addChangeHandler(event -> dialog.setVisibleI54("L03".equals(dialog.getType())));
					dialog.setVisibleReftificationMark(true);
					dialog.setVisibleSolicitudRecepcionRNT(true);
				}

				@Override
				public void visitRespuesta(Void t, Void l) throws RuntimeException {
				}

				@Override
				public void visitTrabajadoresTramos(Void t, Void l) throws RuntimeException {
				}

				@Override
				public void visitSolicitudBorrador(Void t, Void l) throws RuntimeException {
					dialog.setVisiblePreviousBases(true);
					dialog.setVisibleSolicitudRecepcionRNT(true);
				}

				@Override
				public void visitSolicitudCalculos(Void t, Void l) throws RuntimeException {
					dialog.setVisibleCalcsDetailed(true);
				}

				@Override
				public void visitSolicitudConfirmacion(Void t, Void l) throws RuntimeException {
				}

				@Override
				public void visitSolicitudTrabajadoresTramos(Void t, Void l) throws RuntimeException {
				}

				@Override
				public void visitComunicacionDatosBancarios(Void t, Void l) throws RuntimeException {
					// TODO Auto-generated method stub
				}

				@Override
				public void visitDocumentoCalculoLiquidacion(Void t, Void l) throws RuntimeException {
					// TODO Auto-generated method stub

				}

			}, null, null);

			dialog.setAuthorized(MainCreta.getAuthorized());

		}

	}

	interface WorkplaceCommand extends ScheduledCommand {
		void setWorkplace(Workplace workplace);
	}

	class WorkplaceCreateRequestCommand extends CreateRequestCommand implements WorkplaceCommand {

		private Workplace workplace;
		private CretaResults cretaResults;

		public WorkplaceCreateRequestCommand(File file, DetailPanel detailPanel, FileEditor fileEditor) {
			super(file, detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------
		@Override
		protected void onCCCError(CCC ccc, String message) {
			if (cretaResults == null) {
				cretaResults = new CretaResults();
				resultsPanel.setWidget(cretaResults);
				showResultsPanel();
			}
			cretaResults.addWarnings(message);
		}

		@Override
		public void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
			dialog.setData(getCCs(workplace));
		}
		// --------------------------------------------------------------------

		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, workplace);
		}

	}

	class CCCCretaRequestCommand extends CreateRequestCommand implements CCCCommand {

		private CretaResults cretaResults;

		public CCCCretaRequestCommand(File file) {
			super(file, employeeDetail);

			dialog.selectLabel.setVisible(false);
			dialog.selectDataGrid.setVisible(false);
		}

		@Override
		protected void onCCCError(CCC ccc, String message) {
			if (cretaResults == null) {
				cretaResults = new CretaResults();
				resultsPanel.setWidget(cretaResults);
				showResultsPanel();
			}
			resultsPanel.setWidget(cretaResults);
		}

		@Override
		protected String getDescription(CCC ccc) {
			return Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		@Override
		public void setCCC(CCC ccc) {
			dialog.setData(Collections.singletonList(ccc));
			dialog.setSelectedData(Collections.singletonList(ccc));
		}

	}

	class WorkplaceDBACommand extends DBACommand implements WorkplaceCommand {

		protected Workplace workplace;

		public WorkplaceDBACommand(DetailPanel detailPanel, FileEditor fileEditor) {
			super(detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------

		@Override
		public void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
			setData(getCCs(workplace));
			setBankAccounts(enterprise.getBankAccounts());

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(enterprise.getName());

		}

		// --------------------------------------------------------------------

		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, workplace);
		}

	}

	private class CCCDBACommand extends EmployeeTree.DBACommand implements CCCCommand {

		public CCCDBACommand() {
			super(employeeDetail);

		}

		@Override
		protected String getDescription(CCC ccc) {
			return Province.getName(ccc.getGeozone()) + " " + ccc.getCode();
		}

		@Override
		public void setCCC(CCC ccc) {
			setData(Collections.singletonList(ccc));
			setSelectedData(Collections.singletonList(ccc));
			setBankAccounts(enterprise.getBankAccounts());

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(enterprise.getName());
		}

	}

	public interface EnterpriseCommand extends ScheduledCommand {
		void setEnterprise(Enterprise enterprise);
	}

	public class EnterpriseCretaRequestCommand extends CreateRequestCommand implements EnterpriseCommand {

		protected Enterprise enterprise;
		private CretaResults cretaResults;

		public EnterpriseCretaRequestCommand(File file, DetailPanel detailPanel) {
			super(file, detailPanel);
		}

		public EnterpriseCretaRequestCommand(File file, DetailPanel detailPanel, FileEditor fileEditor) {
			super(file, detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------
		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
			dialog.setData(getCCs(enterprise));
		}

		// --------------------------------------------------

		@Override
		protected void onCCCError(CCC ccc, String message) {
			if (cretaResults == null) {
				cretaResults = new CretaResults();
				resultsPanel.setWidget(cretaResults);
				showResultsPanel();
			}
			resultsPanel.setWidget(cretaResults);
		}

		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, this.enterprise);
		}

	}

	public static class DBACommand extends CretaDBACommand {
		private FileEditor fileEditor;
		private DetailPanel detailPanel;

		public DBACommand(DetailPanel detailPanel) {
			this(detailPanel, new FileEditor());
		}

		public DBACommand(DetailPanel detailPanel, FileEditor fileEditor) {
			this.fileEditor = fileEditor;
			this.detailPanel = detailPanel;
		}

		// --------------------------------------------------------------------

		@Override
		protected void onSucces(String response) {
			fileEditor.setMode("xml");
			fileEditor.setText(response);
			fileEditor.setFoldGutter(true);
			fileEditor.setLineNumbers(true);
			fileEditor.setTitle(File.COMUNICACION_DATOS_BANCARIOS.getFilename());
			fileEditor.setFilename(File.COMUNICACION_DATOS_BANCARIOS.getFilename() + ".xml");
			detailPanel.setWidget(fileEditor);
			fileEditor.autoRefresh();
		}

		@Override
		protected void onFailure(Throwable caugth) {
			// TODO Auto-generated method stub
			super.onFailure(caugth);
		}
	}

	public static class EnterpriseDBACommand extends DBACommand implements EnterpriseCommand {

		protected Enterprise enterpr1se;

		public EnterpriseDBACommand(DetailPanel detailPanel) {
			super(detailPanel, new FileEditor());
		}

		public EnterpriseDBACommand(DetailPanel detailPanel, FileEditor fileEditor) {
			super(detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------

		@Override
		public void setEnterprise(Enterprise enterprise) {
			this.enterpr1se = enterprise;
			setData(getCCs(enterprise));
			setBankAccounts(enterprise.getBankAccounts());

			if (AonStringUtils.isBlank(getHolder()))
				setHolder(enterprise.getName());
		}

		// --------------------------------------------------------------------

		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, this.enterpr1se);
		}

	}

	class RefreshWorkplaceCommand implements WorkplaceCommand {

		private Workplace workplace;

		@Override
		public void execute() {
			EmployeeTree.this.employees.refresh(workplace);
		}

		// --------------------------------------------------------------------

		@Override
		public void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
		}
	}

	class CalcWorkplaceCommand implements ScheduledCommand, AcceptHandler, CalculateService,
			AsyncCallback<JsSalaryResult>, SelectionHandler<JsSalaryResult> {

		private SalaryResults results;
		private CalcDialog<Employee> calcDialog;

		private HandlerRegistration registration;
		private ListDataProvider<JsSalaryResult> resultsDataProvider;

		public CalcWorkplaceCommand() {
			calcDialog = new EmployeeCalcDialog();
			calcDialog.addAcceptHandler(this);
			calcDialog.setWidth(Window.getClientWidth() / 2 + "px");

			results = new SalaryResults();
			resultsDataProvider = new ListDataProvider<JsSalaryResult>();
			results.setDataProvider(resultsDataProvider);
			results.addSelectionHandler(this);

		}

		@Override
		public void execute() {
			calcDialog.center();
			calcDialog.show();

		}

		public void setWorkplace(final Workplace workplace) {
			AsyncEmployeeProvider employeeProvider = new AsyncEmployeeProvider() {

				@Override
				Date getStartDate() {
					return calcDialog.getStartDate();
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
			Date startDate = calcDialog.getStartDate();
			Date endDate = calcDialog.getEndDate();
			Date issueDate = calcDialog.getIssueDate();
			Date chargeDate = calcDialog.getChargeDate();

			Set<Employee> employees = calcDialog.getSelectedData();

			resultsPanel.setWidget(results);

			int optionsBits = 0x00;
			if (calcDialog.isSaveSelected())
				optionsBits |= MainCalculator.SAVE_OPTION;
			if (calcDialog.isOverwriteSelected())
				optionsBits |= MainCalculator.OVERWRITE_OPTION;
			if (calcDialog.isDuplicateSelected())
				optionsBits |= MainCalculator.DUPLICATE_OPTION;

			Integer extra = calcDialog.getExtra();
			com.esferalia.aon.gwt.payroll.shared.Salary.Type salaryType = calcDialog.getType();

			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, chargeDate, EMPLOYEES, employees, extra,
					optionsBits, this);
			clear();
			showResultsPanel(); // TODO: Here or at below 'onReadyStateChange'
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
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(), salaryResult.getWorkplaceId(),
					salaryResult.getStartDate(), salaryResult.getEndDate());
		}
	}

	private static interface CCCCommand extends ScheduledCommand {
		void setCCC(CCC ccc);
	}

	protected class BasesCCCCretaRequestCommand extends MainCreta.AbstractCCCCretaRequestCommand
			implements CretaRequestDialog.Callback<Employee>, CCCCommand {

		public BasesCCCCretaRequestCommand(File file) {
			super(file, EmployeeTree.this.employeeDetail);
		}
				
		@Override
		protected void setupDialog(CretaRequestDialog<Employee> dialog) {
			super.setupDialog(dialog);
			dialog.setAuthorized(AonStringUtils.defaultIfBlank(MainCreta.getAuthorized(), "00000000"));
		}
	}

	private class CCCContextMenu extends ContextMenu {

		CCCCommand cccCommands[] = new CCCCommand[7];

		public CCCContextMenu() {

			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					cccCommands[0] = new CCCCretaRequestCommand(CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					cccCommands[1] = new CCCCretaRequestCommand(CretaService.File.SOLICITUD_BORRADOR),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					cccCommands[2] = new CCCCretaRequestCommand(CretaService.File.SOLICITUD_CONFIRMACION),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					cccCommands[3] = new CCCCretaRequestCommand(CretaService.File.SOLICITUD_CALCULOS),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios", cccCommands[4] = new CCCDBACommand(),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					cccCommands[5] = new BasesCCCCretaRequestCommand(File.BASES) { // new
																					// MainCCCCretaRequestCommand(File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth, int toYear,
								String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, resultsPanel, r -> {
								/* TODO: */}, r -> showResultsPanel());
						}

					}, AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
		}

		void setCCC(CCC ccc) {
			for (CCCCommand cmd : cccCommands)
				if (cmd != null)
					cmd.setCCC(ccc);
		}

	}

	class WorkplaceContextMenu extends ContextMenu {

		CalcWorkplaceCommand calcCmd;
		PasteEmployeeCommand pasteCmd;

		WorkplaceCommand workplaceCmds[] = new WorkplaceCommand[8];
		WorkplaceCreateResponseCommand cretaResponseCmds[] = new WorkplaceCreateResponseCommand[1];

		public WorkplaceContextMenu() {

			MenuBar newPopup = new MenuBar(true);
			MenuItem newEmployeeItem = newPopup.addItem(
					getHTML("Contrato", AON.AON_ICON_EMPLOYEE, AON.AON_ICON_CMD_BUTTON), true,
					new NewEmployeeCommand());
			newEmployeeItem.setEnabled(true);

			MenuItem newItem = addItem("Nuevo", newPopup, AON.AON_ICON_RESET, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			pasteItem = addItem("Pegar", pasteCmd = new PasteEmployeeCommand(), AON.AON_CSS.aonIconPaste(),
					AON.AON_ICON_CMD_BUTTON);
			pasteItem.setVisible(false);
			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(), AON.AON_ICON_ACCEPT,
					AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			MenuItem runItem = addItem("Calcular", calcCmd = new CalcWorkplaceCommand(), AON.AON_ICON_TASK_START,
					AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					workplaceCmds[0] = new WorkplaceCreateRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS, employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					workplaceCmds[2] = new WorkplaceCreateRequestCommand(CretaService.File.SOLICITUD_BORRADOR,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					workplaceCmds[3] = new WorkplaceCreateRequestCommand(CretaService.File.SOLICITUD_CONFIRMACION,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00FE1alculos",
					workplaceCmds[4] = new WorkplaceCreateRequestCommand(CretaService.File.SOLICITUD_CALCULOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);

			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					workplaceCmds[5] = new WorkplaceDBACommand(employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					workplaceCmds[6] = new WorkplaceCreateRequestCommand(CretaService.File.BASES, employeeDetail,
							fileEditor) {

						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth, int toYear,
								String tipo, java.util.Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, resultsPanel, r -> {
								/* TODO: */}, r -> showResultsPanel());

						};
					}, AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
					cretaResponseCmds[0] = new WorkplaceCreateResponseCommand(CretaService.File.BASES,
							CretaService.File.TRABAJADORES_TRAMOS, employeeDetail, resultsPanel) {
						@Override
						protected void showResultsPanel() {
							EmployeeTree.this.showResultsPanel();
						}
					}, AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);

			addSeparator();
			addItem("Refrescar", workplaceCmds[7] = new RefreshWorkplaceCommand(), AON.AON_ICON_REFRESH,
					AON.AON_ICON_CMD_BUTTON);
		}

		public void setWorkplace(Workplace workplace) {
			calcCmd.setWorkplace(workplace);

			for (WorkplaceCommand cmd : workplaceCmds)
				if (cmd != null)
					cmd.setWorkplace(workplace);

			for (WorkplaceCreateResponseCommand cmd : cretaResponseCmds)
				if (cmd != null)
					cmd.setWorkplace(workplace);

		}

		public void pasteContract() {
			pasteCmd.execute();
		}
	}

	class EnterpriseContextMenu extends ContextMenu {

		CalcEnterpriseCommand calcCmd;
		EnterpriseCommand enterpriseCommands[] = new EnterpriseCommand[5];

		public EnterpriseContextMenu() {

			MenuBar newPopup = new MenuBar(true);
			MenuItem newWorkPlaceItem = newPopup.addItem(
					getHTML("Centro", AON.AON_ICON_WORKPLACE, AON.AON_ICON_CMD_BUTTON), true,
					new NewWorkplaceCommand());
			newWorkPlaceItem.setEnabled(true);

			MenuItem newActivityItem = newPopup.addItem(getHTML("Actividad", AON.AON_ICON_INE, AON.AON_ICON_CMD_BUTTON),
					true, new NewActivityCommand());
			newActivityItem.setEnabled(true);

			addItem("Nuevo", newPopup, AON.AON_ICON_RESET, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(), AON.AON_ICON_ACCEPT,
					AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			addItem("Calcular", calcCmd = new CalcEnterpriseCommand(), AON.AON_ICON_TASK_START,
					AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					enterpriseCommands[0] = new EnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS, employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					enterpriseCommands[1] = new EnterpriseCretaRequestCommand(CretaService.File.SOLICITUD_BORRADOR,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					enterpriseCommands[2] = new EnterpriseCretaRequestCommand(CretaService.File.SOLICITUD_CONFIRMACION,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					enterpriseCommands[3] = new EnterpriseCretaRequestCommand(CretaService.File.SOLICITUD_CALCULOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					enterpriseCommands[4] = new EnterpriseDBACommand(employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
		}

		void setEnterprise(Enterprise enterprise) {
			calcCmd.setEnterprise(enterprise);
			for (EnterpriseCommand cmd : enterpriseCommands)
				if (cmd != null)
					cmd.setEnterprise(enterprise);
		}

	}

	class EmployeeContextMenu extends ContextMenu {

		private CalcEmployeeCommand calcCmd;
		private CopyEmployeeCommand copyCmd;
		private DeleteEmployeeCommand deleteCmd;
		private RefreshEmployeeCommand refreshCmd;

		public EmployeeContextMenu() {

			MenuItem saveItem = addItem("Guardar", new NewEmployeeCommand(), AON.AON_ICON_ACCEPT,
					AON.AON_ICON_CMD_BUTTON);
			saveItem.setEnabled(false);
			addSeparator();
			addItem("Copiar", copyCmd = new CopyEmployeeCommand(), AON.AON_ICON_COPY, AON.AON_ICON_CMD_BUTTON);
			addItem("Eliminar", deleteCmd = new DeleteEmployeeCommand(), AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			MenuItem runItem = addItem("Calcular", calcCmd = new CalcEmployeeCommand(), AON.AON_ICON_TASK_START,
					AON.AON_ICON_CMD_BUTTON);
			addItem("Resultados", new ShowResultsCommand(), AON.AON_ICON_TIME, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("Refrescar", refreshCmd = new RefreshEmployeeCommand(), AON.AON_ICON_REFRESH,
					AON.AON_ICON_CMD_BUTTON);
		}

		public void setEmployee(Employee employee) {
			calcCmd.setEmployee(employee);
			refreshCmd.setEmployee(employee);
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

		abstract Date getStartDate();

		abstract Workplace getWorkplace();

		/**
		 * {@link #onRangeChanged(HasData)} is called when the table requests a new
		 * range of data. You can push data back to the displays using
		 * {@link #updateRowData(int, List)}.
		 */
		@Override
		protected void onRangeChanged(HasData<Employee> display) {
			// Get the new range.
			final Range range = display.getVisibleRange();
			// Query the data asynchronously (RPC call).
			getServiceAsync().getEmployees(getWorkplace().getId(), getStartDate(),
					EmployeeTree.this.employees.getNamePattern(), range.getStart(), range.getLength(),
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

		DomainEmployeesServiceAsync getServiceAsync() {
			return EmployeeTree.this.employees.getEmployeesService();
		}
	}

	private class BaseCretaDetail extends AbstractBaseCretaDetail {

		public BaseCretaDetail() {
			super(
				EmployeeTree.this.employeeDetail, 
				EmployeeTree.this.resultsPanel, 
				EmployeeTree.this.progressPanel, 
				EmployeeTree.this::reftification,
				EmployeeTree.this::requestSendRNT,
				EmployeeTree.this::acceptPrevBases,
				EmployeeTree.this::showResultsPanel,
				EmployeeTree.this::showProgressPanel,
				EmployeeTree.this::i54
				);
		}

		@Override
		protected String getDescription(String ccc) {
			return null;
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			return null;
		}

		@Override
		protected void onClickDBAButton(ClickEvent e) {}

		@Override
		protected void onClickTrabajadoresYTramosButton(ClickEvent e) {}

		@Override
		protected void onClickConfirmacionButton(ClickEvent e) {}

		@Override
		protected void onClickBorradorButton(ClickEvent e) {}
	}
	
	private class CCCCretaDetail extends BaseCretaDetail {
		
		class IdcCommand implements ScheduledCommand {
			@Override
			public void execute() {
				showIdc(DateUtils.getFirstDayOfMonth());
			}
		}
		
		class laboralLifeCommand implements ScheduledCommand {
			@Override
			public void execute() {
				showLaboralLifeCommand(DateUtils.getFirstDayOfMonth());
			}
		}
		
		class Up2DateCommand implements ScheduledCommand {
			@Override
			public void execute() {
				onUp2DateSS();
			}
		}

		private CCC ccc;
		
		private Button idcButton;
		private Button up2DateButton;
		
		public CCCCretaDetail() {			
			idcButton = new Button("INFORME DATOS DE COTIZACI\u00D3N-CCC (IDC)", (ClickHandler) e -> onClickIdcButton(e));
			up2DateButton = new Button("CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.", (ClickHandler) e -> onClickUp2DateSSButton(e));
			addSLDMenuItem("INFORME DATOS DE COTIZACI\u00D3N-CCC (IDC)", new IdcCommand());
			addSLDMenuItem("VIDA LABORAL", new laboralLifeCommand());
		}

		public void setCCC(CCC ccc) {
			this.ccc = ccc;
		}
		
		protected void setSLDButtonsVisible(boolean enabled) {
			setIdcButtonVisible(enabled);
			setUp2DateButtonVisible(enabled);
		}
		
		protected void setIdcButtonVisible(boolean enabled) {
			idcButton.setVisible(enabled);
		}

		protected void setUp2DateButtonVisible(boolean enabled) {
			up2DateButton.setVisible(enabled);
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			List<T> filtered = new ArrayList<T>();

			for (T jsFile : jsFiles)
				if (MainCreta.accept(ccc, jsFile.getCCC()))
					filtered.add(jsFile);

			return filtered;
		}

		@Override
		protected String getDescription(String fullccc) {
			return MainCreta.getDescription(ccc, fullccc);
		}

		@Override
		protected void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		protected void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		protected void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		protected void onClickDBAButton(ClickEvent e) {
			CCCDBACommand cmd = new CCCDBACommand();
			cmd.setCCC(ccc);
			cmd.execute();
		}
		
		void onClickIdcButton(ClickEvent e) {
			showIdc(DateUtils.getFirstDayOfMonth());
		}
		
		@Override
		protected void onClickEmployee(int x, int y, String naf) {
			EmployeeTree.this.employees.selectEmployee(naf, true);
		}
		
		void showLaboralLifeCommand(Date date) {
			FullViewer viewer = getLaboralLifePDF(employeeDetail, date);
			DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
			showLoadingMessage("Cargando vida laboral del ccc " + ccc.getRegime() + " " + ccc.getCode());
			
			enterpriseService.getCCCLaboralLife(ccc.getRegime(), ccc.getCode(), date, new Date(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String dataURI) {
					hideMessagePanel();
					viewer.open(dataURI);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(new HashMap<String, String>(){{ put("Error Vida Laboral", caught.getMessage()); }});
				}
			});
		}
		
		FullViewer getLaboralLifePDF(DetailPanel detailPanel, Date date) {
			DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
			detailPanel.setWidget(dock);
			AonToolbar tb = new AonToolbar("VIDA LABORAL (" + ccc.getRegime() + " " + ccc.getCode() + ")");
			AonToolbarButton closePDF = new AonToolbarButton("Volver", AON.CSS.aonIconBack());
			closePDF.addClickHandler(e -> {
				onCCCSelected(ccc);
			});
			tb.add(closePDF);
			dock.addNorth(tb, AonToolbar.HEIGTH);
			
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(52);
			monthListBox.setVisibleRange(0, 52);
			monthListBox.addChangeHandler(e -> showLaboralLifeCommand(monthListBox.getSelectedMonth()));
			monthListBox.setSelected(date, true);
			monthListBox.setWidth("200px");
			tb.add(monthListBox);
			
			FullViewer viewer = new FullViewer();
			dock.add(viewer);
			return viewer;
		}

		void showIdc(Date date) {
			FullViewer viewer = getIDCPDF(employeeDetail, date);
			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", SistemaREDService.SISTEMA_RED_URL+ "/" + SistemaREDService.IDC_CCC_REPORT);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();
					if (state != XMLHttpRequest.DONE)
						return;
					try {
						String dataURI = xhr.getResponseText();
						viewer.open(dataURI);
						AON.stop();
					} catch ( Throwable t ) {
						Window.alert(t.getMessage());
						AON.fail();
					}
				}
			});

			StringBuffer requestDataBuffer = new StringBuffer();

			requestDataBuffer
			.append(SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL())
			.append("&" +SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser() )
			.append("&" +SistemaREDService.Parameter.REGIME.name() + "=" + ccc.getRegime() )
			.append("&" +SistemaREDService.Parameter.CCC.name() + "=" + ccc.getCode() )
			.append("&" +SistemaREDService.Parameter.DATE.name() + "=" + DateTimeFormat.getFormat(SistemaREDService.DATE_PATTERN).format(date) )
			;
			
			xhr.send(requestDataBuffer.toString());
			AON.start();
			
		}
		
		FullViewer getIDCPDF(DetailPanel detailPanel, Date date) {
			DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
			detailPanel.setWidget(dock);
			AonToolbar tb = new AonToolbar("IDC/PL-CCC");
			dock.addNorth(tb, AonToolbar.HEIGTH);
			
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -1);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(13);
			monthListBox.setVisibleRange(0, 13);
			monthListBox.addChangeHandler(e -> showIdc(monthListBox.getSelectedMonth()));
			monthListBox.setSelected(date, true);
			monthListBox.setWidth("200px");
			tb.add(monthListBox);
			
			FullViewer viewer = new FullViewer();
			dock.add(viewer);
			return viewer;
		}
		
		void onClickUp2DateSSButton(ClickEvent e) {
			onUp2DateSS();
		}
		
		private void onUp2DateSS() {
			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", SistemaREDService.SISTEMA_RED_URL+ "/" + SistemaREDService.UP2DATE_CCC_REPORT);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();
					if (state != XMLHttpRequest.DONE)
						return;
					try {
						String dataURI = xhr.getResponseText();
						showPDF(dataURI, employeeDetail);
						AON.stop();
					} catch ( Throwable t ) {
						AON.fail();
					}
				}
			});

			StringBuffer requestDataBuffer = new StringBuffer();

			requestDataBuffer
			.append(SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL())
			.append("&" +SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser() )
			.append("&" +SistemaREDService.Parameter.REGIME.name() + "=" + ccc.getRegime() )
			.append("&" +SistemaREDService.Parameter.CCC.name() + "=" + ccc.getCode() )
			;
			
			xhr.send(requestDataBuffer.toString());
			AON.start();
		}

		protected void onRequestCommand(File file) {
			CCCCretaRequestCommand cmd = new CCCCretaRequestCommand(file);
			cmd.setCCC(ccc);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for (Employee e : ccc.getEmployees())
				if (jsEmployee.getNaf().equals(e.getSocialSecurity()))
					return e.getFullname();

			return super.getEmployeeFullName(jsEmployee);
		}
	}

	private class EmployeeTreeSyncCallback implements SyncCallback {

		private Task syncTask;
		private List<JsBases> jsBasess;
		private List<JsRespuesta> jsRespuestas;
		private List<JsTrabajadoresYTramos> jsTrabajadoresYTramoss;

		public EmployeeTreeSyncCallback(Task syncTask) {
			this.syncTask = syncTask;
			this.jsBasess = new ArrayList<JsBases>(5);
			this.jsRespuestas = new ArrayList<JsRespuesta>(5);
			this.jsTrabajadoresYTramoss = new ArrayList<JsTrabajadoresYTramos>(5);
		}

		@Override
		public void onEnd() {
			syncTask.messageChanged("Sincronizaci\u00F3n completada");
			syncTask.finished();
			closeFootPanel();
			// MainCreta.this.enterprises.refresh();

			if (!jsTrabajadoresYTramoss.isEmpty())
				MainCreta.add(File.TRABAJADORES_TRAMOS,
						jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[jsTrabajadoresYTramoss.size()]));
			if (!jsRespuestas.isEmpty())
				MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[jsRespuestas.size()]));
			try {
				if (!jsBasess.isEmpty())
					MainCreta.add(File.BASES, jsBasess.toArray(new JsBases[jsBasess.size()]));
			} catch (Throwable t) {
			}

			// MainCreta.this.enterprises.refresh();

			jsBasess.clear();
			jsRespuestas.clear();
			jsTrabajadoresYTramoss.clear();

			EmployeeTree.this.cccCretaDetail.onTrabajadoresYTramos();

		}

		@Override
		public void onBegin() {
			syncTask.setDescription("Sincronizando mensajes");
		}

		@Override
		public void onMsg(String msg) {
			syncTask.messageChanged(msg);
		}

		@Override
		public void onError(Throwable caught) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onBases(JsBases jsBases) {
			syncTask.messageChanged(File.BASES.getFilename() + " " + Province.getName(jsBases.getCCC().substring(4, 6))
					+ " (" + jsBases.getCCC().substring(6) + ")" + " Sincronizado");
			// TODO:
			jsBasess.add(jsBases);
			if (jsBasess.size() < 5)
				return;

			MainCreta.add(File.BASES, jsBasess.toArray(new JsBases[5]));
			jsBasess.clear();
		}

		@Override
		public void onRespuesta(JsRespuesta jsRespuesta) {
			syncTask.messageChanged(File.RESPUESTA.getFilename() + " " + jsRespuesta.getDate() + " "
					+ jsRespuesta.getType() + " " + Province.getName(jsRespuesta.getCCC().substring(4, 6)) + " ("
					+ jsRespuesta.getCCC().substring(6) + ")" + " Sincronizado");

			jsRespuestas.add(jsRespuesta);
			if (jsRespuestas.size() < 5)
				return;

			MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[5]));
			jsRespuestas.clear();

		}

		@Override
		public void onTrabajadoresYTramos(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
			syncTask.messageChanged(File.TRABAJADORES_TRAMOS.getFilename() + " " + jsTrabajadoresYTramos.getDate() + " "
					+ jsTrabajadoresYTramos.getType() + " "
					+ Province.getName(jsTrabajadoresYTramos.getCCC().substring(4, 6)) + " ("
					+ jsTrabajadoresYTramos.getCCC().substring(6) + ")" + " Sincronizado");
			jsTrabajadoresYTramoss.add(jsTrabajadoresYTramos);
			if (jsTrabajadoresYTramoss.size() < 5)
				return;
			MainCreta.add(File.TRABAJADORES_TRAMOS, jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[5]));

			jsTrabajadoresYTramoss.clear();
		}
	}
	
	private static class CustomTabLayoutPanel extends ResizeComposite implements ProvidesResize {
		
		private static interface SelectCallback {
			void selected();
		}
		
		private class Tab extends SimplePanel {
			private Element containerElement;

			public Tab(Widget child) {
				super(Document.get().createDivElement());
				containerElement = Document.get().createDivElement();
				getElement().appendChild(containerElement);

				setWidget(child);
				setStyleName("gwt-TabLayoutPanelTab");
				containerElement.setClassName("gwt-TabLayoutPanelTabInner");

				getElement().addClassName(CommonResources.getInlineBlockStyle());
				
			}

			public void setSelected(boolean selected) {
				if (selected) {
					addStyleDependentName("selected");
				} else {
					removeStyleDependentName("selected");
				}
			}
			
			public HandlerRegistration addClickHandler(ClickHandler handler) {
				return addDomHandler(handler, ClickEvent.getType());
			}
			
			
		    @Override
		    protected com.google.gwt.user.client.Element getContainerElement() {
		      return containerElement.cast();
		    }
			
		    

		}

		private int selectedIndex = -1;
		private final FlowPanel tabBar = new FlowPanel();
		private final ArrayList<Tab> tabs = new ArrayList<Tab>();
		private final SimpleLayoutPanel widgetPanel = new SimpleLayoutPanel();
		private final ArrayList<Widget> widgets = new ArrayList<Widget>();
		
		
		public CustomTabLayoutPanel() {
			this(38, Unit.PX);
		}
		
		public CustomTabLayoutPanel(double barHeight, Unit barUnit) {
			LayoutPanel panel = new LayoutPanel();
			initWidget(panel);
			
			// Add the tab bar to the panel.
			panel.add(tabBar);
			panel.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
			panel.setWidgetTopHeight(tabBar, 0, Unit.PX, barHeight, barUnit);
			panel.setWidgetVerticalPosition(tabBar, Alignment.END);
			
			// Add the deck panel to the panel.
			panel.add(widgetPanel);
			panel.setWidgetLeftRight(widgetPanel, 0, Unit.PX, 0, Unit.PX);
			panel.setWidgetTopBottom(widgetPanel, barHeight, barUnit, 0, Unit.PX);
			
			
			setStyleName("gwt-TabLayoutPanel");
			tabBar.setStyleName("gwt-TabLayoutPanelTabs");
			widgetPanel.addStyleName("gwt-TabLayoutPanelContentContainer");

			// Make the tab bar extremely wide so that tabs themselves never wrap.
			// (Its layout container is overflow:hidden)
			tabBar.getElement().getStyle().setWidth(16384, Unit.PX);

		}
		
		public int getTabCount() {
			return tabs.size();
		}
		
		public void add(String text, Widget w, SelectCallback callback) {
			Tab tab = new Tab(new Label(text));
			tabs.add(tab);
			widgets.add(w);
			tabBar.add(tab);
		    tab.addClickHandler(event -> {
		    	showWidget(w);
		    	selectTab(tab);
		    	callback.selected();
		    });
		    tab.ensureDebugId(normalize(text)+"Tab");
		}
		
		public void select(int index) {
			selectTab(index);
			showWidget(index);
		}

		public void selectWidget(Widget w) {
			select( widgets.indexOf(w));
		}
		
		private void selectTab(Tab tab) {
			selectTab(tabs.indexOf(tab));
		}

		public void setVisibleWidget(Widget w, boolean visible) {
			setVisibleTab(widgets.indexOf(w), visible);
		}

		private void selectTab(int index) {
		    checkIndex(index);
		    if (index == selectedIndex) {
		      return;
		    }

		    // Update the tabs being selected and unselected.
		    if (selectedIndex != -1) {
		      tabs.get(selectedIndex).setSelected(false);
		    }

		    tabs.get(index).setSelected(true);
		    selectedIndex = index;

		}
		
		private void setVisibleTab(int index, boolean visible) {
		    checkIndex(index);
		    tabs.get(index).setVisible(visible);
		}

		private void showWidget(Widget w) {
			widgetPanel.setWidget(w);
		}
		
		private void showWidget(int index) {
		    checkIndex(index);
		    Widget widget = widgets.get(index);
		    if ( widget == widgetPanel.getWidget())
		    	return;
			widgetPanel.setWidget(widget);
		}

		private void checkIndex(int index) {
		    assert (index >= 0) && (index < getTabCount()) : "Index out of bounds";
		}
		
	}
	
	private class EmployeeTabLayoutPanel extends CustomTabLayoutPanel {
		
		private SalaryDraftObject salaryDraft;
		
        private Map<Integer, ContractBonusObject> contractBonusMap ;  
        private Map<Integer, SSPECObject> ssPECMap ;  
        private Map<Integer, Mod145Object> mod145Map ;  
		private Map<Integer, CategoryDraftObject> contractCategoriesMap ;  
		private Map<Integer, EmployeeContractPaymentsObject> contractPaymentsMap ;  
		private Map<Integer, EmployeeContractVariablesObject> contractVariablesMap ;  
		
		public EmployeeTabLayoutPanel() {
			contractBonusMap = new HashMap<>();
			ssPECMap = new HashMap<>();
			mod145Map = new HashMap<>();
			contractPaymentsMap = new HashMap<>();
			contractVariablesMap = new HashMap<>();
			contractCategoriesMap = new HashMap<>();
			add("Contrato", getEmployeeDraft(), this::onEmployeeSelected);
			add("N\u00f3minas", getEmployeeSalary(), this::onSalariesSelected);
			add("Calendario", getEmployeeCalendarDraftNew(), this::onCalendarSelected);
			add("Peculiaridades", getEmployeeSSPEC(), this::onSSPECSelected);
			add("Mod145", getMod145(), this::onMod145Selected);
			add("Borrador", getSalaryDraft(), this::onDraftSelected);
			add("Variables", getEmployeeEventsDraft(), this::onEventsSelected);
			add("Convenio", getAgreementPreview(), this::onAgreementTabSelected);
			add("Conceptos de C\u00e1lculo", getEmployeeContractPayments(), this::onPaymentsSelected);
			add("Variables de C\u00e1lculo", getEmployeeContractVariables(), this::onVariablesSelected);
		}

		void onDraftSelected() {
			getSalaryDraft().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getSalaryDraft().setSalaryDraftObject(salaryDraft);
		}

		void onEventsSelected() {
			getEmployeeEventsDraft().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			employees.getEmployeeEvents(salaryDraft, o -> getEmployeeEventsDraft().setEmployeeEventsDraftObject(o));
		}

		void onCalendarSelected() {
			getEmployeeCalendarDraftNew().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			employees.getEmployeeCalendar(salaryDraft, o -> getEmployeeCalendarDraftNew().setEmployeeCalendarDraftObject(o));
		}
	
		void onSSPECSelected() {
			getEmployeeSSPEC().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			SSPECObject ssPECObject = ssPECMap.get(salaryDraft.getEmployeeId());
			if(null == ssPECObject) {
				ssPECObject = new SSPECObject(salaryDraft.getEmployeeId(), salaryDraft.getEmployee().getStartDate(), salaryDraft.getEmployee().getEndDate());
				ssPECMap.put(salaryDraft.getEmployeeId(), ssPECObject);
			}
			getEmployeeSSPEC().setContractSSPECObject(ssPECObject);
		}
		
		void onMod145Selected() {
			getMod145().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			Mod145Object mod145Object = mod145Map.get(salaryDraft.getEmployeeId());
			if(null == mod145Object) {
				mod145Object = new Mod145Object(salaryDraft.getEmployeeId(), enterprise.getDomain());
				mod145Map.put(salaryDraft.getEmployeeId(), mod145Object);
			}
			getMod145().setMod145Object(mod145Object);
		}

		void onSalariesSelected() {
			getEmployeeSalary().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			employees.getEmployeeSalary(salaryDraft, o -> getEmployeeSalary().setEmployeeSalaryObject(o));
		}

		void onPaymentsSelected() {
			getEmployeeContractPayments().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			EmployeeContractPaymentsObject employeeContractPaymentsObject = contractPaymentsMap.get(salaryDraft.getEmployeeId());
			if(null == employeeContractPaymentsObject) {
				employeeContractPaymentsObject = new EmployeeContractPaymentsObject(salaryDraft.getEmployeeId(), salaryDraft.getEmployee().getStartDate(), salaryDraft.getEmployee().getEndDate());
				contractPaymentsMap.put(salaryDraft.getEmployeeId(), employeeContractPaymentsObject);
			}
			getEmployeeContractPayments().setEmployeeContractPaymentsObject(employeeContractPaymentsObject);
		}

		void onVariablesSelected() {
			getEmployeeContractVariables().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			EmployeeContractVariablesObject employeeContractVariablesObject = 
					contractVariablesMap.computeIfAbsent(salaryDraft.getEmployeeId(), this::newEmployeeContractVariablesObject );
			getEmployeeContractVariables().setEmployeeContractVariablesObject(employeeContractVariablesObject);
		}

		void onEmployeeSelected() {
			getEmployeeDraft().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			employees.getEmployeeDraft(salaryDraft, o -> { 
				getEnterpriseContext( o::setEnterpriseContext );
				getEmployeeDraft().setEmployeeDraftObject(o);
				singlenton.employee = o.getEmployee();
			});	
		}
		
		void onAgreementTabSelected(){
			Integer agreementId = salaryDraft.getEmployee().getCategory().getAgreement().getId();
			Integer levelId = salaryDraft.getEmployee().getCategory().getLevelId();
			
			showLoadingMessage("Obteniendo convenio " + salaryDraft.getEmployee().getCategory().getAgreement().getDescription()  + " ...");
			
			DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
			impl.getAgreementInfo(agreementId, false, new AsyncCallback<AgreementInfo>() {

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(new HashMap<String, String>(){{ put("Error Convenio", caught.getMessage()); }});
				}

				@Override
				public void onSuccess(AgreementInfo agreementInfo) {
					getAgreementPreview().resetSelectedDate();
					getAgreementPreview().setAgreementPreview(agreementInfo);
					getAgreementPreview().setSelectedLevel(levelId, getTitle(salaryDraft.getEmployee()));
					hideMessagePanel();
				}
			});
			
		}

		public void setSalaryDraft(SalaryDraftObject salaryDraft) {
			this.salaryDraft = salaryDraft;
			getEmployeeDraft().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getEmployeeSalary().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getSalaryDraft().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getEmployeeSSBonus().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getEmployeeCalendarDraftNew().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getEmployeeContractPayments().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			getEmployeeContractVariables().setToolbarTitle(getTitle(salaryDraft.getEmployee()));

			getAgreementPreview().setToolbarTitle(getTitle(salaryDraft.getEmployee()));
			setVisibleWidget(getAgreementPreview(), salaryDraft.getEmployee().getCategory() != null );
		}
		
		private EmployeeContractVariablesObject newEmployeeContractVariablesObject(Integer contractId){
			return new EmployeeContractVariablesObject(contractId, salaryDraft.getEmployee().getStartDate(), salaryDraft.getEmployee().getEndDate());
		}
		
		private String getTitle(Employee employee) {
			return employee.getFullname();
		}
		
	}

	private class WorkplaceTabLayoutPanel extends CustomTabLayoutPanel {
		
		private Workplace workplace;
		
		public WorkplaceTabLayoutPanel() {
			add("Centro de Trabajo", getWorkplaceDraft(), this::onWorkplaceSelected);
			add("Costes", getCost(), this::onCostsSelected);
			add("N\u00f3minas", getWorkplceSalary(), this::onSalariesSelected);
			add("Calendario", getCalendarDraft(), this::onCalendarSelected);
			add("Estad\u00edsticas", getStats(), this::onStatsSelected);
			add("Partes IT", getWorkplceIT(), this::onITsSelected);
			if ( Wnd.isSysAdmin() ) {
				add("Variables de C\u00e1lculo", getEventsDraft(), this::onEventsSelected);
			}
		}

		void onITsSelected() {
			employees.getWorkplaceIT(workplace, o -> getWorkplceIT().setWorkplaceITObject(o));
		}

		void onCostsSelected() {
			employees.getWorkplaceCost(workplace, o -> getCost().setCostDocuments(o));
		}

		void onStatsSelected() {
			employees.getWorkplaceStatistics(workplace, o -> getStats().setStatistics(o));
		}

		void onCalendarSelected() {
			employees.getWorkplaceCalendar(workplace, o -> getCalendarDraft().setCalendarDraftObject(null, o) );
		}

		void onEventsSelected() {
			employees.getWorkplaceEvents(workplace, o -> getEventsDraft().setEventsDraftObject(o));
		}

		void onSalariesSelected() {
			employees.getWorkplaceSalary(workplace, o -> getWorkplceSalary().setWorkplaceSalaryObject(o));
		}

		void onWorkplaceSelected() {
			// NOOP
		}
		
		void checkWorkplaceAgreements() {
			if(null == this.workplace || null == this.workplace.getAgreement() || this.getTabCount() > 6) return;
			add("Convenio", getAgreementPreview(), this::onAgreementTabSelected);
		}
		
		void onAgreementTabSelected(){
			Integer agreementId = workplace.getAgreement().getId();
			
			showLoadingMessage("Obteniendo convenio " + workplace.getAgreement().getDescription()  + " ...");
			
			DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
			impl.getAgreementInfo(agreementId, true, new AsyncCallback<AgreementInfo>() {

				@Override
				public void onFailure(Throwable caught) {
					showErrorMessage(new HashMap<String, String>(){{ put("Error Convenio", caught.getMessage()); }});
				}

				@Override
				public void onSuccess(AgreementInfo agreementInfo) {
					getAgreementPreview().resetSelectedDate();
					getAgreementPreview().setAgreementPreview(agreementInfo);
					getAgreementPreview().payrollPreview();
					hideMessagePanel();
				}
			});
			
		}
		
		public void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
			checkWorkplaceAgreements();
		}
	}

	private class EnterpriseTabLayoutPanel extends CustomTabLayoutPanel {
		
		private Enterprise enterprise;
		
		public EnterpriseTabLayoutPanel() {
			add("Empresa", getEnterpriseDraft(), this::onEnterpriseSelected);
			add("Costes", getCost(), this::onCostsSelected);
			add("N\u00f3minas", getEnterpriseSalary(), this::onSalariesSelected);
			add("Estad\u00edsticas", getStats(), this::onStatsSelected);
			add("Partes IT", getEnterpriseIT(), this::onITsSelected);
		}
		
		void onITsSelected() {
			employees.getEnterpriseIT(enterprise, o-> getEnterpriseIT().setEnterpriseITObject(o));
		}

		void onCostsSelected() {
			employees.getEnterpriseCost(enterprise, o -> getCost().setCostDocuments(o));
		}

		void onStatsSelected() {
			employees.getEnterpriseStatistics(enterprise, o -> getStats().setStatistics(o));
		}

		void onSalariesSelected() {
			employees.getEnterpriseSalary(enterprise, o -> {
				getEnterpriseSalary().setEnterpriseSalaryObject(o);
				getEnterpriseSalary().setEnterpriseView();
			});
		}

		void onEnterpriseSelected() {
			// NOOP
		}

		public void setEnterprise(Enterprise enterprise) {
			this.enterprise = enterprise;
		}
		
	}

	private static EmployeeTree singlenton;

	interface Binder extends UiBinder<Widget, EmployeeTree> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final String EMPLOYEE = "C-EMPLOYEE";

	@UiField(provided = true)
	Employees employees;
	@UiField
	DetailPanel employeeDetail;
	@UiField
	SplitLayoutPanel dockLayoutPanel;

	@UiField
	HTMLPanel messagePanel;

	@UiField
	AonMinimizePanel footPanel;
	
	@UiField
	TabLayoutPanel footTabPanel;
	

	private JSF jsf;
	private Documents documents;
	private Cost cost;
	private Irpf irpf;
	private Salary salary;
	private Statistics stats;
	private EnterpriseSalary enterpriseSalary;
	private EnterpriseIT enterpriseIT;
	private ITEditor it;
	private WorkplaceSalary workplaceSalary;
	private WorkplaceIT workplaceIT;
	private CalendarDraft calendarDraft;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	private EventsDraft eventsDraft;
	private EnterpriseDraft enterpriseDraft;
	private WorkplaceDraft workplaceDraft;
	private ActivityDraft activityDraft;
	private EmployeeEventsDraft employeeEventsDraft;
	private EmployeeDraft employeeDraft;
	private EmployeeCalendarDraft employeeCalendarDraft;
	private EmployeeCalendarDraftNew employeeCalendarDraftNew;
	private ContractBonusUI employeeSSBonus;
	private SSPECDraft ssPECDraft;
	private Mod145 mod145;
	private EmployeeContractPayments employeeContractPayments; 
	private EmployeeContractVariables employeeContractVariables; 
	private EmployeeSalary employeeSalary;
	private com.esferalia.aon.gwt.payroll.client.CategoryDraft categoryDraft;
	private AgreementPreview agreementPreview;
	private AgreementDraft agreementDraft;
	private BonusEditor bonusEditor;
	private PaymentEditor paymentEditor;
	private DeductionEditor deductionEditor;
	private EmployeePopupCopy paste;

	private FileEditor fileEditor;

	private TimeTask sldTask;
	private ResultsPanel resultsPanel;
	private ProgressPanel progressPanel;
	private FlowPanel costsProblemsPanel;

	private CCCContextMenu cccContextMenu;
	private EmployeeContextMenu employeeContextMenu;
	private WorkplaceContextMenu workplaceContextMenu;
	private EnterpriseContextMenu enterpriseContextMenu;

	private Employee employee;
	private Activity activity;
	private Workplace workplace;
	private Enterprise enterprise;

	private ShareResultsGrid shareResultsGrid;
	private ListDataProvider<JsShareResult> shareResultsProvider;

	private Map<String, String> avaiableEmployees;

	private MenuItem pasteItem;

	private Storage storage;
	
	private EmployeeTabLayoutPanel employeePanel;
	private WorkplaceTabLayoutPanel workplacePanel;
	private EnterpriseTabLayoutPanel enterprisePanel;

	// Cret@
	private CCCCretaDetail cccCretaDetail;
	
	
	private EnterpriseContext enterpriseContext;

	public static native String getRootPanel()
	/*-{
		return $wnd.localStorage.getItem("rootPanel");
	}-*/;
	
	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {

		logEvent("start");
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		logEvent("richStylesInjected");
		
		employees = new Workers(true, true) {
			@Override
			public void onEnterprise(Enterprise enterprise) {
				String employeeSearch = 
				getParameter(GWT.getModuleName(), EMPLOYEE_SEARCH_PARAM);
				
				super.onEnterprise(enterprise, AonStringUtils.isBlank(employeeSearch));
				
				if ( AonStringUtils.isNotBlank(employeeSearch) ) {
					Scheduler.get().scheduleDeferred(() -> employees.search(employeeSearch) );
				}
			}
		};

		
		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		logEvent("uiCreatedAndBound");

		// Get rid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		// Window.enableScrolling(false);
		// Window.setMargin("0px");

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);
		logEvent("addedToRootPanel");

		// LocalStorage Items
		storage = Storage.getLocalStorageIfSupported();

		employees.addListener(this);
		
		employeeDetail.setHeight("100%");

		fileEditor = new FileEditor();
		resultsPanel = new ResultsPanel();
		shareResultsGrid = new ShareResultsGrid();
		shareResultsProvider = new ListDataProvider<JsShareResult>();
		shareResultsProvider.addDataDisplay(shareResultsGrid);
		logEvent("resultsWidgetsCreated");

		singlenton = this;

		export2JS();
		logEvent("end");
		try {
			jsf = new JSF();
			logEvent("jsfWidgetCreated");
		} catch (Throwable t) {
			// TODO:
		}

		try {
			new com.esferalia.aon.js.payroll.client.Reports();
		} catch (Throwable t) {

		}
			
		initFootPanel();
		
		initMessagePanel();
	}

	private void initFootPanel() {
		logEvent("initFootPanel");
		footPanel.initNewButtons();
		this.footPanel.addMaximizeHandlerNew(e-> showFootPanel());
		this.footPanel.addMinimizeHandlerNew(e-> closeFootPanel());
	}
	
	private void initMessagePanel() {
		logEvent("initMessagePanel");
		hideMessagePanel();
	}

	// --------------------------------------------------- Cost.Listener methods

	@Override
	public void onPublish(CostDocuments documents, String type) {
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

		com.esferalia.aon.gwt.payroll.shared.Cost cost = documents.getCosts().get(documents.getCurrentIndex());
		share(cost, type, new Callback());

		showResultsPanel();

	}
	
	@Override
	public void onStartSLD() {
		progressPanel = new ProgressPanel();

		HandlerRegistration handlerRegistration [] = new HandlerRegistration[1];
		handlerRegistration[0] = progressPanel.addAttachHandler(e -> {
			// Synchronize cret@ messages.
			sldTask = new TimeTask();
			sldTask.startTime();
			sldTask.setDescription("Consultando C\u00e1lculos del SISTEMA RED ( Remesas SLD, Sistema de Liquidaci\u00f3n Directa )");
			progressPanel.showTask(sldTask);
			handlerRegistration[0].removeHandler();
		});
		showProgressPanel();
		
	}
	
	@Override
	public void onFinishSLD() {
		sldTask.endTime();
		hideProgressPanel();
	}
	
	@Override
	public void onProgressSLD(String message, double progress ) {
		sldTask.endTime();
		sldTask.progressChanged(progress);
		sldTask.messageChanged(message + " " + sldTask.getTimeSeconds() + " secs");
		//progressPanel.showTask(syncTask);
	}
	
	@Override
	public void onNoSex(String naf, String name) {
		if (costsProblemsPanel == null)
			costsProblemsPanel = new FlowPanel();
		Label lbl = new Label("ADVERTENCIA: Sexo no definido - NAF: " + naf + ", Nombre: "+ name);
		lbl.getElement().getStyle().setColor("orange");
		costsProblemsPanel.add(lbl);
		
		showCostProblemsPanel();
		
	}
		
	@Override
	public void onGeneratingDocument() {
		if (costsProblemsPanel == null)
			costsProblemsPanel = new FlowPanel();
		
		Label lbl = new Label("Se est\u00E1 generando su informe. Por favor, espere unos segundos...");
		lbl.addStyleName(AON.CSS.aonColorGreen());		
		costsProblemsPanel.add(lbl);
		
		showCostProblemsPanel();
	}

	// ------------------------------------------------- Salary.Listener methods

	@Override
	public void onPublishSalaries(SalaryInfo salary, String type) {
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

		shareSalary(salary, type, new Callback());
		showResultsPanel();

	}

	// ------------------------------------------------- Salary.Listener methods

	@Override
	public void onPublis(SalaryDocuments documents, String type) {
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

		com.esferalia.aon.gwt.payroll.shared.Salary salary = documents.getSalaries().get(documents.getCurrentIndex());
		share(salary, type, new Callback());
		showResultsPanel();

	}

	// ---------------------------------------------- Employees.Listener methods

	@Override
	public void onLoadAvaiableEmployees(Map<String, String> map) {
		avaiableEmployees = (map != null) ? map : new HashMap<String, String>();
	}

	@Override
	public void onEnterpriseSelected(Enterprise enterprise) {
		EnterpriseDraftObject enterpriseDraftObject = new EnterpriseDraftObject(enterprise);

		employeeDetail.setWidget(getEnterprisePanel());
		getEnterprisePanel().setEnterprise(enterprise);
		getEnterprisePanel().selectWidget(getEnterpriseDraft());
		
		getEnterpriseContext(enterpriseCtx -> {
			enterpriseDraftObject.setAgreements(enterpriseCtx.getAgreements());
			enterpriseDraftObject.setScopes(enterpriseCtx.getScopes());
			getEnterpriseDraft().setEnterpriseDraftObject(enterpriseDraftObject);
		});
		

		this.enterprise = enterprise;
	}

	@Override
	public void onWorkplaceSelected(Workplace workplace) {

		WorkplaceDraftObject employeeNewDraftObject = new WorkplaceDraftObject(enterprise, workplace);
		getEnterpriseContext(enterpriseCtx -> employeeNewDraftObject.setAgreements(enterpriseCtx.getAgreements()));

		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().setWorkplace(workplace);
		getWorkplacePanel().selectWidget(getWorkplaceDraft());
		getWorkplaceDraft().setWorkplaceDraftObject(employeeNewDraftObject);
		this.workplace = workplace;
	}

	@Override
	public void onEmployeeSelected(Employee employee) {

		int pos = employees.getVerticalScrollPosition();
		jsf.setRerenderHandler(() -> employees.setVerticalScrollPosition(pos));

		employeeDetail.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
		this.employee = employee;
	}

	@Override
	public void onActivitySelected(Activity activity) {
		ActivityDraftObject activityDraftObject = new ActivityDraftObject(activity.getId());

		employeeDetail.setWidget(getActivityDraft());
		getActivityDraft().setActivityDraftObject(activityDraftObject);
		
		this.activity = activity;
	}

	@Override
	public void onCCCSelected(CCC ccc) {
		getCCCCretaDetail().setCCC(ccc);
		getCCCCretaDetail().onTrabajadoresYTramos();
		employeeDetail.setWidget(getCCCCretaDetail());
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
	public void onEnterpriseCostsSelected(CostDocuments docs) {
		getCost().setTitle("Costes");
		employeeDetail.setWidget(getEnterprisePanel());
		getEnterprisePanel().selectWidget(getCost());
		getCost().setCostDocuments(docs);

	}

	@Override
	public void onWorkplaceCostsSelected(CostDocuments docs) {
		getCost().setTitle("Costes");
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getCost());
		getCost().setCostDocuments(docs);

	}

	@Override
	public void onWorkplaceStatisticsSelected(com.esferalia.aon.gwt.payroll.shared.Statistics statistics) {
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getStats());
		getStats().setStatistics(statistics);
	}

	@Override
	public void onEnterpriseStatisticsSelected(com.esferalia.aon.gwt.payroll.shared.Statistics statistics) {
		employeeDetail.setWidget(getEnterprisePanel());
		getEnterprisePanel().selectWidget(getStats());
		getStats().setStatistics(statistics);
	}

	@Override
	public void onEnterpriseSalariesSelected(EnterpriseSalaryObject enterpriseSalaryObject) {
		employeeDetail.setWidget(getEnterprisePanel());
		getEnterprisePanel().selectWidget(getEnterpriseSalary());
		getEnterpriseSalary().setEnterpriseSalaryObject(enterpriseSalaryObject);
	}
	
	@Override
	public void onEnterpriseITSelected(EnterpriseITObject enterpriseITObject) {
		employeeDetail.setWidget(getEnterprisePanel());
		getEnterprisePanel().selectWidget(getEnterpriseIT());
		getEnterpriseIT().setEnterpriseITObject(enterpriseITObject);
	}

	@Override
	public void onITDataSelected(ITDataObject dataObject) {
		employeeDetail.setWidget(getIt());
		getIt().setITEditor(dataObject);
	}

	@Override
	public void onWorkplaceSalarySelected(WorkplaceSalaryObject dataObject) {
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getWorkplceSalary());
		getWorkplceSalary().setWorkplaceSalaryObject(dataObject);
	}
	
	@Override
	public void onWorkplaceITSelected(WorkplaceITObject workplaceITObject) {
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getWorkplceIT());
		getWorkplceIT().setWorkplaceITObject(workplaceITObject);
	}

	@Override
	public void onCalendarSelected(CalendarDraftObjectData calendarDraftObjectData) {
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getCalendarDraft());
		getCalendarDraft().setCalendarDraftObject(null, calendarDraftObjectData);
	}

	@Override
	public void onEmployeeCalendarSelected(EmployeeCalendarDraftObjectData calendar) {
		employeeDetail.setWidget(getEmployeeCalendarDraft());
		getEmployeeCalendarDraft().setEmployeeCalendarDraftObject(calendar);
	}

	@Override
	public void onEmployeeNewCalendarSelected(EmployeeCalendarDraftObject calendar) {
		employeeDetail.setWidget(getEmployeePanel());
		getEmployeePanel().selectWidget(getEmployeeCalendarDraftNew());
		getEmployeeCalendarDraftNew().setEmployeeCalendarDraftObject(calendar);
		employees.getEmployeeSalaryDraft(calendar, o -> getEmployeePanel().setSalaryDraft(o));
	}
	
	@Override
	public void onEmployeeSSBonusSelected(ContractBonusObject contractBonusObject) {
		employeeDetail.setWidget(getEmployeePanel());
		getEmployeePanel().selectWidget(getEmployeeSSBonus());
		getEmployeeSSBonus().setContractBonusObject(contractBonusObject);
		employees.getEmployeeSalaryDraft(contractBonusObject, o -> getEmployeePanel().setSalaryDraft(o));
	}

	@Override
	public void onEmployeeSalarySelected(EmployeeSalaryObject employeeSalary) {
		employeeDetail.setWidget(getEmployeePanel());
		getEmployeePanel().selectWidget(getEmployeeSalary());
		getEmployeeSalary().setEmployeeSalaryObject(employeeSalary);
		employees.getEmployeeSalaryDraft(employeeSalary, o -> getEmployeePanel().setSalaryDraft(o));
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
		getEmployeePanel().setSalaryDraft(salaryDraftObject);
		employeeDetail.setWidget(getEmployeePanel());
		getEmployeePanel().selectWidget(getSalaryDraft());
		getSalaryDraft().setSalaryDraftObject(salaryDraftObject);
	}

	@Override
	public void onSalaryPreviewSelected(SalaryPreviewDocument salaryPreviewDocument) {
		employeeDetail.setWidget(getSalaryPreview());
		getSalaryPreview().setSalaryPreviewDocument(salaryPreviewDocument);
	}

	@Override
	public void onCCCContextMenu(CCC ccc, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getCCCContextMenu().setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		getCCCContextMenu().setCCC(ccc);
		getCCCContextMenu().show();
	}

	@Override
	public void onWorkplaceContextMenu(Workplace workplace, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getWorkplaceContextMenu().setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		getWorkplaceContextMenu().setWorkplace(workplace);
		getWorkplaceContextMenu().show();
	}

	@Override
	public void onEnterpriseContextMenu(Enterprise enterprise, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getEnterpriseContextMenu().setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		getEnterpriseContextMenu().setEnterprise(enterprise);
		getEnterpriseContextMenu().show();

	}

	@Override
	public void onEmployeeContextMenu(Employee employee, ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getEmployeeContextMenu().setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		getEmployeeContextMenu().setEmployee(employee);
		getEmployeeContextMenu().show();
	}

	@Override
	public void onCategoryDraftSelected(CategoryDraftObject categoryDraftObject) {
		employeeDetail.setWidget(getCategoryDraft());
		getCategoryDraft().setCategoryDraftObject(categoryDraftObject);

	}

	@Override
	public void onAgreementDraftSelected(AgreementDraftObject agreementDraftObject) {
		employeeDetail.setWidget(getAgreementDraft());
		getAgreementDraft().setAgreementDraftObject(agreementDraftObject);
	}

	@Override
	public void onEventsDraftSelected(EventsDraftObject eventsDraftObject) {
		employeeDetail.setWidget(getWorkplacePanel());
		getWorkplacePanel().selectWidget(getEventsDraft());
		getEventsDraft().setEventsDraftObject(eventsDraftObject);
	}

	@Override
	public void onEmployeeEventsDraftSelected(EmployeeEventsDraftObject employeeEventsDraftObject) {
		employeeDetail.setWidget(getEmployeePanel());
		getEmployeePanel().selectWidget(getEmployeeEventsDraft());
		getEmployeeEventsDraft().setEmployeeEventsDraftObject(employeeEventsDraftObject);
		employees.getEmployeeSalaryDraft(employeeEventsDraftObject, o -> getEmployeePanel().setSalaryDraft(o));
	}
	

	@Override
	public void onEmployeeDraftSelected(EmployeeDraftObject employeeDraftObject) {
		getEmployeeDraft().setOnSaved(e -> {
			refreshWorkplace();
		});
		employeeDetail.setWidget(getEmployeePanel());

		getEmployeePanel().selectWidget(getEmployeeDraft());
		
		getEnterpriseContext(employeeDraftObject::setEnterpriseContext);
		getEmployeeDraft().setEmployeeDraftObject(employeeDraftObject);
		singlenton.employee = employeeDraftObject.getEmployee();
		
		employees.getEmployeeSalaryDraft(employeeDraftObject, o -> getEmployeePanel().setSalaryDraft(o));
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
	
	// --------------------------------------------------------- Private methods

	private void showFootPanel() {
		footPanel.addButtonMore();
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.00);
	}
	
	private void closeFootPanel() {
		footPanel.addButtonLess();
		dockLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void showResultsPanel() {
		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		EmployeeTree.this.footTabPanel.add(EmployeeTree.this.resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);
		EmployeeTree.this.dockLayoutPanel.setWidgetSize(EmployeeTree.this.footPanel, Window.getClientHeight() / 4);

	}
	
	private void showCostProblemsPanel() {
		InlineLabel tab = new InlineLabel("Costes");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		EmployeeTree.this.footTabPanel.add(EmployeeTree.this.costsProblemsPanel, tab);
		footTabPanel.selectTab(costsProblemsPanel);
		EmployeeTree.this.dockLayoutPanel.setWidgetSize(EmployeeTree.this.footPanel, Window.getClientHeight() / 4);
		
	}

	private void selectResultsPanel() {
		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		EmployeeTree.this.footTabPanel.add(EmployeeTree.this.resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);

	}

	private void showProgressPanel() {
		InlineLabel tab = new InlineLabel("Progreso");
		tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(progressPanel, tab);
		footTabPanel.selectTab(progressPanel);
		dockLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
	}

	private void hideProgressPanel() {
		closeFootPanel();
		footTabPanel.remove(progressPanel);
	}

	private ITEditor getIt() {
		if (it == null)
			it = new ITEditor();
		return it;
	}

	private WorkplaceSalary getWorkplceSalary() {
		if (workplaceSalary == null)
			(workplaceSalary = new WorkplaceSalary()).addListener(this);
		return workplaceSalary;
	}
	
	private WorkplaceIT getWorkplceIT() {
		if (workplaceIT == null)
			workplaceIT = new WorkplaceIT();
		return workplaceIT;
	}

	private Cost getCost() {
		if (cost == null)
			(cost = new Cost()).addListener(this);

		return cost;
	}

	private Irpf getIrpf() {
		if (irpf == null)
			irpf = new Irpf();
		return irpf;
	}

	private Statistics getStats() {
		if (stats == null)
			stats = new Statistics();
		return stats;
	}

	private EnterpriseSalary getEnterpriseSalary() {
		if (enterpriseSalary == null)
			(enterpriseSalary = new EnterpriseSalary(){
				@Override
				protected void onBackClick() {}
			}).addListener(this);
		
		return enterpriseSalary;
	}
	
	private EnterpriseIT getEnterpriseIT() {
		if (enterpriseIT == null) {
			enterpriseIT = new EnterpriseIT();
			enterpriseIT.setFooter(dockLayoutPanel, footTabPanel, footPanel);
		} 
		return enterpriseIT;
	}

	public Salary getSalary() {
		if (salary == null)
			(salary = new Salary()).addListener(this);
		return salary;
	}

	private Documents getDocuments() {
		if (documents == null)
			documents = new Documents();
		return documents;
	}

	private BonusEditor getBonusEditor() {
		if (bonusEditor == null)
			bonusEditor = new BonusEditor();
		return bonusEditor;
	}

	private PaymentEditor getPaymentEditor() {
		if (paymentEditor == null)
			paymentEditor = new PaymentEditor();
		return paymentEditor;
	}

	private DeductionEditor getDeductionEditor() {
		if (deductionEditor == null)
			deductionEditor = new DeductionEditor();
		return deductionEditor;
	}

	private SalaryPreview getSalaryPreview() {
		if (salaryPreview == null)
			salaryPreview = new SalaryPreview();
		return salaryPreview;
	}

	public SalaryDraft getSalaryDraft() {
		if (salaryDraft == null)
			(salaryDraft = new SalaryDraft() {
				@Override
				protected Panel getMessagePanel() {
					return EmployeeTree.this.getMessagePanel();
				}
			}).addListener(this); 
		return salaryDraft;
	}

	private AgreementDraft getAgreementDraft() {
		if (agreementDraft == null)
			agreementDraft = new AgreementDraft();
		return agreementDraft;
	}

	private EventsDraft getEventsDraft() {
		if (eventsDraft == null)
			eventsDraft = new EventsDraft();
		return eventsDraft;
	}

	private EnterpriseDraft getEnterpriseDraft() {
		if (enterpriseDraft == null)
			enterpriseDraft = new EnterpriseDraft() {
				@Override
				protected void onCheckStatus(EnterpriseDraftObject enterpriseDraftObject) {
					checkStatus(enterpriseDraftObject);
				}
				
				@Override
				protected void showSuccessMessage(Map<String, String> messages) {
					EmployeeTree.this.showSuccessMessage(messages);
				}

				@Override
				protected void showErrorMessage(Map<String, String> messages) {
					EmployeeTree.this.showErrorMessage(messages);
				}
				
				@Override
				protected void showWarningMessage(Map<String, String> messages) {
					EmployeeTree.this.showWarningMessage(messages);
				}

				@Override
				protected void showLoadingMessage(String message) {
					EmployeeTree.this.showLoadingMessage(message);
				}
			
			}.setOnSaved(w -> refreshEnterprise());
		return enterpriseDraft;
	}
	
	private EnterpriseDraft newEnterpriseDraft() {
		return  new EnterpriseDraft() {
			@Override
			protected void onCheckStatus(EnterpriseDraftObject enterpriseDraftObject) {
				checkStatus(enterpriseDraftObject);
			}
		
			@Override
			protected void showSuccessMessage(Map<String, String> messages) {
				EmployeeTree.this.showSuccessMessage(messages);
			}

			@Override
			protected void showErrorMessage(Map<String, String> messages) {
				EmployeeTree.this.showErrorMessage(messages);
			}
			
			@Override
			protected void showWarningMessage(Map<String, String> messages) {
				EmployeeTree.this.showWarningMessage(messages);
			}

			@Override
			protected void showLoadingMessage(String message) {
				EmployeeTree.this.showLoadingMessage(message);
			}
			
		}.setOnSaved(w -> refreshEnterprise());
	}

	private EmployeeTabLayoutPanel getEmployeePanel() {
		if ( employeePanel == null ) {
			employeePanel = new EmployeeTabLayoutPanel();
		}
		return employeePanel;
	}

	private WorkplaceTabLayoutPanel getWorkplacePanel() {
		if ( workplacePanel == null ) {
			workplacePanel = new WorkplaceTabLayoutPanel();
		}
		return workplacePanel;
	}

	private EnterpriseTabLayoutPanel getEnterprisePanel() {
		if ( enterprisePanel == null ) {
			enterprisePanel = new EnterpriseTabLayoutPanel();
		}
		return enterprisePanel;
	}
	
	private WorkplaceDraft getWorkplaceDraft() {
		if (workplaceDraft == null)
			workplaceDraft = new WorkplaceDraft() {
				
				@Override
				protected void showSuccessMessage(Map<String, String> messages) {
					EmployeeTree.this.showSuccessMessage(messages);
				}
	
				@Override
				protected void showErrorMessage(Map<String, String> messages) {
					EmployeeTree.this.showErrorMessage(messages);
				}

				@Override
				protected void hideMessage() {
					EmployeeTree.this.hideMessagePanel();
				}
			
			};
		return workplaceDraft;
	}

	private ActivityDraft getActivityDraft() {
		if (activityDraft == null)
			activityDraft = new ActivityDraft() {

				@Override
				protected void showSuccessMessage(Map<String, String> messages) {
					EmployeeTree.this.showSuccessMessage(messages);
				}

				@Override
				protected void showErrorMessage(Map<String, String> messages) {
					EmployeeTree.this.showErrorMessage(messages);
				}
				
				@Override
				protected void showWarningMessage(Map<String, String> messages) {
					EmployeeTree.this.showWarningMessage(messages);
				}
				
				@Override
				protected void showInfoMessage(Map<String, String> messages) {
					EmployeeTree.this.showInfoMessage(messages);
				}

				@Override
				protected void showLoadingMessage(String message) {
					EmployeeTree.this.showLoadingMessage(message);
				}

				@Override
				protected void hideMessage() {
					EmployeeTree.this.hideMessagePanel();
				}
			};
				
		return activityDraft;
	}

	private CCCCretaDetail getCCCCretaDetail() {
		if (cccCretaDetail == null) {
			progressPanel = new ProgressPanel();
			cccCretaDetail = new CCCCretaDetail();

			// I use an array for skip compile warning/error 'handlerRegistration may not be initialized'.
			HandlerRegistration handlerRegistration [] = new HandlerRegistration[1];
			handlerRegistration[0] = progressPanel.addAttachHandler(e -> {
				// Synchronize Cret@ messages.
				Task syncTask = new Task();
				syncTask.setDescription("Sincronizando mensajes");
				progressPanel.showTask(syncTask);
				MainCreta.sync(new EmployeeTreeSyncCallback(syncTask), getCCs());
				handlerRegistration[0].removeHandler();
			});
			
			showProgressPanel();
		}
		
		return cccCretaDetail;
	}

	private EmployeeContractVariables getEmployeeContractVariables() {
		if (employeeContractVariables == null) {
			employeeContractVariables = new EmployeeContractVariables() ;
		}
		return employeeContractVariables;
	}

	private EmployeeContractPayments getEmployeeContractPayments() {
		if (employeeContractPayments == null) {
			employeeContractPayments = new EmployeeContractPayments() ;
		}
		return employeeContractPayments;
	}

	private EmployeeEventsDraft getEmployeeEventsDraft() {
		if (employeeEventsDraft == null)
			employeeEventsDraft = new EmployeeEventsDraft() {

				@Override
				protected void onShowCalendar() {
					showEmployeeCalendar(this.getEmployeeCalendarObject());
				}
				
				@Override
				protected void showEvents() {
					super.showEvents();
					hideMessagePanel();
				}
				
				@Override
				protected void showLoading() {
					super.showLoading();
					showLoadingMessage("Obteniendo variables de c\u00E1lculo del trabajador ...");
				}
				
				@Override
				protected void initLoadingPanel() {
					// NOOP
				}
				
			};
		return employeeEventsDraft;
	}

	private EmployeeDraft getEmployeeDraft() {
		if (employeeDraft == null) {
			employeeDraft = new EmployeeDraft() {
			
				@Override
				protected Panel getMessagePanel() {
					return EmployeeTree.this.getMessagePanel();
				}

				@Override
				protected void onCheckStatus(EmployeeDraftObject employeeDraftObject) {
					checkStatus(employeeDraftObject);
				}
				
			};
		}
		return employeeDraft;
	}

	private com.esferalia.aon.gwt.payroll.client.CategoryDraft getCategoryDraft() {
		if (categoryDraft == null)
			categoryDraft = new com.esferalia.aon.gwt.payroll.client.CategoryDraft();
		return categoryDraft;
	}
	
	private AgreementPreview getAgreementPreview() {
		if (agreementPreview == null)
			agreementPreview = new AgreementPreview() {

				@Override
				protected void reloadAgreement() {
					// TODO Auto-generated method stub
				}

				@Override
				public void onSaved() {
					// TODO Auto-generated method stub
				}};
		
		return agreementPreview;
	}

	private CalendarDraft getCalendarDraft() {
		if (calendarDraft == null)
			calendarDraft = new CalendarDraft();
		return calendarDraft;
	}

	private EmployeeCalendarDraft getEmployeeCalendarDraft() {
		if (employeeCalendarDraft == null)
			employeeCalendarDraft = new EmployeeCalendarDraft();
		return employeeCalendarDraft;
	}

	private EmployeeCalendarDraftNew getEmployeeCalendarDraftNew() {
		if (employeeCalendarDraftNew == null)
			employeeCalendarDraftNew = new EmployeeCalendarDraftNew();
		return employeeCalendarDraftNew;
	}
	
	
	private ContractBonusUI getEmployeeSSBonus() {
		if (employeeSSBonus == null)
			employeeSSBonus = new ContractBonusUI();
		return employeeSSBonus;
	}
	
	private SSPECDraft getEmployeeSSPEC() {
		if (ssPECDraft == null)
			ssPECDraft = new SSPECDraft() {
		
			@Override
			protected Panel getMessagePanel() {
				return EmployeeTree.this.getMessagePanel();
			}
			
		};
		return ssPECDraft;
	}
	
	private Mod145 getMod145() {
		FullViewer viewer = new FullViewer();
		
		if (mod145 == null)
			mod145 = new Mod145() {
				
				@Override
				protected void showWarningMessage(String title, String message) {
					EmployeeTree.this.showWarningMessage(new HashMap<String, String>(){{ put(title, message); }});
				}
				
				@Override
				protected void showSuccessMessage(String title, String message) {
					EmployeeTree.this.showSuccessMessage(new HashMap<String, String>(){{ put(title, message); }});
				}
				
				@Override
				protected void showErrorMessage(String title, String message) {
					EmployeeTree.this.showErrorMessage(new HashMap<String, String>(){{ put(title, message); }});
				}
				
				@Override
				protected void showLoadingMessage(String message) {
					EmployeeTree.this.showLoadingMessage(message);
				}
				
				@Override
				protected void createViewer() {
					DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
					
					AonToolbar tb = new AonToolbar("Mod145");
					AonToolbarButton closePDF = new AonToolbarButton("Cerrar PDF Mod145", AON.CSS.aonIconBack());
					closePDF.addClickHandler(e -> {
						employeeDetail.setWidget(getEmployeePanel());
						getEmployeePanel().selectWidget(getMod145());
						Mod145Object mod145Object = new Mod145Object(getMod145().getContractId(), enterprise.getDomain());
						getMod145().setMod145Object(mod145Object);
					});
					tb.add(closePDF);
					
					dock.addNorth(tb, AonToolbar.HEIGTH);
					dock.add(viewer);
					employeeDetail.setWidget(dock);
				}
				
				@Override
				protected void printPDF(String dataURI) {
					EmployeeTree.this.hideMessagePanel();
					viewer.open(dataURI);
				}
			};
			
		mod145.addMainMT();
			
		return mod145;
	}

	private EmployeeSalary getEmployeeSalary() {
		if (employeeSalary == null)
			(employeeSalary = new EmployeeSalary() {

				@Override
				protected void fireEnableDisableButtons(boolean isSomethingSelected, boolean hasSettleSelected) {
					// Nothing to do here
				}

				@Override
				protected void onSalaryShow() {
					// Nothing to do here
				}

				@Override
				protected void onPDFShow() {
					// Nothing to do here
				}})
			.addListener(this);
		
		return employeeSalary;
	}

	private EmployeeContextMenu getEmployeeContextMenu() {
		if (employeeContextMenu == null) {
			employeeContextMenu = new EmployeeContextMenu();
		}
		return employeeContextMenu;
	}

	private EnterpriseContextMenu getEnterpriseContextMenu() {
		if (enterpriseContextMenu == null)
			enterpriseContextMenu = new EnterpriseContextMenu();
		return enterpriseContextMenu;
	}

	private WorkplaceContextMenu getWorkplaceContextMenu() {

		if (workplaceContextMenu == null)
			workplaceContextMenu = new WorkplaceContextMenu();

		Employee employee = getClipboardEmployee();
		if (employee != null) {

			if (employeeContextMenu == null)
				employeeContextMenu = getEmployeeContextMenu();

			employeeContextMenu.setCopyEmployee(employee);
			this.pasteItem.setVisible(true);
		}

		return workplaceContextMenu;
	}

	private CCCContextMenu getCCCContextMenu() {

		if (cccContextMenu == null)
			cccContextMenu = new CCCContextMenu();

		return cccContextMenu;
	}

	private Employee getClipboardEmployee() {

		if (storage.getItem(EMPLOYEE) != null) {
			return JSON2Employee(storage.getItem(EMPLOYEE).toString());
		}
		return null;

	}

	public boolean isReftification() {
		return ((CretaResults) resultsPanel.getChild()).getParameter(CretaService.Parameter.INDICADOR_RECTIFICACION)
				.map(s -> "on".equalsIgnoreCase(s)).orElse(false);
	}

	private void i54(String i54) {
		MainCreta.run(Collections.singletonMap(CretaService.Parameter.I54,
				i54), resultsPanel);
	}

	private void reftification(Void v) {
		MainCreta.run(Collections.singletonMap(CretaService.Parameter.INDICADOR_RECTIFICACION,
				isReftification() ? "off" : "on"), resultsPanel);
	}

	private void requestSendRNT(Void v) {
		MainCreta.run(Collections.singletonMap(CretaService.Parameter.SOLICITUD_RECEPCION_RNT, 
				isRequestSendRNT() ? "off" : "on"), resultsPanel);
	}

	public boolean isRequestSendRNT() {
		return 
		(( CretaResults ) resultsPanel.getChild())
		.getParameter(CretaService.Parameter.SOLICITUD_RECEPCION_RNT)
		.map( s -> "on".equalsIgnoreCase(s))
		.orElse(false)
		;
	}
	
	private void acceptPrevBases(Void v) {
		MainCreta.run(Collections.singletonMap(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES, 
				isAcceptPrevBases() ? "off" : "on"), resultsPanel);
	}

	public boolean isAcceptPrevBases() {
		return 
		(( CretaResults ) resultsPanel.getChild())
		.getParameter(CretaService.Parameter.ACEPTAR_BASES_ANTERIORES)
		.map( s -> "on".equalsIgnoreCase(s))
		.orElse(false)
		;
	}
	
	
	private void showResultsPanel(Void v) {
		showResultsPanel();
	}

	private void showProgressPanel(Void v) {
		showProgressPanel();
	}

	private List<CCC> getCCs() {

		List<CCC> ccs = new LinkedList<CCC>();

		for (Activity activity : enterprise.getActivities()) {

			List<CCC> cccs = activity.getCccs();
			if (ccs == null) {
				continue;
			}

			ccs.addAll(cccs);
		}
		return ccs;
	}

	private void checkStatus(EmployeeDraftObject employeeDraftObject) {
	    employeeDraftObject.getIdcDates(
	    		dates -> {
				getEmployeeDraft().enableSistemaRED();
				getEmployeeDraft().initializeIdcDateListBox(dates);
	    		}, 
	    		throwable -> {
				closeFootPanel();
				getEmployeeDraft().disableSistemaRED();
			}
		    );
	}

	private void checkStatus(EnterpriseDraftObject enterpriseDraftObject) {
		enterpriseDraftObject.checkStatus(enterpiseStatus -> {
		
			EnterpriseStatus.ifSistemaREDEnabled(enterpiseStatus, () -> {
				getCCCCretaDetail().setSLDButtonsVisible(true);
				showFootPanel();
			}, () -> {
				getCCCCretaDetail().setSLDButtonsVisible(false);
			});
			
			EnterpriseStatus.ifSistemaREDError(enterpiseStatus, 
					this::showFootPanel, 
					this::closeFootPanel);
			
			SistemaREDResults saltraResults = new SistemaREDResults() {

				@Override
				public void run() {
					enterpriseDraftObject.checkStatus(enterpiseStatus -> {
						removeAll();
						enterpiseStatus.visit(this);
					}, throwable -> {
					});
				}

				@Override
				public void up2Date() {
					selectResultsPanel();
				}

				@Override
				protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
					run();
					getEmployeeTree().employees.refreshWorkplace(
					jsSaltraResults.getWorkplaceId(),
					(treeItem) -> {
						treeItem.setState(true);
						//getEmployeeTree().employees.selectEmployee(jsSaltraResults.getEmployeeId(), true);
					});
				}

				@Override
				protected void saltraCredentialsFound() {
					enterpriseDraftObject.checkStatus(enterpiseStatus -> {
						removeAll();
						enterpiseStatus.visit(this);
						selectResultsPanel();
						EnterpriseStatus.ifSistemaREDEnabled(enterpiseStatus, () -> {
							getCCCCretaDetail().setSLDButtonsVisible(true);
						}, () -> {
							getCCCCretaDetail().setSLDButtonsVisible(false);
						});
						EnterpriseStatus.ifSistemaREDError(enterpiseStatus, 
								EmployeeTree.this::showFootPanel, 
								EmployeeTree.this::closeFootPanel);

					}, throwable -> {
						closeFootPanel();
						getCCCCretaDetail().setSLDButtonsVisible(false);

					});
				}

			};

			enterpiseStatus.visit(saltraResults);
			resultsPanel.setWidget(saltraResults);
			selectResultsPanel();
			
		}, throwable -> {
			closeFootPanel();
			getCCCCretaDetail().setSLDButtonsVisible(false);
		});
	}
	
	// ------------------------------------------------------ Protected methods

	protected static String getDescription(CCC ccc, Enterprise enterprise) {

		for (Activity activity : enterprise.getActivities())
			for (CCC cc : activity.getCccs())
				if (ccc.getCode().equals(cc.getCode()))
					return activity.getDescription() + ", " + ccc.getCode();

		return ccc.getCode();
	}

	protected static JsBasesResult showBases(String json, DetailPanel detailPanel) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel);
		return result;
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel) {
		MergeEditor mergeEditor = new MainCreta.BasesMergeEditor();
		mergeEditor.setOrig(result.getBasesFile());
		mergeEditor.setMode("text/xml");
		mergeEditor.setFoldGutter(true);
		mergeEditor.setLineNumbers(true);
		mergeEditor.setOrig(result.getBasesFile());

		try {
		    	String salaryBasesFile = result.getSalaryBasesFile();
			mergeEditor.setText(salaryBasesFile);
			mergeEditor.setTitle(CretaService.File.BASES.getFilename());
			mergeEditor.setFilename(getFileName(salaryBasesFile) + ".xml");
			detailPanel.setWidget(mergeEditor);
			mergeEditor.autoRefresh();

		} catch (NoSuchElementException e0) {
			try {

			    	String changedBasesFile = result.getChangedBasesFile();
				mergeEditor.setText(changedBasesFile);
				mergeEditor.setTitle(CretaService.File.BASES.getFilename());
				mergeEditor.setFilename(getFileName(changedBasesFile) + ".xml");
				detailPanel.setWidget(mergeEditor);
				mergeEditor.autoRefresh();

			} catch (NoSuchElementException e1) {
				try {
				    	String drafRequestFile = result.getDraftRequestFile();
					mergeEditor.setShowDifferences(false);
					mergeEditor.setText(drafRequestFile);
					mergeEditor.setTitle(CretaService.File.BASES.getFilename());
					mergeEditor.setFilename(getFileName(drafRequestFile) + ".xml");
					detailPanel.setWidget(mergeEditor);
					mergeEditor.autoRefresh();
				} catch (NoSuchElementException e2) {
					FileEditor basesEditor = new MainCreta.BasesFileEditor();
					basesEditor.setMode("text/xml");
					basesEditor.setFoldGutter(true);
					basesEditor.setLineNumbers(true);
					String basesFile = result.getBasesFile();
					basesEditor.setText(basesFile);
					basesEditor.setTitle(CretaService.File.BASES.getFilename());
					basesEditor.setFilename(getFileName(basesFile) + ".xml");
					detailPanel.setWidget(basesEditor);
					basesEditor.autoRefresh();
				}
			}
		}
	}

	protected static void showPDF(String dataURI, DetailPanel detailPanel) {
		DockLayoutPanel dock = new DockLayoutPanel(Unit.PX);
		AonToolbar tb = new AonToolbar("CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.");
		dock.addNorth(tb, AonToolbar.HEIGTH);
		FullViewer viewer = new FullViewer();
		dock.add(viewer);
		detailPanel.setWidget(dock);
		viewer.open(dataURI);
	}

	protected static JsBasesResult showBases(String json, DetailPanel detailPanel, CreateRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static JsBasesResult showBases(String json, DetailPanel detailPanel,
			BasesCCCCretaRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static JsBasesResult showBases(String json, DetailPanel detailPanel,
			AbstractCCCCretaRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel,
			ClickHandler reftificationClickHandler, ClickHandler rntClickHandler, ClickHandler prevBasesClickHandler, ChangeHandler i54ChangeHandler ) {
		MergeEditor mergeEditor = new MainCreta.BasesMergeEditor();
		mergeEditor.setOrig(result.getBasesFile());
		mergeEditor.setMode("text/xml");
		mergeEditor.setFoldGutter(true);
		mergeEditor.setLineNumbers(true);
		mergeEditor.setOrig(result.getBasesFile());

		try {
		    	String salaryBasesFile = result.getSalaryBasesFile();
			mergeEditor.setText(salaryBasesFile);
			mergeEditor.setTitle(CretaService.File.BASES.getFilename());
			mergeEditor.setFilename(getFileName(salaryBasesFile) + ".xml");
			detailPanel.setWidget(mergeEditor);
			mergeEditor.autoRefresh();

		} catch (NoSuchElementException e0) {
			try {
			    	String changesBasesFile = result.getChangedBasesFile();
				mergeEditor.setText(changesBasesFile);
				mergeEditor.setTitle(CretaService.File.BASES.getFilename());
				mergeEditor.setFilename(getFileName(changesBasesFile) + ".xml");
				detailPanel.setWidget(mergeEditor);
				mergeEditor.autoRefresh();

			} catch (NoSuchElementException e1) {
				try {
				    	String draftRequestFile = result.getDraftRequestFile(); 
					mergeEditor.setShowDifferences(false);
					mergeEditor.setText(draftRequestFile);
					mergeEditor.setTitle(CretaService.File.BASES.getFilename());
					mergeEditor.setFilename(getFileName(draftRequestFile) + ".xml");
					detailPanel.setWidget(mergeEditor);
					mergeEditor.autoRefresh();
				} catch (NoSuchElementException e2) {
					FileEditor basesEditor = new MainCreta.BasesFileEditor();
					basesEditor.setMode("text/xml");
					basesEditor.setFoldGutter(true);
					basesEditor.setLineNumbers(true);
					String basesFile = result.getBasesFile();
					basesEditor.setText(basesFile);
					basesEditor.setTitle(CretaService.File.BASES.getFilename());
					basesEditor.setFilename(getFileName(basesFile) + ".xml");
					detailPanel.setWidget(basesEditor);
					basesEditor.autoRefresh();

					CheckBox reftification = new CheckBox("Reftificativa");
					reftification.setValue(result.isRectifying());
					reftification.setStyleName("aon-finding-toolbar-item");
					reftification.addClickHandler(reftificationClickHandler);
					basesEditor.add(reftification);

					CheckBox solicitudRecepcionRNT = new CheckBox("Solicitud Recepci\u00f3n RNT");
					solicitudRecepcionRNT.setValue(result.isRequestSendRNT());
					solicitudRecepcionRNT.setStyleName("aon-finding-toolbar-item");
					solicitudRecepcionRNT.addClickHandler(rntClickHandler);
					basesEditor.add(solicitudRecepcionRNT);

					CheckBox aceptarBasesAnteriores = new CheckBox("Aceptar Bases Anteriores");
					aceptarBasesAnteriores.setValue(result.isAcceptPrevBases());
					aceptarBasesAnteriores.setStyleName("aon-finding-toolbar-item");
					aceptarBasesAnteriores.addClickHandler(prevBasesClickHandler);
					basesEditor.add(aceptarBasesAnteriores);

					if ( "L03".equalsIgnoreCase(result.getType()) ) {
						Label i54Label = new Label("Causa");
						i54Label.setStyleName("aon-finding-toolbar-item");
						basesEditor.add(i54Label );
						ListBox i54ListBox = new ListBox();
						i54ListBox.addItem("1- Atrasos de convenio", "1");
						i54ListBox.addItem("2- Normativa (disposici\u00f3n legal)", "2");
						i54ListBox.addItem("3- Acta de conciliaci\u00f3n", "3");
						i54ListBox.addItem("4- Sentencia judicial", "4");
						i54ListBox.addItem("5- Cualquier otro t\u00edtulo leg\u00edtimo", "5");
						i54ListBox.addChangeHandler( i54ChangeHandler );
						basesEditor.add(i54ListBox);
					}
				}
			}
		}
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel,
			CreateRequestCommand cretaCommand) {
		showBases(
		result, 
		detailPanel, 
		e -> cretaCommand.reexecute(d -> {
			d.reftificationMarkCheckBox.setValue(!result.isRectifying());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.solicitudRecepcionRNTCheckBox.setValue(!result.isRequestSendRNT());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.aceptarBasesAnterioresCheckBox.setValue(!result.isAcceptPrevBases());
		}),
		e -> cretaCommand.reexecute(d -> {
		    	d.i54ListBox.setSelectedIndex(Math.max(AonNumberUtils.toint(result.getI54())-1,0));
		})		
		);
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel,
			BasesCCCCretaRequestCommand cretaCommand) {
		showBases(
		result, 
		detailPanel, 
		e -> cretaCommand.reexecute(d -> {
			d.reftificationMarkCheckBox.setValue(!result.isRectifying());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.solicitudRecepcionRNTCheckBox.setValue(!result.isRequestSendRNT());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.aceptarBasesAnterioresCheckBox.setValue(!result.isAcceptPrevBases());
		}),
		e -> cretaCommand.reexecute(d -> {
		    	d.i54ListBox.setSelectedIndex(Math.max(AonNumberUtils.toint(result.getI54())-1,0));
		})		
		);
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel,
			AbstractCCCCretaRequestCommand cretaCommand) {
		showBases(
		result, 
		detailPanel, 
		e -> cretaCommand.reexecute(d -> {
			d.reftificationMarkCheckBox.setValue(!result.isRectifying());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.solicitudRecepcionRNTCheckBox.setValue(!result.isRequestSendRNT());
		}),
		e -> cretaCommand.reexecute(d -> {
			d.aceptarBasesAnterioresCheckBox.setValue(!result.isAcceptPrevBases());
		}),
		e -> cretaCommand.reexecute(d -> {
		    	d.i54ListBox.setSelectedIndex(Math.max(AonNumberUtils.toint(result.getI54())-1,0));
		})		
		);

	}

	protected static void showResults(JsBasesResult result, Set<JsFile> jsFiles, ResultsPanel resultsPanel,
			Consumer<JsBasesResult> onBases, Consumer<ResultsPanel> showResultsPanel) {

		CretaResults cretaResults = new CretaResults() {
			@Override
			protected void onBases(JsBasesResult result) {
				onBases.accept(result);
			}
		};
		cretaResults.addErrors(result.getErrors());
		cretaResults.addWarnings(result.getWarnings());
		cretaResults.addUnknown(result.getUnknown());
		cretaResults.setJsFiles(jsFiles);
		resultsPanel.setWidget(cretaResults);

		if (result.getErrors().length > 0 || result.getWarnings().length > 0)
			showResultsPanel.accept(resultsPanel);

	}

	protected static void showResults(JsBasesResult result, ResultsPanel resultsPanel, Consumer<JsBasesResult> onBases,
			Consumer<ResultsPanel> showResultsPanel) {
		showResults(result, Collections.emptySet(), resultsPanel, onBases, showResultsPanel);
	}

	// --------------------------------------------------------- Private methods

	protected static void showSaltraOptions(EmployeeStatus employeeStatus) {
		// NOOP
	}

	protected static void showEmployeeEvents(String variable, int [] years) {
		showEmployeeEvents(new String [] {variable}, years);
	}

	protected static void showEmployeeEvents(String []variables, int [] years) {
		EmployeeEventsDraftObject employeeEventsDraftObject = 
		new EmployeeEventsDraftObject(getEmployeeTree().getSalaryDraft().getSalaryDraftObject().getEmployeeId());
		getEmployeeTree().employeeDetail.setWidget(getEmployeeTree().getEmployeePanel());
		getEmployeeTree().getEmployeePanel().selectWidget(getEmployeeTree().getEmployeeEventsDraft());
		getEmployeeTree().getEmployeeEventsDraft().setEmployeeEventsDraftObject(employeeEventsDraftObject.getEmployeeEventsDraftObject(variables), years);

		getEmployeeTree().employees.getEmployeeSalaryDraft(employeeEventsDraftObject, o -> getEmployeeTree().getEmployeePanel().setSalaryDraft(o));
	}

	protected static void showEmployeeCalendar() {
		EmployeeTree employeeTree = getEmployeeTree();
		EmployeeCalendarDraftObject calendar = new EmployeeCalendarDraftObject(getEmployeeTree().getSalaryDraft().getSalaryDraftObject().getEmployeeId());
		
		employeeTree.employeeDetail.setWidget(employeeTree.getEmployeePanel());
		employeeTree.getEmployeePanel().selectWidget(employeeTree.getEmployeeCalendarDraftNew());
		employeeTree.getEmployeeCalendarDraftNew().setEmployeeCalendarDraftObject(calendar);
	}

	protected static void showEmployeeCalendar(EmployeeCalendarDraftObject employeeCalendarDraftobject) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employeeDetail.setWidget(employeeTree.getEmployeeCalendarDraftNew());
		employeeTree.getEmployeeCalendarDraftNew().setEmployeeCalendarDraftObject(employeeCalendarDraftobject);
	}

	protected static void showWorkplaceCalendar(CalendarDraftObjectData calendarDraftObjectData) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employeeDetail.setWidget(employeeTree.getCalendarDraft());
		employeeTree.getCalendarDraft().setCalendarDraftObject(null, calendarDraftObjectData);
	}

	protected static void showNewWorkplace() {
		WorkplaceDialog workplaceDialog = new WorkplaceDialog();
		WorkplaceDialogObject workplaceDialogObject = new WorkplaceDialogObject(getEmployeeTree().enterprise);
		workplaceDialog.setWorkplaceDialogObject(workplaceDialogObject);
	}

	protected static void showNewActivity() {
		ActivityDialog activityDialog = new ActivityDialog();
		ActivityDialogObject activityDialogObject = new ActivityDialogObject(getEmployeeTree().enterprise);
		activityDialog.setActivityDialogObject(activityDialogObject);
	}

	protected static void showNewContract() {
		EmployeeDialog employeeDialog = new EmployeeDialog(true) {
			@Override
			protected void onAccept(Integer contractId) {}
		};
		EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(getEmployeeTree().workplace);
		employeeDialog.setEmployeeDialogObject(employeeDialogObject);
	}

	protected static void invokeRefreshWorkplace() {
		if(null != getEmployeeTree().workplace)
			getEmployeeTree().employees.refresh(getEmployeeTree().workplace);
	}

	protected static void invokeRefreshEnterprise() {
		getEmployeeTree().employees.refresh(getEmployeeTree().enterprise);
	}

	protected static void showSalaryDraft(int employeeId, int workplaceId, Date startDate, Date endDate) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employees.selectSalaryDraft(employeeId, workplaceId, startDate, endDate, true);
	}

	private static EmployeeTree getEmployeeTree() {
		return singlenton;
	}

	private static <T extends HasId<?>> void share(com.esferalia.aon.gwt.payroll.shared.Salary salary, String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.SALARY + "=" + salary.getId()).append("&type=" + type);

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);
	}

	private static <T extends HasId<?>> void shareSalary(SalaryInfo salary, String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.SALARY + "=" + salary.getId()).append("&type=" + type);

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);
	}

	private static <T extends HasId<?>> void share(com.esferalia.aon.gwt.payroll.shared.Cost cost, String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer.append("&" + ShareService.MONTH + "=" + cost.getMonth());
		requestDataBuffer.append("&" + ShareService.YEAR + "=" + cost.getYear());
		int workplaceId = cost.getWorkplaceId();
		if (workplaceId != 0)
			requestDataBuffer.append("&" + ShareService.WORKPLACE + "=" + workplaceId);
		else
			requestDataBuffer.append("&" + ShareService.ENTERPRISE + "=" + cost.getEnterpriseId());
		requestDataBuffer.append("&type=" + type);
		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);
	}

	private static void share(String requestData, final AsyncCallback<JsShareResult> callback) {
		// Send request to server and catch any errors.

		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SHARE_URL);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

			private int loaded = 0;

			@Override
			public void onReadyStateChange(XMLHttpRequest xhr) {
				int state = xhr.getReadyState();

				if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {

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

	private static void refreshEmployee() {
		singlenton.employees.refresh(singlenton.employee);
	}

	private static void refreshWorkplace() {
		singlenton.employees.refresh(singlenton.workplace);
	}

	private static void refreshActivity() {
		singlenton.employees.refresh(singlenton.activity);
	}

	private static void refreshEnterprise() {
		singlenton.employees.refresh(singlenton.enterprise);
	}

	private static native void export2JS() /*-{
		$wnd.viewResults = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::viewResults());
		$wnd.employeeCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::employeeCalc());
		$wnd.workplaceCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::workplaceCalc());
		$wnd.enterpriseCalc = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::enterpriseCalc());
		$wnd.refreshEmployee = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::refreshEmployee());
		$wnd.refreshActivity = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::refreshActivity());
		$wnd.refreshWorkplace = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::refreshWorkplace());
		$wnd.refreshEnterprise = $entry(@com.esferalia.aon.gwt.payroll.client.EmployeeTree::refreshEnterprise());
	}-*/;

	private static String employee2Json(Employee employee) {

		JSONObject json = new JSONObject();
		try {

			json.put("id", new JSONNumber(employee.getId()));
			json.put("person", new JSONNumber(employee.getPerson()));
			json.put("name", new JSONString(employee.getName()));
			json.put("first", new JSONString(employee.getFirstSurname()));

			json.put("startDate", new JSONString(employee.getStartDate().toString()));

			if (employee.getSecondSurName() != null)
				json.put("second", new JSONString(employee.getSecondSurName()));

			if (employee.getEndDate() != null)
				json.put("endDate", new JSONString(employee.getEndDate().toString()));

			// Si Document es nulo se va a la BD a por el campo
			if (employee.getDocument() != null)
				json.put("document", new JSONString(employee.getDocument()));

			if (employee.getSocialSecurity() != null)
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
			if (json.get("second") != null)
				secondSurname = json.get("second").toString().replaceAll("\"", "");

			String start = json.get("startDate").toString().replaceAll("\"", "");
			Date startDate = getDate(start);

			Date endDate = null;
			if (json.get("endDate") != null) {
				String end = json.get("endDate").toString().replaceAll("\"", "");
				endDate = getDate(end);
			}

			String document = "";
			if (json.get("document") != null)
				document = json.get("document").toString().replaceAll("\"", "");

			String ss = "";
			if (json.get("ss") != null)
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

	private static void logEvent(String type) {
		StatsEventLogger.logEvent("aon", "EmployeeTree", type);
	}

	private static List<CCC> getCCs(Workplace workplace) {

		Activity activity = workplace.getActivity();
		if (activity == null)
			return Collections.emptyList();
		List<CCC> ccs = activity.getCccs();
		if (ccs == null)
			return Collections.emptyList();
		return ccs;
	}

	private static List<CCC> getCCs(Enterprise enterprise) {

		List<CCC> ccs = new LinkedList<CCC>();

		for (Workplace workplace : enterprise.getWorkplaces()) {

			Activity activity = workplace.getActivity();
			if (activity == null) {
				continue;
			}

			List<CCC> workplaceCcs = activity.getCccs();
			if (workplaceCcs == null) {
				continue;
			}

			ccs.addAll(workplaceCcs);
		}
		return ccs;
	}

	private static boolean isSaltraEnabled(EmployeeStatus employeeStatus) {
		try {
			employeeStatus.visit(new Visitor() {

				@Override
				public void up2Date() {
				}
				
				
				@Override
				public void forbidden() {
					throw new RuntimeException();
				}

				@Override
				public void saltraCredentialsNotFound() {
					throw new RuntimeException();
				}

				@Override
				public void occupationNotFound() {
				}
				
				@Override
				public void notAuthorizedCCC() {
				}
				
				@Override
				public void noQueryData(String message) {
				}
				
				@Override
				public void unknownError(String message) {
				}

				@Override
				public void mismatchedStartDate(MismatchedStartDate status) {
				}

				@Override
				public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				}

				@Override
				public void mismatchedOccupation(MismatchedOccupation status) {
				}

				@Override
				public void mismatchedContractType(MismatchedContractType status) {
				}

				@Override
				public void mismatchedCCC(MismatchedCCC status) {
				}

				@Override
				public void invalidData() {
				}

				@Override
				public void endDateNotFound() {
				}

				@Override
				public void employeeNotFound() {
				}

				@Override
				public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				}
			});
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static String getDescription(CCC ccc, Workplace workplace) {
		Activity activity = workplace.getActivity();
		return activity.getDescription() + "," + ccc.getCode();
	}

	private static native String cleanDiacritics(String str) /*-{
		return str.normalize('NFD').replace(/[\u0300-\u036f]/g, "");
	}-*/;

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

	@Override
	public void fireSettleMessage(String message) {
		Tree messageTree = new Tree();
		messageTree.getElement().getStyle().setPaddingTop(10, Unit.PX);
		TreeItem parent = messageTree.addTextItem("Certifi@2");
		parent.addTextItem(message);
		parent.setState(true, false);
		resultsPanel.setWidget(messageTree);
		showResultsPanel();
	}

	private static String normalize(String str){
		return str
		.toLowerCase()
		.replace('\u00E1', 'a')
		.replace('\u00E9', 'e')
		.replace('\u00ED', 'i')
		.replace('\u00F3', 'o')
		.replace('\u00FA', 'u')
		.replace('\u00F1', 'n')
		.replace('\u00FC', 'u')
		.replaceAll("\\s+", "_")
		;
		
	}
	
	private static native void log (String message ) /*-{
		console.log(message);
	}-*/;

	private static String getFileName(String xml) {
	    String tipo = AonStringUtils.substringBetween(xml, "<Tipo>", "</Tipo>");
            String provincia = AonStringUtils.substringBetween(xml, "<Provincia>", "</Provincia>");
            String numero = AonStringUtils.substringBetween(xml, "<Numero>", "</Numero>");
            String periodoDesde = AonStringUtils.substringBetween(xml, "<PeriodoDesde>","</PeriodoDesde>");
            String anhoDesde  = AonStringUtils.substringBetween(periodoDesde, "<Anho>","</Anho>"); 
            String mesDesde  = AonStringUtils.substringBetween(periodoDesde, "<Mes>","</Mes>"); 
            String now = DateTimeFormat.getFormat("MMddHHmm").format(new Date());
	    return "SLD-Bases " + tipo + " " + provincia + numero + " " + anhoDesde+"-"+mesDesde + " " + now;
	}
	
	// MessagePanel
	
	private HTMLPanel getMessagePanel() {
		return messagePanel;
	}
	
	private void hideMessagePanel() {
		AonMessagePanel.hideMessage(getMessagePanel());
	}
	
	private void showSuccessMessage(Map<String, String> messages) {
		AonMessagePanel.showSuccess(getMessagePanel(), messages);
	}
	
	private void showWarningMessage(Map<String, String> messages) {
		AonMessagePanel.showWarning(getMessagePanel(), messages);
	}
	
	private void showErrorMessage(Map<String, String> messages) {
		AonMessagePanel.showError(getMessagePanel(), messages);
	}
	
	private void showInfoMessage(Map<String, String> messages) {
		AonMessagePanel.showInfo(getMessagePanel(), messages);
	}
	
	private void showLoadingMessage(String message) {
		AonMessagePanel.showLoading(getMessagePanel(), message);
	}

	private void getEnterpriseContext( Consumer<EnterpriseContext> callback) {
	    if (EmployeeTree.this.enterpriseContext != null ) {
		    callback.accept(EmployeeTree.this.enterpriseContext);
	    }

	    DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	    
	    enterprisesService.getEnterpriseContext(new AsyncCallback<EnterpriseContext>() {

		@Override
		public void onFailure(Throwable caught) {
		    // Window.alert("NO");
		}

		@Override
		public void onSuccess(EnterpriseContext enterpriseContext) {
		    EmployeeTree.this.enterpriseContext = enterpriseContext;
		    callback.accept(EmployeeTree.this.enterpriseContext);
		}
	    });
	}

	@Override
	public void onCollapseEmployees() {
//		dockLayoutPanel.setWidgetSize(employees, 36);
//		dockLayoutPanel.animate(500);
		employees.setWidth("36px");
		employees.createStaticEmployees();
		dockLayoutPanel.getElement().getStyle().setMarginLeft(0, Unit.PX);
	}

	@Override
	public void onShowEmployees(boolean isCollapsed) {
		if(isCollapsed) dockLayoutPanel.getElement().getStyle().setMarginLeft(36, Unit.PX);
		else dockLayoutPanel.getElement().getStyle().setMarginLeft(0, Unit.PX);
			
		employees.showEmployees();
		employees.setWidth("340px");
//		dockLayoutPanel.setWidgetSize(employees, 275);
//		dockLayoutPanel.animate(500);
	}
	

}
