package com.esferalia.aon.in.payroll.tgss.ivl;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.utils.Municipalities;
import com.esferalia.aon.jooq.Keys;
import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.records.Cnae2009Record;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.ScopeRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqIvl2Contract implements IvlParserListener {

    private static Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
    
    private Optional<String> user; 
    private DSLContext dslContext;
    private String enterpriseScope;
    private String domainName;
    
    private DomainRecord domainRecord;
    private PersonRecord personRecord;
    private ContractRecord contractRecord; 
    private EnterpriseCccRecord enterpriseCccRecord;
    private PayrollWorkplaceRecord payrollWorkplaceRecord;
    
    public JooqIvl2Contract(DSLContext dslContext, String parentDomainName) {
	this(dslContext, parentDomainName, "GENERAL", null);
    }

    public JooqIvl2Contract(DSLContext dslContext, String domainName, String enterpriseScope, String user) {
	this.dslContext = dslContext;
	this.domainName = domainName;
	this.user = Optional.ofNullable(user);
	this.enterpriseScope = enterpriseScope;
    }

    @Override
    public void onEnterprise(String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
            String docType, String docNumber, String enterpriseAddress, String enterpriseCity, String enterpriseCP,
            String enterpriseCNAENumber, String enterpriseCNAEDescription) {
	
	domainRecord = 
	getDomain(dslContext, DOMAIN.NAME.eq(domainName).and(DOMAIN.TYPE.in((byte)0))) 
	.orElseGet(() -> getDomain(dslContext, PARENT_DOMAIN.NAME.eq(domainName).and(REGISTRY.DOCUMENT.equalIgnoreCase(docNumber)))
	.orElseGet(() -> newDomain(dslContext, domainName, docNumber, enterpriseName))
	);
	
	enterpriseCccRecord =
	getEnterpriseCCC(dslContext, ENTERPRISE_CCC.CCC.eq(cccProvince + cccNumber).and(DOMAIN.ID.eq(domainRecord.getId())) )
	.orElseGet(() -> newEnterpriseCCC(dslContext, domainRecord, enterpriseName, cccRegime, cccProvince, cccNumber, docType, docNumber, enterpriseCNAENumber, enterpriseCNAEDescription, enterpriseScope));
	
	payrollWorkplaceRecord = 
	getAnyPayrollWorkplace(dslContext, PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY.eq(enterpriseCccRecord.getEnterpriseActivity()).and(RADDRESS.ZIP.eq(enterpriseCP)))
	.orElseGet(() -> newPayrollWorkplace(dslContext,enterpriseCccRecord, enterpriseAddress, enterpriseCity, enterpriseCP ));
    }
    
    
    @Override
    public void onEmployee(String nafProvince, String nafNumber, String docType, String docNumber,
            String employeeName) {
	
	Integer domainId = enterpriseCccRecord.getDomain();

	personRecord =
	getPerson(dslContext, PERSON.DOMAIN.eq(domainId).and(PERSON.SOCIAL_SECURITY_NUM.eq(nafProvince + nafNumber)))
	.orElseGet(() -> newPerson(dslContext, domainId, nafProvince, nafNumber, docType, docNumber, employeeName));
	
	
    }
    
    @Override
    public void onEmployeeContract(Date realStartDate, Date efectiveStartDate, Date realEndDate, Date efectiveEndDate,
            String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims, Integer quoteDays) {
	
	Integer domainId = enterpriseCccRecord.getDomain();
	java.sql.Date contractStartDate = new java.sql.Date (realStartDate.getTime());
	Optional<java.sql.Date> contractEndDate = realEndDate != null ? Optional.of(new java.sql.Date (realEndDate.getTime())) : Optional.empty() ;
	
	contractRecord = 
	getContract(dslContext, CONTRACT.DOMAIN.eq(domainId).and(CONTRACT.PERSON.eq(personRecord.getRegistry())).and(CONTRACT.START_DATE.eq(contractStartDate)))
	.orElseGet(() -> newContract(dslContext, payrollWorkplaceRecord, enterpriseCccRecord, personRecord, contractStartDate, contractEndDate, quoteGroup, monthly, tc2, partialFactor, it, ims));
    }
    
    // ------------------------------------------------------------------------
    
    private static Condition _getDomainCondition(String parentDomainName) {
	return PARENT_DOMAIN.NAME.eq(parentDomainName).or(DOMAIN.NAME.endsWith('.' + parentDomainName));
    }
    
    private static ContractRecord newContract(DSLContext dslContext, PayrollWorkplaceRecord payrollWorkplaceRecord,
	    EnterpriseCccRecord enterpriseCccRecord, PersonRecord personRecord, java.sql.Date contractStartDate,
	    Optional<java.sql.Date> contractEndDate, String quoteGroup, String monthly, String tc2, Double partialFactor, Double it, Double ims) {
	
	ContractRecord contractRecord = 
	dslContext
	.insertInto(CONTRACT)
	.set(CONTRACT.START_DATE, contractStartDate)
	.set(CONTRACT.END_DATE, contractEndDate.map(DSL::val).orElse(DSL.val((java.sql.Date)null)))
	.set(CONTRACT.PERSON, personRecord.getRegistry())
	.set(CONTRACT.DOMAIN, enterpriseCccRecord.getDomain())
	.set(CONTRACT.WORKPLACE, payrollWorkplaceRecord.getWorkplace())
	.set(CONTRACT.ENTERPRISE_CCC, enterpriseCccRecord.getId())
	.set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseCccRecord.getEnterpriseActivity())
	.returning()
	.fetchOne();
	
	if ( AonStringUtils.isNotBlank(quoteGroup) ) {
        	dslContext
        	.insertInto(CONTRACT_DATA)
        	.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
        	.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
        	.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
        	.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", quoteGroup))
        	.set(CONTRACT_DATA.START_DATE, contractStartDate)
        	.set(CONTRACT_DATA.END_DATE, contractEndDate.map(DSL::val).orElse(DSL.val((java.sql.Date)null)))
        	.execute();
	}
	
	if ( AonStringUtils.equalsIgnoreCase(monthly, "S") ) {
        	dslContext
        	.insertInto(CONTRACT_DATA)
        	.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
        	.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
        	.set(CONTRACT_DATA.NAME, "DIAS_MES")
        	.set(CONTRACT_DATA.EXPRESSION, "30.00")
        	.set(CONTRACT_DATA.START_DATE, contractStartDate)
        	.set(CONTRACT_DATA.END_DATE, contractEndDate.map(DSL::val).orElse(DSL.val((java.sql.Date)null)))
        	.execute();
	}

	if ( AonStringUtils.isNotBlank(tc2) ) {
        	dslContext
        	.insertInto(CONTRACT_DATA)
        	.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
        	.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
        	.set(CONTRACT_DATA.NAME, "TC2")
        	.set(CONTRACT_DATA.EXPRESSION, String.format("\"%s\"", tc2))
        	.set(CONTRACT_DATA.START_DATE, contractStartDate)
        	.set(CONTRACT_DATA.END_DATE, contractEndDate.map(DSL::val).orElse(DSL.val((java.sql.Date)null)))
        	.execute();
	}
	
	if ( AonNumberUtils.isValid(partialFactor) ) {
        	dslContext
        	.insertInto(CONTRACT_DATA)
        	.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
        	.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
        	.set(CONTRACT_DATA.NAME, "COEFICIENTE_PARCIALIDAD")
        	.set(CONTRACT_DATA.EXPRESSION, Double.toString(partialFactor))
        	.set(CONTRACT_DATA.START_DATE, contractStartDate)
        	.set(CONTRACT_DATA.END_DATE, contractEndDate.map(DSL::val).orElse(DSL.val((java.sql.Date)null)))
        	.execute();
	}

	//TODO: ¿ it , ims ? 
	
	return contractRecord;
    }
    
    private static Optional<ContractRecord> getContract(DSLContext dslContext, Condition condition) {
	return
	dslContext
	.select()
	.from(CONTRACT)
	.where(condition)
	.fetchOptionalInto(CONTRACT);
    }
    
    private static PersonRecord newPerson(DSLContext dslContext, Integer domainId, String nafProvince, String nafNumber, String docType, String docNumber,
            String employeeName) {
	
	RegistryRecord registryRecord = 
        	dslContext
        	.select()
        	.from(REGISTRY)
        	.where(REGISTRY.DOMAIN.eq(domainId))
        	.and(REGISTRY.DOCUMENT.eq(docNumber))
        	.fetchOptionalInto(REGISTRY)
        	.orElseGet(() ->
                	dslContext
                	.insertInto(REGISTRY)
                	.set(REGISTRY.TYPE, (byte) 0)
                	.set(REGISTRY.DOMAIN, domainId)
                	.set(REGISTRY.NAME, employeeName)
                	.set(REGISTRY.DOCUMENT, docNumber)
                	.set(REGISTRY.DOCUMENT_COUNTRY, "ES")
                	.set(REGISTRY.DOCUMENT_TYPE, geDocumentType(docType))
                	.returning()
                	.fetchOne()
        	);
	
	String personName = "";
	String personFirstSurname = "";
	String personSecondSurname = "";
	String [] employeeNames = employeeName.split(" +");
	if ( employeeNames.length > 0 ) {
	    personName = employeeNames[0];
	}
	if ( employeeNames.length > 1 ) {
	    personFirstSurname = employeeNames[1];
	}
	if ( employeeNames.length > 2 ) {
	    personSecondSurname = Arrays.stream(employeeNames,  2, employeeNames.length).collect(Collectors.joining(" "));
	}
	
	
		
	dslContext
	.insertInto(PERSON)
	.set(PERSON.DOMAIN, domainId)
	.set(PERSON.REGISTRY, registryRecord.getId())
	.set(PERSON.GENDER, (byte) 2)
	.set(PERSON.NAME, personName )
	.set(PERSON.FIRST_SURNAME, personFirstSurname )
	.set(PERSON.SECOND_SURNAME, personSecondSurname )
	.set(PERSON.SOCIAL_SECURITY_NUM, nafProvince + nafNumber )
	.execute();

	return
	dslContext
	.select()
	.from(PERSON)
	.where(PERSON.REGISTRY.eq(registryRecord.getId()))
	.fetchOneInto(PERSON);
	
    }
    
    private static Optional<PersonRecord> getPerson(DSLContext dslContext, Condition condition) {
	return 
	dslContext
	.select()
	.from(PERSON)
	.innerJoin(REGISTRY).onKey()
	.where(condition)
	.fetchOptionalInto(PERSON);
    }

    private static  EnterpriseCccRecord newEnterpriseCCC(DSLContext dslContext, DomainRecord domainRecord , String enterpriseName, String cccRegime, String cccProvince, String cccNumber,
            String docType, String docNumber, String enterpriseCNAENumber,  String enterpriseCNAEDescription, String enterpriseScope) {
	
	Optional<GeozoneRecord> geozoneRecord = getGeozone(dslContext, cccProvince, domainRecord); 
	
	EnterpriseActivityRecord enterpriseActivityRecord = 
	getEnterpriseActivity(dslContext, ENTERPRISE_ACTIVITY.DOMAIN.eq(domainRecord.getId()).and(CNAE2009.CODE.eq(enterpriseCNAENumber)))
	.orElseGet(() -> newEnterpriseActivity(dslContext, domainRecord, enterpriseName, docType, docNumber, enterpriseCNAENumber, enterpriseCNAEDescription, enterpriseScope));
	
	return 
	dslContext
	.insertInto(ENTERPRISE_CCC)
	.set(ENTERPRISE_CCC.TYPE, (byte) 0 ) 
	.set(ENTERPRISE_CCC.CCC, cccProvince+cccNumber)
	.set(ENTERPRISE_CCC.DOMAIN, domainRecord.getId())
	.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivityRecord.getId())
	.set(ENTERPRISE_CCC.GEOZONE, geozoneRecord.map(GeozoneRecord::getId).map(DSL::val).orElse(DSL.val((Integer)null)))
	.returning()
	.fetchOne();
    }

    private static Optional<GeozoneRecord> getGeozone(DSLContext dslContext, String cccProvince,
	    DomainRecord domainRecord) {
	return getGeozone(dslContext, GEOZONE.DOMAIN.eq(domainRecord.getId()).and(GEOZONE.CODE.eq(cccProvince))) 
	.or(() -> getGeozone(dslContext, GEOZONE.DOMAIN.eq(domainRecord.getParent()).and(GEOZONE.CODE.eq(cccProvince))))
	.or(() -> getGeozone(dslContext, GEOZONE.DOMAIN.eq(0).and(GEOZONE.CODE.eq(cccProvince))));
    }
    
    private static EnterpriseActivityRecord newEnterpriseActivity(
	    DSLContext dslContext, 
	    DomainRecord domainRecord,
	    String enterpriseName, 
	    String docType, 
	    String docNumber, 
	    String enterpriseCNAENumber,
	    String enterpriseCNAEDescription,
	    String enterpriseScope) {
	
	Optional<Cnae2009Record> cnae2009Record = 
	getCnae2009(dslContext, CNAE2009.CODE.eq(enterpriseCNAENumber));
	
	EnterpriseRecord enterpriseRecord = 
	getEnterprise(dslContext, ENTERPRISE.DOMAIN.eq(domainRecord.getId()))
	.orElseGet(() -> newEnterprise(dslContext, domainRecord, docType, docNumber, enterpriseName, enterpriseScope));
	
	return 
	dslContext
	.insertInto(ENTERPRISE_ACTIVITY)
	.set(ENTERPRISE_ACTIVITY.TYPE, (byte) 0 )
	.set(ENTERPRISE_ACTIVITY.DOMAIN, domainRecord.getId() )
	.set(ENTERPRISE_ACTIVITY.CNAE2009, cnae2009Record.map(Cnae2009Record::getId).map(DSL::val).orElse(DSL.val((Integer)null)))
	.set(ENTERPRISE_ACTIVITY.DESCRIPTION, enterpriseCNAEDescription )
	.set(ENTERPRISE_ACTIVITY.ENTERPRISE, enterpriseRecord.getRegistry())
	.returning()
	.fetchOne();

    }
    
    private static EnterpriseRecord newEnterprise(DSLContext dslContext, DomainRecord domainRecord, String docType, String docNumber, String enterpriseName, String scope ) {
	
	RegistryRecord registryRecord =
	dslContext
	.insertInto(REGISTRY)
	.set(REGISTRY.TYPE, (byte) 1)
	.set(REGISTRY.DOMAIN, domainRecord.getId())
	.set(REGISTRY.NAME, enterpriseName)
	.set(REGISTRY.DOCUMENT, docNumber)
	.set(REGISTRY.DOCUMENT_COUNTRY, "ES")
	.set(REGISTRY.DOCUMENT_TYPE, geDocumentType(docType))
	.returning()
	.fetchOne();
	
	dslContext
	.insertInto(COMPANY)
	.set(COMPANY.ACTIVE, (byte)1)
	.set(COMPANY.DOMAIN, registryRecord.getDomain())
	.set(COMPANY.REGISTRY, registryRecord.getId())
	.returning()
	.fetchOne();
	
	ScopeRecord scopeRecord = 
	getScope(dslContext, SCOPE.DOMAIN.eq(domainRecord.getId()).and(SCOPE.DESCRIPTION.eq(scope)))
	.or(() -> getScope(dslContext, SCOPE.DOMAIN.eq(domainRecord.getParent()).and(SCOPE.DESCRIPTION.eq(scope))))
	.orElseGet(() -> newScope(dslContext, domainRecord, scope));
	
	dslContext
	.insertInto(ENTERPRISE)
	.set(ENTERPRISE.SCOPE, scopeRecord.getId())
	.set(ENTERPRISE.DOMAIN, registryRecord.getDomain())
	.set(ENTERPRISE.REGISTRY, registryRecord.getId())
	.execute();
	
	return dslContext
	.select()
	.from(ENTERPRISE)
	.where(ENTERPRISE.REGISTRY.eq(registryRecord.getId()))
	.and(ENTERPRISE.DOMAIN.eq(registryRecord.getDomain()))
	.fetchOneInto(ENTERPRISE);
	
    }
    
    private static ScopeRecord newScope(DSLContext dslContext, DomainRecord domainRecord,  String description) {
	return
	dslContext
	.insertInto(SCOPE)
	.set(SCOPE.DOMAIN, domainRecord.getId())
	.set(SCOPE.DESCRIPTION, description)
	.returning()
	.fetchOne();
    }
    
    private static  DomainRecord newDomain(DSLContext dslContext, String domainName, String name, String description) {
	return 
	dslContext
	.insertInto(DOMAIN)
	.columns(
		DOMAIN.NAME
		, DOMAIN.OWNER
		, DOMAIN.PARENT
		, DOMAIN.DESCRIPTION
		, DOMAIN.ENABLEHEREDITY
		)
	.select(
		DSL.select(
		DSL.concat(DSL.value(name + "-"), DOMAIN.NAME)
		, DOMAIN.OWNER
		, DOMAIN.ID
		, DSL.val(description)
		, DSL.val((byte)1))
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName)))
	.returning()
	.fetchOptionalInto(DOMAIN)
	.orElseGet(() -> 
        	dslContext
        	.insertInto(DOMAIN)
        	.set(DOMAIN.NAME, domainName)
        	.set(DOMAIN.OWNER,  "console@" + domainName)
        	.set(DOMAIN.DESCRIPTION, description)
        	.returning()
        	.fetchOneInto(DOMAIN)
	);
    }

    private static  Optional<DomainRecord> getDomain(DSLContext dslContext,  Condition condition) {
	return dslContext
	.select()
	.from(DOMAIN)
	.leftJoin(PARENT_DOMAIN).onKey(Keys.FK_DOMAIN_PARENT)
	.leftJoin(ENTERPRISE).on(DOMAIN.ID.eq(ENTERPRISE.DOMAIN))
	.leftJoin(REGISTRY).onKey(Keys.FK_ENTERPRISE_REGISTRY)
	.where(condition)
	.fetchOptionalInto(DOMAIN)
	;
    }

    private static PayrollWorkplaceRecord newPayrollWorkplace(DSLContext dslContext,
	    EnterpriseCccRecord enterpriseCccRecord, String enterpriseAddress, String enterpriseCity,
	    String enterpriseCP) {
	WorkplaceRecord workplaceRecord = 
	getAnyWorkplace(dslContext, ENTERPRISE_CCC.ID.eq(enterpriseCccRecord.getId()).and(RADDRESS.ZIP.eq(enterpriseCP)))
	.orElseGet(() -> newWorkplace(dslContext, enterpriseCccRecord, enterpriseAddress, enterpriseCity, enterpriseCP));
	
	return
	dslContext
	.insertInto(PAYROLL_WORKPLACE)
	.set(PAYROLL_WORKPLACE.DOMAIN, workplaceRecord.getDomain())
	.set(PAYROLL_WORKPLACE.WORKPLACE, workplaceRecord.getId())
	.set(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY, enterpriseCccRecord.getEnterpriseActivity())
	.returning()
	.fetchOne();
    }
    
    private static WorkplaceRecord newWorkplace(DSLContext dslContext,
	    EnterpriseCccRecord enterpriseCccRecord, String enterpriseAddress, String enterpriseCity,
	    String enterpriseCP) {
	
	EnterpriseRecord enterpriseRecord = 
	getEnterprise(dslContext, ENTERPRISE.DOMAIN.eq(enterpriseCccRecord.getDomain())).orElseThrow();
	
	RaddressRecord raddressRecord = 
	getAnyRaddress(dslContext, RADDRESS.REGISTRY.eq(enterpriseCccRecord.getId()).and(RADDRESS.ZIP.eq(enterpriseCP)))
	.orElseGet(() -> newRaddress(dslContext, enterpriseRecord, enterpriseAddress, enterpriseCity, enterpriseCP));
	
	return
	dslContext
	.insertInto(WORKPLACE)
	.set(WORKPLACE.DESCRIPTION, enterpriseCity)
	.set(WORKPLACE.ADDRESS, raddressRecord.getId())
	.set(WORKPLACE.SCOPE, enterpriseRecord.getScope())
	.set(WORKPLACE.DOMAIN, enterpriseRecord.getDomain())
	.set(WORKPLACE.ENTERPRISE,enterpriseRecord.getRegistry() )
	.returning()
	.fetchOne();
	
    }
    
    private static RaddressRecord newRaddress(DSLContext dslContext,EnterpriseRecord enterpriseRecord, String enterpriseAddress,
	    String enterpriseCity, String enterpriseCP) {
	
	Optional<String> municipalityCode =  Optional.ofNullable(new Municipalities().getCodeByMunicipalityNameAndZip(enterpriseCP, enterpriseCity));
	
	return
	dslContext
	.insertInto(RADDRESS)
	.set(RADDRESS.ZIP,enterpriseCP )
	.set(RADDRESS.CITY,enterpriseCity )
	.set(RADDRESS.ADDRESS,enterpriseAddress )
	.set(RADDRESS.DOMAIN, enterpriseRecord.getDomain())
	.set(RADDRESS.REGISTRY,enterpriseRecord.getRegistry() )
	.set(RADDRESS.MUNICIPALITY_CODE, municipalityCode.map(DSL::val).orElse(DSL.val((String) null)))
	.returning()
	.fetchOne();
    }
    
    private static  Optional<RaddressRecord> getAnyRaddress(DSLContext dslContext,  Condition condition) {
	return Optional.ofNullable( 
        	dslContext
        	.select()
        	.from(RADDRESS)
        	.where(condition)
        	.fetchAnyInto(RADDRESS));
    }

    private static  Optional<PayrollWorkplaceRecord> getAnyPayrollWorkplace(DSLContext dslContext,  Condition condition) {
	return Optional.ofNullable( 
        	dslContext
        	.select()
        	.from(PAYROLL_WORKPLACE)
        	.innerJoin(WORKPLACE).onKey()
        	.innerJoin(RADDRESS).onKey()
        	.where(condition)
        	.fetchAnyInto(PAYROLL_WORKPLACE));
    }

    private static  Optional<WorkplaceRecord> getAnyWorkplace(DSLContext dslContext,  Condition condition) {
	return Optional.ofNullable( 
        	dslContext
        	.select()
        	.from(WORKPLACE)
        	.innerJoin(RADDRESS).onKey()
        	.innerJoin(ENTERPRISE).onKey()
        	.innerJoin(ENTERPRISE_ACTIVITY).onKey()
        	.innerJoin(ENTERPRISE_CCC).onKey()
        	.where(condition)
        	.fetchAnyInto(WORKPLACE));
    }

    private static  Optional<RaddressRecord> getEnterpriseAddress(DSLContext dslContext,  Condition condition) {
	
	return dslContext
	.select()
	.from(RADDRESS)
	.innerJoin(REGISTRY).onKey()
	.innerJoin(ENTERPRISE).onKey()
	.innerJoin(ENTERPRISE_CCC).onKey()
	.where(condition)
	.limit(1)
	.fetchOptionalInto(RADDRESS)
	;
    }

    private static  Optional<EnterpriseCccRecord> getEnterpriseCCC(DSLContext dslContext,  Condition condition) {
	
	return dslContext
	.select()
	.from(ENTERPRISE_CCC)
	.innerJoin(ENTERPRISE_ACTIVITY).onKey()
	.innerJoin(ENTERPRISE).onKey()
	.innerJoin(REGISTRY).onKey()
	.innerJoin(DOMAIN).onKey(Keys.FK_REGISTRY_DOMAIN)
	.leftJoin(PARENT_DOMAIN).onKey(Keys.FK_DOMAIN_PARENT)
	.where(condition)
	.fetchOptionalInto(ENTERPRISE_CCC)
	;
    }
    
    private static  Optional<EnterpriseActivityRecord> getEnterpriseActivity(DSLContext dslContext,  Condition condition) {
	
	return dslContext
	.select()
	.from(ENTERPRISE_ACTIVITY)
	.innerJoin(CNAE2009).onKey()
	.where(condition)
	.fetchOptionalInto(ENTERPRISE_ACTIVITY)
	;
    }

    private static Optional<EnterpriseRecord> getEnterprise(DSLContext dslContext, Condition condition) {
	return 
	dslContext
	.select()
	.from(ENTERPRISE)
	.leftJoin(DOMAIN).onKey()
	.where(condition)
	.fetchOptionalInto(ENTERPRISE);
	
    }

    private static Optional<GeozoneRecord> getGeozone(DSLContext dslContext, Condition condition) {
	return 
	dslContext
	.select()
	.from(GEOZONE)
	.leftJoin(DOMAIN).onKey()
	.where(condition)
	.fetchOptionalInto(GEOZONE);
	
    }
    
    private static Optional<Cnae2009Record> getCnae2009(DSLContext dslContext, Condition condition) {
	return 
	dslContext
	.select()
	.from(CNAE2009)
	.where(condition)
	.fetchOptionalInto(CNAE2009);
	
    }
    
    private static Optional<ScopeRecord> getScope(DSLContext dslContext, Condition condition) {
	return 
	dslContext
	.select()
	.from(SCOPE)
	.where(condition)
	.fetchOptionalInto(SCOPE);
	
    }

    private static  byte geDocumentType(String docType) {
	switch (docType) {
	case "1": // NIF
	    return (byte) 0;
	case "2": // Passport
	    return (byte) 3;
	case "6": // NIE
	    return (byte) 2;
	case "9": // CIF
	    return (byte) 1;
	default:
	    return (byte) 6;
	}
    }
}
