package com.code.aon.ui.desktop.applications;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.xml.soap.SOAPException;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.desktop.controller.DesktopController;
import com.code.aon.desktop.controller.SMSContactController;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.MessageContent;
import com.code.aon.messaging.event.ISenderListener;
import com.code.aon.messaging.event.SenderEvent;
import com.code.aon.messaging.sms.Message;
import com.code.aon.messaging.sms.Sender;
import com.code.aon.messaging.util.Utils;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class SMSManager implements ISenderListener, Serializable, IServices {

	private static final long serialVersionUID = -5534264216750579958L;
	private static final Logger LOGGER = Logger.getLogger( SMSManager.class.getName() );
	private static final String SMS_CONTACT_MANAGED_BEAN = "smsContact";

	private ApplicationsManager.App app;
	ResourceBundle bundle;

	private boolean showWindow;
	private boolean allowSending = true;
	private boolean activePoll = false;
	private boolean dirtyMessage;

	/** Message attributes. */
	private Sender sender;
	private List<String> recipients;
	private String recipient;
	private Message message;
	private int selected;

	/** User info. */
	private Month month = Month.JUNE;
	private Integer year = 2008;
	String username;
	String domainname;
    String userMessages = "SELECT count(*) FROM Message as msg " +
		"WHERE msg.username = :username AND msg.sentDate BETWEEN :fromDate AND :toDate";
    String companyMessages = "SELECT count(*) FROM Message as msg " +
		"WHERE msg.sentDate BETWEEN :fromDate AND :toDate";
	private List<PriceTariff> priceList;
	private Long userTotalSentMessages;
	private Long companyTotalSentMessages;
	private Double companyMessageUnitPrice;
	private Double companyTotalConsume;

	@SuppressWarnings("unchecked")
	public SMSManager() {
		loadPriceTariff();
		try {
			ApplicationsManager apps = 
				(ApplicationsManager) AonUtil.getRegisteredBean( ApplicationsManager.BEAN_NAME );
			app = apps.getApplication( "aon-sms" );
			this.message = new Message();
			this.sender = new Sender();
			this.sender.addSenderListener( this );
			domainname = UserUtils.getInstance().getPrincipal().getDomain();
			smsEnabling( domainname );
			username = UserUtils.getInstance().getPrincipal().getShortName();
			reset( null );
			bundle = ResourceBundle.getBundle( "com.code.aon.desktop.i18n.messages", AonUtil.getCurrentLocale() );
		} catch (IOException e) {
			LOGGER.severe( e.getMessage() );
		} catch (SOAPException e) {
			LOGGER.severe( e.getMessage() );
		}
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

	public boolean isActivePoll() {
		boolean active = activePoll;
		if ( dirtyMessage ) {
			setActivePoll( false );
			dirtyMessage = false;
			if ( errorCode != 0 ) {
				String bundleName = "com.code.aon.messaging.i18n.messages";
				ResourceBundle bundle = ResourceBundle.getBundle( bundleName, AonUtil.getCurrentLocale() );
				AonUtil.addErrorMessage( bundle.getString( "aon_sms_esendex_" + errorCode ) );
				errorCode = 0;
			}
		}
		return active;
	}

	public void setActivePoll(boolean activePoll) {
		this.activePoll = activePoll;
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

	public void openContacts(ActionEvent event){
		SMSContactController smscc = (SMSContactController) AonUtil.getRegisteredBean( SMS_CONTACT_MANAGED_BEAN );
		smscc.init();
		setShowWindow(true);
	}

	public void add2List(ActionEvent event) {
		if ( this.recipient != null && !this.recipient.equals( "" ) ) {
			this.message.add( this.recipient );
			this.recipients.add( this.recipient );
			this.recipient = null;
		}
	}

	public void removeFromList(ActionEvent event) {
		this.recipients.remove( this.selected );
		this.message.remove( this.selected );
	}

	public void sendMessage(ActionEvent event) throws CloneNotSupportedException {
		if ( !isExecutable() ) {
			AonUtil.addInfoMessage( bundle.getString( "aon_sms_application_service_exception" ) );
			return;
		}
		setActivePoll( true );
		add2List( event ); // if the recipient is not null, then adds to the recipients list.
		if ( this.recipients.size() > 0 ) {
			sender.send( (Message) this.message.clone() );
			reset( event );
		} else {
			setActivePoll( false );
			AonUtil.addInfoMessage( bundle.getString( "aon_messaging_empty_recipient_error" ) );
		}
	}

	public void reset(ActionEvent event) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date() );
		month = Month.getMonthByValue( calendar.get( Calendar.MONTH ) );
		year = calendar.get( Calendar.YEAR );
		this.recipients = new ArrayList<String>();
		this.recipient = null;
		this.message.init();
		this.selected = -1;
		String organization;
		try {
			organization = ( (DesktopController) AonUtil.getController( "desktop" ) ).getCompanyAlias();
			this.message.getInfo().setOrganization( organization );
			this.message.getInfo().setOriginator( organization );
		} catch (ManagerBeanException e) {
			LOGGER.severe( e.getMessage() );
		}
		summary( event );
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
        Session session = HibernateUtil.getSession();
        Query query = session.createQuery( userMessages );
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
        query = session.createQuery( companyMessages );
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

// ************************************** ISenderListener methods implementation *************************************
	public void messageSent(SenderEvent event) {
		Message sentMessage = (Message) event.getSource();
		MessageContent mc = new MessageContent();
		mc.setContent( sentMessage.getInfo().getMessage() );
		LOGGER.info( sentMessage.getInfo().getMessage() );
		IManagerBean bean;
		try {
			bean = BeanManager.getManagerBean( com.code.aon.groupware.Message.class );
			Iterator<Message.Recipient> iter = sentMessage.getRecipients().iterator();
			while (iter.hasNext()) {
				Message.Recipient elem = iter.next();
				com.code.aon.groupware.Message log = new com.code.aon.groupware.Message();
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
		summary( null );
		try {
			smsEnabling( domainname );
		} catch (SOAPException e) {
			LOGGER.severe( e.getMessage() );
		}
		dirtyMessage = true;
	}

	int errorCode;
	public void messageFailed(SenderEvent event) {
		errorCode = event.getErrorCode();
		dirtyMessage = true;
	}
// ********************************** End of ISenderListener methods implementation **********************************

// ************************************** IServices methods implementation *************************************
	public boolean isExecutable() {
		if ( app == null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean( com.code.aon.groupware.Message.class );
				int sent = bean.getCount( null );
				if ( sent >= 5 )
					return false;

				return true;
			} catch (ManagerBeanException e) {
				LOGGER.severe( e.getMessage() );
				return false;
			}
		}
		return app.isExecutable();
	}

	public boolean isInfobarEnabled() {
		return app != null && app.isInfobarEnabled();
	}

	public boolean isSidebarEnabled() {
		return (app == null)? true: app.isSidebarEnabled();
	}

	public boolean isToolbarEnabled() {
		return app != null && app.isToolbarEnabled();
	}
// ********************************** End of IServices methods implementation **********************************
	
	public class PriceTariff {
		
		private String filter, currency;
		private Integer number;
		private Double price;

		PriceTariff(String filter, Integer number, Double price, String currency) {
			this.filter = filter;
			this.number = number;
			this.price = price;
			this.currency = currency;
		}

		public String getFilter() {
			return filter;
		}

		public void setFilter(String filter) {
			this.filter = filter;
		}

		public String getCurrency() {
			return currency;
		}

		public void setCurrency(String currency) {
			this.currency = currency;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
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

	private void smsEnabling(String domain) throws SOAPException {
		if ( app == null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean( com.code.aon.groupware.Message.class );
				int sent = bean.getCount( null );
				if ( sent < 6 )
					this.sender.init();
			} catch (ManagerBeanException e) {
				LOGGER.severe( e.getMessage() );
				this.sender.init();
			}
			return;
		}
		this.sender.init( domain );
	}

}