package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.payroll.sepe.contrata.Contrata;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATOS;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqContractSEPE {

	// ---------------------------------------------------- Constructor
	
	private JooqContractSEPE() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ---------------------------------------------------- DataBase
	
	public static ContractSpecificData getContractSpecificData(Connection conn, Integer contractId) {
		return getContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), contractId);
	}
	
	public static void setContractSpecificData(Connection conn, EmployeeContractInfo employeeContractInfo) {
		setContractSpecificDataDB(DSL.using(conn, getDefaultSettings()), employeeContractInfo);
	}
	
	private static ContractSpecificData getContractSpecificDataDB(DSLContext dslContext, Integer contractId) {
		ContractSpecificData contractSpecificData = new ContractSpecificData();
		
		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("CNO"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetch();
		
		if(cnoRecords.isNotEmpty()) {
			String cno = parseContractData(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
			contractSpecificData.setCno(cno);
		}
		
		Result<Record> contractAttachRecords = dslContext.select().from(CONTRACT_ATTACH)
			.where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
			.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
			.and(CONTRACT_ATTACH.MIMETYPE.eq((byte)5))
			.orderBy(CONTRACT_ATTACH.ID.desc())
			.fetch();
		
		Record contractAttachRecord = null;
		
		if(null == contractAttachRecords || contractAttachRecords.isEmpty()) {
			System.out.println("GETTER - getContractSpecificData empty - id : null");
			return contractSpecificData;	
		} else {
			// Clean DB
			contractAttachRecord = contractAttachRecords.get(0);
			
			dslContext.delete(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.eq(contractId))
				.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
				.and(CONTRACT_ATTACH.MIMETYPE.eq((byte)5))
				.and(CONTRACT_ATTACH.ID.ne(contractAttachRecord.get(CONTRACT_ATTACH.ID)))
				.execute();
		}

		
		
		contractSpecificData.setId(contractAttachRecord.get(CONTRACT_ATTACH.ID));

		Contrata contrata = new Contrata();
		CONTRATOS contratos = contrata.getCONTRATOS(contractAttachRecord.get(CONTRACT_ATTACH.DATA));
		if(null == contratos) return contractSpecificData;
		
		try {
			Object obj = contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
			JooqContrata.completeContratosParams(obj, contractSpecificData);
		} catch (JAXBException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		System.out.println("GETTER : getContractSpecificData hasData - id : " + contractSpecificData.getId() + " - cno : " + contractSpecificData.getCno());
		
		return contractSpecificData;
	}
	
	private static void setContractSpecificDataDB(DSLContext dslContext, EmployeeContractInfo employeeContractInfo) {
		ContractSpecificData contractSpecificData = employeeContractInfo.getContractSpecificData();
		
		Integer contractId = employeeContractInfo.getContractInfo().getContractId();
		Integer domainId = employeeContractInfo.getEmployeeInfo().getDomain();
		
		Date startDate = new Date(employeeContractInfo.getContractInfo().getStartDate().getTime());
		Date endDate = null == employeeContractInfo.getContractInfo().getEndDate() ? null : new Date(employeeContractInfo.getContractInfo().getEndDate().getTime());
		
		String cno = contractSpecificData.getCno();
		if(!AonStringUtils.isBlank(cno)) {
			dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.NAME.eq("CNO")).and(CONTRACT_DATA.CONTRACT.eq(contractId)).execute();
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.NAME, "CNO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, "\"" + cno + "\"")
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
		
		IContratoType contrato = JooqContrata.createCONTRATOS(employeeContractInfo);
		CONTRATOS contratos = new CONTRATOS();
		contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().add(contrato);
		
		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
//			Utils.marshal(contratos, System.out);
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
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	// ---------------------------------------------------- Auxiliar methods
	
	private static String parseContractData(String value) {
		if(null == value)
			return null;
		
		return value.split("\"")[1];
	}
}
