package com.code.aon.ui.webmail.bean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.exception.WebmailException;

public class AonMessage implements IMimeType {

	private static final Logger LOGGER = Logger.getLogger(AonMessage.class
			.getName());

	private AonFolder parent;
	
	protected MimeMessage message;

	private Flags previousMessageFlag;

	private Flags currentMessageFlag;

	private AonMessageTracer amt;
	
	private boolean selected;
	
	private Boolean attachment;

	public AonMessage(){
	}
	
	/**
	 * Gets the MimeMessage that is wrapped by this class.
	 * 
	 * @return a mime message.
	 */
	public MimeMessage getMessage() {
		return message;
	}

	/**
	 * Sets the MimeMessage that this class wrapps.
	 * 
	 * @param message
	 * @throws WebmailException 
	 */
	public void setMessage(MimeMessage message) throws WebmailException {
		try {
			previousMessageFlag = message.getFlags();
			currentMessageFlag = message.getFlags();
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message fags");
			throw new WebmailException(e);
		}

		this.message = message;
		this.attachment = null;
		this.amt = new AonMessageTracer();
		this.amt.setMessage(message);
	}

	/**
	 * @return the parent
	 */
	public AonFolder getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(AonFolder parent) {
		this.parent = parent;
	}
	
	/**
	 * @return the amt
	 */
	public AonMessageTracer getAmt() {
		return amt;
	}

	/**
	 * Sets the cached message flags. This is a cached value and does not
	 * actually set the message for the value.
	 * 
	 * @param messageFlags
	 *            new message flags.
	 */
	public void setMessageFlag(Flags messageFlags) {
		previousMessageFlag = currentMessageFlag;
		currentMessageFlag = messageFlags;
	}

	/**
	 * Gets the previously set message flags. This is a cached value and does
	 * not actually check the message for the value.
	 * 
	 * @return previously set message flags
	 */
	public Flags getPreviousMessageFlag() {
		return previousMessageFlag;
	}

	/**
	 * Gets the currently set message flags. This is a cached value and does not
	 * actually check the message for the value.
	 * 
	 * @return previously set message flags
	 */
	public Flags getCurrentMessageFlag() {
		return currentMessageFlag;
	}

	//**************************************************************************
	//**************************************************************************
	//PROPERTIES
	//**************************************************************************
	//**************************************************************************

	/**
	 * Get the email address. If the email is encoded as per RFC 2047, it is
	 * decoded and converted into Unicode. If the decoding or conversion fails,
	 * the raw data is returned as is.
	 * 
	 * @param sender
	 *            string making up a email encoded as per RFC 2047.
	 * @return the personal value of the email string if found; otherwie an
	 *         empty string.
	 */
	public static String getEmail(String sender) {
		String email = "";
		try {
			InternetAddress addr = InternetAddress.parse(sender)[0];
			if (addr.getAddress() != null)
				email = getDisplayAddressFull((Address)addr);
		} catch (AddressException e) {
			LOGGER.log(Level.ALL, "Invalid sender", e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.log(Level.ALL,
					"Invalid sender, could not par internet address", e);
		}
		return email;
	}

	/**
	 * Gets the send date of this message. If the message has not yest been send
	 * then the current date is returned.
	 * 
	 * @return messages sent date if set, todays date if the message has no date
	 *         value.
	 * @throws WebmailException 
	 */
	public Date getSentDate() throws WebmailException {
		try {
			return message.getSentDate();
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message send date", e);
			throw new WebmailException(e);
		}
	}

	/**
	* Returns the date the message was sent (or received if the sent date
	* is null.
	 * @throws WebmailException 
	*/
	public String getSentDateString() throws WebmailException{
		try{
			Date date;
			SimpleDateFormat df = new SimpleDateFormat("EEE,dd/MM/yy-HH:mm");
			if ((date = message.getSentDate()) != null) {
				return (df.format(date));
			}	else if ((date = message.getReceivedDate()) != null) {
				return (df.format(date));
			}	else {
				return "";
			}
		}catch (Exception e) {
			throw new WebmailException(e);
		}
	}


	/**
	 * Gets the message subject.
	 * 
	 * @return message subject. If null, an empty string is returned.
	 * @throws WebmailException 
	 */
	public String getSubject() throws WebmailException {
		try {
			return message.getSubject();
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message subject", e);
			throw new WebmailException(e);
		}
	}

	private static String getDisplaySubject( Message message ) throws MessagingException {
		String subject = message.getSubject();
		return StringUtils.defaultString(StringEscapeUtils.escapeHtml(subject));
	}
	
	public String getDisplaySubject() throws MessagingException {
		return getDisplaySubject(message);
	}
	
	public String getSubjectShort() throws WebmailException {
		String subject = StringUtils.abbreviate( getSubject(), 50 );
		return StringUtils.defaultString(StringEscapeUtils.escapeHtml(subject));
	}

	/**
	 * Set the subject of the wrapped message.
	 * 
	 * @param subject
	 *            new subject content for message.
	 * @throws WebmailException 
	 */
	public void setSubject(String subject) throws WebmailException {
		try {
			message.setSubject(subject);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error setting subject", e);
			throw new WebmailException(e);
		}
	}

	private static String getSender( Message message ) throws MessagingException {
		Address[] addresses = message.getFrom();
		if (addresses!=null && addresses.length>0){
			InternetAddress tmpAddress = (InternetAddress) addresses[0];
			String sender = getDisplayAddressFull((Address)tmpAddress);
			return sender;
		}else{
			return "";
		}
	}
	
	public String getSender() throws WebmailException {
		try {
			return getSender( message );
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,"Can not recover address.",e);
			throw new WebmailException(e);
		}
	}

	public void setSender(String from) throws WebmailException {
		try {
			InternetAddress address = InternetAddress.parse(from, true)[0];
			setSender(address);
		} catch (javax.mail.MessagingException e) {
			LOGGER.log(Level.ALL,
					"Could decode from string, maynot be in RFC822 format", e);
			throw new WebmailException(e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.log(Level.ALL,
					"Invalid sender, could not par internet address", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Set the RFC 822 "From" header field. Any existing values are replaced
	 * with the given address. If address is null, this header is removed.
	 * 
	 * @param from -
	 *            the address in RFC822 format.
	 * @throws WebmailException 
	 */
	public void setSender(Address from) throws WebmailException {
		if (message != null) {
			try {
				message.setFrom(from);
			} catch (IllegalWriteException e) {
				LOGGER.log(Level.ALL,
						"Could not set from address, read only message", e);
				throw new WebmailException(e);
			} catch (javax.mail.MessagingException e) {
				LOGGER.log(Level.ALL,
						"Could decode from string, maynot be in RFC822 format",
						e);
				throw new WebmailException(e);
			}
		} else {
			LOGGER.log(Level.INFO, "Null message could not set sender address");
		}
	}

	public String getSender( int maxWidth ) throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getFrom();
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,"Can not recover address.",e);
			throw new WebmailException(e);
		}
		String sender = "";
		if (addresses!=null && addresses.length>0){
			InternetAddress tmpAddress = (InternetAddress) addresses[0];
			sender = getDisplayAddressShort((Address)tmpAddress);
		}
		return (maxWidth != -1) ? StringUtils.abbreviate(sender, maxWidth) : sender;
	}
	
	public String getSenderShort() throws WebmailException {
		return getSender(25);
	}

	public String getSenderEmail() throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getFrom();
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,"Can not recover address.",e);
			throw new WebmailException(e);
		}
		String sender = "";
		if (addresses!=null && addresses.length>0){
			InternetAddress tmpAddress = (InternetAddress) addresses[0];
			sender = getDisplayEmail((Address)tmpAddress);
		}
		return sender;
	}

	public void setRecipients(String to, RecipientType type) throws WebmailException {
		try {
			InternetAddress[] address = InternetAddress.parse(to, true);
			setRecipients(address, type);
		} catch (javax.mail.MessagingException e) {
			LOGGER.log(Level.ALL,
					"Could decode from string, maynot be in RFC822 format", e);
			throw new WebmailException(e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.log(Level.ALL,
					"Invalid sender, could not par internet address", e);
			throw new WebmailException(e);
		}
	}

	public void setRecipientsTo(String addresses) throws WebmailException {
		setRecipients(addresses, MimeMessage.RecipientType.TO);
	}
	
	/**
	 * Gets the recipients specifiedy by the "TO" header.
	 * 
	 * @return String representing all the addresses that make up the "To"
	 *         header
	 * @throws WebmailException 
	 */
	public String getRecipientsTo() throws WebmailException {
		try {
			return getRecipient(message, MimeMessage.RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message recepients ", e);
			throw new WebmailException(e);
		}
	}

	public String getRecipientsToShort() throws WebmailException {
		int maxLength = 25;
		Address[] addresses = null;
		try {
			addresses = message.getRecipients(RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,"Can not recover address.",e);
			throw new WebmailException(e);
		}
		String recipientsTo = null;
		StringBuffer addressBuffer = new StringBuffer();
		if (addresses != null && addresses.length > 0) {
			InternetAddress tmpAddress;
			for (int i = 0; i < addresses.length; i++) {
				tmpAddress = (InternetAddress) addresses[i];
				addressBuffer.append(getDisplayAddressShort((Address)tmpAddress)+AonMessageUtils.EMAIL_SEPARATOR);
			}
			recipientsTo = addressBuffer.toString();
			recipientsTo = recipientsTo.substring(0, recipientsTo.length() - 1);
		}
		if (recipientsTo==null)
			recipientsTo = "";
		if (recipientsTo.length()>maxLength)
			return recipientsTo.substring(0,maxLength)+"..";
		return recipientsTo.substring(0,recipientsTo.length());
	}

	public String getRecipientsToEmail() throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getRecipients(RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,"Can not recover address.",e);
			throw new WebmailException(e);
		}
		String recipientsTo = null;
		StringBuffer addressBuffer = new StringBuffer();
		if (addresses != null && addresses.length > 0) {
			InternetAddress tmpAddress;
			for (int i = 0; i < addresses.length; i++) {
				tmpAddress = (InternetAddress) addresses[i];
				addressBuffer.append(getDisplayEmail((Address)tmpAddress)+AonMessageUtils.EMAIL_SEPARATOR);
			}
			recipientsTo = addressBuffer.toString();
			recipientsTo = recipientsTo.substring(0, recipientsTo.length() - 1);
		}
		if (recipientsTo==null)
			recipientsTo = "";
		return recipientsTo;
	}
	
	public void setRecipientsCc(String addresses) throws WebmailException {
		setRecipients(addresses, MimeMessage.RecipientType.CC);
	}

	private static String getRecipientsCc( Message message ) throws MessagingException {
		return getRecipient(message, MimeMessage.RecipientType.CC);
	}
	
	/**
	 * Gets the recipients specifiedy by the "CC" header.
	 * 
	 * @return String representing all the addresses that make up the "CC"
	 *         header
	 * @throws WebmailException 
	 */
	public String getRecipientsCc() throws WebmailException {
		try {
			return getRecipientsCc(message);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message recepients ", e);
			throw new WebmailException(e);
		}
	}

	public void setRecipientsBcc(String addresses) throws WebmailException {
		setRecipients(addresses, MimeMessage.RecipientType.BCC);
	}
	
	/**
	 * Gets the recipients specifiedy by the "BCC" header.
	 * 
	 * @return String representing all the addresses that make up the "BCC"
	 *         header
	 * @throws WebmailException 
	 */
	public String getRecipientsBcc() throws WebmailException {
		try {
			return getRecipient(message, MimeMessage.RecipientType.BCC);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error getting message recepients ", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Utility method getting message recipient.
	 * 
	 * @param type
	 *            recipient type, valid values are constants in
	 *            javax.mail.Message.RecipientType
	 * @return String representing the specified recipient. Empty string if no
	 *         such reciepient is found.
	 * @throws MessagingException 
	 * @throws WebmailException 
	 */
	private static String getRecipient(Message message, RecipientType type) throws MessagingException {
		StringBuffer recipients = new StringBuffer();
		Address[] addresses = message.getRecipients(type);
		if (addresses != null && addresses.length > 0) {
			// Write out the addres in the order they are found.
			for (int i = 0; i < addresses.length; i++) {
				recipients.append(getDisplayAddressFull(addresses[i]) + AonMessageUtils.EMAIL_SEPARATOR);
			}
			return recipients.substring(0, recipients.length() - 1).toString();
		}
		return "";
	}

		
		
	/**
	 * Set the "TO" recipient type to the given addresses. If the address
	 * parameter is null, the corresponding recipient field is removed.
	 * 
	 * @param recipient
	 *            string made up of valid email addresses.
	 * @throws WebmailException 
	 */
	public void setRecipientsTo(Address[] recipient) throws WebmailException {
		setRecipients(recipient, MimeMessage.RecipientType.TO);
	}

	/**
	 * Set the "CC" recipient type to the given addresses. If the address
	 * parameter is null, the corresponding recipient field is removed.
	 * 
	 * @param recipient
	 *            string made up of valid email addresses.
	 * @throws WebmailException 
	 */
	public void setRecipientsCc(Address[] recipient) throws WebmailException {
		setRecipients(recipient, MimeMessage.RecipientType.CC);
	}

	/**
	 * Set the "BCC" recipient type to the given addresses. If the address
	 * parameter is null, the corresponding recipient field is removed.
	 * 
	 * @param recipient
	 *            string made up of valid email addresses.
	 * @throws WebmailException 
	 */
	public void setRecipientsBcc(Address[] recipient) throws WebmailException {
		setRecipients(recipient, MimeMessage.RecipientType.BCC);
	}

	/**
	 * Utility method getting message recipient.
	 * 
	 * @param type
	 *            recipient type, valid values are constants in
	 *            javax.mail.Message.RecipientType
	 * @throws WebmailException 
	 */
	protected void setRecipients(Address[] address,
			final javax.mail.Message.RecipientType type) throws WebmailException {
		try {
			message.setRecipients(type, address);
		} catch (IllegalWriteException e) {
			LOGGER.log(Level.ALL,
					"Could not set recipients address, read only message", e);
			throw new WebmailException(e);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Error setting message To recepients ", e);
			throw new WebmailException(e);
		}
	}

	//**************************************************************************
	//**************************************************************************
	//ATTACHMENTS
	//**************************************************************************
	//**************************************************************************
	private boolean isAttachment( BodyPart part ) throws MessagingException {
		boolean attachment = false;
		String disposition = part.getDisposition();
		if ( (disposition != null) ) {
			if (disposition.equalsIgnoreCase(Part.ATTACHMENT) ) {
				attachment = true;
			} else if (part.getFileName() != null) {
				if (! part.isMimeType(APPLICATION_APPLEFILE) ) {
					attachment = part.isMimeType(IMAGE_ANY) || part.isMimeType(APPLICATION_ANY);	
				}
			}
		}		
		return attachment;
	}
	
	public List<Part> getAttachmentParts( Part part ) throws MessagingException, IOException {
		List<Part> parts = new ArrayList<Part>();
		if ( part.isMimeType(MULTIPART_ANY) ) {
			Multipart mp = (Multipart) part.getContent();
			int numPart = mp.getCount();
			for (int i = 0; i < numPart; i++) {
				BodyPart bodyPart = mp.getBodyPart(i);
				if ( isAttachment(bodyPart) ) {
					parts.add( bodyPart );
				}
				parts.addAll( getAttachmentParts(bodyPart) );
			}			
		}
		return parts;
	}

	public boolean hasAttachments( Part part ) throws MessagingException, IOException {
		if ( part.isMimeType(MULTIPART_ANY) ) {
			Multipart mp = (Multipart) part.getContent();
			int numPart = mp.getCount();
			for (int i = 0; i < numPart; i++) {
				BodyPart bodyPart = mp.getBodyPart(i);
				if ( isAttachment(bodyPart) || hasAttachments(bodyPart) ) {
					return true;
				}
			}			
		}
		return false;
	}
	
	public List<AonAttachment> getAttachements() throws WebmailException {
		List<AonAttachment> attachs = new ArrayList<AonAttachment>();
		try {
			List<Part> parts = getAttachmentParts( message );
			if (! parts.isEmpty() ) {
				for( int i = 0; i < parts.size(); i++) {
					AonAttachment attach = new AonAttachment();
					attach.setPart( parts.get(i) );
					attach.setPosition( i );
					attachs.add(attach);					
				}
			}
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,
					"Error determining if message has attachement", e);
			throw new WebmailException(e);
		} catch (IOException e) {
			LOGGER.log(Level.ALL,
					"Error determining if message has attachement", e);
			throw new WebmailException(e);
		}
		return attachs;
	}

	/**
	* Method for checking if the message has attachments.
	*/
	public boolean isAttachment() throws WebmailException {
		if ( this.attachment == null ) {
			try {
				this.attachment = hasAttachments( message );
			} catch (Exception ex) {
				LOGGER.log(Level.ALL, "Error determining if message has attachement", ex);
				throw new WebmailException(ex);
			}
		}
		return this.attachment;
	}

	//**************************************************************************
	//**************************************************************************
	//CONTENT
	//**************************************************************************
	//**************************************************************************

	
	/**
	 * Gets the content of the message. The method will decide whether or not
	 * will return html or plain text.
	 * 
	 * @return emails content type
	 * @throws WebmailException 
	 */
	public String getContent() throws WebmailException {
		Folder folder = message.getFolder();
		if (folder != null && !folder.isOpen()) {
			try {
				folder.open(Folder.READ_WRITE);
			} catch (MessagingException e) {
				LOGGER.log(Level.ALL,
						"Error opening folder for message content", e);
				throw new WebmailException(e);
			}
		}
		try {
			return amt.getBodyHTML();
		} catch (MessagingException e) {
			return AonMessageUtils.HTML_LINE_BREAK;
		} catch (IOException e) {
			return AonMessageUtils.HTML_LINE_BREAK;
		}
	}

	/**
	 * Sets the message content as plain/text from the given form text area.
	 * This could be update for differnet content types.
	 * @throws WebmailException 
	 */
	public void setContent(MimeMultipart content) throws WebmailException {
		try {
			message.setContent(content);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,
					"Message content could not be set, readonly state", e);
			throw new WebmailException(e);
		}

	}

	public void setContent(String content) throws WebmailException {
		try {
			message.setText(content);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL,
					"Message content could not be set", e);
			throw new WebmailException(e);
		}
	}

	public boolean isExpunged() {
		return message.isExpunged();
	}

	public void delete() throws WebmailException{
		try {
			message.setFlag(Flag.DELETED, true);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Problem while deleting");
			throw new WebmailException(e);
		}
	}
	
    public boolean isReaded() throws WebmailException{
    	try {
			return message.isSet(Flag.SEEN);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Problem while reading message flags");
			throw new WebmailException(e);
		}
    }

    public boolean isAnswered() throws WebmailException{
    	try {
			return message.isSet(Flag.ANSWERED);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Problem while reading message flags");
			throw new WebmailException(e);
		}
    }

	public void setSentDate(Date date) throws WebmailException {
		try {
			message.setSentDate(date);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Problem while setting date");
			throw new WebmailException(e);
		}
	}

	public boolean match(SearchTerm term) throws WebmailException{
		try {
			return message.match(term);
		} catch (MessagingException e) {
			LOGGER.log(Level.ALL, "Problem while searching");
			throw new WebmailException(e);
		}
	}
	
	private static String getDisplayAddressShort(Address a) {
		String pers = null;
		String addr = null;
		if (a instanceof InternetAddress
				&& ((pers = ((InternetAddress)a).getPersonal()) != null)) {
			pers = AonMessageUtils.parse_email(pers);
			addr = pers;
		} else {
			addr = a.toString();
		}
		return addr;
	}
	
	private static String getDisplayAddressFull(Address a) {
		String pers = null;
		String addr = null;
		if (a instanceof InternetAddress
				&& ((pers = ((InternetAddress)a).getPersonal()) != null)) {
			pers = AonMessageUtils.parse_email(pers);
			addr = pers + " " + "&lt;"+((InternetAddress)a).getAddress()+"&gt;";
		} else {
			addr = a.toString();
		}
		return addr;
	}

	private static String getDisplayEmail(Address a) {
		String addr = null;
		if (a instanceof InternetAddress) {
			addr = ""+((InternetAddress)a).getAddress();
		} else {
			addr = a.toString();
		}
		return addr;
	}

	public static String parseDisplayAddress(String address){
		String addressParsed;
		addressParsed = address.replaceAll("&lt;","<");
		addressParsed = addressParsed.replaceAll("&gt;",">");
		return addressParsed;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public static String getMessageEnvelope( Message message, String content, String headerId ) throws MessagingException {
		StringBuffer sb = new StringBuffer();
		Locale locale = AonUtil.getCurrentLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(AonConstants.RESOURCE_BUNDLE, locale);	
		sb.append( "<br/>" );
		if ( headerId != null ) {
			sb.append( "<BLOCKQUOTE style='PADDING-RIGHT: 0px; PADDING-LEFT: 10px; MARGIN-LEFT: 5px; BORDER-LEFT: #000000 2px solid; MARGIN-RIGHT: 0px'>" );
		}
		sb.append("<font face='arial' size='2' >");
		if ( headerId != null ) {
			sb.append("----------").append( bundle.getString(headerId) ).append("----------");
		}
		sb.append( "<DIV style='BACKGROUND: #e4e4e4'>" );
		String from = getSender(message);
		sb.append( "<b>" ).append(bundle.getString("aon_webmail_from")).append(":</b> ").append(from).append( "</DIV>" );
		sb.append( "<b>" ).append(bundle.getString("aon_webmail_date")).append(":</b> ").append( message.getSentDate() );
		String cc = getRecipientsCc(message);
		if (! StringUtils.isEmpty(cc) ) {
			sb.append( "<br/><b>" ).append(bundle.getString("aon_webmail_cc")).append(":</b> ").append( cc );
		}
		String subject = getDisplaySubject(message);
		sb.append( "<br/><b>" ).append(bundle.getString("aon_webmail_subject")).append(":</b> ").append( subject );
   		sb.append( "</font><br/><br/>" );
   		sb.append( content );
		if ( headerId != null ) {
			sb.append( "</BLOCKQUOTE><br/>" );
		}
		return sb.toString();
	}
		
}
