package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomTable extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel header;
	
	public AonCustomTable() {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("gap", "0");
	}
	
	public void setMaxHeight(String maxHeight) {
		getElement().getStyle().setProperty("max-height", maxHeight);
	}
	
	public void createHeader() {
		header = new HTMLPanel(EMPTY_STRING);
		addHeaderStyle();
		add(header);
	}
	
	public void addHeader(Label label, String width) {
		addCellHeaderStyle(label);
		label.getElement().getStyle().setProperty("width", width);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available"))
			label.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		header.add(label);
	}
	
	public void addHeader(AonTableButton button, String width) {
		button.getElement().getStyle().setProperty("width", width);
		header.add(button);
	}
	
	public void addHeaderStyle() {
		header.addStyleName(AON.CSS.aonItemFlex());
		header.addStyleName(AON.CSS.aonCustomTableHeader());
	}

	public void addCellHeaderStyle(Label label) {
		label.addStyleName(AON.CSS.aonCustomTableCellHeader());
	}
	
	public HTMLPanel createRow() {
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		addRowStyle(row);
		add(row);
		return row;
	}
	
	public void addRow(HTMLPanel row, Widget widget, String width) {
		widget.getElement().getStyle().setProperty("width", width);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available"))
			widget.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		row.add(widget);
	}
	
	public void addRowStyle(HTMLPanel row) {
		row.addStyleName(AON.CSS.aonItemFlex());
		row.addStyleName(AON.CSS.aonCustomRow());
	}

	public Integer getRowsCount() {
		return getWidgetCount() - 1;
	}

	public Widget getWidget(int row, int col) {
		HTMLPanel htmlPanelRow = (HTMLPanel) this.getWidget(row);
		Widget widget = htmlPanelRow.getWidget(col);
		return widget;
	}

}
