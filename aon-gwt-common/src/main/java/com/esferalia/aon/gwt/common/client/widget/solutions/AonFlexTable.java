package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonFlexTable extends FlowPanel {
	
	public AonFlexTable(String[] columnWidths, String ... style) {
		this(columnWidths);
		for (String st : style) {
			addStyleName(st);
		}
	}
	
	public AonFlexTable(String[] columnWidths) {
		setStyleName(AON.CSS.aonFlexTable());
		AonCollectionUtils.stream(columnWidths)
			.reduce((a,b) -> a + " " + b)
			.ifPresent(s -> getElement().getStyle().setProperty("grid-template-columns", s));
	}
	
	public FlowPanel addHeaderCell(int colspan) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonFlexTableHeaderCell());
		if (colspan > 1) {
			cell.getElement().setAttribute("style", "grid-column: span " + colspan);
		}
		add(cell);
		return cell;
	}
	public FlowPanel addHeaderCell(int colspan, String ... style ) {
		FlowPanel cell = addHeaderCell(colspan);
		for (String st : style) {
			cell.addStyleName(st);
		}
		return cell;
	}
	public AonFlexTable addHeaderCell( Widget widget, String ... style ) {
		return addHeaderCell(widget, 1, style);
	}
	public AonFlexTable addHeaderCell( Widget widget, int colspan, String ... style ) {
		widget.addStyleName(AON.CSS.aonFlexTableCellInner());
		addHeaderCell(colspan, style).add(widget);
		return this;
	}
	
	public AonFlexTableRow addRow() {
		AonFlexTableRow row = new AonFlexTableRow();
		add(row);
		return row;
	}
	
	public static class AonFlexTableRow extends FlowPanel implements HasClickHandlers {
		
		public AonFlexTableRow() {
			setStyleName(AON.CSS.aonFlexTableRow());
		}
		
		public HandlerRegistration addClickHandler(ClickHandler handler) {
			if (handler != null) {
				this.addStyleName(AON.CSS.aonClickable());
			}
			return addDomHandler(handler, ClickEvent.getType());
		}
		
		private FlowPanel addCell(int colspan) {
			FlowPanel cell = new FlowPanel();
			cell.setStyleName(AON.CSS.aonFlexTableCell());
			if (colspan > 1) {
				cell.getElement().setAttribute("style", "grid-column: span " + colspan);
			}
			add(cell);
			return cell;
		}
		private FlowPanel addCell(int colspan, String ... style ) {
			FlowPanel cell = addCell(colspan);
			for (String st : style) {
				cell.addStyleName(st);
			}
			return cell;
		}
		
		public AonFlexTableRow addCell( Widget widget, String ... style ) {
			return addCell(widget, 1, style);
		}
		
		public AonFlexTableRow addCell( Widget widget, int colspan, String ... style ) {
			widget.addStyleName(AON.CSS.aonFlexTableCellInner());
			addCell(colspan, style).add(widget);
			return this;
		}
		
		public AonFlexTableRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return addCellIfElse( condition,widget, new Label(), style);
		}
		public AonFlexTableRow addCellIfElse( boolean condition, Widget widget, Widget other, String ... style ) {
			return addCell( condition?widget:other , 1, style);
		}
	}

}
