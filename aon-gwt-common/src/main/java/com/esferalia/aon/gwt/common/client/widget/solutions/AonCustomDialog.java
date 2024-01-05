package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomDialog extends PopupPanel implements AonCustomDialogListener {

	private FocusPanel focusAll;
	private SimpleResizePanel simplePanel;
	private FlowPanel dialogBar;
	private Label captionLabel;
	private AonToolbarButton closeDialog;
	private FlowPanel flowPanel;
	private List<AonCustomDialogListener> listeners;

	public AonCustomDialog() {
		setStyleName(AON.CSS.aonCustomDialog());
		flowPanel = new FlowPanel();

		dialogBar = new FlowPanel();
		dialogBar.setStyleName(AON.CSS.aonCustomDialogHeader());
		captionLabel = new Label();
		captionLabel.setStyleName(AON.CSS.aonCustomDialogTitle());
		dialogBar.add(captionLabel);
		closeDialog = new AonToolbarButton("Cerrar", AON.CSS.aonCancelButton());
		closeDialog.addClickHandler(e -> {hide();});
		closeDialog.setVisible(false);
		closeDialog.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		dialogBar.add(closeDialog);
		
		listeners = new LinkedList<>();
		FocusPanel focusBar = new FocusPanel(dialogBar);
		flowPanel.add(focusBar);

		simplePanel = new SimpleResizePanel();
		flowPanel.add(simplePanel);

		focusAll = new FocusPanel(flowPanel);

		super.setWidget(focusAll);

		new WindowMoveHandler(focusBar);
		new WindowMaximizeHandler(focusBar);

		new WindowResizeHandler();
		setGlassEnabled(true);
		setGlassStyleName(AON.CSS.aonCustomDialogGlass());
		setAnimationEnabled(true);
		setModal(false);
		addCloseHandler(this);
	}

	public void addCloseHandler(AonCustomDialogListener listener) {
		listeners.add(listener);
	}

	public void removeCloseHandler(AonCustomDialogListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void onClose() {
		hide();
	}

	public void handleMaximize() {
		// nothing
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
		return captionLabel.getText();
	}

	public void setCaption(String caption) {
		captionLabel.setText(caption);
	}
	
	public void showCloseButton(boolean visible) {
		closeDialog.setVisible(visible);
	}

	public void handleMove(int absX, int absY) {
		RootPanel.get().setWidgetPosition(this, getAbsoluteLeft() + absX, getAbsoluteTop() + absY);
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

	// ------------------------------------------------------------------------

	interface Template extends SafeHtmlTemplates {

		@SafeHtmlTemplates.Template("<div style=\"padding:10px;\"><div id=\"{0}\" style=\"padding-left: 0.5em;padding-bottom: 10px;\" ></div><div id=\"{1}\" style=\"padding-left: 0.5em;\" ></div><div id=\"{2}\" style=\"padding-top: 10px;text-align: right;\" ></div></div>")
		SafeHtml inputDialog(String messageDivId, String inputDivId, String buttonsDivId);

	}

	private static final Template TEMPLATE = GWT.create(Template.class);

	/**
	 * 
	 * @param message
	 * @param title
	 * @return user's input, or null meaning the user canceled the input
	 */
	public static void showInputDialog(String message, String title, final AsyncCallback<String> cb) {

		String messageDivId = "messagesDiv";
		String inputDivId = "inputDiv";
		String buttonsDivId = "buttonsDiv";

		final AonCustomDialog inputDialog = new AonCustomDialog();
		inputDialog.setCaption(title);
		final HTMLPanel htmlPanel = new HTMLPanel(TEMPLATE.inputDialog(messageDivId, inputDivId, buttonsDivId));
		final Label messageLabel = new Label(message);
		htmlPanel.add(messageLabel, messageDivId);
		final TextBox inputTextBox = new TextBox();
		htmlPanel.add(inputTextBox, inputDivId);
		final Button cancelButton = new Button("Cancelar");
		cancelButton.addClickHandler(event -> inputDialog.hide());
		final Button acceptButton = new Button("Aceptar");
		acceptButton.addClickHandler(event -> {
			inputDialog.hide();
			cb.onSuccess(inputTextBox.getText());
		});

		htmlPanel.add(cancelButton, buttonsDivId);
		htmlPanel.add(acceptButton, buttonsDivId);

		inputDialog.setWidget(htmlPanel);

		inputDialog.center();

	}

	/**
	 * 
	 * @param message
	 * @param title
	 * @return user's input, or null meaning the user canceled the input
	 */
	public static void showInputDialog(String message, String title, String[] values, String[] texts, String initialValue, final AsyncCallback<String> cb) {
		String messageDivId = "messagesDiv";
		String inputDivId = "inputDiv";
		String buttonsDivId = "buttonsDiv";

		final AonCustomDialog inputDialog = new AonCustomDialog();

		inputDialog.setCaption(title);

		final HTMLPanel htmlPanel = new HTMLPanel(TEMPLATE.inputDialog(messageDivId, inputDivId, buttonsDivId));

		final Label messageLabel = new Label(message);
		htmlPanel.add(messageLabel, messageDivId);

		int initialSelected = 0;

		final ListBox listBox = new ListBox();
		for (int i = 0; i < texts.length; i++) {
			listBox.addItem(texts[i], values[i]);
			if (AonStringUtils.equals(initialValue, values[i]))
				initialSelected = i;
		}

		listBox.setSelectedIndex(initialSelected);

		htmlPanel.add(listBox, inputDivId);

		final Button cancelButton = new Button("Cancelar");
		cancelButton.addClickHandler(event -> inputDialog.hide());
		final Button acceptButton = new Button("Aceptar");
		acceptButton.addClickHandler(event -> {
			inputDialog.hide();
			cb.onSuccess(listBox.getSelectedValue());
		});

		htmlPanel.add(cancelButton, buttonsDivId);
		htmlPanel.add(acceptButton, buttonsDivId);

		inputDialog.setWidget(htmlPanel);

		inputDialog.center();

	}

	private static class SimpleResizePanel extends SimplePanel implements RequiresResize, ProvidesResize {

		public SimpleResizePanel() {
			Element element = getElement();
			element.getStyle().setPosition(Position.RELATIVE);
		}

		@Override
		public void onResize() {
			Widget child = super.getWidget();
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}

	}

	/**
	 * Handles the logic to track click-drag movements with the mouse.
	 */
	private abstract class MouseDragHandler implements MouseMoveHandler, MouseUpHandler, MouseDownHandler {
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
			event.preventDefault();
			// DOM.eventPreventDefault(DOM.eventGetCurrentEvent());
		}

		public void onMouseMove(MouseMoveEvent event) {
			if (dragging) {
				handleDrag(event.getClientX() - dragStartX, event.getClientY() - dragStartY);
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
			AonCustomDialog.this.handleMove(absX, absY);
		}
	}

	private class NWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.handleMove(0, absY);
			AonCustomDialog.this.incrementPixelSize(0, (-1) * absY);
		}
	}

	private class NWWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.handleMove(absX, absY);
			AonCustomDialog.this.incrementPixelSize((-1) * absX, (-1) * absY);
		}
	}

	private class NEWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.handleMove(0, absY);
			AonCustomDialog.this.incrementPixelSize(absX, (-1) * absY);
		}
	}

	private class SWWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.handleMove(absX, 0);
			AonCustomDialog.this.incrementPixelSize((-1) * absX, absY);
		}
	}

	private class SWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.incrementPixelSize(0, absY);
		}
	}

	private class SEWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.incrementPixelSize(absX, absY);
		}
	}

	private class EWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.incrementPixelSize(absX, 0);
		}
	}

	private class WWindowResizeHandler extends MouseDragHandler {

		@Override
		public void handleDrag(int absX, int absY) {
			AonCustomDialog.this.handleMove(absX, 0);
			AonCustomDialog.this.incrementPixelSize((-1) * absX, 0);
		}
	}

	private class WindowResizeHandler {

		public WindowResizeHandler() {
			init(0, -2, 10, 3, Cursor.NW_RESIZE, new NWWindowResizeHandler());
			init(10, -2, 80, 3, Cursor.N_RESIZE, new NWindowResizeHandler());
			init(90, -2, 10, 3, Cursor.NE_RESIZE, new NEWindowResizeHandler());
			init(0, 99, 10, 3, Cursor.SW_RESIZE, new SWWindowResizeHandler());
			init(10, 99, 80, 3, Cursor.S_RESIZE, new SWindowResizeHandler());
			init(90, 99, 10, 3, Cursor.SE_RESIZE, new SEWindowResizeHandler());
			init(-2, 0, 3, 10, Cursor.NW_RESIZE, new NWWindowResizeHandler());
			init(-2, 10, 3, 80, Cursor.W_RESIZE, new WWindowResizeHandler());
			init(-2, 90, 3, 10, Cursor.SW_RESIZE, new SWWindowResizeHandler());
			init(99, 0, 3, 10, Cursor.NE_RESIZE, new NEWindowResizeHandler());
			init(99, 10, 3, 80, Cursor.E_RESIZE, new EWindowResizeHandler());
			init(99, 90, 3, 10, Cursor.SE_RESIZE, new SEWindowResizeHandler());
		}

		private void init(int left, int top, int width, int height, Cursor cursor, MouseDragHandler dragHandler) {

			if (dragHandler == null) {
				return;
			}

			Element dialogElement = AonCustomDialog.this.getElement();

			Element div = DOM.createDiv();
			div.getStyle().setPosition(Position.ABSOLUTE);
			div.getStyle().setTop(top, Unit.PCT);
			div.getStyle().setLeft(left, Unit.PCT);
			div.getStyle().setWidth(width, Unit.PCT);
			div.getStyle().setHeight(height, Unit.PCT);
			div.getStyle().setCursor(cursor);

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

	public void showLoaded() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	public static interface AonCustomDialogCallback {
		void onEnd();
	}
	
	public void showLoadedCB(AonCustomDialogCallback cb) {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
			cb.onEnd();
		});
	}
}
