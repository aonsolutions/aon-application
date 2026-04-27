package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AonDisplayTable extends FlowPanel {
	
	public static class AonDisplayTableHeaderRow extends FlowPanel {
		public AonDisplayTableHeaderRow() {
			setStyleName(AON.CSS.aonDisplayGridHeaderRow());		
		}
		public AonDisplayTableCell addCell() {
			AonDisplayTableCell cell = new AonDisplayTableCell();
			cell.setStyleName(AON.CSS.aonDisplayGridHeaderCell());
			add(cell);
			return cell;
		}
		public AonDisplayTableCell addCell(String ... style ) {
			AonDisplayTableCell cell = addCell();
			for (String st : style) {
				cell.addStyleName(st);
			}
			return cell;
		}
		public AonDisplayTableHeaderRow addCell( Widget widget ) {
			addCell().add(widget);
			return this;
		}
		public AonDisplayTableHeaderRow addCellIf( boolean condition, Widget widget ) {
			return condition?addCell(widget):this;
		}
		public AonDisplayTableHeaderRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return condition?addCell(widget,style):this;
		}
		public AonDisplayTableHeaderRow addCell(Widget widget, String ... style) {
			addCell(style).add(widget);
			return this;
		}
	}

	public static class AonDisplayTableRow extends FlowPanel {
		public AonDisplayTableRow() {
			setStyleName(AON.CSS.aonDisplayTableRow());		
		}
		public AonDisplayTableCell addCell() {
			AonDisplayTableCell cell = new AonDisplayTableCell();
			add(cell);
			return cell;
		}
		public AonDisplayTableCell addCell(String ... style ) {
			AonDisplayTableCell cell = new AonDisplayTableCell();
			for (String st : style) {
				cell.addStyleName(st);
			}
			add(cell);
			return cell;
		}
		public AonDisplayTableRow addCellIf( boolean condition, Widget widget ) {
			return condition?addCell(widget):this;
		}
		public AonDisplayTableRow addCell( Widget widget ) {
			addCell().add(widget);
			return this;
		}
		public AonDisplayTableRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return condition?addCell(widget,style):this;
		}
		public AonDisplayTableRow addCell(Widget widget, String ... style) {
			addCell(style).add(widget);
			return this;
		}
		public AonDisplayTableRow addEmptyCell() {
			return addCell( new Label() );
		}
		public AonDisplayTableRow addEmptyCellIf( boolean condition) {
			return addCellIf( condition, new Label() );
		}
	}
	
	public static class AonDisplayTableCell extends FlowPanel {
		public AonDisplayTableCell() {
			setStyleName(AON.CSS.aonDisplayTableCell());		
		}
	}
	
	public AonDisplayTable(String ... style) {
		this();
		for (String st : style) {
			addStyleName(st);
		}
	}
	
	public AonDisplayTable() {
		setStyleName(AON.CSS.aonDisplayTable());
	}
	
	public AonDisplayTableRow addRow(String ... styles) {
		AonDisplayTableRow row = addRow();
		for (String st : styles) {
			row.addStyleName(st);
		}
		return row;
	}

	public AonDisplayTableRow addRow() {
		AonDisplayTableRow row = new AonDisplayTableRow();
		add(row);
		return row;
	}

	public AonDisplayTableHeaderRow addHeaderRow() {
		AonDisplayTableHeaderRow row = new AonDisplayTableHeaderRow();
		add(row);
		return row;
	}

	public FlowPanel addFooterRow() {
		FlowPanel footer = new FlowPanel();
		footer.setStyleName(AON.CSS.aonDisplayTableFooterRow());
		add(footer);
		return footer;
	}
	public AonDisplayTable addLabelWidgetRow(String label, Widget widget) {
		return addLabelWidgetRow(new Label(label), widget);	
	}

	public AonDisplayTable addLabelWidgetRow(Widget label, Widget widget) {
		AonDisplayTableRow row = new AonDisplayTableRow();
		add(row);
		row.addCell(label, AON.CSS.aonWidth200());
		row.addCell(widget);
		return this;
	}
	
}
