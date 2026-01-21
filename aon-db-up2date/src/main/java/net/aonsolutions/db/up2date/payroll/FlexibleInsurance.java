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

public class FlexibleInsurance implements Update {

	public static final FlexibleInsurance FLEXIBLEINSURANCE = new FlexibleInsurance();
	
	private static final String CHECK_EXPRESSION = 
			"SELF.addBonus("
			+ "'CHECK(ABS(FLEXIBLE) <= (0.30*(ABS(FLEXIBLE)+TOTAL_DEVENGADO)),"
			+ "\"<div>Retribuci\u00F3n flexible no podr\u00E1n exceder el 30% del salario bruto anual</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ");"
			+ "REMOVE()')";

	private static final String CHECK_INSURANCE = 
			"SELF.addBonus("
			+ "'AVISO("
			+ "\"<div>Retribuci\u00F3n flexible seguro m\u00E9dico tiene un l\u00EDmite de 500\u20AC/persona (\"+BENEFICIARIOS_SEGURO+\" personas x 500.00 = \"+(BENEFICIARIOS_SEGURO*500.00)+\"). La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ")')";
	

	private FlexibleInsurance() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		String expression = String.format("%s;CHECK_DEF({'BENEFICIARIOS_SEGURO', 'CUOTA_SEGURO'});-ABS(/*user*/BENEFICIARIOS_SEGURO * CUOTA_SEGURO/**/)",CHECK_EXPRESSION );
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_SEGURO"))
		.and(PAYMENT_CONCEPT.EXPRESSION.eq(expression))
		) >= 1;
		
		
		if ( upgraded )
			return;
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, expression)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, String.format("(ABS(_P) > (BENEFICIARIOS_SEGURO * 500.00)) ? %s; -(BENEFICIARIOS_SEGURO * 500.00) : _P", CHECK_INSURANCE ))
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_SEGURO"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
