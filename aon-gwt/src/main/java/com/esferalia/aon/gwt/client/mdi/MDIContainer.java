package com.esferalia.aon.gwt.client.mdi;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MDIContainer extends Composite implements MDIWindow.Listener {
	
	public interface Listener {
		void onOpen(MDIContainer mdiContainer, MDIWindow ...mdiWindows);
		void onClose(MDIContainer mdiContainer, MDIWindow ...mdiWindows);
		void onFocus(MDIContainer mdiContainer, MDIWindow ...mdiWindows);
	}
	
	interface IMDIWindowResizeState {
		void resize(MDIWindow mdiWindow, MaximizeHandler handler);
	}

	class MDIWindowMaximizeState implements IMDIWindowResizeState {

		@Override
		public void resize(MDIWindow mdiWindow, MaximizeHandler handler) {
			IMDIWindowResizeState nextState = new MDIWindowRestoreState(
					mdiWindow);
			MDIContainer.this.maximize(mdiWindow);
			handler.setState4(nextState, mdiWindow);
		}
	}

	class MDIWindowRestoreState implements IMDIWindowResizeState {

		int width, height, left, top;

		public MDIWindowRestoreState(MDIWindow mdiWindow) {
			width = mdiWindow.getOffsetWidth();
			height = mdiWindow.getOffsetHeight();

			int offsetX = getOffsetX(mdiWindow);
			int offsetY = getOffsetY(mdiWindow);
			top = MDIContainer.this.absPanel.getWidgetTop(mdiWindow) - offsetY;
			left = MDIContainer.this.absPanel.getWidgetLeft(mdiWindow)
					- offsetX;
		}

		@Override
		public void resize(MDIWindow mdiWindow, MaximizeHandler handler) {

			mdiWindow.setPixelSize(width, height);
			MDIContainer.this.absPanel.setWidgetPosition(mdiWindow, left, top);

			handler.setState4(new MDIWindowMaximizeState(), mdiWindow);
		}
	}

	private class MaximizeHandler {

		private Map<MDIWindow, IMDIWindowResizeState> states = new IdentityHashMap<MDIWindow, IMDIWindowResizeState>();

		void resize(MDIWindow mdiWindow) {
			IMDIWindowResizeState state = states.get(mdiWindow);
			if (state == null) {
				state = new MDIWindowMaximizeState();
			}
			state.resize(mdiWindow, this);
		}

		void remove(MDIWindow mdiWindow) {
			states.remove(mdiWindow);
		}

		void setState4(IMDIWindowResizeState state, MDIWindow mdiWindow) {
			states.put(mdiWindow, state);
		}
	}

	private AbsolutePanel absPanel;

	private MaximizeHandler maximizeHandler;
	
	private List<Listener> listeners ;
	
	private List<MDIWindow> mdiWindows;

	public MDIContainer() {
		absPanel = new AbsolutePanel();
		mdiWindows = new LinkedList<MDIWindow>();
		maximizeHandler = new MaximizeHandler();
		listeners = new LinkedList<MDIContainer.Listener>();
		initWidget(absPanel);
	}

	public final void addWindow(MDIWindow mdiWindow) {

		mdiWindow.addListener(this);

		mdiWindow.setVisible(false);
		absPanel.add(mdiWindow, 0, 0);
		
		// center the MDI window
		int clientWidth = absPanel.getElement().getClientWidth();
		int clientHeight = absPanel.getElement().getClientHeight();
		int offsetWidth = mdiWindow.getOffsetWidth();
		int offsetHeight = mdiWindow.getOffsetHeight();
		int left = offsetWidth > 0 ? (clientWidth - offsetWidth) / 2 : clientWidth /3 ;
		int top = offsetHeight > 0 ? (clientHeight - offsetHeight) / 2 : offsetHeight / 3;

		absPanel.setWidgetPosition(mdiWindow, Math.max(left, 0), Math.max(top, 0) );
		mdiWindow.setVisible(true);
		
		fireOnOpen(mdiWindow);
		
		setFocus(mdiWindow);
	}

	public final void removeAll() {
		int count = absPanel.getWidgetCount();
		MDIWindow  mdiWindows [] = new MDIWindow [count];
		for (int i = 0; i < count; i++) {
			MDIWindow mdiWindow = (MDIWindow) absPanel.getWidget(0);
			removeWindow(mdiWindow);
			mdiWindows[i] = mdiWindow;
		}
		fireOnClose(mdiWindows);
	}

	public final void removeWindow(MDIWindow mdiWindow) {
		removeWindowImpl(mdiWindow);
		fireOnClose(mdiWindow);
		remove(mdiWindow);
	}

	public void cascade() {
		int clientWidth= getRealClientWidth();
		int clientHeight= getRealClientHeight();
		for (int i = 0; i < mdiWindows.size(); i++) {
			MDIWindow mdiWindow = mdiWindows.get(i);
			mdiWindow.setPixelSize( 3 * clientWidth / 4 , 3 * clientHeight / 4 );
			absPanel.setWidgetPosition(mdiWindow, 25 * i , 25 * i );
		}

	}

	public void tileVertical() {
		int count = mdiWindows.size();
		int clientWidth= getRealClientWidth();
		int clientHeight= getRealClientHeight();
		for (int i = 0; i < count; i++) {
			MDIWindow mdiWindow = mdiWindows.get(i);
			mdiWindow.setPixelSize(clientWidth / count , clientHeight);
			absPanel.setWidgetPosition(mdiWindow, clientWidth / count  * i , 0);
		}
	}

	public void tileHorizontal() {
		int count = mdiWindows.size();
		int clientWidth= getRealClientWidth();
		int clientHeight= getRealClientHeight();
		for (int i = 0; i < count; i++) {
			MDIWindow mdiWindow = mdiWindows.get(i);
			absPanel.setWidgetPosition(mdiWindow, 0 , clientHeight / count  * i );
			mdiWindow.setPixelSize(clientWidth , clientHeight / count);
		}

	}
	
	public void center(MDIWindow mdiWindow){
		// center the MDI window
		int clientWidth = absPanel.getElement().getClientWidth();
		int clientHeight = absPanel.getElement().getClientHeight();
		int offsetWidth = mdiWindow.getOffsetWidth();
		int offsetHeight = mdiWindow.getOffsetHeight();
		int left = offsetWidth > 0 ? (clientWidth - offsetWidth) / 2 : clientWidth /3 ;
		int top = offsetHeight > 0 ? (clientHeight - offsetHeight) / 2 : offsetHeight / 3;

		absPanel.setWidgetPosition(mdiWindow, Math.max(left, 0), Math.max(top, 0) );
	}

	@Override
	public void onClose(MDIWindow mdiWindow) {
		removeWindow(mdiWindow);
	}

	@Override
	public void onMaximize(MDIWindow mdiWindow) {
		maximizeHandler.resize(mdiWindow);
	}

	@Override
	public void onMove(MDIWindow mdiWindow, int x, int y) {
		int offsetX = getOffsetX(mdiWindow);
		int offsetY = getOffsetY(mdiWindow);
		absPanel.setWidgetPosition(mdiWindow,
				absPanel.getWidgetLeft(mdiWindow) + x - offsetX,
				absPanel.getWidgetTop(mdiWindow) + y - offsetY);
		setFocus(mdiWindow);
	}

	@Override
	public void onClick(MDIWindow mdiWindow, ClickEvent clickEvent) {
		setFocus(mdiWindow);
	}

	public MDIWindow getFocus() {
		return mdiWindows.isEmpty() ? null : mdiWindows.get(mdiWindows.size()-1);
	}
	

	public void setFocus(MDIWindow mdiWindow) {
		add(mdiWindow);
		fireOnFocus(mdiWindow);
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}
	
	private void add(MDIWindow mdiWindow) {
		remove(mdiWindow);
		mdiWindows.add(mdiWindow);
		mdiWindow.getElement().getStyle().setZIndex(mdiWindows.size()-1);
	}	

	public int getClientWidth() {
		return absPanel.getElement().getClientWidth();
	}
	
	public int getClientHeight() {
		return absPanel.getElement().getClientHeight();
	}

	private void remove(MDIWindow mdiWindow) {
		if ( mdiWindows.remove(mdiWindow) ){
			for (int i = 0; i < mdiWindows.size(); i++) {
				mdiWindows.get(i).getElement().getStyle().setZIndex(i);
			}
		}
	}	
	

	private void removeWindowImpl(MDIWindow mdiWindow) {
		absPanel.remove(mdiWindow);
		mdiWindow.removeListener(this);
		maximizeHandler.remove(mdiWindow);
	}

	private void maximize(MDIWindow mdiWindow) {
		int clientWidth = absPanel.getElement().getClientWidth();
		int clientHeight = absPanel.getElement().getClientHeight();
		int offsetX = getOffsetX(mdiWindow);
		int offsetY = getOffsetY(mdiWindow);

		mdiWindow.setPixelSize(clientWidth - offsetX, clientHeight - offsetY);
		absPanel.setWidgetPosition(mdiWindow, 0, 0);
	}
	
	private int getRealClientWidth() {
		return absPanel.getElement().getClientWidth() - getOffsetX((MDIWindow)absPanel.getWidget(0));
	}
	
	private int getRealClientHeight() {
		return absPanel.getElement().getClientHeight() - getOffsetY((MDIWindow)absPanel.getWidget(0));
	}

	private int getOffsetX(MDIWindow mdiWindow) {
		int offLeft = getWidgetOffsetLeft(mdiWindow);
		int absLeft = absPanel.getWidgetLeft(mdiWindow);
		return absLeft - offLeft;
	}

	private int getOffsetY(MDIWindow mdiWindow) {
		int offLeft = getWidgetOffsetTop(mdiWindow);
		int absLeft = absPanel.getWidgetTop(mdiWindow);
		return absLeft - offLeft;
	}

	private int getWidgetOffsetTop(MDIWindow mdiWindow) {
		Element elem = mdiWindow.getElement();
		String cssLeft = DOM.getStyleAttribute(elem, "top");
		return Integer.parseInt(cssLeft.replace("px", "").trim());
	}

	private int getWidgetOffsetLeft(MDIWindow mdiWindow) {
		Element elem = mdiWindow.getElement();
		String cssLeft = DOM.getStyleAttribute(elem, "left");
		return Integer.parseInt(cssLeft.replace("px", "").trim());
	}
	
	private void fireOnOpen(final MDIWindow ...mdiWindows) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				for (Listener listener : listeners) {
					listener.onOpen(MDIContainer.this, mdiWindows);
				}
			}
		});
	}

	private void fireOnClose(final MDIWindow ...mdiWindows) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				for (Listener listener : listeners) {
					listener.onClose(MDIContainer.this, mdiWindows);
				}
			}
		});
	}
	
	private void fireOnFocus(final MDIWindow ...mdiWindows) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				for (Listener listener : listeners) {
					listener.onFocus(MDIContainer.this, mdiWindows);
				}
			}
		});
	}
	
}
