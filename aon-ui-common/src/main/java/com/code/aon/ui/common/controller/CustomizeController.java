package com.code.aon.ui.common.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;

public class CustomizeController {

	private static final String AON_DOCUMENTS_PREFFIX = "aonDocuments/";

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeController.class.getName());
	
	private static final String AON_CUSTOMIZE_ID = "AON_CUSTOMIZE_ID";
	
	private static final String FAVICON_NAME = "favicon.ico";
	
	private static final String FAVICON_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/favicon.ico']}");

	private static final String LOGIN_LOGO_NAME = "aon-login-logo";
	
	private static final String LOGIN_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/com/code/aon/ui/resources/facelet/login/css/images/login/aon-solutions.gif']}");

	private static final String HEADER_LOGO_NAME = "aon-header-logo";
	
	private static final String HEADER_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-header/aon-solutions.png']}");

	private static final String TOOLBAR_LOGO_NAME = "aon-toolbar-logo";
	
	private static final String TOOLBAR_LOGO_DEFAULT = (String) AonUtil.getValue("#{aonResource.resolve['/images/aon-icon/aon-icon-logo.png']}");
	
	private Integer companyId;
	
	private String applicationTitle;
	
	private String supportTelephone;
	
	private String supportEmail;
	
	private String favicon;
	
	private String loginLogo;
	
	private String headerLogo;
	
	private String toolbarLogo;
	
	public CustomizeController() {
		this.applicationTitle = AonUtil.getMessage("appBundle", "aon_application_title" );
		this.supportTelephone = AonUtil.getMessage("aon_support_telephone_number" );
		this.supportEmail = AonUtil.getMessage("aon_support_send_email" );
		this.favicon = FAVICON_DEFAULT;
		this.loginLogo = LOGIN_LOGO_DEFAULT;
		this.headerLogo = HEADER_LOGO_DEFAULT;
		this.toolbarLogo = TOOLBAR_LOGO_DEFAULT;
		init();
	}

	private Connection getConnection( Properties dbProperties ) throws SQLException {
		DbUtils.loadDriver(dbProperties.getProperty(Environment.DRIVER));
		String url = dbProperties.getProperty(Environment.URL);
		String user = dbProperties.getProperty(Environment.USER);
		String password = dbProperties.getProperty(Environment.PASS);
		Connection connection = DriverManager.getConnection(url, user, password);
		return connection;	
	}
	
	private Integer getCompanyId( Connection connection, Integer domainId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object> h = new ScalarHandler();
			String value = (String) run.query( connection, "SELECT value FROM app_param WHERE domain = ? and name = ?", h, domainId, AON_CUSTOMIZE_ID);
			if (! StringUtils.isEmpty(value) ) {
				Integer id = NumberUtils.toInt(value);
				Long count = (Long) run.query( connection, "SELECT count(id) FROM registry WHERE domain = ? and id = ?", h, domainId, id);
				if ( count > 0 ) {
					return id;
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;		
	}
	
	private void updateApplicationTitle( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object> h = new ScalarHandler();
			String value = (String) run.query( connection, "SELECT alias FROM registry WHERE id = ?", h, this.companyId);
			if (! StringUtils.isEmpty(value) ) {
				this.applicationTitle = value;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	private void updateSupportTelephone( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object> h = new ScalarHandler();
			String value = (String) run.query( connection, "SELECT value FROM rmedia WHERE registry = ? and media = 1", h, this.companyId);
			if (! StringUtils.isEmpty(value) ) {
				this.supportTelephone = value;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	private void updateSupportEmail( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object> h = new ScalarHandler();
			String value = (String) run.query( connection, "SELECT value FROM rmedia WHERE registry = ? and media = 4", h, this.companyId);
			if (! StringUtils.isEmpty(value) ) {
				this.supportEmail = value;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	private String getImageRef( Connection connection, String name ) {
		String ref = null;
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Object[]> h = new ArrayHandler();
			Object[] values = run.query( connection, "SELECT id, MD5(data) FROM rattach WHERE registry = ? and description = ? and type = 2", h, this.companyId, name);
			if (! ArrayUtils.isEmpty(values) ) {
				ref = AON_DOCUMENTS_PREFFIX + values[0] + "-" + new String((byte[]) values[1]);
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
			connection = getConnection(dbProperties);
			if ( connection != null ) {
				Integer domainId = DataSourceUtil.getDomain(connection, AonUtil.getServerName(), AonUtil.isSkipLdap() );
				if (domainId != null) {
					this.companyId = getCompanyId(connection, domainId);
					if ( this.companyId != null ) {
						updateApplicationTitle(connection);
						updateSupportTelephone(connection);
						updateSupportEmail(connection);
						this.favicon = StringUtils.defaultIfEmpty(getImageRef(connection, FAVICON_NAME), this.favicon);
						this.loginLogo = StringUtils.defaultIfEmpty(getImageRef(connection, LOGIN_LOGO_NAME), this.loginLogo);
						this.headerLogo = StringUtils.defaultIfEmpty(getImageRef(connection, HEADER_LOGO_NAME), this.headerLogo);
						this.toolbarLogo = StringUtils.defaultIfEmpty(getImageRef(connection, TOOLBAR_LOGO_NAME), this.toolbarLogo);
					}
				}
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting company name and logo", th );
		} finally {
			DbUtils.closeQuietly(connection);
		}
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
		return companyId != null;
	}

}