package com.esferalia.aon.gwt.stat.client.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.visualizations.Table;

public class ResizableTable extends Table implements RequiresResize {

	protected Options options;
	protected AbstractDataTable data;

	public ResizableTable(AbstractDataTable data, Options options) {
		super(data, options);
		this.data = data;
		this.options = options;
		setStyleName(AON.AON_CSS.aonWidthAll());
		addStyleName(AON.AON_CSS.aonHeightAll());
	}
	

	@Override
	public void onResize() {
		options.setWidth(getParent().getOffsetWidth() + "px");
		options.setHeight(getParent().getOffsetHeight() + "px");
		draw(data, options);
	}
}
