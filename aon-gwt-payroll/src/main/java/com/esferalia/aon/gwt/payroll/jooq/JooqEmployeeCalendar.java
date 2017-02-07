package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.client.Quartet;

public class JooqEmployeeCalendar {

	private static Settings SETTINGS = null;

	public static void getHourEmployee(Connection conn, Integer contract) throws IllegalArgumentException {

		getHoursByDay(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

	private static void getHoursByDay(DSLContext dslContext, Integer contract) throws IllegalArgumentException {

		Quartet<Date, Date, String, String> infoEmployee = new Quartet<Date, Date, String, String>();
		
		Result<Record4<String, String, Date, Date>> contratoInfoEmpleado = dslContext
				  .select(CONTRACT_DATA.NAME, CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
				  .from(CONTRACT_DATA)
				  .where(CONTRACT_DATA.CONTRACT.eq(contract))
				  .and(CONTRACT_DATA.NAME.like("HORAS_%"))
				  .fetch();
		
		contratoInfoEmpleado.forEach(c -> 
				infoEmployee.setStartDate(c.get(CONTRACT_DATA.START_DATE))
							.setEndDate(c.get(CONTRACT_DATA.END_DATE))
							.setName(c.get(CONTRACT_DATA.NAME))
							.setExpression((c.get(CONTRACT_DATA.EXPRESSION)))
				);
	}

}
