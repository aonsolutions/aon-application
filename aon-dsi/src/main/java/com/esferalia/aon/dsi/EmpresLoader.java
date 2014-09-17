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
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import com.code.aon.config.enumeration.DomainType;
import com.code.aon.dbutils.AonSQLException;
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

public class EmpresLoader extends AbstractLoader implements
		Trabaj2Loader.Callback {

	public static interface Callback {

		Integer getCategory(FntrabajRecord trabaj);

		Integer getAgreement(FnempresRecord empres);

		PaymentConceptRecord getConcept(FntconceRecord conce);

	}

	private Callback cb;

	private Map<String, Integer> domains;

	private Map<String, int[]> ssIdsMap;

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
		this.domains = new Hashtable<String, Integer>();
		this.ssIdsMap = new Hashtable<String, int[]>();
	}

	// ------------------------------------------------------------------------

	public EmpresLoader loadEmpres(Integer parentDomain, String domainSuffix,
			String owner, Callback cb, Condition... conditions)
			throws AonSQLException {
		this.cb = cb;

		Cursor<Record> empresCursor = dsiContext.select().from(FNEMPRES)
				.where(conditions).fetchLazy();
		while (empresCursor.hasNext()) {
			FnempresRecord empres = empresCursor.fetchOneInto(FNEMPRES);

			String key = empres.getF20sscod() + empres.getF20ssnum();

			WorkplaceRecord record = getWorkplace(empres);

			if (record != null) {
				// TODO : REPLACE INTO `enterprise`
				ssIdsMap.put(key, new int[] { record.getDomain(), record.getId()});
				continue;
			}

			Integer domain = next(DOMAIN.getIdentity());

			Integer enterprise = next(REGISTRY.getIdentity());

			//@formatter:off

			Integer scope = getId(SCOPE.getIdentity(), 
					SCOPE.DOMAIN.eq(parentDomain));
			//@formatter:on

			String domainName = getDomainName(domainSuffix, domain, empres);
			int workplace = loadEmpres(empres, enterprise, domain,
					parentDomain, domainName, owner, scope, cb);

			ssIdsMap.put(key, new int[] { domain, workplace });

		}
		return this;
	}

	public void execute() {
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
	
	private WorkplaceRecord getWorkplace(FnempresRecord empres) {
		//@formatter:off
		return aonContext
		.select()
		.from(WORKPLACE)
		.where(WORKPLACE.DESCRIPTION.like(String.format("%%%s%%", getImportKey(empres))))
		.fetchOneInto(WORKPLACE);
		//@formatter:on
	}
	
	private String getImportKey(FnempresRecord empres) {
		return String.format("/*SSCOD:%s, SSNUM:%s*/", empres.getF20sscod(), empres.getF20ssnum());
	}


	private int loadEmpres(FnempresRecord empres, int registry, int domain,
			Integer parentDomain, String domainName, String owner, int scope,
			Callback cb) {
		//@formatter:off
		InsertSetStep<DomainRecord> insertSetStepDomain = getDomainInsertSetStep();
		insertSetMoreStepDomain = insertSetStepDomain.set(DOMAIN.ID, domain)
				.set(DOMAIN.TYPE, enum2Byte(DomainType.ENTERPRISE))
				.set(DOMAIN.OWNER, owner).set(DOMAIN.PARENT, parentDomain)
				.set(DOMAIN.NAME, domainName)
				.set(DOMAIN.DESCRIPTION, empres.getF20rsocial());
		//@formatter:on

		//@formatter:off
		InsertSetStep<RegistryRecord> insertSetStepRegistry = 
				getRegistryInsertSetStep();
		insertSetMoreStepRegistry = insertSetStepRegistry
				.set(REGISTRY.ID, registry).set(REGISTRY.DOMAIN, domain)
				.set(REGISTRY.TYPE, enum2Byte(RegistryType.LEGAL))
				.set(REGISTRY.NAME, empres.getF20rsocial())
				.set(REGISTRY.DOCUMENT, empres.getF20nif())
				.set(REGISTRY.DOCUMENT_TYPE, enum2Byte(getDocumentType(empres.getF20nif())));
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
