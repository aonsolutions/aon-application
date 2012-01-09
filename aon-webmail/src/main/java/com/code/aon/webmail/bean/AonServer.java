package com.code.aon.webmail.bean;

import static javax.mail.Folder.HOLDS_MESSAGES;
import static javax.mail.Folder.READ_WRITE;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.SendFailedException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.sun.mail.imap.IMAPStore;

public class AonServer implements IMailConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonServer.class);

    private Store store;

    private Session session;

    private IMailAccount account;
    
    private boolean quotaAware;
    
    /** Creates a new instance of Server */
    public AonServer(IMailAccount account){
        setAccount( account );
        Properties properties = calculateProperties( account );
        this.session = Session.getInstance(properties);
    }
    
    public boolean isQuotaAware() {
    	return quotaAware;
    }
    
    private boolean calculateQuotaAware() {
    	if (store instanceof IMAPStore) {
    		IMAPStore imapStore = (IMAPStore) store;
        	try {    		
				if ( imapStore.hasCapability("QUOTA") ) {
					Quota[] quotas = imapStore.getQuota(INBOX_FOLDER_NAME);
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
			Quota[] quotas = imapStore.getQuota(INBOX_FOLDER_NAME);
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

    private static Properties calculateProperties( IMailAccount account ) {
        Properties values = System.getProperties();
        if (account.isIncomingSsl()) {
        	values.setProperty(MAIL_IMAP_SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
        	values.setProperty(MAIL_IMAP_SOCKET_FACTORY_FALLBACK, "false");
        	values.setProperty(MAIL_IMAP_PORT, String.valueOf(account.getIncomingPort()));
        	values.setProperty(MAIL_IMAP_SOCKET_FACTORY_PORT, String.valueOf(account.getIncomingPort()));
        } else {
            // otherwise log on using http, avoid using incomingSsl properties as
            // it will botch the connection .
        	values.remove(MAIL_IMAP_SOCKET_FACTORY_CLASS);
        	values.remove(MAIL_IMAP_SOCKET_FACTORY_FALLBACK);
        	values.remove(MAIL_IMAP_PORT);
        	values.remove(MAIL_IMAP_SOCKET_FACTORY_PORT);
        }
        values.setProperty(MAIL_HOST, account.getHost());
        Properties override = PropertiesUtil.getProperties(WEBMAIL_PROPERTIES, DEFAULT_PROPERTIES);
        values.putAll(override);
    	return values;
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
		LOGGER.info( "Connecting {}", account.getHost() );
        store = session.getStore();
        store.connect(account.getMailUsername(),account.getPasswordString());
        quotaAware = calculateQuotaAware();
    }
	
    /**
     * Closes the connection incoming mail server.
     */
    public void disconnect() {
    	try {
    		this.store.close();
    		this.store = null;
    	} catch (MessagingException e) {
    		LOGGER.error("Messaging Exception on disconnect method",e);
    	}
    }

    public void ensureConnection() throws MessagingException {
        if ( ! isConnected() ) {
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

    public AonMessage createAonMessage( Address from ) throws WebmailException {
    	AonMessage aonMessage = new AonMessage( new MimeMessage(session) );
		aonMessage.setSender( from );
    	return aonMessage;
    }
    
    public void sendMessage(AonMessage message) throws WebmailException  {
    	sendMessage(message.getMessage());
    }

    private static Transport getTransport( Session session, IMailAccount account ) throws MessagingException {
        Transport transport;
        if (account.isOutgoingSsl()) {
            transport = session.getTransport(SMTPS);
        } else {
            transport = session.getTransport(SMTP);
        }
        if (account.isOutgoingVerification()) {
        	session.getProperties().put(MAIL_SMTP_AUTH, "true");
            transport.connect(
            		account.getOutgoingHost(),
            		account.getOutgoingPort(),
            		account.getMailUsername(),
            		account.getPasswordString());
        } else {
        	session.getProperties().put(MAIL_SMTP_AUTH, "false");
            transport.connect(
            		account.getOutgoingHost(),
            		account.getOutgoingPort(),
                    null, null);
        }
        return transport;
    }
    
    private void sendMessage(Message message) throws WebmailException {
    	Transport transport = null;
        try {
            if ( (message != null) && (message.getFrom() != null) ) {
            	transport = getTransport(session, account);
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
        } finally {
        	closeQuietly(transport);
        }
    }

	/**
	 * @return the account
	 */
	public IMailAccount getAccount() {
		return account;
	}
    
	public void setAccount(IMailAccount account) {
		this.account = account;
	}

	public void createBasicFolders() throws MessagingException{
		if (!getRoot().getFolder(getSentFolderName()).exists()){
			createAonFolder(null, getSentFolderName(), HOLDS_MESSAGES);
		}
		if (!getRoot().getFolder(getTrashFolderName()).exists()){
			createAonFolder(null, getTrashFolderName(), HOLDS_MESSAGES);
		}
		if (!getRoot().getFolder(getDraftFolderName()).exists()){
			createAonFolder(null, getDraftFolderName(), HOLDS_MESSAGES);
		}
		if ( account.isDefault() || (!StringUtils.isEmpty(account.getSpamFolder())) ) {
			if (!getRoot().getFolder(getSpamFolderName()).exists()){
				createAonFolder(null, getSpamFolderName(), HOLDS_MESSAGES);
			}
		}
	}
	
	public String getSentFolderName() {
		return StringUtils.defaultIfEmpty(account.getSentFolder(), SENT_FOLDER_NAME);
	}

	public String getDraftFolderName() {
		return StringUtils.defaultIfEmpty(account.getDraftFolder(), DRAFT_FOLDER_NAME);
	}
	
	public String getTrashFolderName() {
		return StringUtils.defaultIfEmpty(account.getTrashFolder(), TRASH_FOLDER_NAME);
	}

	public String getSpamFolderName() {
		return StringUtils.defaultIfEmpty(account.getSpamFolder(), SPAM_FOLDER_NAME);
	}
	
    public void importMessage( byte[] data, AonFolder destinationFolder ) throws MessagingException {
    	destinationFolder.open(READ_WRITE);
    	Folder folder = destinationFolder.getFolder();
    	folder.appendMessages(new Message[]{createMessage(data)});
    	destinationFolder.close(true);    	
    }    
    
    public MimeMessage createMessage( byte[] data ) throws MessagingException {
    	ByteArrayInputStream source = new ByteArrayInputStream(data);
    	MimeMessage message = new MimeMessage(session, source);
        return message;
    }    
	
    public static void closeQuietly( Service service ) {
    	if ( (service != null) && service.isConnected() ) {
    		try {
				service.close();
			} catch (MessagingException e) {
				LOGGER.error( "Error closing service, " + e.getMessage(), e );
			}
    	}
    }
    
    public static boolean test( IMailAccount account, boolean receive, boolean send ) {
    	boolean ok = true;
        Store store = null;
        Transport transport = null;
        try {
        	Properties properties = calculateProperties( account );
            Session session = Session.getInstance(properties);
        	if ( receive ) {
    	        store = session.getStore();
    	        store.connect(account.getMailUsername(),account.getPasswordString());
    	        ok = store.isConnected();
        	}
        	if ( ok && send ) {
        		transport = getTransport(session, account);
        		ok = transport.isConnected();
        	}
        } catch ( Throwable th ) {
        	LOGGER.error( "Error testing " + account, th );
        	ok = false;
        } finally {
        	closeQuietly(store);
        	closeQuietly(transport);
        }
    	return ok;
    }
    
}
