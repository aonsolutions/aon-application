package com.esferalia.aon.gwt.issues.client;

import java.util.Date;
import java.util.LinkedHashMap;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.stat.JsStatData;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.stat.client.panel.ResizableComboChart;
import com.esferalia.aon.gwt.stat.client.panel.ResizablePieChart;
import com.esferalia.aon.gwt.stat.client.util.StatUtils;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.task.TaskChartType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
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

public class StatPanel extends Composite {
	
	interface Binder extends UiBinder<Widget, StatPanel> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField SimpleLayoutPanel content;	
	@UiField HTMLPanel searchContent;

	Incidence incidence;
	
	private StatParams params;
	private IssueFilter issueFilter;
	private TaskChartType selectedChart;

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
	
	public StatPanel(Incidence incidence) {
		this.incidence = incidence;
		Date date = new Date();
		date.setYear(date.getYear()-1);
		this.params = new StatParams().setFrom(date).setTo(new Date());
		this.issueFilter = new IssueFilter();
		initWidget(binder.createAndBindUi(this));

		searchContent.add(new StatFilterPanel(this, incidence));
		selectStat(TaskChartType.TASK_BY_STATUS);
	}
	
	public void selectStat(TaskChartType sct) {
		selectedChart = sct;
		if(sct.equals(TaskChartType.TASK_BY_STATUS)){
			incidence.getStatDataByStatus(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_TYPE)){
			incidence.getStatDataByType(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_TAG)){
			incidence.getStatDataByTag(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
						
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
						
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_SCHEDULE)){
	    	incidence.getStatDataBySchedule(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_DAY_OF_WEEK)){
			incidence.getStatDataByDayOfWeek(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_MONTH)){
			incidence.getStatDataByMonth(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(comboChart(result.getData(), "Mes"));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_DAY)){
			incidence.getStatDataByDay(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(comboChart(result.getData(), "Dia"));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} else if(sct.equals(TaskChartType.TASK_BY_CUSTOMER)){
			incidence.getStatDataByCustomer(getIssueFilter(),new AsyncCallback<JSON<JsStatData>>() {
				
				@Override
				public void onSuccess(JSON<JsStatData> result) {
					content.setWidget(pieChart(result.getData()));
				}
				
				@Override public void onFailure(Throwable caught) {}
			});
		} 
	}
	
	private ResizableComboChart comboChart(AonJsArray<JsStatData> result, String label) {
		final ComboChart.Options options = ComboChart.createComboOptions();
		options.set("animation", StatUtils.ANIMATION);
		options.setWidth(content.getOffsetWidth());
		options.setHeight(content.getOffsetHeight());
		options.setSeriesType(com.google.gwt.visualization.client.visualizations.corechart.Series.Type.BARS);
		AxisOptions vaxis = AxisOptions.create();
		vaxis.setTitle(AON.MSG.quantity());
		options.setVAxisOptions(vaxis);
		AxisOptions haxis = AxisOptions.create();
		haxis.setTitle(label);
		options.setHAxisOptions(haxis);
		options.setColors(StatUtils.COMBO_CHART_SERIES_COLORS);
		
		final StatData<String, String, Double> table = new StatData<String, String, Double>();
		result.stream().forEach(r -> table.put(r.getRow(), r.getColumn(), r.getQuantity()));
		return new ResizableComboChart(getDataTable(table, label), options);
	}

	private PieChart pieChart(AonJsArray<JsStatData> result){
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

	public TaskChartType getSelectedChart() {
		return selectedChart;
	}

	public void setSelectedChart(TaskChartType selectedChart) {
		this.selectedChart = selectedChart;
	}

	public IssueFilter getIssueFilter() {
		return issueFilter;
	}

	public void setIssueFilter(IssueFilter issueFilter) {
		this.issueFilter = issueFilter;
	}
	
	
}
