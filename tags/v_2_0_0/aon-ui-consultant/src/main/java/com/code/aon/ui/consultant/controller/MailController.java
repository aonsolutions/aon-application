package com.code.aon.ui.consultant.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class MailController {
	
	public DataModel getModel(){
		List<Mail> list = new LinkedList<Mail>();
		
		Mail mail = new Mail();
		mail.setDescription("Bandeja de Entrada");
		mail.setNoRead(new Integer(0));
		mail.setMessages(new Integer(147));
		list.add(mail);
		
		mail = new Mail();
		mail.setDescription("Borradores");
		mail.setNoRead(new Integer(0));
		mail.setMessages(new Integer(0));
		list.add(mail);
		
		mail = new Mail();
		mail.setDescription("Enviados");
		mail.setNoRead(new Integer(0));
		mail.setMessages(new Integer(58));
		list.add(mail);
		
		mail = new Mail();
		mail.setDescription("Papelera");
		mail.setNoRead(new Integer(0));
		mail.setMessages(new Integer(0));
		list.add(mail);
		
		mail = new Mail();
		mail.setDescription("Spam");
		mail.setNoRead(new Integer(3));
		mail.setMessages(new Integer(3));
		list.add(mail);
		
		return new ListDataModel(list);
	}
	
	public class Mail {
		
		private String description;
			
		private Integer noRead;
		
		private Integer messages;
	
		public Integer getMessages() {
			return messages;
		}
	
		public void setMessages(Integer messages) {
			this.messages = messages;
		}
	
		public Integer getNoRead() {
			return noRead;
		}
	
		public void setNoRead(Integer noRead) {
			this.noRead = noRead;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}
