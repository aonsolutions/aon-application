package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.util.Arrays;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.ContractPayment;

import net.aonsolutions.db.up2date.Update;

public class FlexibleIrpfFix implements Update {

	public static final FlexibleIrpfFix FLEXIBLEIRPFFIX = new FlexibleIrpfFix();
	
	private FlexibleIrpfFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded = dslContext.fetchCount(
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.like("DTO_FLEXIBLE"))
			.and(PAYMENT_CONCEPT.IRPF_EXPRESSION.eq("_P")) 
		) >= 1;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("DTO_FLEXIBLE"))
			.execute();

			for ( String concept : new String [] { 
					"FLEXIBLE", 
					"FLEXIBLE_GUARDERIA", 
					"FLEXIBLE_SEGURO", 
					"FLEXIBLE_FORMACION" } ) {
				dslContext
				.update(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "0.00")
				.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
				.and(PAYMENT_CONCEPT.CODE.eq(concept))
				.execute();

				dslContext
				.update(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
				.from(PAYMENT_CONCEPT)
				.where(PAYMENT_CONCEPT.CODE.eq(concept))
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.and(CONTRACT_PAYMENT.IRPF_EXPRESSION.eq("0.00"))
				.execute();
			}

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "EXCESO(136.36)")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)22)// R.ESP. MANUTENCIÓN Y SIMILARES
					.or(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_TRANSPORTE")))
			.execute();

			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "EXCESO(11.00 * DIAS_LABORABLES)")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)61) // PLUSES DE TRANSPORTE Y DE DISTANCIA (MEDIOS COLECTIVOS EMPRESA)
					.or(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE_COMIDA")))
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
