package com.esferalia.aon.gwt.stat.client.panel;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.ajaxloader.client.Properties;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.Series;

public class YearInvoiceTypeComboChart extends ResizableComboChart {

	static StatServiceAsync statService;

	private static StatServiceAsync getStatService() {
		if (statService == null) {
			StatServiceAsync serviceRaw = GWT.create(StatService.class);
			statService = new StatServiceAsyncDecorator(serviceRaw);
		}
		return statService;
	}

	private YearInvoiceTypeComboChart(AbstractDataTable data, Options options) {
		super(data, options);
	}

	public static void getChart(final String domainName,final Integer domainId,final StatParams params, final int width, final int height,
			final AsyncCallback<YearInvoiceTypeComboChart> callback) {
		
		getStatService().getYearInvoiceTypeData(domainName,domainId,params, new AsyncCallback<StatData<Integer, String, Double>>() {

			@Override
			public void onSuccess(final StatData<Integer, String, Double> result) {
				
				final Options options = ComboChart.createComboOptions();
				Properties animation = Properties.create();
				animation.set("durtion", 1000.0);
				animation.set("easing", "out");
				animation.set("startup", true);
				options.set("animation", animation);
				options.setWidth(width);
				options.setHeight(height);
				// "Facturación anual"
				options.setTitle(AON.MSG.yearInvoicing());
				// seriesType: 'bars'
				options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);

				// Colores de cada barra
				options.setColors("#cc0000", "#0059b3", "#e6b800", "#009900", "#3e0099");

				AxisOptions vaxis = AxisOptions.create();
				// "Importe"
				vaxis.setTitle(AON.MSG.amount());
				options.setVAxisOptions(vaxis);

				AxisOptions haxis = AxisOptions.create();
				// "Año"
				haxis.setTitle(AON.MSG.year());
				options.setHAxisOptions(haxis);

				// Linea del BENEFICIO: series: {5: {type: 'line'}
				Series media = Series.create();
				media.setType(Series.Type.LINE);
				options.setSeries(0, media);

				final DataTable dataTable = getDataTable(domainName, domainId, options, result);
				final YearInvoiceTypeComboChart chart = new YearInvoiceTypeComboChart(dataTable, options);

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

	public static DataTable getDataTable(String domainName,Integer domainId, Options options, StatData<Integer, String, Double> result) {

		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, AON.MSG.year());
		// final String profit = "Beneficio";

		LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
		int rowIndex = 0;
		int colIndex = 0;
		for (Integer year : result.getMap().keySet()) {
			rowIndex = dataTable.addRow();
			dataTable.setValue(rowIndex, 0, AON.FMT_INT.format(year));
			LinkedHashMap<String, Double> map = result.getMap().get(year);

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

			// dataTable.addColumn(ColumnType.NUMBER, profit);

		}
		return dataTable;
	}

}