package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities.Peculiarity;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeePeculiarities {
	
	private JooqEmployeePeculiarities() {
		super();
	}
	
	private static Settings serttings = null;
	
	protected static Settings getDefaultSettings() {
		if (serttings == null) {
			serttings = new Settings();
			serttings.setRenderSchema(false);
		}
		return serttings;
	}

	public static Peculiarities getPeculiarities(Integer contractId, Connection connection) {
		return getPeculiaritiesDB(contractId, DSL.using(connection, getDefaultSettings()));
	}
	
	public static String setPeculiarities(String domainName, Integer contractId, Peculiarities peculiarities, Connection connection) {
		return setPeculiaritiesDB(domainName, contractId, peculiarities, DSL.using(connection, getDefaultSettings()));
	}
	
	private static Peculiarities getPeculiaritiesDB(Integer contractId, DSLContext dslContext) {
		//Peculiarities Names
		ArrayList<String> peculiaritiesNames = new ArrayList<>();
		peculiaritiesNames.add("PORCENTAJE_CGC");
		peculiaritiesNames.add("PORCENTAJE_DESMPL");
		peculiaritiesNames.add("PORCENTAJE_FP");
		peculiaritiesNames.add("PORCENTAJE_CGC_E");
		peculiaritiesNames.add("PORCENTAJE_IT");
		peculiaritiesNames.add("PORCENTAJE_IMS");
		peculiaritiesNames.add("PORCENTAJE_FOGASA");
		peculiaritiesNames.add("PORCENTAJE_FP_E");
		peculiaritiesNames.add("PORCENTAJE_DESMPL_E");
		peculiaritiesNames.add("TARIFA_CGC");
		peculiaritiesNames.add("TARIFA_DESMPL");
		peculiaritiesNames.add("TARIFA_FP");
		peculiaritiesNames.add("TARIFA_CGC_E");
		peculiaritiesNames.add("TARIFA_IT");
		peculiaritiesNames.add("TARIFA_IMS");
		peculiaritiesNames.add("TARIFA_FOGASA");
		peculiaritiesNames.add("TARIFA_FP_E");
		peculiaritiesNames.add("TARIFA_DESMPL_E");
		
		//Peculiarities
		Peculiarities peculiarities = new Peculiarities();
		
		// Get TRL
		Date findDate = new Date(new java.util.Date().getTime());
		
		Result<Record> trlRecords = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.START_DATE.le(findDate))
			.and(CONTRACT_DATA.END_DATE.ge(findDate).or(CONTRACT_DATA.END_DATE.isNull()))
			.and(CONTRACT_DATA.NAME.eq("TRL"))
			.orderBy(CONTRACT_DATA.START_DATE.desc())
			.fetch();
		
		if(trlRecords.isNotEmpty()) {
			String trlValue = parseExpression(trlRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
			peculiarities.setTrl(trlValue);
		}
		
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
				
				ArrayList<Peculiarity> peculiaritiesStrech = new ArrayList<>();
				
				for(String peculiarityName: peculiaritiesNames) {
					if(AonStringUtils.containsIgnoreCase(peculiarityName, "TARIFA"))
						continue;
					ArrayList<String> peculiaritiesNamesAux = new ArrayList<>();
					peculiaritiesNamesAux.add(peculiarityName);
					peculiaritiesNamesAux.add(parseTarifa(peculiarityName));
					
					Record peculiarityDataRecord = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.NAME.in(peculiaritiesNamesAux))
						.and(CONTRACT_DATA.START_DATE.eq(pDate.get(CONTRACT_DATA.START_DATE)))
						.and(CONTRACT_DATA.CONTRACT.eq(contractId))
						.fetchOne();
					
					Peculiarity peculiarity = null;
					
					if(null == peculiarityDataRecord) {
						peculiarity = new Peculiarity(peculiarityName, "Sistema", false, null == peculiarityType ? null : Integer.parseInt(peculiarityType));
					}else {
						peculiarity = new Peculiarity(peculiarityName, peculiarityDataRecord.get(CONTRACT_DATA.EXPRESSION), true, null == peculiarityType ? null : Integer.parseInt(peculiarityType));
					}
					
					peculiaritiesStrech.add(peculiarity);
					
//					System.out.println(peculiarity.getName() + " -> " + peculiarity.getValue() + "(" + pDate.get(CONTRACT_DATA.START_DATE) + ")");
				}
				
				peculiarities.getPeculiarities().put(pDate.get(CONTRACT_DATA.START_DATE), peculiaritiesStrech);

			}
		
		}
		
//		peculiarities.getPeculiarities().entrySet().forEach(entry -> entry.getValue().forEach(pec -> System.out.println(pec.getName() + " - " + pec.getValue() + " (" + entry.getKey() + ")")));
		
		return peculiarities;
	}
	
	private static String parseTarifa(String peculiarityName) {
		return AonStringUtils.replace(peculiarityName, "PORCENTAJE", "TARIFA");
	}

	private static String parseExpression(String expr) {
		if(expr.contains("\""))
			return expr.split("\"")[1];
		return expr;
	}

	private static String setPeculiaritiesDB(String domainName, Integer contractId, Peculiarities peculiarities, DSLContext dslContext) {
		
		List<Date> deletedDates = getDeletedDates(dslContext, contractId, peculiarities);
		
		//Peculiarities Names
		ArrayList<String> peculiaritiesNames = new ArrayList<>();
		peculiaritiesNames.add("PORCENTAJE_CGC");
		peculiaritiesNames.add("PORCENTAJE_DESMPL");
		peculiaritiesNames.add("PORCENTAJE_FP");
		peculiaritiesNames.add("PORCENTAJE_CGC_E");
		peculiaritiesNames.add("PORCENTAJE_IT");
		peculiaritiesNames.add("PORCENTAJE_IMS");
		peculiaritiesNames.add("PORCENTAJE_FOGASA");
		peculiaritiesNames.add("PORCENTAJE_FP_E");
		peculiaritiesNames.add("PORCENTAJE_DESMPL_E");
		peculiaritiesNames.add("TARIFA_CGC");
		peculiaritiesNames.add("TARIFA_DESMPL");
		peculiaritiesNames.add("TARIFA_FP");
		peculiaritiesNames.add("TARIFA_CGC_E");
		peculiaritiesNames.add("TARIFA_IT");
		peculiaritiesNames.add("TARIFA_IMS");
		peculiaritiesNames.add("TARIFA_FOGASA");
		peculiaritiesNames.add("TARIFA_FP_E");
		peculiaritiesNames.add("TARIFA_DESMPL_E");
		
		//Delete old records
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(peculiaritiesNames))
			.and(CONTRACT_DATA.START_DATE.in(deletedDates))
			.execute();
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq("PECULIARITY_TYPE"))
			.and(CONTRACT_DATA.START_DATE.in(deletedDates))
			.execute();
		
		//Get domain id
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne(DOMAIN.ID);
		
		boolean insert = false;
		
		for(Entry<java.util.Date, ArrayList<Peculiarity>> entry : peculiarities.getPeculiarities().entrySet()) {
			insert = false;
			for(Peculiarity peculiarity : entry.getValue()) {
				if(Boolean.TRUE.equals(peculiarity.isChecked())) {
					Date date = new Date(entry.getKey().getTime());
					if(notTarifaExist(dslContext, contractId, peculiarity.getName(), date)) {
						ContractDataRecord peculiarityRecord = dslContext.selectFrom(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.NAME.eq(peculiarity.getName()))
							.and(CONTRACT_DATA.START_DATE.eq(date))
							.fetchOne();
						
						if(null == peculiarityRecord) {
							dslContext.insertInto(CONTRACT_DATA)
								.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.NAME, peculiarity.getName())
								.set(CONTRACT_DATA.CONTRACT, contractId)
								.set(CONTRACT_DATA.EXPRESSION, peculiarity.getValue())
								.set(CONTRACT_DATA.START_DATE, date)
								.set(CONTRACT_DATA.END_DATE, (Date) null)
								.execute();
							
							insert = true;
						} else
							dslContext.update(CONTRACT_DATA)
								.set(CONTRACT_DATA.EXPRESSION, peculiarity.getValue())
								.where(CONTRACT_DATA.ID.eq(peculiarityRecord.getId()))
								.execute();
					}
				}
			}
			
			ContractDataRecord peculiarityTypeRecord = dslContext.selectFrom(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("PECULIARITY_TYPE"))
					.and(CONTRACT_DATA.START_DATE.eq(new Date(entry.getKey().getTime())))
					.fetchOne();
			
			if(!entry.getValue().isEmpty() && insert && null == peculiarityTypeRecord)
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

	private static List<Date> getDeletedDates(DSLContext dslContext, Integer contractId, Peculiarities peculiarities) {
		//Peculiarities Names
		ArrayList<String> peculiaritiesNames = new ArrayList<>();
		peculiaritiesNames.add("PORCENTAJE_CGC");
		peculiaritiesNames.add("PORCENTAJE_DESMPL");
		peculiaritiesNames.add("PORCENTAJE_FP");
		peculiaritiesNames.add("PORCENTAJE_CGC_E");
		peculiaritiesNames.add("PORCENTAJE_IT");
		peculiaritiesNames.add("PORCENTAJE_IMS");
		peculiaritiesNames.add("PORCENTAJE_FOGASA");
		peculiaritiesNames.add("PORCENTAJE_FP_E");
		peculiaritiesNames.add("PORCENTAJE_DESMPL_E");
		peculiaritiesNames.add("TARIFA_CGC");
		peculiaritiesNames.add("TARIFA_DESMPL");
		peculiaritiesNames.add("TARIFA_FP");
		peculiaritiesNames.add("TARIFA_CGC_E");
		peculiaritiesNames.add("TARIFA_IT");
		peculiaritiesNames.add("TARIFA_IMS");
		peculiaritiesNames.add("TARIFA_FOGASA");
		peculiaritiesNames.add("TARIFA_FP_E");
		peculiaritiesNames.add("TARIFA_DESMPL_E");
		
		//Peculiarities
		List<Date> deletedDates = new ArrayList<>();
		
		//Find peculiarities DB
		Result<Record1<Date>> peculiaritiesDatesRecords = dslContext.selectDistinct(CONTRACT_DATA.START_DATE).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.in(peculiaritiesNames))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.orderBy(CONTRACT_DATA.START_DATE)
				.fetch();
		
		if(peculiaritiesDatesRecords.isNotEmpty()) 
			for(Record1<Date> r : peculiaritiesDatesRecords)
				deletedDates.add(r.get(CONTRACT_DATA.START_DATE));
		
		Set<java.util.Date> currentDates = peculiarities.getPeculiarities().keySet();
		
		deletedDates = deletedDates.stream().filter(o -> !currentDates.contains(o)).collect(Collectors.toList());;
		
		System.out.println(deletedDates.toString());
		
		return deletedDates;
			
	}

	private static boolean notTarifaExist(DSLContext dslContext, Integer contractId, String name, Date date) {
		Result<Record> records = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.START_DATE.eq(date))
			.and(CONTRACT_DATA.NAME.eq(parseTarifa(name)))
			.fetch();
		
		return records.isEmpty();
	}
	
	
}
