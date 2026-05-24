package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeeEvents {

	private static Settings SETTINGS = null;

	public static EmployeeEventsData getEmployeeEvents(Connection conn, Integer contract, ArrayList<String> employeeContractVariables) throws IllegalArgumentException {
		return getEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract, employeeContractVariables);
	}
	
	public static EmployeeEventsData getEmployeeEventsByContract(Connection conn, Integer contractId,
			ArrayList<String> employeeContractVariables) {
		return getEmployeeEventsByContractInformation(DSL.using(conn, getDefaultSettings()), contractId, employeeContractVariables);	
	}

	public static void setEmployeeEvents(Connection conn, Integer contract, EmployeeEventsUpdate updateInfo){
		setEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
	}
	
	public static EmployeeEventsData setEmployeeEvents(Connection connection, Integer idEmployee,
			EmployeeEventsData employeeEventsData) {
		return setEmployeeEventsInformation(DSL.using(connection, getDefaultSettings()), idEmployee, employeeEventsData);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static EmployeeEventsData getEmployeeEventsInformation(DSLContext dslContext, Integer person_ID, ArrayList<String> employeeContractVariables) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		Map<String,ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>>(); 
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<java.util.Date, java.util.Date, String, String>> varibaleList = new ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>();
			
			Result<Record> contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.PERSON.eq(person_ID))
					.fetch();
			
			if(!contractRecord.isEmpty()) {
				Integer contractId = contractRecord.get(0).get(CONTRACT.ID);
			
				String variableName = getCoefficientVariable(name);
				
				if(!contractRecord.isEmpty()) {
					Result<Record> variableEmployeeInfo = dslContext
							  .select()
							  .from(CONTRACT_DATA)
							  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
							  .and(CONTRACT_DATA.NAME.eq(variableName))
							  .fetch();
					
					for(Record r: variableEmployeeInfo){
						Quartet<java.util.Date, java.util.Date, String, String> quarterVariableEmployeeInfo = new Quartet<java.util.Date, java.util.Date, String, String>();
						
						Date startDate = r.get(CONTRACT_DATA.START_DATE);
						Date endDate = r.get(CONTRACT_DATA.END_DATE);
						String expression = r.get(CONTRACT_DATA.EXPRESSION);
						
						if(isCoefficientVariable(name)) {
							Integer daysBetween = DateUtils.getDaysBetween(startDate, endDate);
							daysBetween++;
							
							expression = daysBetween.toString();
						}
						
						quarterVariableEmployeeInfo
							.setStartDate(parseDateToJava(startDate))
							.setEndDate(parseDateToJava(endDate))
							.setName(name)
							.setExpression(expression);
						
						varibaleList.add(quarterVariableEmployeeInfo);
					}
				}
				
				employeeVariablesEvents.put(name, varibaleList);
			}
		}			

		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		
		return employeeInfoVariablesEvents;
	
	}
	
	private static java.util.Date parseDateToJava(Date date) {
		if(null == date)
			return null;
		
		java.util.Date javaDate = new java.util.Date(date.getTime());
		DateUtils.resetTime(javaDate);
		
		return javaDate;
	}

	private static EmployeeEventsData getEmployeeEventsByContractInformation(DSLContext dslContext, Integer contractId,
			ArrayList<String> employeeContractVariables) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		Map<String,ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>>(); 
		ArrayList<String> employeeFilterContractVariables = new ArrayList<String>();
		
		// ----------------------------------- CONTRACT PERIOD
		
		Record contractRecordInfo = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Date contractStartDate = contractRecordInfo.get(CONTRACT.START_DATE);
		Date contractEndDate = contractRecordInfo.get(CONTRACT.END_DATE);
		
		employeeInfoVariablesEvents.setContractStartDate(parseDateToJava(contractStartDate));
		employeeInfoVariablesEvents.setContractEndDate(parseDateToJava(contractEndDate));
		
		
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
				journeyTypeEmployee = "false";
			else
				journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
		}
		
		employeeInfoVariablesEvents.setFullTimeJourney(isFullTimeJourney(journeyTypeEmployee));
		
		// ----------------------------------- TC2
		
		Result<Record> contractType = dslContext.select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
				  .and(CONTRACT_DATA.NAME.equal("TC2"))
				  .orderBy(CONTRACT_DATA.START_DATE.desc()) // If there is more than one contract
				  .fetch();
		
		if(contractType.isNotEmpty()) {
			String tc2 = contractType.get(0).get(CONTRACT_DATA.EXPRESSION);
			employeeInfoVariablesEvents.setTC2(tc2);
		}
		
		// --------------------------------------------- AÑADIR VARIABLES ---------------------------------------------------------
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<java.util.Date, java.util.Date, String, String>> varibaleList = new ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>();
			
			Result<Record> contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetch();
			
			String variableName = getCoefficientVariable(name);
			
			if(!contractRecord.isEmpty()) {
				Result<Record> variableEmployeeInfo = dslContext
						  .select()
						  .from(CONTRACT_DATA)
						  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
						  .and(CONTRACT_DATA.NAME.eq(variableName))
						  .fetch();
				
				try {
					checkVariableIfComplexExpression(variableEmployeeInfo);
				} catch (Exception e) {
					continue;
				}
				
				for(Record r: variableEmployeeInfo){
					Quartet<java.util.Date, java.util.Date, String, String> quarterVariableEmployeeInfo = new Quartet<java.util.Date, java.util.Date, String, String>();
					
					Date startDate = r.get(CONTRACT_DATA.START_DATE);
					Date endDate = r.get(CONTRACT_DATA.END_DATE);
					String expression = r.get(CONTRACT_DATA.EXPRESSION);
					
					if(isCoefficientVariable(name)) {
						if(null == endDate) {
							expression = "-1";
						} else {
							Integer daysBetween = DateUtils.getDaysBetween(startDate, endDate);
							daysBetween++;
							
							expression = daysBetween.toString();
						}
					}
					
					if(AonStringUtils.isNoneEmpty(expression)) {
						Object expressionEval = MVEL.eval(expression);
						if(null != expressionEval) {
							quarterVariableEmployeeInfo
								.setStartDate(parseDateToJava(startDate))
								.setEndDate(parseDateToJava(endDate))
								.setName(name)
								.setExpression(expressionEval.toString());
							
							varibaleList.add(quarterVariableEmployeeInfo);
						}
						else
							System.out.println("MVEL.eval() -> " + name + " = " + expressionEval);
					} else
						System.out.println(name + " = " + expression);
					
					
				}
			}
			
			employeeVariablesEvents.put(name, varibaleList);
			
			employeeFilterContractVariables.add(name);
		}
		
		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		employeeInfoVariablesEvents.setEmployeeContractVariables(employeeFilterContractVariables);
		
		return employeeInfoVariablesEvents;
	}
	
	private static void checkVariableIfComplexExpression(Result<Record> variableEmployeeInfo) throws Exception {
		for(Record record : variableEmployeeInfo) {
			try {
				Double.parseDouble(record.get(CONTRACT_DATA.EXPRESSION));
			} catch (Exception e) {
				try {
				    Object res = MVEL.eval(record.get(CONTRACT_DATA.EXPRESSION));
				    System.err.println("EVAL EXPRESSION " + record.get(CONTRACT_DATA.EXPRESSION) + " -> " + res);
				} catch (Exception ex) {
					throw new RuntimeException();
				}
			}
			
		}
	}

	private static String getCoefficientVariable(String name) {
		switch (name) {
		case "DIAS_AUSENCIA":
			return "COEFICIENTE_AUSENCIA";
		case "DIAS_HUELGA":
			return "COEFICIENTE_HUELGA";
		case "DIAS_ERE":
			return "COEFICIENTE_ERE";
		case "DIAS_ERE_FZA":
			return "COEFICIENTE_ERE_FZA";
		case "DIAS_ERE_FZA_EXON":
			return "COEFICIENTE_ERE_FZA_EXONERADO";
		default:
			return name;
		}
	}
	
	private static boolean isCoefficientVariable(String name) {
		ArrayList<String> coefficientList = new ArrayList<String>();
		coefficientList.add("DIAS_AUSENCIA");
		coefficientList.add("DIAS_HUELGA");
		coefficientList.add("DIAS_ERE");
		coefficientList.add("DIAS_ERE_FZA");
		coefficientList.add("DIAS_ERE_FZA_EXON");
		
		return coefficientList.contains(name);
	}
	
	

	private static void setEmployeeEventsInformation(DSLContext dslContext, Integer contract,
			EmployeeEventsUpdate updateInfo) {
		
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne().value1();
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
				"IMPORTE_HORA_EXTRA",
				"HORAS_FORMACION_PRESENCIAL",
				"HORAS_FORMACION_DISTANCIA",
				"HORAS_TUTORIA",
				"BONIFICACION_TUTORIA",
				"BONIFICACION_FORMACION_CONTINUA",
				"KMS"
			)).execute();
		
		List<Quartet<Date, Date, String, String>> updateList = updateInfo.getVariableEventsList();
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		
		varNotToUpdate.add("DIAS_TRABAJADOS");
		varNotToUpdate.add("DIAS_VACACIONES");
		varNotToUpdate.add("DIAS_INACTIVIDAD");
		varNotToUpdate.add("DIAS_AUSENCIA");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_ERE_FZA");
		varNotToUpdate.add("DIAS_ERE_FZA_EXON");
		varNotToUpdate.add("HORAS_COMPLEMENTARIAS");
		varNotToUpdate.add("HORAS_EXTRAS");
		
		for (Quartet<Date, Date, String, String> quartet : updateList){
			
			if(!varNotToUpdate.contains(quartet.getName()))
				if(AonStringUtils.equalsIgnoreCase(quartet.getName(), "DIAS_VACACIONES_NO_DISFRUTADOS")) {
					Integer settleId = dslContext.select(SALARY.ID).from(SALARY).where(SALARY.CONTRACT.eq(contract)).and(SALARY.TYPE.eq(SalaryType.SETTLE.value())).fetchOne(SALARY.ID);
					Record holidyasRecord = dslContext.select().from(SALARY_DATA).where(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS")).and(SALARY_DATA.SALARY.eq(settleId)).fetchOne();
					
					if(null == quartet.getExpression() || AonStringUtils.equalsIgnoreCase(quartet.getExpression(), "0.0"))
						dslContext.delete(SALARY_DATA).where(SALARY_DATA.ID.eq(holidyasRecord.get(SALARY_DATA.ID))).execute();
					else
						dslContext.update(SALARY_DATA)
							.set(SALARY_DATA.EXPRESSION, quartet.getExpression())
							.set(SALARY_DATA.START_DATE, quartet.getStartDate())
							.set(SALARY_DATA.END_DATE, quartet.getEndDate())
							.where(SALARY_DATA.ID.eq(holidyasRecord.get(SALARY_DATA.ID)))
							.execute();
				} else
					if(null != quartet.getExpression())
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
								CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(domain, quartet.getName(), contract, quartet.getExpression(), 
										quartet.getStartDate(), quartet.getEndDate())
								.execute();
		}
		
	}
	
	// Get employee calendar info from database
	private static Boolean isFullTimeJourney(String journeyType) {
		return ('1' == journeyType.charAt(1) || '4' == journeyType.charAt(1)|| "true" == journeyType) ? true : false;
	}

	private static EmployeeEventsData setEmployeeEventsInformation(DSLContext dslContext, Integer idEmployee, EmployeeEventsData employeeEventsData) {
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(idEmployee))
				.fetchOne().value1();
		
		ArrayList<String> varsToUpdate = employeeEventsData.getVarsToUpdate();
		
		dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(idEmployee)).and(CONTRACT_DATA.NAME.in(varsToUpdate)).execute();
		
		for(Entry<String, ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> entry : employeeEventsData.getContractEventsList().entrySet()){
			if(varsToUpdate.contains(entry.getKey())) {
				for(Quartet<java.util.Date, java.util.Date, String, String> quartet : entry.getValue()) {
					if(AonStringUtils.isNotBlank(quartet.getExpression()))
						dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
								CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(domain, quartet.getName(), idEmployee, quartet.getExpression(), 
										parseToSQLDate(quartet.getStartDate()), parseToSQLDate(quartet.getEndDate()))
								.execute();
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
	

}
