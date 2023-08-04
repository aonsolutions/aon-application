package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

public class MainCRA extends MainEntryPoint {

	interface Binder extends UiBinder<Widget, MainCRA> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String suggestBox();
	}
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messagePanel;

	@UiField
	DeckPanel deckPanel;

	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	ListBox month;

	@UiField
	ListBox year;

	@UiField
	CheckBox allCCCsCB;

	@UiField
	CheckBox emitCCCsCB;

	@UiField
	CheckBox peddingCCCsCB;

	@UiField
	TableElement filterTable;

	@UiField
	ListBox geozoneList;

	@UiField
	DisclosurePanel collapsePanel;

	@UiField
	SuggestBox enterpriseSB;

	@UiField
	Label enterprisesSelected;

	@UiField
	ListBox typeList;

	@UiField
	ListBox monthTillT;

	@UiField
	ListBox yearTillT;

	@UiField
	HTMLPanel mainTablePanel;

	@UiField
	HTMLPanel mainContainer;

	@UiField
	HTMLPanel crasContainer;

	@UiField(provided = true)
	DataGrid<CCCInfo> cccDataGrid;

	@UiField
	HTMLPanel crasPanel;

	@UiField(provided = true)
	DataGrid<CRA> crasDataGrid;

	private AonToolbarButton exportButton;
	private AonToolbarButton listButton;
	private AonToolbarButton newCRAButton;

	private List<CCCInfo> cccs = Collections.emptyList();
	private List<CRA> cras = Collections.emptyList();
	private Integer enterprisesSelectedCount = 0;

	public MainCRA() {
		// Provide DataGrid
		provideCCCDataGrid();
		provideCRAsDataGrid();
		
		// Create toolbar
		getToolbarPanel();

		// Inject Styles
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		// Init UiBinder
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);

		// Init view and listboxes
		initPreView();
		initListBoxes();
		setCRAHeightNotCollapsePanel();
		setCCCsHeight();
	}

	private void setCCCsHeight() {
		cccDataGrid.setHeight((Window.getClientHeight() - 300) + "px");
	}

	private void setCRAHeightCollapsePanel() {
		crasDataGrid.setHeight((Window.getClientHeight() - 300) + "px");
	}
	
	private void setCRAHeightNotCollapsePanel() {
		crasDataGrid.setHeight((Window.getClientHeight() - 380) + "px");
	}

	private void initPreView() {
		collapsePanel.setOpen(true);
		collapsePanel.addOpenHandler(e -> setCRAHeightNotCollapsePanel());
		collapsePanel.addCloseHandler(e -> setCRAHeightCollapsePanel());
		enterprisesSelected.setText(enterprisesSelectedCount.toString());
	}

	private void initListBoxes() {
		// Set list box for filter by dates
		month.clear();
		monthTillT.clear();
		year.clear();
		yearTillT.clear();

		String[] months = new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto",
				"Septiembre", "Octubre", "Noviembre", "Diciembre" };
		for (int i = 0; i < months.length; i++) {
			month.addItem(months[i], i + "");
			monthTillT.addItem(months[i], i + "");
		}

		Integer yearInt = DateUtils.getYear();

		year.addItem(yearInt + "", yearInt + "");
		year.addItem((yearInt - 1) + "", (yearInt - 1) + "");
		year.addItem((yearInt - 2) + "", (yearInt - 2) + "");
		year.addItem((yearInt - 3) + "", (yearInt - 3) + "");

		yearTillT.addItem(yearInt + "", yearInt + "");
		yearTillT.addItem((yearInt - 1) + "", (yearInt - 1) + "");
		yearTillT.addItem((yearInt - 2) + "", (yearInt - 2) + "");
		yearTillT.addItem((yearInt - 3) + "", (yearInt - 3) + "");

		// Type List
		typeList.clear();
		typeList.addItem("-", "-1");
		typeList.addItem("Principal", "0");
		typeList.addItem("Formacion y aprendizaje", "1");
		typeList.addItem("Aprendizaje", "2");
		typeList.addItem("Representantes de comercio", "3");
		typeList.addItem("Asimilados R.General", "4");
		typeList.addItem("Becarios", "5");
		typeList.addItem("Emploead@s de hogar", "6");
		typeList.addItem("Trabajadores cuenta ajena agrarios", "7");
		typeList.addItem("Artistas", "8");
		typeList.getElement().getElementsByTagName("option").getItem(2).setAttribute("disabled", "disabled");
		typeList.addStyleName("aon-selectOneMenu");

		// Geozone
		geozoneList.clear();
		geozoneList.addItem("-", "-1");

		for (Entry<String, String> province : ProvinceContract.getProvinces().entrySet()) {
			geozoneList.addItem(province.getValue(), province.getKey());
		}

	}

	// --------------------------------------------------------------------------------------------
	// PROVIDE SALARY DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideCCCDataGrid() {
		cccs = Collections.emptyList();

		// Resource Style CellTable
		cccDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, CCCInfo.KEY_PROVIDER);
		cccDataGrid.setWidth("100%");

		// Do not refresh the headers every time the dataGrid is updated.
		cccDataGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		cccDataGrid.setEmptyTableWidget(
				new Label("No existen cuentas de cotizaci\u00F3n con n\u00F3minas para este periodo".toUpperCase()));

		// Add a selection model so we can select cells.
		this.selectionCCCInfoModel = new MultiSelectionModel<>(CCCInfo.KEY_PROVIDER);
		cccDataGrid.setSelectionModel(this.selectionCCCInfoModel,
				DefaultSelectionEventManager.<CCCInfo>createCheckboxManager());

		// Initialize the columns.
		addCCCInfoColumns(this.selectionCCCInfoModel);

		new ListDataProvider<CCCInfo>(Collections.emptyList()).addDataDisplay(cccDataGrid);

	}

	private void addCCCInfoColumns(MultiSelectionModel<CCCInfo> selectionCCCInfoModel) {

		selectionCCCInfoModel.addSelectionChangeHandler(selectionEvent -> {
			enterprisesSelectedCount = selectionCCCInfoModel.getSelectedSet().size();
			enterprisesSelected.setText(enterprisesSelectedCount.toString());
			exportButton.setEnabled(!selectionCCCInfoModel.getSelectedSet().isEmpty());
		});

		Column<CCCInfo, Boolean> checkColumn = new Column<CCCInfo, Boolean>(new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(CCCInfo object) {
				return selectionCCCInfoModel.isSelected(object);
			}
		};

		CheckboxCell selectAllHeaderCB = new CheckboxCell(true, true);
		Header<Boolean> selectAllHeader = new Header<Boolean>(selectAllHeaderCB) {
			@Override
			public Boolean getValue() {
				return null != mainCRAObjectNew && null != mainCRAObjectNew.getEnterpriseCCCs() && selectionCCCInfoModel.getSelectedSet().size() == mainCRAObjectNew.getEnterpriseCCCs().size();
			}
		};

		selectAllHeader.setUpdater(isSelected -> {
			if (null != mainCRAObjectNew && null != mainCRAObjectNew.getEnterpriseCCCs())
				for (CCCInfo cccInfo : mainCRAObjectNew.getEnterpriseCCCs())
					selectionCCCInfoModel.setSelected(cccInfo, isSelected);
		});

		// Add Selection Column to table
		cccDataGrid.addColumn(checkColumn, selectAllHeader);
		cccDataGrid.setColumnWidth(checkColumn, 5, Unit.PCT);
		checkColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		// ----------------------------------------------------------------------
		// CREATE COLUMNS
		// ----------------------------------------------------------------------

		TextColumn<CCCInfo> enterpriseNameColumn = new TextColumn<CCCInfo>() {
			@Override
			public String getValue(CCCInfo cccInfo) {
				return cccInfo.getEnterpriseDesciption();
			}
		};

		enterpriseNameColumn.setSortable(true);
		cccDataGrid.setColumnWidth(enterpriseNameColumn, 25, Unit.PCT);

		TextColumn<CCCInfo> activityNameColumn = new TextColumn<CCCInfo>() {
			@Override
			public String getValue(CCCInfo cccInfo) {
				return cccInfo.getActivityDescription();
			}
		};

		activityNameColumn.setSortable(true);
		cccDataGrid.setColumnWidth(activityNameColumn, 20, Unit.PCT);

		TextColumn<CCCInfo> geozoneColumn = new TextColumn<CCCInfo>() {
			@Override
			public String getValue(CCCInfo cccInfo) {
				return ProvinceContract.getName(cccInfo.getGeozone());
			}
		};

		geozoneColumn.setSortable(true);
		cccDataGrid.setColumnWidth(geozoneColumn, 10, Unit.PCT);

		TextColumn<CCCInfo> typeColumn = new TextColumn<CCCInfo>() {
			@Override
			public String getValue(CCCInfo cccInfo) {
				return getCCCType(cccInfo.getType());
			}

		};

		typeColumn.setSortable(true);
		cccDataGrid.setColumnWidth(typeColumn, 20, Unit.PCT);

		TextColumn<CCCInfo> cccColumn = new TextColumn<CCCInfo>() {
			@Override
			public String getValue(CCCInfo cccInfo) {
				return cccInfo.getCcc();
			}
		};

		cccColumn.setSortable(true);
		cccDataGrid.setColumnWidth(cccColumn, 15, Unit.PCT);

		// Add the columns.
		cccDataGrid.addColumn(enterpriseNameColumn, "Empresa");
		cccDataGrid.addColumn(activityNameColumn, "Actividad");

		cccDataGrid.addColumn(geozoneColumn, "Provincia");

		cccDataGrid.addColumn(typeColumn, "Tipo CCC");
		cccDataGrid.addColumn(cccColumn, "CCC");

	}

	// --------------------------------------------------------------------------------------------
	// PROVIDE CRA DATA GRID
	// --------------------------------------------------------------------------------------------

	private void provideCRAsDataGrid() {
		cras = Collections.emptyList();

		// Resource Style CellTable
		crasDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, CRA.KEY_PROVIDER);
		crasDataGrid.setWidth("100%");

		// Do not refresh the headers every time the dataGrid is updated.
		crasDataGrid.setAutoHeaderRefreshDisabled(true);

		// Set the message to display when the table is empty.
		crasDataGrid.setEmptyTableWidget(new Label("No existen CRAs".toUpperCase()));

		// Add a selection model so we can select cells.
		this.selectionCraModel = new MultiSelectionModel<>(CRA.KEY_PROVIDER);
		crasDataGrid.setSelectionModel(this.selectionCraModel,
				DefaultSelectionEventManager.<CRA>createCheckboxManager());

		// Initialize the columns.
		addCraColumns();

		new ListDataProvider<CRA>(Collections.emptyList()).addDataDisplay(crasDataGrid);

	}

	private void addCraColumns() {

		// ----------------------------------------------------------------------
		// CREATE COLUMNS
		// ----------------------------------------------------------------------

		TextColumn<CRA> creationColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return formatDateHour(cra.getDate());
			}
		};

		creationColumn.setSortable(true);
		creationColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		crasDataGrid.setColumnWidth(creationColumn, 15, Unit.PCT);

		TextColumn<CRA> periodColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return formatDate(cra.getCreationDate());
			}
		};

		periodColumn.setSortable(true);
		periodColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		crasDataGrid.setColumnWidth(periodColumn, 10, Unit.PCT);

		TextColumn<CRA> activityNameColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return cra.getActivityName();
			}
		};

		activityNameColumn.setSortable(true);
		crasDataGrid.setColumnWidth(activityNameColumn, 25, Unit.PCT);

		TextColumn<CRA> rectificativeColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return parseCRAType(cra.getType());
			}
		};

		rectificativeColumn.setSortable(true);
		crasDataGrid.setColumnWidth(rectificativeColumn, 10, Unit.PCT);

		TextColumn<CRA> geozoneColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return cra.getCccProvince();
			}
		};

		geozoneColumn.setSortable(true);
		crasDataGrid.setColumnWidth(geozoneColumn, 10, Unit.PCT);

		TextColumn<CRA> typeColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return getCCCType(cra.getCccType());
			}

		};

		typeColumn.setSortable(true);
		crasDataGrid.setColumnWidth(typeColumn, 15, Unit.PCT);

		TextColumn<CRA> cccColumn = new TextColumn<CRA>() {
			@Override
			public String getValue(CRA cra) {
				return cra.getCcc();
			}
		};

		cccColumn.setSortable(true);
		crasDataGrid.setColumnWidth(cccColumn, 10, Unit.PCT);

		ActionCell<CRA> infoActionCell = new ActionCell<>("", cra -> {
			if (Boolean.TRUE.equals(cra.getIsConsignment())) {
				StringBuilder bld = new StringBuilder();
				
				for (CCCInfo cccInfo : cra.getIncludeCCCs())
					bld.append(cccInfo.toString() + "<br>");
				
				createInfoDialog("CRA (" + cra.getIncludeCCCs().size() + " CCCs)", bld.toString());
			}
		}); 

		Column<CRA, CRA> infoColumn = new Column<CRA, CRA>(infoActionCell) {

			@Override
			public CRA getValue(CRA cra) {
				return cra;
			}

			@Override
			public void render(Context context, CRA cra, SafeHtmlBuilder sb) {
				if (null != cra &&  cra.getIncludeCCCs().size() > 1) {
					sb.appendHtmlConstant("<button type=\"button\" title=\"Ver CCCs\" class=\"aon_button aon_icon_info aon_table_button\" style=\"border: none !important;\"></button>");
				}
			}
		};

		infoColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(infoColumn, 5, Unit.PCT);

		ActionCell<CRA> downloadActionCell = new ActionCell<>("", cra -> {
			String fileDownloadURL = GWT.getModuleBaseURL() + "/download_cra/" + "?craBatchId=" + cra.getCode();
			Window.open(fileDownloadURL, "_blank", null);
		}); 

		Column<CRA, CRA> downloadColumn = new Column<CRA, CRA>(downloadActionCell) {

			@Override
			public CRA getValue(CRA cra) {
				return cra;
			}

			@Override
			public void render(Context context, CRA cra, SafeHtmlBuilder sb) {
				if (null != cra)
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_icon_download aon_table_button\" style=\"border: none !important; height: 20px;\"></button>");
			}
		};

		downloadColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(downloadColumn, 5, Unit.PCT);

		ActionCell<CRA> deleteActionCell = new ActionCell<>("", cra -> {
			mainCRAObjectNew.setDefaultLiquidDate(findingDateCRA);
			mainCRAObjectNew.deteleCRA(cra.getCode(), 
					s -> {
						AonDialog dialog = new AonDialog("Eliminaci\u00f3n CRA",
								new HTML("Se va a proceder a eliminar el CRA para periodo de liquidaci\u00f3n <b>" + formatDate(cra.getCreationDate()) + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
						
						dialog.confirm(new AonAcceptDialogCallback() {

							@Override
							public void onCancel() {
								// Nothing to do here
							}

							@Override
							public void onAccept() {
								AonMessagePanel.showLoading(messagePanel, "Elimando CRA. Periodo de liquidaci\u00f3n " + formatDate(cra.getCreationDate()) + "...");
								mainCRAObjectNew.removeCCCCRADate(cra);
								initCRATable();
								AonMessagePanel.showSuccess(messagePanel, "Se ha eliminado el CRA correctamente");
							}
						});
							
					},
					f -> {});
		}); 

		Column<CRA, CRA> deleteColumn = new Column<CRA, CRA>(deleteActionCell) {

			@Override
			public CRA getValue(CRA cra) {
				return cra;
			}

			@Override
			public void render(Context context, CRA cra, SafeHtmlBuilder sb) {
				if (null != cra)
					sb.appendHtmlConstant("<button type=\"button\" class=\"aon_button aon_icon_delete aon_table_button\" style=\"border: none !important; height: 20px;\"></button>");
			}
		};

		deleteColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		crasDataGrid.setColumnWidth(deleteColumn, 5, Unit.PCT);

		// Add the columns.
		crasDataGrid.addColumn(creationColumn, "F. Creacion");
		crasDataGrid.addColumn(periodColumn, "P. Liquidacion");
		crasDataGrid.addColumn(activityNameColumn, "Actividad");

		crasDataGrid.addColumn(rectificativeColumn, "Tipo CRA");

		crasDataGrid.addColumn(geozoneColumn, "Provincia");

		crasDataGrid.addColumn(typeColumn, "Tipo CCC");
		crasDataGrid.addColumn(cccColumn, "CCC");

		crasDataGrid.addColumn(infoColumn, "");
		crasDataGrid.addColumn(downloadColumn, "");
		crasDataGrid.addColumn(deleteColumn, "");

	}

	private void createInfoDialog(String title, String message) {
		AonDialog dialog = new AonDialog(title, new HTML(message));
		dialog.info();
	}

	private String formatDateHour(Date date) {
		return null == date ? "" : formatFullDateHour.format(date);
	}

	private String formatDate(Date date) {
		return null == date ? "" : formatFullDate.format(date);
	}

	// --------------------------------------------------------------------------------------------
	// VARIABLES
	// --------------------------------------------------------------------------------------------

	private MainCRAObject mainCRAObjectNew;
	private MultiSelectionModel<CCCInfo> selectionCCCInfoModel;
	private MultiSelectionModel<CRA> selectionCraModel;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("MM/yyyy");
	private DateTimeFormat formatFullDateHour = DateTimeFormat.getFormat("dd/MM/yyyy");
	private Date findingDate = new Date();
	private Date findingDateCRA = new Date();

	// --------------------------------------------------------------------------------------------
	// ON MODULE LOAD
	// --------------------------------------------------------------------------------------------
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainCRAObject());
	}

	public void onModuleLoad(MainCRAObject mainCRAObjectNew) {
		this.mainCRAObjectNew = mainCRAObjectNew;

		// Create findPeriod, first day of previus month
		createInitialDate();
		setInitialLBAndCBSelected();
		peddingCCCsCB.setValue(true, false);
		onListCras();

		selectionCCCInfoModel.addSelectionChangeHandler(
				selectionEvent -> exportButton.setVisible(!selectionCCCInfoModel.getSelectedSet().isEmpty()));
	}

	private void createInitialDate() {
		// Get first day of previus month
		findingDate = DateUtils.addMonths2Date(findingDate, -1);
		findingDate = DateUtils.getFirstDayOfMonth(findingDate);
		findingDateCRA = DateUtils.addMonths2Date(findingDateCRA, -1);
		findingDateCRA = DateUtils.getFirstDayOfMonth(findingDateCRA);
	}

	private void initEnterpriseSB() {
		// Enteprise List
		List<String> enterprises = new ArrayList<>(mainCRAObjectNew.getEnterprisesMap().values());
		List<String> enterprisesSuggest = new ArrayList<>();
		for (String enterprise : enterprises)
			enterprisesSuggest.add(enterprise + "");

		MultiWordSuggestOracle orclEnterprise = (MultiWordSuggestOracle) enterpriseSB.getSuggestOracle();
		orclEnterprise.addAll(enterprisesSuggest);
		enterpriseSB.setAutoSelectEnabled(false);

		enterpriseSB.addKeyUpHandler(e -> {
			String value = enterpriseSB.getValue();
			if (AonStringUtils.isBlank(value) || value.length() < 3) {
				mainCRAObjectNew.resetEnterpriseCCCList();
			} else {
				List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
				mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
			}
			initCCCsTable();
		});

		enterpriseSB.addSelectionHandler(e -> {
			String value = enterpriseSB.getValue();
			List<Integer> enterprisesIds = mainCRAObjectNew.getEnterprisesIds(value);
			mainCRAObjectNew.filterEnterpriseCCCListByEnterprise(enterprisesIds);
			initCCCsTable();
		});
	}

	private void setInitialLBAndCBSelected() {
		setSelectedValueLB(this.year, DateUtils.getYear(findingDate) + "");
		setSelectedValueLB(this.month, DateUtils.getMonth(findingDate) + "");
		setSelectedValueLB(this.yearTillT, DateUtils.getYear(findingDateCRA) + "");
		setSelectedValueLB(this.monthTillT, DateUtils.getMonth(findingDateCRA) + "");

		this.allCCCsCB.setValue(false);
		this.emitCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(true);
	}

	// --------------------------------------------------------------------------------------------
	// INIT CCCs TABLE
	// --------------------------------------------------------------------------------------------

	private void initCCCsTable() {
		// Show buttons
		this.exportButton.setVisible(true);

		// Create a data provider.
		ListDataProvider<CCCInfo> dataProvider = new ListDataProvider<>();

		// Connect the table to the data provider.
		dataProvider.addDataDisplay(cccDataGrid);

		// Add the data to the data provider, which automatically pushes it to the
		// widget.
		dataProvider.getList().clear();
		List<CCCInfo> cccList = dataProvider.getList();

		this.cccs = this.mainCRAObjectNew.getEnterpriseCCCs();

		for (CCCInfo cccInfo : this.cccs)
			cccList.add(cccInfo);

		// Set page size
		cccDataGrid.setPageSize(cccs.size());

		addSortColums(cccList);

	}

	private void addSortColums(List<CCCInfo> cccInfoList) {
		ListHandler<CCCInfo> columnSortHandler = new ListHandler<>(cccInfoList);
		
		columnSortHandler.setComparator(cccDataGrid.getColumn(1), 
				(o1, o2) -> compareString(o1, o2, o1.getEnterpriseDesciption(), o2.getEnterpriseDesciption()));
		
		columnSortHandler.setComparator(cccDataGrid.getColumn(2), 
				(o1, o2) -> compareString(o1, o2, o1.getActivityDescription(), o2.getActivityDescription()));
		
		columnSortHandler.setComparator(cccDataGrid.getColumn(3), 
				(o1, o2) -> compareString(o1, o2, o1.getGeozone(), o2.getGeozone()));
		
		columnSortHandler.setComparator(cccDataGrid.getColumn(4), 
				(o1, o2) -> compareString(o1, o2, o1.getTypeStr(), o2.getTypeStr()));
		
		columnSortHandler.setComparator(cccDataGrid.getColumn(5), 
				(o1, o2) -> compareString(o1, o2, o1.getCcc(), o2.getCcc()));

		cccDataGrid.addColumnSortHandler(columnSortHandler);

		// We know that the data is sorted alphabetically by default.
		cccDataGrid.getColumn(2).setDefaultSortAscending(false);
		cccDataGrid.getColumnSortList().push(cccDataGrid.getColumn(2));
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
	
	private int compareByte(Object o1, Object o2, Byte d1, Byte d2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return d1.compareTo(d2);
	}

	// --------------------------------------------------------------------------------------------
	// INIT CRA TABLE
	// --------------------------------------------------------------------------------------------

	private void initCRATable() {
		ListDataProvider<CRA> dataProvider = new ListDataProvider<>();

		dataProvider.addDataDisplay(crasDataGrid);

		List<CRA> crasList = dataProvider.getList();
		crasList.clear();

		this.cras = this.mainCRAObjectNew.getFilteredCRAs();

		for (CRA cra : this.cras)
			crasList.add(cra);

		crasList.sort((o1, o2) -> compareDates(o1, o2, o2.getDate(), o1.getDate()));

		// Set page size
		crasDataGrid.setPageSize(cras.size());

		addSortCRAColums(crasList);
	}

	private void addSortCRAColums(List<CRA> crasList) {
		ListHandler<CRA> columnSortHandler = new ListHandler<>(crasList);

		columnSortHandler.setComparator(crasDataGrid.getColumn(0),
				(o1, o2) -> compareDates(o1, o2, o1.getDate(), o2.getDate()));

		columnSortHandler.setComparator(crasDataGrid.getColumn(1),
				(o1, o2) -> compareDates(o1, o2, o1.getCreationDate(), o2.getCreationDate()));

		columnSortHandler.setComparator(crasDataGrid.getColumn(2),
				(o1, o2) -> compareString(o1, o2, o1.getActivityName(), o2.getActivityName()));

		columnSortHandler.setComparator(crasDataGrid.getColumn(3),
				(o1, o2) -> compareString(o1, o2, o1.getCccProvince(), o2.getCccProvince()));

		columnSortHandler.setComparator(crasDataGrid.getColumn(4),
				(o1, o2) -> compareByte(o1, o2, o1.getCccType(), o2.getCccType()));

		columnSortHandler.setComparator(crasDataGrid.getColumn(5),
				(o1, o2) -> compareString(o1, o2, o1.getCcc(), o2.getCcc()));

		crasDataGrid.addColumnSortHandler(columnSortHandler);
	}

	// --------------------------------------------------------------------------------------------
	// UI HANDLERS
	// --------------------------------------------------------------------------------------------

	@UiHandler("allCCCsCB")
	public void onAllCCCsCB(ValueChangeEvent<Boolean> event) {
		if(Boolean.FALSE.equals(event.getValue()))
			return;
		
		this.emitCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(false);

		this.mainCRAObjectNew.resetEnterpriseCCCList();
		enterpriseSB.setText("");

		clearSelectionModel();
		initCCCsTable();
	}

	@UiHandler("emitCCCsCB")
	public void onEmitCCCsCB(ValueChangeEvent<Boolean> event) {
		if(Boolean.FALSE.equals(event.getValue()))
			return;
		
		this.allCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(false);

		this.mainCRAObjectNew.filterEmitedCCC(findingDate);
		enterpriseSB.setText("");

		clearSelectionModel();
		initCCCsTable();
	}

	@UiHandler("peddingCCCsCB")
	public void onPenddingCCCsCB(ValueChangeEvent<Boolean> event) {
		if(Boolean.FALSE.equals(event.getValue()))
			return;
		
		this.allCCCsCB.setValue(false);
		this.emitCCCsCB.setValue(false);

		this.mainCRAObjectNew.filterPenddingCCC(findingDate);
		enterpriseSB.setText("");

		clearSelectionModel();
		initCCCsTable();
	}

	@UiHandler({ "month", "year" })
	public void onMonthChange(ChangeEvent event) {
		findingDate = DateUtils.getDate(Integer.parseInt(month.getSelectedValue()), Integer.parseInt(year.getSelectedValue()));
		showLoading("Obteniendo CCCs para generar CRAs...");
		this.mainCRAObjectNew.getEnterprisesCCCInfo(findingDate.getTime(), 
				s -> {
					setInitialLBAndCBSelected();
					clearSelectionModel();
					initCCCsTable();
					hideMessage();
				},
				f -> {});
	}

	@UiHandler("typeList")
	public void onTypeListChange(ChangeEvent event) {
		if (0 == typeList.getSelectedIndex())
			this.mainCRAObjectNew.resetCRAsList();
		else {
			Byte type = Byte.parseByte(typeList.getSelectedValue());
			mainCRAObjectNew.filterCRAsListByType(type);
		}
		initCRATable();
	}

	@UiHandler("geozoneList")
	public void onGeozoneListChange(ChangeEvent event) {
		if (0 == geozoneList.getSelectedIndex())
			this.mainCRAObjectNew.resetCRAsList();
		else {
			String geozoneCode = geozoneList.getSelectedItemText();
			mainCRAObjectNew.filterCRAListByGeozone(geozoneCode);
		}
		initCRATable();
	}

	@UiHandler({ "geozoneList", "typeList" })
	public void onFilterChange(ChangeEvent event) {
		// GEOZONE
		String geozoneName = geozoneList.getSelectedItemText();

		// CCC TYPE
		Byte cccType = null;
		if (0 == typeList.getSelectedIndex())
			cccType = Byte.parseByte("-1");
		else
			cccType = Byte.parseByte(typeList.getSelectedValue());

		mainCRAObjectNew.filterCras(geozoneName, cccType);
		initCRATable();
	}

	@UiHandler({ "monthTillT", "yearTillT" })
	public void onFilterDatesChange(ChangeEvent event) {
		findingDateCRA = DateUtils.getDate(Integer.parseInt(monthTillT.getSelectedValue()), Integer.parseInt(yearTillT.getSelectedValue()));
		showLoading("Obteniendo CRAs generados...");
		mainCRAObjectNew.getCRAs(findingDateCRA.getTime(), 
				s -> {
					initCRATable();
					hideMessage();
				}, f -> {});
	}

	@UiHandler("collapsePanel")
	public void onOpenPanel(OpenEvent<DisclosurePanel> event) {
		crasPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 310), Unit.PX);
		crasDataGrid.redraw();
	}

	@UiHandler("collapsePanel")
	public void onClosePanel(CloseEvent<DisclosurePanel> event) {
		crasPanel.getElement().getStyle().setHeight((Window.getClientHeight() - 250), Unit.PX);
		crasDataGrid.redraw();
	}

	// --------------------------------------------------------------------------------------------
	// AUX METHODS
	// --------------------------------------------------------------------------------------------

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

	private void showCCCs() {
		deckPanel.showWidget(0);

		exportButton.setEnabled(false);
		exportButton.setVisible(true);
		listButton.setVisible(true);
		newCRAButton.setVisible(false);

		this.allCCCsCB.setValue(false);
		this.emitCCCsCB.setValue(false);
		this.peddingCCCsCB.setValue(true);
	}

	private void showCRAS() {
		deckPanel.showWidget(1);

		exportButton.setEnabled(false);
		exportButton.setVisible(false);
		listButton.setVisible(false);
		newCRAButton.setVisible(true);
	}

	private String parseCRAType(String type) {
		if (null == type)
			return "-";

		switch (type) {
		case "N":
			return "-";
		case "R":
			return "RECTIFICATIVO";
		default:
			return "-";
		}
	}

	public void clearSelectionModel() {
		this.selectionCCCInfoModel.clear();
		this.selectionCraModel.clear();
	}

	private String getCCCType(Byte type) {
		switch (type) {
		case (byte) 0:
			return "PRINCIPAL";
		case (byte) 1:
			return "FORMACION Y APRENDIZAJE";
		case (byte) 3:
			return "REPRESENTANTES DE COMERCIO";
		case (byte) 4:
			return "ASIMILADOS R.GENERAL";
		case (byte) 5:
			return "BECARIOS";
		case (byte) 6:
			return "EMPLEADOS DE HOGAR";
		case (byte) 7:
			return "TRABAJADOR CUENTA AJENA";
		case (byte) 8:
			return "ARTISTA";
		default:
			return "-";
		}
	}

	private AonToolbar getToolbarPanel() {

		this.toolbar = new AonToolbar("CRA - Conceptos Retributivos Abonados");

		listButton = new AonToolbarButton("Listar CRAs", AON.CSS.aonIconList());
		listButton.addClickHandler(e -> onListCras());
		toolbar.add(listButton);

		exportButton = new AonToolbarButton("Generar CRA", AON.CSS.aonIconTgssCra());
		exportButton.addClickHandler(e -> onExportButton());
		toolbar.add(exportButton);

		newCRAButton = new AonToolbarButton("Nuevo CRA", AON.CSS.aonIconAdd());
		newCRAButton.addClickHandler(e -> onNewCRA());
		toolbar.add(newCRAButton);

		return toolbar;

	}

	private void onListCras() {
		showLoading("Obteniendo CRAs generados...");
		this.mainCRAObjectNew.getCRAs(
				mainCRAObjectNew.getDefaultLiquidDate().getTime(), 
				s -> {
					showCRAS();
					initCRATable();
					hideMessage();
				}, 
				f -> {});
	}

	private void onNewCRA() {
		showLoading("Obteniendo CCCs para generar CRAs...");
		this.mainCRAObjectNew.getEnterprisesCCCInfo(findingDate.getTime(), s -> {
			initEnterpriseSB();
			showCCCs();
			initCCCsTable();
			clearSelectionModel();
			hideMessage();
		}, f -> {
		});
	}

	private void onExportButton() {
		if (!selectionCCCInfoModel.getSelectedSet().isEmpty()) {
			ArrayList<String> cccList = new ArrayList<>();
			ArrayList<Integer> cccIdList = new ArrayList<>();

			for (CCCInfo cccInfo : selectionCCCInfoModel.getSelectedSet()) {
				cccList.add(cccInfo.getCcc());
				cccIdList.add(cccInfo.getCccId());
			}

			ArrayList<CCCInfo> cccsSelected = new ArrayList<>();
			cccsSelected.addAll(selectionCCCInfoModel.getSelectedSet());
			Integer cccId = cccsSelected.get(0).getCccId();

			findingDate = DateUtils.getDate(Integer.parseInt(month.getSelectedValue()),
					Integer.parseInt(year.getSelectedValue()));
			findingDateCRA = DateUtils.copyDateOnly(findingDate);
			// -------------------------

			ArrayList<Integer> selectedCCCIdList = new ArrayList<>();
			for (CCCInfo cccInfo : selectionCCCInfoModel.getSelectedSet()) {
				selectedCCCIdList.add(cccInfo.getCccId());
			}

			mainCRAObjectNew.checkIfRectificative(findingDate, selectedCCCIdList, isRectificative -> {
				if (Boolean.TRUE.equals(isRectificative)) {
					AonConfirmDialog confirmDialog = new AonConfirmDialog();
					confirmDialog.confirm("AVISO: Rectificativo",
							"Ya existe un fichero CRA para esta cuenta de cotizaci\u00F3n en este periodo. Recuerde que puede eliminar de la tabla dicho fichero CRA. Si por lo contrario quiere generar un fichero CRA rectificativo puede acepte esta ventana."
									+ "\u00BFDesea generar un fichero rectificativo?",
							new AonConfirmDialogCallback() {

								@Override
								public void onAccept() {
									createNewCRARectificative(cccsSelected, cccList, selectedCCCIdList, cccId);
								}

								@Override
								public void onCancel() {
									// Cancel dialog
								}
								
							});

				} else {
					mainCRAObjectNew.checkCreateNewCRA(findingDate, cccIdList, p -> {
						if (AonStringUtils.isBlank(p)) {
							createNewCRA(cccsSelected, cccList, cccIdList, cccId);
						} else {
							AonConfirmDialog confirmDialog = new AonConfirmDialog();
							confirmDialog.confirm("AVISO", p, new AonConfirmDialogCallback() {

								@Override
								public void onAccept() {
									createNewCRA(cccsSelected, cccList, cccIdList, cccId);
								}

								@Override
								public void onCancel() {
									// Cancel dialog
								}
							});
						}
					}, f -> {
					});
				}
			}, f -> {
			});
		}
	}

	public void createNewCRA(ArrayList<CCCInfo> cccsSelected, ArrayList<String> cccList, ArrayList<Integer> cccIdList, Integer cccId) {
		showLoading("Generando CRA...");
		mainCRAObjectNew.createNewCRA(findingDate, cccList, cccIdList, cccId, "N", v -> {
			showSuccess("CRA", "CRA generado correctamente");
			if (AonStringUtils.isBlank(v)) {
				for (CCCInfo cccInfo : cccsSelected) {
					cccInfo.getCRADates().add(findingDate);
				}
				showCRAS();
				mainCRAObjectNew.getCRAs(findingDate.getTime(), a -> {
					initCRATable();
					setInitialLBAndCBSelected();
				}, b -> {
				});

			} else
				showError("Error CRA", v);
		}, f -> {
			showError("Error CRA", f.getMessage());
		});
	}

	public void createNewCRARectificative(ArrayList<CCCInfo> cccsSelected, ArrayList<String> cccList, ArrayList<Integer> cccIdList, Integer cccId) {
		showLoading("Generando CRA Rectificativo...");
		mainCRAObjectNew.createNewCRA(findingDate, cccList, cccIdList, cccId, "R", v -> {
			if (AonStringUtils.isBlank(v)) {
				showSuccess("CRA", "CRA Rectificativo generado correctamente");
				showInfo("INTRUCCIONES: CRA Rectificativo", "Debe enviar el CRA rectificativo que se ha generado en el historial de CRAs rectificativos, para anular el anterior y actualizar la informacion.");
				
				for (CCCInfo cccInfo : cccsSelected) {
					cccInfo.getCRADates().add(findingDate);
				}

				showCRAS();
				mainCRAObjectNew.getCRAs(findingDate.getTime(), s -> {
					initCRATable();
					setInitialLBAndCBSelected();
				}, f -> {
				});
			} else 
				showError("Error CRA Rectificativo", v);
		}, f -> {
		});
	}

	// --------------------------- MessagePanel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	private void showInfo(String title, String message) {
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put(title, message);
		AonMessagePanel.showInfo(messagePanel, infoMap);
	}

	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}

}
