package com.esferalia.aon.gwt.payroll.client;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class MinimizePanel extends ResizeComposite implements HasWidgets, AcceptsOneWidget {

	public static interface HasMinimizeHandlers extends HasHandlers {
		/**
		 * Adds a {@link MinimizeEvent} handler.
		 * 
		 * @param handler
		 *            the handler
		 * @return the registration for the event
		 */
		HandlerRegistration addMinimizeHandler(MinimizeHandler handler);
	}

	public static interface MinimizeHandler extends EventHandler {
		/**
		 * Called when MinimizeEvent is fired.
		 * 
		 * @param event
		 *            the MinimizeEvent that was fired
		 */
		void onMinimize(MinimizeEvent event);
	}

	public static class MinimizeEvent extends GwtEvent<MinimizeHandler> {
		/**
		 * Handler type.
		 */
		private static Type<MinimizeHandler> TYPE = new Type<MinimizeHandler>();

		/**
		 * Gets the event type associated with minimize events.
		 * 
		 * @return the handler type
		 */
		public static Type<MinimizeHandler> getType() {
			return TYPE;
		}
		
		public MinimizeEvent(Object source) {
			setSource(source);
		}

		@Override
		public Type<MinimizeHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(MinimizeHandler handler) {
			handler.onMinimize(this);
		}

		/**
		 * Fires a minimize event on all registered handlers in the handler
		 * manager.
		 * 
		 * @param <T>
		 *            the target type
		 * @param source
		 *            the source of the handlers
		 */
		public static <T> void fire(HasHandlers source) {
			source.fireEvent(new MinimizeEvent(source));
		}
	}

	public static interface MaximizeHandler extends EventHandler {
		/**
		 * Called when MaximizeEvent is fired.
		 * 
		 * @param event
		 *            the MaximizeEvent that was fired
		 */
		void onMaximize(MaximizeEvent event);
	}

	public static class MaximizeEvent extends GwtEvent<MaximizeHandler> {
		/**
		 * Handler type.
		 */
		private static Type<MaximizeHandler> TYPE = new Type<MaximizeHandler>();

		/**
		 * Gets the event type associated with Maximize events.
		 * 
		 * @return the handler type
		 */
		public static Type<MaximizeHandler> getType() {
			return TYPE;
		}

		/**
		 * Fires a maximize event on all registered handlers in the handler
		 * manager.
		 * 
		 * @param <T>
		 *            the target type
		 * @param source
		 *            the source of the handlers
		 */
		public static <T> void fire(HasHandlers source) {
			source.fireEvent(new MaximizeEvent(source));
		}
		
		public MaximizeEvent(Object source) {
			setSource(source);
		}

		@Override
		public Type<MaximizeHandler> getAssociatedType() {
			return TYPE;
		}

		@Override
		protected void dispatch(MaximizeHandler handler) {
			handler.onMaximize(this);
		}

	}

	interface Binder extends UiBinder<Widget, MinimizePanel> {

	}

	private static final Binder binder = GWT.create(Binder.class);
	
	
	@UiField
	SimpleLayoutPanel contentPanel;

	public MinimizePanel() {
		initWidget(binder.createAndBindUi(this));

	}

	@UiHandler("minimizeButton")
	void onMinimizeButtonClick(ClickEvent event) {
		MinimizeEvent.fire(this);
	}

	@UiHandler("minimizeButton")
	void onMaximizeButtonClick(ClickEvent event) {
		MaximizeEvent.fire(this);
	}

	// ------------------------------------------
	// HasWidgets
	// ------------------------------------------
	
	@Override
	public void clear() {
		contentPanel.clear();
	}
	
	@Override
	public void add(Widget w) {
		contentPanel.add(w);
	}
	
	@Override
	public Iterator<Widget> iterator() {
		return contentPanel.iterator();
	}

	@Override
	public boolean remove(Widget w) {
		return contentPanel.remove(w);
	}
	
	// ------------------------------------------
	
	@Override
	public void setWidget(IsWidget w) {
		contentPanel.setWidget(w);
	}
	
	
	@Override
	protected Widget getWidget() {
		return contentPanel.getWidget();
	}
	
	public HandlerRegistration addMinimizeHandler(MinimizeHandler handler) {
		return addHandler(handler, MinimizeEvent.getType());
	}

	public HandlerRegistration addMaximizeHandler(MaximizeHandler handler) {
		return addHandler(handler, MaximizeEvent.getType());
	}
	
}
