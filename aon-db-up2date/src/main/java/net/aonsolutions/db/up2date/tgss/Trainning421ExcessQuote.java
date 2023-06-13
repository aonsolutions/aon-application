package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemCostRecord;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class Trainning421ExcessQuote implements Update {

	public static Trainning421ExcessQuote TRAINNING421EXCESSQUOTE = new Trainning421ExcessQuote();

	private static final int DOMAIN = -101;
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq("BASE_EXCESO"))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))) > 0;

		if ( upgraded ) 
			return;		
		
		
		InsertOnDuplicateStep<SystemDataRecord> insert2023ExcessQuote = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, "BASE_EXCESO")
		.set(SYSTEM_DATA.EXPRESSION, "__EXCESO = MAX(0.00, ROUND(BASE_CGC - BASE_CGP_MIN,0)); __EXCESO > 0.00 ? __EXCESO : UNDEFINED(\"BASE_EXCESO\")")
		.set(SYSTEM_DATA.START_DATE, start2023Date)
		;

		UpdateConditionStep<SystemDeductionRecord> updateConstantDesmplSystemDeduciton = 
		dslContext.update(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.START_DATE, start2023Date)
		.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DEDUCTION.END_DATE.isNull())
		.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("DESMPL")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0))));
		
		InsertOnDuplicateStep<SystemDeductionRecord> insertExcessSystemDeductions =
		dslContext
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN,DOMAIN)
		.set(SYSTEM_DEDUCTION.START_DATE,start2023Date)
		.set(SYSTEM_DEDUCTION.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_CGC/100" )
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT , DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("CGC")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0)))
		.newRecord()
		.set(SYSTEM_DEDUCTION.DOMAIN,DOMAIN)
		.set(SYSTEM_DEDUCTION.START_DATE,start2023Date)
		.set(SYSTEM_DEDUCTION.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_DESMPL/100" )
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT , DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("DESMPL")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0)))
		.newRecord()
		.set(SYSTEM_DEDUCTION.DOMAIN,DOMAIN)
		.set(SYSTEM_DEDUCTION.START_DATE,start2023Date)
		.set(SYSTEM_DEDUCTION.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FP/100" )
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT , DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("FP")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0)))
		.newRecord()
		.set(SYSTEM_DEDUCTION.DOMAIN,DOMAIN)
		.set(SYSTEM_DEDUCTION.START_DATE,start2023Date)
		.set(SYSTEM_DEDUCTION.EXPRESSION,"BASE_ESTR * COTIZA_EXCESO * PORCENTAJE_EXTR/100" )
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT , DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("ESTR")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0)))
		.newRecord()
		.set(SYSTEM_DEDUCTION.DOMAIN,DOMAIN)
		.set(SYSTEM_DEDUCTION.START_DATE,start2023Date)
		.set(SYSTEM_DEDUCTION.EXPRESSION,"BASE_NESTR * COTIZA_EXCESO * PORCENTAJE_NEXTR/100" )
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT , DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.CODE.eq("NESTR")).and(DEDUCTION_CONCEPT.DOMAIN.eq(0)))
		;
		
		
		UpdateConditionStep<SystemCostRecord> updateConstantDesmplSystemCost = 
		dslContext.update(SYSTEM_COST)
		.set(SYSTEM_COST.START_DATE, start2023Date)
		.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_COST.END_DATE.isNull())
		.and(SYSTEM_COST.CODE.eq("DESMPL_E"));

		InsertOnDuplicateStep<SystemCostRecord> insertExcessSystemCosts = 
		dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_CGC_E/100" )
		.set(SYSTEM_COST.CODE , "CGC_E")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100" )
		.set(SYSTEM_COST.CODE , "IT_E")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO *  (isdef PORCENTAJE_IMS ? PORCENTAJE_IMS : (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS)))/100" )
		.set(SYSTEM_COST.CODE , "IMS_E")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_DESMPL_E/100" )
		.set(SYSTEM_COST.CODE , "DESMPL_E")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FP_E/100" )
		.set(SYSTEM_COST.CODE , "FP_E")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN,DOMAIN)
		.set(SYSTEM_COST.START_DATE,start2023Date)
		.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FOGASA/100" )
		.set(SYSTEM_COST.CODE , "FOGASA_E")
		;


		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			insert2023ExcessQuote.execute();
			
			updateConstantDesmplSystemCost.execute();
			updateConstantDesmplSystemDeduciton.execute();
			
			insertExcessSystemCosts.execute();
			insertExcessSystemDeductions.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
