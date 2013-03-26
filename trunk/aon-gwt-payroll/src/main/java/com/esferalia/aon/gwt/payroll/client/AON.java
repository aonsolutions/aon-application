package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.NumberFormat;

public class AON {

	
	private static final int CONNECTION_STATUS_STOP = 0;
	private static final int CONNECTION_STATUS_START = 1;
	private static final int CONNECTION_STATUS_FAILED = 2;

	private static final String CONNECTION_STATUS_ELEMENTS[] = {
			"_viewRoot:status.stop", "_viewRoot:status.start", "status_error" };

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat
	.getFormat("#,##0.00");

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
	
	static final String AON_DATA_TABLE_ROW_EVEN = "aon-dataTable-row-even";
	static final String AON_DATA_TABLE_ROW_ODD = "aon-dataTable-row-odd";
	static final String AON_BOLD = "aon-bold";
	static final String AON_TEXT_RIGHT = "aon-text-right";
	static final String AON_TEXT_CENTER = "aon-text-center";
	static final String AON_ICON_RESET = "aon-icon-reset";
	static final String AON_EDIT_DATA_TABLE_BUTTON = "aon-editDataTable-button";
	static final String AON_ICON_DELETE = "aon-icon-delete";
	static final String AON_ICON_CANCEL = "aon-icon-cancel";
	static final String AON_ICON_ROW_SELECTOR = "aon-icon-rowSelector";
	static final String AON_ICON_EXCEPTION = "aon-icon-exception";

	
}
