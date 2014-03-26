package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public class ITEditor<E> extends ResizeComposite {

	private static final String ACTIVE = "Activo";

	interface Binder extends UiBinder<Widget, ITEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	ScrollPanel scrollPanel;
	@UiField
	SimplePanel timelinePanel;

	@UiField
	Label titleLabel;

	@UiField
	ListBox dateListBox;

	private int selectedYear;
	private Options options;
	private com.esferalia.aon.gwt.payroll.shared.ITData itData;

	public ITEditor() {
		initWidget(binder.createAndBindUi(this));
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
		printTimelineChart(itData);

	}

	private void printTimelineChart(
			final com.esferalia.aon.gwt.payroll.shared.ITData pITData) {

		itData = pITData;

		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				try {
					// Create a pie chart visualization.
					AbstractDataTable dataTable = createTable();
					Options options = createOptions(dataTable);
					TimeLineChart timeLine = new TimeLineChart(dataTable,
							options);

					if (itData.getEmployees().isEmpty()) {
						Window.alert("No hay datos");
					} else {
						timelinePanel.setWidget(timeLine);

					}

				} catch (Throwable ex) {
					Window.alert(ex + " Se ha producido un error");
				}

			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.

		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				TimeLineChart.PACKAGE);

	}

	private Options createOptions(AbstractDataTable dataTable) {

		options = Options.create();

		options.setWidth((8 * scrollPanel.getOffsetWidth()) / 9);
		options.setHeight((8 * scrollPanel.getOffsetHeight()) / 9);

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
		for (int row = 0; row < dataTable.getNumberOfRows(); row++){
			String status = dataTable.getValueString(row, 1);
			if ( !statusList.contains(status))
				statusList.add( status ); 

		}
		
		int i = 0;
		String colors [] = new String[statusList.size()] ;
		for (String status : statusList)
			colors[i++]=getColor(status);

		options.setColors(colors);

		return options;
	}

	private final AbstractDataTable createTable() {

		DataTableWrapper data = new DataTableWrapper();

		Date startYear = DateUtils.getFirstDayOfYear(DateUtils.getDate(0,
				selectedYear));
		Date endYear = DateUtils.getLastDayOfYear(DateUtils.getDate(11,
				selectedYear));

		for (Employee employee : itData.getEmployees().values()) {

			Date start = employee.getStartDate();
			Date end = employee.getEndDate();

			if ((DateUtils.compare(start, endYear) <= 0)
					&& (DateUtils.compare(end, startYear) >= 0)) {

				int contractId = employee.getId();

				start = DateUtils.after(start, startYear);
				end = DateUtils.before(end, endYear);

				if (itData.getITDataPerson(contractId).size() > 0) {

					Date leaveStart = null;
					Date leaveEnd = null;

					for (ITDataPerson itDataPerson : itData
							.getITDataPerson(contractId)) {

						if ((DateUtils.compare(
								itDataPerson.getLeaveStartDate(), endYear) > 0)
								|| (DateUtils.compare(
										itDataPerson.getLeaveEndDate(),
										startYear) < 0))
							continue;

						Type type = itDataPerson.getType();

						leaveStart = DateUtils.after(
								itDataPerson.getLeaveStartDate(), startYear);
						leaveEnd = DateUtils.before(
								itDataPerson.getLeaveEndDate(), endYear);

						data.addRow(employee.getFullname(), ACTIVE, start,
								leaveStart);

						data.addRow(employee.getFullname(),
								type.getDescription(), leaveStart, leaveEnd);

						start = leaveEnd;

					}

					data.addRow(employee.getFullname(), ACTIVE, start, end);

				} else {
					data.addRow(employee.getFullname(), ACTIVE, start, end);
				}
			}
		}

		return data.getDataTable();

	}

	// ------------------------------------------------------------- UiHandlers

	@UiHandler("dateListBox")
	void onYearChanged(ChangeEvent event) {
		selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox
				.getSelectedIndex()));

		printTimelineChart(itData);
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
		}

		public AbstractDataTable getDataTable() {
			return data;
		}

	}

	public static String getColor(Type type) {
		if ( type == null )
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
		return  getColor(getType(status));

	}

	private static ITDataPerson.Type getType(String description) {
		for (ITDataPerson.Type type : Type.values())
			if (StringUtils.equals(type.getDescription(), description))
				return type;
		return null;
	}

}
