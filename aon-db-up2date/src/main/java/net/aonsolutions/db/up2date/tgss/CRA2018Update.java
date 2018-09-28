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
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class CRA2018Update implements Update {

	public static CRA2018Update CRA2018UPDATE = new CRA2018Update();

	private CRA2018Update() {
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
		
		Collection<Byte> types = Arrays.asList((byte)57, (byte)58, (byte)59, (byte)60, (byte)61);

		
		boolean upgraded57 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)57))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment57 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)57)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.DESCRIPTION,"HORAS COMPLEMENTARIAS PACTADAS")
		;
		
		boolean upgraded58 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)58))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment58 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)58)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.DESCRIPTION,"HORAS COMPLEMENTARIAS DE ACEPTACI\u00D3N VOLUNTARIA")
		;

		boolean upgraded59 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)59))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment59 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)59)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.DESCRIPTION,"VACACIONES NO DISFRUTADAS, RETRIBUIDAS TRAS EL FALLECIMIENTO DEL TRABAJADOR-")
		;

		boolean upgraded60 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)60))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment60 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)60)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.DESCRIPTION,"VACACIONES RETRIBUIDAS NO DISFRUTADAS. COTIZACI\u00D3N DURANTE EL CONTRATO")
		;

		boolean upgraded61 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)61))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment61 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)61)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.DESCRIPTION,"PLUS DE TRANSPORTE Y DE DISTANCIA. UTILIZACI\u00D3N DE MEDIOS COLECTIVOS APORTADOS POR LA EMPRESA")
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			if ( !upgraded57 )
				insertPayment57.execute();
			if ( !upgraded58 )
				insertPayment58.execute();
			if ( !upgraded59 )
				insertPayment59.execute();
			if ( !upgraded60 )
				insertPayment60.execute();
			if ( !upgraded61 )
				insertPayment61.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
