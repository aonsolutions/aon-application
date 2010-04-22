package es.code.ecm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

import es.code.ecm.ContentRepository;
import es.code.ecm.IConstants;
import es.code.ecm.repository.RepositoryInfo;

/**
 * MessagesUtil includes some common message methods.
 */
public class ECMUtil {

	/** Suffix for message details (<code>_detail</code>) */
	private static final String DETAIL_SUFFIX = "_detail";

	/** Default bundle for messages (<code>javax.faces.Messages</code>) */
	private static final String DEFAULT_BUNDLE = "javax.faces.Messages";

	/** Key to identify an aon error. (value is ""aon_error"") */
	public static final String AON_ERROR = "aon_error";

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param message
	 *            Message to be added to the messages collection
	 */

	public static void addFatalMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_FATAL);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addFatalMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_FATAL);
	}
	/**
	 * Adds <code>message</code> as an error message to the messages
	 * collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addErrorMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addErrorMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds <code>message</code> as an info message to the messages collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addInfoMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addInfoMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds <code>message</code> as an warning message to the messages
	 * collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addWarningMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_WARN);
	}
	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addWarningMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * @param message
	 * @param severity
	 */
	public static void addMessage(String[] message, FacesMessage.Severity severity) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		FacesMessage msg = getMessage(ctx, ECMUtil.AON_ERROR, message);
		msg.setSeverity(severity);
		ctx.addMessage(ECMUtil.AON_ERROR, msg);
	}

	/**
	 * @param context
	 * @param messageId
	 * @param params
	 * @return d
	 */
	public static FacesMessage getMessage(FacesContext context, String messageId, Object params[]) {
		if (context == null || messageId == null)
			throw new NullPointerException(" context " + context + " messageId " + messageId);
		Locale locale = getCurrentLocale( context );
		if (null == locale)
			throw new NullPointerException(" locale " + locale);
		String bundleName = context.getApplication().getMessageBundle();
		FacesMessage message = getMessage(bundleName, locale, messageId, params);
		if (message != null) {
			return message;
		}
		// TODO /FIX: Note that this has fallback behavior to default Locale for
		// message,
		// but similar behavior above does not. The methods should probably
		// behave
		locale = Locale.getDefault();
		return getMessage(bundleName, locale, messageId, params);

	}

	/**
	 * 
	 * @param context
	 * @return currently applicable Locale for this request.
	 */
	public static Locale getCurrentLocale(FacesContext context) {
		Locale locale;
		if (context != null && context.getViewRoot() != null) {
			locale = context.getViewRoot().getLocale();
			if (locale == null)
				locale = Locale.getDefault();
		} else {
			locale = Locale.getDefault();
		}

		return locale;
	}

	/**
	 * @param bundleName
	 * @param locale
	 * @param messageId
	 * @param params
	 * @return message
	 */
	public static FacesMessage getMessage(String bundleName, Locale locale, String messageId, Object params[]) {
		String summary = null;
		String detail = null;
		ResourceBundle bundle = null;

		if (bundleName != null) {
			try {
				bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName));
				summary = bundle.getString(messageId);
			} catch (MissingResourceException e) {
				// NoOp
			}
		}

		if (summary == null) {
			try {
				bundle = ResourceBundle.getBundle(DEFAULT_BUNDLE, locale, getCurrentLoader(DEFAULT_BUNDLE));
				if (bundle == null) {
					throw new NullPointerException();
				}
				summary = bundle.getString(messageId);
			} catch (MissingResourceException e) {
				// NoOp
			}
		}

		if (summary == null) {
			summary = messageId;
		}

		if (bundle == null) {
			throw new NullPointerException("Unable to locate ResrouceBundle: bundle is null");
		}
		summary = substituteParams(locale, summary, params);

		try {
			detail = substituteParams(locale, bundle.getString(messageId + DETAIL_SUFFIX), params);
		} catch (MissingResourceException e) {
			// NoOp
		}

		return new FacesMessage(summary, detail);
	}

	/**
	 * Gets the ClassLoader associated with the current thread. Returns the
	 * class loader associated with the specified default object if no context
	 * loader is associated with the current thread.
	 * 
	 * @param defaultObject
	 *            The default object to use to determine the class loader (if
	 *            none associated with current thread.)
	 * @return ClassLoader
	 */
	protected static ClassLoader getCurrentLoader(Object defaultObject) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = defaultObject.getClass().getClassLoader();
		}
		return loader;
	}

	/**
	 * Uses <code>MessageFormat</code> and the supplied parameters to fill in
	 * the param placeholders in the String.
	 * 
	 * @param locale
	 *            The <code>Locale</code> to use when performing the
	 *            substitution.
	 * @param msgtext
	 *            The original parameterized String.
	 * @param params
	 *            The params to fill in the String with.
	 * @return The updated String.
	 */
	public static String substituteParams(Locale locale, String msgtext, Object params[]) {
		String localizedStr = null;
		if (params == null || msgtext == null)
			return msgtext;
		StringBuffer b = new StringBuffer(100);
		MessageFormat mf = new MessageFormat(msgtext);
		if (locale != null) {
			mf.setLocale(locale);
			b.append(mf.format((params)));
			localizedStr = b.toString();
		}
		return localizedStr;
	}

	/**
	 * Downloads the document.
	 * 
	 * @param context
	 * @param name
	 * @param mimetype
	 * @param is
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public static void download(FacesContext context, String name, String mimetype, InputStream is) 
				throws RepositoryException, IOException {
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
//		Now we create some variables we will use for writting the file to the response
		int read = 0;
		byte[] bytes = new byte[1024];
//Now set the content type for our response, be sure to use the best suitable content type depending on your file
//the content type presented here is ok for, lets say, text files and others (like  CSVs, PDFs)
		response.setContentType( mimetype );
		String fileName = name;
		String agent = req.getHeader("USER-AGENT");
        response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
        response.setHeader("Cache-Control", "max-age=0, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        if(agent != null && -1 != agent.indexOf("MSIE")) {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
        } else if(agent != null && -1 != agent.indexOf("Mozilla")) {
//            fileName = MimeUtility.encodeText(fileName, "UTF-8", "B");
        }
//This is another important attribute for the header of the response
//Here fileName, is a String with the name that you will suggest as a name to save as
//I use the same name as it is stored in the file system of the server.
		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");		
//Stream we will use to write the file bytes to our response.
		OutputStream os = response.getOutputStream();
//While there are still bytes in the file, read them and write them to our OutputStream
		while((read = is.read(bytes)) != -1){
			os.write(bytes,0,read);
		}		
		response.flushBuffer();
		context.responseComplete();
	}

	/**
	 * Parse Jackrabbit exception given in english, an internationalized the message.
	 *  
	 * @param message
	 * @return
	 */
	public static final String parseJackrabbitException(String message) {
		ResourceBundle bundle = ResourceBundle.getBundle( IConstants.ECM_BUNDLE_NAME, Locale.ENGLISH );
		String key = null, value = null;
		boolean found = false;
		Enumeration<String> en = bundle.getKeys();
		while (en.hasMoreElements() && !found) {
			key = en.nextElement();
			if ( key.indexOf( "jackrabbit" ) > -1 ) {
				value = bundle.getString( key );
				if ( message.indexOf( value ) > -1 ) {
					found = true;
				}
			}
		}
		if ( found ) {
			FacesContext context = FacesContext.getCurrentInstance();
			bundle = ResourceBundle.getBundle( IConstants.ECM_BUNDLE_NAME, ECMUtil.getCurrentLocale( context ) );
			return message.replaceAll( value, bundle.getString( key ) );
		}
		return message;
	}

	public static final RepositoryInfo getRepositoryInfo(Properties bootstrap) {
    	RepositoryInfo ri = new RepositoryInfo();
    	ri.setProps( bootstrap );
//    	String ctx = bootstrap.getProperty( IProvider.REPOSITORY_CONNECTION_CONTEXT );
//    	try {
    		// TODO Does not work in Linux because getApplicationDomains method is not supported 
//    		UserManager um = new UserManager();
//			ri.setWorkspaces( um.getApplicationDomains( ctx ) );
//		} catch (UnsupportedOperationException e) {
			ri.addWorkspace( IDomain.DEFAULT_DOMAIN_NAME );
//		} catch (DeploymentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return ri;
	}

	/**
	 * Get AonUser defined in a LDAP entry.
	 * 
	 * @param domain
	 * @param userId
	 * @return
	 */
	public static final Entry getAonUser(String domain, String userId) {
		domain = ( domain.equals( ContentRepository.DEFAULT_WORKSPACE ) )? IDomain.DEFAULT_DOMAIN_NAME: domain;
		Entry entry = null;
		BasicLdap ldap = new BasicLdap();
		try {
			DistinguishedName dn = AonDN.getUserDN( domain, userId );
			String filter = LdapSession.getObjectClass("aonUser");
			entry = ldap.getLdapSession().get(dn.toString(), filter);
		} catch ( LdapException e ) {
			throw new AbortProcessingException( "Error getting user for " + userId + ". " + e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return entry;
	}		
    
}
