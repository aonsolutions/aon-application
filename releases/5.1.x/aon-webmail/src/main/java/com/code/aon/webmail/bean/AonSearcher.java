package com.code.aon.webmail.bean;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import com.code.aon.webmail.WebmailException;

public class AonSearcher {

	private SearchTerm term;
	
	private AonFolder aonFolder;
	
	/**
	 * @return the term
	 */
	public SearchTerm getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(SearchTerm term) {
		this.term = term;
	}

	/**
	 * @return the aonFolder
	 */
	public AonFolder getAonFolder() {
		return aonFolder;
	}

	/**
	 * @param aonFolder the aonFolder to set
	 */
	public void setAonFolder(AonFolder aonFolder) {
		this.aonFolder = aonFolder;
	}

	public AonMessage[] search() throws WebmailException{
		if (!hasTerm())
			return new AonMessage[0];
		Folder folder = aonFolder.getFolder();
		Message[] messages;
		try {
			messages = folder.search(term);
			AonMessage[] search = new AonMessage[messages.length];
			AonMessage aonMessage;
			for (int i = 0; i < messages.length; i++){
				aonMessage = new AonMessage();
				aonMessage.setMessage((MimeMessage)messages[i]);
				aonMessage.setParent(aonFolder);
				search[i] = aonMessage; 
			}
			return search;
		} catch (MessagingException e) {
			throw new WebmailException(e);
		}
	}

	public void addTerm(SearchTerm value){
		term = new FlagTerm(new Flags(Flags.Flag.DELETED), false);
		term = new AndTerm(term,value);
	}
	
	public void addStringTerm(String valueStr,int type){
		SearchTerm value = null;
		switch (type) {
			case 0:
				value = new BodyTerm(valueStr);
				break;
			case 1:
				value = new RecipientStringTerm(Message.RecipientType.TO, valueStr);
				break;
			case 2:
				value = new FromStringTerm(valueStr);
				break;
			case 3:
				value = new SubjectTerm(valueStr);
				break;
		}
		term = new FlagTerm(new Flags(Flags.Flag.DELETED), false);
		if (value!=null)
			term = new AndTerm(term,value);
	}
	
	public boolean hasTerm(){
		return term!=null;
	}
	
	public static int BODYTERM = 0;
	public static int ADDRESS_CC = 1;
	public static int ADDRESS_FROM = 2;
	public static int SUBJECT = 3;
	
}
