package com.code.aon.common.dao.hibernate;

import java.util.Properties;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.common.util.PrincipalUtil;
import com.code.aon.jaas.auth.AuthPrincipal;

/**
 * A strategy for obtaining JDBC connections.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27/03/2008
 * 
 */

public class C3P0ConnectionProvider extends org.hibernate.connection.C3P0ConnectionProvider {

	/**
	 * Logger initialization
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(C3P0ConnectionProvider.class);
	
	
	@Override
	public void configure(Properties props) throws HibernateException {
		AuthPrincipal principal = getAuthPrincipal();
		String domain = principal.getDomain();
		String application = principal.getContext();
		Properties connectionProperties = ConnectionProvider.getDBProperties(domain, application);
		LOGGER.info( "Connection properties: {}", connectionProperties );
		props.putAll( connectionProperties );
		super.configure(props);						
	}
	
	protected AuthPrincipal getAuthPrincipal() {
		return PrincipalUtil.getAuthPrincipal();
	}
	
}
