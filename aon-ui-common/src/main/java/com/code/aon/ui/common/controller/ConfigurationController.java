package com.code.aon.ui.common.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * The Class ConfigurationController is used to set some default configurable
 * parameters of the application.
 */
public class ConfigurationController {
	
	private static final Logger LOGGER = Logger.getLogger(ConfigurationController.class.getName());

	/** The Constant STYLE_SHEET_DIRECTORY. */
	private static final String STYLE_SHEET_DIRECTORY = "/css/";

	/** The Constant STYLE_SHEET_EXTENSION. */
	private static final String STYLE_SHEET_EXTENSION = ".css";

	/** The Constant AON_STYLE_SHEET. */
	private static final String AON_STYLE_SHEET = STYLE_SHEET_DIRECTORY + "aon"
			+ STYLE_SHEET_EXTENSION;

	/** The Constant LAYOUT_STYLE_SHEET. */
	private static final String LAYOUT_STYLE_SHEET = STYLE_SHEET_DIRECTORY
			+ "layout" + STYLE_SHEET_EXTENSION;

	/** The Constant CUSTOMIZED_STYLE_SHEET_PREFIX. */
	private static final String CUSTOMIZED_STYLE_SHEET_PREFIX = "/aon-";

	/** The application logo context relative path. */
	private String applicationLogoContextRelativePath;

	/** The application welcome context relative path. */
	private String applicationWelcomeContextRelativePath;

	/** The application report context relative path. */
	private String applicationReportContextRelativePath;

	/** The style of the application. */
	private String style;

	/** The user style sheets. */
	private List<String> userStyleSheets;

	/** The all style sheets. */
	private List<String> styleSheets;

	/** The properties. */
	private Map<String, String> properties;
	
	/** The user bundles. */
	private Map<String, String> applicationBundles;

	/**
	 * The Constructor.
	 */
	public ConfigurationController() {
		this.properties = new HashMap<String, String>();
		this.applicationBundles = new HashMap<String, String>();
		calculateStyleSheets();
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
	 * Gets the user bundles.
	 * 
	 * @return the user bundles
	 */
	public Map<String, String> getApplicationBundles() {
		return applicationBundles;
	}

	/**
	 * Sets the user bundles.
	 * 
	 * @param userBundles the user bundles
	 */
	public void setApplicationBundles(Map<String, String> userBundles) {
		this.applicationBundles = userBundles;
	}
	
	/**
	 * @return list of userBundles
	 */
	public List<Entry<String, String>> getApplicationBundleList(){
		return new ArrayList<Entry<String, String>>( getApplicationBundles().entrySet() );
	}

	/**
	 * Gets the style.
	 * 
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the style.
	 * 
	 * @param style the style
	 */
	public void setStyle(String style) {
		this.style = style;
		calculateStyleSheets();		
	}

	/**
	 * Gets the style path.
	 * 
	 * @return the style path
	 */
	public String getStylePath() {
		return STYLE_SHEET_DIRECTORY + this.style;
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
	 * Gets the user style sheets.
	 * 
	 * @return the user style sheets
	 */
	public List<String> getUserStyleSheets() {
		return userStyleSheets;
	}

	/**
	 * Sets the user style sheets.
	 * 
	 * @param userStyleSheets the user style sheets
	 */
	public void setUserStyleSheets(List<String> userStyleSheets) {
		this.userStyleSheets = userStyleSheets;
		calculateStyleSheets();
	}

	/**
	 * Calculate style sheets.
	 */
	private void calculateStyleSheets() {
		this.styleSheets = new ArrayList<String>();
		this.styleSheets.add( AON_STYLE_SHEET );
		this.styleSheets.add( LAYOUT_STYLE_SHEET );
		if ( this.style != null ) {
			String css = getStylePath() + CUSTOMIZED_STYLE_SHEET_PREFIX + this.style + STYLE_SHEET_EXTENSION;
			this.styleSheets.add( css );
		}
		if ( this.userStyleSheets != null ) {
			this.styleSheets.addAll( this.userStyleSheets );			
		}
	}
	
	/**
	 * Calculate application version.
	 * 
	 * @return the application version number
	 */
	public String getApplicationVersion() {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			InputStream in = ec.getResourceAsStream("META-INF/MANIFEST.MF");
			Manifest m = new Manifest(in);
			Attributes attrs = m.getMainAttributes();
			String version = attrs.getValue("Implementation-Version");
			if(version != null){
				LOGGER.info(version);
			}else{
				LOGGER.warning("Imposible determinar la versión");
			}
			return version;
		} catch (Throwable e) {
			LOGGER.warning("Imposible determinar la versión" + e.getMessage());
			return null;
		}
	}
}