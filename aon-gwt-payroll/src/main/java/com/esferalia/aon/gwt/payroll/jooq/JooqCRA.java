package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
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
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static String checkCreateNewCRA(Connection conn, long findingDate, ArrayList<Integer> cccList) {
		return checkCreateNewCRA(findingDate, cccList, DSL.using(conn, getDefaultSettings()));
	}

	private static String checkCreateNewCRA(long findingDate, ArrayList<Integer> cccList, DSLContext dslContext) {
		Date startDate = new Date(findingDate);
		DateUtils.resetTime(startDate);
		
		Date endDate = DateUtils.getLastDayOfMonth(startDate);
		DateUtils.resetTime(endDate);
		
		java.sql.Date startDateSQL = new java.sql.Date(startDate.getTime());
		java.sql.Date endDateSQL = new java.sql.Date(endDate.getTime());
		
		Result<Record> contractsActiveRecords = dslContext.select().from(CONTRACT)
			.where(
					CONTRACT.END_DATE.isNull()
					.or(CONTRACT.END_DATE.ge(endDateSQL)))
			.and(CONTRACT.ENTERPRISE_CCC.in(cccList))
			.and(CONTRACT.ID.ge(0))
			.fetch();
		
		for(Record contractRecord : contractsActiveRecords) {
			Integer contractId = contractRecord.get(CONTRACT.ID);
			Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.START_DATE.ge(startDateSQL))
				.and(SALARY.END_DATE.le(endDateSQL))
				.fetch();
			
			if(salariesRecords.isEmpty()) {
				Integer personId = contractRecord.get(CONTRACT.PERSON);
				Record personRecord = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(personId)).fetchOne();
				return personRecord.get(PERSON.NAME) + " " + personRecord.get(PERSON.FIRST_SURNAME) 
				+ " no tiene n" + String.valueOf("\u00F3") + "mina emitida para este perido. " + String.valueOf("\u00BF") + "Desea continuar?";
			}
				 
		}
		
		return "";
	}

	// ********************************************************************************************************************************************
	//													GET DOMAIN CRAs FOR LIST
	// ********************************************************************************************************************************************
	
	public static List<CRA> getDomainCRAs(Integer domainId, Integer parentDomainId, Integer userId, Connection conn) {
		return getDomainCRAsDB(domainId, parentDomainId, userId, DSL.using(conn, getDefaultSettings()));
	}
	
	private static List<CRA> getDomainCRAsDB(Integer domainId, Integer parentDomainId, Integer userId, DSLContext dslContext) {
		List<CRA> cras = new ArrayList<CRA>();
		
		Result<Record> childDomainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.PARENT.eq(domainId))
				.and(DOMAIN.SCOPE.in(
					dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
						.where(USER_SCOPE.USER_ID.eq(userId))
						.fetch(USER_SCOPE.SCOPE)
				)).fetch();
		
		List<Integer> domainChilds = new ArrayList<Integer>();
		
		for(Record childDomainRecord : childDomainRecords)
			domainChilds.add(childDomainRecord.get(DOMAIN.ID));
		
		// Domain enterprise CCCs
		List<Integer> ownCCCs = new ArrayList<Integer>();
		Result<Record> entepriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.DOMAIN.eq(domainId)
					.or(ENTERPRISE_CCC.DOMAIN.in(domainChilds)))
				.fetch();
		
		for(Record enterpriseCCCRecord : entepriseCCCRecords) {
			ownCCCs.add(enterpriseCCCRecord.get(ENTERPRISE_CCC.ID));
		}
		
		// Get CRAs
		Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
				.where(CRA_BATCH.DOMAIN.eq(domainId)
						.or(CRA_BATCH.DOMAIN.eq(parentDomainId))
						.or(CRA_BATCH.DOMAIN.in(domainChilds)))
				.fetch();
		
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
			
			List<CCCInfo> includeCCCs = new ArrayList<CCCInfo>();
			
			for(Record craBatchDetailRecord : craBatchDetailRecords) {
				Integer enterpriseCCCId = craBatchDetailRecord.get(CRA_BATCH_DETAIL.ENTERPRISE_CCC);
				
				Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
						.fetchOne();
				
				Integer cccId = enterpriseCCCRecord.get(ENTERPRISE_CCC.ID);
				String cccCode = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
				String regime = getSSRegime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)).getCode();
				Byte type = enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE);
				
				String completeCCCAccount = regime + cccCode;
				
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
		
		return cras;
		
	}

	private static String getCCCType(Byte cccType) {
		switch (cccType) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		default:
			return "0111";
		}
	}
	
	private static String getCCCTypeName(Byte cccType) {
		switch (cccType) {
		case 0:
			return "Principal";
		case 1:
			return "Formacion y aprendizaje";
		case 2:
			return "Aprendizaje";
		case 3:
			return "Representantes de comercio";
		case 4:
			return "Asimilados R.General";
		case 5:
			return "Becarios";
		case 6:
			return "Emploead@s de hogar";
		case 7:
			return "Trabajadores cuenta ajena agrarios";
		default:
			return "Principal";
		}
	}

	// ********************************************************************************************************************************************
	//													GENERATE JSON CRA
	// ********************************************************************************************************************************************
	
	public static byte[] getDownloadMainCRA(String domainName, String _craBatchId) {
		Connection connection = null;
		
		try {
			connection = AonServletUtils.getConnection(domainName);
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record craBatchRecord = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.eq(Integer.parseInt(_craBatchId)))
					.fetchOne();
			
			byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
			return data;
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	public static String deleteMainCRA(Integer _craBatchId, Connection connection) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		dslContext.delete(CRA_BATCH_DETAIL)
			.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(_craBatchId))
			.execute();
		
		dslContext.delete(CRA_BATCH)
			.where(CRA_BATCH.ID.eq(_craBatchId))
			.execute();
		
		return null;
	}

	public static String setMainCra(String domainName, String _cccId, List<String> cccList, ArrayList<Integer> cccIdList, String agrarianAFI, long _startDate, String craDocumentType, Connection connection) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		java.util.Date startDate = new java.util.Date(_startDate);
		
		//GET AuthKey from DB
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne();
		
		Integer _domainId = domainRecord.get(DOMAIN.ID);
		
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
			
			Date currentDate = new Date();
			
			CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
					.set(CRA_BATCH.DOMAIN, _domainId)
					.set(CRA_BATCH.DATE, new Timestamp(currentDate.getTime()))
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
				.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, cccId)
				.execute();
			}
		} else {
			Date currentDate = new Date();
			
			CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
				.set(CRA_BATCH.DOMAIN, _domainId)
				.set(CRA_BATCH.DATE, new Timestamp(currentDate.getTime()))
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
				.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, cccId)
				.execute();
			}
		}
		
		// RECTIFICATIVO
//		if (craDocumentType.equals("R")) {
//			Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
//					.where(CRA_BATCH.ID.in(
//							dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
//								.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(Integer.parseInt(_cccId)))
//					)).and(CRA_BATCH.DOMAIN.eq(_domainId))
//					.and(CRA_BATCH.OUTCOME_FILE_DATE.eq(new Timestamp(startDate.getTime())))
//					.fetch();
//			
//			byte[] data = null;
//			ArrayList<Integer> oldCraBatchIds = new ArrayList<Integer>();
//			for(Record r: craBatchRecords) {
//				if(r.get(CRA_BATCH.COMMUNICATION_ID).equals("N")) {
//					data = r.get(CRA_BATCH.OUTCOME_FILE);
//					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
//				}else
//					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
//			}
//			
//			String dataStr = new String(data);
//			System.out.println();
//			System.out.println("Lenght DataStr : " + dataStr.length());
//			System.out.println(dataStr);
//			System.out.println();
//			
//			String resultStr = "";
//			String subStringAnalize = "";
//			for(int i=0; i<dataStr.length(); i+=72) {
//				subStringAnalize = dataStr.substring(i, i + 72);
//				if(subStringAnalize.contains("CRE")) {
//					String subStringAnalize1 = subStringAnalize.substring(0, 17);
//					String deleteString = "B";
//					String subStringAnalize2 = subStringAnalize.substring(18, 72);
//					
//					subStringAnalize = subStringAnalize1 + deleteString + subStringAnalize2;
//				}
//				resultStr += subStringAnalize;
//			}
//			System.out.println("Lenght ResultStr : " + resultStr.length());
//			System.out.println(resultStr);
//			String newETI = agrarianAFI.substring(0, 72);
//			resultStr += parseCRAToRectificative(agrarianAFI);
//			String newCRA = newETI + resultStr.substring(72, resultStr.length());
//			System.out.println("RESULTADO FINAL");
//			System.out.println(newCRA);
//			
//			Date currentDate = new Date();
//			
//			CraBatchRecord rectificativeCRABatchRecord = dslContext.insertInto(CRA_BATCH)
//					.set(CRA_BATCH.DOMAIN, _domainId)
//					.set(CRA_BATCH.DATE, new Timestamp(currentDate.getTime()))
//					.set(CRA_BATCH.STATUS, (byte)1)
//					.set(CRA_BATCH.COMMUNICATION_ID, craDocumentType)
//					.set(CRA_BATCH.INCOME_FILE, (byte[])null)
//					.set(CRA_BATCH.OUTCOME_FILE, newCRA.getBytes())
//					.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
//					.returning(CRA_BATCH.ID)
//					.fetchOne();
//				
//			Integer craBatchId = rectificativeCRABatchRecord.getId();
//			
//			dslContext.insertInto(CRA_BATCH_DETAIL)
//				.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
//				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
//				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
//				.execute();
//			
//			// DELETE OLD CRA
//			dslContext.delete(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.CRA_BATCH.in(oldCraBatchIds)).execute();
//			dslContext.delete(CRA_BATCH).where(CRA_BATCH.ID.in(oldCraBatchIds)).execute();
//		}
		
		return null;
		
	}

	private static String parseCRAToRectificative(String cra) {
		String resultStr = "";
		String subStringAnalize = "";
		for(int i=0; i<cra.length(); i+=72) {
			subStringAnalize = cra.substring(i, i + 72);
			if(subStringAnalize.contains("ETI"))
				continue;
			else
				resultStr += subStringAnalize;
		}
		return resultStr;
	}
	
	public static SSRegimeType getSSRegime( int cccType ) {
		Map<CCCType, SSRegimeType> regimes = new HashMap<CCCType, SSRegimeType>(){
			{
				put(CCCType.AGRICULTURAL, SSRegimeType.AGRICULTURAL);
			}
		};
		
		try {
			return regimes.getOrDefault(CCCType.values()[cccType], SSRegimeType.GENERAL);
		} catch ( Throwable t){
			return SSRegimeType.GENERAL;
		}
	}	
	
}
