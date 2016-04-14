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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
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
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Series;

public class StatControlPanel extends MainEntryPoint {

	final static int INFORMATION_TAB = 0;
	final static int INVOICES_TAB = 1;

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
	Button invoices;
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
	@UiField
	ScrollPanel informationPanel;
	
	private StatChartTypeVisitor statChartTypeVisitor;
	private static final FlowPanel ERROR_PANEL = new FlowPanel();
	static {
		ERROR_PANEL.setWidth("100%");	
		ERROR_PANEL.setHeight("100%");
		ERROR_PANEL.setStyleName(AON.AON_CSS.aonPadding());
		ERROR_PANEL.addStyleName(AON.AON_CSS.aonMarginTop());
		ERROR_PANEL.addStyleName(AON.AON_CSS.aonVerticalAlignMiddle());
		ERROR_PANEL.addStyleName(AON.AON_CSS.aonFontBig());
		ERROR_PANEL.addStyleName(AON.AON_CSS.aonColorRed());
		ERROR_PANEL.addStyleName(AON.AON_CSS.aonColorRed());
		Label label = new Label(AON.MSG.noData());
		label.setStyleName(AON.AON_CSS.aonMarginTop());
		label.addStyleName(AON.AON_CSS.aonIconError());
		label.addStyleName(AON.AON_CSS.aonPaddingLeft());
		ERROR_PANEL.add( label ); 
	}

	private Stack<Widget> stack = new Stack<Widget>();
	
	final private AsyncCallback<Widget> coreChartCallback = new AsyncCallback<Widget>() {
		
		@Override
		public void onSuccess(final Widget chart) {
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
		
		tabLayout.setAnimationDuration(300);
		tabLayout.selectTab(INFORMATION_TAB);
		tabLayout.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				openFootPanelIfNeeded();
				if  (tabLayout.getSelectedIndex() == INVOICES_TAB) {
					onInvoicesButtonClick(null);
				}
				
			}
		});
		
		
		statChartTypeVisitor = new StatChartTypeVisitor();
		
		filter.paintFilter(new AsyncCallback<StatParams>() {

			@Override
			public void onSuccess(StatParams result) {
				north.setWidget(filter);
				paintChart();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				ErrorPanel errors = new ErrorPanel();
				errors.showError("Error inesperado. " + caught.getMessage());
				content.setWidget(errors);
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
	
	@UiHandler("invoices")
	void onInvoicesButtonClick(ClickEvent event) {
		final PopupPanel popup = new PopupPanel(false, true);
		Label label = new Label(AON.MSG.processing());
		label.addStyleName(AON.AON_CSS.aonTimer());
		popup.add(label);
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		statService.getInvoicesReport(getCurrentDomainName(), getCurrentDomain(), filter.getParams()
			, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				showInfoPanel(result);
				popup.hide();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				ErrorPanel errors = new ErrorPanel();
				errors.showError("Error inesperado. " + caught.getMessage());
				content.setWidget(errors);
			}
		});
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
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	
	private void showInfoPanel(String htmlText) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(INVOICES_TAB);
		HTMLPanel panel = new HTMLPanel(htmlText);
		informationPanel.setWidget(panel);
		informationPanel.scrollToTop();
	}
	

	private class StatChartTypeVisitor implements IStatChartTypeVisitor {
		
		protected boolean hasNegativeValues(StatData<String, String, Double> result) {
			for (String rowKey : result.getMap().keySet()) {
				for (String col : result.getMap().get(rowKey).keySet()) {
					if (AonMathUtils.isNegative( result.get(rowKey, col))) {
						return true;
					}
						
				}
			}
			return false;
		}

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

		private ResizableComboChart getGenericComboChart(StatData<String, String, Double> result, String label) {
			final ComboChart.Options options = ComboChart.createComboOptions();
			options.set("animation", StatUtils.ANIMATION);
			options.setWidth(content.getOffsetWidth());
			options.setHeight(content.getOffsetHeight());
			options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
			AxisOptions vaxis = AxisOptions.create();
			vaxis.setTitle(AON.MSG.amount());
			options.setVAxisOptions(vaxis);
			AxisOptions haxis = AxisOptions.create();
			haxis.setTitle(label);
			options.setHAxisOptions(haxis);
			if (filter.getParams().isResultVisible()) {
				options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS_RESULT);
				Series media = Series.create();
				media.setType(Series.Type.LINE);
				options.setSeries(0, media);
			} else {
				options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
			}
			Table.Options tableOptions = Table.Options.create();
			tableOptions.setAlternatingRowStyle(true);
			tableOptions.setWidth(south.getOffsetWidth() + "px");
			tableOptions.setHeight(south.getOffsetHeight() + "px");
			final DataTable dataTable = getDataTable(tableOptions, result, label);
			ResizableTable table = new ResizableTable(dataTable, tableOptions); 
			south.setWidget(table);
			excel.setEnabled(true);
			final ResizableComboChart chart = new ResizableComboChart(dataTable, options);
/*			
			chart.addSelectHandler(new SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					Selection selection = chart.getSelections().get(0);
					int row = selection.getRow();
					int col = selection.getColumn();
				}
				
			});
*/			
			return chart;
		}

		@Override
		public void visitInvoiceTypeByYearComboChart() {
			statService.getStatData(getCurrentDomainName(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.year());
//						final Options options = ComboChart.createComboOptions();
//						options.set("animation", StatUtils.ANIMATION);
//						options.setWidth(content.getOffsetWidth());
//						options.setHeight(content.getOffsetHeight());
//						options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
//						options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
//						AxisOptions vaxis = AxisOptions.create();
//						vaxis.setTitle(AON.MSG.amount());
//						options.setVAxisOptions(vaxis);
//						AxisOptions haxis = AxisOptions.create();
//						haxis.setTitle(AON.MSG.year());
//						options.setHAxisOptions(haxis);
//						if (filter.getParams().isResultVisible()) {
//							Series media = Series.create();
//							media.setType(Series.Type.LINE);
//							options.setSeries(0, media);
//						}
//						Table.Options tableOptions = Table.Options.create();
//						tableOptions.setAlternatingRowStyle(true);
//						tableOptions.setWidth(south.getOffsetWidth() + "px");
//						tableOptions.setHeight(south.getOffsetHeight() + "px");
//						final DataTable dataTable = getDataTable(tableOptions, result,AON.MSG.year());
//						ResizableTable table = new ResizableTable(dataTable, tableOptions); 
//						south.setWidget(table);
//						excel.setEnabled(true);
//						ResizableComboChart chart = new ResizableComboChart(dataTable, options);
						coreChartCallback.onSuccess(chart);
					}
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.months());
//						final Options options = ComboChart.createComboOptions();
//						options.set("animation", StatUtils.ANIMATION);
//						options.setWidth(content.getOffsetWidth());
//						options.setHeight(content.getOffsetHeight());
//						options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
//						options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
//						AxisOptions vaxis = AxisOptions.create();
//						vaxis.setTitle(AON.MSG.amount());
//						options.setVAxisOptions(vaxis);
//						AxisOptions haxis = AxisOptions.create();
//						haxis.setTitle(AON.MSG.months());
//						options.setHAxisOptions(haxis);
//						if (filter.getParams().isResultVisible()) {
//							Series media = Series.create();
//							media.setType(Series.Type.LINE);
//							options.setSeries(0, media);
//						}
//						Table.Options tableOptions = Table.Options.create();
//						tableOptions.setAlternatingRowStyle(true);
//						tableOptions.setWidth(south.getOffsetWidth() + "px");
//						tableOptions.setHeight(south.getOffsetHeight() + "px");
//						final DataTable dataTable = getDataTable(tableOptions, result,AON.MSG.months());
//						ResizableTable table = new ResizableTable(dataTable, tableOptions); 
//						south.setWidget(table);
//						excel.setEnabled(true);
//						ResizableComboChart chart = new ResizableComboChart(dataTable, options);
						coreChartCallback.onSuccess(chart);
					}
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.days());
						chart.options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.LINE);
//						final ComboChart.Options options = ComboChart.createComboOptions();
//						options.set("animation", StatUtils.ANIMATION);
//						options.setWidth(content.getOffsetWidth());
//						options.setHeight(content.getOffsetHeight());
//						options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.LINE);
//						options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
//						AxisOptions vaxis = AxisOptions.create();
//						vaxis.setTitle(AON.MSG.amount());
//						options.setVAxisOptions(vaxis);
//						AxisOptions haxis = AxisOptions.create();
//						haxis.setTitle(AON.MSG.months());
//						options.setHAxisOptions(haxis);
//						if (filter.getParams().isResultVisible()) {
//							Series media = Series.create();
//							media.setType(Series.Type.LINE);
//							options.setSeries(0, media);
//						}
//						Table.Options tableOptions = Table.Options.create();
//						tableOptions.setAlternatingRowStyle(true);
//						tableOptions.setWidth(south.getOffsetWidth() + "px");
//						tableOptions.setHeight(south.getOffsetHeight() + "px");
//						final DataTable dataTable = getDataTable(tableOptions, result, AON.MSG.days());
//						ResizableTable table = new ResizableTable(dataTable, tableOptions); 
//						south.setWidget(table);
//						excel.setEnabled(true);
//						ResizableComboChart chart = new ResizableComboChart(dataTable, options);
						coreChartCallback.onSuccess(chart);
					}
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
						if (hasNegativeValues(result)) {
							final ComboChart.Options options = ComboChart.createComboOptions();
							options.set("animation", StatUtils.ANIMATION);
							options.setWidth(content.getOffsetWidth());
							options.setHeight(content.getOffsetHeight());
							options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);						AxisOptions vaxis = AxisOptions.create();
							vaxis.setTitle(AON.MSG.amount());
							options.setVAxisOptions(vaxis);
							AxisOptions haxis = AxisOptions.create();
							haxis.setTitle(AON.MSG.productCategories());
							options.setHAxisOptions(haxis);
							Table.Options tableOptions = Table.Options.create();
							tableOptions.setAlternatingRowStyle(true);
							tableOptions.setWidth(south.getOffsetWidth() + "px");
							tableOptions.setHeight(south.getOffsetHeight() + "px");
							final DataTable dataTable = getDataTable(tableOptions, result, AON.MSG.productCategories());
							ResizableTable table = new ResizableTable(dataTable, tableOptions); 
							south.setWidget(table);
							excel.setEnabled(true);
							ResizableComboChart chart = new ResizableComboChart(dataTable, options);
							coreChartCallback.onSuccess(chart);
						} else {
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
					}
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
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
					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
					else {
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
				}


				@Override
				public void onFailure(Throwable caught) {
					coreChartCallback.onFailure(caught);
				}
			});
		}
	}

}
