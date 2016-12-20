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
	Button acceptTrashButton;
	
	@UiField
	Button acceptFailButton;
	
	@UiField
	Button acceptWarningButton;

	@UiField
	Label informationText;
	
	@UiField
	Label trashText;
	
	@UiField
	Label failText;
	
	@UiField
	Label warningText;
	
	@UiField
	HTMLPanel information;
	
	@UiField
	HTMLPanel delete;
	
	@UiField
	HTMLPanel cancel;
	
	@UiField
	HTMLPanel warn;

	@UiField
	DeckPanel deckPanel;

	public MessageBoxDialog() {
		
		setWidget(binder.createAndBindUi(this));
		
	}
	
	public void createInfoDialog(String text){
		this.deckPanel.showWidget(0);
		information.setTitle("Informacion");
		informationText.setText(text);
	}
	
	public void createTrashDialog(String text){
		this.deckPanel.showWidget(1);
		delete.setTitle("Aviso");
		trashText.setText(text);
	}
	
	public void createFailDialog(String text){
		this.deckPanel.showWidget(2);
		cancel.setTitle("Aviso");
		failText.setText(text);
	}
	
	public void createWarningDialog(String text){
		this.deckPanel.showWidget(3);
		warn.setTitle("Error");
		warningText.setText(text);
	}
	
	@UiHandler("acceptInfoButton")
	void onClickInfoButton(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptTrashButton")
	void onClickTrashButton(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptFailButton")
	void onClickFailButton(ClickEvent event) {
		hide();
	}
	
	@UiHandler("acceptWarningButton")
	void onClickWarningButton(ClickEvent event) {
		hide();
	}
	 
	
	public static MessageBoxDialog showInfoDialog(String msg) {
		final MessageBoxDialog messageBoxDialog = new MessageBoxDialog();
		messageBoxDialog.createInfoDialog(msg);
		messageBoxDialog.setModal(true);
		setPositionCenter(messageBoxDialog);
		
		return messageBoxDialog;
	}
	
	public static MessageBoxDialog showTrashDialog(String msg) {
		final MessageBoxDialog messageBoxDialog = new MessageBoxDialog();
		messageBoxDialog.createTrashDialog(msg);
		messageBoxDialog.setModal(true);
		setPositionCenter(messageBoxDialog);
		
		return messageBoxDialog;
	}
	
	public static MessageBoxDialog showFailDialog(String msg) {
		final MessageBoxDialog messageBoxDialog = new MessageBoxDialog();
		messageBoxDialog.createFailDialog(msg);
		messageBoxDialog.setModal(true);
		setPositionCenter(messageBoxDialog);
		
		return messageBoxDialog;
	}
	
	public static MessageBoxDialog showWarningDialog(String msg) {
		final MessageBoxDialog messageBoxDialog = new MessageBoxDialog();
		messageBoxDialog.createWarningDialog(msg);
		messageBoxDialog.setModal(true);
		setPositionCenter(messageBoxDialog);
		
		return messageBoxDialog;
	}

	private static void setPositionCenter( final MessageBoxDialog messageBoxDialog) {
		messageBoxDialog.setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				// TODO Auto-generated method stub
				messageBoxDialog.setPopupPosition(
						(Window.getClientWidth() - offsetWidth )/2, 
						(Window.getClientHeight() - offsetHeight )/2);
				
			}
		});	
	}
}
