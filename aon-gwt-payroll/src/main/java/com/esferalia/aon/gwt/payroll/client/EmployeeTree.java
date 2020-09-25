package com.esferalia.aon.gwt.payroll.client;

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
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.metrics.StatsEventLogger;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.IndeterminateTask;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.client.MainCreta.AbstractBaseCretaDetail;
import com.esferalia.aon.gwt.payroll.client.MainCreta.AbstractCCCCretaRequestCommand;
import com.esferalia.aon.gwt.payroll.client.MainCreta.SyncCallback;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptEvent;
import com.esferalia.aon.gwt.payroll.client.SelectDialog.AcceptHandler;
import com.esferalia.aon.gwt.payroll.shared.Activity;
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
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Province;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
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

import net.aonsolutions.gwt.pdfjs.client.Viewer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */

public class EmployeeTree implements EntryPoint, Employees.Listener,
		MetaData.Listener, Cost.Listener, Salary.Listener, EmployeeSalary.Listener, WorkplaceSalary.Listener, EnterpriseSalary.Listener {
	
	

	public static String SHARE_URL = URL.encode(GWT.getModuleBaseURL() + "share");
	static class EmployeeCalcDialog extends CalcDialog<Employee> {
		
		DomainEmployeesServiceAsync employeesService ;
		
		public EmployeeCalcDialog() {


			employeesService = DomainEmployeesServiceAsync.newInstance();

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
			Column<Workplace, String> descriptionColumn = new Column<Workplace, String>(
					new TextCell()) {
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
			for ( Workplace workplace: workplaces )
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
		private DomainEmployeesServiceAsync employeesService;
		private DomainEnterprisesServiceAsync enterprisesService;
		
		@Override
		public void execute() {
			// Create a remote service proxy to talk to the server-side Employees
			// service.
			employeesService = DomainEmployeesServiceAsync.newInstance();
			enterprisesService = DomainEnterprisesServiceAsync.newInstance();
			
			EmployeeDialog employeeDialog = new EmployeeDialog() {
				@Override
				protected void onAccept() {}
			};
			EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(workplace, employeesService, enterprisesService);
			employeeDialog.setEmployeeDialogObject(employeeDialogObject);
			workplaceContextMenu.hide();
			employeeDialog.center();
			employeeDialog.show();
//			EmployeeTree.this.employees.refresh(workplace);
		}
	}
	
	class NewActivityCommand implements ScheduledCommand {
		private DomainEnterprisesServiceAsync enterprisesService;
		
		@Override
		public void execute() {
			// Create a remote service proxy to talk to the server-side Employees
			// service.
			enterprisesService = DomainEnterprisesServiceAsync.newInstance();
			
			ActivityDialog activityDialog = new ActivityDialog();
			ActivityDialogObject activityDialogObject = new ActivityDialogObject(enterprise, enterprisesService);
			activityDialog.setActivityDialogObject(activityDialogObject);
			enterpriseContextMenu.hide();
			activityDialog.center();
			activityDialog.show();
		}
	}
	
	class NewWorkplaceCommand implements ScheduledCommand {
		private DomainEnterprisesServiceAsync enterprisesService;
		
		@Override
		public void execute() {
			// Create a remote service proxy to talk to the server-side Employees
			// service.
			enterprisesService = DomainEnterprisesServiceAsync.newInstance();
			
			WorkplaceDialog workplaceDialog = new WorkplaceDialog();
			WorkplaceDialogObject workplaceDialogObject = new WorkplaceDialogObject(enterprise, enterprisesService);
			workplaceDialog.setWorkplaceDialogObject(workplaceDialogObject);
			enterpriseContextMenu.hide();
			workplaceDialog.center();
			workplaceDialog.show();
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

	class PasteEmployeeCommand
			implements ScheduledCommand, EmployeePopupCopy.Listener {

		@Override
		public void execute() {
			paste = new EmployeePopupCopy();
			paste.addListener(this);
			Employee aux = singlenton.getEmployeeContextMenu().getEmployeeCopy();
			
			if (singlenton.avaiableEmployees
					.containsKey(aux.getDocument()) == false)
				existPerson(aux);
			else
				showPopUpPanel();
		}

		private void showPopUpPanel() {
			setEmployeePaste(singlenton.getEmployeeContextMenu().getEmployeeCopy());
			
			setMapAvaiableEmployees(singlenton.avaiableEmployees);
			//TODO: FIX FIRST THIS, DONT KNOW HOW REALLY WORKS
			paste.showPopUpPanel();
		}

		@Override
		public void onAcceptClick(Employee pasteEmployee, boolean value) {
			int workplaceId = workplace.getId();
			int contractId = singlenton.getEmployeeContextMenu()
					.getEmployeeCopy().getId();
			String document = pasteEmployee.getDocument();
			Date startDate = pasteEmployee.getStartDate();
			Date endDate = (pasteEmployee.getEndDate() != null)
					? pasteEmployee.getEndDate() : null;

			pasteContract(workplaceId, contractId, document, startDate, endDate,
					value, null);
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

			if (Window.confirm(employee.getFullname()
					+ " no se encuentra en el dominio. \u00BFDesea insertar "
					+ "el registro\u003F")) {

				singlenton.employees.getEmployeesService()
						.insertPerson(employee, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {

							}

							@Override
							public void onSuccess(Void result) {
								singlenton.avaiableEmployees.put(
										employee.getDocument(),
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

			if(employee.hasSalries()) {
				Window.alert("No se puede borrar un empleado con nominas.");
				callback.onSuccess(null);
			}else {
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

	}

	class CalcEnterpriseCommand implements ScheduledCommand, AcceptHandler,
			AsyncCallback<JsSalaryResult>, SelectionHandler<JsSalaryResult> {

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

			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, WORKPLACES, workplaces,
					extra, optionsBits, this);

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
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
		}

	}
	
	interface EmployeeCommand extends ScheduledCommand {
		void setEmployee(Employee employee);
	}
	
	
	class RefreshEmployeeCommand implements EmployeeCommand{
		
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
	

	class CalcEmployeeCommand
			implements EmployeeCommand, AcceptHandler, CalculateService,
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

			Date startDate = calcDialog.getStartDate(); 
			Date endDate = calcDialog.getEndDate();
			Date issueDate = calcDialog.getIssueDate();

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
			
			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, EMPLOYEES, employees,
					extra, optionsBits, this);
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
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
		}
	}

	public static abstract class CretaCommand
			implements ScheduledCommand, CretaService {

		protected File file;
		protected FileEditor fileEditor;
		protected DetailPanel detailPanel;

		public CretaCommand(File file, DetailPanel detailPanel) {
			this(file, detailPanel, new FileEditor());
		}

		public CretaCommand(File file, DetailPanel detailPanel,
				FileEditor fileEditor) {
			this.file = file;
			this.fileEditor = fileEditor;
			this.detailPanel = detailPanel;
		}

		protected void send(long autorizado, 
				final int fromMonth, final int fromYear,
				final int toMonth, final int toYear,
				final int ctrlMonth, final int ctrlYear,
				final String tipo, 
				final Collection<CCC> cccs,
				final boolean basesMesAnterior,
				final boolean calcsDetailed,
				final String i54, 
				final boolean reftificativa) {
			StringBuffer requestDataBuffer = new StringBuffer();

			requestDataBuffer.append("&" + Parameter.TIPO + "=" + tipo);
			requestDataBuffer.append("&" + Parameter.DESDE_MES + "=" + fromMonth);
			requestDataBuffer.append("&" + Parameter.DESDE_ANHO + "=" + fromYear);
			requestDataBuffer.append("&" + Parameter.HASTA_MES + "=" + toMonth);
			requestDataBuffer.append("&" + Parameter.HASTA_ANHO + "=" + toYear);
			requestDataBuffer.append("&" + Parameter.CTRL_MES + "=" + ctrlMonth);
			requestDataBuffer.append("&" + Parameter.CTRL_ANHO + "=" + ctrlYear);
			requestDataBuffer
					.append("&" + Parameter.AUTORIZADO + "=" + autorizado);

			for (CCC ccc : cccs)
				requestDataBuffer
						.append("&" + Parameter.CCC + "=" + ccc.getRegime() + ccc.getCode());

			if (basesMesAnterior)
				requestDataBuffer.append(
						"&" + Parameter.ACEPTAR_BASES_ANTERIORES + "=on");

			if (calcsDetailed)
				requestDataBuffer.append(
						"&" + Parameter.CALCULOS_DESGLOSADOS + "=on");

			if (reftificativa)
				requestDataBuffer.append(
						"&" + Parameter.INDICADOR_RECTIFICACION + "=on");

			requestDataBuffer.append("&" + Parameter.I54 + "=" + i54);

			for (CCC ccc : cccs)
				for (Employee employee : ccc.getEmployees())
					requestDataBuffer
						.append("&" + Parameter.NAFS + "=" + employee.getSocialSecurity());
			
			// Send request to server and catch any errors.

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", CretaService.CRETA_URL + "/" + file.name());
			xhr.setRequestHeader("Content-type",
					"application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

				private int loaded = 0;

				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();

					if (state != XMLHttpRequest.DONE)
						return;

					onRequestDone(xhr.getResponseText(), fromMonth, fromYear, toMonth, toYear, tipo,
							cccs);
				}

			});

			xhr.send(requestDataBuffer.toString());

		}

		protected void onRequestDone(String response, int fromMonth, int fromYear, int toMonth, int toYear,
				String tipo, Collection<CCC> cccs) {
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


		private String getFileName(int month, int year, String tipo,
				Collection<CCC> cccs) {
			
			
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

	public static abstract class CreateResponseCommand extends CretaCommand
			implements CretaResponseDialog.Handler {

		ResultsPanel resultsPanel;
		CretaResponseDialog dialog;

		public CreateResponseCommand(File outFile, File inFile,
				DetailPanel detailPanel, ResultsPanel resultsPanel) {
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
			
			showResults(result, 
					dialog.getSelectedData(), 
					resultsPanel, 
					CreateResponseCommand.this::onBases, 
					r -> showResultsPanel() );

//			CretaResults cretaResults = new CretaResults() {
//				@Override
//				protected void onBases(JsBasesResult result) {
//					CreateResponseCommand.this.onBases(result);
//				}
//			};
//			cretaResults.addErrors(result.getErrors());
//			cretaResults.addWarnings(result.getWarnings());
//			cretaResults.addUnknown(result.getUnknown());
//			cretaResults.setJsFiles(dialog.getSelectedData());
//			resultsPanel.setWidget(cretaResults);
//
//			if (result.getErrors().length > 0
//					|| result.getWarnings().length > 0)
//				showResultsPanel();

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

	public static abstract class WorkplaceCreateResponseCommand
			extends CreateResponseCommand {

		public WorkplaceCreateResponseCommand(File outFile, File inFile,
				DetailPanel detailPanel, ResultsPanel resultsPanel) {
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

	public static abstract class CreateRequestCommand extends CretaCommand
			implements CretaRequestDialog.Callback<CCC> {

		CretaRequestDialog<CCC> dialog;

		public CreateRequestCommand(File file, DetailPanel detailPanel) {
			super(file, detailPanel);
			dialog = new CretaRequestDialog.CretaCCCRequestDialog(this) {
				@Override
				public String getDescription(CCC ccc) {
					return CreateRequestCommand.this.getDescription(ccc);
				}
			};
			setUpDialog(file, dialog);
		}

		public CreateRequestCommand(File file, DetailPanel detailPanel,
				FileEditor fileEditor) {
			super(file, detailPanel, fileEditor);
			dialog = new CretaRequestDialog.CretaCCCRequestDialog(this) {
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
			Date ctrlMonth = dialog.getToMonth();
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

			send(autorizado, desdeMes, desdeAnyo, hastaMes, hastaAnyo, ctrlMes, ctrlAnyo, tipo, checkCCCs(ccs), basesMesAnterior, calcsDetailed, i54, reftificationMark);

			return true;
		}

		// --------------------------------------------------------------------
		
		public void reexecute(Consumer<CretaRequestDialog<CCC>> consumer) {
			consumer.accept(dialog);
			onAccept(dialog);
		}

		// --------------------------------------------------------------------
		protected void setSelected(Collection<CCC> cccs ) {
			dialog.setSelectedData(cccs);
			
		}
		// --------------------------------------------------------------------

		protected abstract String getDescription(CCC ccc);
		
		protected abstract void onCCCError(CCC ccc, String message);

		// --------------------------------------------------------------------
		
		private Set<CCC> checkCCCs(Set<CCC> cccs ) {
			return 
			cccs.stream()
			.filter(ccc -> {
				String code = ccc.getCode();
				if ( AonStringUtils.isBlank(code)) {
					onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n vac\u00EDo");
					return false;
				}
				if ( AonStringUtils.trim(code).length() < 11 ) {
					onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '"+ code +"'. Recuerde dos d\u00EDgitos para la provincia y nueve d\u00EDgitos para el n\u00FAmero de cotizaci\u00F3n");
					return false;
				}
				try {
					int provincia  = Integer.parseInt(AonStringUtils.substring(code, 0, 2));
					if ( provincia < 1  || provincia > 52 ) {
						onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '"+ code +"'. C\u00F3digo de provincia '"+ AonStringUtils.substring(code, 0, 2) +"' desconocido. Recuerde debe estar entre ( 01 y 52 )");
						return false;
					}
					
				} catch ( Exception e ) {
					onCCCError(ccc, "C\u00F3digo Cuenta de Cotizaci\u00F3n inv\u00E1lido '"+ code +"'. C\u00F3digo de provincia '"+ AonStringUtils.substring(code, 0, 2) +"' desconocido. Recuerde debe estar entre ( 01 y 52 )");	
					return false;
				} 
				
				
				
				String numero = AonStringUtils.substring(code, 2);
				String control = AonStringUtils.substring(code, -2);
				
				
				
				
				
				return true;
			}).collect(Collectors.toSet());
		}

		private static void setUpDialog(File file,
				final CretaRequestDialog dialog) {
			file.accept(new File.Visitor<Void, Void, RuntimeException>() {

				@Override
				public void visitBases(Void t, Void l) throws RuntimeException {
					// TODO: add TypeChangeHadler to CretaRequestDialog? 
					dialog.typeListBox.addChangeHandler(event -> dialog.setVisibleI54("L03".equals(dialog.getType())));
					dialog.setVisibleReftificationMark(true);
				}

				@Override
				public void visitRespuesta(Void t, Void l)
						throws RuntimeException {
				}

				@Override
				public void visitTrabajadoresTramos(Void t, Void l)
						throws RuntimeException {
				}

				@Override
				public void visitSolicitudBorrador(Void t, Void l)
						throws RuntimeException {
					dialog.setVisiblePreviousBases(true);

				}

				@Override
				public void visitSolicitudCalculos(Void t, Void l)
						throws RuntimeException {
					dialog.setVisibleCalcsDetailed(true);
				}

				@Override
				public void visitSolicitudConfirmacion(Void t, Void l)
						throws RuntimeException {
				}

				@Override
				public void visitSolicitudTrabajadoresTramos(Void t, Void l)
						throws RuntimeException {
				}
				
				@Override
				public void visitComunicacionDatosBancarios(Void t, Void l)
						throws RuntimeException {
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

		public WorkplaceCreateRequestCommand(File file, DetailPanel detailPanel,
				FileEditor fileEditor) {
			super(file, detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------
		@Override
		protected void onCCCError(CCC ccc, String message) {
			if ( cretaResults == null ) {
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

	class CCCCretaRequestCommand extends CreateRequestCommand implements CCCCommand{
		
		private CretaResults cretaResults;

		public CCCCretaRequestCommand(File file) {
			super(file, employeeDetail);

			dialog.selectLabel.setVisible(false);
			dialog.selectDataGrid.setVisible(false);
		}

		@Override
		protected void onCCCError(CCC ccc, String message) {
			if ( cretaResults == null ) {
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
	class WorkplaceDBACommand extends DBACommand implements WorkplaceCommand  {
		
		protected Workplace workplace;
		
		public WorkplaceDBACommand(DetailPanel detailPanel,
				FileEditor fileEditor) {
			super(detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------

		@Override
		public void setWorkplace(Workplace workplace) {
			this.workplace = workplace;
			setData(getCCs(workplace));
			setBankAccounts(enterprise.getBankAccounts());

			if ( AonStringUtils.isBlank(getHolder())) 
				setHolder(enterprise.getName());
			
		}

		
		// --------------------------------------------------------------------
		
		
		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, workplace);
		}

	}
	
	private class CCCDBACommand extends EmployeeTree.DBACommand implements CCCCommand{

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

	public interface EnterpriseCommand extends ScheduledCommand{
		void setEnterprise(Enterprise enterprise);
	}

	public class EnterpriseCretaRequestCommand
			extends CreateRequestCommand implements EnterpriseCommand{

		protected Enterprise enterprise;
		private CretaResults cretaResults;

		public EnterpriseCretaRequestCommand(File file,
				DetailPanel detailPanel) {
			super(file, detailPanel);
		}

		public EnterpriseCretaRequestCommand(File file, DetailPanel detailPanel,
				FileEditor fileEditor) {
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
			if ( cretaResults == null ) {
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


		public DBACommand(DetailPanel detailPanel,
				FileEditor fileEditor) {
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

	public static class EnterpriseDBACommand extends DBACommand implements EnterpriseCommand  {
		
		protected Enterprise enterpr1se; 
		
		public EnterpriseDBACommand(DetailPanel detailPanel) {
			super(detailPanel, new FileEditor());
		}


		public EnterpriseDBACommand(DetailPanel detailPanel,
				FileEditor fileEditor) {
			super(detailPanel, fileEditor);
		}

		// --------------------------------------------------------------------

		@Override
		public void setEnterprise(Enterprise enterprise) {
			this.enterpr1se = enterprise;
			setData(getCCs(enterprise));
			setBankAccounts(enterprise.getBankAccounts());
			
			if ( AonStringUtils.isBlank(getHolder())) 
				setHolder(enterprise.getName());
		}

		
		// --------------------------------------------------------------------
		
		@Override
		protected String getDescription(CCC ccc) {
			return EmployeeTree.getDescription(ccc, this.enterpr1se);
		}

	}

	class RefreshWorkplaceCommand implements WorkplaceCommand{
		
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

	class CalcWorkplaceCommand
			implements ScheduledCommand, AcceptHandler, CalculateService,
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

			MainCalculator.calculate(salaryType, startDate, endDate, issueDate, EMPLOYEES, employees,
					extra, optionsBits, this);
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
			EmployeeTree.showSalaryDraft(salaryResult.getEmployeeId(),
					salaryResult.getWorkplaceId(), salaryResult.getStartDate(),
					salaryResult.getEndDate());
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
		protected void onMonthChanged(Date month) {
			
			DomainEnterprisesServiceAsync.newInstance().getCCCEmployees(month, 
					Collections.singletonList(getCCC().getId()), 
					new AsyncCallback<List<Employee>>() {
						
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(List<Employee> result) {
							setData(result);
						}
					} );
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
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios", 
					cccCommands[4] = new CCCDBACommand(),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					cccCommands[5] = new BasesCCCCretaRequestCommand(File.BASES) { // new MainCCCCretaRequestCommand(File.BASES){
						@Override
						protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth,
								int toYear, String tipo, Collection<CCC> cccs) {
							JsBasesResult result = showBases(json, detailPanel, this);
							showResults(result, 
									resultsPanel, 
									r -> { /*TODO: */},  
									r -> showResultsPanel() );
						}				
						
					},
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
//			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
//					cccCommands[6] = new CCCCreateResponseCommand(File.BASES, File.TRABAJADORES_TRAMOS),
//					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
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
					getHTML("Contrato", AON.AON_ICON_EMPLOYEE,
							AON.AON_ICON_CMD_BUTTON),
					true, new NewEmployeeCommand());
			newEmployeeItem.setEnabled(true);

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
			addSeparator();
			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					workplaceCmds[0] = new WorkplaceCreateRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					workplaceCmds[2] = new WorkplaceCreateRequestCommand(
							CretaService.File.SOLICITUD_BORRADOR,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					workplaceCmds[3] = new WorkplaceCreateRequestCommand(
							CretaService.File.SOLICITUD_CONFIRMACION,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00FE1alculos",
					workplaceCmds[4] = new WorkplaceCreateRequestCommand(
							CretaService.File.SOLICITUD_CALCULOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					workplaceCmds[5] = new WorkplaceDBACommand(
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addSeparator();
			addItem("SLD-Fichero de Bases (Desde las n\u00F3minas en AON Solutions)",
					workplaceCmds[6] = new WorkplaceCreateRequestCommand(
							CretaService.File.BASES,
							employeeDetail, fileEditor){
				
				protected void onRequestDone(String json, int fromMonth, int fromYear, int toMonth, int toYear, String tipo, java.util.Collection<CCC> cccs) {
					JsBasesResult result = showBases(json, detailPanel, this);
					showResults(result, 
							resultsPanel, 
							r -> { /*TODO: */},  
							r -> showResultsPanel() );

				};
			}
					, AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Bases (Desde el fichero de Trabajadores y Tramos)",
					cretaResponseCmds[0] = new WorkplaceCreateResponseCommand(
							CretaService.File.BASES,
							CretaService.File.TRABAJADORES_TRAMOS,
							employeeDetail, resultsPanel) {
						@Override
						protected void showResultsPanel() {
							EmployeeTree.this.showResultsPanel();
						}
					}, AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			
			addSeparator();
			addItem("Refrescar",
					workplaceCmds[7] = new RefreshWorkplaceCommand(),
					AON.AON_ICON_REFRESH, AON.AON_ICON_CMD_BUTTON);
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
					getHTML("Centro", AON.AON_ICON_WORKPLACE,
							AON.AON_ICON_CMD_BUTTON),
					true, new NewWorkplaceCommand());
			newWorkPlaceItem.setEnabled(true);

			MenuItem newActivityItem = newPopup.addItem(
					getHTML("Actividad", AON.AON_ICON_INE,
							AON.AON_ICON_CMD_BUTTON),
					true, new NewActivityCommand());
			newActivityItem.setEnabled(true);

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
			addItem("SLD-Fichero de Solicitud de Trabajadores y Tramos",
					enterpriseCommands[0] = new EnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_TRABAJADORES_TRAMOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Borrador",
					enterpriseCommands[1] = new EnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_BORRADOR,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de Confirmaci\u00F3n",
					enterpriseCommands[2] = new EnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_CONFIRMACION,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Solicitud de C\u00E1lculos",
					enterpriseCommands[3] = new EnterpriseCretaRequestCommand(
							CretaService.File.SOLICITUD_CALCULOS,
							employeeDetail, fileEditor),
					AON.AON_ICON_SEGSOCIAL_SMALL, AON.AON_ICON_CMD_BUTTON);
			addItem("SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios",
					enterpriseCommands[4] = new EnterpriseDBACommand(
							employeeDetail, fileEditor),
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
		 * {@link #onRangeChanged(HasData)} is called when the table requests a
		 * new range of data. You can push data back to the displays using
		 * {@link #updateRowData(int, List)}.
		 */
		@Override
		protected void onRangeChanged(HasData<Employee> display) {
			// Get the new range.
			final Range range = display.getVisibleRange();
			// Query the data asynchronously (RPC call).
			getServiceAsync().getEmployees(
					getWorkplace().getId(), 
					getStartDate(),
					EmployeeTree.this.employees.getNamePattern(),
					range.getStart(), 
					range.getLength(),
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
			super(EmployeeTree.this.employeeDetail, 
				EmployeeTree.this.resultsPanel, 
				EmployeeTree.this::reftification,
				EmployeeTree.this::showResultsPanel);
		}

		@Override
		protected String getDescription(String ccc) {
			return null;
		}

		@Override
		protected <T extends JsFile> List<T> filter(Collection<T> jsFiles) {
			return null;
		}
	}
	
	private class CCCCretaDetail extends BaseCretaDetail {

		private CCC ccc;
		
		public void setCCC(CCC ccc) {
			this.ccc = ccc;
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
		void onClickBorradorButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_BORRADOR);
		}

		@Override
		void onClickConfirmacionButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_CONFIRMACION);
		}

		@Override
		void onClickTrabajadoresYTramosButton(ClickEvent e) {
			onRequestCommand(File.SOLICITUD_TRABAJADORES_TRAMOS);
		}

		@Override
		void onClickDBAButton(ClickEvent e) {
			CCCDBACommand cmd = new CCCDBACommand();
			cmd.setCCC(ccc);
			cmd.execute();
		}

		protected void onRequestCommand(File file) {
			CCCCretaRequestCommand cmd = new CCCCretaRequestCommand(file);
			cmd.setCCC(ccc);
			cmd.execute();
		}

		@Override
		String getEmployeeFullName(JsEmployee jsEmployee) {
			for ( Employee e : ccc.getEmployees() )
				if ( jsEmployee.getNaf().equals(e.getSocialSecurity())) 
					return e.getFullname();
			
			return super.getEmployeeFullName(jsEmployee);
		}
	}

	
	
	private class EmployeeTreeSyncCallback implements SyncCallback {

		private IndeterminateTask syncTask ;
		private List<JsBases> jsBasess;
		private List<JsRespuesta> jsRespuestas;
		private List<JsTrabajadoresYTramos> jsTrabajadoresYTramoss ;
		
		public EmployeeTreeSyncCallback(IndeterminateTask syncTask) {
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
			//MainCreta.this.enterprises.refresh();
			
			if ( !jsTrabajadoresYTramoss.isEmpty() )
				MainCreta.add(File.TRABAJADORES_TRAMOS, jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[jsTrabajadoresYTramoss.size()]));
			if ( !jsRespuestas.isEmpty() )
				MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[jsRespuestas.size()]));
			try {
				if ( !jsBasess.isEmpty() )
					MainCreta.add(File.BASES, jsBasess.toArray(new JsBases[jsBasess.size()]));
			} catch ( Throwable t ) {
			}
			
			//MainCreta.this.enterprises.refresh();
			
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
		public void onBases(JsBases jsBases ){
			syncTask.messageChanged(
					File.BASES.getFilename()
					+ " " + Province.getName(jsBases.getCCC().substring(4, 6))
					+ " (" + jsBases.getCCC().substring(6) + ")"
					+ " Sincronizado"
					) ;
			// TODO:
			jsBasess.add(jsBases);
			if ( jsBasess.size() < 5) 
				return;
			
			MainCreta.add(File.BASES, jsBasess.toArray(new JsBases[5]));
			jsBasess.clear();
		}
		
		@Override
		public void onRespuesta(JsRespuesta jsRespuesta ){
			syncTask.messageChanged(
					File.RESPUESTA.getFilename()
					+ " " + jsRespuesta.getDate()
					+ " " + jsRespuesta.getType() 
					+ " " + Province.getName(jsRespuesta.getCCC().substring(4, 6))
					+ " (" + jsRespuesta.getCCC().substring(6) + ")"
					+ " Sincronizado"
					) ;

			jsRespuestas.add(jsRespuesta);
			if ( jsRespuestas.size() < 5) 
				return;
			
			MainCreta.add(File.RESPUESTA, jsRespuestas.toArray(new JsRespuesta[5]));
			jsRespuestas.clear();
		
		}

		@Override
		public void onTrabajadoresYTramos(JsTrabajadoresYTramos jsTrabajadoresYTramos) {
			syncTask.messageChanged(
					File.TRABAJADORES_TRAMOS.getFilename()
					+ " " + jsTrabajadoresYTramos.getDate()
					+ " " + jsTrabajadoresYTramos.getType() 
					+ " " + Province.getName(jsTrabajadoresYTramos.getCCC().substring(4, 6))
					+ " (" + jsTrabajadoresYTramos.getCCC().substring(6) + ")"
					+ " Sincronizado"
					) ;
			jsTrabajadoresYTramoss.add(jsTrabajadoresYTramos);
			if ( jsTrabajadoresYTramoss.size() < 5) 
				return;
			MainCreta.add(File.TRABAJADORES_TRAMOS, jsTrabajadoresYTramoss.toArray(new JsTrabajadoresYTramos[5]));
			
			jsTrabajadoresYTramoss.clear();
		}
	}

	private static EmployeeTree singlenton;

	interface Binder extends UiBinder<Widget, EmployeeTree> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final String EMPLOYEE = "C-EMPLOYEE";

	@UiField(provided=true)
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
	private Irpf irpf;
	private Salary salary;
	private Statistics stats;
	private EnterpriseSalary enterpriseSalary;
	private ITEditor it;
	private WorkplaceSalary workplaceSalary;
	private CalendarDraft calendarDraft;
	private SalaryDraft salaryDraft;
	private SalaryPreview salaryPreview;
	private EventsDraft eventsDraft;
	private EnterpriseDraft enterpriseDraft;
	private WorkplaceDraft workplaceDraft;
	private ActivityDraft activityDraft;
	private EmployeeEventsDraft employeeEventsDraft;
	private EmployeeDraft employeeDraft;
	private EmployeeNewDraft employeeNewDraft;
	private EmployeeCalendarDraft employeeCalendarDraft;
	private EmployeeCalendarDraftNew employeeCalendarDraftNew;
	private EmployeeSalary employeeSalary;
	private CategoryDraft categoryDraft;
	private AgreementDraft agreementDraft;
	private BonusEditor bonusEditor;
	private PaymentEditor paymentEditor;
	private DeductionEditor deductionEditor;
	private EmployeePopupCopy paste;

	private FileEditor fileEditor;

	private ResultsPanel resultsPanel;
	private ProgressPanel progressPanel;

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
	
	// Cret@
	private CCCCretaDetail cccCretaDetail;

	/**
	 * This method constructs the application user interface by instantiating
	 * controls and hooking up event handler.
	 */
	public void onModuleLoad() {
		
		logEvent("start");
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint
				.CodeMirrorResources> create(
						MainEntryPoint.CodeMirrorResources.class)
				.css().ensureInjected();
		logEvent("richStylesInjected");
		
		employees = new Employees(true, true);
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

		// LocalStorage Items
		storage = Storage.getLocalStorageIfSupported();
		
		employees.addListener(this);

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
		} catch ( Throwable t){
			//TODO:
		}
		
		try {
			new Viewer();
		} catch ( Throwable t ) {
			
		}
		try {
			new com.esferalia.aon.js.payroll.client.Reports();
		} catch ( Throwable t ) {
			
		}
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

		com.esferalia.aon.gwt.payroll.shared.Cost cost = documents.getCosts()
				.get(documents.getCurrentIndex());
		share(cost, type, new Callback());

		showResultsPanel();

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

			com.esferalia.aon.gwt.payroll.shared.Salary salary = documents
					.getSalaries().get(documents.getCurrentIndex());
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
		DomainEnterprisesServiceAsync domainEnterprisesServiceAsync = DomainEnterprisesServiceAsync.newInstance();
		EnterpriseDraftObject activityDraftObject = new EnterpriseDraftObject(enterprise, domainEnterprisesServiceAsync);
		
		employeeDetail.setWidget(getEnterpriseDraft());
		getEnterpriseDraft().setEnterpriseDraftObject(activityDraftObject);
		
//		int pos = employees.getVerticalScrollPosition();
//		jsf.setRerenderHandler( () -> employees.setVerticalScrollPosition(pos) );
//
//		employeeDetail.setWidget(jsf);
//		jsf.enterpriseSelected(enterprise.getId());
		this.enterprise = enterprise;
	}

	@Override
	public void onWorkplaceSelected(Workplace workplace) {
		
		DomainEnterprisesServiceAsync domainEnterprisesServiceAsync = DomainEnterprisesServiceAsync.newInstance();
		DomainEmployeesServiceAsync domainEmployeeServiceAsync = DomainEmployeesServiceAsync.newInstance();
		
		WorkplaceDraftObject employeeNewDraftObject = new WorkplaceDraftObject(enterprise, workplace, domainEnterprisesServiceAsync, domainEmployeeServiceAsync);
		
		employeeDetail.setWidget(getWorkplaceDraft());
		getWorkplaceDraft().setWorkplaceDraftObject(employeeNewDraftObject);
		
//		int pos = employees.getVerticalScrollPosition();
//		jsf.setRerenderHandler( () -> employees.setVerticalScrollPosition(pos) );
//
//		employeeDetail.setWidget(jsf);
//		jsf.workplaceSelected(workplace.getId());
		this.workplace = workplace;
	}

	@Override
	public void onEmployeeSelected(Employee employee) {
//		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
//		DomainEnterprisesServiceAsync domainEnterprisesServiceAsync = DomainEnterprisesServiceAsync.newInstance();
//		
//		EmployeeDraftObject employeeDraftObject = new EmployeeDraftObject(getEmployeeTree().workplace, employee, employeesService, domainEnterprisesServiceAsync);
//		employeeDetail.setWidget(getEmployeeDraft());
//		getEmployeeDraft().setEmployeeDraftObject(employeeDraftObject);
		
		int pos = employees.getVerticalScrollPosition();
		jsf.setRerenderHandler( () -> employees.setVerticalScrollPosition(pos) );

		employeeDetail.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
		this.employee = employee;
	}

	@Override
	public void onActivitySelected(Activity activity) {
		DomainEnterprisesServiceAsync domainEnterprisesServiceAsync = DomainEnterprisesServiceAsync.newInstance();
		ActivityDraftObject activityDraftObject = new ActivityDraftObject(activity, domainEnterprisesServiceAsync);
		
		employeeDetail.setWidget(getActivityDraft());
		getActivityDraft().setActivityDraftObject(activityDraftObject);

//		int pos = employees.getVerticalScrollPosition();
//		jsf.setRerenderHandler( () -> employees.setVerticalScrollPosition(pos) );
//
//		employeeDetail.setWidget(jsf);
//		jsf.activitySelected(activity.getId());
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
	public void onCostsSelected(CostDocuments docs) {
		getCost().setTitle("Costes");
		employeeDetail.setWidget(getCost());
		getCost().setCostDocuments(docs);

	}

	@Override
	public void onStatisticsSelected(
			com.esferalia.aon.gwt.payroll.shared.Statistics statistics) {
		employeeDetail.setWidget(getStats());
		getStats().setStatistics(statistics);
	}
	
	@Override
	public void onEnterpriseSalariesSelected(EnterpriseSalaryObject enterpriseSalaryObject) {
		employeeDetail.setWidget(getEnterpriseSalary());
		getEnterpriseSalary().setEnterpriseSalaryObject(enterpriseSalaryObject);
	}

	@Override
	public void onITDataSelected(ITDataObject dataObject) {
		employeeDetail.setWidget(getIt());
		getIt().setITEditor(dataObject);
	}
	
	@Override
	public void onWorkplaceSalarySelected(WorkplaceSalaryObject dataObject) {
		employeeDetail.setWidget(getWorkplceSalary());
		getWorkplceSalary().setWorkplaceSalaryObject(dataObject);
	}

	@Override
	public void onCalendarSelected(
			CalendarDraftObjectData calendarDraftObjectData) {
		employeeDetail.setWidget(getCalendarDraft());
		getCalendarDraft().setCalendarDraftObject(null,
				calendarDraftObjectData);
	}

	@Override
	public void onEmployeeCalendarSelected(EmployeeCalendarDraftObjectData calendar) {
		employeeDetail.setWidget(getEmployeeCalendarDraft());
		getEmployeeCalendarDraft().setEmployeeCalendarDraftObject(calendar);
	}
	
	@Override
	public void onEmployeeNewCalendarSelected(EmployeeCalendarDraftObject calendar) {
		employeeDetail.setWidget(getEmployeeCalendarDraftNew());
		getEmployeeCalendarDraftNew().setEmployeeCalendarDraftObject(calendar);
	}
	
	@Override
	public void onEmployeeSalarySelected(EmployeeSalaryObject employeeSalary) {
		employeeDetail.setWidget(getEmployeeSalary());
		getEmployeeSalary().setEmployeeSalaryObject(employeeSalary);
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
	public void onCCCContextMenu(CCC ccc,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getCCCContextMenu().setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		getCCCContextMenu().setCCC(ccc);
		getCCCContextMenu().show();
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
	public void onEmployeeContextMenu(Employee employee,
			ContextMenuEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		getEmployeeContextMenu().setPopupPosition(nativeEvent.getClientX(),
				nativeEvent.getClientY());
		getEmployeeContextMenu().setEmployee(employee);
		getEmployeeContextMenu().show();
	}

	@Override
	public void onCategoryDraftSelected(
			CategoryDraftObject categoryDraftObject) {
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
	public void onEmployeeEventsDraftSelected(EmployeeEventsDraftObject employeeEventsDraft) {
		employeeDetail.setWidget(getEmployeeEventsDraft());
		getEmployeeEventsDraft().setEmployeeEventsDraftObject(employeeEventsDraft);
	}
	
	@Override
	public void onEmployeeDraftSelected(EmployeeDraftObject employeeDraftObject) {
		getEmployeeDraft().setOnSaved(e->{});
		employeeDetail.setWidget(getEmployeeDraft());
		getEmployeeDraft().setEmployeeDraftObject(employeeDraftObject);
		singlenton.employee = employeeDraftObject.getEmployee();
		
		employeeDraftObject.checkStatus(
		employeeStatus -> {
			SaltraResults saltraResults = new SaltraResults() {
				
				@Override
				public void run() {
					employeeDraftObject.checkStatus(
							employeeStatus -> {
								removeAll();
								employeeStatus.visit(this);
							}, 
							throwable -> {}
					);					
				}
				
				@Override
				protected void saltraCredentialsFound(){
					employeeDraftObject.checkStatus(
							employeeStatus -> {
								removeAll();
								employeeStatus.visit(this);
								selectResultsPanel();
								ifSaltraEnabled(employeeStatus, 
								() -> {
									showFootPanel();
									getEmployeeDraft().idcButton.setVisible(true);
									getEmployeeDraft().setOnSaved(e-> run());
								},
								() -> {
									closeFootPanel();
									getEmployeeDraft().idcButton.setVisible(false);

								});
							}, 
							throwable -> {
								closeFootPanel();
								getEmployeeDraft().idcButton.setVisible(false);
							}
					);					
				}
			};
			
			employeeStatus.visit(saltraResults);
			resultsPanel.setWidget(saltraResults);
			selectResultsPanel();
			
			ifSaltraEnabled(
			employeeStatus, 
			() -> {
				showFootPanel();
				getEmployeeDraft().idcButton.setVisible(true);
				getEmployeeDraft().setOnSaved(e-> saltraResults.run());
			},
			() -> {
				closeFootPanel();
				getEmployeeDraft().idcButton.setVisible(false);
			}
			);
			
		}, 
		throwable -> {
			closeFootPanel();
			getEmployeeDraft().idcButton.setVisible(false);

		});
	}
	
	@Override
	public void onEmployeeNewDraftSelected(EmployeeNewDraftObject employeeNewDraftObject) {
		employeeDetail.setWidget(getEmployeeNewDraft());
		getEmployeeNewDraft().setEmployeeNewDraftObject(employeeNewDraftObject);
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

	private void showFootPanel() {	
		EmployeeTree.this.splitLayoutPanel.setWidgetSize(
			EmployeeTree.this.footPanel, Window.getClientHeight() / 4);
	}
	
	private void showResultsPanel() {

		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		EmployeeTree.this.footTabPanel.add(EmployeeTree.this.resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);
		EmployeeTree.this.splitLayoutPanel.setWidgetSize(
				EmployeeTree.this.footPanel, Window.getClientHeight() / 4);

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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
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
			(enterpriseSalary = new EnterpriseSalary()).addListener(this);
		return enterpriseSalary;
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
			salaryDraft = new SalaryDraft();
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
			enterpriseDraft = new EnterpriseDraft()
									.setOnSaved(w -> refreshEnterprise());
		return enterpriseDraft;
	}
	
	private WorkplaceDraft getWorkplaceDraft() {
		if (workplaceDraft == null)
			workplaceDraft = new WorkplaceDraft()
							.setOnSaved(w -> refreshWorkplace() );
		return workplaceDraft;
	}
	
	private ActivityDraft getActivityDraft() {
		if (activityDraft == null)
			activityDraft = new ActivityDraft();
		return activityDraft;
	}
	
	private CCCCretaDetail getCCCCretaDetail() {
		if ( cccCretaDetail == null ) {
			cccCretaDetail = new CCCCretaDetail();
			
			progressPanel = new ProgressPanel();
			
			progressPanel.addAttachHandler(e ->  {
				// Synchronize cret@ messages. 
				IndeterminateTask syncTask = new IndeterminateTask();
				syncTask.setDescription("Sincronizando mensajes");
				progressPanel.showIndeterminateTask(syncTask);
				MainCreta.sync( new EmployeeTreeSyncCallback(syncTask), getCCs() );
			});
			showProgressPanel();


		}
		return cccCretaDetail;
	}

	private EmployeeEventsDraft getEmployeeEventsDraft() {
		if (employeeEventsDraft == null)
			employeeEventsDraft = new EmployeeEventsDraft();
		return employeeEventsDraft;
	}
	
	private EmployeeDraft getEmployeeDraft() {
		if (employeeDraft == null)
			employeeDraft = new EmployeeDraft();//.setOnSaved(w -> refreshWorkplace() );
		return employeeDraft;
	}
	
	private EmployeeNewDraft getEmployeeNewDraft() {
		if (employeeNewDraft == null)
			employeeNewDraft = new EmployeeNewDraft();
		return employeeNewDraft;
	}

	private CategoryDraft getCategoryDraft() {
		if (categoryDraft == null)
			categoryDraft = new CategoryDraft();
		return categoryDraft;
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
	
	private EmployeeSalary getEmployeeSalary() {
		if (employeeSalary == null)
			(employeeSalary = new EmployeeSalary()).addListener(this);
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
		return 
		(( CretaResults ) resultsPanel.getChild())
		.getParameter(CretaService.Parameter.INDICADOR_RECTIFICACION)
		.map( s -> "on".equalsIgnoreCase(s))
		.orElse(false)
		;
	}

	private void reftification(Void v) { 
		MainCreta.run(Collections.singletonMap(CretaService.Parameter.INDICADOR_RECTIFICACION, isReftification() ? "off" : "on"), resultsPanel);
	}

	private void showResultsPanel(Void v) {
		showResultsPanel();
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
	// ------------------------------------------------------ Protected methods
	
	protected static String getDescription(CCC ccc, Enterprise enterprise) {
		String province = ccc.getGeozone();

		for (Activity activity : enterprise.getActivities())
			for (CCC cc : activity.getCccs())
				if (ccc.getCode().equals(cc.getCode()))
					return activity.getDescription() + ", "
							+ ccc.getCode();

		return ccc.getCode();
	}
	
	protected static JsBasesResult showBases(String json , DetailPanel detailPanel) {
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
			
			mergeEditor.setText(result.getSalaryBasesFile());
			mergeEditor.setTitle(CretaService.File.BASES.getFilename());
			mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
			detailPanel.setWidget(mergeEditor);
			mergeEditor.autoRefresh();
			
		} catch ( NoSuchElementException e0 ) {
			try {
				
				mergeEditor.setText(result.getChangedBasesFile());
				mergeEditor.setTitle(CretaService.File.BASES.getFilename());
				mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
				detailPanel.setWidget(mergeEditor);
				mergeEditor.autoRefresh();
	
			} catch (NoSuchElementException e1) {
				try {
					mergeEditor.setShowDifferences(false);
					mergeEditor.setText(result.getDraftRequestFile());
					mergeEditor.setTitle(CretaService.File.BASES.getFilename());
					mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
					detailPanel.setWidget(mergeEditor);
					mergeEditor.autoRefresh();
				} catch ( NoSuchElementException e2 ){
					FileEditor basesEditor = new MainCreta.BasesFileEditor();
					basesEditor.setMode("text/xml");
					basesEditor.setFoldGutter(true);
					basesEditor.setLineNumbers(true);
					basesEditor.setText(result.getBasesFile());
					basesEditor.setTitle(CretaService.File.BASES.getFilename());
					basesEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
					detailPanel.setWidget(basesEditor);
					basesEditor.autoRefresh();
				}
			}	
		}

		
	}
	
	protected static JsBasesResult showBases(String json , DetailPanel detailPanel, CreateRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static JsBasesResult showBases(String json , DetailPanel detailPanel, BasesCCCCretaRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static JsBasesResult showBases(String json , DetailPanel detailPanel, AbstractCCCCretaRequestCommand cretaCommand) {
		JsBasesResult result = eval("(" + json + ")");
		showBases(result, detailPanel, cretaCommand);
		return result;
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel, ClickHandler clickHandler) {
		MergeEditor mergeEditor = new MainCreta.BasesMergeEditor();
		mergeEditor.setOrig(result.getBasesFile());
		mergeEditor.setMode("text/xml");
		mergeEditor.setFoldGutter(true);
		mergeEditor.setLineNumbers(true);
		mergeEditor.setOrig(result.getBasesFile());

		try {
			
			mergeEditor.setText(result.getSalaryBasesFile());
			mergeEditor.setTitle(CretaService.File.BASES.getFilename());
			mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
			detailPanel.setWidget(mergeEditor);
			mergeEditor.autoRefresh();
			
		} catch ( NoSuchElementException e0 ) {
			try {
				
				mergeEditor.setText(result.getChangedBasesFile());
				mergeEditor.setTitle(CretaService.File.BASES.getFilename());
				mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
				detailPanel.setWidget(mergeEditor);
				mergeEditor.autoRefresh();
	
			} catch (NoSuchElementException e1) {
				try {
					mergeEditor.setShowDifferences(false);
					mergeEditor.setText(result.getDraftRequestFile());
					mergeEditor.setTitle(CretaService.File.BASES.getFilename());
					mergeEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
					detailPanel.setWidget(mergeEditor);
					mergeEditor.autoRefresh();
				} catch ( NoSuchElementException e2 ){
					FileEditor basesEditor = new MainCreta.BasesFileEditor();
					basesEditor.setMode("text/xml");
					basesEditor.setFoldGutter(true);
					basesEditor.setLineNumbers(true);
					basesEditor.setText(result.getBasesFile());
					basesEditor.setTitle(CretaService.File.BASES.getFilename());
					basesEditor.setFilename(CretaService.File.BASES.getFilename() + ".xml");
					detailPanel.setWidget(basesEditor);
					basesEditor.autoRefresh();
	
					CheckBox reftification = new CheckBox("Reftificativa");
					reftification.setValue(result.isRectifying());
					reftification.setStyleName("aon-finding-toolbar-item");
					reftification.addClickHandler(clickHandler );
					basesEditor.add(reftification);
				}
			}	
		}
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel, CreateRequestCommand cretaCommand) {
		showBases(result, detailPanel, e->cretaCommand.reexecute(d-> {d.reftificationMarkCheckBox.setValue(!result.isRectifying());}));
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel, BasesCCCCretaRequestCommand cretaCommand) {
		showBases(result, detailPanel, e->cretaCommand.reexecute(d-> {d.reftificationMarkCheckBox.setValue(!result.isRectifying());}));
	}

	protected static void showBases(CretaService.JsBasesResult result, DetailPanel detailPanel, AbstractCCCCretaRequestCommand cretaCommand) {
		showBases(result, detailPanel, e->cretaCommand.reexecute(d-> {d.reftificationMarkCheckBox.setValue(!result.isRectifying());}));
	}

	protected static void showResults(
			JsBasesResult result, 
			Set<JsFile> jsFiles, 
			ResultsPanel resultsPanel, 
			Consumer<JsBasesResult> onBases,
			Consumer<ResultsPanel> showResultsPanel
			) {
		
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

		if (result.getErrors().length > 0
				|| result.getWarnings().length > 0)
			showResultsPanel.accept(resultsPanel);
		
	}
	
	protected static void showResults(
			JsBasesResult result, 
			ResultsPanel resultsPanel, 
			Consumer<JsBasesResult> onBases,
			Consumer<ResultsPanel> showResultsPanel
			) {
		showResults(result, Collections.emptySet(), resultsPanel, onBases, showResultsPanel);
	}

	// --------------------------------------------------------- Private methods
	
	protected static void showSaltraOptions(EmployeeStatus employeeStatus) {
		
	}

	protected static void showEmployeeCalendar() {
		EmployeeTree employeeTree = getEmployeeTree();
		EmployeeCalendarDraftObject calendar = employeeTree.getSalaryDraft().getSalaryDraftObject().getEmployeeCalendarDraftObject();
		employeeTree.employeeDetail.setWidget(employeeTree.getEmployeeCalendarDraftNew());
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
		DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
		WorkplaceDialog workplaceDialog = new WorkplaceDialog()
												.setOnSaved(w -> refreshEnterprise());
		WorkplaceDialogObject workplaceDialogObject = new WorkplaceDialogObject(getEmployeeTree().enterprise, enterprisesService);
		workplaceDialog.setWorkplaceDialogObject(workplaceDialogObject);
		workplaceDialog.center();
		workplaceDialog.show();
	}
	
	protected static void showNewActivity() {
		DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
		ActivityDialog activityDialog = new ActivityDialog();
		ActivityDialogObject activityDialogObject = new ActivityDialogObject(getEmployeeTree().enterprise, enterprisesService);
		activityDialog.setActivityDialogObject(activityDialogObject);
		activityDialog.center();
		activityDialog.show();
	}

	protected static void showNewContract() {
		DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
		DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
		
		EmployeeDialog employeeDialog = new EmployeeDialog() {
			@Override
			protected void onAccept() {}
		};
		EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(getEmployeeTree().workplace, employeesService, enterprisesService);
		employeeDialog.setEmployeeDialogObject(employeeDialogObject);
		employeeDialog.center();
		employeeDialog.show();
	}
	
	protected static void invokeRefreshWorkplace() {
		getEmployeeTree().employees.refresh(getEmployeeTree().workplace);
	}
	
	protected static void invokeRefreshEnterprise() {
//		Window.alert("Refresh Enterprise : " + singlenton.enterprise + ", Employees : " + singlenton.employees);
		getEmployeeTree().employees.refresh(getEmployeeTree().enterprise);
//		singlenton.employees.refresh(singlenton.enterprise);
	}
	
	protected static void showSalaryDraft(int employeeId, int workplaceId,
			Date startDate, Date endDate) {
		EmployeeTree employeeTree = getEmployeeTree();
		employeeTree.employees.selectSalaryDraft(employeeId, workplaceId, true);
	}

	private static EmployeeTree getEmployeeTree() {
		return singlenton;
	}

	private static <T extends HasId<?>> void share(
			com.esferalia.aon.gwt.payroll.shared.Salary salary,
			String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer
				.append("&" + ShareService.SALARY + "=" + salary.getId())
				.append("&type=" + type);

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);

	}
	
	private static <T extends HasId<?>> void shareSalary(
			SalaryInfo salary,
			String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer
				.append("&" + ShareService.SALARY + "=" + salary.getId())
				.append("&type=" + type);

		// Send request to server and catch any errors.
		share(requestDataBuffer.toString(), callback);

	}

	private static <T extends HasId<?>> void share(
			com.esferalia.aon.gwt.payroll.shared.Cost cost,
			String type,
			final AsyncCallback<JsShareResult> callback) {

		StringBuffer requestDataBuffer = new StringBuffer();

		requestDataBuffer
				.append("&" + ShareService.MONTH + "=" + cost.getMonth());
		requestDataBuffer
				.append("&" + ShareService.YEAR + "=" + cost.getYear());
		int workplaceId = cost.getWorkplaceId();
		if (workplaceId != 0)
			requestDataBuffer
					.append("&" + ShareService.WORKPLACE + "=" + workplaceId);
		else
			requestDataBuffer.append("&" + ShareService.ENTERPRISE + "="
					+ cost.getEnterpriseId());
		requestDataBuffer.append("&type=" + type);
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
						for (JsShareResult result = read(
								text); text != null; result = read(text))
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
		singlenton.getEnterpriseContextMenu()
				.setEnterprise(singlenton.enterprise);
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

			json.put("startDate",
					new JSONString(employee.getStartDate().toString()));

			if (employee.getSecondSurName() != null)
				json.put("second", new JSONString(employee.getSecondSurName()));

			if (employee.getEndDate() != null)
				json.put("endDate",
						new JSONString(employee.getEndDate().toString()));

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
			String firstSurname = json.get("first").toString().replaceAll("\"",
					"");

			String secondSurname = null;
			if (json.get("second") != null)
				secondSurname = json.get("second").toString().replaceAll("\"",
						"");

			String start = json.get("startDate").toString().replaceAll("\"",
					"");
			Date startDate = getDate(start);

			Date endDate = null;
			if (json.get("endDate") != null) {
				String end = json.get("endDate").toString().replaceAll("\"",
						"");
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
	
	private static boolean isSaltraEnabled(EmployeeStatus employeeStatus ) {
		try {
			employeeStatus.visit(new Visitor() {
				
				@Override
				public void up2Date() {
				}
				
				@Override
				public void saltraCredentialsNotFound() {
					throw new RuntimeException();
				}
				
				@Override
				public void occupationNotFound() {
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
		} catch ( Exception e ) {
			return false;
		}
	}
	

	private static void ifSaltraEnabled(EmployeeStatus employeeStatus, Runnable saltraEnable, Runnable saltraDisabled ) {
		employeeStatus.visit(new Visitor() {
			
			@Override
			public void up2Date() {
				saltraEnable.run();
			}
			

			@Override
			public void invalidData() {
				saltraEnable.run();
			}
			
			@Override
			public void endDateNotFound() {
				saltraEnable.run();
			}
			
			@Override
			public void employeeNotFound() {
				saltraEnable.run();
			}
			
			@Override
			public void occupationNotFound() {
				saltraEnable.run();
			}
			
			@Override
			public void saltraCredentialsNotFound() {
				saltraDisabled.run();
			}
						@Override
			public void mismatchedStartDate(MismatchedStartDate status) {
				saltraEnable.run();
			}
			
			@Override
			public void mismatchedPartialFactor(MismatchedPartialFactor status) {
				saltraEnable.run();
			}
			
			@Override
			public void mismatchedOccupation(MismatchedOccupation status) {
				saltraEnable.run();
			}
			
			@Override
			public void mismatchedContractType(MismatchedContractType status) {
				saltraEnable.run();
			}
			
			@Override
			public void mismatchedCCC(MismatchedCCC status) {
				saltraEnable.run();
			}
			
			@Override
			public void mismatchedQuoteGroup(MismatchedQuoteGroup status) {
				saltraEnable.run();

			}

		});
	}

	private static  String getDescription(CCC ccc, Workplace workplace) {
		String province = ccc.getGeozone();
		Activity activity = workplace.getActivity();
		return activity.getDescription() + "," + ccc.getCode();
	}
	
	private static native String cleanDiacritics(String str) /*-{
		return str.normalize('NFD').replace(/[\u0300-\u036f]/g,"");
	}-*/;
	
	
	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

}
