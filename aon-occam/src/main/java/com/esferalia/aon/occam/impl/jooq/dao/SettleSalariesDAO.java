package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jooq.Record;
import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.watson.error.AonCoreException;

public class SettleSalariesDAO {
	
	private static java.sql.Date parseToSQLDate(Date date){
		return null == date ? null : new java.sql.Date(date.getTime());
	}
	
	private static String formatDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return simpleDateFormat.format(date);
	}
	

	public static void createSettleSalaries(CloseableAONContext ctx, Date date) {
		Result<Record> salaries = ctx.getDslContext().select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(RPAYMETHOD).on(RPAYMETHOD.REGISTRY.eq(CONTRACT.PERSON))
				.leftOuterJoin(RBANK).on(RBANK.ID.eq(RPAYMETHOD.RBANK))
				.where(SALARY.DOMAIN.eq(ctx.getDomainId()))
				.and(SALARY.ISSUE_DATE.eq(parseToSQLDate(date)))
				.and(SALARY.TYPE.lt((byte)4)) // Nomina, Extra, Finiquito, Atraso
				.fetch();
		
		if(salaries.isEmpty()) throw new AonCoreException("No existen n\u00f3minas sobre las que generar un vencimiento para el periodo " + formatDate(date));
		
		boolean hasSettleSalaryModify = false;
		
		for(Record record : salaries) {
			// Delete finance for this registry which status == 0 (Pediente)
			ctx.getDslContext().deleteFrom(FINANCE)
				.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
				.and(FINANCE.DUE_DATE.eq(parseToSQLDate(date)))
				.and(FINANCE.REGISTRY.eq(record.get(REGISTRY.ID)))
				.and(FINANCE.PAYROLL.eq((byte)1))
				.and(FINANCE.STATUS.eq((byte)0))
				.execute();
			
			String concept = getSalaryType(record.get(SALARY.TYPE));
			
			Result<FinanceRecord> finances = ctx.getDslContext().selectFrom(FINANCE)
					.where(FINANCE.DOMAIN.eq(ctx.getDomainId()))
					.and(FINANCE.DUE_DATE.eq(parseToSQLDate(date)))
					.and(FINANCE.REGISTRY.eq(record.get(REGISTRY.ID)))
					.and(FINANCE.PAYROLL.eq((byte)1))
					.and(FINANCE.CONCEPT.like(concept + "%"))
					.fetch();
			
			if(finances.isEmpty()) {
				// Insert new salary finance
				ctx.getDslContext().insertInto(FINANCE)
					.set(FINANCE.DOMAIN, ctx.getDomainId())
					.set(FINANCE.PAYMENT, (byte)1)
					.set(FINANCE.REGISTRY, record.get(REGISTRY.ID))
					.set(FINANCE.RDOCUMENT, record.get(REGISTRY.DOCUMENT))
					.set(FINANCE.RDOCUMENT_TYPE, record.get(REGISTRY.DOCUMENT_TYPE))
					.set(FINANCE.RDOCUMENT_COUNTRY, record.get(REGISTRY.DOCUMENT_COUNTRY))
					.set(FINANCE.RNAME, record.get(REGISTRY.NAME))
					.set(FINANCE.AMOUNT, record.get(SALARY.TOTAL_LIQUID))
					.set(FINANCE.CONCEPT, concept + " - " + formatDate(date))
					.set(FINANCE.DUE_DATE, parseToSQLDate(date))
					.set(FINANCE.PAY_METHOD, record.get(RPAYMETHOD.PAY_METHOD))
					.set(FINANCE.BANK_ACCOUNT, record.get(RBANK.BANK_ACCOUNT))
					.set(FINANCE.BANK_ALIAS, record.get(RBANK.ALIAS))
					.set(FINANCE.BIC, record.get(RBANK.BIC))
					.set(FINANCE.SCOPE, record.get(WORKPLACE.SCOPE))
					.set(FINANCE.PAYROLL, (byte)1)
					.set(FINANCE.SOURCE_ID, record.get(SALARY.ID))
					.set(FINANCE.CREATION_DATE, new Timestamp(new Date().getTime()))
					.set(FINANCE.CREATION_USER, ctx.getUser())
					.execute();
				
				hasSettleSalaryModify = true;
			} else {
				// Check if existing amount is same as salary
				Double salaryAmount = record.get(SALARY.TOTAL_LIQUID);
				Double financeAmount = finances.stream().mapToDouble(finance -> finance.getAmount()).sum();
				
				if(!salaryAmount.equals(financeAmount)) {
					Double amountDiff = salaryAmount - financeAmount;
					// Insert new salary diff finance
					ctx.getDslContext().insertInto(FINANCE)
						.set(FINANCE.DOMAIN, ctx.getDomainId())
						.set(FINANCE.PAYMENT, (byte)1)
						.set(FINANCE.REGISTRY, record.get(REGISTRY.ID))
						.set(FINANCE.RDOCUMENT, record.get(REGISTRY.DOCUMENT))
						.set(FINANCE.RDOCUMENT_TYPE, record.get(REGISTRY.DOCUMENT_TYPE))
						.set(FINANCE.RDOCUMENT_COUNTRY, record.get(REGISTRY.DOCUMENT_COUNTRY))
						.set(FINANCE.RNAME, record.get(REGISTRY.NAME))
						.set(FINANCE.AMOUNT, amountDiff)
						.set(FINANCE.CONCEPT, concept + " - " + formatDate(date))
						.set(FINANCE.DUE_DATE, parseToSQLDate(date))
						.set(FINANCE.PAY_METHOD, record.get(RPAYMETHOD.PAY_METHOD))
						.set(FINANCE.BANK_ACCOUNT, record.get(RBANK.BANK_ACCOUNT))
						.set(FINANCE.BANK_ALIAS, record.get(RBANK.ALIAS))
						.set(FINANCE.BIC, record.get(RBANK.BIC))
						.set(FINANCE.SCOPE, record.get(WORKPLACE.SCOPE))
						.set(FINANCE.PAYROLL, (byte)1)
						.set(FINANCE.SOURCE_ID, record.get(SALARY.ID))
						.set(FINANCE.CREATION_DATE, new Timestamp(new Date().getTime()))
						.set(FINANCE.CREATION_USER, ctx.getUser())
						.execute();
					
					hasSettleSalaryModify = true;
				}
			}	
		}
		
		if(!hasSettleSalaryModify) throw new AonCoreException("No existen modificaciones en las n\u00f3minas sobre los vencimiento ya generados para el periodo " + formatDate(date));
	}

	private static String getSalaryType(Byte salaryType) {
		switch (salaryType) {
		case 1:
			return "EXTRA";
		case 2:
			return "FINIQUITO";
		case 3:
			return "ATRASO";
		default:
			return "N\u00d3MINA";
		}
	}
	
}
