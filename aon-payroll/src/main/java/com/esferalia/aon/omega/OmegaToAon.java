package com.esferalia.aon.omega;

import static com.esferalia.aon.jooq.tables.Cnae.CNAE;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class OmegaToAon {
	
	// -------------------------- DeaultSettings
	
	private static Settings settings = null;
	private DSLContext ctx = null;
	private Domain domain = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// -------------------------- Constructor
	
	protected OmegaToAon() {
		super();
	}
	
	// -------------------------- Parser
	
	public void parseOmegaToAon(Connection connection, Domain domain) {
		this.ctx = DSL.using(connection, getDefaultSettings());
		this.domain = domain;
		
		insertOrGetDomain();
		insertWorkplaces();
		insertActivities();
	}
	
	// -------------------------- Parser.Domain

	private void insertOrGetDomain() {
		Record domainRecord = null;
		
		domainRecord = ctx.select().from(DOMAIN
			.join(REGISTRY).on(DOMAIN.ID.eq(REGISTRY.DOMAIN))
			.join(COMPANY).on(REGISTRY.ID.eq(COMPANY.REGISTRY)))
			.where(REGISTRY.DOCUMENT.eq(domain.getDocument()))
			.fetchAny();
		
		if(null != domainRecord) {
			Integer registryId = ctx.select(ENTERPRISE.REGISTRY)
				.from(ENTERPRISE)
				.where(ENTERPRISE.DOMAIN.eq(domain.getDomain()))
				.fetchOne(ENTERPRISE.REGISTRY);
			
			domain.setDomain(domainRecord.get(DOMAIN.ID));
			domain.setParentDomain(domainRecord.get(DOMAIN.PARENT));
			domain.setRegistry(registryId);
		} else {
		
			domainRecord =  ctx.insertInto(DOMAIN)
				.set(DOMAIN.CREATION_USER, "omega")
				.set(DOMAIN.CREATION_DATE, new java.sql.Timestamp(System.currentTimeMillis()))
				.set(DOMAIN.DOMAINMANAGEMENT, (byte) 0)
				.set(DOMAIN.TYPE, (byte) 0)
				.set(DOMAIN.PARENT, DSL.castNull(DOMAIN.PARENT))
				.set(DOMAIN.OWNER, "omega@aonsolutions.org")
				.set(DOMAIN.NAME, domain.getName())
				.set(DOMAIN.DESCRIPTION, domain.getDescription())
				.set(DOMAIN.ENABLEHEREDITY, (byte) 1)
				.set(DOMAIN.MAXDEFINEDUSERS, 0)
				.set(DOMAIN.MAXDOCUMENTSIZE, 1)
				.set(DOMAIN.MAXTOTALDOCUMENTSIZE, 16)
				.returning()
				.fetchOne();
			
			Integer domainId = domainRecord.get(DOMAIN.ID);
			domain.setDomain(domainId);
				
			ctx.insertInto(DOMAIN_APPLICATION)
				.set(DOMAIN_APPLICATION.DOMAIN, domain.getDomain())
				.set(DOMAIN_APPLICATION.APPLICATION, 28)
				.set(DOMAIN_APPLICATION.ACTIVE, (byte) 1)
				.set(DOMAIN_APPLICATION.AUDIT_LEVEL, (byte) 0)
				.execute();
	
			Record registryRecord = ctx.insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, domain.getDomain())
				.set(REGISTRY.DOCUMENT, domain.getDocument())
				.set(REGISTRY.DOCUMENT_TYPE, (byte) 0)
				.set(REGISTRY.NAME, domain.getDescription())
				.set(REGISTRY.TYPE, (byte) 1)
				.returning()
				.fetchOne();
			
			Integer registryId = registryRecord.get(REGISTRY.ID);
			domain.setRegistry(registryId);
	
			ctx.insertInto(COMPANY)
				.set(COMPANY.REGISTRY, domain.getRegistry())
				.set(COMPANY.DOMAIN, domain.getDomain())
				.execute();
	
			EnterpriseRecord registryParentRecord = ctx.selectFrom(ENTERPRISE)
					.where(ENTERPRISE.DOMAIN.eq(domain.getParentDomain()))
					.fetchAny();
	
			ctx.insertInto(ENTERPRISE)
				.set(ENTERPRISE.REGISTRY, domain.getRegistry())
				.set(ENTERPRISE.DOMAIN, domain.getDomain())
				.set(ENTERPRISE.SCOPE, registryParentRecord.getValue(ENTERPRISE.SCOPE))
				.execute();
		}
		
	}
	
	// -------------------------- Parser.Workplace
	
	private void insertWorkplaces() {
		for(Workplace workplace : domain.getWorkplaces()) {
			Record workplaceRecord = ctx.select().from(WORKPLACE
					.join(RADDRESS).on(WORKPLACE.ADDRESS.eq(RADDRESS.ID)))
					.where(WORKPLACE.DESCRIPTION.eq(workplace.getDescription()))
					.and(RADDRESS.ADDRESS.eq(workplace.getAddress().getStreet()))
					.and(RADDRESS.NUMBER.eq(workplace.getAddress().getNumber()))
					.and(RADDRESS.ZIP.eq(workplace.getAddress().getZip()))
					.fetchAny();
			
			if(null != workplaceRecord)
				workplace.setWorkplace(workplaceRecord.get(WORKPLACE.ID));
			else {
				insertOrGetAddress(workplace.getAddress());
				insertWorkplace(workplace);
			}
		}
	}

	private void insertWorkplace(Workplace workplace) {
		workplace.setWorkplace(ctx.insertInto(WORKPLACE)
			.set(WORKPLACE.DOMAIN, domain.getDomain())
			.set(WORKPLACE.ENTERPRISE, domain.getRegistry())
			.set(WORKPLACE.DESCRIPTION, workplace.getDescription())
			.set(WORKPLACE.ADDRESS, workplace.getAddress().getAddress())
			.set(WORKPLACE.SCOPE, domain.getScope())
			.returning(WORKPLACE.ID)
			.fetchOne().get(WORKPLACE.ID));
		
		ctx.insertInto(PAYROLL_WORKPLACE)
			.set(PAYROLL_WORKPLACE.DOMAIN, domain.getDomain())
			.set(PAYROLL_WORKPLACE.WORKPLACE, workplace.getWorkplace())
			.execute();
	}

	private void insertOrGetAddress(Address address) {
		Record addressRecord = ctx.select().from(RADDRESS)
			.where(RADDRESS.DOMAIN.eq(domain.getDomain()))
			.and(RADDRESS.ADDRESS.eq(address.getStreet()))
			.and(RADDRESS.NUMBER.eq(address.getNumber()))
			.and(RADDRESS.ZIP.eq(address.getZip()))
			.fetchAny();
		
		if(null != addressRecord)
			address.setAddress(addressRecord.get(RADDRESS.ID));
		else {
			addressRecord = ctx.insertInto(RADDRESS)
				.set(RADDRESS.DOMAIN, domain.getDomain())
				.set(RADDRESS.REGISTRY, domain.getRegistry())
				.set(RADDRESS.STREET_TYPE, address.getStreetType())
				.set(RADDRESS.ADDRESS, address.getStreet())
				.set(RADDRESS.NUMBER, address.getNumber())
				.set(RADDRESS.ZIP, address.getZip())
				.set(RADDRESS.CITY, address.getCity())
				.set(RADDRESS.GEOZONE, getGeozone(address.getGeozone()))
				.set(RADDRESS.MUNICIPALITY_CODE, address.getMunicipalityCode())
				.returning()
				.fetchOne();
			
			address.setAddress(addressRecord.get(RADDRESS.ID));
		}
	}

	private Integer getGeozone(String geozoneCode) {
		return ctx.select(GEOZONE.ID).from(GEOZONE)
			.where(GEOZONE.DOMAIN.eq(0))
			.and(GEOZONE.CODE.eq(geozoneCode))
			.fetchAny().get(GEOZONE.ID);
	}
	
	// -------------------------- Parser.Activity

	private void insertActivities() {
		for(Activity activity : domain.getActivities()) {
			Record activityRecord = ctx.select().from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.DESCRIPTION.eq(activity.getDescription()))
				.and(ENTERPRISE_ACTIVITY.CNAE.eq(getCNAE(activity.getCnae())))
				.and(ENTERPRISE_ACTIVITY.CNAE2009.eq(getCNAE2009(activity.getCnae2009())))
				.and(ENTERPRISE_ACTIVITY.IAE.eq(getIAE(activity.getIae())))
				.fetchAny();
				
			if(null != activityRecord)
				activity.setActivity(activityRecord.get(ENTERPRISE_ACTIVITY.ID));
			else
				insertActivity(activity);
			
			for(Ccc ccc : activity.getCccs()) {
				Record cccRecord = ctx.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.CCC.eq(ccc.getCccCode()))
					.and(ENTERPRISE_CCC.TYPE.eq(ccc.getCccType()))
					.fetchAny();
				
				if(null != cccRecord)
					ccc.setCcc(cccRecord.get(ENTERPRISE_CCC.ID));
				else
					insertActivityCCC(activity, ccc);
					
			}
		}
	}

	private void insertActivity(Activity activity) {
		activity.setActivity(ctx.insertInto(ENTERPRISE_ACTIVITY)
			.set(ENTERPRISE_ACTIVITY.DOMAIN, domain.getDomain())
			.set(ENTERPRISE_ACTIVITY.DESCRIPTION, activity.getDescription())
			.set(ENTERPRISE_ACTIVITY.ENTERPRISE, domain.getRegistry())
			.set(ENTERPRISE_ACTIVITY.IAE, getIAE(activity.getIae()))
			.set(ENTERPRISE_ACTIVITY.CNAE, getCNAE(activity.getCnae()))
			.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
			.set(ENTERPRISE_ACTIVITY.CNAE2009, getCNAE2009(activity.getCnae2009()))
			.returning(ENTERPRISE_ACTIVITY.ID)
			.fetchOne().get(ENTERPRISE_ACTIVITY.ID));
	}
	
	private void insertActivityCCC(Activity activity, Ccc ccc) {
		ccc.setCcc(ctx.insertInto(ENTERPRISE_CCC)
			.set(ENTERPRISE_CCC.DOMAIN, domain.getDomain())
			.set(ENTERPRISE_CCC.CCC, ccc.getCccCode())
			.set(ENTERPRISE_CCC.TYPE, ccc.getCccType())
			.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, activity.getActivity())
			.set(ENTERPRISE_CCC.GEOZONE, getGeozone(ccc.getCccGeozone()))
			.returning(ENTERPRISE_CCC.ID)
			.fetchOne().get(ENTERPRISE_CCC.ID));
	}

	private Integer getCNAE(String cnae) {
		return ctx.select(CNAE.ID).from(CNAE)
				.where(CNAE.CODE.eq(cnae))
				.fetchAny().get(CNAE.ID);
	}

	private Integer getCNAE2009(String cnae2009) {
		return ctx.select(CNAE2009.ID).from(CNAE2009)
				.where(CNAE2009.CODE.eq(cnae2009))
				.fetchAny().get(CNAE2009.ID);
	}

	private Integer getIAE(String iae) {
		if(AonStringUtils.isBlank(iae) || !AonStringUtils.containsIgnoreCase(iae, "-"))
			return null;
		
		String section = iae.split("-")[0];
		String epigraph = iae.split("-")[1];
		
		return ctx.select(IAE.ID).from(IAE)
				.where(IAE.SECTION.eq(section))
				.and(IAE.EPIGRAPH.eq(epigraph))
				.fetchAny().get(IAE.ID);
	}

}
