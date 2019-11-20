package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class SyncDialog extends CustomDialogB {
	
	interface Binder extends UiBinder<Widget, SyncDialog>{}
	private static final Binder binder = GWT.create(Binder.class);

	@UiField Button accept_button;
	@UiField CheckBox check;
	
	public SyncDialog() {
		setWidget(binder.createAndBindUi(this));
		
		accept_button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		
		
	}
	
	protected abstract void onAccept();

}