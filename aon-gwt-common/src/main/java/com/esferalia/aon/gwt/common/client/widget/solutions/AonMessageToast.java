package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AonMessageToast extends SimplePanel {

	public enum Type { DEFAULT, ERROR, SUCCESS, INFO }

	private static final int DEFAULT_DELAY = 5000;

	private static AonMessageToast instance;

	private final HTMLPanel content = new HTMLPanel("");
	private Timer hideTimer;

	private AonMessageToast() {
		setStyleName(AON.CSS.aonMessageToast());
		content.setStyleName(AON.CSS.aonMessageToastContent());
		setWidget(content);
	}

	private static AonMessageToast get() {
		if (instance == null) {
			instance = new AonMessageToast();
			RootPanel.get().add(instance);
		}
		return instance;
	}

	public static void showError(String message)   { show(message, Type.ERROR,   DEFAULT_DELAY); }
	public static void showInfo(String message)    { show(message, Type.INFO,    DEFAULT_DELAY); }
	public static void showSuccess(String message) { show(message, Type.SUCCESS, DEFAULT_DELAY); }

	public static void show(String message, Type type, int delayMs) {
		AonMessageToast toast = get();
		toast.content.getElement().setInnerText(message);
		toast.applyType(type);
		toast.display(true);
		if (toast.hideTimer != null) toast.hideTimer.cancel();
		toast.hideTimer = new Timer() {
			@Override public void run() { toast.display(false); }
		};
		toast.hideTimer.schedule(delayMs);
	}

	private void applyType(Type type) {
		content.removeStyleName(AON.CSS.aonMessageToastError());
		content.removeStyleName(AON.CSS.aonMessageToastSuccess());
		content.removeStyleName(AON.CSS.aonMessageToastInfo());
		switch (type) {
			case ERROR:   content.addStyleName(AON.CSS.aonMessageToastError());   break;
			case SUCCESS: content.addStyleName(AON.CSS.aonMessageToastSuccess()); break;
			case INFO:    content.addStyleName(AON.CSS.aonMessageToastInfo());    break;
			default: break;
		}
	}

	private void display(boolean show) {
		if (show) {
			addStyleName(AON.CSS.aonMessageToastVisible());
		} else {
			removeStyleName(AON.CSS.aonMessageToastVisible());
		}
	}
}
