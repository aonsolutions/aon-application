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
		header.getElement().getStyle().setProperty("min-height", "2rem");
		header.getElement().getStyle().setProperty("background-color", "transparent");
		header.getElement().getStyle().setProperty("position", "sticky");
		header.getElement().getStyle().setProperty("top", "-1px");
		header.getElement().getStyle().setProperty("z-index", "1");
		header.getElement().getStyle().setProperty("padding", "0 1rem");
		header.getElement().getStyle().setProperty("border-bottom", "1px solid #ddd");
	}

	public void addCellHeaderStyle(Label label) {
		label.getElement().getStyle().setProperty("background", "transparent");
		label.getElement().getStyle().setProperty("color", "#5f6368");
		label.getElement().getStyle().setProperty("font-size", ".8rem");
		label.getElement().getStyle().setProperty("font-weight", "bold");
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

	

}
