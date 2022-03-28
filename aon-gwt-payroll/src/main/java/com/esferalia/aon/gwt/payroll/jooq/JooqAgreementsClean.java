package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.AgreementsCleanDialog.AgreementCleanType;
import com.esferalia.aon.gwt.payroll.shared.AgreementCleanContract;
import com.esferalia.aon.gwt.payroll.shared.AgreementsClean;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqAgreementsClean {

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	public static List<AgreementsClean> getAgreementsClean(Connection conn, Integer domainId, AgreementCleanType cleanType) {
		return getAgreementsClean(DSL.using(conn, getDefaultSettings()), domainId, cleanType);
	}

	private static List<AgreementsClean> getAgreementsClean(DSLContext dslContext, Integer domainId, AgreementCleanType cleanType) {
		switch (cleanType) {
			case DELETED:
				return getAgreementsDeletedClean(dslContext, domainId);
			case UNUSED:
				return getAgreementsUnusedClean(dslContext, domainId);
			default:
				return Collections.emptyList();
		}
	}

	private static List<AgreementsClean> getAgreementsDeletedClean(DSLContext dslContext, Integer domainId) {
		List<AgreementsClean> agreementsClean = new ArrayList<>();
		
		Result<Record> trashAgreements = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.lt(0))
				.and(AGREEMENT.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record agreementRecord : trashAgreements) {
			Integer agreementId = agreementRecord.get(AGREEMENT.ID);
			String description = agreementRecord.get(AGREEMENT.DESCRIPTION);
			String ssNumber = agreementRecord.get(AGREEMENT.SS_NUMBER);
			
			AgreementsClean agreementClean = new AgreementsClean(agreementId, description, ssNumber);
			
			getAgreementContracts(dslContext, agreementId, agreementClean);
			
			agreementsClean.add(agreementClean);
		}
		
		System.out.println("agreementsClean DELETED size : " + agreementsClean.size());
		
		return agreementsClean;
	}

	private static List<AgreementsClean> getAgreementsUnusedClean(DSLContext dslContext, Integer domainId) {
		List<AgreementsClean> agreementsClean = new ArrayList<>();
		
		Result<Record> trashAgreements = dslContext.select().from(AGREEMENT)
				.where(AGREEMENT.ID.ge(0))
				.and(AGREEMENT.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record agreementRecord : trashAgreements) {
			Integer agreementId = agreementRecord.get(AGREEMENT.ID);
			
			if(notHasContracts(dslContext, domainId, agreementId)) {
				String description = agreementRecord.get(AGREEMENT.DESCRIPTION);
				String ssNumber = agreementRecord.get(AGREEMENT.SS_NUMBER);
				
				AgreementsClean agreementClean = new AgreementsClean(agreementId, description, ssNumber);
				
				agreementsClean.add(agreementClean);
			}
		}
		
		System.out.println("agreementsClean UNUSED size : " + agreementsClean.size());
		
		return agreementsClean;
	}
	
	private static boolean notHasContracts(DSLContext dslContext, Integer domainId, Integer agreementId) {
		List<Integer> domainChildIds = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.PARENT.eq(domainId))
				.fetch(DOMAIN.ID);
		
		Result<Record> agreementContracts = dslContext.select().from(CONTRACT)
			.where(CONTRACT.AGREEMENT_LEVEL.in(
					dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
						.fetch(AGREEMENT_LEVEL.ID)
			)).and(CONTRACT.DOMAIN.eq(domainId).or(CONTRACT.DOMAIN.in(domainChildIds)))
			.limit(1)
			.fetch();
		
		return agreementContracts.isEmpty();
	}

	private static void getAgreementContracts(DSLContext dslContext, Integer agreementId, AgreementsClean agreementClean) {
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.innerJoin(WORKPLACE)
				.on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.innerJoin(ENTERPRISE_CCC)
				.on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
				.innerJoin(ENTERPRISE_ACTIVITY)
				.on(ENTERPRISE_ACTIVITY.ID.eq(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(ENTERPRISE_ACTIVITY.ENTERPRISE))
				.innerJoin(PERSON)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.where(CONTRACT.ID.in(
				
						dslContext.select(CONTRACT.ID).from(CONTRACT)
						.where(CONTRACT.AGREEMENT_LEVEL.in(
								dslContext.select(AGREEMENT_LEVEL.ID).from(AGREEMENT_LEVEL)
									.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
									.fetch(AGREEMENT_LEVEL.ID)
						)).fetch(CONTRACT.ID)
						
				)).orderBy(ENTERPRISE_ACTIVITY.ENTERPRISE)
				.fetch();
		
		if(contractRecords.isNotEmpty()) {
			List<AgreementCleanContract> contracts = new ArrayList<>();
			
			for(Record contract : contractRecords) {
				String enterprise = contract.get(REGISTRY.NAME);
				String workplace = contract.get(WORKPLACE.DESCRIPTION);
				String employeeName = getFullName(contract) + " (" + getDocument(dslContext, contract.get(CONTRACT.PERSON)) + "CCC: " + getCompleteCCC(contract) + ")";
			
				AgreementCleanContract agreementCleanContract = new AgreementCleanContract(enterprise, workplace, employeeName);
				contracts.add(agreementCleanContract);
			}
			
			agreementClean.setContracts(contracts);
		} else
			agreementClean.setContracts(Collections.emptyList());
		
	}
	
	private static String getFullName(Record infoRecord) {
		String fullName = "";
		String firstSurname = infoRecord.get(PERSON.FIRST_SURNAME);
		String secondSurname = infoRecord.get(PERSON.SECOND_SURNAME);
		String name = infoRecord.get(PERSON.NAME);
		
		fullName += AonStringUtils.isBlank(firstSurname) ? "" : firstSurname + " ";
		fullName += AonStringUtils.isBlank(secondSurname) ? "" : secondSurname + ", ";
		fullName += AonStringUtils.isBlank(name) ? "" : name;
		
		return fullName;
	}

	private static String getDocument(DSLContext dslContext, Integer registryId) {
		String document = dslContext.select(REGISTRY.DOCUMENT).from(REGISTRY).where(REGISTRY.ID.eq(registryId)).fetchOne(REGISTRY.DOCUMENT);
		return AonStringUtils.isBlank(document) ? "" : document + " - ";
	}
	
	private static String getCompleteCCC(Record infoRecord) {
		return getCCCRegimeCode(infoRecord.get(ENTERPRISE_CCC.TYPE))+infoRecord.get(ENTERPRISE_CCC.CCC);
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
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

}
