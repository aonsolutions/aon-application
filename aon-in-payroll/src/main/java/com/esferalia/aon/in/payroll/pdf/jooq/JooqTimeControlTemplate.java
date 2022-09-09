package com.esferalia.aon.in.payroll.pdf.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.jooq.Record7;
import org.jooq.impl.DSL;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.timecontrol.TimeControlTemplate;
import com.esferalia.aon.in.payroll.pdf.maker.timecontrol.bean.EmployeeData;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;

public class JooqTimeControlTemplate {
//	public static void main(String[] args) throws CanNotCreatePdfException, FileNotFoundException {
//		String domain = "b72384936-ayudat.aonsolutions.net";
//		Integer enterpriseId = 804414;
//		Calendar cal = Calendar.getInstance();
//		cal.set(Calendar.YEAR, 2020);
//		Date period = cal.getTime();
//		OutputStream os = new FileOutputStream("/home/igonzalez/Escritorio/pedefes/actual.pdf");
//		generateTimeControlTemplate(os, domain, "", enterpriseId, period);
//		
//	}
	public static void generateTimeControlTemplate(OutputStream os, String domainName, String user, Integer enterpriseId, Date period) throws CanNotCreatePdfException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(period);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		java.sql.Date sqlDate = new java.sql.Date(cal.getTimeInMillis());
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, user)) {
			
			Registry ent = REGISTRY.as("ent");
			Registry worker = REGISTRY.as("worker");
			
			Stream<Record7<String, String, String, String, String, String, String>> queryStream = aonContext.getDslContext()
			.select(ent.NAME, ent.DOCUMENT, worker.DOCUMENT, worker.NAME, PERSON.SOCIAL_SECURITY_NUM, CONTRACT_DATA.NAME, CONTRACT_DATA.EXPRESSION)
			.from(CONTRACT)
			.innerJoin(CONTRACT_DATA).onKey()
			.innerJoin(WORKPLACE).onKey()
			.innerJoin(PERSON).onKey()
			.innerJoin(worker).on(PERSON.REGISTRY.eq(worker.ID))
			.innerJoin(ent).on(WORKPLACE.ENTERPRISE.eq(ent.ID))
			.where(CONTRACT.DOMAIN.eq(aonContext.getDomainId()))
			.and(WORKPLACE.ENTERPRISE.eq(enterpriseId))
			.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(sqlDate)))
			.and(CONTRACT_DATA.NAME.eq("TC2"))
//			.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.eq(CONTRACT.END_DATE)))
			.and(CONTRACT_DATA.START_DATE.eq(DSL.select(DSL.max(CONTRACT_DATA.START_DATE)).from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID).and(CONTRACT_DATA.NAME.eq("TC2")))))
			.orderBy(worker.NAME.asc())
			.fetchStream();
			String[] enterpriseInfo = new String[2];
			LinkedList<EmployeeData> employees = new LinkedList<EmployeeData>();
			queryStream.forEach(r -> {
				enterpriseInfo[0] = r.get(ent.NAME);
				enterpriseInfo[1] = r.get(ent.DOCUMENT);
				
				String employeeName = r.get(worker.NAME);
				String dni = r.get(worker.DOCUMENT);
				String naf = r.get(PERSON.SOCIAL_SECURITY_NUM);
				String contract = r.get(CONTRACT_DATA.EXPRESSION) != null ? r.get(CONTRACT_DATA.EXPRESSION).replaceAll("\"", "") : null;
				
				employees.add(new EmployeeData(employeeName, contract, naf, dni));
			});
			
			TimeControlTemplate.print(os, enterpriseInfo[0], enterpriseInfo[1], employees, period);
			
		}
		
	}
}
