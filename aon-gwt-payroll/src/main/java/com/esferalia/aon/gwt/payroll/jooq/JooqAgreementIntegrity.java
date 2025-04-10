package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrityFix;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqAgreementIntegrity {

	private static Settings settings;
	
	// ------------------------------- Construtor
	
	private JooqAgreementIntegrity() {
		super();
	}
	
	// ------------------------------- Auxiliar Methods
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	public static AgreementIntegrity checkIntegrity(Connection connection, Integer domainId, Integer agreementId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		AgreementIntegrity agreementIntegrity = new AgreementIntegrity();
		
		AgreementRecord agreement = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
		agreementId = agreement.getId();
		Integer agreementDomain = agreement.getDomain();
		
		agreementIntegrity.setId(agreementId);
		agreementIntegrity.setDomain(agreementDomain);
		
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId)).fetch();
		
		agreementIntegrity.setAgreementPayments(parseAgreementPayments(agreementPayments));
		
		// Agreement Payments from other domain
		List<Record> otherDomainAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> !agreementPayment.get(AGREEMENT_PAYMENT.DOMAIN).equals(agreementDomain))
				.collect(Collectors.toList());
		
		agreementIntegrity.setOtherDomainAgreementPayments(parseOtherDomainAgreementPayments(agreementDomain, otherDomainAgreementPayments));
		
		// Agreement Payments with out payment_concept
		List<Record> noPaymentConceptAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) == null)
				.collect(Collectors.toList());
		
		agreementIntegrity.setNoPaymentConceptAgreementPayments(parseNoPaymentConceptAgreementPayments(noPaymentConceptAgreementPayments));
		
		// Agreement Payments with payment_concept from other domain
		List<Record> otherDomainPaymentConcepts = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) != null && !agreementPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(agreementDomain))
				.collect(Collectors.toList());
		
		agreementIntegrity.setOtherDomainPaymentConcepts(parseOtherDomainPaymentConcepts(agreementDomain, otherDomainPaymentConcepts));
		
		// Payment Concepts with out code or wrong code
		List<Record> paymentConceptsNoCode = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						(
							null == agreementPayment.get(PAYMENT_CONCEPT.CODE) || 
							checkPaymentConceptCode(agreementPayment.get(PAYMENT_CONCEPT.CODE))
						)
				).collect(Collectors.toList());
		
		agreementIntegrity.setPaymentConceptsNoCode(parsePaymentConceptsNoCode(paymentConceptsNoCode));
		
		// Get variables
		Set<String> variables = new HashSet<String>();
		
		List<String> agreementDatas = dslContext.selectDistinct(AGREEMENT_DATA.NAME)
				.from(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreementId))
				.fetch(AGREEMENT_DATA.NAME);
		
		variables.addAll(agreementDatas);
		
		List<String> agreementLevelDatas = dslContext.selectDistinct(AGREEMENT_LEVEL_DATA.NAME)
				.from(AGREEMENT_LEVEL_DATA)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.fetch(AGREEMENT_LEVEL_DATA.NAME);
		
		variables.addAll(agreementLevelDatas);
		
		// Expressions with var like payment_concept code
		List<Record> codeInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						expressionCodeCheck(agreementPayment, agreementPayments)
				)
				.collect(Collectors.toList());
		
		agreementIntegrity.setCodeInExpression(parseCodeInExpression(codeInExpression));
		
		// Var like payment_concept code
		List<String> variableLikeCodes = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						variables.contains(agreementPayment.get(PAYMENT_CONCEPT.CODE))
				)
				.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setVariableLikeCodes(variableLikeCodes);
		
		// Variables or cocepts like contextVariables
		Set<String> variablesCodes = new HashSet<String>();
		variablesCodes.addAll(variables);
		
		variablesCodes.addAll(
				agreementPayments.stream()
					.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE)
					).map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
					.collect(Collectors.toSet())
		);
		
		agreementIntegrity.setVariableCodeLikeContext(parseVariableCodeLikeContext(variablesCodes));
		
		// Contract Payments
		Result<Record> contractPayments = dslContext.select()
				.from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId)).fetch();
		
		// Contract Payments with payment_concept from other domain (only accepts other domain if its agreement domain)
		List<Record> otherDomainPaymentConceptContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					contractPayment.get(PAYMENT_CONCEPT.DOMAIN) != agreementDomain &&
					!contractPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(contractPayment.get(CONTRACT.DOMAIN)))
				.collect(Collectors.toList());
		
		
		agreementIntegrity.setOtherDomainPaymentConceptContracts(parseOtherDomainPaymentConceptContracts(agreementDomain, otherDomainPaymentConceptContracts));
		
		// Payment Concepts with out code
		List<Record> paymentConceptsNoCodeContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					null == contractPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setPaymentConceptsNoCodeContracts(parsePaymentConceptsNoCodeContracts(paymentConceptsNoCodeContracts));
		
		// Payment Concept with no agreement_payment or contract_payment
		Result<Record> paymentConceptsNoRef = dslContext.select()
				.from(PAYMENT_CONCEPT)
				.leftJoin(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(CONTRACT_PAYMENT).on(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(PAYMENT_CONCEPT.DOMAIN.gt(0).and(PAYMENT_CONCEPT.DOMAIN.eq(domainId)))
				.fetch();
		
		agreementIntegrity.setPaymentConceptsNoRef(parsePaymentConceptsNoRef(paymentConceptsNoRef));
		
		// Agreement Payment Extra wrong format 
		Result<Record> agreementExtras = dslContext.select()
				.from(AGREEMENT_EXTRA)
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.and(
					AGREEMENT_EXTRA.START_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$")
		            .or(AGREEMENT_EXTRA.END_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$"))
		            .or(AGREEMENT_EXTRA.ISSUE_DATE.notLikeRegex("^\\d{2}/\\d{2}$"))
				).fetch();
		
		agreementIntegrity.setAgreementExtras(parseAgreementExtrasMessage(agreementExtras));
		
		Result<Record> agreementExtrasDatesR = dslContext.select()
				.from(AGREEMENT_EXTRA)
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreementId))
				.and(
					AGREEMENT_EXTRA.START_DATE.likeRegex("^\\d{2}/\\d{2}( -1)?$")
		            .and(AGREEMENT_EXTRA.END_DATE.likeRegex("^\\d{2}/\\d{2}( -1)?$"))
		            .and(AGREEMENT_EXTRA.ISSUE_DATE.likeRegex("^\\d{2}/\\d{2}$"))
				).fetch();
		
		List<Record> agreementExtrasDates = agreementExtrasDatesR.stream().filter(agreementExtrasDate -> checkDatesPeriod(agreementExtrasDate)).collect(Collectors.toList());
		
		agreementIntegrity.getAgreementExtras().addAll(parseAgreementExtrasDates(agreementExtrasDates));
		
		return agreementIntegrity;
	}

	private static boolean checkDatesPeriod(Record agreementExtrasDate) {
		// Formato base dd/MM o dd/MM -1
	    Pattern pattern = Pattern.compile("^(\\d{2})/(\\d{2})( -1)?$");
	    Matcher mStart = pattern.matcher(agreementExtrasDate.get(AGREEMENT_EXTRA.START_DATE));
	    Matcher mEnd = pattern.matcher(agreementExtrasDate.get(AGREEMENT_EXTRA.END_DATE));

	    if (!mStart.matches() || !mEnd.matches()) return false;

	    int monthStart = Integer.parseInt(mStart.group(2));
	    int yearOffsetStart = (mStart.group(3) != null) ? -1 : 0;

	    int monthEnd = Integer.parseInt(mEnd.group(2));
	    int yearOffsetEnd = (mEnd.group(3) != null) ? -1 : 0;

	    // Usamos año ficticio para comparar distancia
	    YearMonth ymStart = YearMonth.of(2000 + yearOffsetStart, monthStart);
	    YearMonth ymEnd = YearMonth.of(2000 + yearOffsetEnd, monthEnd);
	    
	    byte type = null == agreementExtrasDate.get(AGREEMENT_PAYMENT.TYPE) ? agreementExtrasDate.get(PAYMENT_CONCEPT.TYPE) : agreementExtrasDate.get(AGREEMENT_PAYMENT.TYPE);
	    
	    return (type == (byte)4 && !ymEnd.minusMonths(11).equals(ymStart) && !ymEnd.minusMonths(5).equals(ymStart)) ||
	    		(type == (byte)5 && !ymEnd.minusMonths(11).equals(ymStart));
	}

	private static boolean checkPaymentConceptCode(String code) {
		if (AonStringUtils.isBlank(code)) return true;

	    return !code.matches("^[A-Za-z_].*") || !code.matches("^[A-Za-z_][A-Za-z0-9_]*$");
	}

	private static boolean expressionCodeCheck(Record agreementPayment, Result<Record> agreementPayments) {
		String code = agreementPayment.get(PAYMENT_CONCEPT.CODE);
		
		if(AonStringUtils.isBlank(code) || agreementPayments.isEmpty()) return false;
		
		List<Record> appearence = new ArrayList<Record>();
		
		agreementPayments.forEach(agreementPaymentIt -> {
			String expression = AonStringUtils.isBlank(agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION)) ? agreementPaymentIt.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION);
			
			// Compilar la expresión
	        ParserContext context = new ParserContext();
	        MVEL.compileExpression(expression, context);

	        // Obtener las variables
	        Set<String> variables = context.getInputs().keySet();
	        
	        if(variables.contains(code)) appearence.add(agreementPaymentIt);
		});
		
		return !appearence.isEmpty() && appearence.size() > 1 && appearence.stream().filter(a -> a.get(AGREEMENT_PAYMENT.ID).equals(agreementPayment.get(AGREEMENT_PAYMENT.ID))).count() > 0;
	}
	
	private static List<String> parseOtherDomainAgreementPayments(Integer agreementDomain, List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			messages.add("El devengo " + description + " (Dom.: " + paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) + ") no esta en el dominio del convenio (" + agreementDomain + ")");
		});
		
		return messages;
	}
	
	private static List<String> parseNoPaymentConceptAgreementPayments(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			messages.add("El devengo " + description + " no tiene concepto asignado");
		});
		
		return messages;
	}
	
	private static List<String> parseOtherDomainPaymentConcepts(Integer agreementDomain, List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			messages.add("El concepto " + paymentIt.get(PAYMENT_CONCEPT.CODE) + " (Dom.: " + paymentIt.get(PAYMENT_CONCEPT.DOMAIN) + ") esta en otro dominio al del devengo " + description + " (Dom.: " + paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) + ")");
		});
		
		return messages;
	}
	
	private static List<String> parsePaymentConceptsNoCode(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String code = paymentIt.get(PAYMENT_CONCEPT.CODE);
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			
			String messageType = AonStringUtils.isNotBlank(code) ? "El c\u00f3digo " : " El concepto ";
			
			String messageReason = AonStringUtils.isBlank(code) ? " no tiene c\u00f3digo definido" : " no tiene el formato correcto";
			messages.add(messageType + (AonStringUtils.isBlank(code) ? description : code) + messageReason);
		});
		
		return messages;
	}
	
	private static List<String> parseCodeInExpression(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			messages.add("El concepto con c\u00f3digo " + paymentIt.get(PAYMENT_CONCEPT.CODE) + " aparece como variable en mas de un devengo, a parte del suyo propio");
		});
		
		return messages;
	}
	
	private static List<String> parseVariableCodeLikeContext(Set<String> variablesCodes) {
		List<String> messages = new ArrayList<String>();
		
		variablesCodes.forEach(variablesCode -> {
			if(ContextVariable.isContextVariable(variablesCode))
				messages.add("El c\u00f3digo de concepto / variable " + variablesCode + " no se puede usar ya que es una variable de contexto");
		});
		
		return messages;
	}
	
	private static List<String> parseOtherDomainPaymentConceptContracts(Integer agreementDomain, List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("El devengo " + description + " del contrato (Dom. Contr.: " + paymentIt.get(CONTRACT_PAYMENT.DOMAIN) + ", Dom. Conv.: " + agreementDomain + ") esta en otro dominio al del concepto (Dom.: " +  paymentIt.get(PAYMENT_CONCEPT.DOMAIN) + ")");
		});
		
		return messages;
	}
	
	private static List<String> parsePaymentConceptsNoCodeContracts(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("El devengo del contrato " + description + " tiene un concepto sin c\u00f3digo");
		});
		
		return messages;
	}
	
	private static List<String> parsePaymentConceptsNoRef(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			messages.add("El concepto (Id: " +  paymentIt.get(PAYMENT_CONCEPT.ID) + ") " + paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) + " no tiene referencias, ni al convenio, ni a los contratos");
		});
		
		return messages;
	}
	
	private static List<String> parseAgreementExtrasMessage(Result<Record> agreementExtras) {
		List<String> messages = new ArrayList<String>();
		
		agreementExtras.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("La extra " + description + " (F.Cobro: " + paymentIt.get(AGREEMENT_EXTRA.ISSUE_DATE) + ", F.Ini: " + paymentIt.get(AGREEMENT_EXTRA.START_DATE) + ", F.Fin : " + paymentIt.get(AGREEMENT_EXTRA.END_DATE) + ") tiene un formato err\u00f3neo en las fechas 'dd/mm' o 'dd/mm -1'");
		});
		
		return messages;
	}
	
	private static List<String> parseAgreementExtrasDates(List<Record> agreementExtras) {
		List<String> messages = new ArrayList<String>();
		
		agreementExtras.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("La extra " + description + " (F.Ini: " + paymentIt.get(AGREEMENT_EXTRA.START_DATE) + ", F.Fin : " + paymentIt.get(AGREEMENT_EXTRA.END_DATE) + ") tiene un periocidad distinta a 6 o 12 meses");
		});
		
		return messages;
	}

	private static List<Payment> parseAgreementPayments(List<Record> payments) {
		List<Payment> otherDomainAgreementPayments = new ArrayList<Payment>();
		
		payments.forEach(paymentIt -> {
			
			Payment payment = new Payment();
			payment.setId(null != paymentIt.get(AGREEMENT_PAYMENT.ID) ? paymentIt.get(AGREEMENT_PAYMENT.ID) : paymentIt.get(PAYMENT_CONCEPT.ID));
			payment.setDomain(null != paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) ? paymentIt.get(AGREEMENT_PAYMENT.DOMAIN) : paymentIt.get(PAYMENT_CONCEPT.DOMAIN));
			
			String description = AonStringUtils.isBlank(paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(AGREEMENT_PAYMENT.DESCRIPTION);
			payment.setDescription(description);
			
			otherDomainAgreementPayments.add(payment);
		
		});
		
		return otherDomainAgreementPayments;
	}
	
	// ------------------------------- agreementIntegrityFix Methods

	public static void agreementIntegrityFix(Connection connection, Integer domainId, Integer agreementId, AgreementIntegrityFix agreementIntegrityFix) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		AgreementRecord agreement = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
		
		dslContext.transaction(configuration -> {
			switch (agreementIntegrityFix) {
				case OTHER_DOMAIN_AGREEMENT_PAYMENTS:
					fixOtherDomainAgreementPayments(dslContext, domainId, agreement);
					break;
				case AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT:
					fixAgreementPaymentsWithoutPaymentConcept(dslContext, domainId, agreement);
					break;
				case AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT:
					fixAgreementPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
					break;
				case NO_CODE_WRONG_CODE_PAYMENT_CONCEPT:
					fixNoCodeWrongCodePaymentConcept(dslContext, domainId, agreement);
					break;
				case PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION:
					fixPaymentConceptCodeInExpression(dslContext, domainId, agreement);
					break;
				case AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE:
					fixAgreementVariablesAsPaymentConceptCode(dslContext, domainId, agreement);
					break;
				case AGREEMENT_VARIABLES_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE:
					fixAgreementVariablesPaymentConceptCodeAsContextVariable(dslContext, domainId, agreement);
					break;
				case CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT:
					fixContractPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
					break;
				case CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE:
					fixContractPaymentsPaymentConceptWithoutCode(dslContext, domainId, agreement);
					break;
				case PAYMENT_CONCEPT_NO_REFERENCE:
					fixPaymentConceptNoReferences(dslContext, domainId, agreement);
					break;
				case AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD:
					fixAgreementExtraWrongFormatPeriod(dslContext, domainId, agreement);
					break;
				default:
					throw new IllegalArgumentException("No se ha podido encontrar : " + agreementIntegrityFix);
			}
		});
		
	}

	private static void fixOtherDomainAgreementPayments(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_PAYMENT.DOMAIN.ne(agreement.getDomain()))
				.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			dslContext.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DOMAIN, agreement.getDomain())
				.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
		});
	}

	private static void fixAgreementPaymentsWithoutPaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNull())
				.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			// Create payment_concept with agreement_payment info, the code is __agreementPaymentId, then set null agreement_payment fields
			Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, agreement.getDomain())
				.set(PAYMENT_CONCEPT.CODE, "__" + agreementPayment.get(AGREEMENT_PAYMENT.ID))
				.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION))
				.set(PAYMENT_CONCEPT.TYPE, agreementPayment.get(AGREEMENT_PAYMENT.TYPE))
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE))
				.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION))
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.QUOTE_EXPRESSION))
				.returning(PAYMENT_CONCEPT.ID)
				.fetchOne(PAYMENT_CONCEPT.ID);
			
			dslContext.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION))
				.set(AGREEMENT_PAYMENT.TYPE, DSL.castNull(AGREEMENT_PAYMENT.TYPE))
				.set(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE))
				.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION))
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.QUOTE_EXPRESSION))
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
				.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
		});
	}

	private static void fixAgreementPaymentsWithOtherDomainPaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull())
				.and(PAYMENT_CONCEPT.DOMAIN.ne(agreement.getDomain()))
				.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			// If payment_concept domain 0, create copy and set to agreement_payment
			// If payment_concept domain other, check if it used, 
			// 		If true create copy and set to agreement_payment
			//	 	If false update domain payment_concept
			
			Integer paymentConceptDomain = agreementPayment.get(PAYMENT_CONCEPT.DOMAIN);
			if(paymentConceptDomain.equals(0)) {
				Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, agreement.getDomain())
						.set(PAYMENT_CONCEPT.CODE, agreementPayment.get(PAYMENT_CONCEPT.CODE))
						.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION))
						.set(PAYMENT_CONCEPT.TYPE, agreementPayment.get(PAYMENT_CONCEPT.TYPE))
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE))
						.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION))
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.IRPF_EXPRESSION))
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION))
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne(PAYMENT_CONCEPT.ID);
				
				dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
					.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
					.execute();
			} else {
				List<Integer> agreementPaymentDomains = dslContext.selectDistinct(AGREEMENT_PAYMENT.DOMAIN)
					.from(AGREEMENT_PAYMENT)
					.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
					.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
					.fetch(AGREEMENT_PAYMENT.DOMAIN);
				
				List<Integer> contractPaymentDomains = dslContext.selectDistinct(CONTRACT_PAYMENT.DOMAIN)
					.from(CONTRACT_PAYMENT)
					.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
					.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
					.fetch(CONTRACT_PAYMENT.DOMAIN);
				
				Set<Integer> uniqueDomains = new HashSet<Integer>();
				uniqueDomains.addAll(agreementPaymentDomains);
				uniqueDomains.addAll(contractPaymentDomains);
				
				// Update payment_concept domain
				if(uniqueDomains.size() == 1) {
					dslContext.update(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, agreement.getDomain())
						.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
						.execute();
				} else {
					// Duplicate payment_concept domain
					Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
							.set(PAYMENT_CONCEPT.DOMAIN, agreement.getDomain())
							.set(PAYMENT_CONCEPT.CODE, agreementPayment.get(PAYMENT_CONCEPT.CODE))
							.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION))
							.set(PAYMENT_CONCEPT.TYPE, agreementPayment.get(PAYMENT_CONCEPT.TYPE))
							.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE))
							.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION))
							.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.IRPF_EXPRESSION))
							.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION))
							.returning(PAYMENT_CONCEPT.ID)
							.fetchOne(PAYMENT_CONCEPT.ID);
					
					dslContext.update(AGREEMENT_PAYMENT)
						.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
						.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
						.execute();
				}
			}
		});
	}

	private static void fixNoCodeWrongCodePaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixPaymentConceptCodeInExpression(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixAgreementVariablesAsPaymentConceptCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixAgreementVariablesPaymentConceptCodeAsContextVariable(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixContractPaymentsWithOtherDomainPaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixContractPaymentsPaymentConceptWithoutCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixPaymentConceptNoReferences(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixAgreementExtraWrongFormatPeriod(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	
}
