package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

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
	
	// DateRange for extras
	
	public static class DateRange {
        public String issueDate;
        public String startDate;
        public String endDate;

        public DateRange(String issueDate, String startDate, String endDate) {
            this.issueDate = issueDate;
            this.startDate = startDate;
            this.endDate = endDate;
        }

		public String getIssueDate() {
			return issueDate;
		}

		public String getStartDate() {
			return startDate;
		}

		public String getEndDate() {
			return endDate;
		}
        
    }

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
		
		// Expressions with var like payment_concept code
		List<Record> codeInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						expressionCodeCheck(agreementPayment, agreementPayments)
				)
				.collect(Collectors.toList());
		
		agreementIntegrity.setCodeInExpression(parseCodeInExpression(codeInExpression));
		
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
		
		// Var like payment_concept code not contains in expressions
		List<String> variableLikeCodes = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						variables.contains(agreementPayment.get(PAYMENT_CONCEPT.CODE)) &&
						!containsExpression(agreementPayment.get(PAYMENT_CONCEPT.CODE), agreementPayments)
				)
				.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setVariableLikeCodes(variableLikeCodes);
		
		// Concept codes like contextVariables
		Set<String> variablesCodes = new HashSet<String>();
		
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
		
		// Contract Payments whith payment_concept with out code
		List<Record> paymentConceptsNoCodeContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					null == contractPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		agreementIntegrity.setPaymentConceptsNoCodeContracts(parsePaymentConceptsNoCodeContracts(paymentConceptsNoCodeContracts));
		
		// Payment Concept with no agreement_payment or contract_payment or system_payment
		Result<Record> paymentConceptsNoRef = dslContext.select()
				.from(PAYMENT_CONCEPT)
				.leftJoin(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(CONTRACT_PAYMENT).on(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(SYSTEM_PAYMENT).on(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.isNull())
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
	
	private static boolean containsExpression(String code, Result<Record> agreementPayments) {
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
		
		return !appearence.isEmpty();
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
				messages.add("El c\u00f3digo de concepto " + variablesCode + " no se puede usar ya que es una variable de contexto");
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
			if(null == agreementIntegrityFix) {
				fixOtherDomainAgreementPayments(dslContext, domainId, agreement);
				fixAgreementPaymentsWithoutPaymentConcept(dslContext, domainId, agreement);
				fixAgreementPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				fixNoCodeWrongCodePaymentConcept(dslContext, domainId, agreement);
				fixPaymentConceptCodeInExpression(dslContext, domainId, agreement);
				fixAgreementVariablesAsPaymentConceptCode(dslContext, domainId, agreement);
				fixAgreementVariablesPaymentConceptCodeAsContextVariable(dslContext, domainId, agreement);
				fixContractPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				fixContractPaymentsPaymentConceptWithoutCode(dslContext, domainId, agreement);
				fixPaymentConceptNoReferences(dslContext, domainId, agreement);
				fixAgreementExtraWrongFormatPeriod(dslContext, domainId, agreement);
			} else {
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
//					case AGREEMENT_VARIABLES_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE:
//						fixAgreementVariablesPaymentConceptCodeAsContextVariable(dslContext, domainId, agreement);
//						break;
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
				
				List<Integer> systemPaymentDomains = dslContext.selectDistinct(SYSTEM_PAYMENT.DOMAIN)
						.from(SYSTEM_PAYMENT)
						.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(SYSTEM_PAYMENT.PAYMENT_CONCEPT))
						.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
						.fetch(SYSTEM_PAYMENT.DOMAIN);
				
				Set<Integer> uniqueDomains = new HashSet<Integer>();
				uniqueDomains.addAll(agreementPaymentDomains);
				uniqueDomains.addAll(contractPaymentDomains);
				uniqueDomains.addAll(systemPaymentDomains);
				
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
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull())
				.fetch();
		
		List<Record> paymentConceptsNoCode = agreementPayments.stream()
				.filter(agreementPayment -> 
					null == agreementPayment.get(PAYMENT_CONCEPT.CODE) || 
					checkPaymentConceptCode(agreementPayment.get(PAYMENT_CONCEPT.CODE))
				).collect(Collectors.toList());
		
		
		paymentConceptsNoCode.forEach(paymentConcept -> {
			// Set default code as '__id'
			if(AonStringUtils.isBlank(paymentConcept.get(PAYMENT_CONCEPT.CODE))) {
				String newCode = "__" + paymentConcept.get(PAYMENT_CONCEPT.ID);
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.CODE, newCode)
					.where(PAYMENT_CONCEPT.ID.eq(paymentConcept.get(PAYMENT_CONCEPT.ID)))
					.execute();
			} else {
				// Fix code format
				String code = paymentConcept.get(PAYMENT_CONCEPT.CODE);
				String newCode = code.replaceAll("[^A-Za-z0-9_]", "_");
				
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.CODE, newCode)
					.where(PAYMENT_CONCEPT.ID.eq(paymentConcept.get(PAYMENT_CONCEPT.ID)))
					.execute();
			
			}
		});
	}

	private static void fixPaymentConceptCodeInExpression(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull())
				.fetch();
		
		List<Record> codeInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						expressionCodeCheck(agreementPayment, agreementPayments)
				)
				.collect(Collectors.toList());
		
		codeInExpression.forEach(code -> {
			String expression = AonStringUtils.isBlank(code.get(AGREEMENT_PAYMENT.EXPRESSION)) ? code.get(PAYMENT_CONCEPT.EXPRESSION) : code.get(AGREEMENT_PAYMENT.EXPRESSION);
			String codeValue = code.get(PAYMENT_CONCEPT.CODE);
			String newCode = "I_" + codeValue;
			
			String newExpression = renameVariable(expression, codeValue, newCode);
			
			if(AonStringUtils.isBlank(code.get(AGREEMENT_PAYMENT.EXPRESSION)))
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.EXPRESSION, newExpression)
					.where(PAYMENT_CONCEPT.ID.eq(code.get(PAYMENT_CONCEPT.ID)))
					.execute();
			else
				dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.EXPRESSION, newExpression)
					.where(AGREEMENT_PAYMENT.ID.eq(code.get(AGREEMENT_PAYMENT.ID)))
					.execute();
			
			// Update agreement_data, agreement_level_data, contract_data, salary_data
			
			dslContext.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.NAME, newCode)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_DATA.NAME.eq(codeValue))
				.execute();
			
			dslContext.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.NAME, newCode)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(
						dslContext.select(AGREEMENT_LEVEL.ID)
							.from(AGREEMENT_LEVEL)
							.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
				)).and(AGREEMENT_LEVEL_DATA.NAME.eq(codeValue))
				.execute();
			
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.NAME, newCode)
				.where(CONTRACT_DATA.CONTRACT.in(
						dslContext.select(CONTRACT.ID)
							.from(CONTRACT)
							.where(CONTRACT.AGREEMENT_LEVEL.in(
									dslContext.select(AGREEMENT_LEVEL.ID)
										.from(AGREEMENT_LEVEL)
										.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
							))
				)).and(CONTRACT_DATA.NAME.eq(codeValue))
				.execute();
			
			dslContext.update(SALARY_DATA)
				.set(SALARY_DATA.NAME, newCode)
				.where(SALARY_DATA.SALARY.in(
						dslContext.select(SALARY.ID)
							.from(SALARY)
							.where(SALARY.CONTRACT.in(
									dslContext.select(CONTRACT.ID)
										.from(CONTRACT)
										.where(CONTRACT.AGREEMENT_LEVEL.in(
												dslContext.select(AGREEMENT_LEVEL.ID)
													.from(AGREEMENT_LEVEL)
													.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
										))
							))
				)).and(SALARY_DATA.NAME.eq(codeValue))
				.execute();
		});
		
	}

	private static void fixAgreementVariablesAsPaymentConceptCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Set<String> variables = getAgreementVariables(dslContext, agreement.getId());
		
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull().and(PAYMENT_CONCEPT.CODE.isNotNull()))
				.fetch();
		
		List<String> variableLikeCodesNotCointainsInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						variables.contains(agreementPayment.get(PAYMENT_CONCEPT.CODE)) &&
						!containsExpression(agreementPayment.get(PAYMENT_CONCEPT.CODE), agreementPayments)
				)
				.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		variableLikeCodesNotCointainsInExpression.forEach(code -> {
			String newCode = "I_" + code;
			
			// Update agreement_data, agreement_level_data, contract_data, salary_data
			
			dslContext.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.NAME, newCode)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_DATA.NAME.eq(code))
				.execute();
			
			dslContext.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.NAME, newCode)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(
						dslContext.select(AGREEMENT_LEVEL.ID)
							.from(AGREEMENT_LEVEL)
							.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
				)).and(AGREEMENT_LEVEL_DATA.NAME.eq(code))
				.execute();
			
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.NAME, newCode)
				.where(CONTRACT_DATA.CONTRACT.in(
						dslContext.select(CONTRACT.ID)
							.from(CONTRACT)
							.where(CONTRACT.AGREEMENT_LEVEL.in(
									dslContext.select(AGREEMENT_LEVEL.ID)
										.from(AGREEMENT_LEVEL)
										.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
							))
				)).and(CONTRACT_DATA.NAME.eq(code))
				.execute();
			
			dslContext.update(SALARY_DATA)
				.set(SALARY_DATA.NAME, newCode)
				.where(SALARY_DATA.SALARY.in(
						dslContext.select(SALARY.ID)
							.from(SALARY)
							.where(SALARY.CONTRACT.in(
									dslContext.select(CONTRACT.ID)
										.from(CONTRACT)
										.where(CONTRACT.AGREEMENT_LEVEL.in(
												dslContext.select(AGREEMENT_LEVEL.ID)
													.from(AGREEMENT_LEVEL)
													.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
										))
							))
				)).and(SALARY_DATA.NAME.eq(code))
				.execute();
		});
	}

	private static void fixAgreementVariablesPaymentConceptCodeAsContextVariable(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Set<String> variables = getAgreementVariables(dslContext, agreement.getId());
		
		Set<String> variablesCodes = new HashSet<String>();
		variablesCodes.addAll(variables);
		
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull().and(PAYMENT_CONCEPT.CODE.isNotNull()))
				.fetch();
		
		variablesCodes.addAll(
				agreementPayments.stream()
					.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
					.collect(Collectors.toSet())
		);
		
		variablesCodes.forEach(variablesCode -> {
			if(ContextVariable.isContextVariable(variablesCode)) {
				String newCode = "I_" + variablesCode;
				
				agreementPayments.forEach(agreementPayment -> {
					String expression = AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION);
					
					String newExpression = renameVariable(expression, variablesCode, newCode);
					
					if(AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE"))
						dslContext.update(PAYMENT_CONCEPT)
							.set(PAYMENT_CONCEPT.EXPRESSION, newExpression)
							.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
							.execute();
					else
						dslContext.update(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.EXPRESSION, newExpression)
							.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
							.execute();
				});
				
				// Update agreement_data, agreement_level_data, contract_data, salary_data
				
				dslContext.update(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.NAME, newCode)
					.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
					.and(AGREEMENT_DATA.NAME.eq(variablesCode))
					.execute();
				
				dslContext.update(AGREEMENT_LEVEL_DATA)
					.set(AGREEMENT_LEVEL_DATA.NAME, newCode)
					.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(
							dslContext.select(AGREEMENT_LEVEL.ID)
								.from(AGREEMENT_LEVEL)
								.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
					)).and(AGREEMENT_LEVEL_DATA.NAME.eq(variablesCode))
					.execute();
				
				dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.NAME, newCode)
					.where(CONTRACT_DATA.CONTRACT.in(
							dslContext.select(CONTRACT.ID)
								.from(CONTRACT)
								.where(CONTRACT.AGREEMENT_LEVEL.in(
										dslContext.select(AGREEMENT_LEVEL.ID)
											.from(AGREEMENT_LEVEL)
											.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
								))
					)).and(CONTRACT_DATA.NAME.eq(variablesCode))
					.execute();
				
				dslContext.update(SALARY_DATA)
					.set(SALARY_DATA.NAME, newCode)
					.where(SALARY_DATA.SALARY.in(
							dslContext.select(SALARY.ID)
								.from(SALARY)
								.where(SALARY.CONTRACT.in(
										dslContext.select(CONTRACT.ID)
											.from(CONTRACT)
											.where(CONTRACT.AGREEMENT_LEVEL.in(
													dslContext.select(AGREEMENT_LEVEL.ID)
														.from(AGREEMENT_LEVEL)
														.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))	
											))
								))
					)).and(SALARY_DATA.NAME.eq(variablesCode))
					.execute();
			}
		});
	}

	private static void fixContractPaymentsWithOtherDomainPaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixContractPaymentsPaymentConceptWithoutCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// TODO Auto-generated method stub
		
	}

	private static void fixPaymentConceptNoReferences(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> paymentConceptsNoRef = dslContext.select()
				.from(PAYMENT_CONCEPT)
				.leftJoin(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(CONTRACT_PAYMENT).on(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.leftJoin(SYSTEM_PAYMENT).on(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.isNull())
				.and(PAYMENT_CONCEPT.DOMAIN.gt(0).and(PAYMENT_CONCEPT.DOMAIN.eq(domainId)))
				.fetch();
		
		paymentConceptsNoRef.forEach(paymentConceptNoref -> 
			dslContext.delete(PAYMENT_CONCEPT)
				.where(PAYMENT_CONCEPT.ID.eq(paymentConceptNoref.get(PAYMENT_CONCEPT.ID)))
				.execute()
		);
	}

	private static void fixAgreementExtraWrongFormatPeriod(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// Agreement Payment Extra wrong format 
		List<Record> records = new ArrayList<>();
		
		Result<Record> agreementExtras = dslContext.select()
				.from(AGREEMENT_EXTRA)
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(
					AGREEMENT_EXTRA.START_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$")
		            .or(AGREEMENT_EXTRA.END_DATE.notLikeRegex("^\\d{2}/\\d{2}( -1)?$"))
		            .or(AGREEMENT_EXTRA.ISSUE_DATE.notLikeRegex("^\\d{2}/\\d{2}$"))
				).fetch();
		
		records.addAll(agreementExtras);
		
		Result<Record> agreementExtrasDatesR = dslContext.select()
				.from(AGREEMENT_EXTRA)
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(
					AGREEMENT_EXTRA.START_DATE.likeRegex("^\\d{2}/\\d{2}( -1)?$")
		            .and(AGREEMENT_EXTRA.END_DATE.likeRegex("^\\d{2}/\\d{2}( -1)?$"))
		            .and(AGREEMENT_EXTRA.ISSUE_DATE.likeRegex("^\\d{2}/\\d{2}$"))
				).fetch();
		
		List<Record> agreementExtrasDates = agreementExtrasDatesR.stream().filter(agreementExtrasDate -> checkDatesPeriod(agreementExtrasDate)).collect(Collectors.toList());
		
		records.addAll(agreementExtrasDates);
		
		records.forEach(agreementExtra -> {
			DateRange range = processDates(
					agreementExtra.get(AGREEMENT_EXTRA.ISSUE_DATE), 
					agreementExtra.get(AGREEMENT_EXTRA.START_DATE), 
					agreementExtra.get(AGREEMENT_EXTRA.END_DATE),
					null == agreementExtra.get(AGREEMENT_PAYMENT.TYPE) ? agreementExtra.get(PAYMENT_CONCEPT.TYPE) : agreementExtra.get(AGREEMENT_PAYMENT.TYPE)
			);
			
			dslContext.update(AGREEMENT_EXTRA)
				.set(AGREEMENT_EXTRA.ISSUE_DATE, range.getIssueDate())
				.set(AGREEMENT_EXTRA.START_DATE, range.getStartDate())
				.set(AGREEMENT_EXTRA.END_DATE, range.getEndDate())
				.where(AGREEMENT_EXTRA.ID.eq(agreementExtra.get(AGREEMENT_EXTRA.ID)))
				.execute();
		});
	}

	private static Set<String> getAgreementVariables(DSLContext dslContext, Integer agreementId){
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
		
		return variables;
	}
	
	private static String renameVariable(String expression, String oldVarName, String newVarName) {
		ParserContext context = new ParserContext();
        MVEL.compileExpression(expression, context);

        Set<String> variables = context.getInputs().keySet();

        if (variables.contains(oldVarName)) {
        	// Reemplazar solo variables exactas, no substrings
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(oldVarName) + "\\b");
            Matcher matcher = pattern.matcher(expression);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(newVarName));
            }
            
            matcher.appendTail(result);

            return result.toString();
        } else 
        	return expression;
    }
	
	public static DateRange processDates(String issueDate, String startDate, String endDate, byte type) {
        issueDate = normalizeDate(issueDate);
        startDate = normalizeDate(startDate);
        endDate = normalizeDate(endDate);

        if (!isValidDate(issueDate) || !isValidDate(startDate) || !isValidDate(endDate)) {
            throw new IllegalArgumentException("Alguna de las fechas no es válida");
        }

        String finalStartDate;
        String finalEndDate;

        String issueMonth = issueDate.substring(3, 5);

        switch (issueMonth) {
            case "03":
                finalStartDate = "01/01 -1";
                finalEndDate = "31/12 -1";
                break;
            case "06":
            case "07":
                if (startDate.contains("-1")) {
                    finalStartDate = "01/07 -1";
                    finalEndDate = "30/06";
                } else {
                    finalStartDate = "01/01";
                    finalEndDate = "30/06";
                }
                break;
            case "12":
                if (isLessThanOrEqualSixMonths(startDate, endDate)) {
                    finalStartDate = "01/07";
                    finalEndDate = "31/12";
                } else {
                    finalStartDate = "01/01";
                    finalEndDate = "31/12";
                }
                break;
            default:
                finalStartDate = "01/01";
                finalEndDate = "31/12";
                break;
        }

        // Validación final del rango: si el período es inválido según checkDatesPeriod
        if (!checkDatesPeriod(finalStartDate, finalEndDate, type)) {
            finalStartDate = "01/01";
            finalEndDate = "31/12";
        }

        return new DateRange(issueDate, finalStartDate, finalEndDate);
    }
	
	private static String normalizeDate(String date) {
        boolean hasMinusOne = date.contains("-1");

        // Eliminar '-1' temporalmente
        String clean = date.replace(" -1", "").trim();

        String[] parts = clean.split("/");

        // Si es del tipo dd/dd/mm, ignorar el primer dd/
        if (parts.length > 2) {
            clean = parts[parts.length - 2] + "/" + parts[parts.length - 1];
        }

        String[] corrected = clean.split("/");
        if (corrected.length != 2) throw new IllegalArgumentException("Formato inválido: " + date);

        String day = corrected[0];
        String month = corrected[1];

        if (day.length() == 1) day = "0" + day;
        if (month.length() == 1) month = "0" + month;

        String normalized = day + "/" + month;
        if (hasMinusOne) normalized += " -1";

        return normalized;
    }
	
	private static boolean isValidDate(String date) {
        Pattern pattern = Pattern.compile("^(\\d{2})/(\\d{2})( -1)?$");
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) return false;

        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));

        if (month < 1 || month > 12) return false;
        if (day < 1 || day > daysInMonth(month)) return false;

        return true;
    }

    private static int daysInMonth(int month) {
        switch (month) {
            case 2: return 29; // Aceptamos febrero largo porque no hay año concreto
            case 4: case 6: case 9: case 11: return 30;
            default: return 31;
        }
    }

    private static boolean isLessThanOrEqualSixMonths(String startDate, String endDate) {
        YearMonth ymStart = parseToYearMonth(startDate);
        YearMonth ymEnd = parseToYearMonth(endDate);

        return ymEnd.minusMonths(6).compareTo(ymStart) <= 0;
    }

    private static boolean checkDatesPeriod(String startDate, String endDate, byte type) {
        Pattern pattern = Pattern.compile("^(\\d{2})/(\\d{2})( -1)?$");
        Matcher mStart = pattern.matcher(startDate);
        Matcher mEnd = pattern.matcher(endDate);

        if (!mStart.matches() || !mEnd.matches()) return false;

        int monthStart = Integer.parseInt(mStart.group(2));
        int yearOffsetStart = (mStart.group(3) != null) ? -1 : 0;

        int monthEnd = Integer.parseInt(mEnd.group(2));
        int yearOffsetEnd = (mEnd.group(3) != null) ? -1 : 0;

        YearMonth ymStart = YearMonth.of(2000 + yearOffsetStart, monthStart);
        YearMonth ymEnd = YearMonth.of(2000 + yearOffsetEnd, monthEnd);

        if (type == 4) {
            return ymEnd.minusMonths(11).equals(ymStart) || ymEnd.minusMonths(5).equals(ymStart);
        } else if (type == 5) {
            return ymEnd.minusMonths(11).equals(ymStart);
        }

        return true; // Para otros tipos, no se aplica lógica especial
    }

    private static YearMonth parseToYearMonth(String date) {
        Pattern pattern = Pattern.compile("^(\\d{2})/(\\d{2})( -1)?$");
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) throw new IllegalArgumentException("Fecha inválida: " + date);

        int month = Integer.parseInt(matcher.group(2));
        int yearOffset = matcher.group(3) != null ? -1 : 0;

        return YearMonth.of(2000 + yearOffset, month);
    }
}
