package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Enterprise;
import com.code.aon.config.Domain;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.bean.GWT;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.google.gwt.user.server.Base64Utils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

@SuppressWarnings("serial")
public class AonRemoteServiceServlet extends RemoteServiceServlet {

	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	Integer getUserID() {
		HttpServletRequest request = getThreadLocalRequest();
		return ((AuthPrincipal)request.getUserPrincipal()).getUserId();
	}

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	AuthPrincipal getAuthPrincipal() {
		HttpServletRequest request = getThreadLocalRequest();
		return ((AuthPrincipal)request.getUserPrincipal());
	}

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	Integer getPersonID() {
		return null;
	}
	
	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	String getEntryPoint(){
		return ((GWT) getSession().getAttribute("gwt")).getEntryPoint();
	}

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	boolean isAtEnterpriseSite(){
		return Constants.ENTERPRISE_SITE_ENTRY_POINT.equals(getEntryPoint());
	}

	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	Integer getParentDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getParentDomainId();
	}

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	HttpSession getSession() {
		HttpServletRequest request = getThreadLocalRequest();
		return request.getSession(false);
	}
	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}

	@Override
	protected SerializationPolicy doGetSerializationPolicy(
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		return loadSerializationPolicy(this, request, moduleBaseURL, strongName);
	}
	
	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet,
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		// The request can tell you the path of the web app relative to the
		// container root.
		String contextPath = request.getContextPath();

		String modulePath = null;
		if (moduleBaseURL != null) {
			try {
				modulePath = new URL(moduleBaseURL).getPath();
			} catch (MalformedURLException ex) {
				// log the information, we will default
				servlet.log("Malformed moduleBaseURL: " + moduleBaseURL, ex);
			}
		}

		SerializationPolicy serializationPolicy = null;

		/*
		 * Check that the module path must be in the same web app as the servlet
		 * itself. If you need to implement a scheme different than this,
		 * override this method.
		 */
		if (modulePath == null || !modulePath.startsWith(contextPath)) {
			String message = "ERROR: The module path requested, "
					+ modulePath
					+ ", is not in the same web application as this servlet, "
					+ contextPath
					+ ".  Your module may not be properly configured or your client and server code maybe out of date.";
			servlet.log(message);
		} else {
			// Strip off the context path from the module base URL. It should be
			// a strict prefix.
			String contextRelativePath = modulePath.substring(contextPath
					.length());
			
			String serializationPolicyFilePath = SerializationPolicyLoader
					.getSerializationPolicyFileName(contextRelativePath
							+ strongName);

			// Remove Leading "/"
			if ( serializationPolicyFilePath.startsWith("/") ) {
				serializationPolicyFilePath = serializationPolicyFilePath.substring(1);
			}
			// Open the RPC resource file and read its contents.
			InputStream is = getResourceLoader().getResourceAsStream(
					serializationPolicyFilePath);
			try {
				if (is != null) {
					try {
						serializationPolicy = SerializationPolicyLoader
								.loadFromStream(is, null);
					} catch (ParseException e) {
						servlet.log("ERROR: Failed to parse the policy file '"
								+ serializationPolicyFilePath + "'", e);
					} catch (IOException e) {
						servlet.log("ERROR: Could not read the policy file '"
								+ serializationPolicyFilePath + "'", e);
					}
				} else {
					String message = "ERROR: The serialization policy file '"
							+ serializationPolicyFilePath
							+ "' was not found; did you forget to include it in this deployment?";
					servlet.log(message);
				}
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// Ignore this error
					}
				}
			}
		}
	    return serializationPolicy;
	}
	
	static ClassLoader getResourceLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	static void encodeURIComponent(String mime, InputStream is, Writer writer ) 
	throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		int read = 0; 
		byte buffer [] = new byte [3 * 50];
		while ( ( read = is.read(buffer) ) > 0 ) {
			byte data [] = Arrays.copyOfRange(buffer, 0, read);
			
			String safe = Base64Utils.toBase64(data);
			String base64 = safe.replace('$', '+');
			base64 = base64.replace('_', '/');
			
			writer.write(base64);
		}
		
		
	}
	static void encodeURIComponent(String mime, byte data [], Writer writer ) 
	throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		String safe = Base64Utils.toBase64(data);
		String base64 = safe.replace('$', '+');
		base64 = base64.replace('_', '/');
		writer.write(base64);
	}

	
	static void encodeURIComponent(String mime, String base64, Writer writer ) 
	throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		base64 = base64.replace('$', '+');
		base64 = base64.replace('_', '/');
		writer.write(base64);
	}
	// ------------------------------------------------------------------------

	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static Integer getDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getDomainId();
	}


	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static Integer[] getChildDomainIDs(Integer domainId) throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(Domain.class);
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.DOMAIN_PARENT_ID),
				domainId );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return new Integer[]{};
		
		Integer[] ids = new Integer[tos.size()];
		for (int i = 0; i < ids.length; i++ ) 
			ids[i] = ((Domain)tos.get(i)).getId();
		
		return ids;
	}


	/**
	 * We assume here that one domain one enterprise. 
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static Integer getEnterpriseID() throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.company.Enterprise.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN),
				getDomainID() );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return null;
		
		return ((Enterprise) tos.get(0)).getId();
		
	}

	public static Enterprise getEnterprise(String domainName) throws SQLException {
		Connection connection = null;
		try {
		
			connection = AonServletUtils.getConnection(domainName);
			AONContext aonContext = new AONContext(connection);
			RegistryRecord record = 
			aonContext.getDslContext()
				.select()
				.from(com.esferalia.aon.jooq.tables.Registry.REGISTRY)
				.join(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE)
				.on(com.esferalia.aon.jooq.tables.Registry.REGISTRY.ID.eq(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE.REGISTRY))
				.join(com.esferalia.aon.jooq.tables.Domain.DOMAIN)
				.on(com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE.DOMAIN.eq(com.esferalia.aon.jooq.tables.Domain.DOMAIN.ID))
				.where(com.esferalia.aon.jooq.tables.Domain.DOMAIN.NAME.eq(domainName))
				.fetchOneInto(com.esferalia.aon.jooq.tables.Registry.REGISTRY);
			
			com.code.aon.registry.Registry registry = 
					new com.code.aon.registry.Registry();
			registry.setId(record.getId());
			registry.setDomain(record.getDomain());
			registry.setAlias(record.getAlias());
			registry.setDocument(record.getDocument());
			registry.setName(record.getName());
			if ( record.getNationality() != null )
				registry.setNationality(Country.obtainCountry(record.getNationality()));
			if ( record.getDocumentCountry() != null )
				registry.setDocumentCountry(Country.obtainCountry(record.getDocumentCountry()));
			if ( record.getDocumentType() != null  && 
				record.getDocumentType() >= 0 && 
				record.getDocumentType() < DocumentType.values().length) 
				registry.setDocumentType(DocumentType.values()[record.getDocumentType()]);

			Enterprise enterprise = new Enterprise();
			enterprise.setId(record.getId());
			enterprise.setDomain(record.getDomain());
			enterprise.setRegistry(registry);

			return enterprise;
		
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			if ( connection != null )
				connection.close();
		}
	}

	/*
	 * We assume here that one domain one enterprise. 
	 */
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static Enterprise getHEnterprise() throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.company.Enterprise.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN),
				getDomainID() );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return null;
		
		return ((Enterprise) tos.get(0));
		
	}
	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static EnterpriseCCC getDefaultHEnterpriseCCC() throws ManagerBeanException{
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.EnterpriseCCC.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_CCC_DOMAIN),
				getDomainID() );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return null;
		
		return ((EnterpriseCCC) tos.get(0));
		
	}
	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static Integer [] getEnterpriseIDs() throws ManagerBeanException {
		
		List<Integer> ids  = getEnterpriseIDs(getDomainID());
		
		for ( Integer child: getChildDomainIDs(getDomainID()) )
			ids.addAll(getEnterpriseIDs(child));
		
		return ids.toArray(new Integer[ids.size()]);
	}
	
	/**
	 * @deprecated Don't use this method.
	 */
	@Deprecated
	protected static List<Integer> getEnterpriseIDs(Integer domainId) throws ManagerBeanException {
		
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.company.Enterprise.class);

		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN),
				domainId );
		
		List<ITransferObject> tos = beanManager.getList(criteria);
		
		 List<Integer> ids  = new ArrayList<Integer>(tos.size());
		 for ( ITransferObject to : tos )
			ids.add(((Enterprise)to).getId());
		
		return ids;

	}

}
