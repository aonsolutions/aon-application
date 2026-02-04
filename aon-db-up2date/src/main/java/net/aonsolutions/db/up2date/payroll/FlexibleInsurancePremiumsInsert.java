package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class FlexibleInsurancePremiumsInsert implements Update {

	public static final FlexibleInsurancePremiumsInsert FLEXIBLEINSURANCEPREMIUMSINSERT = new FlexibleInsurancePremiumsInsert();
	
	private FlexibleInsurancePremiumsInsert() {
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
		.and(PAYMENT_CONCEPT.CODE.like("FLEXIBLE_PRIMA_%"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)39)
			.set(PAYMENT_CONCEPT.EXPRESSION, "0.00" )
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE_PRIMA_TRABAJ")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE PRIMAS SEGURO ENFERMEDAD COM\u00DAN TRABAJ.")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)40)
			.set(PAYMENT_CONCEPT.EXPRESSION, "0.00" )
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE_PRIMA_FAMILIAR")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "RETRIBUCI\u00D3N FLEXIBLE PRIMAS SEGURO ENFERMEDAD COM\u00DAN FAMILIAR")
			.execute();
	
			PaymentConceptRecord dtoFlexibleConcept = 
			dslContext.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("DTO_FLEXIBLE"))
			.fetchAnyInto(PAYMENT_CONCEPT);
			
			String flexibleSumExpression =
			dslContext
			.select(PAYMENT_CONCEPT.CODE)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.like("FLEXIBLE%"))
			.groupBy(PAYMENT_CONCEPT.CODE)
			.fetchStreamInto(PAYMENT_CONCEPT)
			.map(PaymentConceptRecord::getCode)
			.map( code -> String.format("'%s'", code) )
			.collect(Collectors.joining(","));
			
			dslContext.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, String.format("/*hideable*/TOTAL_DEVENGADO;DTO=SUMIFDEF(%s);(DTO > 0) ? -DTO : HIDE()", flexibleSumExpression ) )
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(dtoFlexibleConcept.getId()))
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
