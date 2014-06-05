package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.visualization.client.visualizations.TimeLineChart.Options.Timeline;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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

public class ITEditor extends AbstractPager implements RequiresResize {

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
	private MenuBar popupMenuBar;

	private Date startYear;
	private Date endYear;

	public ITEditor() {

		nameEmployee = new SuggestBox(names);
		initWidget(binder.createAndBindUi(this));

		this.expressionCallback = new ExpressionCallback();
		this.tooltipCallback = new TooltipCallBack();
		this.tooltip = new Tooltip();
	}

	class MouseEventsHandlers implements MouseOverHandler, ContextMenuHandler,
			ScrollHandler {

		public MouseEventsHandlers(TimeLineChart timelineChart) {
			timelineChart.addMouseOverHandler(this);
			timelineChart.addContextMenuHandler(this);
			timelineChart.addScrollHandler(this);
		}

		@Override
		public void onMouseOver(MouseOverEvent event) {
			try {
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				mouseClientX = event.getClientX();
				mouseClientY = event.getClientY();
				cadenaTooltip = el.getPropertyJSO("logicalname").toString();

				if (tooltip.isShowing() == false) {

					evalTooltip();
				}

			} catch (Throwable ex) {

			} finally {
				event.stopPropagation();
			}
		}

		@Override
		public void onContextMenu(ContextMenuEvent event) {

			Element el = Element.as(event.getNativeEvent().getEventTarget());

			String cadena = el.getPropertyJSO("logicalname").toString();

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

			} catch (Exception ex) {

			} finally {
				event.preventDefault();
				event.stopPropagation();
				event.getNativeEvent();
			}
		}

		@Override
		public void onScroll(ScrollEvent event) {
			// TODO Apéndice de método generado automáticamente
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

			}
		});
	}

	private final void printTimelineChart() {

		Runnable onLoadCallback = new Runnable() {
			public void run() {
				try {
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
					addLeaveRows(employee, contractId, start, end);

				} else {

					/**
					 * contractId is duplicate because obviusly isnt leave when
					 * its active. i havent other constructor in DataTable
					 * Wrapper.
					 */

					data.addRow(employee.getFullname(), ACTIVE, start, end,
							employee.getStartDate(), employee.getEndDate(),
							employee.getDocument(),
							employee.getSocialSecurity(), contractId,
							contractId, -1);
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

			data.addRow(employee.getFullname(), ACTIVE, start, leaveStart,
					employee.getStartDate(), employee.getEndDate(),
					employee.getDocument(), employee.getSocialSecurity(),
					contractId, contractId, -1);

			data.addRow(employee.getFullname(), type.getDescription(),
					leaveStart, leaveEnd, itDataPerson.getLeaveStartDate(),
					itDataPerson.getLeaveEndDate(), employee.getDocument(),
					employee.getSocialSecurity(), contractId, contractLeaveId,
					itDataPerson.getDischarge_cause());

			start = leaveEnd;

		}
		if (DateUtils.equals(start, end) == false) {
			data.addRow(employee.getFullname(), ACTIVE, start, end,
					employee.getStartDate(), employee.getEndDate(),
					employee.getDocument(), employee.getSocialSecurity(),
					contractId, contractId, -1);
		}

	}

	private int mouseClientX;
	private int mouseClientY;

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

	private void getPosStatusEmployee(String element) {

		int auxX = element.indexOf("vR\":") + 4;
		int auxY = element.indexOf("uR\":") - 2;

		posColumn = Integer.parseInt(cadenaTooltip.substring(auxX, auxY));

		auxX = element.indexOf("uR\":") + 4;
		auxY = element.indexOf("}}");

		posCell = Integer.parseInt(element.substring(auxX, auxY));

	}

	protected boolean isLeaveEmployee(String pElement) {

		getPosStatusEmployee(pElement);
		String cadena = pElement;

		if (cadena.contains("\"vR\":") && cadena.contains("\"uR\":")
				&& data.isActive(posColumn, posCell) == false) {
			return true;
		} else {
			return false;
		}
	}

	private void tratarContrato(String pElement) {

		getPosStatusEmployee(pElement);

		String fullName = data.getFullName(posColumn, posCell);
		String dni = data.getDocument(posColumn, posCell);
		String segSocial = data.getSocialSecurity(posColumn, posCell);
		String estado = data.getStatus(posColumn, posCell);
		Date startDate = data.getStartDate(posColumn, posCell);
		String formatStartDate = format.format(startDate);
		Date endDate = data.getEndDate(posColumn, posCell);
		String formatEndDate = "...";

		if (endDate != null) {
			formatEndDate = format.format(endDate);
		}

		Date rowStart = data.getRowStartDate(posColumn, posCell);
		Date rowEndDate = data.getRowEndDate(posColumn, posCell);

		int dischargeCause = data.getDischargeCause(posColumn, posCell);
		int typeTooltip = data.getTypeTooltip(posColumn, posCell);

		tooltip = new Tooltip();

		tooltip.setFullName(fullName);
		tooltip.setDNI(dni);
		tooltip.setSocialSecurity(segSocial);
		tooltip.setColor(getColor(estado));
		tooltip.setStatus(estado);
		tooltip.setWorkPeriod(formatStartDate, formatEndDate);
		tooltip.setNumDays(startDate, endDate);
		tooltip.setDischargeCause(dischargeCause);

		/**
		 * 0 - contract active 1 - contract ended 2 - leave active 3 - leave
		 * ended
		 */

		switch (typeTooltip) {
		case 0:
			this.showContractActiveTooltip(tooltip, mouseClientX, mouseClientY);
			break;
		case 1:
			this.showContractEndedTooltip(tooltip, mouseClientX, mouseClientY);
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

	private int decremental = 0;

	protected void showLeaveEndedTooltip(Tooltip tooltip, final int clientX,
			final int clientY) {
		tooltip.showLeaveEndedTooltip(clientX, clientY);
	}

	protected void showContractActiveTooltip(final Tooltip tooltip,
			final int clientX, final int clientY) {

		tooltip.showContractActiveTooltip(clientX, clientY);

		tooltip.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {

				try {

					if (tooltip.isAccept()) {

						Date leaveStartDate = tooltip.getStartDateBoxValue();
						Date leaveEndDate = tooltip.getFromDateBoxValue();

						try {
							if (leaveEndDate == null) {
								leaveEndDate = data.getStartDate(posColumn,
										posCell + 1);
							}
						} catch (Exception ex) {
						}

						int leaveType = tooltip.getTypeLeaveListBox();
						int contractId = data.getContractId(posColumn, posCell);

						decremental = decremental - 1;
						ITDataPerson newData = new ITDataPerson();
						newData.setContractId(contractId);
						newData.setContractLeaveId(decremental);
						newData.setType(getEnumConstant(
								ITDataPerson.Type.class, leaveType));
						newData.setDischarge_cause(tooltip
								.getTypeDischargeListBox());
						newData.setLeaveStartDate(leaveStartDate);
						newData.setLeaveEndDate(leaveEndDate);
						dataObject.addLeaveItem(newData);
						reloadTimeline();
					}

				} catch (Throwable ex) {

				}
			}
		});

	}

	protected void showContractEndedTooltip(Tooltip tooltip, final int clientX,
			final int clientY) {
		tooltip.showContractEndedTooltip(clientX, clientY);

	}

	protected void showLeaveActiveTooltip(final Tooltip tooltip,
			final int clientX, final int clientY) {

		tooltip.showLeaveActiveTooltip(clientX, clientY);

		tooltip.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {

				if (tooltip.isAccept()) {

					try {

						int leaveType = tooltip.getTypeLeaveListBox();
						int contractId = data.getContractId(posColumn, posCell);
						int leaveId = data.getContractLeaveId(posColumn,
								posCell);
						int dischargeCause = tooltip.getDischargeCause();

						Date leaveEndDate = tooltip.getFromDateBoxValue();
						Date leaveStartDate = tooltip.getStartDateBoxValue();

						ITDataPerson newDataPerson = new ITDataPerson();

						newDataPerson.setContractId(contractId);
						newDataPerson.setContractLeaveId(leaveId);
						newDataPerson.setLeaveStartDate(leaveStartDate);
						newDataPerson.setLeaveEndDate(leaveEndDate);
						newDataPerson.setDischarge_cause(dischargeCause);
						newDataPerson.setType(getEnumConstant(
								ITDataPerson.Type.class, leaveType));						
						dataObject.updateItem(newDataPerson);
						reloadTimeline();
						

					} catch (Exception ex) {
						Window.alert(ex.getMessage() + " " + ex.getCause()
								+ " " + ex.getStackTrace());
					}
				}
			}
		});
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

	class LeaveContextMenu extends ContextMenu {

		DeleteContractCommand deleteContract;

		public LeaveContextMenu() {

			popupMenuBar = new MenuBar(true);

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
				int contractLeave = data.getContractLeaveId(posColumn, posCell);
				dataObject.removeLeaveItem(dataObject.getDataIts(contractId)
						.get(contractLeave));
				reloadTimeline();
			} catch (Exception ex) {

			}
		}

	}

	class EmployeeContextMenu extends ContextMenu {

		AddContractCommand addContract;

		public EmployeeContextMenu() {

			popupMenuBar = new MenuBar(true);

			MenuItem add = addItem("Nuevo Contrato",
					addContract = new AddContractCommand(), AON.AON_ICON_RESET,
					AON.AON_ICON_CMD_BUTTON);

			popupMenuBar.addItem(add);
			popupMenuBar.setVisible(true);
			popupPanel.add(popupMenuBar);
		}
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

		@Override
		public void run() {

			if (cadenaTooltip.contains("axis")) {
				// Click sobre el Mes. De momento no hago nada
			}
			if (cadenaTooltip.contains("\"vR\":")) {
				// Click en tipo de contrato
				tratarContrato(cadenaTooltip);
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
				int discharge_cause) {

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

		public int getDischarge_cause() {

			return discharge_cause + 1;
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

}
