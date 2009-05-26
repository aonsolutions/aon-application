package com.code.aon.ui.messaging.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.xml.soap.SOAPException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.messaging.MessageContent;
import com.code.aon.messaging.sms.Message;
import com.code.aon.messaging.sms.SMSException;
import com.code.aon.messaging.sms.Sender;
import com.code.aon.messaging.sms.SynchronizedSender;
import com.code.aon.messaging.util.Utils;
import com.code.aon.ui.messaging.PriceTariff;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.Contact;

public class SMSController implements Serializable {

	private static final long serialVersionUID = -5534264216750579958L;
	private static final Logger LOGGER = Logger.getLogger( SMSController.class.getName() );
	
	public static final String AON_SMS_APPLICATION = "aon-sms";
	
    private static final String EXTENDED_CHARACTERS = "^{}\\[]|";
	
	private static final String SMS_CONTACT_MANAGED_BEAN = "smsContact";
	
	private static final String SMS_BUNDLE = "smsBundle";
	
    private static final String USER_MESSAGES = "SELECT count(*) FROM Message as msg " +
    	"WHERE msg.username = :username AND msg.sentDate BETWEEN :fromDate AND :toDate";
    
    private static final String COMPANY_MESSAGES = "SELECT count(*) FROM Message as msg " +
    	"WHERE msg.sentDate BETWEEN :fromDate AND :toDate";

    private ResourceBundle bundle;
    
	private boolean showWindow;
	private boolean allowSending;

	/** Message attributes. */
	private Sender sender;
	private List<String> recipients;
	private String recipient;
	private Message message;
	private int selected;

	/** User info. */
	private Month month;
	private Integer year;
	private String username;
	private String domainName;
	private String organization;
    private List<PriceTariff> priceList;
	private Long userTotalSentMessages;
	private Long companyTotalSentMessages;
	private Double companyMessageUnitPrice;
	private Double companyTotalConsume;

	private boolean allowUpdateRecipients;
	private boolean showToolbar;
	private boolean showDemoMessage;
	
	@SuppressWarnings("unchecked")
	public SMSController() {
		this.allowUpdateRecipients = true;
		this.showToolbar = true;
		this.bundle = AonUtil.getResourceBundle(SMS_BUNDLE);
		loadPriceTariff();
		try {
			this.message = new Message();
			this.sender = new Sender();
		} catch (IOException e) {
			LOGGER.severe( e.getMessage() );
		} catch (SOAPException e) {
			LOGGER.severe( e.getMessage() );
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
	}

	public boolean isAllowSending() {
		return allowSending;
	}

	public void setAllowSending(boolean allowSending) {
		this.allowSending = allowSending;
	}
	
	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public List<PriceTariff> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<PriceTariff> priceList) {
		this.priceList = priceList;
	}

	public Long getUserTotalSentMessages() {
		return userTotalSentMessages;
	}

	public Long getCompanyTotalSentMessages() {
		return companyTotalSentMessages;
	}

	public Double getCompanyMessageUnitPrice() {
		return companyMessageUnitPrice;
	}

	public Double getCompanyTotalConsume() {
		return companyTotalConsume;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
	
	public int getCharacterCount() {
		int count = 0;
		String text = this.message.getInfo().getMessage();
		if ( text != null ) {
			count = text.length();
		    for (int i = 0; i < text.length(); i++) { 
			    if (EXTENDED_CHARACTERS.indexOf(text.charAt(i)) != -1) { 
				    count++ ;
			    }
		    }
		}
		return count;
	}
	
	public String getCharacterCountMessage() {
		int count = getCharacterCount();
		if ( count == 1 ) {
			return bundle.getString("sms_message_size_one");
		} else {
			String pattern = bundle.getString("sms_message_size_many"); 
			return MessageFormat.format(pattern, count);
		}
	}

	public void checkMessageLength() {
		if ( getCharacterCount() > 160 ) {
			String message = bundle.getString("sms_message_size_limit");
			AonUtil.addErrorMessage( message );
			throw new AbortProcessingException( message );							
		}
	}
	
	public void openContacts(ActionEvent event){
		SMSContactController smscc = (SMSContactController) AonUtil.getRegisteredBean( SMS_CONTACT_MANAGED_BEAN );
		smscc.init();
		setShowWindow(true);
	}

	public void add2List(ActionEvent event) {
		if (! StringUtils.isEmpty(this.recipient) ) {
			this.message.add( this.recipient );
			this.recipients.add( this.recipient );
			this.recipient = null;
		}
	}

	public void removeFromList(ActionEvent event) {
		this.recipients.remove( this.selected );
		this.message.remove( this.selected );
	}

	public void sendMessage(ActionEvent event) {
		// if the recipient is not null, then adds to the recipients list.
		add2List( event );
		if ( this.recipients.size() > 0 ) {
			checkMessageLength();
			try {
				sendMessage( this.message );
				reset(event);
			} catch ( Throwable e ) {
				LOGGER.severe(">>>> sendMessage " + e.getMessage());
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);				
			}
		} else {
			String message = bundle.getString("sms_empty_recipient_error");
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException( message );
		}
	}
	
	public void sendMessage( Message message ) throws SOAPException, SMSException {
		updateDomain();
		SynchronizedSender synSender = new SynchronizedSender( sender, AonUtil.getCurrentLocale() );
		Message sentMessage = synSender.send( message );
		messageSent(sentMessage);
	}

	public void reset(ActionEvent event) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		setMonth( Month.getMonthByValue(calendar.get(Calendar.MONTH)) );
		setYear( calendar.get(Calendar.YEAR) );
		this.recipients = new ArrayList<String>();
		this.recipient = null;
		this.message.init();
		this.selected = -1;
		if (! StringUtils.isEmpty(organization) ) {
			this.message.getInfo().setOrganization( organization );
			this.message.getInfo().setOriginator( organization );			
		}
		refreshSummaryData();
	}

	public void accept(ActionEvent event) {
		SMSContactController bean = (SMSContactController) AonUtil.getRegisteredBean( SMS_CONTACT_MANAGED_BEAN );
		List<Contact> lst = bean.getSelectedRows();
	    for (int i = 0, max = lst.size(); i < max; i++) {
	    	Contact e = lst.get(i);
	    	if ( e.getCellularPhone() != null ) {
	    		String r = Utils.parsePhoneNumber( e.getCellularPhone() );
	    		this.message.add( r );
				this.recipients.add( e.getDisplayName() + "-" + r );
	    	}
		}
	}

	public void summary(ActionEvent event) {
		refreshSummaryData();
	}
	
	public void refreshSummaryData() {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName();
        Session session = HibernateUtil.getSession(sessionFactoryName);
        Query query = session.createQuery( USER_MESSAGES );
        query.setString( "username", username );
        Calendar fromDate = Calendar.getInstance();
        fromDate.set( Calendar.DATE, 1 );
        fromDate.set( Calendar.MONTH, this.month.getValue() );
        fromDate.set( Calendar.YEAR, this.year );
        Calendar toDate = Calendar.getInstance();
        toDate.set( Calendar.DATE, 1 );
        toDate.set( Calendar.MONTH, this.month.getValue() + 1 );
        toDate.set( Calendar.YEAR, this.year );
        query.setDate( "fromDate", fromDate.getTime() );
        query.setDate( "toDate", toDate.getTime() );
        this.userTotalSentMessages = (Long) query.uniqueResult();
        query = session.createQuery( COMPANY_MESSAGES );
        query.setDate( "fromDate", fromDate.getTime() );
        query.setDate( "toDate", toDate.getTime() );
        this.companyTotalSentMessages = (Long) query.uniqueResult();
        Iterator<PriceTariff> iter = priceList.iterator();
        boolean greaterThan = true;
        while (iter.hasNext() && greaterThan) {
        	PriceTariff elem = iter.next();
			this.companyMessageUnitPrice = elem.getPrice();
			if ( this.companyTotalSentMessages < elem.getNumber() ) {
				greaterThan = false;
			}
		}
        long msgNumber = this.companyTotalSentMessages - ( (PriceTariff) priceList.get(0) ).getNumber() + 1;
        if ( msgNumber <= 0 ) msgNumber = msgNumber * -1;
		this.companyTotalConsume = this.companyMessageUnitPrice * msgNumber;
	}

	public void messageSent( Message sentMessage ) {
		MessageContent mc = new MessageContent();
		mc.setContent( sentMessage.getInfo().getMessage() );
		LOGGER.info( sentMessage.getInfo().getMessage() );
		try {
			IManagerBean bean = BeanManager.getManagerBean( com.code.aon.messaging.Message.class );
			Iterator<Message.Recipient> iter = sentMessage.getRecipients().iterator();
			while (iter.hasNext()) {
				Message.Recipient elem = iter.next();
				com.code.aon.messaging.Message log = new com.code.aon.messaging.Message();
				log.setMessageId( elem.getId() );
				log.setContent( mc );
				log.setMessageParts( 1 );
				log.setRecipient( elem.getName() );
				log.setSentDate( sentMessage.getInfo().getSentat().getTime() );
				log.setType( "0" );
				log.setUsername( username );
				bean.insert( log );
			}
		} catch (ManagerBeanException e) {
			LOGGER.severe( e.getMessage() );
		}
	}

	private void loadPriceTariff() {
		priceList = new ArrayList<PriceTariff>();
		priceList.add( new PriceTariff( "<", 6, 0d, "Promocion Lanzamiento. Gratis" ) );
		priceList.add( new PriceTariff( "<", 100, 0.14d, "\u20AC + IVA" ) );
		priceList.add( new PriceTariff( "<", 500, 0.13d, "\u20AC + IVA" ) );
		priceList.add( new PriceTariff( "<", 1000, 0.12d, "\u20AC + IVA" ) );
		priceList.add( new PriceTariff( "<", 2000, 0.11d, "\u20AC + IVA" ) );
		priceList.add( new PriceTariff( "<", 5000, 0.10d, "\u20AC + IVA" ) );
		priceList.add( new PriceTariff( ">=", 5000, 0.09d, "\u20AC + IVA" ) );
	}

	public void updateDomain() throws SOAPException {
		if ( this.domainName == null ) {
			this.sender.init();
		} else {
			this.sender.init( this.domainName );
		}
	}

	public boolean isAllowUpdateRecipients() {
		return allowUpdateRecipients;
	}

	public void setAllowUpdateRecipients(boolean allowUpdateRecipients) {
		this.allowUpdateRecipients = allowUpdateRecipients;
	}

	public boolean isShowToolbar() {
		return showToolbar;
	}

	public void setShowToolbar(boolean showToolbar) {
		this.showToolbar = showToolbar;
	}

	public boolean isShowDemoMessage() {
		return showDemoMessage;
	}

	public void setShowDemoMessage(boolean showDemoMessage) {
		this.showDemoMessage = showDemoMessage;
	}
	
}