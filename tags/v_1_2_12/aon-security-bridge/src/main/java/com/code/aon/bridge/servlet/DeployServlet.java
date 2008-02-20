package com.code.aon.bridge.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.Utils;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Deploy application default properties. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 17-nov-2004
 * @since 1.0
 *  
 */
public class DeployServlet extends HttpServlet {

	private static final long serialVersionUID = -3412566142103096869L;

	/*(non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			IConsoleAdmin console = Utils.getSecurityConsole();
			String appId = config.getInitParameter("name");
			if ( console != null ) {
				IApplication app = 
					(IApplication) console.invoke( console.getAonSecurityName(), IOperation.GET_APPLICATION, new Object[] { appId }, new String[] { String.class.getName() });
				if ( app == null ) {
					//	Deploy application
					console.deploy( appId );
					app = (IApplication) console.invoke( console.getAonSecurityName(), IOperation.GET_APPLICATION, new Object[] { appId }, new String[] { String.class.getName() });
					IDomain domain = app.getDomain( IDomain.DEFAULT_DOMAIN_NAME );
					//	Only updates "localhost" domain.
					if ( domain != null ) {
						Object[] params = { app.getId(), domain.getId(), new Boolean( false ), IConsoleAdmin.EMPTY_STRING, domain.getAccessPolicy(), domain.getDomainApplication( appId ).getDataSourceMetaData() };
						String[] sig = { String.class.getName(), String.class.getName(), Boolean.class.getName(), String.class.getName(), IAccessPolicy.class.getName(), IDataSourceMetaData.class.getName() };
						console.invoke( console.getAonSecurityName(), IOperation.INIT_APPLICATION_DEPLOYED, params, sig);
					}
				}
			}
		} catch (DeploymentException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

}