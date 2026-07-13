package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonFlexGrid extends FlowPanel {
	
	public AonFlexGrid(String[] columnWidths, String ... style) {
		this(columnWidths);
		for (String st : style) {
			addStyleName(st);
		}
	}
	
	public AonFlexGrid(String[] columnWidths) {
		setStyleName(AON.CSS.aonFlexGrid());
		AonCollectionUtils.stream(columnWidths)
			.reduce((a,b) -> a + " " + b)
			.ifPresent(s -> getElement().getStyle().setProperty("grid-template-columns", s));
	}
	
	public FlowPanel addCell(int colspan) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonFlexGridCell());
		if (colspan > 1) {
			cell.getElement().setAttribute("style", "grid-column: span " + colspan);
		}
		add(cell);
		return cell;
	}
	public FlowPanel addCell(int colspan, String ... style ) {
		FlowPanel cell = addCell(colspan);
		for (String st : style) {
			cell.addStyleName(st);
		}
		return cell;
	}
	public AonFlexGrid addCell( Widget widget, String ... style ) {
		return addCell(widget, 1, style);
	}
	public AonFlexGrid addCell( Widget widget, int colspan, String ... style ) {
		addCell(colspan, style).add(widget);
		return this;
	}
	
}
