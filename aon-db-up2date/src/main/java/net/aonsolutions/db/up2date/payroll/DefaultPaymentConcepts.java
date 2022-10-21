package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.io.Serializable;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class DefaultPaymentConcepts implements Update {

	public static final DefaultPaymentConcepts DEFAULTPAYMENTCONCEPTS = new DefaultPaymentConcepts();
	
	private DefaultPaymentConcepts() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction(t -> addDefaultPaymentConcepts(dslContext));
		
	}
	
	private enum PaymentConcept implements Serializable {
	
		PLUS_XS_MENSUAL("PLUS_XS", "PLUS EXTRA SALARIAL MENSUAL", (byte) 1, "/*user*/ 0.00 /**/ * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P"),
		PLUS_XS_DIARIO("PLUS_XS", "PLUS EXTRA SALARIAL DIARIO", (byte) 1, "(PLUS_DIARIO=/*user*/0.00/**/) * DIAS_TRABAJADOS", "_P", "_P"),
		PLUS_XS_DIAS_REALES("PLUS_XS", "PLUS EXTRA SALARIAL DIAS REALES", (byte) 1, "(PLUS_DIARIO=/*user*/0.00/**/) * DIAS_EFECTIVOS ", "_P", "_P"),
		;
		
		private String code;
		private String description;
		private byte type;
		private String expression;
		private String irpfExpression;
		private String quoteExpression;
		
		private PaymentConcept(String code, String description, byte type, String expression, String irpfExpression, String quoteExpression) {
			this.code = code;
			this.description = description;
			this.type = type;
			this.expression = expression;
			this.irpfExpression = irpfExpression;
			this.quoteExpression = quoteExpression;
		}

		public String getCode() {
			return code;
		}

		public String getDescription() {
			return description;
		}

		public byte getType() {
			return type;
		}

		public String getExpression() {
			return expression;
		}

		public String getIrpfExpression() {
			return irpfExpression;
		}

		public String getQuoteExpression() {
			return quoteExpression;
		}

	}
	
	private void addDefaultPaymentConcepts(DSLContext dslContext) {
		if(checkIfCanInsertPayments(dslContext))
			for(int i = 0; i < PaymentConcept.values().length; i++) {
				PaymentConcept paymentConcept = PaymentConcept.values()[i];
				
				dslContext.insertInto(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DOMAIN, 0)
					.set(PAYMENT_CONCEPT.CODE, paymentConcept.getCode())
					.set(PAYMENT_CONCEPT.DESCRIPTION, paymentConcept.getDescription())
					.set(PAYMENT_CONCEPT.TYPE, paymentConcept.getType())
					.set(PAYMENT_CONCEPT.EXPRESSION, paymentConcept.getExpression())
					.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, paymentConcept.getIrpfExpression())
					.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, paymentConcept.getQuoteExpression())
					.execute();
			}
	}

	private boolean checkIfCanInsertPayments(DSLContext dslContext) {
		Result<PaymentConceptRecord> findConcept = dslContext.selectFrom(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PLUS EXTRA SALARIAL MENSUAL"))
			.fetch();
		
		System.out.println(findConcept.isEmpty() ? "Se procede a insertar nuevos devengos predefinidos" : "Ya existen los devengo que se quieren insertar");
		
		return findConcept.isEmpty();
	}

}
