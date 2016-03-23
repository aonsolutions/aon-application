/**

package com.esferalia.aon.gwt.stat.client.panel;

import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.StatService;
import com.esferalia.aon.gwt.stat.client.StatServiceAsync;
import com.esferalia.aon.gwt.stat.client.StatServiceAsyncDecorator;
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
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;
import com.google.gwt.visualization.client.visualizations.corechart.Series.Type;

public class YearInvoiceTypePieChart {

	static StatServiceAsync statService;
	

	private static StatServiceAsync getStatService() {
		if (statService == null) {
			StatServiceAsync serviceRaw = GWT.create(StatService.class);
			statService = new StatServiceAsyncDecorator(serviceRaw);
		}
		return statService;
	}

	public static void getChart(final StatParams params
			, final int width
			, final int height
			, final AsyncCallback<ResizablePieChart> callback) {
		getStatService().getYearInvoiceTypeData(params, 
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
						//Cambiar el MSG según que tipo de factura/producto esté seleccionado ¿?
						options.setTitle(AON.MSG.yearInvoicing());
						
						//AxisOptions vaxis = AxisOptions.create();
						//vaxis.setTitle(AON.MSG.amount());
						//options.setVAxisOptions(vaxis);
						
						//AxisOptions haxis = AxisOptions.create();
						//haxis.setTitle(AON.MSG.year());
						//options.setHAxisOptions(haxis);
						
						final DataTable dataTable = getDataTable(options,result);
						
						final ResizablePieChart chart = new ResizablePieChart(dataTable, options);
						
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
	//Pintar la tabla de abajo
	private static DataTable getDataTable(PieOptions options, StatData<Integer, InvoiceType, Double> result) {
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, AON.MSG.year());
		LinkedHashMap<InvoiceType,Integer> colMap = new LinkedHashMap<InvoiceType,Integer>();
		int rowIndex = 0;
		int colIndex = 0;
		for (Integer year : result.getMap().keySet() ) {
			rowIndex = dataTable.addRow();
			dataTable.setValue(rowIndex, 0, AON.FMT_INT.format(year));
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

**/