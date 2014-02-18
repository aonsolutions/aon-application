package com.esferalia.aon.gwt.payroll.client;

import java.io.FileOutputStream;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.HorizontalAxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;

public class Statistics extends ResizeComposite {

	private static final int ZOOM_STEP = 20;
	private static final int MIN_ZOOM = 25;
	private static final int MAX_ZOOM = 500;

	private static final int DEFAULT_ZOOM = 135;

	private static final DateTimeFormat DATE_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH);

	interface Binder extends UiBinder<Widget, Statistics> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	private static final String STYLENAME_CHECKED_ITEM = "aon-MenuItemCheckYes";

	private int selectedYear;

	@UiField
	Grid grid;

	@UiField
	ListBox dateListBox;

	@UiField
	Label titleLabel;

	private int zoom = DEFAULT_ZOOM;
	


	// Variable que guarda que grafica se esta mostrando
	/*
	 * 0 --> ColumnChart 1 --> LineChart
	 */
	//private int estado = 0;
	private boolean inicilizated = false;

	private com.esferalia.aon.gwt.payroll.shared.Statistics statistics;

	public Statistics() {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		titleLabel.setText(title);
	}

	/**
	 * Pinta el ColumnChart
	 * 
	 * @param pStats
	 *            Instancia de la clase Statistics
	 */
	public void setStatistics(
			final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {
		this.statistics = pStats;

		// Create a callback to be called when the visualization API
		// has been loaded.		
		grid.clear();
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				ColumnChart column = new ColumnChart(
						createTable(pStats),
						 createOptions(pStats));
				grid.setWidget(0, 0, column);
				}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
			CoreChart.PACKAGE);

		if (!inicilizated) {
			initDateListBox();
			inicilizated = true;
		}
		
		loadLineStatistics(pStats);
	}

	/**
	 * Pinta el LineChart
	 * 
	 * @param pStats
	 *            Instancia de la clase Statistics
	 */
	public void loadLineStatistics(
			final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {

		this.statistics = pStats;
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				LineChart line = new LineChart(createLineTable(pStats),
						createLineOptions(pStats));				
				grid.setWidget(1, 0, line);				
			}
		};
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				ColumnChart.PACKAGE);

		if (!inicilizated) {
			initDateListBox();
			inicilizated = true;
		}
	}

	// ------------------------------------------------------------- UiHandlers

	/*@UiHandler("printButton")
	void onClickPrintButton(ClickEvent event) {
		//Window.alert("onClickPrintButton(...)");
	}*/

	@UiHandler("dateListBox")
	void onYearChanged(ChangeEvent event) {
		selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox
				.getSelectedIndex()));
		setStatistics(statistics);
		
		/*if (estado == 0) {
			setStatistics(statistics);
		} else if (estado == 1) {
			loadLineStatistics(statistics);
		}*/

	}

	private void initDateListBox() {
		dateListBox.clear();

		for (int x = 0; x < statistics.getEnterpriseStatisticYears().size(); x++) {
			dateListBox.addItem(String.valueOf(statistics
					.getEnterpriseStatisticYears().get(x).getYear()));
		}

		dateListBox.setSelectedIndex(dateListBox.getItemCount() - 1);
		selectedYear = Integer.parseInt(dateListBox.getItemText(dateListBox
				.getSelectedIndex()));
	}

	/*private void setCheckedStyle(MenuItem menuItem, boolean checked) {
		if (checked) {
			menuItem.addStyleName(STYLENAME_CHECKED_ITEM);
		} else {
			menuItem.removeStyleName(STYLENAME_CHECKED_ITEM);
		}
	}*/

	// *****************************************************
	// ***********************COLUMN CHART********************
	// *****************************************************
	
	private Options createOptions(
			com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		Options options = Options.create();
		// 400 y 240
		options.setFontSize(11);
		options.setWidth(3*this.getOffsetWidth()/4);
		options.setHeight(3*this.getOffsetHeight()/8);		
		HorizontalAxisOptions hAxisOption = HorizontalAxisOptions.create();		
		hAxisOption.setMinValue(0);
		options.setHAxisOptions(hAxisOption);
		AxisOptions vAxisOption = AxisOptions.create();
		vAxisOption.setMinValue(0);		
		
		options.setColors("#3366CC","#109618", "#FF9900","#DD4477","#990099");
		
		options.setVAxisOptions(vAxisOption);
		
		
		options.setTitle("Acumulaci\u00F3n de Costes Mensuales");
		options.setIsStacked(true);

		return options;
	}

	private AbstractDataTable createTable(
			com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {

		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Mes");
		data.addColumn(ColumnType.NUMBER, "Neto");
		data.addColumn(ColumnType.NUMBER, "IRPF");
		data.addColumn(ColumnType.NUMBER, "SS Empleado");
		data.addColumn(ColumnType.NUMBER, "Otros");
		data.addColumn(ColumnType.NUMBER, "SS Empresa");
		
		
		com.google.gwt.visualization.client.formatters.NumberFormat.Options options =
				com.google.gwt.visualization.client.formatters.NumberFormat.Options.create();
		options.setSuffix("\u20AC");
		
		NumberFormat f = NumberFormat.getCurrencyFormat();

		int yearIndex = 0;

		for (int x = 0; x < pStat.getEnterpriseStatisticYears().size(); x++) {

			if (pStat.getEnterpriseStatisticYears().get(x).getYear() == selectedYear) {
				yearIndex = x;
			}
		}

		for (int x = 0; x < pStat.getEnterpriseStatisticYears().get(yearIndex)
				.getStatsDataLength(); x++) {

			data.addRow();
			int w = 0;
			data.setValue(x, w, pStat.getEnterpriseStatisticYears()
					.get(yearIndex).getStatsData(x).getMonthName());
			w++;		
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
					.getStatsData(x).getLiquid());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));			 
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getIrpf());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getSSEmployee());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getOtros());		
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getSSEnterprise());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));		
			
			
			
		}
		
		return data;
	}

	// *****************************************************
	// ***********************LINE CHART********************
	// *****************************************************

	private Options createLineOptions(
			com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		Options options = Options.create();
		options.setFontSize(11);
		options.setWidth(3*this.getOffsetWidth()/4);
		options.setHeight(3*this.getOffsetHeight()/8);		
	
		
		options.setLineWidth(4);
		options.setTitle("Progresi\u00F3n de Gastos Mensuales");
		
		AxisOptions vAxisOption = AxisOptions.create();
		vAxisOption.setMinValue(0);
		options.setVAxisOptions(vAxisOption);
		
		options.setColors("#990099","#AAAA11","#DC3912");
		
		return options;
	}

	private AbstractDataTable createLineTable(
			com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Mes");
		data.addColumn(ColumnType.NUMBER, "SS Empresa");
		data.addColumn(ColumnType.NUMBER, "Devengos");		
		data.addColumn(ColumnType.NUMBER, "Totales");

		int yearIndex = 0;
		
		com.google.gwt.visualization.client.formatters.NumberFormat.Options options =
				com.google.gwt.visualization.client.formatters.NumberFormat.Options.create();
		options.setSuffix("\u20AC");
		
		NumberFormat f = NumberFormat.getCurrencyFormat();

		for (int x = 0; x < pStat.getEnterpriseStatisticYears().size(); x++) {
			
			if (pStat.getEnterpriseStatisticYears().get(x).getYear() == selectedYear) {
				yearIndex = x;
			}
		}

		for (int x = 0; x < pStat.getEnterpriseStatisticYears().get(yearIndex)
				.getStatsDataLength(); x++) {

			data.addRow();
			int w = 0;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getMonthName());
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getSSEnterprise());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getTotalPayment());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			w++;			
			data.setValue(x, w,
					pStat.getEnterpriseStatisticYears().get(yearIndex)
							.getStatsData(x).getGastoTotal());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));



		}
		
		return data;
	}
	
	//*****************************************************
	//*****************************************************
	//*****************************************************
	
} 
	

