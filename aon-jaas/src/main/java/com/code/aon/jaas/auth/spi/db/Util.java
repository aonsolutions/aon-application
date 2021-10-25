package com.code.aon.jaas.auth.spi.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.ConnectionInfo;

public class Util {
	
	private static final String DB_SEP = "`";
	private String AON_SUPPORT_ENABLED = "AON_SUPPORT_ENABLED";
	private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);

	private ConnectionInfo info;
	private Connection connection;

	public Util(ConnectionInfo info) {
		this.info = info;
	}
	
	public Connection createConnection(String domainName) throws AonConnectionException {
		setConnection(info.getDomainConnection(domainName));
		return connection;
	}

	public Connection createMetadataConnection(String domainName) throws AonConnectionException {
		String dbName = info.getDomainDatabase(domainName);
		setConnection(info.getMetadataConnection(dbName));
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	private boolean dbExists( String dbName ) {
	    Statement statement = null;
		try {
			statement = connection.createStatement();
			String sql = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '" + dbName + "'";
			LOGGER.debug( "Check if exists database: {}", dbName );
			ResultSet set = statement.executeQuery(sql);
			return set.next();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(statement);
		}		
		return false;	
	}

	private Domain getDomainInfo( String dbName, String domainName ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Domain> h = new BeanHandler<Domain>(Domain.class);
			Domain domain = run.query( connection,
				    "SELECT id, name, parent, active, expirationDate, scope FROM " + DB_SEP + dbName + DB_SEP + ".domain WHERE name =?", h, domainName); 
			LOGGER.debug( "Get domain {} id from {}", domainName, dbName );
			if ( domain != null ) {
				domain.setDataBaseName(dbName);
				return domain;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}
	
	public Domain getDomain( String domainName ) {
		String dbName = StringUtils.replace(domainName, ".", "-");
		if ( dbExists(dbName) ) {
			Domain domain = getDomainInfo(dbName, domainName);
			if ( domain != null ) {
				return domain;
			}
		}
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<List<Object>> h = new ColumnListHandler<Object>();
			List<Object> result = run.query( connection,
					"SELECT t.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as t " +
					"WHERE t.TABLE_NAME = 'domain'", h);
			if ( result != null ) {
				for( Object db : result ) {
					dbName = db.toString();
					Domain domain = getDomainInfo(dbName, domainName);
					if ( domain != null ) {
						return domain;
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private Integer getAdminDomain() {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection, "SELECT id FROM domain where type=5", h); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}		

	public boolean isSupportEnabled( Integer domainId ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<String> h = new ScalarHandler<String>();
			String value = run.query( connection,
					"SELECT value FROM app_param where domain=? and name=?", h, domainId, AON_SUPPORT_ENABLED);
			if ( value != null ) {
				Date date = new Date( NumberUtils.toLong(value) );
				if ( DateUtils.isSameDay(date, new Date()) ) {
					return true;
				}				
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return false;					
	}
	
	public User getUserOfAdminDomain( String userName ) {
		Integer adminDomaindId = getAdminDomain();
		if ( adminDomaindId != null ) {
			return getUser(adminDomaindId, userName);
		}
		return null;
	}
	
	private User getUser( Integer domainId, String userName ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "Get user {} in domain {}", userName, domainId );
			ResultSetHandler<User> h = new BeanHandler<User>(User.class);
			User user = run.query( connection,
				    "SELECT id, login, password, domain, active FROM user WHERE domain =? AND login =?", h, domainId, userName); 
			return user;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}
	
	public User getUser( Domain domain, String userName ) {
		User user = getUser(domain.getId(), userName );
		if ( (user == null) && (domain.getParent() != null) ) {
			user = getUser(domain.getParent(), userName );
		}
		return user;
	}	

	public Integer getApplicationId( String applicationName ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "application name: {}", applicationName );
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection,
				    "SELECT id FROM application WHERE name =?", h, applicationName); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}

	public DomainApplication getDomainApplication( Integer domainId, Integer applicationId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "domain: {}, application {}", applicationId, domainId );
			ResultSetHandler<DomainApplication> h = new BeanHandler<DomainApplication>(DomainApplication.class);
			DomainApplication da = run.query( connection,
				    "SELECT id, domain, application, active FROM domain_application WHERE domain =? AND application =?", h, domainId, applicationId); 
			return da;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}
	
	public BasicInfo getApplicationUser( Integer userId, Integer domainApplicationId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "user: {}, domainApplication: {}", userId, domainApplicationId );
			ResultSetHandler<BasicInfo> h = new BeanHandler<BasicInfo>(BasicInfo.class);
			return (BasicInfo) run.query( connection,
				    "SELECT id, active FROM application_user WHERE user_id=? AND domain_application=?", h, userId, domainApplicationId ); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	public List<Integer> getProfiles( Integer applicationUserId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "application user: {}", applicationUserId );
			ResultSetHandler<List<Integer>> h = new ColumnListHandler<Integer>();
			List<Integer> result = run.query( connection,
				    "SELECT profile FROM application_user_profile WHERE application_user=?", h, applicationUserId );
			return result;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	public List<String> getRoles( Integer profileId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "profile: {}", profileId );
			ResultSetHandler<List<String>> h = new ColumnListHandler<String>();
			List<String> result = run.query( connection,
					"SELECT role.name FROM profile_role, application_role, role WHERE " +
					"profile_role.application_role = application_role.id and " +
					"application_role.role = role.id and " +
					"profile_role.profile = ?", h, profileId );
			return result;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	public List<Integer> getUserScopes( Integer userId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "user: {}", userId );
			ResultSetHandler<List<Integer>> h = new ColumnListHandler<Integer>();
			List<Integer> result = run.query( connection,
				    "SELECT scope FROM user_scope WHERE user_id=?", h, userId );
			return result;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	public boolean hasScope( Integer userId, Integer scopeId ) {
		List<Integer> scopes = getUserScopes(userId);
		if ( (scopes != null) && (!scopes.isEmpty()) ) {
			return scopes.contains(scopeId);
		}
		return false;
	}
	
}