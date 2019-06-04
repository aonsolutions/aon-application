package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities.Peculiarity;

public class JooqEmployeePeculiarities {
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	public static Peculiarities getPeculiarities(String domainName, Integer contractId, Connection connection) {
		return getPeculiaritiesDB(domainName, contractId, DSL.using(connection, getDefaultSettings()));
	}
	
	public static String setPeculiarities(String domainName, Integer contractId, Peculiarities peculiarities, Connection connection) {
		return setPeculiaritiesDB(domainName, contractId, peculiarities, DSL.using(connection, getDefaultSettings()));
	}
	
	private static Peculiarities getPeculiaritiesDB(String domainName, Integer contractId, DSLContext dslContext) {
		//Peculiarities Names
		ArrayList<String> peculiaritiesNames = new ArrayList<String>();
		peculiaritiesNames.add("PORCENTAJE_CGC");
		peculiaritiesNames.add("PORCENTAJE_DESMPL");
		peculiaritiesNames.add("PORCENTAJE_FP");
		peculiaritiesNames.add("PORCENTAJE_CGC_E");
		peculiaritiesNames.add("PORCENTAJE_IT");
		peculiaritiesNames.add("PORCENTAJE_IMS");
		peculiaritiesNames.add("PORCENTAJE_FOGASA");
		peculiaritiesNames.add("PORCENTAJE_FP_E");
		peculiaritiesNames.add("PORCENTAJE_DESMPL_E");
		
		//Peculiarities
		Peculiarities peculiarities = new Peculiarities();
		
		//Find peculiarities DB
		Result<Record1<Date>> peculiaritiesDatesRecords = dslContext.selectDistinct(CONTRACT_DATA.START_DATE).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.in(peculiaritiesNames))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_DATA.START_DATE)
				.fetch();
		
		if(peculiaritiesDatesRecords.isNotEmpty()) {
			
			for(Record pDate : peculiaritiesDatesRecords) {
				
				String peculiarityType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
					.where(CONTRACT_DATA.NAME.eq("PECULIARITY_TYPE"))
					.and(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.START_DATE.eq(pDate.get(CONTRACT_DATA.START_DATE)))
					.fetchOne(CONTRACT_DATA.EXPRESSION);
				
				ArrayList<Peculiarity> peculiaritiesStrech = new ArrayList<Peculiarity>();
				
				for(String peculiarityName: peculiaritiesNames) {
					Record peculiarityDataRecord = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.NAME.eq(peculiarityName))
						.and(CONTRACT_DATA.START_DATE.eq(pDate.get(CONTRACT_DATA.START_DATE)))
						.and(CONTRACT_DATA.CONTRACT.eq(contractId))
						.fetchOne();
					
					Peculiarity peculiarity = null;
					
					if(null == peculiarityDataRecord) {
						peculiarity = new Peculiarity(peculiarityName, "Sistema", false, Integer.parseInt(peculiarityType));
					}else {
						peculiarity = new Peculiarity(peculiarityName, peculiarityDataRecord.get(CONTRACT_DATA.EXPRESSION), true, Integer.parseInt(peculiarityType));
					}
					
					peculiaritiesStrech.add(peculiarity);
				}
				
				peculiarities.getPeculiarities().put(pDate.get(CONTRACT_DATA.START_DATE), peculiaritiesStrech);

			}
		
		}
		
		return peculiarities;
	}
	
	private static String setPeculiaritiesDB(String domainName, Integer contractId, Peculiarities peculiarities, DSLContext dslContext) {
		
		//Peculiarities Names
		ArrayList<String> peculiaritiesNames = new ArrayList<String>();
		peculiaritiesNames.add("PORCENTAJE_CGC");
		peculiaritiesNames.add("PORCENTAJE_DESMPL");
		peculiaritiesNames.add("PORCENTAJE_FP");
		peculiaritiesNames.add("PORCENTAJE_CGC_E");
		peculiaritiesNames.add("PORCENTAJE_IT");
		peculiaritiesNames.add("PORCENTAJE_IMS");
		peculiaritiesNames.add("PORCENTAJE_FOGASA");
		peculiaritiesNames.add("PORCENTAJE_FP_E");
		peculiaritiesNames.add("PORCENTAJE_DESMPL_E");
		
		//Delete old records
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(peculiaritiesNames))
			.execute();
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq("PECULIARITY_TYPE"))
			.execute();
		
		//Get domain id
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne(DOMAIN.ID);
		
		for(Entry<java.util.Date, ArrayList<Peculiarity>> entry : peculiarities.getPeculiarities().entrySet()) {
			for(Peculiarity peculiarity : entry.getValue()) {
				if(peculiarity.isChecked()) {
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, peculiarity.getName())
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, peculiarity.getValue())
						.set(CONTRACT_DATA.START_DATE, new Date(entry.getKey().getTime()))
						.set(CONTRACT_DATA.END_DATE, (Date) null)
						.execute();
				}
			}
			
			if(entry.getValue().size() > 0)
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "PECULIARITY_TYPE")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, entry.getValue().get(0).getType().toString())
					.set(CONTRACT_DATA.START_DATE, new Date(entry.getKey().getTime()))
					.set(CONTRACT_DATA.END_DATE, (Date) null)
					.execute();
		}
		
		return "";
	}
	
	
}
