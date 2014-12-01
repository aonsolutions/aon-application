package com.esferalia.aon.gwt.common.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

@SuppressWarnings("serial")
public class AonRemoteServiceServlet extends RemoteServiceServlet {

	AuthPrincipal getAuthPrincipal() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		AuthPrincipal authPrincipal = (AuthPrincipal) request.getUserPrincipal();
		return authPrincipal;
	}
	
	Integer getDomainID() {
		return getAuthPrincipal().getDomainId();
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
}
