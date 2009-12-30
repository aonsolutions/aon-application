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
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.Contact;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonAttachment;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonFile;
import com.code.aon.ui.webmail.bean.AonFolder;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.bean.AonMessageTracer;
import com.code.aon.ui.webmail.bean.AonMessageUtils;
import com.code.aon.ui.webmail.exception.WebmailException;
import com.code.aon.ui.webmail.listener.IAonFileListener;
import com.code.aon.ui.webmail.listener.IFileUploadedListener;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;
import com.code.aon.webmail.enumeration.SignatureType;
import com.sun.mail.util.LineOutputStream;

public class MessageController implements IAonFileListener,IFileUploadedListener{

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
	       	String dest = message.getSender()+",";
	       	dest += message.getRecipientsTo()+",";
	       	dest += message.getRecipientsCc()+",";
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
	       			File f = new File(attach.getFileName()); 
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

	public void fileUploaded(File file) {
    	AonFile f = new AonFile();
    	f.setFile(file);
    	f.addAonFileListener(this);
    	newMsgFileList.add(f);
	}
    
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
				part.setDataHandler(bp.getDataHandler());
				part.setContentID(bp.getHeader("Content-ID")[0]);
				part.setDisposition(Part.INLINE);
				multipart1.addBodyPart(part);
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
    	try{
    		WebMailController wmc = (WebMailController)AonUtil.getRegisteredBean(AonConstants.BEAN_WEBMAIL);
    		MailAccount account = wmc.getServer().getAccount();
    		IManagerBean bean = BeanManager.getManagerBean(Signature.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(bean.getFieldName(IWebMailAlias.SIGNATURE_MAIL_ACCOUNT_ID), account.getId());
    		criteria.addExpression(bean.getFieldName(IWebMailAlias.SIGNATURE_ACTIVE), String.valueOf(SignatureType.ACTIVE.ordinal()));
    		List list = bean.getList(criteria);
    		if (!list.isEmpty()){
    			return (Signature)list.get(0);
    		}
    	}catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
    	return null;
    }
    
	//********************************************************************************************
	// EMAIL SELECTION POPUP
	//********************************************************************************************
    
    private boolean showEmailsPanelPopup;
    
	public boolean isShowEmailsPanelPopup() {
		return showEmailsPanelPopup;
	}

	public void setShowEmailsPanelPopup(boolean showNewFolderPanelPopup) {
		this.showEmailsPanelPopup = showNewFolderPanelPopup;
	}
    
	public void closeEmailsPanelPopup(ActionEvent event){
		this.showEmailsPanelPopup = false;
	}

	public void openEmailsPanelPopup(ActionEvent event){
		MultiSelectionEmailBean bean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(AonConstants.BEAN_MULTISELECTIONEMAIL);
		bean.init();
		this.showEmailsPanelPopup = true;
		this.selectedDestinyContainer = null;
	}

	public void acceptAllEmailItems(ActionEvent event){
		closeEmailsPanelPopup(null);
		MultiSelectionEmailBean bean = (MultiSelectionEmailBean)AonUtil.getRegisteredBean(AonConstants.BEAN_MULTISELECTIONEMAIL);
		List<Contact> lst = bean.getSelectedRows();
		email = "";
        for (int i = 0, max = lst.size(); i < max; i++) {
        	Contact e = lst.get(i);
        	email += e.getEmail();
        	if (i+1 < max)
        		email += ", ";
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

    private String selectedDestinyContainer;

    /**
	 * @return the selectedDestinyContainer
	 */
	public String getSelectedDestinyContainer() {
		return selectedDestinyContainer;
	}

	/**
	 * @param selectedDestinyContainer the selectedDestinyContainer to set
	 */
	public void setSelectedDestinyContainer(String selectedDestinyContainer) {
		this.selectedDestinyContainer = selectedDestinyContainer;
	}

	public SelectItem[] getDestinyContainers() {
    	return new SelectItem[]{
    			new SelectItem(CONTAINER_TO),
                new SelectItem(CONTAINER_CC),
                new SelectItem(CONTAINER_BCC)
        };
    }
    
	private static final String CONTAINER_TO = "To";
	private static final String CONTAINER_CC = "Cc";
	private static final String CONTAINER_BCC = "Bcc";
	
	//********************************************************************************************
	// EMAIL AUTOCOMPLETE
	//********************************************************************************************

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

	public void acceptToEmailItem(ActionEvent event){
		recipientsTo = acceptEmailItem(recipientsTo);
	}

	public void acceptCcEmailItem(ActionEvent event){
		recipientsCc = acceptEmailItem(recipientsCc);
	}

	public void acceptBccEmailItem(ActionEvent event){
		recipientsBcc = acceptEmailItem(recipientsBcc);
	}

	private String acceptEmailItem(String recipient){
		if (recipient==null || recipient.trim().length()==0){
			recipient = "";
		}else if (!recipient.trim().endsWith(",")){
			recipient += ", ";
		}
		recipient += email + ",";
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

}
