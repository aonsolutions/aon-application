package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.payroll.sepe.contrata.Contrata;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.watson.util.AonStringUtils;

import aon.sepe.objects.Contract;
import aon.sepe.objects.Contract.DetailType;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqContractSEPE {

	// ---------------------------------------------------- Constructor
	
	private JooqContractSEPE() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ---------------------------------------------------- DataBase
	
	public static ContractSpecificData getContractSpecificData(Connection conn, Integer contractId) throws IllegalArgumentException {
		return getContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	public static void setContractSpecificData(Connection conn, Integer domainId, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		setContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), domainId, employeeContractInfo);
	}
	
	public static void setSepeIde(Connection conn, Integer domainId, Integer contractId, String ide) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractIDE(dslContext, domainId, contractId, startDate, endDate, ide);	
	}

	public static void setSepeComunicationDate(Connection conn, Integer domainId, Integer contractId, java.util.Date comunicationDate) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractComunicationDate(dslContext, domainId, contractId, startDate, endDate, comunicationDate);
	}
	
	public static void setSepeTransformIde(Connection conn, Integer domainId, Integer contractId, String ide) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractTransformIDE(dslContext, domainId, contractId, startDate, endDate, ide);	
	}

	public static void setSepeTransformComunicationDate(Connection conn, Integer domainId, Integer contractId, java.util.Date comunicationDate) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractTransformComunicationDate(dslContext, domainId, contractId, startDate, endDate, comunicationDate);
	}
	
	public static void setSepeExtensionIde(Connection conn, Integer domainId, Integer contractId, String ide) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractExtensionIDE(dslContext, domainId, contractId, startDate, endDate, ide, "1");	
	}

	public static void setSepeExtensionComunicationDate(Connection conn, Integer domainId, Integer contractId, java.util.Date comunicationDate) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		updateContractExtensionComunicationDate(dslContext, domainId, contractId, startDate, endDate, comunicationDate, "1");
	}
	
	// ---------------------------------------------------- DataBase (Implementation)
	
	private static ContractSpecificData getContractSpecificDataDB(DSLContext dslContext, Integer contractId) throws IllegalArgumentException {
		ContractSpecificData contractSpecificData = new ContractSpecificData();
		
		getContractCNO(dslContext, contractId, contractSpecificData);
		getContractIDE(dslContext, contractId, contractSpecificData);
		getContractComunicationDate(dslContext, contractId, contractSpecificData);
		getContractTransformIDE(dslContext, contractId, contractSpecificData);
		getContractTransformComunicationDate(dslContext, contractId, contractSpecificData);
		getContractDisc(dslContext, contractId, contractSpecificData);
		getContractTrueDate(dslContext, contractId, contractSpecificData);
		getContractExtensions(dslContext, contractId, contractSpecificData);
		
		Result<Record> contractAttachRecords = dslContext.select().from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
			.and(CONTRACT_ATTACH.MIMETYPE.eq((byte)5))
			.orderBy(CONTRACT_ATTACH.ID.desc())
			.fetch();
		
		if(null == contractAttachRecords || contractAttachRecords.isEmpty()) {
			System.out.println("GETTER - getContractSpecificData empty - id : null");
			return contractSpecificData;	
		} 
		
		Record contractAttachRecord = contractAttachRecords.get(0);
		
		if(contractAttachRecords.size() > 1) {
			dslContext.delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
				.and(CONTRACT_ATTACH.MIMETYPE.eq((byte)5))
				.and(CONTRACT_ATTACH.ID.ne(contractAttachRecord.get(CONTRACT_ATTACH.ID)))
				.execute();
		}

		contractSpecificData.setId(contractAttachRecord.get(CONTRACT_ATTACH.ID));

		try {
			Contrata contrata = new Contrata();
			CONTRATOS contratos = contrata.getCONTRATOS(contractAttachRecord.get(CONTRACT_ATTACH.DATA));
			if(null == contratos || null == contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150() || contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().isEmpty()) return contractSpecificData;
			Object obj = contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
			JooqContrata.completeContratosParams(obj, contractSpecificData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
		
		return contractSpecificData;
	}
	
	private static void getContractCNO(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("CNO"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(cnoRecords.isNotEmpty()) {
			String cno = parseContractData(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
			contractSpecificData.setCno(cno);
		}
	}
	
	private static void getContractIDE(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> ideRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("SEPE_ID"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(ideRecords.isNotEmpty()) {
			contractSpecificData.setIde(ideRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
		}
	}
	
	private static void getContractComunicationDate(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> comunicateDateRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("COMUNICATION_DATE"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(comunicateDateRecords.isNotEmpty()) {
			java.util.Date comunicationDate;
			try {
				comunicationDate = dateFormat.parse(comunicateDateRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
				contractSpecificData.setComunicationDate(comunicationDate);
			} catch (IllegalArgumentException | ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void getContractTransformIDE(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> ideRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("SEPE_TRANSFORM_ID"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(ideRecords.isNotEmpty()) {
			contractSpecificData.setTransformIde(ideRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
		}
	}
	
	private static void getContractTransformComunicationDate(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> comunicateDateRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("COMUNICATION_TRANSFORM_DATE"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(comunicateDateRecords.isNotEmpty()) {
			java.util.Date comunicationDate;
			try {
				comunicationDate = dateFormat.parse(comunicateDateRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
				contractSpecificData.setComunicationTransformDate(comunicationDate);
			} catch (IllegalArgumentException | ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void getContractDisc(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> discRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("DISCONTINUOS"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(discRecords.isNotEmpty()) {
			Boolean discBoolean;
			try {
				discBoolean = Boolean.parseBoolean(discRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
				contractSpecificData.setDisc(discBoolean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Result<Record> discReasonRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("DISCONTINUOS_REASON"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(discReasonRecords.isNotEmpty()) {
			String discReason;
			try {
				discReason = discReasonRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
				contractSpecificData.setDiscReason(discReason);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void getContractTrueDate(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> trueDateRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("TRUE_DATE"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(trueDateRecords.isNotEmpty()) {
			Boolean trueDate;
			try {
				trueDate = Boolean.parseBoolean(trueDateRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
				contractSpecificData.setTrueDate(trueDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static void getContractExtensions(DSLContext dslContext, Integer contractId, ContractSpecificData contractSpecificData) {
		Result<Record> extensionRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.contains("SEPE_EXTENSION_ID").or(CONTRACT_DATA.NAME.contains("COMUNICATION_EXTENSION_DATE")))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(extensionRecords.isNotEmpty()) {
			for(Record record : extensionRecords) {
				String contractDataName = record.get(CONTRACT_DATA.NAME);
				if(AonStringUtils.contains(contractDataName, "SEPE_EXTENSION_ID")) {
					String extensionIdx = contractDataName.length() > 1 ? contractDataName.substring(contractDataName.length() - 1) : contractDataName;
					String extensionIde = record.get(CONTRACT_DATA.EXPRESSION);
					java.util.Date extensionDate = getExtensionDate(extensionRecords, extensionIdx);
					
					contractSpecificData.addExtension(extensionIde, extensionDate);
				}
			}
		}
	}

	private static java.util.Date getExtensionDate(Result<Record> extensionRecords, String extensionIdx) {
		Optional<Record> extensionRecord = extensionRecords.stream().filter(record -> AonStringUtils.equalsIgnoreCase(record.get(CONTRACT_DATA.NAME), "COMUNICATION_EXTENSION_DATE_" + extensionIdx )).findFirst();
		try {
			return extensionRecord.isPresent() ? dateFormat.parse(extensionRecord.get().get(CONTRACT_DATA.EXPRESSION)) : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void setContractSpecificDataDB(DSLContext dslContext, Integer domainId, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
		
			ContractSpecificData contractSpecificData = employeeContractInfo.getContractSpecificData();
			
			Integer contractId = employeeContractInfo.getContractInfo().getContractId();
			Date startDate = parseToSQLDate(employeeContractInfo.getContractInfo().getStartDate());
			Date endDate = parseToSQLDate(employeeContractInfo.getContractInfo().getEndDate());
			
			updateContractCNO(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getCno());
			updateContractIDE(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getIde());
			updateContractComunicationDate(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getComunicationDate());
			updateContractTransformIDE(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getTransformIde());
			updateContractTransformComunicationDate(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getComunicationTransformDate());
			updateContractDisc(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getDisc(), contractSpecificData.getDiscReason());
			updateContractTrueDate(dslContext, domainId, contractId, startDate, endDate, contractSpecificData.getTrueDate());
			
			IContratoType contrato = JooqContrata.createCONTRATOS(employeeContractInfo);
			
			if(null != contrato) {
				CONTRATOS contratos = new CONTRATOS();
				contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contrato);
				
				// ByteArrayOutputStream
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				
//				Utils.marshal(contratos, System.out);
				Utils.marshal(contratos, out);
				
				Integer attachId = employeeContractInfo.getContractSpecificData().getId();
				
				System.out.println("SETTER : setContractSpecificData - id : " + contractSpecificData.getId() + " - cno : " + contractSpecificData.getCno());
				
				if(null == attachId) {
					dslContext.insertInto(CONTRACT_ATTACH)
						.set(CONTRACT_ATTACH.DOMAIN, employeeContractInfo.getEmployeeInfo().getDomain())
						.set(CONTRACT_ATTACH.CONTRACT, employeeContractInfo.getContractInfo().getContractId())
						.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
						.set(CONTRACT_ATTACH.DESCRIPTION, "CONTRACT - Contrat@")
						.set(CONTRACT_ATTACH.DATA, out.toByteArray())
						.set(CONTRACT_ATTACH.TYPE, (byte)4)
						.set(CONTRACT_ATTACH.ATTACH_DATE, new Timestamp(new java.util.Date().getTime()))
						.execute();
				} else {
					dslContext.update(CONTRACT_ATTACH)
						.set(CONTRACT_ATTACH.DATA, out.toByteArray())
						.where(CONTRACT_ATTACH.ID.eq(attachId))
						.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static void updateContractCNO(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, String cno) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("CNO"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(AonStringUtils.isNotBlank(cno)) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "CNO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, "\"" + cno + "\"")
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractIDE(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, String ide) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("SEPE_ID"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(AonStringUtils.isNotBlank(ide)) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "SEPE_ID")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, ide)
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractComunicationDate(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, java.util.Date comunicationDate) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("COMUNICATION_DATE"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(null != comunicationDate) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "COMUNICATION_DATE")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, dateFormat.format(comunicationDate))
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractTransformIDE(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, String ide) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("SEPE_TRANSFORM_ID"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(AonStringUtils.isNotBlank(ide)) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "SEPE_TRANSFORM_ID")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, ide)
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractTransformComunicationDate(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, java.util.Date comunicationDate) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("COMUNICATION_TRANSFORM_DATE"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(null != comunicationDate) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "COMUNICATION_TRANSFORM_DATE")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, dateFormat.format(comunicationDate))
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractExtensionIDE(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, String ide, String extensionIdx) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("SEPE_EXTENSION_ID_" + extensionIdx))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(AonStringUtils.isNotBlank(ide)) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "SEPE_EXTENSION_ID_" + extensionIdx)
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, ide)
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractExtensionComunicationDate(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, java.util.Date comunicationDate, String extensionIdx) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("COMUNICATION_EXTENSION_DATE_" + extensionIdx))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		if(null != comunicationDate) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "COMUNICATION_EXTENSION_DATE_" + extensionIdx)
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, dateFormat.format(comunicationDate))
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}
	
	private static void updateContractDisc(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, Boolean disc, String discReason) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("DISCONTINUOS").or(CONTRACT_DATA.NAME.eq("DISCONTINUOS_REASON")))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
	
		if(null != disc) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "DISCONTINUOS")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, disc ? "true" : "false")
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
			
			if(disc && AonStringUtils.isNotBlank(discReason))
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "DISCONTINUOS_REASON")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, discReason)
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.execute();
		}
	}
	
	private static void updateContractTrueDate(DSLContext dslContext, Integer domainId, Integer contractId, Date startDate,
			Date endDate, Boolean trueDate) {
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("TRUE_DATE"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
	
		if(null != trueDate) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "TRUE_DATE")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, trueDate ? "true" : "false")
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
	}

	public static void setSepeComunications(Connection conn, Integer domainId, Integer contractId, Contract sepeContract) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Record2<Date, Date> contractRecord = dslContext.select(CONTRACT.START_DATE, CONTRACT.END_DATE).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		
		sepeContract.getDetails().forEach(contractDetail -> {
			if(contractDetail.getType().equals(DetailType.CONTRATO)) {
				updateContractIDE(dslContext, domainId, contractId, startDate, endDate, contractDetail.getSepeId().get());
				updateContractComunicationDate(dslContext, domainId, contractId, startDate, endDate, contractDetail.getCommunicationDate());
			} else if(contractDetail.getType().equals(DetailType.TRANSFORMACION)) {
				updateContractTransformIDE(dslContext, domainId, contractId, startDate, endDate, contractDetail.getSepeId().get());
				updateContractTransformComunicationDate(dslContext, domainId, contractId, startDate, endDate, contractDetail.getCommunicationDate());
			} else if(contractDetail.getType().equals(DetailType.PRORROGA)) {
				String extensionIde = contractDetail.getSepeId().get();
				String extensionIdx = extensionIde.length() > 1 ? extensionIde.substring(extensionIde.length() - 1) : extensionIde;
				updateContractExtensionIDE(dslContext, domainId, contractId, startDate, endDate, extensionIde, extensionIdx);
				updateContractExtensionComunicationDate(dslContext, domainId, contractId, startDate, endDate, contractDetail.getCommunicationDate(), extensionIdx);
			}
		});
	}
	
	// ---------------------------------------------------- Auxiliar methods
	
	private static String parseContractData(String value) {
		String parsedValue = null;
		if(AonStringUtils.isNotBlank(value) && value.contains("\""))
			try {
				parsedValue = value.split("\"")[1];
			} catch (IndexOutOfBoundsException e) {
				parsedValue = value;
			}
		else
			parsedValue = value;
		
		return parsedValue;
	}
	
	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date) return null;
		
		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}
	
}
