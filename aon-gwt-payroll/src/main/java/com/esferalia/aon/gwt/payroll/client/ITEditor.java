package com.esferalia.aon.gwt.payroll.client;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.BarLabelStyle;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.RowLabelStyle;
import com.esferalia.aon.gwt.payroll.client.TimeLineChart.Options.Timeline;
import com.esferalia.aon.gwt.payroll.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;

public class ITEditor<E> extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	interface Binder extends UiBinder<Widget, ITEditor> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	
	private int selectedYear; // Año seleccionado	

	@UiField
	ListBox dateListBox;

	@UiField
	Label titleLabel;

	@UiField
	Grid timeline;

	private int zoom = DEFAULT_ZOOM;
	private com.esferalia.aon.gwt.payroll.shared.ITData itData;
	private Options options;

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
		timeline.clearCell(0, 0);
		timeline.setCellPadding(0);
		timeline.setCellSpacing(0);
		BorderLayout border = new BorderLayout(0,0);		
		timeline.setLayoutData(border);
		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				try {
					// Create a pie chart visualization.
					TimeLineChart timeLine = new TimeLineChart(createTable(),
							createOptions());

					if (itData.getEmployees().isEmpty()) {
						Window.alert("No hay datos");
					} else {
						timeline.setWidget(0, 0, timeLine);	
						
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

	private Options createOptions() {

		options = Options.create();

		options.setWidth((8 * this.getOffsetWidth()) / 9);
		options.setHeight((5*this.getOffsetHeight())/6);		
		

		Timeline timeline = Timeline.create();
		options.setAvoidOverlappingGridLines(false);
		timeline.setGroupByRowLabel(true);
		timeline.setShowBarLabels(false);

		BarLabelStyle barLabelStyle = BarLabelStyle.create();
		barLabelStyle.setFontName("Helvetica");
		barLabelStyle.setFontSize("10");

		RowLabelStyle rowStyle = RowLabelStyle.create();
		rowStyle.setFontName("Arial");
		rowStyle.setFontSize("9");

		timeline.setRowLabelStyle(rowStyle);
		timeline.setBarLabelStyle(barLabelStyle);

		options.setTimeline(timeline);
		
		//No funciona
		//options.setColors("#FF99FF","#D65C33");
		//data.addColors(options);
		//options.setColors(data.getColors());
		

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
						
						int type = itDataPerson.getType();
						String baja = itData.getLeaveTypePosition(type);

						leaveStart = DateUtils.after(
								itDataPerson.getLeaveStartDate(), startYear);
						leaveEnd = DateUtils.before(
								itDataPerson.getLeaveEndDate(), endYear);
						data.addRow(employee.getFullname(), "Activo", start, leaveStart);
						data.addRow(employee.getFullname(), baja, leaveStart,
								leaveEnd);
						start = leaveEnd;				

					}
					
					data.addRow(employee.getFullname(), "Activo", start, end);
					
				} else {
					data.addRow(employee.getFullname(), "Activo", start, end);
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

	// -------------------------------------------------------------

	private static class DataTableWrapper {

		private DataTable data;
		private int row;
		private List<String> listColors;
		private String[] colors;
		private Map<String, String> typeColors;
		

		public DataTableWrapper() {
			data = DataTable.create();
			row = 0;
			typeColors = new LinkedHashMap<String, String>();
			listColors = new LinkedList<String>();

			initColumns();
		}

		private void initColumns() {

			data.addColumn(ColumnType.STRING, "Nombre");
			data.addColumn(ColumnType.STRING, "Estado");
			data.addColumn(ColumnType.DATE, "Start");
			data.addColumn(ColumnType.DATE, "Fin");

		}

		public void addRow(String pName, String pEstado, Date pStartDate,
				Date pEndDate) {
			data.addRow();
			
			Properties prop = Properties.create();			
			
			data.setCell(row, 0, pName, null, null);
			data.setCell(row, 1, pEstado, null, null);
			data.setCell(row, 2, pStartDate, null, null);
			data.setCell(row, 3, pEndDate, null, null);
			this.row++;
			
			/*
			data.setValue(row, 0, pName);
			data.setValue(row, 1, pEstado);
			data.setValue(row, 2, pStartDate);
			data.setValue(row, 3, pEndDate);			
			setColor(pEstado);
			this.row++;
			 */
		}

		public AbstractDataTable getDataTable() {
			return data;
		}

		private void setColor(String pEstado) {

			if (typeColors.containsKey(pEstado) == false) {
				switch (pEstado) {
				case "Activo":
					typeColors.put("Activo", "#FFFFFF");
					listColors.add("#FFFFFF");
					break;

				case "Maternidad":
					typeColors.put("Maternidad", "#FF99FF");
					listColors.add("#FF99FF");
					break;

				case "Enfermedad Común":
					typeColors.put("EnfermedadComun", "#99FF99");
					listColors.add("#99FF99");
					break;

				case "Enfermedad Profesional":
					typeColors.put("EnfermedadProfesional", "#FF6600");
					listColors.add("#FF6600");
					break;

				case "Paternidad":
					typeColors.put("Paternidad", "#DAC679");
					listColors.add("#DAC679");
					break;

				case "Riesgo Durante Embarazo":
					typeColors.put("RiesgoDuranteEmbarazo", "#D65C33");
					listColors.add("#D65C33");
					break;

				case "Lactancia Materna":
					typeColors.put("LactanciaMaterna", "#CC7A52");
					listColors.add("#CC7A52");
					break;

				case "EnfermedadNoProfesional":
					typeColors.put("EnfermedadNoProfesional", "#66CCFF");
					listColors.add("#66CCFF");
					break;

				default:
					break;
				}
			}
		}
		
		public void addColors(Options options) {
			JsArrayString colores = JavaScriptObject.createArray().cast();
			colors = new String[listColors.size()];
			for(int x=0; x<listColors.size(); x++) {				
				colores.push(listColors.get(x));				
			}
			
			//options.setColors(colores);
		}
		public String[] getColors() {
			return colors;
		}

	}

}
