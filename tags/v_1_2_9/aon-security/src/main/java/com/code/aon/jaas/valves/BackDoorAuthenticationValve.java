/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.servlet.ServletException;

import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This class validates a user forward using an encrypted file from one context to another
 * in Tomcat servlet container.
 *  
 * @author Consulting & Development. Iñaki Ayerbe - 22/10/2007
 */
public class BackDoorAuthenticationValve extends ValveBase {

	/** Default directory for encrypted serialized sessions file. */
	public static final String RESOURCES_DEFAULT_DIR= "/home/COMMON-RESOURCES/ENC/";
	/** Serialized file extension. */
	public static final String SER_EXT= ".enc";
	/** Serialized session identifier name. */
	public static final String SER_SESSION_ID= "serSessionId";
	/** Maintain the Catalina Request for programmatic web login */
	public static ThreadLocal<Request> activeRequest = new ThreadLocal<Request>();
	/** BackDoorAuthenticationValve Log */
	private static final Log LOGGER = LogFactory.getLog( BackDoorAuthenticationValve.class.getName() );
	/** List of serialized sessions, destroyed sessions are removed. */
	private static final List<String> serializedSessions = new ArrayList<String>(); 

	/* (non-Javadoc)
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		Session session = request.getSessionInternal( false );
		if ( session != null ) {
			String nonHashedPassword = (String) session.getNote( Constants.SESS_PASSWORD_NOTE );
			if ( nonHashedPassword != null ) {
				String username = (String) session.getNote( Constants.SESS_USERNAME_NOTE );
				if ( !serializedSessions.contains( session.getId() ) ) {
					serialize( session.getId(), username, nonHashedPassword );
					serializedSessions.add( session.getId() );
				}
			}
		}
		activeRequest.set( request );// Set the active request
		try {
			getNext().invoke(request, response);// Perform the request
		} finally {
			activeRequest.set( null );
		}
	}

	/**
	 * Serializes Principal.
	 * 
	 * @param id
	 * @param username
	 * @param nonHashedPassword
	 * @throws IOException
	 */
	private void serialize(String id, String username, String nonHashedPassword) throws IOException {
		FileOutputStream ostream = null;
		try {
			String path = RESOURCES_DEFAULT_DIR + id + SER_EXT;
			ostream = new FileOutputStream( path );
			/* Create the output stream */
			ObjectOutputStream oopstream = new ObjectOutputStream( ostream );
			/* Create the serialized principal */
			BackDoorPrincipal bdp = new BackDoorPrincipal( username, nonHashedPassword );
			SecretKey key = DesEncrypter.getSecretKeyInstance( SER_EXT + id );
			// Create encrypter/decrypter class
			DesEncrypter encrypter = new DesEncrypter(key);
			SealedObject so = encrypter.encrypt( bdp );
			oopstream.writeObject( so );
			oopstream.flush();
		} catch(IOException e) {
			LOGGER.fatal( e.getMessage() );
			throw e;
		} catch (InvalidKeySpecException e) {
			LOGGER.fatal( e.getMessage() );
		} catch (NoSuchAlgorithmException e) {
			LOGGER.fatal( e.getMessage() );
		} finally {
			if ( ostream != null ) 
				ostream.close();
		}
	}

	/**
	 * Removes serialized session and its bound encrypted file.
	 * 
	 * @param id
	 * @return
	 */
	public static final boolean remove(String id) {
		serializedSessions.remove( id );
		String path = RESOURCES_DEFAULT_DIR + id + SER_EXT;
		return new File( path ).delete();
	}

}
