package com.esferalia.aon.gwt.fiscal.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DialogMessages {

	public static final FiscalMessages FISCAL_MESSAGES = (FiscalMessages) GWT
			.create(FiscalMessages.class);

	public static DialogBox alertErrorWidget(final String msg) {
		return alertWidget(FISCAL_MESSAGES.error(),msg);
	}
	public static DialogBox alertWarningWidget(final String msg) {
		return alertWidget(FISCAL_MESSAGES.warning(),msg);
	}

	public static DialogBox alertWidget(final String header, final String content) {
        final DialogBox box = new DialogBox() {
        	@Override
            protected void onPreviewNativeEvent(NativePreviewEvent event) {
                super.onPreviewNativeEvent(event);
                switch (event.getTypeInt()) {
                    case Event.ONKEYDOWN:
                        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
                            hide();
                        }
                        break;
                }
            }        	
        };
        final VerticalPanel panel = new VerticalPanel( );
        final Label emptyLabel = new Label("");
        panel.setWidth("300px");
        
        box.setText( header );
        box.setGlassEnabled(true);
        box.setAnimationEnabled(true);
        box.setModal(true);
        
        Label msg = new Label(content);
        panel.add(emptyLabel);
        panel.setCellHorizontalAlignment(emptyLabel, HasAlignment.ALIGN_LEFT);
        
        panel.add(msg);
        final Button acceptClose = new Button(FISCAL_MESSAGES.accept(),new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
            }
        });
        emptyLabel.setSize("100%","25px");
        
        panel.add(emptyLabel);
        panel.add(emptyLabel);
        panel.add(acceptClose);
        panel.setCellHorizontalAlignment(acceptClose, HasAlignment.ALIGN_RIGHT);
        box.add(panel);
        acceptClose.setFocus(true);
        return box;
    }
}
