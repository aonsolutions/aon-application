package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SalaryPayment;

import net.aonsolutions.db.up2date.Update;

public class SalaryTypeUpdate implements Update {

	// 	----------------------------
	// 	 	 OLD		NEW
	// 	----------------------------
	//	0	SALARY	=  	SALARY
	//	1	EXTRA	=	EXTRA
	//	2	SETTLE	=	SETTLE
	//	3	DELAY	=	DELAY

	//	4	L00		=	UNKNOWN_4 
	//	5	L03 	=	UNKNOWN_5
	//	6	L13 	=	UNKNOWN_6
	//	7	M190 	=	UNKNOWN_7
	//  8	--		=	UNKNOWN_8
	//  9	--		=	PROCEDURAL
	
	// 	10	--		=	L00
	//  11	--		=	L02
	//  12	--		=	L03
	// 	13	--		=	L13

	//	14	--		=	UNKNOWN_14,
	//	15	--		=	UNKNOWN_15,
	//	16	--		=	UNKNOWN_16,
	//	17	--		=	UNKNOWN_17,
	//	18	--		=	UNKNOWN_18,
	//	19	--		=	UNKNOWN_19,
	
	//	20	--		=	M190		 	

	public static final SalaryTypeUpdate SALARY_TYPE_UPDATE = new SalaryTypeUpdate();

	private SalaryTypeUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select(SALARY)
		.from(SALARY)
		.where(SALARY.TYPE.in((byte)10, (byte)12, (byte)13, (byte)20))
		) >= 1;		
		
		if ( upgraded )
			return;
		
		dslContext.transaction( config -> {
			config.dsl()
			.update(SALARY)
			.set(SALARY.TYPE, 
			DSL.case_()
			.when(SALARY.TYPE.eq((byte)4), (byte)10) // 10 	--	=	L00		( 4  L00 = 	UNKNOWN_4) 
			.when(SALARY.TYPE.eq((byte)5), (byte)12) // 12 	--	=	L03 	( 5  L03 = 	UNKNOWN_5)
			.when(SALARY.TYPE.eq((byte)6), (byte)13) // 13 	--	=	L13 	( 6  L13 = 	UNKNOWN_6)
			
			.when(SALARY.TYPE.eq((byte)7), (byte)20) // 20  --	=	M190 	( 7  M190 = UNKNOWN_7)
			)
			.execute();

			config.dsl()
			.update(SALARY)
			.set(SALARY.TYPE, (byte) 9 )				//  9	--		=	PROCEDURAL
			.from(SALARY_PAYMENT)
			.where(SALARY_PAYMENT.SALARY.eq(SALARY.ID))
			.and(SALARY_PAYMENT.TYPE.eq((byte) 7)) 		// CRA0007 Salarios de Tramitación
			.execute();
		}
		);

	}
	
	
	

}
