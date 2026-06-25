package com.code.aon.ui.common.controller;

import static com.code.aon.ui.common.ICommonConstants.ON_LOGOUT;

import java.io.Serializable;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.util.PrincipalUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;

/**
 * The Class ConfigurationController is used to set some default configurable
 * parameters of the application.
 */
public class ConfigurationController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Locale SPANISH = new Locale("es", "ES");
	private static final Locale BASQUE = new Locale("eu", "ES");
	private static final Locale GALICIAN = new Locale("gl", "ES");
	private static final Locale CATALAN = new Locale("ca", "ES");
	
	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");
	
	private static final LocaleElement[] LOCALES = new LocaleElement[] {
		new LocaleElement(SPANISH), 
		new LocaleElement(Locale.ENGLISH), 
		new LocaleElement(Locale.FRENCH), 
		new LocaleElement(Locale.GERMAN),
		new LocaleElement(BASQUE), 
		new LocaleElement(CATALAN), 
		new LocaleElement(GALICIAN)
	};
	
    public static final int DEFAULT_PAGE_LIMIT = 20;	

    public static final int DEFAULT_LINES_PAGE_LIMIT = 10;

	/** The all style sheets. */
	private List<String> styleSheets;

	/** The properties. */
	private Map<String, Object> properties;
	
	private Map<String,Map<String,Object>> bean;	
	
	private String currentAction = "home";
	
	private transient List<SelectItem> localeList;
	
	private AuthPrincipal principal;
	
	private Integer pageLimit = DEFAULT_PAGE_LIMIT;
	
	private Integer linesPageLimit = DEFAULT_LINES_PAGE_LIMIT;
	
	private Locale locale;
	
	/**
	 * The Constructor.
	 */
	public ConfigurationController() {
		this.properties = new HashMap<String, Object>();
		this.styleSheets = new ArrayList<String>();
		this.principal = resolvePrincipal();
		this.locale = AonUtil.getCurrentLocale();
	}
	
	private AuthPrincipal resolvePrincipal() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest(); 
		Principal principal = request.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			return (AuthPrincipal) principal;
		}
		return PrincipalUtil.getAuthPrincipal();
	}

	public Integer getPageLimit() {
		if(pageLimit == null) {
			pageLimit = DEFAULT_PAGE_LIMIT;
		}
		return pageLimit;
	}

	public void setPageLimit(Integer pageLimit) {
		if (pageLimit == null) {
			pageLimit = DEFAULT_PAGE_LIMIT;
		}
		this.pageLimit = pageLimit;
	}

	public Integer getLinesPageLimit() {
		if(linesPageLimit == null) {
			linesPageLimit = DEFAULT_LINES_PAGE_LIMIT;
		}
		return linesPageLimit;
	}

	public void setLinesPageLimit(Integer linesPageLimit) {
		this.linesPageLimit = linesPageLimit;
	}

	public AuthPrincipal getAuthPrincipal() {
		return this.principal;
	}

	/**
	 * Gets the properties map.
	 * 
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties map.
	 * 
	 * @param properties the properties map
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Sets the property in the properties map.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Gets the property.
	 * 
	 * @param key the key
	 * 
	 * @return the property linked with 'key' in the map
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * Gets the style sheets.
	 * 
	 * @return the style sheets
	 */
	public List<String> getStyleSheets() {
		return styleSheets;
	}

	
	/**
	 * Sets the style sheets.
	 * 
	 * @param styleSheets the new style sheets
	 */
	public void setStyleSheets(List<String> styleSheets) {
		this.styleSheets = styleSheets;
	}

    /**
     * Gets the current date.
     * 
     * @return the current date
     */
    public String getCurrentDate() {
        return FORMATTER.format(new Date()).toUpperCase();
    }

    /**
     * Gets the session id.
     * 
     * @return the session id
     */
    public String getSessionId() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	return session.getId();
    }
    
    public void savePageLimit( ActionEvent event ) {
    	System.out.println("Working!");
    	
    	//save configuration to db
    }
    
    private void onLogout( ActionEvent event ) {
    	String onLogout = (String) getProperty(ON_LOGOUT);
    	if (! StringUtils.isEmpty(onLogout) ) {
			String expression = "#{" + onLogout + "}";
			AonUtil.actionListener(expression, event);
    	}
    }
    
    /**
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	onLogout(event);
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	session.invalidate();    	
    	context.responseComplete();
    }
    
	/**
	 * Gets the bean.
	 * 
	 * @return the bean
	 */
	public Map<String, Map<String, Object>> getBean() {
		if ( bean == null ) {
			this.bean = BeanConfiguration.getInstance().getBeanCopy();
		}
		return bean;
	}
	
	/**
	 * Gets the current action.
	 *
	 * @return the current action
	 */
	public String getCurrentAction() {
		return currentAction;
	}

	/**
	 * Sets the current action.
	 *
	 * @param currentAction the new current action
	 */
	public void setCurrentAction(String currentAction) {
		this.currentAction = currentAction;
	}

	private String getURL( String action ) {
		StringBuffer url = new StringBuffer();
		url.append("http://help.aonsolutions.es/ayuda/resumen.php?application=");
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		String application = StringUtils.stripStart(ec.getRequestContextPath(), "/" );		
		url.append( application );
		if ( action != null ) {
			url.append("&action_id=").append(action);
		}
		return url.toString();		
	}
	
	/**
	 * Gets the help url.
	 *
	 * @return the help url
	 */
	public String getHelpURL() {
		return "http://faqs.aonsolutions.es"; //getURL(this.currentAction);
	}
	
	public LocaleElement[] getLocales() {
		return LOCALES;
	}

	public List<SelectItem> getLocaleList() {
    	if ( localeList == null ) {
    		localeList = new LinkedList<SelectItem>();
    		for( LocaleElement element : getLocales() ) {
	            SelectItem item = new SelectItem(element.getId(), element.getDisplayName());
	            localeList.add( item );
    		}
        }
        return localeList;
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}