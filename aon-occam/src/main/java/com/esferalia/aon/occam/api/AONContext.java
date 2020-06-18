package com.esferalia.aon.occam.api;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.model.aonsolutions.AonConnection;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.AonEnumUtils;

import net.aonsolutions.core.pool.AonConnectionException;
import net.aonsolutions.core.pool.AonDataSource;
import net.aonsolutions.core.pool.ConnectionInfo;

public class AONContext implements AutoCloseable{

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
	
	/**
	 * @deprecated Usar getAONContext(String domainName, int domainId, <b>String user</b>)
	 *  para obtener el usuario, desde un servlet (parte cliente), se puede llamar al método
	 *  <code>AonServletUtils.getLoggedUser()<code>
	 */
	@Deprecated
	public static AONContext getAONContext(String domainName, int domainId) {
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
			return new AONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, domainId, null);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static AONContext getAONContext(String schema) {
		try {
			return new AONContext(AonDataSource.getInstance().getDatabaseConnection(schema));
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static AONContext getAONContext(AonConnection aonConnection) {
		try {
			return new AONContext(AonDataSource.getInstance().getDatabaseConnection(aonConnection.getSchema()), 
					aonConnection.getDomains().toArray(new Integer[aonConnection.getDomains().size()]),
					aonConnection.getUsers().toArray(new Integer[aonConnection.getUsers().size()]));
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}

	public static LinkedList<AonConnection> getAonConnections(String token) {
		JSONArray json = SECURITY.decodeJWT(token);
		LinkedList<AonConnection> list = new LinkedList<>();
		json.forEach(r -> list.add(AonConnection.parse((JSONObject) r)));
		return list;
	}
	
	public static AonConnection getAonConnection(String token, String domainName) {
		try {
			ConnectionInfo ci = ConnectionInfo.getDefaultConnectionInfo();
			String schema = ci.getDomainDatabase(domainName);
			return getAonConnections(token).stream().filter(ac -> ac.getSchema().equals(schema))
					.findFirst().orElse(new AonConnection());
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static AONContext getAONContext(String domainName, int domainId, String user) {
		try {
			return new AONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, domainId,user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	public static AONContext getAONContext(Integer[] domains, Integer[] users) {
		try {
			ConnectionInfo connectionInfo = ConnectionInfo.getDefaultConnectionInfo();
			connectionInfo.getSchemas();
			String domain = connectionInfo.getSchemaFirstDomain("pro-aonsolutions-net");
			
			return new AONContext(AonDataSource.getInstance().getConnection(domain)
					, domains, users);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
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
	
	private ILogger logger;
	
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private int domainId;
	private String user;
	
	private Integer[] domains;
	private Integer[] users;

	public AONContext(DSLContext dslContext) {
		this.dslContext = dslContext;
	}


	public AONContext(Connection connection) {
		this(DSL.using(connection,getDefaultSettings()));
		
	}

	private AONContext(Connection connection, Integer[] domains, Integer[] users) {
		this.domains = domains;
		this.users = users;
		this.connection = connection;
		this.dslContext = DSL.using(connection,getDefaultSettings());
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
	public Integer[] getDomains() {
		return domains;
	}
	public Integer[] getUsers() {
		return users;
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
	
	@Override
	public void finalize() {
		close();
	}
	
	public void close() {
		AonDatabaseUtil.closeQuietly(connection);
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
			// implementacion basico de log. Revisar.
			logger = new ILogger() {

				@Override
				public void error(String msg) {
					System.out.println(MessageFormat.format(ERR,new Date(),AONContext.this.domainId,msg));
				}

				@Override
				public void warn(String msg) {
					System.out.println(MessageFormat.format(WAR,new Date(),AONContext.this.domainId,msg));
				}

				@Override
				public void info(String msg) {
					System.out.println(MessageFormat.format(INF,new Date(),AONContext.this.domainId,msg));
				}

				@Override
				public void debug(String msg) {
					System.out.println(MessageFormat.format(DEB,new Date(),AONContext.this.domainId,msg));
				}
				
			};
		}
		return logger; 
	}
}
