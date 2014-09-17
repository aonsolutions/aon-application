package com.esferalia.aon.dsi;

import static com.esferalia.aon.dsi.CconceLoader.getExpression;
import static com.esferalia.aon.dsi.CconceLoader.getVariable;
import static com.esferalia.aon.dsi.jooq.tables.Fncatego.FNCATEGO;
import static com.esferalia.aon.dsi.jooq.tables.Fnconven.FNCONVEN;
import static com.esferalia.aon.dsi.jooq.tables.Fnzantig.FNZANTIG;
import static com.esferalia.aon.dsi.jooq.tables.Fnzconce.FNZCONCE;
import static com.esferalia.aon.dsi.jooq.tables.Fnzpagas.FNZPAGAS;
import static com.esferalia.aon.dsi.util.EnumUtils.enum2Byte;
import static com.esferalia.aon.dsi.util.RomanNumber.toRoman;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.salary.enumeration.SalaryType.EXTRA;
import static com.esferalia.aon.salary.enumeration.SalaryType.SALARY;
import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;

import com.esferalia.aon.dsi.jooq.tables.records.FncategoRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnconvenRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnempresRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FntrabajRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnzantigRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnzconceRecord;
import com.esferalia.aon.dsi.jooq.tables.records.FnzpagasRecord;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class ConvenLoader extends AbstractLoader implements
		EmpresLoader.Callback {

	public static interface Callback {
		PaymentConceptRecord getpaymentConcept(FnzconceRecord zconce);

		PaymentConceptRecord getpaymentConcept(FntconceRecord zconce);

	}

	private Callback cb = null;

	private Map<String, Integer> categories;
	private Map<String, Integer> agreements;

	private InsertSetMoreStep<AgreementRecord> insertSetMoreStepAgreement;
	// private InsertSetMoreStep<AgreementDataRecord>
	// insertSetMoreStepAgreementData;
	private InsertSetMoreStep<AgreementLevelRecord> insertSetMoreStepAgreementLevel;
	private InsertSetMoreStep<AgreementLevelDataRecord> insertSetMoreStepAgreementLevelData;
	private InsertSetMoreStep<AgreementLevelCategoryRecord> insertSetMoreStepAgreementLevelCategory;
	private InsertSetMoreStep<AgreementPaymentRecord> insertSetMoreStepAgreementPayment;
	private InsertSetMoreStep<AgreementExtraRecord> insertSetMoreStepAgreementExtra;

	public ConvenLoader(DSLContext dsiContext, DSLContext aonContext) {
		super(dsiContext, aonContext);
		categories = new HashMap<String, Integer>();
		agreements = new HashMap<String, Integer>();
	}

	public void loadConven(int domain, Callback cb, Condition... conditions) {
		this.cb = cb;
		//@formatter:off
		Cursor<Record> convenCursor = dsiContext
			.select()
			.from(FNCONVEN)
			.where(conditions)
			.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> categoCursor = dsiContext
			.select()
			.from(FNCATEGO)
			.where(conditions)
			.orderBy(FNCATEGO.F20CODCON, FNCATEGO.F20CODCAT)
			.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> pagasCursor = dsiContext
			.select()
			.from(FNZPAGAS)
			.where(conditions)
			.orderBy(FNZPAGAS.F21CODCON, FNZPAGAS.F21CODCAT)
			.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> antigCursor = dsiContext
			.select()
			.from(FNZANTIG)
			.where(conditions)
			.orderBy(FNZANTIG.F21CODCON, FNZANTIG.F21CODCAT)
			.fetchLazy();
		//@formatter:on

		//@formatter:off
		Cursor<Record> conceCursor = dsiContext
			.select()
			.from(FNZCONCE)
			.where(conditions)
			.orderBy(FNZCONCE.F21CODCON, FNZCONCE.F21CODCAT)
			.fetchLazy();
		//@formatter:on

		FncategoRecord catego = categoCursor.hasNext() ? categoCursor
				.fetchOneInto(FNCATEGO) : null;

		FnzconceRecord conce = conceCursor.hasNext() ? conceCursor
				.fetchOneInto(FNZCONCE) : null;

		FnzpagasRecord zpaga = pagasCursor.hasNext() ? pagasCursor
				.fetchOneInto(FNZPAGAS) : null;

		FnzantigRecord antig = antigCursor.hasNext() ? pagasCursor
				.fetchOneInto(FNZANTIG) : null;

		Integer extraConcept = getExtraConcept();

		while (convenCursor.hasNext()) {

			Set<String> zPagas = new HashSet<String>();
			Set<String> zConces = new HashSet<String>();
			Set<String> zAntigs = new HashSet<String>();

			Set<String> zPagasZconces = new HashSet<String>();

			StringBuffer antigBuffer = new StringBuffer();

			FnconvenRecord conven = convenCursor.fetchOneInto(FNCONVEN);

			Date startDate = conven.getF20fecha() != null ? conven
					.getF20fecha() : new Date(2010 - 1900, 0, 1);

			int agreement = loadConven(domain, conven);

			while (catego != null
					&& StringUtils.equals(catego.getF20codcon(),
							conven.getF20codigo())) {

				int level = loadCatego(domain, agreement, catego);

				while (antig != null
						&& StringUtils.equals(antig.getF21codcon(),
								conven.getF20codigo())
						&& StringUtils.equals(antig.getF21codcat(),
								catego.getF20codcat())) {
					if (zAntigs.add(conce.getF21clave())
							&& !isBlank(antig.getF21annos())) {
						if (antigBuffer.length() > 0)
							antigBuffer.append(" + ");
						antigBuffer.append(format(
								"%s(\"SALARIO_BASE\" * %.2f,%d)",
								ContextVariable.OLD, antig.getF21valor() / 100,
								Integer.valueOf(antig.getF21annos())));
					}

					antig = antigCursor.hasNext() ? antigCursor
							.fetchOneInto(FNZANTIG) : null;
				}

				while (conce != null
						&& StringUtils.equals(conce.getF21codcon(),
								conven.getF20codigo())
						&& StringUtils.equals(conce.getF21codcat(),
								catego.getF20codcat())) {

					if (zConces.add(conce.getF21clave()))
						loadConce(
								domain,
								agreement,
								startDate,
								conce,
								cb,
								equalsIgnoreCase("ANTIGUEDAD",
										conce.getF21nombre()) ? antigBuffer
										.toString() : null);

					if (equalsIgnoreCase("S", conce.getF21pagas()))
						zPagasZconces.add(CconceLoader.getCode(conce));

					loadConceData(domain, level, startDate, conce);

					conce = conceCursor.hasNext() ? conceCursor
							.fetchOneInto(FNZCONCE) : null;
				}

				while (zpaga != null
						&& StringUtils.equals(zpaga.getF21codcon(),
								conven.getF20codigo())
						&& StringUtils.equals(zpaga.getF21codcat(),
								catego.getF20codcat())) {

					if (zPagas.add(zpaga.getF21norden()))
						loadPaga(domain, agreement, startDate, zpaga,
								extraConcept, zPagasZconces);

					zpaga = pagasCursor.hasNext() ? pagasCursor
							.fetchOneInto(FNZPAGAS) : null;
				}

				catego = categoCursor.hasNext() ? categoCursor
						.fetchOneInto(FNCATEGO) : null;
			}
		}

	}

	public void execute() {
		execute(insertSetMoreStepAgreement);
		execute(insertSetMoreStepAgreementLevel);
		execute(insertSetMoreStepAgreementLevelData);
		execute(insertSetMoreStepAgreementLevelCategory);
		execute(insertSetMoreStepAgreementPayment);
		execute(insertSetMoreStepAgreementExtra);

		insertSetMoreStepAgreement = null;
		insertSetMoreStepAgreementPayment = null;
		insertSetMoreStepAgreementLevel = null;
		insertSetMoreStepAgreementLevelCategory = null;
		insertSetMoreStepAgreementLevelData = null;
		insertSetMoreStepAgreementExtra = null;
	}

	// ------------------------------------------------------------------------
	@Override
	public Integer getCategory(FntrabajRecord trabaj) {
		return categories.get(format("%s%s", trabaj.getF20conven(),
				trabaj.getF20categ()));
	}

	@Override
	public Integer getAgreement(FnempresRecord empres) {
		return agreements.get(empres.getF20conven());
	}

	@Override
	public PaymentConceptRecord getConcept(FntconceRecord conce) {
		return cb.getpaymentConcept(conce);
	}

	// ------------------------------------------------------------------------

	private int loadConven(int domain, FnconvenRecord conven) {

		AgreementRecord record = getAgreementRecord(conven, domain);
		if (record != null) {
			// TODO: REPLACE INTO `agreement` (...
			agreements.put(conven.getF20codigo(), record.getId());
			return record.getId();
		}
		// INSERT INTO `agreement` (...
		InsertSetStep<AgreementRecord> insertSetStepAgreement = getAgreementInsertSetStep();
		int agreement = next(AGREEMENT.getIdentity());
		String description = format("%s %s", getImportKey(conven),
				conven.getF20nombre());
		//@formatter:off
		insertSetMoreStepAgreement = insertSetStepAgreement
				.set(AGREEMENT.ID, agreement)
				.set(AGREEMENT.DOMAIN, domain)
				.set(AGREEMENT.DESCRIPTION, description)
				;
		//@formatter:on
		agreements.put(conven.getF20codigo(), agreement);
		return agreement;
	}

	private int loadCatego(int domain, int agreement, FncategoRecord catego) {
		AgreementLevelCategoryRecord record = getCategoryRecord(catego, domain);
		if (record != null) {
			// TODO: REPLACE INTO `agreement_level` (...
			// TODO: REPLACE INTO `agreement_level_category` (...
			categories.put(getImportKey(catego), record.getId());
			return record.getId();
		}

		// INSERT INTO `agreement_level` (...
		String levelDescription;
		try {
			levelDescription = toRoman(Integer.valueOf(catego.getF20codcat()));
		} catch (NumberFormatException e) {
			levelDescription = null;
		}
		int level = next(AGREEMENT_LEVEL.getIdentity());
		InsertSetStep<AgreementLevelRecord> insertSetStepAgreementLevel = getAgreementLevelInsertSetStep();
		//@formatter:off
		insertSetMoreStepAgreementLevel = insertSetStepAgreementLevel
				.set(AGREEMENT_LEVEL.ID, level)
				.set(AGREEMENT_LEVEL.DOMAIN, domain)
				.set(AGREEMENT_LEVEL.AGREEMENT, agreement)
				.set(AGREEMENT_LEVEL.DESCRIPTION, levelDescription)
				;
		//@formatter:on

		// TODO: INSERT INTO `agreement_level_category` (...
		int category = next(AGREEMENT_LEVEL_CATEGORY.getIdentity());
		InsertSetStep<AgreementLevelCategoryRecord> insertSetStepAgreementLevelCategory = getAgreementLevelCategoryInsertSetStep();
		//@formatter:off
		insertSetMoreStepAgreementLevelCategory = insertSetStepAgreementLevelCategory
				.set(AGREEMENT_LEVEL_CATEGORY.ID, category)
				.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, domain)
				.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, level)
				.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, format("%s %s", getImportKey(catego), catego.getF20nomcat()))
				;
		//@formatter:on

		categories.put(getImportKey(catego), category);

		return level;
	}

	private int loadPaga(int domain, int agreement, Date startDate,
			FnzpagasRecord zpagas, Integer concept, Set<String> payments) {

		AgreementPaymentRecord record = getPaymentRecord(zpagas, domain);
		if (record != null) {
			// TODO: REPLACE INTO `agreement_extra` (...
			// TODO: REPLACE INTO `agreement_payment` (...
			return record.getId();
		}

		// INSERT INTO `agreement_payment` (...
		InsertSetStep<AgreementPaymentRecord> insertSetStepAgreementPayment = getAgreementPaymentInsertSetStep();

		int paymentId = next(AGREEMENT_PAYMENT.getIdentity());

		byte mes = Byte.valueOf(zpagas.getF21mes());

		StringBuffer paymentsSumBuff = new StringBuffer();
		for (String payment : payments)
			paymentsSumBuff.append((paymentsSumBuff.length() > 0 ? " + " : "")
					+ payment);

		//@formatter:off
		insertSetMoreStepAgreementPayment = insertSetStepAgreementPayment
				.set(AGREEMENT_PAYMENT.ID, paymentId)
				.set(AGREEMENT_PAYMENT.DOMAIN, domain)
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreement)
				
				.set(AGREEMENT_PAYMENT.TYPE, concept != null ? (Byte) null : enum2Byte(PaymentType.CRA_0004))
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, concept != null ? null : "_P")
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, concept != null ? null : "_P")
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, concept)
				
				.set(AGREEMENT_PAYMENT.DESCRIPTION, zpagas.getF21descri())

				.set(AGREEMENT_PAYMENT.MONTH, (Byte) mes)
				.set(AGREEMENT_PAYMENT.START_DATE, startDate )
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, enum2Byte(EXTRA))
				.set(AGREEMENT_PAYMENT.EXPRESSION, format("( /*user*/ %s /**/ ) * ( %s / 12 )",paymentsSumBuff.toString(), ContextVariable.MONTHS))
				;
		//@formatter:on

		InsertSetStep<AgreementExtraRecord> insertSetStepAgreementExtra = getAgreementExtraInsertSetStep();

		// INSERT INTO `agreement_payment` (...

		int mesdes = Integer.valueOf(zpagas.getF21mesdes());
		int diades = Integer.valueOf(zpagas.getF21diades());
		int meshas = Integer.valueOf(zpagas.getF21meshas());
		int diahas = Integer.valueOf(zpagas.getF21diahas());

		int dia = meshas == mes ? diahas : 15; // TODO : 15???

		String extraStart = StringUtils.equalsIgnoreCase("A",
				zpagas.getF21annohas()) ? format("%d/%d -1", diades, mesdes)
				: format("%d/%d", diades, mesdes);

		String extraEnd = StringUtils.equalsIgnoreCase("A",
				zpagas.getF21annohas()) ? format("%d/%d -1", diahas, meshas)
				: format("%d/%d", diahas, meshas);

		String extraIssue = format("%d/%d", dia, mes);

		//@formatter:off
		insertSetMoreStepAgreementExtra = insertSetStepAgreementExtra
				.set(AGREEMENT_EXTRA.ID, paymentId)
				.set(AGREEMENT_EXTRA.DOMAIN, domain)
				.set(AGREEMENT_EXTRA.AGREEMENT, agreement)
				.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, paymentId)
				.set(AGREEMENT_EXTRA.START_DATE, extraStart)
				.set(AGREEMENT_EXTRA.END_DATE, extraEnd)
				.set(AGREEMENT_EXTRA.ISSUE_DATE, extraIssue )
				
				;
		//@formatter:on

		return paymentId;
	}

	private int loadConce(int domain, int agreement, Date startDate,
			FnzconceRecord zconce, Callback cb, String expression) {
		InsertSetStep<AgreementPaymentRecord> insertSetStepAgreementPayment = getAgreementPaymentInsertSetStep();

		AgreementPaymentRecord record = getPaymentRecord(zconce, domain);
		if (record != null) {
			// TODO: REPLACE INTO `agreement_payment` (...
			return record.getId();
		}

		// INSERT INTO `agreement_payment` (...
		int payment = next(AGREEMENT_PAYMENT.getIdentity());

		PaymentConceptRecord concept = cb.getpaymentConcept(zconce);

		Byte type = StringUtils.isBlank(zconce.getF21clavecra()) ? 1 : Byte
				.parseByte(zconce.getF21clavecra());

		if (concept != null && type.equals(concept.getType()))
			type = null;

		String irpf = null;
		if (equalsIgnoreCase("S", zconce.getF21irpf())) {
			irpf = (concept != null && "_P".equals(concept.getIrpfExpression())) ? null
					: "_P";
		}

		String quote = null;
		if (equalsIgnoreCase("S", zconce.getF21segsoc())) {
			quote = (concept != null && "_P".equals(concept
					.getQuoteExpression())) ? null : "_P";
		}

		String description = zconce.getF21nombre();
		if (concept != null && description != null
				&& description.equals(concept.getDescription()))
			description = null;

		//@formatter:off
		insertSetMoreStepAgreementPayment = insertSetStepAgreementPayment
				.set(AGREEMENT_PAYMENT.ID, payment)
				.set(AGREEMENT_PAYMENT.DOMAIN, domain)
				.set(AGREEMENT_PAYMENT.AGREEMENT, agreement)
				
				.set(AGREEMENT_PAYMENT.TYPE, type)
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, irpf)
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, quote)
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, concept != null ? concept.getId(): null)
				.set(AGREEMENT_PAYMENT.DESCRIPTION, description)

				.set(AGREEMENT_PAYMENT.MONTH, (Byte) null)
				.set(AGREEMENT_PAYMENT.START_DATE, startDate )
				.set(AGREEMENT_PAYMENT.SALARY_TYPE, enum2Byte(SALARY))
				.set(AGREEMENT_PAYMENT.EXPRESSION, expression != null ? expression : getExpression(zconce))
				;
		//@formatter:on
		return payment;
	}

	private void loadConceData(int domain, int level, Date startDate,
			FnzconceRecord zconce) {

		AgreementLevelDataRecord record = getDataRecord(domain, level,
				startDate, zconce);
		if (record != null) {
			// TODO: REPLACE INTO `agreement_level_data` (...
			return;
		}

		// INSERT INTO `agreement_level_data` (...
		InsertSetStep<AgreementLevelDataRecord> insertSetStepAgreeementLevelData = getAgreementLevelDataInsertSetStep();
		String expression = null;
		if (zconce.getF21impor() != null)
			expression = format("%.2f", zconce.getF21impor());

		if (isBlank(expression))
			return;

		//@formatter:off
		insertSetMoreStepAgreementLevelData = insertSetStepAgreeementLevelData
				.set(AGREEMENT_LEVEL_DATA.DOMAIN, domain)
				.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, level)
				.set(AGREEMENT_LEVEL_DATA.START_DATE, startDate )
				.set(AGREEMENT_LEVEL_DATA.NAME, getVariable(zconce))
				.set(AGREEMENT_LEVEL_DATA.EXPRESSION,expression )
				;
		//@formatter:on
	}

	private int getExtraConcept() {
		//@formatter:off
		Record1<Integer> record1=
		aonContext
		.select(PAYMENT_CONCEPT.ID).
		from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq(enum2Byte(PaymentType.CRA_0004)))
		.fetchOne();
		//@formatter:on
		return record1 != null ? record1.value1() : null;
	}

	private String getImportKey(FnconvenRecord conven) {
		return format("/*CODIGO:%s*/", conven.getF20codigo());
	}

	private String getImportKey(FncategoRecord catego) {
		return format("/*CODCON:%s, CODCAT:%s*/", catego.getF20codcon(),
				catego.getF20codcat());
	}

	private String getImportKey(FnzpagasRecord pagas) {
		return format("/*CODCON:%s, CODCAT:%s, NORDEN:%s*/",
				pagas.getF21codcon(), pagas.getF21codcat(),
				pagas.getF21norden());
	}

	private String getImportKey(FnzconceRecord conce) {
		return format("/*CODCON:%s, CODCAT:%s, NORDEN:%s*/",
				conce.getF21codcon(), conce.getF21codcat(),
				conce.getF21norden());
	}

	private AgreementRecord getAgreementRecord(FnconvenRecord conven, int domain) {
		//@formatter:off
		return 
		aonContext
		.select()
		.from(AGREEMENT)
		.where(AGREEMENT.DOMAIN.eq(domain))
		.and(AGREEMENT.DESCRIPTION.like(format("%%%s%%", getImportKey(conven))))
		.fetchOneInto(AGREEMENT)
		;
		//@formatter:on
	}

	private AgreementLevelCategoryRecord getCategoryRecord(
			FncategoRecord catego, int domain) {
		//@formatter:off
		return 
		aonContext
		.select()
		.from(AGREEMENT_LEVEL_CATEGORY)
		.where(AGREEMENT_LEVEL_CATEGORY.DOMAIN.eq(domain))
		.and(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION.like(format("%%%s%%", getImportKey(catego))))
		.fetchOneInto(AGREEMENT_LEVEL_CATEGORY)
		;
		//@formatter:on
	}

	private AgreementPaymentRecord getPaymentRecord(FnzpagasRecord pagas,
			int domain) {
		//@formatter:off
		return 
		aonContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.DOMAIN.eq(domain))
		.and(AGREEMENT_PAYMENT.SALARY_TYPE.eq(enum2Byte(EXTRA)))
		.and(AGREEMENT_PAYMENT.EXPRESSION.like(format("%%%s%%", getImportKey(pagas))))
		.fetchOneInto(AGREEMENT_PAYMENT)
		;
		//@formatter:on
	}

	private AgreementPaymentRecord getPaymentRecord(FnzconceRecord conce,
			int domain) {
		//@formatter:off
		return 
		aonContext
		.select()
		.from(AGREEMENT_PAYMENT)
		.where(AGREEMENT_PAYMENT.DOMAIN.eq(domain))
		.and(AGREEMENT_PAYMENT.SALARY_TYPE.eq(enum2Byte(SALARY)))
		.and(AGREEMENT_PAYMENT.EXPRESSION.like(format("%%%s%%", getImportKey(conce))))
		.fetchOneInto(AGREEMENT_PAYMENT)
		;
		//@formatter:on
	}

	private AgreementLevelDataRecord getDataRecord(int domain, int level,
			Date startDate, FnzconceRecord conce) {
		//@formatter:off
		return 
		aonContext
		.select()
		.from(AGREEMENT_LEVEL_DATA)
		.where(AGREEMENT_LEVEL_DATA.DOMAIN.eq(domain))
		.and(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(level))
		.and(AGREEMENT_LEVEL_DATA.START_DATE.eq(startDate))
		.and(AGREEMENT_LEVEL_DATA.NAME.eq(getVariable(conce)))
		.fetchOneInto(AGREEMENT_LEVEL_DATA)
		;
		//@formatter:on
	}

	private InsertSetStep<AgreementRecord> getAgreementInsertSetStep() {
		return get(insertSetMoreStepAgreement, AGREEMENT);
	}

	private InsertSetStep<AgreementExtraRecord> getAgreementExtraInsertSetStep() {
		return get(insertSetMoreStepAgreementExtra, AGREEMENT_EXTRA);
	}

	private InsertSetStep<AgreementPaymentRecord> getAgreementPaymentInsertSetStep() {
		return get(insertSetMoreStepAgreementPayment, AGREEMENT_PAYMENT);
	}

	private InsertSetStep<AgreementLevelRecord> getAgreementLevelInsertSetStep() {
		return get(insertSetMoreStepAgreementLevel, AGREEMENT_LEVEL);
	}

	private InsertSetStep<AgreementLevelDataRecord> getAgreementLevelDataInsertSetStep() {
		return get(insertSetMoreStepAgreementLevelData, AGREEMENT_LEVEL_DATA);
	}

	private InsertSetStep<AgreementLevelCategoryRecord> getAgreementLevelCategoryInsertSetStep() {
		return get(insertSetMoreStepAgreementLevelCategory,
				AGREEMENT_LEVEL_CATEGORY);
	}
}
