package com.code.aon.webmail.bean;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailException;
import com.sun.mail.imap.IMAPStore;

public class AonServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonServer.class);
	
	private static final String X_MAILER = "X-Mailer";
	
	private static final String WEBMAIL_MAILER = "OfficeWeb - AonWebMail 4.11.0";
	
	private static final String IMAP = "imap";
	
    private Store store;

    private Session session;

    private MailAccount account;
    
    private boolean ensure_connection;
    
    private boolean quotaAware;
    
    /** Creates a new instance of Server */
    public AonServer(MailAccount account){
        setAccount( account );
        this.ensure_connection = true;
    }
    
    public boolean isQuotaAware() {
    	return quotaAware;
    }
    
    private boolean calculateQuotaAware() {
    	if (store instanceof IMAPStore) {
    		IMAPStore imapStore = (IMAPStore) store;
        	try {    		
				if ( imapStore.hasCapability("QUOTA") ) {
					Quota[] quotas = imapStore.getQuota(AonFolder.INBOX_FOLDER_NAME);
					return ! ArrayUtils.isEmpty(quotas);
				}
    		} catch (MessagingException e) {
    			LOGGER.error("Error checking is QUOTA enabled" + account.toString(),e);
    		}
		}
    	return false;
    }
    
    public Quota.Resource getQuotaResource() {
    	IMAPStore imapStore = (IMAPStore) store;
    	try {
			Quota[] quotas = imapStore.getQuota(AonFolder.INBOX_FOLDER_NAME);
			for( Quota quota : quotas ) {
				for( Quota.Resource resource : quota.resources ) {
					return resource;	
				}
			}
		} catch (MessagingException e) {
			LOGGER.error("Error getting QUOTA" + account.toString(),e);
		}
		return null;
    }

    public boolean isQuotaExceeded() {
    	if ( this.quotaAware ) {
    		Quota.Resource quota = getQuotaResource();
    		return ( quota.usage >= quota.limit );
    	}
    	return false;
    }
    
	/**
     * Is this service currently connected?
     * @return true if the service is connected, false if it is not connected
     */
    public boolean isConnected(){
        return store != null && store.isConnected();
    }

	/**
     * Connects the incoming mail server information specified by the MailAccount
     * class.  This method will return true if the connection has already made
     * or if the connection process was successful.
     *
     * @return true if the connection succeded; otherwise, false.
	 * @throws MessagingException 
     */
	public void connect() throws MessagingException {
        Properties mailProperties = System.getProperties();

        // mailProperties.setProperty("mail.debug", "true");
        // setup SSL connection factory
        mailProperties.setProperty( "mail.mime.decodetext.strict", "false" );
        if (account.isIncomingSsl()) {
            mailProperties.setProperty("mail.imap.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            mailProperties.setProperty("mail.imap.socketFactory.fallback",
                    "false");
            mailProperties.setProperty("mail.imap.port",
                    String.valueOf(account.getIncomingPort()));
            mailProperties.setProperty("mail.imap.socketFactory.port",
                    String.valueOf(account.getIncomingPort()));
        } else {
            // otherwise log on using http, avoid using incomingSsl properties as
            // it will botch the connection .
            mailProperties.remove("mail.imap.socketFactory.class");
            mailProperties.remove("mail.imap.socketFactory.fallback");
            mailProperties.remove("mail.imap.port");
            mailProperties.remove("mail.imap.socketFactory.port");
        }
        URLName url = new URLName(IMAP, account.getHost(), -1, "INBOX", account.getMailUsername(),account.getPasswordString());
        session = Session.getInstance(mailProperties, null);
        store = session.getStore(url);
        store.connect();
        quotaAware = calculateQuotaAware();
    }
	
    /**
     * Closes the connection incoming mail server.
     */
    public void disconnect() {
    	try {
    		store.close();
    	} catch (MessagingException e) {
    		LOGGER.error("Messaging Exception on disconnect method",e);
    	}
    }

    private void ensureConnection() throws MessagingException {
        if (ensure_connection && (! isConnected())) {
        	connect();
        }
    }
    public Folder getRoot() {
        try {
        	ensureConnection();
            return store.getDefaultFolder();
        } catch (MessagingException e) {
        	LOGGER.error("getRoot failed " , e);
            return null;
        }
    }
    
    public AonFolder getAonFolder(String folderName) {
        try {
        	ensureConnection();
            return new AonFolder(store.getFolder(folderName), this);
        } catch (MessagingException e) {
        	LOGGER.error("getAonFolder failed " , e);
            return null;
        }
    }
    
	public AonFolder createAonFolder(AonFolder parent, String folderName, int type) {
		try {
			Folder new_folder;
			if (parent == null){
				new_folder = getRoot().getFolder(folderName);	
			}else{
				new_folder = getRoot().getFolder(parent.getFolder().getFullName()+getRoot().getSeparator()+folderName);
			}
			if (!new_folder.exists()) {
				LOGGER.info( "Creating folder : {}",folderName);
				new_folder.create(type);
			} else {
				LOGGER.info("Found folder : {}",folderName);
			}
			return new AonFolder(new_folder, this);
		} catch (MessagingException e) {
			LOGGER.error("Creating new folder failed ", e);
		}
		return null;
	}


    public String toString() {
        return this.account.toString();
    }
        
    public AonMessage createAonMessage( String address, String personal ) throws UnsupportedEncodingException, WebmailException {
    	return createAonMessage(new InternetAddress(address, personal));
    }

    public AonMessage createAonMessage( Address from ) throws WebmailException {
    	AonMessage aonMessage = new AonMessage();
		aonMessage.setMessage(new MimeMessage(session));
		aonMessage.setSender( from );
    	return aonMessage;
    }
    
    public void sendMessage(AonMessage message) throws WebmailException  {
    	sendMessage(message.getMessage());
    }

    private void sendMessage(Message message) throws WebmailException {
        try {
            Transport transport;
            if (account.isOutgoingSsl()) {
                transport = session.getTransport("smtps");
            } else {
                transport = session.getTransport("smtp");
            }
            if (account.isOutgoingVerification()) {
            	session.getProperties().put("mail.smtp.auth", "true");
                transport.connect(
                		account.getOutgoingHost(),
                		account.getOutgoingPort(),
                		account.getMailUsername(),
                		account.getPasswordString());
            } else {
            	session.getProperties().put("mail.smtp.auth", "false");
                transport.connect(
                		account.getOutgoingHost(),
                		account.getOutgoingPort(),
                        null, null);
            }
            if (transport.isConnected() &&
                    message != null &&
                    message.getFrom() != null) {
                message.setSentDate(new Date());
                message.setHeader(X_MAILER, WEBMAIL_MAILER);
                transport.sendMessage(message,
                        message.getAllRecipients());
            } else {
            	LOGGER.error("Could not send message, null message");
            }
        } catch (SendFailedException e) {
        	throw new WebmailException( "Message send failed", e );
        } catch (MessagingException e) {
        	throw new WebmailException( "Message was not sent correctly", e );
        } catch (Throwable e) {
        	throw new WebmailException( "Unexpected error sending the message", e );        	
        }
    }

	/**
	 * @return the account
	 */
	public MailAccount getAccount() {
		return account;
	}
    
	public void setAccount(MailAccount account) {
		this.account = account;
	}

	public void createBasicFolders() throws MessagingException{
		if (!getRoot().getFolder(getSentFolderName()).exists()){
			createAonFolder(null, getSentFolderName(), Folder.HOLDS_MESSAGES);
		}
		if (!getRoot().getFolder(getTrashFolderName()).exists()){
			createAonFolder(null, getTrashFolderName(), Folder.HOLDS_MESSAGES);
		}
		if (!getRoot().getFolder(getDraftFolderName()).exists()){
			createAonFolder(null, getDraftFolderName(), Folder.HOLDS_MESSAGES);
		}
		if ( account.isDefault() || (!StringUtils.isEmpty(account.getSpamFolder())) ) {
			if (!getRoot().getFolder(getSpamFolderName()).exists()){
				createAonFolder(null, getSpamFolderName(), Folder.HOLDS_MESSAGES);
			}
		}
	}
	
	public String getSentFolderName() {
		return StringUtils.defaultIfEmpty(account.getSentFolder(), AonFolder.SENT_FOLDER_NAME);
	}

	public String getDraftFolderName() {
		return StringUtils.defaultIfEmpty(account.getDraftFolder(), AonFolder.DRAFT_FOLDER_NAME);
	}
	
	public String getTrashFolderName() {
		return StringUtils.defaultIfEmpty(account.getTrashFolder(), AonFolder.TRASH_FOLDER_NAME);
	}

	public String getSpamFolderName() {
		return StringUtils.defaultIfEmpty(account.getSpamFolder(), AonFolder.SPAM_FOLDER_NAME);
	}
	
}
