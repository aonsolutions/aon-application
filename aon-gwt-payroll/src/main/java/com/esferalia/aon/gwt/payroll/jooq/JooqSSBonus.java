package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;

public class JooqSSBonus {

	private static Settings SETTINGS = null;

	public static List<SSBonusData> getSSBonus(Connection conn, Integer contract) throws IllegalArgumentException {
		return getSSBonusInformation(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static List<SSBonusData> setSSBonus(Connection conn, Integer contract, List<SSBonusData> updateInfo){
		return setSSBonusInformation(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static List<SSBonusData> getSSBonusInformation(DSLContext dslContext, Integer contract) {
		List<SSBonusData> result = new ArrayList<SSBonusData>();
		
		Result<Record> bonuses = dslContext.select()
					.from(CONTRACT_BONUS)
					.where(CONTRACT_BONUS.CONTRACT.eq(contract))
					.fetch();
		
		int contId = 0;
		for(Record b : bonuses){
			Record bonusConcept = null;
			if(b.get(CONTRACT_BONUS.BONUS_CONCEPT) != null){
				bonusConcept = dslContext.select()
						.from(BONUS_CONCEPT)
						.where(BONUS_CONCEPT.ID.eq(b.get(CONTRACT_BONUS.BONUS_CONCEPT)))
						.fetchOne();
			}
			
			SSBonusData bonusData = new SSBonusData();
			bonusData.setId(contId);
			bonusData.setStartDate(b.get(CONTRACT_BONUS.START_DATE));
			bonusData.setEndDate(b.get(CONTRACT_BONUS.END_DATE));
			bonusData.setDescription(b.get(CONTRACT_BONUS.DESCRIPTION));
			bonusData.setType(bonusConcept == null ? null : bonusConcept.get(BONUS_CONCEPT.TYPE));
			bonusData.setFormula(b.get(CONTRACT_BONUS.EXPRESSION));
			
			result.add(bonusData);
			contId++;
		}
		
		return result;
	}
	
	private static List<SSBonusData> setSSBonusInformation(DSLContext dslContext, Integer contract, List<SSBonusData> updateInfo) {
		//GET domain
		Record contractRecord = dslContext.select()
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		Integer domain = contractRecord.get(CONTRACT.DOMAIN);
		
		Result<Record> contractBonusRecords = dslContext.select()
			.from(CONTRACT_BONUS)
			.where(CONTRACT_BONUS.CONTRACT.eq(contract))
			.fetch();
		
		//DELETE all contract bonus from a contract
		dslContext.delete(CONTRACT_BONUS)
			.where(CONTRACT_BONUS.CONTRACT.eq(contract))
			.execute();
		
		for(Record bonus : contractBonusRecords){
			//DELETE all bonus concept from a contract
			dslContext.delete(BONUS_CONCEPT)
				.where(BONUS_CONCEPT.ID.eq(bonus.get(CONTRACT_BONUS.BONUS_CONCEPT))
				).execute();
		}
		
		for(SSBonusData bonus : updateInfo){
			BonusConceptRecord bonusConceptRecord = dslContext.insertInto(BONUS_CONCEPT)
									.set(BONUS_CONCEPT.DOMAIN, domain)
									.set(BONUS_CONCEPT.EXPRESSION, bonus.getFormula())
									.set(BONUS_CONCEPT.DESCRIPTION, bonus.getDescription())
									.set(BONUS_CONCEPT.TYPE, bonus.getType())
									.returning(BONUS_CONCEPT.ID).fetchOne();
			
			int bonusConceptId = bonusConceptRecord.getId();
			Date startDate = new Date(bonus.getStartDate().getTime());
			Date endDate = new Date(bonus.getEndDate().getTime());
			
			dslContext.insertInto(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DOMAIN, domain)
				.set(CONTRACT_BONUS.CONTRACT, contract)
				.set(CONTRACT_BONUS.DESCRIPTION, bonus.getDescription())
				.set(CONTRACT_BONUS.EXPRESSION, bonus.getFormula())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, endDate)
				.set(CONTRACT_BONUS.BONUS_CONCEPT, bonusConceptId)
				.execute();
		}
		
		return updateInfo;
	}

}
