package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ContractClause;

public class JooqContractClauses {

	// ---------------------------------------------------- Constructor
	
	private JooqContractClauses() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ---------------------------------------------------- DataBase
	
	public static List<ContractClause> getContractClauses(Connection conn, Integer contractId) {
		return getContractClausesDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	private static List<ContractClause> getContractClausesDB(DSLContext dslContext, Integer contractId) {
		List<ContractClause> contractClauses = new ArrayList<>();
		
		Result<Record> clauseRecords = dslContext.select().from(CONTRACT_CLAUSE)
			.where(CONTRACT_CLAUSE.CONTRACT.eq(contractId))
			.fetch();
		
		for(Record clauseRecord : clauseRecords) {
			
			ContractClause contractClause =  new ContractClause();
			contractClause.setId(clauseRecord.get(CONTRACT_CLAUSE.ID));
			contractClause.setDomain(clauseRecord.get(CONTRACT_CLAUSE.DOMAIN));
			contractClause.setContract(clauseRecord.get(CONTRACT_CLAUSE.CONTRACT));
			contractClause.setLineNumber(clauseRecord.get(CONTRACT_CLAUSE.LINE));
			contractClause.setName(clauseRecord.get(CONTRACT_CLAUSE.NAME));
			contractClause.setDescription(clauseRecord.get(CONTRACT_CLAUSE.DESCRIPTION));
			contractClause.setGeneral(clauseRecord.get(CONTRACT_CLAUSE.GENERAL));
			
			contractClauses.add(contractClause);
		}
		
		return contractClauses;
	}

	public static List<ContractClause> createContractClause(Connection conn, ContractClause contractClause) {
		return createContractClauseDB(DSL.using(conn, getDefaultSettings()), contractClause);
	}
	
	private static List<ContractClause> createContractClauseDB(DSLContext dslContext, ContractClause contractClause) {
		 dslContext.insertInto(CONTRACT_CLAUSE)
			.set(CONTRACT_CLAUSE.DOMAIN, contractClause.getDomain())
			.set(CONTRACT_CLAUSE.CONTRACT, contractClause.getContract())
			.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
			.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
			.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
			.execute();
		 
		return getContractClausesDB(dslContext, contractClause.getContract());
	}

	public static List<ContractClause> deleteContractClause(Connection conn, ContractClause contractClause) {
		return deleteContractClauseDB(DSL.using(conn, getDefaultSettings()), contractClause);
	}
	
	private static List<ContractClause> deleteContractClauseDB(DSLContext dslContext, ContractClause contractClause) {
		dslContext.delete(CONTRACT_CLAUSE).where(CONTRACT_CLAUSE.ID.eq(contractClause.getId())).execute();	
		return getContractClausesDB(dslContext, contractClause.getContract());
	}
	
	public static List<ContractClause> setContractClauses(Connection conn, List<ContractClause> contractClauses) {
		return setContractClausesDB(DSL.using(conn, getDefaultSettings()), contractClauses);
	}
	
	private static List<ContractClause> setContractClausesDB(DSLContext dslContext, List<ContractClause> contractClauses) {
		
		for(ContractClause contractClause : contractClauses) {
			if(null == contractClause.getContract())
				continue;
			
			dslContext.update(CONTRACT_CLAUSE)
				.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
				.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
				.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
				.where(CONTRACT_CLAUSE.ID.eq(contractClause.getId()))
				.execute();
			
		}

		return contractClauses;
	}
	
}
