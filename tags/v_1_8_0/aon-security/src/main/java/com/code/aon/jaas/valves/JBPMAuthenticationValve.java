/**
 * 
 */
package com.code.aon.jaas.valves;

import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.authenticator.Constants;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.JDBCRealm;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used by BPN application for user authentication in an e-Commerce area. The 
 * functionality is similar to the j_security_check action, validates the user an puts it in the 
 * session.
 * 
 * @author iayerbe
 *
 */
public class JBPMAuthenticationValve extends ValveBase {

	/** SecurityValve Log */
	private static final Log LOGGER = LogFactory.getLog( JBPMAuthenticationValve.class.getName() );

	/* (non-Javadoc)
	 * @see org.apache.catalina.valves.ValveBase#invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response)
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
LOGGER.info( "invoke:" + request.getParameter( "j_hidden" ) + "-" + request.getUserPrincipal());
		if ( request.getParameter( "j_hidden" ) != null && request.getUserPrincipal() == null) {
        	authenticate(request);
		}
		// Perform the request
        getNext().invoke(request, response);
	}

	/**
	 * Authenticate user.
	 * 
	 * @param req
	 */
	private void authenticate(Request req) {
		String username = req.getParameter( Constants.FORM_USERNAME );
		String password = req.getParameter( Constants.FORM_PASSWORD );
LOGGER.info( "authenticate:" + username + "-" + password);
		Principal principal = null;
		Realm realm = req.getContext().getRealm();
		if (realm instanceof JDBCRealm) {
			principal = realm.authenticate(username, password);
			if (principal == null)
				return;
			principal = 
				new GenericPrincipal( realm, username, password, getRoles( (JDBCRealm)realm, username ) );
		}
LOGGER.info( "authenticate:" + principal + "-" );
	    req.setAuthType( Constants.FORM_METHOD );
	    req.setUserPrincipal(principal);
		Session session = req.getSessionInternal(true);
		if (session != null) {
			session.setAuthType( Constants.FORM_METHOD );
			session.setPrincipal(principal);
	        if (username != null)
	            session.setNote( Constants.SESS_USERNAME_NOTE, username );
	        else
	            session.removeNote( Constants.SESS_USERNAME_NOTE );
	        if (password != null)
	            session.setNote( Constants.SESS_PASSWORD_NOTE, password);
	        else
	            session.removeNote( Constants.SESS_PASSWORD_NOTE );
		}
	}

    /**
     * Return the roles associated with the gven user name.
     */
    private ArrayList<String> getRoles(JDBCRealm realm, String username) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        // Number of tries is the numebr of attempts to connect to the database
        // during this login attempt (if we need to open the database)
        // This needs rewritten wuth better pooling support, the existing code
        // needs signature changes since the Prepared statements needs cached
        // with the connections.
        // The code below will try twice if there is a SQLException so the
        // connection may try to be opened again. On normal conditions (including
        // invalid login - the above is only used once.
        int numberOfTries = 2;
        while (numberOfTries>0) {
        	Connection conn = null;
            try {
                // Ensure that we have an open database connection
                conn = open(realm);
                try {
                    // Accumulate the user's roles
                    ArrayList<String> roleList = new ArrayList<String>();
                    stmt = roles( realm, conn, username );
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        String role = rs.getString(1);
                        if (null!=role) {
                            roleList.add(role.trim());
                        }
                    }
                    rs.close();
                    rs = null;
                    return (roleList);
                } finally {
                    if (rs!=null) {
                        try {
                            rs.close();
                        } catch(SQLException e) {
                        	LOGGER.info( sm.getString("jdbcRealm.abnormalCloseResultSet") );
                        }
                    }
                    conn.commit();
                }
            } catch (SQLException e) {
                // Log the problem for posterity
            	LOGGER.info( sm.getString("jdbcRealm.exception") );
                // Close the connection so that it gets reopened next time
                if (conn != null)
                    // Close this database connection, and log any errors
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                    	LOGGER.warn( sm.getString("jdbcRealm.close") ); // Just log it here
                    }
            }
            numberOfTries--;
        }
        return (null);
    }

    /**
     * Open (if necessary) and return a database connection for use by
     * this Realm.
     *
     * @exception SQLException if a database error occurs
     */
    private Connection open(JDBCRealm realm) throws SQLException {
    	Driver driver;
        // Instantiate our database driver if necessary
        try {
            Class clazz = Class.forName( realm.getDriverName() );
            driver = (Driver) clazz.newInstance();
        } catch (Throwable e) {
            throw new SQLException(e.getMessage());
        }
        // Open a new connection
        Properties props = new Properties();
        props.put( "user", realm.getConnectionName() );
        props.put( "password", realm.getConnectionPassword() );
        Connection conn = driver.connect( realm.getConnectionURL(), props );
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Return a PreparedStatement configured to perform the SELECT required
     * to retrieve user roles for the specified username.
     *
     * @param realm The JDBCRealm
     * @param conn The database connection to be used
     * @param username Username for which roles should be retrieved
     *
     * @exception SQLException if a database error occurs
     */
    private PreparedStatement roles(JDBCRealm realm, Connection conn, String username)
    			throws SQLException {
        PreparedStatement stmt = null;
        if (stmt == null) {
            StringBuffer sb = new StringBuffer("SELECT ");
            sb.append( realm.getRoleNameCol() );
            sb.append(" FROM ");
            sb.append( realm.getUserRoleTable());
            sb.append(" WHERE ");
            sb.append( realm.getUserNameCol() );
            sb.append(" = ?");
            stmt = conn.prepareStatement(sb.toString());
        }
        stmt.setString(1, username);
        return stmt;
    }

}
