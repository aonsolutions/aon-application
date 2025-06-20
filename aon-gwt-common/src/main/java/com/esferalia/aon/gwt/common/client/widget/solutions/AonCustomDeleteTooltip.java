package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DeleteEvent;
import com.esferalia.aon.gwt.common.shared.DeleteEventHandler;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonCustomDeleteTooltip extends PopupPanel {
	
	private final HandlerManager handlerManager = new HandlerManager(this);

	private final HandlerRegistration[] closeHandler = new HandlerRegistration[1];
	
	private FocusPanel focusablePanel = new FocusPanel();
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel toolbar = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel message = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel buttons = new HTMLPanel(AonStringUtils.EMPTY);
	
	public AonCustomDeleteTooltip(String title, String text, Widget widget) {
		super(true);
		setGlassEnabled(false);
		setAnimationEnabled(false);
		setPreviewingAllNativeEvents(true);
		setAutoHideOnHistoryEventsEnabled(false);
		
		closeHandler[0] =Event.addNativePreviewHandler(event -> {
			if (event.getTypeInt() == Event.ONKEYDOWN) {
            	int keyCode = event.getNativeEvent().getKeyCode();
                if (keyCode == KeyCodes.KEY_ESCAPE || keyCode == KeyCodes.KEY_ENTER) {
                	hideDialog(hide -> {});
                    closeHandler[0].removeHandler();
                    event.cancel();
                    event.getNativeEvent().stopPropagation();
                }
            }
		});
		
		addStyleName(AON.CSS.aonSlideDownAppear());
		getElement().getStyle().setProperty("border-radius", "10px");
		getElement().getStyle().setProperty("border", "none");
		
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.getElement().getStyle().setProperty("padding", ".3rem");
		content.getElement().getStyle().setProperty("min-width", "17.5rem");
		
		toolbar.addStyleName(AON.CSS.aonFlexBetween());
		toolbar.getElement().getStyle().setProperty("align-items", "start");
		
		buttons.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttons.getElement().getStyle().setProperty("margin-top", ".5rem");
		
		Label titleLabel = new Label(title);
		titleLabel.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		titleLabel.getElement().getStyle().setProperty("font-weight", "bold");
		titleLabel.getElement().getStyle().setProperty("font-size", "14px");
		
		AonTableButton closeButton = new AonTableButton(AON.MSG.close(), AON.CSS.aonIconClose());
		closeButton.getElement().getStyle().setProperty("width", "10px");
		closeButton.getElement().getStyle().setProperty("height", "10px");
		closeButton.addClickHandler(e -> hideDialog(hide -> {}));
		
		toolbar.add(titleLabel);
		toolbar.add(closeButton);
		
		Label textLabel = new Label(text);
		textLabel.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		textLabel.getElement().getStyle().setProperty("font-size", "12px");
		message.add(textLabel);
		
		Button cancelButton = createButton("Cancelar");
		cancelButton.addClickHandler(e -> hideDialog(hide -> {}));
		Button deleteButton = createButton("Eliminar");
		deleteButton.getElement().getStyle().setProperty("color", "#d60606");
		deleteButton.addClickHandler(e -> {
			hideDialog(hide -> fireEvent(new DeleteEvent()));
		});
		buttons.add(cancelButton);
		buttons.add(deleteButton);
		
		content.add(toolbar);
		content.add(message);
		content.add(buttons);
		
		focusablePanel.add(content);
		
		setWidget(focusablePanel);
		
		int left = widget.getAbsoluteLeft();
	    int top = widget.getAbsoluteTop() + widget.getOffsetHeight();
	    setPopupPosition(left, top);
	    show();
		
		Scheduler.get().scheduleDeferred(() -> {
			focusablePanel.setFocus(true);
			addStyleName(AON.CSS.aonSlideDownAppearActive());
		});
		
	}
	
	@Override
	public void show() {
		super.show();
		
		Scheduler.get().scheduleDeferred(() -> {
            setClipAutoImportant(getElement());
        });
	}
	
	private void hideDialog(Consumer<Void> hide) {
		removeStyleName(AON.CSS.aonSlideDownAppearActive());
		addStyleName(AON.CSS.aonSlideDownDisappear());
		
		Scheduler.get().scheduleFixedDelay(() -> {
			hide();
	        removeStyleName(AON.CSS.aonSlideDownAppearActive());
	        removeStyleName(AON.CSS.aonSlideDownDisappear());
	        hide.accept(null);
	        return false;
	    }, 400); 
	}
	
	private Button createButton(String text) {
		Button button = new Button(text);
		button.getElement().getStyle().setProperty("background", "none");
		button.getElement().getStyle().setProperty("background-color", "#fafafa");
		button.getElement().getStyle().setProperty("padding", "5px");
		button.getElement().getStyle().setProperty("height", "auto");
		button.getElement().getStyle().setProperty("font-size", "11px");
		button.getElement().getStyle().setProperty("font-family", "Arial Unicode MS, Arial, sans-serif");
		button.getElement().getStyle().setProperty("text-transform", "inherit");
		button.getElement().getStyle().setProperty("font-weight", "bold");
		button.getElement().getStyle().setProperty("border", "1px solid #d0d0d0");
		button.getElement().getStyle().setProperty("border-radius", "5px");
		
		return button;
	}

	public void addDeleteHandler(DeleteEventHandler handler) {
        handlerManager.addHandler(DeleteEvent.TYPE, handler);
    }

    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }
    
    private native void setClipAutoImportant(Element element) /*-{
        element.style.setProperty("clip", "auto", "important");
    }-*/;
	
}

