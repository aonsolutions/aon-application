package com.esferalia.aon.gwt.stat.client.panel;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.stat.JsStatData;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.stat.client.util.StatUtils;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.ComboChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;

public abstract class StatPanel implements EntryPoint {
	
	interface Binder extends UiBinder<Widget, StatPanel> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@UiField SimpleLayoutPanel content;	
	@UiField HTMLPanel searchContent;
	@UiField Button excel;
	@UiField Button pdf;

	StatParams params;

	protected abstract void excel();
	protected abstract void pdf();
	
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
	
	public StatPanel() {
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		map.put("from", new String[]{Long.toString(new Date().getTime())});
		this.params = new StatParams().setFrom(new Date())
				.setFilterMap(map);
	}

	@Override
	public void onModuleLoad() {
		AON.ensureInjected();
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
	}
	
	public void setSearchContent(Widget w){
		searchContent.add(w);
	}
	
	public void setContent(Widget w){
		content.setWidget(w);
	}
	
	protected ResizableComboChart comboChart(AonJsArray<JsStatData> result, String title, String label) {
		final ComboChart.Options options = ComboChart.createComboOptions();
		options.set("animation", StatUtils.ANIMATION);
		options.setWidth(content.getOffsetWidth());
		options.setHeight(content.getOffsetHeight());
		options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
		AxisOptions vaxis = AxisOptions.create();
		vaxis.setTitle(title);
		options.setVAxisOptions(vaxis);
		AxisOptions haxis = AxisOptions.create();
		haxis.setTitle(label);
		options.setHAxisOptions(haxis);
		options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
		
		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		result.stream().forEach(r -> table.put(r.getRow(), r.getColumn(), r.getQuantity()));
		return new ResizableComboChart(getDataTable(table, label), options);
	}

	protected PieChart pieChart(AonJsArray<JsStatData> result, String title, String label){
		final PieOptions options = PieChart.createPieOptions();
		options.set("animation", StatUtils.ANIMATION);
		options.setWidth(content.getOffsetWidth());
		options.setHeight(content.getOffsetHeight());
		options.set3D(true);
		AxisOptions vaxis = AxisOptions.create();
		vaxis.setTitle(title);
		options.setVAxisOptions(vaxis);
		AxisOptions haxis = AxisOptions.create();
		haxis.setTitle(label);
		options.setHAxisOptions(haxis);

		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		result.stream().forEach(r -> table.put(r.getRow(), r.getColumn(), r.getQuantity()));
	    return new ResizablePieChart(getDataTable(table, "Task"), options);
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

	public StatParams getParams() {
		return params;
	}

	public void setParams(StatParams params) {
		this.params = params;
	}
	
	@UiHandler("excel")
	void excel(ClickEvent event){
		excel();
	}
	
	@UiHandler("pdf")
	void pdf(ClickEvent event){
		pdf();
	}

}
