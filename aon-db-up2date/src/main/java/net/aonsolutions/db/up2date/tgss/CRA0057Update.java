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

public class CRA0057Update implements Update {

	public static CRA0057Update CRA0057UPDATE = new CRA0057Update();
	private static final String HORAS_COMPL_PACTADAS = "HORAS_COMPL";

	private CRA0057Update() {
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
		
		boolean upgraded0057 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)57))
		.and(PAYMENT_CONCEPT.CODE.eq(HORAS_COMPL_PACTADAS))
		) >= 1;
		
		if ( upgraded0057 )
			return;

		UpdateConditionStep<PaymentConceptRecord> update0057 = 
		dslContext
		.update(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.CODE, HORAS_COMPL_PACTADAS)
		.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/HORAS_COMPLEMENTARIAS * IMPORTE_HORA_COMPLEMENTARIA/**/")
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)57))
		;
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			update0057.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
