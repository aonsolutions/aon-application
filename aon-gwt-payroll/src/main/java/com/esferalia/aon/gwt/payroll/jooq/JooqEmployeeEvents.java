package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

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

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static EmployeeEventsData getEmployeeEventsInformation(DSLContext dslContext, Integer person_ID, ArrayList<String> employeeContractVariables) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		Map<String,ArrayList<Quartet<Date, Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<Date, Date, String, String>>>(); 
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<Date, Date, String, String>> varibaleList = new ArrayList<Quartet<Date, Date, String, String>>();
			
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
						Quartet<Date, Date, String, String> quarterVariableEmployeeInfo = new Quartet<Date, Date, String, String>();
						
						Date startDate = r.get(CONTRACT_DATA.START_DATE);
						Date endDate = r.get(CONTRACT_DATA.END_DATE);
						String expression = r.get(CONTRACT_DATA.EXPRESSION);
						
						if(isCoefficientVariable(name)) {
							Integer daysBetween = DateUtils.getDaysBetween(startDate, endDate);
							daysBetween++;
							
							expression = daysBetween.toString();
						}
						
						quarterVariableEmployeeInfo.setStartDate(startDate)
						.setEndDate(endDate)
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
	
	private static EmployeeEventsData getEmployeeEventsByContractInformation(DSLContext dslContext, Integer contractId,
			ArrayList<String> employeeContractVariables) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		Map<String,ArrayList<Quartet<Date, Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<Date, Date, String, String>>>(); 
		
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
			
			journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
		}
		
		employeeInfoVariablesEvents.setFullTimeJourney(isFullTimeJourney(journeyTypeEmployee));
		
		// --------------------------------------------- AÑADIR VARIABLES ---------------------------------------------------------
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<Date, Date, String, String>> varibaleList = new ArrayList<Quartet<Date, Date, String, String>>();
			
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
				
				for(Record r: variableEmployeeInfo){
					Quartet<Date, Date, String, String> quarterVariableEmployeeInfo = new Quartet<Date, Date, String, String>();
					
					Date startDate = r.get(CONTRACT_DATA.START_DATE);
					Date endDate = r.get(CONTRACT_DATA.END_DATE);
					String expression = r.get(CONTRACT_DATA.EXPRESSION);
					
					if(isCoefficientVariable(name)) {
						Integer daysBetween = DateUtils.getDaysBetween(startDate, endDate);
						daysBetween++;
						
						expression = daysBetween.toString();
					}
					
					quarterVariableEmployeeInfo.setStartDate(startDate)
					.setEndDate(endDate)
					.setName(name)
					.setExpression(expression);
					
					varibaleList.add(quarterVariableEmployeeInfo);
				}
			}
			
			employeeVariablesEvents.put(name, varibaleList);
		}		

		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		
		return employeeInfoVariablesEvents;
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

	

}
