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
	
	public static int createContractTransform(Connection conn, ContractTransform contractTransform) {
		return createContractTransform(DSL.using(conn, getDefaultSettings()), contractTransform);
	}

	private static int createContractTransform(DSLContext dslContext, ContractTransform contractTransform) {
		
		// Dates
		Date newStartDateContract = contractTransform.getContractStartDate();
		Date oldEndDateContract = DateUtils.copyDateOnly(newStartDateContract);
		DateUtils.addDays2Date(oldEndDateContract, -1);
		
		// Close old contract
		
		Integer oldContractId = contractTransform.getContractId();
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.END_DATE, parseDateToSQL(oldEndDateContract))
			.where(CONTRACT.ID.eq(oldContractId))
			.execute();
		
		// Create new contract based on old one
		
		Record oldContractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(oldContractId)).fetchOne();
		
		Date oldStartDate = oldContractRecord.get(CONTRACT.START_DATE);
		
		Integer newContractId = dslContext.insertInto(CONTRACT)
			.set(CONTRACT.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT.PERSON, oldContractRecord.get(CONTRACT.PERSON))
			.set(CONTRACT.WORKPLACE, oldContractRecord.get(CONTRACT.WORKPLACE))
			.set(CONTRACT.ENTERPRISE_CCC, oldContractRecord.get(CONTRACT.ENTERPRISE_CCC))
			.set(CONTRACT.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT.END_DATE, DSL.castNull(CONTRACT.END_DATE))
			.set(CONTRACT.CALENDAR, oldContractRecord.get(CONTRACT.CALENDAR))
			.set(CONTRACT.DESCRIPTION, oldContractRecord.get(CONTRACT.DESCRIPTION))
			.set(CONTRACT.SEPE_STATUS, oldContractRecord.get(CONTRACT.SEPE_STATUS))
			.set(CONTRACT.REGISTRATION, oldContractRecord.get(CONTRACT.REGISTRATION))
			.set(CONTRACT.SENIORITY_DATE, oldContractRecord.get(CONTRACT.SENIORITY_DATE))
			.set(CONTRACT.ENTERPRISE_ACTIVITY, oldContractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY))
			.set(CONTRACT.SS_REGIME, oldContractRecord.get(CONTRACT.SS_REGIME))
			.set(CONTRACT.MODEL, oldContractRecord.get(CONTRACT.MODEL))
			.set(CONTRACT.CATEGORY_DESCRIPTION, oldContractRecord.get(CONTRACT.CATEGORY_DESCRIPTION))
			.set(CONTRACT.SS_STATUS, oldContractRecord.get(CONTRACT.SS_STATUS))
			.set(CONTRACT.AGREEMENT_LEVEL, oldContractRecord.get(CONTRACT.AGREEMENT_LEVEL))
			.returning(CONTRACT.ID)
			.fetchOne().getId();
		
		// Save old contract start date
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_DATA.NAME, "ORIGINAL_START_DATE")
			.set(CONTRACT_DATA.CONTRACT, newContractId)
			.set(CONTRACT_DATA.EXPRESSION, formatDate.format(oldStartDate))
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
			.execute();
		
		// Save old contract end date
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_DATA.NAME, "ORIGINAL_END_DATE")
			.set(CONTRACT_DATA.CONTRACT, newContractId)
			.set(CONTRACT_DATA.EXPRESSION, formatDate.format(oldEndDateContract))
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
			.execute();
		
		// Copy contract data
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_DATA.NAME, "TC2")
			.set(CONTRACT_DATA.CONTRACT, newContractId)
			.set(CONTRACT_DATA.EXPRESSION, "\"" + contractTransform.getTc2() + "\"")
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
			.execute();
		
		String cno = contractTransform.getCno();
		
		if(AonStringUtils.isNotBlank(cno))
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_DATA.NAME, "CNO")
				.set(CONTRACT_DATA.CONTRACT, newContractId)
				.set(CONTRACT_DATA.EXPRESSION, "\"" + cno + "\"")
				.set(CONTRACT_DATA.START_DATE, parseDateToSQL(newStartDateContract))
				.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
				.execute();
		
		Result<Record> contractDataRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(oldContractId))
				.and(CONTRACT_DATA.NAME.ne("TC2"))
				.and(CONTRACT_DATA.NAME.ne("SEPE_ID"))
				.and(CONTRACT_DATA.NAME.ne("COMUNICATION_DATE"))
				.and(CONTRACT_DATA.END_DATE.isNull()
						.or(CONTRACT_DATA.END_DATE.gt(parseDateToSQL(newStartDateContract)))
				).fetch();
		
		for(Record contractDataRecord : contractDataRecords)
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, contractDataRecord.get(CONTRACT_DATA.DOMAIN))
				.set(CONTRACT_DATA.NAME, contractDataRecord.get(CONTRACT_DATA.NAME))
				.set(CONTRACT_DATA.CONTRACT, newContractId)
				.set(CONTRACT_DATA.EXPRESSION, contractDataRecord.get(CONTRACT_DATA.EXPRESSION))
				.set(CONTRACT_DATA.START_DATE, parseDateToSQL(newStartDateContract))
				.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
				.execute();
		
		// Create contract info
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_INFO.CONTRACT, newContractId)
			.set(CONTRACT_INFO.NAME, "SS_ALTA")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT_INFO.END_DATE, DSL.castNull(CONTRACT_INFO.END_DATE))
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.execute();
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
			.set(CONTRACT_INFO.CONTRACT, newContractId)
			.set(CONTRACT_INFO.NAME, "SEPE_CONTRATO")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, parseDateToSQL(newStartDateContract))
			.set(CONTRACT_INFO.END_DATE, DSL.castNull(CONTRACT_INFO.END_DATE))
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.execute();
		
		// Copy Old Sepe Data
		
		Record oldSepeDataRecord = dslContext.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.eq(oldContractId)).and(CONTRACT_ATTACH.TYPE.eq((byte)4)).fetchOne();
		if(null != oldSepeDataRecord)
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, oldContractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_ATTACH.CONTRACT, newContractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
				.set(CONTRACT_ATTACH.DESCRIPTION, "CONTRACT - Contrat@")
				.set(CONTRACT_ATTACH.DATA, oldSepeDataRecord.get(CONTRACT_ATTACH.DATA))
				.set(CONTRACT_ATTACH.TYPE, (byte)4)
				.set(CONTRACT_ATTACH.ATTACH_DATE, oldSepeDataRecord.get(CONTRACT_ATTACH.ATTACH_DATE))
				.execute();
		
		// Enterprise Data
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(newContractId))
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
				.set(CONTRACT_ATTACH.CONTRACT, newContractId)
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
				.set(CONTRACT_ATTACH.DESCRIPTION, "TRANSFORM - Contrat@")
				.set(CONTRACT_ATTACH.DATA, out.toByteArray())
				.set(CONTRACT_ATTACH.TYPE, (byte)19)
				.execute();
			
			// Inset CONTRACT_INFO
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_INFO.CONTRACT, newContractId)
				.set(CONTRACT_INFO.NAME, "SEPE_TRANSFORMACION")
				.set(CONTRACT_INFO.EXPRESSION, "PENDING")
				.set(CONTRACT_INFO.START_DATE, parseDateToSQL(newStartDateContract))
				.set(CONTRACT_INFO.END_DATE, DSL.castNull(CONTRACT_INFO.END_DATE))
				.set(CONTRACT_INFO.CREATION_USER, "admin")
				.execute();
			
		} catch (JAXBException e) {
			System.err.println("No se ha podidod Utils.marshal(prorroga, out)");
		}
		
		return newContractId;
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
