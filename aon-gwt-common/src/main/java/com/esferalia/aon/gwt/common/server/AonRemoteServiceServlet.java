package com.esferalia.aon.gwt.common.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.esferalia.aon.gwt.common.bean.GWT;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.google.gwt.user.server.Base64Utils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

/**
 * @deprecated 
 * Esta clase accede a datos de la sesion. Utilizar AonStatelessRemoteServiceServlet 
 * 
 */
@SuppressWarnings("serial")
@Deprecated
public class AonRemoteServiceServlet extends RemoteServiceServlet {
	
	protected Integer getUserID() {
		HttpServletRequest request = getThreadLocalRequest();
		return ((AuthPrincipal)request.getUserPrincipal()).getUserId();
	}

	protected Integer getUserDomainID() {
		HttpServletRequest request = getThreadLocalRequest();
		return ((AuthPrincipal)request.getUserPrincipal()).getUserDomainId();
	}
	
	protected AuthPrincipal getAuthPrincipal() {
		HttpServletRequest req = this.getThreadLocalRequest();
		AuthPrincipal authPrincipal = (AuthPrincipal) req.getUserPrincipal();
		return authPrincipal;
	}
	
	protected Integer getDomainID() {
		return getAuthPrincipal().getDomainId();
	}

	protected String getUserLogin() {
		return getAuthPrincipal().getShortName();
	}

	protected String getEntryPoint() {
		return ((GWT) getSession().getAttribute("gwt")).getEntryPoint();
	}
	
	protected HttpSession getSession() {
		HttpServletRequest request = getThreadLocalRequest();
		return request.getSession(false);
	}
	
	protected boolean isAtEnterpriseSite() {
		return Constants.ENTERPRISE_SITE_ENTRY_POINT.equals(getEntryPoint());
	}
	
//	protected Integer getParentDomainID() {
//		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
//				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
//		return domainSwitcher.getParentDomainId();
//	}

	@Override
	protected SerializationPolicy doGetSerializationPolicy(
			HttpServletRequest request, String moduleBaseURL, String strongName) {
		return loadSerializationPolicy(this, request, moduleBaseURL, strongName);
	}
	
	protected void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	protected void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
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
	
	protected static void encodeURIComponent(String mime, InputStream is, Writer writer ) 
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

	static ClassLoader getResourceLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

}
