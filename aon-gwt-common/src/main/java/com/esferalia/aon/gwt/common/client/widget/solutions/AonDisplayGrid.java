package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonDisplayGrid extends FlowPanel {
	
	public static class AonDisplayGridHeaderRow extends FlowPanel {
		public AonDisplayGridHeaderRow() {
			setStyleName(AON.CSS.aonDisplayGridHeaderRow());		
		}
		public AonDisplayGridCell addCell() {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			cell.setStyleName(AON.CSS.aonDisplayGridHeaderCell());
			add(cell);
			return cell;
		}
		public AonDisplayGridCell addCell(String ... style ) {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			for (String st : style) {
				cell.addStyleName(st);
			}
			add(cell);
			return cell;
		}
		public AonDisplayGridHeaderRow addCell( Widget widget ) {
			addCell().add(widget);
			return this;
		}
		public AonDisplayGridHeaderRow addCellIf( boolean condition, Widget widget ) {
			return condition?addCell(widget):this;
		}
		public AonDisplayGridHeaderRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return condition?addCell(widget,style):this;
		}
		public AonDisplayGridHeaderRow addCell(Widget widget, String ... style) {
			addCell(style).add(widget);
			return this;
		}
	}
	
	public static class AonDisplayGridFooterRow extends FlowPanel {
		public AonDisplayGridFooterRow() {
			setStyleName(AON.CSS.aonDisplayGridFooterRow());		
		}
		public AonDisplayGridCell addCell() {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			cell.setStyleName(AON.CSS.aonDisplayGridCell());
			add(cell);
			return cell;
		}
		public AonDisplayGridCell addCell(String ... style ) {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			for (String st : style) {
				cell.addStyleName(st);
			}
			add(cell);
			return cell;
		}
		public AonDisplayGridFooterRow addCell( Widget widget ) {
			addCell().add(widget);
			return this;
		}
		public AonDisplayGridFooterRow addCellIf( boolean condition, Widget widget ) {
			return condition?addCell(widget):this;
		}
		public AonDisplayGridFooterRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return condition?addCell(widget,style):this;
		}
		public AonDisplayGridFooterRow addCell(Widget widget, String ... style) {
			addCell(style).add(widget);
			return this;
		}
	}

	public static class AonDisplayGridRow extends FlowPanel {
		public AonDisplayGridRow() {
			setStyleName(AON.CSS.aonDisplayGridRow());		
		}
		public AonDisplayGridCell addCell() {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			add(cell);
			return cell;
		}
		public AonDisplayGridCell addCell(String ... style ) {
			AonDisplayGridCell cell = new AonDisplayGridCell();
			for (String st : style) {
				cell.addStyleName(st);
			}
			add(cell);
			return cell;
		}
		public AonDisplayGridRow addCellIf( boolean condition, Widget widget ) {
			return condition?addCell(widget):this;
		}
		public AonDisplayGridRow addCell( Widget widget ) {
			addCell().add(widget);
			return this;
		}
		public AonDisplayGridRow addCellIf( boolean condition, Widget widget, String ... style ) {
			return condition?addCell(widget,style):this;
		}
		public AonDisplayGridRow addCell(Widget widget, String ... style) {
			addCell(style).add(widget);
			return this;
		}
	}
	
	public static class AonDisplayGridCell extends FlowPanel {
		public AonDisplayGridCell() {
			setStyleName(AON.CSS.aonDisplayGridCell());		
		}
	}
	
	public AonDisplayGrid(String ... style) {
		this();
		for (String st : style) {
			addStyleName(st);
		}
	}
	
	public AonDisplayGrid() {
		setStyleName(AON.CSS.aonDisplayGrid());
	}

	public AonDisplayGridRow addRow() {
		AonDisplayGridRow row = new AonDisplayGridRow();
		add(row);
		return row;
	}

	public AonDisplayGridHeaderRow addHeaderRow() {
		AonDisplayGridHeaderRow row = new AonDisplayGridHeaderRow();
		add(row);
		return row;
	}
	
	public AonDisplayGridFooterRow addFooterRow() {
		AonDisplayGridFooterRow row = new AonDisplayGridFooterRow();
		add(row);
		return row;
	}
	
}
