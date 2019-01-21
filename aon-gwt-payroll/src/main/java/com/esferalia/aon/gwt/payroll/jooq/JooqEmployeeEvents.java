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
		
		
		// --------------------------------------------- AÑADIR VARIABLES ---------------------------------------------------------
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<Date, Date, String, String>> varibaleList = new ArrayList<Quartet<Date, Date, String, String>>();
			
			Result<Record> contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.PERSON.eq(person_ID))
					.fetch();
			
			if(!contractRecord.isEmpty()) {
				Integer contractId = contractRecord.get(0).get(CONTRACT.ID);
				
				Result<Record> variableEmployeeInfo = dslContext
						  .select()
						  .from(CONTRACT_DATA)
						  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
						  .and(CONTRACT_DATA.NAME.eq(name))
						  .fetch();
				
				for(Record r: variableEmployeeInfo){
					Quartet<Date, Date, String, String> quarterVariableEmployeeInfo = new Quartet<Date, Date, String, String>();
					
					quarterVariableEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
					.setEndDate(r.get(CONTRACT_DATA.END_DATE))
					.setName(r.get(CONTRACT_DATA.NAME))
					.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
					
					varibaleList.add(quarterVariableEmployeeInfo);
				}
			}
			
			employeeVariablesEvents.put(name, varibaleList);
		}		

		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		
		return employeeInfoVariablesEvents;
	
	}
	
	private static EmployeeEventsData getEmployeeEventsByContractInformation(DSLContext dslContext, Integer contractId,
			ArrayList<String> employeeContractVariables) {
		
		EmployeeEventsData employeeInfoVariablesEvents = new EmployeeEventsData();
		Map<String,ArrayList<Quartet<Date, Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<Date, Date, String, String>>>(); 
		
		
		// --------------------------------------------- AÑADIR VARIABLES ---------------------------------------------------------
		
		for(String name: employeeContractVariables){
			ArrayList<Quartet<Date, Date, String, String>> varibaleList = new ArrayList<Quartet<Date, Date, String, String>>();
			
			Result<Record> contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetch();
			
			if(!contractRecord.isEmpty()) {
				Result<Record> variableEmployeeInfo = dslContext
						  .select()
						  .from(CONTRACT_DATA)
						  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
						  .and(CONTRACT_DATA.NAME.eq(name))
						  .fetch();
				
				for(Record r: variableEmployeeInfo){
					Quartet<Date, Date, String, String> quarterVariableEmployeeInfo = new Quartet<Date, Date, String, String>();
					
					quarterVariableEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
					.setEndDate(r.get(CONTRACT_DATA.END_DATE))
					.setName(r.get(CONTRACT_DATA.NAME))
					.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
					
					varibaleList.add(quarterVariableEmployeeInfo);
				}
			}
			
			employeeVariablesEvents.put(name, varibaleList);
		}		

		// ------------------------------------------------- SOLUCION ---------------------------------------------------------		
		
		employeeInfoVariablesEvents.setContractEventsList(employeeVariablesEvents);
		
		return employeeInfoVariablesEvents;
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
				   ContextVariable.WORKED_DAYS.getName()
//				  ,ContextVariable.ERE_DAYS.getName()
//				  ,ContextVariable.STRIKE_DAYS.getName()
				  ,ContextVariable.LEAVE_DAYS.getName()
				  ,ContextVariable.WORKED_HOURS.getName()
				  ,ContextVariable.REAL_DAYS.getName()
//				  ,ContextVariable.HOLIDAYS.getName()
				  ,ContextVariable.EXTRA_HOURS.getName())
				.or(CONTRACT_DATA.NAME.eq("DIAS_EFECTIVOS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_COMPLEMENTARIAS"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA_EXTRANJERO"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION_EXTRANJERO"))
				.or(CONTRACT_DATA.NAME.eq("KMS"))
				.or(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
				.or(CONTRACT_DATA.NAME.eq("IMPORTE_HORA_EXTRA"))
				.or(CONTRACT_DATA.NAME.eq("VENTAS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS_FZA"))
				)
		   .execute();
		
		List<Quartet<Date, Date, String, String>> updateList = updateInfo.getVariableEventsList();
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_VACACIONES");
		
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

	

}
