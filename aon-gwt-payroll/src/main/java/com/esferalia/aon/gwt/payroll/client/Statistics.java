package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ColumnChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.LineChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class Statistics extends AonCustomDockLayout {
	
	// ----------------------------------------------- UiFields

	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	private Grid gridColumnChart;
	private Grid gridLinePieChart;
	
	// ----------------------------------------------- Variables

	private com.esferalia.aon.gwt.payroll.shared.Statistics statistics;
	
	private ListBox dateListBox = new ListBox();
	
	private int selectedYear; //Año seleccionado
	private int yearIndex;	//Index del año en el dateListBox
	
	// ----------------------------------------------- Constructo
	
	public Statistics() {
		super("Estad\u00EDsiticas");
		hideSearchWidget();
		
		getToolbarPanel();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());
		
		gridColumnChart = new Grid(1, 1);
		gridLinePieChart = new Grid(1, 2);
		
		container.add(gridColumnChart);
		container.add(gridLinePieChart);
		
		scrollPanel = new ScrollPanel(container);
		
		add(scrollPanel);
	}
	
	@Override
	protected void onClearFilter() {}
	
	// ----------------------------------------------- setStatics

	public final void setStatistics(final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {
		this.statistics = pStats;
		gridColumnChart.clear();
		gridLinePieChart.clear();
		initDateListBox();
		implColumnSetStatistics(pStats);
	}
	
	// ----------------------------------------------- setStatics.Methods
	
	private void initDateListBox() {
		dateListBox.clear();

		for (int x = 0; x < statistics.getStatisticYears().size(); x++)
			dateListBox.addItem(String.valueOf(statistics.getStatisticYears().get(x).getYear()));
		
		dateListBox.addChangeHandler(e -> {
			selectedYear = Integer.valueOf(dateListBox.getValue(dateListBox.getSelectedIndex()));
			yearIndex = dateListBox.getSelectedIndex();
			implColumnSetStatistics(statistics);
		});

		dateListBox.setSelectedIndex(dateListBox.getItemCount() - 1);
		selectedYear = Integer.parseInt(dateListBox.getItemText(dateListBox.getSelectedIndex()));
		yearIndex = dateListBox.getSelectedIndex();
	}
	
	private void implColumnSetStatistics(final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {
		
		this.statistics = pStats;		

		// Create a callback to be called when the visualization API has been loaded.		
		gridColumnChart.clear();
		gridColumnChart.setCellPadding(0);
		gridColumnChart.setCellSpacing(0);
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				ColumnChart column = new ColumnChart(
						createTable(pStats),
						createOptions());
			
				gridColumnChart.setWidget(0, 0, column);
			}
		};
		
		// Load the visualization api, passing the onLoadCallback to be called when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);		
		
		implLineSetStatistics(pStats);
		
	}
	
	private void implLineSetStatistics(final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {	
		
		gridLinePieChart.clear();
		gridLinePieChart.setCellPadding(0);
		gridLinePieChart.setCellSpacing(0);
		
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				LineChart line = new LineChart(
						createLineTable(pStats),
						createLineOptions(pStats));
				
				gridLinePieChart.setWidget(0, 0, line);				
			}
		};
		
		// Load the visualization api, passing the onLoadCallback to be called when loading is done.		
		VisualizationUtils.loadVisualizationApi(onLoadCallback, ColumnChart.PACKAGE);
		
		implPieSetStatistics(pStats);
	}
	
	private void implPieSetStatistics(final com.esferalia.aon.gwt.payroll.shared.Statistics pStats) {	
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				PieChart line = new PieChart(
						createPieTable(pStats),
						createPieOptions());	
				
				gridLinePieChart.setWidget(0, 1, line);	
			}
		};
		
		// Load the visualization api, passing the onLoadCallback to be called when loading is done.		
		VisualizationUtils.loadVisualizationApi(onLoadCallback, ColumnChart.PACKAGE);
	}

	// ----------------------------------------------- Statics.Methods
	
	private Options createOptions() {
		
		Options options = Options.create();
		options.setFontSize(11);
		
		options.setWidth((8*this.getOffsetWidth())/9);
		options.setHeight(3*this.getOffsetHeight()/8);
		
		AxisOptions vAxisOption = AxisOptions.create();
		vAxisOption.setMinValue(0);		
		options.setColors("#3366CC","#109618", "#FF9900","#DD4477","#990099");				
		
		options.setTitle("Acumulaci\u00F3n de Costes Mensuales");
		options.setIsStacked(true);		
		
		return options;
	}
	
	// ----------------------------------------------- Statics.ColumnSet

	private AbstractDataTable createTable(com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {

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

		for (int x = 0; x < pStat.getStatisticYears().get(yearIndex).getStatsDataLength(); x++) {

			data.addRow();
			int w = 0;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getMonthName());
			
			w++;		
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getLiquid());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));			 
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getIrpf());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getSSEmployee());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getOtros());		
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getSSEnterprise());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));				
		}
		
		return data;
	}

	// ----------------------------------------------- Statics.LineChart

	private Options createLineOptions(com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		Options options = Options.create();
		options.setFontSize(11);		
	
		options.setWidth(this.getOffsetWidth()/2);
		options.setHeight(3*this.getOffsetHeight()/8);
		
		options.setLineWidth(4);
		options.setTitle("Progresi\u00F3n de Gastos Mensuales");
		
		AxisOptions vAxisOption = AxisOptions.create();
		vAxisOption.setMinValue(0);
		options.setVAxisOptions(vAxisOption);
		
		options.setColors("#990099","#AAAA11","#DC3912");
		
		return options;
	}

	private AbstractDataTable createLineTable(com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		
		DataTable data = DataTable.create();
		
		data.addColumn(ColumnType.STRING, "Mes");
		data.addColumn(ColumnType.NUMBER, "SS Empresa");
		data.addColumn(ColumnType.NUMBER, "Devengos");		
		data.addColumn(ColumnType.NUMBER, "Totales");
		
		com.google.gwt.visualization.client.formatters.NumberFormat.Options options =
				com.google.gwt.visualization.client.formatters.NumberFormat.Options.create();
		options.setSuffix("\u20AC");
		
		NumberFormat f = NumberFormat.getCurrencyFormat();		

		for (int x = 0; x < pStat.getStatisticYears().get(yearIndex).getStatsDataLength(); x++) {
			data.addRow();
			
			int w = 0;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getMonthName());
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getSSEnterprise());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getTotalPayment());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
			
			w++;			
			data.setValue(x, w, pStat.getStatisticYears().get(yearIndex).getStatsData(x).getGastoTotal());
			data.setFormattedValue(x, w, f.format(data.getValueDouble(x, w)));
		}
		
		return data;
	}
	
	// ----------------------------------------------- Statics.PieChart

	private Options createPieOptions() {
		Options options = Options.create();
		options.setFontSize(11);
	
		options.setWidth(this.getOffsetWidth()/2);
		options.setHeight(3*this.getOffsetHeight()/8);
		
		options.setTitle("Total Gastos Anual " + selectedYear);
		
		AxisOptions vAxisOption = AxisOptions.create();
		vAxisOption.setMinValue(0);
		options.setVAxisOptions(vAxisOption);			
		
		options.setColors("#3366CC","#109618", "#FF9900", "#990099");
		
		return options;
	}

	private AbstractDataTable createPieTable(com.esferalia.aon.gwt.payroll.shared.Statistics pStat) {
		
		DataTable data = DataTable.create();
		
		data.addColumn(ColumnType.STRING, "Task");
		data.addColumn(ColumnType.NUMBER, "Valor");			
		
		com.google.gwt.visualization.client.formatters.NumberFormat.Options options =
				com.google.gwt.visualization.client.formatters.NumberFormat.Options.create();
		options.setSuffix("\u20AC");
		
		NumberFormat f = NumberFormat.getCurrencyFormat();
		
		double liquid = pStat.getStatisticYears().get(yearIndex).getTotalLiquid();
		double irpf = pStat.getStatisticYears().get(yearIndex).getTotalIRPF();
		double ssEmployee = pStat.getStatisticYears().get(yearIndex).getTotalSSEmployee();
		double ssEnterprise = pStat.getStatisticYears().get(yearIndex).getTotalSSEnterprise();
		
		if(liquid < 0) liquid = 0;
		if(irpf < 0) irpf = 0;
		if(ssEmployee < 0) ssEmployee = 0;
		if(ssEnterprise < 0) ssEnterprise = 0;
		
		//4 filas. NETO-IRPF-SSEMPLEADO-SSEMPRESA
		
		data.addRows(4);
		
		data.setValue(0, 0, "Neto");
		data.setValue(0, 1, liquid);
		data.setFormattedValue(0, 1, f.format(data.getValueDouble(0, 1)));
		
		data.setValue(1, 0, "IRPF");
		data.setValue(1, 1, irpf);
		data.setFormattedValue(1, 1, f.format(data.getValueDouble(1, 1)));
		
		data.setValue(2, 0, "SS Empleado");
		data.setValue(2, 1, ssEmployee);
		data.setFormattedValue(2, 1, f.format(data.getValueDouble(2, 1)));
		
		data.setValue(3, 0, "SS Empresa");
		data.setValue(3, 1, ssEnterprise);
		data.setFormattedValue(3, 1, f.format(data.getValueDouble(3, 1)));		
		
		return data;
	}
		
	// ----------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		addToolbarButton(dateListBox);
	}
		
} 
	

