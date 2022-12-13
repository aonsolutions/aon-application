package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.client.ITDataObject.CallculateCallback;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public class ITEditor extends AbstractPager implements RequiresResize,
		CallculateCallback {

	private static final String ACTIVE = "Activo";
	private static int selectedYear;

	interface Style extends CssResource {

		@ClassName("legend-icon")
		String legendIcon();

		@ClassName("legend-caption")
		String legendCaption();

	}

	interface Binder extends UiBinder<Widget, ITEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Style style;

	@UiField
	Button saveButton;

	@UiField
	Button undoButton;

	@UiField
	Button redoButton;

	@UiField
	ScrollPanel scrollPanelTimeline;

	@UiField
	SimplePanel timelinePanel;

	@UiField(provided = true)
	SuggestBox nameEmployee;

	@UiField
	Label titleLabel;

	@UiField
	ListBox dateListBox;

	private static final DateTimeFormat format = DateTimeFormat
			.getFormat(PredefinedFormat.DATE_LONG);
	
	private static LinkedList<LinkedList<Status>> myEmployees;

	private boolean finalizado;
	private boolean ifNull; // Evitar el Null del Timeline

	private Map<Integer, Employee> centineels;

	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();

	private static TimeLineChart timelineChart;

	private Tooltip tooltip;
	private ITDataObject dataObject;
	private DataTableWrapper data;
	private Options options;

	private String cadenaTooltip;

	// private static int DEFAULT_INCREMENT = 50;

	private PopupPanel popupPanel;

	private Date startYear;
	private Date endYear;

	public ITEditor() {

		nameEmployee = new SuggestBox(names);
		initWidget(binder.createAndBindUi(this));
		
		this.expressionCallback = new ExpressionCallback();
		this.tooltipCallback = new TooltipCallBack();
		this.popupPanel = new PopupPanel(true);
		this.tooltip = new Tooltip();
	}

	private class MouseEventsHandlers extends DecoratedPopupPanel 
	implements MouseOverHandler, ContextMenuHandler, ClickHandler {

		public MouseEventsHandlers(TimeLineChart timelineChart) {
			timelineChart.addMouseOverHandler(this);			
			timelineChart.addContextMenuHandler(this);
			timelineChart.addClickHandler(this);
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			try {	
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());				
				int mouseClientX = event.getClientX();
				int mouseClientY = event.getClientY();								
				cadenaTooltip = getLogicalName(el, mouseClientX, mouseClientY);
				
				if (cadenaTooltip == null)
					return;

				if (tooltip.isShowing() == false
						&& popupPanel.isShowing() == false) {
					tooltipCallback.setProperties(cadenaTooltip, mouseClientX, mouseClientY);
					evalTooltip();
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

			Element el = Element.as(event.getNativeEvent().getEventTarget());
			int mouseClientX = event.getNativeEvent().getClientX();
			int mouseClientY = event.getNativeEvent().getClientY();
			String cadena = getLogicalName(el, mouseClientX, mouseClientY);
			if(cadena == null)
				return;
			try {				
				if (isLeaveEmployee(cadena)) {					
					popupPanel = new PopupPanel(true);					
					new LeaveContextMenu();					
					popupPanel.setPopupPosition(event.getNativeEvent()
							.getClientX(), event.getNativeEvent().getClientY());					
					popupPanel.show();					
					if (tooltipCallback.isRunning())
						tooltipCallback.cancel();
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
			
			try {
				tooltipCallback.cancel();
			//	Window.alert("Click!");
				
			}catch (Throwable ex) {
				
			}finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}
			
		}		
	}	
	
	class LeaveContextMenu extends ContextMenu {
		DeleteContractCommand deleteContract;
		public LeaveContextMenu() {
			MenuBar popupMenuBar = new MenuBar(true);
			MenuItem add = addItem("Eliminar Baja",
					deleteContract = new DeleteContractCommand(),
					AON.AON_ICON_DELETE, AON.AON_ICON_CMD_BUTTON);
			popupMenuBar.addItem(add);
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
				dataObject.removeLeaveItem(contractId, leaveId);				
				reloadTimeline();
			} catch (Exception ex) {
			}
		}
	}
	
	private int decremental = 0;

	private class TooltipController implements Tooltip.Listener, CloseHandler<PopupPanel> {
		
		private static final String UPDATE = "UPDATE";
		private static final String INTERVAL = "Intervalo de fechas no correcto";
		private static final String LEAVE_EXIST = "Baja existente en el per\u00EDodo indicado";
		private static final String OUT_PERIOD = "Baja fuera del periodo de contrato";
		
		private Tooltip tooltip;
		private String action;
		private int contractId;
		private int leaveId;
		
		private Date startContract;
		private Date endContract;

		public TooltipController(Tooltip tooltip, String action) {			
			this.tooltip = tooltip;
			this.action = action;
			this.contractId = tooltip.getContractId();
			this.leaveId = tooltip.getLeaveId();
			this.tooltip.addCloseHandler(this);
			this.tooltip.addListener(this);			
			this.startContract = dataObject.getEmployees().get(contractId).getStartDate();
			this.endContract = dataObject.getEmployees().get(contractId).getEndDate();
		}

		@Override
		public void onStartDateChangeEvent(ValueChangeEvent<Date> event) {
			
			Date startDate = event.getValue();		
			
			if(getEndDateBoxError() == false)
				initStyles();			
			
			
			boolean correct = dataObject.isCorrectStartDateLeave(contractId,
					leaveId, startDate);

			if (correct == false) {
				tooltip.startLeaveDateBox.setStyleName(AON.AON_ICON_ERROR, true);
				tooltip.startLeaveDateBox.setTitle(LEAVE_EXIST);
				tooltip.acceptButton.setEnabled(false);
				return;
			}	

			if( DateUtils.compare(startDate, startContract) < 0 || DateUtils.compare(startDate, endContract) > 0){
				tooltip.startLeaveDateBox.setStyleName(AON.AON_ICON_WARN, true);
				tooltip.startLeaveDateBox.setTitle(OUT_PERIOD);
				//tooltip.acceptButton.setEnabled(false);
				//return;				
			}			
			
			initStyles();
		}

		@Override
		public void onEndDateChangeEvent(ValueChangeEvent<Date> event) {		
			
			Date endDate = event.getValue();
			Date startDate = tooltip.getStartDateBoxValue();
			
			if(getStartDateBoxError() == false) 
				initStyles();			
			
			if(DateUtils.compare(endDate, startContract) < 0 || DateUtils.compare(endDate, endContract) > 0) {
				tooltip.endDateBox.setStyleName(AON.AON_ICON_ERROR, true);
				tooltip.endDateBox.setTitle(OUT_PERIOD);
				tooltip.acceptButton.setEnabled(false);
				return;
			}
			
			boolean correct = dataObject.isCorrectEndDateLeave(contractId, leaveId, 
					startDate, endDate);
			
			if(correct == false) {
				tooltip.endDateBox.setStyleName(AON.AON_ICON_ERROR, true);
				tooltip.endDateBox.setTitle(LEAVE_EXIST);
				tooltip.acceptButton.setEnabled(false);
				return;
			}
			
			initStyles();
		}
		
		private boolean getStartDateBoxError() {
			return tooltip.startLeaveDateBox.getStyleName().contains(AON.AON_ICON_ERROR)
					|| tooltip.startLeaveDateBox.getStyleName().contains(AON.AON_ICON_WARN);
		}
		
		private boolean getEndDateBoxError() {
			return tooltip.endDateBox.getStyleName().contains(AON.AON_ICON_ERROR)
					|| tooltip.endDateBox.getStyleName().contains(AON.AON_ICON_WARN);
		}
		
		@Override
		public void onAcceptButtonClickEvent(ClickEvent event) {			
			
			if(tooltip.typeLeaveListBox.getSelectedIndex() == 0) {
				tooltip.typeLeaveListBox.setFocus(true);
				return;
			}
			
			Date startDate = tooltip.getStartDateBoxValue();
			Date endDate = tooltip.getFromDateBoxValue();
			
			if(DateUtils.compare(startDate, endDate) > 0) {
				tooltip.startLeaveDateBox.setStyleName(AON.AON_ICON_WARN, true);
				tooltip.endDateBox.setStyleName(AON.AON_ICON_WARN, true);
				tooltip.startLeaveDateBox.setTitle(INTERVAL);
				tooltip.endDateBox.setTitle(INTERVAL);
				return;
			}
			
			tooltip.setAccept(true);
			tooltip.hide();
		}	

		@Override
		public void onClose(CloseEvent<PopupPanel> event) {
			if(tooltip.isAccept()) {
				
				int leaveType = tooltip.getTypeLeaveListBox();								
				int dischargeCause = tooltip.getTypeDischargeListBox();
				Date startDate = tooltip.getStartDateBoxValue();
				Date endDate = getNextEndDate(tooltip.getFromDateBoxValue());
				
				ITDataPerson newDataPerson = new ITDataPerson();
				newDataPerson.setContractId(tooltip.getContractId());
				newDataPerson.setLeaveStartDate(startDate);
				newDataPerson.setLeaveEndDate(endDate);
				newDataPerson.setDischarge_cause(dischargeCause);
				newDataPerson.setNumType(leaveType);
				newDataPerson.setType(getEnumConstant(
						ITDataPerson.Type.class, leaveType));
				newDataPerson.setRegBase(tooltip.getRegBase());
				if(action.equals(UPDATE)) {
					newDataPerson.setContractLeaveId(tooltip.getLeaveId());
					dataObject.updateLeaveItem(newDataPerson);
				}
				else {
					newDataPerson.setContractLeaveId(--decremental);
					dataObject.addLeaveItem(newDataPerson);
				}
				
				reloadTimeline();
			}			
		}
		
		private Date getNextEndDate(Date endDate) {
			
			if(endDate == null) {				
				try {
					endDate = data.getStartDate(posColumn, posCell + 1);
				}catch(Exception ex){}				
			}
			
			return endDate;
		}	
		
		private void initStyles() {			
			tooltip.startLeaveDateBox.removeStyleName(AON.AON_ICON_ERROR);
			tooltip.endDateBox.removeStyleName(AON.AON_ICON_ERROR);
			tooltip.startLeaveDateBox.removeStyleName(AON.AON_ICON_WARN);
			tooltip.endDateBox.removeStyleName(AON.AON_ICON_WARN);
			tooltip.startLeaveDateBox.setTitle("");
			tooltip.endDateBox.setTitle("");
			tooltip.acceptButton.setEnabled(true);									
		}
	}
 
	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		titleLabel.setText(title);
	}

	public final void setITEditor(final ITDataObject dataObject) {
		dataObject.load(new AsyncCallback<ITDataObject>() {

			@Override
			public void onSuccess(ITDataObject dataObject) {				
				ITEditor.this.dataObject = dataObject;
				dataObject.addListener(new UndoListener());
				initDateListBox();
				finalizado = false;
				initSuggestBox();
				printTimelineChart();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
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
					
					if (ifNull == false) {
						timelinePanel.setWidget(timelineChart);
						new MouseEventsHandlers(timelineChart);
					} else {
						timelinePanel.clear();
					}
				} catch (Throwable ex) {
					Window.alert(ex
							+ " Se ha producido un error, printTimelineChart");
				}
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				TimeLineChart.PACKAGE);
	}

	private final void reloadTimeline() {
		AbstractDataTable dataTable = createTable();
		Options options = createOptions(dataTable);
		timelineChart = new TimeLineChart(dataTable, options);
		timelinePanel.setWidget(timelineChart);
		new MouseEventsHandlers(timelineChart);
	}

	private Options createOptions(AbstractDataTable dataTable) {

		options = Options.create();

		options.setWidth((8 * scrollPanelTimeline.getOffsetWidth()) / 9);
		options.setHeight((8 * scrollPanelTimeline.getOffsetHeight()) / 9);

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
		String container = nameEmployee.getText().toUpperCase();
		data = new DataTableWrapper();
		ifNull = true;

		for (Employee employee : centineels.values()) {

			employee.getPerson();
			String fullName = employee.getFullname();

			if (fullName.contains(container)) {

				ifNull = false;

				/*
				 * if (data.getSizeEmployees() > DEFAULT_INCREMENT &&
				 * data.getEmployees().containsKey(idPerson) == false) {
				 * finalizado = false; break; }
				 */

				Date start = employee.getStartDate();
				Date end = employee.getEndDate();

				int contractId = employee.getId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (dataObject.getDataIts(contractId).size() > 0) {
					addLeaveRows(employee, 
							contractId, 
							start, 
							end);

				} else {

					/**
					 * contractId is duplicate because obviusly isnt leave when
					 * its active. i havent other constructor in DataTable
					 * Wrapper.
					 */

					data.addRow(employee.getFullname(), 
							ACTIVE, 
							start, 
							end,
							employee.getStartDate(), 
							employee.getEndDate(),
							employee.getDocument(),
							employee.getSocialSecurity(), 
							contractId,
							contractId, 
							-1, 
							new ITDataPerson());
				}

				finalizado = true;
			}
		}
		return data.getDataTable();
	}

	private final void addLeaveRows(Employee employee, int contractId,
			Date start, Date end) {

		Date leaveStart = null;
		Date leaveEnd = null;

		for (ITDataPerson itDataPerson : dataObject.getDataIts(contractId)
				.values()) {

			if ((DateUtils.compare(itDataPerson.getLeaveStartDate(), endYear) > 0)
					|| (DateUtils.compare(itDataPerson.getLeaveEndDate(),
							startYear) < 0))
				continue;

			Type type = itDataPerson.getType();
			Date leaveEndAux = itDataPerson.getLeaveEndDate();

			if (leaveEndAux == null) {
				leaveEndAux = end;
			}

			leaveStart = DateUtils.after(itDataPerson.getLeaveStartDate(),
					startYear);
			leaveEnd = DateUtils.before(leaveEndAux, endYear);

			int contractLeaveId = itDataPerson.getContractLeaveId();
			
			// skip previous IT
			if ( start.before(leaveStart))
				data.addRow(employee.getFullname(), 
						ACTIVE, 
						start, 
						leaveStart,
						employee.getStartDate(), 
						employee.getEndDate(),
						employee.getDocument(), 
						employee.getSocialSecurity(),
						contractId, 
						contractId, 
						-1, 
						new ITDataPerson());

			data.addRow(employee.getFullname(), 
					type.getDescription(),
					leaveStart, 
					leaveEnd, 
					itDataPerson.getLeaveStartDate(),
					itDataPerson.getLeaveEndDate(), 
					employee.getDocument(),
					employee.getSocialSecurity(), 
					contractId, 
					contractLeaveId,
					itDataPerson.getDischarge_cause(), 
					itDataPerson);

			start = leaveEnd;

		}
		if (DateUtils.equals(start, end) == false) {
			data.addRow(
					employee.getFullname(), 
					ACTIVE, 
					start, 
					end,
					employee.getStartDate(), 
					employee.getEndDate(),
					employee.getDocument(), 
					employee.getSocialSecurity(),
					contractId, 
					contractId, 
					-1, 
					new ITDataPerson());
		}

	}

	// private void loadTimelineEvents() {

	/*
	 * timelineChart.addScrollHandler(new ScrollHandler() {
	 * 
	 * int lastScrollPos = 0;
	 * 
	 * public void onScroll(ScrollEvent event) {
	 * 
	 * EventTarget target = event.getNativeEvent().getEventTarget();
	 * 
	 * Element el = Element.as(target);
	 * 
	 * int scrollPos = timelineChart.getVerticalScrollPosition(el);
	 * 
	 * if (lastScrollPos >= scrollPos) { lastScrollPos = scrollPos; return; }
	 * 
	 * lastScrollPos = scrollPos;
	 * 
	 * int maxScrollPos = timelineChart .getMaximumVerticalScrollPosition(el);
	 * 
	 * if ((lastScrollPos + (maxScrollPos / 10)) >= maxScrollPos && finalizado
	 * == false) { Window.alert("Acercandose"); //DEFAULT_INCREMENT += 50;
	 * //printTimelineChart(itData);
	 * 
	 * } } });
	 */

	private void evalTooltip() {
		tooltipCallback.cancel();
		tooltipCallback.schedule(750);
	}

	private int posCell; // vR
	private int posColumn; // uR
	
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
		
/*		while ( iterator.hasNext() ) {
			Window.alert(iterator.next());
		}
		
		int auxX = element.indexOf("uO\":") + 4;
		int auxY = element.indexOf("sO\":") - 2;

		posColumn = Integer.parseInt(cadenaTooltip.substring(auxX, auxY));

		auxX = element.indexOf("sO\":") + 4;
		auxY = element.indexOf("}}");

		posCell = Integer.parseInt(element.substring(auxX, auxY));*/

	}

	protected boolean isLeaveEmployee(String pElement) {

		String cadena = pElement;
		getPosStatusEmployee(pElement);
		if (cadena.contains("{\"type\":\"bar\"")
				&& data.isActive(posColumn, posCell) == false) {			
			return true;
		} else {
			return false;
		}
	}

	private void tratarContrato(String pElement, int mouseClientX, int mouseClientY) {

		getPosStatusEmployee(pElement);

		String fullName = data.getFullName(posColumn, posCell);
		String dni = data.getDocument(posColumn, posCell);
		String segSocial = data.getSocialSecurity(posColumn, posCell);
		int contractId = data.getContractId(posColumn, posCell);
		int leaveId = data.getContractLeaveId(posColumn, posCell);
		String estado = data.getStatus(posColumn, posCell);
		String regBase = data.getITDataPerson(posColumn, posCell).getRegBase();
		Date startDate = data.getStartDate(posColumn, posCell);
		String formatStartDate = format.format(startDate);
		Date endDate = data.getEndDate(posColumn, posCell);
		String formatEndDate = "...";

		if (endDate != null) {
			formatEndDate = format.format(endDate);
		}

		int dischargeCause = data.getDischargeCause(posColumn, posCell);
		int typeTooltip = data.getTypeTooltip(posColumn, posCell);

		tooltip = new Tooltip();

		tooltip.setFullName(fullName);
		tooltip.setDNI(dni);
		tooltip.setSocialSecurity(segSocial);
		tooltip.setContractId(contractId);
		tooltip.setLeaveId(leaveId);
		tooltip.setColor(getColor(estado));
		tooltip.setStatus(estado);
		tooltip.setWorkPeriod(formatStartDate, formatEndDate);
		tooltip.setNumDays(startDate, endDate);
		tooltip.setDischargeCause(dischargeCause);
		tooltip.setRegBase(regBase);

		/**
		 * 0 - contract active 1 - contract ended 2 - leave active 3 - leave
		 * ended
		 */

		switch (typeTooltip) {
		case 0:
			this.showContractActiveTooltip(tooltip, mouseClientX, mouseClientY);
			break;
		case 1:
			this.showContractActiveTooltip(tooltip, mouseClientX, mouseClientY);
			break;
		case 2:
			this.showLeaveActiveTooltip(tooltip, mouseClientX, mouseClientY);
			break;
		case 3:
			this.showLeaveActiveTooltip(tooltip, mouseClientX, mouseClientY);
		default:
			break;
		}
	}

	protected void showLeaveEndedTooltip(Tooltip tooltip, final int clientX,
			final int clientY) {
		tooltip.showLeaveEndedTooltip(clientX, clientY);
	}

	protected void showContractActiveTooltip(final Tooltip tooltip,
			final int clientX, final int clientY) {

		tooltip.showContractActiveTooltip(clientX, clientY);
		new TooltipController(tooltip, "INSERT");
	}

	protected void showContractEndedTooltip(Tooltip tooltip, final int clientX,
			final int clientY) {
		tooltip.showContractEndedTooltip(clientX, clientY);

	}

	protected void showLeaveActiveTooltip(final Tooltip tooltip,
			final int clientX, final int clientY) {
		tooltip.showLeaveActiveTooltip(clientX, clientY);
		new TooltipController(tooltip, "UPDATE");
	}

	

	class AddContractCommand implements ScheduledCommand {

		public AddContractCommand() {
		}

		@Override
		public void execute() {

			popupPanel.hide();
			Window.alert("Nuevo contracto");
		}
	}

	class ExpressionCallback extends Timer {

		@Override
		public void run() {

			String container = nameEmployee.getText().toUpperCase();

			if (StringUtils.isEmpty(container)) {
				// DEFAULT_INCREMENT = 50;
				reloadTimeline();
			} else if (container.length() > 2) {
				reloadTimeline();
			}
		}
	}

	class TooltipCallBack extends Timer {
		
		private String cadenaTooltip;
		private int mouseClientX;
		private int mouseClientY;
		
		public TooltipCallBack() {
			
		}
		
		public void setProperties(String cadenaTooltip, int ClientX, int ClientY) {
			this.cadenaTooltip = cadenaTooltip;
			this.mouseClientX = ClientX;
			this.mouseClientY = ClientY;
		}
	
		@Override
		public void run() {

			if (cadenaTooltip.contains("axis")) {
				// Click sobre el Mes. De momento no hago nada
			}
			if (cadenaTooltip.contains("{\"type\":\"bar\"")) {
				// Click en tipo de contrato
				tratarContrato(cadenaTooltip, mouseClientX, mouseClientY);
			}
			if (cadenaTooltip.contains("index")) {
				// Click fuera del contrato
				// tratarFueraContrato(cadenaTooltip);
			}
		}

	}

	// ------------------------------------------

	private void enableUndoRedoButtons() {
		undoButton.setEnabled(dataObject.canUndo());
		redoButton.setEnabled(dataObject.canRedo());

		saveButton.setEnabled(dataObject.saveActive());

	}

	// ------------------------------------------------------------- UiHandlers

	private ExpressionCallback expressionCallback;
	private TooltipCallBack tooltipCallback;

	@UiHandler("undoButton")
	void onUndoSelected(ClickEvent event) {
		dataObject.undo();
		reloadTimeline();
	}

	@UiHandler("redoButton")
	void onRedoSelected(ClickEvent event) {
		dataObject.redo();
		reloadTimeline();
	}

	@UiHandler("saveButton")
	void onClick(ClickEvent event) {		
		dataObject.save(this);		
		saveButton.setEnabled(dataObject.saveActive());		
	}

	@UiHandler("dateListBox")
	void onYearChanged(ChangeEvent event) {

		selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox
				.getSelectedIndex()));

		startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		endYear = DateUtils.getLastDayOfYear(DateUtils
				.getDate(11, selectedYear));
		initSuggestBox();
		reloadTimeline();
	}

	@UiHandler("nameEmployee")
	void onExpressionKeyUp(KeyUpEvent event) {
		expressionCallback.cancel();
		expressionCallback.schedule(1500);
	}

	private void initDateListBox() {
		dateListBox.clear();

		for (int x = 0; x < dataObject.getYears().size(); x++) {
			dateListBox.addItem(String.valueOf(dataObject.getYears().get(x)));
		}

		dateListBox.setSelectedIndex(dateListBox.getItemCount() - 1);
		selectedYear = Integer.parseInt(dateListBox.getItemText(dateListBox
				.getSelectedIndex()));

		startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		endYear = DateUtils.getLastDayOfYear(DateUtils
				.getDate(11, selectedYear));
	}

	private final void initSuggestBox() {

		centineels = new LinkedHashMap<Integer, Employee>();
		Date startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		Date endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11,
				selectedYear));
		names.clear();
		for (Employee employee : dataObject.getEmployees().values()) {

			int contractId = employee.getId();

			if ((DateUtils.compare(employee.getStartDate(), endYear) <= 0)
					&& (DateUtils.compare(employee.getEndDate(), startYear) >= 0)) {
				names.add(employee.getFullname());
				centineels.put(contractId, employee);
			}
		}
	}

	@Override
	protected void onRangeOrRowCountChanged() {
		// TODO Apéndice de método generado automáticamente
	}

	@Override
	public void onResize() {
		/*
		 * Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		 * 
		 * @Override public void execute() { Runnable onLoadCallback = new
		 * Runnable() { public void run() { try { AbstractDataTable dataTable =
		 * getTimelineDataTable(); Options options = getOptions();
		 * options.setWidth((8 * scrollPanelTimeline.getOffsetWidth()) / 9);
		 * options.setHeight((8 * scrollPanelTimeline.getOffsetHeight()) / 9);
		 * timelineChart = new TimeLineChart(dataTable, options);
		 * timelinePanel.setWidget(timelineChart); new
		 * MouseEventsHandlers(timelineChart);
		 * 
		 * } catch (Throwable ex) { Window.alert(ex +
		 * " Se ha producido un error"); } } };
		 * VisualizationUtils.loadVisualizationApi(onLoadCallback,
		 * TimeLineChart.PACKAGE); } });
		 */

	}

	private class UndoListener implements UndoManager.Listener {

		@Override
		public void onChange(UndoManager undoManager) {
			enableUndoRedoButtons();
		}

	}

	// TODO: To EnumUtils ???
	public static <T extends Enum<?>> T getEnumConstant(Class<T> enumClass,
			Integer ordinal) {
		if (ordinal == null)
			return null;
		if (ordinal < 0)
			return null;

		T constants[] = enumClass.getEnumConstants();
		if (ordinal >= constants.length)
			return null;

		return constants[ordinal];
	}

	@Override
	public void onCalculateSuccess(ITDataObject object) {

		boolean dataObjectChanged = this.dataObject != object;

		if (dataObjectChanged) {
			this.dataObject = object;
			this.dataObject.addListener(new UndoListener());
			this.initDateListBox();
			this.finalizado = false;
			this.initSuggestBox();
			this.printTimelineChart();
		}
	}

	@Override
	public void onCalculateFailure(Throwable throwable) {		
		Window.alert(throwable.getMessage());
	}

	// ---------------------------------------------------------------- Library

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
				Date pRowEndDate, Date pStartDate, Date pEndDate, String pDni,
				String pSocialSecurity, int pContractId, int contractLeaveId,
				int discharge_cause, ITDataPerson dataPerson) {

			data.addRow();

			data.setValue(row, 0, pName);
			data.setValue(row, 1, pStatus);
			data.setValue(row, 2, pRowStartDate);
			data.setValue(row, 3, pRowEndDate);

			this.row++;

			if (myEmployees.isEmpty()) {
				statusList = new LinkedList<ITEditor.Status>();
				myEmployees.add(statusList);
			} else {
				if (myEmployees.getLast().getLast().getFullName().equals(pName) == false) {
					statusList = new LinkedList<ITEditor.Status>();
					myEmployees.add(statusList);
				}
			}
			statusList.add(new Status());
			myEmployees.getLast().getLast().setFullName(pName);
			myEmployees.getLast().getLast().setDNI(pDni);
			myEmployees.getLast().getLast().setSocialSecurity(pSocialSecurity);
			myEmployees.getLast().getLast().setITDataPerson(dataPerson);
			myEmployees.getLast().getLast().setEstado(pStatus);
			myEmployees.getLast().getLast().setRowStartDate(pRowStartDate);
			myEmployees.getLast().getLast().setRowEndDate(pRowEndDate);
			myEmployees.getLast().getLast().setStartDate(pStartDate);
			myEmployees.getLast().getLast().setEndDate(pEndDate);
			myEmployees.getLast().getLast().setContractId(pContractId);
			myEmployees.getLast().getLast().setContractLeaveId(contractLeaveId);
			myEmployees.getLast().getLast().setDischarge_cause(discharge_cause);
			myEmployees.getLast().getLast().calculateTypeTooltip();

		}

		public AbstractDataTable getDataTable() {
			return data;
		}

		private String getFullName(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getFullName();
		}

		private String getDocument(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getDNI();
		}

		private String getSocialSecurity(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getSocialSecurity();
		}
		
		private ITDataPerson getITDataPerson(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getITDataPerson();
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

		private Date getStartDate(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getStartDate();
		}

		private Date getEndDate(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getEndDate();
		}

		private Date getRowStartDate(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getRowStartDate();
		}

		private Date getRowEndDate(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getRowEndDate();
		}

		private int getDischargeCause(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getDischarge_cause();
		}

		private int getTypeTooltip(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).getTypeTooltip();
		}

		private boolean isActive(int posColumn, int posCell) {
			return myEmployees.get(posColumn).get(posCell).isActive();
		}
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

	private static String getColor(String status) {
		return getColor(getType(status));

	}

	private static ITDataPerson.Type getType(String description) {
		for (ITDataPerson.Type type : Type.values())
			if (StringUtils.equals(type.getDescription(), description))
				return type;
		return null;
	}

	private static class Status {

		private String fullName;
		private String dni;
		private String socialSecurityNum;
		private ITDataPerson dataPerson;
		private int contractId;
		private int contractLeaveId;
		private String estado;
		private Date startDate;
		private Date endDate;
		private Date rowStartDate;
		private Date rowEndDate;
		private int discharge_cause;

		/**
		 * 0 - contract active 1 - contract ended 2 - leave active 3 - leave
		 * ended
		 */
		private int typeTooltip;

		public Status() {

		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public void setDNI(String pDni) {
			dni = pDni;
		}

		public String getDNI() {
			return dni;
		}

		public void setSocialSecurity(String pSocialSecurity) {
			socialSecurityNum = pSocialSecurity;
		}

		public String getSocialSecurity() {
			return socialSecurityNum;
		}
		
		public void setITDataPerson(ITDataPerson dataPerson) {
			this.dataPerson = dataPerson;
		}
		
		public ITDataPerson getITDataPerson() {
			return dataPerson;
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

		public boolean isActive() {

			if (getEstado().equals(ACTIVE)) {
				return true;
			} else {
				return false;
			}
		}

		public void setEstado(String estado) {
			this.estado = estado;
		}

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Date getRowStartDate() {
			return rowStartDate;
		}

		public void setRowStartDate(Date rowStartDate) {
			this.rowStartDate = rowStartDate;
		}

		public Date getRowEndDate() {
			return rowEndDate;
		}

		public void setRowEndDate(Date rowEndDate) {
			this.rowEndDate = rowEndDate;
		}

		/**
		 * @return discharge_cause - Min value -1
		 */

		public int getDischarge_cause() {
			return discharge_cause;
		}

		public void setDischarge_cause(int discharge_cause) {
			this.discharge_cause = discharge_cause;
		}

		private void setTypeTooltip(int pTypeTooltip) {
			typeTooltip = pTypeTooltip;
		}

		public int getTypeTooltip() {
			return typeTooltip;
		}

		/**
		 * 0 - contract active 1 - contract ended 2 - contractLeave active 3 -
		 * contractLeave ended
		 */

		public void calculateTypeTooltip() {

			if (selectedYear == DateUtils.getYear()) {

				if (getEstado().equals(ACTIVE)) {

					if (DateUtils.isAfterOrEquals(getEndDate(), new Date()))
						setTypeTooltip(0);
					else
						setTypeTooltip(1);
				} else {
					if (DateUtils.isAfterOrEquals(getEndDate(), new Date()))
						setTypeTooltip(2);
					else
						setTypeTooltip(3);
				}
			}

			else {

				if (getEstado().equals(ACTIVE))
					setTypeTooltip(1);
				else
					setTypeTooltip(3);
			}
		}
	}

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
	
}
