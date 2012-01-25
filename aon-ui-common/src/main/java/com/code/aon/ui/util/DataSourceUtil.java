package com.code.aon.ui.util;

import java.util.Properties;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
    	ExternalContext ectx = FacesContext.getCurrentInstance().getExternalContext();
    	HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
    	return DataSourceUtil.getDBProperties(request);
	}
	
	/**
	 * Gets the DB properties.
	 *
	 * @param request the request
	 * @return the DB properties
	 */
	public static Properties getDBProperties( HttpServletRequest request ) {
    	String domain = DomainResolver.getDomain(request);
    	String application = DomainResolver.getApplication(request.getContextPath());
    	BasicPrincipal bp = new BasicPrincipal(domain, application);
    	return ConnectionProvider.getDBProperties(new AuthPrincipal(bp.getName()));
	}	
    
}
