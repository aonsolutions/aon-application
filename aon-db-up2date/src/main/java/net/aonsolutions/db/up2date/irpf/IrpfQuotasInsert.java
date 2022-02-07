package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;

import net.aonsolutions.db.up2date.Update;

public class IrpfQuotasInsert implements Update {
	
	public static IrpfQuotasInsert IRPFQUOTASINSERT = new IrpfQuotasInsert();
	
	private IrpfQuotasInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {

		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022 = new Date ( calendar.getTimeInMillis() );
		
		SelectConditionStep<Record1<Integer>> salaryUpgraded = 
		DSL
		.select(SALARY_DATA.SALARY)
		.from(SALARY_DATA)
		.where(SALARY_DATA.NAME.like("CRA_%IRPF"));

		dslContext.transaction( config -> {
			try {
			dslContext
			.select()
			.from(SALARY)
			.innerJoin(SALARY_PAYMENT).onKey()
			.where(SALARY.TYPE.lt((byte)4))
			.and(SALARY.ID.notIn(salaryUpgraded))
			.and(SALARY.ISSUE_DATE.ge(start2022))
			.and(SALARY.IRPF_BASE.gt(0.00))
			//.and(SALARY.TOTAL_IRPF.gt(0.00))
			.fetchGroups(SALARY.ID)
			.forEach((id, salaryResult) -> { 
				Map<Byte, Result<Record>> typeMap = 
				salaryResult.intoGroups(SALARY_PAYMENT.TYPE);
				
				BigDecimal sumIrpfBase = ZERO;
				BigDecimal sumIrpfQuota = ZERO;
				List<SalaryDataRecord> irpfBaseSalaryDataRecords = new ArrayList<>(typeMap.size());
				List<SalaryDataRecord> irpfQuotaSalaryDataRecords = new ArrayList<>(typeMap.size());
				
				for(Result<Record> typeResult : typeMap.values() ) {
					
					boolean last = irpfBaseSalaryDataRecords.size() == typeMap.size() -1;
					
					Byte type = typeResult.getValues(SALARY_PAYMENT.TYPE).stream().filter(Objects::nonNull).findFirst().orElse((byte) 0 );
					BigDecimal totalIrpfBase = typeResult.getValues(SALARY.IRPF_BASE).stream().map(BigDecimal::new).findFirst().orElse(ZERO);
					BigDecimal totalIrpfQuota = typeResult.getValues(SALARY.TOTAL_IRPF).stream().map(BigDecimal::new).findFirst().orElse(ZERO);
					
					Integer domain = typeResult.getValues(SALARY_PAYMENT.DOMAIN).stream().findFirst().get();
					Integer salary = typeResult.getValues(SALARY_PAYMENT.SALARY).stream().findFirst().get();
					Date startDate = typeResult.getValues(SALARY.START_DATE).stream().findFirst().get();
					Date endDate = typeResult.getValues(SALARY.END_DATE).stream().findFirst().get();

					BigDecimal irpfBase = last ?
							totalIrpfBase.subtract(sumIrpfBase)
							:typeResult.getValues(SALARY_PAYMENT.IRPF).stream().map(BigDecimal::new).reduce(ZERO, (d1, d2) -> d1.add(d2) );
					BigDecimal irpfQuota = last ? 
							totalIrpfQuota.subtract(sumIrpfQuota)
							:irpfBase.multiply(totalIrpfQuota).divide(totalIrpfBase,MathContext.DECIMAL128);
							
					irpfBase = irpfBase.setScale(2, RoundingMode.HALF_UP);
					irpfQuota = irpfQuota.setScale(2, RoundingMode.HALF_UP);
					
					sumIrpfBase = sumIrpfBase.add(irpfBase);
					sumIrpfQuota = sumIrpfQuota.add(irpfQuota);
					
					SalaryDataRecord irpfQuotaSalaryDataRecord = new SalaryDataRecord();
					irpfQuotaSalaryDataRecord.setDomain(domain);
					irpfQuotaSalaryDataRecord.setSalary(salary);
					irpfQuotaSalaryDataRecord.setStartDate(startDate);
					irpfQuotaSalaryDataRecord.setEndDate(endDate);
					irpfQuotaSalaryDataRecord.setName(String.format("CRA_00%02d_IRPF", type));
					irpfQuotaSalaryDataRecord.setExpression(String.valueOf(irpfQuota) );
					irpfQuotaSalaryDataRecords.add(irpfQuotaSalaryDataRecord);
					

					SalaryDataRecord irpfBaseSalaryDataRecord = new SalaryDataRecord();
					irpfBaseSalaryDataRecord.setDomain(domain);
					irpfBaseSalaryDataRecord.setSalary(salary);
					irpfBaseSalaryDataRecord.setStartDate(startDate);
					irpfBaseSalaryDataRecord.setEndDate(endDate);
					irpfBaseSalaryDataRecord.setName(String.format("CRA_00%02d_BASE", type));
					irpfBaseSalaryDataRecord.setExpression(String.valueOf(irpfBase) );
					irpfBaseSalaryDataRecords.add(irpfBaseSalaryDataRecord);
					
				}
				
				//irpfBaseSalaryDataRecords.forEach( r -> System.out.println(r.toString() ));
				//irpfQuotaSalaryDataRecords.forEach( r -> System.out.println(r.toString() ));
				dslContext.batchInsert(irpfBaseSalaryDataRecords).execute();
				dslContext.batchInsert(irpfQuotaSalaryDataRecords).execute();
			});
		} catch ( Throwable t ) {
			t.printStackTrace();
		}
			
		}) ;
		
	}
	

}
