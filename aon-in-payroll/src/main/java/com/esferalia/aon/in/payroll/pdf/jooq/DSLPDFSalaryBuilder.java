package com.esferalia.aon.in.payroll.pdf.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.Update;
import org.jooq.UpdateConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFException;
import com.esferalia.aon.in.payroll.pdf.template.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.utils.Utils;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.PayrollWorkplaceRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.jooq.DSLSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class  DSLPDFSalaryBuilder extends CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> {
	
	
	public static interface PDFFilter {
		void accept(PDFContract contract) throws SalaryPDFException;
	}
	

	private static final class PDFSalaryBuilder extends SalaryBuilder {
		@Override
		public void setContract(Object o) {
			SQLSalaryProxy proxy = (SQLSalaryProxy) o;
			salary.setDomain(proxy.getDomainId());
			Contract contract = new Contract();
			contract.setId(proxy.getContractId());
			salary.setContract(contract);
			super.setContract(o);
		}
	}

	String creationUser;
	byte enableHeredity;
	String parentDomainName;
	String domainNamePreffix; 
	
	PDFFilter filter;
	private int deleted;
	private int inserted;
	
	int deleteMark;
	Collection<Update<?>> mark4DeleteUpdates;
	
	Map<Integer, PDFContract> pdfContractsMap ;


	@SuppressWarnings("unchecked")
	public DSLPDFSalaryBuilder() {
		super(new LazySalaryBuilder<DSLSalaryBuilder<Salary>, Salary>(new DSLSalaryBuilder<>()), new PDFSalaryBuilder());
		
		
		this.filter = p -> {};
		this.enableHeredity = 1;
		
		this.creationUser = "";
		this.domainNamePreffix = "";

		this.pdfContractsMap = new HashMap<>();

		this.deleteMark = new Random().nextInt();
		this.mark4DeleteUpdates = new LinkedList<>();
		
	}
	
	public int getDeleted() {
		return deleted;
	}
	
	public int getInserted() {
		return inserted;
	}
	
	public DSLPDFSalaryBuilder setFilter(PDFFilter filter) {
		this.filter = filter;
		return this;
	}
	
	@Override
	public void setContract(Object contract) {
		filter.accept((PDFContract)contract);
		
		int contractId = -1 * pdfContractsMap.size();
		pdfContractsMap.put(contractId, (PDFContract) contract);
		
		super.setContract(new SQLSalaryProxy(contractId, contractId));
	}
	
	
	@Override
	public Salary getSalary() {
		Salary salary = super.getSalary();
		mark4Delete(salary);
		getLazySalaryBuilder().call();
		return salary;
	}
	
	
	
	
	public void execute(DSLContext dslContext, String parentDomainName) {
		setParentDomainName(parentDomainName);
		setContracts(dslContext);
		mark4Delete(dslContext);
		deleted = delete(dslContext);
		info("%d nóminas eliminadas.", deleted);
		inserted = getDSLSalaryBuilder().execute(dslContext);
		info("%d nóminas traspasadas.", inserted);
	}
	
	// ------------------------------------------------------------------------

	
	protected void info(String format, Object ...args) {
		System.out.print("INFO: ");
		System.out.printf(format,args);
		System.out.println("");
	}
	
	protected void setParentDomainName(String parentDomainName) {
		this.parentDomainName = parentDomainName;
	}
	
	// ------------------------------------------------------------------------
	
	private void setContracts(DSLContext dslContext) {
		pdfContractsMap.forEach((contractId, pdfContract) -> {
			SQLSalaryProxy salaryProxy = 
			getSQLSalaryProxy(dslContext, pdfContract).orElseGet(() -> newSQLSalaryProxy(dslContext, pdfContract));
			setContract( contractId, salaryProxy, pdfContract);
		});
	}
	
	
	private void setContract(int contractId, SQLSalaryProxy salaryProxy,PDFContract pdfContract) {
		SalaryRecord salaryRecord = 
		getDSLSalaryBuilder().getSalaryRecord(r -> AonNumberUtils.equals(r.getContract(), contractId));
		
		if ( salaryRecord == null )
			salaryRecord = 
			getDSLSalaryBuilder().getSalaryRecord(r -> AonStringUtils.equals(r.getEmployeeDocument(), pdfContract.getNif()));
	
		if ( salaryRecord == null )
			salaryRecord = 
			getDSLSalaryBuilder().getSalaryRecord(r -> AonStringUtils.equals(r.getSocialSecurityNumber(), pdfContract.getNaf()));

		salaryRecord.setDomain(salaryProxy.getDomainId());
		salaryRecord.setContract(salaryProxy.getContractId());
	}
	

	private Optional<SQLSalaryProxy> getSQLSalaryProxy(DSLContext dslContext, PDFContract contract) {
		try {
		return 
		dslContext
		.select(CONTRACT.ID, DOMAIN.ID)
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.innerJoin(DOMAIN).on(CONTRACT.DOMAIN.eq(DOMAIN.ID))
		.where(ENTERPRISE_CCC.CCC.equalIgnoreCase(contract.getCcc()))
		.and(PERSON.SOCIAL_SECURITY_NUM.equalIgnoreCase(contract.getNaf()))
		.and(CONTRACT.START_DATE.le(toSqlDate(contract.getEndDate())))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(toSqlDate(contract.getStartDate()))))
		.fetchOptional()
		.map(r -> { 
			
			info("%s", getMessage(contract));

			int contractId = r.get(CONTRACT.ID);
			int domainId = r.get(DOMAIN.ID);			
			return new SQLSalaryProxy(contractId, domainId);
			
		});
		} catch ( TooManyRowsException e ) {
			System.err.printf("ERROR: [DUPLICATE EMPLOYEE] %s\r\n", getMessage(contract));
			return Optional.empty();
			//throw new SalaryPDFException("ERROR: [DUPLICATE EMPLOYEE] %s", getMessage(contract));
		}
	}
	
	private SQLSalaryProxy newSQLSalaryProxy(DSLContext dslContext, PDFContract pdfContract) {
		
		Salary salary = super.getSalary();
		
		EnterpriseRecord enterprise = getEnterprise(dslContext, pdfContract)
		.orElseGet(() -> newEnterprise(dslContext, pdfContract));
		
		EnterpriseCccRecord enterpriseCcc = getEnterpriseCCC(dslContext, pdfContract)
		.orElseGet(() -> newEnterpriseCCC(dslContext, pdfContract, enterprise));
		
		
		RegistryRecord person = getPerson(dslContext, pdfContract, enterpriseCcc.getDomain())
		.orElseGet(() -> newPerson(dslContext, pdfContract,  enterpriseCcc.getDomain()));
		
		PayrollWorkplaceRecord workplace = getWorkplace(dslContext, pdfContract, enterpriseCcc.getDomain())
		.orElseGet(() -> newWorkplace(dslContext, pdfContract, enterprise, enterpriseCcc, salary));
		
		ContractRecord contract = newContract(dslContext, enterpriseCcc, person, workplace, salary);
		
		System.out.printf("WARN: [EMPLOYEE CREATED] %s,\"%2$td/%2$tm/%2$tY\",\"%3$td/%3$tm/%3$tY\"\r\n", getMessage(pdfContract), contract.getStartDate(), contract.getEndDate());

		return new SQLSalaryProxy(contract.getId(), contract.getDomain());
		
		
		//throw new SalaryPDFException("Error: No employee, %s",getMessage(pdfContract));
	}
	
	
	private Optional<RegistryRecord> getPerson(DSLContext dslContext,PDFContract contract, int domainId) {
		return dslContext
		.select()
		.from(REGISTRY)
		.innerJoin(PERSON).onKey()
		.where(REGISTRY.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.eq(contract.getNif()))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(contract.getNaf()))
		.limit(1)
		.fetchOptionalInto(REGISTRY);
	}
	
	private RegistryRecord newPerson(DSLContext dslContext,PDFContract contract, int domainId) {
		
		RegistryRecord registry = 
		dslContext
		.select()
		.from(REGISTRY)
		.where(REGISTRY.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.eq(contract.getNif()))
		.limit(1).fetchOptionalInto(REGISTRY)
		.orElseGet(() -> { 
			String nationality =  "ES";
			Byte documentType = Utils.getType(contract.getNif());
//			if ( documentType == Utils.DNI 
//				||documentType == Utils.NIF
//				||documentType == Utils.CIF)
//				nationality = "ES";
			
			RegistryRecord r = 
			dslContext.newRecord(REGISTRY);			
			r.setDomain(domainId);
			r.setNationality(nationality);
			r.setDocument(contract.getNif());
			r.setDocumentCountry(nationality);
			r.setDocumentType(documentType);			
			r.setName(contract.getEmployeeName());
			r.setType(type(RegistryType.NATURAL));
			r.setAlias(String.format("%s-%s",contract.getEnterpriseCode(), contract.getEmployeeCode()));
			r.insert();
			return r;
		});
		

		String names [] = Utils.split(contract.getEmployeeName());
		
		try {
			PersonRecord person = 
			dslContext.newRecord(PERSON);			
			person.setDomain(domainId);
			person.setRegistry(registry.getId());
			person.setName(names[0]);
			person.setFirstSurname(names[1]);
			person.setSecondSurname(names[2]);
			person.setSocialSecurityNum(contract.getNaf());
			dslContext
			.insertInto(PERSON)
			.set(person)
			.execute()
			;
		}
		catch ( DataAccessException e) {
			System.err.println(e.getMessage());
			throw new SalaryPDFException("ERROR: [UNEXPECTED NAF] %s", getMessage(contract));
		}
		
		return registry;
		
	}
	
	private Optional<EnterpriseRecord> getEnterprise(DSLContext dslContext, PDFContract contract) {
		try {
			return dslContext
			.select()
			.from(ENTERPRISE)
			.innerJoin(REGISTRY).onKey()
			.innerJoin(DOMAIN).onKey(REGISTRY.DOMAIN)
			.where(REGISTRY.DOCUMENT.eq(contract.getCif()))
			.and(DOMAIN.PARENT.eq(getParentDomain(dslContext).getId()))
			.fetchOptionalInto(ENTERPRISE)
			;
		} catch ( TooManyRowsException  e) {
			throw new SalaryPDFException("ERROR: [DUPLICATE ENTERPRISE] %s", getMessage(contract));
		}
	}
	
	private EnterpriseRecord newEnterprise(DSLContext dslContext, PDFContract contract) {
		try {
			return
			dslContext
			.select()
			.from(ENTERPRISE)
			.innerJoin(REGISTRY).onKey()
			.innerJoin(DOMAIN).onKey(REGISTRY.DOMAIN)
			.where(REGISTRY.DOCUMENT.eq(contract.getCif()))
			.and(DOMAIN.PARENT.eq(getParentDomain(dslContext).getId()))
			.fetchOptionalInto(ENTERPRISE)
			.orElseGet(() -> {
				
				DomainRecord parentDomain = getParentDomain(dslContext);
								
				DomainRecord domain = 
				dslContext.newRecord(DOMAIN);
				domain.setCreationUser(creationUser);
				domain.setParent(parentDomain.getId());
				domain.setOwner(parentDomain.getOwner());
				domain.setEnableheredity(enableHeredity);
				domain.setScope(parentDomain.getScope());
				domain.setDescription(contract.getEnterpriseName());
				domain.setCreationDate(new Timestamp(System.currentTimeMillis()));
				domain.setName(String.format("%s%s-%s", domainNamePreffix, contract.getCif(), parentDomainName));
				domain.insert();

				String nationality =  "ES";
				Byte documentType = Utils.getType(contract.getCif());

				RegistryRecord r3gistry = 
				dslContext.newRecord(REGISTRY);			
				r3gistry.setType(type(RegistryType.LEGAL));
				r3gistry.setDomain(domain.getId());
				r3gistry.setNationality(nationality);
				r3gistry.setDocument(contract.getCif());
				r3gistry.setDocumentCountry(nationality);
				r3gistry.setDocumentType(documentType);			
				r3gistry.setName(contract.getEnterpriseName());
				r3gistry.setAlias(contract.getEnterpriseCode());
				r3gistry.insert();	
								
				EnterpriseRecord enterprise = 
				dslContext.newRecord(ENTERPRISE);
				enterprise.setDomain(domain.getId());
				enterprise.setRegistry(r3gistry.getId());
				enterprise.setScope(parentDomain.getScope());
				enterprise.insert();
				
				return enterprise;
				
				
			});
			
		
		} catch ( TooManyRowsException  e) {
			throw new SalaryPDFException("ERROR: [DUPLICATE ENTERPRISE] %s", getMessage(contract));
		}
	}

	private Optional<EnterpriseCccRecord> getEnterpriseCCC(DSLContext dslContext, PDFContract contract) {
		return dslContext
		.select()
		.from(ENTERPRISE_CCC)
		.innerJoin(ENTERPRISE_ACTIVITY).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(ENTERPRISE_CCC.CCC.eq(contract.getCcc()))
		.and(REGISTRY.DOCUMENT.eq(contract.getCif()))
		.orderBy(ENTERPRISE_CCC.ID).limit(1).fetchOptionalInto(ENTERPRISE_CCC)
		;
	}
	
	private EnterpriseCccRecord newEnterpriseCCC(DSLContext dslContext, PDFContract contract, EnterpriseRecord enterprise) {
		
		
		EnterpriseActivityRecord enterpriseActivity =
		dslContext
		.select()
		.from(ENTERPRISE_ACTIVITY)
		.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterprise.getRegistry()))
		.orderBy(ENTERPRISE_ACTIVITY.ID).limit(1).fetchOptionalInto(ENTERPRISE_ACTIVITY)
		.orElseGet(() -> {
			
			EnterpriseActivityRecord activity =
			dslContext
			.newRecord(ENTERPRISE_ACTIVITY);
			activity.setPrincipal((byte) 1);
			activity.setDomain(enterprise.getDomain());
			activity.setEnterprise(enterprise.getRegistry());
			activity.setDescription(contract.getEnterpriseName());
			activity.setType(type(EnterpriseActivityType.PRINCIPAL));
			activity.insert();
			
			return activity;
			
		});
		
		EnterpriseCccRecord enterpriseCcc =
		dslContext
		.newRecord(ENTERPRISE_CCC);
		enterpriseCcc.setType(type(CCCType.PRINCIPAL)); 
		enterpriseCcc.setDomain(enterprise.getDomain());
		enterpriseCcc.setCcc(contract.getCcc());
		enterpriseCcc.setEnterpriseActivity(enterpriseActivity.getId());
		enterpriseCcc.insert();
		
		return enterpriseCcc;
	}
	
	private Optional<PayrollWorkplaceRecord> getWorkplace(DSLContext dslContext, PDFContract contract, int domainId) {
		return dslContext
		.select()
		.from(PAYROLL_WORKPLACE)
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.eq(contract.getCif()))
		.orderBy(PAYROLL_WORKPLACE.ID).limit(1).fetchOptionalInto(PAYROLL_WORKPLACE) ;
	}
	
	private PayrollWorkplaceRecord newWorkplace(DSLContext dslContext, PDFContract contract, EnterpriseRecord enterprise, EnterpriseCccRecord enterpriseCcc, Salary salary) {
		
		RaddressRecord raddress =
		dslContext
		.select()
		.from(RADDRESS)
		.where(RADDRESS.REGISTRY.eq(enterprise.getRegistry()))
		.orderBy(RADDRESS.ID).limit(1).fetchOptionalInto(RADDRESS)
		.orElseGet(() -> {
			
			RaddressRecord raddr3ss = dslContext.newRecord(RADDRESS);
			raddr3ss.setType(type(AddressType.MAIN));
			raddr3ss.setRegistry(enterprise.getRegistry());
			raddr3ss.setDomain(enterprise.getDomain());
			raddr3ss.setAddress(AonStringUtils.abbreviate(salary.getEnterpriseAddress(),128));
			raddr3ss.insert();
			
			return raddr3ss;

		});		

		WorkplaceRecord workplace = 
		dslContext
		.select()
		.from(WORKPLACE)
		.where(WORKPLACE.ENTERPRISE.eq(enterprise.getRegistry()))
		.orderBy(WORKPLACE.ID).limit(1).fetchOptionalInto(WORKPLACE)
		.orElseGet(() ->{
			
			WorkplaceRecord workplac3 =
			dslContext.newRecord(WORKPLACE);
			workplac3.setDescription("PRINCIPAL");
			workplac3.setDomain(enterprise.getDomain());
			workplac3.setEnterprise(enterprise.getRegistry());
			workplac3.setScope(enterprise.getScope());
			workplac3.setAddress(raddress.getId());
			workplac3.insert();
			
			return workplac3;

		});
		
		
		PayrollWorkplaceRecord payrollWorkplace =
		dslContext.newRecord(PAYROLL_WORKPLACE);
		payrollWorkplace.setWorkplace(workplace.getId());
		payrollWorkplace.setDomain(enterprise.getDomain());		
		payrollWorkplace.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
		//workplace.setAddress(value);
		workplace.insert();
		
		return payrollWorkplace;
	}
	
	private ContractRecord newContract(DSLContext dslContext, EnterpriseCccRecord enterpriseCcc, RegistryRecord person, PayrollWorkplaceRecord workplace, Salary salary ) {
		
		ContractRecord contract  =
		dslContext.newRecord(CONTRACT);		
		contract.setDomain(enterpriseCcc.getDomain());
		contract.setPerson(person.getId());
		contract.setWorkplace(workplace.getWorkplace());
		contract.setEnterpriseCcc(enterpriseCcc.getId());
		contract.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
//		contract.setStartDate(toSqlDate(salary.getSeniorityDate()));
//		if ( !isLastDayOfMonth(salary.getEndDate()) )
//			contract.setEndDate(toSqlDate(salary.getEndDate()));
		contract.setStartDate(getContractStartDate(dslContext, salary));
		contract.setEndDate(getContractEndDate(dslContext, salary));
		contract.setSeniorityDate(toSqlDate(salary.getSeniorityDate()));
		contract.setRegistration(salary.getRegistration());
		contract.setCategoryDescription(salary.getCategory());
		contract.setSsRegime(type(SSRegimeType.GENERAL));
		contract.insert();
		
		ContractDataRecord quoteGroupData = 
		dslContext.newRecord(CONTRACT_DATA);
		quoteGroupData.setDomain(contract.getDomain());
		quoteGroupData.setContract(contract.getId());
		quoteGroupData.setName(ContextVariable.QUOTE_GROUP.getName());
		quoteGroupData.setStartDate(contract.getStartDate());
		quoteGroupData.setEndDate(contract.getEndDate());
		quoteGroupData.setExpression(String.format("\"%s\"", salary.getQuoteGroup()));
		quoteGroupData.insert();
		
		return contract;
		
	}
	
	private java.sql.Date getContractStartDate(DSLContext dslContext, Salary salary) {
		return 
		dslContext
		.select(
		DSL.max(CONTRACT.END_DATE))
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(ENTERPRISE_CCC.CCC.equalIgnoreCase(salary.getCcc()))
		.and(PERSON.SOCIAL_SECURITY_NUM.equalIgnoreCase(salary.getSocialSecurityNumber()))
		.and(CONTRACT.END_DATE.lt(toSqlDate(salary.getStartDate())))
		.and(CONTRACT.END_DATE.gt(toSqlDate(salary.getSeniorityDate())))
		.fetchOptional(DSL.max(CONTRACT.END_DATE))
		.map( d -> AonDateUtils.add(d, Calendar.DAY_OF_MONTH, 1))
		.orElse(toSqlDate(salary.getSeniorityDate()));		
	}
	
	private java.sql.Date getContractEndDate(DSLContext dslContext, Salary salary) {
		
		if ( !isLastDayOfMonth(salary.getEndDate()))
			return toSqlDate(salary.getEndDate());
		
		return 
		dslContext
		.select(
		DSL.min(CONTRACT.START_DATE))
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(ENTERPRISE_CCC.CCC.equalIgnoreCase(salary.getCcc()))
		.and(PERSON.SOCIAL_SECURITY_NUM.equalIgnoreCase(salary.getSocialSecurityNumber()))
		.and(CONTRACT.START_DATE.gt(toSqlDate(salary.getEndDate())))
		.fetchOptional(DSL.min(CONTRACT.START_DATE))
		.map( d -> AonDateUtils.add(d, Calendar.DAY_OF_MONTH, -1))
		.orElse(null);		
	}
	
	private DomainRecord getParentDomain(DSLContext dslContext) {
		return dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq(parentDomainName)).fetchOneInto(DOMAIN);	
	}

	private String getMessage(PDFContract contract) {
		return String.format(
		"\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%6$td/%6$tm/%6$tY\",\"%7$td/%7$tm/%7$tY\"",
		contract.getCcc(),
		contract.getNaf(),
		contract.getNif(),
		contract.getEmployeeName(),
		contract.getEnterpriseName(),
		contract.getStartDate(),
		contract.getEndDate());

	}

	private void mark4Delete(Salary salary) {
		UpdateConditionStep<SalaryRecord> mark4DeleteUpdate = 
		DSL
		.update(SALARY)
		.set(SALARY.REGISTRATION, deleteMark)
		.where(SALARY.EMPLOYEE_DOCUMENT.eq(salary.getEmployeeDocument()))
		.and(SALARY.CCC.eq(salary.getCcc()))
		.and(SALARY.START_DATE.eq(toSqlDate(salary.getStartDate())))
		.and(SALARY.END_DATE.eq(toSqlDate(salary.getEndDate())))
		;
		
		mark4DeleteUpdates.add(mark4DeleteUpdate);
	}
	
	private void mark4Delete(DSLContext dslContext) {
		mark4DeleteUpdates.forEach(dslContext::execute);
	}
	private int  delete(DSLContext dslContext) {
		
		dslContext.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		dslContext.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		dslContext.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		dslContext.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		dslContext.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		dslContext.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		return dslContext.delete(SALARY).where(SALARY.REGISTRATION.eq(deleteMark)).execute();

	}

	private DSLSalaryBuilder<Salary> getDSLSalaryBuilder(){
		return getLazySalaryBuilder().getSalaryBuilder();
	}
	
	private LazySalaryBuilder<DSLSalaryBuilder<Salary>, Salary> getLazySalaryBuilder(){
		return (LazySalaryBuilder<DSLSalaryBuilder<Salary>, Salary>) getBuilders()[0];
	}
	
	

	protected static boolean isLastDayOfMonth(Date date) {
		return getMax(date, DAY_OF_MONTH) == get(date, DAY_OF_MONTH);
	} 
	
	protected static java.sql.Date toSqlDate(Date date) {
		return date == null ? null : new java.sql.Date(date.getTime());
	}
	
	protected static <E extends Enum<?>> byte type(E constant) {
		return (byte) constant.ordinal();
	}
	
	protected static <T> List<T> merge(List<T> l1, List<T> l2) {
		List<T> l = new LinkedList<>();
		l.addAll(l1);
		l.addAll(l2);
		return l;
	}
} 
