package com.code.aon.file.tax.model.MOD303.data;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;


public class Declaration {

	private Integer year;
	private String period;
	private boolean replacement;
	private boolean complementary;
	private boolean taxRefundRegistry;
	private boolean simplRegimeOnly;
	private boolean mergedDeclaration;
	private boolean concurso;
	private Date concursoDate;
	private String concursoAuto;
	private boolean vatAccrualRegime;
	private boolean vatAccrualRegimeReceiver;
	private boolean prorataOption;
	private boolean prorataRevoke;
	
	private String bankName;
	private String ccc;
	
	private boolean generalProrataApplied;
	private boolean specialProrataApplied;
	private double prorata;

	private boolean person;
	private String document;
	private Integer startPeriod;
	private Integer endPeriod;
	private Double result;
	private String name;
	private String surname;
	private String address;
	private Integer addressNumber;
	private String entity;
	private String city;
	private String provinceID;
	private String province;
	private Integer zip;
	private String telephone;
	private String fax;
	private String email;
	
	private int todayDay;
	private String todayMonth;
	private int todayYear;
	
	
	private Map<String,Breakdown> outputVat = new TreeMap<String, Breakdown>();
	private Map<String,Breakdown> surcharge = new TreeMap<String, Breakdown>();
	private Map<String,Breakdown> intracommunitary = new TreeMap<String, Breakdown>();
	private Map<String,Breakdown> invPasive = new TreeMap<String, Breakdown>();
	private Map<String,Breakdown> outputVatInvPasive = new TreeMap<String, Breakdown>();
	
	private double baseIntracommunitary;
	private double quotaIntracommunitary;

	private double baseIntracommunitaryCT;
	private double quotaIntracommunitaryCT;
	private double baseISPCT;
	private double quotaISPCT;
	
	private double baseInvPasive;
	private double quotaInvPasive;

	private double baseModifications;
	private double quotaModifications;

	private double baseSurchrageModifications;
	private double quotaSurchrageModifications;

	private double outputTotal;
	
	private double innerCommonOperationsBase;
	private double innerCommonOperationsQuota;
	private double innerInvestmentOperationsBase;
	private double innerInvestmentOperationsQuota;
	private double innerExpensesOperationsBase;
	private double innerExpensesOperationsQuota;
	private double importedCommonOperationsBase;
	private double importedCommonOperationsQuota;
	private double importedInvestmentOperationsBase;
	private double importedInvestmentOperationsQuota;
	private double intracommunitaryCommonOperationsBase;
	private double intracommunitaryCommonOperationsQuota;
	private double intracommunitaryInvestmentOperationsBase;
	private double intracommunitaryInvestmentOperationsQuota;
	private double intracommunitaryExpensesOperationsBase;
	private double intracommunitaryExpensesOperationsQuota;
	
	private double deductionRestificationBase;
	private double deductionRestificationQuota;

	private double agriculturalRegimeCompensation;
	private double investmentNormalization;
	private double prorataNormalization;
	private double deductTotal;
	private double difference;
	private double intracommunitaryDeliveries;
	private double intracommunitaryServiceDeliveries;
	private double exportationTotal;
	private double nonTaxableTotal;
	private double invSujPasNotIncluded;
	private double presIntraServices;
	
	private double alavaPercent; 
	private double gipuzkoaPercent; 
	private double bizkaiaPercent; 
	private double navarraPercent; 
	private double commonTerritoryPercent;
	private double regularizationResult;
	private double toDeduct;
	private double quota;
	private double previousYearCompensateQuota;
	private double extraCharge;
	private double delayInterest;
	private double deposit;
	private double compensate;
	private double payBack;
	private double previousPayBack;
	private double previousDeposit;
	private double totalDebt;
	private boolean withoutActivity;
	private String bankAccount;
	private String depositBankAccount;
	private String payBackBankAccount;
	
	private double vatAccrualInputBase;
	private double vatAccrualInputQuota;
	private double vatAccrualOutputBase;
	private double vatAccrualOutputQuota;
	
	
	private Map<String,Breakdown> innerAssetPurchases = new TreeMap<String, Breakdown>();
	private double baseInnerAssetPurchases;
	private double quotaInnerAssetPurchases;
	private double deductibleQuotaInnerAssetPurchases;
	private Map<String,Breakdown> expenses = new TreeMap<String, Breakdown>();
	private double baseExpenses;
	private double quotaExpenses;
	private double deductibleQuotaExpenses;
	private Map<String,Breakdown> investmentAsset = new TreeMap<String, Breakdown>();
	private double baseInvestmentAsset;
	private double quotaInvestmentAsset;
	private double deductibleQuotaInvestmentAsset;
	private double baseTotalAddInfo;
	private double quotaTotalAddInfo;
	private double deductibleQuotaTotalAddInfo;

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getPeriod() {
		return period;
	}
	public String getPeriodForBizkaia() {
		if ("01".equals(period)) return "01M";
		if ("02".equals(period)) return "02M";
		if ("03".equals(period)) return "03M";
		if ("04".equals(period)) return "04M";
		if ("05".equals(period)) return "05M";
		if ("06".equals(period)) return "06M";
		if ("07".equals(period)) return "07M";
		if ("08".equals(period)) return "08M";
		if ("09".equals(period)) return "09M";
		if ("10".equals(period)) return "10M";
		if ("11".equals(period)) return "11M";
		if ("12".equals(period)) return "12M";
		if ("T1".equals(period)) return "01T";
		if ("T2".equals(period)) return "02T";
		if ("T3".equals(period)) return "03T";
		if ("T4".equals(period)) return "0A"; // ¿?
		if ("An".equals(period)) return "0A"; // ¿?
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getGipuzkoaModel() {
		// Si el periodo es trimestral (1T,2T ...) --> modelo 300.
		return StringUtils.isNumeric(getPeriod())?"320":"300";
	}
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public int getComplementaryNumber() {
		return isComplementary()?1:0;
	}

	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}
	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
	}
	public int getTaxRefundRegistryNumber() {
		return isTaxRefundRegistry()?1:2;
	}
	
	public boolean isSimplRegimeOnly() {
		return simplRegimeOnly;
	}
	public void setSimplRegimeOnly(boolean simplRegimeOnly) {
		this.simplRegimeOnly = simplRegimeOnly;
	}
	public int getSimplRegimeOnlyNumber() {
		return isSimplRegimeOnly()?1:2;
	}
	public boolean isMergedDeclaration() {
		return mergedDeclaration;
	}
	public void setMergedDeclaration(boolean mergedDeclaration) {
		this.mergedDeclaration = mergedDeclaration;
	}
	public int getMergedDeclarationNumber() {
		return isMergedDeclaration()?1:2;
	}
	public boolean isConcurso() {
		return concurso;
	}
	public void setConcurso(boolean concurso) {
		this.concurso = concurso;
	}
	public int getConcursoNumber() {
		return isConcurso()?1:2;
	}
	public Date getConcursoDate() {
		return concursoDate;
	}
	public void setConcursoDate(Date concursoDate) {
		this.concursoDate = concursoDate;
	}
	public String getConcursoDateString() {
		return "";
	}
	public String getConcursoAuto() {
		return concursoAuto;
	}
	public void setConcursoAuto(String concursoAuto) {
		this.concursoAuto = concursoAuto;
	}
	public boolean isVatAccrualRegime() {
		return vatAccrualRegime;
	}
	public void setVatAccrualRegime(boolean vatAccrualRegime) {
		this.vatAccrualRegime = vatAccrualRegime;
		System.out.println("vatAccrualRegime ..: " + vatAccrualRegime);
	}
	public int getVatAccrualRegimeNumber() {
		return isVatAccrualRegime()?1:2;
	}
	public boolean isVatAccrualRegimeReceiver() {
		return vatAccrualRegimeReceiver;
	}
	public void setVatAccrualRegimeReceiver(boolean vatAccrualRegimeReceiver) {
		this.vatAccrualRegimeReceiver = vatAccrualRegimeReceiver;
		System.out.println("vatAccrualRegimeReceiver ..: " + vatAccrualRegimeReceiver);
	}
	public int getVatAccrualRegimeReceiverNumber() {
		return isVatAccrualRegimeReceiver()?1:2;
	}
	public boolean isProrataOption() {
		return prorataOption;
	}
	public void setProrataOption(boolean prorataOption) {
		this.prorataOption = prorataOption;
	}
	public int getProrataOptionNumber() {
		return isProrataOption()?1:2;
	}
	public boolean isProrataRevoke() {
		return prorataRevoke;
	}
	public void setProrataRevoke(boolean prorataRevoke) {
		this.prorataRevoke = prorataRevoke;
	}
	public int getProrataRevokeNumber() {
		return isProrataRevoke()?1:2;
	}
	public boolean isPerson() {
		return person;
	}
	public void setPerson(boolean person) {
		this.person = person;
	}
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public Integer getStartPeriod() {
		return startPeriod;
	}
	public void setStartPeriod(Integer startPeriod) {
		this.startPeriod = startPeriod;
	}

	public Integer getEndPeriod() {
		return endPeriod;
	}
	public void setEndPeriod(Integer endPeriod) {
		this.endPeriod = endPeriod;
	}

	public String getDeclarationType() {
		if (getResult() == 0  ) {
			return "N";
		} else if (getResult() > 0  ) {
			return "I";
		} else if (getResult() < 0  ) {
			if (getPayBack() > 0  ) {
				return "D";
			} 
			if (getCompensate() > 0  ) {
				return "C";
			}
		}
		return " ";
	}
	
	public Double getResult() {
		return result;
	}
	public void setResult(Double result) {
		this.result = result;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getAddressNumber() {
		return addressNumber;
	}
	public void setAddressNumber(Integer addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvinceID() {
		return provinceID;
	}
	public void setProvinceID(String provinceID) {
		this.provinceID = provinceID;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public Integer getZip() {
		return zip;
	}
	public void setZip(Integer zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getTodayDay() {
		return todayDay;
	}
	public void setTodayDay(int todayDay) {
		this.todayDay = todayDay;
	}
	public String getTodayMonth() {
		return todayMonth;
	}
	public void setTodayMonth(String todayMonth) {
		this.todayMonth = todayMonth;
	}
	public int getTodayYear() {
		return todayYear;
	}
	public void setTodayYear(int todayYear) {
		this.todayYear = todayYear;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	public boolean isGeneralProrataApplied() {
		return generalProrataApplied;
	}
	public void setGeneralProrataApplied(boolean generalProrataApplied) {
		this.generalProrataApplied = generalProrataApplied;
	}
	public boolean isSpecialProrataApplied() {
		return specialProrataApplied;
	}
	public void setSpecialProrataApplied(boolean specialProrataApplied) {
		this.specialProrataApplied = specialProrataApplied;
	}
	public double getProrata() {
		return prorata;
	}
	public void setProrata(double prorata) {
		this.prorata = prorata;
	}
	public Map<String, Breakdown> getOutputVat() {
		return outputVat;
	}
	public void setOutputVat(Map<String, Breakdown> outputVat) {
		this.outputVat = outputVat;
	}
	public Map<String, Breakdown> getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(Map<String, Breakdown> surcharge) {
		this.surcharge = surcharge;
	}
	public Map<String, Breakdown> getIntracommunitary() {
		return intracommunitary;
	}
	public void setIntracommunitary(Map<String, Breakdown> intracommunitary) {
		this.intracommunitary = intracommunitary;
	}
	public Map<String, Breakdown> getInvPasive() {
		return invPasive;
	}
	public void setInvPasive(Map<String, Breakdown> invPasive) {
		this.invPasive = invPasive;
	}
	public Map<String, Breakdown> getOutputVatInvPasive() {
		return outputVatInvPasive;
	}
	public void setOutputVatInvPasive(Map<String, Breakdown> outputVatInvPasive) {
		this.outputVatInvPasive = outputVatInvPasive;
	}
	public double getBaseIntracommunitary() {
		return baseIntracommunitary;
	}
	public void setBaseIntracommunitary(double baseIntracommunitary) {
		this.baseIntracommunitary = baseIntracommunitary;
	}
	public double getQuotaIntracommunitary() {
		return quotaIntracommunitary;
	}
	public void setQuotaIntracommunitary(double quotaIntracommunitary) {
		this.quotaIntracommunitary = quotaIntracommunitary;
	}
	public double getBaseIntracommunitaryCT() {
		return baseIntracommunitaryCT;
	}
	public void setBaseIntracommunitaryCT(double baseIntracommunitaryCT) {
		this.baseIntracommunitaryCT = baseIntracommunitaryCT;
	}
	public double getQuotaIntracommunitaryCT() {
		return quotaIntracommunitaryCT;
	}
	public void setQuotaIntracommunitaryCT(double quotaIntracommunitaryCT) {
		this.quotaIntracommunitaryCT = quotaIntracommunitaryCT;
	}
	public double getBaseISPCT() {
		return baseISPCT;
	}
	public void setBaseISPCT(double baseISPCT) {
		this.baseISPCT = baseISPCT;
	}
	public double getQuotaISPCT() {
		return quotaISPCT;
	}
	public void setQuotaISPCT(double quotaISPCT) {
		this.quotaISPCT = quotaISPCT;
	}
	public double getBaseInvPasive() {
		return baseInvPasive;
	}
	public void setBaseInvPasive(double baseInvPasive) {
		this.baseInvPasive = baseInvPasive;
	}
	public double getQuotaInvPasive() {
		return quotaInvPasive;
	}
	public void setQuotaInvPasive(double quotaInvPasive) {
		this.quotaInvPasive = quotaInvPasive;
	}
	public double getBaseModifications() {
		return baseModifications;
	}
	public void setBaseModifications(double baseModifications) {
		this.baseModifications = baseModifications;
	}
	public double getQuotaModifications() {
		return quotaModifications;
	}
	public void setQuotaModifications(double quotaModifications) {
		this.quotaModifications = quotaModifications;
	}
	public double getBaseSurchrageModifications() {
		return baseSurchrageModifications;
	}
	public void setBaseSurchrageModifications(double baseSurchrageModifications) {
		this.baseSurchrageModifications = baseSurchrageModifications;
	}
	public double getQuotaSurchrageModifications() {
		return quotaSurchrageModifications;
	}
	public void setQuotaSurchrageModifications(double quotaSurchrageModifications) {
		this.quotaSurchrageModifications = quotaSurchrageModifications;
	}
	public double getOutputTotal() {
		return outputTotal;
	}
	public void setOutputTotal(double outputTotal) {
		this.outputTotal = outputTotal;
	}
	public double getInnerOperationsTotalBase() {
		return innerCommonOperationsBase + innerExpensesOperationsBase + innerInvestmentOperationsBase;
	}
	public double getInnerOperationsTotalQuota() {
		return innerCommonOperationsQuota + innerExpensesOperationsQuota + innerInvestmentOperationsQuota;
	}
	public double getInnerOperationsBase() {
		return innerCommonOperationsBase + innerExpensesOperationsBase;
	}
	public double getInnerOperationsQuota() {
		return innerCommonOperationsQuota + innerExpensesOperationsQuota;
	}
	public double getInnerCommonOperationsBase() {
		return innerCommonOperationsBase;
	}
	public void setInnerCommonOperationsBase(double innerCommonOperationsBase) {
		this.innerCommonOperationsBase = innerCommonOperationsBase;
	}
	public double getInnerCommonOperationsQuota() {
		return innerCommonOperationsQuota;
	}
	public void setInnerCommonOperationsQuota(double innerCommonOperationsQuota) {
		this.innerCommonOperationsQuota = innerCommonOperationsQuota;
	}
	public double getInnerInvestmentOperationsBase() {
		return innerInvestmentOperationsBase;
	}
	public void setInnerInvestmentOperationsBase(double innerInvestmentOperationsBase) {
		this.innerInvestmentOperationsBase = innerInvestmentOperationsBase;
	}
	public double getInnerInvestmentOperationsQuota() {
		return innerInvestmentOperationsQuota;
	}
	public void setInnerInvestmentOperationsQuota(double innerInvestmentOperationsQuota) {
		this.innerInvestmentOperationsQuota = innerInvestmentOperationsQuota;
	}
	public double getInnerExpensesOperationsBase() {
		return innerExpensesOperationsBase;
	}
	public void setInnerExpensesOperationsBase(double innerExpensesOperationsBase) {
		this.innerExpensesOperationsBase = innerExpensesOperationsBase;
	}
	public double getInnerExpensesOperationsQuota() {
		return innerExpensesOperationsQuota;
	}
	public void setInnerExpensesOperationsQuota(double innerExpensesOperationsQuota) {
		this.innerExpensesOperationsQuota = innerExpensesOperationsQuota;
	}
	public double getImportedOperationsTotalBase() {
		return importedCommonOperationsBase + importedInvestmentOperationsBase;
	}
	public double getImportedOperationsTotalQuota() {
		return importedCommonOperationsQuota + importedInvestmentOperationsQuota;
	}
	public double getImportedCommonOperationsBase() {
		return importedCommonOperationsBase;
	}
	public void setImportedCommonOperationsBase(double importedCommonOperationsBase) {
		this.importedCommonOperationsBase = importedCommonOperationsBase;
	}
	public double getImportedCommonOperationsQuota() {
		return importedCommonOperationsQuota;
	}
	public void setImportedCommonOperationsQuota(double importedCommonOperationsQuota) {
		this.importedCommonOperationsQuota = importedCommonOperationsQuota;
	}
	public double getImportedInvestmentOperationsBase() {
		return importedInvestmentOperationsBase;
	}
	public void setImportedInvestmentOperationsBase(double importedInvestmentOperationsBase) {
		this.importedInvestmentOperationsBase = importedInvestmentOperationsBase;
	}
	public double getImportedInvestmentOperationsQuota() {
		return importedInvestmentOperationsQuota;
	}
	public void setImportedInvestmentOperationsQuota(double importedInvestmentOperationsQuota) {
		this.importedInvestmentOperationsQuota = importedInvestmentOperationsQuota;
	}
	
	public double getIntracommunitaryOperationsBase() {
		return intracommunitaryCommonOperationsBase + intracommunitaryExpensesOperationsBase;
	}
	public double getIntracommunitaryOperationsQuota() {
		return intracommunitaryCommonOperationsQuota + intracommunitaryExpensesOperationsQuota;
	}

	public double getIntracommunitaryOperationsTotalBase() {
		return intracommunitaryCommonOperationsBase + intracommunitaryExpensesOperationsBase + intracommunitaryInvestmentOperationsBase;
	}
	public double getIntracommunitaryOperationsTotalQuota() {
		return intracommunitaryCommonOperationsQuota + intracommunitaryExpensesOperationsQuota + intracommunitaryInvestmentOperationsQuota;
	}

	public double getIntracommunitaryOperationsCTBase() {
		return intracommunitaryCommonOperationsBase + intracommunitaryExpensesOperationsBase;
	}
	public double getIntracommunitaryOperationsCTQuota() {
		return intracommunitaryCommonOperationsQuota + intracommunitaryExpensesOperationsQuota;
	}

	public double getIntracommunitaryCommonOperationsBase() {
		return intracommunitaryCommonOperationsBase;
	}
	public void setIntracommunitaryCommonOperationsBase(double intracommunitaryCommonOperationsBase) {
		this.intracommunitaryCommonOperationsBase = intracommunitaryCommonOperationsBase;
	}
	public double getIntracommunitaryCommonOperationsQuota() {
		return intracommunitaryCommonOperationsQuota;
	}
	public void setIntracommunitaryCommonOperationsQuota(double intracommunitaryCommonOperationsQuota) {
		this.intracommunitaryCommonOperationsQuota = intracommunitaryCommonOperationsQuota;
	}
	public double getIntracommunitaryInvestmentOperationsBase() {
		return intracommunitaryInvestmentOperationsBase;
	}
	public void setIntracommunitaryInvestmentOperationsBase(double intracommunitaryInvestmentOperationsBase) {
		this.intracommunitaryInvestmentOperationsBase = intracommunitaryInvestmentOperationsBase;
	}
	public double getIntracommunitaryInvestmentOperationsQuota() {
		return intracommunitaryInvestmentOperationsQuota;
	}
	public void setIntracommunitaryInvestmentOperationsQuota(double intracommunitaryInvestmentOperationsQuota) {
		this.intracommunitaryInvestmentOperationsQuota = intracommunitaryInvestmentOperationsQuota;
	}
	public double getIntracommunitaryExpensesOperationsBase() {
		return intracommunitaryExpensesOperationsBase;
	}
	public void setIntracommunitaryExpensesOperationsBase(double intracommunitaryExpensesOperationsBase) {
		this.intracommunitaryExpensesOperationsBase = intracommunitaryExpensesOperationsBase;
	}
	public double getIntracommunitaryExpensesOperationsQuota() {
		return intracommunitaryExpensesOperationsQuota;
	}
	public void setIntracommunitaryExpensesOperationsQuota(double intracommunitaryExpensesOperationsQuota) {
		this.intracommunitaryExpensesOperationsQuota = intracommunitaryExpensesOperationsQuota;
	}
	public double getDeductionRestificationBase() {
		return deductionRestificationBase;
	}
	public void setDeductionRestificationBase(double deductionRestificationBase) {
		this.deductionRestificationBase = deductionRestificationBase;
	}
	public double getDeductionRestificationQuota() {
		return deductionRestificationQuota;
	}
	public void setDeductionRestificationQuota(double deductionRestificationQuota) {
		this.deductionRestificationQuota = deductionRestificationQuota;
	}
	public double getAgriculturalRegimeCompensation() {
		return agriculturalRegimeCompensation;
	}
	public void setAgriculturalRegimeCompensation(double agriculturalRegimeCompensation) {
		this.agriculturalRegimeCompensation = agriculturalRegimeCompensation;
	}
	public double getInvestmentNormalization() {
		return investmentNormalization;
	}
	public void setInvestmentNormalization(double investmentNormalization) {
		this.investmentNormalization = investmentNormalization;
	}
	public double getProrataNormalization() {
		return prorataNormalization;
	}
	public void setProrataNormalization(double prorataNormalization) {
		this.prorataNormalization = prorataNormalization;
	}
	public double getDeductTotal() {
		return deductTotal;
	}
	public void setDeductTotal(double deductTotal) {
		this.deductTotal = deductTotal;
	}
	public double getDifference() {
		return difference;
	}
	public void setDifference(double difference) {
		this.difference = difference;
	}
	public double getIntracommunitaryDeliveries() {
		return intracommunitaryDeliveries;
	}
	public void setIntracommunitaryDeliveries(double intracommunitaryDeliveries) {
		this.intracommunitaryDeliveries = intracommunitaryDeliveries;
	}
	public double getIntracommunitaryServiceDeliveries() {
		return intracommunitaryServiceDeliveries;
	}
	public void setIntracommunitaryServiceDeliveries(
			double intracommunitaryServiceDeliveries) {
		this.intracommunitaryServiceDeliveries = intracommunitaryServiceDeliveries;
	}
	public double getIntracommunitaryDeliveriesCT() {
		return CommonUtil.round( getIntracommunitaryDeliveries() + getIntracommunitaryServiceDeliveries() );
	}
	public double getExportationTotal() {
		return exportationTotal;
	}
	public void setExportationTotal(double exportationTotal) {
		this.exportationTotal = exportationTotal;
	}
	public double getNonTaxableTotal() {
		return nonTaxableTotal;
	}
	public void setNonTaxableTotal(double nonTaxableTotal) {
		this.nonTaxableTotal = nonTaxableTotal;
	}
	public double getInvSujPasNotIncluded() {
		return invSujPasNotIncluded;
	}
	public void setInvSujPasNotIncluded(double invSujPasNotIncluded) {
		this.invSujPasNotIncluded = invSujPasNotIncluded;
	}
	public double getPresIntraServices() {
		return presIntraServices;
	}
	public void setPresIntraServices(double presIntraServices) {
		this.presIntraServices = presIntraServices;
	}
	
	public double getVatAccrualInputBase() {
		return vatAccrualInputBase;
	}
	public void setVatAccrualInputBase(double vatAccrualInputBase) {
		this.vatAccrualInputBase = vatAccrualInputBase;
	}
	public double getVatAccrualInputQuota() {
		return vatAccrualInputQuota;
	}
	public void setVatAccrualInputQuota(double vatAccrualInputQuota) {
		this.vatAccrualInputQuota = vatAccrualInputQuota;
	}
	public double getVatAccrualOutputBase() {
		return vatAccrualOutputBase;
	}
	public void setVatAccrualOutputBase(double vatAccrualOutputBase) {
		this.vatAccrualOutputBase = vatAccrualOutputBase;
	}
	public double getVatAccrualOutputQuota() {
		return vatAccrualOutputQuota;
	}
	public void setVatAccrualOutputQuota(double vatAccrualOutputQuota) {
		this.vatAccrualOutputQuota = vatAccrualOutputQuota;
	}
	public double getAlavaPercent() {
		return alavaPercent;
	}
	public void setAlavaPercent(double alavaPercent) {
		this.alavaPercent = alavaPercent;
	}
	public double getGipuzkoaPercent() {
		return gipuzkoaPercent;
	}
	public void setGipuzkoaPercent(double gipuzkoaPercent) {
		this.gipuzkoaPercent = gipuzkoaPercent;
	}
	public double getBizkaiaPercent() {
		return bizkaiaPercent;
	}
	public void setBizkaiaPercent(double bizkaiaPercent) {
		this.bizkaiaPercent = bizkaiaPercent;
	}
	public double getNavarraPercent() {
		return navarraPercent;
	}
	public void setNavarraPercent(double navarraPercent) {
		this.navarraPercent = navarraPercent;
	}
	public double getCommonTerritoryPercent() {
		return commonTerritoryPercent;
	}
	public void setCommonTerritoryPercent(double commonTerritoryPercent) {
		this.commonTerritoryPercent = commonTerritoryPercent;
	}
	public double getRegularizationResult() {
		return regularizationResult;
	}
	public void setRegularizationResult(double regularizationResult) {
		this.regularizationResult = regularizationResult;
	}
	public double getToDeduct() {
		return toDeduct;
	}
	public void setToDeduct(double toDeduct) {
		this.toDeduct = toDeduct;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	public double getPreviousYearCompensateQuota() {
		return previousYearCompensateQuota;
	}
	public void setPreviousYearCompensateQuota(double previousYearCompensateQuota) {
		this.previousYearCompensateQuota = previousYearCompensateQuota;
	}
	public double getExtraCharge() {
		return extraCharge;
	}
	public void setExtraCharge(double extraCharge) {
		this.extraCharge = extraCharge;
	}
	public double getDelayInterest() {
		return delayInterest;
	}
	public void setDelayInterest(double delayInterest) {
		this.delayInterest = delayInterest;
	}
	public double getDeposit() {
		return deposit;
	}
	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}
	public double getCompensate() {
		return compensate;
	}
	public void setCompensate(double compensate) {
		this.compensate = compensate;
	}
	public double getPayBack() {
		return payBack;
	}
	public void setPayBack(double payBack) {
		this.payBack = payBack;
	}
	public double getPreviousPayBack() {
		return previousPayBack;
	}
	public void setPreviousPayBack(double previousPayBack) {
		this.previousPayBack = previousPayBack;
	}
	public double getPreviousDeposit() {
		return previousDeposit;
	}
	public void setPreviousDeposit(double previousDeposit) {
		this.previousDeposit = previousDeposit;
	}
	public double getTotalDebt() {
		return totalDebt;
	}
	public void setTotalDebt(double totalDebt) {
		this.totalDebt = totalDebt;
	}
	public boolean isWithoutActivity() {
		return withoutActivity;
	}
	public void setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
	}
	public int getWithoutActivityNumber() {
		return isWithoutActivity()?1:0;
	}
	public String getWithoutActivityString() {
		return isWithoutActivity()?"1":" ";
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getDepositBankAccount() {
		return depositBankAccount;
	}
	public void setDepositBankAccount(String depositBankAccount) {
		this.depositBankAccount = depositBankAccount;
	}
	

	public String getPayBackBankAccount() {
		return payBackBankAccount;
	}
	public void setPayBackBankAccount(String payBackBankAccount) {
		this.payBackBankAccount = payBackBankAccount;
	}
	
	public Map<String, Breakdown> getInnerAssetPurchases() {
		return innerAssetPurchases;
	}
	public void setInnerAssetPurchases(Map<String, Breakdown> innerAssetPurchases) {
		this.innerAssetPurchases = innerAssetPurchases;
	}
	public double getBaseInnerAssetPurchases() {
		return baseInnerAssetPurchases;
	}
	public void setBaseInnerAssetPurchases(double baseInnerAssetPurchases) {
		this.baseInnerAssetPurchases = baseInnerAssetPurchases;
	}
	public double getQuotaInnerAssetPurchases() {
		return quotaInnerAssetPurchases;
	}
	public void setQuotaInnerAssetPurchases(double quotaInnerAssetPurchases) {
		this.quotaInnerAssetPurchases = quotaInnerAssetPurchases;
	}
	public double getDeductibleQuotaInnerAssetPurchases() {
		return deductibleQuotaInnerAssetPurchases;
	}
	public void setDeductibleQuotaInnerAssetPurchases(double deductibleQuotaInnerAssetPurchases) {
		this.deductibleQuotaInnerAssetPurchases = deductibleQuotaInnerAssetPurchases;
	}
	public Map<String, Breakdown> getExpenses() {
		return expenses;
	}
	public void setExpenses(Map<String, Breakdown> expenses) {
		this.expenses = expenses;
	}
	public double getBaseExpenses() {
		return baseExpenses;
	}
	public void setBaseExpenses(double baseExpenses) {
		this.baseExpenses = baseExpenses;
	}
	public double getQuotaExpenses() {
		return quotaExpenses;
	}
	public void setQuotaExpenses(double quotaExpenses) {
		this.quotaExpenses = quotaExpenses;
	}
	public double getDeductibleQuotaExpenses() {
		return deductibleQuotaExpenses;
	}
	public void setDeductibleQuotaExpenses(double deductibleQuotaExpenses) {
		this.deductibleQuotaExpenses = deductibleQuotaExpenses;
	}
	public Map<String, Breakdown> getInvestmentAsset() {
		return investmentAsset;
	}
	public void setInvestmentAsset(Map<String, Breakdown> investmentAsset) {
		this.investmentAsset = investmentAsset;
	}
	public double getBaseInvestmentAsset() {
		return baseInvestmentAsset;
	}
	public void setBaseInvestmentAsset(double baseInvestmentAsset) {
		this.baseInvestmentAsset = baseInvestmentAsset;
	}
	public double getQuotaInvestmentAsset() {
		return quotaInvestmentAsset;
	}
	public void setQuotaInvestmentAsset(double quotaInvestmentAsset) {
		this.quotaInvestmentAsset = quotaInvestmentAsset;
	}
	public double getDeductibleQuotaInvestmentAsset() {
		return deductibleQuotaInvestmentAsset;
	}
	public void setDeductibleQuotaInvestmentAsset(double deductibleQuotaInvestmentAsset) {
		this.deductibleQuotaInvestmentAsset = deductibleQuotaInvestmentAsset;
	}
	public double getBaseTotalAddInfo() {
		return baseTotalAddInfo;
	}
	public void setBaseTotalAddInfo(double baseTotalAddInfo) {
		this.baseTotalAddInfo = baseTotalAddInfo;
	}
	public double getQuotaTotalAddInfo() {
		return quotaTotalAddInfo;
	}
	public void setQuotaTotalAddInfo(double quotaTotalAddInfo) {
		this.quotaTotalAddInfo = quotaTotalAddInfo;
	}
	public double getDeductibleQuotaTotalAddInfo() {
		return deductibleQuotaTotalAddInfo;
	}
	public void setDeductibleQuotaTotalAddInfo(double deductibleQuotaTotalAddInfo) {
		this.deductibleQuotaTotalAddInfo = deductibleQuotaTotalAddInfo;
	}
	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
	
	public void changeInvalidCharacters() {
		setPeriod(changeInvalidCharacters(getPeriod()));
		setBankName(changeInvalidCharacters(getBankName()));
		setCcc(changeInvalidCharacters(getCcc()));	
		setDocument(changeInvalidCharacters(getDocument()));
		setName(changeInvalidCharacters(getName()));
		setSurname(changeInvalidCharacters(getSurname()));
		setAddress(changeInvalidCharacters(getAddress()));
		setEntity(changeInvalidCharacters(getEntity()));
		setCity(changeInvalidCharacters(getCity()));
		setProvinceID(changeInvalidCharacters(getProvinceID()));
		setProvince(changeInvalidCharacters(getProvince()));
		setTelephone(changeInvalidCharacters(getTelephone()));
		setFax(changeInvalidCharacters(getFax()));
		setEmail(changeInvalidCharacters(getEmail()));
		setTodayMonth(changeInvalidCharacters(getTodayMonth()));
		setBankAccount(changeInvalidCharacters(getBankAccount()));
		setDepositBankAccount(changeInvalidCharacters(getDepositBankAccount()));
		setPayBackBankAccount(changeInvalidCharacters(getPayBackBankAccount()));
	}
	
	public String changeInvalidCharacters(String token) {
		char[] seek  = new char[]{'á','é','í','ó','ú','Á','É','Í','Ó','Ú','º','ª'};
		char[] alter = new char[]{'a','e','i','o','u','A','E','I','O','U',' ',' '};
		if (StringUtils.isNotBlank(token)) {
			for (int i = 0; i < seek.length ; i ++) {
				token = StringUtils.replaceChars(token, seek[i], alter[i]);
			}
		}
		return token;
	}
}
