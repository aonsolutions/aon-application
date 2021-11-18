package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.SSBonusData;

public class JooqContractBonus {

	// ---------------------------------------------------- Constructor
	
	private JooqContractBonus() {
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
	
	public static List<SSBonusData> getContractBonus(Connection conn, Integer contractId) {
		return getContractBonusDB(DSL.using(conn, getDefaultSettings()), contractId);
	}

	private static List<SSBonusData> getContractBonusDB(DSLContext dslContext, Integer contractId) {
		List<SSBonusData> contractBonusList = new ArrayList<>();
		
		Result<Record> contractBonusRecords = dslContext.select().from(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(contractId))
				.and(CONTRACT_BONUS.EXPRESSION.isNotNull().and(CONTRACT_BONUS.EXPRESSION.ne("")))
				.orderBy(CONTRACT_BONUS.START_DATE.desc())
				.fetch();
		
		for(Record r : contractBonusRecords) {
			SSBonusData ssBonusData = new SSBonusData();
			ssBonusData.setStartDate(r.get(CONTRACT_BONUS.START_DATE));
			ssBonusData.setEndDate(r.get(CONTRACT_BONUS.END_DATE));
			ssBonusData.setFormula(r.get(CONTRACT_BONUS.EXPRESSION));
			ssBonusData.setDescription(r.get(CONTRACT_BONUS.DESCRIPTION));
			
			contractBonusList.add(ssBonusData);
		}
		
		return contractBonusList;
	}
	
}
