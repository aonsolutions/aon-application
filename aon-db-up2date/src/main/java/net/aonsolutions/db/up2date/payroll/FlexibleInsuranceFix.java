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

public class FlexibleInsuranceFix implements Update {

	public static final FlexibleInsuranceFix FLEXIBLEINSURANCEFIX = new FlexibleInsuranceFix();
	
	private static final String CHECK_INSURANCE = 
			"SELF.addBonus("
			+ "'AVISO("
			+ "\"<div>Retribuci\u00F3n flexible seguro m\u00E9dico tiene un l\u00EDmite anual de 500\u20AC/persona, mensual 41,67\u20AC/persona  (\"+BENEFICIARIOS_SEGURO+\" personas x 41,67 = \"+(BENEFICIARIOS_SEGURO*41.67)+\"). La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\""
			+ ")')";
	

	private FlexibleInsuranceFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		String irpfExpression = String.format("(ABS(_P) > (BENEFICIARIOS_SEGURO * 41.67)) ? %s; -(BENEFICIARIOS_SEGURO * 41.67 ) : _P", CHECK_INSURANCE );
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_SEGURO"))
		.and(PAYMENT_CONCEPT.IRPF_EXPRESSION.eq(irpfExpression))
		) >= 1;
		
		
		if ( upgraded )
			return;
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, irpfExpression)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_SEGURO"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
