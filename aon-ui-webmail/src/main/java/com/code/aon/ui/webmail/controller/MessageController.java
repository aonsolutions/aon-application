package com.code.aon.ui.webmail.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.naming.Name;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.AonException;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonMessageTracer;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.AonFile;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonAttachment;
import com.code.aon.webmail.bean.AonFolder;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonMessageUtils;
import com.code.aon.webmail.bean.AonServer;
import com.code.aon.webmail.bean.BundleConstants;
import com.code.aon.webmail.dao.IWebMailAlias;
import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.util.LineOutputStream;

public class MessageController implements WebMailConstants, BundleConstants {

	private static final int MAX_LENGTH_STRING = 120;

	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/webmail/";
	
	private static final String PRINT_TEMPLATE = "print.html.vm";

	private static final Logger LOGGER = Logger.getLogger(MessageController.class.getName());
	
	private AonMessage message;

	private AonMessage parentMessage;
	
	private String messageContent;
	
	private Long draftMessageUID;

	private String sender;

	private String recipientsTo;
	
	private String recipientsCc;
	
	private String recipientsBcc;
	
	private String subject;
	
	private String content;

    private List<AonFile> newMsgFileList;

    private String returnAction = NAVIGATION_FOLDER;
    
    private Name senderMailAccountId;
    
    private String messageBody;
    
    private VelocityHelper velocityHelper;
    
    private boolean showRecipients;
    
    private boolean showToolbar;
    
    private int attachRemoveIndex;
    
	public MessageController() {
		this.showRecipients = true;
		this.showToolbar = true;
	}

	/**
	 * @return the message
	 */
	public AonMessage getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(AonMessage message) {
		this.message = message;
		this.messageContent = null;
		afterSetMessage();
		AttachController attachController = (AttachController) AonUtil.getRegisteredBean(BEAN_ATTACH);
		attachController.update(this.message);
	}


	private void afterSetMessage(){
		initShotMessageToCcBcc();
		initContactName();
	}
	
	
	/**
	 * @return the returnAction
	 */
	public String getReturnAction() {
		return returnAction;
	}

	/**
	 * @param returnAction the returnAction to set
	 */
	public void setReturnAction(String returnAction) {
		this.returnAction = returnAction;
	}

	public void createNewMessage(ActionEvent event){
		initVars();
    }

	public void editDraftMessage(AonMessage message, long uid) {
		initVars();
		this.parentMessage = message;
		this.draftMessageUID = uid;
		try {
			copyAttachmentsToFileList( message );			
			recipientsTo = AonMessage.parseDisplayAddress(message.getRecipientsTo());
			recipientsCc = AonMessage.parseDisplayAddress(message.getRecipientsCc());
			recipientsBcc = AonMessage.parseDisplayAddress(message.getRecipientsBcc());
	       	subject = message.getSubject();
	       	content = getMessageContent( message );
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}	
	
	public void replyToSelectedMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try {
			recipientsTo = AonMessage.parseDisplayAddress(message.getSender());
	       	subject = "Reply: "+message.getSubject();
	       	messageBody = AonMessage.getMessageEnvelope(message.getMessage(), getMessageContent(), REPLIED_MESSAGE, AonUtil.getCurrentLocale());
	       	content += messageBody;
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		} catch (MessagingException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}
	
	private String getReplyToAllRecipients( AonMessage message ) throws WebmailException {
		List<Address> addresses = new LinkedList<Address>();
		addresses.add(message.getSenderAddress());
		Address[] to = message.getRecipientsToAddress();
		if (! ArrayUtils.isEmpty(to) ) {
			addresses.addAll(Arrays.asList(to));
		}
		Address[] cc = message.getRecipientsCcAddress();
		if (! ArrayUtils.isEmpty(cc) ) {
			addresses.addAll(Arrays.asList(cc));
		}
		for( int i = addresses.size()-1; i >= 0; i-- ) {
			String email = AonMessage.getDisplayEmail(addresses.get(i));
			if ( StringUtils.equalsIgnoreCase(sender, email) ) {
				addresses.remove(i);
			}
		}
		StringBuffer recipients = new StringBuffer();
		for ( Address address : addresses ) {
			String email = AonMessage.getDisplayAddressFull(address);
			recipients.append(email).append(AonMessageUtils.EMAIL_SEPARATOR);
		}
		return recipients.substring(0, recipients.length() - 1).toString();		
	}

	public void replyToAllMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try{
	       	String dest = getReplyToAllRecipients(message);
			recipientsTo = AonMessage.parseDisplayAddress(dest);
	       	subject = "ReplyALL: "+message.getSubject();
	       	messageBody = AonMessage.getMessageEnvelope(message.getMessage(), getMessageContent(), REPLIED_MESSAGE, AonUtil.getCurrentLocale());
	       	content += messageBody;
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (MessagingException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);			
		}
    }
	
	private void refreshDraftFolder() {
    	FolderController folderController = (FolderController) AonUtil.getRegisteredBean(BEAN_FOLDER);
    	if ( folderController.getFolder().isDraftFolder() ) {
    		folderController.getFolder().close(false);
    		folderController.refresh(null);
    	}
	}

	private void copyAttachmentsToFileList( AonMessage message ) throws WebmailException {
       	Iterator<AonAttachment> iter =message.getAttachements().iterator();
       	while (iter.hasNext()){
       		AonAttachment attach = iter.next();
       		MimeBodyPart m = (MimeBodyPart) attach.getPart();
       		try {
       			AonFile af = new AonFile();
       			InputStream is = m.getInputStream();
       			String name = attach.getFileName();
       			File f = File.createTempFile( "webmail-", name ); 
       			FileOutputStream fos = new FileOutputStream(f);
       			IOUtils.copy( is, fos );
       			is.close();
    			fos.close();
    			af.setFile(f);
    			af.setFileName( name );
       			newMsgFileList.add(af);
			} catch (IOException e) {
				AonUtil.addErrorMessage(e.getMessage());
			} catch (MessagingException e) {
				AonUtil.addErrorMessage(e.getMessage());
			}
       	}		
	}
	
	public void forwardMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try {
			copyAttachmentsToFileList( message );
	       	subject = "Fwd: "+message.getSubject();
	       	messageBody = AonMessage.getMessageEnvelope(message.getMessage(), getMessageContent(), FORWARDED_MESSAGE, AonUtil.getCurrentLocale());
	       	content += messageBody;
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (MessagingException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);			
		}
    }

    public void deleteSelectedMessage(ActionEvent event){
    	AonMessage[] messagesLst = new AonMessage[] { this.message };
    	FolderController folderController = (FolderController) AonUtil.getRegisteredBean(BEAN_FOLDER);
    	try {
    		folderController.deleteMessages(messagesLst, false);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}    	
	}

    public void purgeSelectedMessage(ActionEvent event){
    	AonMessage[] messagesLst = new AonMessage[] { this.message };
    	FolderController folderController = (FolderController) AonUtil.getRegisteredBean(BEAN_FOLDER);
    	try {
    		folderController.deleteMessages(messagesLst, true);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}    	
	}
    
    public List<AonFile> getFiles(){
    	return newMsgFileList;
    }

	//***************************************************************
	//*********** ATTACH ********************************************
	//***************************************************************

	public void fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
    	AonFile f = new AonFile();
    	f.setFile(item.getFile());
    	f.setFileName(item.getFileName());
    	newMsgFileList.add(f);
	}	
	
	//***************************************************************
	//*********** END ATTACH ****************************************
	//***************************************************************
	
	
    public void send(ActionEvent event) {
    	try {
	    	AonMessage aonMessage = compoundMessage();
	    	AonFolder dest = null;
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
	    	AonServer server = webMailController.getServer();
	    	try {
	    		server.sendMessage(aonMessage);
		    	dest = server.getAonFolder(server.getSentFolderName());
		    	if (parentMessage!=null){
			    	parentMessage.getMessage().setFlag(Flag.ANSWERED, true);
			    	parentMessage.getParent().getFolder().expunge();
		    	}
	    	} catch ( WebmailException e ) {
	    		LOGGER.log(Level.SEVERE, e.getMessage(), e);
		    	dest = server.getAonFolder(server.getDraftFolderName());
	    	}
	    	Message[] messages = new Message[1];
    		messages[0] = aonMessage.getMessage();
    		messages[0].setFlag(Flag.SEEN, true);
	    	dest.open(Folder.READ_WRITE);
	    	Folder desfFolder = dest.getFolder();
	    	desfFolder.appendMessages(messages);
	    	desfFolder.expunge();
	    	dest.close(false);
	    	deleteDraftMessage();
	    	refreshDraftFolder();
		} catch (Throwable th) {
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th);
		}
    }
    
    private void deleteDraftMessage() throws MessagingException {
    	if ( this.draftMessageUID != null ) {
    		WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
    		AonServer server = webMailController.getServer();
    		AonFolder folder = server.getAonFolder(server.getDraftFolderName());
    		IMAPFolder imapFolder = (IMAPFolder) folder.getFolder();
    		imapFolder.open(Folder.READ_WRITE);
    		Message message = imapFolder.getMessageByUID(this.draftMessageUID);
    		if ( message != null ) {
	    		message.setFlag(Flags.Flag.DELETED, true);	    			
    		}
    		imapFolder.close(true);
    		this.draftMessageUID = null;
    	}    	
    }

    public void saveDraft(ActionEvent event) {
    	try {
	    	AonMessage aonMessage = compoundMessage();    		
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
	    	AonServer server = webMailController.getServer();
	    	AonFolder dest = server.getAonFolder(server.getDraftFolderName());
	    	Message[] messages = new Message[1];
    		messages[0] = aonMessage.getMessage();
    		messages[0].setFlag(Flag.DRAFT, true);
	    	dest.open(Folder.READ_WRITE);
	    	deleteDraftMessage();
	    	IMAPFolder desfFolder = (IMAPFolder) dest.getFolder();
	    	AppendUID[] uids = desfFolder.appendUIDMessages(messages);
	    	if (! ArrayUtils.isEmpty(uids) ) {
		    	this.draftMessageUID = uids[0].uid;	
	    	}
	    	desfFolder.expunge();
	    	dest.close(false);
	    	refreshDraftFolder();
		} catch (Throwable th) {
			AonUtil.addErrorMessage(th.getMessage());
			throw new AbortProcessingException(th);
		}
    }
    
    public void addAttach(MimeMultipart multipart,File file) throws IOException, MessagingException{
    	MimeBodyPart adjunto = new MimeBodyPart ();
    	adjunto.attachFile(file);
    	multipart.addBodyPart(adjunto);
    }
    
    //*******************************************************************************************
	/**
	* Method for compounding the message.
	 * @throws WebmailException 
	 * @throws UnsupportedEncodingException 
	*/
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
       	if (! StringUtils.isEmpty(recipientsTo)) {
       		newMessage.setRecipientsTo(recipientsTo);
       	}
       	if (! StringUtils.isEmpty(recipientsCC)) {       	
       		newMessage.setRecipientsCc(recipientsCC);
       	}
       	if (! StringUtils.isEmpty(recipientsBCC)) {       	
       		newMessage.setRecipientsBcc(recipientsBCC);
       	}
       	if (! StringUtils.isEmpty(subject)) {       	
       		newMessage.setSubject(subject);
       	}
       	newMessage.getMessage().setHeader("X-Mailer", "OfficeWeb - AonWebMail 1.0");

       	MimeMultipart multipart1 =new MimeMultipart("related");
       	MimeBodyPart part=new MimeBodyPart();
       	part=new MimeBodyPart();
       	part.setContent(text,"text/html");
       	multipart1.addBodyPart(part);
       	if (parentAonMsg!=null){
       		AonMessageTracer amt = new AonMessageTracer(parentAonMsg.getMessage());
	       	List<BodyPart> list = amt.getRelateds();
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
				AonFile file = fileList.get(i);
				MimeBodyPart mbpNext = new MimeBodyPart();
				fds = new FileDataSource(file.getFile());
				String name = FilenameUtils.getName(file.getFileName());
				mbpNext.setFileName( name );
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

	public AonMessage compoundMessage() throws ManagerBeanException, UnsupportedEncodingException, MessagingException, WebmailException {
		IManagerBean mailAccountBean = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();	
		MailAccount mailAccount = (MailAccount) mailAccountBean.get( senderMailAccountId );   		
    	AonMessage aonMessage = compoundMessage(
    			mailAccount.getEmail(),
    			recipientsTo,
    			recipientsCc, 
    			recipientsBcc,
    			subject, 
    			AonMessageUtils.unparse_cid(content),
    			parentMessage,
    			newMsgFileList
    			);
    	return aonMessage;
	}
	
	private void initVars(){
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(BEAN_WEBMAIL);
    	MailAccount account = webMailController.getServer().getAccount();
		sender = account.getEmail();
		senderMailAccountId = account.getId();
		recipientsTo = null;
		recipientsCc = null;
		recipientsBcc = null;
		subject = null;		
		content = (account.getSignature()!=null)?account.getSignature().getSignature():"";
    	newMsgFileList = new ArrayList<AonFile>();
		draftMessageUID = null;
		parentMessage = null;
		messageContent = null;
		messageBody = null;
	}
	//********************************************************************************************

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
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
		this.subject = subject;
	}

    public String getContent(){
    	return content;
    }

    public void setContent(String content){
    	this.content = content;
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
		multiSelectionEmailBean.reload();
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


	public void acceptAllEmailItems(ActionEvent event){
		MultiSelectionEmailBean bean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(BEAN_MULTISELECTIONEMAIL);
		List<Contact> lst = bean.getSelectedRows();
		String emails = "";
        for (int i = 0, max = lst.size(); i < max; i++) {
        	Contact e = lst.get(i);
        	emails += StringEscapeUtils.unescapeHtml(e.getEmailLarge());
        	if (i+1 < max) {
        		emails += AonMessageUtils.EMAIL_SEPARATOR + " ";
        	}
		}
        if (CONTAINER_TO.equals(selectedDestinyContainer)){
            recipientsTo = acceptEmailItem(emails, recipientsTo);
        }else if (CONTAINER_CC.equals(selectedDestinyContainer)){
            recipientsCc = acceptEmailItem(emails, recipientsCc);
        }else if (CONTAINER_BCC.equals(selectedDestinyContainer)){
            recipientsBcc = acceptEmailItem(emails, recipientsBcc);
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
	// SAVE MESSAGE
	//********************************************************************************************

	public void save(HttpServletResponse response) {
		try{
			Date date;
			String fecha = "";
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
			Folder folder = message.getParent().getFolder();
			Message msg = folder.getMessage(message.getMessage().getMessageNumber());
			if ((date = msg.getSentDate()) != null) {
				fecha = (df.format(date));
			}	else if ((date = msg.getReceivedDate()) != null) {
				fecha = (df.format(date));
			}
			String from = message.getSender();
			int index = from.indexOf("&lt;");
			if (index > 0) {
				from = from.substring(0, index).trim();
			}
			String fileName = msg.getSubject() + ".eml";
			fileName = fecha + "-[" + from + "]-" + fileName;
			fileName = fileName.replace('\\','_');
			fileName = fileName.replace('/','_');
			fileName = fileName.replace(':','_');
			fileName = fileName.replace('*','_');
			fileName = fileName.replace('?','_');
			fileName = fileName.replace('\"','_');
			fileName = fileName.replace('<','_');
			fileName = fileName.replace('>','_');
			fileName = fileName.replace('|','_');

			response.setHeader("content-disposition", "attachment;filename=\""
					+ fileName + "\"");
			
			OutputStream out = response.getOutputStream();
			InputStream in = msg.getInputStream();

			System.out.println("");
	        Enumeration enumeration = msg.getAllHeaders();
	        LineOutputStream lineoutputstream = new LineOutputStream(out);
	        while(enumeration.hasMoreElements()){
	        	try{
	        		Header header = (Header)enumeration.nextElement();
	        		String line = header.getName();
	        		line += ": ";
	        		line += header.getValue();
	        		lineoutputstream.writeln(line);
	        	}catch (Exception e){}
	        }
	        lineoutputstream.writeln();

			byte[] block = new byte[1024];
			int len = 0;
			while ((len = in.read(block)) > -1) {
			    out.write(block, 0, len);
			}
			int read = 0;
			while ((read = in.read(block)) > -1) {
			    out.write(block, 0, read);
			}
			in.close();
			response.flushBuffer();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (WebmailException e) {
			e.printStackTrace();
		}
	}
	
	// ****************************************************************
	// NEXT - PREVIOUS
	// ****************************************************************

	private AonMessage[] getMessageList(){
		if (NAVIGATION_FOLDER.equals(returnAction)){
			if (this.getMessage()==null)
				return new AonMessage[0];
			return this.getMessage().getParent().getMessageList();
		}else if(NAVIGATION_SEARCH.equals(returnAction)){
			SearchController sc = (SearchController)AonUtil.getRegisteredBean(BEAN_SEARCH);
			return sc.getSortableList().getMessageList();
		}
		return null;
	}

	public int getCurrentIndex(){
		return ArrayUtils.indexOf( getMessageList(), getMessage() );
	}
	
	public boolean isPreviousMessage(){
		int index = getCurrentIndex();
		index--;
		if (index>=0)
			return true;
		return false;
	}

	public boolean isNextMessage(){
		int index = getCurrentIndex();
		index++;
		if (index<getMessageList().length)
			return true;
		return false;
	}

	private AonMessage getPreviousMessage(){
		int index = getCurrentIndex();
		index--;
		if (index>=0) {
			return getMessageList()[index];
		}
		return null;
	}
	
	private AonMessage getNextMessage(){
		int index = getCurrentIndex();
		index++;
		if (index < getMessageList().length ) {
			return getMessageList()[index];
		}
		return null;
	}
	
	public void previousMessage(ActionEvent event) {
		setMessage(getPreviousMessage());
	}

	public void nextMessage(ActionEvent event) {
		setMessage(getNextMessage());
	}

	//********************************************************************************************
	// DESTINY FOLDER SELECTION POPUP
	//********************************************************************************************
    
    public void moveSelectedMessageAndMove(AonFolder dest){
    	AonMessage nextMessage = null; 
    	if(isNextMessage()){
    		nextMessage = getNextMessage();
    	}else if (isPreviousMessage()){
    		nextMessage = getPreviousMessage();
    	}
    	AonMessage[] lst = new AonMessage[] { this.message };
		try{
	    	message.getParent().moveMessages(lst, dest);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    	setMessage(nextMessage);
	}

	//********************************************************************************************
	// TO, CC, BCC LONG/SHOT
	//********************************************************************************************
    
    private boolean shotMessageTo;
    private boolean shotMessageCc;

    private void initShotMessageToCcBcc(){
        shotMessageTo = false;
        shotMessageCc = false;
    }
    
    public boolean isShotMessageTo() {
    	return shotMessageTo;
	}

	public void changeShotMessageTo(ActionEvent event){
    	shotMessageTo = !shotMessageTo;
    }

	private boolean isShotMessageToControl(){
    	try {
			if (message.getRecipientsTo().length()>MAX_LENGTH_STRING)
				return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public boolean isShotMessageToControlUp(){
		if (isShotMessageToControl()) return shotMessageTo;
		return false;
	}
	
	public boolean isShotMessageToControlDown(){
		if (isShotMessageToControl()) return !shotMessageTo;
		return false;
	}
	
    public boolean isShotMessageCc() {
    	return shotMessageCc;
	}

	public void changeShotMessageCc(ActionEvent event){
    	shotMessageCc = !shotMessageCc;
    }
	
    private boolean isShotMessageCcControl() {
    	try {
			if (message.getRecipientsCc().length()>MAX_LENGTH_STRING)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	public boolean isShotMessageCcControlUp(){
		if (isShotMessageCcControl()) return shotMessageCc;
		return false;
	}
	
	public boolean isShotMessageCcControlDown(){
		if (isShotMessageCcControl()) return !shotMessageCc;
		return false;
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

	public void initContactName() {
		this.contactName = "";
		try {
			if ( this.message != null ) {
				String sender = message.getSenderSummary();
				String email = message.getSenderEmail();
				if ( StringUtils.equals(sender, email) ) {
					int pos = email.indexOf('@');
					this.contactName = (pos != -1) ? email.substring(0, pos) : email;
				} else {
					this.contactName = sender;
				}
			}
		} catch (WebmailException e) {
			LOGGER.severe( "Error setting contactName" );
		}
    }

	public void saveToContacts(ActionEvent event) throws WebmailException, ManagerBeanException {
		String email = message.getSenderEmail();
		IController contactController = FormUtil.getController(BEAN_CONTACT);
		IManagerBean contactsBean = contactController.getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(contactsBean.getFieldName(IWebMailAlias.CONTACT_EMAIL), email);
		List<ITransferObject> list = contactsBean.getList(criteria);
		if (list.size()==0){
			Contact contact = new Contact();
			contact.setEmail(email);
			contact.setDisplayName(contactName);
			int pos = contactName.indexOf(' ');
			if ( pos != -1 ) {
				contact.setName(contactName.substring(0, pos));
				String surname = StringUtils.trimToNull( StringUtils.substring(contactName, pos+1) );
				contact.setSurname(surname);
			} else {
				contact.setName(contactName);
			}

			contactsBean.insert(contact);		
			contactController.onSearch(null);
		}
    }
	
	public Name getSenderMailAccountId() {
		return senderMailAccountId;
	}

	public void setSenderMailAccountId(Name senderMailAccountId) {
		this.senderMailAccountId = senderMailAccountId;
	}
	
	@SuppressWarnings("unchecked")
	public void onMailAccountChanged(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null) {
			IManagerBean mailAccountBean = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();	
			MailAccount mailAccount = (MailAccount) mailAccountBean.get( (Name) event.getNewValue() );
			if ( mailAccount.getSignature() != null ) {
				content = mailAccount.getSignature().getSignature() + StringUtils.defaultString(messageBody);
			} else {
				content = StringUtils.defaultString(messageBody);	
			}
		}
	}	
	
	private VelocityHelper getVelocityHelper() {
		if ( this.velocityHelper == null ) {
			this.velocityHelper = new VelocityHelper();
			try {
				this.velocityHelper.init( VM_PATH_DEFAULT );
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Velocity engine could not be initialized", e );
			}
		}
		return this.velocityHelper;
	}
	
	
	private void printMessage( HttpServletResponse response, AonMessage message ) {
		try {
			response.setContentType(MimeType.MIME_HTML.getName());
			Writer out = new OutputStreamWriter( response.getOutputStream() );
			TemplateHelper th = getVelocityHelper().getTemplateHelper();
			FacesContext context = FacesContext.getCurrentInstance();
			th.putInContext("contextPath", context.getExternalContext().getRequestContextPath());
			LoggedUser loggerUser = (LoggedUser) AonUtil.getRegisteredBean(BEAN_LOGGED_USER);
			th.putInContext("username", loggerUser.getLoggedUserName());
			SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yy-HH:mm");
			th.putInContext("nowDate", df.format(new Date()));
			Locale locale = AonUtil.getCurrentLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(BundleConstants.RESOURCE_BUNDLE, locale);	
			th.putInContext("fromLiteral", bundle.getString(FROM_MESSAGE));
			th.putInContext("sender", message.getSender());
			th.putInContext("toLiteral", bundle.getString(TO_MESSAGE));
			th.putInContext("recipientsTo", message.getRecipientsTo());
			String cc = message.getRecipientsCc();
			if (! StringUtils.isEmpty(cc) ) {
				th.putInContext("ccLiteral", bundle.getString(CC_MESSAGE));
				th.putInContext("recipientsCc", cc );				
			}
			th.putInContext("dateLiteral", bundle.getString(DATE_MESSAGE));
			th.putInContext("sentDateString", message.getSentDateString());
			th.putInContext("subjectLiteral", bundle.getString(SUBJECT_MESSAGE));
			th.putInContext("subject", message.getSubject());
			th.putInContext("messageContent", getMessageContent());
			th.processTemplate(PRINT_TEMPLATE, out);
			response.flushBuffer();
			out.close();
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} catch (AonException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} catch (WebmailException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );			
		}		
	}
	
    public void print( ActionEvent event ) throws MessagingException, WebmailException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        printMessage(response, this.message);
        context.responseComplete();    	
    }

    public void save( ActionEvent event ) throws MessagingException, WebmailException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        save(response);
        context.responseComplete();    	
    }
    
	public List<ITransferObject> suggestionEmails( Object value ) {
		if ( value != null ) {
			String text = value.toString();
			if (! StringUtils.isBlank(text) ) {
				try {
					IManagerBean bean = FormUtil.getController(WebMailConstants.BEAN_CONTACT).getManagerBean();
					Criteria criteria = new Criteria();
					String displayName = bean.getFieldName(IWebMailAlias.CONTACT_DISPLAY_NAME);
					String email = bean.getFieldName(IWebMailAlias.CONTACT_EMAIL);
					Expression exp1 = ExpressionUtilities.getLikeExpression(displayName, text + "*");
					Expression exp2 = ExpressionUtilities.getLikeExpression(email, text + "*");
					criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
					criteria.addNotNullExpression(email);
					criteria.addOrder(displayName);
					return bean.getList(criteria);
		    	} catch (ManagerBeanException e) {
		    		LOGGER.log( Level.SEVERE, "Error getting suggestion emails", e );
				}				
			}
		}
    	return Collections.emptyList();
    }
	
	public String getMessageContent() {
		return getMessageContent(message);
	}

	public String getMessageContent( AonMessage message ) {
		if ( messageContent == null ) {
			AonMessageTracer amt = new AonMessageTracer(message.getMessage());
			try {
				this.messageContent = amt.getBodyHTML();
			} catch (MessagingException e) {
				this.messageContent = AonMessageUtils.HTML_LINE_BREAK;
			} catch (IOException e) {
				this.messageContent = AonMessageUtils.HTML_LINE_BREAK;
			}		
		}
		return this.messageContent;
	}
	
	public boolean isShowRecipients() {
		return showRecipients;
	}

	public void setShowRecipients(boolean showRecipients) {
		this.showRecipients = showRecipients;
	}

	public boolean isShowToolbar() {
		return showToolbar;
	}

	public void setShowToolbar(boolean showToolbar) {
		this.showToolbar = showToolbar;
	}

	public int getAttachRemoveIndex() {
		return attachRemoveIndex;
	}

	public void setAttachRemoveIndex(int attachRemoveIndex) {
		this.attachRemoveIndex = attachRemoveIndex;
	}
	
	public void removeAttachment( ActionEvent event ) {
		getFiles().remove(this.attachRemoveIndex);
	}
	
}
