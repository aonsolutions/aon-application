package com.code.aon.webmail.bean;

import static jakarta.mail.Folder.READ_WRITE;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Quota;
import jakarta.mail.SendFailedException;
import jakarta.mail.Service;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.util.PropertiesUtil;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.sun.mail.imap.IMAPStore;

public class AonServer implements IMailConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AonServer.class);
	
	private static final String BUNDLE_RESOURCE = "com.code.aon.common.i18n.messages";
	
	private static final String NO_OUTGOING_HOST = "webmail_send_error_no_outgoinHost";
	
	private static final String SEND_FAILED = "webmail_send_error_failed";
	
	private static final String SEND_ERROR = "webmail_send_error";
	
	private static final String SEND_UNEXPECTED_ERROR = "webmail_send_error_unexpected";
	
	private Properties properties;

    private transient Store store;

    private transient Session session;
    
    private transient Transport transport;

    private IMailAccount account;
    
    private boolean quotaAware;
    
    private int numberOfMessagesToSend;
    
    private int numberOfMessagesPerTransport;
    
    /** Creates a new instance of Server */
    public AonServer(IMailAccount account){
        this.account = account;
        this.properties = calculateProperties( account );
        this.numberOfMessagesPerTransport = 1;
    }
    
    public Session getSession() {
    	if ( this.session == null ) {	
    		this.session = Session.getInstance(properties);
    	}
		return this.session;
	}

	public boolean isQuotaAware() {
    	return quotaAware;
    }
    
    private boolean calculateQuotaAware(Store store) {
    	if (store instanceof IMAPStore) {
    		IMAPStore imapStore = (IMAPStore) store;
        	try {    		
				if ( imapStore.hasCapability("QUOTA") ) {
					Quota[] quotas = imapStore.getQuota(INBOX_FOLDER_NAME);
					return ! ArrayUtils.isEmpty(quotas);
				}
    		} catch (Throwable e) {
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
		} catch (Throwable e) {
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
    	String store = properties.getProperty(MAIL_STORE_PROTOCOL);
    	return StringUtils.contains(store, IMAP);
    }
    
    private String getStoreProtocol( IMailAccount account ) {
    	String store = IMAP;
        if (! StringUtils.isEmpty(account.getProtocol())) {
        	store = account.getProtocol();
        }
        if ( account.getIncomingSecurity()==ConnectionSecurity.SSL && !StringUtils.endsWith(store, "s") ) {
        	store += "s";
        }
        return store;
    }
    
    private void setIncomingProperties( IMailAccount account, Properties values ) {
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

    private String getTransportProtocol( IMailAccount account ) {
    	String transport = SMTP;
        if (account.getOutgoingSecurity() == ConnectionSecurity.SSL) {
        	transport = SMTPS;
        }
        return transport;
    }
    
    private String getDomain (IMailAccount account) {
    	String someEmail = account.getEmail();
	    return  someEmail != null ? someEmail.substring(someEmail.indexOf("@") + 1) : "aon";
    }
    
    private void setOutcomingProperties( IMailAccount account, Properties values ) {
    	String transport = getTransportProtocol(account);
        String prefix = MAIL_PREFIX + transport;

        values.setProperty(prefix + LOCALHOST, getDomain(account));
        if (account.getOutgoingSecurity() == ConnectionSecurity.SSL) {
        	values.setProperty(prefix + SOCKET_FACTORY_CLASS, "javax.net.ssl.SSLSocketFactory");
        	values.setProperty(prefix + SOCKET_FACTORY_FALLBACK, Boolean.FALSE.toString());
        	values.setProperty(prefix + SOCKET_FACTORY_PORT, String.valueOf(account.getOutgoingPort()));
        } else if (account.getOutgoingSecurity() == ConnectionSecurity.TLS) {
        	values.put(prefix + STARTTLS_ENABLE, Boolean.TRUE.toString());
        	values.put(prefix + SSL_PROTOCOLS, TLS1_2);
        }
        // setTimeout(values, prefix);
        if (account.isOutgoingVerification()) {
        	values.put(prefix + AUTH, Boolean.TRUE.toString());
        } else {
        	values.put(prefix + AUTH, Boolean.FALSE.toString());
        }
        values.setProperty(MAIL_TRANSPORT_PROTOCOL, transport);
    }
    
    private Properties calculateProperties( IMailAccount account ) {
        Properties values = new Properties();
        if ( account.getProtocol() != null ) {
            setIncomingProperties(account, values);	
        }
        setOutcomingProperties(account, values);
        Properties override = PropertiesUtil.getProperties(WEBMAIL_PROPERTIES, DEFAULT_PROPERTIES);
        values.putAll(override);
    	return values;
    }
    
    private void setTimeout( Properties properties, String prefix ) {
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
        store = getSession().getStore();
        store.connect(account.getMailUsername(),account.getPasswordString());
        quotaAware = calculateQuotaAware(store);
    }
	
    /**
     * Closes the connection incoming mail server.
     */
    public void close() {
   		closeQuietly(this.store);
   		this.store = null;
   		closeTransport();
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
    	Folder folder = getFolder(folderName);
    	if ( folder != null ) {
    		return new AonFolder(folder, this);
    	}
    	return null;
    }
    
    public Folder getFolder(String folderName) {
        try {
        	ensureConnection();
            return store.getFolder(folderName);
        } catch (MessagingException e) {
        	LOGGER.error(e.getMessage(), e);
        }
        return null;
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
		} catch (Throwable e) {
			LOGGER.error("Creating new folder failed ", e);
		}
		return null;
	}


    public String toString() {
        return this.account.toString();
    }

    public AonMessage createAonMessage( Address from ) throws WebmailException {
    	AonMessage aonMessage = new AonMessage( new MimeMessage(getSession()) );
		aonMessage.setSender( from );
    	return aonMessage;
    }
    
    public void sendMessage(AonMessage message) throws WebmailException  {
    	sendMessage(message.getMessage());
    }

    private Transport getTransport() throws MessagingException {
    	if ( (this.transport != null) && !this.transport.isConnected() ) {
			LOGGER.warn("Transport not connected");
    		closeTransport();
    	}
    	if ( this.transport == null ) {
            this.transport = getSession().getTransport();
            this.numberOfMessagesToSend = this.numberOfMessagesPerTransport;
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
    	}
        return transport;
    }
    
    private void sendMessage(Message message) throws WebmailException {
    	if ( StringUtils.isEmpty(account.getOutgoingHost()) ) {
    		throw new WebmailException( getMessage(NO_OUTGOING_HOST, account.getName()) );
    	}
    	Transport transport = null;
        try {
            if ( message!=null && message.getFrom()!=null ) {
                CommandMap.setDefaultCommandMap(new MailcapCommandMap());
            	transport = getTransport();
                message.setSentDate(new Date());
                message.setHeader(X_MAILER, WEBMAIL_MAILER);
                message.saveChanges();
                LOGGER.info( "Sending email from {} to {}", message.getFrom(), message.getAllRecipients() );                
                transport.sendMessage(message,
                        message.getAllRecipients());
                this.numberOfMessagesToSend--;
            } else {
            	LOGGER.error("Could not send message, null message");
            }
        } catch (SendFailedException e) {
        	this.numberOfMessagesToSend = 0;
        	throw new WebmailException( getMessage(SEND_FAILED, e.getMessage()), e );
        } catch (MessagingException e) {
        	this.numberOfMessagesToSend = 0;
        	throw new WebmailException( getMessage(SEND_ERROR, e.getMessage()), e );
        } catch (Throwable e) {
        	this.numberOfMessagesToSend = 0;
        	throw new WebmailException( getMessage(SEND_UNEXPECTED_ERROR, e.getMessage()), e );       	
        } finally {
        	if ( this.numberOfMessagesToSend < 1 ) {
        		closeTransport();
        	}
        }
    }

	/**
	 * @return the account
	 */
	public IMailAccount getAccount() {
		return account;
	}
	
	public String getSentFolderName() {
		return StringUtils.trimToNull(account.getSentFolder());
	}

	public String getDraftFolderName() {
		return StringUtils.trimToNull(account.getDraftFolder());
	}
	
	public String getTrashFolderName() {
		return StringUtils.trimToNull(account.getTrashFolder());
	}

	public String getSpamFolderName() {
		return StringUtils.trimToNull(account.getSpamFolder());
	}
	
    public void importMessage( byte[] data, AonFolder destinationFolder ) throws MessagingException {
    	destinationFolder.open(READ_WRITE);
    	Folder folder = destinationFolder.getFolder();
    	folder.appendMessages(new Message[]{createMessage(data)});
    	destinationFolder.close(true);    	
    }    
    
    public MimeMessage createMessage( byte[] data ) throws MessagingException {
    	ByteArrayInputStream source = new ByteArrayInputStream(data);
    	MimeMessage message = new MimeMessage(getSession(), source);
        return message;
    }    
	
    private void closeQuietly( Service service ) {
    	if ( service!=null && service.isConnected() ) {
    		try {
				service.close();
			} catch (Throwable e) {
				LOGGER.error( "Error closing service, " + e.getMessage(), e );
			}
    	}
    }

    private String getMessage(String messageKey, Object ... arguments ) {
    	ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_RESOURCE);
    	String value = bundle.getString(messageKey);
    	if ( arguments.length > 0 ) {
    		MessageFormat mf = new MessageFormat( value );
    		value = mf.format( arguments );
    	}
    	return value;
    }

    private void closeTransport() {
    	closeQuietly(transport);
    	this.transport = null;    	
    }

	public void setNumberOfMessagesPerTransport(int numberOfMessagesPerTransport) {
		this.numberOfMessagesPerTransport = numberOfMessagesPerTransport;
	}
    
}	