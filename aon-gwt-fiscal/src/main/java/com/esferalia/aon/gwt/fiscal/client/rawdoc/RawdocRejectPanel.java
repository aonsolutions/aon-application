package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextArea;

class RawdocRejectPanel extends AonCustomDialog implements Focusable {
	
	interface RawdocRejectPanelCallback {
		void onAccept(String reason);
		default void onCancel() {
			
		}
	}
	final TextArea reason;
			
	RawdocRejectPanel(RawdocModuleOptions opt, Rawdoc rawdoc, RawdocRejectPanelCallback callback) {
		this.setCaption(AON.MSG.rejectReason());
		FlowPanel reasonPanel = new FlowPanel();
		reasonPanel.setStyleName(AON.CSS.aonTextCenter());
		reasonPanel.addStyleName(AON.CSS.aonPadding());
		
		reason = new TextArea();
		reason.setWidth("400px");
		reason.setHeight("100px");
		reason.addKeyUpHandler(event1 -> {
			if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				this.hide();
				callback.onCancel();
			}
		});
	
		FlowPanel buttons = new FlowPanel();
		buttons.setStyleName(AON.CSS.aonTextCenter());
		buttons.addStyleName(AON.CSS.aonMarginTop());
	
		final Button okButton = new Button();
		okButton.setStyleName(AON.CSS.aonOkButton());
		okButton.setText( AON.MSG.accept());
		okButton.addKeyUpHandler(event1 -> {
			if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				this.hide();
				callback.onCancel();
			}
		});
		
		okButton.addClickHandler(event1 -> {
			if (AonStringUtils.isBlank( reason.getValue() )) {
				AonMessageDialog msg = new AonMessageDialog();
				msg.show("ERROR", "Debe indicar una raz\u00F3n para proceder a rechazar el documento.", () -> {});
			} else {
				okButton.setEnabled(false);
				this.hide();
				callback.onAccept(reason.getValue());
			}
		});
		buttons.add(okButton);
	
		final Button cancelButton = new Button();
		cancelButton.setStyleName(AON.CSS.aonCancelButton());
		cancelButton.addStyleName(AON.CSS.aonMarginLeft());
		cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event1 -> {
			cancelButton.setEnabled(false);
			this.hide();
			callback.onCancel();
		});
		cancelButton.addKeyUpHandler(event1 -> {
			if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				this.hide();
				callback.onCancel();
			}
		});
		buttons.add(cancelButton);
	
		reasonPanel.add(reason);
		reasonPanel.add(buttons);
		this.add(reasonPanel);
	}

	@Override
	public void setFocus(boolean focused) {
		reason.setFocus( focused );
	}
	
	@Override public int getTabIndex() {return 0;}
	@Override public void setAccessKey(char arg0) {/*Nothing*/}
	@Override public void setTabIndex(int arg0) {/*Nothing*/}
}
