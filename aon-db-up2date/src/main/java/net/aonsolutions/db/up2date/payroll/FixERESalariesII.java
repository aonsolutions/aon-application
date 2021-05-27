package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;

import java.math.BigDecimal;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.SalaryData;

import net.aonsolutions.db.up2date.Update;

public class FixERESalariesII implements Update {

	public static final FixERESalariesII FIXERESALARIESII = new FixERESalariesII();
	
	private FixERESalariesII() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
		dslContext.transaction( (config) -> {
			
			SalaryData ERE_FACTOR = SALARY_DATA.as(DSL.name("ERE_FACTOR"));
			
			
			Table<?> SALARY_HOURS = 
			SALARY_DATA.innerJoin(ERE_FACTOR)
			.on(ERE_FACTOR.NAME.startsWith("COEFICIENTE_ERE")
			.and(ERE_FACTOR.EXPRESSION.cast(SQLDataType.DECIMAL(5, 2)).lt(BigDecimal.valueOf(1.00)))
			.and(ERE_FACTOR.EXPRESSION.cast(SQLDataType.DECIMAL(5, 2)).gt(BigDecimal.valueOf(0.00)))
			.and(SALARY_DATA.SALARY.eq(ERE_FACTOR.SALARY))
			.and(SALARY_DATA.START_DATE.eq(ERE_FACTOR.START_DATE))
			.and(SALARY_DATA.END_DATE.eq(ERE_FACTOR.END_DATE))
			);
			
			
			Field<String> NAME = DSL.field(SALARY_DATA.NAME.getQualifiedName(), String.class);
			Field<String> EXPRESSION = DSL.field(SALARY_DATA.EXPRESSION.getQualifiedName(), String.class);
			
			int updated = 
			dslContext
			.update(SALARY_HOURS)
			.set(
			SALARY_HOURS.field(EXPRESSION), 
			DSL.concat(DSL.floor(SALARY_HOURS.field(EXPRESSION).cast(SQLDataType.DECIMAL(5, 2)).mul(DSL.field("1.0", SQLDataType.DECIMAL(5, 2)).minus(ERE_FACTOR.EXPRESSION.cast(SQLDataType.DECIMAL(5, 2))))).cast(String.class), ".0000")
			)
			.where(SALARY_HOURS.field(NAME).eq("HORAS_NOMINA"))
			.and(DSL.not(SALARY_HOURS.field(EXPRESSION).endsWith(".0000")))
			.execute()
			;
			
			System.out.println(updated);

			
		});
	}

}
