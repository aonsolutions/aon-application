package com.code.aon.ui.resources.bean;

import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_FONT_COLOR;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_ID;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_SUPPORT_PHONE;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_TITLE;
import static com.code.aon.common.enumeration.AppParam.AON_HIDE_TRADEMARK;
import static com.code.aon.ui.common.ICommonMessages.APPLICATION_TITLE;
import static com.code.aon.ui.common.ICommonMessages.BUNDLE_RESOURCE;
import static com.code.aon.ui.common.ICommonConstants.FAVICON_NAME;
import static com.code.aon.ui.common.ICommonConstants.LOGIN_LOGO_NAME;
import static com.code.aon.ui.common.ICommonMessages.SUPPORT_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonMessages.SUPPORT_TELEPHONE_NUMBER;
import static com.code.aon.ui.common.ICommonMessages.SUPPORT_TELEPHONE_NUMBER2;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.AppParam;
import com.code.aon.dbutils.DatabaseUtil;

public class CustomizeBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeBean.class.getName());
	
	private static final String AON_DOCUMENTS_PREFFIX = "aonDocuments/";
	
	private static final String IMPLEMENTATION_VERSION = "Implementation-Version";

	private static final String BUILD_NUMBER = "buildNumber";
	
	private static final String BUILD_DATE = "buildDate";
	
	private static final String BUILD_REVISION = "buildRevision";
	
	private static final String FONT_STYLE_DEFAULT = "black";
	
	private static final String FAVICON_DEFAULT = "/images/favicon.ico";
	
	private static final String LOGIN_LOGO_DEFAULT = "/com/code/aon/ui/resources/facelet/login/css/images/login/aon-solutions.gif";	
	
	private String favicon;
	
	private String loginLogo;
	
	private String fontStyle;
	
	private String applicationTitle;
	
	private String supportTelephone;
	
	private String supportEmail;
	
	private boolean hideTrademark;
	
	private String applicationVersion;

	private String buildNumber;
	
	private String buildDate;
	
	private String buildRevision;
	
	private Integer domainId;
	
	private Integer companyId;
	
	private ResourceBundle bundle;	
	
	public CustomizeBean() {
		this.fontStyle = getColorStyle(FONT_STYLE_DEFAULT);
	}
	
	public void initResources( ) {
		ResourceResolver resolver = new ResourceResolver();
		initResources(resolver);
	}	
	protected void initResources(ResourceResolver resolver) {
		this.loginLogo = resolver.getResolve().get(LOGIN_LOGO_DEFAULT);
		this.favicon = resolver.getResolve().get(FAVICON_DEFAULT);
	}

	public void initMessages( Locale locale ) {
		bundle = ResourceBundle.getBundle(BUNDLE_RESOURCE, locale);
		ResourceBundle appBundle = ResourceBundle.getBundle("com.code.aon.web.aio.i18n.messages", locale);
		this.applicationTitle = appBundle.getString( APPLICATION_TITLE );
		this.supportTelephone = bundle.getString(SUPPORT_TELEPHONE_NUMBER) + " · " + bundle.getString(SUPPORT_TELEPHONE_NUMBER2);
		this.supportEmail = bundle.getString(SUPPORT_SEND_EMAIL);
	}	
	
	/**
	 * Calculate application version.
	 * 
	 */
	public void initApplicationVersion( InputStream in ) {
		try {
			Manifest m = new Manifest(in);
			Attributes attrs = m.getMainAttributes();
			this.applicationVersion = StringUtils.trimToNull(attrs.getValue(IMPLEMENTATION_VERSION));
			this.buildNumber = StringUtils.trimToNull( attrs.getValue(BUILD_NUMBER) );
			this.buildDate = StringUtils.trimToNull( attrs.getValue(BUILD_DATE) );
			this.buildRevision = StringUtils.trimToNull( attrs.getValue(BUILD_REVISION) );
		} catch (Throwable e) {
			LOGGER.warn("Imposible determinar la versión");
		}
	}	
	
	public void init( String domain ) {
		Connection connection = null;
		try {
			connection =  DatabaseUtil.getConnection(domain);
			if ( connection != null ) {
				this.domainId = DatabaseUtil.getDomain(connection, domain );
				if (this.domainId != null) {
					this.companyId = getCompanyId(connection);
					if ( this.companyId != null ) {
						loadValues(connection);	
					}
				}
			}				
		} catch ( Throwable th ) {
			LOGGER.error( "Error loading customization values", th );
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}	
	
	private Integer getCompanyId( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> hs = new ScalarHandler<String>();
			String value = run.query( connection, "SELECT value FROM app_param WHERE domain = ? and name = ?", hs, domainId, AON_CUSTOMIZE_ID.getValue());
			if (! StringUtils.isEmpty(value) ) {
				Integer id = NumberUtils.toInt(value);
				ResultSetHandler<Long> hl = new ScalarHandler<Long>();
				Long count = run.query( connection, "SELECT count(id) FROM registry WHERE id = ?", hl, id);
				if ( count > 0 ) {
					return id;
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;		
	}	
	
	private Integer getCompanyDomain( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection, "SELECT domain FROM registry WHERE id = ?", h, this.companyId);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return this.domainId;		
	}	
	
	private String getMD5( Object value ) {
		if ( value != null ) {
			if ( value instanceof byte[] ) {
				return new String( (byte[]) value);
			} else {
				return value.toString();
			}			
		}
		return null;
	}	
	
	protected String getImageRef( Connection connection, String name ) {
		String ref = null;
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object[]> h = new ArrayHandler();
			Object[] values = run.query( connection, "SELECT id, MD5(data) FROM rattach WHERE registry = ? and description = ? and type = 2", h, this.companyId, name);
			if (! ArrayUtils.isEmpty(values) ) {
				ref = AON_DOCUMENTS_PREFFIX + values[0] + "-" + getMD5(values[1]);
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}			
		return ref;
	}
	
	protected void loadValues( Connection connection ) {
		this.domainId = getCompanyDomain(connection);
		updateApplicationTitle(connection);
		updateSupportTelephone(connection);
		updateSupportEmail(connection);
		updateFontStyle(connection);
		updateHideTrademark(connection);
		this.favicon = StringUtils.defaultIfEmpty(getImageRef(connection, FAVICON_NAME), this.favicon);
		this.loginLogo = StringUtils.defaultIfEmpty(getImageRef(connection, LOGIN_LOGO_NAME), this.loginLogo);
	}
	
	private String getValue( Connection connection, AppParam appParam ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> h = new ScalarHandler<String>();
			return run.query( connection, "SELECT value FROM app_param WHERE domain = ? and name = ?", h, domainId, appParam.getValue());
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;				
	}
	
	private void updateApplicationTitle( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_TITLE);
		if (! StringUtils.isEmpty(value) ) {
			this.applicationTitle = value;
		}
	}

	private void updateFontStyle( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_FONT_COLOR);
		if (! StringUtils.isEmpty(value) ) {
			this.fontStyle = getColorStyle(value);
		}
	}
	
	private void updateSupportTelephone( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_SUPPORT_PHONE);
		if (! StringUtils.isEmpty(value) ) {
			this.supportTelephone = value;
		}
	}

	private void updateSupportEmail( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_SUPPORT_EMAIL);
		if (! StringUtils.isEmpty(value) ) {
			this.supportEmail = value;
		}
	}
	
	private void updateHideTrademark( Connection connection ) {
		String value = getValue( connection, AON_HIDE_TRADEMARK);
		if (! StringUtils.isEmpty(value) ) {
			this.hideTrademark = Boolean.valueOf(value);
		}
	}		
	
	private String getColorStyle( String color ) {
		return "color: " + color + " !important;";
	}		

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public String getBuildDate() {
		return buildDate;
	}

	public String getBuildRevision() {
		return buildRevision;
	}

	public String getApplicationTitle() {
		return this.applicationTitle;
	}
	
	public String getSupportTelephone() {
		return supportTelephone;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public String getFavicon() {
		return favicon;
	}
	
	public String getLoginLogo() {
		return loginLogo;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public boolean isHideTrademark() {
		return hideTrademark;
	}

	public boolean isCustomized() {
		return this.companyId != null;
	}
	
}