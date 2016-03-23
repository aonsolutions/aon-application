package com.esferalia.aon.gwt.stat.client.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;

public class MonthInvoiceTypePieChart {

	static StatServiceAsync statService;

	private static StatServiceAsync getStatService() {
		if (statService == null) {
			StatServiceAsync serviceRaw = GWT.create(StatService.class);
			statService = new StatServiceAsyncDecorator(serviceRaw);
		}
		return statService;
	}

	public static void getChart(String domainName,Integer domainId,final StatParams params
			, final int width
			, final int height
			, final AsyncCallback<ResizablePieChart> asyncCallback) {
		getStatService().getMonthInvoiceTypeData(domainName,domainId,params, 
				new AsyncCallback<StatData<Integer, String, Double>>() {

					@Override
					public void onSuccess(final StatData<Integer, String, Double> result) {
						final PieOptions options = PieChart.createPieOptions();
						Properties animation = Properties.create();
						animation.set("duration", 1000.0);
						animation.set("easing", "out");
						animation.set("startup", true);
						options.set("animation", animation);
						options.setWidth(width);
						options.setHeight(height);
						options.setTitle(AON.MSG.monthInvoicing());
						//options.setSeriesType(Type.BARS);
						
						AxisOptions vaxis = AxisOptions.create();
						vaxis.setTitle(AON.MSG.amount());
						options.setVAxisOptions(vaxis);
						
						AxisOptions haxis = AxisOptions.create();
						haxis.setTitle(AON.MSG.months());
						options.setHAxisOptions(haxis);
//						final DataTable dataTable = getDataTable(result);
						
//						final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
//						chart.setStyleName(AON.AON_CSS.aonWidthAll());
//						chart.addStyleName(AON.AON_CSS.aonHeightAll());
//						asyncCallback.onSuccess(chart);
					}


					@Override
					public void onFailure(Throwable caught) {
						asyncCallback.onFailure(caught);
					}
				});
	}

/**
	private static DataTable getDataTable(StatData<Integer, InvoiceType, Double> result) {
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, AON.MSG.months());
		for (InvoiceType type : InvoiceType.values()) {
			dataTable.addColumn(ColumnType.NUMBER,type.getDescription());
		}
		int monthIndex = 0;
		for (Integer month : result.getMap().keySet() ) {
			monthIndex = dataTable.addRow();
			String key = AonStringUtils.upperCase((AonStringUtils.substring(AON.MSG.month(month - 1), 0, 3))); 
			dataTable.setValue(monthIndex, 0, key);
			LinkedHashMap<InvoiceType, Double> map = result.getMap().get(month);
			for (InvoiceType type : map.keySet() ) {
				double d = AonMathUtils.round(map.get(type));	
				dataTable.setValue(monthIndex, type.ordinal() + 1, d);
				dataTable.setFormattedValue(monthIndex, type.ordinal() + 1, AON.FMT.format(d));
			}
		}
		return dataTable;
	}
**/
}
