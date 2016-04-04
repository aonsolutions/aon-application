package com.esferalia.aon.gwt.stat.client.panel;

import java.util.LinkedHashMap;
import java.util.Stack;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.gwt.stat.client.panel.GeoChartWrapper.DisplayMode;
import com.esferalia.aon.gwt.stat.client.util.StatUtils;
import com.esferalia.aon.occam.api.model.stat.IStatChartTypeVisitor;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Series;

public class StatControlPanel extends MainEntryPoint {

	static StatServiceAsync statService;

	interface StatControlPanelBinder extends UiBinder<Widget, StatControlPanel> {
	}

	private static final StatControlPanelBinder INVOICE_STAT_BINDER = GWT.create(StatControlPanelBinder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	HTMLPanel toolbarPanel;
	@UiField
	SimplePanel excelFormContainer;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	Button back;
	@UiField
	Button excel;
	@UiField
	ScrollPanel north;
	@UiField
	StatFilter filter;
	@UiField
	SimpleLayoutPanel content;
	@UiField
	SimpleLayoutPanel south;
	@UiField
	MinimizePanel footPanel;
	@UiField
	TabLayoutPanel tabLayout;
	
	private StatChartTypeVisitor statChartTypeVisitor;	

	private Stack<Widget> stack = new Stack<Widget>();
	
	final private AsyncCallback<CoreChart> coreChartCallback = new AsyncCallback<CoreChart>() {
		
		@Override
		public void onSuccess(final CoreChart chart) {
			content.setWidget(chart);
			stack.push(chart);
		}

		@Override
		public void onFailure(Throwable caught) {
			ErrorPanel errors = new ErrorPanel();
			errors.showError("Error inesperado");
			content.setWidget(errors);
		}
	};

	@Override
	public void onModuleLoad() {
		StatServiceAsync serviceRaw = GWT.create(StatService.class);
		statService = new StatServiceAsyncDecorator(serviceRaw);
		
		AON.ensureInjected();
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		Widget ui = INVOICE_STAT_BINDER.createAndBindUi(this);
		root.add(ui);
		statChartTypeVisitor = new StatChartTypeVisitor();
		
		filter.paintFilter(new AsyncCallback<StatParams>() {

			@Override
			public void onSuccess(StatParams result) {
				north.setWidget(filter);
				paintChart();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}


		});
		
	}

	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;

	@UiHandler("back")
	void onBackButtonClick(ClickEvent event) {
		if (stack.size() > 0 ) {
			content.setWidget(stack.pop());
		}
	}

	@UiHandler("excel")
	void onExcelButtonClick(ClickEvent event) {
		TableToExcelClient ttec = new TableToExcelClient(south.getWidget().getElement(), "table.xls");
		excelFormContainer.setWidget(ttec.getExportFormWidget());
		ttec.getExportFormWidget().submit();
	}
	
	@UiHandler("filter")
	void onFilterChanged( ValueChangeEvent<StatParams> event) {
		paintChart();
	}
	
	protected void paintChart() {
		excel.setEnabled(false);
		filter.getParams().getChartType().visit( statChartTypeVisitor );
		back.setEnabled(stack.size() > 0);
	}

	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}

	@UiHandler("footPanel")
	void onFootMaximize(MaximizeEvent event) {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2);
		splitLayoutPanel.animate(500);
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}

	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4);
		splitLayoutPanel.animate(500);
	}

	private class StatChartTypeVisitor implements IStatChartTypeVisitor {
		
		protected DataTable getDataTable(Table.Options options, StatData<String, String, Double> result, String columnLabel) {
			DataTable dataTable = DataTable.create();
			dataTable.addColumn(ColumnType.STRING, columnLabel);
			LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
			int rowIndex = 0;
			int colIndex = 0;
			for (String rowKey : result.getMap().keySet()) {
				rowIndex = dataTable.addRow();
				dataTable.setValue(rowIndex, 0, rowKey);
				LinkedHashMap<String, Double> map = result.getMap().get(rowKey);
				for (String col : map.keySet()) {
					if (!colMap.containsKey(col)) {
						colMap.put(col, colMap.size() + 1);
						dataTable.addColumn(ColumnType.NUMBER, col);
					}
					colIndex = colMap.get(col);
					double d = AonMathUtils.round(map.get(col));
					dataTable.setValue(rowIndex, colIndex, d);
					dataTable.setFormattedValue(rowIndex, colIndex, AON.FMT.format(d));
				}
			}
			return dataTable;
		}

		protected DataTable getGeoDataTable(Table.Options options, StatData<String, String, Double> result, String columnLabel) {
			DataTable dataTable = DataTable.create();
			dataTable.addColumn(ColumnType.STRING, columnLabel);
			dataTable.addColumn(ColumnType.NUMBER, "Importe");
			int rowIndex = 0;
			LinkedHashMap<String, Double> map = result.getMap().get("CHART");
			for (String col : map.keySet()) {
				rowIndex = dataTable.addRow();
				double d = AonMathUtils.round(map.get(col));
				dataTable.setValue(rowIndex, 0, col);
				dataTable.setValue(rowIndex, 1, d);
				dataTable.setFormattedValue(rowIndex, 1, AON.FMT.format(d));
			}
			return dataTable;
		}

		@Override
		public void visitInvoiceTypeByYearComboChart() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final Options options = ComboChart.createComboOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
					options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.year());
					options.setHAxisOptions(haxis);
					Series media = Series.create();
					media.setType(Series.Type.LINE);
					options.setSeries(0, media);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result,AON.MSG.year());
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					ResizableComboChart chart = new ResizableComboChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}

				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}
		
		@Override
		public void visitInvoiceTypeByMonthsComboChart() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final Options options = ComboChart.createComboOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
					options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Series media = Series.create();
					media.setType(Series.Type.LINE);
					options.setSeries(0, media);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result,AON.MSG.months());
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					ResizableComboChart chart = new ResizableComboChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}

				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}
		
		@Override
		public void visitInvoiceTypeByDaysComboChart() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final ComboChart.Options options = ComboChart.createComboOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.LINE);
					options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Series media = Series.create();
					media.setType(Series.Type.LINE);
					options.setSeries(0, media);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, AON.MSG.days());
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					ResizableComboChart chart = new ResizableComboChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}

				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}

		@Override
		public void visitAbcInvoiceTitular() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final PieOptions options = PieChart.createPieOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.set3D(true);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, "ABC");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
	
		}

		@Override
		public void visitAbcInvoiceCategory() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final PieOptions options = PieChart.createPieOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.set3D(true);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, "ABC");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}

		@Override
		public void visitAbcInvoiceProduct() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final PieOptions options = PieChart.createPieOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.set3D(true);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, "ABC");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}

		@Override
		public void visitAbcInvoiceWorkplace() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final PieOptions options = PieChart.createPieOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.set3D(true);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, "ABC");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}

		@Override
		public void visitAbcInvoiceSeller() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final PieOptions options = PieChart.createPieOptions();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.set3D(true);
					AxisOptions vaxis = AxisOptions.create();
					vaxis.setTitle(AON.MSG.amount());
					options.setVAxisOptions(vaxis);
					AxisOptions haxis = AxisOptions.create();
					haxis.setTitle(AON.MSG.months());
					options.setHAxisOptions(haxis);
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getDataTable(tableOptions, result, "ABC");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}

		@Override
		public void visitGeoProvince() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					final  GeoChartWrapper.Options options = GeoChartWrapper.Options.create();
					options.set("animation", StatUtils.ANIMATION);
					options.setWidth(content.getOffsetWidth());
					options.setHeight(content.getOffsetHeight());
					options.setRegion("ES");
					options.setDisplayMode(DisplayMode.MARKERS);
					
					Table.Options tableOptions = Table.Options.create();
					tableOptions.setAlternatingRowStyle(true);
					tableOptions.setWidth(south.getOffsetWidth() + "px");
					tableOptions.setHeight(south.getOffsetHeight() + "px");
					final DataTable dataTable = getGeoDataTable(tableOptions, result, "Provincias");
					ResizableTable table = new ResizableTable(dataTable, tableOptions); 
					south.setWidget(table);
					excel.setEnabled(true);
					final ResizableGeoChart chart = new ResizableGeoChart(dataTable,options);
					coreChartCallback.onSuccess(chart);
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}
	}
}
