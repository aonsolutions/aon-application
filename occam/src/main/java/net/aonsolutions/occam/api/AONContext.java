package net.aonsolutions.occam.api;

import java.sql.Connection;
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
	
	private AonLogger logger;
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private String user;
	private Boolean canRead = null;
	private Boolean canWrite = null;
	
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
	public Occam getOccam() {
		return new Occam().setDomainName(getDomainName()).setUser(getUser());
	}

	public DSLContext getDslContext() {
		return dslContext;
	}
	
	public boolean canWrite() {
		return canWrite == null || canWrite;
	}
	public void allowWrite() {
		canWrite = Boolean.TRUE;
	}
	public void denyWrite() {
		canWrite = Boolean.FALSE;
	}
	public boolean canRead() {
		return canRead == null || canRead;
	}
	public void allowRead() {
		canRead = Boolean.TRUE;
	}
	public void denyRead() {
		canRead = Boolean.FALSE;
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
	
	@Override
	public void close() {
		AonDatabaseUtil.closeQuietly(connection);
	}

	public AonLogger log() {
		if (logger == null) {
			logger = new AonLogger(Logger.getLogger(this.getClass().getName()), getDomainName());
		}
		return logger; 
	}
	
	// [BEGIN] Methods for tests purpose.
	public Boolean canWriteForTests() {return canWrite;}
	public void setCanWriteValueForTests(Boolean canWrite) {this.canWrite = canWrite;}
	public Boolean canReadForTests() {return canRead;}
	public void setCanReadValueForTests(Boolean canRead) {this.canRead = canRead;}
	public void setLoggerForTests(AonLogger logger) {this.logger = logger;}
	// [END] Methods for tests purpose.
	
}
