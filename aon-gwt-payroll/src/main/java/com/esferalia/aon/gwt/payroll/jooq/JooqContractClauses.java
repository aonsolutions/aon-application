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
import com.esferalia.aon.watson.util.AonStringUtils;

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
		
		// Contract Clauses
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
	
	public static void setContractClauses(Connection conn, List<ContractClause> contractClauses) {
		setContractClausesDB(DSL.using(conn, getDefaultSettings()), contractClauses);
	}
	
	private static void setContractClausesDB(DSLContext dslContext, List<ContractClause> contractClauses) {
		
		for(ContractClause contractClause : contractClauses) {
			if(null == contractClause.getContract() || (contractClause.getId() == null && AonStringUtils.isBlank(contractClause.getName())))
				continue;
			
			if(contractClause.getId() == null && AonStringUtils.isNotBlank(contractClause.getName()))
				dslContext.insertInto(CONTRACT_CLAUSE)
					.set(CONTRACT_CLAUSE.DOMAIN, contractClause.getDomain())
					.set(CONTRACT_CLAUSE.CONTRACT, contractClause.getContract())
					.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
					.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
					.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
					.execute();
			else if(contractClause.getId() < 0)
				dslContext.delete(CONTRACT_CLAUSE)
					.where(CONTRACT_CLAUSE.ID.eq(contractClause.getId() * -1))
					.execute();
			else
				dslContext.update(CONTRACT_CLAUSE)
					.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
					.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
					.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
					.where(CONTRACT_CLAUSE.ID.eq(contractClause.getId()))
					.execute();
			
		}
	}
	
}
