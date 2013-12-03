package com.code.aon.webmail.bean;

import static javax.mail.Folder.HOLDS_MESSAGES;
import static javax.mail.Folder.READ_WRITE;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
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
import com.code.aon.webmail.enumeration.ConnectionSecurity;
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
    	Quota.Resource resource = null;
    	IMAPStore imapStore = (IMAPStore) store;
    	try {
			Quota[] quotas = imapStore.getQuota(INBOX_FOLDER_NAME);
			for( Quota quota : quotas ) {
				if (! ArrayUtils.isEmpty(quota.resources) ) {
					resource = quota.resources[0];
					break;
				}
			}
		} catch (MessagingException e) {
			LOGGER.error("Error getting QUOTA" + account.toString(),e);
		}
		return resource;
    }

    public boolean isQuotaExceeded() {
    	if ( this.quotaAware ) {
    		Quota.Resource quota = getQuotaResource();
    		return quota.usage >= quota.limit;
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

    public boolean isIMAP() {
    	String store = session.getProperty(MAIL_STORE_PROTOCOL);
    	return StringUtils.contains(store, IMAP);
    }
    
    private static String getStoreProtocol( IMailAccount account ) {
    	String store = IMAP;
        if (! StringUtils.isEmpty(account.getProtocol())) {
        	store = account.getProtocol();
        }
        if ( account.getIncomingSecurity()==ConnectionSecurity.SSL && !StringUtils.endsWith(store, "s") ) {
        	store += "s";
        }
        return store;
    }
    
    private static void setIncomingProperties( IMailAccount account, Properties values ) {
    	String store = getStoreProtocol(account);
        String prefix = MAIL_PREFIX + store;
        if (account.getIncomingSecurity() == ConnectionSecurity.SSL) {
        	values.setProperty(prefix + SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
        	values.setProperty(prefix + SOCKET_FACTORY_FALLBACK, Boolean.FALSE.toString());
        	values.setProperty(prefix + SOCKET_FACTORY_PORT, String.valueOf(account.getIncomingPort()));
        } else if (account.getIncomingSecurity() == ConnectionSecurity.TLS) {
        	values.setProperty(prefix + STARTTLS_ENABLE, Boolean.TRUE.toString());
        }
        if ( account.getIncomingPort() > 0 ) {
        	values.setProperty(prefix + PORT, String.valueOf(account.getIncomingPort()));
        }
        if (! StringUtils.isEmpty(account.getIncomingHost())) {
            values.setProperty(MAIL_HOST, account.getIncomingHost());	
        }
        values.setProperty(MAIL_STORE_PROTOCOL, store);	
        setTimeout(values, prefix);
    }

    private static String getTransportProtocol( IMailAccount account ) {
    	String transport = SMTP;
        if (account.getOutgoingSecurity() == ConnectionSecurity.SSL) {
        	transport = SMTPS;
        }
        return transport;
    }
    
    private static void setOutcomingProperties( IMailAccount account, Properties values ) {
    	String transport = getTransportProtocol(account);
        String prefix = MAIL_PREFIX + transport;
        if (account.getOutgoingSecurity() == ConnectionSecurity.SSL) {
        	values.setProperty(prefix + SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
        	values.setProperty(prefix + SOCKET_FACTORY_FALLBACK, Boolean.FALSE.toString());
        	values.setProperty(prefix + SOCKET_FACTORY_PORT, String.valueOf(account.getOutgoingPort()));
        } else if (account.getOutgoingSecurity() == ConnectionSecurity.TLS) {
        	values.put(prefix + STARTTLS_ENABLE, Boolean.TRUE.toString());
        }
        setTimeout(values, prefix);
        if (account.isOutgoingVerification()) {
        	values.put(prefix + AUTH, Boolean.TRUE.toString());
        } else {
        	values.put(prefix + AUTH, Boolean.FALSE.toString());
        }
        values.setProperty(MAIL_TRANSPORT_PROTOCOL, transport);
    }
    
    private static Properties calculateProperties( IMailAccount account ) {
        Properties values = new Properties();
        if ( account.getProtocol() != null ) {
            setIncomingProperties(account, values);	
        }
        setOutcomingProperties(account, values);
        Properties override = PropertiesUtil.getProperties(WEBMAIL_PROPERTIES, DEFAULT_PROPERTIES);
        values.putAll(override);
    	return values;
    }
    
    private static void setTimeout( Properties properties, String prefix ) {
    	properties.setProperty(prefix + TIMEOUT, DEFAULT_TIMEOUT);
    	properties.setProperty(prefix + CONNECTION_TIMEOUT, DEFAULT_TIMEOUT);
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
		LOGGER.info( "Connecting {}", account.getIncomingHost() );
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
        Transport transport = session.getTransport();
        if (account.isOutgoingVerification()) {
            transport.connect(
            		account.getOutgoingHost(),
            		account.getOutgoingPort(),
            		account.getMailUsername(),
            		account.getPasswordString());
        } else {
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
            if ( message!=null && message.getFrom()!=null ) {
                CommandMap.setDefaultCommandMap(new MailcapCommandMap());
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
	
	public void createBasicFolders() throws MessagingException {
		if ( isIMAP() ) {
			if (!getRoot().getFolder(getSentFolderName()).exists()){
				createAonFolder(null, getSentFolderName(), HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(getTrashFolderName()).exists()){
				createAonFolder(null, getTrashFolderName(), HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(getDraftFolderName()).exists()){
				createAonFolder(null, getDraftFolderName(), HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(getSpamFolderName()).exists()) {
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
    	if ( service!=null && service.isConnected() ) {
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
