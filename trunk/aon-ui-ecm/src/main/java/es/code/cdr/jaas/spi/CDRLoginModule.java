/**
 * 
 */
package es.code.cdr.jaas.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.jackrabbit.core.security.SystemPrincipal;
import org.apache.jackrabbit.core.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 29/06/2007
 *
 */
public class CDRLoginModule extends AbstractLoginModule {

    /** Obtiene un logger apropiado. */
	protected static final Logger LOGGER = LoggerFactory.getLogger( CDRLoginModule.class.getName() );

	/** The login identity */
	private Principal identity;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#login()
	 */
	@Override
	public boolean login() throws LoginException {
		if (this.callbackHandler == null) {
			throw new LoginException("Error: no CallbackHandler available for JCRModule");
		}
		String username = getUsername();
		if ( username != null ) {
			try {
				identity = createIdentity(username);
			} catch(Exception e) {
				LOGGER.warn( "Failed to create principal" );
				throw new LoginException("Failed to create principal: "+ e.getMessage());
			}
			super.loginOk = true;
		}
		if (!super.loginOk) {
			throw new LoginException( "failed to authenticate " + username );
		}
		return super.loginOk;
	}

	/**
	 * This method is called by <code>login()</code> and returns username and password.
	 * 
	 * @return String, username
	 * @exception LoginException, thrown if CallbackHandler is not set or fails.
	 */
	protected String getUsername() throws LoginException {
		//	prompt for a username and password
		if (callbackHandler == null) {
			throw new LoginException( "Error: no CallbackHandler available to collect authentication information" );
		}
		NameCallback nc = new NameCallback( "User name: ", "guest" );
		Callback[] callbacks = { nc };
		String username = null;
		try {
			callbackHandler.handle(callbacks);
			username = nc.getName();
		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException( "CallbackHandler does not support: " + uce.getCallback() );
		}
		return username;
	}
	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#createIdentity(java.lang.String)
	 */
	@Override
	protected Principal createIdentity(String username) throws Exception {
		String system = new SystemPrincipal().getName();
		return ( username.equals( system ) )? new SystemPrincipal(): new UserPrincipal( username );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getActiveUsers(java.lang.String, java.lang.String)
	 */
	@Override
	protected Integer getActiveUsers(String host, String context)
			throws LoginException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getIdentity()
	 */
	@Override
	protected Principal getIdentity() {
		return identity;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getRoleSets()
	 */
	@Override
	protected Group[] getRoleSets() throws LoginException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#roles4Subject(java.util.Set)
	 */
	@Override
	protected void roles4Subject(Set<Principal> principals)
			throws LoginException {
		// TODO Auto-generated method stub

	}

}
