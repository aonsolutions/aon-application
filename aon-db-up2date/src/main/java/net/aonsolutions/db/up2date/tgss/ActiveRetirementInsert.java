package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.DeductionConcept;
import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.SystemCost;
import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.SystemDeduction;

import net.aonsolutions.db.up2date.Update;

public class ActiveRetirementInsert implements Update {

    public static final Integer DOMAIN = -2;
    public static final ActiveRetirementInsert ACTIVERETIREMENTINSERT = new ActiveRetirementInsert();

    private ActiveRetirementInsert() {
    }

    @Override
    public void upgrade(Connection connection) {
	Settings settings;
	DSLContext dslContext;

	settings = new Settings();
	settings.setRenderSchema(false);
	settings.setParamType(ParamType.INLINED);

	dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

	boolean upgraded = dslContext
		.fetchCount(dslContext.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))) > 0;

	if (upgraded)
	    return;

	dslContext.transaction((config) -> {

	    dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
	    
	    // All payments. PREST_IT, PAGO_DIRECTO, INDENIZACION...
	    dslContext
	    .insertInto(SYSTEM_PAYMENT)
	    .columns(
		    SYSTEM_PAYMENT.DOMAIN, 
		    SYSTEM_PAYMENT.START_DATE,
		    SYSTEM_PAYMENT.END_DATE,
		    SYSTEM_PAYMENT.PAYMENT_CONCEPT, 
		    SYSTEM_PAYMENT.DESCRIPTION, 
		    SYSTEM_PAYMENT.EXPRESSION, 
		    SYSTEM_PAYMENT.IRPF_EXPRESSION, 
		    SYSTEM_PAYMENT.QUOTE_EXPRESSION,
		    SYSTEM_PAYMENT.MONTH,
		    SYSTEM_PAYMENT.SALARY_TYPE,
		    SYSTEM_PAYMENT.DESCRIPTION_DECORABLE)
	    .select(DSL.select(
		    DSL.val(DOMAIN), 
		    SYSTEM_PAYMENT.START_DATE,
		    SYSTEM_PAYMENT.END_DATE,
		    SYSTEM_PAYMENT.PAYMENT_CONCEPT, 
		    SYSTEM_PAYMENT.DESCRIPTION, 
		    SYSTEM_PAYMENT.EXPRESSION, 
		    SYSTEM_PAYMENT.IRPF_EXPRESSION, 
		    SYSTEM_PAYMENT.QUOTE_EXPRESSION,
		    SYSTEM_PAYMENT.MONTH,
		    SYSTEM_PAYMENT.SALARY_TYPE,
		    SYSTEM_PAYMENT.DESCRIPTION_DECORABLE)
		    .from(SYSTEM_PAYMENT)
		    .where(SYSTEM_PAYMENT.DOMAIN.eq(0)))
	    .execute();

	    // CGC Deduction
	    dslContext
	    .insertInto(SYSTEM_DEDUCTION)
	    .columns(
		    SYSTEM_DEDUCTION.DOMAIN, 
		    SYSTEM_DEDUCTION.TYPE,
		    SYSTEM_DEDUCTION.MONTH,
		    SYSTEM_DEDUCTION.START_DATE,
		    SYSTEM_DEDUCTION.END_DATE,
		    SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, 
		    SYSTEM_DEDUCTION.DESCRIPTION, 
		    SYSTEM_DEDUCTION.EXPRESSION,
		    SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE)
	    .select(DSL.select(
		    DSL.val(DOMAIN), 
		    SYSTEM_DEDUCTION.TYPE,
		    SYSTEM_DEDUCTION.MONTH,
		    SYSTEM_DEDUCTION.START_DATE,
		    SYSTEM_DEDUCTION.END_DATE,
		    SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, 
		    SYSTEM_DEDUCTION.DESCRIPTION, 
		    SYSTEM_DEDUCTION.EXPRESSION,
		    SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE)
		    .from(SYSTEM_DEDUCTION)
		    .innerJoin(DEDUCTION_CONCEPT).onKey()
		    .where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
		    .and(DEDUCTION_CONCEPT.CODE.in("CGC")))
	    .execute();

	    // CGC Cost
	    dslContext
	    .insertInto(SYSTEM_COST)
	    .columns(
		    SYSTEM_COST.DOMAIN, 
		    SYSTEM_COST.TYPE,
		    SYSTEM_COST.START_DATE,
		    SYSTEM_COST.END_DATE,
		    SYSTEM_COST.CODE, 
		    SYSTEM_COST.DESCRIPTION, 
		    SYSTEM_COST.EXPRESSION)
	    .select(DSL.select(
		    DSL.val(DOMAIN), 
		    SYSTEM_COST.TYPE,
		    SYSTEM_COST.START_DATE,
		    SYSTEM_COST.END_DATE,
		    SYSTEM_COST.CODE, 
		    SYSTEM_COST.DESCRIPTION, 
		    SYSTEM_COST.EXPRESSION)
		    .from(SYSTEM_COST)
		    .where(SYSTEM_COST.DOMAIN.eq(0))
		    .and(SYSTEM_COST.CODE.in("CGC_E", "IT_E", "IMS_E")))
	    .execute();

	    // CGC Percentss
	    dslContext
	    .insertInto(SYSTEM_DATA)
	    .columns(
		    SYSTEM_DATA.DOMAIN,
		    SYSTEM_DATA.NAME,
		    SYSTEM_DATA.EXPRESSION,
		    SYSTEM_DATA.START_DATE,
		    SYSTEM_DATA.END_DATE,
		    SYSTEM_DATA.COMMENTS)
	    .select(DSL.select(
		    DSL.val(DOMAIN), 
		    SYSTEM_DATA.NAME,
		    DSL.val("0.25"), 
		    SYSTEM_DATA.START_DATE,
		    SYSTEM_DATA.END_DATE,
		    SYSTEM_DATA.COMMENTS)
		    .from(SYSTEM_DATA)
		    .where(SYSTEM_DATA.DOMAIN.eq(0))
		    .and(SYSTEM_DATA.NAME.in("PORCENTAJE_CGC")))
	    .execute();

	    dslContext
	    .insertInto(SYSTEM_DATA)
	    .columns(
		    SYSTEM_DATA.DOMAIN,
		    SYSTEM_DATA.NAME,
		    SYSTEM_DATA.EXPRESSION,
		    SYSTEM_DATA.START_DATE,
		    SYSTEM_DATA.END_DATE,
		    SYSTEM_DATA.COMMENTS)
	    .select(DSL.select(
		    DSL.val(DOMAIN), 
		    SYSTEM_DATA.NAME,
		    DSL.val("1.30"), 
		    SYSTEM_DATA.START_DATE,
		    SYSTEM_DATA.END_DATE,
		    SYSTEM_DATA.COMMENTS)
		    .from(SYSTEM_DATA)
		    .where(SYSTEM_DATA.DOMAIN.eq(0))
		    .and(SYSTEM_DATA.NAME.in("PORCENTAJE_CGC_E")))
	    .execute();
	    
	    dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
	});
    }

}
