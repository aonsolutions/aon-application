package com.esferalia.aon.dsi;

import static com.esferalia.aon.dsi.jooq.tables.Fnempres.FNEMPRES;
import static com.esferalia.aon.dsi.util.EnumUtils.enum2Byte;
import static com.esferalia.aon.dsi.util.EnumUtils.getDocumentType;
import static com.esferalia.aon.dsi.util.RomanNumber.toRoman;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static java.lang.String.format;
import static org.jooq.tools.StringUtils.isBlank;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.exception.InvalidResultException;

import com.code.aon.config.enumeration.DomainType;
import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.dsi.jooq.tables.records.FnempresRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntrabajRecord;
import com.esferalia.aon.jooq.tables.records.CompanyRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.payroll.Pair;

public class EmpresLoader extends AbstractLoader implements
		Trabaj2Loader.Callback {

	public static interface Listener {
		void onEnterpriseIgnored(RegistryRecord enterprise);

		void onEnterpriseUpdated(RegistryRecord enterprise);

		void onEnterpriseInserted(RegistryRecord enterprise);
	}

	public static class NullListener implements Listener {

		static Listener NULL_LISTENER = new NullListener();

		@Override
		public void onEnterpriseIgnored(RegistryRecord enterprise) {
		}

		@Override
		public void onEnterpriseUpdated(RegistryRecord enterprise) {
		}

		@Override
		public void onEnterpriseInserted(RegistryRecord enterprise) {
		}

	}

	public static interface Callback {

		Integer getCategory(FntrabajRecord trabaj);

		Integer getAgreement(FnempresRecord empres);

		PaymentConceptRecord getConcept(FntconceRecord conce);

	}

	private Callback cb;

	private boolean replace;

	private Listener listener;

	private List<Query> updates;

	private Map<String, Integer> domains;

	private Map<String, int[]> ssIdsMap;
	
	private RegistryRecord registryRecord;

	private InsertSetMoreStep<DomainRecord> insertSetMoreStepDomain;
	private InsertSetMoreStep<RegistryRecord> insertSetMoreStepRegistry;
	private InsertSetMoreStep<RmediaRecord> insertSetMoreStepRmedia;
	private InsertSetMoreStep<RaddressRecord> insertSetMoreStepRaddress;
	private InsertSetMoreStep<EnterpriseRecord> insertSetMoreStepEnterprise;
	private InsertSetMoreStep<CompanyRecord> insertSetMoreStepCompany;
	private InsertSetMoreStep<WorkplaceRecord> insertSetMoreStepWorkplace;
	private InsertSetMoreStep<PayrollWorkplaceRecord> insertSetMoreStepPayrollWorkplace;

	public EmpresLoader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
		this.updates = new LinkedList<Query>();
		this.listener = NullListener.NULL_LISTENER;
		this.domains = new Hashtable<String, Integer>();
		this.ssIdsMap = new Hashtable<String, int[]>();
	}

	public EmpresLoader setReplace(boolean replace) {
		this.replace = replace;
		return this;
	}
	
	public EmpresLoader setListener(Listener listener) {
		this.listener = listener;
		return this;
	}

	// ------------------------------------------------------------------------

	public EmpresLoader loadEmpres(Integer parentDomain, String domainSuffix,
			String owner, Callback cb, Condition... conditions)
			throws AonSQLException {
		this.cb = cb;

		//@formatter:off
		Cursor<Record> empresCursor = 
				dsiContext
				.select()
				.from(FNEMPRES)
				.where(conditions)
				.fetchLazy()
				;
		//@formatter:on

		while (empresCursor.hasNext()) {
			FnempresRecord empres = empresCursor.fetchOneInto(FNEMPRES);
			Integer scope = null;
			
			try {
			//@formatter:off
			scope = getId(SCOPE.getIdentity(), 
					SCOPE.DOMAIN.eq(parentDomain));
			//@formatter:on
			} catch ( InvalidResultException e){
				//TODO: More than one SCOPE
				scope = getAnyId(SCOPE.getIdentity(), 
						SCOPE.DOMAIN.eq(parentDomain));
			}
			
			String key = empres.getF20sscod() + empres.getF20ssnum();

			Pair<WorkplaceRecord, RegistryRecord> pair = getWorkplace(empres);
			

			if (pair != null) {
				WorkplaceRecord workplace = pair.getFirst();
				RegistryRecord registry = pair.getSecond();
				ssIdsMap.put(key,
						new int[] { workplace.getDomain(), workplace.getId() });
				if (replace) {
					String domainName = getDomainName(domainSuffix,
							workplace.getDomain(), empres);

					//@formatter:off
					updateEmpres(
							empres, 
							registry, 
							parentDomain, 
							domainName, 
							owner,
							scope, 
							cb);
					//@formatter:on
					listener.onEnterpriseUpdated(registry);
				} else {
					listener.onEnterpriseIgnored(registry);
				}

				continue;
			}

			Integer domain = next(DOMAIN.getIdentity());

			Integer enterpriseId = next(REGISTRY.getIdentity());
			String domainName = getDomainName(domainSuffix, domain, empres);

			int workplaceId = loadEmpres(empres, enterpriseId, domain,
					parentDomain, domainName, owner, scope, cb);
			
			RegistryRecord registry = lastRegistry();
			listener.onEnterpriseInserted(registry);

			ssIdsMap.put(key, new int[] { domain, workplaceId });

		}
		return this;
	}

	public void execute() {
		update();
		insert();
	}

	// ------------------------------------------------------------------------
	@Override
	public int getDomain(FntrabajRecord trabaj) {
		return ssIdsMap.get(trabaj.getF20sscodem() + trabaj.getF20ssnumem())[0];
	}

	@Override
	public int getWorplace(FntrabajRecord trabaj) {
		return ssIdsMap.get(trabaj.getF20sscodem() + trabaj.getF20ssnumem())[1];
	}

	@Override
	public Integer getCategory(FntrabajRecord trabaj) {
		return cb.getCategory(trabaj);
	}

	@Override
	public PaymentConceptRecord getConcept(FntconceRecord conce) {
		return cb.getConcept(conce);
	}

	// ------------------------------------------------------------------------
	private void insert() {
		execute(insertSetMoreStepDomain);
		execute(insertSetMoreStepRegistry);
		execute(insertSetMoreStepRmedia);
		execute(insertSetMoreStepRaddress);
		execute(insertSetMoreStepEnterprise);
		execute(insertSetMoreStepCompany);
		execute(insertSetMoreStepWorkplace);
		execute(insertSetMoreStepPayrollWorkplace);

		insertSetMoreStepDomain = null;
		insertSetMoreStepRegistry = null;
		insertSetMoreStepRmedia = null;
		insertSetMoreStepRaddress = null;
		insertSetMoreStepEnterprise = null;
		insertSetMoreStepCompany = null;
		insertSetMoreStepWorkplace = null;
		insertSetMoreStepPayrollWorkplace = null;
	}

	private void update() {
		if (updates.isEmpty())
			return;

		aonContext.batch(updates).execute();

		updates.clear();
	}

	private Pair<WorkplaceRecord, RegistryRecord> getWorkplace(
			FnempresRecord empres) {
		//@formatter:off
		Record record = aonContext
		.select()
		.from(WORKPLACE)
		.join(REGISTRY)
		.on(WORKPLACE.ENTERPRISE.eq(REGISTRY.ID))
		.where(WORKPLACE.DESCRIPTION.like(String.format("%%%s%%", getImportKey(empres))))
		.fetchOne();
		//@formatter:on
		return record == null ? null
				: new Pair<WorkplaceRecord, RegistryRecord>(
						record.into(WORKPLACE), record.into(REGISTRY));
	}

	private String getImportKey(FnempresRecord empres) {
		return String.format("/*SSCOD:%s, SSNUM:%s*/", empres.getF20sscod(),
				empres.getF20ssnum());
	}

	private RegistryRecord updateEmpres(FnempresRecord empres, RegistryRecord registry,
			Integer parentDomain, String domainName, String domainOwner,
			int scope, Callback cb) {
		
		//@formatter:off
		updates.add(
			aonContext
			.update(DOMAIN)
			.set(DOMAIN.NAME, domainName)
			.set(DOMAIN.OWNER, domainOwner)
			.set(DOMAIN.PARENT, parentDomain)
			.set(DOMAIN.DESCRIPTION, empres.getF20rsocial())
			.where(DOMAIN.ID.eq(registry.getDomain())) 
			);
		//@formatter:on
		
		registry.setName(empres.getF20rsocial());
		registry.setDocument(empres.getF20nif());
		registry.setDocumentType(enum2Byte(getDocumentType(empres.getF20nif())));
		//@formatter:off
		updates.add(
			aonContext
			.update(REGISTRY)
			.set(registry)
			.where(REGISTRY.ID.eq(registry.getId()))
			);
		//@formatter:on

		//@formatter:off
		updates.add(
			aonContext
			.update(ENTERPRISE)
			.set(ENTERPRISE.SCOPE, scope)
			.where(ENTERPRISE.REGISTRY.eq(registry.getId())) 
			);
		//@formatter:on

		//@formatter:off
		updates.add(
			aonContext
			.update(ENTERPRISE)
			.set(ENTERPRISE.SCOPE, scope)
			.where(ENTERPRISE.REGISTRY.eq(registry.getId())) 
			);
		//@formatter:on

		//@formatter:off
		updates.add(
				aonContext
				.update(WORKPLACE)
				.set(WORKPLACE.SCOPE, scope)
				.where(WORKPLACE.ENTERPRISE.eq(registry.getId()))
				.and(WORKPLACE.DESCRIPTION.startsWith(getImportKey(empres)))
				);
		//@formatter:on

		//@formatter:off
		updates.add(
				aonContext
				.update(RADDRESS)
				.set(RADDRESS.TYPE, enum2Byte(AddressType.MAIN))
				.set(RADDRESS.NUMBER, empres.getF20numero())
				.set(RADDRESS.ADDRESS, empres.getF20domicil())
				.set(RADDRESS.CITY, empres.getF20poblaci())
				.set(RADDRESS.MUNICIPALITY_CODE,
						getMunicipality(empres.getF20poblaci()))
				.set(RADDRESS.ZIP, empres.getF20cp())
				.set(RADDRESS.GEOZONE, getGeozone(empres.getF20provin(), parentDomain))
				.where(RADDRESS.REGISTRY.eq(registry.getId()))
				.and(RADDRESS.TYPE.eq(enum2Byte(AddressType.MAIN)))
				);
		//@formatter:on

		//@formatter:off
		updates.add(
				aonContext
				.delete(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(registry.getId()))
				.and(RMEDIA.MEDIA.eq(enum2Byte(MediaType.FIXED_PHONE)))
				);
		//@formatter:on

		if (!isBlank(empres.getF20telef())) {
			//@formatter:off
			InsertSetStep<RmediaRecord> insertSetStepRmedia= getRmediaInsertSetStep();
			insertSetMoreStepRmedia = insertSetStepRmedia
					.set(RMEDIA.REGISTRY, registry.getId())
					.set(RMEDIA.DOMAIN, registry.getDomain())
					.set(RMEDIA.MEDIA, enum2Byte(MediaType.FIXED_PHONE))
					.set(RMEDIA.VALUE, empres.getF20telef());
			// TODO : raddress ?
			//@formatter:on
		}
		return registry;
	}

	private int loadEmpres(FnempresRecord empres, int registry, int domain,
			Integer parentDomain, String domainName, String owner, int scope,
			Callback cb) {
		//@formatter:off
		InsertSetStep<DomainRecord> insertSetStepDomain = getDomainInsertSetStep();
		insertSetMoreStepDomain = insertSetStepDomain
				.set(DOMAIN.ID, domain)
				.set(DOMAIN.TYPE, enum2Byte(DomainType.ENTERPRISE))
				.set(DOMAIN.OWNER, owner).set(DOMAIN.PARENT, parentDomain)
				.set(DOMAIN.NAME, domainName)
				.set(DOMAIN.DESCRIPTION, empres.getF20rsocial());
		
		FnempresRecord f;
		
		//@formatter:on

		registryRecord = new RegistryRecord();
		registryRecord.setId(registry);
		registryRecord.setDomain(domain);
		registryRecord.setType(enum2Byte(RegistryType.NATURAL));
		registryRecord.setName(empres.getF20rsocial());
		registryRecord.setDocument(empres.getF20nif());
		registryRecord.setDocumentType(enum2Byte(getDocumentType(empres.getF20nif())));
		
		registryRecord.setAlias(null);
		registryRecord.setNationality("ES");
		registryRecord.setDocumentCountry("ES");
		registryRecord.setSecurityLevel((byte)0);
		//@formatter:off
		InsertSetStep<RegistryRecord> insertSetStepRegistry = 
				getRegistryInsertSetStep();
		insertSetMoreStepRegistry = insertSetStepRegistry
				.set(registryRecord);
		//@formatter:on

		int raddress = next(RADDRESS.getIdentity());
		//@formatter:off
		InsertSetStep<RaddressRecord> insertSetStepRaddress= getRaddressInsertSetStep();
		insertSetMoreStepRaddress = insertSetStepRaddress
				.set(RADDRESS.ID, raddress)
				.set(RADDRESS.REGISTRY, registry)
				.set(RADDRESS.DOMAIN, domain)
				.set(RADDRESS.TYPE, enum2Byte(AddressType.MAIN))
				.set(RADDRESS.NUMBER, empres.getF20numero())
				.set(RADDRESS.ADDRESS, empres.getF20domicil())
				.set(RADDRESS.CITY, empres.getF20poblaci())
				.set(RADDRESS.MUNICIPALITY_CODE,
						getMunicipality(empres.getF20poblaci()))
				.set(RADDRESS.ZIP, empres.getF20cp())
				.set(RADDRESS.GEOZONE, getGeozone(empres.getF20provin(), parentDomain));
		//@formatter:on

		// telephone
		String telef = empres.getF20telef();
		if (!isBlank(telef)) {
			//@formatter:off
			InsertSetStep<RmediaRecord> insertSetStepRmedia= getRmediaInsertSetStep();
			insertSetMoreStepRmedia = insertSetStepRmedia
					.set(RMEDIA.REGISTRY, registry)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.MEDIA, enum2Byte(MediaType.FIXED_PHONE))
					.set(RMEDIA.VALUE, telef);
			// TODO : raddress ?
			//@formatter:on
		}

		//@formatter:off
		InsertSetStep<EnterpriseRecord> insertSetStepEnterprise= getEnterpriseInsertSetStep();
		insertSetMoreStepEnterprise = insertSetStepEnterprise
				.set(ENTERPRISE.REGISTRY, registry)
				.set(ENTERPRISE.DOMAIN, domain)
				.set(ENTERPRISE.SCOPE, scope);
		//@formatter:on

		//@formatter:off
		InsertSetStep<CompanyRecord> insertSetStepCompany= getCompanyInsertSetStep();
		insertSetMoreStepCompany = insertSetStepCompany
				.set(COMPANY.REGISTRY,registry)
				.set(COMPANY.DOMAIN,domain)
				.set(COMPANY.ACTIVE,(byte) 1);
		//@formatter:on

		int workplace = next(WORKPLACE.getIdentity());
		//@formatter:off
		InsertSetStep<WorkplaceRecord> insertSetStepWorkplace= getWorkplaceInsertSetStep();
		insertSetMoreStepWorkplace = insertSetStepWorkplace
				.set(WORKPLACE.ID, workplace)
				.set(WORKPLACE.SCOPE, scope)
				.set(WORKPLACE.DOMAIN, domain)
				.set(WORKPLACE.ENTERPRISE, registry)
				.set(WORKPLACE.ADDRESS, raddress)
				.set(WORKPLACE.DESCRIPTION, format("%s %S", getImportKey(empres) ,"PRINCIPAL"));
		//@formatter:on

		//@formatter:off
		InsertSetStep<PayrollWorkplaceRecord> insertSetStepPayrollWorkplace= getPayrollWorkplaceInsertSetStep();
		insertSetMoreStepPayrollWorkplace = insertSetStepPayrollWorkplace
				.set(PAYROLL_WORKPLACE.DOMAIN, domain)
				.set(PAYROLL_WORKPLACE.WORKPLACE, workplace)
				.set(PAYROLL_WORKPLACE.AGREEMENT, cb.getAgreement(empres))
				
				;
		//@formatter:on

		return workplace;
	}
	
	private RegistryRecord lastRegistry() {
		return registryRecord;
	}

	private String getDomainName(String domainSuffix, int domain,
			FnempresRecord empres) {
		StringBuffer buff = new StringBuffer();
		if (!isBlank(domainSuffix))
			buff.append(domainSuffix);

		String nif = empres.getF20nif();
		String rsocial = empres.getF20rsocial();

		if (!isBlank(rsocial)) {
			buff.insert(0, rsocial.replaceAll("\\W", "").toLowerCase());
			String name = buff.toString();
			Integer count = domains.get(name);
			if (count == null || count == 0) {
				domains.put(buff.toString(), 1);
			} else {
				domains.put(buff.toString(), ++count);
				buff.insert(0, toRoman(count) + "-");
			}
		} else {
			buff.insert(0, !isBlank(nif) ? nif : toRoman(domain));
			domains.put(buff.toString(), 1);
		}

		return buff.toString();
	}

	private InsertSetStep<PayrollWorkplaceRecord> getPayrollWorkplaceInsertSetStep() {
		return get(insertSetMoreStepPayrollWorkplace, PAYROLL_WORKPLACE);
	}

	private InsertSetStep<WorkplaceRecord> getWorkplaceInsertSetStep() {
		return get(insertSetMoreStepWorkplace, WORKPLACE);
	}

	private InsertSetStep<CompanyRecord> getCompanyInsertSetStep() {
		return get(insertSetMoreStepCompany, COMPANY);
	}

	private InsertSetStep<EnterpriseRecord> getEnterpriseInsertSetStep() {
		return get(insertSetMoreStepEnterprise, ENTERPRISE);
	}

	private InsertSetStep<RmediaRecord> getRmediaInsertSetStep() {
		return get(insertSetMoreStepRmedia, RMEDIA);
	}

	private InsertSetStep<RaddressRecord> getRaddressInsertSetStep() {
		return get(insertSetMoreStepRaddress, RADDRESS);
	}

	private InsertSetStep<DomainRecord> getDomainInsertSetStep() {
		return get(insertSetMoreStepDomain, DOMAIN);
	}

	private InsertSetStep<RegistryRecord> getRegistryInsertSetStep() {
		return get(insertSetMoreStepRegistry, REGISTRY);
	}

	// ------------------------------------------------------------------------

}
