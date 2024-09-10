package com.esferalia.aon.occam.api;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.mchange.v2.c3p0.DataSources;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.aonsolutions.core.pool.ConnectionInfo;

public class AONContext {
	
	public static class CloseableAONContext extends AONContext implements AutoCloseable{

		private CloseableAONContext(Connection connection, String schema) {
			super(connection, schema);
		}
		
		private CloseableAONContext(Connection connection, String domainName, String user) {
			super(connection, domainName, user);
		}
		
		protected CloseableAONContext(Connection connection, String domainName, int domainId, String user) {
			super(connection, domainName, domainId, user);
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
	
	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
	private static Settings SETTINGS = null;
	
	private static Settings getDefaultSettings(){
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			SETTINGS.setParamType( ParamType.INLINED );
		}
		return SETTINGS;
	}
	
	private static int getDomainId(DSLContext dslContext, String domainName) {
		return 
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName))
		.fetchOne(DOMAIN.ID)
		;
	}
	
	/**
	 * @deprecated Usar getAONContext(String domainName, int domainId, <b>String user</b>)
	 *  para obtener el usuario, desde un servlet (parte cliente), se puede llamar al método
	 *  <code>AonServletUtils.getLoggedUser()<code>
	 */
	@Deprecated
	public static CloseableAONContext getAONContext(String domainName, int domainId) {
		try {
			// ----------------------
			// ----------------------
//			System.out.println("[AON WARNING]");
//			System.out.println("AONContext getAONContext(domainName,domainId) esta "
//					+ "deprecado y sera borrado en el futuro. Usar "
//					+ "AONContext getAONContext(domainName, domainId,user) "
//					+ " con el fin de verificar si el usuario tiene permisos, scopes, etc ...");
//			StackTraceElement[] stes = Thread.currentThread().getStackTrace();
//			if (stes != null && stes.length > 2) {
//				System.out.println();
//				System.out.println("\tat " + stes[2]);
//				System.out.println();
//			}
			// ----------------------
			// ----------------------
			return new CloseableAONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, domainId, null);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
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
	
	public static CloseableAONContext getAONContext(String domainName,String user) {
		try {
			return new CloseableAONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static CloseableAONContext getAONContext(Occam occam) {
		return getAONContext(occam.getDomainName(),occam.getDomain(),occam.getUser());
	}
	
	public static CloseableAONContext getAONContext(String domainName, int domainId, String user) {
		try {
			return new CloseableAONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, domainId,user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static CloseableAONContext getAONContext(Domain domain, String user) {
		try {
			return new CloseableAONContext(AonDataSource.getInstance().getConnection(
					domain.getName()), domain.getName(), domain.getId(),user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static void closeQuietly(AONContext ctx) {
		if (ctx != null && ctx instanceof CloseableAONContext) {
			((CloseableAONContext) ctx).close();
		}
	}
	
	public static CloseableAONContext getAONContext(Domain domain, User user) {
		return getAONContext(domain, user.getLogin());
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
	private int domainId;
	private String user;
	private AonConfiguration configuration;
	
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
		this.domainId = getDomainId(dslContext, domainName);
	}

	private AONContext(Connection connection, String domainName, int domainId, String user) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		this.connection = connection;
		this.dslContext = DSL.using(connection,getDefaultSettings());
	}

	public String getDomainName() {
		return domainName;
	}
	public int getDomainId() {
		return domainId;
	}

	public DSLContext getDslContext() {
		return dslContext;
	}
	
	public void deactivateForeignKeys(){
		try {
			Statement sOpen = connection.createStatement();
			sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
			System.out.println("Claves referenciales desactivadas");	
			sOpen.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void activateForeignKeys(){
		try{
			Statement sClose = connection.createStatement();
			sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
			System.out.println("Claves referenciales activadas");		
			sClose.close();
		} catch(SQLException e){
			e.printStackTrace();
		} 
	}
	
	public AonConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = ConfigurationDAO.getAccountingConfiguration(this);
		}
		return configuration;
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
	
	
	/**
	 * @deprecated USE SecurityDAO.getDomainInheritanceCondition
	 */
	@Deprecated 
	public Condition getDomainInheritanceCondition(Integer domainId,
			TableField<? extends Record, java.lang.Integer> field) {
		DomainRecord record = dslContext.fetchOne(DOMAIN,
				DOMAIN.ID.equal(domainId));
		if (AonEnumUtils.getBoolean(record.getValue(DOMAIN.ENABLEHEREDITY))) {
			Integer parentDomain = record.getValue(DOMAIN.PARENT);
			return (field.equal(domainId)).or(field.equal(parentDomain));
		} else {
			return (field.equal(domainId));
		}
	}

	public String getUser() {
		return user;
	}

	public ILogger log() {
		if (logger == null) {
			logger = new ILogger() {

				private final Logger log = Logger.getLogger(this.getClass().getName());

				@Override
				public void error(String msg) {
					log.log(Level.SEVERE, ERR, new Object[]{new Date(), Integer.valueOf( AONContext.this.domainId) ,msg});
				}
				@Override
				public void error(String msg, Object ... params) {
					this.error(MessageFormat.format(msg,params));
				}

				@Override
				public void warn(String msg) {
					log.log(Level.WARNING, WAR, new Object[]{new Date(), Integer.valueOf( AONContext.this.domainId) ,msg});
				}
				@Override
				public void warn(String msg, Object ... params) {
					this.warn(MessageFormat.format(msg,params));
				}

				@Override
				public void info(String msg) {
					log.log(Level.INFO, INF, new Object[]{new Date(), Integer.valueOf( AONContext.this.domainId) ,msg});
				}
				@Override
				public void info(String msg, Object ... params) {
					this.info(MessageFormat.format(msg,params));
				}

				@Override
				public void debug(String msg) {
					log.log(Level.FINE, DEB, new Object[]{new Date(), Integer.valueOf( AONContext.this.domainId) ,msg});
				}
				@Override
				public void debug(String msg, Object ... params) {
					this.debug(MessageFormat.format(msg,params));
				}
				
			};
		}
		return logger; 
	}
}
