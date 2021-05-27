package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class CRA0035Update implements Update {

	public static final CRA0035Update CRA0035UPDATE = new CRA0035Update();

	private CRA0035Update() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		PaymentConceptRecord cra0035 =
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)35))
		.fetchOneInto(PAYMENT_CONCEPT)
		;
		
		boolean upgraded0035 = cra0035.getIrpfExpression() == null 
								&& cra0035.getQuoteExpression() == null; 
				
		if ( upgraded0035 )
			return;

		UpdateConditionStep<PaymentConceptRecord> update0035 = 
		dslContext
		.update(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, DSL.castNull(String.class))
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, DSL.castNull(String.class))
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)35))
		;
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			update0035.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
