package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class OLD_AonConsoleWidget extends ScrollPanel {
	
	private final HTMLPanel consoleWidget;
	private int lastIndex = 0;
	
	public OLD_AonConsoleWidget() {
		setStyleName(AON.CSS.aonScrollArea());
		consoleWidget = new HTMLPanel("pre","");
		consoleWidget.setStyleName(AON.CSS.aonPadding());
		consoleWidget.getElement().getStyle().setBackgroundColor("black");
		consoleWidget.getElement().getStyle().setColor("white");
		setWidget(consoleWidget);
	}

	public void log(String text) {
		int newLastIndex = AonStringUtils.lastIndexOf(text, '\n');
		String text2 = AonStringUtils.substring(text, lastIndex, newLastIndex);
		String[] array = AonStringUtils.split(text2, '\n');
		if (array != null) {
			Arrays.stream(array)
				.forEach(line ->  consoleWidget.add(new Label(line)));
		}
		lastIndex = newLastIndex;
		scrollToBottom();
	}
	
}
