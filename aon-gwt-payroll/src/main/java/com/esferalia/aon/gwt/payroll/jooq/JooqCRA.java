package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.jooq.tables.records.CraBatchRecord;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;

public class JooqCRA {
	
	// --------------------------------------------- Constructor
	
	private JooqCRA() {
		super();
	}
	
	// --------------------------------------------- Variables
	
	private static Settings settings = null;
	private static SimpleDateFormat formatDate = new SimpleDateFormat("ddHHmmss");
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// --------------------------------------------- Check Create CRA
	
	public static String checkCreateNewCRA(Connection conn, long findingDate, ArrayList<Integer> cccList) {
		return checkCreateNewCRA(findingDate, cccList, DSL.using(conn, getDefaultSettings()));
	}

	private static String checkCreateNewCRA(long findingDate, ArrayList<Integer> cccList, DSLContext dslContext) {
		Date startDate = new Date(findingDate);
		Date endDate = DateUtils.getLastDayOfMonth(startDate);
		
		Result<Record> contractsActiveRecords = dslContext.select().from(CONTRACT)
			.where(
					CONTRACT.END_DATE.isNull()
					.or(CONTRACT.END_DATE.ge(parseDateToSQL(startDate))))
			.and(CONTRACT.START_DATE.lt(parseDateToSQL(endDate)))
			.and(CONTRACT.ENTERPRISE_CCC.in(cccList))
			.and(CONTRACT.ID.ge(0))
			.fetch();
		
		for(Record contractRecord : contractsActiveRecords) {
			Integer contractId = contractRecord.get(CONTRACT.ID);
			
			Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.START_DATE.ge(parseDateToSQL(startDate)))
				.and(SALARY.END_DATE.le(parseDateToSQL(endDate)))
				.fetch();
			
			if(salariesRecords.isEmpty()) {
				Integer personId = contractRecord.get(CONTRACT.PERSON);
				Record personRecord = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(personId)).fetchOne();
				
				return personRecord.get(PERSON.NAME) + " " + personRecord.get(PERSON.FIRST_SURNAME) 
				+ " no tiene n\u00F3mina emitida para este perido. \u00BFDesea continuar?";
			}
				 
		}
		
		return "";
	}

	// --------------------------------------------- Get CRAs
	
	public static List<CRA> getDomainCRAs(Integer domainId, Integer userId, long liquidDateTime, Connection conn) {
		return getDomainCRAsDB(domainId, userId, DSL.using(conn, getDefaultSettings()), liquidDateTime);
	}
	
	private static List<CRA> getDomainCRAsDB(Integer domainId, Integer userId, DSLContext dslContext, long liquidDateTime) {
		List<CRA> cras = new ArrayList<>();
		Date endDate = getEndDate(liquidDateTime);
		
		// Domain Childs
		List<Integer> domainChilds = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.PARENT.eq(domainId))
				.and(DOMAIN.SCOPE.in(
					dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.eq(userId))
						.fetch(USER_SCOPE.SCOPE)
				).or(DOMAIN.SCOPE.isNull())).fetch(DOMAIN.ID);
		
		
		// Get CRAs
		Result<Record6<Integer, Integer, Byte, Timestamp, Timestamp, String>> craBatchRecords = 
				dslContext.select(CRA_BATCH.ID, CRA_BATCH.DOMAIN, CRA_BATCH.STATUS, CRA_BATCH.DATE, CRA_BATCH.OUTCOME_FILE_DATE, CRA_BATCH.COMMUNICATION_ID).from(CRA_BATCH)
					.where(CRA_BATCH.DOMAIN.eq(domainId)
						   .or(CRA_BATCH.DOMAIN.in(domainChilds)))
					.and(CRA_BATCH.OUTCOME_FILE_DATE.ge(new Timestamp(liquidDateTime)))
					.and(CRA_BATCH.OUTCOME_FILE_DATE.le(new Timestamp(endDate.getTime())))
					.fetch();
		
		Integer countCras = 0;
		
		for(Record craBatchRecord : craBatchRecords) {
			Integer craBatchId = craBatchRecord.get(CRA_BATCH.ID);
			Integer craBatchDomain = craBatchRecord.get(CRA_BATCH.DOMAIN);
			
			CRA cra = new CRA();
			
			cra.setCode(craBatchRecord.get(CRA_BATCH.ID));
			cra.setDomain(craBatchDomain);
			cra.setStatus(craBatchRecord.get(CRA_BATCH.STATUS));
			cra.setDate(craBatchRecord.get(CRA_BATCH.DATE));
			cra.setCreationDate(craBatchRecord.get(CRA_BATCH.OUTCOME_FILE_DATE));
			cra.setType(craBatchRecord.get(CRA_BATCH.COMMUNICATION_ID));
			
			Result<Record> craBatchDetailRecords = dslContext.select().from(CRA_BATCH_DETAIL)
					.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(craBatchId))
					.fetch();
			
			// isConsignment (es remesa)
			Boolean isConsignment = false;
			
			if(craBatchDetailRecords.size() > 1)
				isConsignment = true;
			
			List<CCCInfo> includeCCCs = new ArrayList<>();
			
			for(Record craBatchDetailRecord : craBatchDetailRecords) {
				
				countCras++;
				
				Integer enterpriseCCCId = craBatchDetailRecord.get(CRA_BATCH_DETAIL.ENTERPRISE_CCC);
				
				if(null == enterpriseCCCId)
					continue;
				
				Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
						.fetchOne();
				
				Integer cccId = enterpriseCCCRecord.get(ENTERPRISE_CCC.ID);
				String cccCode = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
				String regime = getSSRegime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)).getCode();
				Byte type = enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
				
				String geozoneCode = dslContext.select(GEOZONE.CODE).from(GEOZONE)
						.where(GEOZONE.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE)))
						.fetchOne(GEOZONE.CODE);
				
				// ENTERPRISE ACTIVITY
				Record enterpriseActivityRecord = dslContext.select().from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
						.fetchOne();
				
				Integer enterpriseActivityId = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.ID);
				String enterpriseActivityDescription = enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION);
				
				// ENTERPRISE REGISTRY
				
				Record enterpriseRegistry = dslContext.select().from(REGISTRY)
						.where(REGISTRY.ID.eq(enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.ENTERPRISE)))
						.fetchOne();
				
				Integer enterpriseId = enterpriseRegistry.get(REGISTRY.ID);
				String enterpriseName =  enterpriseRegistry.get(REGISTRY.NAME);
				
				CCCInfo cccInfo = new CCCInfo();
				cccInfo.setCccId(cccId);
				cccInfo.setCcc(cccCode);
				cccInfo.setCccAccount(cccCode);
				cccInfo.setCccRegimeCode(regime);
				cccInfo.setTypeStr(regime);
				cccInfo.setGeozone(geozoneCode);
				cccInfo.setType(type);
				cccInfo.setActivityId(enterpriseActivityId);
				cccInfo.setActivityDescription(enterpriseActivityDescription);
				cccInfo.setUseByContracts(true);
				cccInfo.setEnterpriseDesciption(enterpriseName);
				cccInfo.setEnterpriseId(enterpriseId);
				
				includeCCCs.add(cccInfo);
			}
			
			cra.setIsConsignment(isConsignment);
			cra.setIncludeCCCs(includeCCCs);
			
			cras.add(cra);
		}
		
		System.err.println("Count CRAS : " + countCras);
		
		return cras;
		
	}

	private static Date getEndDate(long liquidDateTime) {
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(liquidDateTime);
		endDate.add(Calendar.MONTH, 1);
		endDate.add(Calendar.DAY_OF_MONTH, -1);
		return endDate.getTime();
	}

	// --------------------------------------------- Get CRA data
	
	public static Record getDownloadMainCRA(String domainName, String craBatchId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record craBatchRecord = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.eq(Integer.parseInt(craBatchId)))
					.fetchOne();
			
			return craBatchRecord;
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	// --------------------------------------------- Delete CRA
	
	public static void deleteMainCRA(Integer craBatchId, Connection connection) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		dslContext.delete(CRA_BATCH_DETAIL)
			.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(craBatchId))
			.execute();
		
		dslContext.delete(CRA_BATCH)
			.where(CRA_BATCH.ID.eq(craBatchId))
			.execute();
	}
	
	// --------------------------------------------- Set CRA

	public static String setMainCra(Integer domainId, List<String> cccList, ArrayList<Integer> cccIdList, String agrarianAFI, long startDateTime, String craDocumentType, String fileName, Connection connection) {
		
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		java.util.Date startDate = new java.util.Date(startDateTime);
		
		// RECTIFICATIVO
		if (craDocumentType.equals("R")) {
			Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.in(
							dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
								.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.in(cccIdList))
					))
					.and(CRA_BATCH.OUTCOME_FILE_DATE.eq(new Timestamp(startDate.getTime())))
					.fetch();
			
			String resultStr = "";
			
			for(Record craBatchRecord : craBatchRecords) {
				byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
				String dataStr = new String(data);
				
				String subStringAnalize = "";
				
				for(int i=0; i<dataStr.length(); i+=72) {
					
					subStringAnalize = dataStr.substring(i, i + 72);
					
					if(subStringAnalize.contains("DDE")) {
						
						String analizeCCC = subStringAnalize.substring(7, 18);
						
						if(cccList.contains(analizeCCC)) {
							resultStr += subStringAnalize;
							
							for(int j=i+72; i<dataStr.length(); j+=72) {
								
								if(j == dataStr.length()) {
									i = j;
									break;
								}
								
								subStringAnalize = dataStr.substring(j, j + 72);
								
								if(subStringAnalize.contains("DDE"))
									break;
								
								if(subStringAnalize.contains("TRB"))
									resultStr += subStringAnalize;
								
								if(subStringAnalize.contains("CRE")) {
									String subStringAnalize1 = subStringAnalize.substring(0, 17);
									String deleteString = "B";
									String subStringAnalize2 = subStringAnalize.substring(18, 72);
									
									subStringAnalize = subStringAnalize1 + deleteString + subStringAnalize2;
									resultStr += subStringAnalize;
								}
							}
						}
					}
				}
			}
			
			String newETI = agrarianAFI.substring(0, 72);
			String newCRA = newETI + resultStr + agrarianAFI.substring(72, agrarianAFI.length());
			System.out.println("RESULTADO FINAL");
			System.out.println(newCRA);
			
			try {
			
				CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
						.set(CRA_BATCH.DOMAIN, domainId)
						.set(CRA_BATCH.DATE, new Timestamp(formatDate.parse(fileName).getTime()))
						.set(CRA_BATCH.STATUS, (byte)1)
						.set(CRA_BATCH.COMMUNICATION_ID, "R")
						.set(CRA_BATCH.INCOME_FILE, (byte[])null)
						.set(CRA_BATCH.OUTCOME_FILE, newCRA.getBytes())
						.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
						.returning(CRA_BATCH.ID)
						.fetchOne();
					
				Integer craBatchId = craBatchRecord.getId();
				
				for(Integer cccId : cccIdList) {
					dslContext.insertInto(CRA_BATCH_DETAIL)
					.set(CRA_BATCH_DETAIL.DOMAIN, domainId)
					.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
					.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, cccId)
					.execute();
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			
			try { 
				CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
					.set(CRA_BATCH.DOMAIN, domainId)
					.set(CRA_BATCH.DATE, new Timestamp(formatDate.parse(fileName).getTime()))
					.set(CRA_BATCH.STATUS, (byte)1)
					.set(CRA_BATCH.COMMUNICATION_ID, "N")
					.set(CRA_BATCH.INCOME_FILE, (byte[])null)
					.set(CRA_BATCH.OUTCOME_FILE, agrarianAFI.getBytes())
					.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
					.returning(CRA_BATCH.ID)
					.fetchOne();
				
				Integer craBatchId = craBatchRecord.getId();
				
				for(Integer cccId : cccIdList) {
					dslContext.insertInto(CRA_BATCH_DETAIL)
					.set(CRA_BATCH_DETAIL.DOMAIN, domainId)
					.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
					.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, cccId)
					.execute();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return "";
		
	}
	
	// --------------------------------------------- Check Rectificative CRA
	
	public static boolean checkIfRectificative(DSLContext dslContext, Date findingDate, ArrayList<Integer> selectedCCCList) {
		
		Result<Record> craRecords = dslContext.select().from(CRA_BATCH)
			.where(CRA_BATCH.OUTCOME_FILE_DATE.eq(new Timestamp(findingDate.getTime())))
			.and(CRA_BATCH.ID.in(
					dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
						.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.in(selectedCCCList))
						.fetch(CRA_BATCH_DETAIL.CRA_BATCH)
			)).fetch();
		
		return craRecords.isNotEmpty();
	}
	
	// --------------------------------------------- Auxiliar Methods
	
	public static SSRegimeType getSSRegime( int cccType ) {
		Map<CCCType, SSRegimeType> regimes = new HashMap<>();
		regimes.put(CCCType.AGRICULTURAL, SSRegimeType.AGRICULTURAL);
		
		try {
			return regimes.getOrDefault(CCCType.values()[cccType], SSRegimeType.GENERAL);
		} catch ( Exception t){
			return SSRegimeType.GENERAL;
		}
	}
	
	private static java.sql.Date parseDateToSQL(java.util.Date dateJava) {
		if(null == dateJava)
			return null;
		
		DateUtils.resetTime(dateJava);
		return new java.sql.Date(dateJava.getTime());
	}

	private static java.util.Date parseDateToJava(Date dateSQL) {
		if(null == dateSQL)
			return null;
		
		java.util.Date dateJava = new java.util.Date(dateSQL.getTime());
		DateUtils.resetTime(dateJava);
		
		return dateJava;
	}
	
}
