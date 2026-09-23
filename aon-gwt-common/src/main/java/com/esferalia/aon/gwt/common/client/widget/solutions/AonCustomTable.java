package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomTable extends HTMLPanel {

	private static final String EMPTY_STRING = "";
	private HTMLPanel header;
	private HTMLPanel footer;
	
	public AonCustomTable() {
		super(EMPTY_STRING);
		addStyleName(AON.CSS.aonFlexColumn2());
		getElement().getStyle().setProperty("margin", "0");
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("gap", "0");
		getElement().getStyle().setProperty("width", "100%");
		getElement().getStyle().setProperty("boxSizing", "border-box");
		getElement().getStyle().setProperty("minWidth", "0");
		getElement().getStyle().setProperty("maxWidth", "100%");
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
		applyCellWidth(label, width);
		header.add(label);
		return label;
	}
	
	public Label addHeader(Label label, String width, String styles) {
		addCellHeaderStyle(label);
		applyCellWidth(label, width);
		header.add(label);
		addInlineStyle(label, styles);
		return label;
	}
	
	public Widget addHeader(Widget wdiget, String width, String styles) {
		addCellHeaderStyle(wdiget);
		applyCellWidth(wdiget, width);
		header.add(wdiget);
		addInlineStyle(wdiget, styles);
		return wdiget;
	}
	
	public void addHeader(AonTableButton button, String width) {
		applyCellWidth(button, width);
		header.add(button);
	}
	
	public void createFooter() {
		footer = new HTMLPanel(EMPTY_STRING);
		addFooterStyle();
		add(footer);
	}

	public Label addFooter(Label label, String width) {
		addCellFooterStyle(label);
		applyCellWidth(label, width);
		footer.add(label);
		return label;
	}

	public Label addFooter(Label label, String width, String styles) {
		addCellFooterStyle(label);
		applyCellWidth(label, width);
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
	
	private static final String FLEX_WIDTH = "-moz-available";

	/** Aplica el ancho de una celda. El valor {@value #FLEX_WIDTH} marca la columna
	 *  el\u00e1stica: crece con el espacio sobrante y puede encoger (min-width: 0),
	 *  que es lo que permite el ellipsis sin desbordar la fila. */
	private void applyCellWidth(Widget widget, String width) {
		Style style = widget.getElement().getStyle();
		style.setProperty("boxSizing", "border-box");
		style.setProperty("minWidth", "0");

		if (AonStringUtils.equalsIgnoreCase(width, FLEX_WIDTH)) {
			style.setProperty("flex", "1 1 0");
			style.clearProperty("width");
			style.clearProperty("maxWidth");
		} else {
			style.setProperty("flex", "0 0 " + width);
			style.setProperty("width", width);
			style.setProperty("maxWidth", width);
		}
	}
	
	public void addHeaderStyle() {
		header.addStyleName(AON.CSS.aonItemFlex());
		header.addStyleName(AON.CSS.aonCustomTableHeader());
		applyContainerStyle(header);
	}

	public void addFooterStyle() {
		footer.addStyleName(AON.CSS.aonItemFlex());
		footer.addStyleName(AON.CSS.aonCustomTableFooter());
		applyContainerStyle(footer);
	}

	private void applyContainerStyle(HTMLPanel panel) {
		panel.getElement().getStyle().setProperty("width", "100%");
		panel.getElement().getStyle().setProperty("minWidth", "0");
		panel.getElement().getStyle().setProperty("maxWidth", "100%");
		panel.getElement().getStyle().setProperty("boxSizing", "border-box");
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
		applyCellWidth(widget, width);
		row.add(widget);
	}

	public void addRowStyle(HTMLPanel row) {
		row.addStyleName(AON.CSS.aonItemFlex());
		row.addStyleName(AON.CSS.aonCustomRow());
		applyContainerStyle(row);
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
