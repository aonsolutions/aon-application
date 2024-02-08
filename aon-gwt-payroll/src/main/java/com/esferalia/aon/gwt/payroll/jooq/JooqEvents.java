package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.ast.Function;
import org.mvel2.util.MethodStub;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.client.Quintet;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.VariableDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.gwt.payroll.sql.SQLAgreementDraft;
import com.esferalia.aon.gwt.payroll.sql.SQLEvents;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SystemDataColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEvents {

	private static Settings SETTINGS = null;
	
	public static WorkplaceEmployees getWorkplaceEmployees(Connection conn, Workplace workplace, Integer domainId) {
		return getWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), workplace, domainId);
	}
	
	public static EventsWorkplace setWorkplaceEmployees(Connection conn, EventsWorkplace updateEventsWorkplace) {
		return setWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), updateEventsWorkplace);
	}
	
	public static WorkplaceEmployees getWorkplaceEmployeesEvents(Connection conn, Integer workplaceId) throws IllegalArgumentException {
		return getWorkplaceEmployeesEventsInformation(DSL.using(conn, getDefaultSettings()), workplaceId);
	}
	
	public static ArrayList<EventEmployee> setEventsDraft(Connection connection, ArrayList<EventEmployee> eventEmployees) {
		return setEventsDraftDB(DSL.using(connection, getDefaultSettings()), eventEmployees);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static WorkplaceEmployees getWorkplaceEmployeesEventsInformation(DSLContext dslContext, Integer workplaceId) throws IllegalArgumentException {
		
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		
		System.out.println("Workplace :"+workplaceId);
		
		java.util.Date newDatePreviusMonth = DateUtils.getFirstDayOfMonth(new java.util.Date());
		Date actualDate = new Date(newDatePreviusMonth.getTime());
		
		Result<Record> activeContractsRecords = dslContext.select().from(CONTRACT)
				.where(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(CONTRACT.END_DATE.isNull()
						.or(CONTRACT.END_DATE.ge(actualDate)))
				.orderBy(CONTRACT.ID.desc())
				.fetch();
		
		ArrayList<Integer> existingPerson = new ArrayList<Integer>(); 
		
		for(Record c : activeContractsRecords) {
			
			if(existingPerson.isEmpty() || !existingPerson.contains(c.get(CONTRACT.PERSON))) {
				
				existingPerson.add(c.get(CONTRACT.PERSON));
				
				Record personRecord = dslContext.select().from(PERSON)
						.where(PERSON.REGISTRY.eq(c.get(CONTRACT.PERSON)))
						.fetchOne();
				
				EmployeeInfo employee = new EmployeeInfo();
				
				Result<Record> contractRecords = dslContext.select().from(CONTRACT)
					.where(CONTRACT.PERSON.eq(personRecord.get(PERSON.REGISTRY)))
					.orderBy(CONTRACT.START_DATE.desc())
					.fetch();
				
				Integer contractId = null;
				
				if(null != contractRecords && !contractRecords.isEmpty())
					contractId = contractRecords.get(0).get(CONTRACT.ID);
				
				if(null != contractId) {
					// ----------------------------------- FULL TIME JOURNEY
					
					String journeyTypeEmployee = "";
					
					Result<Record> journeyTypeRecords = dslContext.select()
							  .from(CONTRACT_DATA)
							  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
							  .and(CONTRACT_DATA.NAME.equal("TIEMPO_COMPLETO"))
							  .orderBy(CONTRACT_DATA.START_DATE.desc()) // If there is more than one contract
							  .fetch();
					
					if(!journeyTypeRecords.isEmpty())
						journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
					else {
						journeyTypeRecords = dslContext.select()
								  .from(CONTRACT_DATA)
								  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
								  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
								  .orderBy(CONTRACT_DATA.START_DATE.desc())
								  .fetch();
						
						if(journeyTypeRecords.isEmpty())
							continue;
						else
							journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
					}
					
					employee.setIsFullTime(isFullTimeJourney(journeyTypeEmployee));
				}
				
				Record registryRecord = dslContext.select()
						.from(REGISTRY)
						.where(REGISTRY.ID.eq(personRecord.get(PERSON.REGISTRY)))
						.fetchOne();
				
				Integer agreementId = null;
				Integer agreementLevelId = c.get(CONTRACT.AGREEMENT_LEVEL);
				
				if(null != agreementLevelId)
					agreementId = dslContext.select(AGREEMENT_LEVEL.AGREEMENT).from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.ID.eq(agreementLevelId))
						.fetchOne(AGREEMENT_LEVEL.AGREEMENT);
				
				
				//PERSON TABLE
				Integer id = personRecord.get(PERSON.REGISTRY);
				String name = personRecord.get(PERSON.NAME);
				String surName = personRecord.get(PERSON.FIRST_SURNAME);
				String secondSurName = personRecord.get(PERSON.SECOND_SURNAME);
				Date bithDate = personRecord.get(PERSON.BIRTH_DATE);
				Byte gender = personRecord.get(PERSON.GENDER);
				String ssNumber = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);
				
				//REGISTRY TABLE
				String document = registryRecord.get(REGISTRY.DOCUMENT);
				Byte documentType = registryRecord.get(REGISTRY.DOCUMENT_TYPE);
				String nationality = registryRecord.get(REGISTRY.NATIONALITY);
				
				//SET EMPLOYEE INFO
				employee.setContractId(contractId);
				
				employee.setEmployeeId(id);
				employee.setName(name);
				employee.setSurName(surName);
				employee.setSecondSurName(secondSurName);
				employee.setBirthdate(bithDate);
				employee.setGender(gender);
				employee.setSsNumber(ssNumber);
				
				employee.setDocument(document);
				employee.setDocumentType(documentType);
				employee.setNationality(nationality);
				
				employee.setAgreementId(agreementId);
				
				workplaceEmployees.addEmployee(employee);
			}
		}
		
		return workplaceEmployees;
	}
	
	private static WorkplaceEmployees getWorkplaceEmployeesInformation(DSLContext dslContext, Workplace workplace, Integer domainId) {
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		
		List<Integer> enterprisesId = null;
		
		if(null != workplace) {
			enterprisesId = dslContext.select(WORKPLACE.ENTERPRISE)
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(workplace.getId()))
					.fetch(WORKPLACE.ENTERPRISE);
		} else {
			enterprisesId = dslContext.select(WORKPLACE.ENTERPRISE)
					.from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetch(WORKPLACE.ENTERPRISE);
		}
		
		Result<Record> personRecords = dslContext.select()
				.from(PERSON)
				.innerJoin(CONTRACT)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.where(WORKPLACE.ENTERPRISE.in(enterprisesId))
				.and(CONTRACT.ID.gt(0))
				.and(WORKPLACE.ACTIVE.eq((byte)1))
				.fetch();
		
		for(Record p : personRecords){
			EmployeeInfo employee = new EmployeeInfo();
			
			Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.where(CONTRACT.PERSON.eq(p.get(PERSON.REGISTRY)))
				.orderBy(CONTRACT.START_DATE.desc())
				.fetch();
			
			Integer contractId = null;
			Boolean isActive = false;
			
			if(null != contractRecords && !contractRecords.isEmpty()){
				contractId = contractRecords.get(0).get(CONTRACT.ID);
				Date endDate = contractRecords.get(0).get(CONTRACT.END_DATE);
				Date actualDate = new Date(new java.util.Date().getTime());
				if(null == endDate || endDate.after(actualDate))
					isActive = true;
			}
			
			Record registryRecord = dslContext.select()
					.from(REGISTRY)
					.where(REGISTRY.ID.eq(p.get(PERSON.REGISTRY)))
					.fetchOne();
			
			//PERSON TABLE
			Integer id = p.get(PERSON.REGISTRY);
			String name = p.get(PERSON.NAME);
			String surName = p.get(PERSON.FIRST_SURNAME);
			String secondSurName = p.get(PERSON.SECOND_SURNAME);
			Date bithDate = p.get(PERSON.BIRTH_DATE);
			Byte gender = p.get(PERSON.GENDER);
			String ssNumber = p.get(PERSON.SOCIAL_SECURITY_NUM);
			
			//REGISTRY TABLE
			String document = registryRecord.get(REGISTRY.DOCUMENT);
			Byte documentType = registryRecord.get(REGISTRY.DOCUMENT_TYPE);
			String nationality = registryRecord.get(REGISTRY.NATIONALITY);
			
			Result<Record> raddressRecords = dslContext.select()
					.from(RADDRESS)
					.where(RADDRESS.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			//RADDRESS TABLE
			Integer raddressId = null;
			String streetType = null;
			String address = null;
			String addressNum = null;
			String addressZip = null;
			String addressCity = null;
			Integer geozoneId = null;
			
			if(null != raddressRecords && !raddressRecords.isEmpty()) {
				raddressId = raddressRecords.get(0).get(RADDRESS.ID);
				streetType = raddressRecords.get(0).get(RADDRESS.STREET_TYPE);
				address = raddressRecords.get(0).get(RADDRESS.ADDRESS);
				addressNum = raddressRecords.get(0).get(RADDRESS.NUMBER);
				addressZip = raddressRecords.get(0).get(RADDRESS.ZIP);
				addressCity = raddressRecords.get(0).get(RADDRESS.CITY);
				geozoneId = raddressRecords.get(0).get(RADDRESS.GEOZONE);
			}
			
			//RMEDIA TABLE
			Result<Record> rmediaRecords = dslContext.select()
					.from(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			Integer mobileId = null;
			String mobile = null;
			Integer phoneId = null;
			String phone = null;
			Integer emailId = null;
			String email = null;
			
			for(Record record : rmediaRecords) {
				if(1 == record.get(RMEDIA.MEDIA)) {
					phoneId = record.get(RMEDIA.ID);
					phone = record.get(RMEDIA.VALUE);
				}else if(2 == record.get(RMEDIA.MEDIA)) {
					mobileId = record.get(RMEDIA.ID);
					mobile = record.get(RMEDIA.VALUE);
				}else if(4 == record.get(RMEDIA.MEDIA)) {
					emailId = record.get(RMEDIA.ID);
					email = record.get(RMEDIA.VALUE);
				}
			}
			
			//PAY METHOD
			Result<Record> rpaymethodRecords = dslContext.select()
					.from(RPAYMETHOD)
					.where(RPAYMETHOD.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			Integer rpaymethodId = null;
			Integer paymethodId = null;
			String payMethod = null;
			Integer rbankId = null;
			String account = null;
			String bic = null;
			
			if(null != rpaymethodRecords && !rpaymethodRecords.isEmpty()) {
				rpaymethodId = rpaymethodRecords.get(0).get(RPAYMETHOD.ID);
				Record paymethodRecord = dslContext.select()
						.from(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.PAY_METHOD)))
						.fetchOne();
				
				paymethodId = paymethodRecord.get(PAY_METHOD.ID);
				payMethod = paymethodRecord.get(PAY_METHOD.NAME);
				
				if(null != rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)) {
					Record rbankRecord = dslContext.select()
							.from(RBANK)
							.where(RBANK.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)))
							.fetchOne();
					
					rbankId = rbankRecord.get(RBANK.ID);
					account = rbankRecord.get(RBANK.BANK_ACCOUNT);
					bic = rbankRecord.get(RBANK.BIC);
				}
			}
			
			//SET EMPLOYEE INFO
			employee.setContractId(contractId);
			employee.setContractActive(isActive);
			
			employee.setEmployeeId(id);
			employee.setName(name);
			employee.setSurName(surName);
			employee.setSecondSurName(secondSurName);
			employee.setBirthdate(bithDate);
			employee.setGender(gender);
			employee.setSsNumber(ssNumber);
			
			employee.setDocument(document);
			employee.setDocumentType(documentType);
			employee.setNationality(nationality);
			
			employee.setRaddressId(raddressId);
			employee.setStreetType(streetType);
			employee.setAddress(address);
			employee.setAddresNum(addressNum);
			employee.setAddressZip(addressZip);
			employee.setAddressCity(addressCity);
			employee.setAddressProvinces(geozoneId);
			
			employee.setMobileId(mobileId);
			employee.setMobile(mobile);
			employee.setPhoneId(phoneId);
			employee.setPhone(phone);
			employee.setEmailId(emailId);
			employee.setEmail(email);
			
			employee.setRpaymethodId(rpaymethodId);
			employee.setPaymethodId(paymethodId);
			employee.setPayMethodType(payMethod);
			employee.setRbankId(rbankId);
			employee.setAccount(account);
			employee.setBic(bic);
			
			workplaceEmployees.addEmployee(employee);
		}
		
		return workplaceEmployees;
	}
	
	
	private static EventsWorkplace setWorkplaceEmployeesInformation(DSLContext dslContext,
			EventsWorkplace updateEventsWorkplace) {
		
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_VACACIONES");
		
		for(Quintet<Integer, String, java.util.Date, java.util.Date, String> varQuintet : updateEventsWorkplace.getUpdateEventsWorkplace()){
			Integer employeeId = varQuintet.getContractId();
			deleteEmployeeEventsVariables(dslContext, employeeId);
		}
		
		for(Quintet<Integer, String, java.util.Date, java.util.Date, String> varQuintet : updateEventsWorkplace.getUpdateEventsWorkplace()){
			Integer employeeId = varQuintet.getContractId();
			
			addEmployeeEventsVariable(
					dslContext, 
					employeeId, 
					varQuintet.getVarName(), 
					varQuintet.getStart_date(),
					varQuintet.getEnd_date(),
					varQuintet.getExpression(),
					varNotToUpdate);
		}
		
		return updateEventsWorkplace;
	}

	private static void deleteEmployeeEventsVariables(DSLContext dslContext, Integer employeeContractId) {
		Result<Record> contractRecord = dslContext.select()
				.from(CONTRACT)
				.where(CONTRACT.PERSON.eq(employeeContractId))
				.fetch();
		 
		Integer contract = contractRecord.get(0).get(CONTRACT.ID);
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				  ContextVariable.EXTRA_HOURS.getName())
				.or(CONTRACT_DATA.NAME.eq("HORAS_COMPLEMENTARIAS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_FORMACION_PRESENCIAL"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_FORMACION_DISTANCIA"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_TUTORIA"))
				.or(CONTRACT_DATA.NAME.eq("BONIFICACION_TUTORIA"))
				)
		   .execute();
		
	}
	
	private static void addEmployeeEventsVariable(DSLContext dslContext, Integer employeeContractId, String varName,
			java.util.Date start_date, java.util.Date end_date, String expression,
			ArrayList<String> varNotToUpdate) {
		
		Result<Record> contractRecord = dslContext.select()
				.from(CONTRACT)
				.where(CONTRACT.PERSON.eq(employeeContractId))
				.fetch();
		 
		Integer domain = contractRecord.get(0).get(CONTRACT.DOMAIN);
		Integer contract = contractRecord.get(0).get(CONTRACT.ID);
		
		if(!varNotToUpdate.contains(varName))
			if(null != expression){
				
				Date sqlStartDate = new Date(start_date.getTime());
				Date sqlEndDate = new Date(end_date.getTime());
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
						CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(domain, varName, contract, expression, 
								sqlStartDate, sqlEndDate)
						.execute();
			}
	}

	// Get employee calendar info from database
	private static Boolean isFullTimeJourney(String journeyType) {
		return ('1' == journeyType.charAt(1) || '4' == journeyType.charAt(1)|| "true" == journeyType) ? true : false;
	}

	private static ArrayList<EventEmployee> setEventsDraftDB(DSLContext dslContext, ArrayList<EventEmployee> eventEmployees) {
		for(EventEmployee eventEmployee : eventEmployees) {
			Integer contractId = eventEmployee.getContractId();
			
			Integer domain = dslContext.select(CONTRACT.DOMAIN)
					.from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne().value1();
			
			ArrayList<String> varsToUpdate = eventEmployee.getEmployeeEventsData().getVarsToUpdate();
			
			dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.in(varsToUpdate)).execute();
			
			for(Entry<String, ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> entry : eventEmployee.getEmployeeEventsData().getContractEventsList().entrySet()){
				if(varsToUpdate.contains(entry.getKey())) {
					for(Quartet<java.util.Date, java.util.Date, String, String> quartet : entry.getValue()) {
						if(AonStringUtils.isNotBlank(quartet.getExpression()))
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
									CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
									.values(domain, quartet.getName(), contractId, quartet.getExpression(), 
											parseToSQLDate(quartet.getStartDate()), parseToSQLDate(quartet.getEndDate()))
									.execute();
					}
				}
			}
		}
		
		return null;
	}
	
	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date)
			return null;
		
		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}
	
	// ------------------------------------------------------------------------------------
	// 							exportEmployeeEventsVariablesExcel
	// ------------------------------------------------------------------------------------
	
	public static final String REMOVE = "REMOVE()";
	
	public static Set<String> exportEmployeeEventsVariablesExcel(String domainName, String userLogin, Date date) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			List<Integer> domainContracts = JooqEnterprise.getEnterpriseActiveContracts(connection, domainId, date);
			
			List<ContextDescriptor> contractsContextDescriptor = new ArrayList<>();
			for(Integer contractId : domainContracts) {
//				contractsContextDescriptor.add(getEmployeeEventsVariables(domainName, contractId, AonDateUtils.getYearFirstDay(date), AonDateUtils.getYearLastDay(date)));
				contractsContextDescriptor.add(getEmployeeEventsVariables(domainName, contractId, AonDateUtils.getMonthFirstDay(date), AonDateUtils.getMonthLastDay(date)));
			}

			Set<String> variables = new TreeSet<>();
			variables.add("IMPORTE_HORA_EXTRA");
			variables.add("HORAS_FORMACION_PRESENCIAL");
			variables.add("HORAS_FORMACION_DISTANCIA");
			variables.add("HORAS_TUTORIA");
			variables.add("BONIFICACION_TUTORIA");
			variables.add("BONIFICACION_FORMACION_CONTINUA");
			
			for(ContextDescriptor contextDescriptor : contractsContextDescriptor) {
				variables.addAll(filterContextVariables(contextDescriptor.getVariables(), variables));
				
				// Check agreement only variables
				for(Entry<String, ArrayList<VariableDescriptor>> entry : contextDescriptor.getVariableDescriptors().entrySet()) {
					String variableName = entry.getKey();
					ArrayList<VariableDescriptor> variableDescriptors = entry.getValue();
					if(null == variableDescriptors || variableDescriptors.isEmpty())
						continue;
					
					for(VariableDescriptor variableDescriptor : variableDescriptors) {
						if(variableDescriptor.getScope() == Scope.AGREEMENT) {
							variables.add(variableName);
						}
						
						if(variableDescriptor.getScope() == Scope.CONTRACT && !continueVariable(variableName))
							variables.add(variableName);
					}
				}
			}
			
			System.out.println(variables);
			
			return variables;
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private static ContextDescriptor getEmployeeEventsVariables(String domain, Integer employeeId, java.util.Date startDate, java.util.Date endDate)
			throws IllegalArgumentException {
		Connection connection = null;
		try {
			System.out.println("ENTRANDO PARA BUSCAR VARIABLES, Domain : " + domain + ", Employee Id : " + employeeId + ", StartDate : " + startDate + ", endDate : " + endDate);

			// Get info
			connection = AonServletUtils.getConnection(domain);
			Integer domainID = AonServletUtils.getDomainID(domain);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domain);

			Integer agreementId = SQLEvents.getAgreementId(connection, employeeId);

			// ContextResult returned
			ContextDescriptor contextResult = new ContextDescriptor();

			// Criteria
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(SQLConstants.CONTRACT + "." + ContractColumns.ID, employeeId);

			java.util.Date iteratorDate = DateUtils.copyDateOnly(startDate);
			while (iteratorDate.before(endDate)) {

				if (connection.isClosed())
					connection = AonServletUtils.getConnection(domain);

				// Context
				SQLContractSalaryCalculatorContext context = new SQLContractSalaryCalculatorContext(connection,
						iteratorDate, AonDateUtils.getMonthLastDay(iteratorDate),
						AonDateUtils.getMonthLastDay(iteratorDate), criteria);

				if (context.next()) {

					// Salary Calculator
					SmartContractSalaryCalculator<com.esferalia.aon.payroll.Salary> smartContractSalaryCalculator = new SmartContractSalaryCalculator<>(
							new SalaryBuilder());

					smartContractSalaryCalculator.setListener(new GenericContractSalaryCalculator.IListener() {

						@Override public void onUndefinedData(IContractDeduction deduction, String variableName, String message) {}

						@Override public void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var) {}

						@Override
						public void onUndefinedData(IContractPayment payment, String variableName, String message) {
							if (payment.getScope() != ExpressionScope.SYSTEM && !variableName.contains("DIAS_"))
								contextResult.add(variableName);
						}

						@Override public void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var) {}

						@Override public void onRemove(IContractPayment payment) {}

						@Override public void onRemove(IContractDeduction payment) {}

						@Override public void onRemove(IContractBonus bonus) {}

						@Override public void onInvalidData(IContractBonus bonus, String variableName, String message) {}

						@Override public void onInvalidData(IContractDeduction deduction, String variableName, String message) {}

						@Override public void onInvalidData(IContractPayment payment, String variableName, String message) {}

						@Override public void onInvalidData(String variableName, String message) {}

						@Override public void onCompileError(IContractBonus bonus, String message) {}

						@Override public void onCompileError(IContractDeduction deduction, String message) {}

						@Override public void onCompileError(IContractPayment payment, String message) {}

						@Override public void onCompileError(String variableName, String message) {}

						@Override public void onCheckError(IContractBonus bonus, String message) {}

						@Override public void onCheckError(IContractDeduction deduction, String message) {}

						@Override public void onCheckError(IContractPayment payment, String message) {}

						@Override public void onCheckError(String message) {}

					});

					// Salary
					try {
						smartContractSalaryCalculator.calculate(context);
					} catch (SalaryException e) {
						e.printStackTrace();
					}

					// ContextDrescriptor
					ContextDescriptor contextDescriptor = getContext(connection, context, startDate, endDate);

					ContextDescriptor contextDescriptorPayments = getEmployeePayments(connection, employeeId,
							agreementId, iteratorDate, AonDateUtils.getMonthLastDay(iteratorDate), domainID,
							parentDomainID);

					contextDescriptorPayments.mixAll(contextDescriptor);

					for (String key : contextDescriptorPayments.getVariables()) {
						if (!contextDescriptorPayments.getList(key).isEmpty()) {
							for (VariableDescriptor variable : contextDescriptorPayments.getList(key)) {
								if (Number.class != variable.getType() || null == variable.getScope()
										|| Scope.APPLICATION == variable.getScope()
										|| Scope.SYSTEM == variable.getScope())
									continue;

								contextResult.add(key, variable);
							}
						} else
							contextResult.add(key);
					}
				}

				iteratorDate = AonDateUtils.addMonths(iteratorDate, 1);
			}

			return contextResult;

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
			}
		}
	}

	protected static ContextDescriptor getContext(Connection conn, IContractSalaryCalculatorContext calculatorCtx, java.util.Date startDate, java.util.Date endDate) {
		try {

			ExpressionContext expressionContext = notNull(calculatorCtx.getExpressionContext(),
					calculatorCtx.getSystemExpressionContext());

			java.util.Date start = notNull(calculatorCtx.getStartDate(), startDate);

			java.util.Date end = notNull(calculatorCtx.getEndDate(), endDate);

			ContextDescriptor contextDescriptor = new ContextDescriptor();

			Map<String, String> descriptions = getSystemDescriptions(conn, start, end);

			Set<String> varNames = expressionContext.variablesSet();

			for (String varName : varNames) {
				Object value = null;
				List<ITimedVariable<Object>> vars = expressionContext.getVariables(varName, start, end);

				for (ITimedVariable<Object> var : vars) {
					try {
						value = var.getValue(var.getPeriod());
					} catch (Throwable e) {

					}

					if (value == null)
						continue;

					String description = null;

					ContextVariable ctxVar = ContextVariable.getVariableByName(varName);

					if (ctxVar != null) {
						try {
							description = ctxVar.getDescription(new Locale("es", "ES"));
						} catch (MissingResourceException e) {
						}
						if (description != null)
							description = String.format(description, start, end);
					}
					if (description == null)
						description = descriptions.get(varName);

					if (Function.class == value.getClass()) {
						Class<?> type = ctxVar != null ? ctxVar.getType().getJavaType() : Object.class;
						contextDescriptor.add(varName, description, type, ((Function) value).getParameters());
					} else if (MethodStub.class == value.getClass()) {
						Method method = ((MethodStub) value).getMethod();
						contextDescriptor.add(varName, description, method.getReturnType(), method.getParameterTypes());
					} else {
						Class<?> type = value.getClass();
						if (ContextDescriptor.isKnownType(type)) {
							VariableDescriptor variableDescriptor = new VariableDescriptor();
							variableDescriptor.setType(type);
							variableDescriptor.setValue(value.toString());
							variableDescriptor.setDescription(description);
							variableDescriptor.setStartDate(var.getPeriod().getStart());
							variableDescriptor.setEndDate(var.getPeriod().getEnd());

							if (var instanceof IExpressionVariable<?>) {
								try {
									IExpression expression = ((IExpressionVariable<?>) var).getExpression();
									variableDescriptor.setExpression(expression.getExpression());

									variableDescriptor.setScope((expression.getScope() != null)
											? Scope.values()[expression.getScope().ordinal()]
											: null);
								} catch (Exception e) {
									System.out.println("Var name failed : " + varName);
								}

							}
							contextDescriptor.add(varName, variableDescriptor);
						}
					}
				}
			}

			return contextDescriptor;

		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private static Map<String, String> getSystemDescriptions(Connection conn, java.util.Date start, java.util.Date end)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {

			stmt = conn.prepareStatement("SELECT " + SystemDataColumns.NAME + ", " + SystemDataColumns.COMMENTS
					+ " FROM " + SQLConstants.SYSTEM_DATA + " WHERE " + SystemDataColumns.START_DATE + " <= ? "
					+ " AND ( " + SystemDataColumns.END_DATE + " IS NULL " + " OR " + SystemDataColumns.END_DATE
					+ " >= ? " + ") ");
			stmt.setDate(1, new java.sql.Date(end.getTime()));
			stmt.setDate(2, new java.sql.Date(start.getTime()));

			Map<String, String> descriptions = new HashMap<String, String>();

			rs = stmt.executeQuery();
			while (rs.next()) {
				descriptions.put(rs.getString(SystemDataColumns.NAME), rs.getString(SystemDataColumns.COMMENTS));
			}
			return descriptions;
		} finally {

		}
	}
	
	private static ContextDescriptor getEmployeePayments(Connection connection, Integer employeeId, Integer agreementId,
			java.util.Date startDate, java.util.Date endDate, Integer domainID, Integer parentDomainID) throws SQLException {

		ArrayList<String> eraseAgreements = new ArrayList<>();

		try {
			Set<Payment> payments = SQLEvents.getEmployeePayments(connection, employeeId, startDate, endDate);

			if (agreementId != null) {

				eraseAgreements = SQLAgreementDraft.getEraseAgreement(connection, agreementId, startDate, endDate);

				payments.addAll(SQLAgreementDraft.getPaymentsAux(connection, agreementId, startDate, endDate));
			}

			ContextDescriptor result = new ContextDescriptor();

			for (Payment payment : payments) {

				if (AonStringUtils.equals(REMOVE, payment.getExpression()))
					continue;

				try {
					Set<String> paymentVars = ExpressionContext.getVariableSet(payment.getExpression());
					for (String var : paymentVars) {
						if (var.endsWith("_ACTUAL"))
							continue; // This is awfull ... very awful
						result.add(var);
					}
				} catch (Exception e) {
					// TODO: Error...
				}

				result.remove(payment.getName());

			}

			// Add Filter Allways Variables
			// eraseAgreements.add("INICIO_ANTIGUEDAD");
			eraseAgreements.add("DIAS_MES");
			eraseAgreements.add("INICIO_CONTRATO");
			eraseAgreements.add("SALARIO_BASE");
			eraseAgreements.add("INICIO_NOMINA");
			eraseAgreements.add("SALARIO_MENSUAL");
			eraseAgreements.add("TRIENIO");
			// TODO: Esto es una prueba
			eraseAgreements.add("KMS");

			// Filter Agreement Variables
			for (String varName : eraseAgreements)
				result.remove(varName);

			Set<Level> levels = null;
			SalaryTable salaryTable = null;

			if (null != agreementId)
				levels = SQLAgreementDraft.getLevels(connection, agreementId, domainID, parentDomainID);

			if (null != agreementId)
				salaryTable = SQLAgreementDraft.getSalaryTable(connection, agreementId, startDate, endDate, domainID,
						parentDomainID);

			if (null != levels && null != salaryTable) {
				Set<String> names = result.getVariables();
				for (Level level : levels) {
					Iterator<String> namesIt = names.iterator();
					while (namesIt.hasNext()) {
						String name = namesIt.next();
						if (salaryTable.get(level.getId(), name) != null)
							namesIt.remove();
					}
				}
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	
	private static Set<String> filterContextVariables(Set<String> contextVariables, Set<String> variables) {
		Set<String> resultSet = new LinkedHashSet<>();
		
		// Filter set
		Set<String> filterSet = new LinkedHashSet<>();
		filterSet.add("PAGA_EXTRA_HELP");
		filterSet.add("TC2");
		filterSet.add("SALARIO_VARIABLE_DIA");
		filterSet.add("AÑOS_TRABAJADOS");
		filterSet.add("GRUPO_COTIZACION");
		filterSet.add("ANTIGUEDAD_HELP");
		filterSet.add("GET_VARIABLE");
		filterSet.add("LABORABLE");
		filterSet.add("OS_TRABAJADOS");
		filterSet.add("DIAS_");
		filterSet.add("OCUPACI");
		filterSet.add("COEFICIENTE_PARCIALIDAD");
		filterSet.add("SMI");
		filterSet.add("HIDE");
		filterSet.add("TODO");
		filterSet.add("ORIGINAL_START_DATE");
		filterSet.add("ORIGINAL_END_DATE");
		filterSet.add("CAUSA_INACTIVIDAD");
		filterSet.add("SELF");
		
		filterSet.addAll(variables);
		
		for(String var : contextVariables) {
			if(contains(filterSet, var))
				continue;
			
			resultSet.add(var);
		}
		
		return resultSet;
	}
	
	private static boolean continueVariable(String variableName) {
		// Filter set
		Set<String> filterSet = new LinkedHashSet<>();
		filterSet.add("PAGA_EXTRA_HELP");
		filterSet.add("TC2");
		filterSet.add("SALARIO_VARIABLE_DIA");
		filterSet.add("AÑOS_TRABAJADOS");
		filterSet.add("GRUPO_COTIZACION");
		filterSet.add("ANTIGUEDAD_HELP");
		filterSet.add("GET_VARIABLE");
		filterSet.add("LABORABLE");
		filterSet.add("OS_TRABAJADOS");
		filterSet.add("DIAS_");
		filterSet.add("OCUPACI");
		filterSet.add("COEFICIENTE_PARCIALIDAD");
		filterSet.add("ORIGINAL_START_DATE");
		filterSet.add("ORIGINAL_END_DATE");
		filterSet.add("CAUSA_INACTIVIDAD");
		filterSet.add("SELF");
		
		
		for(String filterVar : filterSet){
			if(AonStringUtils.equalsIgnoreCase(filterVar, variableName) || AonStringUtils.containsIgnoreCase(variableName, filterVar))
				return true;
		}
		
		return false;
	}
	
	private static boolean contains(Set<String> filterSet, String var) {
		for(String filterVar : filterSet) {
			if(AonStringUtils.equalsIgnoreCase(filterVar, var) || AonStringUtils.containsIgnoreCase(var, filterVar))
				return true;
		}
		return false;
	}
	
	@SafeVarargs
	private static <T> T notNull(T... ts) {
		for (T t : ts) {
			if (t != null)
				return t;
		}
		return null;
	}
	
}
