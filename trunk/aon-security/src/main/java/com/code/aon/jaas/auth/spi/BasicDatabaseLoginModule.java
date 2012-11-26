package com.code.aon.jaas.auth.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.transaction.Transaction;

import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;
import org.jboss.tm.TransactionDemarcationSupport;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.vendor.jboss.JBossSessionManagerMBean;

/**
 * A JDBC based login module that supports authentication and role mapping.
 * It is based on two logical tables:
 * <ul>
 * <li>Principals(PrincipalID text, Password text)
 * <li>Roles(PrincipalID text, Role text, RoleGroup text)
 * </ul>
 * <p>
 * LoginModule options:
 * <ul>
 * <li><em>dsJndiName</em>: The name of the DataSource of the database
 * containing the Principals, Roles tables
 * <li><em>principalsQuery</em>: The prepared statement query, equivalent to:
 * <pre>
 *    "select Password from Principals where PrincipalID=?"
 * </pre>
 * <li><em>rolesQuery</em>: The prepared statement query, equivalent to:
 * <pre>
 *    "select Role, RoleGroup from Roles where PrincipalID=?"
 * </pre>
 * </ul>
 *
 * @author <a href="mailto:on@ibis.odessa.ua">Oleg Nitz</a>
 * @author Scott.Stark@jboss.org
 * @version $Revision: 57203 $
 */
public abstract class BasicDatabaseLoginModule extends UsernamePasswordLoginModule {

	/** The sql query to obtain the user password */
	protected String principalsQuery = "select password from user where login=SUBSTRING_INDEX(?,'@',1) and active=1";
	/** The sql query to obtain the user roles */
	protected String rolesQuery = "select 'User', 'Roles' from user where login=SUBSTRING_INDEX(?,'@',1) and active=1";
	/** Whether to suspend resume transactions during database operations */
	protected boolean suspendResume = true;
	/** Tells the MBean SessionManager ObjectName . */
	protected String sessionManagerObjectName = JBossSessionManagerMBean.OBJECT_NAME;	

	/**
	 * Initialize this LoginModule.
	 * 
	 * @param options
	 *            - dsJndiName: The name of the DataSource of the database
	 *            containing the Principals, Roles tables principalsQuery: The
	 *            prepared statement query, equivalent to:
	 *            "select Password from Principals where PrincipalID=?"
	 *            rolesQuery: The prepared statement query, equivalent to:
	 *            "select Role, RoleGroup from Roles where PrincipalID=?"
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		Object tmp = options.get("principalsQuery");
		if (tmp != null)
			principalsQuery = tmp.toString();
		tmp = options.get("rolesQuery");
		if (tmp != null)
			rolesQuery = tmp.toString();
		tmp = options.get("suspendResume");
		if( tmp != null )
			suspendResume = Boolean.valueOf(tmp.toString()).booleanValue();
		if (log.isTraceEnabled()) {
			log.trace("DatabaseServerLoginModule, principalsQuery=" + principalsQuery);
			log.trace("rolesQuery=" + rolesQuery);
			log.trace("suspendResume="+suspendResume);
		}
	}

	/**
	 * Get the expected password for the current username available via the
	 * getUsername() method. This is called from within the login() method after
	 * the CallbackHandler has returned the username and candidate password.
	 * 
	 * @return the valid password String
	 */
	protected String getUsersPassword() throws LoginException {
		boolean trace = log.isTraceEnabled();
		String username = getUsername();
		String password = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Transaction tx = null;
		if (suspendResume) {
			tx = TransactionDemarcationSupport.suspendAnyTransaction();
			if (trace)
				log.trace("suspendAnyTransaction");
		}

		try {
			conn = getConnection(username);
			// Get the password
			if (trace)
				log.trace("Excuting query: " + principalsQuery
						+ ", with username: " + username);
			ps = conn.prepareStatement(principalsQuery);
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (rs.next() == false) {
				if (trace)
					log.trace("Query returned no matches from db");
				throw new FailedLoginException( "No matching username found in Principals" );
			}

			password = rs.getString(1);
			password = convertRawPassword(password);
			if (trace)
				log.trace("Obtained user password");
		} catch (SQLException ex) {
			LoginException le = new LoginException("Query failed");
			le.initCause(ex);
			throw le;
		} catch (ClassNotFoundException e) {
			LoginException le = new LoginException("JDBC driver not found");
			le.initCause(e);
			throw le;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex) {
				}
			}
			if (suspendResume) {
				TransactionDemarcationSupport.resumeAnyTransaction(tx);
				if (log.isTraceEnabled())
					log.trace("resumeAnyTransaction");
			}
		}
		return password;
	}

	/** Execute the rolesQuery against the dsJndiName to obtain the roles for
    the authenticated user.
     
    @return Group[] containing the sets of roles
	 * @throws ClassNotFoundException 
    */
   private Group[] getRoleSets(String username, String rolesQuery, boolean suspendResume)
      throws LoginException
   {
      boolean trace = log.isTraceEnabled();
      Connection conn = null;
      HashMap setsMap = new HashMap();
      PreparedStatement ps = null;
      ResultSet rs = null;

      Transaction tx = null;
      if (suspendResume)
      {
         tx = TransactionDemarcationSupport.suspendAnyTransaction();
         if( trace )
            log.trace("suspendAnyTransaction");
      }

      try
      {
         conn = getConnection(username);
         // Get the user role names
         if (trace)
            log.trace("Excuting query: "+rolesQuery+", with username: "+username);
         ps = conn.prepareStatement(rolesQuery);
         try
         {
            ps.setString(1, username);
         }
         catch(ArrayIndexOutOfBoundsException ignore)
         {
            // The query may not have any parameters so just try it
         }
         rs = ps.executeQuery();
         if( rs.next() == false )
         {
            if( trace )
               log.trace("No roles found");
            if( getUnauthenticatedIdentity() == null ) {
            	throw new FailedLoginException( "No matching username found in Roles" );
            }
            /* We are running with an unauthenticatedIdentity so create an
               empty Roles set and return.
            */
            Group[] roleSets = { new SimpleGroup("Roles") };
            return roleSets;
         }

         do
         {
            String name = rs.getString(1);
            String groupName = rs.getString(2);
            if( groupName == null || groupName.length() == 0 )
               groupName = "Roles";
            Group group = (Group) setsMap.get(groupName);
            if( group == null )
            {
               group = new SimpleGroup(groupName);
               setsMap.put(groupName, group);
            }

            try
            {
               Principal p = createIdentity(name);
               if( trace )
                  log.trace("Assign user to role " + name);
               group.addMember(p);
            }
            catch(Exception e)
            {
               log.debug("Failed to create principal: "+name, e);
            }
         } while( rs.next() );
      } catch(SQLException ex) {
    	  LoginException le = new LoginException("Query failed");
    	  le.initCause(ex);
    	  throw le;
      } catch (ClassNotFoundException e) {
    	  LoginException le = new LoginException("JDBC driver not found");
    	  le.initCause(e);
    	  throw le;
      } finally {
         if( rs != null )
         {
            try
            {
               rs.close();
            }
            catch(SQLException e)
            {}
         }
         if( ps != null )
         {
            try
            {
               ps.close();
            }
            catch(SQLException e)
            {}
         }
         if( conn != null )
         {
            try
            {
               conn.close();
            }
            catch (Exception ex)
            {}
         }
         if (suspendResume)
         {
            TransactionDemarcationSupport.resumeAnyTransaction(tx);
            if( trace )
               log.trace("resumeAnyTransaction");
         }
      }
      
      Group[] roleSets = new Group[setsMap.size()];
      setsMap.values().toArray(roleSets);
      return roleSets;
   }
	
	/**
	 * Execute the rolesQuery against the dsJndiName to obtain the roles for the
	 * authenticated user.
	 * 
	 * @return Group[] containing the sets of roles
	 */
	protected Group[] getRoleSets() throws LoginException {
		String username = getUsername();
		if (log.isTraceEnabled())
			log.trace("getRoleSets using rolesQuery: " + rolesQuery
					+ ", username: " + username);
		Group[] roleSets = getRoleSets(username, rolesQuery, suspendResume);
		return roleSets;
	}

	/**
	 * A hook to allow subclasses to convert a password from the database into a
	 * plain text string or whatever form is used for matching against the user
	 * input. It is called from within the getUsersPassword() method.
	 * 
	 * @param rawPassword
	 *            - the password as obtained from the database
	 * @return the argument rawPassword
	 */
	protected String convertRawPassword(String rawPassword) {
		return rawPassword;
	}
	
	@Override
	public boolean login() throws LoginException {
		try {
			return super.login();
		} catch ( LoginException e ) {
			AuthPrincipal principal = new AuthPrincipal( getUsername() );
			settingFailedLoginException( "aon_login_err_0", principal.getShortName() );
			throw e;
		}
	}

	private MBeanServer getMBeanServer() {
		return MBeanServerLocator.locateJBoss();
	}	
	
    /**
     * Set Failed Login Exception.
     * 
     * @param failedLoginException
     * @throws FailedLoginException
     */
	private void settingFailedLoginException(String message, Object obj) {
    	try {
    		ObjectName name = new ObjectName(this.sessionManagerObjectName);
    		AuthenticationLoginException e = new AuthenticationLoginException( message, obj); 
    		getMBeanServer().invoke( name, "fillLastLoginException", new Object[] { e }, new String[] { AuthenticationLoginException.class.getName() } );
    	} catch (Throwable th) {
    		log.error( "Error setting FailedLoginException", th );
        }
	}	

	protected abstract Connection getConnection( String username ) throws SQLException, ClassNotFoundException, LoginException;
	
}
