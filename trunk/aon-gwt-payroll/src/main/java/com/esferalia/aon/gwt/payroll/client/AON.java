package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;

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

	
}
