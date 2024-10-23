package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AgreementInfo implements Serializable, HasId<Integer>, HasDomain<Integer> {
	
	public static class Level implements Serializable, HasId<Integer>, HasDomain<Integer>, Comparable<Level> {

		private Integer id;
		private Integer domain;
		private String description;
		private boolean deleted = false;
		private boolean modify = false;
		private boolean catModify = false;

		@Override
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		@Override
		public Integer getDomain() {
			return domain;
		}
		
		public void setDomain(Integer domain) {
			this.domain = domain;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
		
		public boolean isModify() {
			return modify;
		}

		public void setModify(boolean modify) {
			this.modify = modify;
		}
		
		public boolean isCatModify() {
			return catModify;
		}

		public void setCatModify(boolean catModify) {
			this.catModify = catModify;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Level && id.equals(((Level) obj).id);
		}

		@Override
		public int compareTo(Level lvl) {
			return this.description.compareTo(lvl.getDescription());
		}
	}
	
	public static class LevelData implements Serializable, HasId<Integer>, HasDomain<Integer>, Comparable<LevelData> {

		private Integer id;
		private Integer domain;
		private String name;
		private String expression;
		private Date startDate;
		private Date endDate;
		private boolean deleted = false;
		private boolean modify = false;

		@Override
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		@Override
		public Integer getDomain() {
			return domain;
		}
		
		public void setDomain(Integer domain) {
			this.domain = domain;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String getExpression() {
			return expression;
		}

		public void setExpression(String expression) {
			this.expression = expression;
		}
		
		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}
		
		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
		
		public boolean isModify() {
			return modify;
		}

		public void setModify(boolean modify) {
			this.modify = modify;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Level && id.equals(((Level) obj).id);
		}

		@Override
		public int compareTo(LevelData lvl) {
			return this.name.compareTo(lvl.getName());
		}
	}

	public static class AgreementExtra implements Serializable, HasId<Integer>, HasDomain<Integer> {

		private Integer id;
		private Integer domain;
		private Integer agreementPayment;
		private String startDate;
		private String endDate;
		private String issueDate;
		private boolean deleted = false;

		@Override
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		@Override
		public Integer getDomain() {
			return domain;
		}
		
		public void setDomain(Integer domain) {
			this.domain = domain;
		}

		public Integer getAgreementPayment() {
			return agreementPayment;
		}
		
		public void setAgreementPayment(Integer agreementPayment) {
			this.agreementPayment = agreementPayment;
		}
		
		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		
		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		
		public String getIssueDate() {
			return issueDate;
		}

		public void setIssueDate(String issueDate) {
			this.issueDate = issueDate;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Level && id.equals(((Level) obj).id);
		}
	}
	
	public enum AgreementOwner {
		AONSOLUTIONS,
		SERVICONVENIOS
	}
	
	public enum ShownVariables {
		VALUES,
		NO_VALUES,
		ALL,
		MANUAL,
		NO_CONCEPT,
		EDIT_VARIABLE
	}
	
	private int id;
	private Integer domain;
	private String description;
	private AgreementOwner owner;
	private String ssNumber;

	private boolean hasContracts;
	
	private Set<Level> levels;
	private Map<Date, Set<String>> variables;
	private Map<Integer, Set<String>> categories;
	private Map<Integer, Set<String>> contracts;
	private Map<Integer, Set<LevelData>> levelDatas;
	
	private Set<Payment> payments;
	private Set<AgreementExtra> extras;

	private Set<Date> dates;
	
	private Set<String> filteredVariables = new HashSet<>();
	private Set<String> allVariables = new HashSet<>();
	private Set<String> noConceptVariables = new HashSet<>();
	private ShownVariables shownVariables = ShownVariables.VALUES;
	
	private Level selectedLevel;
	
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public AgreementOwner getOwner() {
		return owner;
	}

	public void setOwner(AgreementOwner owner) {
		this.owner = owner;
	}

	public String getSSNumber() {
		return ssNumber;
	}

	public void setSSNumber(String ssNumber) {
		this.ssNumber = ssNumber;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setHasContract(boolean hasContract) {
		this.hasContracts = hasContract;
	}
	
	public boolean getHasContract() {
		return this.hasContracts;
	}
	
	public Set<Level> getLevels() {
		return levels != null ? levels : Collections.<Level>emptySet();
	}
	
	public Set<Level> getActiveLevels(){
		return levels != null ? levels.stream().filter(level -> !level.isDeleted()).collect(Collectors.toSet()) : Collections.<Level>emptySet();
	}
	
	public boolean existLevel(String levelDescription) {
		return getLevels().stream().filter(level -> AonStringUtils.equalsIgnoreCase(level.getDescription(), levelDescription)).findAny().isPresent();
	}

	public void setLevels(Set<Level> levels) {
		this.levels = levels;
	}
	
	public Level getLevelById(Integer levelId) {
		return getLevels().stream().filter(level -> level.getId().equals(levelId)).findFirst().get();
	}
	
	public void deleteLevel(Integer levelId) {
		Level level = getLevelById(levelId);
		level.setDeleted(true);
		if(getLevelDatasMap().get(levelId) != null)
			getLevelDatasMap().get(levelId).forEach(levelData -> levelData.setDeleted(true));
	}
	
	public Map<Date, Set<String>> getVariables() {
		return variables != null ? variables : new HashMap<Date, Set<String>>();
	}

	public void setVariables(Map<Date, Set<String>> variables) {
		this.variables = variables;
		setFilteredVariables();
	}
	
	public Set<String> getVariablesByDate(Date selectedDate) {
		Set<String> variables = new HashSet<String>();
		switch (shownVariables) {
			case VALUES:
				getVariables().get(selectedDate).stream().filter(variable -> getAllVariables().contains(variable)).forEach(variable -> variables.add(variable));
				break;
			case NO_VALUES:
				getAllVariables().stream().filter(variable -> !getVariables().get(selectedDate).contains(variable)).forEach(variable -> variables.add(variable));
				break;
			case ALL:
				getAllVariables().stream().forEach(variable -> variables.add(variable));
				break;
			case NO_CONCEPT:
				getNoConceptVariables().stream().forEach(variable -> variables.add(variable));
				break;
			default:
				getAllVariables().stream().filter(variable -> this.filteredVariables.contains(variable)).forEach(variable -> variables.add(variable));
				break;
		}
		
		if(shownVariables.equals(ShownVariables.VALUES) && variables.isEmpty()) {
			this.shownVariables = ShownVariables.ALL;
			getAllVariables().stream().forEach(variable -> variables.add(variable));
		}
		
		List<String> variablesList = new ArrayList<>(variables);
		variablesList.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if(AonStringUtils.containsIgnoreCase(o1, "SALARIO_") && AonStringUtils.containsIgnoreCase(o2, "SALARIO_")) return o1.compareTo(o2);
				if(AonStringUtils.containsIgnoreCase(o1, "P_E_") && AonStringUtils.containsIgnoreCase(o2, "P_E_")) return o1.compareTo(o2);
				if(AonStringUtils.containsIgnoreCase(o1, "SALARIO_") && AonStringUtils.containsIgnoreCase(o2, "P_E_")) return -1;
				if(AonStringUtils.containsIgnoreCase(o1, "P_E_") && AonStringUtils.containsIgnoreCase(o2, "SALARIO_")) return 1;
				
				if(AonStringUtils.containsIgnoreCase(o1, "SALARIO_")) return -1;
				if(AonStringUtils.containsIgnoreCase(o1, "SALARIO_")) return -1;
				if(AonStringUtils.containsIgnoreCase(o1, "P_E_")) return -1;
				
				if(AonStringUtils.containsIgnoreCase(o2, "SALARIO_")) return 1;
				if(AonStringUtils.containsIgnoreCase(o2, "SALARIO_")) return 1;
				if(AonStringUtils.containsIgnoreCase(o2, "P_E_")) return 1;
				
				return o1.compareTo(o2);
			}
		});
		
		return new HashSet<>(variablesList);
	}
	
	private void setFilteredVariables() {
		Set<String> variables = new HashSet<String>();
		getVariables().values().forEach(variableSet -> variableSet.stream().forEach(variable -> variables.add(variable)));
		this.filteredVariables = variables;
	}

	public Set<String> getAllVariables() {
		return allVariables;
	}
	
	public void setAllVariables(Set<String> variables) {
		this.allVariables = variables;
	}
	
	public Set<String> getNoConceptVariables() {
		return noConceptVariables;
	}
	
	public void setNoConceptVariables(Set<String> variables) {
		this.noConceptVariables = variables;
	}
	
	public String getShownVariables() {
		return this.shownVariables.toString();
	}
	
	public void setShowVariables(String shownVariable) {
		this.shownVariables = ShownVariables.valueOf(shownVariable);
	}

	public void setFilteredVariables(Set<String> variables) {
		this.shownVariables = ShownVariables.MANUAL;
		this.filteredVariables = variables;
	}
	
	public void setFilteredValuesVariables() {
		this.shownVariables = ShownVariables.VALUES;
	}

	public void setFilteredNoValuesVariables() {
		this.shownVariables = ShownVariables.NO_VALUES;
	}

	public void setFilteredAllVariables() {
		this.shownVariables = ShownVariables.ALL;
	}
	
	public void setFilteredNoConceptVariables() {
		this.shownVariables = ShownVariables.NO_CONCEPT;
	}

	public Map<Integer, Set<String>> getCategoriesMap() {
		return categories != null ? categories : new HashMap<Integer, Set<String>>();
	}

	public void setCategoriesMap(Map<Integer, Set<String>> categories) {
		this.categories = categories;
	}
	
	public void addCategory(Integer levelId, String category) {
		Set<String> levelCategories = this.categories.get(levelId);
		if(null == levelCategories) levelCategories = new HashSet<>();
		levelCategories.add(category);
		this.categories.put(levelId, levelCategories);
	}
	
	public Map<Integer, Set<String>> getContractsMap() {
		return contracts != null ? contracts : new HashMap<Integer, Set<String>>();
	}

	public void setContractsMap(Map<Integer, Set<String>> contracts) {
		this.contracts = contracts;
	}
	
	public Map<Integer, Set<LevelData>> getLevelDatasMap() {
		return levelDatas != null ? levelDatas : new HashMap<Integer, Set<LevelData>>();
	}

	public void setLevelDatasMap(Map<Integer, Set<LevelData>> levelDatas) {
		this.levelDatas = levelDatas;
	}
	
	public LevelData getLevelData(Integer levelId, String variable, Date date) {
		Set<LevelData> levelDatas = getLevelDatasMap().get(levelId);
		if(null == levelDatas) return null;
		
		for(LevelData levelData : levelDatas)
			if(AonStringUtils.equalsIgnoreCase(levelData.getName(), variable) && levelData.getStartDate().equals(date))
				return levelData;
		return null;
	}
	
	public LevelData getDefaultLevelData(String variable, Date date) {
		Set<LevelData> levelDatas = getLevelDatasMap().get(0);
		if(null == levelDatas) return null;
		
		for(LevelData levelData : levelDatas)
			if(AonStringUtils.equalsIgnoreCase(levelData.getName(), variable) && levelData.getStartDate().equals(date))
				return levelData;
		return null;
	}
	
	public void createLevelData(Integer levelId, String variable, String expression, Date selectedDate) {
		Set<LevelData> levelDatas = getLevelDatasMap().get(levelId);
		if(null == levelDatas) levelDatas = new HashSet<>();
		Date endDate = getNextEndDate(selectedDate);
		
		Random rand = new Random();
		int newId = rand.nextInt(1000) * -1;
		
		LevelData levelData = new LevelData();
		levelData.setId(newId);
		levelData.setDomain(this.getDomain());
		levelData.setName(variable);
		levelData.setExpression(expression);
		levelData.setStartDate(selectedDate);
		levelData.setEndDate(endDate);
		levelData.setDeleted(false);
		levelData.setModify(true);
		levelDatas.add(levelData);
		getLevelDatasMap().put(levelId, levelDatas);
	}
	
	public void createLevel(String descriptionIn) {
		Random rand = new Random();
		int newLevelId = rand.nextInt(1000) * -1;
		
		Level level = new Level();
		level.setId(newLevelId);
		level.setDomain(getDomain());
		level.setDescription(descriptionIn);
		level.setModify(true);
		
		getLevels().add(level);
		getCategoriesMap().put(newLevelId, new HashSet<>());
		getLevelDatasMap().put(newLevelId, new HashSet<>());
	}
	
	private Date getNextEndDate(Date selectedDate) {
		int index = getSortedDates().stream()
			    .map(date -> date)
			    .collect(Collectors.toList())
			    .indexOf(selectedDate);
		
		if(index == 0) return null;
		Date nextDate = (Date) getSortedDates().toArray()[index -1];
		Date endDate = DateUtils.copyDateOnly(nextDate);
		DateUtils.addDays2Date(endDate, -1);
		
		return endDate;
	}

	public void updateLevelData(Integer levelId, Integer levelDataId, String newExpression) {
		Set<LevelData> levelDatas = getLevelDatasMap().get(levelId);
		levelDatas.stream().filter(levelData -> levelData.getId().equals(levelDataId))
			.findFirst()
			.ifPresent(levelData -> {
				if(AonStringUtils.isBlank(newExpression)) levelData.setDeleted(true); 
				levelData.setExpression(newExpression);
				levelData.setModify(true);
			});
	}
	
	public Set<Payment> getPayments() {
		return payments != null ? payments : new HashSet<Payment>();
	}
	
	public Set<Payment> getActivePayments() {
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && !isHideExpression(payment) && null != payment.getType() && !payment.getType().equals(Type.CRA_0004)).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getActivePaymentsExtra() {
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && !isHideExpression(payment) && null != payment.getType() && payment.getType().equals(Type.CRA_0004)).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getPaymentsExtraAndHides() {
		Date date = new Date();
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && payment.getType() != null && payment.getType().equals(Type.CRA_0004) && (payment.getEndDate() == null || payment.getEndDate().after(date))).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getOldPaymentsExtraAndHides() {
		Date date = new Date();
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && payment.getType() != null && payment.getType().equals(Type.CRA_0004) && (payment.getEndDate() == null || payment.getEndDate().before(date))).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getPaymentsAndHides() {
		Date date = new Date();
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && payment.getType() != null && !payment.getType().equals(Type.CRA_0004) && (payment.getEndDate() == null || payment.getEndDate().after(date))).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getOldPaymentsAndHides() {
		Date date = new Date();
		return payments != null ? payments.stream().filter(payment -> !payment.isDeleted() && payment.getType() != null && !payment.getType().equals(Type.CRA_0004) && (payment.getEndDate() == null || payment.getEndDate().before(date))).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}
	
	public Set<Payment> getDeleteAndHidesPayments() {
		return payments != null ? payments.stream().filter(payment -> payment.isDeleted() || isHideExpression(payment)).collect(Collectors.toSet()) : Collections.<Payment>emptySet();
	}

	private boolean isHideExpression(Payment payment) {
		String expression = payment.getExpression();
		return !AonStringUtils.isBlank(expression) && AonStringUtils.containsIgnoreCase(expression, "HIDE") && AonStringUtils.startsWithIgnoreCase(expression, "HIDE");
	}
	
	public void setPayments(Set<Payment> payments) {
		this.payments = payments;
	}
	
	public void addPayment(Payment payment) {
		this.payments.add(payment);
	}

	public Payment getPaymentById(Integer paymentId) {
		return getPayments().stream().filter(agreementPayment -> agreementPayment.getId().equals(paymentId)).findFirst().get();
	}
	
	public void deletePayment(Payment payment) {
		payment.setDeleted(true);
		getExtras().stream().filter(extra -> extra.getAgreementPayment().equals(payment.getId())).findFirst().ifPresent(agreementExtra -> agreementExtra.setDeleted(true));
	}
	
	public Set<AgreementExtra> getExtras() {
		return extras != null ? extras : new HashSet<AgreementExtra>();
	}
	
	public Set<AgreementExtra> getActiveExtras() {
		List<Integer> deleteHidePaymentIds = getDeleteAndHidesPayments().stream().map(payment -> payment.getId()).collect(Collectors.toList());
		return extras != null ? extras.stream().filter(extra -> !extra.isDeleted() && !deleteHidePaymentIds.contains(extra.getAgreementPayment())).collect(Collectors.toSet()) : Collections.<AgreementExtra>emptySet();
	}

	public void setExtras(Set<AgreementExtra> extras) {
		this.extras = extras;
	}
	
	public void addExtra(AgreementExtra extra) {
		this.extras.add(extra);
		if(null != extra.getAgreementPayment()) {
			Payment payment = getPaymentById(extra.getAgreementPayment());
			payment.setModify(true);
		}
	}
	
	public void addExtra(Extra extra) {
		AgreementExtra newExtra = new AgreementExtra();
		newExtra.setId(extra.getId());
		newExtra.setDomain(extra.getDomain());
		newExtra.setAgreementPayment(extra.getPaymentId());
		newExtra.setStartDate(extra.getStartDate());
		newExtra.setEndDate(extra.getEndDate());
		newExtra.setIssueDate(extra.getIssueDate());
		addExtra(newExtra);
	}
	
	public AgreementExtra getExtraPayment(Integer paymentId) {
		Optional<AgreementExtra> extraFind = getExtras().stream().filter(extra -> null != extra.getAgreementPayment() && extra.getAgreementPayment().equals(paymentId)).findFirst();
		return extraFind.isPresent() ? extraFind.get() : null;
	}
	
	public void updateExtraPaymentId(Integer oldPaymentId, Integer newPaymentId) {
		this.extras.stream().filter(extra -> extra.getAgreementPayment().equals(oldPaymentId)).findFirst().ifPresent(extra -> extra.setAgreementPayment(newPaymentId));
	}
	
	public Set<Date> getDates() {
		return dates != null ? dates : new HashSet<Date>();
	}
	
	public Set<Date> getSortedDates() {
		if(null == dates)
			return new HashSet<Date>();
		
		List<Date> list = new ArrayList<>(dates);
		Collections.sort(list, Collections.reverseOrder());
		return new LinkedHashSet<>(list);
	}

	public void setDates(Set<Date> dates) {
		this.dates = dates;
	}
	
	public void createNewPeriod(Date newDate) {
		getDates().add(newDate);
		Set<Date> datesAux = getSortedDates();
		
		if(datesAux.size() > 1) {
			Date date = (Date)datesAux.toArray()[1];
			Set<String> dateVariables = getVariables().get(date);
			getVariables().put(newDate, dateVariables);
		
			Date newEndDate = DateUtils.copyDateOnly(newDate);
			DateUtils.addDays2Date(newEndDate, -1);
			
			Map<Integer, Set<LevelData>> newLevelDataMap = new HashMap<>();
			for(Entry<Integer, Set<LevelData>> entry : getLevelDatasMap().entrySet()) {
				Integer levelId = entry.getKey();
				Set<LevelData> levelDatasAux = entry.getValue();
				
				Set<LevelData> newLevelDatas = new HashSet<>();
				
				levelDatasAux.forEach(levelData -> {
					if(!levelData.getStartDate().equals(date)) return;
					LevelData newLevelData = copyLevelData(levelData, newDate);
					newLevelDatas.add(newLevelData);
				});
				
				newLevelDataMap.put(levelId, newLevelDatas);
			}
			
			newLevelDataMap.entrySet().forEach(entry -> {
				Integer levelId = entry.getKey();
				Set<LevelData> levelDatas = entry.getValue();
				getLevelDatasMap().get(levelId).addAll(levelDatas);
			});
			 
			 
			// Set endDate to oldPeriod
			getLevelDatasMap().values().forEach(levelDatas -> levelDatas.forEach(levelData -> {
				if(levelData.getStartDate().equals(date)) levelData.setEndDate(newEndDate);
			}));
			
		} else {
			getLevels().forEach(level -> getLevelDatasMap().put(level.getId(), new HashSet<>()));
			getVariables().put(newDate, getAllVariables());
		}
	}
	
	private LevelData copyLevelData(LevelData levelData, Date newDate) {
		LevelData levelDataCopy = new LevelData();
		
		Random rand = new Random();
		int newId = rand.nextInt(1000) * -1;
		
		levelDataCopy.setId(newId);
		levelDataCopy.setDomain(levelData.getDomain());
		levelDataCopy.setName(levelData.getName());
		levelDataCopy.setExpression(levelData.getExpression());
		levelDataCopy.setStartDate(newDate);
		levelDataCopy.setEndDate(null);
		levelDataCopy.setDeleted(false);
		
		return levelDataCopy;
	}

	public void deletePeriod(Date deleteDate) {
		getDates().remove(deleteDate);
		getVariables().remove(deleteDate);
		getLevelDatasMap().values().forEach(levelDatas -> levelDatas.forEach(levelData -> levelData.setDeleted(levelData.getStartDate().equals(deleteDate))));
	}
	
	// ----------------------------------------------------------------------

	public Level getSelectedLevel() {
		return selectedLevel;
	}

	public void setSelectedLevel(Level selectedLevel) {
		this.selectedLevel = selectedLevel;
	}

	public boolean isSaved(){
		return id > 0;
	}

	public boolean canDelete(){
		return !isSaved();
	}

	public void replacePayment(Payment payment) {
		getPayments().removeIf(paymentIt -> null != paymentIt.getId() && paymentIt.getId().equals(payment.getId()));
		payment.setModify(true);
		getPayments().add(payment);
	}
	
	public void syncPayment(Payment payment, AgreementExtra extra) {
		if(payment.getType() == Payment.Type.CRA_0004 && (extra == null || extra.isDeleted())) {
			getExtras().forEach(extraIt -> {
				Payment paymentIt = getPaymentById(extraIt.getAgreementPayment());
				if(paymentIt.getType() == Payment.Type.CRA_0004) {
					paymentIt.setMonth(null);
					paymentIt.setModify(true);
					extraIt.setDeleted(true);
				}
			});
		}
	}

	public void replaceExtra(AgreementExtra extra) {
		getExtras().removeIf(extraIt -> extraIt.getId().equals(extra.getId()));
		getExtras().add(extra);
		getPaymentById(extra.getAgreementPayment()).setModify(true);
	}
	
	public void syncExtras(AgreementExtra extra) {
		String issueDate = extra.getIssueDate();
		// Summer = 0 || Winter = 1
		Byte winterSummer = AonStringUtils.containsIgnoreCase(issueDate, "12") ? (byte)1 : (byte)0;
		String period = getExtraPeriod(extra);
		
		if(winterSummer == (byte)0)
			syncWinterExtra(period);
		else
			syncSummerExtra(period);
	}
	
	private void syncSummerExtra(String period) {
		Optional<AgreementExtra> summerExtra = getExtras().stream()
			.filter(extraIt -> AonStringUtils.containsIgnoreCase(extraIt.getEndDate(), "6") || AonStringUtils.containsIgnoreCase(extraIt.getEndDate(), "7"))
			.findFirst();
		
		if(summerExtra.isPresent()) {
			summerExtra.get().setStartDate(AonStringUtils.equalsIgnoreCase(period, "A") ? "01/07 -1" : "01/01");
			summerExtra.get().setEndDate("30/06");
			
			Payment summerPayment = getPaymentById(summerExtra.get().getAgreementPayment());
			summerPayment.setModify(true);
		}
	}

	private void syncWinterExtra(String period) {
		Optional<AgreementExtra> winterExtra = getExtras().stream()
			.filter(extraIt -> AonStringUtils.containsIgnoreCase(extraIt.getEndDate(), "12"))
			.findFirst();
		
		if(winterExtra.isPresent()) {
			winterExtra.get().setStartDate(AonStringUtils.equalsIgnoreCase(period, "A") ? "01/01" : "01/07");
			winterExtra.get().setEndDate("31/12");
			
			Payment winterPayment = getPaymentById(winterExtra.get().getAgreementPayment());
			winterPayment.setModify(true);
		}
	}

	private String getExtraPeriod(AgreementExtra extra) {
		if(AonStringUtils.containsIgnoreCase(extra.getStartDate(), "-1")) return "A";
		
		int startMonth = Integer.parseInt(extra.getStartDate().split("/")[1]);
		int endMonth = Integer.parseInt(extra.getEndDate().split("/")[1]);
		
		switch (endMonth - startMonth) {
		case 11:
			return "A";
		case 5:
			return "S";
		default:
			return null;
		}
	}

	public void addSeniority(String seniorityExpression) {
		Optional<Date> initialDateOpt = getDates().stream().findFirst();
		if(!initialDateOpt.isPresent()) return;
		
		Optional<LevelData> levelDataOpt = levelDatas.get(0).stream().filter(levelData -> AonStringUtils.equalsIgnoreCase(levelData.getName(), "INICIO_ANTIGUEDAD")).findFirst();
		if(!levelDataOpt.isPresent() && AonStringUtils.isBlank(seniorityExpression)) return;
		
		Date initialDate = initialDateOpt.get();
		
		if(!levelDataOpt.isPresent()) {
			Random rand = new Random();
			int newId = rand.nextInt(1000) * -1;
			if(newId > 0) newId = newId * -1;
			
			LevelData levelData = new LevelData();
			levelData.setId(newId);
			levelData.setDomain(this.domain);
			levelData.setDeleted(false);
			levelData.setModify(false);
			levelData.setStartDate(initialDate);
			levelData.setName("INICIO_ANTIGUEDAD");
			levelData.setExpression(seniorityExpression);
			
			levelDatas.get(0).add(levelData);
			variables.get(initialDate).add("INICIO_ANTIGUEDAD");
		} else {
			LevelData levelData = levelDataOpt.get();
			levelData.setDeleted(AonStringUtils.isBlank(seniorityExpression));
			levelData.setModify(!AonStringUtils.isBlank(seniorityExpression));
			levelData.setExpression(seniorityExpression);
			variables.get(initialDate).remove("INICIO_ANTIGUEDAD");
		}
		
	}
	
	public String getSeniority() {
		Optional<LevelData> levelDataOpt = levelDatas.get(0).stream().filter(levelData -> AonStringUtils.equalsIgnoreCase(levelData.getName(), "INICIO_ANTIGUEDAD")).findFirst();
		return levelDataOpt.isPresent() ? levelDataOpt.get().getExpression() : null;
	}

}
