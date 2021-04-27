package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
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
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public abstract class ITWidget extends ResizeComposite {
	
	// --------------------------------------------------- UiBinder

	private static ITWidgetUiBinder uiBinder = GWT.create(ITWidgetUiBinder.class);

	interface ITWidgetUiBinder extends UiBinder<Widget, ITWidget> {}

	// --------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String filterPanel();
		String flexPanel();
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	ScrollPanel scrollPanelTimeline;
	
	@UiField
	DockLayoutPanel mainContainerDockLPanel;
	
	@UiField
	HTMLPanel filterITListPanel;
	
	@UiField
	HTMLPanel mainITContainer;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	HTMLPanel timelinePanel;
	
	// --------------------------------------------------- Variables
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private List<ITEmployee> itEmployeeList = Collections.emptyList();
	
	private SuggestBox employeeSB;
	private ListBox dateListBox;
	private CheckBox allContracts;
	
	// --------------------------------------------------- TimeLineChart.Variables
	
	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
	
	private static final String ACTIVE = "Activo";
	
	private static int selectedYear;
	private Date startYear;
	private Date endYear;
	
	private static LinkedList<LinkedList<Status>> myEmployees;

	private boolean ifNull; // Evitar el Null del Timeline

	private Map<Integer, ITEmployee> centineels;

	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();

	private static TimeLineChart timelineChart;

	private ITTooltip tooltip;
	private DataTableWrapper data;
	private Options options;
	
	private int posCell; // vR
	private int posColumn; // uR
	
	private ExpressionCallback expressionCallback;
	private TooltipCallBack tooltipCallback;

	private String cadenaTooltip;

	private PopupPanel popupPanel;
	
	// --------------------------------------------------- Toolbar.Variables
	
	private AonToolbar toolbar;
	private AonToolbarButton addIT;
	private AonToolbarButton leyend;
	
	private AonToolbarButton msjFIE;
	private FormPanel msjFIEFormPanel;
	private Hidden userNameHidden;
	private Hidden domainNameHidden;
	private MultiFileUpload msjFIEFileUpload;

	// --------------------------------------------------- Constructor

	public ITWidget() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
	}

	// --------------------------------------------------- TimeLineChart.MouseEventsHandlers
    
    private class MouseEventsHandlers extends DecoratedPopupPanel implements MouseOverHandler, ContextMenuHandler, ClickHandler {

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
			
			if(AonStringUtils.isBlank(cadenaTooltip))
				return;
				
			try {	
				
				if (cadenaTooltip.contains("{\"type\":\"bar\"")) {
					
					tratarContrato(cadenaTooltip, mouseClientX, mouseClientY);
					
					int contractId = data.getContractId(posColumn, posCell);
					int leaveId = data.getContractLeaveId(posColumn, posCell);
					
					if(AonNumberUtils.equals(contractId, leaveId)) {
						checkTooltipCB();
					} else {
						tooltipCallback.setProperties(mouseClientX, mouseClientY);
						evalTooltip();
					}
				} else {
					checkTooltipCB();
				}

			} catch (Throwable ex) {

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
			
			if(AonStringUtils.isBlank(cadenaTooltip))
				return;
			
			try {					
				if (isLeaveEmployee(cadenaTooltip)) {	
					
					checkTooltipCB();
					
					tratarContrato(cadenaTooltip, mouseClientX, mouseClientY);
					
					int leaveId = data.getContractLeaveId(posColumn, posCell);
					IT it = getIT(leaveId); //mainContrataITObject.getITs(leaveId);
					
					if(!isUserComunica() /*mainContrataITObject.isUserComunica()*/ && itIsNotComunicate(it)){
						popupPanel = new PopupPanel(true);					
						new ITContextMenu();					
						popupPanel.setPopupPosition(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY());					
						popupPanel.show();							
					}
					
				}
				
			} catch (Throwable ex) {
				
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
			
			if(AonStringUtils.isBlank(cadenaTooltip))
				return;
				
			try {	
				
				checkTooltipCB();
				
				if (cadenaTooltip.contains("{\"type\":\"bar\"")) {
					
					tratarContrato(cadenaTooltip, mouseClientX, mouseClientY);
					
					int contractId = data.getContractId(posColumn, posCell);
					int leaveId = data.getContractLeaveId(posColumn, posCell);
					
					if(AonNumberUtils.equals(contractId, leaveId))
						openNewITDialog(contractId);
					else
						openITDialog(contractId, leaveId);
				}

			} catch (Throwable ex) {

			} finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}
		}
		
		private boolean itIsNotComunicate(IT it) {
			return null == it.isComunicate() || !it.isComunicate();
		}
		
		private void checkTooltipCB() {
			if (tooltipCallback.isRunning())
				tooltipCallback.cancel();
			tooltip.hide();
		}
		
	}
    
    // --------------------------------------------------- ExpressionCallback
    
    class ExpressionCallback extends Timer {
		@Override
		public void run() {
			String container = employeeSB.getText().toUpperCase();
			
			if (AonStringUtils.isEmpty(container))
				reloadTimeline();
			else if (container.length() > 2)
				reloadTimeline();
		}
	}
    
    // --------------------------------------------------- TooltipCallback
    
    class TooltipCallBack extends Timer {
		
		private int mouseClientX;
		private int mouseClientY;
		
		public TooltipCallBack() {}
		
		public void setProperties(int ClientX, int ClientY) {
			this.mouseClientX = ClientX;
			this.mouseClientY = ClientY;
		}
	
		@Override
		public void run() {
			int contractId = data.getContractId(posColumn, posCell);
			int itId = data.getContractLeaveId(posColumn, posCell);
			
			IT itInfo = getIT(itId); //mainContrataITObject.getITs(itId);
	    	ITEmployee itEmployee = getITEmployee(contractId); //mainContrataITObject.getITEmployee(contractId);
	    	
	    	tooltip.setFullName(itEmployee.getEmployeeInfo().getFullName());
	    	tooltip.setDocument(itEmployee.getEmployeeInfo().getDocument());
	    	tooltip.setNaf(itEmployee.getEmployeeInfo().getSsNumber());
	    	tooltip.setComunicationStatus(itInfo.isComunicate());
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
    
    private void evalTooltip() {
		tooltipCallback.cancel();
		tooltipCallback.schedule(750);
	}
    
    // --------------------------------------------------- ContextMenu
	
	class ITContextMenu extends ContextMenu {
		
		DeleteContractCommand deleteContract;
		ComunicateITCommand comunicateIT;
		
		public ITContextMenu() {
			MenuBar popupMenuBar = new MenuBar(true);
			
			MenuItem deleteMenuItem = addItem(
					"Eliminar Baja",
					deleteContract = new DeleteContractCommand(),
					AON.CSS.aonIconDelete(), style.cmd_btn());
			
			// TODO : to add delete option
			// popupMenuBar.addItem(deleteMenuItem);
			
			MenuItem comunicateMenuItem = addItem(
					"Comunicar IT",
					comunicateIT = new ComunicateITCommand(),
					AON.CSS.aonIconSend(), style.cmd_btn());
			
			popupMenuBar.addItem(comunicateMenuItem);
			
			popupMenuBar.setVisible(true);
			popupPanel.add(popupMenuBar);
		}
	}

	class DeleteContractCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			try {
				popupPanel.hide();	
				
				int contractId = data.getContractId(posColumn, posCell);				
				int leaveId = data.getContractLeaveId(posColumn, posCell);
				
				IT it = getIT(leaveId); //mainContrataITObject.getITs(leaveId);
				ITEmployee itEmployee = getITEmployee(contractId); //mainContrataITObject.getITEmployee(contractId);
				
				deleteLeave(itEmployee, it);
				
			} catch (Exception ex) {}
		}
		
	}
	
	class ComunicateITCommand implements ScheduledCommand {
		
		@Override
		public void execute() {
			try {
				popupPanel.hide();	
				
				int contractId = data.getContractId(posColumn, posCell);				
				int leaveId = data.getContractLeaveId(posColumn, posCell);
				
				IT it = getIT(leaveId); //mainContrataITObject.getITs(leaveId);
				ITEmployee itEmployee = getITEmployee(contractId); //mainContrataITObject.getITEmployee(contractId);
				
				// Paternidad / Maternidad
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3)
					comunicatePaternity(itEmployee, it);
				else
					cominicateIT(itEmployee, it);
				
			} catch (Exception ex) {}
		}

	}
	
	// --------------------------------------------------- OnModuleLoad
	
	public void loadITWidget() {
		getITEmployeeListDB(itEmployeeList -> {
			setITEmployees(itEmployeeList);
			
			this.expressionCallback = new ExpressionCallback();
			this.tooltipCallback = new TooltipCallBack();
			this.popupPanel = new PopupPanel(true);
			this.tooltip = new ITTooltip() {
				
				@Override
				protected void onTooltipClick(Integer contractId, Integer itId) {
					openITDialog(contractId, itId);
				}
			};
			
			getFilterITListPanel();
			initDateListBox();
			initSuggestBox();
			printTimelineChart();
			
		}, f -> {});
	}
	
	private void getFilterITListPanel() {
		filterITListPanel.clear();
		filterITListPanel.setStyleName(AON.CSS.aonSearchPanel());
		filterITListPanel.addStyleName(AON.CSS.aonScrollArea());
		filterITListPanel.addStyleName(AON.CSS.aonMarginBottom());
		filterITListPanel.addStyleName(AON.CSS.aonMarginLeft());
		filterITListPanel.addStyleName(AON.CSS.aonMarginRight());
		filterITListPanel.addStyleName(AON.CSS.aonBlockCenter());
		
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
		cleanSB.addClickHandler(e -> {
			employeeSB.setValue("", true);
		});
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

		for (Integer year : getAviableYears() /*mainContrataITObject.getAviableYears()*/)
			dateListBox.addItem(String.valueOf(year));

		dateListBox.setSelectedIndex(0);
		
		selectedYear = Integer.parseInt(dateListBox.getItemText(dateListBox.getSelectedIndex()));

		startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0, selectedYear));
		// Check if selected year is current year
		int actualYear = DateUtils.getYear();
		if(AonNumberUtils.equals(selectedYear, actualYear)) {
			Date nextMonth = DateUtils.addMonths2Date(new Date(), 1);
			endYear = DateUtils.getLastDayOfMonth(nextMonth);
		} else
			endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11, selectedYear));
		
		dateListBox.addChangeHandler(e -> {
			selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox.getSelectedIndex()));

			startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,selectedYear));
			
			// Check if selected year is current year
			int currentYear = DateUtils.getYear();
			if(AonNumberUtils.equals(selectedYear, currentYear)) {
				Date nextMonth = DateUtils.addMonths2Date(new Date(), 1);
				endYear = DateUtils.getLastDayOfMonth(nextMonth);
			} else
				endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11, selectedYear));
			
			initSuggestBox();
			reloadTimeline();
		});
	}

	private final void initSuggestBox() {
		names.clear();
		
		centineels = new LinkedHashMap<Integer, ITEmployee>();
		
		Date startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0, selectedYear));
		Date endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11, selectedYear));
		
		for (ITEmployee itEmployee : getFilterITEmployeeList(allContracts.getValue(), startYear, endYear) /*mainContrataITObject.getEmployeesList(allContracts.getValue(), startYear, endYear)*/) {

			int contractId = itEmployee.getContractInfo().getContractId();

			if ((DateUtils.compare(itEmployee.getContractInfo().getStartDate(), endYear) <= 0) && 
				(DateUtils.compare(itEmployee.getContractInfo().getEndDate(), startYear) >= 0)) {
				
				names.add(itEmployee.getEmployeeInfo().getFullName());
				centineels.put(contractId, itEmployee);
			}
		}
		
		if(centineels.isEmpty())
			showMessage();
		else
			showTimeLine();
		
		employeeSB.addValueChangeHandler(e -> {
			expressionCallback.cancel();
			expressionCallback.schedule(1500);
		});
	}

	private final void printTimelineChart() {		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
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
					
				} catch (Throwable ex) {
					Window.alert(ex + " Se ha producido un error, printTimelineChart");
				}
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
		options = Options.create();
		
		options.setWidth(scrollPanelTimeline.getOffsetWidth() - 30);
		options.setHeight(scrollPanelTimeline.getOffsetHeight() - 70);

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

		List<String> statusList = new LinkedList<String>();
		
		for (int row = 0; row < dataTable.getNumberOfRows(); row++) {
			String status = dataTable.getValueString(row, 1);
			if (!statusList.contains(status))
				statusList.add(status);
		}

		int i = 0;
		String colors[] = new String[statusList.size()];
		for (String status : statusList)
			colors[i++] = getColor(status);

		options.setColors(colors);

		options.setEnableInteractivity(false);

		return options;
	}

	private final AbstractDataTable createTable() {
		String container = employeeSB.getText().toUpperCase();
		
		data = new DataTableWrapper();
		ifNull = true;

		for (ITEmployee itEmployee : centineels.values()) {

			String fullName = itEmployee.getEmployeeInfo().getFullName();
			
			if (fullName.contains(container)) {

				ifNull = false;

				Date start = itEmployee.getContractInfo().getStartDate();
				Date end = itEmployee.getContractInfo().getEndDate();

				int contractId = itEmployee.getContractInfo().getContractId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (itEmployee.getIts().size() > 0)
					addLeaveRows(itEmployee, 
							contractId, 
							start, 
							end);

				else
					data.addRow(
							itEmployee.getEmployeeInfo().getFullName(), 
							ACTIVE, 
							start, 
							end,
							contractId,
							contractId);
			}
		}
		
		return data.getDataTable();
	}

	private final void addLeaveRows(ITEmployee itEmployee, int contractId, Date start, Date end) {

		Date leaveStart = null;
		Date leaveEnd = null;

		for (IT it : itEmployee.getIts()) {

			if ((DateUtils.compare(it.getStartDate(), endYear) > 0) || 
				(DateUtils.compare(it.getEndDate(), startYear) < 0))
				continue;

			Byte type = it.getTypeLowPart();
			Date leaveEndAux = it.getEndDate();

			if (leaveEndAux == null)
				leaveEndAux = end;

			Date itStartDate = getRealStartDate(it);
					
//			leaveStart = DateUtils.after(it.getStartDate(), startYear);
			leaveStart = DateUtils.after(itStartDate, startYear);
			leaveEnd = DateUtils.before(leaveEndAux, endYear);

			int contractLeaveId = it.getId();
			
			// skip previous IT
			if (start.before(leaveStart))
				data.addRow(itEmployee.getEmployeeInfo().getFullName(), 
						ACTIVE, 
						start, 
						leaveStart,
						contractId, 
						contractId);

			data.addRow(
					itEmployee.getEmployeeInfo().getFullName(), 
					getTypeDescription(type),
					leaveStart, 
					leaveEnd, 
					contractId, 
					contractLeaveId);

			start = leaveEnd;

		}
		
		if (!DateUtils.equals(start, end))
			data.addRow(
					itEmployee.getEmployeeInfo().getFullName(), 
					ACTIVE, 
					start, 
					end,
					contractId, 
					contractId);
	}
	
	// --------------------------------------------------- TimeLineChart.Auxiliar_Methods

	private static <T extends JavaScriptObject> T parseJson(String json) {
		return JsonUtils.safeEval(json);
	}

	private void getPosStatusEmployee(String element) {
		
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
		getPosStatusEmployee(pElement);

		if (cadena.contains("{\"type\":\"bar\"") && !AonStringUtils.equals(data.getStatus(posColumn, posCell), ACTIVE)) {
			return true;
		} else {
			return false;
		}
	}

	private void tratarContrato(String pElement, int mouseClientX, int mouseClientY) {
		getPosStatusEmployee(pElement);
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
		if((byte) 1 == it.getTypeLowPart()) {
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
		default:
			return "Enfermedad Com\u00FAn, Prestaci\u00F3n Profesional (COVID-19)";
		}
	}

	// --------------------------------------------------- TimeLineChart.DataTableWrapper

	public static class DataTableWrapper {

		private int row;
		private DataTable data;
		private LinkedList<Status> statusList;

		public DataTableWrapper() {
			row = 0;
			myEmployees = new LinkedList<LinkedList<Status>>();
			data = DataTable.create();
			initColumns();
		}

		private void initColumns() {
			data.addColumn(ColumnType.STRING, "Nombre");
			data.addColumn(ColumnType.STRING, "Estado");
			data.addColumn(ColumnType.DATE, "Inicio");
			data.addColumn(ColumnType.DATE, "Fin");
		}

		public void addRow(String pName, String pStatus, Date pRowStartDate,
				Date pRowEndDate, int pContractId, int contractLeaveId) {

			data.addRow();

			data.setValue(row, 0, pName);
			data.setValue(row, 1, pStatus);
			data.setValue(row, 2, pRowStartDate);
			data.setValue(row, 3, pRowEndDate);

			this.row++;

			if (myEmployees.isEmpty()) {
				statusList = new LinkedList<Status>();
				myEmployees.add(statusList);
			} else {
				if (myEmployees.getLast().getLast().getFullName().equals(pName) == false) {
					statusList = new LinkedList<Status>();
					myEmployees.add(statusList);
				}
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
		
		public Status() {}

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

	// --------------------------------------------------- TimeLineChart.getLogicalName()
	
	private static class JsLogicalName extends JavaScriptObject {

		private static class JsData extends JavaScriptObject {
			
			protected JsData() {}

			public final native int getvR() /*-{
											return this.vR;
											}-*/;

			public final native int getuR() /*-{
											return this.uR;
											}-*/;
		}

		protected JsLogicalName() {}

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
			return json; // JsonUtils.safeEval(json);

		Document doc = Document.get();
		//
		if (IFrameElement.is(el)) {
			x -= el.getAbsoluteLeft();
			y -= el.getAbsoluteTop();
			doc = IFrameElement.as(el).getContentDocument();
		}

		NodeList<Element> rects = doc.getElementsByTagName("rect");

		for (int i = 0; i < rects.getLength(); i++) {

			Element rect = rects.getItem(i);

			json = rect.getPropertyString("logicalname");
			if (json == null)
				continue;

			JsLogicalName logicalName = JsonUtils.safeEval(json);

			if (!"bar".equals(logicalName.getType()))
				continue;

			if (isElementAt(rect, x, y))
				return json;
		}

		return null;
	}

	private static boolean isElementAt(Element el, int x, int y) {
		if (x < el.getAbsoluteLeft())
			return false;
		if (x > el.getAbsoluteLeft() + el.getOffsetWidth())
			return false;
		if (y < el.getAbsoluteTop())
			return false;
		if (y > el.getAbsoluteTop() + el.getOffsetHeight())
			return false;
		return true;
	}

	// --------------------------------------------------- FromJS to ITEmployee, IT, ContractInfo, EmployeeInfo
	
	private static ITEmployee fromJsITEmployee(JsITEmployee jsITEmployee) {
		ITEmployee itEmployee = new ITEmployee();
		
		JsEmployeeInfo jsEmployeeInfo = jsITEmployee.getEmployeeInfo();
		EmployeeInfo employeeInfo = fromJsEmployeeInfo(jsEmployeeInfo);
		
		JsContractInfo jsContractInfo = jsITEmployee.getContractInfo();
		ContractInfo contractInfo = fromJsContractInfo(jsContractInfo);
		
		JsArray<JsIT> jsITs = jsITEmployee.getITs();
		List<IT> its = new ArrayList<IT>();
		for ( int i = 0; i < jsITs.length(); i++ ) {
			its.add(fromJsIT(jsITs.get(i)));
		}

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
		it.setEndDate(parseDate(jsIT.getEndDate()));
		it.setFullName(jsIT.getFullName());
		it.setMaternityReason(jsIT.getMaternityReason());
		it.setMaternityType(jsIT.getMaternityType());
		it.setId(jsIT.getId());
		it.setIsParent(jsIT.isParent());
		it.setITParts(createDefaultITParts(jsIT));
		it.setParent(jsIT.getParent());
		it.setStartDate(parseDate(jsIT.getStartDate()));
		it.setTypeHighPart(jsIT.getTypeHighPart());
		it.setTypeLowPart(jsIT.getTypeLowPart());
		return it;
	}
	
	private static List<ITPart> createDefaultITParts(JsIT jsIT) {
		List<ITPart> itParts = new ArrayList<ITPart>();
		
		// Low ITPart
		ITPart itPart = new ITPart();
		itPart.setType((byte)0);
		itPart.setDomain(jsIT.getDomain());
		itPart.setDate(parseDate(jsIT.getStartDate()));
		
		itParts.add(itPart);
		
		return itParts;
	}

	private static ContractInfo fromJsContractInfo(JsContractInfo jsContractInfo) {
		ContractInfo contractInfo = new ContractInfo();	
		contractInfo.setActivityId(jsContractInfo.getActivityId());
		contractInfo.setEnterpriseCIF(jsContractInfo.getEnterpriseCIF());
		contractInfo.setCccId(jsContractInfo.getCccId());
		contractInfo.setCompleteCCC(jsContractInfo.getCompleteCCC());
		contractInfo.setCccType(jsContractInfo.getCccType());
		contractInfo.setWorkplaceId(jsContractInfo.getWorkplaceId());
		contractInfo.setWorkplaceZIP(jsContractInfo.getWorkplaceZIP());
		contractInfo.setWorkplaceFullAddress(jsContractInfo.getWorkplaceFullAddress());
		contractInfo.setContractType(jsContractInfo.getContractType());
		contractInfo.setContractModel(jsContractInfo.getContractModel());
		contractInfo.setStartDate(parseDate(jsContractInfo.getStartDate()));
		contractInfo.setEndDate(parseDate(jsContractInfo.getEndDate()));
		contractInfo.setSeniorityDate(parseDate(jsContractInfo.getSeniorityDate()));
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
		contractInfo.setOldStartDate(parseDate(jsContractInfo.getOldStartDate()));
		contractInfo.setOldEndDate(parseDate(jsContractInfo.getOldEndDate()));
		contractInfo.setHasPayroll(jsContractInfo.getHasPayroll());
		contractInfo.setPayrollDate(parseDate(jsContractInfo.getPayrollDate()));
		return contractInfo;
	}

	private static EmployeeInfo fromJsEmployeeInfo(JsEmployeeInfo jsEmployeeInfo) {		
		EmployeeInfo employeeInfo = new EmployeeInfo();
		
		employeeInfo.setAccount(jsEmployeeInfo.getAccount());
		employeeInfo.setAddresNum(jsEmployeeInfo.getAddresNum());
		employeeInfo.setAddress(jsEmployeeInfo.getAddress());
		employeeInfo.setAddressCity(jsEmployeeInfo.getAddressCity());
		employeeInfo.setAddressInfo(jsEmployeeInfo.getAddressInfo());
		employeeInfo.setAddressProvinces(jsEmployeeInfo.getAddressProvinces());
		employeeInfo.setAddressZip(jsEmployeeInfo.getAddressZip());
		employeeInfo.setBic(jsEmployeeInfo.getBic());
		employeeInfo.setBirthdate(parseDate(jsEmployeeInfo.getBirthdate()));
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
		employeeInfo.setGeozoneId(jsEmployeeInfo.getGeozoneId());
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
	
	private static Date parseDate(String str) {
		if ( str == null )
			return null;
		if (str.trim().length() == 0 )
			return null;
		try {
			return DateTimeFormat.getFormat("yyyy-MM-dd").parse(str);
		} catch ( IllegalArgumentException e ) {
			return null;
		}
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
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #A0C3FF;\" title=\"Periodo Activo del Empleado\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Activo</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FFA500;\" title=\"Enfermedad Com&uacute;n, Accidente no Laboral\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Accidente no Laboral</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #AA0033;\" title=\"Enfermedad Profesional\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Profesional</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FF66CC;\" title=\"Maternidad, Lactancia, Riesgo Durante el Embarazo\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Maternidad</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #36C;\" title=\"Baja por Paternidad\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Paternidad</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #FFA500;\" title=\"Enfermedad Com&uacute;n, Periodo de Carencia\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Periodo de Carencia</a>";
		leyend += "</div>";
		
		leyend += "<div style=\"display: flex; align-items: center; gap: 10px; padding: 2px 5px;\">";
		leyend += "<a style=\"width: 13px; height: 13px; background-color: #E3DC14;\" title=\"Enfermedad Com&uacute;n, Prestaci&oacute;n Profesional (COVID-19)\"></a>";
		leyend += "<a style=\"text-decoration: none; color: black; font-weight: bold;\">Enfermedad Com&uacute;n, Prestaci&oacute;n Profesional (COVID-19)</a>";
		leyend += "</div>";
		
		leyend += "</div>";
		
		AonDialog leyendDialog = new AonDialog("Leyenda", new HTML(leyend));
		leyendDialog.info();
	}
	
	// --------------------------------------------------- Toolbar

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("Partes IT");
		
		// FORM
		msjFIEFormPanel = new FormPanel();
		msjFIEFormPanel.setMethod(FormPanel.METHOD_POST);
		msjFIEFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		msjFIEFormPanel.setAction(FIEService.FIE_URL);
		
		userNameHidden = new Hidden(FIEService.Parameter.USER.name(), Wnd.getCurrentUser());
		domainNameHidden = new Hidden(FIEService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
		
		msjFIEFileUpload = new MultiFileUpload();
		msjFIEFileUpload.setName(FIEService.Parameter.FILE.name());
		msjFIEFileUpload.setVisible(false);
		msjFIEFileUpload.setAccept(".msj");
		msjFIEFileUpload.addChangeHandler(e -> {
			msjFIEFormPanel.submit();
		});
		msjFIEFormPanel.addSubmitCompleteHandler(e -> {
			String json = e.getResults();
			
			JsArray<JsITEmployee> jsITEmployees = eval("(" + json + ")");
			
			List<ITEmployee> itEmployees = new ArrayList<ITEmployee>(jsITEmployees.length());
			
			for (int i = 0; i < jsITEmployees.length(); i++ ) {
				JsITEmployee jsITEmployee = jsITEmployees.get(i);			
				ITEmployee itEmployee = fromJsITEmployee(jsITEmployee);
				itEmployees.add(itEmployee); 		
			}
			
			setITEmployeeList(itEmployees, s -> {
				loadITWidget();
			}, f -> {});
			
//			mainContrataITObject.setEmployeesInfo(itEmployees,
//				s -> {
//					loadMainContrataIT();
//				},
//				f -> {}
//			);
		});
		
		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		
		msjFIEFormPanel.add(formFlowPanel);
		toolbar.add(msjFIEFormPanel);
		
		addIT = new AonToolbarButton( "Nueva IT", AON.CSS.aonIconAdd() );
		addIT.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAddIT(event);
			}
		});
		toolbar.add(addIT);
		
		msjFIE = new AonToolbarButton( "Mensaje del INSS Empresa (FIE)", AON.CSS.aonIconTgssFie() );
		msjFIE.setAccessKey('F');
		msjFIE.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onFIE(event);
			}
		});
		toolbar.add(msjFIE);
		
		leyend = new AonToolbarButton( "Leyenda", AON.CSS.aonIconInfo() );
		leyend.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onLeyend(event);
			}
		});
		toolbar.add(leyend);

		return toolbar;

	}
	
	// --------------------------------------------------- Toolbar.Methods
	
	private void onAddIT(ClickEvent event) {
		newITDialog();
	}
	
	private void onFIE(ClickEvent event) {
		msjFIEFileUpload.click();
	}
	
	private void onLeyend(ClickEvent event) {
		openLeyend();
	}
	
	// --------------------------------------------------- ITDialog.Methods
	
	private void openNewITDialog(int contractId) {
		ITEmployee itEmployee = getITEmployee(contractId); //mainContrataITObject.getITEmployee(contractId);
    	ITDialog itDialog = newITDialog();
    	itDialog.setITEmployee(itEmployee);
	}

	private ITDialog newITDialog() {
		ITDialog itDialog = new ITDialog("Creaci\u00F3n") {

    		@Override
			protected void onAccept() {
    			accept(getITEmployee());
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
			protected void onComunicateIT(IT it) {}
    		
    	};
    	
    	itDialog.setEmployeesList(getActiveEmployeesList() /*mainContrataITObject.getActiveEmployeesList()*/);
    	itDialog.initConfirmationsTable();
		itDialog.setModal(true);
    	itDialog.setAnimationEnabled(true);
		itDialog.center();
		itDialog.show();
		
		return itDialog;
	}

	private void openITDialog(int contractId, int itId) {
		IT itInfo = getIT(itId); //mainContrataITObject.getITs(itId);
    	ITEmployee itEmployee = getITEmployee(contractId); //mainContrataITObject.getITEmployee(contractId);

    	ITDialog itDialog = new ITDialog("Edici\u00F3n") {
    		
    		@Override
			protected void onAccept() {
				accept(itEmployee);
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
			protected void onComunicateIT(IT it) {
				// Paternity / Maternity
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3)
					comunicatePaternity(itEmployee, it);
				else
					cominicateIT(itEmployee, it);
			}
			
    	};
    	
    	ITDialogObject itDialogObject = new ITDialogObject(itEmployee);
    	itDialog.setITDialogObject(itDialogObject, itInfo, true);
    	itDialog.setIsUserComunica(isUserComunica() /*mainContrataITObject.isUserComunica()*/);
    	
    	itDialog.setModal(true);
    	itDialog.setAnimationEnabled(true);
    	itDialog.show();
    	itDialog.center();
	}	
	
	private void accept(ITEmployee itEmployee) {
		setITEmployee(itEmployee, s -> {
			loadITWidget();
		}, f -> {});
		
//		mainContrataITObject.createUpdateITEmployee(itEmployee,
//				s -> {
//					loadMainContrataIT();
//				},
//				f -> {});
	}
	
	private void deleteLeave(ITEmployee itEmployee, IT it) {
		if(ITDialog.isPartenityPart(it))
			deletePaternity(it, itEmployee);
		else
			delete(it, itEmployee);
	}
	
	private void delete(IT it, ITEmployee itEmployee) {
		
		deleteIT(itEmployee, it, s -> {
			loadITWidget();
		}, f -> {});
		
//		mainContrataITObject.removeIT(itEmployee, it,
//				s -> {
//					if(it.isComunicate() && mainContrataITObject.isUserComunica())
//						mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
//							loadMainContrataIT();
//						}, d -> {});
//					else
//						loadMainContrataIT();
//				},
//				f -> {});
		
	}

	private void deletePaternity(IT it, ITEmployee itEmployee) {
		
		deletePaternityIT(itEmployee, it, s -> {
			loadITWidget();
		}, f -> {});
		
//		mainContrataITObject.deleteIT(it,
//				s -> {
//					if(it.isComunicate() && mainContrataITObject.isUserComunica())
//						mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
//							loadMainContrataIT();
//						}, d -> {});
//					else
//						loadMainContrataIT();
//				},
//				f -> {});
	}
	
	private void cominicateIT(ITEmployee itEmployee, IT it) {
		AonConfirmDialog comunicateDialog = new AonConfirmDialog();
		comunicateDialog.confirm(
				"COMUNIC" + String.valueOf("\u0040"), 
				String.valueOf("\u00BF") + "Desea comunicar el parte IT?",
				new AonConfirmDialogCallback() {
					@Override
					public void onAccept() {
						
						comunicateIT(itEmployee, it, s -> {
							getITCertificatePDF(itEmployee, it);
						}, f -> {});
						
//						mainContrataITObject.comunicateITBaja(itEmployee, it, t -> {
//							AonConfirmDialog dialog = new AonConfirmDialog();
//							dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
//							
//							getITCertificatePDF(itEmployee, it);
//						}, d -> {});
					}

					@Override
					public void onCancel() {}
				}
		);
	}

	private void comunicatePaternity(ITEmployee itEmployee, IT it) {
		AonConfirmDialog comunicateDialog = new AonConfirmDialog();
		comunicateDialog.confirm(
				"COMUNIC" + String.valueOf("\u0040"), 
				String.valueOf("\u00BF") + "Desea comunicar el parte IT?",
				new AonConfirmDialogCallback() {
					@Override
					public void onAccept() {
						
						comunicatePaternityIT(itEmployee, it, s -> {
							getITCertificatePDF(itEmployee, it);
						}, f -> {});
						
//						mainContrataITObject.comunicatePaternityIT(itEmployee, it, t -> {
//							AonConfirmDialog dialog = new AonConfirmDialog();
//							dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
//							
//							getITCertificatePDF(itEmployee, it);
//						}, d -> {});
					}

					@Override
					public void onCancel() {}
				}
		);
	}
	
	private void getITCertificatePDF(ITEmployee itEmployee, IT it) {
		getNafxIpf(itEmployee, s -> {
			String affiliationNumber = s.getNss();
			String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
			String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
			String dateFromStr = formatFullDate.format(new Date());
			String dateToStr = formatFullDate.format(new Date());
			String startDateStr = formatFullDate.format(it.getStartDate());
			
			String fileDownloadURL = GWT.getModuleBaseURL()+ "it_export/";
			String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
			 		+ "&userLogin=" + Wnd.getCurrentUser()
		            + "&affiliationNumber=" + affiliationNumber
		            + "&regime=" + regime
		            + "&contributionAccount=" + contributionAccount
		            + "&dateFromStr=" + dateFromStr
					+ "&dateToStr=" + dateToStr
					+ "&startDateStr=" + startDateStr
					+ "&itType=" + it.getTypeLowPart();
			
			Window.open(fileDownloadURL+query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
		}, f -> {});
		
//		mainContrataITObject.getNafxIpf(itEmployee, s -> {
//			String affiliationNumber = s.getNss();
//			String regime = itEmployee.getContractInfo().getCompleteCCC().substring(0, 4);
//			String contributionAccount = itEmployee.getContractInfo().getCompleteCCC().substring(4, itEmployee.getContractInfo().getCompleteCCC().length());
//			String dateFromStr = formatFullDate.format(new Date());
//			String dateToStr = formatFullDate.format(new Date());
//			String startDateStr = formatFullDate.format(it.getStartDate());
//			
//			String fileDownloadURL = GWT.getModuleBaseURL()+ "it_export/";
//			String query = "?domainName=" + Wnd.getCurrentDomainNameURL()
//			 		+ "&userLogin=" + Wnd.getCurrentUser()
//		            + "&affiliationNumber=" + affiliationNumber
//		            + "&regime=" + regime
//		            + "&contributionAccount=" + contributionAccount
//		            + "&dateFromStr=" + dateFromStr
//					+ "&dateToStr=" + dateToStr
//					+ "&startDateStr=" + startDateStr
//					+ "&itType=" + it.getTypeLowPart();
//			
//			Window.open(fileDownloadURL+query, "ITExporter", "resizable=yes,scrollbars=yes,status=yes");
//		}, f -> {});
	}
	
	// --------------------------------------------------- Setters
	
	public void setITEmployees(List<ITEmployee> itEmployeeList) {
		this.itEmployeeList = itEmployeeList;
	}
	
	// --------------------------------------------------- Abstract Methdos
	
	protected abstract void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure);
	
	public abstract boolean isUserComunica();
	public abstract SortedSet<Integer> getAviableYears();
	
	public abstract ITEmployee getITEmployee(Integer contractId);
	public abstract IT getIT(Integer itId);
	
	protected abstract List<ITEmployee> getActiveEmployeesList();
	protected abstract List<ITEmployee> getFilterITEmployeeList(Boolean allContracts, Date start, Date end);

	protected abstract void setITEmployeeList(List<ITEmployee> itEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure);
	protected abstract void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure);
	
	protected abstract void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure);
	protected abstract void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure);

	protected abstract void comunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure);
	protected abstract void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure);
	
	protected abstract void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure);

}
