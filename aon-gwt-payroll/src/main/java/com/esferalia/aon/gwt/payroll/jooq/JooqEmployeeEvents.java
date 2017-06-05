package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEmployeeEvents {

	private static Settings SETTINGS = null;

	public static EmployeeEventsData getEmployeeEvents(Connection conn, Integer contract) throws IllegalArgumentException {
		return getEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static void setEmployeeEvents(Connection conn, Integer contract, EmployeeCalendarUpdate updateInfo){
		setEmployeeEventsInformation(DSL.using(conn, getDefaultSettings()), contract, updateInfo);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static EmployeeEventsData getEmployeeEventsInformation(DSLContext dslContext, Integer contract) {
		
		EmployeeEventsData employeeInfoVariablesEvents = null;
		
		Map<String,ArrayList<Quartet<Date, Date, String, String>>> employeeVariablesEvents = new HashMap<String,ArrayList<Quartet<Date, Date, String, String>>>(); 
		ArrayList<Quartet<Date, Date, String, String>> workedDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		ArrayList<Quartet<Date, Date, String, String>> ereDaysVaribaleList = new ArrayList<Quartet<Date, Date, String, String>>();
		
		// ---------------------------------------------- DIAS TRABAJADOS ---------------------------------------------------------
		
		Result<Record> contractWorkedDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.WORKED_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractWorkedDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterWorkedDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterWorkedDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			workedDaysVaribaleList.add(quarterWorkedDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("HORAS_TRABAJADAS", workedDaysVaribaleList);
		
		
		// ---------------------------------------------- DIAS ERE ---------------------------------------------------------
		
		Result<Record> contractEREDaysEmployeeInfo = dslContext
				  .select()
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.eq(ContextVariable.ERE_DAYS.getName()))
				  .fetch();
		
		for(Record r: contractEREDaysEmployeeInfo){
			Quartet<Date, Date, String, String> quarterEREDaysEmployeeInfo = new Quartet<Date, Date, String, String>();
			
			quarterEREDaysEmployeeInfo.setStartDate(r.get(CONTRACT_DATA.START_DATE))
			.setEndDate(r.get(CONTRACT_DATA.END_DATE))
			.setName(r.get(CONTRACT_DATA.NAME))
			.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
			
			ereDaysVaribaleList.add(quarterEREDaysEmployeeInfo);
		}
		
		employeeVariablesEvents.put("DIAS_ERE", ereDaysVaribaleList);
		
		
		// ---------------------------------------------- SOLUCION ---------------------------------------------------------		
		return employeeInfoVariablesEvents;
	}
	
	private static void setEmployeeEventsInformation(DSLContext using, Integer contract,
			EmployeeCalendarUpdate updateInfo) {
		// TODO Auto-generated method stub
		
	}

}
