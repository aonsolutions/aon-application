package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.ifSistemaREDEnabled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.Task;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus.AffiliatedNotFound;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class MainContrataContract extends MainEntryPoint {

	// ------------------------------------------ ContrataEmployee

	private class ContrataEmployeeImpl extends ContrataEmployee {

		Task syncTask;

		@Override
		protected void onListShow(boolean reloadEmployees) {
			if (reloadEmployees)
				redrawTable();
			else
				employeeDataGrid.redraw();

			deckPanel.showWidget(0);
		}

		@Override
		protected void getContractBonus(Consumer<List<SSBonusData>> success, Consumer<Throwable> failure) {

			syncTask = new Task();
			syncTask.setDescription("Comprobando bonificaciones...");
			MainContrataContract.this.progressPanel.showTask(syncTask);

			splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.00);

			InlineLabel tab = new InlineLabel("Progreso");
			tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
			tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
			footTabPanel.add(progressPanel, tab);
			footTabPanel.selectTab(progressPanel);

			super.getContractBonus(l -> {
				success.accept(l);
				syncTask.messageChanged("Bonificaciones actualizadas :-)");
				syncTask.finished();
			}, failure);
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
		
		@Override
		protected void onTransformContract(Integer newContractId) {
			employeeSB.setValue("");
			inactiveContractsCB.setValue(false);
			workplaceLB.setSelectedIndex(0);
			mainContrataContractObject.getEmployeesInfo(false, 
				s -> {
					Integer selectedEmployeeIdx = getSelectedEmployeeIdx(newContractId);
					
					contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
					ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
					contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getActivitiesCCCContex());
					contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getWorkplacesContext());
					contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getAgreementsContext());
					contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getPayMethodsMapContext());
					contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, newContractId, selectedEmployeeIdx,
							employeesList.size(), su -> deckPanel.showWidget(2));
				}, 
				f -> {}
			);
			
			
		}

		private void loadEmployee(EmployeeContractInfo employee, Integer selectedEmployeeIdx, int employeesSize,
				int selectedTab) {
			if (null != employee) {
				Integer contractId = employee.getContractInfo().getContractId();
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getActivitiesCCCContex());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getWorkplacesContext());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getAgreementsContext());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getPayMethodsMapContext());
				contrataEmployee.setContrataEmployeeObject(contrataEmployeeDialogObject, contractId,
						selectedEmployeeIdx, employeesSize, selectedTab, s -> deckPanel.showWidget(2));
			}
		}

	}

	// ------------------------------------------ EnterpriseSalary

	private class EnterpriseSalaryImpl extends EnterpriseSalary {

		@Override
		protected void onBackClick() {
			deckPanel.showWidget(0);
		}

	}

	// ------------------------------------------ UiBinder

	interface UIBinder extends UiBinder<Widget, MainContrataContract> {
	}

	private static final UIBinder binder = GWT.create(UIBinder.class);

	// ------------------------------------------ UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();

		String filterPanel();

		String flexPanel();
	}

	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	SplitLayoutPanel splitLayoutPanel;

	@UiField
	HTMLPanel filterEmployeePanel;

	@UiField
	HTMLPanel mainTablePanel;

	@UiField
	HTMLPanel mainContainer;

	@UiField
	DeckPanel deckPanel;

	@UiField(provided = true)
	DataGrid<EmployeeContractInfo> employeeDataGrid;

	@UiField(provided = true)
	ContrataEmployee contrataEmployee;

	@UiField
	MinimizePanel footPanel;

	@UiField
	TabLayoutPanel footTabPanel;

	@UiField
	PDFViewer pdfViewer;

	ResultsPanel resultsPanel;

	ProgressPanel progressPanel;

	// Trash Employee

	@UiField
	DockLayoutPanel trashDockLayoutPanel;

	@UiField
	SplitLayoutPanel trashSplitLayoutPanel;

	@UiField
	HTMLPanel trashMainContainer;

	@UiField
	HTMLPanel filterTrashEmployeePanel;

	@UiField
	HTMLPanel mainTrashTablePanel;

	@UiField(provided = true)
	DataGrid<EmployeeContractInfo> trashEmployeeDataGrid;

	// Enterprise Salary

	@UiField(provided = true)
	EnterpriseSalary enterpriseSalary;

	// ------------------------------------------ Variables

	private MainContrataContractObject mainContrataContractObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private List<EmployeeContractInfo> employeesList = Collections.emptyList();
	private List<EmployeeContractInfo> trashEmployeesList = Collections.emptyList();
	private EnterpriseSalaryObject enterpriseSalaryObject;

	private AonToolbar toolbar;
	private AonToolbar trashToolbar;
	private AonToolbarButton up2DateSS;

	private SuggestBox employeeSB;
	private CheckBox inactiveContractsCB;
	private ListBox workplaceLB;

	// ------------------------------------------ Constructor

	public MainContrataContract() {
		contrataEmployee = new ContrataEmployeeImpl();
		enterpriseSalary = new EnterpriseSalaryImpl();
		enterpriseSalary.setBackButtonVisible();

		provideEmployeesDataGrid();
		provideTrashEmployeesDataGrid();

		// Add style to table header
		addStyleToHeader();

		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);

		getToolbarPanel();
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);

		getTrashToolbarPanel();
		trashDockLayoutPanel.addNorth(trashToolbar, AonToolbar.HEIGTH);

		getFilterEmployeePanel();

		// Show table
		deckPanel.showWidget(0);

		initFootPanel();
		initResultsPanel();
		initProgressPanel();
		initPDFViewer();
	}

	// ------------------------------------------ Provide Employees DataGrid

	private void provideEmployeesDataGrid() {
		employeesList = Collections.emptyList();

		employeeDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, EmployeeContractInfo.KEY_PROVIDER);
		employeeDataGrid.setWidth("100%");

		employeeDataGrid.setAutoHeaderRefreshDisabled(true);

		employeeDataGrid.setEmptyTableWidget(new Label("No existen contratos".toUpperCase()));

		NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel = new NoSelectionModel<>(EmployeeContractInfo.KEY_PROVIDER);
		employeeDataGrid.setSelectionModel(selectionCCCInfoModel);

		addEmployeeInfoColumns(selectionCCCInfoModel);

		new ListDataProvider<EmployeeContractInfo>(Collections.emptyList()).addDataDisplay(employeeDataGrid);

	}

	private void addEmployeeInfoColumns(NoSelectionModel<EmployeeContractInfo> selectionCCCInfoModel) {
		selectionCCCInfoModel.addSelectionChangeHandler(event -> {
			EmployeeContractInfo employeeContractInfoSelected = selectionCCCInfoModel.getLastSelectedObject();
			Integer contractId = employeeContractInfoSelected.getContractInfo().getContractId();

			Integer selectedEmployeeIdx = getSelectedEmployeeIdx(contractId);

			contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());
			ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
			contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getActivitiesCCCContex());
			contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getWorkplacesContext());
			contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getAgreementsContext());
			contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getPayMethodsMapContext());
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

		addStyleToHeader();
		
		addSortColums(employeeDataGrid, employeeContractInfoList);
	}
	
	// ------------------------------------------ Provide Trash Employees DataGrid

	private void provideTrashEmployeesDataGrid() {
		trashEmployeesList = Collections.emptyList();

		trashEmployeeDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, EmployeeContractInfo.KEY_PROVIDER);
		trashEmployeeDataGrid.setWidth("100%");

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
				if (null != employeeContractInfo.getContractInfo().getEndDate())
					return formatFullDate.format(employeeContractInfo.getContractInfo().getEndDate());

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

		addStyleToHeader();

		addSortColums(trashEmployeeDataGrid, trashEmployeeContractInfoList);
	}
	
	// ------------------------------------------ Header Styles

	public void addStyleToHeader() {
		String headerStyles = "rich-table-thead rich-table-subheader rich-table-subheadercell aon-dataTable-header";

		employeeDataGrid.getHeader(0).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(1).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(2).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(3).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(4).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(5).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(6).setHeaderStyleNames(headerStyles);
		employeeDataGrid.getHeader(7).setHeaderStyleNames(headerStyles);

		trashEmployeeDataGrid.getHeader(0).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(1).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(2).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(3).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(4).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(5).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(6).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(7).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(8).setHeaderStyleNames(headerStyles);
		trashEmployeeDataGrid.getHeader(9).setHeaderStyleNames(headerStyles);
	}

	// ------------------------------------------ OnModuleLoad

	public void onModuleLoad(MainContrataContractObject mainContrataContractObject) {
		this.mainContrataContractObject = mainContrataContractObject;
		
		this.mainContrataContractObject.getEmployeesInfo(false, 
				s -> {
					initWorkplaceLB();
					initEnterpriseSB();
					initContractTable();
					setTableHeights();
					checkStatus(this.mainContrataContractObject);
				}, 
				f -> {}
		);

		this.mainContrataContractObject.getContextInfo();

	}
	
	// ------------------------------------------ Initialize View

	private void initWorkplaceLB() {
		workplaceLB.clear();
		workplaceLB.addItem("-", "");
		for (Workplace workplace : this.mainContrataContractObject.getWorkplaces())
			workplaceLB.addItem(workplace.getDescription(), workplace.getId().toString());
		
		workplaceLB.addChangeHandler(e -> {
			String workplaceId = workplaceLB.getSelectedValue();
			if (AonStringUtils.isBlank(workplaceId))
				mainContrataContractObject.resetEmployeesList();
			else {
				List<Integer> employeesContractIds = mainContrataContractObject.getEmployeesContractIdsByWorkplace(workplaceId);
				mainContrataContractObject.filterEmployeesList(employeesContractIds);
			}

			initContractTable();
			employeeDataGrid.redraw();
		});
	}

	private void initEnterpriseSB() {
		// Enteprise List

		List<String> enterprises = new ArrayList<>(mainContrataContractObject.getEmployeesMap().keySet());

		List<String> enterprisesSuggest = new ArrayList<>();
		for (String enterprise : enterprises)
			enterprisesSuggest.add(enterprise + "");

		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) employeeSB.getSuggestOracle();
		orclEnterprise.addAll(enterprisesSuggest);
		employeeSB.setAutoSelectEnabled(false);

		employeeSB.addKeyUpHandler(e -> {
			String value = employeeSB.getValue();
			if (AonStringUtils.isBlank(value) || value.length() < 3)
				mainContrataContractObject.resetEmployeesList();
			else {
				List<Integer> employeesContractIds = mainContrataContractObject.getEmployeesContractIds(value);
				mainContrataContractObject.filterEmployeesList(employeesContractIds);
			}

			initContractTable();
		});

		employeeSB.addSelectionHandler(e -> {
			String value = employeeSB.getValue();
			List<Integer> employeesContractIds = mainContrataContractObject.getEmployeesContractIds(value);
			mainContrataContractObject.filterEmployeesList(employeesContractIds);

			initContractTable();
			employeeDataGrid.redraw();
		});
	}

	private void setTableHeights() {
		employeeDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 175), Unit.PX);

		trashEmployeeDataGrid.getElement().getStyle().setHeight(100, Unit.PCT);
		mainTrashTablePanel.getElement().getStyle().setHeight((Window.getClientHeight() - 175), Unit.PX);
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

	protected void showPFDF(String dataURI) {
		pdfViewer.setTitle("CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.");
		pdfViewer.setDocument(dataURI, Constants.DEFAULT_ZOOM / 100.00);
		deckPanel.showWidget(3);
	}

	protected void showTrashEmployee() {
		deckPanel.showWidget(1);
	}

	protected void showEnterpriseSalary() {
		if (null == enterpriseSalaryObject) {
			mainContrataContractObject.getEnterprise(
					enterprise -> {
						enterpriseSalaryObject = new EnterpriseSalaryObject(enterprise);
						enterpriseSalary.setEnterpriseSalaryObject(enterpriseSalaryObject);
					}, 
					f -> {}
			);
		}

		deckPanel.showWidget(4);
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
		employeeSB = new SuggestBox();
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
		footPanel.addMaximizeHandler(e -> splitLayoutPanel.setWidgetSize(footPanel, 150));
		footPanel.addMinimizeHandler(e -> splitLayoutPanel.setWidgetSize(footPanel, 25));
	}

	private void initResultsPanel() {
		resultsPanel = new ResultsPanel();
	}

	private void initProgressPanel() {
		progressPanel = new ProgressPanel();
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void showFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.00);
	}

	private void selectResultsPanel() {
		InlineLabel tab = new InlineLabel("Resultados");
		tab.addStyleName(AON.AON_ICON_TIME);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(resultsPanel, tab);
		footTabPanel.selectTab(resultsPanel);
	}

	private void selectProgressPanel() {
		InlineLabel tab = new InlineLabel("Progreso");
		tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		footTabPanel.add(progressPanel, tab);
		footTabPanel.selectTab(progressPanel);
	}

	private void closeProgressPanel() {
		footTabPanel.remove(progressPanel);
	}

	private void initPDFViewer() {
		Button closeButton = new Button("Cerrar");
		closeButton.setStylePrimaryName(AON.AON_ICON_CANCEL);
		closeButton.addClickHandler(e -> deckPanel.showWidget(0));
		pdfViewer.addCustomToolBarButton(closeButton);
	}
	
	private void setSistemaREDVisible(boolean visible) {
		up2DateSS.setVisible(visible);
	}

	private void checkStatus(MainContrataContractObject mainContrataContractObject) {
		mainContrataContractObject.checkStatus(enterpriseStatus -> {

			ifSistemaREDEnabled(enterpriseStatus, () -> {
				showFootPanel();
				MainContrataContract.this.setSistemaREDVisible(true);
			}, () -> {
				closeFootPanel();
				MainContrataContract.this.setSistemaREDVisible(false);
			});

			SistemaREDResults sistemaREDResults = new SistemaREDResults() {

				Task syncTask;

				@Override
				public void up2Date() {
					// Up2Date
				}

				@Override
				public void up2DateEnterprise() {
					this.setUp2DateEnterprise();
					closeFootPanel();
				}

				@Override
				public void run() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
					}, throwable -> {});
				}

				@Override
				protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false, s -> {
						MainContrataContract.this.initEnterpriseSB();
						MainContrataContract.this.initContractTable();
						MainContrataContract.this.setTableHeights();
					}, f -> {});
					run();
				}

				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults) {
					syncTask.messageChanged("Importados todos los trabajadores.");
					syncTask.finished();
					closeProgressPanel();
					MainContrataContract.this.mainContrataContractObject.getEmployeesInfo(false, s -> {
						MainContrataContract.this.initEnterpriseSB();
						MainContrataContract.this.initContractTable();
						MainContrataContract.this.setTableHeights();
					}, f -> {
					});
					run();
				}

				@Override
				protected void newAffiliated(JsArray<JsSistemaREDResults> jsResults, int total) {
					selectProgressPanel();
					syncTask.progressChanged((jsResults.length() / (double) total) * 100.00);
					String lastEmployeeName = jsResults.get(jsResults.length() - 1).getEmployeeName();
					syncTask.messageChanged("Importado '" + lastEmployeeName + "' (" + jsResults.length() + " de " + total + ").");
				}

				@Override
				protected void newEmployees(AffiliatedNotFound[] affiliatedNotFound) {
					syncTask = new Task();
					syncTask.setDescription("Importando trabajadores desde la Seguridad Social (Sistema R.E.D)");
					progressPanel.showTask(syncTask);
					super.newEmployees(affiliatedNotFound);
				}

				@Override
				protected void saltraCredentialsFound() {
					mainContrataContractObject.checkStatus(enterpriseStatus -> {
						removeAll();
						enterpriseStatus.visit(this);
						selectResultsPanel();
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
			};

			enterpriseStatus.visit(sistemaREDResults);
			resultsPanel.setWidget(sistemaREDResults);
			selectResultsPanel();

		}, throwable -> {
			closeFootPanel();
			MainContrataContract.this.setSistemaREDVisible(false);
		});
	}

	// ------------------------------------------ Toolbar
	
	private void getToolbarPanel() {
		this.toolbar = new AonToolbar("Contratos");

		AonToolbarButton newContract = new AonToolbarButton("Nuevo contrato", AON.CSS.aonIconAdd());
		newContract.addClickHandler(e -> onNewContract());
		toolbar.add(newContract);

		AonToolbarButton trashListBtn = new AonToolbarButton("Papelera Contratos", AON.CSS.aonIconTrashList());
		trashListBtn.addClickHandler(e -> onTrashListBtn());
		toolbar.add(trashListBtn);

		up2DateSS = new AonToolbarButton("CERTI. ESTAR AL CORRIENTE EN OBLIGAC. DE S.S.", AON.CSS.aonIconTgss());
		up2DateSS.addClickHandler(e -> onUp2DateSS());
		toolbar.add(up2DateSS);

		AonToolbarButton salariesBtn = new AonToolbarButton("N\u00F3minas Empresa", AON.CSS.aonIconReceipt());
		salariesBtn.addClickHandler(e -> showEnterpriseSalary());
		toolbar.add(salariesBtn);
	}
	
	// ------------------------------------------ Toolbar. Methods
	
	private void onNewContract() {
		EmployeeDialog employeeDialog = new EmployeeDialog(true) {
			@Override
			protected void onAccept(Integer contractId) {
				contrataEmployee.setHasCertificateSEPE(mainContrataContractObject.hasCertificateSEPE());

				Integer selectedEmployeeIdx = getSelectedEmployeeIdx(contractId);

				ContrataEmployeeObject contrataEmployeeDialogObject = new ContrataEmployeeObject();
				contrataEmployeeDialogObject.setActivitiesCCC(mainContrataContractObject.getActivitiesCCCContex());
				contrataEmployeeDialogObject.setWorkplaces(mainContrataContractObject.getWorkplacesContext());
				contrataEmployeeDialogObject.setAgreements(mainContrataContractObject.getAgreementsContext());
				contrataEmployeeDialogObject.setPayMethodsMap(mainContrataContractObject.getPayMethodsMapContext());
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
	
	private void onUp2DateSS() {
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", SistemaREDService.SISTEMA_RED_URL + "/" + SistemaREDService.UP2DATE_REPORT);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(xhrIn -> {
			int state = xhrIn.getReadyState();
			if (state != XMLHttpRequest.DONE)
				return;
			try {
				String dataURI = xhrIn.getResponseText();
				showPFDF(dataURI);
				AON.stop();
			} catch (Exception t) {
				AON.fail();
			}
		});

		StringBuilder requestDataBuffer = new StringBuilder();

		requestDataBuffer.append(SistemaREDService.Parameter.DOMAIN.name() + "=" + Wnd.getCurrentDomainNameURL())
				.append("&" + SistemaREDService.Parameter.USER.name() + "=" + Wnd.getCurrentUser());

		xhr.send(requestDataBuffer.toString());
		AON.start();
	}
	
	// ------------------------------------------ Toolbar Trash

	private void getTrashToolbarPanel() {
		this.trashToolbar = new AonToolbar("Papelera Contratos");

		AonToolbarButton backListBtn = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backListBtn.addClickHandler(e -> onBackListBtn());
		trashToolbar.add(backListBtn);
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

}
