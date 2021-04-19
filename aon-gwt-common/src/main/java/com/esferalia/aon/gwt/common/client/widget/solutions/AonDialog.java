package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonDialog extends AonCustomDialog {

	private static AonDialogUiBinder uiBinder = GWT.create(AonDialogUiBinder.class);

	interface AonDialogUiBinder extends	UiBinder<Widget, AonDialog> {}
	
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
	
	public AonDialog(String caption, Widget widget) {
		setCaption(caption);
		setWidget(uiBinder.createAndBindUi(this));
		scrollPanel.add(widget);
		
	}
	
	public void info() {
		getBtnPanel();
		showDialog();
	}
	
	public void warning() {
		getBtnPanel();
		showDialog();
	}
	
	public void confirm(AonAcceptDialogCallback callback) {
		getAcceptCancelBtnPanel(callback);
		showDialog();
	}
	
	private void showDialog() {
		center();
    	show();
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute() {
	        	okButton.setFocus(true);        	
	        }
	    });
	}
	
	private void getBtnPanel() {
		okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.cancelAction() );
    	
    	okButton.addKeyUpHandler(e -> {
    		if (e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
				hide();
			}
    	});
    	
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
			hide();
    	});
    	
    	buttonsPanel.add(okButton);
	}

	private void getAcceptCancelBtnPanel(AonAcceptDialogCallback callback) {
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
