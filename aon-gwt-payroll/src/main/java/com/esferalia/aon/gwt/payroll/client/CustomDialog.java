package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomDialog extends PopupPanel implements
		CustomDialogBar.Listener {

	private static final String DEFAULT_STYLENAME = "aon-CustomDialog";
	
	private static class SimpleResizePanel extends SimplePanel implements RequiresResize , ProvidesResize{
		
		public SimpleResizePanel() {
			Element element = getElement();
			element.getStyle().setPosition(Position.RELATIVE);
		}
		
		@Override
		public void onResize() {
		    Widget child = getWidget();
		    if ((child != null) && (child instanceof RequiresResize)) {
		      ((RequiresResize) child).onResize();
		    }
		}
		
	}
	
	/**
	 * Handles the logic to track click-drag movements with the mouse.
	 */
	private abstract class MouseDragHandler implements MouseMoveHandler,
			MouseUpHandler, MouseDownHandler {
		protected int dragStartX;
		protected int dragStartY;
		protected boolean dragging = false;

		public MouseDragHandler(HasAllMouseHandlers... dragHandles) {
			setWidgets(dragHandles);
		}

		void setWidgets(HasAllMouseHandlers... dragHandles) {
			for (HasAllMouseHandlers hamh : dragHandles) {
				hamh.addMouseDownHandler(this);
				hamh.addMouseUpHandler(this);
				hamh.addMouseMoveHandler(this);

			}
		}

		public abstract void handleDrag(int absX, int absY);

		public void onMouseDown(MouseDownEvent event) {
			dragging = true;
			Widget widget = (Widget) event.getSource();
			DOM.setCapture(widget.getElement());
			dragStartX = event.getClientX();
			dragStartY = event.getClientY();
			DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
		}

		public void onMouseMove(MouseMoveEvent event) {
			if (dragging) {
				handleDrag(event.getClientX() - dragStartX, event.getClientY()
						- dragStartY);
				dragStartX = event.getClientX();
				dragStartY = event.getClientY();
			}
		}

		public void onMouseUp(MouseUpEvent event) {
			dragging = false;
			Widget widget = (Widget) event.getSource();
			DOM.releaseCapture(widget.getElement());
		}
	}

	private class WindowMoveHandler extends MouseDragHandler {
		public WindowMoveHandler(HasAllMouseHandlers dragHandle) {
			super(dragHandle);
		}

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(absX, absY);
		}
	}

	private class NWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(0, absY);
			CustomDialog.this.incrementPixelSize(0, (-1)*absY);
		}
	}

	private class NWWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(absX, absY);
			CustomDialog.this.incrementPixelSize((-1)*absX, (-1)*absY);
		}
	}

	private class NEWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(0, absY);
			CustomDialog.this.incrementPixelSize(absX, (-1)*absY);
		}
	}

	private class SWWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(absX, 0);
			CustomDialog.this.incrementPixelSize((-1)*absX, absY);
		}
	}

	private class SWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.incrementPixelSize(0, absY);
		}
	}

	private class SEWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.incrementPixelSize(absX, absY);
		}
	}

	private class EWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.incrementPixelSize(absX, 0);
		}
	}

	private class WWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			CustomDialog.this.handleMove(absX, 0);
			CustomDialog.this.incrementPixelSize((-1)*absX, 0);
		}
	}
	

	private class WindowResizeHandler {

		public WindowResizeHandler() {
			init(0, -2, 10, 3, "nw-resize", new NWWindowResizeHandler());
			init(10, -2, 80, 3, "n-resize", new NWindowResizeHandler());
			init(90, -2, 10, 3, "ne-resize", new NEWindowResizeHandler());
			init(0, 99, 10, 3, "sw-resize" , new SWWindowResizeHandler());
			init(10, 99, 80, 3, "s-resize", new SWindowResizeHandler() );
			init(90, 99, 10, 3, "se-resize", new SEWindowResizeHandler());
			init(-2, 0, 3, 10, "nw-resize", new NWWindowResizeHandler());
			init(-2, 10, 3, 80, "w-resize", new WWindowResizeHandler());
			init(-2, 90, 3, 10, "sw-resize", new SWWindowResizeHandler());
			init(99, 0, 3, 10, "ne-resize", new NEWindowResizeHandler());
			init(99, 10, 3, 80, "e-resize", new EWindowResizeHandler());
			init(99, 90, 3, 10, "se-resize", new SEWindowResizeHandler());
		}

		private void init(int left, int top, int width, int height,
				String cursor, MouseDragHandler dragHandler) {
			init(left + "%", top + "%", width + "%", height + "%", cursor,
					dragHandler);
		}

		private void init(String left, String top, String width, String height,
				String cursor, MouseDragHandler dragHandler) {
			
			if ( dragHandler == null ) {
				return;
			}
			
			Element dialogElement = CustomDialog.this.getElement();

			Element div = DOM.createDiv();
			DOM.setStyleAttribute(div, "position", "absolute");
			DOM.setStyleAttribute(div, "top", top);
			DOM.setStyleAttribute(div, "left", left);
			DOM.setStyleAttribute(div, "width", width);
			DOM.setStyleAttribute(div, "height", height);
			DOM.setStyleAttribute(div, "cursor", cursor);

			dialogElement.appendChild(div);
			dragHandler.setWidgets(HTML.wrap(div));
		}

	}

	private class WindowMaximizeHandler implements DoubleClickHandler {

		public WindowMaximizeHandler(Widget maximizeHandle) {
			maximizeHandle.addDomHandler(this, DoubleClickEvent.getType());
		}

		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			handleMaximize();
		}
	}

	FocusPanel focusBar;
	FocusPanel focusAll;
	FocusPanel focusResize;
	SimpleResizePanel simplePanel;
	CustomDialogBar dialogBar;
	FlowPanel flowPanel;

	public CustomDialog() {
		initWidget();
		setGlassEnabled(true);
		setAnimationEnabled(false);

	}

	private void initWidget() {

		flowPanel = new FlowPanel();

		dialogBar = new CustomDialogBar();
		focusBar = new FocusPanel(dialogBar);
		flowPanel.add(focusBar);

		simplePanel = new SimpleResizePanel();
		flowPanel.add(simplePanel);

		focusAll = new FocusPanel(flowPanel);

		super.setWidget(focusAll);

		dialogBar.addCloseHandler(this);
		new WindowMoveHandler(focusBar);
		new WindowMaximizeHandler(focusBar);

		new WindowResizeHandler();

	}

	@Override
	public void onClose() {
		hide();
	}

	public void handleMaximize() {
	}

	@Override
	public void setWidget(Widget w) {
		simplePanel.setWidget(w);
	}
	
	@Override
	public Widget getWidget() {
		return simplePanel.getWidget();
	}
	
	public String getCaption() {
		return dialogBar.getCaption();
	}

	public void setCaption(String caption) {
		dialogBar.setCaption(caption);
	}

	public void handleMove(int absX, int absY) {
		RootPanel.get().setWidgetPosition(this, getAbsoluteLeft() + absX,
				getAbsoluteTop() + absY);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focusAll.addClickHandler(handler);
	}

	public void incrementPixelSize(int width, int height) {
		int offsetWidth = simplePanel.getOffsetWidth();
		int offsetHeight = simplePanel.getOffsetHeight();

		simplePanel.setPixelSize(offsetWidth + width, offsetHeight + height);
		
		simplePanel.onResize();
	}

	@Override
	public void setPixelSize(int width, int height) {
		int offsetWidth = getOffsetWidth();
		int offsetHeight = getOffsetHeight();
		incrementPixelSize(width - offsetWidth, height - offsetHeight);
	}
	

}
