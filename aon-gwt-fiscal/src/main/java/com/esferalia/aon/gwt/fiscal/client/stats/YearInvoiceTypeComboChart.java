package com.esferalia.aon.gwt.fiscal.client.stats;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.StatsService;
import com.esferalia.aon.gwt.fiscal.client.StatsServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.StatsServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart.Options;
import com.google.gwt.visualization.client.visualizations.corechart.Series.Type;

public class YearInvoiceTypeComboChart {

	static StatsServiceAsync statsService;

	private static StatsServiceAsync getStatsService() {
		if (statsService == null) {
			StatsServiceAsync serviceRaw = GWT.create(StatsService.class);
			statsService = new StatsServiceAsyncDecorator(serviceRaw);
		}
		return statsService;
	}

	public static void getChart(final StatParams params
			, final int width
			, final int height
			, final AsyncCallback<ResizableComboChart> callback) {
		getStatsService().getYearInvoiceTypeData(params, 
				new AsyncCallback<StatData<Integer, InvoiceType, Double>>() {

					@Override
					public void onSuccess(final StatData<Integer, InvoiceType, Double> result) {
						final Options options = ComboChart.createComboOptions();
						Properties animation = Properties.create();
						animation.set("duration", 1000.0);
						animation.set("easing", "out");
						animation.set("startup", true);
						options.set("animation", animation);
						options.setWidth(width);
						options.setHeight(height);
						options.setTitle(AON.MSG.yearInvoicing());
						options.setSeriesType(Type.BARS);
						
						AxisOptions vaxis = AxisOptions.create();
						vaxis.setTitle(AON.MSG.amount());
						options.setVAxisOptions(vaxis);
						
						AxisOptions haxis = AxisOptions.create();
						haxis.setTitle(AON.MSG.year());
						options.setHAxisOptions(haxis);
						final DataTable dataTable = getDataTable(result);
						final ResizableComboChart chart = new ResizableComboChart(dataTable, options);
						chart.setStyleName(AON.AON_CSS.aonWidthAll());
						chart.addStyleName(AON.AON_CSS.aonHeightAll());
						callback.onSuccess(chart);
					}


					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	private static DataTable getDataTable(StatData<Integer, InvoiceType, Double> result) {
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.NUMBER, AON.MSG.year());
		LinkedHashMap<InvoiceType,Integer> colMap = new LinkedHashMap<InvoiceType,Integer>();
		int rowIndex = 0;
		int colIndex = 0;
		for (Integer year : result.getMap().keySet() ) {
			rowIndex = dataTable.addRow();
			dataTable.setValue(rowIndex, 0, year);
			LinkedHashMap<InvoiceType, Double> map = result.getMap().get(year);
			for (InvoiceType type : map.keySet() ) {
				if (!colMap.containsKey(type)) {
					colMap.put(type, colMap.size()+1 );
					dataTable.addColumn(ColumnType.NUMBER,type.getDescription());	
				} 
				colIndex = colMap.get(type); 
				double d = AonMathUtils.round(map.get(type));	
				dataTable.setValue(rowIndex, colIndex, d);
				dataTable.setFormattedValue(rowIndex, colIndex, AON.FMT.format(d));
			}
		}
		return dataTable;
	}
	
}