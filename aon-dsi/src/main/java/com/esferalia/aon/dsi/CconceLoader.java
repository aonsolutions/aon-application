package com.esferalia.aon.dsi;

import static com.esferalia.aon.dsi.jooq.tables.Fncconce.FNCCONCE;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;

import com.esferalia.aon.dsi.jooq.tables.records.FncconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnzconceRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class CconceLoader extends AbstractLoader implements
		ConvenLoader.Callback {

	public static String getCode(FntconceRecord tconce) {
		return getCode(tconce.getF21nombre(), tconce.getF21pagas(),
				tconce.getF21enfer(), tconce.getF21vacac(),
				tconce.getF21accid());
	}

	public static String getCode(FncconceRecord cconce) {
		return getCode(cconce.getF21nombre(), cconce.getF21pagas(),
				cconce.getF21enfer(), cconce.getF21vacac(),
				cconce.getF21accid());
	}

	public static String getCode(FnzconceRecord zconce) {
		return getCode(zconce.getF21nombre(), zconce.getF21pagas(),
				zconce.getF21enfer(), zconce.getF21vacac(),
				zconce.getF21accid());
	}

	public static String getCode(String nombre, String pagas, String enfer,
			String vacac, String accid) {
		String code = null;
		if (!isBlank(nombre) 
//@formatter:off
				&&( equalsIgnoreCase("S", pagas) 
				|| equalsIgnoreCase("S", enfer) // GTZDO
				|| equalsIgnoreCase("S", vacac) 
				|| equalsIgnoreCase("S", accid) ) 
				//@formatter:on
		) {
			code = nombre.toUpperCase().replaceAll("\\W", "_")
					.replaceAll("_$", "");
		}
		return code;

	}

	public static String getExpression(FntconceRecord tconce) {
		return getExpression(tconce.getF21tipo(), getVariable(tconce));
	}

	public static String getExpression(FnzconceRecord zconce) {
		return getExpression(zconce.getF21tipo(), getVariable(zconce));
	}

	public static String getExpression(String tipo, String var) {
		if (equalsIgnoreCase("M", tipo))
			return String.format("/*user*/ %s /**/ * %s / %s", var,
					ContextVariable.WORKED_DAYS, ContextVariable.MONTH_DAYS);

		else if (equalsIgnoreCase("D", tipo))
			return String.format("/*user*/ %s /**/ * %s", var,
					ContextVariable.WORKED_DAYS);

		else if (equalsIgnoreCase("V", tipo) || equalsIgnoreCase("S", tipo))
			return String.format("/*user*/ %s /**/ * %s", var,
					ContextVariable.ACTUAL_DAYS);

		return String.format("%s", var, ContextVariable.ACTUAL_DAYS);
	}

	public static String getVariable(FntconceRecord tconce) {
		return getVariable((isBlank(tconce.getF21nombre()) ? tconce
				.getF21clave() : tconce.getF21nombre()).toUpperCase()
				.replaceAll("\\W", "_").replaceAll("_$", ""));

	}

	public static String getVariable(FnzconceRecord zconce) {
		return getVariable((isBlank(zconce.getF21nombre()) ? zconce
				.getF21clave() : zconce.getF21nombre()).toUpperCase()
				.replaceAll("\\W", "_").replaceAll("_$", ""));

	}

	public static String getVariable(String code) {
		return String.format("IMPORTE_%s", code);

	}

	// ------------------------------------------------------------------------

	private static String getExpression(FncconceRecord cconce) {
		return String.format("/*CLAVE:%s*/", cconce.getF21clave());
	}

	// ------------------------------------------------------------------------

	private Map<String, PaymentConceptRecord> concepts;

	private InsertSetMoreStep<PaymentConceptRecord> insertSetMoreStepPaymentConcept;

	//private InsertOnDuplicateSetMoreStep<PaymentConceptRecord> replaceSetMoreStepPaymentConcept;

	public CconceLoader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
		concepts = new HashMap<String, PaymentConceptRecord>();
	}

	public void load(int domain, Condition... conditions) {
		//@formatter:off
		Cursor<Record> cconceCursor =
		dsiContext
		.select()
		.from(FNCCONCE)
		.where(conditions)
		.fetchLazy();
		//@formatter:on

		while (cconceCursor.hasNext()) {
			FncconceRecord cconce = cconceCursor.fetchOneInto(FNCCONCE);
			load(cconce, domain);
		}
	}

	public void execute() {
		execute(insertSetMoreStepPaymentConcept);

		insertSetMoreStepPaymentConcept = null;
	}

	// ------------------------------------------------------------------------

	@Override
	public PaymentConceptRecord getpaymentConcept(FnzconceRecord zconce) {
		return concepts.get(zconce.getF21clave());
	}

	@Override
	public PaymentConceptRecord getpaymentConcept(FntconceRecord tconce) {
		return concepts.get(tconce.getF21clave());
	}

	// ------------------------------------------------------------------------

	private int load(FncconceRecord cconce, int domain) {

		PaymentConceptRecord record = getPaymentConcept(cconce, domain);
		if (record != null) {
			// TODO: REPLACE INTO ....
			//replaceSetMoreStepPaymentConcept = replaceSetMoreStepPaymentConcept.set(record);
		} else {
			record = newPaymentConcept(cconce, domain);
			InsertSetStep<PaymentConceptRecord> insertSetStepPaymentConcept = getPaymentConceptInsertSetStep();
			insertSetMoreStepPaymentConcept = insertSetStepPaymentConcept
					.set(record);
		}

		concepts.put(cconce.getF21clave(), record);
		return record.getId();
	}

	private PaymentConceptRecord newPaymentConcept(FncconceRecord cconce,
			int domain) {

		PaymentConceptRecord record = new PaymentConceptRecord();
		record.setId(next(PAYMENT_CONCEPT.getIdentity()));

		byte clavecra = StringUtils.isBlank(cconce.getF21clavecra()) ? 1 : Byte
				.parseByte(cconce.getF21clavecra());

		String irpf = null;
		if (equalsIgnoreCase("S", cconce.getF21irpf()))
			irpf = "_P";

		String quote = null;
		if (equalsIgnoreCase("S", cconce.getF21segsoc()))
			quote = "_P";

		record.setType(clavecra);
		record.setDomain(domain);
		record.setCode(getCode(cconce));
		record.setIrpfExpression(irpf);
		record.setQuoteExpression(quote);
		record.setDescription(cconce.getF21nombre());
		record.setDescriptionDecorable((byte) 0);
		record.setExpression(getExpression(cconce));

		return record;
	}

	private PaymentConceptRecord getPaymentConcept(FncconceRecord cconce,
			int domain) {
		return 
//@formatter:off
		aonContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(domain))
		.and(PAYMENT_CONCEPT.EXPRESSION.like("%"+getExpression(cconce)+"%"))
		.fetchOneInto(PAYMENT_CONCEPT)
		;
		//@formatter:off
	}

	private InsertSetStep<PaymentConceptRecord> getPaymentConceptInsertSetStep() {
		return get(insertSetMoreStepPaymentConcept, PAYMENT_CONCEPT);
	}
	
	// ------------------------------------------------------------------------
}
