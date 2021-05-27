package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;
import static com.code.aon.webmail.bean.IMailConstants.IMAP;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.servlet.SendEmailServlet;
import com.code.aon.webmail.IContact;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.AonServer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MailProcessType;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class MessageController implements IWebMailConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String MESSAGE_WINDOW_INCLUDED = "com.code.aon.ui.webmail.MessageWindow";

	private final static Logger LOGGER = LoggerFactory.getLogger(MessageController.class);
	
	private String recipientsTo;
	
	private String recipientsCc;
	
	private String recipientsBcc;
	
	private String subject;
	
	private String content;

    private List<AonFile> newMsgFileList;
    
    private IMailAccount senderMailAccount;
    
    private String messageBody;
    
    private int attachRemoveIndex;
    
    private boolean showNewMessageWindow;
    
    private boolean loadContacts;
    
    private boolean appendSignature;
    
    private boolean saveSent;
    
    private Integer template;
    
    private boolean showTemplates;
    
    private List<SelectItem> templates;
    
    private List<Address> sentAddressList;
    
    private Map<String, String> variableMap;
    
    Boolean genericMessage;
    
	private MailProcessType mailProccessType;
    
	private MailConfigController getMailConfig() {
		return (MailConfigController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MAIL_CONFIG);
	}

	public void onNewMessage(ActionEvent event){
		initNewMessage();
    }
	
	public void finishMessage() {
    	setShowNewMessageWindow(false);
		content = null;
		if ( newMsgFileList != null ) {
			for( AonFile af : newMsgFileList ) {
				af.clean();
			}
	    	newMsgFileList = null;
		}
	}
    
    public List<AonFile> getFiles(){
    	return newMsgFileList;
    }

	//***************************************************************
	//*********** ATTACH ********************************************
	//***************************************************************

	public void fileUploaded(UploadEvent event) {
    	AonFile f = AttachmentUtil.fileUploaded(event);
    	addAttachment( f );
	}	
	
	public void addAttachment( AonFile aonFile ) {
    	newMsgFileList.add( aonFile );		
	}
	
	//***************************************************************
	//*********** END ATTACH ****************************************
	//***************************************************************
	
	private void storeMessage( AonServer server, AonMessage aonMessage, boolean draft ) {
		String folderName = draft ? server.getDraftFolderName() : server.getSentFolderName();
		if ( isSaveSent() && (folderName != null) && server.isIMAP() ) {
	    	Message[] messages = new Message[1];
			messages[0] = aonMessage.getMessage();
			try {
				messages[0].setFlag(Flag.SEEN, true);
				AonFolder folder = server.getAonFolder(folderName);
				folder.open(Folder.READ_WRITE);
		    	Folder desfFolder = folder.getFolder();
		    	desfFolder.appendMessages(messages);
		    	desfFolder.expunge();
		    	folder.close(false);		
			} catch (MessagingException e) {
				LOGGER.error( "Error setting contactName", e );
				AonUtil.addErrorMessage(e.getMessage());
			}
		}
	}
	
    public void onSend(ActionEvent event) {
    	AonServer server = new AonServer(this.senderMailAccount);
    	try {
    		send(server);
		} catch (Throwable th) {
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th);
		} finally {
	    	finishMessage();
		}
    }

	public void send(AonServer server) throws WebmailException {
    	AonMessage sentMessage = null;
    	try {
	    	sentMessage = compoundMessage(server);
    		server.sendMessage(sentMessage);
    		this.sentAddressList = sentMessage.getAllRecipients();
		} catch (Throwable th) {
			if ( sentMessage != null ) {
	    		storeMessage(server, sentMessage, true);  		
			}
			throw new WebmailException(th.getMessage(), th);
		}
    	try {	
   			storeMessage(server, sentMessage, false);
		} catch (Throwable th) {
			throw new WebmailException(th.getMessage(), th);
		}
    }    

    public void onCancelSend(ActionEvent event) {
    	finishMessage();
    }
    
    //*******************************************************************************************
    
    private String getPersonal( IMailAccount account ) throws UnsupportedEncodingException {
    	String personal = null;
    	if ( ! StringUtils.isEmpty(account.getDisplayName()) ) {
    		personal = account.getDisplayName();
    	} else {
    		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
        	personal = loggedUser.getLoggedUserName();    	    		
    	}
    	return MimeUtility.encodeText(personal);
    }
    
	/**
	* Method for compounding the message.
	 * @throws WebmailException 
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	*/
	public AonMessage compoundMessage( AonServer server) throws WebmailException, MessagingException, UnsupportedEncodingException {
		String personal = getPersonal( senderMailAccount );
		Address from = new InternetAddress(senderMailAccount.getEmail(), personal);
    	AonMessage newMessage = server.createAonMessage( from );
    	if (! StringUtils.isEmpty(senderMailAccount.getReplyToMail())) {
    		Address replyTo = new InternetAddress(senderMailAccount.getReplyToMail(), personal);
    		newMessage.getMessage().setReplyTo(new Address[]{replyTo});
    	}
       	if (! StringUtils.isEmpty(recipientsTo)) {
       		newMessage.setRecipientsTo(recipientsTo);
       	}
       	if (! StringUtils.isEmpty(recipientsCc)) {       	
       		newMessage.setRecipientsCc(recipientsCc);
       	}
       	if (! StringUtils.isEmpty(recipientsBcc)) {       	
       		newMessage.setRecipientsBcc(recipientsBcc);
       	}
       	if (! StringUtils.isEmpty(subject)) {       	
       		newMessage.setSubject(subject);
       	}
       	newMessage.getMessage().setHeader("X-Mailer", WEBMAIL_HEADER_NAME);

       	MimeMultipart mainPart = new MimeMultipart();
       	MimeBodyPart part = new MimeBodyPart();
       	String text = AonMessageUtils.unparse_cid(content);
       	part.setContent( text, MimeType.MIME_HTML.getName() );
       	mainPart.addBodyPart(part);
		for ( AonFile file : newMsgFileList ) {
			BodyPart bodyPart = WebmailUtil.getBodyPart(file);
			mainPart.addBodyPart(bodyPart);
		}       	
		newMessage.setContent(mainPart);
		newMessage.setSentDate( new Date() );
		return newMessage; 
	}
	
	public void initNewMessage(){
		saveSent = true;
		appendSignature = true;
		senderMailAccount = getMailConfig().getDefaultMailAccount(true);
		recipientsTo = null;
		recipientsCc = null;
		recipientsBcc = null;
		subject = null;		
		messageBody = null;
		updateContent(senderMailAccount);
    	newMsgFileList = new ArrayList<AonFile>();
    	sentAddressList = null;
		loadContacts = true;
		template = null;
		showTemplates = true;
		variableMap = new HashMap<>();
		genericMessage = true;
		templates = new LinkedList<SelectItem>();
	}

	public void initNewMsgFileList(){
		newMsgFileList = new ArrayList<AonFile>();
	}
	
	/**
	 * @return the recipientsTo
	 */
	public String getRecipientsTo() {
		return recipientsTo;
	}

	/**
	 * @param recipientsTo the recipientsTo to set
	 */
	public void setRecipientsTo(String recipientsTo) {
		this.recipientsTo = recipientsTo;
	}

	/**
	 * @return the recipientsCc
	 */
	public String getRecipientsCc() {
		return recipientsCc;
	}

	/**
	 * @param recipientsCc the recipientsCc to set
	 */
	public void setRecipientsCc(String recipientsCc) {
		this.recipientsCc = recipientsCc;
	}

	/**
	 * @return the recipientsBcc
	 */
	public String getRecipientsBcc() {
		return recipientsBcc;
	}

	/**
	 * @param recipientsBcc the recipientsBcc to set
	 */
	public void setRecipientsBcc(String recipientsBcc) {
		this.recipientsBcc = recipientsBcc;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = velocity(subject);
	}

    public String getContent(){
    	return content;
    }

    public void setContent(String content){
    	this.content = content;
    }
    
    public void updateMessageBody(String messageBody) {
    	this.messageBody = messageBody;
    	updateContent( senderMailAccount );    	
    }

    public Map<String, String> getVariableMap(){
    	return variableMap;
    }

    public void setVariableMap(Map<String, String> variableMap){
    	this.variableMap = variableMap;
    }
    
	private String velocity(String text)  {
		if(getVariableMap() != null) {
			VelocityContext context = new VelocityContext();
			for( Map.Entry<String,String> entry : getVariableMap().entrySet() ) {
				context.put(entry.getKey(), entry.getValue());
			}
			StringWriter out = new StringWriter();
			
			try {
				Velocity.evaluate( context, out, "template text", text);
			} catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}	
			return out.toString();
		}
		return text;
	}
	
	public void onTemplateChanged(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null) {
			Integer id = (Integer) event.getNewValue();
			updateTemplateBody(id);
		} else updateMessageBody(" ");
	}	
	
	public void updateTemplateBody(Integer templateId) {
		MailTemplate template = AON.getMailTemplate(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "", 
				f -> f.getIdProperty().eq(templateId));
		updateMessageBody(getMessageBody(template));
		setSubject(template.getSubject());
	}
	
	public String getMessageBody(MailTemplate template) {
		StringBuffer sb = new StringBuffer();
		addHeader( template, sb );
		addFooter( template, sb );
		return sb.toString();				
	}
	
	public void addHeader( MailTemplate template, StringBuffer sb ) {
		boolean nullTemplate = template==null || template.getId()==null;
		sb.append("<div style=\"text-align: center;");
		if (! (nullTemplate || StringUtils.isEmpty(template.getBackgroundColor())) ) {
			sb.append("background-color:");
			sb.append(template.getBackgroundColor());	
		}
		sb.append("\">");
		sb.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"margin: 0 auto");
		if ( !nullTemplate && !StringUtils.isEmpty(template.getWidth()) ) {
			sb.append(";width: ").append(template.getWidth());			
		}		
		sb.append(";\">");
		sb.append("<tbody><tr><td align=\"center\">");		
		if (!nullTemplate && template.getHeaderTemplate() != null) {
			addTemplate(sb, template, template.getHeaderTemplate());
		}		
	}
	
	public void addFooter(MailTemplate template, StringBuffer sb ) {
		if (template!=null && template.getId()!=null && template.getFooterTemplate() != null) {
			addTemplate(sb, template, template.getFooterTemplate());
		}
		sb.append("</td></tr></tbody></table></div>");		
	}
		
	private void addTemplate( StringBuffer sb, MailTemplate template, Integer attachId) {
		Attach attach = AON.getAttach(AonUtil.getDomainName(), template.getDomain(), "", 
				f -> f.getIdProperty().eq(attachId), AttachType.REGISTRY);
		if(attach.getData() == null) {
			DomainGserviceaccount d = AON.getDomainGserviceaccount(AonUtil.getDomainName(), template.getDomain(), "");
			Drive drive = AonDrive.getInstace().serviceInitialize(d);
			attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		}
		if(attach.getData() != null) {
			sb.append(new String(attach.getData()));
		}	
	}
	
	public Boolean initMessageController(Domain domain, String login, Map<String, String> map, String type) {
		setVariableMap(map);
		ApplicationParameter mp = AON.getApplicationParameter(domain.getName(), domain.getId(), login, type);
		if(mp != null && mp.getId() != null) {
			String[] ids = mp.getValue().split(" ");
			MailAccount ma = AON.getMailAccount(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(Integer.parseInt(ids[0])));
			IMailAccount mailAccount = (IMailAccount) SendEmailServlet.getInstance().getMa2(ma);
			setSenderMailAccount(mailAccount);
			setTemplate(Integer.parseInt(ids[1]));
			updateTemplateBody(Integer.parseInt(ids[1]));
		}
		return mp != null && mp.getId() != null;
	}
	
	//********************************************************************************************
	// EMAIL SELECTION POPUP
	//********************************************************************************************
    
    private boolean showEmailsWindow;
    
    private String selectedDestinyContainer;
    
	public boolean isShowEmailsWindow() {
		return showEmailsWindow;
	}

	public void setShowEmailsWindow(boolean showEmailsWindow) {
		this.showEmailsWindow = showEmailsWindow;
	}

	public String getSelectedDestinyContainer() {
		return selectedDestinyContainer;
	}
	
	public void openEmailsPanelPopup(ActionEvent event){
		MultiSelectionEmailBean multiSelectionEmailBean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(BEAN_MULTISELECTIONEMAIL);
		multiSelectionEmailBean.init(loadContacts);
		loadContacts = false;
		setShowEmailsWindow(true);
	}

	
	public void openEmailsToPanelPopup(ActionEvent event){
		this.selectedDestinyContainer = CONTAINER_TO;
		openEmailsPanelPopup(event);
	}

	public void openEmailsCcPanelPopup(ActionEvent event){
		this.selectedDestinyContainer = CONTAINER_CC;
		openEmailsPanelPopup(event);
	}

	public void openEmailsBccPanelPopup(ActionEvent event){
		this.selectedDestinyContainer = CONTAINER_BCC;
		openEmailsPanelPopup(event);
	}


	public void onAcceptAllEmailItems(ActionEvent event){
		MultiSelectionEmailBean bean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(BEAN_MULTISELECTIONEMAIL);
		List<IContact> lst = bean.getSelectedRows();
		StringBuffer emails = new StringBuffer();
        for (int i = 0, max = lst.size(); i < max; i++) {
        	IContact e = lst.get(i);
        	emails.append( e.getEmailLarge() );
        	if (i+1 < max) {
        		emails.append(AonMessageUtils.EMAIL_SEPARATOR).append(' ');
        	}
		}
        if (CONTAINER_TO.equals(selectedDestinyContainer)){
            recipientsTo = acceptEmailItem(emails.toString(), recipientsTo);
        } else if (CONTAINER_CC.equals(selectedDestinyContainer)) {
            recipientsCc = acceptEmailItem(emails.toString(), recipientsCc);
        } else if (CONTAINER_BCC.equals(selectedDestinyContainer)) {
            recipientsBcc = acceptEmailItem(emails.toString(), recipientsBcc);
        } 
	}

	private static final String CONTAINER_TO = "To";
	private static final String CONTAINER_CC = "Cc";
	private static final String CONTAINER_BCC = "Bcc";

	private String acceptEmailItem(String emails, String recipient){
		String result = StringUtils.trimToEmpty(recipient);
		if (! StringUtils.isEmpty(result) ) {
			result = StringUtils.replace(result, ";", AonMessageUtils.EMAIL_SEPARATOR);
			if (!result.endsWith(AonMessageUtils.EMAIL_SEPARATOR)) {
				result += AonMessageUtils.EMAIL_SEPARATOR;
			}
			result += " ";
		}
		if (! StringUtils.isBlank(emails) ) {
			result += emails + AonMessageUtils.EMAIL_SEPARATOR;
		}
		emails = null;
		return result;
	}
	
	//********************************************************************************************
	// TO ADDED TO CONTACTS
	//********************************************************************************************

	private String contactName;
	
	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public IMailAccount getSenderMailAccount() {
		return senderMailAccount;
	}

	public void setSenderMailAccount(IMailAccount senderMailAccount) {
		this.senderMailAccount = senderMailAccount;
	}
	
	public void onMailAccountChanged(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null) {
			updateContent( (IMailAccount) event.getNewValue() );
		}
		
	}	
	
	public void onAppendSignatureChanged(ActionEvent event) throws ManagerBeanException {
		updateContent( getSenderMailAccount() );
	}	
	
	private boolean includeSignature( IMailAccount mailAccount ) {
		return isAppendSignature() && !AonUtil.isAppleDevice() && mailAccount.getISignature()!=null;
	}
	
	private void updateContent( IMailAccount mailAccount ) {
		if ( includeSignature(mailAccount) ) {
			content = velocity(StringUtils.defaultString(messageBody) + mailAccount.getISignature().getSignature());
		} else {
			content = velocity(StringUtils.defaultString(messageBody));	
		}	
		if(getMailProccessType() != null) {
			setTemplates(getMailTemplates((com.code.aon.webmail.db.MailAccount)mailAccount));
		}
	}
    
	private LinkedList<SelectItem> getMailTemplates(com.code.aon.webmail.db.MailAccount mailAccount) {
		LinkedList<SelectItem> templates = new LinkedList<>();
		setTemplate(null);
		AON.getApplicationParameterStream(AonUtil.getDomainName(), DomainManager.getCurrentDomain(), "", f -> 
			f.getDomainProperty().eq(DomainManager.getCurrentDomain())
			.and(f.getNameProperty().like("AON_MAIL_PROCESS_" + getMailProccessType().ordinal() + "%")))
		.forEach(ap -> {
				String[] ids = StringUtils.split(ap.getValue());
				if(mailAccount.getId().equals(Integer.parseInt(ids[0])) || "-1".equals(ids[0])) {
					MailTemplate mt = AON.getMailTemplate(AonUtil.getDomainName(), ap.getDomain(), "", f-> 
						f.getIdProperty().eq(Integer.parseInt(ids[1])));
					if(getTemplate()== null) {
						setTemplate(mt.getId());
					} else if("1".equals(ap.getName().substring(ap.getName().length()-1))) {
						setTemplate(mt.getId());
					}
					templates.add(new SelectItem(mt.getId(), mt.getName()));
				}
			});

		return templates;
	}
	
	public List<IContact> suggestionEmails( Object value ) {
		if ( value != null ) {
			String text = value.toString();
			if (! StringUtils.isBlank(text) ) {
				return getMailConfig().getContact().suggestionEmails(text);
			}
		}
    	return Collections.emptyList();
    }
	
	public int getAttachRemoveIndex() {
		return attachRemoveIndex;
	}

	public void setAttachRemoveIndex(int attachRemoveIndex) {
		this.attachRemoveIndex = attachRemoveIndex;
	}
	
	public void onRemoveAttachment( ActionEvent event ) {
		AonFile af = getFiles().remove(this.attachRemoveIndex);
		af.clean();
	}

	public boolean isShowNewMessageWindow() {
		return showNewMessageWindow;
	}

	public void setShowNewMessageWindow(boolean showNewMessageWindow) {
		this.showNewMessageWindow = showNewMessageWindow;
	}

	public boolean isAppendSignature() {
		return appendSignature;
	}

	public void setAppendSignature(boolean appendSignature) {
		this.appendSignature = appendSignature;
	}
    
    public List<Address> getSentAddressList() {
		return sentAddressList;
	}

	public void onSendEmail(ActionEvent event) {
		setShowNewMessageWindow(false);
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			initNewMessage();
			FacesContext context = FacesContext.getCurrentInstance();
			Object email = context.getExternalContext().getRequestParameterMap().get("email");
			if ( email != null ) {
				setRecipientsTo(email.toString());	
			}
			setShowNewMessageWindow(true);
		} else {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.NOT_MAIL_ACCOUNTS);
		}
	}

	public void onPrepareEmailWindow(ActionEvent event) {
		setShowNewMessageWindow(false);
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			setShowNewMessageWindow(true);
		} else {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.NOT_MAIL_ACCOUNTS);
		}
	}
	
	public boolean isShowAppendSignature() {
		if (getSenderMailAccount() != null) {
			ISignature signature = getSenderMailAccount().getISignature();
			return signature!=null && signature.getName()!=null;
		}
		return false;
	}	
	
	public boolean isShowSaveSent() {
		return getSenderMailAccount()!=null && IMAP.equals(getSenderMailAccount().getProtocol())
				&& !StringUtils.isBlank(getSenderMailAccount().getSentFolder());
	}
	
	public boolean isSaveSent() {
		return saveSent;
	}

	public void setSaveSent(boolean saveSent) {
		this.saveSent = saveSent;
	}

	public boolean isIncluded() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Map<String,Object> map = ctx.getExternalContext().getRequestMap();
		Boolean value = (Boolean) map.get(MESSAGE_WINDOW_INCLUDED);
		if (value == null) {
			map.put(MESSAGE_WINDOW_INCLUDED, Boolean.TRUE);
			return false;
		}
		return true;
	}

	public Integer getTemplate() {
		return template;
	}

	public void setTemplate(Integer template) {
		this.template = template;
	}

	public boolean isShowTemplates() {
		return showTemplates;
	}

	public void setShowTemplates(boolean showTemplates) {
		this.showTemplates = showTemplates;
	}

	public List<SelectItem> getTemplates() {
		return templates;
	}

	public void setTemplates(List<SelectItem> templates) {
		this.templates = templates;
	}

	public Boolean getGenericMessage() {
		return genericMessage;
	}

	public void setGenericMessage(Boolean genericMessage) {
		this.genericMessage = genericMessage;
	}
		
	public MailProcessType getMailProccessType() {
		return mailProccessType;
	}
	
	public void setMailProccessType(MailProcessType mailProccessType) {
		this.mailProccessType = mailProccessType;
	}
	
}