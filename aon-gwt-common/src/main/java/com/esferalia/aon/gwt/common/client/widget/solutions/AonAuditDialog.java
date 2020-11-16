package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class AonAuditDialog extends AonCustomDialog {

	public void show(HasAudit auditable) {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.audit());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		Label created = new Label();
		created.addStyleName(AON.CSS.aonMargin());
		created.setText(AonStringUtils.isEmpty(auditable.getCreationUser()) ? AON.MSG
				.emptyCreatedBy() : AON.MSG.createdBy(
				auditable.getCreationUser(), auditable.getCreationDate()));
		panel.add(created);
		Label modified = new Label();
		modified.setText(AonStringUtils.isEmpty(auditable.getModificationUser()) ? AON.MSG
				.emptyModifiedBy() : AON.MSG.modifiedBy(
				auditable.getModificationUser(),
				auditable.getModificationDate()));
		modified.addStyleName(AON.CSS.aonMargin());
		
    	FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
				}
			}
		});
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				hide();
			}
		});
    	buttons.add(okButton);
    	
    	addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				hide();
			}
		});

    	panel.add(modified);
    	panel.add(buttons);
		add(panel);
		center();
		show();
	}

}
