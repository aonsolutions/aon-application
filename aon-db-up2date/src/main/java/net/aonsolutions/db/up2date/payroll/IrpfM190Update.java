package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class IrpfM190Update implements Update {

	public static final IrpfM190Update IRPFM190UPDATE = new IrpfM190Update();
	
	private IrpfM190Update() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSalaryEmployeeInfo(dslContext);

			updateSalaryPayments(dslContext);

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

	private void updateSalaryEmployeeInfo(DSLContext dslContext) {
		Result<Record> salaryM190Records = dslContext.select().from(SALARY)
			.where(SALARY.TYPE.eq((byte)7)) //M190
			.and(SALARY.EMPLOYEE_NAME.isNull())
			.and(SALARY.EMPLOYEE_DOCUMENT.isNull())
			.fetch();
		
		for(Record salaryM190Record : salaryM190Records) {
			Integer salaryId = salaryM190Record.get(SALARY.ID);
			Integer contractId = salaryM190Record.get(SALARY.CONTRACT);
			
			Record registryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.in(
							dslContext.select(CONTRACT.PERSON).from(CONTRACT)
								.where(CONTRACT.ID.eq(contractId))
								.fetchOne(CONTRACT.PERSON)
					)).fetchOne();
			
			String document = registryRecord.get(REGISTRY.DOCUMENT);
			
			Record personRecord = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.in(
							dslContext.select(CONTRACT.PERSON).from(CONTRACT)
								.where(CONTRACT.ID.eq(contractId))
								.fetchOne(CONTRACT.PERSON)
					)).fetchOne();
			
			String fullName = getFullName(personRecord);
			
			dslContext.update(SALARY)
				.set(SALARY.EMPLOYEE_DOCUMENT, document)
				.set(SALARY.EMPLOYEE_NAME, fullName)
				.where(SALARY.ID.eq(salaryId))
				.execute();
		}
	}

	private String getFullName(Record personRecord) {
		StringBuilder fullName = new StringBuilder();
		
		if(null != personRecord.get(PERSON.FIRST_SURNAME))
			fullName.append(personRecord.get(PERSON.FIRST_SURNAME));
		
		if(null != personRecord.get(PERSON.SECOND_SURNAME))
			fullName.append(" " + personRecord.get(PERSON.SECOND_SURNAME));
		
		if(null != personRecord.get(PERSON.NAME))
			fullName.append(fullName.length() == 0 ? personRecord.get(PERSON.NAME) : ", " + personRecord.get(PERSON.NAME));
			
		return fullName.toString();
	}

	private void updateSalaryPayments(DSLContext dslContext) {
		Result<Record> salaryM190Records = dslContext.select().from(SALARY)
			.where(SALARY.TYPE.eq((byte)7)) //M190
			.fetch();
		
		for(Record salaryM190Record : salaryM190Records) {
			Integer salaryId = salaryM190Record.get(SALARY.ID);
			
			Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.eq(salaryId))
					.fetch();
			
			if(salaryPaymentRecords.isEmpty()) {
				Integer domainId = salaryM190Record.get(SALARY.DOMAIN);
				Double moneyBase = salaryM190Record.get(SALARY.MONEY_IRPF_BASE);
				Double inkindBase = salaryM190Record.get(SALARY.INKIND_IRPF_BASE);
				
				dslContext.insertInto(SALARY_PAYMENT)
					.set(SALARY_PAYMENT.DOMAIN, domainId)
					.set(SALARY_PAYMENT.SALARY, salaryId)
					.set(SALARY_PAYMENT.TYPE, (byte)1)
					.set(SALARY_PAYMENT.DESCRIPTION, "RETRIBUCI\u00d3N NO INCLUIDA OTROS APARTADOS (M190)")
					.set(SALARY_PAYMENT.AMOUNT, moneyBase)
					.set(SALARY_PAYMENT.IRPF, moneyBase)
					.set(SALARY_PAYMENT.QUOTE, moneyBase)
					.execute();
			
				dslContext.insertInto(SALARY_PAYMENT)
					.set(SALARY_PAYMENT.DOMAIN, domainId)
					.set(SALARY_PAYMENT.SALARY, salaryId)
					.set(SALARY_PAYMENT.TYPE, (byte)13)
					.set(SALARY_PAYMENT.DESCRIPTION, "RETRIBUCI\u00d3N EN ESPECIE (M190)")
					.set(SALARY_PAYMENT.AMOUNT, inkindBase)
					.set(SALARY_PAYMENT.IRPF, inkindBase)
					.set(SALARY_PAYMENT.QUOTE, inkindBase)
					.execute();
			}
		}
	}
}
