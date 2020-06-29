package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Person.PERSON;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.jooq.tables.records.CraBatchRecord;

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
	
	public static List<CRA> getDomainCRAs(String domainName, Connection conn) {
		return getDomainCRAsDB(domainName, DSL.using(conn, getDefaultSettings()));
	}
	
	private static List<CRA> getDomainCRAsDB(String domainName, DSLContext dslContext) {
		List<CRA> cras = new ArrayList<CRA>();
		
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne(DOMAIN.ID);
		
		Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
				.where(CRA_BATCH.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record craBatch : craBatchRecords) {
			CRA cra = new CRA();
			
			cra.setCode(craBatch.get(CRA_BATCH.ID));
			cra.setStatus(craBatch.get(CRA_BATCH.STATUS));
			cra.setDate(craBatch.get(CRA_BATCH.DATE));
			cra.setCreationDate(craBatch.get(CRA_BATCH.OUTCOME_FILE_DATE));
			cra.setType(craBatch.get(CRA_BATCH.COMMUNICATION_ID));
			
			 Result<Record> craBatchDetailRecord = dslContext.select().from(CRA_BATCH_DETAIL)
					.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(craBatch.get(CRA_BATCH.ID)))
					.fetch();
			
			if(null != craBatchDetailRecord && !craBatchDetailRecord.isEmpty()){
			
				Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(craBatchDetailRecord.get(0).get(CRA_BATCH_DETAIL.ENTERPRISE_CCC)))
						.fetchOne();
				
				cra.setCcc(getCCCType(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)) + enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC));
				cra.setCccType(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
				
				String geozone = dslContext.select(GEOZONE.NAME).from(GEOZONE)
						.where(GEOZONE.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE)))
						.fetchOne(GEOZONE.NAME);
				
				cra.setCccProvince(geozone);
				
				Record enterpriseActivityRecord = dslContext.select().from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
						.fetchOne();
				
				cra.setActivityName(enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
				
				cras.add(cra);
				
			}
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

	public static String setMainCra(String domainName, String _cccId, String agrarianAFI, long _startDate, String craDocumentType, Connection connection) {
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
								.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(Integer.parseInt(_cccId)))
					)).and(CRA_BATCH.DOMAIN.eq(_domainId))
					.and(CRA_BATCH.OUTCOME_FILE_DATE.eq(new Timestamp(startDate.getTime())))
					.fetch();
			
			byte[] data = null;
			ArrayList<Integer> oldCraBatchIds = new ArrayList<Integer>();
			for(Record r: craBatchRecords) {
				if(r.get(CRA_BATCH.COMMUNICATION_ID).equals("N")) {
					data = r.get(CRA_BATCH.OUTCOME_FILE);
					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
				}else
					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
			}
			
			String dataStr = new String(data);
			System.out.println();
			System.out.println("Lenght DataStr : " + dataStr.length());
			System.out.println(dataStr);
			System.out.println();
			
			String resultStr = "";
			String subStringAnalize = "";
			for(int i=0; i<dataStr.length(); i+=72) {
				subStringAnalize = dataStr.substring(i, i + 72);
				if(subStringAnalize.contains("CRE")) {
					String subStringAnalize1 = subStringAnalize.substring(0, 17);
					String deleteString = "B";
					String subStringAnalize2 = subStringAnalize.substring(18, 72);
					
					subStringAnalize = subStringAnalize1 + deleteString + subStringAnalize2;
				}
				resultStr += subStringAnalize;
			}
			System.out.println("Lenght ResultStr : " + resultStr.length());
			System.out.println(resultStr);
			String newETI = agrarianAFI.substring(0, 72);
			resultStr += parseCRAToRectificative(agrarianAFI);
			String newCRA = newETI + resultStr.substring(72, resultStr.length());
			System.out.println("RESULTADO FINAL");
			System.out.println(newCRA);
			
			Date currentDate = new Date();
			
			CraBatchRecord rectificativeCRABatchRecord = dslContext.insertInto(CRA_BATCH)
					.set(CRA_BATCH.DOMAIN, _domainId)
					.set(CRA_BATCH.DATE, new Timestamp(currentDate.getTime()))
					.set(CRA_BATCH.STATUS, (byte)1)
					.set(CRA_BATCH.COMMUNICATION_ID, craDocumentType)
					.set(CRA_BATCH.INCOME_FILE, (byte[])null)
					.set(CRA_BATCH.OUTCOME_FILE, newCRA.getBytes())
					.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
					.returning(CRA_BATCH.ID)
					.fetchOne();
				
			Integer craBatchId = rectificativeCRABatchRecord.getId();
			
			dslContext.insertInto(CRA_BATCH_DETAIL)
				.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
				.execute();
			
			// DELETE OLD CRA
			dslContext.delete(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.CRA_BATCH.in(oldCraBatchIds)).execute();
			dslContext.delete(CRA_BATCH).where(CRA_BATCH.ID.in(oldCraBatchIds)).execute();
		}
		
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
		
		dslContext.insertInto(CRA_BATCH_DETAIL)
			.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
			.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
			.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
			.execute();
		
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

	
	
}
