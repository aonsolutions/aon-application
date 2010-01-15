package com.code.aon.messaging.sms;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.messaging.event.ISenderListener;
import com.code.aon.messaging.event.SenderEvent;
import com.code.aon.messaging.util.BooleanLock;

public class SynchronizedSender implements ISenderListener {
	
	private static final String BUNDLE_NAME = "com.code.aon.messaging.i18n.messages";
	
	private static final long DEFAULT_TIMEOUT = 30000;
	
	private Sender sender;
	
	private long timeout;
	
	private BooleanLock sent;
	
	private Message sentMessage;
	
	private int errorCode;
	
	private ResourceBundle bundle;

	public SynchronizedSender(Sender sender, Locale locale) {
		this.sender = sender;
		this.bundle = ResourceBundle.getBundle( BUNDLE_NAME, locale );
		this.timeout = DEFAULT_TIMEOUT;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Message send(Message msg) throws SMSException {
		this.errorCode = 0;
		this.sentMessage = null;
		this.sent = new BooleanLock(false);
		this.sender.addSenderListener(this);
		try {
			Message newMessage = (Message) msg.clone();
			sender.send( newMessage );
			this.sent.waitUntilTrue(this.timeout);
		} catch (Throwable th) {
			throw new SMSException( bundle.getString("aon_sms_esendex_error") );
		} finally {
			this.sender.removeSenderListener(this);
		}
		if ( this.errorCode != 0 ) {
			String message = bundle.getString( "aon_sms_esendex_" + errorCode );
			throw new SMSException( message );
		}
		return this.sentMessage;
	}

	@Override
	public void messageFailed(SenderEvent event) {
		this.errorCode = event.getErrorCode();
		this.sent.setValue(true);
	}

	@Override
	public void messageSent(SenderEvent event) {
		this.sentMessage = (Message) event.getSource();
		this.sent.setValue(true);
	}
	
}
