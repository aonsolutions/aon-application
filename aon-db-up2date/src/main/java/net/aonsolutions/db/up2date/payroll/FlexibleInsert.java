package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FlexibleInsert implements Update {

	public static final FlexibleInsert FLEXIBLEINSERT = new FlexibleInsert();
	
	private static final String CHECK_EXPRESSION = 
			"SELF.addBonus("
			+ "'CHECK(ABS(FLEXIBLE) <= (0.30*(ABS(FLEXIBLE)+TOTAL_DEVENGADO)),"
			+ "\"<div>Retribuci\u00F3n flexible no podr\u00E1n exceder el 30% del salario bruto anual</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ");"
			+ "REMOVE()')";
	
	private FlexibleInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE TRANSPORTE")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE FORMACI\u00D3N")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE TICKETS RESTAURANTE/COMIDA")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE SEGURO M\u00C9DICO")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE GUARDER\u00CDA/EDUCACI\u00D3N INFANTIL")
			.set(PAYMENT_CONCEPT.EXPRESSION, String.format("%s;-(/*user*/0.00/**/)",CHECK_EXPRESSION) )
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
