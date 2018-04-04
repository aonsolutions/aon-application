package com.esferalia.aon.gwt.stat.client.panel.directsales;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.stat.client.MainEntryPoint;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.gwt.stat.client.util.StatUtils;
import com.esferalia.aon.gwt.stat.shared.JsonParams;
import com.esferalia.aon.occam.api.model.stat.IStatFilterItemVisitor;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.invoice.DirectSalesChartType;
import com.esferalia.aon.occam.api.model.stat.invoice.IDirectSalesChartTypeVisitor;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;

public class DirectSalesStatControlPanel extends MainEntryPoint {

	final static int INFORMATION_TAB = 0;
	final static int INVOICES_TAB = 1;

	static StatServiceAsync statService;

	interface StatControlPanelBinder extends UiBinder<Widget, DirectSalesStatControlPanel> {
	}

	protected static final StatControlPanelBinder INVOICE_STAT_BINDER = GWT.create(StatControlPanelBinder.class);

	@UiField
	DockLayoutPanel dockLayoutPanel;
	@UiField
	HTMLPanel toolbarPanel;
	@UiField
	SimplePanel excelFormContainer;
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	Button data;
	@UiField
	Button excelTable;
//	@UiField
//	Button excel;
//	@UiField
//	Button invoices;
	@UiField
	SimpleLayoutPanel north;
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
	
	StatFilter filter;
	FormPanel diskForm;
	Hidden invoiceTypes;
	Hidden categoryIds;
	Hidden brandIds;
	Hidden workplaceIds;
	Hidden sellerIds;
	Hidden segmentIds;
	Hidden productTagIds;
	Hidden fromDate;
	Hidden toDate;
	Hidden domainId;
	Hidden domainName;
	Hidden user;
	Hidden statParams;
	
	private DirectSalesStatChartTypeVisitor statChartTypeVisitor;
	private static final ScrollPanel NO_DATA_PANEL = new ScrollPanel();
	static {
		FlexTable tab = new FlexTable();
		tab.setWidth("95%");
		tab.setStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonMarginBottom());
		tab.addStyleName(AON.AON_CSS.aonMarginTop());
		tab.getColumnFormatter().setWidth(0, "20px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		InlineLabel icon = new InlineLabel("");
		icon.setStyleName(AON.AON_CSS.aonIconPointRed());
		icon.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
		tab.setWidget(0, 0, icon);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		
		InlineLabel label = new InlineLabel(AON.MSG.noData());
		label.addStyleName(AON.AON_CSS.aonColorRed());
		label.addStyleName(AON.AON_CSS.aonBold());
		tab.setWidget(0, 1, label);
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonPanelGridEven());
		NO_DATA_PANEL.add(tab);
	}

	final private AsyncCallback<Widget> coreChartCallback = new AsyncCallback<Widget>() {
		
		@Override
		public void onSuccess(final Widget chart) {
			content.setWidget(chart);
			if ( tabLayout.getSelectedIndex() == INVOICES_TAB && !isFootPanelClosed()) {
				onInvoicesButtonClick(null);
			}
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
		
		filter = new StatFilter(getCurrentDomainName(), getCurrentUser(), getCurrentDomain());
		filter.addValueChangeHandler( new ValueChangeHandler<StatParams>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<StatParams> event) {
				paintChart();
			}
		});
		north.setWidget(filter);

		content.getElement().setId("content");

		diskForm = new FormPanel("_blank");
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		invoiceTypes = new Hidden("invoiceTypes");
		formFlowPanel.add(invoiceTypes);
		categoryIds = new Hidden("categoryIds");
		formFlowPanel.add(categoryIds);
		brandIds = new Hidden("brandIds");
		formFlowPanel.add(brandIds);
		workplaceIds = new Hidden("workplaceIds");
		formFlowPanel.add(workplaceIds);
		sellerIds = new Hidden("sellerIds");
		formFlowPanel.add(sellerIds);
		productTagIds = new Hidden("productTagIds");
		formFlowPanel.add(productTagIds);
		segmentIds = new Hidden("segmentIds");
		formFlowPanel.add(segmentIds);
		fromDate = new Hidden("fromDate");
		formFlowPanel.add(fromDate);
		toDate = new Hidden("toDate");
		formFlowPanel.add(toDate);
		domainId = new Hidden("domainId");
		formFlowPanel.add(domainId);
		domainName = new Hidden("domainName");
		formFlowPanel.add(domainName);
		user = new Hidden("user");
		formFlowPanel.add(user);
		statParams = new Hidden("statParams");
		formFlowPanel.add(statParams);
		toolbarPanel.add(diskForm);
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
		
		
		statChartTypeVisitor = new DirectSalesStatChartTypeVisitor();
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

	@UiHandler("data")
	void onDataButtonClick(ClickEvent event) {
		openFootPanelIfNeeded();
		tabLayout.selectTab(INFORMATION_TAB);
	}

	@UiHandler("excelTable")
	void onExcelTableButtonClick(ClickEvent event) {
		prepareSubmit();
		statParams.setValue( JsonParams.convert( filter.getParams() ) );
		diskForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
		diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_stat/StatTableExcel");
		diskForm.submit();
	}

//	@UiHandler("excel")
//	void onExcelButtonClick(ClickEvent event) {
//		prepareSubmit();
//		diskForm.setMethod(FormPanel.METHOD_POST);
//		diskForm.setEncoding(FormPanel.ENCODING_URLENCODED);
//		diskForm.setAction(GWT.getHostPageBaseURL() + "aon_gwt_fiscal/InvoiceReport");
//		diskForm.submit();
//	}
	
	private void prepareSubmit() {
		invoiceTypes.setValue("");
		categoryIds.setValue("");
		categoryIds.setValue("");
		workplaceIds.setValue("");
		sellerIds.setValue("");
		segmentIds.setValue("");
		productTagIds.setValue("");
		brandIds.setValue("");
		fromDate.setValue("");
		toDate.setValue("");
		domainId.setValue("");
		domainName.setValue("");
		DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat("dd/MM/yyyy");
		if ( filter.getParams().getFrom() != null) {
			fromDate.setValue(DATE_FORMAT.format(filter.getParams().getFrom()));
		}
		if ( filter.getParams().getTo() != null) {
			toDate.setValue(DATE_FORMAT.format(filter.getParams().getTo()));
		}
		
		IStatFilterItemVisitor visitor = new IStatFilterItemVisitor() {

			@Override
			public void visitInvoiceTypeCondition(StatFilterItem item) {
				 if (AonStringUtils.isNotBlank(invoiceTypes.getValue())) {
					 invoiceTypes.setValue(invoiceTypes.getValue() + ",");
				 }
				 invoiceTypes.setValue(invoiceTypes.getValue() + InvoiceType.valueOf(item.getId()).ordinal());
			}

			@Override
			public void visitWorkplaceCondition(StatFilterItem item) {
				 if (AonStringUtils.isNotBlank(workplaceIds.getValue())) {
					 workplaceIds.setValue(workplaceIds.getValue() + ",");
				 }
				 workplaceIds.setValue(workplaceIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			@Override
			public void visitSellerCondition(StatFilterItem item) {
				 if (AonStringUtils.isNotBlank(sellerIds.getValue())) {
					 sellerIds.setValue(sellerIds.getValue() + ",");
				 }
				 sellerIds.setValue(sellerIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			@Override
			public void visitProductCategoryCondition(StatFilterItem item) {
				 if (AonStringUtils.isNotBlank(categoryIds.getValue())) {
					 categoryIds.setValue(categoryIds.getValue() + ",");
				 }
				 categoryIds.setValue(categoryIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			@Override
			public void visitProductBrandCondition(StatFilterItem item) {
				if (AonStringUtils.isNotBlank(brandIds.getValue())) {
					brandIds.setValue(brandIds.getValue() + ",");
				}
				brandIds.setValue(brandIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			@Override
			public void visitProductTagCondition(StatFilterItem item) {
				if (AonStringUtils.isNotBlank(productTagIds.getValue())) {
					productTagIds.setValue(productTagIds.getValue() + ",");
				}
				productTagIds.setValue(productTagIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			@Override
			public void visitSegmentCondition(StatFilterItem item) {
				if (AonStringUtils.isNotBlank(segmentIds.getValue())) {
					segmentIds.setValue(segmentIds.getValue() + ",");
				}
				segmentIds.setValue(segmentIds.getValue() + AonNumberUtils.toInteger(item.getId()));
			}

			
		};
		
		
		for (StatFilterItem item : filter.getParams().getFilterItems()) {
			 if (item.isSelected()) {
				 item.getType().visit(visitor, item);
			 }
		}
		user.setValue(getCurrentUser());
		domainName.setValue(getCurrentDomainName());
		domainId.setValue(String.valueOf(getCurrentDomain()));
	}
	
//	@UiHandler("invoices")
	void onInvoicesButtonClick(ClickEvent event) {
//		final PopupPanel popup = new PopupPanel(false, true);
//		Label label = new Label(AON.MSG.processing());
//		label.addStyleName(AON.AON_CSS.aonTimer());
//		popup.add(label);
//		popup.setGlassEnabled(true);
//		popup.setAnimationEnabled(true);
//		popup.center();
//		
//		statService.getInvoicesReport(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams()
//			, new AsyncCallback<String>() {
//
//			@Override
//			public void onSuccess(String result) {
//				showInfoPanel(result);
//				popup.hide();
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				popup.hide();
//				ErrorPanel errors = new ErrorPanel();
//				errors.showError("Error inesperado. " + caught.getMessage());
//				content.setWidget(errors);
//			}
//		});
	}
	
	protected void paintChart() {
//		excel.setEnabled(false);
		excelTable.setEnabled(false);
		DirectSalesChartType.values()[filter.getParams().getChartType()].visit(statChartTypeVisitor);
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
	private boolean isFootPanelClosed() {
		return (splitLayoutPanel.getWidgetSize(footPanel) <= 50);
	}
	
//	private void showInfoPanel(String htmlText) {
//		openFootPanelIfNeeded();
//		tabLayout.selectTab(INVOICES_TAB);
//		HTMLPanel panel = new HTMLPanel(htmlText);
//		informationPanel.setWidget(panel);
//		informationPanel.scrollToTop();
//	}
	

	private class DirectSalesStatChartTypeVisitor implements IDirectSalesChartTypeVisitor {
		
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

		protected DataTable getDataTable(StatData<String, String, Double> result, String columnLabel) {
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

//		protected DataTable getGeoDataTable(StatData<String, String, Double> result, String columnLabel) {
//			DataTable dataTable = DataTable.create();
//			dataTable.addColumn(ColumnType.STRING, columnLabel);
//			dataTable.addColumn(ColumnType.NUMBER, filter.getParams().isViewAmounts()? "Caontidad" : "Importe");
//			int rowIndex = 0;
//			LinkedHashMap<String, Double> map = result.getMap().get("CHART");
//			for (String col : map.keySet()) {
//				rowIndex = dataTable.addRow();
//				double d = AonMathUtils.round(map.get(col));
//				dataTable.setValue(rowIndex, 0, col);
//				dataTable.setValue(rowIndex, 1, d);
//				dataTable.setFormattedValue(rowIndex, 1, AON.FMT.format(d));
//			}
//			return dataTable;
//		}

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
			options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
			final DataTable dataTable = getDataTable(result, label);
			RawDataTable table =  new RawDataTable(dataTable);
			south.setWidget(table);
//			excel.setEnabled(true);
			excelTable.setEnabled(true);
			final ResizableComboChart chart = new ResizableComboChart(dataTable, options);
			return chart;
		}

		@Override
		public void visitDirectSalesTypeByYearComboChart() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.year());
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
		public void visitDirectSalesTypeByMonthsComboChart() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.months());
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
		public void visitDirectSalesTypeByWeeksComboChart() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.weeks());
						chart.options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.LINE);
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
		public void visitDirectSalesTypeByDaysComboChart() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(),
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						ResizableComboChart chart = getGenericComboChart(result,AON.MSG.days());
						chart.options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.LINE);
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
		public void visitAbcDirectSalesTitular() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
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
						final DataTable dataTable = getDataTable(result, "ABC");
						RawDataTable table =  new RawDataTable(dataTable);
						south.setWidget(table);
//						excel.setEnabled(true);
						excelTable.setEnabled(true);
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
		public void visitAbcDirectSalesTitularAddress() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
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
						final DataTable dataTable = getDataTable(result, "ABC");
						RawDataTable table =  new RawDataTable(dataTable);
						south.setWidget(table);
//						excel.setEnabled(true);
						excelTable.setEnabled(true);
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
		public void visitAbcDirectSalesCategory() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						if (hasNegativeValues(result)) {
							final ComboChart.Options options = ComboChart.createComboOptions();
							options.set("animation", StatUtils.ANIMATION);
							options.setWidth(content.getOffsetWidth());
							options.setHeight(content.getOffsetHeight());
							options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
							AxisOptions vaxis = AxisOptions.create();
							vaxis.setTitle(AON.MSG.amount());
							options.setVAxisOptions(vaxis);
							AxisOptions haxis = AxisOptions.create();
							haxis.setTitle(AON.MSG.productCategories());
							options.setHAxisOptions(haxis);
							final DataTable dataTable = getDataTable(result, AON.MSG.productCategories());
							RawDataTable table =  new RawDataTable(dataTable);
							south.setWidget(table);
//							excel.setEnabled(true);
							excelTable.setEnabled(true);
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
							final DataTable dataTable = getDataTable(result, "ABC");
							RawDataTable table =  new RawDataTable(dataTable);
							south.setWidget(table);
//							excel.setEnabled(true);
							excelTable.setEnabled(true);
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
		public void visitAbcProductBrand() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
					else {
						if (hasNegativeValues(result)) {
							final ComboChart.Options options = ComboChart.createComboOptions();
							options.set("animation", StatUtils.ANIMATION);
							options.setWidth(content.getOffsetWidth());
							options.setHeight(content.getOffsetHeight());
							options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
							AxisOptions vaxis = AxisOptions.create();
							vaxis.setTitle(AON.MSG.amount());
							options.setVAxisOptions(vaxis);
							AxisOptions haxis = AxisOptions.create();
							haxis.setTitle(AON.MSG.productBrands());
							options.setHAxisOptions(haxis);
							final DataTable dataTable = getDataTable(result, AON.MSG.productBrands());
							RawDataTable table =  new RawDataTable(dataTable);
							south.setWidget(table);
//							excel.setEnabled(true);
							excelTable.setEnabled(true);
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
							final DataTable dataTable = getDataTable(result, "ABC");
							RawDataTable table =  new RawDataTable(dataTable);
							south.setWidget(table);
//							excel.setEnabled(true);
							excelTable.setEnabled(true);
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
		public void visitAbcDirectSalesProduct() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
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
						final DataTable dataTable = getDataTable(result, "ABC");
						RawDataTable table =  new RawDataTable(dataTable);
						south.setWidget(table);
//						excel.setEnabled(true);
						excelTable.setEnabled(true);
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
		public void visitAbcDirectSalesWorkplace() {
			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
					new AsyncCallback<StatData<String, String, Double>>() {

				@Override
				public void onSuccess(final StatData<String, String, Double> result) {
					if (result.isEmpty()) coreChartCallback.onSuccess(NO_DATA_PANEL);
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
						final DataTable dataTable = getDataTable(result, "ABC");
						RawDataTable table =  new RawDataTable(dataTable);
						south.setWidget(table);
//						excel.setEnabled(true);
						excelTable.setEnabled(true);
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

//		@Override
//		public void visitAbcDirectSalesSeller() {
//			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
//					new AsyncCallback<StatData<String, String, Double>>() {
//
//				@Override
//				public void onSuccess(final StatData<String, String, Double> result) {
//					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
//					else {
//						final PieOptions options = PieChart.createPieOptions();
//						options.set("animation", StatUtils.ANIMATION);
//						options.setWidth(content.getOffsetWidth());
//						options.setHeight(content.getOffsetHeight());
//						options.set3D(true);
//						AxisOptions vaxis = AxisOptions.create();
//						vaxis.setTitle(AON.MSG.amount());
//						options.setVAxisOptions(vaxis);
//						AxisOptions haxis = AxisOptions.create();
//						haxis.setTitle(AON.MSG.months());
//						options.setHAxisOptions(haxis);
//						final DataTable dataTable = getDataTable(result, "ABC");
//						RawDataTable table =  new RawDataTable(dataTable);
//						south.setWidget(table);
//						excel.setEnabled(true);
//						excelTable.setEnabled(true);
//						final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
//						coreChartCallback.onSuccess(chart);
//					}
//				}
//
//
//				@Override
//				public void onFailure(Throwable caught) {
//					coreChartCallback.onFailure(caught);
//				}
//			});
//		}
//
//		@Override
//		public void visitGeoProvince() {
//			statService.getStatData(getCurrentDomainName(),getCurrentUser(), getCurrentDomain(), filter.getParams(), 
//					new AsyncCallback<StatData<String, String, Double>>() {
//
//				@Override
//				public void onSuccess(final StatData<String, String, Double> result) {
//					if (result.isEmpty()) coreChartCallback.onSuccess(ERROR_PANEL);
//					else {
//						final  GeoChartWrapper.Options options = GeoChartWrapper.Options.create();
//						options.set("animation", StatUtils.ANIMATION);
//						options.setWidth(content.getOffsetWidth());
//						options.setHeight(content.getOffsetHeight());
//						options.setRegion("ES");
//						options.setDisplayMode(DisplayMode.MARKERS);
//						final DataTable dataTable = getGeoDataTable(result, "Provincias");
//						RawDataTable table =  new RawDataTable(dataTable);
//						south.setWidget(table);
//						excel.setEnabled(true);
//						excelTable.setEnabled(true);
//						final ResizableGeoChart chart = new ResizableGeoChart(dataTable,options);
//						coreChartCallback.onSuccess(chart);
//					}
//				}
//
//
//				@Override
//				public void onFailure(Throwable caught) {
//					coreChartCallback.onFailure(caught);
//				}
//			});
//		}

	}
	
/*
	@UiHandler("pdf")
	void onPDFButtonClick(ClickEvent event) {
		takeScreenShot();
	}
*/
	
//	public static native void takeScreenShot()
//	/*-{
//		$wnd.doTakeScreenshot(
//			$doc.getElementById("content"),
//			$doc.getElementById("pdfIframe")
//		);		
//	}-*/;
	
	
}

