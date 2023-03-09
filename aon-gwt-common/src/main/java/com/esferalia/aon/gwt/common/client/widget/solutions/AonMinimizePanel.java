package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Iterator;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safecss.shared.SafeStylesBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonMinimizePanel extends ResizeComposite implements HasWidgets, AcceptsOneWidget, ProvidesResize {

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

	interface Binder extends UiBinder<Widget, AonMinimizePanel> {

	}

	private SimpleLayoutPanel contentPanel;
	
	private FlowPanel buttons;

	private AbsolutePanel absolutePanel;

	public AonMinimizePanel() {
		absolutePanel = new AbsolutePanel();
		absolutePanel.setStyleName(AON.CSS.aonSelector());

		buttons = new FlowPanel();
		buttons.setStyleName(AON.CSS.aonSelector());
		buttons.addStyleName(AON.CSS.aonTextRight());
		Button minimizeButton = new Button();
		minimizeButton.setStyleName(AON.CSS.aonTabButton());
		minimizeButton.addStyleName(AON.CSS.aonIconMinimize());
		minimizeButton.addStyleName(AON.CSS.aonPointerEventsAuto());
		minimizeButton.getElement().getStyle().setPosition(Position.RELATIVE);
		minimizeButton.getElement().getStyle().setZIndex(1);
		minimizeButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MinimizeEvent.fire(AonMinimizePanel.this);
			}
		});
		
		Button maximizeButton = new Button();
		maximizeButton.setStyleName(AON.CSS.aonTabButton());
		maximizeButton.addStyleName(AON.CSS.aonIconMaximize());
		maximizeButton.addStyleName(AON.CSS.aonPointerEventsAuto());
		maximizeButton.getElement().getStyle().setPosition(Position.RELATIVE);
		maximizeButton.getElement().getStyle().setZIndex(1);
		maximizeButton.addClickHandler( new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				MaximizeEvent.fire(AonMinimizePanel.this);
			}
		});
		
		buttons.add(minimizeButton);
		buttons.add(maximizeButton);
		absolutePanel.add(buttons);
		
		contentPanel = new SimpleLayoutPanel();
		contentPanel.setStyleName(AON.CSS.aonWidthAll());
		contentPanel.addStyleName(AON.CSS.aonHeightAll());
		contentPanel.addStyleName(AON.CSS.aonPointerEventsNone());
		contentPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
		contentPanel.getElement().getStyle().setTop(0.0, Unit.PX);
		contentPanel.getElement().getStyle().setLeft(0.0, Unit.PX);
		absolutePanel.add(contentPanel);
		
		initWidget(absolutePanel);

	}


	@UiHandler("maximizeButton")
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
	
	public void initNewButtons() {
		clearButtons();

		this.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		this.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);

		buttons.getElement().getStyle().setMarginTop(-18,  Unit.PX);
		buttons.getElement().getStyle().setPosition(Position.ABSOLUTE);
		buttons.getElement().getStyle().setZIndex(3);
		buttons.getElement().getStyle().setRight(0, Unit.PX);
	}
	
	public void clearButtons() {
		buttons.clear();
	}
	
	public void addButtonLess() {
		clearButtons();
		Button btn = createButtonIcon("expand_less", "Maximizar");
		btn.addClickHandler( (e)->{ 
			addButtonMore();
			MaximizeEvent.fire(AonMinimizePanel.this);
		});
		buttons.add(btn);
	}
	
	public void addButtonMore() {
		clearButtons();
		Button btn = createButtonIcon("expand_more", "Minimizar");
		btn.addClickHandler( (e)-> {
			addButtonLess();
			MinimizeEvent.fire(AonMinimizePanel.this);
		});
		buttons.add(btn);
	}
	
	public HandlerRegistration addMinimizeHandlerNew(MinimizeHandler handler) {
		clearButtons();
		addButtonLess();
		return addMinimizeHandler(handler);
	}

	public HandlerRegistration addMaximizeHandlerNew(MaximizeHandler handler) {
		clearButtons();
		addButtonMore();
		return addMaximizeHandler(handler);
	}
	
	private Button createButtonIcon(String icon, String title){
		Button btn = new Button(AON.MATERIAL.icon(icon, 
				new SafeStylesBuilder()
				.fontSize(24, Unit.PX)
				.cursor(Cursor.POINTER)
				.trustedColor(AON.AON_BLACK).toSafeStyles()
		));
		btn.removeStyleName("gwt-Button");
		btn.getElement().setAttribute("title", title);
		btn.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		btn.getElement().getStyle().setBackgroundColor("transparent");
		btn.getElement().getStyle().setPosition(Position.RELATIVE);
		btn.getElement().getStyle().setZIndex(1);

		return btn;
	}
}
