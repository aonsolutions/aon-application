package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.i18n.client.NumberFormat;

public class AON {

	private static final int CONNECTION_STATUS_STOP = 0;
	private static final int CONNECTION_STATUS_START = 1;
	private static final int CONNECTION_STATUS_FAILED = 2;

	private static final String CONNECTION_STATUS_ELEMENTS[] = {
			"_viewRoot:status.stop", "_viewRoot:status.start", "status_error" };

	public static void start() {
		show(CONNECTION_STATUS_START);
	}

	public static void stop() {
		show(CONNECTION_STATUS_STOP);
	}

	public static void fail() {
		show(CONNECTION_STATUS_FAILED);
	}

	private static void show(int el) {

		for (int i = 0; i < el; i++) {
			Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[i])
					.getStyle().setDisplay(Display.NONE);
		}

		Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[el])
				.getStyle().clearDisplay();

		for (int i = el + 1; i < CONNECTION_STATUS_ELEMENTS.length; i++) {
			Document.get().getElementById(CONNECTION_STATUS_ELEMENTS[i])
					.getStyle().setDisplay(Display.NONE);
		}
	}

	public static String format(Double d) {
		return d == null ? null : CURRENCY_FORMAT.format(d);
	}

	public static final String AON_DATA_TABLE_ROW_EVEN = "aon-dataTable-row-even";
	public static final String AON_DATA_TABLE_ROW_ODD = "aon-dataTable-row-odd";
	public static final String AON_DATA_TABLE_ROW_HIGHLIGHT = "aon-dataTable-row-highlight";
	public static final String AON_DATA_TABLE_ROW_HIGHLIGHT_TOP = "aon-dataTable-row-highlight-top";
	public static final String AON_DATA_TABLE_CELL_HIGHLIGHT = "aon-dataTable-cell-highlight";
	public static final String AON_DATA_TABLE_CELL_HIGHLIGHT_TOP = "aon-dataTable-cell-highlight-top";
	public static final String AON_BOLD = "aon-bold";
	public static final String AON_WIDTH_ALL = "aon-width-all";
	public static final String AON_TEXT_RIGHT = "aon-text-right";
	public static final String AON_TEXT_CENTER = "aon-text-center";
	public static final String AON_ICON_RESET = "aon-icon-reset";
	public static final String AON_EDIT_DATA_TABLE_BUTTON = "aon-editDataTable-button";
	public static final String AON_ICON_COPY = "aon-icon-copy";
	public static final String AON_ICON_CHANGED = "aon-icon-changed";
	public static final String AON_ICON_CLIPBOARD = "aon-icon-clipboard";
	public static final String AON_ICON_DELETE = "aon-icon-delete";
	public static final String AON_ICON_CANCEL = "aon-icon-cancel";
	public static final String AON_ICON_ACCEPT = "aon-icon-accept";
	public static final String AON_ICON_INE = "aon-icon-ine";
	public static final String AON_ICON_AET = "aon-icon-aet";
	public static final String AON_ICON_WORKPLACE = "aon-icon-workplace";
	public static final String AON_ICON_EMPLOYEE = "aon-icon-employee";
	public static final String AON_ICON_ROW_SELECTOR = "aon-icon-rowSelector";
	public static final String AON_ICON_ROW_SELECTOR_S = "aon-icon-rowSelector-S";
	public static final String AON_ICON_ROW_SELECTOR_C = "aon-icon-rowSelector-C";
	public static final String AON_ICON_ROW_SELECTOR_CHANGED = "aon-icon-rowSelector-Changed";
	public static final String AON_ICON_EXCEPTION = "aon-icon-exception";
	public static final String AON_ICON_WARN = "aon-icon-warn";
	public static final String AON_ICON_ERRORWARNING = "aon-icon-errorwarning";
	public static final String GWT_HORIZONTAL_PANEL = "gwt-HorizontalPanel";
	public static final String AON_ICON_TASK_START = "aon-icon-task-start";
	public static final String AON_ICON_CMD_BUTTON = "aon-icon-commandButton";
	public static final String AON_ICON_TIME = "aon-icon-time";
	public static final String AON_ICON_EXCEL = "aon-icon-excel";
	public static final String AON_ICON_AGREEMENT = "aon-icon-agreement";
	public static final String AON_ICON_PAYMENT = "aon-icon-payment";
	public static final String AON_ICON_DEDUCTION = "aon-icon-deduction";
	public static final String AON_ICON_BONUS = "aon-icon-segsocial";
	public static final String AON_ICON_CONFIG = "aon-icon-config";
	public static final String AON_ICON_CLEAN = "aon-lookupButton-clear";
	public static final String AON_NO_MARGIN = "aon-no-margin";
	public static final String AON_INPUT_REQUIRED = "aon-input-required";
	public static final String AON_PADDING_LEFT = "aon-padding-left";
	
	public static final String RICH_CALENDAR_BUTTON = "rich-calendar-button";

	public static final NumberFormat CURRENCY_FORMAT = NumberFormat
			.getFormat("#,##0.00");
	public static final DateTimeFormat MONTH_FORMAT = DateTimeFormat
			.getFormat(PredefinedFormat.YEAR_MONTH_NUM);
	static final String AON_NOWRAP = "aon-nowrap";


}
