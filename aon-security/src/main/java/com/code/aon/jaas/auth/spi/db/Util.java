package com.code.aon.jaas.auth.spi.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final String DB_SEP = "`";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);
	
	private Connection connection;

	public Util(Connection connection) {
		this.connection = connection;
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
				    "SELECT id, active FROM " + DB_SEP + dbName + DB_SEP + ".domain WHERE name =?", h, domainName); 
			LOGGER.debug( "Get domain {} id from {}", domainName, dbName );
			if ( domain != null ) {
				domain.setDataBaseName(dbName);
				domain.setName(domainName);
				return domain;
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;			
	}
	
	private Domain getDomainInfo( String domainName ) {
		String dbName = StringUtils.replace(domainName, ".", "-");
		if ( dbExists(dbName) ) {
			Domain domain = getDomainInfo(dbName, domainName);
			if ( domain != null ) {
				return domain;
			}
		}
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<List<Object>> h = new ColumnListHandler();
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
	
	public Domain getDomain( String domainName ) {
		Domain domain = null;
		String name = domainName;
		do {
			domain = getDomainInfo(name);
			if ( domain == null ) {
				name = StringUtils.substringAfter(name, ".");	
			}
		} while ( (domain == null) && StringUtils.contains(name, '.') );
		return domain;
	}

	public User getUser( Integer domainId, String userName ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "Get user {} in domain {}", userName, domainId );
			ResultSetHandler<User> h = new BeanHandler<User>(User.class);
			User user = run.query( connection,
				    "SELECT id, login, password, active FROM user WHERE domain =? AND login =?", h, domainId, userName); 
			return user;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}

	public Integer getApplicationId( String applicationName ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "application name: {}", applicationName );
			ResultSetHandler<Object> h = new ScalarHandler();
			return (Integer) run.query( connection,
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Integer> getProfiles( Integer applicationUserId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "application user: {}", applicationUserId );
			ResultSetHandler<List<Object>> h = new ColumnListHandler();
			List<Object> result = run.query( connection,
				    "SELECT profile FROM application_user_profile WHERE application_user=?", h, applicationUserId );
			return (List) result;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> getRoles( Integer profileId ) {
		QueryRunner run = new QueryRunner();
		try {
			LOGGER.debug( "profile: {}", profileId );
			ResultSetHandler<List<Object>> h = new ColumnListHandler();
			List<Object> result = run.query( connection,
					"SELECT role.name FROM profile_role, application_role, role WHERE " +
					"profile_role.application_role = application_role.id and " +
					"application_role.role = role.id and " +
					"profile_role.profile = ?", h, profileId );
			return (List) result;
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}

	
}
