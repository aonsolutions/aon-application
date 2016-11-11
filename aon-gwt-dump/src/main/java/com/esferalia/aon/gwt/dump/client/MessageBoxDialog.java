package com.esferalia.aon.gwt.dump.client;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageBoxDialog extends CustomDialog {

	interface Binder extends UiBinder<Widget, MessageBoxDialog> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Button acceptInfoButton;
	
	@UiField
	Button accepTrashButton;

	@UiField
	Label informationText;
	
	@UiField
	Label trashText;
	
	@UiField
	HTMLPanel information;

	@UiField
	DeckPanel deckPanel;

	public MessageBoxDialog() {
		
		setWidget(binder.createAndBindUi(this));
		
	}
	
	public void createInfoDialog(String text){
		this.deckPanel.showWidget(0);
		informationText.setText(text);
	}
	
	public void createTrashDialog(String text){
		this.deckPanel.showWidget(1);
		trashText.setText(text);
	}
	
	@UiHandler("acceptInfoButton")
	void onClickInfoButton(ClickEvent event) {
		hide();
	}
	
	@UiHandler("accepTrashButton")
	void onClickTrashButton(ClickEvent event) {
		hide();
	}
	 
	
	public static MessageBoxDialog showInfoDialog(String msg) {
		final MessageBoxDialog messageBoxDialog = new MessageBoxDialog();
		messageBoxDialog.createInfoDialog(msg);
		messageBoxDialog.setModal(true);
		messageBoxDialog.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// TODO Auto-generated method stub
				messageBoxDialog.setPopupPosition(
						(Window.getClientWidth() - offsetWidth )/2, 
						(Window.getClientHeight() - offsetHeight )/2);
				
			}
		});
		return messageBoxDialog;
	}
}
