package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;

import java.sql.Connection;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.AgreementComunica;
import com.esferalia.aon.gwt.payroll.shared.AgreementComunicaInfo;
import com.esferalia.aon.gwt.payroll.shared.ComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunica;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceComunicaInfo;

public class JooqComunicaEnterpriseSettings {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// ------------------------------------------------- JooqComunicaEnterpriseSettings.Methods
	
	public static ComunicaEnterpriseSettings getComunicaEnterpriseSettings(Connection conn, Integer domainId, Integer userId) {
		return getComunicaEnterpriseSettingsDB(DSL.using(conn, getDefaultSettings()), conn, domainId, userId);
	}
	
	private static ComunicaEnterpriseSettings getComunicaEnterpriseSettingsDB(DSLContext dslContext, Connection conn, Integer domainId, Integer userId) {
		ComunicaEnterpriseSettings comunicaEnterpriseSettings = new ComunicaEnterpriseSettings();
		
		Integer enterpriseId = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);
		comunicaEnterpriseSettings.setEnterpriseId(enterpriseId);
		
		comunicaEnterpriseSettings.setWorkplaceComunica(getWorkplaceComunicaDB(dslContext, domainId));
		comunicaEnterpriseSettings.setAgreementComunica(getAgreementComunicaDB(dslContext, domainId));
		
		return comunicaEnterpriseSettings;
	}
	
	public static void setComunicaEnterpriseSettings(Connection conn, Integer domainId, Integer userId, ComunicaEnterpriseSettings comunicaEnterpriseSettings) {
		setComunicaEnterpriseSettings(DSL.using(conn, getDefaultSettings()), conn, domainId, userId, comunicaEnterpriseSettings);
	}
	
	private static void setComunicaEnterpriseSettings(DSLContext dslContext, Connection conn, Integer domainId, Integer userId, ComunicaEnterpriseSettings comunicaEnterpriseSettings) {
		Record enterpriseRecord = dslContext.select().from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne();
		Integer enterpriseId = enterpriseRecord.get(ENTERPRISE.REGISTRY);
		Integer scopeId = enterpriseRecord.get(ENTERPRISE.SCOPE);
				
		setWorkplaceComunicaDB(dslContext, domainId, enterpriseId, scopeId, comunicaEnterpriseSettings.getWorkplaceComunica());
		setAgreementComunicaDB(dslContext, domainId, enterpriseId, comunicaEnterpriseSettings.getAgreementComunica());
	}

	// ------------------------------------------------- WorkplaceComunica.Methods

	private static WorkplaceComunica getWorkplaceComunicaDB(DSLContext dslContext, Integer domainId) {
		WorkplaceComunica workplaceComunica = new WorkplaceComunica();
		
		Result<Record> workplaceRecords = dslContext.select().from(WORKPLACE)
			.where(WORKPLACE.DOMAIN.eq(domainId))
			.fetch();
		
		for(Record workplaceRecord : workplaceRecords) {
			Integer workplaceId = workplaceRecord.get(WORKPLACE.ID);
			String description = workplaceRecord.get(WORKPLACE.DESCRIPTION);
			Integer addressId = workplaceRecord.get(WORKPLACE.ADDRESS);
			workplaceComunica.insertWokplace(workplaceId, description, addressId);
		}
		
		return workplaceComunica;
	}
	
	private static void setWorkplaceComunicaDB(DSLContext dslContext, Integer domainId, Integer enterpriseId, Integer scopeId, WorkplaceComunica workplaceComunica) {
		for(Integer deleteId : workplaceComunica.getDeletedWorkplaces().keySet()) {
			dslContext.delete(PAYROLL_WORKPLACE).where(PAYROLL_WORKPLACE.WORKPLACE.eq(deleteId)).execute();
			dslContext.delete(WORKPLACE).where(WORKPLACE.ID.eq(deleteId)).execute();
		}
		
		for( Entry<Integer, WorkplaceComunicaInfo> workplaceEntry : workplaceComunica.getWorkplaces().entrySet()) {
			Integer workplaceId = workplaceEntry.getKey();
			WorkplaceComunicaInfo workplaceComunicaInfo = workplaceEntry.getValue();
			
			if(workplaceId > 0) {
				dslContext.update(WORKPLACE)
					.set(WORKPLACE.DESCRIPTION, workplaceComunicaInfo.getDescription())
					.set(WORKPLACE.ADDRESS, workplaceComunicaInfo.getAddressId())
					.where(WORKPLACE.ID.eq(workplaceId))
					.execute();
			} else {
				dslContext.insertInto(WORKPLACE)
					.set(WORKPLACE.DOMAIN, domainId)
					.set(WORKPLACE.ENTERPRISE, enterpriseId)
					.set(WORKPLACE.DESCRIPTION, workplaceComunicaInfo.getDescription())
					.set(WORKPLACE.ADDRESS, workplaceComunicaInfo.getAddressId())
					.set(WORKPLACE.ECONOMICAGREEMENT, DSL.castNull(WORKPLACE.ECONOMICAGREEMENT))
					.set(WORKPLACE.SCOPE, scopeId)
					.execute();
			}
		}
	}
	
	// ------------------------------------------------- AgreementComunica.Methods
	
	private static AgreementComunica getAgreementComunicaDB(DSLContext dslContext, Integer domainId) {
		AgreementComunica agreementComunica = new AgreementComunica();
		
		Result<Record> agreementRecords = dslContext.select().from(AGREEMENT)
			.where(AGREEMENT.DOMAIN.eq(domainId))
			.fetch();
		
		for(Record agreementRecord : agreementRecords) {
			Integer agreementId = agreementRecord.get(AGREEMENT.ID);
			String description = agreementRecord.get(AGREEMENT.DESCRIPTION);
			String ssCode = agreementRecord.get(AGREEMENT.SS_NUMBER);
			agreementComunica.insertAgreement(agreementId, description, ssCode);
		}
		
		return agreementComunica;
	}
	
	private static void setAgreementComunicaDB(DSLContext dslContext, Integer domainId, Integer enterpriseId, AgreementComunica agreementComunica) {
		for(Integer deleteId : agreementComunica.getDeletedAgreements().keySet()) {
			dslContext.update(PAYROLL_WORKPLACE)
				.set(PAYROLL_WORKPLACE.AGREEMENT, DSL.castNull(PAYROLL_WORKPLACE.AGREEMENT))
				.where(PAYROLL_WORKPLACE.AGREEMENT.eq(deleteId))
				.execute();
			
			dslContext.delete(AGREEMENT).where(AGREEMENT.ID.eq(deleteId)).execute();
		}
		
		for( Entry<Integer, AgreementComunicaInfo> agreementEntry : agreementComunica.getAgreements().entrySet()) {
			Integer agreementId = agreementEntry.getKey();
			AgreementComunicaInfo agreementComunicaInfo = agreementEntry.getValue();
			
			if(agreementId > 0) {
				dslContext.update(AGREEMENT)
					.set(AGREEMENT.DESCRIPTION, agreementComunicaInfo.getDescription())
					.set(AGREEMENT.SS_NUMBER, agreementComunicaInfo.getSSNumber())
					.where(AGREEMENT.ID.eq(agreementId))
					.execute();
			} else {
				dslContext.insertInto(AGREEMENT)
					.set(AGREEMENT.DOMAIN, domainId)
					.set(AGREEMENT.DESCRIPTION, agreementComunicaInfo.getDescription())
					.set(AGREEMENT.SS_NUMBER, agreementComunicaInfo.getSSNumber())
					.execute();
			}
		}
	}
	
}
