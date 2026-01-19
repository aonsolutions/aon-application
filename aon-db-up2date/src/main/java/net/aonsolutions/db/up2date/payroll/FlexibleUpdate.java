package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class FlexibleUpdate implements Update {

	public static final FlexibleUpdate FLEXIBLEUPDATE = new FlexibleUpdate();
	
	private static final String CHECK_EXPRESSION = 
			"SELF.addBonus("
			+ "'CHECK(ABS(FLEXIBLE) <= (0.30*(ABS(FLEXIBLE)+TOTAL_DEVENGADO)),"
			+ "\"<div>Retribuci\u00F3n flexible no podr\u00E1n exceder el 30% del salario bruto anual</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ");"
			+ "REMOVE()')";
	
	private static final String CHECK_TICKETS = 
			"SELF.addBonus("
			+ "'AVISO("
			+ "\"<div>Retribuci\u00F3n flexible tickets restaurante/comida tiene un l\u00EDmite diario de 11 euros (\"+DIAS_LABORABLES+\" d\u00EDas x 11 = \"+(DIAS_LABORABLES*11.00)+\"). La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ")')";

	private static final String CHECK_TRANSPORTE = 
			"SELF.addBonus("
			+ "'AVISO("
			+ "\"<div>Retribuci\u00F3n flexible transporte tiene un l\u00EDmite mensual de 136 euros. La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ")')";

	private FlexibleUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		String expression = String.format("%s;-ABS(/*user*/0.00/**/)",CHECK_EXPRESSION );
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
		.and(PAYMENT_CONCEPT.EXPRESSION.eq(expression))
		) >= 1;
		
		
		if ( upgraded )
			return;
		
		dslContext
		.alterTable(PAYMENT_CONCEPT)
		.alterColumn(PAYMENT_CONCEPT.IRPF_EXPRESSION)
		.set(SQLDataType.VARCHAR.length(1024))
		.execute();

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, expression)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
			.execute();

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, String.format("(ABS(_P) > 136.00) ? %s; -136.00 : _P", CHECK_TRANSPORTE ))
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.like("%TRANSPORTE%"))
			.execute();

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, String.format("(ABS(_P) > (11.0 * DIAS_LABORABLES)) ? %s; -(11.0 * DIAS_LABORABLES) : _P", CHECK_TICKETS ))
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
			.and(PAYMENT_CONCEPT.DESCRIPTION.like("%TICKETS%RESTAURANTE%"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
