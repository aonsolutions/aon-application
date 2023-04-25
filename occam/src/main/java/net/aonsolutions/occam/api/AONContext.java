package net.aonsolutions.occam.api;

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;

public class AONContext implements AutoCloseable {
	
	private static Settings settings = null;
	
	private ILogger logger;
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private String user;
	
	private static Settings getDefaultSettings(){
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
			settings.setParamType( ParamType.INLINED );
		}
		return settings;
	}
	
	public static AONContext getAONContext(Occam occam) {
		return getAONContext(occam.getDomainName(),occam.getUser());
	}
	
	public static AONContext getAONContext(String domainName, String user) {
		try {
			return new AONContext(AonDataSource.getInstance().getConnection(domainName), domainName,user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}

	protected AONContext(Connection connection, String domainName, String user) {
		this.domainName = domainName;
		this.user = user;
		this.connection = connection;
		this.dslContext = DSL.using(connection,getDefaultSettings());
		log().debug("AONContext created"); 
	}

	public String getDomainName() {
		return domainName;
	}
	public String getUser() {
		return user;
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
	
	public static void closeQuietly(AONContext ctx) {
		if (ctx != null) ctx.close();
	}

	@Override
	public void close() {
		AonDatabaseUtil.closeQuietly(connection);
	}

	public ILogger log() {
		if (logger == null) {
			logger = new ILogger() {

				private final Logger log = Logger.getLogger(this.getClass().getName());

				@Override
				public void error(String msg) {
					log.log(Level.SEVERE, ERR, new Object[]{new Date(), AONContext.this.domainName ,msg});
				}
				@Override
				public void error(String msg, Object ... params) {
					this.error(MessageFormat.format(msg,params));
				}

				@Override
				public void warn(String msg) {
					log.log(Level.WARNING, WAR, new Object[]{new Date(), AONContext.this.domainName ,msg});
				}
				@Override
				public void warn(String msg, Object ... params) {
					this.warn(MessageFormat.format(msg,params));
				}

				@Override
				public void info(String msg) {
					log.log(Level.INFO, INF, new Object[]{new Date(), AONContext.this.domainName ,msg});
				}
				@Override
				public void info(String msg, Object ... params) {
					this.info(MessageFormat.format(msg,params));
				}

				@Override
				public void debug(String msg) {
					log.log(Level.FINE, DEB, new Object[]{new Date(), AONContext.this.domainName,msg});
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
