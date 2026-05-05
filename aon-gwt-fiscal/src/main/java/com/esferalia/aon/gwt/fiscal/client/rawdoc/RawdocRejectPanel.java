package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextArea;

class RawdocRejectPanel extends AonCustomDialog implements Focusable {
	
	interface RawdocRejectPanelCallback {
		void onAccept(String reason, String email);
		default void onCancel() {
			
		}
	}
	final TextArea reason;
	final AonTextBox emailTextBox;
			
	RawdocRejectPanel(RawdocModuleOptions opt, Rawdoc rawdoc, RawdocRejectPanelCallback callback) {
		this.setCaption(AON.MSG.rejectReason());
		FlowPanel reasonPanel = new FlowPanel();
		reasonPanel.setStyleName(AON.CSS.aonTextCenter());
		reasonPanel.addStyleName(AON.CSS.aonPadding());
		
		FlowPanel email = new FlowPanel();
		email.setStyleName(AON.CSS.aonTextCenter());
		email.addStyleName(AON.CSS.aonMarginTop());

		emailTextBox = new AonTextBox();
		emailTextBox.setTitle("Correo electr\u00F3nico para notificar al usuario (opcional)");
		emailTextBox.setName("Correo electr\u00F3nico para notificar al usuario (opcional)");
		
		emailTextBox.setWidth("400px");
		RawdocModule.RAWDOC_SERVICE.getUserEmail(opt.getOccam(), rawdoc.getCreationUser(), new AsyncCallback<String>() {
			
			@Override
			public void onFailure(Throwable caught) {
				emailTextBox.setValue("");
			}
			
			@Override
			public void onSuccess(String result) {
				emailTextBox.setValue(result);
			}
		});
		email.add(emailTextBox);
		
		FlowPanel reasonFP = new FlowPanel();
		reasonFP.setStyleName(AON.CSS.aonTextCenter());
		reasonFP.addStyleName(AON.CSS.aonMarginTop());
		
		reason = new TextArea();
		reason.setWidth("400px");
		reason.setHeight("100px");
		reason.addKeyUpHandler(event1 -> {
			if (event1.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				this.hide();
				callback.onCancel();
			}
		});
		reasonFP.add(reason);
		
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
				callback.onAccept(reason.getValue(), emailTextBox.getValue());
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

		reasonPanel.add(email);
		reasonPanel.add(reasonFP);
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
