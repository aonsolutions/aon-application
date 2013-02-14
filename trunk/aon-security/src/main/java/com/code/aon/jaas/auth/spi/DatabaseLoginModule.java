package com.code.aon.jaas.auth.spi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Name;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

public class DatabaseLoginModule extends BasicDatabaseLoginModule implements IAonObjectClasses, ILdapConstants {

	@Override
	protected Connection getConnection( String username ) throws SQLException, ClassNotFoundException, LoginException {
		Connection connection = null;
		BasicLdap ldap = new BasicLdap();
		AuthPrincipal principal = new AuthPrincipal(username);
		String domain = principal.getDomain();
		String application = StringUtils.substringAfter( principal.getContext(), "/" );
		if ( log.isTraceEnabled() ) {
			log.trace("Domain: " + domain + " Application: " + application);
		}
    	Name domainApplicationDN = NameResolver.getDomainApplicationDN(domain, application);
		Entry domainApplication = ldap.get( domainApplicationDN, DOMAIN_APPLICATION );
		if ( domainApplication != null ) {
			if ( domainApplication.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
				String dataSourceValue = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
				Name dataSourceDN = NameResolver.getName(dataSourceValue);
				Entry dataSource = ldap.get( dataSourceDN, DB_CONNECTION );
				if ( dataSource != null ) {
					String driver = dataSource.getAsString(DRIVER_CLASS_NAME_ATTRIBUTE);
					Class.forName(driver);
					String user = dataSource.getAsString(USER_ID_ATTRIBUTE);
					String password = new String(dataSource.getAsByteArray(USER_PASSWORD_ATTRIBUTE));
					String url = dataSource.getAsString(LABELED_URI_ATTRIBUTE);
					connection = DriverManager.getConnection(url, user, password);
				} else {
					throw new LoginException("DataSource not found: " + dataSourceDN);
				}
			}
		} else {
			throw new LoginException("Domain Application not found: " + domainApplicationDN);
		}
    	return connection;				
	}

}
