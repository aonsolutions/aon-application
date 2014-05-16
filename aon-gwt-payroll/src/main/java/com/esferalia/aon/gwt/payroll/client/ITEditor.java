package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.Timeline;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson.Type;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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

	interface Style extends CssResource {

		@ClassName("legend-icon")
		String legendIcon();

		@ClassName("legend-caption")
		String legendCaption();
		
	}
	

	interface Binder extends UiBinder<Widget, ITEditor> {
	}

	class ExpressionCallback extends Timer {	

		@Override
		public void run() {
			container = nameEmployee.getText().toUpperCase();
			
			if (StringUtils.isEmpty(container)) {
				//DEFAULT_INCREMENT = 50;
				printTimelineChart(itData);
			} else if (container.length() > 2) {
				
				printTimelineChart(itData);
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
				//tratarFueraContrato(cadenaTooltip);
			}		
		}
		
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Style style;
	
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
	
	
	
	private static final DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
			//.getFormat("dd/MM/yyyy");
	
	//private static Map<Integer, String> employees;
	private static LinkedList<LinkedList<Status>> myEmployees;
	private int selectedYear;	
	private boolean finalizado;
	private boolean ifNull; // Evitar el Null del Timeline
	private Map<Integer, Employee> centineels;
	private DataTableWrapper data;
	
	private EmployeeContextMenu menuEmployee;
	
	private MultiWordSuggestOracle names = new MultiWordSuggestOracle();	
	
	private static TimeLineChart timelineChart;
	
	private Tooltip tooltip;
	
	private String cadenaTooltip;
	private String container;
	
	//private static int DEFAULT_INCREMENT = 50;	
	
	private Date startYear;
	private Date endYear;

	private com.esferalia.aon.gwt.payroll.shared.ITData itData;

	public ITEditor() {

		nameEmployee = new SuggestBox(names);
		initWidget(binder.createAndBindUi(this));
		nameEmployee.setWidth("300px");
		container = "";
		
		this.expressionCallback = new ExpressionCallback();
		this.tooltipCallback = new TooltipCallBack();
		
		tooltip = new Tooltip();		
		
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		titleLabel.setText(title);

	}

	public final void setITEditor(
			final com.esferalia.aon.gwt.payroll.shared.ITData pITData) {

		this.itData = pITData;
		initDateListBox();
		finalizado = false;
		initSuggestBox();
		printTimelineChart(itData);

	}

	private void printTimelineChart(
			final com.esferalia.aon.gwt.payroll.shared.ITData pITData) {
		
	
		
		Runnable onLoadCallback = new Runnable() {

			public void run() {
				try {
					// Create a pie chart visualization.
					AbstractDataTable dataTable = createTable(container);
					Options options = createOptions(dataTable);
					timelineChart = new TimeLineChart(dataTable, options);
					
					if(ifNull == false) {
						timelinePanel.setWidget(timelineChart);
						loadTimelineEvents();
					}else {
						timelinePanel.clear();
					}					

				} catch (Throwable ex) {
					Window.alert(ex + " Se ha producido un error");
				}
			}
		};

		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				TimeLineChart.PACKAGE);
	}
	
	private int mouseClientX;
	private int mouseClientY;

	private void loadTimelineEvents() {
		
		timelineChart.addScrollHandler(new ScrollHandler() {

			int lastScrollPos = 0;

			public void onScroll(ScrollEvent event) {

				EventTarget target = event.getNativeEvent().getEventTarget();

				Element el = Element.as(target);

				int scrollPos = timelineChart.getVerticalScrollPosition(el);

				if (lastScrollPos >= scrollPos) {
					lastScrollPos = scrollPos;
					return;
				}

				lastScrollPos = scrollPos;

				int maxScrollPos = timelineChart
						.getMaximumVerticalScrollPosition(el);

				if ((lastScrollPos + (maxScrollPos / 10)) >= maxScrollPos
						&& finalizado == false) {
					Window.alert("Acercandose");
					//DEFAULT_INCREMENT += 50;
					//printTimelineChart(itData);

				}
			}
		});

		timelineChart.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
	
			}
		});
		
		timelineChart.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				
				Element el = Element
						.as(event.getNativeEvent().getEventTarget());				
				
				mouseClientX = event.getClientX();				
				mouseClientY = event.getClientY();				
				cadenaTooltip = el.getPropertyJSO("logicalname").toString();
				
				if(tooltip.isShowing() == false) {
					evalTooltip();
				}
				event.stopPropagation();
				
			}
		});

		timelineChart.addContextMenuHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {

				Element el = Element
						.as(event.getNativeEvent().getEventTarget());
				
				String cadena = el.getPropertyJSO("logicalname").toString();
				
				if(cadena.contains("index")) {
					//Click fuera del contrato
					popupPanel = new PopupPanel(true);
					menuEmployee = new EmployeeContextMenu();		
					
					
					popupPanel.setPopupPosition(event.getNativeEvent().getClientX(),
							event.getNativeEvent().getClientY());	
					
					popupPanel.show();
					
				}			
				
				event.preventDefault();			
				event.stopPropagation();
				event.getNativeEvent();
				
				
			}
		});
	}
	
	private void evalTooltip() {
		
		tooltipCallback.cancel();
		tooltipCallback.schedule(1000);	
	}
	

	private void tratarContrato(String element) {
		
		int auxX = element.indexOf("vR\":") + 4;
		int auxY = element.indexOf("uR\":") - 2;

		int posColumn = Integer.parseInt(cadenaTooltip.substring(auxX, auxY));

		auxX = element.indexOf("uR\":") + 4;
		auxY = element.indexOf("}}");

		int posCell = Integer.parseInt(element.substring(auxX, auxY));
		
		String fullName = data.getFullName(posColumn, posCell);
		String segSocial = data.getMyEmployees().get(posColumn).get(posCell)
				.getSocialSecurity();
		String estado = data.getMyEmployees().get(posColumn).get(posCell)
				.getEstado();
		String startDate = format.format(data.getMyEmployees().get(posColumn)
				.get(posCell).getStartDate());

		String endDate = "";

		if (data.getMyEmployees().get(posColumn).get(posCell).getEndDate() == null) {
			endDate = "En vigor";
		} else {
			endDate = format.format(data.getMyEmployees().get(posColumn)
					.get(posCell).getEndDate());
		}
		
		Date start = data.getMyEmployees().get(posColumn)
				.get(posCell).getStartDate();
		Date finish = data.getMyEmployees().get(posColumn)
				.get(posCell).getEndDate();
		
		int days = DateUtils.getDaysBetween(start, (finish == null ? new Date() : finish )) +1;

			
			//CONTRATO ACTUAL			
			tooltip = new Tooltip();
			tooltip.setFinish(finish);
			tooltip.setFullName(fullName);
			tooltip.setSocialSecurity(segSocial);
			tooltip.setStatus(estado);		
			tooltip.setWorkPeriod(startDate, endDate);
			tooltip.setNumDays(days);
			tooltip.setColor(getColor(estado));
			tooltip.showToolTip(mouseClientX, mouseClientY);
			
	}
	
	private PopupPanel popupPanel;
	private MenuBar popupMenuBar;

	class EmployeeContextMenu extends ContextMenu { 

		AddContractCommand addContract;
		
		public EmployeeContextMenu() {			

			popupMenuBar = new MenuBar(true);
			
			MenuItem add = addItem("Nuevo Contrato", addContract = new AddContractCommand(),
					AON.AON_ICON_RESET, AON.AON_ICON_CMD_BUTTON);
			
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

	private Options createOptions(AbstractDataTable dataTable) {

		Options options = Options.create();

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


	private final AbstractDataTable createTable(String container) {

		data = new DataTableWrapper();
		ifNull = true;

		for (Employee employee : centineels.values()) {
			
			employee.getPerson();
			String fullName = employee.getFullname();
			

			if (fullName.contains(container)) {

				ifNull = false;				

				/*if (data.getSizeEmployees() > DEFAULT_INCREMENT
						&& data.getEmployees().containsKey(idPerson) == false) {
					finalizado = false;
					break;
				}*/				
				
				
				Date start = employee.getStartDate();
				Date end = employee.getEndDate();

				int contractId = employee.getId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (itData.getITDataPerson(contractId).size() > 0) {
					addLeaveRows(employee, contractId, start, end);

				} else {
					data.addRow(employee.getFullname(), ACTIVE, start, end,
							employee.getStartDate(), employee.getEndDate(), 
							employee.getSocialSecurity());
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

		for (ITDataPerson itDataPerson : itData.getITDataPerson(contractId)) {

			if ((DateUtils.compare(itDataPerson.getLeaveStartDate(), endYear) > 0)
					|| (DateUtils.compare(itDataPerson.getLeaveEndDate(),
							startYear) < 0))
				continue;

			Type type = itDataPerson.getType();
			Date leaveEndAux =  itDataPerson.getLeaveEndDate();
			
			if(leaveEndAux == null) {
				leaveEndAux = end;
			}

			leaveStart = DateUtils.after(itDataPerson.getLeaveStartDate(),
					startYear);
			leaveEnd = DateUtils
					.before(leaveEndAux, endYear);
			
			data.addRow(employee.getFullname(), ACTIVE, start, leaveStart,
					employee.getStartDate(), employee.getEndDate(), 
					employee.getSocialSecurity());

			data.addRow(employee.getFullname(), type.getDescription(),
					leaveStart, leaveEnd, itDataPerson.getLeaveStartDate(),
					itDataPerson.getLeaveEndDate(), 
					employee.getSocialSecurity());

			start = leaveEnd;

		}
		if(DateUtils.equals(start, end) == false) {
			data.addRow(employee.getFullname(), ACTIVE, start, end,
					employee.getStartDate(), employee.getEndDate(), 
					employee.getSocialSecurity());
		}
		
	}

	// ------------------------------------------------------------- UiHandlers

	private ExpressionCallback expressionCallback;
	private TooltipCallBack tooltipCallback;

	@UiHandler("dateListBox")
	void onYearChanged(ChangeEvent event) {

		selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox
				.getSelectedIndex()));

		startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		endYear = DateUtils.getLastDayOfYear(DateUtils
				.getDate(11, selectedYear));

		initSuggestBox();

		printTimelineChart(itData);
	}

	@UiHandler("nameEmployee")
	void onExpressionKeyUp(KeyUpEvent event) {
		expressionCallback.cancel();
		expressionCallback.schedule(1500);
	}

	private void initDateListBox() {
		dateListBox.clear();

		for (int x = 0; x < itData.getYears().size(); x++) {
			dateListBox.addItem(String.valueOf(itData.getYears().get(x)));
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

		for (Employee employee : itData.getEmployees().values()) {

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
		// TODO Apéndice de método generado automáticamente

	}

	// ---------------------------------------------------------------- Library

	private static class DataTableWrapper {

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
				Date pRowEndDate, Date pStartDate, Date pEndDate, 
				String pSocialSecurity) {
			
			data.addRow();

			data.setValue(row, 0, pName);
			data.setValue(row, 1, pStatus);
			data.setValue(row, 2, pRowStartDate);
			data.setValue(row, 3, pRowEndDate);

			this.row++;		
			
			/*if(employees.containsKey(idPerson) == false) {
				employees.put(idPerson, pName);
			}*/

			Status status;

			if (myEmployees.isEmpty()) {
				statusList = new LinkedList<ITEditor.Status>();
				status = new Status();
				status.setFullName(pName);
				status.setSocialSecurity(pSocialSecurity);
				status.setEstado(pStatus);
				status.setRowStartDate(pRowStartDate);
				status.setRowEndDate(pRowEndDate);
				status.setStartDate(pStartDate);
				status.setEndDate(pEndDate);				
				statusList.add(status);				
				myEmployees.add(statusList);

			} else {
				
				if (myEmployees.getLast().getLast().getFullName().compareTo(pName) == 0) {
					status = new Status();
					status.setFullName(pName);
					status.setSocialSecurity(pSocialSecurity);
					status.setEstado(pStatus);
					status.setStartDate(pRowStartDate);
					status.setEndDate(pRowEndDate);
					status.setStartDate(pStartDate);
					status.setEndDate(pEndDate);				
					myEmployees.getLast().addLast(status);
					
				} else {
					statusList = new LinkedList<ITEditor.Status>();
					status = new Status();
					status.setFullName(pName);
					status.setSocialSecurity(pSocialSecurity);
					status.setEstado(pStatus);
					status.setStartDate(pRowStartDate);
					status.setEndDate(pRowEndDate);
					status.setStartDate(pStartDate);
					status.setEndDate(pEndDate);				
					statusList.add(status);
					myEmployees.add(statusList);
				}
			}

		}

		public LinkedList<LinkedList<Status>> getMyEmployees() {
			return myEmployees;
		}

		public String getFullName(int pColumn, int pRow) {
			return getMyEmployees().get(pColumn).get(pRow).getFullName();
		}
		
		/*public int getSizeEmployees() {
			return employees.size();
		}

		public Map<Integer, String> getEmployees() {
			return employees;
		}*/

		public AbstractDataTable getDataTable() {
			return data;
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
		private String socialSecurityNum;
		private String estado;
		private Date startDate;
		private Date endDate;
		private Date rowStartDate;
		private Date rowEndDate;

		public Status() {

		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		
		public void setSocialSecurity(String pSocialSecurity) {
			socialSecurityNum = pSocialSecurity;
		}
		
		public String getSocialSecurity() {
			return socialSecurityNum;
		}

		public String getEstado() {
			return estado;
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

	}

}
