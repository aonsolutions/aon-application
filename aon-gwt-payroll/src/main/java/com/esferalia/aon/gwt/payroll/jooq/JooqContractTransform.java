package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.transformaciones.CIFNIFTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSGENERALESTRANSFORMACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION109TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION139TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION189TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION209TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION239TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION289TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION309TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION339TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACION389TYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.TRANSFORMACIONES;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqContractTransform {
	
	// --------------------------------------------- Constructor
	
	private JooqContractTransform() {
		super();
	}
	
	// --------------------------------------------- Variables
	
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
	
	// --------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// --------------------------------------------- Methods. createContractExtension
	
	public static void createContractTransform(Connection conn, ContractTransform contractTransform) {
		createContractTransform(DSL.using(conn, getDefaultSettings()), contractTransform);
	}

	private static void createContractTransform(DSLContext dslContext, ContractTransform contractTransform) {
		// Dates
		Date transformStartDate = contractTransform.getContractStartDate();
		Date oldEndDateContract = DateUtils.copyDateOnly(transformStartDate);
		DateUtils.addDays2Date(oldEndDateContract, -1);
		
		dslContext.transaction(t -> {
			// Close old contract
			
			Integer contractId = contractTransform.getContractId();
			
			// Copy and close contractData & contractInfo
			
			copyDataInfo(dslContext, contractId, transformStartDate, contractTransform);
			
			// Add contractData transform
			
			addDataTransform(dslContext, contractId, transformStartDate);
			
			// Create Transform Contrat@ file
			
			createTransformFile(dslContext, contractId, transformStartDate, contractTransform);
		});
		
	}

	private static void copyDataInfo(DSLContext dslContext, Integer contractId, Date transformStartDate, ContractTransform contractTransform) {
		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Date endDate = contractRecord.getEndDate();
		
		Date endDateContract = DateUtils.copyDateOnly(transformStartDate);
		DateUtils.addDays2Date(endDateContract, -1);
		
		Result<Record> contractDatas = dslContext.selectDistinct().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.ne("TC2"))
				.and(CONTRACT_DATA.NAME.ne("SEPE_ID"))
				.and(CONTRACT_DATA.NAME.ne("COMUNICATION_DATE"))
				.and(CONTRACT_DATA.NAME.ne("CNO"))
				.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(endDate))))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDateContract))
			.where(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(endDate))))
			.and(CONTRACT_DATA.CONTRACT.eq(contractId))
			.execute();
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE, parseDateToSQL(endDateContract))
			.where(CONTRACT_INFO.END_DATE.isNull().or(CONTRACT_INFO.END_DATE.eq(parseDateToSQL(endDate))))
			.and(CONTRACT_INFO.CONTRACT.eq(contractId))
			.execute();
		
		contractDatas.forEach(contractData ->
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, contractData.get(CONTRACT_DATA.DOMAIN))
				.set(CONTRACT_DATA.NAME, contractData.get(CONTRACT_DATA.NAME))
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, contractData.get(CONTRACT_DATA.EXPRESSION))
				.set(CONTRACT_DATA.START_DATE, parseDateToSQL(transformStartDate))
				.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDate))
				.execute()
		);
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_DATA.NAME, "TC2")
			.set(CONTRACT_DATA.CONTRACT, contractId)
			.set(CONTRACT_DATA.EXPRESSION, "\"" + contractTransform.getTc2() + "\"")
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(transformStartDate))
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDate))
			.execute();
	
		if(AonStringUtils.isNotBlank(contractTransform.getCno()))
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_DATA.NAME, "CNO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, "\"" + contractTransform.getCno() + "\"")
				.set(CONTRACT_DATA.START_DATE, parseDateToSQL(transformStartDate))
				.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDate))
				.execute();
		
	}

	private static void addDataTransform(DSLContext dslContext, Integer contractId, Date transformStartDate) {
		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Date endDate = contractRecord.getEndDate();
		Integer domainId = contractRecord.getDomain();
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, domainId)
			.set(CONTRACT_DATA.NAME, "TRANSFORM_DATE")
			.set(CONTRACT_DATA.CONTRACT, contractId)
			.set(CONTRACT_DATA.EXPRESSION, formatDate.format(transformStartDate))
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(transformStartDate))
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDate))
			.execute();
	}

	private static void createTransformFile(DSLContext dslContext, Integer contractId, Date transformStartDate, ContractTransform contractTransform) {
		// Enterprise Data
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
				.fetchOne();
		
		String regime = parseRegime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
		String ccc = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
		String completeCCC = regime + ccc;
		
		Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(
					dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
						.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
			)).fetchOne();
		
		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
		
		String municipalityCodeCT = dslContext.select(RADDRESS.MUNICIPALITY_CODE).from(RADDRESS)
			.where(RADDRESS.ID.eq(
					dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE)
						.where(WORKPLACE.ID.eq(contractRecord.get(CONTRACT.WORKPLACE)))
						.fetchOne(WORKPLACE.ADDRESS)
			)).fetchOne(RADDRESS.MUNICIPALITY_CODE);
		
		// TRANSFORMACION
		TRANSFORMACIONES transformaciones = createTransform(enterpriseCIF, completeCCC, municipalityCodeCT, contractTransform.getCno(), contractTransform);
		
		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Utils.marshal(transformaciones, out);
			
			// Insert CONTRACT_ATTACH
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_ATTACH.CONTRACT, contractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
				.set(CONTRACT_ATTACH.DESCRIPTION, "TRANSFORM - Contrat@")
				.set(CONTRACT_ATTACH.DATA, out.toByteArray())
				.set(CONTRACT_ATTACH.TYPE, (byte)19)
				.execute();
			
			// Inset CONTRACT_INFO
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_INFO.CONTRACT, contractId)
				.set(CONTRACT_INFO.NAME, "SEPE_TRANSFORMACION")
				.set(CONTRACT_INFO.EXPRESSION, "PENDING")
				.set(CONTRACT_INFO.START_DATE, parseDateToSQL(transformStartDate))
				.set(CONTRACT_INFO.END_DATE, DSL.castNull(CONTRACT_INFO.END_DATE))
				.set(CONTRACT_INFO.CREATION_USER, "admin")
				.execute();
			
		} catch (JAXBException e) {
			System.err.println("No se ha podidod Utils.marshal(prorroga, out)");
		}
	}

	// --------------------------------------------- Methods. createProrroga

	private static TRANSFORMACIONES createTransform(String enterpriseCIF, String completeCCC, String municipalityCodeCT, String cno, ContractTransform contractTransform) {
		TRANSFORMACIONES transformacion = new TRANSFORMACIONES();
		ITransformacionType contractTransformType = getContractTransformType(contractTransform.getTc2());
		
		if(null == contractTransformType)
			return transformacion;
		
		// Datos empresa
		CIFNIFTYPE cifNifType = new CIFNIFTYPE();
		cifNifType.setCIFNIF(enterpriseCIF);
		DATOSEMPRESATYPE datosEmpresa = new DATOSEMPRESATYPE();
		datosEmpresa.setCIFNIFEMPRESA(cifNifType);
		datosEmpresa.setCODIGOCUENTACOTIZACION(completeCCC);
		contractTransformType.setDATOSEMPRESA(datosEmpresa);
		
		// Datos contrato
		DATOSCONTRATOTYPE datosContrato = new DATOSCONTRATOTYPE();
		datosContrato.setFECHAINICIOCTO(formatDate.format(contractTransform.getContractStartDate()));
		if(AonStringUtils.isNotBlank(contractTransform.getSepeId()))
			datosContrato.setCLAVECONTRATO(contractTransform.getSepeId());
		contractTransformType.setDATOSCONTRATO(datosContrato);
			
		// Datos generales
		DATOSGENERALESTRANSFORMACIONTYPE datosGeneralTransformacion = new DATOSGENERALESTRANSFORMACIONTYPE();
		datosGeneralTransformacion.setFECHAINICIO(formatDate.format(contractTransform.getContractStartDate()));
		datosGeneralTransformacion.setCODIGOOCUPACION(AonStringUtils.rightPad(cno, 8, ' '));
		datosGeneralTransformacion.setMUNICIPIOCT(municipalityCodeCT);
		datosGeneralTransformacion.setNACIONALIDADCT("724");
		if(null != contractTransform.getDiscontinuosInd() && Boolean.TRUE.equals(contractTransform.getDiscontinuosInd()))
			datosGeneralTransformacion.setINDICADORDISCONTINUIDAD("I");
		
		contractTransformType.setDATOSGENERALESTRANSFORMACION(datosGeneralTransformacion);
		
		transformacion.getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().add(contractTransformType);
		
		return transformacion;
	}
	
	private static ITransformacionType getContractTransformType(String tc2) {
		switch (tc2) {
		case "109":
			return new TRANSFORMACION109TYPE();
		case "139":
			return new TRANSFORMACION139TYPE();
		case "189":
			return new TRANSFORMACION189TYPE();
		case "209":
			return new TRANSFORMACION209TYPE();
		case "239":
			return new TRANSFORMACION239TYPE();
		case "289":
			return new TRANSFORMACION289TYPE();
		case "309":
			return new TRANSFORMACION309TYPE();
		case "339":
			return new TRANSFORMACION339TYPE();
		case "389":
			return new TRANSFORMACION389TYPE();
		default:
			return null;
		}
	}
	
	// --------------------------------------------- Methods. createContractExtension
	
	public static void removeContractTransform(Connection conn, Integer contractId) {
		removeContractTransform(DSL.using(conn, getDefaultSettings()), contractId);
	}

	private static void removeContractTransform(DSLContext dslContext, Integer contractId) {
		ContractDataRecord transformDataRecord = dslContext.selectFrom(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq("TRANSFORM_DATE"))
			.fetchOne();
		
		if(null == transformDataRecord) throw new IllegalArgumentException("Fecha transformaci\u00f3n no encontrada");
		
		try {
			
			Date transformDate = formatDate.parse(transformDataRecord.getExpression());
			
			// Delete transform file
			
			deleteTransformFile(dslContext, contractId, transformDate);
			
			// Delete data transform
			
			deleteDataTransform(dslContext, contractId, transformDate);
			
			deleteTransformDataInfo(dslContext, contractId, transformDate);
			
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static void deleteTransformFile(DSLContext dslContext, Integer contractId, Date transformDate) {
		
		// Delete CONTRACT_ATTACH
		
		dslContext.delete(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)19))
			.and(CONTRACT_ATTACH.DESCRIPTION.eq("TRANSFORM - Contrat@"))
			.and(CONTRACT_ATTACH.MIMETYPE.eq((byte)5))
			.execute();
					
		// Delete CONTRACT_INFO
		
		dslContext.delete(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contractId))
			.and(CONTRACT_INFO.NAME.eq("SEPE_TRANSFORMACION"))
			.and(CONTRACT_INFO.START_DATE.eq(parseDateToSQL(transformDate)))
			.execute();
		
	}
	
	private static void deleteDataTransform(DSLContext dslContext, Integer contractId, Date transformDate) {
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.eq("TRANSFORM_DATE"))
			.and(CONTRACT_DATA.START_DATE.eq(parseDateToSQL(transformDate)))
			.execute();
	}
	
	private static void deleteTransformDataInfo(DSLContext dslContext, Integer contractId, Date transformDate) {
		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Date endDate = contractRecord.getEndDate();
		
		Date endDateContract = DateUtils.copyDateOnly(transformDate);
		DateUtils.addDays2Date(endDateContract, -1);
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.START_DATE.eq(parseDateToSQL(transformDate)))
			.execute();
		
		dslContext.delete(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contractId))
			.and(CONTRACT_INFO.START_DATE.eq(parseDateToSQL(transformDate)))
			.execute();
		
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(endDate))
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(endDateContract)))
			.execute();	
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE, parseDateToSQL(endDate))
			.where(CONTRACT_INFO.CONTRACT.eq(contractId))
			.and(CONTRACT_INFO.END_DATE.eq(parseDateToSQL(endDateContract)))
			.execute();	
	}

	// --------------------------------------------- Auxiliar Methods
	
	private static String parseRegime(Byte regime) {
		switch (regime) {
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
			case 8:
				return "0112";
			default:
				return "0111";
		}
	}
	
	private static java.sql.Date parseDateToSQL(java.util.Date dateJava) {
		if(null == dateJava)
			return null;
		
//		DateUtils.resetTime(dateJava);
		return new java.sql.Date(dateJava.getTime());
	}

}
