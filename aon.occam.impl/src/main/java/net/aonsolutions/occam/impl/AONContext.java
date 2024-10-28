package net.aonsolutions.occam.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.mchange.v2.c3p0.DataSources;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.aonsolutions.core.pool.ConnectionInfo;
import net.aonsolutions.occam.api.model.Activity;
import net.aonsolutions.occam.api.model.ApplicationParameters;
import net.aonsolutions.occam.api.model.CompanyFull;
import net.aonsolutions.occam.api.model.Configuration;
import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.InvestAsset;
import net.aonsolutions.occam.api.model.Occam;
import net.aonsolutions.occam.api.model.Scope;
import net.aonsolutions.occam.api.model.TbaiConfiguration;
import net.aonsolutions.occam.api.model.User;
import net.aonsolutions.occam.api.model.Workplace;
import net.aonsolutions.occam.impl.handler.CONFIGURATION;

public class AONContext {
	
	public static class CloseableAONContext extends AONContext implements AutoCloseable{

		private CloseableAONContext(Connection connection, String schema) {
			super(connection, schema);
		}
		
		private CloseableAONContext(Connection connection, String domainName, String user) {
			super(connection, domainName, user);
		}
		
		@Override
		public void close() {
			AonDatabaseUtil.closeQuietly(super.connection);
		}
		
		
	}
	
	public static class UnpooledCloseableAONContext extends AONContext implements AutoCloseable{
		
		public UnpooledCloseableAONContext(Connection connection, Settings settings) {
			super(DSL.using( connection, settings));
			super.connection = connection;
		}

		@Override
		public void close() {
			AonDatabaseUtil.closeQuietly(super.connection);
		}
		
	}
	
	private static Settings SETTINGS = null;
	
	private static Settings getDefaultSettings(){
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			SETTINGS.setParamType( ParamType.INLINED );
		}
		return SETTINGS;
	}
	
	public static UnpooledCloseableAONContext getUnpooledAONContext(String schema) {
		try {
			ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
			Class.forName(ci.getDriverClass(schema));
			Properties properties = new Properties();
			properties.setProperty("url", ci.getSchemaUrl(schema));
			properties.setProperty("user", ci.getUser(schema));
			properties.setProperty("password", ci.getPassword(schema));
			properties.setProperty("useSSL", ci.getUseSSL(schema));
			properties.setProperty("serverTimezone", ci.getTimeZone(schema));
			DataSource dsUnpooled = DataSources.unpooledDataSource(ci.getSchemaUrl(schema),properties);
			Settings settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType( ParamType.INLINED );
			return new UnpooledCloseableAONContext( dsUnpooled.getConnection(), settings );
		} catch (AonConnectionException | ClassNotFoundException | SQLException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static CloseableAONContext getAONContext(String schema) {
		try {
			return new CloseableAONContext(AonDataSource.getInstance().getDatabaseConnection(schema), schema );
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static CloseableAONContext getAONContext(Occam occam) {
		return getAONContext(occam.getDomainName(),occam.getUser());
	}
	
	public static CloseableAONContext getAONContext(String domainName, String user) {
		try {
			return new CloseableAONContext(AonDataSource.getInstance().getConnection(domainName), domainName, user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static void closeQuietly(AONContext ctx) {
		if (ctx instanceof CloseableAONContext closeableCTX) {
			closeableCTX.close();
		}
	}
	
	public static List<String> getSchemas() {
		ConnectionInfo connectionInfo = null;
		try {
			connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
			return  connectionInfo.getSchemas();
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		} finally {
			// TODO CLOSE!!!
		}
	}
	
	public static String getSchemaFirstDomain(String schema) {
		ConnectionInfo connectionInfo = null;
		try {
			connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
			return  connectionInfo.getSchemaFirstDomain(schema);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	private ILogger logger;
	
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private String user;
	
	public AONContext(DSLContext dslContext) {
		this.dslContext = dslContext;
	}


	public AONContext(Connection connection) {
		this(DSL.using(connection,getDefaultSettings()));
	}
	
	private AONContext(Connection connection, String schema) {
		this(DSL.using(connection,getDefaultSettings()));
		this.connection = connection;
	}

	private AONContext(Connection connection, String domainName, String user) {
		this.domainName = domainName;
		this.user = user;
		this.connection = connection;
		this.dslContext = DSL.using(connection,getDefaultSettings());
	}

	public String getDomainName() {
		return domainName;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}
	
	public boolean canWrite() {
		return true;
	}
	public boolean canRead() {
		return true;
	}

	public void checkRead() {
		if (!canRead()) 
			throw new SecurityException( AonError.READ_FORBIDDEN.getMessage() );	
	}
	public void checkWrite() {
		if (!canWrite()) 
			throw new SecurityException( AonError.WRITE_FORBIDDEN.getMessage() );	
	}
	
	public void transaction(TransactionalRunnable transactional) {
		getDslContext().transaction(transactional);
	}
	
	
	public String getUser() {
		return user;
	}

	public ILogger log() {
		if (logger == null) {
			logger = new ILogger() {

				private final Logger log = Logger.getLogger(this.getClass().getName());
				
				private Object[] getParams( String msg) {
					return new Object[]{new Date(), AONContext.this.domainName ,msg};	
				}

				@Override
				public void error(String msg) {
					log.log(Level.SEVERE, ERR, getParams(msg));
				}
				@Override
				public void error(String msg, Object ... params) {
					this.error(MessageFormat.format(msg,params));
				}

				@Override
				public void warn(String msg) {
					log.log(Level.WARNING, WAR, getParams(msg));
				}
				@Override
				public void warn(String msg, Object ... params) {
					this.warn(MessageFormat.format(msg,params));
				}

				@Override
				public void info(String msg) {
					log.log(Level.INFO, INF, getParams(msg));
				}
				@Override
				public void info(String msg, Object ... params) {
					this.info(MessageFormat.format(msg,params));
				}

				@Override
				public void debug(String msg) {
					log.log(Level.FINE, DEB, getParams(msg));
				}
				@Override
				public void debug(String msg, Object ... params) {
					this.debug(MessageFormat.format(msg,params));
				}
				
			};
		}
		return logger; 
	}
	
	// ----------------------------------------------
	// ------------------------------ ¿¿¿AQUI??? ----
	// ----------------------------------------------
	private Configuration configuration;
	private void checkConfiguration(int domain) {
		if ( configuration == null) {
			configuration = CONFIGURATION.getConfiguration( this, domain);
		} else if (AonNumberUtils.equals(configuration.getDomainId(), domain) ) {
			// Ok. Configuration of the same domain. 	
		} else {
			// Saved configuration is not from the same domina. New one is created.
			configuration = CONFIGURATION.getConfiguration( this, domain);
			log().warn( "*** New AON CONFIGURATION INSTANTIAIED!!");
		}
	}
	
	public Domain getDomain( int domain ) {
		checkConfiguration( domain );
		return CONFIGURATION.getDomain( this, this.configuration);
	}
	public User getCurrentUser( int domain ) {
		checkConfiguration( domain );
		return CONFIGURATION.getCurrentUser( this, this.configuration);
	}
	public CompanyFull getCompany(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getCompany( this, this.configuration);
	}
	public Stream<Activity> getActivities( int domain ) {
		checkConfiguration( domain );
		return CONFIGURATION.getActivities( this, this.configuration);
	}
	public Optional<Activity> getDefaultActivity( int domain ) {
		List<Activity> acts = getActivities(domain).toList();
		if (AonCollectionUtils.size(acts) == 1) {
			return Optional.of(acts.get(0));
		}
		return Optional.empty();
	}
	
	public Optional<Workplace> getDefaultWorkplace(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getDefaultWorkplace( this, this.configuration);
	}
	public Optional<Scope> getDefaultScope( int domain ) {
		checkConfiguration( domain );
		return CONFIGURATION.getDefaultScope( this, this.configuration);
	}
	public Integer[] getInheritanceDomainIds(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getInheritanceDomainIds( this, this.configuration);
	}
	public Integer[] getUserScopes(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getUserScopes( this, this.configuration);
	}

	public Stream<InvestAsset> getInvestAssets(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getInvestAssets( this, this.configuration);
	}

	public ApplicationParameters getApplicationParameters(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getApplicationParameters( this, this.configuration);
	}
	
	public TbaiConfiguration getTbaiConfig(int domain) {
		checkConfiguration( domain );
		return CONFIGURATION.getTbaiConfiguration( this, this.configuration);
	}

}
