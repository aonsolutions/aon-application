package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomTable extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel header;
	private HTMLPanel footer;
	
	public AonCustomTable() {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("margin", "0");
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("gap", "0");
		getElement().getStyle().setProperty("width", "100%");
		getElement().getStyle().setProperty("min-width", "fit-content");
	}
	
	public void setMaxHeight(String maxHeight) {
		getElement().getStyle().setProperty("max-height", maxHeight);
	}
	
	public HTMLPanel createHeader() {
		header = new HTMLPanel(EMPTY_STRING);
		addHeaderStyle();
		add(header);
		return header;
	}
	
	public Label addHeader(Label label, String width) {
		addCellHeaderStyle(label);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			label.getElement().getStyle().setProperty("width", width);
			label.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else label.getElement().getStyle().setProperty("min-width", width);
		header.add(label);
		return label;
	}
	
	public Label addHeader(Label label, String width, String styles) {
		addCellHeaderStyle(label);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			label.getElement().getStyle().setProperty("width", width);
			label.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else label.getElement().getStyle().setProperty("min-width", width);
		header.add(label);
		addInlineStyle(label, styles);
		return label;
	}
	
	public Widget addHeader(Widget wdiget, String width, String styles) {
		addCellHeaderStyle(wdiget);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			wdiget.getElement().getStyle().setProperty("width", width);
			wdiget.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else wdiget.getElement().getStyle().setProperty("min-width", width);
		header.add(wdiget);
		addInlineStyle(wdiget, styles);
		return wdiget;
	}
	
	public void createFooter() {
		footer = new HTMLPanel(EMPTY_STRING);
		addFooterStyle();
		add(footer);
	}
	
	public Label addFooter(Label label, String width) {
		addCellFooterStyle(label);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			label.getElement().getStyle().setProperty("width", width);
			label.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else label.getElement().getStyle().setProperty("min-width", width);
		footer.add(label);
		return label;
	}
	
	public Label addFooter(Label label, String width, String styles) {
		addCellFooterStyle(label);
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			label.getElement().getStyle().setProperty("width", width);
			label.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else label.getElement().getStyle().setProperty("min-width", width);
		footer.add(label);
		addInlineStyle(label, styles);
		return label;
	}
	
	public void addInlineStyle(Widget label, String styleString) {
	    // Split the style string into individual properties (e.g., "text-align: right;")
	    String[] styleProperties = styleString.split(";");
	    
	    for (String property : styleProperties) {
	        // Split each property into key-value pairs (e.g., "text-align" and "right")
	        String[] keyValue = property.split(":");
	        
	        if (keyValue.length == 2) {
	            // Trim the key and value to remove any excess whitespace
	            String key = keyValue[0].trim();
	            String value = keyValue[1].trim();

	            // Convert the CSS property (e.g., "text-align") to camelCase (e.g., "textAlign")
	            String camelCaseKey = convertToCamelCase(key);

	            // Set the style on the element
	            label.getElement().getStyle().setProperty(camelCaseKey, value);
	        }
	    }
	}
	
	private String convertToCamelCase(String cssProperty) {
	    String[] parts = cssProperty.split("-");
	    StringBuilder camelCaseProperty = new StringBuilder(parts[0]);
	    
	    for (int i = 1; i < parts.length; i++) {
	        camelCaseProperty.append(Character.toUpperCase(parts[i].charAt(0)))
	                         .append(parts[i].substring(1));
	    }
	    
	    return camelCaseProperty.toString();
	}
	
	public void addHeader(AonTableButton button, String width) {
		button.getElement().getStyle().setProperty("width", width);
		header.add(button);
	}
	
	public void addHeaderStyle() {
		header.addStyleName(AON.CSS.aonItemFlex());
		header.addStyleName(AON.CSS.aonCustomTableHeader());
	}
	
	public void addFooterStyle() {
		footer.addStyleName(AON.CSS.aonItemFlex());
		footer.addStyleName(AON.CSS.aonCustomTableFooter());
	}

	public void addCellHeaderStyle(Label label) {
		label.addStyleName(AON.CSS.aonCustomTableCellHeader());
	}
	
	public void addCellHeaderStyle(Widget label) {
		label.addStyleName(AON.CSS.aonCustomTableCellHeader());
	}
	
	public void addCellFooterStyle(Label label) {
		label.addStyleName(AON.CSS.aonCustomTableCellFooter());
	}
	
	public HTMLPanel createRow() {
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		addRowStyle(row);
		add(row);
		return row;
	}
	
	public void addRow(HTMLPanel row, Widget widget, String width) {
		if(AonStringUtils.equalsIgnoreCase(width, "-moz-available")) {
			widget.getElement().getStyle().setProperty("width", width);
			widget.getElement().getStyle().setProperty("width", "-webkit-fill-available");
		} else widget.getElement().getStyle().setProperty("min-width", width);
		
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
