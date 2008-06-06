package com.code.aon.desktop.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.xml.soap.SOAPException;

import com.code.aon.groupware.Contact;
import com.code.aon.messaging.event.ISenderListener;
import com.code.aon.messaging.event.SenderEvent;
import com.code.aon.messaging.sms.Message;
import com.code.aon.messaging.sms.Sender;
import com.code.aon.messaging.util.Utils;
import com.code.aon.ui.util.AonUtil;

public class SMSController implements ISenderListener, Serializable {

	private static final long serialVersionUID = -5534264216750579958L;
	private static final Logger LOGGER = Logger.getLogger( SMSController.class.getName() );
	private static final String SMS_CONTACT_MANAGED_BEAN = "smsContact";

	Sender sender;

	private List<String> recipients;
	private String recipient;
	private String message;
	private Message msg;
	private boolean showWindow;

	public SMSController() {
		try {
			this.msg = new Message();
			this.sender = new Sender();
			this.sender.addSenderListener( this );
		} catch (IOException e) {
			LOGGER.severe( e.getMessage() );
		} catch (SOAPException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
	}

	public void openContacts(ActionEvent event){
		SMSContactController smscc = (SMSContactController) AonUtil.getRegisteredBean( SMS_CONTACT_MANAGED_BEAN );
		smscc.init();
		setShowWindow(true);
	}

	public void add2List(ActionEvent event) {
		if ( this.recipient != null && !this.recipient.equals( "" ) ) {
			this.msg.add( this.recipient );
			this.recipients.add( this.recipient );
			this.recipient = null;
		}
	}

	public void sendMessage(ActionEvent event) throws CloneNotSupportedException {
		this.msg.getInfo().setMessage( message );
		sender.send( (Message) this.msg.clone() );
		reset( event );
	}

	public void reset(ActionEvent event) {
		this.recipients = new ArrayList<String>();
		this.recipient = null;
		this.message = null;
		this.msg.init();
	}

	public void accept(ActionEvent event) {
		SMSContactController bean = (SMSContactController) AonUtil.getRegisteredBean( SMS_CONTACT_MANAGED_BEAN );
		List<Contact> lst = bean.getSelectedRows();
	    for (int i = 0, max = lst.size(); i < max; i++) {
	    	Contact e = lst.get(i);
	    	if ( e.getCellularPhone() != null ) {
	    		String r = Utils.parsePhoneNumber( e.getCellularPhone() );
	    		this.msg.add( r );
				this.recipients.add( e.getDisplayName() + "-" + r );
	    	}
		}
	}

	public void messageSent(SenderEvent event) {
		LOGGER.info( event.getMessageId() + " " + event.getStatus() + " " + ( (Message) event.getSource() ).getInfo().getMessage() );
	}

}