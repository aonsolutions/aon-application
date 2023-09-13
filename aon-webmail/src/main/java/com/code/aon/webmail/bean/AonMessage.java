package com.code.aon.webmail.bean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Flags;
import jakarta.mail.Flags.Flag;
import jakarta.mail.IllegalWriteException;
import jakarta.mail.Message;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import jakarta.mail.search.SearchTerm;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.WebmailUtil;

public class AonMessage implements IMimeType {

	private static final String CAN_NOT_RECOVER_ADDRESS = "Can not recover address";

	private static final Logger LOGGER = LoggerFactory.getLogger(AonMessage.class);

	private static final DateFormat TODAY_FORMAT = new SimpleDateFormat("hh:mm a");
	
	private static final DateFormat THIS_YEAR_FORMAT = new SimpleDateFormat("MMM d, EEE");
	
	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.SHORT);
	
	private static final DateFormat DATE_TIME_FORMAT = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM);
	
	private AonFolder parent;
	
	protected Message message;

	private Flags previousMessageFlag;

	private Flags currentMessageFlag;

	private boolean selected;
	
	private List<BodyPart> inlines;
	
	private List<AonAttachment> attachments;
	
	private Boolean attachment;

	public AonMessage( Message message ) {
		this.message = message;
		try {
			previousMessageFlag = message.getFlags();
			currentMessageFlag = message.getFlags();
		} catch (MessagingException e) {
			LOGGER.error("Error getting message flags", e);
		}		
	}
	
	/**
	 * Gets the MimeMessage that is wrapped by this class.
	 * 
	 * @return a mime message.
	 */
	public Message getMessage() {
		return message;
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
			if (addr.getAddress() != null) {
				email = getDisplayAddressFull(addr);
			}
		} catch (AddressException e) {
			LOGGER.error("Invalid sender", e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Invalid sender, could not par internet address", e);
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
			LOGGER.error("Error getting message send date", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Gets the send date of this message. If the message has not yet been send
	 * then the current date is returned.
	 * 
	 * @return messages sent date if set, todays date if the message has no date
	 *         value.
	 * @throws WebmailException 
	 */
	public Date getDate() throws WebmailException {
		Date date = null;
		try {
			date = message.getSentDate();
			if ( date == null ) {
				date = message.getReceivedDate();
			}
		} catch (MessagingException e) {
			LOGGER.error("Error getting message send date", e);
			throw new WebmailException(e);
		}
		return date;		
	}	
	
	/**
	* Returns the date the message was sent (or received if the sent date
	* is null.
	 * @throws WebmailException 
	*/
	public String getSentDateString() throws WebmailException{
		String value = "";
		try {
			Date date = getDate();
			if ( date != null ) {
				Date today = new Date();
				if ( DateUtils.isSameDay(date, today) ) {
					value = TODAY_FORMAT.format(date);
				} else {
			        Calendar calendar = Calendar.getInstance();
			        calendar.setTime(date);
			        int year = calendar.get(Calendar.YEAR);
			        calendar.setTime(today);
			        if ( year == calendar.get(Calendar.YEAR) ) {
			        	value = THIS_YEAR_FORMAT.format(date);
			        } else {
			        	value = DATE_FORMAT.format(date);
			        }
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error getting send date", e);
			throw new WebmailException(e);
		}
		return value;
	}

	/**
	* Returns the date the message was sent (or received if the sent date
	* is null.
	 * @throws WebmailException 
	*/
	public String getSentDateFullString() throws WebmailException{
		String value = "";
		try {
			Date date = getDate();
			if ( date != null ) {
				value = DATE_TIME_FORMAT.format(date);
			}
		} catch (Exception e) {
			LOGGER.error("Error getting send date", e);
			throw new WebmailException(e);
		}
		return value;
	}
	
	/**
	 * Gets the message subject.
	 * 
	 * @return message subject. If null, an empty string is returned.
	 * @throws WebmailException 
	 */
	public String getSubject() throws WebmailException {
		try {
			String subject = message.getSubject();
			return AonMessageUtils.decodeText(subject);
		} catch (MessagingException e) {
			LOGGER.error("Error getting message subject", e);
			throw new WebmailException(e);
		}
	}
	
	public String getDisplaySubject() throws WebmailException {
		try {
			return getDisplaySubject(message);
		} catch (MessagingException e) {
			LOGGER.error("Error getting display subject", e);
			throw new WebmailException(e);
		}		
	}

	public static String getDisplaySubject( Message message ) throws MessagingException {
		String subject = message.getSubject();
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
			LOGGER.error("Error setting subject", e);
			throw new WebmailException(e);
		}
	}

	private static String getSender( Message message ) throws MessagingException {
		Address[] addresses = message.getFrom();
		if (addresses!=null && addresses.length>0){
			InternetAddress tmpAddress = (InternetAddress) addresses[0];
			String sender = getDisplayAddressFull(tmpAddress);
			return sender;
		}
		return "";
	}
	
	public String getSender() throws WebmailException {
		try {
			return getSender( message );
		} catch (MessagingException e) {
			LOGGER.error(CAN_NOT_RECOVER_ADDRESS,e);
			throw new WebmailException(e);
		}
	}

	public void setSender(String from) throws WebmailException {
		try {
			InternetAddress address = InternetAddress.parse(from, true)[0];
			setSender(address);
		} catch (MessagingException e) {
			LOGGER.error("Could decode from string, maynot be in RFC822 format", e);
			throw new WebmailException(e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Invalid sender, could not par internet address", e);
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
				LOGGER.error("Could not set from address, read only message", e);
				throw new WebmailException(e);
			} catch (MessagingException e) {
				LOGGER.error("Could decode from string, maynot be in RFC822 format",e);
				throw new WebmailException(e);
			}
		} else {
			LOGGER.info("Null message could not set sender address");
		}
	}

	public String getSenderSummary() throws WebmailException {
		Address sender = getSenderAddress();
		if (sender != null) {
			return getDisplayAddressShort( sender );
		}
		return "";		
	}

	public Address getSenderAddress() throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getFrom();
			if (! ArrayUtils.isEmpty(addresses)) {
				return addresses[0];
			}
		} catch (MessagingException e) {
			LOGGER.error("Can not recover address.",e);
			throw new WebmailException(e);
		}
		return null;
	}	
	
	public String getSenderEmail() throws WebmailException {
		Address sender = getSenderAddress();
		if (sender != null) {
			return getDisplayEmail( sender );
		}
		return "";
	}

	public void setRecipients(String recipients, RecipientType type) throws WebmailException {
		try {
			String value = StringUtils.replace(recipients, ";", AonMessageUtils.EMAIL_SEPARATOR);
			InternetAddress[] addresses = InternetAddress.parse(value, true);
			for( InternetAddress address : addresses ) {
				String personal = address.getPersonal();
				if (! StringUtils.isEmpty(personal) ) {
					address.setPersonal(MimeUtility.encodeText(personal));
				}
			}
			setRecipients(addresses, type);
		} catch (MessagingException e) {
			LOGGER.error("Could decode from string, maynot be in RFC822 format", e);
			throw new WebmailException(e);
		} catch (IndexOutOfBoundsException e) {
			LOGGER.error("Invalid sender, could not par internet address", e);
			throw new WebmailException(e);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error enconding addresses " + recipients, e);
			throw new WebmailException(e);
		}
	}

	public void setRecipientsTo(String addresses) throws WebmailException {
		setRecipients(addresses, MimeMessage.RecipientType.TO);
	}
	
	private static String getRecipientsTo( Message message ) throws MessagingException {
		return getRecipient(message, MimeMessage.RecipientType.TO);
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
			return getRecipientsTo(message);
		} catch (MessagingException e) {
			LOGGER.error("Error getting message recepients ", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Gets the recipients specifiedy by the "TO" header.
	 * 
	 * @return Address[] representing all the addresses that make up the "To"
	 *         header
	 * @throws WebmailException 
	 */
	public Address[] getRecipientsToAddress() throws WebmailException {
		try {
			return getRecipientAddress(message, MimeMessage.RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.error("Error getting message recepients ", e);
			throw new WebmailException(e);
		}
	}
	
	public String getRecipientsToSummary() throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getRecipients(RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.error("Can not recover address.",e);
			throw new WebmailException(e);
		}
		StringBuffer addressBuffer = new StringBuffer();
		if (! ArrayUtils.isEmpty(addresses)) {
			for (int i = 0; i < addresses.length; i++) {
				addressBuffer.append(getDisplayAddressShort(addresses[i]));
				if ( i+1 < addresses.length ) {
					addressBuffer.append(AonMessageUtils.EMAIL_SEPARATOR);
				}
			}
		}
		return addressBuffer.toString();
	}
	
	public String getRecipientsToEmail() throws WebmailException {
		Address[] addresses = null;
		try {
			addresses = message.getRecipients(RecipientType.TO);
		} catch (MessagingException e) {
			LOGGER.error("Can not recover address.",e);
			throw new WebmailException(e);
		}
		StringBuffer addressBuffer = new StringBuffer();
		if (! ArrayUtils.isEmpty(addresses)) {
			for (int i = 0; i < addresses.length; i++) {
				addressBuffer.append(getDisplayEmail(addresses[i]));
				if ( i+1 < addresses.length ) {
					addressBuffer.append(AonMessageUtils.EMAIL_SEPARATOR);
				}
			}
		}
		return addressBuffer.toString();
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
			LOGGER.error("Error getting message Cc recepients ", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Gets the recipients specifiedy by the "CC" header.
	 * 
	 * @return Address[] representing all the addresses that make up the "CC"
	 *         header
	 * @throws WebmailException 
	 */
	public Address[] getRecipientsCcAddress() throws WebmailException {
		try {
			return getRecipientAddress(message, MimeMessage.RecipientType.CC);
		} catch (MessagingException e) {
			LOGGER.error("Error getting message Cc recepients ", e);
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
			LOGGER.error("Error getting message Bcc recepients ", e);
			throw new WebmailException(e);
		}
	}

	/**
	 * Gets the recipients specifiedy by the "BCC" header.
	 * 
	 * @return Address[] representing all the addresses that make up the "BCC"
	 *         header
	 * @throws WebmailException 
	 */
	public Address[] getRecipientsBccAddress() throws WebmailException {
		try {
			return getRecipientAddress(message, MimeMessage.RecipientType.BCC);
		} catch (MessagingException e) {
			LOGGER.error("Error getting message Bcc recepients ", e);
			throw new WebmailException(e);
		}
	}	
	/**
	 * Utility method getting message recipient.
	 * 
	 * @param type
	 *            recipient type, valid values are constants in
	 *            jakarta.mail.Message.RecipientType
	 * @return String representing the specified recipient. Empty string if no
	 *         such reciepient is found.
	 * @throws MessagingException 
	 * @throws WebmailException 
	 */
	private static String getRecipient(Message message, RecipientType type) throws MessagingException {
		StringBuffer recipients = new StringBuffer();
		Address[] addresses = getRecipientAddress(message, type);
		if (addresses != null) {
			// Write out the addres in the order they are found.
			for (int i = 0; i < addresses.length; i++) {
				recipients.append(getDisplayAddressFull(addresses[i]) + AonMessageUtils.EMAIL_SEPARATOR);
			}
			return recipients.substring(0, recipients.length() - 1).toString();
		}
		return "";
	}

	private static Address[] getRecipientAddress(Message message, RecipientType type) throws MessagingException {
		Address[] addresses = message.getRecipients(type);
		if (ArrayUtils.isEmpty(addresses)) {
			return null;
		}
		return addresses;
	}
	
	public List<Address> getAllRecipients() throws WebmailException {
		List<Address> addresses = new LinkedList<Address>();
		Address[] to = getRecipientsToAddress();
		if (! ArrayUtils.isEmpty(to) ) {
			addresses.addAll(Arrays.asList(to));
		}
		Address[] cc = getRecipientsCcAddress();
		if (! ArrayUtils.isEmpty(cc) ) {
			addresses.addAll(Arrays.asList(cc));
		}
		Address[] bcc = getRecipientsBccAddress();
		if (! ArrayUtils.isEmpty(bcc) ) {
			addresses.addAll(Arrays.asList(bcc));
		}
		return addresses;
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
	 *            jakarta.mail.Message.RecipientType
	 * @throws WebmailException 
	 */
	protected void setRecipients(Address[] address, final RecipientType type) throws WebmailException {
		try {
			message.setRecipients(type, address);
		} catch (IllegalWriteException e) {
			LOGGER.error("Could not set recipients address, read only message", e);
			throw new WebmailException(e);
		} catch (MessagingException e) {
			LOGGER.error("Error setting message To recepients ", e);
			throw new WebmailException(e);
		}
	}

	//**************************************************************************
	//**************************************************************************
	//ATTACHMENTS
	//**************************************************************************
	//**************************************************************************
	private boolean isAttachment( Part part ) throws MessagingException {
		String disposition = part.getDisposition();
		if ( StringUtils.endsWithIgnoreCase(disposition, Part.ATTACHMENT) ) {
			return true;
		}
		if (part.getFileName() != null) {
			String contentId = WebmailUtil.getContentId(part);
			return StringUtils.isEmpty(contentId); 
		}
		return false;
	}

	private boolean isInline( Part part ) throws MessagingException {
		if ( part.isMimeType(IMAGE_ANY) || part.isMimeType(TEXT_ANY) ) {
			String contentId = WebmailUtil.getContentId(part);
			if (! StringUtils.isEmpty(contentId) ) {
				LOGGER.info( "Inline file name: " + part.getFileName() );
				return true;
			}
		}
		return false;
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
	
	public List<BodyPart> getInlines() {
		if ( this.inlines == null ) {
			initFilesIncluded();
		}
		return this.inlines;
	}

	public List<AonAttachment> getAttachements() {
		if ( this.attachments == null ) {
			initFilesIncluded();
		}
		return this.attachments;
	}

	private void initFilesIncluded() {
		this.inlines = new LinkedList<BodyPart>();
		this.attachments = new LinkedList<AonAttachment>();
		initFilesIncluded( message );
	}
	
	private void initFilesIncluded( Part part ) {
		try {
			if ( part.isMimeType(MULTIPART_ANY) ) {
				Multipart mp = (Multipart) part.getContent();
				int numPart = mp.getCount();
				for (int i = 0; i < numPart; i++) {
					BodyPart bodyPart = mp.getBodyPart(i);
					if ( isInline(bodyPart) ) {
						this.inlines.add( bodyPart );
					} else if ( isAttachment(bodyPart) ) {
						AonAttachment attach = new AonAttachment( bodyPart, this.attachments.size() );
						this.attachments.add( attach );
					}
					initFilesIncluded( bodyPart );
				}			
			}
		} catch ( Throwable e ) {
			LOGGER.error("Error on init attachemnts and inlines", e);
		}
	}
	
	/**
	* Method for checking if the message has attachments.
	*/
	public boolean isAttachment() throws WebmailException {
		if ( this.attachment == null ) {
			try {
				this.attachment = hasAttachments( message );
			} catch (Exception ex) {
				LOGGER.error("Error determining if message has attachement", ex);
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
	 * Sets the message content as plain/text from the given form text area.
	 * This could be update for differnet content types.
	 * @throws WebmailException 
	 */
	public void setContent(MimeMultipart content) throws WebmailException {
		try {
			message.setContent(content);
		} catch (MessagingException e) {
			LOGGER.error("Message content could not be set, readonly state", e);
			throw new WebmailException(e);
		}

	}

	public void setContent(String content) throws WebmailException {
		try {
			message.setText(content);
		} catch (MessagingException e) {
			LOGGER.error("Message content could not be set", e);
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
			LOGGER.error("Problem while deleting", e);
			throw new WebmailException(e);
		}
	}
	
    public boolean isReaded() throws WebmailException{
    	try {
			return message.isSet(Flag.SEEN);
		} catch (MessagingException e) {
			LOGGER.error("Problem while reading message flags", e);
			throw new WebmailException(e);
		}
    }

    public boolean isAnswered() throws WebmailException{
    	try {
			return message.isSet(Flag.ANSWERED);
		} catch (MessagingException e) {
			LOGGER.error("Problem while reading message flags", e);
			throw new WebmailException(e);
		}
    }

	public void setSentDate(Date date) throws WebmailException {
		try {
			message.setSentDate(date);
		} catch (MessagingException e) {
			LOGGER.error("Problem while setting date", e);
			throw new WebmailException(e);
		}
	}

	public boolean match(SearchTerm term) throws WebmailException{
		try {
			return message.match(term);
		} catch (MessagingException e) {
			LOGGER.error("Problem while searching", e);
			throw new WebmailException(e);
		}
	}
	
	private static String getDisplayAddressShort(Address a) {
		String pers = null;
		String addr = null;
		if (a instanceof InternetAddress
				&& (pers = ((InternetAddress)a).getPersonal())!=null) {
			pers = AonMessageUtils.parse_email(pers);
			addr = pers;
		} else {
			addr = a.toString();
		}
		return addr;
	}
	
	public static String getEditAddress(Address a) {
		String address = null;
		if ( a instanceof InternetAddress ) {
			InternetAddress ia = (InternetAddress) a;
			String per = ia.getPersonal();
			if ( !StringUtils.isEmpty(per) && StringUtils.contains(per, "\"") ) {
				try {
					ia.setPersonal( StringUtils.remove(per, '"') );
				} catch (UnsupportedEncodingException e) {
					LOGGER.error( "Error cleaning personal in " + a, e);
				}
			}
			address = ia.toUnicodeString();
		} else {
			address = ObjectUtils.toString(a);
		}
		return address;
	}	
	
	public static String getDisplayAddressFull(Address a) {
		String pers = null;
		String addr = null;
		if (a instanceof InternetAddress
				&& (pers = ((InternetAddress)a).getPersonal()) != null) {
			pers = AonMessageUtils.parse_email(pers);
			addr = pers + " " + "&lt;"+((InternetAddress)a).getAddress()+"&gt;";
		} else {
			addr = a.toString();
		}
		return addr;
	}

	public static String getDisplayEmail(Address a) {
		String addr = null;
		if (a instanceof InternetAddress) {
			addr = ""+((InternetAddress)a).getAddress();
		} else {
			addr = a.toString();
		}
		return addr;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
		
	public String getDisplaySize() throws WebmailException {
		try {
			return AonMessageUtils.getDisplaySize(message.getSize());
		} catch (MessagingException e) {
			LOGGER.error("Error getting message size", e);
			throw new WebmailException(e);
		}
	}
	
}
