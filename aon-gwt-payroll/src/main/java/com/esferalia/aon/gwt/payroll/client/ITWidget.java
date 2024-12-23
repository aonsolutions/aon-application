package com.esferalia.aon.gwt.payroll.client;

import static com.google.gwt.user.client.ui.FormPanel.METHOD_GET;
import static com.google.gwt.user.client.ui.FormPanel.METHOD_POST;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.ProgressPanel;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
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
import com.esferalia.aon.gwt.payroll.shared.ItParams;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDEmployeeIT;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDIT;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.Parameter;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.esferalia.aon.occam.api.model.EmployeeIT;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.type.ContractLeaveType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
//import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class ITWidget extends AonCustomDockLayout {

	private static final Logger LOGGER = Logger.getLogger(ITWidget.class.getName());

	static { LOGGER.addHandler(new ConsoleLogHandler()); }
	
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
			results.hideNorth();
			results.removeAll();

			AonMessagePanel.showLoading(messagePanel, "Sincronizando Its...");
			DateTimeFormat dateFormat = DateTimeFormat.getFormat(SistemaREDService.DATE_FORMAT);

			StringBuilder requestDataBuffer = new StringBuilder();

			requestDataBuffer.append("&" + Parameter.USER + "=" + Wnd.getCurrentUser());
			requestDataBuffer.append("&" + Parameter.DOMAIN + "=" + Wnd.getCurrentDomainNameURL());
			
			Date startYear = start.getValue() == null ? DateUtils.getFirstDayOfYear() : start.getValue();
			Date endYear = end.getValue() == null ? DateUtils.getLastDayOfYear(new Date()) : end.getValue();
			
			requestDataBuffer.append("&" + Parameter.START_DATE + "=" + dateFormat.format(startYear));
			requestDataBuffer.append("&" + Parameter.END_DATE + "=" + dateFormat.format(endYear));

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
							AonMessagePanel.showLoading(messagePanel,
									SafeHtmlUtils.fromTrustedString("Actualizando las ITs del empleado "
											+ employeeIT.getName() + " , ninguna IT encontrada."));
						} else {
							AonMessagePanel.showLoading(messagePanel,
									SafeHtmlUtils.fromTrustedString("Actualizando las ITs del empleado "
											+ employeeIT.getName() + " , ITs encontradas en TGSS: "
											+ employeeIT.getItsInTgss().length()));
						}

						if (employeeIT.getItsNotInAon().length() != 0) {
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
								Window.alert("itNotExistInAon");
								results.itNotExist(itNotExistInAon);
							}
						}

						if (employeeIT.getItsNotInTgss().length() != 0) {
							JsArray<JsSistemaREDIT> itsNotInTgss = employeeIT.getItsNotInTgss();
							for (int i = 0; i < itsNotInTgss.length(); i++) {
								ItNotExist itNotExistInTgss = new ItNotExist();
								JsSistemaREDIT sistemaRedIT = itsNotInTgss.get(i);
								EmployeeIT employee = new EmployeeIT();
								
								Integer employeeId = null;
								try {
									employeeId = Integer.parseInt(sistemaRedIT.getId());
								} catch (Exception e) {}

								employee.setCcc(sistemaRedIT.getCcc()).setDni(sistemaRedIT.getDni())
										.setName(sistemaRedIT.getName()).setNss(sistemaRedIT.getNss())
										.setRegime(sistemaRedIT.getRegime())
										.setType(ContractLeaveType.valueOf(sistemaRedIT.getType()))
										.setId(Integer.parseInt(sistemaRedIT.getId()))
										.setStartDate(dateFormat.parse(sistemaRedIT.getStartDate()))
										.setId(employeeId);

								if (sistemaRedIT.getEndDate() != null) {
									employee.setEndDate(dateFormat.parse(sistemaRedIT.getEndDate()));
								}

								itNotExistInTgss.setEmployeeIT(employee);
								Window.alert("itNotExistInTgss");
								results.itNotExist(itNotExistInTgss);
							}
						}

					} catch (Exception e) {
					}
					int state = xhr.getReadyState();
					if (state != XMLHttpRequest.DONE) {
						return;
					}

					Scheduler.get().scheduleDeferred(() -> AonMessagePanel.showSuccess(messagePanel, "Its actualizadas"));
					Scheduler.get().scheduleDeferred(() -> {
						if(results.getTreeItems() != 0) {
							ResultsPanel resultPanel = new ResultsPanel();
							resultPanel.setHeight((results.getTreeItems() > 20 ? (20 * 30) : results.getTreeItems() * 30) + "px");
							resultPanel.setWidth("800px");
							resultPanel.setWidget(results);
							
							AonDialog dialog = new AonDialog("SistemaRED", resultPanel);
							dialog.removeMaxWidth();
							dialog.info();
						}
						
					});
				}

			});

			xhr.send(requestDataBuffer.toString());
			
		}

	}

	class TGSSContextMenu extends ContextMenu {

		private MenuItem fie;
		private MenuItem tgssSyncro;

		public TGSSContextMenu() {

			fie = addItem("Mensaje/Fichero INSS (FIE)", new MsjFIECommand(), AON.CSS.aonIconTgss(),
					AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			fie.ensureDebugId("fie");

			tgssSyncro = addItem("Sincronizaci\u00f3n TGSS", new TgssSyncroCommand(), AON.CSS.aonIconTgss(),
					AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			tgssSyncro.ensureDebugId("tgssSyncro");
		}

	}

	// ------------------------------------------------- DockLayoutPanel

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private AonCustomDateBox start = new AonCustomDateBox("F. Inicio IT");
	private AonCustomDateBox end = new AonCustomDateBox("F. Fin IT");

	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");

	private AonToolbarButton showList;
	private AonToolbarButton showStatics;
	private AonToolbarButton leyend;

	private TGSSContextMenu tgssContextMenu;

	private FormPanel msjFIEFormPanel;
	private MultiFileUpload msjFIEFileUpload;
	private SubmitButton msjFIESubmitButton;
	private SistemaREDITResults results = new SistemaREDITResults();

	private DeckLayoutPanel deckPanel;

	private HTMLPanel staticsPanel;
	private SimpleLayoutPanel centerPanel;
	private ITTable staticsList;

	private ProgressPanel progressPanel;

	private List<ITEmployee> employeesList = new ArrayList<ITEmployee>();
	private List<IT> itsList = new ArrayList<IT>();
	
	private ITDialog itDialogEdit;
	private boolean minimizedByUser;
	
	private DomainUserRoles dur;
	private Integer workplace;
	
	// --------------------------------------------------- Variables

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final String CONTRACT_EXP = "{\"type\":\"bar\"";

	// --------------------------------------------------- TimeLineChart.Variables

	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);

	private static final String ACTIVE = "Activo";

	private static LinkedList<LinkedList<Status>> myEmployees;

	private boolean ifNull; // Evitar el Null del Timeline

	private Map<Integer, ITEmployee> centineels;


	private TimeLineChart timelineChart;

	private ITTooltip tooltip;
	private DataTableWrapper data;

	private int posCell; // vR
	private int posColumn; // uR

	private TooltipCallBack tooltipCallback;

	private String cadenaTooltip;

	private PopupPanel popupPanel;

	// --------------------------------------------------- Constructor

	protected ITWidget() {
		super("Partes IT");
		
		getDur(durDB -> dur = durDB, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));

		tgssContextMenu = new TGSSContextMenu();
		centineels = new LinkedHashMap<>();

		addButtonsToolbar();

		hideToolbarFilterMessages();
		setSearchPlaceholder("Busque por nombre / NIF ...");
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if (AonStringUtils.isNotBlank(value) && value.length() > 3) {
				onSearch();
			} else if (AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});

		HTMLPanel datesPanel = new HTMLPanel(AonStringUtils.EMPTY);
		start.setValue(DateUtils.getFirstDayOfYear());
		start.addValueChangeHandler(e -> onSearch());
		end.addValueChangeHandler(e -> onSearch());
		datesPanel.add(start);
		datesPanel.add(end);
		addFilterWidget(datesPanel);

		sort.addItem("Nombre", "name");
		sort.addItem("F.Baja", "start");
		sort.addItem("Causa Baja", "low");
		sort.addItem("F.Alta", "end");
		sort.addItem("Causa Alta", "hight");
		sort.addItem("Inicio Contrato", "startContract");
		sort.addItem("Fin Contrato", "endContract");
		sort.getListBox().addChangeHandler(event -> onSearch());

		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());

		addSortWidget(sort);
		addSortWidget(asc);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);

		deckPanel = new DeckLayoutPanel();
		deckPanel.setHeight("100%");

		staticsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		staticsPanel.setHeight("100%");
		deckPanel.add(staticsPanel);
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		
		deckPanel.add(centerPanel);
		
		// Default statics view
		deckPanel.showWidget(0);

		container.add(deckPanel);

		add(container);
	}

	@Override
	protected void onClearFilter() {
		getSearchTextBox().setValue(null, false);
		start.setValue(DateUtils.getFirstDayOfYear());
		end.setValue(null);

		staticsList.resetSearchOffset();

		onSearch();
	}

	private void addButtonsToolbar() {
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
			
			if(!itEmployees.isEmpty()) {
				String pattern = itEmployees.stream()
					    .map(ITEmployee::getEmployeeInfo)
					    .map(EmployeeInfo::getFullName)
					    .map(fullName -> fullName.contains(",") ? fullName.substring(0, fullName.indexOf(",")) : fullName)
					    .collect(Collectors.joining(" | "));
				
				AonMessagePanel.showSuccess(messagePanel, "Se han importado los partes IT de : " + pattern);
				getSearchTextBox().setValue(pattern);
				onSearch();
			} else {
				AonMessagePanel.showInfo(messagePanel, "Sincronizaci\u00f3n finalizada. No se han encontrado partes que importar.");
			}
		});

		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		formFlowPanel.add(msjFIESubmitButton);

		msjFIEFormPanel.add(formFlowPanel);
		addToolbarButton(msjFIEFormPanel);

		AonToolbarButton addIT = new AonToolbarButton("Nueva IT", AON.CSS.aonIconAdd());
		addIT.addClickHandler(e -> onAddIT());
		addToolbarButton(addIT);

		showList = new AonToolbarButton("Lista ITs", AON.CSS.aonIconList());
		showList.addClickHandler(e -> showList());
		addToolbarButton(showList);

		showStatics = new AonToolbarButton("L\u00ednea temporal ITs", AON.CSS.aonIconStatics());
		showStatics.addClickHandler(e -> {
			showStatics();
		});
		showStatics.setVisible(false);
		addToolbarButton(showStatics);

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
		addToolbarButton(tgssExpand);

		leyend = new AonToolbarButton("Leyenda", AON.CSS.aonIconInfo());
		leyend.addClickHandler(e -> onLeyend());
		leyend.setVisible(false);
		addToolbarButton(leyend);

	}

	private void onAddIT() {
		newITDialog();
	}

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
			protected void onCommunicateITPart(IT it, ITPart part) {
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

	private void deleteLeave(ITEmployee itEmployee, IT it) {
		if (ITDialog.isPartenityPart(it))
			deletePaternity(it, itEmployee);
		else
			delete(it, itEmployee);
	}

	private void accept(ITEmployee itEmployee, boolean newIT) {
		createUpdateITEmployee(itEmployee, s -> {
			if (newIT)
				showCreateMessage();
			else
				showUpdateMessage();
			loadITWidget();
		}, f -> {
		});
	}

	private void showList() {
		deckPanel.showWidget(1);
		showStatics.setVisible(true);
		showList.setVisible(false);
		leyend.setVisible(false);

		showOrder();
		
		loadITsList();
	}

	private void showStatics() {
		deckPanel.showWidget(0);
		showStatics.setVisible(false);
		showList.setVisible(true);
		leyend.setVisible(true);

		hideOrder();
		loadITStatics();
	}

	private void onFIEFileSync() {
		msjFIEFormPanel.setMethod(METHOD_GET);
		msjFIESubmitButton.click();
		AonMessagePanel.showLoading(messagePanel, "Consultando/Descargando el Fichero INSS Empresas (FIER)");
	}

	public void loadITWidget() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo ITs de los trabajadores...");
		onSearch();
	}
	
	private void onSearch() {
		employeesList.clear();
		itsList.clear();
		centineels.clear();
		
		ItParams params = getParams();
		params.setOffset(0);
		params.setLimit(Integer.MAX_VALUE);
		
		getEmployeeItList(
			params,
			itEmployeeList -> {
				if (itEmployeeList.isEmpty()) {
					AonMessagePanel.showWarning(messagePanel, "No existen IT para los filtros seleccionados");
				} else {
					employeesList = itEmployeeList;
					itEmployeeList.stream().map(itEmployee -> itEmployee.getIts()).collect(Collectors.toList()).forEach(listIt -> itsList.addAll(listIt));					
					int deckIdx = deckPanel.getVisibleWidgetIndex();
					
					employeesList.forEach(itEmployee -> centineels.put(itEmployee.getContractInfo().getContractId(), itEmployee));
					
					if (0 == deckIdx) {
						showStatics();
					} else
						showList();
					
					AonMessagePanel.hideMessage(messagePanel);
				}
			},
			failure -> AonMessagePanel.showError(messagePanel, failure.getMessage())
		);
	}

	private void loadITStatics() {
		this.tooltipCallback = new TooltipCallBack();
		this.popupPanel = new PopupPanel(true);
		this.tooltip = new ITTooltip() {

			@Override
			protected void onTooltipClick(Integer contractId, Integer itId) {
				openITDialog(contractId, itId);
			}
		};

		AonMessagePanel.hideMessage(messagePanel);
		printTimelineChart();
	}
	
	private final void printTimelineChart() {
		Runnable onLoadCallback = () -> {
			try {
				data = new DataTableWrapper();
				AbstractDataTable dataTable = createTable();
				Options options = createOptions(dataTable);
				timelineChart = new TimeLineChart(dataTable, options);

				if (ifNull)
					staticsPanel.clear();
				else {
					staticsPanel.clear();
					staticsPanel.add(timelineChart);
					new MouseEventsHandlers(timelineChart);
				}

			} catch (Exception ex) {
				Window.alert(ex + " Se ha producido un error, printTimelineChart");
			}
		};

		VisualizationUtils.loadVisualizationApi(onLoadCallback, TimeLineChart.PACKAGE);
	}
	
	private void loadITsList() {
		ItParams params = getParams();
		staticsList = new ITTable(params) {

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onITOpen(Integer contractId, Integer itId) {
				openITDialog(contractId, itId);
			}

		};
		
		centerPanel.setWidget(staticsList);
	}

	private ItParams getParams() {
		ItParams params = new ItParams()
				.setDescription(getSearchTextBox().getValue())
				.setStart(start.getValue())
				.setEnd(end.getValue())
				.setWorkplace(this.workplace)
				.setOrderBy(sort.getValue())
				.setAsc(Boolean.parseBoolean(asc.getValue()))
				;
		
		return params;
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


	// --------------------------------------------------- TimeLineChart.Methods

	private final void reloadTimeline() {
		AbstractDataTable dataTable = createTable();
		Options options = createOptions(dataTable);
		timelineChart = new TimeLineChart(dataTable, options);
		
		timelineChart.getParent().getElement().getStyle().setProperty("display", "flex");
		timelineChart.getParent().getElement().getStyle().setProperty("justify-content", "center");
		
		staticsPanel.clear();
		staticsPanel.add(timelineChart);
		new MouseEventsHandlers(timelineChart);
	}

	private Options createOptions(AbstractDataTable dataTable) {
		Options options = Options.create();

		options.setWidth(deckPanel.getOffsetWidth() - 30);
		options.setHeight(Window.getClientHeight() - 210);

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
		data = new DataTableWrapper();
		ifNull = true;

		for (ITEmployee itEmployee : centineels.values().stream().sorted((o1, o2) -> o1.getEmployeeInfo().getFullName().compareTo(o2.getEmployeeInfo().getFullName())).collect(Collectors.toList())) {

			ifNull = false;

			Date start = itEmployee.getContractInfo().getStartDate();
			Date end = itEmployee.getContractInfo().getEndDate();

			int contractId = itEmployee.getContractInfo().getContractId();

			Date startYear = this.start.getValue() == null ? DateUtils.getFirstDayOfYear() : this.start.getValue();
			Date endYear = this.end.getValue() == null ? DateUtils.getLastDayOfYear(new Date()) : this.end.getValue();
			
			start = DateUtils.after(start, startYear);
			end = DateUtils.before(end, endYear);

			if (!itEmployee.getIts().isEmpty())
				addLeaveRows(itEmployee, contractId, start, end);
			else
				data.addRow(itEmployee.getEmployeeInfo().getFullName(), ACTIVE, start, end, contractId, contractId);
			
		}

		return data.getDataTable();
	}

	private final void addLeaveRows(ITEmployee itEmployee, int contractId, Date start, Date end) {

		Date leaveStart = null;
		Date leaveEnd = null;

		Date startYear = this.start.getValue() == null ? DateUtils.getFirstDayOfYear() : this.start.getValue();
		Date endYear = this.end.getValue() == null ? DateUtils.getLastDayOfYear(new Date()) : this.end.getValue();
		
		for (IT it : itEmployee.getIts()) {

//			if ((DateUtils.compare(it.getStartDate(), endYear) > 0)
//					|| (DateUtils.compare(it.getEndDate(), startYear) < 0))
//				continue;

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

	private void onFIEFileUpload() {
		msjFIEFormPanel.setMethod(METHOD_POST);
		msjFIEFileUpload.click();
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

	private void onLeyend() {
		openLeyend();
	}

	// --------------------------------------------------- ITDialog.Methods

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
			protected void onCommunicateITPart(IT it, ITPart part) {
				startLoading(true);
				sendEconomicData(itEmployee, it, part, s -> {

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

	private void acceptUpdate(ITEmployee itEmployee) {
		createUpdateITEmployee(itEmployee, s -> {
			loadITWidget();
			showCommunicateIT();
		}, f -> {
		});
	}

	private void acceptSave(ITEmployee itEmployee) {
		createUpdateITEmployee(itEmployee, s -> {
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
		deleteIT(itEmployee, it, s -> {
			showDeleteMessage();
			loadITWidget();
		}, f -> {
			showDeleteMessage();
			loadITWidget();
		});
	}

	// --------------------------------------------------- MessagePanel

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}

	// --------------------------------------------------- Data Methdos
	
	private final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();

	private void getEmployeeItList(ItParams params, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
		impl.getEmployeeItList(params, new AsyncCallback<List<ITEmployee>>() {
			
			@Override
			public void onSuccess(List<ITEmployee> result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void getDur(Consumer<DomainUserRoles> success, Consumer<Throwable> failure) {
		impl.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void createUpdateITEmployee(ITEmployee employeeITInfo, Consumer<String> success, Consumer<Throwable> failure) {
		impl.createUpdateITEmployee(employeeITInfo, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(message);	
			}

			@Override
			public void onFailure(Throwable caught) { }
		});
	}
	
	private void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure){
		impl.deleteIT(it.getId(), new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {	
				success.accept(null);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	private void sendEconomicData(ITEmployee itEmployee, IT it, ITPart itPart, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.sendEconomicData(itEmployee, it, itPart, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}}
		);
	}
	
	private void saveITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.saveITParts(list, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}}
		);
	}

	private void removeITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
		impl.removeITParts(list, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}}
		);
	}

	private boolean isUserComunica() {
		return null != this.dur && this.dur.isComunica();
	}

	private ITEmployee getITEmployee(Integer contractId) {
		Optional<ITEmployee> itEmployeeOpt = employeesList.stream().filter(itEmployee -> AonNumberUtils.equals(itEmployee.getContractInfo().getContractId(), contractId)).findFirst();
		return itEmployeeOpt.isEmpty() ? null : itEmployeeOpt.get();
	}

	private IT getIT(Integer itId) {
		Optional<IT> itOpt = itsList.stream().filter(it -> AonNumberUtils.equals(it.getId(), itId)).findFirst();
		return itOpt.isEmpty() ? null : itOpt.get();
	}

	private List<ITEmployee> getActiveEmployeesList(){
		Date today = new Date();
		return employeesList.stream()
				.filter(employeeIT -> null == employeeIT.getContractInfo().getEndDate() || DateUtils.isAfterOrEquals(employeeIT.getContractInfo().getEndDate(), today))
				.collect(Collectors.toList());
	}

	public void setWorkplace(Integer workplaceId) {
		this.workplace = workplaceId;
	}

}
