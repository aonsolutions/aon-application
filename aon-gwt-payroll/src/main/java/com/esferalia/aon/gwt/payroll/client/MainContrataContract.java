package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.Task;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedNotFound;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainContrataContract extends MainEntryPoint {

	// ------------------------------------------ UiBinder

	interface UIBinder extends UiBinder<Widget, MainContrataContract> {}

	private static final UIBinder binder = GWT.create(UIBinder.class);
	
	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template ("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}
	
	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT.create(TabLayoutFolderSafeTemplate.class);

	// ------------------------------------------------- ScheduledCommand (TGSS)

	class UpdateCertCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onUpdateCert();
		}
	}
	
	class LaboralLifeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			onLaboralLife(new Date());
		}
	}
	
	class TGSSContextMenu extends ContextMenu {
		
		private MenuItem updateCert;

		public TGSSContextMenu() {
			updateCert = addMenuItem("Cert. de estar al corriente con TGSS", new UpdateCertCommand(), AON.CSS.aonIconTgss(), "updateCert");
			addMenuItem("Vida laboral", new LaboralLifeCommand(), AON.CSS.aonIconTgss(), "laboralLife");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public MenuItem getUpdateCert() {
			return this.updateCert;
		}

	}
	
	// ------------------------------------------ UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
		String filterPanel();
		String flexPanel();
		String inactive();
		String prevAlta();
		String closeEnd();
		String cmdBtn();
	}
	
	@UiField
	DeckPanel deckPanel;
	
	// Employees List
	
	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField (provided = true)
	AonToolbar employeeToolbar;
	
	@UiField
	HTMLPanel messageContainer;
	
	@UiField
	HTMLPanel filterEmployeePanel;

	@UiField(provided = true)
	DataGrid<EmployeeContractInfo> employeeDataGrid;
	
	@UiField
	AonMinimizePanel footPanel;

	@UiField
	TabLayoutPanel footTabPanel;
	
	ResultsPanel resultsPanel;

	ProgressPanel progressPanel;
	
	// Trash Employees List

	@UiField (provided = true)
	AonToolbar employeeTrashToolbar;

	@UiField
	HTMLPanel filterTrashEmployeePanel;

	@UiField(provided = true)
	DataGrid<EmployeeContractInfo> trashEmployeeDataGrid;

	// Contrata Employee
	
	@UiField(provided = true)
	ContrataEmployee contrataEmployee;

	// PDF Viewer
	
	@UiField
	HTMLPanel messagePDFContainer;
	
	@UiField(provided = true)
	AonToolbar pdfViewerToolbar;

	@UiField
	FullViewer pdfViewer;

	// Enterprise Salary

	@UiField(provided = true)
	EnterpriseSalary enterpriseSalary;

	
	@UiField
	SplitLayoutPanel  splitLayoutPanel;
	
	// ------------------------------------------ Variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private MainContrataContractObject mainContrataContractObject;
	private EnterpriseSalaryObject enterpriseSalaryObject;
	
	private List<EmployeeContractInfo> employeesList = Collections.emptyList();
	private List<EmployeeContractInfo> trashEmployeesList = Collections.emptyList();
	
	private AonExpandButton tgssExpandButton;
	private TGSSContextMenu tgssContextMenu;

	private TextBox employeeSB;
	private CheckBox inactiveContractsCB;
	private ListBox workplaceLB;

	private Task syncTask;
	
	private boolean contextLoaded = false;
	
	// ------------------------------------------ ContrataEmployee

	private class ContrataEmployeeImpl extends ContrataEmployee {

		@Override
		protected void onListShow(boolean reloadEmployees) {
			if (getChanges()) {
				redrawTable();
				checkStatus(mainContrataContractObject);
				setChanges(false);
			} else {
				employeeDataGrid.redraw();
			}

			deckPanel.showWidget(0);
		}

		@Override
		protected void onPreviusContract(Integer currentContractId) {
			EmployeeContractInfo newSelectectedEmployee = null;
			Integer selectedEmployeeIdx = null;

			for (int i = 0; i < employeesList.size(); i++) {
				EmployeeContractInfo employeeContractInfo = employeesList.get(i);
				Integer contractId = employeeContractInfo.getContractInfo().getContractId();
				if (currentContractId.equals(contractId)) {
					if (i == 0) {
						newSelectectedEmployee = employeesList.get(employeesList.size() - 1);
						selectedEmployeeIdx = employeesList.size() - 1;
					} else {
						newSelectectedEmployee = employeesList.get(i - 1);
						selectedEmployeeIdx = i - 1;
					}

					selectedEmployeeIdx++;
					break;
				}
			}

			loadEmployee(newSelectectedEmployee, selectedEmployeeIdx, employeesList.size(),
					tabLayOutPanel.getSelectedIndex());

		}

		@Override
		protected void onNextContract(Integer currentContractId) {
			EmployeeContractInfo newSelectectedEmployee = null;
			Integer selectedEmployeeIdx = null;

			for (int i = 0; i < employeesList.size(); i++) {
				EmployeeContractInfo employeeContractInfo = employeesList.get(i);
				Integer contractId = employeeContractInfo.getContractInfo().getContractId();
				if (currentContractId.equals(contractId)) {
					if (i == (employeesList.size() - 1)) {
						newSelectectedEmployee = employeesList.get(0);
						selectedEmployeeIdx = 0;
					} else {
						newSelectectedEmployee = employeesList.get(i + 1);
						selectedEmployeeIdx = i + 1;
					}

					selectedEmployeeIdx++;
					break;
				}
			}

			loadEmployee(newSelectectedEmployee, selectedEmployeeIdx, employeesList.size(),
					tabLayOutPanel.getSelectedIndex());
		}

		private void loadEmployee(EmployeeContractInfo employee, Integer selectedEmployeeIdx, int employeesSize,
				int selectedTab) {
			if (null != employee) {
				Integer contractId = employee.getContractInfo().getContractId();
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
				contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
				contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());
				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
				contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId,
						selectedEmployeeIdx, employeesSize, selectedTab, s -> deckPanel.showWidget(2));
			}
		}

		@Override
		protected DomainUserRoles getDomainUserRole() {
			return mainContrataContractObject.getDomainUserRoles();
		}

	}

	// ------------------------------------------ EnterpriseSalary

	private class EnterpriseSalaryImpl extends EnterpriseSalary {

		@Override
		protected void onBackClick() {
			deckPanel.showWidget(0);
		}

	}


	// ------------------------------------------ Constructor

	public MainContrataContract() {
		contrataEmployee = new ContrataEmployeeImpl();
		enterpriseSalary = new EnterpriseSalaryImpl();
		enterpriseSalary.setBackButtonVisible();

		provideEmployeesDataGrid();
		provideTrashEmployeesDataGrid();
		
		this.employeeToolbar = new AonToolbar("CONTRATOS");
		this.pdfViewerToolbar = new AonToolbar("CONTRATOS");
	
		getTrashToolbarPanel();

		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		showContracts();
		
		getToolbarPanel();
		getToolbarPDFViewerPanel();

		getFilterEmployeePanel();

		// Show table
		deckPanel.showWidget(0);

		tgssContextMenu = new TGSSContextMenu();
		
		initFootPanel();
		initResultsPanel();
		initProgressPanel();
	}

	// ------------------------------------------ Provide Employees DataGrid

	private void provideEmployeesDataGrid() {
		employeesList = Collections.emptyList();

		employeeDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, EmployeeContractInfo.KEY_PROVIDER);
		
		employeeDataGrid.setAutoHeaderRefreshDisabled(true);

		employeeDataGrid.setEmptyTableWidget(new Label("No existen contratos".toUpperCase()));

		NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel = new NoSelectionModel<>(EmployeeContractInfo.KEY_PROVIDER);
		employeeDataGrid.setSelectionModel(selectionCCCInfoModel);

		addEmployeeInfoColumns(selectionCCCInfoModel);

		new ListDataProvider<EmployeeContractInfo>(Collections.emptyList()).addDataDisplay(employeeDataGrid);
	}

	private void addEmployeeInfoColumns(NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel) {
		selectionCCCInfoModel.addSelectionChangeHandler(event -> {
			if(!contextLoaded)
				return;
			
			EmployeeContractInfo employeeContractInfoSelected = selectionCCCInfoModel.getLastSelectedObject();
			Integer contractId = employeeContractInfoSelected.getContractInfo().getContractId();

			Integer selectedEmployeeIdx = getSelectedEmployeeIdx(contractId);
			
			contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
			contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
			contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());
			ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
			contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
			contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
			contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
			contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
			contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId, selectedEmployeeIdx,
					employeesList.size(), s -> deckPanel.showWidget(2));
		});

		// Add Selection Column to table
		employeeDataGrid.setSelectionModel(selectionCCCInfoModel);

		// Columns
		TextColumn<EmployeeContractInfo> employeeNameColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getFullName();
			}
			
			@Override
			public void render(Context context, EmployeeContractInfo employeeContractInfo, SafeHtmlBuilder sb) {
				if (null != employeeContractInfo) {
					if(checkInactive(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: red;\" title=\"Inactivo\">" + employeeContractInfo.getEmployeeInfo().getFullName() + "</div>");
					else if(checkPrevAlta(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: green;\" title=\"Alta previa\">" + employeeContractInfo.getEmployeeInfo().getFullName() + "</div>");
					else if(checkCloseEnd(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: orange;\" title=\"Contrato cerca de finalizar\">" + employeeContractInfo.getEmployeeInfo().getFullName() + "</div>");
					else super.render(context, employeeContractInfo, sb);
				} else
					super.render(context, employeeContractInfo, sb);
			}
		};

		employeeNameColumn.setSortable(true);

		TextColumn<EmployeeContractInfo> documentColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getDocument();
			}
		};

		documentColumn.setSortable(true);
		documentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(documentColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> ssNumberColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getSsNumber();
			}
		};

		ssNumberColumn.setSortable(true);
		ssNumberColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(ssNumberColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> contractTypeColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				if ((byte) 3 == employeeContractInfo.getContractInfo().getSsRegimen())
					return "RETA";
				if ("000".equals(employeeContractInfo.getContractInfo().getContractType()))
					return "BECARIO";
				return employeeContractInfo.getContractInfo().getContractType();
			}

		};

		contractTypeColumn.setSortable(true);
		contractTypeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(contractTypeColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> workplaceColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getContractInfo().getWorkplaceName();
			}

		};

		workplaceColumn.setSortable(true);
		workplaceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(workplaceColumn, 15, Unit.PCT);

		TextColumn<EmployeeContractInfo> categoryColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getContractInfo().getAgreementCategory();
			}

		};

		categoryColumn.setSortable(true);
		categoryColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(categoryColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> startDateColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate());
			}
			
			@Override
			public void render(Context context, EmployeeContractInfo employeeContractInfo, SafeHtmlBuilder sb) {
				if (null != employeeContractInfo && null != employeeContractInfo.getContractInfo().getStartDate()) {
					if(checkPrevAlta(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: green;\" title=\"Alta previa\">" + formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate()) + "</div>");
					else super.render(context, employeeContractInfo, sb);
				} else
					super.render(context, employeeContractInfo, sb);
			}
		};

		startDateColumn.setSortable(true);
		startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(startDateColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> endDateColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				if (null != employeeContractInfo.getContractInfo().getEndDate())
					return formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate());

				return "";
			}
			
			@Override
			public void render(Context context, EmployeeContractInfo employeeContractInfo, SafeHtmlBuilder sb) {
				if (null != employeeContractInfo && null != employeeContractInfo.getContractInfo().getEndDate()) {
					if(checkInactive(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: red;\" title=\"Inactivo\">" + formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate()) + "</div>");
					else if(checkPrevAlta(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: green;\" title=\"Alta previa\">" + formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate()) + "</div>");
					else if(checkCloseEnd(employeeContractInfo)) sb.appendHtmlConstant("<div style=\"font-weight: bold !important; color: orange;\" title=\"Contrato cerca de finalizar\">" + formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate()) + "</div>");
					else super.render(context, employeeContractInfo, sb);
				} else
					super.render(context, employeeContractInfo, sb);
			}
		};

		endDateColumn.setSortable(true);
		endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		employeeDataGrid.setColumnWidth(endDateColumn, 10, Unit.PCT);

		// Add the columns.
		employeeDataGrid.addColumn(employeeNameColumn, "Nobre Completo");
		employeeDataGrid.addColumn(documentColumn, "Documento");
		employeeDataGrid.addColumn(ssNumberColumn, "N\u00B0 SS");
		employeeDataGrid.addColumn(contractTypeColumn, "Tipo Contrato");
		employeeDataGrid.addColumn(workplaceColumn, "Centro Trabajo");
		employeeDataGrid.addColumn(categoryColumn, "Categor\u00EDa");
		employeeDataGrid.addColumn(startDateColumn, "Fecha Inicio");
		employeeDataGrid.addColumn(endDateColumn, "Fecha Fin");

	}

	private void initContractTable() {
		ListDataProvider<EmployeeContractInfo> dataProvider = new ListDataProvider<>();

		dataProvider.addDataDisplay(employeeDataGrid);

		List<EmployeeContractInfo> employeeContractInfoList = dataProvider.getList();
		employeeContractInfoList.clear();
		
		this.employeesList = mainContrataContractObject.getEmployeesList();

		for (EmployeeContractInfo employeeContractInfo : this.employeesList)
			employeeContractInfoList.add(employeeContractInfo);

		employeeDataGrid.setPageSize(employeesList.size());
		
		addSortColums(employeeDataGrid, employeeContractInfoList);
	}
	
	// ------------------------------------------ Provide Trash Employees DataGrid

	private void provideTrashEmployeesDataGrid() {
		trashEmployeesList = Collections.emptyList();

		trashEmployeeDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, EmployeeContractInfo.KEY_PROVIDER);
		
		trashEmployeeDataGrid.setAutoHeaderRefreshDisabled(true);

		trashEmployeeDataGrid.setEmptyTableWidget(new Label("No existen contratos en la papelera".toUpperCase()));

		NoSelectionModel<EmployeeContractInfo> selectionTrashCCCInfoModel = new NoSelectionModel<>(EmployeeContractInfo.KEY_PROVIDER);
		trashEmployeeDataGrid.setSelectionModel(selectionTrashCCCInfoModel);

		addTrashEmployeeInfoColumns(selectionTrashCCCInfoModel);

		new ListDataProvider<EmployeeContractInfo>(Collections.emptyList()).addDataDisplay(trashEmployeeDataGrid);
	}

	private void addTrashEmployeeInfoColumns(NoSelectionModel<EmployeeContractInfo> selectionTrashCCCInfoModel) {
		selectionTrashCCCInfoModel.addSelectionChangeHandler(e -> {});

		// Add Selection Column to table
		trashEmployeeDataGrid.setSelectionModel(selectionTrashCCCInfoModel);

		// Columns
		TextColumn<EmployeeContractInfo> employeeNameColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getFullName();
			}
		};

		employeeNameColumn.setSortable(true);

		TextColumn<EmployeeContractInfo> documentColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getDocument();
			}
		};

		documentColumn.setSortable(true);
		documentColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(documentColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> ssNumberColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getEmployeeInfo().getSsNumber();
			}
		};

		ssNumberColumn.setSortable(true);
		ssNumberColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(ssNumberColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> contractTypeColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				if ((byte) 3 == employeeContractInfo.getContractInfo().getSsRegimen())
					return "RETA";
				if (employeeContractInfo.getContractInfo().getContractType().equals("000"))
					return "BECARIO";
				return employeeContractInfo.getContractInfo().getContractType();
			}

		};

		contractTypeColumn.setSortable(true);
		contractTypeColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(contractTypeColumn, 10, Unit.PCT);

		TextColumn<EmployeeContractInfo> workplaceColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getContractInfo().getWorkplaceName();
			}

		};

		workplaceColumn.setSortable(true);
		workplaceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(workplaceColumn, 15, Unit.PCT);

		TextColumn<EmployeeContractInfo> startDateColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return formatFullDate.format(employeeContractInfo.getContractInfo().getStartDate());
			}
		};

		startDateColumn.setSortable(true);
		startDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(startDateColumn, 8, Unit.PCT);

		TextColumn<EmployeeContractInfo> endDateColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				if (null != employeeContractInfo.getContractInfo().getEndDate()) {
					return formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate());
				}
				return "";
			}
		};

		endDateColumn.setSortable(true);
		endDateColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(endDateColumn, 8, Unit.PCT);

		TextColumn<EmployeeContractInfo> salaryNumColumn = new TextColumn<EmployeeContractInfo>() {
			@Override
			public String getValue(EmployeeContractInfo employeeContractInfo) {
				return employeeContractInfo.getContractInfo().getSalariesCount() + "";
			}
		};

		salaryNumColumn.setSortable(true);
		salaryNumColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		trashEmployeeDataGrid.setColumnWidth(salaryNumColumn, 9, Unit.PCT);

		ActionCell<EmployeeContractInfo> draftActionCell = new ActionCell<>("",
			employeeContractInfo -> {
				AonConfirmDialog confirmDialog = new AonConfirmDialog();
				confirmDialog.confirm("BORRADO",
						"\u00BFDesea eliminar definitivamente el contrato de  "
						+ employeeContractInfo.getEmployeeInfo().getFullName()
						+ "?. Le recordamos que este contrato tiene n\u00F3"
						+ "minas generadas, si lo elimina definitivamente no podr"
						+ "\u00E1 recuperar dichas n\u00F3minas.",
						new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								mainContrataContractObject.delete4EverContract(
										employeeContractInfo.getContractInfo().getContractId(), 
										s -> redrawTrashTable(), 
										f -> {}
								);
							}

							@Override
							public void onCancel() {
								// Cancel
							}
						});
			}
		);
				

		Column<EmployeeContractInfo, EmployeeContractInfo> draftColumn = new Column<EmployeeContractInfo, EmployeeContractInfo>(draftActionCell) {

			@Override
			public EmployeeContractInfo getValue(EmployeeContractInfo object) {
				return object;
			}

			@Override
			public void render(Context context, EmployeeContractInfo object, SafeHtmlBuilder sb) {
				if (null != object) {
					sb.appendHtmlConstant(
							"<button type=\"button\" class=\"aon_button aon_table_button aon_icon_delete_forever\" style=\"border: none !important; height: 20px;\" title=\"Borrar contrato definitivamente\"></button>");
				}
			}
		};

		draftColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		trashEmployeeDataGrid.setColumnWidth(draftColumn, 5, Unit.PCT);

		ActionCell<EmployeeContractInfo> restoreActionCell = new ActionCell<>("",
				employeeContractInfo -> {
					AonConfirmDialog confirmDialog = new AonConfirmDialog();
					confirmDialog.confirm("RESTAURAR",
							"\u00BFDesea restaurar el contrato de "
							+ employeeContractInfo.getEmployeeInfo().getFullName() + "?",
							new AonConfirmDialogCallback() {

								@Override
								public void onAccept() {
									mainContrataContractObject.restoreContract(
											employeeContractInfo.getContractInfo().getContractId(), 
											s -> redrawTrashTable(), 
											f -> {}
									);
								}

								@Override
								public void onCancel() {
									// Cancel
								}
							});
				}
		);
				

		Column<EmployeeContractInfo, EmployeeContractInfo> restoreColumn = new Column<EmployeeContractInfo, EmployeeContractInfo>(
				restoreActionCell) {

			@Override
			public EmployeeContractInfo getValue(EmployeeContractInfo object) {
				return object;
			}

			@Override
			public void render(Context context, EmployeeContractInfo object, SafeHtmlBuilder sb) {
				if (null != object) {
					sb.appendHtmlConstant(
							"<button type=\"button\" class=\"aon_button aon_table_button aon_icon_restore\" style=\"border: none !important; height: 20px;\" title=\"Restaurar contrato\"></button>");
				}
			}
		};

		restoreColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		trashEmployeeDataGrid.setColumnWidth(restoreColumn, 5, Unit.PCT);

		// Add the columns.
		trashEmployeeDataGrid.addColumn(employeeNameColumn, "Nobre Completo");
		trashEmployeeDataGrid.addColumn(documentColumn, "Documento");
		trashEmployeeDataGrid.addColumn(ssNumberColumn, "N\u00B0 SS");
		trashEmployeeDataGrid.addColumn(contractTypeColumn, "Tipo Contrato");
		trashEmployeeDataGrid.addColumn(workplaceColumn, "Centro Trabajo");
		trashEmployeeDataGrid.addColumn(startDateColumn, "Fecha Inicio");
		trashEmployeeDataGrid.addColumn(endDateColumn, "Fecha Fin");
		trashEmployeeDataGrid.addColumn(salaryNumColumn, "N\u00B0 n\u00F3minas");
		trashEmployeeDataGrid.addColumn(draftColumn, "");
		trashEmployeeDataGrid.addColumn(restoreColumn, "");
	}

	private void initTrashContractTable() {
		ListDataProvider<EmployeeContractInfo> dataProvider = new ListDataProvider<>();

		dataProvider.addDataDisplay(trashEmployeeDataGrid);

		List<EmployeeContractInfo> trashEmployeeContractInfoList = dataProvider.getList();
		trashEmployeeContractInfoList.clear();

		this.trashEmployeesList = mainContrataContractObject.getTrashEmployeesList();

		for (EmployeeContractInfo employeeContractInfo : this.trashEmployeesList)
			trashEmployeeContractInfoList.add(employeeContractInfo);

		trashEmployeeDataGrid.setPageSize(trashEmployeesList.size());

		addSortColums(trashEmployeeDataGrid, trashEmployeeContractInfoList);
	}
	
	private boolean checkInactive(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date endDate = employeeContractInfo.getContractInfo().getEndDate();
		return null != endDate && DateUtils.isBeforeOrEquals(endDate, currentDate);
	}

	private boolean checkPrevAlta(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date startDate = employeeContractInfo.getContractInfo().getStartDate();
		return DateUtils.isAfterOrEquals(startDate, currentDate) && !DateUtils.equals(startDate, currentDate);
	}

	private boolean checkCloseEnd(EmployeeContractInfo employeeContractInfo) {
		Date currentDate = new Date();
		Date endDate = employeeContractInfo.getContractInfo().getEndDate();
		return null != endDate && DateUtils.isBeforeOrEquals(currentDate, endDate) && DateUtils.getDaysBetween(currentDate, endDate) < 30;
	}
	
	// ------------------------------------------ OnModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainContrataContractObject());
	}

	public void onModuleLoad(MainContrataContractObject mainContrataContractObject) {
		this.mainContrataContractObject = mainContrataContractObject;
		this.contextLoaded = false;
		
		AonMessagePanel.showLoading(messageContainer, "Obteniendo contexto de la empresa...");
		
		this.mainContrataContractObject.getEmployeesInfo(false, 
				s -> {
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
					checkStatus(this.mainContrataContractObject);
				}, 
				f -> {}
		);

		this.mainContrataContractObject.getContextInfo(
				s -> {
					hideMessage();
					setTableHeights();
					employeeDataGrid.redraw();
					initWorkplaceLB();
					contextLoaded = true;
				}, f -> {}
		);

	}
	
	// ------------------------------------------ Initialize View

	private void initWorkplaceLB() {
		workplaceLB.clear();
		workplaceLB.addItem("-", "");
		for (Workplace workplace : this.mainContrataContractObject.getEnterpriseContext().getWorkplaces())
			workplaceLB.addItem(workplace.getDescription(), workplace.getId().toString());
		
		workplaceLB.addChangeHandler(e -> {
			String workplaceIdStr = workplaceLB.getSelectedValue();
			if (AonStringUtils.isBlank(workplaceIdStr))
				mainContrataContractObject.resetEmployeesList();
			else {
				Integer workplaceId = Integer.parseInt(workplaceIdStr);
				mainContrataContractObject.filterEmployeesList(workplaceId);
			}

			initContractTable();
			employeeDataGrid.redraw();
		});
	}

	private void initEnterpriseSB() {
		// Enteprise List

//		List<EmployeeContractInfo> employees = mainContrataContractObject.getAllEmployeesList();
//
//		List<String> enterprisesSuggest = new ArrayList<>();
//		for (EmployeeContractInfo employee : employees) {
//			String fullName = employee.getEmployeeInfo().getFullName();
//			String document = employee.getEmployeeInfo().getDocument();
//			String ssNumber = employee.getEmployeeInfo().getSsNumber();
//			
//			enterprisesSuggest.add(fullName + ", "+ document + ", " + ssNumber);
//		}
//			
//		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) employeeSB.getSuggestOracle();
//		orclEnterprise.addAll(enterprisesSuggest);
//		employeeSB.setAutoSelectEnabled(false);

		employeeSB.addKeyUpHandler(e -> {
			String value = employeeSB.getValue();
			if (AonStringUtils.isBlank(value) || value.length() < 3)
				mainContrataContractObject.resetEmployeesList();
			else
				mainContrataContractObject.filterEmployeesList(value);

			initContractTable();
		});
	}

	private void setTableHeights() {
		employeeDataGrid.getElement().getStyle().setHeight(Window.getClientHeight() - 200.00, Unit.PX);
		trashEmployeeDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
	}

	// ------------------------------------------ addSortColums
	
	private void addSortColums(DataGrid<EmployeeContractInfo> dataGrid, List<EmployeeContractInfo> employeeContractInfoList) {
		
		ListHandler<EmployeeContractInfo> columnSortHandler = new ListHandler<>(employeeContractInfoList);

		columnSortHandler.setComparator(dataGrid.getColumn(0), 
				(o1, o2) -> compareString(o1, o2, o1.getEmployeeInfo().getFullName(), o2.getEmployeeInfo().getFullName()));

		columnSortHandler.setComparator(dataGrid.getColumn(1),
				(o1, o2) -> compareString(o1, o2, o1.getEmployeeInfo().getDocument(), o2.getEmployeeInfo().getDocument()));

		columnSortHandler.setComparator(dataGrid.getColumn(2),
				(o1, o2) -> compareString(o1, o2, o1.getEmployeeInfo().getSsNumber(), o2.getEmployeeInfo().getSsNumber()));

		columnSortHandler.setComparator(dataGrid.getColumn(3), 
				(o1, o2) -> compareString(o1, o2, o1.getContractInfo().getContractType(), o2.getContractInfo().getContractType()));

		columnSortHandler.setComparator(dataGrid.getColumn(4),
				(o1, o2) -> compareString(o1, o2, o1.getContractInfo().getWorkplaceName(), o2.getContractInfo().getWorkplaceName()));

		columnSortHandler.setComparator(dataGrid.getColumn(5),
				(o1, o2) -> compareString(o1, o2, o1.getContractInfo().getAgreementCategory(), o2.getContractInfo().getAgreementCategory()));

		columnSortHandler.setComparator(dataGrid.getColumn(6),
				(o1, o2) -> compareDates(o1, o2, o1.getContractInfo().getStartDate(), o2.getContractInfo().getStartDate()));

		columnSortHandler.setComparator(dataGrid.getColumn(7),
				(o1, o2) -> compareDates(o1, o2, o1.getContractInfo().getEndDate(), o2.getContractInfo().getEndDate()));

		// We know that the data is sorted alphabetically by default.
		dataGrid.getColumn(0).setDefaultSortAscending(false);
		dataGrid.getColumnSortList().push(dataGrid.getColumn(0));

		dataGrid.addColumnSortHandler(columnSortHandler);

	}
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}

	// ------------------------------------------ DeckPanel Methods

	protected void showTrashEmployee() {
		deckPanel.showWidget(1);
	}

	protected void showEnterpriseSalary() {
		if (null == enterpriseSalaryObject) {
			mainContrataContractObject.getEnterprise(
					enterprise -> {
						enterpriseSalaryObject = new EnterpriseSalaryObject(enterprise);
						enterpriseSalary.setEnterpriseSalaryObject(enterpriseSalaryObject);
						enterpriseSalary.hideEditSalaryButton();
					}, 
					f -> {}
			);
		}

		deckPanel.showWidget(4);
	}
	
	protected void exportEnterpriseContracts() {
		String printURL = URL.encode(GWT.getModuleBaseURL() + "enteprise_contracts/");
		
		FormPanel formPanel = new FormPanel("_blank");
		formPanel.setAction(printURL);
		formPanel.setMethod(FormPanel.METHOD_GET);
		
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.add(new Hidden("domain", Wnd.getCurrentDomainNameURL()));
		formPanel.add(flowPanel);
		
		formPanel.addSubmitCompleteHandler(e1 -> employeeToolbar.remove(formPanel));
		
		employeeToolbar.add(formPanel);
		
		formPanel.submit();
	}
	
	// ------------------------------------------ Redraw Tables

	private void redrawTable() {
		this.employeeSB.setValue("");
		this.inactiveContractsCB.setValue(false);
		this.workplaceLB.setSelectedIndex(0);
		this.mainContrataContractObject.getEmployeesInfo(false, 
			s -> {
				initContractTable();
				setTableHeights();
			}, 
			f -> {}
		);
	}

	private void redrawTrashTable() {
		this.mainContrataContractObject.getTrashEmployeesInfo(
			s -> {
				initTrashContractTable();
				setTableHeights();
			}, 
			f -> {}
		);
	}
	
	// ------------------------------------------ Filter Panel

	private void getFilterEmployeePanel() {
		filterEmployeePanel.setStyleName(AON.CSS.aonSearchPanel());
		filterEmployeePanel.addStyleName(AON.CSS.aonScrollArea());
		filterEmployeePanel.addStyleName(AON.CSS.aonMarginBottom());
		filterEmployeePanel.addStyleName(AON.CSS.aonMarginLeft());
		filterEmployeePanel.addStyleName(AON.CSS.aonMarginRight());
		filterEmployeePanel.addStyleName(AON.CSS.aonBlockCenter());

		HTMLPanel filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(style.filterPanel());

		HTMLPanel employeePanel = new HTMLPanel("");
		employeePanel.addStyleName(style.flexPanel());
		Label employeeL = new Label("Persona : ");
		employeeL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		employeeL.getElement().getStyle().setMarginRight(10, Unit.PX);
		employeeSB = new TextBox();
		employeeSB.getElement().getStyle().setWidth(300, Unit.PX);
		employeePanel.add(employeeL);
		employeePanel.add(employeeSB);

		HTMLPanel showPanel = new HTMLPanel("");
		showPanel.addStyleName(style.flexPanel());
		Label workplaceL = new Label("Centro Trabajo : ");
		workplaceL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		workplaceL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		workplaceLB = new ListBox();
		workplaceLB.setStyleName("aon-selectOneMenu");
		workplaceLB.getElement().getStyle().setMarginLeft(5, Unit.PX);
		workplaceLB.getElement().getStyle().setMarginRight(5, Unit.PX);

		Label inactiveL = new Label("Empleados Inactivos");
		inactiveL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		inactiveL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		inactiveContractsCB = new CheckBox();
		inactiveContractsCB.addValueChangeHandler(e -> 
			this.mainContrataContractObject.getEmployeesInfo(e.getValue(), 
				s -> {
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
				}, 
				f -> {}
			)
		);

		showPanel.add(workplaceL);
		showPanel.add(workplaceLB);
		showPanel.add(inactiveL);
		showPanel.add(inactiveContractsCB);

		filterPanel.add(employeePanel);
		filterPanel.add(showPanel);

		filterEmployeePanel.add(filterPanel);
	}
	
	// ------------------------------------------ Auxiliar Methods

	private void initFootPanel() {
		footPanel.addMaximizeHandlerNew(event -> showFootPanel());
		footPanel.addMinimizeHandlerNew(event -> closeFootPanel());
		footPanel.clearButtons();
		footPanel.addButtonLess();
	}

	private void initResultsPanel() {
		resultsPanel = new ResultsPanel();
		footTabPanel.add(resultsPanel, TABLAYOUT_FOLDER_TEMPLATE.tab("Resultados", AON.CSS.aonIconHistory()));
		footTabPanel.selectTab(resultsPanel);
	}

	private void initProgressPanel() {
		progressPanel = new ProgressPanel();
		syncTask = new Task();
		progressPanel.showTask(syncTask);
		InlineLabel tab = new InlineLabel("Progreso");
		tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(progressPanel, tab);
		footTabPanel.selectTab(progressPanel);
	}
	
	private void showFootPanel() {
		footPanel.addButtonMore();
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.00);
		splitLayoutPanel.animate(500);
	}
	
	private void closeFootPanel() {
		footPanel.addButtonLess();
		splitLayoutPanel.setWidgetSize(footPanel, 17);
		splitLayoutPanel.animate(500);
	}

	private void showResultsPanel() {
		hideTabs();
		visibleTabItem(resultsPanel, true);
		footTabPanel.selectTab(resultsPanel);
	}

	private void showProgressPanel(String message) {	
		hideTabs();
		
		syncTask.messageChanged(AonStringUtils.isBlank(message) ? "Consultando Trabajadores en el SISTEMA RED" : message);
		visibleTabItem(progressPanel, true);
		footTabPanel.selectTab(progressPanel);
	}

	private void hideTabs(){
		if(resultsPanel!=null) 
			visibleTabItem(resultsPanel, false);

		if(progressPanel!=null) 
			visibleTabItem(progressPanel, false);
	}
	
	private void setSistemaREDVisible(boolean visible) {
		tgssExpandButton.setVisible(visible);
	}

	private void checkStatus(MainContrataContractObject mainContrataContractObject) {
		showProgressPanel(null);
		mainContrataContractObject.checkStatus(enterpriseStatus -> {
			SistemaREDResults sistemaREDResults = new SistemaREDResults() {
//				Task syncTask;
				
				@Override
				public void up2Date() {
					// Up2Date
					closeFootPanel();
				}

				@Override
				public void up2DateEnterprise() {
					this.setUp2DateEnterprise();
					closeFootPanel();
				}

				@Override
				public void run() {
					showProgressPanel(null);
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						finish();
					}, throwable -> {
						finish();
					});
				}

				@Override
				protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
					showProgressPanel("Importando Trabajador/es");
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false, s -> {
						MainContrataContract.this.initEnterpriseSB();
						MainContrataContract.this.initContractTable();
						MainContrataContract.this.setTableHeights();
					}, f -> {});
					run();
				}

				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults) {
//					syncTask.messageChanged("Importados todos los trabajadores.");
//					syncTask.finished();
					showProgressPanel("Importados todos los trabajadores.");
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false, s -> {
						MainContrataContract.this.initEnterpriseSB();
						MainContrataContract.this.initContractTable();
						MainContrataContract.this.setTableHeights();
					}, f -> {});
					run();
				}

				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsResults, int total) {
					showProgressPanel("Importando Trabajadores");
//					syncTask.progressChanged((jsResults.length() / (double) total) * 100.00);
//					String lastEmployeeName = jsResults.get(jsResults.length() - 1).getEmployeeName();
//					syncTask.messageChanged();
				}

				@Override
				protected void newEmployees(AffiliatedNotFound[] affiliatedNotFound) {
//					syncTask = new Task();
//					syncTask.setDescription("Importando trabajadores desde la Seguridad Social (Sistema R.E.D)");
//					progressPanel.showTask(syncTask);
					showProgressPanel("Importando trabajadores desde la Seguridad Social (Sistema R.E.D)");
					super.newEmployees(affiliatedNotFound);
				}

				@Override
				protected void saltraCredentialsFound() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						showResultsPanel();
						EnterpriseStatus.ifSistemaREDEnabled(enterpriseStatus, () -> {
							showFootPanel();
							MainContrataContract.this.setSistemaREDVisible(true);
						}, () -> {
							closeFootPanel();
							MainContrataContract.this.setSistemaREDVisible(false);
						});
					}, throwable -> {
						closeFootPanel();
						MainContrataContract.this.setSistemaREDVisible(false);
					});
				}
				
				protected void init() {
					showProgressPanel("Importando trabajador/es desde la Seguridad Social (Sistema R.E.D)");
				}
				
				protected void finish() {
					showResultsPanel();
				}
			};

			enterpriseStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			showResultsPanel();

			EnterpriseStatus.ifSistemaREDEnabled(
				enterpriseStatus,
				MainContrataContract.this::showFootPanel,
				MainContrataContract.this::closeFootPanel
			);
			
			EnterpriseStatus.ifSistemaREDError(
				enterpriseStatus,
				MainContrataContract.this::showFootPanel,
				MainContrataContract.this::closeFootPanel
			);
		}, throwable -> {
			closeFootPanel();
			MainContrataContract.this.setSistemaREDVisible(false);
		});
	}

	// ------------------------------------------ Toolbar
	
	private void getToolbarPanel() {

		AonToolbarButton newContract = new AonToolbarButton("Nuevo contrato", AON.CSS.aonIconAdd());
		newContract.addClickHandler(e -> onNewContract());
		employeeToolbar.add(newContract);

		AonToolbarButton trashListBtn = new AonToolbarButton("Papelera Contratos", AON.CSS.aonIconTrashList());
		trashListBtn.addClickHandler(e -> onTrashListBtn());
		employeeToolbar.add(trashListBtn);
		
		tgssExpandButton = new AonExpandButton("TGSS", AON.CSS.aonIconTgss()) {

			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				tgssContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				tgssContextMenu.show();
			}

			@Override
			public void onDefaultClick(ClickEvent evet) {
				onUpdateCert();
			}
		};
		employeeToolbar.add(tgssExpandButton);

		AonToolbarButton salariesBtn = new AonToolbarButton("N\u00F3minas Empresa", AON.CSS.aonIconReceipt());
		salariesBtn.addClickHandler(e -> showEnterpriseSalary());
		employeeToolbar.add(salariesBtn);
		
		AonToolbarButton exportExcelBtn = new AonToolbarButton("Exportar Contratos Empresa", AON.CSS.aonIconExcel());
		exportExcelBtn.addClickHandler(e -> exportEnterpriseContracts());
		employeeToolbar.add(exportExcelBtn);
	}
	
	private void onUpdateCert() {
		AonMessagePanel.showLoading(messageContainer, "Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					showPdf(false);
					pdfViewer.open(dataURI);
					AonMessagePanel.hideMessage(messageContainer);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(messageContainer, warningMap);
				});
	}
	
	public void onLaboralLife(Date date) {
		AonMessagePanel.showLoading(messageContainer, "Obteniendo vida laboral ...");
		
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getCCCLaboralLife(completeCCC.getKey(), completeCCC.getValue(), date, new Date(),
				dataURI -> {
					showPdf(true);
					pdfViewer.open(dataURI);
					AonMessagePanel.hideMessage(messageContainer);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(messageContainer, warningMap);
				});

	}
	
	public void onLaboralLifeCahngeDate(Date date) {
		AonMessagePanel.showLoading(messagePDFContainer, "Obteniendo vida laboral ...");
		
		Pair<String, String> completeCCC = mainContrataContractObject.getPrincipalAccount();
		
		mainContrataContractObject.getCCCLaboralLife(completeCCC.getKey(), completeCCC.getValue(), date, new Date(),
				dataURI -> {
					pdfViewer.open(dataURI);
					AonMessagePanel.hideMessage(messagePDFContainer);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(messagePDFContainer, warningMap);
				});

	}
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private AonToolbar getToolbarPDFViewerPanel() {

		AonToolbarButton closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.addClickHandler(e -> onClosePDF());
		pdfViewerToolbar.add(closePDF);
		
		return pdfViewerToolbar;
	}
	
	// ------------------------------------------ Toolbar. Methods
	
	private void onNewContract() {
		EmployeeDialog employeeDialog = new EmployeeDialog(true) {
			@Override
			protected void onAccept(Integer contractId) {
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
				contrataEmployee.setIsComunica(mainContrataContractObject.isComunica());
				contrataEmployee.setHasPayroll(mainContrataContractObject.hasPayroll());

				Integer selectedEmployeeIdx = getSelectedEmployeeIdx(contractId);

				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getEnterpriseContext().getActivitiesCCC());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getEnterpriseContext().getWorkplaces());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getEnterpriseContext().getAgreements());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getEnterpriseContext().getPayMethods());
				contrataEmployee.setChanges(true);
				contrataEmployee.setContrataEmployeeObject(
						contrataEmployeeDialogObject, 
						contractId,
						selectedEmployeeIdx, 
						employeesList.size(), 
						s -> deckPanel.showWidget(2));
			}
		};

		EmployeeDialogObject employeeDialogObject = new EmployeeDialogObject(null);
		employeeDialog.setEmployeeDialogObject(employeeDialogObject);
		employeeDialog.setModal(true);
		employeeDialog.setAnimationEnabled(true);
		employeeDialog.center();
		employeeDialog.show();
	}
	
	private void onTrashListBtn() {
		redrawTrashTable();
		showTrashEmployee();
	}
	
	// ------------------------------------------ Toolbar Trash

	private void getTrashToolbarPanel() {
		employeeTrashToolbar = new AonToolbar("Papelera Contratos");

		AonToolbarButton backListBtn = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backListBtn.addClickHandler(e -> onBackListBtn());
		employeeTrashToolbar.add(backListBtn);
	}

	private void onBackListBtn() {
		redrawTable();
		deckPanel.showWidget(0);
	}

	private Integer getSelectedEmployeeIdx(Integer currentContractId) {
		Integer selectedEmployee = null;
		for (int i = 0; i < employeesList.size(); i++) {
			EmployeeContractInfo employeeContractInfo = employeesList.get(i);
			Integer contractId = employeeContractInfo.getContractInfo().getContractId();
			if (currentContractId.equals(contractId)) {
				selectedEmployee = i + 1;
				break;
			}
		}
		return selectedEmployee;
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	private void hideMessage() {
		AonMessagePanel.hideMessage(messageContainer);
	}
	
	public void visibleTabItem ( Widget tabItem, boolean hideFl ) {
		Widget element = footTabPanel.getTabWidget(tabItem);

		element.setVisible(hideFl);

		
	}
	
	// ------------------------------------------------- Show/Hide PDF

	private void showContracts() {
		deckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		deckPanel.showWidget(3);
		checkPDFToolbar(isLaboralLife);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		if(isLaboralLife && pdfViewerToolbar.getButtonContainer().getWidgetCount() == 1) {
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(52);
			monthListBox.setVisibleRange(0, 52);
			monthListBox.addChangeHandler(e -> onLaboralLifeCahngeDate(monthListBox.getSelected()));
			monthListBox.setSelected(DateUtils.getFirstDayOfMonth(), true);
			monthListBox.setWidth("200px");
			pdfViewerToolbar.add(monthListBox);
		} else if(!isLaboralLife && pdfViewerToolbar.getButtonContainer().getWidgetCount() > 1)
			pdfViewerToolbar.getButtonContainer().remove(pdfViewerToolbar.getButtonContainer().getWidgetCount()-1);
		
	}

	private void onClosePDF() {
		AonMessagePanel.hideMessage(messageContainer);
		showContracts();
	}
}
