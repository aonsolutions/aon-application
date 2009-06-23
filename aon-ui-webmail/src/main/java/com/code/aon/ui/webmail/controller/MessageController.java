package com.code.aon.ui.webmail.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.validator.LengthValidator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Contact;
import com.code.aon.groupware.dao.IContactAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonAttachment;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFile;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.bean.AonMessageUtils;
import com.code.aon.ui.webmail.converter.MaxLenghtStringConverter;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.IAonFileListener;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.sun.mail.util.LineOutputStream;

public class MessageController implements IAonFileListener{

	private static final Logger LOGGER = Logger.getLogger(MessageController.class.getName());
	
	private AonMessage message;

	private AonMessage parentMessage;

	private String sender;

	private String recipientsTo;
	
	private String recipientsCc;
	
	private String recipientsBcc;
	
	private String subject;
	
	private String content;

    private List<AonFile> newMsgFileList;

    private String returnAction = AonConstants.NAVIGATION_FOLDER;
    
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
		afterSetMessage();
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
	 * @return the returnAction
	 */
	public String getDeleteReturnAction() {
		if (getMessageList().size()>0)
			return AonConstants.NAVIGATION_MESSAGE;
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
		parentMessage = null;
    }

	public void replyToSelectedMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try {
			recipientsTo = AonMessage.parseDisplayAddress(message.getSender());
	       	subject = "Reply: "+message.getSubject();
	        StringBuffer localBody = new StringBuffer(content);
	        localBody.append("<br/>---------- Replyed message ----------");
	        localBody.append("<br/>From: ").append(message.getSender());
	        localBody.append("<br/>Date: ").append(message.getSentDate());
	        localBody.append("<br/>Subject: ").append(message.getSubject());
	        localBody.append("<br/><br/><br/>");
	        localBody.append(message.getContent());
	       	content = localBody.toString();
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}

	public void replyToAllMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try{
	       	String dest = message.getSender()+AonMessageUtils.EMAIL_SEPARATOR;
	       	dest += message.getRecipientsTo()+AonMessageUtils.EMAIL_SEPARATOR;
	       	dest += message.getRecipientsCc()+AonMessageUtils.EMAIL_SEPARATOR;
			recipientsTo = AonMessage.parseDisplayAddress(dest);
	       	subject = "ReplyALL: "+message.getSubject();
	        StringBuffer localBody = new StringBuffer(content);
	        localBody.append("<br/>---------- Replyed message ----------");
	        localBody.append("<br/>From: ").append(message.getSender());
	        localBody.append("<br/>Date: ").append(message.getSentDate());
	        localBody.append("<br/>Subject: ").append(message.getSubject());
	        localBody.append("<br/><br/><br/>");
	        localBody.append(message.getContent());
	       	content = localBody.toString();
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

	public void forwardMessage(ActionEvent event) {
		initVars();
		parentMessage = message;
		try{
	       	Iterator<AonAttachment> iter =message.getAttachements().iterator();
	       	while (iter.hasNext()){
	       		AonAttachment attach = iter.next();
	       		MimeBodyPart m = (MimeBodyPart) attach.getPart();
	       		try {
	       			AonFile af = new AonFile();
	       			InputStream is = m.getInputStream();
	       			File f = new File("/tmp/"+attach.getFileName()); 
	       			FileOutputStream fos = new FileOutputStream(f);
	    			byte buff [] = new byte [ 256 ];
	    			int read = is.read ( buff );
	    			while ( read != -1  ) {
	    				fos.write ( buff,0,read );
	    				read = is.read ( buff );
	    			}
	    			fos.flush();
	    			fos.close();
	    			af.setFile(f);
	    	    	af.addAonFileListener(this);
	       			newMsgFileList.add(af);
				} catch (IOException e) {
					AonUtil.addErrorMessage(e.getMessage());
				} catch (MessagingException e) {
					AonUtil.addErrorMessage(e.getMessage());
				}
	       	}
	       	subject = "Fwd: "+message.getSubject();
	        StringBuffer localBody = new StringBuffer(content);
	        localBody.append("<br/>---------- Forwarded message ----------");
	        localBody.append("<br/>From: ").append(message.getSender());
	        localBody.append("<br/>Date: ").append(message.getSentDate());
	        localBody.append("<br/>Subject: ").append(message.getSubject());
	        localBody.append("<br/><br/><br/>");
	        localBody.append(message.getContent());
	       	content = localBody.toString();
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
    }

    public void deleteSelectedMessage(ActionEvent event){
    	ArrayList<AonMessage> lst = new ArrayList<AonMessage>();
		lst.add(this.message);
		try{
			if (message.getParent().getFolder().getFullName().equals(AonFolder.TRASH_FOLDER_NAME)){
				message.getParent().deleteMessages(lst);
			}else if (message.getParent().getFolder().getFullName().equals(AonFolder.SPAM_FOLDER_NAME)){
				message.getParent().deleteMessages(lst);
			}else{
		    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		    	AonFolder dest = webMailController.getServer().getAonFolder(AonFolder.TRASH_FOLDER_NAME);
		    	message.getParent().moveMessages(lst, dest);
			}
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

    public void deleteSelectedMessageAndMove(ActionEvent event){
    	AonMessage message = null; 
    	if(isNextMessage()){
    		message = getNextMessage();
    	}else if (isPreviousMessage()){
    		message = getPreviousMessage();
    	}
    	deleteSelectedMessage(event);
    	setMessage(message);
    }

    public List<AonFile> getFiles(){
    	return newMsgFileList;
    }

	public void fileDeleted(AonFile aonFile) {
		newMsgFileList.remove(aonFile);
	}

	//***************************************************************
	//*********** ATTACH ********************************************
	//***************************************************************
	public void fileUploaded(File file) {
    	AonFile f = new AonFile();
    	f.setFile(file);
    	f.addAonFileListener(this);
    	newMsgFileList.add(f);
	}
    
	private UploadedFile inputFile;
	
	private long maximumSize = -1;

	public UploadedFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(UploadedFile inputFile) {
		this.inputFile = inputFile;
	}
	
	public void fileUploaded( ActionEvent event ) throws IOException {
		if ( this.inputFile!= null ) {
			long size = this.inputFile.getSize();
			String upload_name = inputFile.getName();
			upload_name = upload_name.replace('\\', '/');
			if (upload_name.lastIndexOf('/')>=0)
				upload_name = upload_name.substring(upload_name.lastIndexOf('/'));
			String preffix = upload_name;
			String suffix = "";
			if (upload_name.lastIndexOf('.')>=0){
				preffix = upload_name.substring(0,upload_name.lastIndexOf('.'));
				suffix = upload_name.substring(upload_name.lastIndexOf('.'));
			}
			File file = File.createTempFile(preffix, suffix);
			if ( (maximumSize != -1) && (size > maximumSize) ) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				FacesMessage message = AonUtil.getMessage( ctx,
						LengthValidator.MAXIMUM_MESSAGE_ID, new Object[]{maximumSize, upload_name} );
				ctx.addMessage(AonUtil.AON_ERROR, message);
			} else {
				byte[] data = this.inputFile.getBytes();
		        FileOutputStream outputStream = new FileOutputStream(file);
		        outputStream.write(data);
		        outputStream.close();			
		        fileUploaded(file);
			}
		}
	}

	//***************************************************************
	//*********** END ATTACH ****************************************
	//***************************************************************
	
	
    public void send(ActionEvent event) {
    	try{
	    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
	    	AonMessage aonMessage = compoundMessage(
	    			recipientsTo,
	    			recipientsCc, 
	    			recipientsBcc,
	    			subject, 
	    			AonMessageUtils.unparse_cid(content),
	    			parentMessage,
	    			newMsgFileList
	    			);
	    	AonFolder dest;
	    	if (webMailController.getServer().sendMessage(aonMessage)){
		    	dest = webMailController.getServer().getAonFolder(AonFolder.SENT_FOLDER_NAME);
		    	if (parentMessage!=null){
			    	parentMessage.getMessage().setFlag(Flag.ANSWERED, true);
			    	parentMessage.getParent().getFolder().expunge();
		    	}
	    	}else{
		    	dest = webMailController.getServer().getAonFolder(AonFolder.DRAFT_FOLDER_NAME);
	    	}
	    	Message[] messages = new Message[1];
    		messages[0] = aonMessage.getMessage();
    		messages[0].setFlag(Flag.SEEN, true);
	    	dest.open(Folder.READ_WRITE);
	    	Folder desfFolder = dest.getFolder();
	    	desfFolder.appendMessages(messages);
	    	desfFolder.expunge();
	    	dest.close(false);
		} catch (WebmailException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (MessagingException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
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
	*/
	private AonMessage compoundMessage( 
			String recipientsTo,
			String recipientsCC, 
			String recipientsBCC,
			String subject, 
			String text, 
			AonMessage parentAonMsg,
			List<AonFile> fileList) 
			throws MessagingException, WebmailException {
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
    	AonMessage newMessage = webMailController.getServer().createAonMessage();
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

	private void initVars(){
    	WebMailController webMailController = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
		sender = webMailController.getServer().getAccount().getEmail();
		recipientsTo = null;
		recipientsCc = null;
		recipientsBcc = null;
		subject = null;
		content = getSignature()!=null?getSignature().getSignature():"";
    	newMsgFileList = new ArrayList<AonFile>();
		
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
	//SIGNATURE
	//********************************************************************************************
    
    private Signature getSignature(){
   		WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
   		MailAccount account = wmc.getServer().getAccount();
   		return account.getSignature();
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
		MultiSelectionEmailBean multiSelectionEmailBean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(AonConstants.BEAN_MULTISELECTIONEMAIL);
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
		MultiSelectionEmailBean bean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(AonConstants.BEAN_MULTISELECTIONEMAIL);
		List<Contact> lst = bean.getSelectedRows();
		email = "";
        for (int i = 0, max = lst.size(); i < max; i++) {
        	Contact e = lst.get(i);
        	email += e.getEmail();
        	if (i+1 < max)
        		email += AonMessageUtils.EMAIL_SEPARATOR + " ";
		}
        if (CONTAINER_TO.equals(selectedDestinyContainer)){
            recipientsTo = acceptEmailItem(recipientsTo);
        }else if (CONTAINER_CC.equals(selectedDestinyContainer)){
            recipientsCc = acceptEmailItem(recipientsCc);
        }else if (CONTAINER_BCC.equals(selectedDestinyContainer)){
            recipientsBcc = acceptEmailItem(recipientsBcc);
        } 
        email = null;
	}

	private static final String CONTAINER_TO = "To";
	private static final String CONTAINER_CC = "Cc";
	private static final String CONTAINER_BCC = "Bcc";
	
	private String email = null;
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	private String acceptEmailItem(String recipient){
		if (recipient==null || recipient.trim().length()==0){
			recipient = "";
		}else if (!recipient.trim().endsWith(AonMessageUtils.EMAIL_SEPARATOR)){
			recipient += AonMessageUtils.EMAIL_SEPARATOR + " ";
		}
		recipient += email + AonMessageUtils.EMAIL_SEPARATOR;
		email = null;
		return recipient;
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
			out.flush();
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

	private ArrayList<AonMessage> getMessageList(){
		if (AonConstants.NAVIGATION_FOLDER.equals(returnAction)){
			if (this.getMessage()==null)
				return new ArrayList<AonMessage>();
			return this.getMessage().getParent().getMessageList();
		}else if(AonConstants.NAVIGATION_SEARCH.equals(returnAction)){
			SearchController sc = (SearchController)AonUtil.getRegisteredBean(AonConstants.BEAN_SEARCH);
			return sc.getSortableList().getMessageList();
		}
		return null;
	}

	public int getCurrentIndex(){
		return getMessageList().indexOf(this.getMessage());
	}
	
	public boolean isPreviousMessage(){
		int index = getMessageList().indexOf(this.getMessage());
		index--;
		if (index>=0)
			return true;
		return false;
	}

	public boolean isNextMessage(){
		int index = getMessageList().indexOf(this.getMessage());
		index++;
		if (index<getMessageList().size())
			return true;
		return false;
	}

	private AonMessage getPreviousMessage(){
		int index = getMessageList().indexOf(this.getMessage());
		index--;
		if (index>=0)
			return getMessageList().get(index);
		return null;
	}
	
	private AonMessage getNextMessage(){
		int index = getMessageList().indexOf(this.getMessage());
		index++;
		if (index<getMessageList().size())
			return getMessageList().get(index);
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
    	ArrayList<AonMessage> lst = new ArrayList<AonMessage>();
		lst.add(this.message);
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
			if (message.getRecipientsTo().length()>MaxLenghtStringConverter.getMax())
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
			if (message.getRecipientsCc().length()>MaxLenghtStringConverter.getMax())
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
			String sender = message.getSenderShort();
			if (! StringUtils.equals(sender, message.getSenderEmail()) ) {
				this.contactName = sender;
			}
		} catch (WebmailException e) {
			LOGGER.severe( "Error setting contactName" );
		}
    }

	public void saveToContacts(ActionEvent event) throws WebmailException, ManagerBeanException {
		String email = message.getSenderEmail();
		IController contactController = AonUtil.getController(AonConstants.BEAN_CONTACT);
		IManagerBean contactsBean = contactController.getManagerBean();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(contactsBean.getFieldName(IContactAlias.CONTACT_EMAIL), email);
		List list = contactsBean.getList(criteria);
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

}
