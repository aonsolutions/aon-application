package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonDisplayTable extends FlowPanel {
	
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

	public AonDisplayTableRow addRow() {
		AonDisplayTableRow row = new AonDisplayTableRow();
		add(row);
		return row;
	}

	public FlowPanel addFooterRow() {
		FlowPanel footer = new FlowPanel();
		footer.setStyleName(AON.CSS.aonDisplayTableFooterRow());
		add(footer);
		return footer;
	}

	public AonDisplayTable addLabelWidgetRow(Widget label, Widget widget) {
		AonDisplayTableRow row = new AonDisplayTableRow();
		add(row);
		row.addCell(label, AON.CSS.aonTableLabel());
		row.addCell(widget);
		return this;
	}
}
