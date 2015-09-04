package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

public class NewIssuePopupPanel extends CustomDialog {

	private static NewIssuePopupPanelUiBinder uiBinder = GWT
			.create(NewIssuePopupPanelUiBinder.class);

	interface NewIssuePopupPanelUiBinder extends
			UiBinder<Widget, NewIssuePopupPanel> {
	}

	public NewIssuePopupPanel() {
		setCaption("Nueva Incidencia");
		
		setWidget(uiBinder.createAndBindUi(this));
		
		setAnimationEnabled(false);
		setGlassEnabled(true);
	}
	
	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		hide();
	}
	
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		hide();
	}
	
	public void showPopUpPanel() {
		center();
	}
	
	@Override
	public void center() {
		super.center();
	}
	
	@Override
	public void show() {
		super.show();
	}

}
