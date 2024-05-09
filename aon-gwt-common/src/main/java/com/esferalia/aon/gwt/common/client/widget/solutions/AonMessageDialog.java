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

public class AonMessageDialog extends AonCustomDialog {
    
	public static interface AonMessageDialogCallback {
		void onAccept();
		default void onClose() {
			this.onAccept();
		}
	}

	private SimpleLayoutPanel root;
	
	public AonMessageDialog() {
		addStyleName(AON.CSS.aonConfirmDialog());
		root = new SimpleLayoutPanel();
		root.setWidth("500px");
		root.setHeight("150px");
		root.setStyleName(AON.CSS.aonPadding());
		this.setWidget(root);
	}
    public void show(String msg, final AonMessageDialogCallback callback) {
    	show("", msg, callback);
    }
    
	public void show(String header,String msg, final AonMessageDialogCallback callback) {
    	setCaption(header);
    	FlowPanel panel = new FlowPanel();
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
					if (callback != null) callback.onAccept();;	
				}
			}
		});
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				hide();
				if (callback != null) callback.onAccept();
			}
		});
    	buttons.add(okButton);
    	
    	addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				if (callback != null) callback.onClose();
			}
		});
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

	public static void show(String header,String msg) {
		_show(header,msg,null);
	}
	public static void error(String msg) {
		_show(AON.MSG.error(),msg,null);
	}
	public static void error(String msg,final AonMessageDialogCallback callback) {
		_show(AON.MSG.error(),msg,callback);
	}
	public static void warning (final String msg) {
		_show(AON.MSG.warning(),msg,null);
	}
	
	public static void _show(String header,String msg, final AonMessageDialogCallback callback) {
		AonMessageDialog dialog = new AonMessageDialog();
		dialog.show(header, msg, callback);
    }
	
}
