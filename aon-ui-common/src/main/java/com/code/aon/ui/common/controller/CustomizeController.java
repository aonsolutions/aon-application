package com.code.aon.ui.common.controller;

import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_COLOR;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_ID;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_SUPPORT_EMAIL;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_SUPPORT_PHONE;
import static com.code.aon.ui.common.ICommonConstants.AON_CUSTOMIZE_TITLE;
import static com.code.aon.ui.common.ICommonConstants.AON_HIDE_TRADEMARK;
import static com.code.aon.ui.common.ICommonConstants.APPLICATION_TITLE;
import static com.code.aon.ui.common.ICommonConstants.FAVICON_NAME;
import static com.code.aon.ui.common.ICommonConstants.HEADER_LOGO_NAME;
import static com.code.aon.ui.common.ICommonConstants.LOGIN_LOGO_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_FAILED_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_START_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_STOP_NAME;
import static com.code.aon.ui.common.ICommonConstants.SUPPORT_SEND_EMAIL;
import static com.code.aon.ui.common.ICommonConstants.SUPPORT_TELEPHONE_NUMBER;
import static com.code.aon.ui.common.ICommonConstants.SUPPORT_TELEPHONE_NUMBER2;
import static com.code.aon.ui.common.ICommonConstants.TOOLBAR_LOGO_NAME;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class CustomizeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeController.class.getName());
	
	private static final String IMPLEMENTATION_VERSION = "Implementation-Version";

	private static final String BUILD_NUMBER = "buildNumber";
	
	private static final String BUILD_DATE = "buildDate";
	
	private static final String BUILD_REVISION = "buildRevision";

	private static final String AON_DOCUMENTS_PREFFIX = "aonDocuments/";
	
	private static final String FAVICON_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/favicon.ico']}");
	
	private static final String LOGIN_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/com/code/aon/ui/resources/facelet/login/css/images/login/aon-solutions.gif']}");
	
	private static final String HEADER_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-header/aon-solutions.png']}");

	private static final String TOOLBAR_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-icon/aon-icon-logo.png']}");
	
	private static final String STATUS_START_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-header/aon-outputConnectionStatus-start.gif']}");

	private static final String STATUS_STOP_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-header/aon-outputConnectionStatus-stop.png']}");
	
	private static final String STATUS_FAILED_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-header/aon-outputConnectionStatus-failed.png']}");
	
	private static final String FONT_STYLE_DEFAULT = "black";
	
	private Integer domainId;
	
	private String applicationVersion;
	
	private String buildNumber;
	
	private String buildDate;
	
	private String buildRevision;
	
	private Integer companyId;
	
	private String applicationTitle;
	
	private String supportTelephone;
	
	private String supportEmail;
	
	private String favicon;
	
	private String loginLogo;
	
	private String headerLogo;
	
	private String toolbarLogo;
	
	private String fontStyle;
	
	private String statusStartStyle;
	
	private String statusStopStyle;
	
	private String statusFailedStyle;
	
	private boolean hideTrademark;
	
	public CustomizeController() {
		this.applicationTitle = AonUtil.getMessage("appBundle", APPLICATION_TITLE );
		this.supportTelephone = AonUtil.getMessage(SUPPORT_TELEPHONE_NUMBER) + " · " + AonUtil.getMessage(SUPPORT_TELEPHONE_NUMBER2);
		this.supportEmail = AonUtil.getMessage(SUPPORT_SEND_EMAIL);
		this.favicon = FAVICON_DEFAULT;
		this.loginLogo = LOGIN_LOGO_DEFAULT;
		this.headerLogo = HEADER_LOGO_DEFAULT;
		this.toolbarLogo = TOOLBAR_LOGO_DEFAULT;
		this.fontStyle = getColorStyle(FONT_STYLE_DEFAULT);
		this.statusStartStyle = getBackgroundImageStyle(STATUS_START_DEFAULT);
		this.statusStopStyle = getBackgroundImageStyle(STATUS_STOP_DEFAULT);
		this.statusFailedStyle = getBackgroundImageStyle(STATUS_FAILED_DEFAULT);
		initApplicationVersion();
		init();
	}

	private String getColorStyle( String color ) {
		return "color: " + color + " !important;";
	}	
	
	private String getBackgroundImageStyle( String url ) {
		return "background-image: url(" + url + ");";
	}
	
	/**
	 * Calculate application version.
	 * 
	 */
	private void initApplicationVersion() {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			InputStream in = ec.getResourceAsStream("META-INF/MANIFEST.MF");
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

	private String getValue( Connection connection, String name ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> h = new ScalarHandler<String>();
			return run.query( connection, "SELECT value FROM app_param WHERE domain = ? and name = ?", h, domainId, name);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;				
	}
	
	private Integer getCompanyId( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> hs = new ScalarHandler<String>();
			String value = run.query( connection, "SELECT value FROM app_param WHERE domain = ? and name = ?", hs, domainId, AON_CUSTOMIZE_ID);
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
	
	private void updateApplicationTitle( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_TITLE);
		if (! StringUtils.isEmpty(value) ) {
			this.applicationTitle = value;
		}
	}

	private void updateFontStyle( Connection connection ) {
		String value = getValue( connection, AON_CUSTOMIZE_COLOR);
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

	private String getStatusStyle( Connection connection, String name, String _default ) {
		String ref = getImageRef(connection, name);
		if (! StringUtils.isEmpty(ref) ) {
			return getBackgroundImageStyle(ref);	
		}
		return _default;
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
	
	private String getImageRef( Connection connection, String name ) {
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
	
	private void init() {
		Connection connection = null;
		try {
			Properties dbProperties = DataSourceUtil.getDBProperties();
			connection =  ConnectionProvider.getConnection(dbProperties);
			if ( connection != null ) {
				this.domainId = DataSourceUtil.getDomain(connection, AonUtil.getServerName(), AonUtil.isSkipLdap() );
				if (this.domainId != null) {
					this.companyId = getCompanyId(connection);
					if ( this.companyId != null ) {
						loadValues(connection);	
					}
				}
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting company name and logo", th );
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	private void loadValues( Connection connection ) {
		this.domainId = getCompanyDomain(connection);
		updateApplicationTitle(connection);
		updateSupportTelephone(connection);
		updateSupportEmail(connection);
		updateFontStyle(connection);
		updateHideTrademark(connection);
		this.favicon = StringUtils.defaultIfEmpty(getImageRef(connection, FAVICON_NAME), this.favicon);
		this.loginLogo = StringUtils.defaultIfEmpty(getImageRef(connection, LOGIN_LOGO_NAME), this.loginLogo);
		this.headerLogo = StringUtils.defaultIfEmpty(getImageRef(connection, HEADER_LOGO_NAME), this.headerLogo);
		this.toolbarLogo = StringUtils.defaultIfEmpty(getImageRef(connection, TOOLBAR_LOGO_NAME), this.toolbarLogo);
		this.statusStartStyle = getStatusStyle(connection, STATUS_START_NAME, this.statusStartStyle);
		this.statusStopStyle = getStatusStyle(connection, STATUS_STOP_NAME, this.statusStopStyle);
		this.statusFailedStyle = getStatusStyle(connection, STATUS_FAILED_NAME, this.statusFailedStyle);
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

	public String getHeaderLogo() {
		return headerLogo;
	}

	public String getToolbarLogo() {
		return toolbarLogo;
	}

	public boolean isCustomized() {
		return this.companyId != null;
	}

	public String getFontStyle() {
		return fontStyle;
	}

	public String getStatusStartStyle() {
		return statusStartStyle;
	}

	public String getStatusStopStyle() {
		return statusStopStyle;
	}

	public String getStatusFailedStyle() {
		return statusFailedStyle;
	}

	public boolean isHideTrademark() {
		return hideTrademark;
	}
	
}