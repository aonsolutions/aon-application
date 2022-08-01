package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
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
			Date contractEndDate = DateUtils.copyDateOnly(contractExtension.getNewContractStartDate());
			DateUtils.addDays2Date(contractEndDate, -1);
			
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, contractExtension.getDomainId())
				.set(CONTRACT_INFO.CONTRACT, contractExtension.getContractId())
				.set(CONTRACT_INFO.NAME, "SEPE_PRORROGA")
				.set(CONTRACT_INFO.EXPRESSION, "PENDING")
				.set(CONTRACT_INFO.START_DATE, parseDateToSQL(contractExtension.getContractStartDate()))
				.set(CONTRACT_INFO.END_DATE, parseDateToSQL(contractEndDate))
				.set(CONTRACT_INFO.CREATION_USER, "admin")
				.execute();
			
			// Update CONTRACT
			dslContext.update(CONTRACT)
				.set(CONTRACT.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
				.where(CONTRACT.ID.eq(contractExtension.getContractId()))
				.execute();
			
			// Update CONTRACT_INFO
			dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
				.where(CONTRACT_INFO.CONTRACT.eq(contractExtension.getContractId()))
				.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
				.and(CONTRACT_INFO.END_DATE.eq(parseDateToSQL(contractEndDate)))
				.execute();
			
			// Update CONTRACT_DATA
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
				.where(CONTRACT_DATA.CONTRACT.eq(contractExtension.getContractId()))
				.and(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(contractEndDate)))
				.execute();
			
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, contractRecord.get(CONTRACT.DOMAIN))
				.set(CONTRACT_DATA.NAME, "DISCONTINUOS")
				.set(CONTRACT_DATA.CONTRACT, contractRecord.get(CONTRACT.ID))
				.set(CONTRACT_DATA.EXPRESSION, contractExtension.getDiscontinuosInd() ? "true" : "false")
				.set(CONTRACT_DATA.START_DATE, contractRecord.get(CONTRACT.START_DATE))
				.set(CONTRACT_DATA.END_DATE, parseDateToSQL(contractExtension.getNewContractEndDate()))
				.execute();
			
		} catch (JAXBException e) {
			System.err.println("No se ha podidod Utils.marshal(prorroga, out)");
		}
	}
	
	// --------------------------------------------- Methods. deleteContractExtension

	public static void deleteContractExtension(Connection conn, Integer contractId) {
		deleteContractExtension(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	private static void deleteContractExtension(DSLContext dslContext, Integer contractId) {
		Result<Record> contractExtensions = dslContext.select().from(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.and(CONTRACT_ATTACH.TYPE.eq((byte)13))
				.orderBy(CONTRACT_ATTACH.ID.desc())
				.fetch();
		
		if(contractExtensions.isNotEmpty()) {
			try {
				byte[] data = contractExtensions.get(0).get(CONTRACT_ATTACH.DATA);
				InputStream in = new ByteArrayInputStream(data); 
				
				Integer deleteRattachId = contractExtensions.get(0).get(CONTRACT_ATTACH.ID);
				
				PRORROGAS prorrogas = Utils.unmarshal(PRORROGAS.class, in);
				
				String startDateStr = prorrogas.getPRORROGATIPO().get(0).getDATOSGENERALESPRORROGA().getFECHAINICIO();
				Date startDate = formatDate.parse(startDateStr);
				
				String endDateStr = prorrogas.getPRORROGATIPO().get(0).getDATOSGENERALESPRORROGA().getFECHAFIN();
				Date endDate = formatDate.parse(endDateStr);
				
				Date newContractEndDate = DateUtils.copyDateOnly(startDate);
				newContractEndDate = DateUtils.addDays2Date(newContractEndDate, -1);
				
				// Update contract end date
				dslContext.update(CONTRACT)
					.set(CONTRACT.END_DATE, parseDateToSQL(newContractEndDate))
					.where(CONTRACT.ID.eq(contractId))
					.execute();
				
				// Update end date
				dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.END_DATE, parseDateToSQL(newContractEndDate))
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.END_DATE.eq(parseDateToSQL(endDate)))
					.execute();
				
				dslContext.update(CONTRACT_INFO)
					.set(CONTRACT_INFO.END_DATE, parseDateToSQL(newContractEndDate))
					.where(CONTRACT_INFO.CONTRACT.eq(contractId))
					.and(CONTRACT_INFO.END_DATE.eq(parseDateToSQL(endDate)))
					.execute();
				
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("DISCONTINUOS"))
					.execute();
				
				// Delete rattach extension
				dslContext.delete(CONTRACT_ATTACH)
					.where(CONTRACT_ATTACH.ID.eq(deleteRattachId))
					.execute();
				
			} catch (JAXBException | ParseException e) {
				e.printStackTrace();
			}
		}
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
