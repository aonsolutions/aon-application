package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.SystemVariable;
import com.esferalia.aon.gwt.payroll.shared.SystemVariableType;

public class JooqDomainSystemVariables {

	private static Settings settings;
	
	// ------------------------------- Construtor
	
	private JooqDomainSystemVariables() {
		super();
	}
	
	// ------------------------------- Auxiliar Methods
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date)
			return null;

		return new Date(date.getTime());
	}
	
	// ------------------------------- Database Methods

	public static List<SystemVariable> getSystemVariables(Connection conn, Integer domainId) {
		return getSystemVariables(DSL.using(conn, getDefaultSettings()), domainId);	
	}

	public static void updateSystemVariables(Connection conn, List<SystemVariable> systemVariables) {
		updateSystemVariables(DSL.using(conn, getDefaultSettings()), systemVariables);
	}

	public static void createSystemVariable(Connection conn, Integer domainId, SystemVariable systemVariable) {
		createSystemVariable(DSL.using(conn, getDefaultSettings()), domainId, systemVariable);
	}

	private static List<SystemVariable> getSystemVariables(DSLContext dslContext, Integer domainId) {
		List<SystemVariable> systemVariable = new ArrayList<>();
		
		List<SystemVariable> systemData = getSystemData(dslContext, domainId);
		
		systemVariable.addAll(systemData);
		
		return systemVariable;
	}
	
	private static List<SystemVariable> getSystemData(DSLContext dslContext, Integer domainId) {
		Result<Record> systemDataRecords = dslContext.select().from(SYSTEM_DATA)
				.where(SYSTEM_DATA.DOMAIN.eq(domainId))
				.fetch();
			
		if(systemDataRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<SystemVariable> systemVariables = new ArrayList<>();
			
			for(Record systemDataRecord : systemDataRecords) {
				SystemVariable systemVariable = new SystemVariable();
				
				systemVariable.setId(systemDataRecord.get(SYSTEM_DATA.ID))
								.setVariableType(SystemVariableType.SYSTEM_DATA)
								.setDescription(systemDataRecord.get(SYSTEM_DATA.NAME))
								.setExpression(systemDataRecord.get(SYSTEM_DATA.EXPRESSION))
								.setStartDate(systemDataRecord.get(SYSTEM_DATA.START_DATE))
								.setEndDate(systemDataRecord.get(SYSTEM_DATA.END_DATE))
								.setHasChange(false);
				systemVariables.add(systemVariable);
			}
			
			return systemVariables;
		}
	}
	

	private static void updateSystemVariables(DSLContext dslContext, List<SystemVariable> systemVariables) {
		for(SystemVariable systemVariable : systemVariables) {
			SystemVariableType systemVariableType = systemVariable.getVariableType();
			switch (systemVariableType) {
				case SYSTEM_DATA:
					updateDeleteContractData(dslContext, systemVariable);
					break;
				default:
					break;
			}
		}
	}

	private static void updateDeleteContractData(DSLContext dslContext, SystemVariable systemVariable) {
		if(systemVariable.getId() < 0) {
			Integer id = -1 * systemVariable.getId();
			dslContext.delete(SYSTEM_DATA)
				.where(SYSTEM_DATA.ID.eq(id))
				.execute();
		} else if(systemVariable.getHasChange())
			dslContext.update(SYSTEM_DATA)
				.set(SYSTEM_DATA.NAME, systemVariable.getDescription())
				.set(SYSTEM_DATA.EXPRESSION, systemVariable.getExpression())
				.set(SYSTEM_DATA.START_DATE, parseToSQLDate(systemVariable.getStartDate()))
				.set(SYSTEM_DATA.END_DATE, parseToSQLDate(systemVariable.getEndDate()))
				.where(SYSTEM_DATA.ID.eq(systemVariable.getId()))
				.execute();
	}
	
	
	private static void createSystemVariable(DSLContext dslContext, Integer domainId, SystemVariable systemVariable) {
		SystemVariableType systemVariableType = systemVariable.getVariableType();
		switch (systemVariableType) {
			case SYSTEM_DATA:
				createSystemData(dslContext, domainId, systemVariable);
				break;
			default:
				break;
		}
	}

	private static void createSystemData(DSLContext dslContext, Integer domainId, SystemVariable systemVariable) {
		dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, domainId)
			.set(SYSTEM_DATA.NAME, systemVariable.getDescription())
			.set(SYSTEM_DATA.EXPRESSION, systemVariable.getExpression())
			.set(SYSTEM_DATA.START_DATE, parseToSQLDate(systemVariable.getStartDate()))
			.set(SYSTEM_DATA.END_DATE, parseToSQLDate(systemVariable.getEndDate()))
			.execute();
	}


}
