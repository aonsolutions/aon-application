package com.esferalia.aon.gwt.common.client.i18n;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DialogMessages {

	public static PopupPanel alertErrorWidget(final String msg) {
		return alertWidget(AON.MSG.error(),msg);
	}
	public static PopupPanel alertWarningWidget(final String msg) {
		return alertWidget(AON.MSG.warning(),msg);
	}

	public static PopupPanel alertWidget(final String header, final String content) {
        final CustomDialog box = new CustomDialog() {
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
        box.addStyleName(AON.AON_CSS.aonSelector());
        box.setWidth("300px");
        box.setGlassEnabled(true);
        box.setAnimationEnabled(true);
        box.setModal(true);
        
        box.setCaption(header);
        
        final VerticalPanel panel = new VerticalPanel( );
        panel.addStyleName( AON.AON_CSS.aonWidthAll() );
        Label emptyLabel = new Label();
        emptyLabel.setSize("100%","10px");
        panel.add(emptyLabel);
        Label msg = new Label(content);
        msg.addStyleName(AON.AON_CSS.aonIconError() );
        panel.setCellHorizontalAlignment(msg, HasAlignment.ALIGN_CENTER);
        panel.add(msg);
        final Button acceptClose = new Button(AON.MSG.accept(),new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                box.hide();
            }
        });
        panel.add(emptyLabel);
        panel.add(acceptClose);
        panel.setCellHorizontalAlignment(acceptClose, HasAlignment.ALIGN_CENTER);
        box.setWidget( panel );
        acceptClose.setFocus(true);
        return box;
    }
}
