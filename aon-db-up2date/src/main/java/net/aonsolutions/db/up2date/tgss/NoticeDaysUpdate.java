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

public class NoticeDaysUpdate implements Update {

	public static NoticeDaysUpdate NOTICEDAYSUPDATE = new NoticeDaysUpdate();

	private static final String NOTICE = "PREAVISO";

	private NoticeDaysUpdate() {
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

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)54))
		.and(PAYMENT_CONCEPT.CODE.eq(NOTICE))
		) >= 1;

		if ( upgraded )
			return;

		UpdateConditionStep<PaymentConceptRecord> update =
		dslContext
		.update(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.TYPE, (byte)54)
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, DSL.castNull(String.class))
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, DSL.castNull(String.class))
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq(NOTICE))
		;


		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			update.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});

	}

}
