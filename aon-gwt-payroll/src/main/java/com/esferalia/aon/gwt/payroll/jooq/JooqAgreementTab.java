package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementExtra;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.AgreementOwner;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.Level;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo.LevelData;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.jooq.tables.records.AgreementDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelDataRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqAgreementTab {
	
	private JooqAgreementTab() {
		super();
	}

	private static Settings settings = null;
	private static Map<java.util.Date, Set<String>> variables = new TreeMap<>();
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	// ------------------------------- getAgreementInfo

	public static AgreementInfo getAgreementInfo(Connection conn, Integer agreementId, boolean withContracts, Integer domainId, Integer parentDomainId) throws Exception {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		AgreementInfo agreement = new AgreementInfo();
		variables.clear();
		
		getAgreement(dslContext, agreement, agreementId);
		getAgreementLevel(dslContext, agreement, agreementId);
		getAgreementHasContracts(dslContext, agreement);
		getAgreementLevelCategory(dslContext, agreement);
		if(withContracts) getAgreementLevelContract(dslContext, agreement, domainId);
		getAgreementData(dslContext, agreement);
		getAgreementLevelData(dslContext, agreement);
		getAgreementPayments(dslContext, agreement);
		getAgreementExtras(dslContext, agreement);
		getAgreementDates(dslContext, agreement);
		// Set variables
		agreement.setVariables(variables);
		
		Set<String> allVariables = EmployeesServiceHelper.getVariables(conn, agreement, agreement.getDomain(), parentDomainId);
		if(allVariables.contains("DIAS_TRABAJADOS")) allVariables.add("DIAS_NO_TRABAJADOS");
		if(!allVariables.contains("HORAS_CONVENIO")) allVariables.add("HORAS_CONVENIO");
		
		// Add AÑOS_ANTIGUEDAD if needed
		Optional<Payment> antiguedadPayment = agreement.getPayments().stream().filter(payment -> AonStringUtils.containsIgnoreCase(payment.getDescription(), "ANTIGUEDAD") || AonStringUtils.containsIgnoreCase(payment.getDescription(), "ANTIG\u00dcEDAD")).findAny();
		if(antiguedadPayment.isPresent()) allVariables.add("A\u00d1OS_ANTIGUEDAD");
		
		agreement.setAllVariables(allVariables);
		
		// No concept
		Set<String> noConceptVariables = new HashSet<String>();
		Set<String> agreementDataVariables = agreement.getLevelDatasMap().get(0).stream().map(levelData -> levelData.getName()).collect(Collectors.toSet());
		for(String agreementDataVariable : agreementDataVariables) {
			if(!agreement.getAllVariables().contains(agreementDataVariable)) noConceptVariables.add(agreementDataVariable);
		}
		agreement.setNoConceptVariables(noConceptVariables);
		
		List<String> contextVariables = Arrays.asList(ContextVariable.values()).stream().map(cv -> cv.getName()).collect(Collectors.toList());
		contextVariables.addAll( ContextVariable.getNames() );
		// Remove allowed ones
		contextVariables.removeAll(getAllowedConceptContextVars());
		agreement.setContextVariables(contextVariables);
		
		return agreement;
	}

	private static void getAgreement(DSLContext dslContext, AgreementInfo agreement, Integer agreementId) {
		AgreementRecord agreementRecord = dslContext.selectFrom(AGREEMENT)
				.where(AGREEMENT.ID.eq(agreementId))
				.fetchOne();
		
		agreement.setId(agreementRecord.getId());
		agreement.setDomain(agreementRecord.getDomain());
		agreement.setDescription(agreementRecord.getDescription());
		agreement.setSSNumber(agreementRecord.getSsNumber());
		agreement.setOwner(agreementRecord.getOwner() == (byte)0 ? AgreementOwner.AONSOLUTIONS : AgreementOwner.SERVICONVENIOS);
		
	}

	private static void getAgreementLevel(DSLContext dslContext, AgreementInfo agreement, Integer agreementId) {
		Result<AgreementLevelRecord> agreementLevelRecords = dslContext.selectFrom(AGREEMENT_LEVEL)
				.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreementId))
				.orderBy(AGREEMENT_LEVEL.DESCRIPTION)
				.fetch();
		
		Set<Level> levels = new HashSet<>();
		
		// Create Level 0 (Agreement Data)
		Level levelZero = new Level();
		levelZero.setId(0);
		levelZero.setDomain(agreement.getDomain());
		levelZero.setDescription("");
		levels.add(levelZero);
		
		for(AgreementLevelRecord agreementLevelRecord : agreementLevelRecords) {
			Level level = new Level();
			level.setId(agreementLevelRecord.getId());
			level.setDomain(agreementLevelRecord.getDomain());
			level.setDescription(agreementLevelRecord.getDescription());
			
			levels.add(level);
		}
		
		agreement.setLevels(sortLevelSet(levels));
	}
	
	private static Set<Level> sortLevelSet(Set<Level> levelsSetIn) {
		Set<Level> levelsSet = new LinkedHashSet<>();
		
		ArrayList<Level> levelArray = new ArrayList<>(levelsSetIn);
		levelArray.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
		levelsSet.addAll(levelArray);
		
		return levelsSet;
	}

	private static void getAgreementHasContracts(DSLContext dslContext, AgreementInfo agreement) {
		List<Integer> agreementLevels = agreement.getLevels().stream().map(level -> level.getId()).collect(Collectors.toList());
		Record1<Integer> contractCount = dslContext.selectCount().from(CONTRACT).where(CONTRACT.AGREEMENT_LEVEL.in(agreementLevels)).fetchOne();
		agreement.setHasContract(contractCount != null);
	}

	private static void getAgreementLevelCategory(DSLContext dslContext, AgreementInfo agreement) {
		Map<Integer, Set<String>> categories = new TreeMap<>();
		
		Set<String> levelZeroCat = new HashSet<>();
		levelZeroCat.add("Por defecto");
		categories.put(0, levelZeroCat);
		
		for(Level level : agreement.getLevels()) {
			List<String> categoryList = dslContext.select(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION).from(AGREEMENT_LEVEL_CATEGORY)
					.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(level.getId()))
					.fetch(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION);
			
			categories.put(level.getId(),  new HashSet<>(categoryList));
		}
		
		agreement.setCategoriesMap(categories);
	}
	
	private static void getAgreementLevelContract(DSLContext dslContext, AgreementInfo agreement, Integer domainId) {
		Map<Integer, Set<String>> contracts = new TreeMap<>();
		
		for(Level level : agreement.getLevels()) {
			
			Result<Record> levelContracts = dslContext.select().from(PERSON)
					.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
					.where(CONTRACT.AGREEMENT_LEVEL.eq(level.getId()))
					.and(CONTRACT.DOMAIN.eq(domainId))
					.fetch();
			
			Set<String> contractList = new HashSet<>();
			
			for(Record levelContract : levelContracts) {
				String name = levelContract.get(PERSON.NAME);
				String firstSurname = levelContract.get(PERSON.FIRST_SURNAME);
				String secondSurname = levelContract.get(PERSON.SECOND_SURNAME);
				
				String fullName = name;
				fullName = AonStringUtils.isBlank(fullName) ? firstSurname : fullName + " " + firstSurname;
				fullName = fullName.trim();
				
				fullName = AonStringUtils.isBlank(fullName) ? secondSurname : fullName + " " + secondSurname;
				fullName = fullName.trim();
				
				contractList.add(fullName);
			}
			
			contracts.put(level.getId(),  contractList);
		}
		
		agreement.setContractsMap(contracts);
	}
	
	private static void getAgreementData(DSLContext dslContext, AgreementInfo agreement) {
		Result<AgreementDataRecord> agreementDataRecords = dslContext.selectFrom(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.fetch();
		
		Map<Integer, Set<LevelData>> levelDatas = new TreeMap<>();
		
		if(agreementDataRecords.isEmpty())
			levelDatas.put(0, new HashSet<>());
		else
			for(AgreementDataRecord agreementDataRecord : agreementDataRecords) {
				// Add variable to set
				String variable = agreementDataRecord.getName();
				java.util.Date startDate = parseToJavaDate(agreementDataRecord.getStartDate());
				
				Set<String> variableSet = variables.getOrDefault(startDate, new HashSet<String>());
				variableSet.add(variable);
				variables.put(startDate, variableSet);
				
				Set<LevelData> levelDataSet = levelDatas.getOrDefault(0, new HashSet<LevelData>());
				
				LevelData levelData = new LevelData();
				levelData.setId(agreementDataRecord.getId());
				levelData.setDomain(agreementDataRecord.getDomain());
				levelData.setName(agreementDataRecord.getName());
				levelData.setExpression(agreementDataRecord.getExpression());
				levelData.setStartDate(startDate);
				levelData.setEndDate(parseToJavaDate(agreementDataRecord.getEndDate()));
				
				levelDataSet.add(levelData);
				levelDatas.put(0, levelDataSet);
				
				// Add date
				agreement.getDates().add(parseToJavaDate(agreementDataRecord.getStartDate()));
				
			}
		
		agreement.setLevelDatasMap(levelDatas);
	}
	
	private static void getAgreementLevelData(DSLContext dslContext, AgreementInfo agreement) {
		Map<Integer, Set<LevelData>> levelDatas = agreement.getLevelDatasMap();
		List<Integer> agreementLevels = agreement.getLevels().stream().map(level -> level.getId()).collect(Collectors.toList());
		
		Result<AgreementLevelDataRecord> levelDataRecords = dslContext.selectFrom(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(agreementLevels))
				.fetch();
		
		for(AgreementLevelDataRecord levelDataRecord : levelDataRecords) {
			// Add variable to set
			String variable = levelDataRecord.getName();			
			java.util.Date startDate = parseToJavaDate(levelDataRecord.getStartDate());
			
			Set<String> variableSet = variables.getOrDefault(startDate, new HashSet<>());
			variableSet.add(variable);
			variables.put(startDate, variableSet);
			
			Set<LevelData> levelDataSet = levelDatas.getOrDefault(levelDataRecord.getAgreementLevel(), new HashSet<>());
			
			LevelData levelData = new LevelData();
			levelData.setId(levelDataRecord.getId());
			levelData.setDomain(levelDataRecord.getDomain());
			levelData.setName(levelDataRecord.getName());
			levelData.setExpression(levelDataRecord.getExpression());
			levelData.setStartDate(parseToJavaDate(levelDataRecord.getStartDate()));
			levelData.setEndDate(parseToJavaDate(levelDataRecord.getEndDate()));
			
			levelDataSet.add(levelData);
			levelDatas.put(levelDataRecord.getAgreementLevel(), levelDataSet);
			
			// Add date
			agreement.getDates().add(parseToJavaDate(levelDataRecord.getStartDate()));
		}
		
		agreement.setLevelDatasMap(levelDatas);
	}
	
	private static void getAgreementPayments(DSLContext dslContext, AgreementInfo agreement) {
		Result<Record> agreementPaymentRecords = dslContext.select().from(AGREEMENT_PAYMENT)
				.leftOuterJoin(PAYMENT_CONCEPT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.orderBy(AGREEMENT_PAYMENT.DESCRIPTION)
				.fetch();
		
		Set<Payment> paymentsSet = new LinkedHashSet<>();
		
		for(Record agreementPaymentRecord : agreementPaymentRecords) {
			Payment payment = new Payment();
			payment.setId(agreementPaymentRecord.get(AGREEMENT_PAYMENT.ID));
			payment.setDomain(agreementPaymentRecord.get(AGREEMENT_PAYMENT.DOMAIN));
			payment.setConceptId(agreementPaymentRecord.get(AGREEMENT_PAYMENT.PAYMENT_CONCEPT));
			payment.setName(agreementPaymentRecord.get(PAYMENT_CONCEPT.CODE));
			payment.setType(null != agreementPaymentRecord.get(AGREEMENT_PAYMENT.TYPE) ? getPaymentType(agreementPaymentRecord.get(AGREEMENT_PAYMENT.TYPE)) : getPaymentType(agreementPaymentRecord.get(PAYMENT_CONCEPT.TYPE)));
			payment.setDescription(AonStringUtils.isNotBlank(agreementPaymentRecord.get(AGREEMENT_PAYMENT.DESCRIPTION)) ? agreementPaymentRecord.get(AGREEMENT_PAYMENT.DESCRIPTION) : agreementPaymentRecord.get(PAYMENT_CONCEPT.DESCRIPTION));
			payment.setExpression(
					AonStringUtils.isNotBlank(agreementPaymentRecord.get(AGREEMENT_PAYMENT.EXPRESSION)) 
						?  agreementPaymentRecord.get(AGREEMENT_PAYMENT.EXPRESSION) : agreementPaymentRecord.get(PAYMENT_CONCEPT.EXPRESSION));
			payment.setStartDate(parseToJavaDate(agreementPaymentRecord.get(AGREEMENT_PAYMENT.START_DATE)));
			payment.setEndDate(parseToJavaDate(agreementPaymentRecord.get(AGREEMENT_PAYMENT.END_DATE)));
			payment.setMonth(null == agreementPaymentRecord.get(AGREEMENT_PAYMENT.MONTH) ? null : (short)agreementPaymentRecord.get(AGREEMENT_PAYMENT.MONTH));
			payment.setSalaryType(getSalaryType(agreementPaymentRecord.get(AGREEMENT_PAYMENT.SALARY_TYPE)));
			
			payment.setIrpfExpression(AonStringUtils.isNotBlank(agreementPaymentRecord.get(AGREEMENT_PAYMENT.IRPF_EXPRESSION)) ? agreementPaymentRecord.get(AGREEMENT_PAYMENT.IRPF_EXPRESSION) : agreementPaymentRecord.get(PAYMENT_CONCEPT.IRPF_EXPRESSION));
			payment.setQuoteExpression(AonStringUtils.isNotBlank(agreementPaymentRecord.get(AGREEMENT_PAYMENT.QUOTE_EXPRESSION)) ? agreementPaymentRecord.get(AGREEMENT_PAYMENT.QUOTE_EXPRESSION) : agreementPaymentRecord.get(PAYMENT_CONCEPT.QUOTE_EXPRESSION));
			
			if(AonStringUtils.equalsIgnoreCase(payment.getExpression(), "DISABLE();"))
				payment.setHiddenExpression(agreementPaymentRecord.get(PAYMENT_CONCEPT.EXPRESSION));
			
			paymentsSet.add(payment);
		}

		agreement.setPayments(sortPaymentSet(paymentsSet));
	}
	
	private static Set<Payment> sortPaymentSet(Set<Payment> paymentsSetIn) {
		Set<Payment> paymentsSet = new LinkedHashSet<>();
		
		ArrayList<Payment> paymentArray = new ArrayList<>(paymentsSetIn);
		paymentArray.sort((o1, o2) -> {
			if(AonStringUtils.isBlank(o1.getDescription())) return -1;
			if(AonStringUtils.isBlank(o2.getDescription())) return 1;
			return o1.getDescription().compareTo(o2.getDescription());
		});
		
		paymentsSet.addAll(paymentArray);
		
		return paymentsSet;
	}

	private static void getAgreementExtras(DSLContext dslContext, AgreementInfo agreement) {
		Result<AgreementExtraRecord> agreementExtraRecords = dslContext.selectFrom(AGREEMENT_EXTRA)
				.where(AGREEMENT_EXTRA.AGREEMENT.eq(agreement.getId()))
				.fetch();
		
		Set<AgreementExtra> agreementExtraSet = new HashSet<>();
		
		for(AgreementExtraRecord agreementExtraRecord : agreementExtraRecords) {
			AgreementExtra extra = new AgreementExtra();
			extra.setId(agreementExtraRecord.getId());
			extra.setDomain(agreementExtraRecord.getDomain());
			extra.setAgreementPayment(agreementExtraRecord.getAgreementPayment());
			extra.setStartDate(agreementExtraRecord.getStartDate());
			extra.setEndDate(agreementExtraRecord.getEndDate());
			extra.setIssueDate(agreementExtraRecord.getIssueDate());
			
			agreementExtraSet.add(extra);
		}
		
		agreement.setExtras(agreementExtraSet);
	}
	
	private static void getAgreementDates(DSLContext dslContext, AgreementInfo agreement) {
		List<Date> agreementDataDates = dslContext.selectDistinct(AGREEMENT_DATA.START_DATE).from(AGREEMENT_DATA)
				.where(AGREEMENT_DATA.AGREEMENT.eq(agreement.getId()))
				.and(AGREEMENT_DATA.NAME.ne("AON_AUTO_UPDATE"))
				.fetch(AGREEMENT_DATA.START_DATE);
		
		List<Integer> agreementLevels = agreement.getLevels().stream().map(level -> level.getId()).collect(Collectors.toList());
		
		List<Date> agreementLevelDataDates = dslContext.selectDistinct(AGREEMENT_LEVEL_DATA.START_DATE).from(AGREEMENT_LEVEL_DATA)
				.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.in(agreementLevels))
				.fetch(AGREEMENT_LEVEL_DATA.START_DATE);
		
		Set<java.util.Date> dateSet = new HashSet<>();
		
		for(Date agreementDataDate : agreementDataDates)
			dateSet.add(parseToJavaDate(agreementDataDate));
		
		for(Date agreementLevelDataDate : agreementLevelDataDates)
			dateSet.add(parseToJavaDate(agreementLevelDataDate));
		
		agreement.setDates(dateSet);
	}

	// ------------------------------- setAgreementInfo
	
	public static void setAgreementInfo(Connection conn, AgreementInfo agreementInfo) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		dslContext.transaction(t -> {
			setAgreement(dslContext, agreementInfo);
			setAgreementLevel(dslContext, agreementInfo);
			setAgreementLevelData(dslContext, agreementInfo);
			setAgreementData(dslContext, agreementInfo);
			setAgreementLevelCategory(dslContext, agreementInfo);
			setAgreementExtras(dslContext, agreementInfo);
			setAgreementPayments(dslContext, agreementInfo);
		});
	}
	
	private static void setAgreement(DSLContext dslContext, AgreementInfo agreementInfo) {
		dslContext.update(AGREEMENT)
			.set(AGREEMENT.DESCRIPTION, agreementInfo.getDescription())
			.set(AGREEMENT.SS_NUMBER, agreementInfo.getSSNumber())
			.set(AGREEMENT.OWNER, agreementInfo.getOwner() == AgreementOwner.AONSOLUTIONS ? (byte)0 : (byte)1)
			.where(AGREEMENT.ID.eq(agreementInfo.getId()))
			.execute();
	}
	
	private static void setAgreementLevelData(DSLContext dslContext, AgreementInfo agreementInfo) {
		agreementInfo.getLevelDatasMap().entrySet().forEach(entry -> {
			Integer levelId = entry.getKey();
			Level level = agreementInfo.getLevelById(levelId);
			Set<LevelData> levelDatas = entry.getValue();
			
			if(levelId == 0) return;
			
			levelDatas.forEach(levelData -> {
				if(levelData.isDeleted() || level.isDeleted())
					dslContext.delete(AGREEMENT_LEVEL_DATA)
						.where(AGREEMENT_LEVEL_DATA.ID.eq(levelData.getId()))
						.execute();
				else if(null == levelData.getId() || levelData.getId() < 0)
					dslContext.insertInto(AGREEMENT_LEVEL_DATA)
						.set(AGREEMENT_LEVEL_DATA.DOMAIN, agreementInfo.getDomain())
						.set(AGREEMENT_LEVEL_DATA.NAME, levelData.getName())
						.set(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL, levelId)
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION, levelData.getExpression())
						.set(AGREEMENT_LEVEL_DATA.START_DATE, parseToSqlDate(levelData.getStartDate()))
						.set(AGREEMENT_LEVEL_DATA.END_DATE, parseToSqlDate(levelData.getEndDate()))
						.execute();
				else
					dslContext.update(AGREEMENT_LEVEL_DATA)
						.set(AGREEMENT_LEVEL_DATA.EXPRESSION, levelData.getExpression())
						.set(AGREEMENT_LEVEL_DATA.START_DATE, parseToSqlDate(levelData.getStartDate()))
						.set(AGREEMENT_LEVEL_DATA.END_DATE, parseToSqlDate(levelData.getEndDate()))
						.where(AGREEMENT_LEVEL_DATA.ID.eq(levelData.getId()))
						.execute();
			});
		});
	}
	
	private static void setAgreementData(DSLContext dslContext, AgreementInfo agreementInfo) {
		Set<LevelData> levelDatas = agreementInfo.getLevelDatasMap().getOrDefault(0, new HashSet<>());
		
		levelDatas.forEach(levelData -> {
			if(levelData.isDeleted())
				dslContext.delete(AGREEMENT_DATA)
					.where(AGREEMENT_DATA.ID.eq(levelData.getId()))
					.execute();
			else if(null == levelData.getId() || levelData.getId() < 0)
				dslContext.insertInto(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_DATA.NAME, levelData.getName())
					.set(AGREEMENT_DATA.AGREEMENT, agreementInfo.getId())
					.set(AGREEMENT_DATA.EXPRESSION, levelData.getExpression())
					.set(AGREEMENT_DATA.START_DATE, parseToSqlDate(levelData.getStartDate()))
					.set(AGREEMENT_DATA.END_DATE, parseToSqlDate(levelData.getEndDate()))
					.execute();
			else
				dslContext.update(AGREEMENT_DATA)
					.set(AGREEMENT_DATA.EXPRESSION, levelData.getExpression())
					.set(AGREEMENT_DATA.START_DATE, parseToSqlDate(levelData.getStartDate()))
					.set(AGREEMENT_DATA.END_DATE, parseToSqlDate(levelData.getEndDate()))
					.where(AGREEMENT_DATA.ID.eq(levelData.getId()))
					.execute();
		});
	}
	
	private static void setAgreementLevelCategory(DSLContext dslContext, AgreementInfo agreementInfo) {
		agreementInfo.getLevels().forEach(level ->
			dslContext.delete(AGREEMENT_LEVEL_CATEGORY)
				.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(level.getId()))
				.execute()
		);
		
		agreementInfo.getCategoriesMap().entrySet().forEach(entry -> {
			Integer levelId = entry.getKey();
			Set<String> categories = entry.getValue();
			
			Level level = agreementInfo.getLevelById(levelId);
			if(level.isDeleted()) return;
			
			categories.forEach(category ->
				dslContext.insertInto(AGREEMENT_LEVEL_CATEGORY)
					.set(AGREEMENT_LEVEL_CATEGORY.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL, levelId)
					.set(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION, category)
					.execute()
			);
		});
	}
	
	private static void setAgreementLevel(DSLContext dslContext, AgreementInfo agreementInfo) {
		agreementInfo.getLevels().forEach(level -> { 
			if(level.isDeleted()) {
				dslContext.delete(AGREEMENT_LEVEL_CATEGORY)
					.where(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(level.getId()))
					.execute();
				
				dslContext.delete(AGREEMENT_LEVEL_DATA)
					.where(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(level.getId()))
					.execute();
				
				dslContext.delete(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.ID.eq(level.getId()))
					.execute();
			} else if (null == level.getId() || level.getId() < 0) {
				Integer newLevelId = dslContext.insertInto(AGREEMENT_LEVEL)
					.set(AGREEMENT_LEVEL.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_LEVEL.AGREEMENT, agreementInfo.getId())
					.set(AGREEMENT_LEVEL.DESCRIPTION, level.getDescription())
					.returning(AGREEMENT_LEVEL.ID)
					.fetchOne(AGREEMENT_LEVEL.ID);
				
				Integer oldId = level.getId();
				level.setId(newLevelId);
				
				agreementInfo.getCategoriesMap().put(newLevelId, agreementInfo.getCategoriesMap().get(oldId));
				agreementInfo.getCategoriesMap().remove(oldId);
				agreementInfo.getLevelDatasMap().put(newLevelId, agreementInfo.getLevelDatasMap().get(oldId));
				agreementInfo.getLevelDatasMap().remove(oldId);
				
			} else
				dslContext.update(AGREEMENT_LEVEL)
					.set(AGREEMENT_LEVEL.DESCRIPTION, level.getDescription())
					.where(AGREEMENT_LEVEL.ID.eq(level.getId()))
					.execute();
		});
	}
	
	private static void setAgreementPayments(DSLContext dslContext, AgreementInfo agreementInfo) {
		agreementInfo.getPayments().forEach(payment -> {
			//Delete
			if(payment.isDeleted()) {
				
				// Check if need to delete paymentConcept
				if(null != payment.getConceptId()) {
					Result<Record1<Integer>> agreementPaymentsWithPaymentConcept = dslContext.selectDistinct(AGREEMENT_PAYMENT.ID)
						.from(AGREEMENT_PAYMENT)
						.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
						.fetch();
					
					Result<Record1<Integer>> contractPaymentsWithPaymentConcept = dslContext.selectDistinct(CONTRACT_PAYMENT.ID)
							.from(CONTRACT_PAYMENT)
							.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
							.fetch();
					
					Result<Record1<Integer>> systemPaymentsWithPaymentConcept = dslContext.selectDistinct(SYSTEM_PAYMENT.ID)
							.from(SYSTEM_PAYMENT)
							.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
							.fetch();
					
					if(
							contractPaymentsWithPaymentConcept.isEmpty() && 
							systemPaymentsWithPaymentConcept.isEmpty() && 
							agreementPaymentsWithPaymentConcept.isNotEmpty() && 
							agreementPaymentsWithPaymentConcept.size() == 1 &&
							agreementPaymentsWithPaymentConcept.get(0).get(AGREEMENT_PAYMENT.ID) == payment.getId()
					) {
						dslContext.delete(PAYMENT_CONCEPT)
						.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
						.and(PAYMENT_CONCEPT.DOMAIN.ne(0))
						.and(PAYMENT_CONCEPT.DOMAIN.ne(agreementInfo.getDomain()))
						.execute();
					}
				}
				
				// Delete agreementPayment
				dslContext.delete(AGREEMENT_PAYMENT)
					.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
					.execute();
			
			// Create
			} else if(null == payment.getId() || payment.getId() < 0) {
				
				// Create PaymentConcept
				InsertSetMoreStep<PaymentConceptRecord> insertPaymentConcept = dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, agreementInfo.getDomain())
						.set(PAYMENT_CONCEPT.CODE, payment.getName())
						.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
						.set(PAYMENT_CONCEPT.TYPE, null == payment.getType() ? (byte) 1 : (byte) payment.getType().ordinal())
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
						;
				
				if(AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "DISABLE"))
					insertPaymentConcept.set(PAYMENT_CONCEPT.EXPRESSION, AonStringUtils.substringAfter(payment.getExpression(), "DISABLE();"));
				else
					insertPaymentConcept.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression());
				
				PaymentConceptRecord paymentConceptRecord = insertPaymentConcept.returning(PAYMENT_CONCEPT.ID)
						.fetchOne();
				
				payment.setConceptId(paymentConceptRecord.getId());
				
				// Create AgreementPayment
				InsertSetMoreStep<AgreementPaymentRecord> insertAgreementPayment = dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreementInfo.getId())
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
					.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
					.set(AGREEMENT_PAYMENT.MONTH, AonNumberUtils.toByte(payment.getMonth()))
					.set(AGREEMENT_PAYMENT.SALARY_TYPE,AonEnumUtils.getByte(payment.getSalaryType()))
					;
				
				// If disable set agreementPaymente expression else null
				if(AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "DISABLE"))
					insertAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, "DISABLE();");
				else
					insertAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
				
				Integer newPaymentId = insertAgreementPayment
						.returning(AGREEMENT_PAYMENT.ID)
						.fetchOne(AGREEMENT_PAYMENT.ID);
				
				if(payment.hasExtra())
					agreementInfo.updateExtraPaymentId(payment.getId(), newPaymentId);
				
			// Update
			} else {
				
				// Se crea un paymentConcept
				if(null == payment.getConceptId()) {
					payment.setConceptId(createPaymentConcept(dslContext, agreementInfo.getDomain(), payment));
				
				} else {
					
					// Get paymentConcept
					PaymentConceptRecord paymentConceptRecord = dslContext.selectFrom(PAYMENT_CONCEPT)
							.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
							.fetchOne();
					
					// Se crear un paymentConcept en el dominio actual si es del dominio 0
					if(null != paymentConceptRecord && (paymentConceptRecord.getDomain() == 0 || !paymentConceptRecord.getDomain().equals(agreementInfo.getDomain()))) {
						payment.setConceptId(createPaymentConcept(dslContext, agreementInfo.getDomain(), payment));
					
					// Se actualizar el paymentConcept si no es del dominio 0	
					} else {
						
						Result<Record> agreementPaymentsAssociatedPaymentConcept = dslContext.select().from(AGREEMENT_PAYMENT)
								.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
								.and(AGREEMENT_PAYMENT.DOMAIN.eq(agreementInfo.getDomain()))
								.fetch();
						
						if(agreementPaymentsAssociatedPaymentConcept.size() <= 1) {
							UpdateSetMoreStep<PaymentConceptRecord> update = dslContext.update(PAYMENT_CONCEPT)
									.set(PAYMENT_CONCEPT.CODE, payment.getName())
									.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
									.set(PAYMENT_CONCEPT.TYPE, null == payment.getType() ? (byte) 1 : (byte) payment.getType().ordinal())
									.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
									.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
									.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression());
							
							if(!(AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "DISABLE")))
								update.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression());
							
							update.where(PAYMENT_CONCEPT.ID.eq( payment.getConceptId() ))
								.and(PAYMENT_CONCEPT.DOMAIN.eq( payment.getDomain() ).and(PAYMENT_CONCEPT.DOMAIN.ne(0)) )
								.execute();
							
							dslContext.update(AGREEMENT_PAYMENT)
								.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION))
								.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
								.execute();
						}
					}
							
				}
				
				Result<Record> agreementPaymentsAssociatedPaymentConcept = dslContext.select().from(AGREEMENT_PAYMENT)
						.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(payment.getConceptId()))
						.and(AGREEMENT_PAYMENT.DOMAIN.eq(agreementInfo.getDomain()))
						.fetch();
				
				UpdateSetMoreStep<AgreementPaymentRecord> updateAgreementPayment = dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
					.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
					.set(AGREEMENT_PAYMENT.MONTH, AonNumberUtils.toByte(payment.getMonth()))
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, AonEnumUtils.getByte(payment.getSalaryType()))
					.set(AGREEMENT_PAYMENT.TYPE, DSL.castNull(AGREEMENT_PAYMENT.TYPE))
//					.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.castNull(AGREEMENT_PAYMENT.DESCRIPTION))
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.IRPF_EXPRESSION))
					;
				
				if(AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "DISABLE"))
					updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, "DISABLE();");
				else if(agreementPaymentsAssociatedPaymentConcept.size() <= 1)
					updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(AGREEMENT_PAYMENT.EXPRESSION));
				else
					updateAgreementPayment.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression());
				
				updateAgreementPayment
					.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
					.execute();
			}
		});
	}
	
	private static Integer createPaymentConcept(DSLContext dslContext, Integer domain, Payment payment) {
		// Create PaymentConcept
		InsertSetMoreStep<PaymentConceptRecord> insertPaymentConcept = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, domain)
				.set(PAYMENT_CONCEPT.CODE, payment.getName())
				.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
				.set(PAYMENT_CONCEPT.TYPE, null == payment.getType() ? (byte) 1 : (byte) payment.getType().ordinal())
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
				;
		
		if(AonStringUtils.isNotBlank(payment.getExpression()) && AonStringUtils.containsIgnoreCase(payment.getExpression(), "DISABLE"))
			insertPaymentConcept.set(PAYMENT_CONCEPT.EXPRESSION, AonStringUtils.substringAfter(payment.getExpression(), "DISABLE();"));
		else
			insertPaymentConcept.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression());
		
		PaymentConceptRecord paymentConceptRecord = insertPaymentConcept.returning(PAYMENT_CONCEPT.ID)
				.fetchOne();
		
		 return paymentConceptRecord.getId();
	}

	private static void setAgreementExtras(DSLContext dslContext, AgreementInfo agreementInfo) {
		agreementInfo.getExtras().forEach(extra -> {
			if(extra.isDeleted())
				dslContext.delete(AGREEMENT_EXTRA)
					.where(AGREEMENT_EXTRA.ID.eq(extra.getId()))
					.execute();
			else if(null == extra.getId() || extra.getId() < 0) {
				Payment payment = agreementInfo.getPaymentById(extra.getAgreementPayment());
				
				if(null == payment.getId() || payment.getId() < 0) {
					
					Integer paymentConceptId = dslContext.insertInto(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.DOMAIN, agreementInfo.getDomain())
						.set(PAYMENT_CONCEPT.CODE, "PAGA_EXTRA")
						.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
						.set(PAYMENT_CONCEPT.TYPE, null == payment.getType() ? (byte) 1 : (byte) payment.getType().ordinal())
						.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
						.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
						.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
						.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
						.returning(PAYMENT_CONCEPT.ID)
						.fetchOne(PAYMENT_CONCEPT.ID);
					
					Integer newPaymentId = dslContext.insertInto(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.DOMAIN, agreementInfo.getDomain())
							.set(AGREEMENT_PAYMENT.AGREEMENT, agreementInfo.getId())
							.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, paymentConceptId)
							.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
							.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
							.set(AGREEMENT_PAYMENT.MONTH, AonNumberUtils.toByte(payment.getMonth()))
							.set(AGREEMENT_PAYMENT.SALARY_TYPE, AonEnumUtils.getByte(payment.getSalaryType()))
							.returning(AGREEMENT_PAYMENT.ID)
							.fetchOne(AGREEMENT_PAYMENT.ID);
					
					extra.setAgreementPayment(newPaymentId);
					payment.setId(newPaymentId);
				}
				
				dslContext.insertInto(AGREEMENT_EXTRA)
					.set(AGREEMENT_EXTRA.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_EXTRA.AGREEMENT, agreementInfo.getId())
					.set(AGREEMENT_EXTRA.AGREEMENT_PAYMENT, extra.getAgreementPayment())
					.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
					.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
					.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
					.execute();
			} else
				dslContext.update(AGREEMENT_EXTRA)
					.set(AGREEMENT_EXTRA.START_DATE, extra.getStartDate())
					.set(AGREEMENT_EXTRA.END_DATE, extra.getEndDate())
					.set(AGREEMENT_EXTRA.ISSUE_DATE, extra.getIssueDate())
					.where(AGREEMENT_EXTRA.ID.eq(extra.getId()))
					.execute();
		});
	}
	
	// ------------------------------- deletePayments

	public static void deletePayments(Connection conn, Integer domainId, List<Integer> paymentIds) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		dslContext.delete(AGREEMENT_EXTRA)
			.where(AGREEMENT_EXTRA.DOMAIN.eq(domainId))
			.and(AGREEMENT_EXTRA.AGREEMENT_PAYMENT.in(paymentIds))
			.execute();
		
		dslContext.delete(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(domainId))
			.and(AGREEMENT_PAYMENT.ID.in(paymentIds))
			.execute();
	}
	
	// ------------------------------- Auxiliar methods
	
	private static java.util.Date parseToJavaDate(Date date) {
		return null == date ? null : new java.util.Date(date.getTime());
	}
	
	private static Date parseToSqlDate(java.util.Date date) {
		return null == date ? null : new Date(date.getTime());
	}
	
	private static Payment.Type getPaymentType(Byte type) {
		return type != null ? Payment.Type.values()[type.intValue()] : null;
	}

	private static Salary.Type getSalaryType(Byte type) {
		return type != null ? Salary.Type.values()[type.intValue()] : null;
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

}
