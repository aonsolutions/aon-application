package com.code.aon.messaging.sms;

import java.util.ArrayList;
import java.util.List;

import com.esendex.sdk.ems.soapinterface.MessageContentType;
import com.esendex.sdk.ems.soapinterface.Messagesubmission;

public class Message implements Cloneable {

	MessageInfo info;
	List<Recipient> recipients;

	public Message() {
		init();
	}

	public MessageInfo getInfo() {
		return info;
	}

	public void setInfo(MessageInfo info) {
		this.info = info;
	}

	public List<Recipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<Recipient> recipients) {
		this.recipients = recipients;
	}

	public void add(String recipient) {
		this.recipients.add( new Recipient( recipient ) );
	}

	public Recipient remove(int index) {
		return this.recipients.remove( index );
	}

	/**
	 * If multiple recipients returns true, false otherwise.
	 * 
	 * @return
	 */
	public boolean isMultiple() {
		return this.recipients.size() > 1;
	}

	/**
	 * If full message returns true, false otherwise.
	 * 
	 * @return
	 */
	public boolean isFull() {
		return (this.info.getOriginator() != null);
	}

	public String[] toRecipientsArray() {
		String[] s = new String[ this.recipients.size() ];
		for (int i = 0; i < this.recipients.size(); i++) {
			s[ i ] = this.recipients.get( i ).getName();
		}
		return s;
	}

	/**
	 * Esendex message for sending in a Batch way.
	 * 
	 * @return
	 */
	public Messagesubmission getMessagesubmission() {
		return new Messagesubmission( info.getOriginator(), recipients.get( 0 ).getName(), 
				info.getMessage(), MessageContentType.Text, info.getValidityperiod() );
	}

	/**
	 * Initializes message attributes.
	 */
	public void init() {
		this.info = new MessageInfo();
		this.recipients = new ArrayList<Recipient>();
	}

	/**
	 * Fill message identifiers for each recipient.
	 * 
	 * @param messageIds
	 */
	public void fillIds(String[] messageIds) {
		for (int i = 0; i < this.recipients.size(); i++) {
			this.recipients.get( i ).setId( messageIds[ i ] );
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public class Recipient {

		private String id;
		private String name;

		public Recipient(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

}
