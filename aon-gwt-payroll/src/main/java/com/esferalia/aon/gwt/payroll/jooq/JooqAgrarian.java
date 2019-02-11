package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;

public class JooqAgrarian {

	private static Settings SETTINGS = null;
	
	public static Map<Integer, List<AgrarianJourney>> getAgrarianJourney(java.util.Date startDate, java.util.Date endDate, String domain, Integer enterprise_ccc, Connection conn) {
		return getAgrarianJourneyoDB(startDate, endDate, domain, enterprise_ccc, DSL.using(conn, getDefaultSettings()));
	}
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static Map<Integer, List<AgrarianJourney>> getAgrarianJourneyoDB(java.util.Date  startDate, java.util.Date endDate, String domain, Integer enterprise_ccc, DSLContext dslContext) {
		
		Map<Integer, List<AgrarianJourney>> agrarianJourneyMap = new HashMap<>();
		
		Result<Record> agrarianJourney = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.in(
				dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ENTERPRISE_CCC.eq(enterprise_ccc))
					.fetch()	
			))
			.and(CONTRACT_DATA.NAME.eq("PEONADAS"))
			.and(CONTRACT_DATA.START_DATE.greaterOrEqual(new Date(startDate.getTime())))
			.and(CONTRACT_DATA.END_DATE.lessOrEqual(new Date(endDate.getTime())))
			.fetch();
		
		for (Record r : agrarianJourney){
			Result<Record> personRecords = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.in(
							dslContext.select(CONTRACT.PERSON).from(CONTRACT)
								.where(CONTRACT.ID.eq(r.get(CONTRACT_DATA.CONTRACT)))
							))
					.fetch();
			if(!personRecords.isEmpty())
				addAgrarianJourney(
						agrarianJourneyMap, 
						r.get(CONTRACT_DATA.CONTRACT), 
						r.get(CONTRACT_DATA.START_DATE),
						r.get(CONTRACT_DATA.END_DATE),
						personRecords.get(0).get(PERSON.NAME),
						personRecords.get(0).get(PERSON.FIRST_SURNAME)
				);
			
			
			System.out.println(
					"Contract : " + r.get(CONTRACT_DATA.CONTRACT) +
					" PEONADAS -> StartDate : " + r.get(CONTRACT_DATA.START_DATE) +
					" EndDate : " + r.get(CONTRACT_DATA.END_DATE) +
					" Nombre : " + personRecords.get(0).get(PERSON.NAME) +
					" Apellido : " + personRecords.get(0).get(PERSON.FIRST_SURNAME)
			);
		}
		
		
		return agrarianJourneyMap;
	}

	private static void addAgrarianJourney(
			Map<Integer, List<AgrarianJourney>> agrarianJourneyMap, 
			Integer contractId, Date startDate, 
			Date endDate,
			String name,
			String firstSurname) {
		
		List<AgrarianJourney> listJournies = agrarianJourneyMap.get(contractId);
		if(null == listJournies){
			List<AgrarianJourney> newJournies = new ArrayList<>();
			AgrarianJourney journey = new AgrarianJourney(contractId, startDate, endDate);
			journey.setName(name);
			journey.setSurname(firstSurname);
			newJournies.add(journey);
			agrarianJourneyMap.put(contractId, newJournies);
		}else{
			AgrarianJourney journey = new AgrarianJourney(contractId, startDate, endDate);
			journey.setName(name);
			journey.setSurname(firstSurname);
			listJournies.add(journey);
			agrarianJourneyMap.put(contractId, listJournies);
		}
	}

}
