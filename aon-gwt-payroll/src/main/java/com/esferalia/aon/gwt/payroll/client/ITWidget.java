package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.user.client.ui.FormPanel.METHOD_GET;
import static com.google.gwt.user.client.ui.FormPanel.METHOD_POST;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.code.aon.marketing.enumeration.NewsletterLayout;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.CustomDataGrid;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel.Task;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsEmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITEmployee;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDEmployeeIT;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDIT;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.Parameter;
import com.esferalia.aon.gwt.payroll.shared.StringEscapeUtils;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
//import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public abstract class ITWidget extends ResizeComposite {

	private static final Logger LOGGER = Logger.getLogger(ITWidget.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}
	// --------------------------------------------------- UiBinder

	private static ITWidgetUiBinder uiBinder = GWT.create(ITWidgetUiBinder.class);

	interface ITWidgetUiBinder extends UiBinder<Widget, ITWidget> {
	}

	interface TabLayoutFolderSafeTemplate extends SafeHtmlTemplates {
		@Template("<span class=\"aon_tab_label {1}\">{0}</span>")
		SafeHtml tab(String title, String icon);
	}

	private static final TabLayoutFolderSafeTemplate TABLAYOUT_FOLDER_TEMPLATE = GWT
			.create(TabLayoutFolderSafeTemplate.class);

	// ------------------------------------------------- ScheduledCommand (TGSS)

	class MsjFIECommand implements ScheduledCommand {

		@Override
		public void execute() {
			onFIEFileUpload();
		}
	}

	class TgssSyncroCommand implements ScheduledCommand {

		@Override
		public void execute() {
			SistemaREDITResults results = new SistemaREDITResults();

			AonMessagePanel.showLoading(messagePanel, "Sincronizando Its...");
			DateTimeFormat dateFormat = DateTimeFormat.getFormat(SistemaREDService.DATE_FORMAT);

			StringBuilder requestDataBuffer = new StringBuilder();

			requestDataBuffer.append("&" + Parameter.USER + "=" + Wnd.getCurrentUser());
			requestDataBuffer.append("&" + Parameter.DOMAIN + "=" + Wnd.getCurrentDomainNameURL());
			requestDataBuffer.append("&" + Parameter.START_DATE + "=" + dateFormat.format(ITWidget.this.startYear));
			requestDataBuffer.append("&" + Parameter.END_DATE + "=" + dateFormat.format(ITWidget.this.endYear));

			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open("POST", SistemaREDService.SISTEMA_RED_URL + "/" + SistemaREDService.ENTERPRISE_IT_STATUS_TEST);
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {

				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					String response = xhr.getResponseText();
					if (response.isEmpty()) {
						return;
					}
					try {
						JsArray<JsSistemaREDEmployeeIT> employeeITs = null;
						if (response.endsWith("]")) {
							employeeITs = eval("(" + response + ")");
						} else {
							employeeITs = eval("(" + response + "])");
						}
						JsSistemaREDEmployeeIT employeeIT = employeeITs.get(employeeITs.length() - 1);
						if (employeeIT.getItsInTgss().length() == 0) {
							AonMessagePanel.showLoading(messagePanel,SafeHtmlUtils.fromTrustedString("Actualizando las ITs del empleado "
											+ employeeIT.getName() + " , ninguna IT encontrada."));
						} else {
							AonMessagePanel.showLoading(messagePanel, SafeHtmlUtils.fromTrustedString(
									"Actualizando las ITs del empleado "
											+ employeeIT.getName() + " , ITs encontradas en TGSS: "
											+ employeeIT.getItsInTgss().length()));
						}

						if (employeeIT.getItsNotInAon().length() != 0) {
							showResultsPanel();
							showFootPanel();
							JsArray<JsSistemaREDIT> itsNotInAon = employeeIT.getItsNotInAon();
							for (int i = 0; i < itsNotInAon.length(); i++) {
								ItNotExist itNotExistInAon = new ItNotExist();
								JsSistemaREDIT sistemaRedIT = itsNotInAon.get(i);
								EmployeeIT employee = new EmployeeIT();

								employee.setCcc(sistemaRedIT.getCcc()).setDni(sistemaRedIT.getDni())
										.setName(sistemaRedIT.getName()).setNss(sistemaRedIT.getNss())
										.setRegime(sistemaRedIT.getRegime())
										.setType(ContractLeaveType.valueOf(sistemaRedIT.getType()))
										.setStartDate(dateFormat.parse(sistemaRedIT.getStartDate()));

								if (sistemaRedIT.getEndDate() != null) {
									employee.setEndDate(dateFormat.parse(sistemaRedIT.getEndDate()));
								}

								itNotExistInAon.setEmployeeIT(employee);
								results.itNotExist(itNotExistInAon);
							}
						}

						if (employeeIT.getItsNotInTgss().length() != 0) {
							showResultsPanel();
							showFootPanel();
							JsArray<JsSistemaREDIT> itsNotInTgss = employeeIT.getItsNotInTgss();
							for (int i = 0; i < itsNotInTgss.length(); i++) {
								ItNotExist itNotExistInTgss = new ItNotExist();
								JsSistemaREDIT sistemaRedIT = itsNotInTgss.get(i);
								EmployeeIT employee = new EmployeeIT();

								employee.setCcc(sistemaRedIT.getCcc()).setDni(sistemaRedIT.getDni())
										.setName(sistemaRedIT.getName()).setNss(sistemaRedIT.getNss())
										.setRegime(sistemaRedIT.getRegime())
										.setType(ContractLeaveType.valueOf(sistemaRedIT.getType()))
										.setId(Integer.parseInt(sistemaRedIT.getId()))
										.setStartDate(dateFormat.parse(sistemaRedIT.getStartDate()));

								if (sistemaRedIT.getEndDate() != null) {
									employee.setEndDate(dateFormat.parse(sistemaRedIT.getEndDate()));
								}

								itNotExistInTgss.setEmployeeIT(employee);
								results.itNotExist(itNotExistInTgss);
							}
						}

					} catch (Exception e) {
					}
					int state = xhr.getReadyState();
					if (state != XMLHttpRequest.DONE) {
						return;
					}

					Scheduler.get()
							.scheduleDeferred(() -> AonMessagePanel.showSuccess(messagePanel, "Its actualizadas"));
				}

			});

			xhr.send(requestDataBuffer.toString());

			resultsPanel.setWidget(results);
		}

	}

	class TGSSContextMenu extends ContextMenu {

		private MenuItem fie;
		private MenuItem tgssSyncro;

		public TGSSContextMenu() {

			fie = addItem("Mensaje/Fichero INSS (FIE)", new MsjFIECommand(), AON.CSS.aonIconTgss(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			fie.ensureDebugId("fie");

			tgssSyncro = addItem("Sincronizaci\u00f3n TGSS", new TgssSyncroCommand(), AON.CSS.aonIconTgss(),
					AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			tgssSyncro.ensureDebugId("tgssSyncro");
		}

	}

	// --------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String filterPanel();

		String flexPanel();

		String cmdBtn();

		String mt10();
	}

	@UiField
	DockLayoutPanel dockLayoutPanel;

	@UiField
	DeckPanel mainDeckPanel;

	@UiField
	HTMLPanel messagePanel;

	@UiField
	HTMLPanel filterITListPanel;

	@UiField
	DeckPanel deckPanel;

	@UiField
	HTMLPanel timelinePanel;

	@UiField
	DockLayoutPanel splitLayoutPanel;

	HTMLPanel itTablePanel;

	@UiField(provided = true)
	DataGrid<IT> itDataGrid;

	// --------------------------------------------------- Variables

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final String CONTRACT_EXP = "{\"type\":\"bar\"";

	private SuggestBox employeeSB;
	private ListBox dateListBox;
	private CheckBox allContracts;

	// --------------------------------------------------- DataGrid

	private List<IT> itsList = Collections.emptyList();

	// --------------------------------------------------- TimeLineChart.Variables

	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);

	private static final String ACTIVE = "Activo";

	private int selectedYear;
	private Date startYear;
	private Date endYear;

	private static LinkedList<LinkedList<Status>> myEmployees;

	private boolean ifNull; // Evitar el Null del Timeline

	private Map<Integer, ITEmployee> centineels;

	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();

	private TimeLineChart timelineChart;

	private ITTooltip tooltip;
	private DataTableWrapper data;

	private int posCell; // vR
	private int posColumn; // uR

	private ExpressionCallback expressionCallback;
	private TooltipCallBack tooltipCallback;

	private String cadenaTooltip;

	private PopupPanel popupPanel;

	// --------------------------------------------------- Toolbar.Variables

	private AonToolbar toolbar;
	private AonToolbarButton showList;
	private AonToolbarButton showStatics;
	private AonToolbarButton leyend;

	private FormPanel msjFIEFormPanel;
	private SubmitButton msjFIESubmitButton;

	private MultiFileUpload msjFIEFileUpload;

	private TGSSContextMenu tgssContextMenu;

	private boolean minimizedByUser;

	private AonMinimizePanel footPanel;

	private FlowPanel sessionLog;
	private ResultsPanel resultsPanel;

	private TabLayoutPanel tabLayout;

	private List<ITEmployee> itEmployeeIts;

	private ProgressPanel progressPanel;

	private ITDialog itDialogEdit;

	// --------------------------------------------------- Constructor

	protected ITWidget() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();

		provideITsDataGrid();

		initWidget(uiBinder.createAndBindUi(this));

		getToolbarPanel();
		dockLayoutPanel.addNorth(toolbar, AonToolbar.HEIGTH);
		tgssContextMenu = new TGSSContextMenu();

		initFootPanel();

		showResultsPanel();

		splitLayoutPanel.setHeight((Window.getClientHeight() - 150) + "px");
		mainDeckPanel.showWidget(0);
	}

	// --------------------------------------------------- provideITsDataGrid

	private void provideITsDataGrid() {
		itsList = Collections.emptyList();

		itDataGrid = new CustomDataGrid<>(Integer.MAX_VALUE, IT.KEY_PROVIDER);

		itDataGrid.setAutoHeaderRefreshDisabled(true);

		itDataGrid.setEmptyTableWidget(new Label("No existen its".toUpperCase()));

		NoSelectionModel<IT> selectionITModel = new NoSelectionModel<>(IT.KEY_PROVIDER);
		itDataGrid.setSelectionModel(selectionITModel);

		addITInfoColumns(selectionITModel);

		new ListDataProvider<IT>(Collections.emptyList()).addDataDisplay(itDataGrid);
	}

	private void addITInfoColumns(NoSelectionModel<IT> selectionITModel) {
		selectionITModel.addSelectionChangeHandler(event -> {
			IT itSelected = selectionITModel.getLastSelectedObject();
			openITDialog(itSelected.getContract(), itSelected.getId());
		});

		// Add Selection Column to table
		itDataGrid.setSelectionModel(selectionITModel);

		// Columns
		TextColumn<IT> employeeNameColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				return it.getFullName();
			}

			@Override
			public void render(Context context, IT it, SafeHtmlBuilder sb) {
				if (null != it) {
					// it empiez post contrato ROJO
					if (checkOutOfContractA(it))
						sb.appendHtmlConstant(
								"<div style=\"font-weight: bold !important; color: red;\" title=\"IT posterior a la fecha fin contrato\">"
										+ it.getFullName() + "</div>");
					// it fin antes tipo contrato NARANJA
					else if (checkOutOfContractB(it))
						sb.appendHtmlConstant(
								"<div style=\"font-weight: bold !important; color: orange;\" title=\"IT anterior a la fecha inicio contrato\">"
										+ it.getFullName() + "</div>");
					// it fin antes tipo contrato y fini post inicio contrato VERDE
					else if (checkOutOfContractBtw(it))
						sb.appendHtmlConstant(
								"<div style=\"font-weight: bold !important; color: green;\" title=\"IT abierta sin fecha alta o fecha alta posterior al fin del contrato\">"
										+ it.getFullName() + "</div>");
					else
						super.render(context, it, sb);
				} else
					super.render(context, it, sb);
			}
		};

		employeeNameColumn.setSortable(true);

		TextColumn<IT> itStartColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				return formatFullDate.format(it.getStartDate());
			}
		};

		itStartColumn.setSortable(true);
		itStartColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		itDataGrid.setColumnWidth(itStartColumn, 10, Unit.PCT);

		TextColumn<IT> startCauseColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				return parseLowCause(it.getTypeLowPart());
			}
		};

		startCauseColumn.setSortable(true);
		startCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		TextColumn<IT> itEndColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				Date endDate = it.getEndDate();
				return null == endDate ? "" : formatFullDate.format(endDate);
			}
		};

		itEndColumn.setSortable(true);
		itEndColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		itDataGrid.setColumnWidth(itEndColumn, 10, Unit.PCT);

		TextColumn<IT> endCauseColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				return parseHighCause(it.getTypeHighPart());
			}
		};

		endCauseColumn.setSortable(true);
		endCauseColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		TextColumn<IT> startColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				return formatFullDate.format(it.getContractStartDate());
			}
		};

		startColumn.setSortable(true);
		startColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		itDataGrid.setColumnWidth(startColumn, 10, Unit.PCT);

		TextColumn<IT> endColumn = new TextColumn<IT>() {
			@Override
			public String getValue(IT it) {
				Date endDate = it.getContractEndDate();
				return null == endDate ? "" : formatFullDate.format(endDate);
			}
		};

		endColumn.setSortable(true);
		endColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		itDataGrid.setColumnWidth(endColumn, 10, Unit.PCT);

		// Add the columns.
		itDataGrid.addColumn(employeeNameColumn, "Trabajador");
		itDataGrid.addColumn(itStartColumn, "F. Baja");
		itDataGrid.addColumn(startCauseColumn, "Causa Baja");
		itDataGrid.addColumn(itEndColumn, "F. Alta");
		itDataGrid.addColumn(endCauseColumn, "Causa Alta");
		itDataGrid.addColumn(startColumn, "Inicio Contrato");
		itDataGrid.addColumn(endColumn, "Fin Contrato");

	}

	private void initITTable() {
		ListDataProvider<IT> dataProvider = new ListDataProvider<>();

		dataProvider.addDataDisplay(itDataGrid);

		List<IT> itInfoList = dataProvider.getList();
		itInfoList.clear();

		for (IT it : this.itsList)
			itInfoList.add(it);

		itDataGrid.setPageSize(itsList.size());

		addSortColums(itDataGrid, itInfoList);
	}

	private void addSortColums(DataGrid<IT> dataGrid, List<IT> itList) {

		ListHandler<IT> columnSortHandler = new ListHandler<>(itList);

		columnSortHandler.setComparator(dataGrid.getColumn(0),
				(o1, o2) -> compareString(o1, o2, o1.getFullName(), o2.getFullName()));

		columnSortHandler.setComparator(dataGrid.getColumn(1),
				(o1, o2) -> compareDates(o1, o2, o1.getStartDate(), o2.getStartDate()));

		columnSortHandler.setComparator(dataGrid.getColumn(2), (o1, o2) -> compareString(o1, o2,
				parseLowCause(o1.getTypeLowPart()), parseLowCause(o2.getTypeLowPart())));

		columnSortHandler.setComparator(dataGrid.getColumn(3),
				(o1, o2) -> compareDates(o1, o2, o1.getEndDate(), o2.getEndDate()));

		columnSortHandler.setComparator(dataGrid.getColumn(4), (o1, o2) -> compareString(o1, o2,
				parseHighCause(o1.getTypeHighPart()), parseHighCause(o2.getTypeHighPart())));

		columnSortHandler.setComparator(dataGrid.getColumn(5),
				(o1, o2) -> compareDates(o1, o2, o1.getContractStartDate(), o2.getContractStartDate()));

		columnSortHandler.setComparator(dataGrid.getColumn(6),
				(o1, o2) -> compareDates(o1, o2, o1.getContractEndDate(), o2.getContractEndDate()));

		// We know that the data is sorted alphabetically by default.
		dataGrid.getColumn(1).setDefaultSortAscending(false);
		dataGrid.getColumnSortList().push(dataGrid.getColumn(1));

		dataGrid.addColumnSortHandler(columnSortHandler);

	}

	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2)
			return 0;
		else if (o1 == null)
			return -1;
		else if (o2 == null)
			return 1;
		else
			return s1.compareTo(s2);
	}

	private int compareDates(Object o1, Object o2, Date d1, Date d2) {
		if (o1 == o2 || d1 == d2)
			return 0;
		else if (o1 == null || d1 == null)
			return -1;
		else if (o2 == null || d2 == null)
			return 1;
		else
			return d1.compareTo(d2);
	}

	// it empiez post contrato ROJO
	private boolean checkOutOfContractA(IT it) {
		return null != it.getContractEndDate() && it.getStartDate().after(it.getContractEndDate());
	}

	// it fin antes tipo contrato NARANJA
	private boolean checkOutOfContractB(IT it) {
		return null != it.getEndDate() && it.getEndDate() != it.getContractStartDate()
				&& it.getEndDate().before(it.getContractStartDate());
	}

	// it fin antes tipo contrato y fini post inicio contrato VERDE
	private boolean checkOutOfContractBtw(IT it) {
		return it.getStartDate().after(it.getContractStartDate()) && (null == it.getEndDate()
				|| (null != it.getContractEndDate() && it.getEndDate().after(it.getContractEndDate())));
	}

	private String parseLowCause(Byte typeLowPart) {
		switch (typeLowPart) {
		case (byte) 0:
			return "Enfermedad Com\u00FAn";
		case (byte) 1:
			return "Accidente de trabajo";
		case (byte) 2:
			return "Maternidad";
		case (byte) 3:
			return "Paternidad";
		case (byte) 4:
			return "Riesgo para el embarazo";
		case (byte) 5:
			return "Riesgo durante la lactancia";
		case (byte) 6:
			return "Accidente no laboral";
		case (byte) 7:
			return "Enfermedad com\u00FAn periodo de carencia";
		case (byte) 8:
			return "Enfermedad com\u00FAn, prestaci\u00F3n profesional (COVID-19)";
		case (byte) 9:
			return "Periodo de Observaci\u00f3n por Enfermedad Profesional";
		default:
			return "";
		}
	}

	private String parseHighCause(Byte typeHighPart) {
		if (null == typeHighPart)
			return "";

		switch (typeHighPart) {
		case (byte) 0:
			return "Curaci\u00F3n";
		case (byte) 1:
			return "Fallecimiento";
		case (byte) 2:
			return "Inspecci\u00F3n m\u00E9dica";
		case (byte) 3:
			return "Propuesta incapacidad";
		case (byte) 4:
			return "Agotamiento de plazo";
		case (byte) 5:
			return "Mejor\u00EDa que permite realizar el trabajo habitual";
		case (byte) 6:
			return "Incomparecencia";
		case (byte) 7:
			return "Control INSS duraci\u00F3n 12 meses";
		case (byte) 8:
			return "Recuperaci\u00F3n capacidad profesional";
		case (byte) 9:
			return "Incomparecencia contratos de formaci\u00F3n";
		default:
			return "";
		}
	}

	// ---------------------------------------------------
	// TimeLineChart.MouseEventsHandlers

	private class MouseEventsHandlers extends DecoratedPopupPanel
			implements MouseOverHandler, ContextMenuHandler, ClickHandler {

		public MouseEventsHandlers(TimeLineChart timelineChart) {
			timelineChart.addMouseOverHandler(this);
			timelineChart.addContextMenuHandler(this);
			timelineChart.addClickHandler(this);
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			Element element = Element.as(event.getNativeEvent().getEventTarget());
			int mouseClientX = event.getClientX();
			int mouseClientY = event.getClientY();
			cadenaTooltip = getLogicalName(element, mouseClientX, mouseClientY);

			if (AonStringUtils.isBlank(cadenaTooltip))
				return;

			try {

				if (AonStringUtils.isNotBlank(cadenaTooltip)
						&& AonStringUtils.containsIgnoreCase(cadenaTooltip, CONTRACT_EXP)) {

					tratarContrato();

					int contractId = data.getContractId(posColumn, posCell);
					int leaveId = data.getContractLeaveId(posColumn, posCell);

					if (AonNumberUtils.equals(contractId, leaveId))
						checkTooltipCB();
					else {
						tooltipCallback.setProperties(mouseClientX, mouseClientY);
						evalTooltip();
					}
				} else
					checkTooltipCB();

			} finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}

		}

		@Override
		public void onContextMenu(ContextMenuEvent event) {

			Element element = Element.as(event.getNativeEvent().getEventTarget());
			int mouseClientX = event.getNativeEvent().getClientX();
			int mouseClientY = event.getNativeEvent().getClientY();
			cadenaTooltip = getLogicalName(element, mouseClientX, mouseClientY);

			if (AonStringUtils.isBlank(cadenaTooltip))
				return;

			try {
				if (isLeaveEmployee(cadenaTooltip)) {

					checkTooltipCB();

					tratarContrato();

					int leaveId = data.getContractLeaveId(posColumn, posCell);
					IT it = getIT(leaveId);

					if (!isUserComunica() && itIsNotComunicate(it)) {
						popupPanel = new PopupPanel(true);
//						new ITContextMenu();					
						popupPanel.setPopupPosition(event.getNativeEvent().getClientX(),
								event.getNativeEvent().getClientY());
						popupPanel.show();
					}
				}

			} finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}
		}

		@Override
		public void onClick(ClickEvent event) {

			Element element = Element.as(event.getNativeEvent().getEventTarget());
			int mouseClientX = event.getClientX();
			int mouseClientY = event.getClientY();
			cadenaTooltip = getLogicalName(element, mouseClientX, mouseClientY);

			if (AonStringUtils.isBlank(cadenaTooltip))
				return;

			try {

				checkTooltipCB();

				if (AonStringUtils.isNotBlank(cadenaTooltip)
						&& AonStringUtils.containsIgnoreCase(cadenaTooltip, CONTRACT_EXP)) {

					tratarContrato();

					int contractId = data.getContractId(posColumn, posCell);
					int leaveId = data.getContractLeaveId(posColumn, posCell);

					if (AonNumberUtils.equals(contractId, leaveId))
						openNewITDialog(contractId);
					else
						openITDialog(contractId, leaveId);
				}

			} finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}
		}

		private void evalTooltip() {
			tooltipCallback.cancel();
			tooltipCallback.schedule(750);
		}

		private void tratarContrato() {
			getPosStatusEmployee();
		}

		private boolean itIsNotComunicate(IT it) {
			return null == it.isComunicate() || !it.isComunicate();
		}

		private void checkTooltipCB() {
			if (tooltipCallback.isRunning())
				tooltipCallback.cancel();
			tooltip.hide();
		}

		private void openNewITDialog(int contractId) {
			ITEmployee itEmployee = getITEmployee(contractId);
			ITDialog itDialog = newITDialog();
			itDialog.setITEmployee(itEmployee);
		}

	}

	// --------------------------------------------------- ExpressionCallback

	class ExpressionCallback extends Timer {
		@Override
		public void run() {
			String container = employeeSB.getText().toUpperCase();

			if (AonStringUtils.isEmpty(container) || container.length() > 2)
				reloadTimeline();
		}
	}

	// --------------------------------------------------- TooltipCallback

	class TooltipCallBack extends Timer {

		private int mouseClientX;
		private int mouseClientY;

		public TooltipCallBack() {
			super();
		}

		public void setProperties(int clientX, int clientY) {
			this.mouseClientX = clientX;
			this.mouseClientY = clientY;
		}

		@Override
		public void run() {
			int contractId = data.getContractId(posColumn, posCell);
			int itId = data.getContractLeaveId(posColumn, posCell);

			IT itInfo = getIT(itId);

			ITEmployee itEmployee = getITEmployee(contractId);
			ITDialogObject itDialogObject = new ITDialogObject(itEmployee);

			Optional<ITPart> bjOptional = itDialogObject.getITBaja(itInfo);
			Optional<ITPart> altaOptional = itDialogObject.getITAlta(itInfo);

			tooltip.setFullName(itEmployee.getEmployeeInfo().getFullName());
			tooltip.setDocument(itEmployee.getEmployeeInfo().getDocument());
			tooltip.setNaf(itEmployee.getEmployeeInfo().getSsNumber());
			tooltip.setCompleteCCC(itEmployee.getContractInfo().getCompleteCCC());
			tooltip.setComunicationBaja(bjOptional.isPresent() && bjOptional.get().getStatus().equals((byte) 3));
			tooltip.setComunicationAlta(altaOptional.isPresent() && altaOptional.get().getStatus().equals((byte) 3));
			tooltip.setITType(itInfo.getTypeLowPart());
			tooltip.setLowType(itInfo.getTypeLowPart());
			tooltip.setHighType(itInfo.getTypeHighPart());
			tooltip.setStartDate(getRealStartDate(itInfo));
			tooltip.setEndDate(itInfo.getEndDate());

			tooltip.setContractId(contractId);
			tooltip.setITId(itId);

			tooltip.showTooltip(mouseClientX, mouseClientY);
		}

	}

	public void loadITWidget(Runnable success, Consumer<Throwable> failure) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo ITs de los trabajadores...");

		getITEmployeeListDB(itEmployeeList -> {
			if (itEmployeeList.isEmpty()) {
				showMessage();
			} else {
				itEmployeeIts = itEmployeeList;
				int deckIdx = mainDeckPanel.getVisibleWidget();
				if (0 == deckIdx)
					showStatics();
				else
					showList();
			}

			AonMessagePanel.hideMessage(messagePanel);

			success.run();
		}, f -> {
		});
	}

	// --------------------------------------------------- ContextMenu

	// --------------------------------------------------- OnModuleLoad

	public void loadITWidget() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo ITs de los trabajadores...");

		getITEmployeeListDB(itEmployeeList -> {
			if (itEmployeeList.isEmpty()) {
				showMessage();
			} else {
				itEmployeeIts = itEmployeeList;
				int deckIdx = mainDeckPanel.getVisibleWidget();
				if (0 == deckIdx)
					showStatics();
				else
					showList();
			}

			AonMessagePanel.hideMessage(messagePanel);
		}, f -> {
		});
	}

	private void loadITStatics() {
		this.expressionCallback = new ExpressionCallback();
		this.tooltipCallback = new TooltipCallBack();
		this.popupPanel = new PopupPanel(true);
		this.tooltip = new ITTooltip() {

			@Override
			protected void onTooltipClick(Integer contractId, Integer itId) {
				openITDialog(contractId, itId);
			}
		};

		AonMessagePanel.hideMessage(messagePanel);
		getFilterITListPanel();
		initDateListBox();
		initSuggestBox();
		printTimelineChart();

		// By now this doesn't work
		// checkStatusITs();
	}

	private void loadITsList() {
		itsList = getITsList();
		initITTable();
		setTableHeights();
	}

	private void setTableHeights() {
		itDataGrid.getElement().getStyle().setHeight(Window.getClientHeight() - 180.00, Unit.PX);
	}

	private void getFilterITListPanel() {
		filterITListPanel.clear();
		filterITListPanel.setStyleName(AON.CSS.aonSearchPanel());
		filterITListPanel.addStyleName(AON.CSS.aonScrollArea());
		filterITListPanel.addStyleName(AON.CSS.aonMarginBottom());
		filterITListPanel.addStyleName(AON.CSS.aonMarginLeft());
		filterITListPanel.addStyleName(AON.CSS.aonMarginRight());
		filterITListPanel.addStyleName(AON.CSS.aonBlockCenter());
		filterITListPanel.addStyleName(style.mt10());

		HTMLPanel filterPanel = new HTMLPanel("");
		filterPanel.addStyleName(style.filterPanel());

		HTMLPanel itPanel = new HTMLPanel("");
		itPanel.addStyleName(style.flexPanel());
		Label itL = new Label("Trabajador : ");
		itL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		itL.getElement().getStyle().setMarginRight(10, Unit.PX);
		itPanel.add(itL);
		employeeSB = new SuggestBox(names);
		employeeSB.setWidth("300px");
		employeeSB.getElement().getStyle().setMarginRight(10, Unit.PX);
		itPanel.add(employeeSB);
		AonTableButton cleanSB = new AonTableButton("Limpiar", AON.CSS.aonIconClear());
		cleanSB.addClickHandler(e -> employeeSB.setValue("", true));
		itPanel.add(cleanSB);

		HTMLPanel showPanel = new HTMLPanel("");
		showPanel.addStyleName(style.flexPanel());
		Label showL = new Label("Mostrar ITs : ");
		showL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		showL.getElement().getStyle().setMarginRight(5, Unit.PX);
		showPanel.add(showL);
		dateListBox = new ListBox();
		dateListBox.getElement().getStyle().setMarginRight(5, Unit.PX);
		showPanel.add(dateListBox);

		Label allContractsL = new Label("Contratos con IT");
		allContractsL.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		allContracts = new CheckBox();
		allContracts.setValue(true);
		allContracts.addValueChangeHandler(e -> {
			initSuggestBox();
			reloadTimeline();
		});
		showPanel.add(allContractsL);
		showPanel.add(allContracts);

		filterPanel.add(itPanel);
		filterPanel.add(showPanel);

		filterITListPanel.add(filterPanel);
	}

	private void initDateListBox() {
		dateListBox.clear();

		dateListBox.addItem("\u00daltimos 12 meses", "0");
		for (Integer year : getAviableYears())
			dateListBox.addItem(String.valueOf(year));

		dateListBox.setSelectedIndex(0);

		selectedYear = 0;
		Date date = new Date();
		endYear = DateUtils.getLastDayOfMonth(date);
		startYear = DateUtils.copyDateOnly(endYear);
		startYear = DateUtils.getFirstDayOfMonth(DateUtils.addYears2Date(startYear, -1));

		dateListBox.addChangeHandler(e -> {
			try {
				selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox.getSelectedIndex()));
			} catch (Exception ex) {
				selectedYear = 0;
			}

			if (AonNumberUtils.equals(selectedYear, 0)) {
				Date currentDate = new Date();
				endYear = DateUtils.getLastDayOfMonth(currentDate);
				startYear = DateUtils.copyDateOnly(endYear);
				startYear = DateUtils.getFirstDayOfMonth(DateUtils.addYears2Date(startYear, -1));
			} else {

				startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0, selectedYear));

				// Check if selected year is current year
				int currentYear = DateUtils.getYear();
				if (AonNumberUtils.equals(selectedYear, currentYear)) {
					Date nextMonth = DateUtils.addMonths2Date(new Date(), 1);
					endYear = DateUtils.getLastDayOfMonth(nextMonth);
				} else
					endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11, selectedYear));
			}

			initSuggestBox();
			reloadTimeline();
		});
	}

	private final void initSuggestBox() {
		names.clear();

		centineels = new LinkedHashMap<>();

		Date startYearAux = null;
		Date endYearAux = null;
		if (AonNumberUtils.equals(selectedYear, 0)) {
			startYearAux = DateUtils.copyDateOnly(startYear);
			endYearAux = DateUtils.getLastDayOfMonth(endYear);
		} else {
			startYearAux = DateUtils.getFirstDayOfYear(DateUtils.getDate(0, selectedYear));
			endYearAux = DateUtils.getLastDayOfYear(DateUtils.getDate(11, selectedYear));
		}

		for (ITEmployee itEmployee : getFilterITEmployeeList(allContracts.getValue(), startYearAux, endYearAux)) {

			int contractId = itEmployee.getContractInfo().getContractId();

			if ((DateUtils.compare(itEmployee.getContractInfo().getStartDate(), endYearAux) <= 0)
					&& (DateUtils.compare(itEmployee.getContractInfo().getEndDate(), startYearAux) >= 0)) {

				names.add(itEmployee.getEmployeeInfo().getFullName());
				centineels.put(contractId, itEmployee);
			}
		}

		if (centineels.isEmpty())
			showMessage();
		else
			showTimeLine();

		employeeSB.addValueChangeHandler(e -> {
			expressionCallback.cancel();
			expressionCallback.schedule(1500);
		});
	}

	private final void printTimelineChart() {
		Runnable onLoadCallback = () -> {
			try {
				data = new DataTableWrapper();
				AbstractDataTable dataTable = createTable();
				Options options = createOptions(dataTable);
				timelineChart = new TimeLineChart(dataTable, options);

				if (ifNull)
					timelinePanel.clear();
				else {
					timelinePanel.clear();
					timelinePanel.add(timelineChart);
					new MouseEventsHandlers(timelineChart);
				}

			} catch (Exception ex) {
				Window.alert(ex + " Se ha producido un error, printTimelineChart");
			}
		};

		VisualizationUtils.loadVisualizationApi(onLoadCallback, TimeLineChart.PACKAGE);
	}

	// --------------------------------------------------- TimeLineChart.Methods

	private final void reloadTimeline() {
		AbstractDataTable dataTable = createTable();
		Options options = createOptions(dataTable);
		timelineChart = new TimeLineChart(dataTable, options);
		timelinePanel.clear();
		timelinePanel.add(timelineChart);
		new MouseEventsHandlers(timelineChart);
	}

	private Options createOptions(AbstractDataTable dataTable) {
		Options options = Options.create();

		options.setWidth(deckPanel.getOffsetWidth() - 20);
		options.setHeight(Window.getClientHeight() - 240);

		Timeline timeline = Timeline.create();
		options.setAvoidOverlappingGridLines(false);
		timeline.setGroupByRowLabel(true);
		timeline.setShowBarLabels(false);

		BarLabelStyle barLabelStyle = BarLabelStyle.create();
		barLabelStyle.setFontName("Arial");
		barLabelStyle.setFontSize("10");
		barLabelStyle.setColor("#4b4b4b");

		RowLabelStyle rowStyle = RowLabelStyle.create();
		rowStyle.setFontName("Arial");
		rowStyle.setFontSize("10");
		rowStyle.setColor("#4b4b4b");

		timeline.setRowLabelStyle(rowStyle);
		timeline.setBarLabelStyle(barLabelStyle);

		options.setTimeline(timeline);

		List<String> statusList = new LinkedList<>();

		for (int row = 0; row < dataTable.getNumberOfRows(); row++) {
			String status = dataTable.getValueString(row, 1);
			if (!statusList.contains(status))
				statusList.add(status);
		}

		int i = 0;
		String[] colors = new String[statusList.size()];
		for (String status : statusList)
			colors[i++] = getColor(status);

		options.setColors(colors);

		options.setEnableInteractivity(false);

		return options;
	}

	private final AbstractDataTable createTable() {
		String container = employeeSB.getText().toUpperCase();

		String[] patterns = AonStringUtils.split(container, '|');

		data = new DataTableWrapper();
		ifNull = true;

		for (ITEmployee itEmployee : centineels.values()) {

			String fullName = itEmployee.getEmployeeInfo().getFullName();

			// if (fullName.contains(container)) {
			if (patterns == null || patterns.length == 0
					|| Arrays.stream(patterns).anyMatch(p -> AonStringUtils.contains(fullName, p))) {

				ifNull = false;

				Date start = itEmployee.getContractInfo().getStartDate();
				Date end = itEmployee.getContractInfo().getEndDate();

				int contractId = itEmployee.getContractInfo().getContractId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (!itEmployee.getIts().isEmpty())
					addLeaveRows(itEmployee, contractId, start, end);

				else
					data.addRow(itEmployee.getEmployeeInfo().getFullName(), ACTIVE, start, end, contractId, contractId);
			}
		}

		return data.getDataTable();
	}

	private final void addLeaveRows(ITEmployee itEmployee, int contractId, Date start, Date end) {

		Date leaveStart = null;
		Date leaveEnd = null;

		for (IT it : itEmployee.getIts()) {

			if ((DateUtils.compare(it.getStartDate(), endYear) > 0)
					|| (DateUtils.compare(it.getEndDate(), startYear) < 0))
				continue;

			Byte type = it.getTypeLowPart();
			Date leaveEndAux = it.getEndDate();

			if (leaveEndAux == null)
				leaveEndAux = end;

			Date itStartDate = getRealStartDate(it);

			leaveStart = DateUtils.after(itStartDate, startYear);
			leaveEnd = DateUtils.before(leaveEndAux, endYear);

			int contractLeaveId = it.getId();

			// Skip previous IT
			if (start.before(leaveStart))
				data.addRow(itEmployee.getEmployeeInfo().getFullName(), ACTIVE, start, leaveStart, contractId,
						contractId);

			data.addRow(itEmployee.getEmployeeInfo().getFullName(), getTypeDescription(type), leaveStart, leaveEnd,
					contractId, contractLeaveId);

			start = leaveEnd;

		}

		if (!DateUtils.equals(start, end))
			data.addRow(itEmployee.getEmployeeInfo().getFullName(), ACTIVE, start, end, contractId, contractId);
	}

	// ---------------------------------------------------
	// TimeLineChart.Auxiliar_Methods

	private static <T extends JavaScriptObject> T parseJson(String json) {
		return JsonUtils.safeEval(json);
	}

	private void getPosStatusEmployee() {

		JSONObject json = new JSONObject(parseJson(cadenaTooltip));
		String values = json.get("data").toString();
		JSONObject datas = new JSONObject(parseJson(values));

		Iterator<String> iterator = datas.keySet().iterator();

		String column = iterator.next();
		posColumn = Integer.parseInt(datas.get(column).toString());

		String cell = iterator.next();
		posCell = Integer.parseInt(datas.get(cell).toString());

	}

	protected boolean isLeaveEmployee(String pElement) {
		String cadena = pElement;
		getPosStatusEmployee();

		return AonStringUtils.containsIgnoreCase(cadena, CONTRACT_EXP)
				&& !AonStringUtils.equals(data.getStatus(posColumn, posCell), ACTIVE);
	}

	private static String getColor(String status) {
		return getColor(getType(status));
	}

	private static ITDataPerson.Type getType(String description) {
		for (ITDataPerson.Type type : Type.values())
			if (AonStringUtils.equals(type.getDescription(), description))
				return type;
		return null;
	}

	private Date getRealStartDate(IT it) {
		if ((byte) 1 == it.getTypeLowPart()) {
			Date realStartDate = DateUtils.copyDateOnly(it.getStartDate());
			return DateUtils.addDays2Date(realStartDate, -1);
		}

		return it.getStartDate();
	}

	public static String getColor(Type type) {
		if (type == null)
			return "#A0C3FF";

		switch (type) {
		case MATERNITY:
		case PREGNANCY_RISK:
		case BREASTFEEDING_RISK:
			return "#FF66CC";
		case PATERNITY:
			return "#36C";
		case OCCUPATIONAL_DISEASE:
			return "#AA0033";
		case COMMON_OCCUPATIONAL_DISEASE:
			return "#E3DC14";
		case OCCUPATIONAL_DISEASE_OBSERVATION:
			return "#f6788e";
		default:
			return "#FFA500";
		}
	}

	private String getTypeDescription(Byte type) {
		switch (type) {
		case (byte) 0:
			return "Enfermedad Com\u00FAn";
		case (byte) 1:
			return "Enfermedad Profesional";
		case (byte) 2:
			return "Maternidad";
		case (byte) 3:
			return "Paternidad";
		case (byte) 4:
			return "Riesgo Durante Embarazo";
		case (byte) 5:
			return "Lactancia Materna";
		case (byte) 6:
			return "Enfermedad No Profesional";
		case (byte) 7:
			return "Enfermedad Com\u00FAn Periodo de Carencia";
		case (byte) 8:
			return "Enfermedad Com\u00FAn, Prestaci\u00F3n Profesional (COVID-19)";
		case (byte) 9:
			return "Periodo de Observaci\u00f3n por Enfermedad Profesional";
		default:
			return "-";
		}
	}

	// ---------------------------------------------------
	// TimeLineChart.DataTableWrapper

	public static class DataTableWrapper {

		private int row;
		private DataTable data;
		private LinkedList<Status> statusList;

		public DataTableWrapper() {
			row = 0;
			myEmployees = new LinkedList<>();
			data = DataTable.create();
			initColumns();
		}

		private void initColumns() {
			data.addColumn(ColumnType.STRING, "Nombre");
			data.addColumn(ColumnType.STRING, "Estado");
			data.addColumn(ColumnType.DATE, "Inicio");
			data.addColumn(ColumnType.DATE, "Fin");
		}

		public void addRow(String pName, String pStatus, Date pRowStartDate, Date pRowEndDate, int pContractId,
				int contractLeaveId) {

			if (DateUtils.isAfterOrEquals(pRowStartDate, pRowEndDate))
				return;

			data.addRow();

			data.setValue(row, 0, pName);
			data.setValue(row, 1, pStatus);
			data.setValue(row, 2, pRowStartDate);
			data.setValue(row, 3, pRowEndDate);

			this.row++;

			if (myEmployees.isEmpty()
					|| !AonStringUtils.equalsIgnoreCase(myEmployees.getLast().getLast().getFullName(), pName)) {
				statusList = new LinkedList<>();
				myEmployees.add(statusList);
			}

			statusList.add(new Status());
			myEmployees.getLast().getLast().setFullName(pName);
			myEmployees.getLast().getLast().setEstado(pStatus);
			myEmployees.getLast().getLast().setContractId(pContractId);
			myEmployees.getLast().getLast().setContractLeaveId(contractLeaveId);

		}

		public AbstractDataTable getDataTable() {
			return data;
		}

		private int getContractId(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getContractId();
		}

		private int getContractLeaveId(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getContractLeaveId();
		}

		private String getStatus(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getEstado();
		}

	}

	// --------------------------------------------------- TimeLineChart.Status

	private static class Status {

		private String fullName;
		private int contractId;
		private int contractLeaveId;
		private String estado;

		public Status() {
			super();
		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public int getContractId() {
			return contractId;
		}

		public void setContractId(int contractId) {
			this.contractId = contractId;
		}

		public void setContractLeaveId(int pLeaveId) {
			contractLeaveId = pLeaveId;
		}

		public int getContractLeaveId() {
			return contractLeaveId;
		}

		public String getEstado() {
			return estado;
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

	}

	// ---------------------------------------------------
	// TimeLineChart.getLogicalName()

	private static class JsLogicalName extends JavaScriptObject {

		private static class JsData extends JavaScriptObject {

			protected JsData() {
			}

			public final native int getvR() /*-{
				return this.vR;
			}-*/;

			public final native int getuR() /*-{
				return this.uR;
			}-*/;
		}

		protected JsLogicalName() {
		}

		public final native String getType() /*-{
			return this.type;
		}-*/;

		public final native JsData getData() /*-{
			return this.data;
		}-*/;
	}

	private static String getLogicalName(Element el, int x, int y) {
		String json = el.getPropertyString("logicalname");
		if (json != null)
			return json;

		Document doc = Document.get();

		if (IFrameElement.is(el)) {
			x -= el.getAbsoluteLeft();
			y -= el.getAbsoluteTop();
			doc = IFrameElement.as(el).getContentDocument();
		}

		NodeList<Element> rects = doc.getElementsByTagName("rect");

		for (int i = 0; i < rects.getLength(); i++) {

			Element rect = rects.getItem(i);
			json = rect.getPropertyString("logicalname");
			JsLogicalName logicalName = null;

			if (json != null)
				logicalName = JsonUtils.safeEval(json);

			if (null == json || null == logicalName || !"bar".equals(logicalName.getType()))
				continue;

			if (isElementAt(rect, x, y))
				return json;
		}

		return null;
	}

	private static boolean isElementAt(Element el, int x, int y) {
		return !(x < el.getAbsoluteLeft() || x > el.getAbsoluteLeft() + el.getOffsetWidth() || y < el.getAbsoluteTop()
				|| y > el.getAbsoluteTop() + el.getOffsetHeight());
	}

	// --------------------------------------------------- FromJS to ITEmployee, IT,
	// ContractInfo, EmployeeInfo

	private static ITEmployee fromJsITEmployee(JsITEmployee jsITEmployee) {
		ITEmployee itEmployee = new ITEmployee();

		JsEmployeeInfo jsEmployeeInfo = jsITEmployee.getEmployeeInfo();
		EmployeeInfo employeeInfo = fromJsEmployeeInfo(jsEmployeeInfo);

		JsContractInfo jsContractInfo = jsITEmployee.getContractInfo();
		ContractInfo contractInfo = fromJsContractInfo(jsContractInfo);

		JsArray<JsIT> jsITs = jsITEmployee.getITs();
		List<IT> its = new ArrayList<>();
		for (int i = 0; i < jsITs.length(); i++)
			its.add(fromJsIT(jsITs.get(i)));

		itEmployee.setEmployeeInfo(employeeInfo);
		itEmployee.setContractInfo(contractInfo);
		itEmployee.setIts(its);

		itEmployee.setStatus(jsITEmployee.getStatus());

		return itEmployee;
	}

	private static IT fromJsIT(JsIT jsIT) {

		IT it = new IT();
		it.setContract(jsIT.getContract());
		it.setDailyCGCBase(jsIT.getDailyCGCBase());
		it.setDailyCGPBase(jsIT.getDailyCGPBase());
		it.setDailyREGBase(jsIT.getDailyREGBase());
		it.setDescription(jsIT.getDescription());
		it.setDomain(jsIT.getDomain());
		it.setEndDate(jsIT.getEndDate());
		it.setFullName(jsIT.getFullName());
		it.setMaternityReason(jsIT.getMaternityReason());
		it.setMaternityType(jsIT.getMaternityType());
		it.setId(jsIT.getId());
		it.setIsParent(jsIT.isParent());
		it.setITParts(createDefaultITParts(jsIT));
		it.setParent(jsIT.getParent());
		it.setStartDate(jsIT.getStartDate());
		it.setTypeHighPart(jsIT.getTypeHighPart());
		it.setTypeLowPart(jsIT.getTypeLowPart());
		return it;
	}

	private static List<ITPart> createDefaultITParts(JsIT jsIT) {
		List<ITPart> itParts = new ArrayList<>();

		// Low ITPart
		ITPart itPart = new ITPart();
		itPart.setType((byte) 0);
		itPart.setDomain(jsIT.getDomain());
		itPart.setDate(jsIT.getStartDate());

		itParts.add(itPart);

		return itParts;
	}

	private static ContractInfo fromJsContractInfo(JsContractInfo jsContractInfo) {
		ContractInfo contractInfo = new ContractInfo();
		contractInfo.setActivityId(jsContractInfo.getActivityId());
		contractInfo.setEnterpriseCIF(jsContractInfo.getEnterpriseCIF());
		contractInfo.setEnterpriseName(jsContractInfo.getEnterpriseName());
		contractInfo.setCccId(jsContractInfo.getCccId());
		contractInfo.setCompleteCCC(jsContractInfo.getCompleteCCC());
		contractInfo.setCccType(jsContractInfo.getCccType());
		contractInfo.setWorkplaceId(jsContractInfo.getWorkplaceId());
		contractInfo.setWorkplaceZIP(jsContractInfo.getWorkplaceZIP());
		contractInfo.setWorkplaceFullAddress(jsContractInfo.getWorkplaceFullAddress());
		contractInfo.setContractType(jsContractInfo.getContractType());
		contractInfo.setContractModel(jsContractInfo.getContractModel());
		contractInfo.setStartDate(jsContractInfo.getStartDate());
		contractInfo.setEndDate(jsContractInfo.getEndDate());
		contractInfo.setSeniorityDate(jsContractInfo.getSeniorityDate());
		contractInfo.setAgreementId(jsContractInfo.getAgreementId());
		contractInfo.setAgreementLevelId(jsContractInfo.getAgreementLevelId());
		contractInfo.setAgreementCategory(jsContractInfo.getAgreementCategory());
		contractInfo.setQuoteGroup(jsContractInfo.getQuoteGroup());
		contractInfo.setOcupation(jsContractInfo.getOcupation());
		contractInfo.setJourneyType(jsContractInfo.getJourneyType());
		contractInfo.setSsRegimen(jsContractInfo.getSsRegimen());
		contractInfo.setContractId(jsContractInfo.getContractId());
		contractInfo.setContracttypeId(jsContractInfo.getContracttypeId());
		contractInfo.setQuotegroupId(jsContractInfo.getQuotegroupId());
		contractInfo.setOcupationId(jsContractInfo.getOcupationId());
		contractInfo.setJourneytypeId(jsContractInfo.getJourneytypeId());
		contractInfo.setContractmodelId(jsContractInfo.getContractmodelId());
		contractInfo.setRetaId(jsContractInfo.getRetaId());
		contractInfo.setHasPayroll(jsContractInfo.getHasPayroll());
		contractInfo.setPayrollDate(jsContractInfo.getPayrollDate());
		return contractInfo;
	}

	private static EmployeeInfo fromJsEmployeeInfo(JsEmployeeInfo jsEmployeeInfo) {
		EmployeeInfo employeeInfo = new EmployeeInfo();

		employeeInfo.setAccount(jsEmployeeInfo.getAccount());
		employeeInfo.setAddresNum(jsEmployeeInfo.getAddresNum());
		employeeInfo.setAddress(jsEmployeeInfo.getAddress());
		employeeInfo.setAddressCity(jsEmployeeInfo.getAddressCity());
		employeeInfo.setAddressInfo(jsEmployeeInfo.getAddressInfo());
//		employeeInfo.setAddressProvinces(jsEmployeeInfo.getAddressProvinces());
		employeeInfo.setAddressZip(jsEmployeeInfo.getAddressZip());
		employeeInfo.setBic(jsEmployeeInfo.getBic());
		employeeInfo.setBirthdate(jsEmployeeInfo.getBirthdate());
		employeeInfo.setCivilStatus(jsEmployeeInfo.getCivilStatus());
		employeeInfo.setContractActive(jsEmployeeInfo.getContractActive());
		employeeInfo.setContractId(jsEmployeeInfo.getContractId());
		employeeInfo.setDocument(jsEmployeeInfo.getDocument());
		employeeInfo.setDocumentType(jsEmployeeInfo.getDocumentType());
		employeeInfo.setDomain(jsEmployeeInfo.getDomain());
		employeeInfo.setEmail(jsEmployeeInfo.getEmail());
		employeeInfo.setEmailId(jsEmployeeInfo.getEmailId());
		employeeInfo.setEmployeeId(jsEmployeeInfo.getEmployeeId());
		employeeInfo.setGender(jsEmployeeInfo.getGender());
//		employeeInfo.setGeozoneId(jsEmployeeInfo.getGeozoneId());
		employeeInfo.setIsFullTime(jsEmployeeInfo.getIsFullTime());
		employeeInfo.setMobile(jsEmployeeInfo.getMobile());
		employeeInfo.setMobileId(jsEmployeeInfo.getMobileId());
		employeeInfo.setName(jsEmployeeInfo.getName());
		employeeInfo.setNationality(jsEmployeeInfo.getNationality());
		employeeInfo.setPaymethodId(jsEmployeeInfo.getPaymethodId());
		employeeInfo.setPayMethodType(jsEmployeeInfo.getPayMethodType());
		employeeInfo.setPayMethodTypeB(jsEmployeeInfo.getPayMethodTypeB());
		employeeInfo.setPhone(jsEmployeeInfo.getPhone());
		employeeInfo.setPhoneId(jsEmployeeInfo.getPhoneId());
		employeeInfo.setRaddressId(jsEmployeeInfo.getRaddressId());
		employeeInfo.setRbankId(jsEmployeeInfo.getRbankId());
		employeeInfo.setRpaymethodId(jsEmployeeInfo.getRpaymethodId());
		employeeInfo.setSecondSurName(jsEmployeeInfo.getSecondSurName());
		employeeInfo.setSsNumber(jsEmployeeInfo.getSsNumber());
		employeeInfo.setStreetType(jsEmployeeInfo.getStreetType());
		employeeInfo.setSurName(jsEmployeeInfo.getSurName());

		return employeeInfo;
	}

	private static native <T extends JavaScriptObject> T eval(String javascript)
	/*-{
		return eval(javascript);
	}-*/;

	// --------------------------------------------------- DeckPanel.Methods

	private void showTimeLine() {
		deckPanel.showWidget(0);
	}

	private void showMessage() {
		deckPanel.showWidget(1);
	}

	// --------------------------------------------------- Leyend.Methods

	public void openLeyend() {
		String leyend = "<div style=\"display: flex; flex-direction: column; width: 390px;\">";
		String divFlex = "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		String divEnd = "</div>";

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #A0C3FF;\" title=\"Periodo Activo del Empleado\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Activo</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FFA500;\" title=\"Enfermedad Com&uacute;n, Accidente no Laboral\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Accidente no Laboral</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #AA0033;\" title=\"Enfermedad Profesional\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Profesional</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FF66CC;\" title=\"Maternidad, Lactancia, Riesgo Durante el Embarazo\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Maternidad</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #36C;\" title=\"Baja por Paternidad\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Paternidad</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FFA500;\" title=\"Enfermedad Com&uacute;n, Periodo de Carencia\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Periodo de Carencia</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #E3DC14;\" title=\"Enfermedad Com&uacute;n, Prestaci&oacute;n Profesional (COVID-19)\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Prestaci&oacute;n Profesional (COVID-19)</a>";
		leyend += divEnd;

		leyend += divFlex;
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #f6788e;\" title=\"Periodo de Observaci&oacute;n por Enfermedad Profesional\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Periodo de Observaci&oacute;n por Enfermedad Profesional</a>";
		leyend += divEnd;

		leyend += divEnd;

		AonDialog leyendDialog = new AonDialog("Leyenda", new HTML(leyend));
		leyendDialog.info();
	}

	// --------------------------------------------------- Toolbar

	private void getToolbarPanel() {
		this.toolbar = new AonToolbar("Partes IT");

		msjFIEFormPanel = new FormPanel();
		msjFIEFormPanel.setMethod(FormPanel.METHOD_POST);
		msjFIEFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		msjFIEFormPanel.setAction(FIEService.FIE_URL);

		Hidden userNameHidden = new Hidden(FIEService.Parameter.USER.name(), Wnd.getCurrentUser());
		Hidden domainNameHidden = new Hidden(FIEService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());

		msjFIESubmitButton = new SubmitButton();
		msjFIESubmitButton.setVisible(false);

		msjFIEFileUpload = new MultiFileUpload();
		msjFIEFileUpload.setName(FIEService.Parameter.FILE.name());
		msjFIEFileUpload.setVisible(false);
		msjFIEFileUpload.setAccept(".msj,application/vnd.ms-excel (.xls)");
		msjFIEFileUpload.addChangeHandler(e -> msjFIEFormPanel.submit());
		msjFIEFormPanel.addSubmitCompleteHandler(e -> {
			String json = e.getResults();

			JsArray<JsITEmployee> jsITEmployees = eval("(" + json + ")");

			List<ITEmployee> itEmployees = new ArrayList<>(jsITEmployees.length());

			for (int i = 0; i < jsITEmployees.length(); i++) {
				JsITEmployee jsITEmployee = jsITEmployees.get(i);
				ITEmployee itEmployee = fromJsITEmployee(jsITEmployee);
				itEmployees.add(itEmployee);
			}

			setITEmployeeList(itEmployees, s -> {
				loadITWidget(() -> {
					String pattern = itEmployees.stream().map(ITEmployee::getEmployeeInfo)
							.map(EmployeeInfo::getFullName).collect(Collectors.joining("|"));
					employeeSB.getValueBox().setValue(pattern, true);

				}, f -> {
				});
			}, f -> {
			});

		});

		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		formFlowPanel.add(msjFIESubmitButton);

		msjFIEFormPanel.add(formFlowPanel);
		toolbar.add(msjFIEFormPanel);

		AonToolbarButton addIT = new AonToolbarButton("Nueva IT", AON.CSS.aonIconAdd());
		addIT.addClickHandler(e -> onAddIT());
		toolbar.add(addIT);

		showList = new AonToolbarButton("Lista ITs", AON.CSS.aonIconList());
		showList.addClickHandler(e -> showList());
		toolbar.add(showList);

		showStatics = new AonToolbarButton("L\u00ednea temporal ITs", AON.CSS.aonIconStatics());
		showStatics.addClickHandler(e -> showStatics());
		showStatics.setVisible(false);
		toolbar.add(showStatics);

		AonExpandButton tgssExpand = new AonExpandButton("Seguridad Social", AON.CSS.aonIconTgss()) {

			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				tgssContextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				tgssContextMenu.show();
			}

			@Override
			public void onDefaultClick(ClickEvent evet) {
				onFIEFileSync();
			}
		};
		toolbar.add(tgssExpand);

		leyend = new AonToolbarButton("Leyenda", AON.CSS.aonIconInfo());
		leyend.addClickHandler(e -> onLeyend());
		leyend.setVisible(false);
		toolbar.add(leyend);

	}

	// --------------------------------------------------- Toolbar.Methods

	private void showStatics() {
		mainDeckPanel.showWidget(0);
		showStatics.setVisible(false);
		showList.setVisible(true);
		leyend.setVisible(true);
		loadITStatics();
	}

	private void showList() {
		mainDeckPanel.showWidget(1);
		showStatics.setVisible(true);
		showList.setVisible(false);
		leyend.setVisible(false);
		loadITsList();
	}

	private void onAddIT() {
		newITDialog();
	}

	private void onFIEFileSync() {
		msjFIEFormPanel.setMethod(METHOD_GET);
		msjFIESubmitButton.click();
		AonMessagePanel.showLoading(messagePanel, "Consultando/Descargando el Fichero INSS Empresas (FIER)");
	}

	private void onFIEFileUpload() {
		msjFIEFormPanel.setMethod(METHOD_POST);
		msjFIEFileUpload.click();
	}

	private void checkStatusITs() {
		showProgressPanel();
		checkStatus(status -> {
			SistemaREDITResults results = new SistemaREDITResults() {
				@Override
				public void run() {
					showProgressPanel();
					checkStatus(status -> {
						removeAll();
						status.visit(this);
					}, throwable -> {
						LOGGER.info("error run");
					});
				}

				@Override
				public void up2DateEnterprise() {
					this.setUp2DateEnterprise();
					closeFootPanel();
				}

				@Override
				public void updatedEnterprise() {
					loadITWidget();
				}

				@Override
				protected void credentialsFound() {
					checkStatus(status -> {
						removeAll();
						status.visit(this);
						showResultsPanel();
						EnterpriseITStatus.ifSistemaREDEnabled(status, ITWidget.this::showFootPanel,
								ITWidget.this::closeFootPanel);

						EnterpriseITStatus.ifSistemaREDError(status, ITWidget.this::showFootPanel,
								ITWidget.this::closeFootPanel);
					}, throwable -> {
						closeFootPanel();
					});
				}

				@Override
				protected void onOpenITPart(ItNotExist itNotExist) {
					openITPartDialog(itNotExist);
				}

				@Override
				protected void onSaveITPart(ItNotExist itNotEx) {
					List<ItNotExist> itNotExist = new ArrayList<>();
					itNotExist.add(itNotEx);
					saveITPartsAon(itNotExist);
				}

				@Override
				protected void onRemoveITPartToSS(ItNotExist ItNotExist) {
					confirmDeleteITToTGSS(ItNotExist);
				}

				@Override
				protected void onRemoveITPartToAon(ItNotExist itNotEx) {
					AonConfirmDialog confirmDialog = new AonConfirmDialog();
					confirmDialog.confirm("BORRADO",
							String.valueOf("\u00BF") + "Realmente desea eliminar el parte IT de aon Solutions?",
							new AonConfirmDialogCallback() {
								@Override
								public void onCancel() {
								}

								@Override
								public void onAccept() {
									removeITPart(itNotEx);
								}
							});
				}

				@Override
				public void onFinish() {
					showResultsPanel();
				}
			};
			// results.itNotExist(null);
			results.setIsUserComunica(isUserComunica());
			status.visit(results);
			resultsPanel.setWidget(results);

			showResultsPanel();

			EnterpriseITStatus.ifSistemaREDEnabled(status, ITWidget.this::showFootPanel, ITWidget.this::closeFootPanel);

			EnterpriseITStatus.ifSistemaREDError(status, ITWidget.this::showFootPanel, ITWidget.this::closeFootPanel);
		}, f -> {
			closeFootPanel();
		});

	}

	private void saveITPartsAon(List<ItNotExist> itNotExist) {
		saveITParts(itNotExist, s -> {
			showCreateMessage();
			loadITWidget();
		}, e -> {
			AonDialog dialog = new AonDialog("Error", new HTML(e.getMessage()));
			dialog.warning();
		});
	}

	private void confirmDeleteITToTGSS(ItNotExist itNotEx) {
		AonConfirmDialog confirmDialog = new AonConfirmDialog();
		confirmDialog.confirm("BORRADO",
				String.valueOf("\u00BF") + "Realmente desea anular el parte IT del Sistema RED?",
				new AonConfirmDialogCallback() {
					@Override
					public void onCancel() {
					}

					@Override
					public void onAccept() {
						removeITPart(itNotEx);
						if (itDialogEdit != null) {
							itDialogEdit.hide();
						}
					}
				});
	}

	private void removeITPart(ItNotExist itNotEx) {
		List<ItNotExist> itNotExist = new ArrayList<>();
		itNotExist.add(itNotEx);
		removeITParts(itNotExist, s -> {
			showDeleteMessage();
			loadITWidget();
		}, e -> {
			AonDialog dialog = new AonDialog("Error", new HTML(e.getMessage()));
			dialog.warning();
		});
	}

	private void openITPartDialog(ItNotExist itNotExist) {
		Optional<Integer> idPart = itNotExist.getIdPart();
		if (idPart.isPresent()) {
			Optional<ITEmployee> itEmployee = Optional.empty();
			Optional<ITPart> itPart = Optional.empty();

			outerLoop: for (ITEmployee itE : itEmployeeIts) {
				for (IT it : itE.getIts()) {
					for (ITPart part : it.getITParts()) {
						if (part.getId().equals(idPart.get())) {
							itEmployee = Optional.ofNullable(itE);
							itPart = Optional.ofNullable(part);
							break outerLoop;
						}
					}
				}
			}

			if (itPart.isPresent()) {
				ITPart part = itPart.get();
				int contractId = itEmployee.get().getContractInfo().getContractId();

				ITDialog dialog = openITDialog(contractId, part.getIt());
				dialog.setViewPartComunica(part);
			}
		}
	}

	private void onLeyend() {
		openLeyend();
	}

	// --------------------------------------------------- ITDialog.Methods

	private ITDialog newITDialog() {
		ITDialog itDialog = new ITDialog("Creaci\u00F3n") {
			@Override
			protected void onAccept() {
				accept(getITEmployee(), true);
			}

			@Override
			protected void onDelete(IT it) {
				deleteLeave(getITEmployee(), it);
			}

			@Override
			protected void onShowCertitificateIT(IT it) {
				getITCertificatePDF(getITEmployee(), it);
			}

			@Override
			protected void onCommunicateITPart(IT it, ITPart part) {
			}

			@Override
			protected void onRemoveITPartTGSS(ItNotExist ItNotExist) {
			}

			@Override
			protected void onDownloadFDIITPart(IT it, ITPart itPart) {
			}
		};

		itDialog.setEmployeesList(getActiveEmployeesList());
		itDialog.setIsUserComunica(isUserComunica());
		itDialog.initConfirmationsTable();
		itDialog.setModal(true);
		itDialog.setAnimationEnabled(true);
		itDialog.center();
		itDialog.show();

		return itDialog;
	}

	private ITDialog openITDialog(int contractId, int itId) {
		IT itInfo = getIT(itId);
		ITEmployee itEmployee = getITEmployee(contractId);
		itDialogEdit = null;
		itDialogEdit = new ITDialog("Edici\u00F3n") {

			@Override
			protected void onAccept() {
				accept(itEmployee, false);
			}

			@Override
			protected void onDelete(IT it) {
				deleteLeave(itEmployee, it);
			}

			@Override
			protected void onShowCertitificateIT(IT it) {
				getITCertificatePDF(itEmployee, it);
			}

			@Override
			protected void onCommunicateITPart(IT it, ITPart part) {
				startLoading(true);
				communicateITPart(itEmployee, it, part, s -> {

					normalizeITToSave();

					acceptUpdate(itEmployee);

					hide();

					startLoading(false);

				}, e -> {
					changeStatusPending();

					AonDialog dialog = new AonDialog("Error", new HTML(e.getMessage()));
					dialog.warning();
					startLoading(false);
				});
			}

			@Override
			protected void onRemoveITPartTGSS(ItNotExist ItNotExist) {
				confirmDeleteITToTGSS(ItNotExist);
			}

			@Override
			protected void onDownloadFDIITPart(IT it, ITPart part) {
				startLoading(true);

				normalizeITToSave();

				acceptSave(itEmployee);

				hide();
				startLoading(false);

				String fileDownloadURL = GWT.getModuleBaseURL() + "/download_fdi/" + "?contract="
						+ itEmployee.getContractInfo().getContractId() + "&it=" + it.getId() + "&quoteDays="
						+ it.getQuoteDays();
				Window.open(fileDownloadURL, "_blank", null);

			}
		};

		ITDialogObject itDialogObject = new ITDialogObject(itEmployee);
		itDialogEdit.setIsUserComunica(isUserComunica());
		itDialogEdit.setITDialogObject(itDialogObject, itInfo, true);
		return itDialogEdit;
	}

	private void accept(ITEmployee itEmployee, boolean newIT) {
		setITEmployee(itEmployee, s -> {
			if (newIT)
				showCreateMessage();
			else
				showUpdateMessage();
			loadITWidget();
		}, f -> {
		});
	}

	private void acceptUpdate(ITEmployee itEmployee) {
		setITEmployee(itEmployee, s -> {
			loadITWidget();
			showCommunicateIT();
		}, f -> {
		});
	}

	private void acceptSave(ITEmployee itEmployee) {
		setITEmployee(itEmployee, s -> {
			loadITWidget();
		}, f -> {
		});
	}

	private void showMessage(String title, String body) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, body);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	private void showMessages(String... messages) {
		AonMessagePanel.showSuccess(messagePanel, messages);
	}

	private void showCreateMessage() {
		showMessage("Creaci\u00F3n IT", "El parte ha sido creado correctamente");
	}

	private void showUpdateMessage() {
		showMessage("Actualizaci\u00F3n IT", "El parte ha sido actualizado correctamente");
	}

	private void showCommunicateIT() {
		showMessage("Comunicaci\u00F3n", "Parte IT comunicada a la TGSS");
	}

	private void showDownloadFIEIT() {
		showMessage("Fichero FIE", "Fichero FIE descargado correctamente");
	}

	private void showDeleteMessage() {
		showMessage("Borrado IT", "El parte ha sido eliminado correctamente");
	}

	private void showSyncMessage() {
		showMessage("Sincronizaci\u00f3n ITs", "Los partes IT se han sincronizaco correctamente con la TGSS");
	}

	private void showComunicateMessage() {
		showMessage("Comunicaci\u00F3 IT", "El parte ha sido comunicado a la TGSS");
	}

	private void deleteLeave(ITEmployee itEmployee, IT it) {
		if (ITDialog.isPartenityPart(it))
			deletePaternity(it, itEmployee);
		else
			delete(it, itEmployee);
	}

	private void delete(IT it, ITEmployee itEmployee) {
		deleteIT(itEmployee, it, s -> {
			showDeleteMessage();
			loadITWidget();
		}, f -> {
			showDeleteMessage();
			loadITWidget();
		});
	}

	private void deletePaternity(IT it, ITEmployee itEmployee) {
		deletePaternityIT(itEmployee, it, s -> {
			showDeleteMessage();
			loadITWidget();
		}, f -> {
			showDeleteMessage();
			loadITWidget();
		});
	}

	private void getITCertificatePDF(ITEmployee itEmployee, IT it) {
		getNafxIpf(itEmployee, s -> {
			String affiliationNumber = s.getNss();
			String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
			String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4,
					itEmployee.getContractInfo().getCompleteCCC().length());
			String dateFromStr = formatFullDate.format(new Date());
			String dateToStr = formatFullDate.format(new Date());
			String startDateStr = formatFullDate.format(it.getStartDate());

			String fileDownloadURL = GWT.getModuleBaseURL() + "it_export/";
			String query = "?domainName=" + Wnd.getCurrentDomainNameURL() + "&userLogin=" + Wnd.getCurrentUser()
					+ "&affiliationNumber=" + affiliationNumber + "&regime=" + regime + "&contributionAccount="
					+ contributionAccount + "&dateFromStr=" + dateFromStr + "&dateToStr=" + dateToStr + "&startDateStr="
					+ startDateStr + "&itType=" + it.getTypeLowPart() + "&itPartType=0";

			Window.open(fileDownloadURL + query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
		}, f -> {
		});
	}

	// --------------------------------------------------- MessagePanel

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	// --------------------------------------------NEW------------

	private void initFootPanel() {
		resultsPanel = new ResultsPanel();

		footPanel = new AonMinimizePanel();
		footPanel.addMaximizeHandlerNew(event -> showFootPanel());
		footPanel.addMinimizeHandlerNew(event -> closeFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		footPanel.addStyleName("aon-EmployeeTree-Events");

		tabLayout = new TabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		tabLayout.setAnimationDuration(300);
		footPanel.add(tabLayout);

		splitLayoutPanel.addSouth(footPanel, 17);
		footPanel.initNewButtons();
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
		removeTabs();
		tabLayout.add(resultsPanel, TABLAYOUT_FOLDER_TEMPLATE.tab("Resultados ITs", AON.CSS.aonIconTgss()));
		tabLayout.selectTab(resultsPanel);
	}

	private void showProgressPanel() {
		removeTabs();
		progressPanel = new ProgressPanel();
		HandlerRegistration handlerRegistration[] = new HandlerRegistration[1];
		handlerRegistration[0] = progressPanel.addAttachHandler(e -> {
			Task syncTask = new Task();
			syncTask.setDescription("Consultando ITs con el SISTEMA RED");
			progressPanel.showTask(syncTask);
			handlerRegistration[0].removeHandler();
		});

		InlineLabel tab = new InlineLabel("Progreso");
		tab.addStyleName(AON.AON_ICON_PROGRESS_BAR);
		tab.addStyleName(AON.AON_ICON_CMD_BUTTON);
		tabLayout.add(progressPanel, tab);
		tabLayout.selectTab(progressPanel);
	}

	private void removeTabs() {
		if (resultsPanel != null)
			tabLayout.remove(resultsPanel);

		if (progressPanel != null)
			tabLayout.remove(progressPanel);
	}

	// --------------------------------------------------- Abstract Methdos

	protected abstract void syncITs(Consumer<Void> success, Consumer<Throwable> failure);

	protected abstract void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure);

	protected abstract List<IT> getITsList();

	public abstract boolean isUserComunica();

	public abstract SortedSet<Integer> getAviableYears();

	public abstract ITEmployee getITEmployee(Integer contractId);

	public abstract IT getIT(Integer itId);

	protected abstract List<ITEmployee> getActiveEmployeesList();

	protected abstract List<ITEmployee> getFilterITEmployeeList(Boolean allContracts, Date start, Date end);

	protected abstract void setITEmployeeList(List<ITEmployee> itEmployees, Consumer<List<ITEmployee>> success,
			Consumer<Throwable> failure);

	protected abstract void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure);

	protected abstract void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure);

	protected abstract void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success,
			Consumer<Throwable> failure);

	@Deprecated
	protected abstract void comunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success,
			Consumer<Throwable> failure);

	@Deprecated
	protected abstract void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success,
			Consumer<Throwable> failure);

	protected abstract void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success,
			Consumer<Throwable> failure);

	protected abstract void communicateITPart(ITEmployee itEmployee, IT it, ITPart part, Consumer<Void> success,
			Consumer<Throwable> failure);

	protected abstract void saveITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure);

	protected abstract void removeITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure);

	protected abstract void checkStatus(Consumer<EnterpriseITStatus> success, Consumer<Throwable> failure);

	public void setFooter(SplitLayoutPanel splitLayoutPanel, TabLayoutPanel tabLayout, AonMinimizePanel footPanel) {
		this.splitLayoutPanel.remove(this.footPanel);
		this.footPanel = footPanel;
		this.tabLayout = tabLayout;
		this.splitLayoutPanel = splitLayoutPanel;
	}

	public void removeFootPanel() {
		this.splitLayoutPanel.remove(this.footPanel);
	}

}
