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
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrity;
import com.esferalia.aon.gwt.payroll.shared.AgreementIntegrityFix;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.jooq.tables.records.AgreementDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
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
	
	private static Set<String> allowedVars;
	
	private static Set<Record> wrongExpressions;
	
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	
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
		wrongExpressions = new HashSet<Record>();
		
		allowedVars = getAllowedConceptContextVars();
		
		AgreementIntegrity agreementIntegrity = new AgreementIntegrity();
		HashMap<AgreementIntegrityFix, List<String>> messages = new HashMap<AgreementIntegrityFix, List<String>>();
		
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
		
		// ----- Agreement Payments from other domain
		
		List<Record> otherDomainAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> !agreementPayment.get(AGREEMENT_PAYMENT.DOMAIN).equals(agreementDomain))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.OTHER_DOMAIN_AGREEMENT_PAYMENTS, parseOtherDomainAgreementPayments(agreementDomain, otherDomainAgreementPayments));
		
		// ----- Agreement Payments with out payment_concept
		
		List<Record> noPaymentConceptAgreementPayments = agreementPayments.stream()
				.filter(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.ID) == null)
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITHOUT_PAYMENT_CONCEPT, parseNoPaymentConceptAgreementPayments(noPaymentConceptAgreementPayments));
		
		// ----- Agreement Payments with payment_concept from other domain
		
		List<Record> otherDomainPaymentConcepts = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						!agreementPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(agreementDomain))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT, parseOtherDomainPaymentConcepts(agreementDomain, otherDomainPaymentConcepts));
		
		// ----- Payment Concepts with out code or wrong code
		
		List<Record> paymentConceptsNoCode = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						(
							null == agreementPayment.get(PAYMENT_CONCEPT.CODE) || 
							checkPaymentConceptCode(agreementPayment.get(PAYMENT_CONCEPT.CODE))
						)
				).collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.NO_CODE_WRONG_CODE_PAYMENT_CONCEPT, parsePaymentConceptsNoCode(paymentConceptsNoCode));
		
		// -----  Expressions (agreement_payment / payment_concept) with variables like payment_concept.code
		
		List<Record> codeInExpression = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						expressionCodeCheck(agreementPayment, agreementPayments)
				)
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.PAYMENT_CONCEPT_CODE_AS_VAR_IN_EXPRESSION, parseCodeInExpression(codeInExpression));
		
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
		
		// ----- Variables like payment_concept.code which are not contains in expressions
		
		List<String> variableLikeCodes = agreementPayments.stream()
				.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE) && 
						variables.contains(agreementPayment.get(PAYMENT_CONCEPT.CODE)) &&
						!containsExpression(agreementPayment.get(PAYMENT_CONCEPT.CODE), agreementPayments)
				)
				.map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_VARIABLES_AS_PAYMENT_CONCEPT_CODE, variableLikeCodes);
		
		// ----- payment_concept.code like contextVariables name (except allowed ones)
		
		Set<String> conceptsCodes =
				agreementPayments.stream()
					.filter(agreementPayment -> 
						agreementPayment.get(PAYMENT_CONCEPT.ID) != null && 
						null != agreementPayment.get(PAYMENT_CONCEPT.CODE)
					).map(agreementPayment -> agreementPayment.get(PAYMENT_CONCEPT.CODE))
					.collect(Collectors.toSet());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_CONCEPT_CODE_AS_CONTEXT_VARIABLE, parseVariableCodeLikeContext(conceptsCodes));
		
		// ----- Expressions (agreement_payment / payment_concept) with duplicate variables
		
		List<Record> duplicateVariablesInExpression = agreementPayments.stream()
				.filter(agreementPayment ->  duplicateVariablesInExpression(agreementPayment))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_DUPLICATE_VARIABLES, parseDuplicateVariablesInExpression(duplicateVariablesInExpression));
		
		// Contract Payments
		Result<Record> contractPayments = dslContext.select()
				.from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId)).fetch();
		
		// ----- Contract payments with payment_concept from other domain (only accepts other domain if its agreement domain, or 0)
		
		List<Record> otherDomainPaymentConceptContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					!contractPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(0) &&
					!contractPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(agreementDomain) &&
					!contractPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(contractPayment.get(CONTRACT.DOMAIN)))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.CONTRACT_PAYMENTS_WITH_OTHER_DOMAIN_PAYMENT_CONCEPT, parseOtherDomainPaymentConceptContracts(agreementDomain, otherDomainPaymentConceptContracts));
		
		// ----- Contract payments with payment_concept with out code
		
		List<Record> paymentConceptsNoCodeContracts = contractPayments.stream()
				.filter(contractPayment -> 
					contractPayment.get(PAYMENT_CONCEPT.ID) != null && 
					null == contractPayment.get(PAYMENT_CONCEPT.CODE) &&
					!contractPayment.get(PAYMENT_CONCEPT.DOMAIN).equals(0))
				.collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.CONTRACT_PAYMENTS_PAYMENT_CONCEPTS_WITHOUT_CODE, parsePaymentConceptsNoCodeContracts(paymentConceptsNoCodeContracts));
		
		// ----- payment_oncept without agreement_payment or contract_payment or system_payment reference
		
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
		
		messages.put(AgreementIntegrityFix.PAYMENT_CONCEPT_NO_REFERENCE, parsePaymentConceptsNoRef(paymentConceptsNoRef));
		
		// ----- Agreement extra wrong format 
		
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
		
		messages.put(AgreementIntegrityFix.AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD, parseAgreementExtrasMessage(agreementExtras));
		
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
		
		messages.get(AgreementIntegrityFix.AGREEMENT_EXTRA_WRONG_FORMAT_PERIOD).addAll(parseAgreementExtrasDates(agreementExtrasDates));
		
		// ----- Agreement extra wrong start or end dates
		
		List<Record> agreementExtrasStartEndDates = agreementExtrasDatesR.stream().filter(agreementExtrasDate -> !checkStartEndDates(agreementExtrasDate)).collect(Collectors.toList());
		
		messages.put(AgreementIntegrityFix.AGREEMENT_EXTRA_START_END, parseAgreementExtrasStartEndDatesMessage(agreementExtrasStartEndDates));
		
		// ----- Wrong expressions in agreement_payment or payment_concept
		
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_WRONG_EXPRESSION, parseAgreementPaymentWrongExpressionMessage());
		
		// ----- AgreementData with inherit expression
		
		Result<AgreementDataRecord> agreementInheritDatas = dslContext.selectFrom(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_DATA.EXPRESSION.like("%inherit%"))
				.fetch();
		
		List<String> agreementInheritDataMessages = new ArrayList<String>();
		agreementInheritDatas.forEach(data -> agreementInheritDataMessages.add("La expresi\u00f3n " + data.getExpression() + " contiene 'inherit'"));
		messages.put(AgreementIntegrityFix.AGREEMENT_DATA_INHERIT, agreementInheritDataMessages);
		
		// ----- AgreementLevelData with inherit expression
		
		Result<Record> agreementInheritLevelDatas = dslContext.select().from(AGREEMENT_LEVEL_DATA)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_LEVEL_DATA.EXPRESSION.like("%inherit%"))
				.fetch();
		
		List<String> agreementInheritLevelDataMessages = new ArrayList<String>();
		agreementInheritLevelDatas.forEach(data -> agreementInheritLevelDataMessages.add("La expresi\u00f3n " + data.get(AGREEMENT_LEVEL_DATA.EXPRESSION) + " del nivel " + data.get(AGREEMENT_LEVEL.DESCRIPTION) + " contiene 'inherit'"));
		messages.put(AgreementIntegrityFix.AGREEMENT_LEVEL_DATA_INHERIT, agreementInheritLevelDataMessages);
		
		// ----- AgreementPayment with inherit expression
		
		Result<AgreementPaymentRecord> agreementInheritPayments = dslContext.selectFrom(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
			.and(AGREEMENT_PAYMENT.EXPRESSION.like("%inherit%"))
			.fetch();
		
		List<String> agreementInheritPaymentMessages = new ArrayList<String>();
		agreementInheritPayments.forEach(data -> agreementInheritPaymentMessages.add("La expresi\u00f3n " + data.get(AGREEMENT_PAYMENT.EXPRESSION) + " contiene 'inherit'"));
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_INHERIT, agreementInheritPaymentMessages);
		
		// ----- ContractPayment with inherit expression
		
		Result<Record> contractInheritPayments = dslContext.select().from(CONTRACT_PAYMENT)
			.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
			.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
			.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
			.and(CONTRACT_PAYMENT.EXPRESSION.like("%inherit%"))
			.fetch();
		
		List<String> contractInheritPaymentMessages = new ArrayList<String>();
		contractInheritPayments.forEach(data -> contractInheritPaymentMessages.add("La expresi\u00f3n, del contrato " + data.get(CONTRACT_PAYMENT.CONTRACT) + ", " + data.get(CONTRACT_PAYMENT.EXPRESSION) + " contiene 'inherit'"));
		messages.put(AgreementIntegrityFix.CONTRACT_PAYMENT_INHERIT, contractInheritPaymentMessages);
		
		// ----- AgreementPayment expression same as paymentConcept.expression
		
		Result<Record> agreementPaymentPaymentConceptsSameExpr = dslContext.select().from(AGREEMENT_PAYMENT)
				.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_PAYMENT.EXPRESSION.eq(PAYMENT_CONCEPT.EXPRESSION))
				.and(AGREEMENT_PAYMENT.DOMAIN.eq(PAYMENT_CONCEPT.DOMAIN))
				.fetch();
		
		List<String> agreementPaymentPaymentConceptsSameExprMessages = new ArrayList<String>();
		agreementPaymentPaymentConceptsSameExpr.forEach(data -> agreementPaymentPaymentConceptsSameExprMessages.add("La expresi\u00f3n " + data.get(AGREEMENT_PAYMENT.EXPRESSION) + " es igual a la expresi\u00f3n de su concepto"));
		messages.put(AgreementIntegrityFix.AGREEMENT_PAYMENT_PAYMENT_CONCEPT_SAME_EXPR, agreementPaymentPaymentConceptsSameExprMessages);
		
		
		agreementIntegrity.setMessages(messages);
		
		return agreementIntegrity;
	}

	private static boolean checkStartEndDates(Record agreementExtrasDate) {
		// Based on dd/MM o dd/MM -1 format check if start_date starts 01 & end_date las day of month
		int testYear = 2024;

		String[] startMainParts = agreementExtrasDate.get(AGREEMENT_EXTRA.START_DATE).split(" ");
		String[] startParts = startMainParts[0].split("/");
        String startDay = startParts[0];

         if (!startDay.equals("01")) return false;

        String[] endMainParts = agreementExtrasDate.get(AGREEMENT_EXTRA.END_DATE).split(" ");
        String[] endParts = endMainParts[0].split("/");

        int endMonth = Integer.parseInt(endParts[1]);
        int endDay = Integer.parseInt(endParts[0]);

        // Ajuste si se indica "-1"
        int yearAdjustment = (endMainParts.length > 1 && endMainParts[1].equals("-1")) ? -1 : 0;

        YearMonth ym = YearMonth.of(testYear + yearAdjustment, endMonth);
        int lastDayOfMonth = ym.lengthOfMonth();

        return endDay == lastDayOfMonth;
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
			String expression = AonStringUtils.isBlank(agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPaymentIt.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION);
			
			try {
				// Compilar la expresión
		        ParserContext context = new ParserContext();
		        MVEL.compileExpression(expression, context);
	
		        // Obtener las variables
		        Set<String> variables = context.getInputs().keySet();
		        
		        if(variables.contains(code)) appearence.add(agreementPaymentIt);
			} catch (Exception e) {
				System.out.println("expressionCodeCheck --> Wrong expression from agreement_payment: " + agreementPaymentIt.get(AGREEMENT_PAYMENT.ID));
				wrongExpressions.add(agreementPaymentIt);
			}
		});
		
		return !appearence.isEmpty() && appearence.size() > 1 && appearence.stream().filter(a -> a.get(AGREEMENT_PAYMENT.ID).equals(agreementPayment.get(AGREEMENT_PAYMENT.ID))).count() > 0;
	}
	
	private static boolean containsExpression(String code, Result<Record> agreementPayments) {
		if(AonStringUtils.isBlank(code) || agreementPayments.isEmpty()) return false;
		
		List<Record> appearence = new ArrayList<Record>();
		
		agreementPayments.forEach(agreementPaymentIt -> {
			String expression = AonStringUtils.isBlank(agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPaymentIt.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPaymentIt.get(AGREEMENT_PAYMENT.EXPRESSION);
			
			try {
				// Compilar la expresión
		        ParserContext context = new ParserContext();
		        MVEL.compileExpression(expression, context);
	
		        // Obtener las variables
		        Set<String> variables = context.getInputs().keySet();
		        
		        if(variables.contains(code)) appearence.add(agreementPaymentIt);
			} catch (Exception e) {
				System.out.println("containsExpression --> Wrong expression from agreement_payment: " + agreementPaymentIt.get(AGREEMENT_PAYMENT.ID));
				wrongExpressions.add(agreementPaymentIt);
			}
		});
		
		return !appearence.isEmpty();
	}
	
	private static boolean duplicateVariablesInExpression(Record agreementPayment) {
		String expression = AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION);
		
		try {
			// 1. Eliminar comentarios tipo /* ... */
	        expression = expression.replaceAll("/\\*.*?\\*/", "");

	        // 2. Buscar variables: palabras que tengan solo letras mayúsculas, números o guión bajo
	        Pattern varPattern = Pattern.compile("\\b[A-ZÑ_][A-Z\u00d10-9_]*\\b");
	        Matcher matcher = varPattern.matcher(expression);

	        Map<String, Integer> variableCounts = new LinkedHashMap<>();
	        while (matcher.find()) {
	            String var = matcher.group();
	            variableCounts.put(var, variableCounts.getOrDefault(var, 0) + 1);
	        }

	        // 3. Verificar duplicados
	        boolean hasDuplicates = false;
	        
	        for (Map.Entry<String, Integer> entry : variableCounts.entrySet()) {
	            if (entry.getValue() > 1) {
	               hasDuplicates = true;
	            }
	        }

	        return hasDuplicates;
		} catch (Exception e) {
			System.out.println("duplicateVariablesInExpression --> Wrong expression from agreement_payment (id.: " + agreementPayment.get(AGREEMENT_PAYMENT.ID) + "). Expression: " + expression);
			return false;
		}
	}
	
	private static Set<String> getDuplicateVariablesInExpression(Record agreementPayment) {
		String expression = AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION);
		
		Set<String> duplicates = new LinkedHashSet<>();
		
		try {
			 // Eliminar comentarios tipo /* ... */
	        expression = expression.replaceAll("/\\*.*?\\*/", "");

	        // Buscar variables válidas (letras mayúsculas, Ñ, números y _)
	        Pattern varPattern = Pattern.compile("\\b[A-ZÑ_][A-Z\u00d10-9_]*\\b");
	        Matcher matcher = varPattern.matcher(expression);

	        Map<String, Integer> variableCounts = new LinkedHashMap<>();
	        while (matcher.find()) {
	            String var = matcher.group();
	            variableCounts.put(var, variableCounts.getOrDefault(var, 0) + 1);
	        }

	        // Agregar al set las variables repetidas
	        for (Map.Entry<String, Integer> entry : variableCounts.entrySet()) {
	            if (entry.getValue() > 1) {
	                duplicates.add(entry.getKey());
	            }
	        }
		} catch (Exception e) {
			System.out.println("getDuplicateVariablesInExpression --> Wrong expression from agreement_payment (id.: " + agreementPayment.get(AGREEMENT_PAYMENT.ID) + "). Expression: " + expression);
		}
		
		return duplicates;
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
			if(isContextVariable(variablesCode))
				messages.add("El c\u00f3digo de concepto " + variablesCode + " no se puede usar ya que es una variable de contexto");
		});
		
		return messages;
	}
	
	private static List<String> parseDuplicateVariablesInExpression(List<Record> agreementPayments) {
		List<String> messages = new ArrayList<String>();
		
		agreementPayments.forEach(agreementPayment -> {
			Set<String> duplicateVariables = getDuplicateVariablesInExpression(agreementPayment);
			
			String expression = AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION) : agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION);
			String description = AonStringUtils.isBlank(agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION) : agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION);
			
			if(!duplicateVariables.isEmpty())
				messages.add("La variable(s) " + String.join(", ", duplicateVariables) + " se encuentra(n) duplicada(s) en el devengo " + description + "\ncuya expresi\u00f3n es " + expression);
		});
		
		return messages;	
	}
	
	private static List<String> parseOtherDomainPaymentConceptContracts(Integer agreementDomain, List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("En el contrato de "+ paymentIt.get(REGISTRY.NAME) + " (Dom.: " + paymentIt.get(CONTRACT.DOMAIN) + ") existe el devengo \n" + description + " que esta en otro dominio al del concepto (Dom.: " +  paymentIt.get(PAYMENT_CONCEPT.DOMAIN) + ")");
			});
		
		return messages;
	}
	
	private static List<String> parsePaymentConceptsNoCodeContracts(List<Record> payments) {
		List<String> messages = new ArrayList<String>();
		
		payments.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("En el contrato de "+ paymentIt.get(REGISTRY.NAME) + " existe el devengo \n" + description + " que  tiene un concepto (Dom.: " + paymentIt.get(PAYMENT_CONCEPT.DOMAIN) + ") sin c\u00f3digo");
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
	
	private static List<String> parseAgreementExtrasStartEndDatesMessage(List<Record> agreementExtras) {
		List<String> messages = new ArrayList<String>();
		
		agreementExtras.forEach(paymentIt -> {
			String description = AonStringUtils.isBlank(paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION)) ? paymentIt.get(PAYMENT_CONCEPT.DESCRIPTION) : paymentIt.get(CONTRACT_PAYMENT.DESCRIPTION);
			messages.add("La extra " + description + " (F.Ini: " + paymentIt.get(AGREEMENT_EXTRA.START_DATE) + ", F.Fin : " + paymentIt.get(AGREEMENT_EXTRA.END_DATE) + ") no empieza o termina acorde al inicio o al fin del mes");
		});
		
		return messages;
	}
	
	private static List<String> parseAgreementPaymentWrongExpressionMessage() {
		List<String> messages = new ArrayList<String>();
		
		wrongExpressions.forEach(wrongExpression -> {
			String expression = AonStringUtils.isBlank(wrongExpression.get(AGREEMENT_PAYMENT.EXPRESSION)) || AonStringUtils.containsIgnoreCase(wrongExpression.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") ? wrongExpression.get(PAYMENT_CONCEPT.EXPRESSION) : wrongExpression.get(AGREEMENT_PAYMENT.EXPRESSION);
			String description = AonStringUtils.isBlank(wrongExpression.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? wrongExpression.get(PAYMENT_CONCEPT.DESCRIPTION) : wrongExpression.get(AGREEMENT_PAYMENT.DESCRIPTION);
			
			messages.add("El devengo " + description + " (Id: " + wrongExpression.get(AGREEMENT_PAYMENT.ID) + ") cuya expresi\u00f3n es \n" + expression + " tiene un formato que no es valido");
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
		
		allowedVars = getAllowedConceptContextVars();
		
		AgreementRecord agreement = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
		
		dslContext.transaction(configuration -> {
			if(null == agreementIntegrityFix) {
				fixAgreementDataInherit(dslContext, domainId, agreement);
				fixAgreementLevelDataInherit(dslContext, domainId, agreement);
				fixAgreementPaymentDataInherit(dslContext, domainId, agreement);
				fixContractPaymentDataInherit(dslContext, domainId, agreement);
				
				fixOtherDomainAgreementPayments(dslContext, domainId, agreement);
				fixAgreementPaymentsWithoutPaymentConcept(dslContext, domainId, agreement);
				fixAgreementPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				fixNoCodeWrongCodePaymentConcept(dslContext, domainId, agreement);
				fixPaymentConceptCodeInExpression(dslContext, domainId, agreement);
				fixAgreementVariablesAsPaymentConceptCode(dslContext, domainId, agreement);
				
//				fixAgreementVariablesPaymentConceptCodeAsContextVariable(dslContext, domainId, agreement);
				
				fixContractPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				fixContractPaymentsPaymentConceptWithoutCode(dslContext, domainId, agreement);
				fixPaymentConceptNoReferences(dslContext, domainId, agreement);
				fixAgreementExtraWrongFormatPeriod(dslContext, domainId, agreement);

				fixAgreementPaymentExpression(dslContext, domainId, agreement);
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
					case AGREEMENT_EXTRA_START_END:
						fixAgreementExtraStartEndDates(dslContext, domainId, agreement);
						break;
					case AGREEMENT_DATA_INHERIT:
						fixAgreementDataInherit(dslContext, domainId, agreement);
						break;
					case AGREEMENT_LEVEL_DATA_INHERIT:
						fixAgreementLevelDataInherit(dslContext, domainId, agreement);
						break;
					case AGREEMENT_PAYMENT_INHERIT:
						fixAgreementPaymentDataInherit(dslContext, domainId, agreement);
						break;
					case CONTRACT_PAYMENT_INHERIT:
						fixContractPaymentDataInherit(dslContext, domainId, agreement);
						break;
					case AGREEMENT_PAYMENT_PAYMENT_CONCEPT_SAME_EXPR:
						fixAgreementPaymentExpression(dslContext, domainId, agreement);
						break;
					default:
						throw new IllegalArgumentException("No se ha podido encontrar : " + agreementIntegrityFix);
				}
			}
			
//			JooqAgreementIntegrityCalculator.checkSalaries(connection, domainId, agreementId);
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
		
		System.out.println("Agreement Integrity : agreement payments relocated from other domains --> " + agreementPayments.size());
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
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.QUOTE_EXPRESSION))
				.set(PAYMENT_CONCEPT.EXPRESSION, 
						AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) && AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE")
							? removeInheritExpr( AonStringUtils.substringAfter(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE();") )
							: removeInheritExpr(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION))
				)
				.returning(PAYMENT_CONCEPT.ID)
				.fetchOne(PAYMENT_CONCEPT.ID);
			
			// AgreementPayment
			UpdateSetMoreStep<AgreementPaymentRecord> updateAgreementPayment = dslContext.update(AGREEMENT_PAYMENT)
//				.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION))
				.set(AGREEMENT_PAYMENT.TYPE, DSL.castNull(AGREEMENT_PAYMENT.TYPE))
				.set(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, (byte)0)
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.QUOTE_EXPRESSION))
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
				;
			
			// If disable set agreementPaymente expression else null
			if(AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) && AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE"))
				updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, "DISABLE();");
			else
				updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
			
			updateAgreementPayment
				.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
			
		});
		
		System.out.println("Agreement Integrity : payment concept inserted for agreement payment without payment concept --> " + agreementPayments.size());
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
			Integer oldPaymentConceptId = agreementPayment.get(PAYMENT_CONCEPT.ID);
			
			// Si tienen expression el agreement_payment se usa esta como expresion del nueveo paymentConcept (ojo el disabled, eliminarlo si es necesario)
			// Si no tiene se usa la expresion del antiguo payment_concept (el que estamos copiando)
			
			Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DOMAIN, agreement.getDomain())
					.set(PAYMENT_CONCEPT.CODE, agreementPayment.get(PAYMENT_CONCEPT.CODE))
					.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION))
					.set(PAYMENT_CONCEPT.TYPE, agreementPayment.get(PAYMENT_CONCEPT.TYPE))
					.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE))
					.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.IRPF_EXPRESSION))
					.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, agreementPayment.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION))
					.set(PAYMENT_CONCEPT.EXPRESSION, 
							AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION))
								? (
										AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE")
											? removeInheritExpr(AonStringUtils.substringAfter(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE();"))
											: removeInheritExpr(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION))
								)
								: removeInheritExpr(agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION))
					)
					.returning(PAYMENT_CONCEPT.ID)
					.fetchOne(PAYMENT_CONCEPT.ID);
			
			// AgreementPayment
			UpdateSetMoreStep<AgreementPaymentRecord> updateAgreementPayment = dslContext.update(AGREEMENT_PAYMENT)
//				.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION))
				.set(AGREEMENT_PAYMENT.TYPE, DSL.castNull(AGREEMENT_PAYMENT.TYPE))
				.set(AGREEMENT_PAYMENT.DESCRIPTION_DECORABLE, (byte)0)
				.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
				.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.QUOTE_EXPRESSION))
				.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
				;
			
			// If disable set agreementPaymente expression else null
			if(AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) && AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE"))
				updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, "DISABLE();");
			else
				updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
			
			updateAgreementPayment
				.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
			
			// ContractPayment
			List<Record> contractPayments = dslContext.select().from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(oldPaymentConceptId))
				.fetch();
			
			contractPayments.forEach(contractPayment -> {
				
				// Si el contractPayment ha modificar esta redefinido se deja, si no, se arrastra la expression del antiguo paymentConcept
				
				String oldPaymentConceptExpr = agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION);
				
				dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
					.set(CONTRACT_PAYMENT.EXPRESSION, 
							AonStringUtils.isNotBlank(contractPayment.get(CONTRACT_PAYMENT.EXPRESSION))
								? contractPayment.get(CONTRACT_PAYMENT.EXPRESSION) 
								: oldPaymentConceptExpr
					)
					.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(oldPaymentConceptId))
					.and(CONTRACT_PAYMENT.ID.eq(contractPayment.get(CONTRACT_PAYMENT.ID)))
					.execute();
			});
			
			System.out.println("Agreement Integrity : payment concept " + oldPaymentConceptId + ", download from domain 0 to agreement (PC: " + paymentConceptId + ")");
			System.out.println("Agreement Integrity : contract payments update new payment concept --> " + contractPayments.size());
		});
	}

	private static void fixNoCodeWrongCodePaymentConcept(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select()
				.from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.and(PAYMENT_CONCEPT.ID.isNotNull())
				.and(PAYMENT_CONCEPT.DOMAIN.ne(0))
				.fetch();
		
		List<Record> paymentConceptsNoCode = agreementPayments.stream()
				.filter(agreementPayment -> 
					null == agreementPayment.get(PAYMENT_CONCEPT.CODE) || 
					checkPaymentConceptCode(agreementPayment.get(PAYMENT_CONCEPT.CODE))
				).collect(Collectors.toList());
		
		
		paymentConceptsNoCode.forEach(paymentConcept -> {
			// Set default code as '__id'
			if(AonStringUtils.isBlank(paymentConcept.get(PAYMENT_CONCEPT.CODE))) {
				String newCode = "__" + paymentConcept.get(AGREEMENT_PAYMENT.ID);
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
			if(isContextVariable(variablesCode)) {
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
		// Contract Payments
		Result<Record> otherDomainPaymentConceptContracts = dslContext.select()
				.from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(
					PAYMENT_CONCEPT.ID.isNotNull()
					.and(PAYMENT_CONCEPT.DOMAIN.ne(0))
					.and(PAYMENT_CONCEPT.DOMAIN.ne(agreement.getDomain()))
					.and(PAYMENT_CONCEPT.DOMAIN.ne(CONTRACT.DOMAIN))
				).fetch();
		
		otherDomainPaymentConceptContracts.forEach(contractPayment -> {
			Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DOMAIN, contractPayment.get(CONTRACT_PAYMENT.DOMAIN))
					.set(PAYMENT_CONCEPT.CODE, contractPayment.get(PAYMENT_CONCEPT.CODE))
					.set(PAYMENT_CONCEPT.DESCRIPTION, contractPayment.get(PAYMENT_CONCEPT.DESCRIPTION))
					.set(PAYMENT_CONCEPT.TYPE, contractPayment.get(PAYMENT_CONCEPT.TYPE))
					.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, contractPayment.get(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE))
					.set(PAYMENT_CONCEPT.EXPRESSION, contractPayment.get(PAYMENT_CONCEPT.EXPRESSION))
					.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, contractPayment.get(PAYMENT_CONCEPT.IRPF_EXPRESSION))
					.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, contractPayment.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION))
					.returning(PAYMENT_CONCEPT.ID)
					.fetchOne(PAYMENT_CONCEPT.ID);
			
			dslContext.update(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
				.where(CONTRACT_PAYMENT.ID.eq(contractPayment.get(CONTRACT_PAYMENT.ID)))
				.execute();
		});
	}

	private static void fixContractPaymentsPaymentConceptWithoutCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		// Contract Payments
		Result<Record> paymentConceptsNoCodeContracts = dslContext.select()
				.from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(
					PAYMENT_CONCEPT.ID.isNotNull()
					.and(PAYMENT_CONCEPT.CODE.isNull())
				).fetch();
		
		List<Record> paymentConceptsNoCode = paymentConceptsNoCodeContracts.stream()
				.filter(contractPayment -> 
					null == contractPayment.get(PAYMENT_CONCEPT.CODE) || 
					checkPaymentConceptCode(contractPayment.get(PAYMENT_CONCEPT.CODE))
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
		
		System.out.println("Agreement Integrity : agreement extra wrong format dates --> " + records.size());
	}
	
	private static void fixAgreementExtraStartEndDates(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
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
		
		List<Record> agreementExtrasStartEndDates = agreementExtrasDatesR.stream().filter(agreementExtrasDate -> !checkStartEndDates(agreementExtrasDate)).collect(Collectors.toList());
		
		if(!agreementExtrasStartEndDates.isEmpty()) {
			Map<Integer, Integer> monthEnds = Map.ofEntries(
			        Map.entry(1, 31),
			        Map.entry(2, 28),
			        Map.entry(3, 31),
			        Map.entry(4, 30),
			        Map.entry(5, 31),
			        Map.entry(6, 30),
			        Map.entry(7, 31),
			        Map.entry(8, 31),
			        Map.entry(9, 30),
			        Map.entry(10, 31),
			        Map.entry(11, 30),
			        Map.entry(12, 31)
			    );
			
			agreementExtrasStartEndDates.forEach(agreementExtra -> {
				String[] startParts = agreementExtra.get(AGREEMENT_EXTRA.START_DATE).split("/");
		        String startDate = "01/" + startParts[1]; // mm o mm -1
		         
		        String[] endMainParts = agreementExtra.get(AGREEMENT_EXTRA.END_DATE).split(" ");
		        String[] endParts = endMainParts[0].split("/");

		        int endMonth = Integer.parseInt(endParts[1]);
		        int endDay = monthEnds.get(endMonth);
				
		        String endDate = String.format("%02d/%02d", endDay, endMonth);
		        if (endMainParts.length > 1 && endMainParts[1].equals("-1")) {
		        	endDate += " -1";
		        }
		        
		        dslContext.update(AGREEMENT_EXTRA)
		        	.set(AGREEMENT_EXTRA.START_DATE, startDate)
		        	.set(AGREEMENT_EXTRA.END_DATE, endDate)
		        	.where(AGREEMENT_EXTRA.ID.eq(agreementExtra.get(AGREEMENT_EXTRA.ID)))
		        	.execute();
			});
			
			System.out.println("Agreement Integrity : agreement extra wrong start/end dates --> " + agreementExtrasStartEndDates.size());
		}
	}
	
	private static void fixAgreementExtraPairs(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
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
		
		List<Record> agreementExtrasStartEndDates = agreementExtrasDatesR.stream().filter(agreementExtrasDate -> !checkStartEndDates(agreementExtrasDate)).collect(Collectors.toList());
		
		if(!agreementExtrasStartEndDates.isEmpty()) {
			Map<Integer, Integer> monthEnds = Map.ofEntries(
			        Map.entry(1, 31),
			        Map.entry(2, 28),
			        Map.entry(3, 31),
			        Map.entry(4, 30),
			        Map.entry(5, 31),
			        Map.entry(6, 30),
			        Map.entry(7, 31),
			        Map.entry(8, 31),
			        Map.entry(9, 30),
			        Map.entry(10, 31),
			        Map.entry(11, 30),
			        Map.entry(12, 31)
			    );
			
			agreementExtrasStartEndDates.forEach(agreementExtra -> {
				String[] startParts = agreementExtra.get(AGREEMENT_EXTRA.START_DATE).split("/");
		        String startDate = "01/" + startParts[1]; // mm o mm -1
		         
		        String[] endMainParts = agreementExtra.get(AGREEMENT_EXTRA.END_DATE).split(" ");
		        String[] endParts = endMainParts[0].split("/");

		        int endMonth = Integer.parseInt(endParts[1]);
		        int endDay = monthEnds.get(endMonth);
				
		        String endDate = String.format("%02d/%02d", endDay, endMonth);
		        if (endMainParts.length > 1 && endMainParts[1].equals("-1")) {
		        	endDate += " -1";
		        }
		        
		        dslContext.update(AGREEMENT_EXTRA)
		        	.set(AGREEMENT_EXTRA.START_DATE, startDate)
		        	.set(AGREEMENT_EXTRA.END_DATE, endDate)
		        	.where(AGREEMENT_EXTRA.ID.eq(agreementExtra.get(AGREEMENT_EXTRA.ID)))
		        	.execute();
			});
			
			System.out.println("Agreement Integrity : agreement extra wrong start/end dates --> " + agreementExtrasStartEndDates.size());
		}
	}
	
	private static void fixDuplicatePaymentConceptCode(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		
		Result<Record> paymentConcepts = dslContext.select().from(PAYMENT_CONCEPT)
			.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
			.and(PAYMENT_CONCEPT.DOMAIN.ne(0))
			.fetch();
		
		// Map<code, List<payment_concept record>>
		Map<String, List<Record>> conceptsByCode = paymentConcepts.stream()
		    .collect(Collectors.groupingBy(record -> record.get(PAYMENT_CONCEPT.CODE)));

		// Los únicos códigos que pueden repetirse
		Set<String> allowedDuplicates = Set.of("SALARIO_BASE", "PAGA_EXTRA");

		for (Map.Entry<String, List<Record>> entry : conceptsByCode.entrySet()) {
		    String code = entry.getKey();
		    List<Record> duplicates = entry.getValue();

		    if (allowedDuplicates.contains(code) || duplicates.size() <= 1) continue;

		    // Renombrar duplicados
		    for (int i = 0; i < duplicates.size(); i++) {
		        Record record = duplicates.get(i);
		        Integer conceptId = record.get(PAYMENT_CONCEPT.ID);
		        String newCode = code + "_" + (i + 1);

		        dslContext.update(PAYMENT_CONCEPT)
		            .set(PAYMENT_CONCEPT.CODE, newCode)
		            .where(PAYMENT_CONCEPT.ID.eq(conceptId))
		            .execute();
		    }
		}
		
	}
	
	private static void fixAgreementDataInherit(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<AgreementDataRecord> agreementDatas = dslContext.selectFrom(AGREEMENT_DATA)
			.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
			.and(AGREEMENT_DATA.EXPRESSION.like("%inherit%"))
			.fetch();
		
		agreementDatas.forEach(agreementData -> {
			dslContext.update(AGREEMENT_DATA)
				.set(AGREEMENT_DATA.EXPRESSION, removeInheritExpr(agreementData.getExpression()))
				.where(AGREEMENT_DATA.ID.eq(agreementData.getId()))
				.execute();
		});
		
		System.out.println("Agreement Integrity : remove inherit from agreement payment --> " + agreementDatas.size());
		
	}

	private static void fixAgreementLevelDataInherit(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementLevelDatas = dslContext.select().from(AGREEMENT_LEVEL_DATA)
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_LEVEL_DATA.EXPRESSION.like("%inherit%"))
				.fetch();
		
		agreementLevelDatas.forEach(agreementLevelData -> {
			dslContext.update(AGREEMENT_LEVEL_DATA)
				.set(AGREEMENT_LEVEL_DATA.EXPRESSION, removeInheritExpr(agreementLevelData.get(AGREEMENT_LEVEL_DATA.EXPRESSION)))
				.where(AGREEMENT_LEVEL_DATA.ID.eq(agreementLevelData.get(AGREEMENT_LEVEL_DATA.ID)))
				.execute();
		});
		
		System.out.println("Agreement Integrity : remove inherit from agreement level data --> " + agreementLevelDatas.size());
	}

	private static void fixAgreementPaymentDataInherit(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<AgreementPaymentRecord> agreementPayments = dslContext.selectFrom(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
			.and(AGREEMENT_PAYMENT.EXPRESSION.like("%inherit%"))
			.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			dslContext.update(AGREEMENT_PAYMENT)
				.set(AGREEMENT_PAYMENT.EXPRESSION, removeInheritExpr(agreementPayment.getExpression()))
				.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.getId()))
				.execute();
		});
		
		System.out.println("Agreement Integrity : remove inherit from agreement payment --> " + agreementPayments.size());
	}
	
	private static void fixContractPaymentDataInherit(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> contractPayments = dslContext.select().from(CONTRACT_PAYMENT)
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.and(CONTRACT_PAYMENT.EXPRESSION.like("%inherit%"))
				.fetch();
		
		contractPayments.forEach(contractPayment -> {
			dslContext.update(CONTRACT_PAYMENT)
				.set(CONTRACT_PAYMENT.EXPRESSION, removeInheritExpr(contractPayment.get(CONTRACT_PAYMENT.EXPRESSION)))
				.where(CONTRACT_PAYMENT.ID.eq(contractPayment.get(CONTRACT_PAYMENT.ID)))
				.execute();
		});
		
		System.out.println("Agreement Integrity : remove inherit from contract payments --> " + contractPayments.size());
	}
	
	private static void fixPaymentConceptDataInherit(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> contractPayments = dslContext.select().from(PAYMENT_CONCEPT)
				.where(PAYMENT_CONCEPT.DOMAIN.eq(agreement.getDomain()))
				.and(PAYMENT_CONCEPT.EXPRESSION.like("%inherit%"))
				.fetch();
		
		contractPayments.forEach(contractPayment -> {
			dslContext.update(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.EXPRESSION, removeInheritExpr(contractPayment.get(PAYMENT_CONCEPT.EXPRESSION)))
				.where(PAYMENT_CONCEPT.ID.eq(contractPayment.get(PAYMENT_CONCEPT.ID)))
				.execute();
		});
		
		System.out.println("Agreement Integrity : remove inherit from contract payments --> " + contractPayments.size());
	}
	
	private static void fixSyncContractPayment(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> contractPayments = dslContext.select().from(CONTRACT_PAYMENT)
				.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.fetch();
		
		contractPayments.forEach(contractPayment -> {
			UpdateSetMoreStep<ContractPaymentRecord> update = dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DOMAIN, contractPayment.get(CONTRACT_PAYMENT.DOMAIN))
					;
			
			if(AonStringUtils.isBlank(contractPayment.get(CONTRACT_PAYMENT.DESCRIPTION)))
				update.set(CONTRACT_PAYMENT.DESCRIPTION, contractPayment.get(PAYMENT_CONCEPT.DESCRIPTION));
			
			if(AonStringUtils.isBlank(contractPayment.get(CONTRACT_PAYMENT.EXPRESSION)))
				update.set(CONTRACT_PAYMENT.EXPRESSION, contractPayment.get(PAYMENT_CONCEPT.EXPRESSION));
			
			update
				.where(CONTRACT_PAYMENT.ID.eq(contractPayment.get(CONTRACT_PAYMENT.ID)))
				.execute();
		});
	
	}
	
	private static void fixSyncAgreementPayment(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select().from(AGREEMENT_PAYMENT)
			.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
			.and(AGREEMENT_PAYMENT.DOMAIN.eq(PAYMENT_CONCEPT.DOMAIN))
			.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			UpdateSetMoreStep<AgreementPaymentRecord> update = dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, agreementPayment.get(AGREEMENT_PAYMENT.DOMAIN))
					;
			
			if(AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION))) {
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DESCRIPTION, agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION))
					.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
					.execute();
				
				update.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION));
			}
			
			if(AonStringUtils.isNotBlank(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION)) && !AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE")) {
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.EXPRESSION, agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION))
					.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
					.execute();
				
				update.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
				
			} else if(AonStringUtils.containsIgnoreCase(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE") && !AonStringUtils.equals(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE();")) {
				dslContext.update(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.EXPRESSION, AonStringUtils.substringAfter(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISABLE();") )
					.where(PAYMENT_CONCEPT.ID.eq(agreementPayment.get(PAYMENT_CONCEPT.ID)))
					.execute();
			
				update.set(AGREEMENT_PAYMENT.EXPRESSION, "DISABLE();");
			}
			
			update.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
			
		});
	
	}
	
	private static void fixContractPaymentExpression(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> contractPayments = dslContext.select().from(CONTRACT_PAYMENT)
				.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.fetch();
		
		contractPayments.forEach(contractPayment -> {
			
			UpdateSetMoreStep<ContractPaymentRecord> update = dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.DOMAIN, contractPayment.get(CONTRACT_PAYMENT.DOMAIN))
					;
			
			if(AonStringUtils.equals(contractPayment.get(CONTRACT_PAYMENT.DESCRIPTION), contractPayment.get(PAYMENT_CONCEPT.DESCRIPTION))) {
				update.set(CONTRACT_PAYMENT.DESCRIPTION, DSL.castNull(CONTRACT_PAYMENT.DESCRIPTION));
			}
			
			if(AonStringUtils.equals(contractPayment.get(CONTRACT_PAYMENT.EXPRESSION), contractPayment.get(PAYMENT_CONCEPT.EXPRESSION))) {
				update.set(CONTRACT_PAYMENT.EXPRESSION, DSL.castNull(CONTRACT_PAYMENT.EXPRESSION));
			}
			
			update.where(CONTRACT_PAYMENT.ID.eq(contractPayment.get(CONTRACT_PAYMENT.ID)))
				.execute();
			
		});
	
	}
	
	private static void fixAgreementPaymentExpression(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> agreementPayments = dslContext.select().from(AGREEMENT_PAYMENT)
			.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
			.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
			.and(AGREEMENT_PAYMENT.DOMAIN.eq(PAYMENT_CONCEPT.DOMAIN))
			.fetch();
		
		agreementPayments.forEach(agreementPayment -> {
			UpdateSetMoreStep<AgreementPaymentRecord> update = dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, agreementPayment.get(AGREEMENT_PAYMENT.DOMAIN))
					;
			
			if(AonStringUtils.equals(agreementPayment.get(AGREEMENT_PAYMENT.DESCRIPTION), agreementPayment.get(PAYMENT_CONCEPT.DESCRIPTION))) {
				update.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION));
			}
			
			if(AonStringUtils.equals(agreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), agreementPayment.get(PAYMENT_CONCEPT.EXPRESSION))) {
				update.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
			}
			
			update.where(AGREEMENT_PAYMENT.ID.eq(agreementPayment.get(AGREEMENT_PAYMENT.ID)))
				.execute();
		});
	
	}
	
	private static void fixContractAgreementPaymentExpression(DSLContext dslContext, Integer domainId, AgreementRecord agreement) {
		Result<Record> contractAgreementPayments = dslContext.select().from(CONTRACT_PAYMENT)
				.join(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
				.join(AGREEMENT_PAYMENT).on(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
				.join(CONTRACT).on(CONTRACT.ID.eq(CONTRACT_PAYMENT.CONTRACT))
				.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL.ID.eq(CONTRACT.AGREEMENT_LEVEL))
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement.getId()))
				.fetch();
		
		contractAgreementPayments.forEach(contractAgreementPayment -> {
			if(AonStringUtils.equalsIgnoreCase(contractAgreementPayment.get(AGREEMENT_PAYMENT.EXPRESSION), "DISBALE();") && 
					( AonStringUtils.containsIgnoreCase(contractAgreementPayment.get(CONTRACT_PAYMENT.EXPRESSION), "DISABLE") ||
					  AonStringUtils.containsIgnoreCase(contractAgreementPayment.get(CONTRACT_PAYMENT.EXPRESSION), "DELETE") )
			) {
				dslContext.delete(CONTRACT_PAYMENT)
					.where(CONTRACT_PAYMENT.ID.eq(contractAgreementPayment.get(CONTRACT_PAYMENT.ID)))
					.execute();
			}
		});
	}

	private static String removeInheritExpr(String expression) {
		if(!AonStringUtils.contains(expression, "inherit")) return expression;
		
		String inheritTag = "/*inherit*/";
        String endTag = "/**/";

        int inheritPos = expression.indexOf(inheritTag);
        if (inheritPos == -1) return null;

        int contentStart = inheritPos + inheritTag.length();

        // Buscar el cierre /**/ correspondiente (ignorando si está dentro de paréntesis)
        int openParens = 0;
        int endPos = -1;
        int i = contentStart;

        while (i < expression.length()) {
            char c = expression.charAt(i);
            if (c == '(') openParens++;
            else if (c == ')') openParens = Math.max(0, openParens - 1);
            else if (expression.startsWith(endTag, i) && openParens == 0) {
                endPos = i;
                break;
            }
            i++;
        }

        if (endPos == -1) return null;

        // Tomar contenido sin el /*inherit*/ ni /**/
        String before = expression.substring(0, inheritPos).trim();
        String middle = expression.substring(contentStart, endPos).trim();
        String after = expression.substring(endPos + endTag.length()).trim();

        // Armar expresión limpia
        String expresion = (before + " " + middle + " " + after).replaceAll("\\s+", " ").trim();
        
        return expresion;
		
//		Pattern pattern = Pattern.compile("/\\*inherit\\*/(.*?)/\\*\\*/");
//        Matcher matcher = pattern.matcher(exrpression);
//
//        if (matcher.find()) {
//            return matcher.group(1).trim();
//        }
//
//        return exrpression;
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
    
    // ----------- Allowed conceptCodes contextVars
    
    public static boolean isContextVariable(String name) {
		return !allowedVars.contains(name) && ContextVariable.isContextVariable(name);
	}
    
    private static Set<String> getAllowedConceptContextVars(){
    	Set<String> allowedVars = new HashSet<String>();
    	
    	allowedVars.add("ADVERTENCIA");
    	allowedVars.add("ANTIGUEDAD");
    	allowedVars.add("A_CUENTA_CONVENIO");
    	allowedVars.add("DEVENGO_TEMPORAL");
    	allowedVars.add("GARANTIZADO");
    	allowedVars.add("GEROA");
    	allowedVars.add("HORAS_COMPL");
    	allowedVars.add("HORAS_EXTRAS");
    	allowedVars.add("INFO");
    	allowedVars.add("MEJORA");
    	allowedVars.add("NOTA");
    	allowedVars.add("PAGA_BENEFICIOS");
    	allowedVars.add("PAGA_EXTRA");
    	allowedVars.add("PLUS_EXTRA_SALARIAL");
    	allowedVars.add("PLUS_SALARIAL");
    	allowedVars.add("PLUS_XS");
    	allowedVars.add("PPE");
    	allowedVars.add("SALARIO_BASE");
    	allowedVars.add("SEGURO_AT");
    	
    	return allowedVars;
    }
    
 // ----------- fixAgreementIntegrity

	public static void fixAgreementIntegrity(Connection connection, Integer domainId, Integer agreementId) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		allowedVars = getAllowedConceptContextVars();
		
		AgreementRecord agreement = dslContext.selectFrom(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne();
		
		dslContext.transaction(configuration -> {
			
			AgreementDataRecord aonAutoUpdate = dslContext.selectFrom(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_DATA.NAME.eq("AON_AUTO_UPDATE"))
				.fetchOne();
			
			if(null == aonAutoUpdate) {
				
				/* [00] */ fixAgreementDataInherit(dslContext, domainId, agreement);
				/* [00] */ fixAgreementLevelDataInherit(dslContext, domainId, agreement);
				/* [00] */ fixAgreementPaymentDataInherit(dslContext, domainId, agreement);
				/* [00] */ fixContractPaymentDataInherit(dslContext, domainId, agreement);
				/* [00] */ fixPaymentConceptDataInherit(dslContext, domainId, agreement);
				
				/* [01] */ fixOtherDomainAgreementPayments(dslContext, domainId, agreement);
				/* [02] */ fixAgreementPaymentsWithoutPaymentConcept(dslContext, domainId, agreement);
				/* [03] */ fixAgreementPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				
				/* [04] */ fixContractPaymentsWithOtherDomainPaymentConcept(dslContext, domainId, agreement);
				
				/* [05] */ fixNoCodeWrongCodePaymentConcept(dslContext, domainId, agreement);
				
				/* [06] */ fixSyncContractPayment(dslContext, domainId, agreement);
				/* [07] */ fixSyncAgreementPayment(dslContext, domainId, agreement);
				
				/* [08] */ fixContractPaymentExpression(dslContext, domainId, agreement);
				/* [08] */ fixAgreementPaymentExpression(dslContext, domainId, agreement);
				
				/* [09] */ fixContractAgreementPaymentExpression(dslContext, domainId, agreement);
				
				/* [10] */ fixAgreementExtraWrongFormatPeriod(dslContext, domainId, agreement);
				/* [10] */ fixAgreementExtraStartEndDates(dslContext, domainId, agreement);
//				/* [10] */ fixAgreementExtraPairs(dslContext, domainId, agreement);
				
				/* [11] */ fixDuplicatePaymentConceptCode(dslContext, domainId, agreement);
				
				/* [14] */ fixPaymentConceptNoReferences(dslContext, domainId, agreement);
				
//				JooqAgreementIntegrityCalculator.checkSalaries(connection, domainId, agreementId);
				
//				throw new IllegalArgumentException("Prueba de error");
				
				dslContext.insertInto(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.DOMAIN, agreement.getDomain())
					.set(AGREEMENT_DATA.NAME, "AON_AUTO_UPDATE")
					.set(AGREEMENT_DATA.AGREEMENT, agreement.getId())
					.set(AGREEMENT_DATA.EXPRESSION, formatter.format(new java.util.Date()))
					.set(AGREEMENT_DATA.START_DATE, new Date(new java.util.Date().getTime()))
					.set(AGREEMENT_DATA.END_DATE, new Date(new java.util.Date().getTime()))
					.execute();
			}
			
		});
	}
    
}
