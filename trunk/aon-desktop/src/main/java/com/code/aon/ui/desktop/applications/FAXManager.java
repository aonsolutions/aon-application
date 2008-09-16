package com.code.aon.ui.desktop.applications;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.messaging.util.Utils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonFile;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.bean.AonMessageUtils;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.ui.webmail.controller.WebMailController;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.webmail.MailAccount;

public class FAXManager extends MessageController {

	private ApplicationsManager.App app;

	private boolean allowCover = false;
	/** International prefixes zones. */
	private List<SelectItem> zones; 
	private String zone = Utils.DEFAULT_ZONE;
	private boolean isDirtyZone;
	/** Country codes international prefixes. */
	private List<SelectItem> countrycodes; 
	private String countrycode = Utils.DEFAULT_COUNTRY_CODE;

	public FAXManager() {
		ApplicationsManager apps = 
			(ApplicationsManager) AonUtil.getRegisteredBean( ApplicationsManager.BEAN_NAME );
		app = apps.getApplication( "aon-fax" );
	}

	public boolean isAllowCover() {
		return allowCover;
	}

	public void setAllowCover(boolean allowCover) {
		this.allowCover = allowCover;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		isDirtyZone = ( this.zone.equals(zone) )? false: true;
		this.zone = zone;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public boolean isEnabled() {
		return ( super.getFiles().size() < 1);
	}

	@Override
	public void createNewMessage(ActionEvent event) {
		super.createNewMessage(event);
		super.setContent( "" );
		setAllowCover( false );
		setCountrycode( Utils.DEFAULT_COUNTRY_CODE );
	}

	@Override
	@SuppressWarnings("unused")
	public void send(ActionEvent event) {
		try {
	    	AonMessage aonMessage = compoundMessage();
			AonFolder dest = null;
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
	    	webMailController.getServer().sendMessage( aonMessage );
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (UnsupportedEncodingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} finally {
			createNewMessage( event );
		}
	}

// ************************************** IServices methods implementation *************************************
	public boolean isExecutable() {
		return app != null && app.isExecutable();
	}

	public boolean isInfobarEnabled() {
		return app != null && app.isInfobarEnabled();
	}

	public boolean isSidebarEnabled() {
		return app != null && app.isSidebarEnabled();
	}

	public boolean isToolbarEnabled() {
		return app != null && app.isToolbarEnabled();
	}
// ********************************** End of IServices methods implementation **********************************

	private AonMessage compoundMessage() throws ManagerBeanException, UnsupportedEncodingException, MessagingException, WebmailException {
		IManagerBean mailAccountBean = AonUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();	
		MailAccount mailAccount = (MailAccount) mailAccountBean.get( super.getSenderMailAccountId() );
		String cc = getCountrycode();
    	AonMessage aonMessage = compoundMessage(
    			Utils.FAX_MAIL_FROM, //mailAccount.getEmail(),
    			cc + super.getRecipientsTo().trim() + "@efaxsend.com",
    			mailAccount.getEmail() + "," + Utils.FAX_MAIL_FROM, //super.getRecipientsCc(), 
    			super.getRecipientsBcc(),
    			super.getSubject(), 
    			AonMessageUtils.unparse_cid( super.getContent() ),
    			null,
    			super.getFiles()
    			);
    	return aonMessage;
	}
	
	private AonMessage compoundMessage(
			String sender,
			String recipientsTo,
			String recipientsCC, 
			String recipientsBCC,
			String subject, 
			String text, 
			AonMessage parentAonMsg,
			List<AonFile> fileList) 
			throws MessagingException, WebmailException, UnsupportedEncodingException {
    	LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(BEAN_LOGGED_USER);
    	String personal = loggedUser.getLoggedUserName();
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);    	
    	AonMessage newMessage = webMailController.getServer().createAonMessage(sender, personal);
       	if (recipientsTo!=null)
       		newMessage.setRecipientsTo(recipientsTo);
       	if (recipientsCC!=null)
       		newMessage.setRecipientsCc(recipientsCC);
       	if (recipientsBCC!=null)
       		newMessage.setRecipientsBcc(recipientsBCC);
       	if (subject!=null)
       		newMessage.setSubject(subject);
       	newMessage.getMessage().setHeader("X-Mailer", "OfficeWeb - AonWebMail 1.0");

       	MimeMultipart multipart1 =new MimeMultipart("related");
       	MimeBodyPart part=new MimeBodyPart();
       	part=new MimeBodyPart();
       	part.setContent(text,"text/html");
       	multipart1.addBodyPart(part);
       	if (parentAonMsg!=null && 
       			parentAonMsg.getAmt() != null){
	       	List<BodyPart> list = parentAonMsg.getAmt().getRelateds();
	       	Iterator<BodyPart> iter = list.iterator();
	       	while (iter.hasNext()){
		       	part=new MimeBodyPart();
		       	BodyPart bp = iter.next();
		       	if (bp != null){
					part.setDataHandler(bp.getDataHandler());
					part.setContentID(bp.getHeader("Content-ID")[0]);
					part.setDisposition(Part.INLINE);
					multipart1.addBodyPart(part);
		       	}
	       	}
       	}
		if ( fileList.size() > 0 ) {
			FileDataSource fds;
			for ( int i=0; i < fileList.size(); i++ ) {
				MimeBodyPart mbpNext = new MimeBodyPart();
				fds = new FileDataSource((fileList.get(i)).getFile());
				mbpNext.setFileName(fds.getName());
				mbpNext.setDataHandler(new DataHandler(fds));
				multipart1.addBodyPart(mbpNext);
			}
			newMessage.setContent( (MimeMultipart) multipart1 );
		}       	
		newMessage.setContent(multipart1);

		newMessage.setSentDate( new Date() );
		//newMessage.getMessage().saveChanges();
		return newMessage; 
	}

	public boolean isRoleFAXSender() {
		return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("FAXSender");
	}

	public List<SelectItem> getZones() {
		if ( zones == null ) {
			ResourceBundle bundle = 
				Utils.getBundle( FacesContext.getCurrentInstance().getExternalContext().getRequestLocale() );
			zones = new ArrayList<SelectItem>();
			for (int i = 1; i < 10; i++) {
				SelectItem si = new SelectItem( i, bundle.getString( Utils.DEFAULT_ZONE_PREFIX + i ) );
				zones.add( si );
			}
		}
		return zones;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getCountrycodes() {
		if ( countrycodes == null || isDirtyZone ) {
			ResourceBundle bundle = 
				Utils.getBundle( FacesContext.getCurrentInstance().getExternalContext().getRequestLocale() );
			countrycodes = new ArrayList<SelectItem>();
			Properties cc = new Properties();
			try {
				cc.load( Utils.getCountrycodesResourceAsStream() );
				Enumeration en = cc.keys();
				while (en.hasMoreElements()) {
					String elem = (String) en.nextElement();
					String value = cc.getProperty( elem );
					if ( zone.equals( value ) ) {
						String key = Utils.DEFAULT_ZONE_PREFIX + value + "_" + elem;
						SelectItem si = new SelectItem( elem, bundle.getString( key ) );
						countrycodes.add( si );
					}
				}
				if ( zone.equals( Utils.DEFAULT_ZONE ) ) {
					countrycode = Utils.DEFAULT_COUNTRY_CODE;
				}
				Collections.sort( countrycodes, new CountryCodesComparator() );
				isDirtyZone = false;
			} catch (IOException e) {
				countrycode = Utils.DEFAULT_COUNTRY_CODE;
				zone = Utils.DEFAULT_ZONE;
				String key = Utils.DEFAULT_ZONE_PREFIX + zone + "_" + countrycode;
				SelectItem si = new SelectItem( countrycode, bundle.getString( key ) );
				countrycodes.add( si );
			}
		}
		return countrycodes;
	}

	private class CountryCodesComparator implements Comparator<SelectItem> {

		public int compare(SelectItem si1, SelectItem si2) {
			String str1 = (String) si1.getValue();
			String str2 = (String) si2.getValue();
			return str1.compareTo( str2);
		}
		
	}
}
