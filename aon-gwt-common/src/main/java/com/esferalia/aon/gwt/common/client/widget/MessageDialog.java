package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class MessageDialog extends CustomDialog {
    
	public static interface MessageDialogCallback {
		void onClose();
	}

	
	private static MessageDialog INSTANCE;
	 
	private SimpleLayoutPanel root;
	private MessageDialog() {
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setStyleName(AON.AON_CSS.aonConfirmDialog());
		root = new SimpleLayoutPanel();
		root.setWidth("500px");
		root.setHeight("90px");
		root.setStyleName(AON.AON_CSS.aonPadding());
		this.setWidget(root);
	}
	private SimpleLayoutPanel getRoot() {
		return root;
	}
	private static MessageDialog getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MessageDialog();
		}
		return INSTANCE;
	}
	
	public static void error(String msg) {
		show(AON.MSG.error(),msg);
	}
	public static void warning (final String msg) {
		show(AON.MSG.warning(),msg);
	}
	
    public static void show(String msg) {
    	show(msg, (String) null);
    }
	
    public static void show(String header,String msg) {
    	show(header,msg, null);
    }

    public static void show(String msg, final MessageDialogCallback callback) {
    	show(null, msg, callback);
    }
    
	public static void show(String header,String msg, final MessageDialogCallback callback) {
		final MessageDialog md = getInstance();
		md.setCaption( AonStringUtils.defaultIfBlank(header, "Mensaje"));
    	FlowPanel panel = new FlowPanel();
    	Label label = new Label(msg);
    	label.setStyleName(AON.AON_CSS.aonConfirmDialogMsg());
    	panel.add(label);
    	FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.AON_CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.AON_CSS.aonConfirmDialogOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					md.hide();
					if (callback != null) callback.onClose();	
				}
			}
		});
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				md.hide();
				if (callback != null) callback.onClose();
			}
		});
    	buttons.add(okButton);
    	
    	md.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (callback != null) callback.onClose();
			}
		});
    	panel.add(buttons);
    	md.getRoot().setWidget(panel);
    	
    	md.center();
    	md.show();
    	
	    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute() {
	        	okButton.setFocus(true);        	
	        }
	    });
    }
	
}
