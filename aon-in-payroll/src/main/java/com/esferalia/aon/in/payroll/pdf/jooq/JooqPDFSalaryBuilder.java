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
import java.util.Date;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.RegistryType;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFBuilder;
import com.esferalia.aon.in.payroll.pdf.SalaryPDFException;
import com.esferalia.aon.in.payroll.pdf.templates.AltaiPDFTemplate.PDFContract;
import com.esferalia.aon.in.payroll.utils.Utils;
import com.esferalia.aon.jooq.tables.SalaryBonus;
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
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.CompositeSalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class  JooqPDFSalaryBuilder extends CompositeSalaryBuilder<Salary, ISalaryBuilder<Salary>> implements SalaryPDFBuilder<Salary>  {
	
	
	private static final Date ISSUE_DATE = new Date(System.currentTimeMillis());
	
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
	DomainRecord parentDomain;
	
	int deleteMark;
	
	PDFFilter filter;
	private int deleted;
	private int inserted;
	

	@SuppressWarnings("unchecked")
	public JooqPDFSalaryBuilder(DSLContext dslContext, String parentDomainName) {
		super(new LazySalaryBuilder<JooqSalaryBuilder<Salary>, Salary>(new JooqSalaryBuilder<Salary>(dslContext)), new PDFSalaryBuilder());
		
		
		this.filter = (p) -> {};
		this.enableHeredity = 1;
		this.creationUser = "altai2aon";
		this.domainNamePreffix = "altai";
		this.parentDomainName = parentDomainName;
		this.parentDomain = getParentDomain(parentDomainName);
		this.deleteMark = (int) (Math.random() * Integer.MAX_VALUE );
	}
	
	public int getDeleted() {
		return deleted;
	}
	
	public int getInserted() {
		return inserted;
	}
	
	public JooqPDFSalaryBuilder setFilter(PDFFilter filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public void createNewSalary() {
		super.createNewSalary();
	}
	
	@Override
	public void setContract(Object contract) {
		filter.accept((PDFContract)contract);
		getSQLSalaryProxy((PDFContract)contract)
		.ifPresentOrElse(
		(s) -> {
			super.setContract(s); 
		},
		() ->  {
			SQLSalaryProxy s = newSQLSalaryProxy((PDFContract)contract);
			super.setContract(s); 
		});
		;
	}
	
	
	@Override
	public Salary getSalary() {
		Salary salary = super.getSalary();
		mark4Delete(salary);
		getLazySalaryBuilder().call();
		return salary;
	}
	
	
	public void execute() {
		deleted = delete();
		info("%d nóminas eliminadas.", deleted);
		inserted = getJooqSalaryBuilder().execute();
		info("%d nóminas traspasadas.", inserted);
	}
	
	// ------------------------------------------------------------------------

	
	protected void info(String format, Object ...args) {
		System.out.print("INFO: ");
		System.out.printf(format,args);
		System.out.println("");
	}
	
	// ------------------------------------------------------------------------
	

	private DomainRecord getParentDomain(String parentDomainName ) {
		return getDSLContext().select().from(DOMAIN).where(DOMAIN.NAME.eq(parentDomainName)).fetchOneInto(DOMAIN);	
	}
	
	
	private Optional<SQLSalaryProxy> getSQLSalaryProxy(PDFContract contract) {
		try {
		return 
		getDSLContext()
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
			throw new SalaryPDFException("ERROR: [DUPLICATE EMPLOYEE] %s", getMessage(contract));
		}
	}
	
	private SQLSalaryProxy newSQLSalaryProxy(PDFContract pdfContract) {
		
		Salary salary = super.getSalary();
		
		EnterpriseRecord enterprise = getEnterprise(pdfContract)
		.orElseGet(() -> newEnterprise(pdfContract));
		
		EnterpriseCccRecord enterpriseCcc = getEnterpriseCCC(pdfContract)
		.orElseGet(() -> newEnterpriseCCC(pdfContract, enterprise));
		
		
		RegistryRecord person = getPerson(pdfContract, enterpriseCcc.getDomain())
		.orElseGet(() -> newPerson(pdfContract,  enterpriseCcc.getDomain()));
		
		PayrollWorkplaceRecord workplace = getWorkplace(pdfContract, enterpriseCcc.getDomain())
		.orElseGet(() -> newWorkplace(pdfContract, enterprise, enterpriseCcc, salary));
		
		ContractRecord contract = newContract(enterpriseCcc, person, workplace, salary);
		
		System.out.printf("WARN: [EMPLOYEE CREATED] %s,\"%2$td/%2$tm/%2$tY\",\"%3$td/%3$tm/%3$tY\"\r\n", getMessage(pdfContract), contract.getStartDate(), contract.getEndDate());

		return new SQLSalaryProxy(contract.getId(), contract.getDomain());
		
		
		//throw new SalaryPDFException("Error: No employee, %s",getMessage(pdfContract));
	}
	
	
	private Optional<RegistryRecord> getPerson(PDFContract contract, int domainId) {
		return getDSLContext()
		.select()
		.from(REGISTRY)
		.innerJoin(PERSON).onKey()
		.where(REGISTRY.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.eq(contract.getNif()))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(contract.getNaf()))
		.limit(1)
		.fetchOptionalInto(REGISTRY);
	}
	
	private RegistryRecord newPerson(PDFContract contract, int domainId) {
		
		RegistryRecord registry = 
		getDSLContext()
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
			getDSLContext().newRecord(REGISTRY);			
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
			getDSLContext().newRecord(PERSON);			
			person.setDomain(domainId);
			person.setRegistry(registry.getId());
			person.setName(names[0]);
			person.setFirstSurname(names[1]);
			person.setSecondSurname(names[2]);
			person.setSocialSecurityNum(contract.getNaf());
			getDSLContext()
			.insertInto(PERSON)
			.set(person)
			.execute()
			;
		}
		catch ( DataAccessException e) {
			throw new SalaryPDFException("ERROR: [UNEXPECTED NAF] %s", getMessage(contract));
		}
		
		return registry;
		
	}
	
	private Optional<EnterpriseRecord> getEnterprise(PDFContract contract) {
		try {
			return getDSLContext()
			.select()
			.from(ENTERPRISE)
			.innerJoin(REGISTRY).onKey()
			.innerJoin(DOMAIN).onKey(REGISTRY.DOMAIN)
			.where(REGISTRY.DOCUMENT.eq(contract.getCif()))
			.and(DOMAIN.PARENT.eq(parentDomain.getId()))
			.fetchOptionalInto(ENTERPRISE)
			;
		} catch ( TooManyRowsException  e) {
			throw new SalaryPDFException("ERROR: [DUPLICATE ENTERPRISE] %s", getMessage(contract));
		}
	}
	
	private EnterpriseRecord newEnterprise(PDFContract contract) {
		try {
			return
			getDSLContext()
			.select()
			.from(ENTERPRISE)
			.innerJoin(REGISTRY).onKey()
			.where(REGISTRY.DOCUMENT.eq(contract.getCif()))
			.fetchOptionalInto(ENTERPRISE)
			.orElseGet(() -> {
								
				DomainRecord domain = 
				getDSLContext().newRecord(DOMAIN);
				domain.setCreationUser(creationUser);
				domain.setParent(parentDomain.getId());
				domain.setOwner(parentDomain.getOwner());
				domain.setEnableheredity(enableHeredity);
				domain.setScope(3535/*parentDomain.getScope()*/);
				domain.setDescription(contract.getEnterpriseName());
				domain.setCreationDate(new Timestamp(System.currentTimeMillis()));
				domain.setName(String.format("%s-%s-%s", domainNamePreffix, contract.getCif(), parentDomainName));
				domain.insert();

				String nationality =  "ES";
				Byte documentType = Utils.getType(contract.getCif());

				RegistryRecord r3gistry = 
				getDSLContext().newRecord(REGISTRY);			
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
				getDSLContext().newRecord(ENTERPRISE);
				enterprise.setDomain(domain.getId());
				enterprise.setRegistry(r3gistry.getId());
				enterprise.setScope(3535/*parentDomain.getScope()*/);
				enterprise.insert();
				
				return enterprise;
				
				
			});
			
		
		} catch ( TooManyRowsException  e) {
			throw new SalaryPDFException("ERROR: [DUPLICATE ENTERPRISE] %s", getMessage(contract));
		}
	}

	private Optional<EnterpriseCccRecord> getEnterpriseCCC(PDFContract contract) {
		return getDSLContext()
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
	
	private EnterpriseCccRecord newEnterpriseCCC(PDFContract contract, EnterpriseRecord enterprise) {
		
		
		EnterpriseActivityRecord enterpriseActivity =
		getDSLContext()
		.select()
		.from(ENTERPRISE_ACTIVITY)
		.where(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(enterprise.getRegistry()))
		.orderBy(ENTERPRISE_ACTIVITY.ID).limit(1).fetchOptionalInto(ENTERPRISE_ACTIVITY)
		.orElseGet(() -> {
			
			EnterpriseActivityRecord activity =
			getDSLContext()
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
		getDSLContext()
		.newRecord(ENTERPRISE_CCC);
		enterpriseCcc.setType(type(CCCType.PRINCIPAL)); 
		enterpriseCcc.setDomain(enterprise.getDomain());
		enterpriseCcc.setCcc(contract.getCcc());
		enterpriseCcc.setEnterpriseActivity(enterpriseActivity.getId());
		enterpriseCcc.insert();
		
		return enterpriseCcc;
	}
	
	private Optional<PayrollWorkplaceRecord> getWorkplace(PDFContract contract, int domainId) {
		return getDSLContext()
		.select()
		.from(PAYROLL_WORKPLACE)
		.innerJoin(WORKPLACE).onKey()
		.innerJoin(ENTERPRISE).onKey()
		.innerJoin(REGISTRY).onKey()
		.where(PAYROLL_WORKPLACE.DOMAIN.eq(domainId))
		.and(REGISTRY.DOCUMENT.eq(contract.getCif()))
		.orderBy(PAYROLL_WORKPLACE.ID).limit(1).fetchOptionalInto(PAYROLL_WORKPLACE) ;
	}
	
	private PayrollWorkplaceRecord newWorkplace(PDFContract contract, EnterpriseRecord enterprise, EnterpriseCccRecord enterpriseCcc, Salary salary) {
		
		RaddressRecord raddress =
		getDSLContext()
		.select()
		.from(RADDRESS)
		.where(RADDRESS.REGISTRY.eq(enterprise.getRegistry()))
		.orderBy(RADDRESS.ID).limit(1).fetchOptionalInto(RADDRESS)
		.orElseGet(() -> {
			
			RaddressRecord raddr3ss = getDSLContext().newRecord(RADDRESS);
			raddr3ss.setType(type(AddressType.MAIN));
			raddr3ss.setRegistry(enterprise.getRegistry());
			raddr3ss.setDomain(enterprise.getDomain());
			raddr3ss.setAddress(AonStringUtils.abbreviate(salary.getEnterpriseAddress(),128));
			raddr3ss.insert();
			
			return raddr3ss;

		});		

		WorkplaceRecord workplace = 
		getDSLContext()
		.select()
		.from(WORKPLACE)
		.where(WORKPLACE.ENTERPRISE.eq(enterprise.getRegistry()))
		.orderBy(WORKPLACE.ID).limit(1).fetchOptionalInto(WORKPLACE)
		.orElseGet(() ->{
			
			WorkplaceRecord workplac3 =
			getDSLContext().newRecord(WORKPLACE);
			workplac3.setDescription("PRINCIPAL");
			workplac3.setDomain(enterprise.getDomain());
			workplac3.setEnterprise(enterprise.getRegistry());
			workplac3.setScope(enterprise.getScope());
			workplac3.setAddress(raddress.getId());
			workplac3.insert();
			
			return workplac3;

		});
		
		
		PayrollWorkplaceRecord payrollWorkplace =
		getDSLContext().newRecord(PAYROLL_WORKPLACE);
		payrollWorkplace.setWorkplace(workplace.getId());
		payrollWorkplace.setDomain(enterprise.getDomain());		
		payrollWorkplace.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
		//workplace.setAddress(value);
		workplace.insert();
		
		return payrollWorkplace;
	}
	
	private ContractRecord newContract(EnterpriseCccRecord enterpriseCcc, RegistryRecord person, PayrollWorkplaceRecord workplace, Salary salary ) {
		
		ContractRecord contract  =
		getDSLContext().newRecord(CONTRACT);		
		contract.setDomain(enterpriseCcc.getDomain());
		contract.setPerson(person.getId());
		contract.setWorkplace(workplace.getWorkplace());
		contract.setEnterpriseCcc(enterpriseCcc.getId());
		contract.setEnterpriseActivity(enterpriseCcc.getEnterpriseActivity());
//		contract.setStartDate(toSqlDate(salary.getSeniorityDate()));
//		if ( !isLastDayOfMonth(salary.getEndDate()) )
//			contract.setEndDate(toSqlDate(salary.getEndDate()));
		contract.setStartDate(getContractStartDate(salary));
		contract.setEndDate(getContractEndDate(salary));
		contract.setSeniorityDate(toSqlDate(salary.getSeniorityDate()));
		contract.setRegistration(salary.getRegistration());
		contract.setCategoryDescription(salary.getCategory());
		contract.setSsRegime(type(SSRegimeType.GENERAL));
		contract.insert();
		
		ContractDataRecord quoteGroupData = 
		getDSLContext().newRecord(CONTRACT_DATA);
		quoteGroupData.setDomain(contract.getDomain());
		quoteGroupData.setContract(contract.getId());
		quoteGroupData.setName(ContextVariable.QUOTE_GROUP.getName());
		quoteGroupData.setStartDate(contract.getStartDate());
		quoteGroupData.setEndDate(contract.getEndDate());
		quoteGroupData.setExpression(String.format("\"%s\"", salary.getQuoteGroup()));
		quoteGroupData.insert();
		
		return contract;
		
	}
	
	
	
	private void mark4Delete(Salary salary) {
		getDSLContext()
		.update(SALARY)
		.set(SALARY.REGISTRATION, deleteMark)
		.where(SALARY.CONTRACT.eq(salary.getContract().getId()))
		.and(SALARY.START_DATE.eq(toSqlDate(salary.getStartDate())))
		.and(SALARY.END_DATE.eq(toSqlDate(salary.getEndDate())))
		.execute()
		;
//		SelectConditionStep<Record1<Integer>> condition = 
//			DSL.select(SALARY.ID).from(SALARY)
//			.where(SALARY.CONTRACT.eq(salary.getContract().getId()))
//			.and(SALARY.START_DATE.eq(toSqlDate(salary.getStartDate())))
//			.and(SALARY.END_DATE.eq(toSqlDate(salary.getEndDate())));
//		
//		getDSLContext().delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(condition)).execute();
//		getDSLContext().delete(SALARY_COST).where(SALARY_COST.SALARY.in(condition)).execute();
//		getDSLContext().delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(condition)).execute();
//		getDSLContext().delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(condition)).execute();
//		getDSLContext().delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(condition)).execute();
//		getDSLContext().delete(SALARY).where(SALARY.ID.in(condition)).execute();
		
	}
	
	private int  delete() {
		SelectConditionStep<Record1<Integer>> condition = 
		DSL.select(SALARY.ID).from(SALARY).where(SALARY.REGISTRATION.eq(deleteMark));
		
		getDSLContext().delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(condition)).execute();
		getDSLContext().delete(SALARY_COST).where(SALARY_COST.SALARY.in(condition)).execute();
		getDSLContext().delete(SALARY_BONUS).where(SALARY_BONUS.SALARY.in(condition)).execute();
		getDSLContext().delete(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.in(condition)).execute();
		getDSLContext().delete(SALARY_EMBARGO).where(SALARY_EMBARGO.SALARY.in(condition)).execute();
		getDSLContext().delete(SALARY_DEDUCTION).where(SALARY_DEDUCTION.SALARY.in(condition)).execute();
		return getDSLContext().delete(SALARY).where(SALARY.REGISTRATION.eq(deleteMark)).execute();
		
	}

	private java.sql.Date getContractStartDate(Salary salary) {
		return 
		getDSLContext()
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
	
	private java.sql.Date getContractEndDate(Salary salary) {
		
		if ( !isLastDayOfMonth(salary.getEndDate()))
			return toSqlDate(salary.getEndDate());
		
		return 
		getDSLContext()
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
	
	
	private DSLContext getDSLContext() {
		return getJooqSalaryBuilder().getDSLContext();
	}
	
	private JooqSalaryBuilder<Salary> getJooqSalaryBuilder(){
		return getLazySalaryBuilder().getSalaryBuilder();
	}
	
	private LazySalaryBuilder<JooqSalaryBuilder<Salary>, Salary> getLazySalaryBuilder(){
		return (LazySalaryBuilder<JooqSalaryBuilder<Salary>, Salary>) getBuilders()[0];
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
}
