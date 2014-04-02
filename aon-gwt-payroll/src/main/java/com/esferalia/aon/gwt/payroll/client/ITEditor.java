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
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public class ITEditor<E> extends AbstractPager implements RequiresResize {

	private static final String ACTIVE = "Activo";

	interface Binder extends UiBinder<Widget, ITEditor> {
	}

	class ExpressionCallback extends Timer {

		@Override
		public void run() {
			final String expression = nameEmployee.getText().toUpperCase();
			ifNull = true;
			if (StringUtils.isEmpty(expression)) {
				printTimelineChart(itData);
			} else {
				Runnable onLoadCallback = new Runnable() {

					@Override
					public void run() {
						AbstractDataTable dataTable = createSpecificDataTable(expression);
						Options options = createOptions(dataTable);
						final TimeLineChart timelineChart = new TimeLineChart(
								dataTable, options);
						if (ifNull == false) {
							timelinePanel.setWidget(timelineChart);
						} else {
							timelinePanel.clear();
						}
					}
				};
				// Load the visualization api, passing the onLoadCallback to be
				// called
				// when loading is done.
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						TimeLineChart.PACKAGE);
			}
		}
	}

	private static final Binder binder = GWT.create(Binder.class);

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
	private static Map<Integer, String> employees;
	private static int idPerson;
	private int selectedYear;
	private Options options;
	private boolean finalizado;
	private boolean ifNull; // Evitar el Null del Timeline
	private Map<Integer, Employee> centineels;
	private DataTableWrapper data;
	private Date startYear;
	private Date endYear;

	private final MultiWordSuggestOracle names = new MultiWordSuggestOracle();

	/**
	 * The default increment size.
	 */
	private static int DEFAULT_INCREMENT = 50;

	/**
	 * The increment size.
	 */
	private int incrementSize = DEFAULT_INCREMENT;

	private com.esferalia.aon.gwt.payroll.shared.ITData itData;

	public ITEditor() {

		nameEmployee = new SuggestBox(names);
		initWidget(binder.createAndBindUi(this));
		nameEmployee.setWidth("300px");
		this.expressionCallback = new ExpressionCallback();
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
		printTimelineChart(itData);

	}

	private void printTimelineChart(
			final com.esferalia.aon.gwt.payroll.shared.ITData pITData) {

		itData = pITData;
		employees = new LinkedHashMap<Integer, String>();
		centineels = new LinkedHashMap<Integer, Employee>();
		ifNull = true;
		startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		endYear = DateUtils.getLastDayOfYear(DateUtils
				.getDate(11, selectedYear));

		initSuggestBox();

		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				try {
					// Create a pie chart visualization.
					AbstractDataTable dataTable = createTable();
					Options options = createOptions(dataTable);
					final TimeLineChart timelineChart = new TimeLineChart(
							dataTable, options);

					timelinePanel.setWidget(timelineChart);
					timelineChart.addScrollHandler(new ScrollHandler() {

						int lastScrollPos = 0;

						public void onScroll(ScrollEvent event) {

							EventTarget target = event.getNativeEvent()
									.getEventTarget();

							Element el = Element.as(target);

							int scrollPos = timelineChart
									.getVerticalScrollPosition(el);

							if (lastScrollPos >= scrollPos) {
								lastScrollPos = scrollPos;
								return;
							} // end-if: up

							lastScrollPos = scrollPos;

							int maxScrollPos = timelineChart
									.getMaximumVerticalScrollPosition(el);

							if ((lastScrollPos + (maxScrollPos / 10)) >= maxScrollPos
									&& finalizado == false) {
								// Window.alert("Acercandose");
								DEFAULT_INCREMENT += 50;
								printTimelineChart(itData);

							}
						}
					});

				} catch (Throwable ex) {
					Window.alert(ex + " Se ha producido un error");
				}

			}
		};	

		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				TimeLineChart.PACKAGE);
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

		return options;
	}

	private final AbstractDataTable createTable() {

		data = new DataTableWrapper();

		for (Employee employee : centineels.values()) {
			idPerson = employee.getPerson();

			if (data.getSizeEmployees() > DEFAULT_INCREMENT
					&& data.getEmployees().containsKey(idPerson) == false) {
				finalizado = false;
				break;
			}
			Date start = employee.getStartDate();
			Date end = employee.getEndDate();

			if ((DateUtils.compare(start, endYear) <= 0)
					&& (DateUtils.compare(end, startYear) >= 0)) {

				int contractId = employee.getId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (itData.getITDataPerson(contractId).size() > 0) {
					addLeaveRows(employee, contractId, start, end);

				} else {
					data.addRow(employee.getFullname(), ACTIVE, start, end);
				}
				finalizado = true;
			}
		}
		return data.getDataTable();
	}

	private AbstractDataTable createSpecificDataTable(String container) {

		data = new DataTableWrapper();

		for (Employee employee : centineels.values()) {

			String fullName = employee.getFullname();

			if (fullName.contains(container)) {
				ifNull = false;
				Date start = employee.getStartDate();
				Date end = employee.getEndDate();

				if ((DateUtils.compare(start, endYear) <= 0)
						&& (DateUtils.compare(end, startYear) >= 0)) {

					int contractId = employee.getId();

					start = DateUtils.after(start, startYear);
					end = DateUtils.before(end, endYear);

					if (itData.getITDataPerson(contractId).size() > 0) {
						addLeaveRows(employee, contractId, start, end);
					} else {
						data.addRow(employee.getFullname(), ACTIVE, start, end);
					}
				}
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

			leaveStart = DateUtils.after(itDataPerson.getLeaveStartDate(),
					startYear);
			leaveEnd = DateUtils
					.before(itDataPerson.getLeaveEndDate(), endYear);

			data.addRow(employee.getFullname(), ACTIVE, start, leaveStart);

			data.addRow(employee.getFullname(), type.getDescription(),
					leaveStart, leaveEnd);

			start = leaveEnd;

		}
		data.addRow(employee.getFullname(), ACTIVE, start, end);
	}

	// ------------------------------------------------------------- UiHandlers

	private ExpressionCallback expressionCallback;

	@UiHandler("dateListBox")
	void onYearChanged(ChangeEvent event) {
		selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox
				.getSelectedIndex()));

		printTimelineChart(itData);
	}

	@UiHandler("nameEmployee")
	void onExpressionKeyUp(KeyUpEvent event) {
		expressionCallback.cancel();
		expressionCallback.schedule(1000);
	}

	private void initDateListBox() {
		dateListBox.clear();

		for (int x = 0; x < itData.getYears().size(); x++) {
			dateListBox.addItem(String.valueOf(itData.getYears().get(x)));
		}

		dateListBox.setSelectedIndex(dateListBox.getItemCount() - 1);
		selectedYear = Integer.parseInt(dateListBox.getItemText(dateListBox
				.getSelectedIndex()));
	}

	private final void initSuggestBox() {

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

		public DataTableWrapper() {
			row = 0;
			data = DataTable.create();
			initColumns();
		}

		private void initColumns() {

			data.addColumn(ColumnType.STRING, "Nombre");
			data.addColumn(ColumnType.STRING, "Estado");
			data.addColumn(ColumnType.DATE, "Inicio");
			data.addColumn(ColumnType.DATE, "Fin");

		}

		public void addRow(String pName, String pStatus, Date pStartDate,
				Date pEndDate) {
			data.addRow();

			data.setValue(row, 0, pName);
			data.setValue(row, 1, pStatus);
			data.setValue(row, 2, pStartDate);
			data.setValue(row, 3, pEndDate);

			this.row++;

			if (employees.containsKey(idPerson) == false) {
				employees.put(idPerson, pName);
			}

		}

		public int getSizeEmployees() {
			return employees.size();
		}

		public Map<Integer, String> getEmployees() {
			return employees;
		}

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

}
