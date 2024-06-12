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
import com.esferalia.aon.jooq.tables.records.ContractClauseRecord;
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
			.orderBy(CONTRACT_CLAUSE.LINE)
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
		
		//contractClauses.sort((o1, o2) -> o1.getLineNumber().compareTo(o2.getLineNumber()));
		
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
			else
				dslContext.update(CONTRACT_CLAUSE)
					.set(CONTRACT_CLAUSE.LINE, contractClause.getLineNumber())
					.set(CONTRACT_CLAUSE.NAME, contractClause.getName())
					.set(CONTRACT_CLAUSE.DESCRIPTION, contractClause.getDescription())
					.where(CONTRACT_CLAUSE.ID.eq(contractClause.getId()))
					.execute();
			
		}
	}
	
	public static void deleteContractClause(Connection conn, Integer clauseId) {
		deleteContractClauseDB(DSL.using(conn, getDefaultSettings()), clauseId);
	}
	
	private static void deleteContractClauseDB(DSLContext dslContext, Integer clauseId) {
		dslContext.delete(CONTRACT_CLAUSE)
		.where(CONTRACT_CLAUSE.ID.eq(clauseId))
		.execute();
	}

	public static List<ContractClause> getDomainClauses(Connection conn, Integer domainId, Integer parentDomainId) {
		return getDomainClausesDB(DSL.using(conn, getDefaultSettings()), domainId, parentDomainId);
	}

	private static List<ContractClause> getDomainClausesDB(DSLContext dslContext, Integer domainId, Integer parentDomainId) {
		List<ContractClause> contractClauses = new ArrayList<>();
		
		// Contract Clauses
		Result<Record> clauseRecords = dslContext.select().from(CONTRACT_CLAUSE)
			.where(CONTRACT_CLAUSE.CONTRACT.isNull())
			.and(CONTRACT_CLAUSE.DOMAIN.eq(domainId)
				.or(CONTRACT_CLAUSE.DOMAIN.eq(parentDomainId))
			).fetch();
		
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
		
		contractClauses.sort((o1, o2) -> o1.getLineNumber().compareTo(o2.getLineNumber()));
		
		return contractClauses;
	}

	public static void importContractClauses(Connection conn, List<Integer> clausesIds, Integer domainId, Integer contractId) throws IllegalArgumentException {
		importContractClausesDB(DSL.using(conn, getDefaultSettings()), clausesIds, domainId, contractId);
	}

	private static void importContractClausesDB(DSLContext dslContext, List<Integer> clausesIds, Integer domainId, Integer contractId) throws IllegalArgumentException {
		// Contract Clauses
		Result<ContractClauseRecord> clauseRecords = dslContext.selectFrom(CONTRACT_CLAUSE).where(CONTRACT_CLAUSE.ID.in(clausesIds)).fetch();
		
		clauseRecords.forEach(clause -> {
			dslContext.insertInto(CONTRACT_CLAUSE)
				.set(CONTRACT_CLAUSE.DOMAIN, domainId)
				.set(CONTRACT_CLAUSE.CONTRACT, contractId)
				.set(CONTRACT_CLAUSE.LINE, clause.getLine())
				.set(CONTRACT_CLAUSE.NAME, clause.getName())
				.set(CONTRACT_CLAUSE.DESCRIPTION, clause.getDescription())
				.set(CONTRACT_CLAUSE.GENERAL, (byte)0)
				.execute();
		});
	}

}
