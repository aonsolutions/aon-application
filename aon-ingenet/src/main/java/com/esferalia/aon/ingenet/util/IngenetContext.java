package com.esferalia.aon.ingenet.util;

import java.sql.Connection;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;

public class IngenetContext extends AONContext {

	private static Settings SETTINGS = null;
	
	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			SETTINGS.setParamType( ParamType.INLINED );
		}
		return SETTINGS;
	}
	

	public static IngenetContext getAONContext(String domainName, int domainId, String user) {
		try {
			return new IngenetContext(IngenetDataSource.getInstance().getConnection(
					domainName), domainName, domainId,user);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private int domainId;
	private String user;

	public IngenetContext(DSLContext dslContext) {
		super(dslContext);
	}


	public IngenetContext(Connection connection) {
		super(DSL.using(connection,getDefaultSettings()));
		
	}

	private IngenetContext(Connection connection, String domainName, int domainId, String user) {
		super(connection);
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		this.connection = connection;
		this.dslContext = DSL.using(connection,getDefaultSettings());
		
		
	}

	public static int getUdapaDomainId() {
		int id = -1;
		try {
			Map<String, Integer> map = IngenetConnectionInfo.getDefaultConnectionInfo().getDomainMap();
			String updapaDomainName = map.keySet().stream().filter((key) -> (key.startsWith("udapa."))).findFirst().get();
			id = IngenetConnectionInfo.getDefaultConnectionInfo().getDomainMap().get(updapaDomainName);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
		return id;
	}
	
	public static int getUdapaMainScopeId() {
		int scope = -1;
		try {
			int domain = getUdapaDomainId();
			scope = IngenetConnectionInfo.getDefaultConnectionInfo().getDomainScope(domain);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
		return scope;
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
	
	
	public void close() {
		AonDatabaseUtil.closeQuietly(connection);
	}
		
	public void transaction(TransactionalRunnable transactional) {
		getDslContext().transaction(transactional);
	}
	

	public String getUser() {
		return user;
	}

}
