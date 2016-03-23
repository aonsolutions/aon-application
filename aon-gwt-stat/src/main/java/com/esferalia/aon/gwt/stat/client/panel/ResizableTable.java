package com.esferalia.aon.gwt.stat.client.panel;

import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart.PieOptions;

public class ResizableTable extends Table implements RequiresResize {

	protected Options options;
	protected PieOptions po;
	protected AbstractDataTable data;

	public ResizableTable(AbstractDataTable data, Options options) {
		super(data, options);
		this.data = data;
		this.options = options;
	}
	
	public ResizableTable(AbstractDataTable data, PieOptions po) {
		super();
		this.data = data;
		this.po = po;
	}

	@Override
	public void onResize() {
		options.setWidth(getParent().getOffsetWidth() + "px");
//		options.setHeight(getParent().getOffsetHeight() + "px");
		draw(data, options);
	}
}
