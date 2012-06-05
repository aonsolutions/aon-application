package com.esferalia.aon.gwt.client.mdi;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.client.CustomDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class MDIWindow extends CustomDialog {

	interface Listener {
		void onClose(MDIWindow mdiWindow);

		void onMaximize(MDIWindow mdiWindow);

		void onMove(MDIWindow mdiWindow, int x, int y);

		void onClick(MDIWindow mdiWindow, ClickEvent event);
	}

	class WindowClickHandler implements ClickHandler {

		public WindowClickHandler() {
			MDIWindow.this.addClickHandler(this);
		}

		@Override
		public void onClick(ClickEvent clickEvent) {
			MDIWindow.this.fireOnClick(clickEvent);
		}
	}


	private List<Listener> listeners;

	public MDIWindow() {
		setModal(false);
		setGlassEnabled(false);
		new WindowClickHandler();
		listeners = new LinkedList<Listener>();
	}

	@Override
	public void onClose() {
		fireOnClose();
	}
	
	@Override
	public void handleMaximize() {
		fireOnMaximize();
	}
	
	@Override
	public void handleMove(int x, int y) {
		fireOnMove(x, y);
	}

	
	
	protected void addListener(Listener listener) {
		listeners.add(listener);
	}

	protected void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void fireOnClose() {
		for (Listener listener : listeners) {
			listener.onClose(this);
		}
	}

	private void fireOnMove(int x, int y) {
		for (Listener listener : listeners) {
			listener.onMove(this, x, y);
		}
	}

	private void fireOnClick(ClickEvent event) {
		for (Listener listener : listeners) {
			listener.onClick(this, event);
		}
	}

	private void fireOnMaximize() {
		for (Listener listener : listeners) {
			listener.onMaximize(this);
		}
	}
}
