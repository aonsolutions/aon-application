package com.code.aon.ui.util;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.StatelessSession;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.controller.DomainResolver;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DataSourceUtil {
	
	/**
	 * Gets the dB properties.
	 * 
	 * @return the dB properties
	 */
	public static Properties getDBProperties() {
		String server = AonUtil.getServerName();
		String context = AonUtil.getContextPath();
    	return DataSourceUtil.getDBProperties(server, context, AonUtil.isSkipLdap());
	}
	
	/**
	 * Gets the DB properties.
	 *
	 * @param server the server
	 * @param context the context
	 * @param skipLdap the skip ldap
	 * @return the DB properties
	 */
	public static Properties getDBProperties( String server, String context, boolean skipLdap ) {
    	String domain = DomainResolver.getDomain(server, skipLdap);
    	String application = DomainResolver.getApplication(context);
    	BasicPrincipal bp = new BasicPrincipal(domain, application);
    	return ConnectionProvider.getDBProperties(new AuthPrincipal(bp.getName()));
	}	
 
	public static Integer getDomain( StatelessSession session, String host, boolean skipLdap ) {
		Long count = (Long) session.createQuery("SELECT count(id) FROM Domain").uniqueResult();
		if ( count == 1 ) {
			return (Integer) session.createQuery("SELECT id FROM Domain").uniqueResult();
		}
		String domainName = DomainResolver.getDomain(host, skipLdap);		
		Integer domainId = null;
		do {
			Query query = session.createQuery("SELECT id FROM Domain d WHERE d.name = ?");
			domainId = (Integer) query.setString(0, domainName).uniqueResult();
			if ( domainId == null ) {
				domainName = StringUtils.substringAfter(domainName, ".");	
			}
		} while ( (domainId == null) && StringUtils.contains(domainName, '.') );
		return domainId;
	}
	
}