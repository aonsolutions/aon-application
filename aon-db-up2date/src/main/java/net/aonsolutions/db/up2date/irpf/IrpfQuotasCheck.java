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
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;

import net.aonsolutions.db.up2date.Update;

public class IrpfQuotasCheck implements Update {
	
	public static IrpfQuotasCheck IRPFQUOTASCHECK = new IrpfQuotasCheck();
	
	private IrpfQuotasCheck() {
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
		
		Name SUM = DSL.name("summ"); 
		Name TOTAL = DSL.name("total"); 
		
		System.err.println();
		
		dslContext
		.select(
		SALARY.ID,		
		DSL.cast(SALARY.TOTAL_IRPF, SQLDataType.DECIMAL(15, 2)).as(TOTAL), 
		DSL.sum(DSL.cast(SALARY_DATA.EXPRESSION, SQLDataType.DECIMAL(15, 2))).as(SUM)
		)
		.from(SALARY)
		.innerJoin(SALARY_DATA).onKey()
		.where(SALARY.TYPE.lt((byte)4))
		.and(SALARY.ISSUE_DATE.ge(start2022))
		.and(SALARY_DATA.NAME.like("CRA_%_IRPF"))
		.groupBy(SALARY.ID)
		.stream()
		.filter( r -> r.value2().compareTo(r.value3()) != 0 )
		.forEach( r -> System.err.printf("ERROR [%d]: IRPF quota diffs %f == %f \r\n", r.value1(), r.value2(), r.value3()))
		;

		dslContext
		.select(
		SALARY.ID,		
		DSL.cast(SALARY.IRPF_BASE, SQLDataType.DECIMAL(15, 2)).as(TOTAL), 
		DSL.sum(DSL.cast(SALARY_DATA.EXPRESSION, SQLDataType.DECIMAL(15, 2))).as(SUM)
		)
		.from(SALARY)
		.innerJoin(SALARY_DATA).onKey()
		.where(SALARY.TYPE.lt((byte)4))
		.and(SALARY.ISSUE_DATE.ge(start2022))
		.and(SALARY_DATA.NAME.like("CRA_%_BASE"))
		.groupBy(SALARY.ID)
		.stream()
		.filter( r -> r.value2().compareTo(r.value3()) != 0 )
		.forEach( r -> System.err.printf("ERROR [%d]: IRPF base diffs %f == %f \r\n", r.value1(), r.value2(), r.value3()))
		;
	}
	

}
