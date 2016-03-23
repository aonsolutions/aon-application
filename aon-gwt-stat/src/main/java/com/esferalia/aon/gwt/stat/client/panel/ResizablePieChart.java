package com.esferalia.aon.gwt.stat.client.panel;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

public class ResizablePieChart extends PieChart implements RequiresResize {

	protected PieOptions options;
	protected AbstractDataTable data;

	public ResizablePieChart(AbstractDataTable data, PieOptions options) {
		super(data, options);
		this.data = data;
		this.options = options;
	}

	@Override
	public void onResize() {
		options.setWidth(getParent().getOffsetWidth());
		options.setHeight(getParent().getOffsetHeight());
		draw(data, options);
	}

}
