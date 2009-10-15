package com.code.aon.ui.ecommerce.hibernate;

import java.util.Properties;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.naming.Name;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.common.dao.hibernate.DefaultConfigurationFactory;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DomainConfigurationFactory extends DefaultConfigurationFactory implements IAonObjectClasses, ILdapConstants {

	private static final Logger LOGGER = Logger.getLogger(DomainConfigurationFactory.class.getName());
	
	private static final IConfigurationFactory SINGLETON = new DomainConfigurationFactory();

    private static final String DOMAIN_RESOLVER = "domainResolver";
    	
    /**
     * Instantiates a new default configuration factory.
     */
    protected DomainConfigurationFactory() {
    }
    
    /**
     * Gets the single instance of DefaultConfigurationFactory.
     * 
     * @return single instance of DefaultConfigurationFactory
     */
    public static IConfigurationFactory getInstance() {
    	return SINGLETON;
    }
    
	public String getApplicationId( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		return application;
	}    
	
    private Properties getProperties( String domain, String context ) {
    	Properties properties = new Properties();
    	String application = getApplicationId(context);
    	LOGGER.info( "Domain: " + domain + " Application: " + application );
    	BasicLdap ldap = new BasicLdap();
    	Name domainApplicationDN = NameResolver.getDomainApplicationDN(domain, application);
		Entry domainApplication = ldap.get( domainApplicationDN, DOMAIN_APPLICATION );
		if ( domainApplication != null ) {
			if ( domainApplication.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
				String dataSourceValue = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
				Name dataSourceDN = NameResolver.getName(dataSourceValue);
				Entry dataSource = ldap.get( dataSourceDN, DB_CONNECTION );
				if ( dataSource != null ) {
					String userName = dataSource.getAsString(USER_ID_ATTRIBUTE);
					properties.put(Environment.USER, userName);
					byte[] password = dataSource.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
					properties.put(Environment.PASS, new String(password));
					String url = dataSource.getAsString(LABELED_URI_ATTRIBUTE);
					properties.put(Environment.URL, url);
					String driverClassName = dataSource.getAsString(DRIVER_CLASS_NAME_ATTRIBUTE);
					properties.put(Environment.DRIVER, driverClassName);
				} else {
					LOGGER.severe( "DataSource not found: " + dataSourceDN );
				}
			}
		} else {
			LOGGER.severe( "Domain Application not found: " + domainApplicationDN );
		}
    	return properties;
    }
    
    /**
     * Complete configuration.
     * 
     * @param configuration the configuration
     */
    protected void completeConfiguration( Configuration configuration ) {
    	DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER);
    	String domain = resolver.getDomain();
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	String context = ctx.getExternalContext().getRequestContextPath();
    	configuration.addProperties( getProperties(domain, context) );
    }

}
