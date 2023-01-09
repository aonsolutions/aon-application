package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
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
		agreement.setAllVariables(allVariables);
		
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
		Result<AgreementPaymentRecord> agreementPaymentRecords = dslContext.selectFrom(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(agreement.getId()))
				.orderBy(AGREEMENT_PAYMENT.DESCRIPTION)
				.fetch();
		
		Set<Payment> paymentsSet = new LinkedHashSet<>();
		
		for(AgreementPaymentRecord agreementPaymentRecord : agreementPaymentRecords) {
			Integer conceptId = agreementPaymentRecord.getPaymentConcept();
			Optional<PaymentConceptRecord> conceptRecord = dslContext.selectFrom(PAYMENT_CONCEPT).where(PAYMENT_CONCEPT.ID.eq(conceptId)).fetchOptional();
			//conceptId = conceptRecord.map(r -> r.getId()).orElse(null); 
			
			Payment payment = new Payment();
			payment.setId(agreementPaymentRecord.getId());
			payment.setDomain(agreementPaymentRecord.getDomain());
			payment.setConceptId(conceptRecord.map(r -> r.getId()).orElse(null));
			payment.setName(conceptRecord.map(r -> r.getCode()).orElse(null));
			payment.setType((conceptRecord.isPresent() && null == agreementPaymentRecord.getType()) ? getPaymentType(conceptRecord.get().getType()) : getPaymentType(agreementPaymentRecord.getType()));
			payment.setDescription((conceptRecord.isPresent() && AonStringUtils.isBlank(agreementPaymentRecord.getDescription())) ? conceptRecord.get().getDescription() : agreementPaymentRecord.getDescription());
			payment.setExpression((conceptRecord.isPresent() && AonStringUtils.isBlank(agreementPaymentRecord.getExpression())) ? conceptRecord.get().getExpression() : agreementPaymentRecord.getExpression());
			payment.setStartDate(parseToJavaDate(agreementPaymentRecord.getStartDate()));
			payment.setEndDate(parseToJavaDate(agreementPaymentRecord.getEndDate()));
			payment.setMonth(null == agreementPaymentRecord.getMonth() ? null : (short)agreementPaymentRecord.getMonth());
			payment.setSalaryType(getSalaryType(agreementPaymentRecord.getSalaryType()));
			payment.setIrpfExpression((conceptRecord.isPresent() && AonStringUtils.isBlank(agreementPaymentRecord.getIrpfExpression())) ? conceptRecord.get().getIrpfExpression() : agreementPaymentRecord.getIrpfExpression());
			payment.setQuoteExpression((conceptRecord.isPresent() && AonStringUtils.isBlank(agreementPaymentRecord.getQuoteExpression())) ? conceptRecord.get().getQuoteExpression() : agreementPaymentRecord.getQuoteExpression());
			
			paymentsSet.add(payment);
		}

		agreement.setPayments(sortPaymentSet(paymentsSet));
	}
	
	private static Set<Payment> sortPaymentSet(Set<Payment> paymentsSetIn) {
		Set<Payment> paymentsSet = new LinkedHashSet<>();
		
		ArrayList<Payment> paymentArray = new ArrayList<>(paymentsSetIn);
		paymentArray.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
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
			if(payment.isDeleted())
				dslContext.delete(AGREEMENT_PAYMENT)
					.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
					.execute();
			else if(null == payment.getId() || payment.getId() < 0) {
				if(null == payment.getConceptId() && !AonStringUtils.isBlank(payment.getName()))
					payment.setConceptId(createPaymentConcept(dslContext, payment));
				
				Integer newPaymentId = dslContext.insertInto(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.DOMAIN, agreementInfo.getDomain())
					.set(AGREEMENT_PAYMENT.AGREEMENT, agreementInfo.getId())
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
					.set(AGREEMENT_PAYMENT.TYPE, (byte) payment.getType().ordinal())
					.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
					.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
					.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
					.set(AGREEMENT_PAYMENT.MONTH, null == payment.getMonth() ? null : payment.getMonth().byteValue())
					.set(AGREEMENT_PAYMENT.SALARY_TYPE,null == payment.getSalaryType() ? (byte) Salary.Type.SALARY.ordinal() : (byte) payment.getSalaryType().ordinal())
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
					.returning(AGREEMENT_PAYMENT.ID)
					.fetchOne(AGREEMENT_PAYMENT.ID);
				
				if(payment.hasExtra())
					agreementInfo.updateExtraPaymentId(payment.getId(), newPaymentId);
			} else {
				if(null == payment.getConceptId() && !AonStringUtils.isBlank(payment.getName()))
					payment.setConceptId(createPaymentConcept(dslContext, payment));
				else
					dslContext.update(PAYMENT_CONCEPT)
						.set(PAYMENT_CONCEPT.CODE, payment.getName())
						.where(PAYMENT_CONCEPT.ID.eq(payment.getConceptId()))
						.execute();
			
				dslContext.update(AGREEMENT_PAYMENT)
					.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, payment.getConceptId())
					.set(AGREEMENT_PAYMENT.TYPE, (byte) payment.getType().ordinal())
					.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
					.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
					.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
					.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
					.set(AGREEMENT_PAYMENT.MONTH, null == payment.getMonth() ? null : payment.getMonth().byteValue())
					.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) payment.getSalaryType().ordinal())
					.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
					.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
					.where(AGREEMENT_PAYMENT.ID.eq(payment.getId()))
					.execute();
			}
		});
	}
	
	private static Integer createPaymentConcept(DSLContext dslContext, Payment payment) {
		PaymentConceptRecord paymentConceptRecord = dslContext.insertInto(PAYMENT_CONCEPT)
				.set(PAYMENT_CONCEPT.DOMAIN, payment.getDomain())
				.set(PAYMENT_CONCEPT.CODE, payment.getName())
				.set(PAYMENT_CONCEPT.DESCRIPTION, payment.getDescription())
				.set(PAYMENT_CONCEPT.TYPE, (byte) payment.getType().ordinal())
				.set(PAYMENT_CONCEPT.DESCRIPTION_DECORABLE, (byte)0)
				.set(PAYMENT_CONCEPT.EXPRESSION, payment.getExpression())
				.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, payment.getIrpfExpression())
				.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, payment.getQuoteExpression())
				.returning(PAYMENT_CONCEPT.ID)
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
					Integer newPaymentId = dslContext.insertInto(AGREEMENT_PAYMENT)
							.set(AGREEMENT_PAYMENT.DOMAIN, agreementInfo.getDomain())
							.set(AGREEMENT_PAYMENT.AGREEMENT, agreementInfo.getId())
							.set(AGREEMENT_PAYMENT.TYPE, (byte) payment.getType().ordinal())
							.set(AGREEMENT_PAYMENT.DESCRIPTION, payment.getDescription())
							.set(AGREEMENT_PAYMENT.EXPRESSION, payment.getExpression())
							.set(AGREEMENT_PAYMENT.START_DATE, null == payment.getStartDate() ? parseToSqlDate((java.util.Date)agreementInfo.getSortedDates().toArray()[agreementInfo.getSortedDates().size()-1]) : parseToSqlDate(payment.getStartDate()))
							.set(AGREEMENT_PAYMENT.END_DATE, parseToSqlDate(payment.getEndDate()))
							.set(AGREEMENT_PAYMENT.MONTH, null == payment.getMonth() ? null : payment.getMonth().byteValue())
							.set(AGREEMENT_PAYMENT.SALARY_TYPE, null == payment.getSalaryType() ? (byte) Salary.Type.SALARY.ordinal() : (byte) payment.getSalaryType().ordinal())
							.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, payment.getIrpfExpression())
							.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, payment.getQuoteExpression())
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

}
