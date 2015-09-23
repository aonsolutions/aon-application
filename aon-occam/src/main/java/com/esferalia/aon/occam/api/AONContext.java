package com.esferalia.aon.occam.api;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.TransactionalRunnable;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.code.aon.pool.AonConnectionException;
import com.code.aon.pool.AonDataSource;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class AONContext {

	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";
	private static Settings SETTINGS = null;
	
	private static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
			SETTINGS.setParamType( ParamType.INLINED );
		}
		return SETTINGS;
	}

	public static AONContext getAONContext(String domainName, int domainId) {
		try {
			return new AONContext(AonDataSource.getInstance().getConnection(
					domainName), domainName, domainId);
		} catch (AonConnectionException e) {
			throw new AonCoreException(e.getMessage(),e);
		}
	}
	
	private DSLContext dslContext;
	private Connection connection;
	private String domainName;
	private int domainId;

	public AONContext(DSLContext dslContext) {
		this.dslContext = dslContext;
	}


	public AONContext(Connection connection) {
		this(DSL.using(connection,getDefaultSettings()));
		
	}

	private AONContext(Connection connection, String domainName, int domainId) {
		this.domainName = domainName;
		this.domainId = domainId;
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void activateForeignKeys(){
		try{
			Statement sClose = connection.createStatement();
			sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
			System.out.println("Claves referenciales activadas");		
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
	
	
	// TODO here?
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

}
