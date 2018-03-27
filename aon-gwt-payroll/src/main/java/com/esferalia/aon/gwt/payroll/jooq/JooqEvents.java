package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Person.PERSON;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.Quintet;
import com.esferalia.aon.gwt.payroll.client.Triplet;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEvents {

	private static Settings SETTINGS = null;
	
	public static WorkplaceEmployees getWorkplaceEmployees(Connection conn, Integer workplaceId) {
		return getWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), workplaceId);
	}
	
	public static EventsWorkplace setWorkplaceEmployees(Connection conn, EventsWorkplace updateEventsWorkplace) {
		return setWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), updateEventsWorkplace);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static WorkplaceEmployees getWorkplaceEmployeesInformation(DSLContext dslContext, Integer workplaceId) {
		WorkplaceEmployees workplaceEmployees = null;
		System.out.println("Workplace :"+workplaceId);
		
		Result<Record> result = dslContext.select()
				.from(PERSON)
				.innerJoin(CONTRACT)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.where(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(CONTRACT.ID.gt(0))
				.fetch();
		
		ArrayList<Triplet<Integer, String, String>> listEmployeeInfo = new ArrayList<>();
		for(Record r : result){
			Integer id = r.get(CONTRACT.ID);
			String name = r.get(PERSON.NAME);
			String surName = r.get(PERSON.FIRST_SURNAME) + " " + r.get(PERSON.SECOND_SURNAME);
			Triplet<Integer, String, String> triplet = new Triplet<Integer, String, String>(id, name, surName);
			listEmployeeInfo.add(triplet);
		}
		
		workplaceEmployees = new WorkplaceEmployees(listEmployeeInfo);
		
		return workplaceEmployees;
	}
	
	
	private static EventsWorkplace setWorkplaceEmployeesInformation(DSLContext dslContext,
			EventsWorkplace updateEventsWorkplace) {
		
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_VACACIONES");
		
		Integer numBorrado = 0;
		Integer old_employeeId = null;
		
		for(Quintet<Integer, String, java.util.Date, java.util.Date, String> varQuintet : updateEventsWorkplace.getUpdateEventsWorkplace()){
			Integer employeeId = varQuintet.getContractId();
			if(old_employeeId != employeeId){
				numBorrado++;
				deleteEmployeeEventsVariables(dslContext, employeeId);
				old_employeeId = employeeId;
			}
			
			addEmployeeEventsVariable(
					dslContext, 
					employeeId, 
					varQuintet.getVarName(), 
					varQuintet.getStart_date(),
					varQuintet.getEnd_date(),
					varQuintet.getExpression(),
					varNotToUpdate);
		}
		
		System.out.println("Numero de variables :"+ updateEventsWorkplace.getUpdateEventsWorkplace().size());
		System.out.println("Numero de borrados :"+numBorrado);		
		
		return updateEventsWorkplace;
	}

	private static void deleteEmployeeEventsVariables(DSLContext dslContext, Integer employeeContractId) {
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(employeeContractId))
		   .and(CONTRACT_DATA.NAME.in(
				   ContextVariable.WORKED_DAYS.getName()
				  ,ContextVariable.LEAVE_DAYS.getName()
				  ,ContextVariable.WORKED_HOURS.getName()
				  ,ContextVariable.REAL_DAYS.getName()
//				  ,ContextVariable.ERE_DAYS.getName()
//				  ,ContextVariable.STRIKE_DAYS.getName()
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
		
	}
	
	private static void addEmployeeEventsVariable(DSLContext dslContext, Integer employeeContractId, String varName,
			java.util.Date start_date, java.util.Date end_date, String expression,
			ArrayList<String> varNotToUpdate) {
		
		Integer domain = dslContext.select(CONTRACT.DOMAIN)
				.from(CONTRACT)
				.where(CONTRACT.ID.eq(employeeContractId))
				.fetchOne().value1();
		
		if(!varNotToUpdate.contains(varName))
			if(null != expression){
				
				Date sqlStartDate = new Date(start_date.getTime());
				Date sqlEndDate = new Date(end_date.getTime());
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
						CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(domain, varName, employeeContractId, expression, 
								sqlStartDate, sqlEndDate)
						.execute();
			}
	}
	
}
