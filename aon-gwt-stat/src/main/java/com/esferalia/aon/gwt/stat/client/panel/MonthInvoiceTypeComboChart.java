package com.esferalia.aon.gwt.stat.client.panel;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.Series;

public class MonthInvoiceTypeComboChart extends ResizableComboChart{

	static StatServiceAsync statService;

	private static StatServiceAsync getStatService() {
		if (statService == null) {
			StatServiceAsync serviceRaw = GWT.create(StatService.class);
			statService = new StatServiceAsyncDecorator(serviceRaw);
		}
		return statService;
	}
	
	private MonthInvoiceTypeComboChart(AbstractDataTable data, Options options) {
		super(data, options);
	}

	public static void getChart(String domainName,Integer domainId,final StatParams params, final int width, final int height,
			final AsyncCallback<MonthInvoiceTypeComboChart> callback) {
		
		getStatService().getMonthInvoiceTypeData(domainName,domainId,params, new AsyncCallback<StatData<Integer,String,Double>>() {

			@Override
			public void onSuccess(final StatData<Integer, String, Double> result) {

				final ComboChart.Options options = ComboChart.createComboOptions();
				Properties animation = Properties.create();
				animation.set("duration", 1000.0);
				animation.set("easing", "out");
				animation.set("startup", true);
				options.set("animation", animation);
				options.setWidth(width);
				options.setHeight(height);
				// "Facturación mensual año X"
//**			Integer year = params.getYearAux();
//**			options.setTitle(AON.MSG.monthInvoicing() + " del " + year);
				// seriesType: 'bars'
				options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);

				// Colores de cada barra
				options.setColors("#cc0000", "#0059b3", "#e6b800", "#009900", "#3e0099");

				AxisOptions vaxis = AxisOptions.create();
				// "Importe"
				vaxis.setTitle(AON.MSG.amount());
				options.setVAxisOptions(vaxis);

				AxisOptions haxis = AxisOptions.create();
				// "Meses"
				haxis.setTitle(AON.MSG.months());
				options.setHAxisOptions(haxis);

				// Linea del BENEFICIO: series: {5: {type: 'line'}
				Series media = Series.create();
				media.setType(Series.Type.LINE);
				options.setSeries(0, media);

				final DataTable dataTable = getDataTable(options, result);
				final MonthInvoiceTypeComboChart chart = new MonthInvoiceTypeComboChart(dataTable, options);

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

	private static DataTable getDataTable(ComboChart.Options options, StatData<Integer, String, Double> result) {
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, AON.MSG.months());
		LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
		int rowIndex = 0;
		int colIndex = 0;
		for (Integer month : result.getMap().keySet()) {
			rowIndex = dataTable.addRow();
			String key = AonStringUtils.upperCase((AonStringUtils.substring(AON.MSG.month(month - 1), 0, 3)));
			dataTable.setValue(rowIndex, 0, key);
			LinkedHashMap<String, Double> map = result.getMap().get(month);
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
	
	
	

}
