package com.esferalia.aon.gwt.document.client;

import java.util.LinkedList;
import java.util.Vector;

import com.esferalia.aon.gwt.document.shared.Contact;
import com.esferalia.aon.gwt.document.shared.ContactList;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class Utils {

	public static String getMonth(Integer i){
		switch (i){
		case 0 : return "01";
		case 1 : return "02";
		case 2 : return "03";
		case 3 : return "04";
		case 4 : return "05";
		case 5 : return "06";
		case 6 : return "07";
		case 7 : return "08";
		case 8 : return "09";
		case 9 : return "10";
		case 10 : return "11";
		default : return "12";
		}
	}
	
	public static String getDay(Integer i){
		if(i<10){
			return "0"+i.toString();
		}
		else return i.toString();
	}
	
	
	public static boolean isNotAlpKey(int code) {
	    switch (code) {
	      case KeyCodes.KEY_ALT:
	      case KeyCodes.KEY_CAPS_LOCK:
	      case KeyCodes.KEY_CONTEXT_MENU:
	      case KeyCodes.KEY_CTRL:
	      case KeyCodes.KEY_DOWN:
	      case KeyCodes.KEY_END:
	      case KeyCodes.KEY_ENTER:
	      case KeyCodes.KEY_ESCAPE:
	      case KeyCodes.KEY_F1:
	      case KeyCodes.KEY_F2:
	      case KeyCodes.KEY_F3:
	      case KeyCodes.KEY_F4:
	      case KeyCodes.KEY_F5:
	      case KeyCodes.KEY_F6:
	      case KeyCodes.KEY_F7:
	      case KeyCodes.KEY_F8:
	      case KeyCodes.KEY_F9:
	      case KeyCodes.KEY_F10:
	      case KeyCodes.KEY_F11:
	      case KeyCodes.KEY_F12:
	      case KeyCodes.KEY_FIRST_MEDIA_KEY:
	      case KeyCodes.KEY_HOME:
	      case KeyCodes.KEY_INSERT:
	      case KeyCodes.KEY_LAST_MEDIA_KEY:
	      case KeyCodes.KEY_LEFT:
	      case KeyCodes.KEY_MAC_ENTER:
	      case KeyCodes.KEY_MAC_FF_META:
	      case KeyCodes.KEY_NUMLOCK:
	      case KeyCodes.KEY_PAGEDOWN:
	      case KeyCodes.KEY_PAGEUP:
	      case KeyCodes.KEY_PAUSE:
	      case KeyCodes.KEY_PRINT_SCREEN:
	      case KeyCodes.KEY_RIGHT:
	      case KeyCodes.KEY_SCROLL_LOCK:
	      case KeyCodes.KEY_SHIFT:
	      case KeyCodes.KEY_SPACE:
	      case KeyCodes.KEY_TAB:
	      case KeyCodes.KEY_UP:
	        return true;
	      default:
	        return false;
	    }
	  }

	static AonSuggestOracle createOracle(LinkedList<Domain> vector) {
		AonSuggestOracle oracleSons = new AonSuggestOracle();

		for (Domain d : vector) {
			oracleSons.add(d.getDescription()+" ( "+d.getName()+")");
		}
		return oracleSons;
	}

	static AonSuggestOracle createOracleContact(ContactList cl) {
		AonSuggestOracle oracleSons = new AonSuggestOracle();

		for (Contact c : cl.getList()) {
			oracleSons.add(c.getEmail());
			//oracleSons.add(c.getDisplayName()+" <"+c.getEmail()+">");
		}
		return oracleSons;
	}

	/*static String getOracleStringContact(String s){
		String aux="";
		if(s.length()>1){
			Integer pos = s.indexOf('<');
			Integer pos2 = s.indexOf('>');
			aux = s.substring(pos+1,pos2);
			String s2 = s.substring(pos2);
			String aux2 = getOracleStringContact(s2);
		
		}
		
		return aux;
	}*/
	
	static String getOracleString(String s){
		String aux;
		Integer pos = s.lastIndexOf('(');
		aux = s.substring(pos+2,s.length()-1);
		return aux;
	}
	
	static MultiWordSuggestOracle createOracle2(Vector<String> l) {
		Vector<Suggestion> suggestions = new Vector<SuggestOracle.Suggestion>();
		
		for (String string : l) {
			suggestions.add(new Suggestion() {
				//Sustituye el string en SuggestBox.
				@Override
				public String getReplacementString() {
					return  "1";
				}
				
				//Sustituye el string en el popUp.
				@Override
				public String getDisplayString() {
					return  "1";
				}
			});
		}
		
		return new MultiWordSuggestOracle();
	}
	
	public static String icon(String m) {
		if(m.contains("audio")) return "aon-icon-google-drive-audio";
		if(m.contains("image")) return "aon-icon-google-drive-image";
		if(m.contains("video")) return "aon-icon-google-drive-mov";
		switch (m) {
		case "application/vnd.google-apps.audio":return "aon-icon-google-drive-audio";
		case "application/vnd.google-apps.document":return "aon-icon-google-drive-docs";
		case "application/vnd.google-apps.drawing":return "aon-icon-google-drive-drawing";
		case "application/vnd.google-apps.folder":return "aon-icon-google-drive-folder";
		case "application/vnd.google-apps.form":return "aon-icon-google-drive-form";
		case "application/vnd.google-apps.photo":return "aon-icon-google-drive-image";
		case "application/vnd.google-apps.presentation":return "aon-icon-google-drive-presentation";
		case "application/vnd.google-apps.spreadsheet":return "aon-icon-google-drive-calc";
		case "application/vnd.google-apps.video":return "aon-icon-google-drive-mov";
		
		case "application/msword":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
		case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
		case "application/vnd.ms-word.document.macroEnabled.12":
		case "application/vnd.ms-word.template.macroEnabled.12":
			return "aon-icon-google-drive-word";
		case "application/vnd.ms-excel": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": 
		case "application/vnd.openxmlformats-officedocument.spreadsheetml.template": 
		case "application/vnd.ms-excel.sheet.macroEnabled.12": 
		case "application/vnd.ms-excel.template.macroEnabled.12": 
		case "application/vnd.ms-excel.addin.macroEnabled.12": 
		case "application/vnd.ms-excel.sheet.binary.macroEnabled.12": 
			return "aon-icon-google-drive-excel";
		case "application/vnd.ms-powerpoint": 
		case "application/vnd.openxmlformats-officedocument.presentationml.presentation": 
		case "application/vnd.openxmlformats-officedocument.presentationml.template": 
		case "application/vnd.openxmlformats-officedocument.presentationml.slideshow": 
		case "application/vnd.ms-powerpoint.addin.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.presentation.macroEnabled.12": 
		case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12": 
			return "aon-icon-google-drive-power-point";
		case "application/pdf": return "aon-icon-google-drive-pdf-sinfondo";
		
		case "application/x-rar-compressed": return "aon-icon-google-drive-zip";
		case "application/zip": return "aon-icon-google-drive-zip";
		default:
			return "aon-icon-google-drive-unknown";
		}
	}
	
	public static Boolean isGdocs(String m){
		switch (m) {
			case "application/vnd.google-apps.document":
			case "application/vnd.google-apps.drawing":
			case "application/vnd.google-apps.folder":
			case "application/vnd.google-apps.form":
			case "application/vnd.google-apps.presentation":
			case "application/vnd.google-apps.spreadsheet":
				return true;
			default:
				return false;
			}
		
	}
	
	static MailAccount mailAccount;
	/*
	public static IMailAccount getIMailAccount(MailAccount ma) {
		mailAccount = ma;
		return new IMailAccount() {
			
			@Override
			public void setISignature(ISignature signature) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDefaultAccount(boolean defaultAccount) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isOutgoingVerification() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isIMAP() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isEnterpriseAccount() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isDefaultAccount() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public String getTrashFolder() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getSpamFolder() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getSentFolder() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getReplyToMail() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getProtocol() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getPasswordString() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ConnectionSecurity getOutgoingSecurity() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getOutgoingPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getOutgoingHost() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getName() {
				return mailAccount.getName();
			}
			
			@Override
			public String getMailUsername() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ConnectionSecurity getIncomingSecurity() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getIncomingPort() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getIncomingHost() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ISignature getISignature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getEmail() {
				return mailAccount.getEmail();
			}
			
			@Override
			public String getDraftFolder() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDisplayName() {
				return mailAccount.getName();
			}
		};
	}
	
	public static AonMessage getAonMessage(AonServer server, MailAccount ma) {
		mailAccount = ma;
		Message message = new Message() {
			
			@Override
			public void writeTo(OutputStream arg0) throws IOException,
					MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setText(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setHeader(String arg0, String arg1) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFileName(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDisposition(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDescription(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDataHandler(DataHandler arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setContent(Object arg0, String arg1) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setContent(Multipart arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void removeHeader(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean isMimeType(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public int getSize() throws MessagingException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Enumeration getNonMatchingHeaders(String[] arg0)
					throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getMatchingHeaders(String[] arg0)
					throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getLineCount() throws MessagingException {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public InputStream getInputStream() throws IOException, MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String[] getHeader(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getFileName() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDisposition() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getDescription() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public DataHandler getDataHandler() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getContentType() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object getContent() throws IOException, MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Enumeration getAllHeaders() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addHeader(String arg0, String arg1) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSubject(String arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setSentDate(Date arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setRecipients(RecipientType arg0, Address[] arg1)
					throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFrom(Address arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFrom() throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setFlags(Flags arg0, boolean arg1) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void saveChanges() throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Message reply(boolean arg0) throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String getSubject() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Date getSentDate() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Address[] getRecipients(RecipientType arg0)
					throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Date getReceivedDate() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Address[] getFrom() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Flags getFlags() throws MessagingException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void addRecipients(RecipientType arg0, Address[] arg1)
					throws MessagingException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void addFrom(Address[] arg0) throws MessagingException {
				// TODO Auto-generated method stub
				
			}
		};
		AonMessage am = new AonMessage(message);
		return am;
	}
	
	*/
	public static void main(String[] args) {
		String s = "ARISTIZABAL GARCIA, ROBINSON ( robinson-novus.aibanez.net)";
		System.out.println(s);
		System.out.println(getOracleString(s));
	}
	
	
}

