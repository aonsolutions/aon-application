package com.code.aon.messaging.sms;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.messaging.event.ISenderListener;
import com.code.aon.messaging.event.SenderEvent;
import com.esendex.sdk.ems.soapinterface.EsendexHeader;
import com.esendex.sdk.ems.soapinterface.MessageStatusCode;
import com.esendex.sdk.ems.soapinterface.MessageType;
import com.esendex.sdk.ems.soapinterface.Messagesubmission;
import com.esendex.sdk.ems.soapinterface.SendServiceLocator;
import com.esendex.sdk.ems.soapinterface.SendServiceSoap_BindingStub;

public class Sender implements IConstants, Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger( Sender.class.getName() );

	List<ISenderListener> listeners;
	Properties bootstrap;
	EsendexHeader header;
	List<Message> messages;
	Thread thread = null;

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 * @throws SOAPException
	 */
	public Sender() throws IOException, SOAPException {
		listeners = new ArrayList<ISenderListener>();
		bootstrap = new Properties();
		InputStream is = getClass().getResourceAsStream( "/bootstrap.properties" );
		bootstrap.load(is);
	}

	/**
	 * Add a ISenderListener to the listener list.
	 * 
	 * @param l
	 */
	public void addSenderListener(ISenderListener l) {
		if (l == null) {
		    return;
		}

		listeners.add( l );
	}

	/**
	 * Remove a ISenderListener from the listener list.
	 *  
	 * @param l
	 */
	public void removeSenderListener(ISenderListener l) {
		if (l == null) {
		    return;
		}

		listeners.remove( l );
	}

	/**
	 * Initializes messages list and <code>EsendexHeader</code> class.
	 * @throws SOAPException 
	 * 
	 * @throws SOAPException 
	 */
	public void init() throws SOAPException {
		init( bootstrap.getProperty( USERNAME ) );
	}

	/**
	 * Initializes messages list and <code>EsendexHeader</code> class.
	 * 
	 * @throws SOAPException 
	 */
	public void init(String username) throws SOAPException {
		messages = new ArrayList<Message>();
		if ( header == null ) {
			header = 
				new EsendexHeader( "sms@" + username, bootstrap.getProperty( PASSWORD ), bootstrap.getProperty( ACCOUNT ) );
		}
	}

	/**
	 * Return the message status of the id passed by parameter.
	 * 
	 * @param id
	 * @return
	 */
	public String getMessageStatus(String id) {
		SendServiceLocator locator = new SendServiceLocator();
		try {
			SendServiceSoap_BindingStub service = (SendServiceSoap_BindingStub)locator.getSendServiceSoap();
			service.setHeader(header);
			MessageStatusCode status = service.getMessageStatus( id );
			return status.getValue();
		} catch (ServiceException e) {
			LOGGER.error( e.getMessage() );
		} catch (RemoteException e) {
			LOGGER.error( e.getMessage() );
		}
		return null;
	}

	/**
	 * Send a message to a single recipient.
	 * 
	 * @param recipient
	 * @param message
	 */
	public synchronized void send(String recipient, String message) {
		Message msg = new Message();
		msg.add( recipient );
		msg.getInfo().setMessage( message );
		messages.add( msg );
		send();
	}

	/**
	 * Send a message to multiple recipients.
	 * 
	 * @param recipients
	 * @param message
	 */
	public synchronized void send(List<String> recipients, String message) {
		Message msg = new Message();
		Iterator<String> iter = recipients.iterator();
		while (iter.hasNext()) {
			String elem = iter.next();
			msg.add( elem );
		}
		msg.getInfo().setMessage( message );
		messages.add( msg );
		send();
	}

	/**
	 * Send a message.
	 * 
	 * @param msg
	 */
	public synchronized void send(Message msg) {
		messages.add( msg );
		send();
	}

	/**
	 * Send a message list in a batch mode.
	 * 
	 * @param l
	 */
	public synchronized void sendBatch(List<Message> l) {
		messages = l;
		send();
	}

	public static void main(String[] args) {
		Message message = new Message();
		String username = null;
		for(int i=0; i < args.length; i++) {
			if ( args[i].equals( "-originator" ) ) {
				message.getInfo().setOriginator( args[ ++i ] );
			}
			if ( args[i].equals( "-recipients" ) ) {
				StringTokenizer st = new StringTokenizer( args[ ++i ] );
				while ( st.hasMoreTokens() ) {
					String recipient = st.nextToken( ";" );
					message.add( recipient );
				}
			}
			if ( args[i].equals( "-username" ) ) {
				username = args[ ++i ];
			}
			if ( args[i].equals( "-message" ) ) {
				message.getInfo().setMessage( args[ ++i ] );
			}
		}
		Sender sender;
		try {
			sender = new Sender();
			if ( username == null )
				sender.init();
			else
				sender.init( username );
			sender.send( message );
		} catch (IOException e) {
			LOGGER.error( e.getMessage() );
		} catch (SOAPException e) {
			LOGGER.error( e.getMessage() );
		}
	}

	@Override
	public void run() {
		LOGGER.info( "Start sending message" );
		SendServiceLocator locator = new SendServiceLocator();
		try {
			SendServiceSoap_BindingStub service = (SendServiceSoap_BindingStub)locator.getSendServiceSoap();
			service.setHeader(header);
			if ( messages.size() > 1 ) {
				//TODO
				List<Messagesubmission> l = new ArrayList<Messagesubmission>();
				Iterator<Message> iter = messages.iterator();
				while ( iter.hasNext() ) {
					Message elem = iter.next();
					l.add( elem.getMessagesubmission() );
				}
				String[] status = service.sendMessageBatch( (Messagesubmission[]) l.toArray() );
				LOGGER.info( status.toString() );
			} else {
				Message msg = messages.get( 0 );
				if ( msg != null ) {
					SenderEvent event = new SenderEvent( msg );
					MessageInfo info = msg.getInfo();
					Calendar c = Calendar.getInstance();
					c.setTime( new Date() );
					info.setSentat( c );
					if ( msg.isMultiple() ) {
						String[] response;
						if ( msg.isFull() ) {
							response = 
								service.sendMessageMultipleRecipientsFull( 
										info.getOriginator(), 
										msg.toRecipientsArray(), 
										info.getMessage(), MessageType.Text, info.getValidityperiod() );
						} else {
							response = 
								service.sendMessageMultipleRecipients( 
										msg.toRecipientsArray(), 
										info.getMessage(), MessageType.Text );
						}
						msg.fillIds( response );
					} else {
						String response = null;
						if ( msg.isFull() ) {
							response = 
								service.sendMessageFull( info.getOriginator(), msg.getRecipients().get( 0 ).getName(), 
										info.getMessage(), MessageType.Text, info.getValidityperiod() );
						} else {
							response = 
								service.sendMessage( msg.getRecipients().get( 0 ).getName(), info.getMessage(), MessageType.Text);
						}
						msg.fillIds( new String[] {response} );
					}
					c.setTime( new Date() );
					info.setReceivedat( c );
					fireMessageSent( event );
				}
			}
		} catch (ServiceException e) {
			LOGGER.error( e.getMessage() );
			fireMessageFailed( new SenderEvent( messages.get( 0 ) ) );
		} catch (RemoteException e) {
			LOGGER.error( e.getMessage() );
			fireMessageFailed( new SenderEvent( messages.get( 0 ), 2 ) );
		} finally {
			messages = new ArrayList<Message>();
		}
	}

	/**
	 * Fire an existing SenderEvent to any registered listeners.
	 *  
	 * @param event
	 */
	private void fireMessageSent(SenderEvent event) {
		if ( listeners != null ) {
			Iterator<ISenderListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				ISenderListener l = iter.next();
				l.messageSent( event );
			}
		}
	}

	/**
	 * Fire an existing SenderEvent to any registered listeners.
	 *  
	 * @param event
	 */
	private void fireMessageFailed(SenderEvent event) {
		if ( listeners != null ) {
			Iterator<ISenderListener> iter = listeners.iterator();
			while (iter.hasNext()) {
				ISenderListener l = iter.next();
				l.messageFailed( event );
			}
		}
	}

	/**
	 * Send message.
	 */
	private void send() {
		thread = new Thread( this );
		thread.start() ;
	}

}
