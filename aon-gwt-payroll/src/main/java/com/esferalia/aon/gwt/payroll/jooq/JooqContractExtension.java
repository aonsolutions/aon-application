package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.sepe.api.contrata.prorrogas.CIFNIFTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSGENERALESPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGAS;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGATIPOTYPE;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqContractExtension {
	
	// --------------------------------------------- Constructor
	
	private JooqContractExtension() {
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
	
	public static void createContractExtension(Connection conn, ContractExtension contractExtension) {
		createContractExtension(DSL.using(conn, getDefaultSettings()), contractExtension);
	}

	private static void createContractExtension(DSLContext dslContext, ContractExtension contractExtension) {
		
		dslContext.transaction(t -> {
			// Create extension file
			
			createExtensionFile(dslContext, contractExtension);
			
			// Create extension data
			
			createExtensionData(dslContext, contractExtension);
		});
		
	}

	private static void createExtensionFile(DSLContext dslContext, ContractExtension contractExtension) {
		
		// Enterprise Data
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractExtension.getContractId()))
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
		
		// PRORROGAS
		PRORROGAS prorroga = createProrroga(enterpriseCIF, completeCCC, contractExtension);
		
		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			Utils.marshal(prorroga, out);
			
			// Insert CONTRACT_ATTACH
			
			dslContext.insertInto(CONTRACT_ATTACH)
				.set(CONTRACT_ATTACH.DOMAIN, contractExtension.getDomainId())
				.set(CONTRACT_ATTACH.CONTRACT, contractExtension.getContractId())
				.set(CONTRACT_ATTACH.MIMETYPE, (byte)5)
				.set(CONTRACT_ATTACH.DESCRIPTION, "EXTENSION - Contrat@")
				.set(CONTRACT_ATTACH.DATA, out.toByteArray())
				.set(CONTRACT_ATTACH.TYPE, (byte)13)
				.execute();
			
			// Inset CONTRACT_INFO
			
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, contractExtension.getDomainId())
				.set(CONTRACT_INFO.CONTRACT, contractExtension.getContractId())
				.set(CONTRACT_INFO.NAME, "SEPE_PRORROGA")
				.set(CONTRACT_INFO.EXPRESSION, "PENDING")
				.set(CONTRACT_INFO.START_DATE, parseDateToSQL(contractExtension.getNewContractStartDate()))
				.set(CONTRACT_INFO.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
				.set(CONTRACT_INFO.CREATION_USER, "admin")
				.execute();
			
		} catch (JAXBException e) {
			System.err.println("No se ha podidod Utils.marshal(prorroga, out)");
		}
	}
	
	private static void createExtensionData(DSLContext dslContext, ContractExtension contractExtension) {
		ContractRecord contractRecord = dslContext.selectFrom(CONTRACT).where(CONTRACT.ID.eq(contractExtension.getContractId())).fetchOne();
		
		Date originalEndDate = DateUtils.copyDateOnly(contractExtension.getNewContractStartDate());
		DateUtils.addDays2Date(originalEndDate, -1);
		
		// Update CONTRACT
		dslContext.update(CONTRACT)
			.set(CONTRACT.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
			.where(CONTRACT.ID.eq(contractExtension.getContractId()))
			.execute();
		
		// Update CONTRACT_INFO
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
			.where(CONTRACT_INFO.CONTRACT.eq(contractExtension.getContractId()))
			.and(CONTRACT_INFO.END_DATE.eq(parseDateToSQL(originalEndDate)))
			.execute();
		
		// Update CONTRACT_DATA
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
			.where(CONTRACT_DATA.CONTRACT.eq(contractExtension.getContractId()))
			.and(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(originalEndDate)))
			.execute();
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_DATA.NAME, "DISCONTINUOS")
			.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
			.set(CONTRACT_DATA.EXPRESSION, Boolean.TRUE.equals(contractExtension.getDiscontinuosInd()) ? "true" : "false")
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(contractExtension.getNewContractStartDate()))
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
			.execute();
		
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_DATA.NAME, "EXTENSION_DATE")
			.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
			.set(CONTRACT_DATA.EXPRESSION, formatDate.format(contractExtension.getNewContractStartDate()))
			.set(CONTRACT_DATA.START_DATE, parseDateToSQL(contractExtension.getNewContractStartDate()))
			.set(CONTRACT_DATA.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
			.execute();
	}
	
	// --------------------------------------------- Methods. createProrroga

	private static PRORROGAS createProrroga(String enterpriseCIF, String completeCCC, ContractExtension contractExtension) {
		PRORROGAS prorroga = new PRORROGAS();
		PRORROGATIPOTYPE prorrogaType = new PRORROGATIPOTYPE();
		
		// Datos empresa
		CIFNIFTYPE cifNifType = new CIFNIFTYPE();
		cifNifType.setCIFNIF(enterpriseCIF);
		DATOSEMPRESATYPE datosEmpresa = new DATOSEMPRESATYPE();
		datosEmpresa.setCIFNIFEMPRESA(cifNifType);
		datosEmpresa.setCCC(completeCCC);
		prorrogaType.setDATOSEMPRESA(datosEmpresa);
		
		// Datos contrato
		DATOSCONTRATOTYPE datosContrato = new DATOSCONTRATOTYPE();
		datosContrato.setFECHAINICIOCTO(formatDate.format(contractExtension.getContractStartDate()));
		if(AonStringUtils.isNotBlank(contractExtension.getSepeId()))
			datosContrato.setCLAVECONTRATO(contractExtension.getSepeId());
		prorrogaType.setDATOSCONTRATO(datosContrato);
			
		// Datos generales
		DATOSGENERALESPRORROGATYPE datosGeneralProrroga = new DATOSGENERALESPRORROGATYPE();
		datosGeneralProrroga.setFECHAINICIO(formatDate.format(contractExtension.getNewContractStartDate()));
		datosGeneralProrroga.setFECHAFIN(formatDate.format(contractExtension.getNewContractEndDate()));
		datosGeneralProrroga.setINDEMPRESAAAPPUNIVERSIDAD("N");
		if(null != contractExtension.getDiscontinuosInd() && Boolean.TRUE.equals(contractExtension.getDiscontinuosInd()))
			datosGeneralProrroga.setINDICADORDISCONTINUIDAD("I");
		if(null != contractExtension.getEnterpriseInd() && Boolean.TRUE.equals(contractExtension.getEnterpriseInd()))
			datosGeneralProrroga.setINDICADORCONVCOL("S");
		
		prorrogaType.setDATOSGENERALESPRORROGA(datosGeneralProrroga);
		
		// Datos uso libre
		if(AonStringUtils.isNotBlank(contractExtension.getFreeEnterprise())) {
			DATOSUSOLIBREEMPRESATYPE datosUsoLibre = new DATOSUSOLIBREEMPRESATYPE();
			datosUsoLibre.setUSOLIBREEMPRESA(contractExtension.getFreeEnterprise());
			prorrogaType.setDATOSUSOLIBREEMPRESA(datosUsoLibre);
		}
		
		// Add prorroga type to prorroga
		prorroga.getPRORROGATIPO().add(prorrogaType);
		
		return prorroga;
	}

	// --------------------------------------------- Methods. deleteContractExtension

	public static void deleteContractExtension(Connection conn, Integer contractId) {
		deleteContractExtension(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	private static void deleteContractExtension(DSLContext dslContext, Integer contractId) {
		
		dslContext.transaction(t -> {
			// Delete extension data
			
			deleteExtensionData(dslContext, contractId);
			
			// Delete extension file
			
			deleteExtensionFile(dslContext, contractId);
		});
		
	}
	
	private static void deleteExtensionData(DSLContext dslContext, Integer contractId) {
		ContractDataRecord extensionDataRecord = dslContext.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("EXTENSION_DATE"))
				.fetchOne();
		
		if(null == extensionDataRecord) throw new IllegalArgumentException("Fecha pr\u00f3rroga no encontrada");
		
		try {
		
			Date extensionStartDate = formatDate.parse(extensionDataRecord.getExpression());
			java.sql.Date extensionEndDate = extensionDataRecord.getEndDate();
			
			Date originalEndDate = DateUtils.copyDateOnly(extensionStartDate);
			DateUtils.addDays2Date(originalEndDate, -1);
			
			// Delete CONTRACT_DATA
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.START_DATE.eq(parseDateToSQL(extensionStartDate)))
				.execute();
			
			// Delete CONTRACT_INFO
			
			dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.START_DATE.eq(parseDateToSQL(extensionStartDate)))
				.execute();
			
			// Update contract end date
			dslContext.update(CONTRACT)
				.set(CONTRACT.END_DATE, parseDateToSQL(originalEndDate))
				.where(CONTRACT.ID.eq(contractId))
				.execute();
			
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.END_DATE, parseDateToSQL(originalEndDate))
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.END_DATE.eq(extensionEndDate))
				.execute();
			
			dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.END_DATE, parseDateToSQL(originalEndDate))
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.END_DATE.eq(extensionEndDate))
				.execute();
			
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private static void deleteExtensionFile(DSLContext dslContext, Integer contractId) {
		dslContext.delete(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)13))
			.and(CONTRACT_ATTACH.DESCRIPTION.eq("EXTENSION - Contrat@"))
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
		
		DateUtils.resetTime(dateJava);
		return new java.sql.Date(dateJava.getTime());
	}

}
