package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable.VariableType;

public class JooqEmployeeContractVariables {

	private static Settings settings;
	
	// ------------------------------- Construtor
	
	private JooqEmployeeContractVariables() {
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

	public static List<ContractVariable> getContractVariables(Connection conn, Integer contractId) {
		return getContractVariables(DSL.using(conn, getDefaultSettings()), contractId);	
	}

	public static void updateContractVariables(Connection conn, List<ContractVariable> contractVariables) {
		updateContractVariables(DSL.using(conn, getDefaultSettings()), contractVariables);
	}

	public static void createContractVariable(Connection conn, Integer domainId, Integer contractId, ContractVariable contractVariable) {
		createContractVariable(DSL.using(conn, getDefaultSettings()), domainId, contractId, contractVariable);
	}

	private static List<ContractVariable> getContractVariables(DSLContext dslContext, Integer contractId) {
		List<ContractVariable> contractVariable = new ArrayList<>();
		
		List<ContractVariable> contractData = getContractData(dslContext, contractId);
		List<ContractVariable> contractInfo = getContractInfo(dslContext, contractId);
		
		contractVariable.addAll(contractData);
		contractVariable.addAll(contractInfo);
		
		return contractVariable;
	}
	
	private static List<ContractVariable> getContractData(DSLContext dslContext, Integer contractId) {
		Result<Record> contractDataRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
			
		if(contractDataRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractVariable> contractVariables = new ArrayList<>();
			
			for(Record contractDataRecord : contractDataRecords) {
				ContractVariable contractVariable = new ContractVariable();
				
				contractVariable.setId(contractDataRecord.get(CONTRACT_DATA.ID))
								.setVariableType(VariableType.CONTRACT_DATA)
								.setDescription(contractDataRecord.get(CONTRACT_DATA.NAME))
								.setExpression(contractDataRecord.get(CONTRACT_DATA.EXPRESSION))
								.setStartDate(contractDataRecord.get(CONTRACT_DATA.START_DATE))
								.setEndDate(contractDataRecord.get(CONTRACT_DATA.END_DATE))
								.setHasChange(false);
				contractVariables.add(contractVariable);
			}
			
			return contractVariables;
		}
	}
	
	private static List<ContractVariable> getContractInfo(DSLContext dslContext, Integer contractId) {
		Result<Record> contractInfoRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.fetch();
			
		if(contractInfoRecords.isEmpty())
			return new ArrayList<>();
		else {
			List<ContractVariable> contractVariables = new ArrayList<>();
			
			for(Record contractInfoRecord : contractInfoRecords) {
				ContractVariable contractVariable = new ContractVariable();
				
				contractVariable.setId(contractInfoRecord.get(CONTRACT_INFO.ID))
								.setVariableType(VariableType.CONTRACT_INFO)
								.setDescription(contractInfoRecord.get(CONTRACT_INFO.NAME))
								.setExpression(contractInfoRecord.get(CONTRACT_INFO.EXPRESSION))
								.setStartDate(contractInfoRecord.get(CONTRACT_INFO.START_DATE))
								.setEndDate(contractInfoRecord.get(CONTRACT_INFO.END_DATE))
								.setHasChange(false);
				contractVariables.add(contractVariable);
			}
			
			return contractVariables;
		}
	}

	private static void updateContractVariables(DSLContext dslContext, List<ContractVariable> contractVariables) {
		for(ContractVariable contractVariable : contractVariables) {
			VariableType variableType = contractVariable.getVariableType();
			switch (variableType) {
				case CONTRACT_DATA:
					updateDeleteContractData(dslContext, contractVariable);
					break;
				case CONTRACT_INFO:
					updateDeleteContractInfo(dslContext, contractVariable);
					break;
				default:
					break;
			}
		}
	}

	private static void updateDeleteContractData(DSLContext dslContext, ContractVariable contractVariable) {
		if(contractVariable.getId() < 0) {
			Integer id = -1 * contractVariable.getId();
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(id))
				.execute();
		} else if(contractVariable.getHasChange())
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.NAME, contractVariable.getDescription())
				.set(CONTRACT_DATA.EXPRESSION, contractVariable.getExpression())
				.set(CONTRACT_DATA.START_DATE, parseToSQLDate(contractVariable.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, parseToSQLDate(contractVariable.getEndDate()))
				.where(CONTRACT_DATA.ID.eq(contractVariable.getId()))
				.execute();
	}
	
	private static void updateDeleteContractInfo(DSLContext dslContext, ContractVariable contractVariable) {
		if(contractVariable.getId() < 0) {
			Integer id = -1 * contractVariable.getId();
			dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(id))
				.execute();
		} else if(contractVariable.getHasChange())
			dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.NAME, contractVariable.getDescription())
				.set(CONTRACT_INFO.EXPRESSION, contractVariable.getExpression())
				.set(CONTRACT_INFO.START_DATE, parseToSQLDate(contractVariable.getStartDate()))
				.set(CONTRACT_INFO.END_DATE, parseToSQLDate(contractVariable.getEndDate()))
				.where(CONTRACT_INFO.ID.eq(contractVariable.getId()))
				.execute();
	}
	

	
	private static void createContractVariable(DSLContext dslContext, Integer domainId, Integer contractId, ContractVariable contractVariable) {
		VariableType variableType = contractVariable.getVariableType();
		switch (variableType) {
			case CONTRACT_DATA:
				createContractData(dslContext, domainId, contractId, contractVariable);
				break;
			case CONTRACT_INFO:
				createContractInfo(dslContext, domainId, contractId, contractVariable);
				break;
			default:
				break;
		}
	}

	private static void createContractData(DSLContext dslContext, Integer domainId, Integer contractId, ContractVariable contractVariable) {
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, domainId)
			.set(CONTRACT_DATA.CONTRACT, contractId)
			.set(CONTRACT_DATA.NAME, contractVariable.getDescription())
			.set(CONTRACT_DATA.EXPRESSION, contractVariable.getExpression())
			.set(CONTRACT_DATA.START_DATE, parseToSQLDate(contractVariable.getStartDate()))
			.set(CONTRACT_DATA.END_DATE, parseToSQLDate(contractVariable.getEndDate()))
			.execute();
	}

	private static void createContractInfo(DSLContext dslContext, Integer domainId, Integer contractId, ContractVariable contractVariable) {
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domainId)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, contractVariable.getDescription())
			.set(CONTRACT_INFO.EXPRESSION, contractVariable.getExpression())
			.set(CONTRACT_INFO.START_DATE, parseToSQLDate(contractVariable.getStartDate()))
			.set(CONTRACT_INFO.END_DATE, parseToSQLDate(contractVariable.getEndDate()))
			.execute();
	}

}
