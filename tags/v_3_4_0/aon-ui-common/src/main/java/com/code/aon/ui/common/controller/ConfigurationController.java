package com.code.aon.ui.common.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

/**
 * The Class ConfigurationController is used to set some default configurable
 * parameters of the application.
 */
public class ConfigurationController implements Serializable {
	
	private static final long serialVersionUID = -1159615075844874762L;
	
	private static final Logger LOGGER = Logger.getLogger(ConfigurationController.class.getName());
	
	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");	

	/** The application logo context relative path. */
	private String applicationLogoContextRelativePath;

	/** The application welcome context relative path. */
	private String applicationWelcomeContextRelativePath;

	/** The application report context relative path. */
	private String applicationReportContextRelativePath;

	/** The all style sheets. */
	private List<String> styleSheets;

	/** The properties. */
	private Map<String, String> properties;
	
	private boolean versionChecked;
	
	private String version;
	
	/**
	 * The Constructor.
	 */
	public ConfigurationController() {
		this.properties = new HashMap<String, String>();
		this.styleSheets = new ArrayList<String>();
	}

	/**
	 * Gets the application logo context relative path.
	 * 
	 * @return the application logo context relative path
	 */
	public String getApplicationLogoContextRelativePath() {
		return applicationLogoContextRelativePath;
	}

	/**
	 * Sets the application logo context relative path.
	 * 
	 * @param applicationLogoContextRelativePath the application logo context relative path
	 */
	public void setApplicationLogoContextRelativePath(
			String applicationLogoContextRelativePath) {
		this.applicationLogoContextRelativePath = applicationLogoContextRelativePath;
	}

	/**
	 * Gets the application report context relative path.
	 * 
	 * @return the application report context relative path
	 */
	public String getApplicationReportContextRelativePath() {
		return applicationReportContextRelativePath;
	}

	/**
	 * Sets the application report context relative path.
	 * 
	 * @param applicationReportContextRelativePath the application report context relative path
	 */
	public void setApplicationReportContextRelativePath(
			String applicationReportContextRelativePath) {
		this.applicationReportContextRelativePath = applicationReportContextRelativePath;
	}

	/**
	 * Gets the application welcome context relative path.
	 * 
	 * @return the application welcome context relative path
	 */
	public String getApplicationWelcomeContextRelativePath() {
		return applicationWelcomeContextRelativePath;
	}

	/**
	 * Sets the application welcome context relative path.
	 * 
	 * @param applicationWelcomeContextRelativePath the application welcome context relative path
	 */
	public void setApplicationWelcomeContextRelativePath(
			String applicationWelcomeContextRelativePath) {
		this.applicationWelcomeContextRelativePath = applicationWelcomeContextRelativePath;
	}

	/**
	 * Gets the properties map.
	 * 
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties map.
	 * 
	 * @param properties the properties map
	 */
	public void setProperties(Map<String, String> properties) {
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
	public String getProperty(String key) {
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
	 * Calculate application version.
	 * 
	 * @return the application version number
	 */
	public String getApplicationVersion() {
		if (! versionChecked ) {
			this.versionChecked = true;
			try {
				ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
				InputStream in = ec.getResourceAsStream("META-INF/MANIFEST.MF");
				Manifest m = new Manifest(in);
				Attributes attrs = m.getMainAttributes();
				String version = attrs.getValue("Implementation-Version");
				if(version != null){
					LOGGER.info(version);
				} else {
					LOGGER.warning("Imposible determinar la versión");
				}
			} catch (Throwable e) {
				LOGGER.warning("Imposible determinar la versión" + e.getMessage());
			}
		}
		return version;
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
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	session.invalidate();    	
    }
    
}