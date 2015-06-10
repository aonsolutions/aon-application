package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public abstract class AbstractEventsDraft extends ResizeComposite {

	protected interface DateRange {
	
			String getDescription();
	
			DateField getDateField();
	
			Date getStart(Date date);
	
			Date getNext(Date date);
	
			Date getPrevious(Date date);
	
			Date[] getSplits(Date start, Date end);
	
			Date parseSplit(String str);
	
			String formatSplit(Date start);
	
			String format(Date start, Date end);
	
		}

	protected static class WeekDateRange implements DateRange {
	
			private static final int WEEK_DAYS = 7;
			private static final DateTimeFormat SPLIT_DATE_FORMAT = DateTimeFormat
					.getFormat("EEE dd/M");
	
			private static final DateTimeFormat START_DATE_FORMAT = DateTimeFormat
					.getFormat("dd");
			private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
					.getFormat("dd 'de' MMMM 'de' yyyy");
	
			@Override
			public String getDescription() {
				return "Semana";
			}
	
			@Override
			public DateField getDateField() {
				return DateField.DAY;
			}
	
			@Override
			public Date getStart(Date date) {
				return DateUtils.getFirstDayOfWorkWeek(date);
			}
	
			@Override
			public Date getNext(Date date) {
				Date next = CalendarUtil.copyDate(date);
				CalendarUtil.addDaysToDate(next, WEEK_DAYS);
				return next;
			}
	
			@Override
			public Date getPrevious(Date date) {
				Date next = CalendarUtil.copyDate(date);
				CalendarUtil.addDaysToDate(next, -1 * WEEK_DAYS);
				return next;
			}
	
			@Override
			public Date[] getSplits(Date start, Date end) {
				Date days[] = new Date[WEEK_DAYS];
				for (int i = 0; i < WEEK_DAYS; i++) {
					days[i] = CalendarUtil.copyDate(start);
					CalendarUtil.addDaysToDate(days[i], i);
				}
				return days;
			}
	
			@Override
			public Date parseSplit(String str) {
				return SPLIT_DATE_FORMAT.parse(str);
			}
	
			@Override
			public String formatSplit(Date start) {
				return SPLIT_DATE_FORMAT.format(start);
			}
	
			@Override
			public String format(Date start, Date end) {
				return START_DATE_FORMAT.format(start) + " - "
						+ END_DATE_FORMAT.format(end);
			}
	
		}

	protected static class MonthDateRange implements DateRange {
	
			private static final DateTimeFormat SPLIT_DATE_FORMAT = DateTimeFormat
					.getFormat("dd/M");
	
			private static final DateTimeFormat START_DATE_FORMAT = DateTimeFormat
					.getFormat("dd");
			private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
					.getFormat("dd 'de' MMMM 'de' yyyy");
	
			@Override
			public String getDescription() {
				return "Mes";
			}
	
			@Override
			public DateField getDateField() {
				return DateField.DAY;
			}
	
			@Override
			public Date getStart(Date date) {
				return DateUtils.getFirstDayOfMonth(date);
			}
	
			@Override
			public Date getNext(Date date) {
				Date next = CalendarUtil.copyDate(date);
				CalendarUtil.addMonthsToDate(next, 1);
				return next;
			}
	
			@Override
			public Date getPrevious(Date date) {
				Date prev = CalendarUtil.copyDate(date);
				CalendarUtil.addMonthsToDate(prev, -1);
				return prev;
			}
	
			@Override
			public Date[] getSplits(Date start, Date end) {
				int count = CalendarUtil.getDaysBetween(start, end) + 1;
				Date days[] = new Date[count];
				for (int i = 0; i < count; i++) {
					days[i] = CalendarUtil.copyDate(start);
					CalendarUtil.addDaysToDate(days[i], i);
				}
				return days;
			}
	
			@Override
			public Date parseSplit(String str) {
				return SPLIT_DATE_FORMAT.parse(str);
			}
	
			@Override
			public String formatSplit(Date start) {
				return SPLIT_DATE_FORMAT.format(start);
			}
	
			@Override
			public String format(Date start, Date end) {
				return START_DATE_FORMAT.format(start) + " - "
						+ END_DATE_FORMAT.format(end);
			}
	
		}

	protected static class YearDateRange implements DateRange {
	
			private static final int YEAR_MONTHS = 12;
	
			private static final DateTimeFormat SPLIT_DATE_FORMAT = DateTimeFormat
					.getFormat("MMMM");
	
			private static final DateTimeFormat START_DATE_FORMAT = DateTimeFormat
					.getFormat("dd 'de' MMMM");
			private static final DateTimeFormat END_DATE_FORMAT = DateTimeFormat
					.getFormat("dd 'de' MMMM 'de' yyyy");
	
			@Override
			public String getDescription() {
				return "A\u00f1o";
			}
	
			@Override
			public DateField getDateField() {
				return DateField.MONTH;
			}
	
			@Override
			public Date getStart(Date date) {
				return DateUtils.getFirstDayOfYear(date);
			}
	
			@Override
			public Date getNext(Date date) {
				Date next = CalendarUtil.copyDate(date);
				CalendarUtil.addMonthsToDate(next, YEAR_MONTHS);
				return next;
			}
	
			@Override
			public Date getPrevious(Date date) {
				Date next = CalendarUtil.copyDate(date);
				CalendarUtil.addMonthsToDate(next, -1 * YEAR_MONTHS);
				return next;
			}
	
			@Override
			public Date[] getSplits(Date start, Date end) {
				Date months[] = new Date[YEAR_MONTHS];
				for (int i = 0; i < YEAR_MONTHS; i++) {
					months[i] = CalendarUtil.copyDate(start);
					CalendarUtil.addMonthsToDate(months[i], i);
				}
				return months;
			}
	
			@Override
			public Date parseSplit(String str) {
				return SPLIT_DATE_FORMAT.parse(str);
			}
	
			@Override
			public String formatSplit(Date start) {
				return SPLIT_DATE_FORMAT.format(start);
			}
	
			@Override
			public String format(Date start, Date end) {
				return START_DATE_FORMAT.format(start) + " - "
						+ END_DATE_FORMAT.format(end);
			}
		}

	protected static class Td {
			int row;
			int col;
	
			public Td(int row, int col) {
				this.row = row;
				this.col = col;
			}
	
			public int getCol() {
				return col;
			}
	
			public int getRow() {
				return row;
			}
	
			@Override
			public boolean equals(Object obj) {
				return (obj instanceof Td) && (((Td) obj).row == row)
						&& (((Td) obj).col == col);
			}
		}

	public static final int WEEK_DATE_RANGE = 0;

	protected static Element getEventTargetCell(com.google.gwt.user.client.Event event, Element tableElem) {
		Element td = DOM.eventGetTarget(event);
		for (; td != null; td = DOM.getParent(td)) {
			// If it's a TD, it might be the one we're looking for.
			if (DOM.getElementProperty(td, "tagName").equalsIgnoreCase("td")) {
				// Make sure it's directly a part of this table before returning
				// it.
				Element tr = DOM.getParent(td);
				Element body = DOM.getParent(tr);
				Element table = DOM.getParent(body);
				if (table == tableElem) {
					return td;
				}
			}
			// If we run into this table's body, we're out of options.
			if (td == tableElem) {
				return null;
			}
		}
		return null;
	}

	protected static Element cloneTR(Element tr) {
	
		Element rt = DOM.clone(tr, true);
	
		com.google.gwt.dom.client.Element td = tr.getFirstChildElement();
		com.google.gwt.dom.client.Element dt = rt.getFirstChildElement();
	
		while (td != null) {
			Style style = dt.getStyle();
			style.setWidth(td.getClientWidth(), Unit.PX);
			// remove padding already included at above 'client' width.
			style.setPaddingLeft(0, Unit.PX);
			style.setPaddingRight(0, Unit.PX);
	
			// remove padding already included at above 'client' height.
			style.setHeight(td.getClientHeight(), Unit.PX);
			style.setPaddingTop(0, Unit.PX);
			style.setPaddingBottom(0, Unit.PX);
	
			td = td.getNextSiblingElement();
			dt = dt.getNextSiblingElement();
		}
	
		return rt;
	
	}

	protected static void removeFromParent(Element el) {
		if (el != null && el.hasParentElement())
			el.removeFromParent();
	
	}

	protected static Element cloneUpperLeftEl(FlexTable flexTable, int cols,
			int rows) {
			
				Element table = DOM.createTable();
				Element tbody = DOM.createTBody();
				DOM.appendChild(table, tbody);
			
				for (int row = 0; row < rows; row++) {
					Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
					// remove all columns except 'cols' at right.
					for (int i = rt.getChildCount(); i > cols; i--)
						rt.getChild(i - 1).removeFromParent();
			
					DOM.appendChild(tbody, rt);
			
				}
			
				Style style = table.getStyle();
				style.setPosition(Position.FIXED);
				style.setBackgroundColor("white");
				style.setProperty("width", "auto"); /* override width: 100% */
			
				table.setClassName(flexTable.getElement().getClassName());
			
				return table;
			}

	protected static Element cloneUpperRightEl(FlexTable flexTable, int cols,
			int rows) {
			
				Element table = DOM.createTable();
				Element tbody = DOM.createTBody();
				DOM.appendChild(table, tbody);
			
				for (int row = 0; row < rows; row++) {
					Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
					// remove all columns except 'cols' at left.
					for (int i = rt.getChildCount() - 1 - cols; i >= 0; i--)
						rt.getChild(i).removeFromParent();
			
					DOM.appendChild(tbody, rt);
			
				}
			
				Style style = table.getStyle();
				style.setPosition(Position.FIXED);
				style.setBackgroundColor("white");
				style.setProperty("width", "auto"); /* override width: 100% */
			
				table.setClassName(flexTable.getElement().getClassName());
			
				return table;
			}


	protected static Element cloneLeftColEl(FlexTable flexTable, int cols,
			int start) {
			
				Element table = DOM.createTable();
				Element tbody = DOM.createTBody();
				DOM.appendChild(table, tbody);
			
				int rows = flexTable.getRowCount();
			
				for (int row = start; row < rows; row++) {
					Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
					// remove all columns except 'cols' at right.
					for (int i = rt.getChildCount(); i > cols; i--)
						rt.getChild(i - 1).removeFromParent();
			
					DOM.appendChild(tbody, rt);
				}
			
				Style style = table.getStyle();
				style.setPosition(Position.FIXED);
				style.setBackgroundColor("white");
				style.setProperty("width", "auto"); /* override width: 100% */
			
				table.setClassName(flexTable.getElement().getClassName());
			
				return table;
			}

	protected static Element cloneRightColEl(FlexTable flexTable, int cols,
			int start) {
			
				Element table = DOM.createTable();
				Element tbody = DOM.createTBody();
				DOM.appendChild(table, tbody);
			
				int rows = flexTable.getRowCount();
			
				for (int row = start; row < rows; row++) {
					Element rt = cloneTR(flexTable.getRowFormatter().getElement(row));
					// remove all columns except 'cols' at left.
					for (int i = rt.getChildCount() - 1 - cols; i >= 0; i--)
						rt.getChild(i).removeFromParent();
			
					DOM.appendChild(tbody, rt);
				}
			
				Style style = table.getStyle();
				style.setPosition(Position.FIXED);
				style.setBackgroundColor("white");
				style.setProperty("width", "auto"); /* override width: 100% */
			
				table.setClassName(flexTable.getElement().getClassName());
			
				return table;
			}

	protected static void moveEl(Element el, int top, int left,
			int width, int height) {
			
				Style style = el.getStyle();
				style.setTop(top, Unit.PX);
				style.setLeft(left, Unit.PX);
			
				setClip(style, 0, width, height, 0);
			}

	protected static void moveEl(Element el, int top, int left,
			int clipTop, int clipLeft, int clipRight, int clipBottom) {
			
				Style style = el.getStyle();
				style.setTop(top, Unit.PX);
				style.setLeft(left, Unit.PX);
			
				setClip(style, clipTop, clipRight, clipBottom, clipLeft);
			}

	private static void setClip(Style style, int top, int right,
			int bottom, int left) {
				style.setProperty("clip", "rect(" + top + "px," + right + "px,"
						+ bottom + "px, " + left + "px)");
			}

	protected static void hide(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}

	protected static void show(UIObject uiObject) {
		uiObject.getElement().getStyle().setVisibility(Visibility.VISIBLE);
	}

	protected static boolean equals(Object obj1, Object obj2) {
		if (obj1 == obj2)
			return true;
		if (obj1 == null)
			return false;
		if (obj2 == null)
			return false;
		return obj1.equals(obj2);
	}
	

	protected static enum DateField {
			DAY, WEEK, MONTH, YEAR;
	}


	public AbstractEventsDraft() {
		super();
	}

}