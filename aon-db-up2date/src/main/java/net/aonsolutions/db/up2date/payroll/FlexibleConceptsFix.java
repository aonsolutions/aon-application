package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.Arrays;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class FlexibleConceptsFix implements Update {

	public static final FlexibleConceptsFix FLEXIBLECONCEPTSFIX = new FlexibleConceptsFix();
	
	private static final String[] CONCEPTS = new String[] { "TRANSPORTE", "COMIDA", "GUARDERIA", "SEGURO", "FORMACION" };
	

	private FlexibleConceptsFix() {
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
			.and(PAYMENT_CONCEPT.CODE.in( Arrays.stream(CONCEPTS).map(concept -> "FLEXIBLE_" + concept).toArray(String[]::new) ) )
		) >= CONCEPTS.length;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			for ( String  concept : CONCEPTS ) {
				dslContext
				.update(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.CODE, "FLEXIBLE_" + concept )
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, DSL.concat("FLEXIBLE=(isdef FLEXIBLE ? FLEXIBLE : 0.00 ) + _P;", (PAYMENT_CONCEPT.IRPF_EXPRESSION)))
				.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
				.and(PAYMENT_CONCEPT.CODE.eq("FLEXIBLE"))
				.and(PAYMENT_CONCEPT.DESCRIPTION.containsIgnoreCase(concept))
				.execute();
			}


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
