package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ResultsPanel extends ResizeComposite implements ProvidesResize {

	public static class ClearEvent extends GwtEvent<ClearHandler> {
		/**
		 * Handler type.
		 */
		public static Type<ClearHandler> TYPE = new Type<ClearHandler>();

		/**
		 * Fires a value change event on all registered handlers in the handler
		 * manager. If no such handlers exist, this method will do nothing.
		 * 
		 * @param <T>
		 *            the old value type
		 * @param source
		 *            the source of the handlers
		 * @param value
		 *            the value
		 */
		public static void fire(HasHandlers source) {
			ClearEvent event = new ClearEvent();
			source.fireEvent(event);
		}

		@Override
		protected void dispatch(ClearHandler handler) {
			// TODO Auto-generated method stub
			handler.onClear(this);
		}

		@Override
		public GwtEvent.Type<ClearHandler> getAssociatedType() {
			return TYPE;
		}

	}

	/**
	 * Handler for {@link ClearEvent} events.
	 */
	public static interface ClearHandler extends EventHandler {
		/**
		 * Called when a native clear event is fired.
		 * 
		 * @param event
		 *            the {@link ClearEvent} that was fired
		 */
		void onClear(ClearEvent event);

	}

	/**
	 * A object that implements this interface provides registration for
	 * {@link ClearHandler} instances.
	 */
	public static interface HasClearHandler extends HasHandlers {

		/**
		 * Adds a {@link ClearEvent} handler.
		 * 
		 * @param handler
		 *            the clear handler
		 * @return {@link HandlerRegistration} used to remove this handler
		 */
		HandlerRegistration addClearHandler(ClearHandler handler);
	}

	interface Binder extends UiBinder<DockLayoutPanel, ResultsPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Button clearButton;

	@UiField
	DockLayoutPanel dockLayoutPanel;

	public ResultsPanel() {
		initWidget(binder.createAndBindUi(this));
	}

	public void setWidget(Widget child) {
		dockLayoutPanel.add(child);
		TextBox b;
	}

	public void setHTML(String html) {
		// htmlPanel.add(new HTML(html));
	}

	public void addHTML(String html) {
		// htmlPanel.add(new HTML(html));
	}

	public HandlerRegistration addClearHandler(ClearHandler handler) {
		return addHandler(handler, ClearEvent.TYPE);
	}

	// ------------------------------------------------------------- UIHandlers

	@UiHandler("clearButton")
	void onClickClear(ClickEvent event) {
		ClearEvent.fire(this);
	}
}
