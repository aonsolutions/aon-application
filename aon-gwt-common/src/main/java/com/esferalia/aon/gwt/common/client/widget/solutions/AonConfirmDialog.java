package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class AonConfirmDialog extends AonCustomDialog {
    
	public static interface AonConfirmDialogCallback {
		void onAccept();
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}

	private SimpleLayoutPanel root;
	
	public AonConfirmDialog() {
		addStyleName(AON.CSS.aonConfirmDialog());
		root = new SimpleLayoutPanel();
		root.setWidth("500px");
		root.setHeight("120px");
		root.setStyleName(AON.CSS.aonPadding());
		this.setWidget(root);
	}
    
	public void confirm(String msg, final AonConfirmDialogCallback callback) {
    	confirm("Pregunta", msg, callback);
    }
    
	public void confirm(String header,String msg, final AonConfirmDialogCallback callback) {
    	setCaption(header);
    	FlowPanel panel = new FlowPanel();
    	panel.setStyleName(AON.CSS.aonCustomConfirmDialog());
    	Label label = new Label(msg);
    	label.setStyleName(AON.CSS.aonConfirmDialogMsg());
    	panel.add(label);
    	FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	buttons.addStyleName(AON.CSS.aonMarginBottom());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
					callback.onCancel();	
				}
			}
		});
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				hide();
				callback.onAccept();
			}
		});
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				hide();
				callback.onCancel();
			}
		});
    	cancelButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
					callback.onCancel();	
				}
			}
		});
    	addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				callback.onClose();
			}
		});
    	buttons.add(cancelButton);
    	panel.add(buttons);
    	root.setWidget(panel);
    	
    	center();
    	show();
    	
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute() {
	        	okButton.setFocus(true);        	
	        }
	    });
    }
	
	public void info(String msg) {
    	info("Aviso", msg);
    }
    
	public void info(String header,String msg) {
    	setCaption(header);
    	FlowPanel panel = new FlowPanel();
    	panel.setStyleName(AON.CSS.aonCustomConfirmDialog());
    	Label label = new Label(msg);
    	label.setStyleName(AON.CSS.aonConfirmDialogMsg());
    	panel.add(label);
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
    	
    	panel.add(buttons);
    	root.setWidget(panel);
    	
    	center();
    	show();
    	
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute() {
	        	okButton.setFocus(true);        	
	        }
	    });
    }
	
}
