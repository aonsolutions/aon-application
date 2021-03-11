package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonAcceptDialog extends AonCustomDialog {

	private static AonAcceptDialogUiBinder uiBinder = GWT.create(AonAcceptDialogUiBinder.class);

	interface AonAcceptDialogUiBinder extends	UiBinder<Widget, AonAcceptDialog> {}
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	private Button okButton;
	private Button cancelButton;

	public static interface AonAcceptDialogCallback {
		void onAccept();
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}
	
	public AonAcceptDialog(String caption, Widget widget, AonAcceptDialogCallback callback) {
		setCaption(caption);
		setWidget(uiBinder.createAndBindUi(this));
		initView(widget);
		getButtonsPanel(callback);
		
		center();
    	show();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute() {
	        	okButton.setFocus(true);        	
	        }
	    });
	}

	private void initView(Widget widget) {
		scrollPanel.setHeight((Window.getClientHeight()/2 - 100) + "px");
		scrollPanel.add(widget);
	}
	
	private void getButtonsPanel(AonAcceptDialogCallback callback) {
		
		okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	
    	okButton.addKeyUpHandler(e -> {
    		if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
				callback.onCancel();	
			}
    	});
    	
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			hide();
			callback.onAccept();
    	});
    	
    	buttonsPanel.add(okButton);
    	
    	cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.setText( AON.MSG.cancelAction());
    	
    	cancelButton.addClickHandler(e -> {
    		cancelButton.setEnabled(false);
			hide();
			callback.onCancel();
    	});
    	
    	cancelButton.addKeyUpHandler(e -> {
    		if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
				callback.onCancel();	
			}
    	});
    	
    	addCloseHandler(e -> {
    		callback.onClose();
    	});
    	
    	buttonsPanel.add(cancelButton);
	}
	
	
	
}
