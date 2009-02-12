package com.code.aon.webmail.bean;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailException;

public class AonServer {
	
	private static final Logger LOGGER = Logger.getLogger(AonServer.class.getName());
	
	private static final String IMAP = "imap";
	
    private Store store;

    private Session session;

    private MailAccount account;
    
    private boolean ensure_connection;
    
    /** Creates a new instance of Server */
    public AonServer(MailAccount account){
        setAccount( account );
        this.ensure_connection = true;
    }

	/**
     * Is this service currently connected?
     * @return true if the service is connected, false if it is not connected
     */
    private boolean isConnected(){
        return store != null && store.isConnected();
    }

	/**
     * Connects the incoming mail server information specified by the MailAccount
     * class.  This method will return true if the connection has already made
     * or if the connection process was successful.
     *
     * @return true if the connection succeded; otherwise, false.
     */
	public synchronized boolean connect() {
        try {
            Properties mailProperties = System.getProperties();

            // mailProperties.setProperty("mail.debug", "true");
            // setup SSL connection factory
            if (account.isIncomingSsl()) {
                mailProperties.setProperty("mail.imap.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
                mailProperties.setProperty("mail.imap.socketFactory.fallback",
                        "false");
                mailProperties.setProperty("mail.imap.port",
                        String.valueOf(account.getIncomingPort()));
                mailProperties.setProperty("mail.imap.socketFactory.port",
                        String.valueOf(account.getIncomingPort()));
            }
            // otherwise log on using http, avoid using incomingSsl properties as
            // it will botch the connection .
            else {
                mailProperties.remove("mail.imap.socketFactory.class");
                mailProperties.remove("mail.imap.socketFactory.fallback");
                mailProperties.remove("mail.imap.port");
                mailProperties.remove("mail.imap.socketFactory.port");
            }
            URLName url = new URLName(IMAP, account.getHost(), -1, "INBOX", account.getMailUsername(),account.getPasswordString());
            session = Session.getInstance(mailProperties, null);
            store = session.getStore(url);
            store.connect();

            return true;
        }catch (NoSuchProviderException e) {
        	LOGGER.log(Level.SEVERE,"Connection Error - No such provider for " + account.toString(),e);
            return false;
        }catch(AuthenticationFailedException e){
        	LOGGER.log(Level.SEVERE,"Connection Error - Authentication error " + account.toString(),e);
            return false;
        }catch (MessagingException e) {
        	LOGGER.log(Level.SEVERE,"Connection Error - Messaging Exception " + account.toString(),e);
            return false;
        }catch (Throwable e) {
        	LOGGER.log(Level.SEVERE,"Connection Error - Misc. Exception " + account.toString(),e);
            return false;
        }
    }

	
    /**
     * Closes the connection incoming mail server.
     */
    public void disconnect() {
    	try {
    		store.close();
    	} catch (MessagingException e) {
    		LOGGER.log(Level.SEVERE,"Messaging Exception on disconnect method",e);
    	}
    }

    protected void ensureConnection() {
        if (ensure_connection && (! isConnected())) {
            if (!connect()) {
        		LOGGER.log(Level.INFO,"Error connecting to " + account.getHost() );
            }
        }
    }
    public Folder getRoot() {
    	ensureConnection();
        try {
            return store.getDefaultFolder();
        } catch (MessagingException e) {
        	LOGGER.log(Level.SEVERE,"getRoot failed " , e);
            return null;
        }
    }
    
    public AonFolder getAonFolder(String folderName) {
    	ensureConnection();
        try {
            return new AonFolder(store.getFolder(folderName));
        } catch (MessagingException e) {
        	LOGGER.log(Level.SEVERE,"getAonFolder failed " , e);
            return null;
        }
    }
    
	public AonFolder createAonFolder(AonFolder parent, String folderName, int type) {
		try {
			Folder new_folder;
			if (parent == null){
				new_folder = getRoot().getFolder(folderName);	
			}else{
				new_folder = getRoot().getFolder(parent.getFolder().getName()+getRoot().getSeparator()+folderName);
			}
			if (!new_folder.exists()) {
				LOGGER.log(Level.INFO, "Creating folder : " + folderName);
				new_folder.create(type);
			} else {
				LOGGER.log(Level.INFO, "Found folder : " + folderName);
			}
			return new AonFolder(new_folder);
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE,
					"Creating new folder failed ", e);
		}
		return null;
	}


    public String toString() {
        return this.account.toString();
    }
        
    public AonMessage createAonMessage( String address, String personal ) throws AddressException, MessagingException, WebmailException, UnsupportedEncodingException{
    	AonMessage aonMessage = new AonMessage();
		aonMessage.setMessage(new MimeMessage(session));
		aonMessage.setSender(new InternetAddress(address, personal));
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
                transport.sendMessage(message,
                        message.getAllRecipients());
            } else {
            	LOGGER.log(Level.SEVERE,"Could not send message, null message");
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

	public void createBasicFolders(){
		try{
			if (!getRoot().getFolder(AonFolder.SENT_FOLDER_NAME).exists()){
				createAonFolder(null, AonFolder.SENT_FOLDER_NAME, Folder.HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(AonFolder.TRASH_FOLDER_NAME).exists()){
				createAonFolder(null, AonFolder.TRASH_FOLDER_NAME, Folder.HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(AonFolder.DRAFT_FOLDER_NAME).exists()){
				createAonFolder(null, AonFolder.DRAFT_FOLDER_NAME, Folder.HOLDS_MESSAGES);
			}
			if (!getRoot().getFolder(AonFolder.SPAM_FOLDER_NAME).exists()){
				createAonFolder(null, AonFolder.SPAM_FOLDER_NAME, Folder.HOLDS_MESSAGES);
			}
		} catch (MessagingException e) {
			LOGGER.severe( e.getMessage() );
		}
	}
	
}
