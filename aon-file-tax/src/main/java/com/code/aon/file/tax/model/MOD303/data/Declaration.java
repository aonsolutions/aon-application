package com.code.aon.file.tax.model.MOD303.data;


public class Declaration {

	private Integer period;
	private boolean replacement;
	private boolean complementary;
	private boolean taxRefundRegistry;
	private String ccc1;
	private String ccc2;
	private String ccc3;
	private String ccc4;

	private String document;
	private Integer startPeriod;
	private Integer endPeriod;
	private Double result;
	private String name;
	private String address;
	private Integer addressNumber;
	private String entity;
	private String city;
	private String province;
	private Integer zip;
	private String telephone;
	private String fax;
	private String email;
	
	private double baseOutputVat4;
	private double percentOutputVat4;
	private double quotaOutputVat4;
	private double baseOutputVat8;
	private double percentOutputVat8;
	private double quotaOutputVat8;
	private double baseOutputVat18;
	private double percentOutputVat18;
	private double quotaOutputVat18;
	private double baseOutputVat7;
	private double percentOutputVat7;
	private double quotaOutputVat7;
	private double baseOutputVat16;
	private double percentOutputVat16;
	private double quotaOutputVat16;
	private double baseSurcharge05;
	private double percentSurcharge05;
	private double quotaSurcharge05;
	private double baseSurcharge1;
	private double percentSurcharge1;
	private double quotaSurcharge1;
	private double baseSurcharge4;
	private double percentSurcharge4;
	private double quotaSurcharge4;
	private double baseIntracommunitary4;
	private double percentIntracommunitary4;
	private double quotaIntracommunitary4;
	private double baseIntracommunitary8;
	private double percentIntracommunitary8;
	private double quotaIntracommunitary8;
	private double baseIntracommunitary18;
	private double percentIntracommunitary18;
	private double quotaIntracommunitary18;
	private double baseIntracommunitary7;
	private double percentIntracommunitary7;
	private double quotaIntracommunitary7;
	private double baseIntracommunitary16;
	private double percentIntracommunitary16;
	private double quotaIntracommunitary16;
	private double outputTotal;
	private double innerCommonOperations;
	private double innerInvestmentOperations;
	private double innerExpensesOperations;
	private double importedCommonOperations;
	private double importedInvestmentOperations;
	private double intracommunitaryCommonOperations;
	private double investmentCommonOperations;
	private double agriculturalRegimeCompensation;
	private double investmentNormalization;
	private double deductTotal;
	private double difference;
	private double intracommunitaryDeliveries;
	private double exportationTotal;
	private double nonTaxableTotal;
	
	private double alavaPercent; 
	private double gipuzkoaPercent; 
	private double bizkaiaPercent; 
	private double navarraPercent; 
	private double commonTerritoryPercent;
	private double quota;
	private double previousYearCompensateQuota;
	private double extraCharge;
	private double delayInterest;
	private double deposit;
	private double compensate;
	private double payBack;
	private boolean withoutActivity;
	private String bankEntity;
	private String bankOffice;
	private String bankControl;
	private String bankAccount;
	
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
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
	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}
	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
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
	public String getCcc1() {
		return ccc1;
	}
	public void setCcc1(String ccc1) {
		this.ccc1 = ccc1;
	}
	public String getCcc2() {
		return ccc2;
	}
	public void setCcc2(String ccc2) {
		this.ccc2 = ccc2;
	}
	public String getCcc3() {
		return ccc3;
	}
	public void setCcc3(String ccc3) {
		this.ccc3 = ccc3;
	}
	public String getCcc4() {
		return ccc4;
	}
	public void setCcc4(String ccc4) {
		this.ccc4 = ccc4;
	}
	
	public double getBaseOutputVat4() {
		return baseOutputVat4;
	}
	public void setBaseOutputVat4(double baseOutputVat4) {
		this.baseOutputVat4 = baseOutputVat4;
	}
	public double getPercentOutputVat4() {
		return percentOutputVat4;
	}
	public void setPercentOutputVat4(double percentOutputVat4) {
		this.percentOutputVat4 = percentOutputVat4;
	}
	public double getQuotaOutputVat4() {
		return quotaOutputVat4;
	}
	public void setQuotaOutputVat4(double quotaOutputVat4) {
		this.quotaOutputVat4 = quotaOutputVat4;
	}
	public double getBaseOutputVat8() {
		return baseOutputVat8;
	}
	public void setBaseOutputVat8(double baseOutputVat8) {
		this.baseOutputVat8 = baseOutputVat8;
	}
	public double getPercentOutputVat8() {
		return percentOutputVat8;
	}
	public void setPercentOutputVat8(double percentOutputVat8) {
		this.percentOutputVat8 = percentOutputVat8;
	}
	public double getQuotaOutputVat8() {
		return quotaOutputVat8;
	}
	public void setQuotaOutputVat8(double quotaOutputVat8) {
		this.quotaOutputVat8 = quotaOutputVat8;
	}
	public double getBaseOutputVat18() {
		return baseOutputVat18;
	}
	public void setBaseOutputVat18(double baseOutputVat18) {
		this.baseOutputVat18 = baseOutputVat18;
	}
	public double getPercentOutputVat18() {
		return percentOutputVat18;
	}
	public void setPercentOutputVat18(double percentOutputVat18) {
		this.percentOutputVat18 = percentOutputVat18;
	}
	public double getQuotaOutputVat18() {
		return quotaOutputVat18;
	}
	public void setQuotaOutputVat18(double quotaOutputVat18) {
		this.quotaOutputVat18 = quotaOutputVat18;
	}
	public double getBaseOutputVat7() {
		return baseOutputVat7;
	}
	public void setBaseOutputVat7(double baseOutputVat7) {
		this.baseOutputVat7 = baseOutputVat7;
	}
	public double getPercentOutputVat7() {
		return percentOutputVat7;
	}
	public void setPercentOutputVat7(double percentOutputVat7) {
		this.percentOutputVat7 = percentOutputVat7;
	}
	public double getQuotaOutputVat7() {
		return quotaOutputVat7;
	}
	public void setQuotaOutputVat7(double quotaOutputVat7) {
		this.quotaOutputVat7 = quotaOutputVat7;
	}
	public double getBaseOutputVat16() {
		return baseOutputVat16;
	}
	public void setBaseOutputVat16(double baseOutputVat16) {
		this.baseOutputVat16 = baseOutputVat16;
	}
	public double getPercentOutputVat16() {
		return percentOutputVat16;
	}
	public void setPercentOutputVat16(double percentOutputVat16) {
		this.percentOutputVat16 = percentOutputVat16;
	}
	public double getQuotaOutputVat16() {
		return quotaOutputVat16;
	}
	public void setQuotaOutputVat16(double quotaOutputVat16) {
		this.quotaOutputVat16 = quotaOutputVat16;
	}
	public double getBaseSurcharge05() {
		return baseSurcharge05;
	}
	public void setBaseSurcharge05(double baseSurcharge05) {
		this.baseSurcharge05 = baseSurcharge05;
	}
	public double getPercentSurcharge05() {
		return percentSurcharge05;
	}
	public void setPercentSurcharge05(double percentSurcharge05) {
		this.percentSurcharge05 = percentSurcharge05;
	}
	public double getQuotaSurcharge05() {
		return quotaSurcharge05;
	}
	public void setQuotaSurcharge05(double quotaSurcharge05) {
		this.quotaSurcharge05 = quotaSurcharge05;
	}
	public double getBaseSurcharge1() {
		return baseSurcharge1;
	}
	public void setBaseSurcharge1(double baseSurcharge1) {
		this.baseSurcharge1 = baseSurcharge1;
	}
	public double getPercentSurcharge1() {
		return percentSurcharge1;
	}
	public void setPercentSurcharge1(double percentSurcharge1) {
		this.percentSurcharge1 = percentSurcharge1;
	}
	public double getQuotaSurcharge1() {
		return quotaSurcharge1;
	}
	public void setQuotaSurcharge1(double quotaSurcharge1) {
		this.quotaSurcharge1 = quotaSurcharge1;
	}
	public double getBaseSurcharge4() {
		return baseSurcharge4;
	}
	public void setBaseSurcharge4(double baseSurcharge4) {
		this.baseSurcharge4 = baseSurcharge4;
	}
	public double getPercentSurcharge4() {
		return percentSurcharge4;
	}
	public void setPercentSurcharge4(double percentSurcharge4) {
		this.percentSurcharge4 = percentSurcharge4;
	}
	public double getQuotaSurcharge4() {
		return quotaSurcharge4;
	}
	public void setQuotaSurcharge4(double quotaSurcharge4) {
		this.quotaSurcharge4 = quotaSurcharge4;
	}
	public double getBaseIntracommunitary4() {
		return baseIntracommunitary4;
	}
	public void setBaseIntracommunitary4(double baseIntracommunitary4) {
		this.baseIntracommunitary4 = baseIntracommunitary4;
	}
	public double getPercentIntracommunitary4() {
		return percentIntracommunitary4;
	}
	public void setPercentIntracommunitary4(double percentIntracommunitary4) {
		this.percentIntracommunitary4 = percentIntracommunitary4;
	}
	public double getQuotaIntracommunitary4() {
		return quotaIntracommunitary4;
	}
	public void setQuotaIntracommunitary4(double quotaIntracommunitary4) {
		this.quotaIntracommunitary4 = quotaIntracommunitary4;
	}
	public double getBaseIntracommunitary8() {
		return baseIntracommunitary8;
	}
	public void setBaseIntracommunitary8(double baseIntracommunitary8) {
		this.baseIntracommunitary8 = baseIntracommunitary8;
	}
	public double getPercentIntracommunitary8() {
		return percentIntracommunitary8;
	}
	public void setPercentIntracommunitary8(double percentIntracommunitary8) {
		this.percentIntracommunitary8 = percentIntracommunitary8;
	}
	public double getQuotaIntracommunitary8() {
		return quotaIntracommunitary8;
	}
	public void setQuotaIntracommunitary8(double quotaIntracommunitary8) {
		this.quotaIntracommunitary8 = quotaIntracommunitary8;
	}
	public double getBaseIntracommunitary18() {
		return baseIntracommunitary18;
	}
	public void setBaseIntracommunitary18(double baseIntracommunitary18) {
		this.baseIntracommunitary18 = baseIntracommunitary18;
	}
	public double getPercentIntracommunitary18() {
		return percentIntracommunitary18;
	}
	public void setPercentIntracommunitary18(double percentIntracommunitary18) {
		this.percentIntracommunitary18 = percentIntracommunitary18;
	}
	public double getQuotaIntracommunitary18() {
		return quotaIntracommunitary18;
	}
	public void setQuotaIntracommunitary18(double quotaIntracommunitary18) {
		this.quotaIntracommunitary18 = quotaIntracommunitary18;
	}
	public double getBaseIntracommunitary7() {
		return baseIntracommunitary7;
	}
	public void setBaseIntracommunitary7(double baseIntracommunitary7) {
		this.baseIntracommunitary7 = baseIntracommunitary7;
	}
	public double getPercentIntracommunitary7() {
		return percentIntracommunitary7;
	}
	public void setPercentIntracommunitary7(double percentIntracommunitary7) {
		this.percentIntracommunitary7 = percentIntracommunitary7;
	}
	public double getQuotaIntracommunitary7() {
		return quotaIntracommunitary7;
	}
	public void setQuotaIntracommunitary7(double quotaIntracommunitary7) {
		this.quotaIntracommunitary7 = quotaIntracommunitary7;
	}
	public double getBaseIntracommunitary16() {
		return baseIntracommunitary16;
	}
	public void setBaseIntracommunitary16(double baseIntracommunitary16) {
		this.baseIntracommunitary16 = baseIntracommunitary16;
	}
	public double getPercentIntracommunitary16() {
		return percentIntracommunitary16;
	}
	public void setPercentIntracommunitary16(double percentIntracommunitary16) {
		this.percentIntracommunitary16 = percentIntracommunitary16;
	}
	public double getQuotaIntracommunitary16() {
		return quotaIntracommunitary16;
	}
	public void setQuotaIntracommunitary16(double quotaIntracommunitary16) {
		this.quotaIntracommunitary16 = quotaIntracommunitary16;
	}
	public double getOutputTotal() {
		return outputTotal;
	}
	public void setOutputTotal(double outputTotal) {
		this.outputTotal = outputTotal;
	}
	public double getInnerCommonOperations() {
		return innerCommonOperations;
	}
	public void setInnerCommonOperations(double innerCommonOperations) {
		this.innerCommonOperations = innerCommonOperations;
	}
	public double getInnerInvestmentOperations() {
		return innerInvestmentOperations;
	}
	public void setInnerInvestmentOperations(double innerInvestmentOperations) {
		this.innerInvestmentOperations = innerInvestmentOperations;
	}
	public double getInnerExpensesOperations() {
		return innerExpensesOperations;
	}
	public void setInnerExpensesOperations(double innerExpensesOperations) {
		this.innerExpensesOperations = innerExpensesOperations;
	}
	public double getImportedCommonOperations() {
		return importedCommonOperations;
	}
	public void setImportedCommonOperations(double importedCommonOperations) {
		this.importedCommonOperations = importedCommonOperations;
	}
	public double getImportedInvestmentOperations() {
		return importedInvestmentOperations;
	}
	public void setImportedInvestmentOperations(double importedInvestmentOperations) {
		this.importedInvestmentOperations = importedInvestmentOperations;
	}
	public double getIntracommunitaryCommonOperations() {
		return intracommunitaryCommonOperations;
	}
	public void setIntracommunitaryCommonOperations(double intracommunitaryCommonOperations) {
		this.intracommunitaryCommonOperations = intracommunitaryCommonOperations;
	}
	public double getInvestmentCommonOperations() {
		return investmentCommonOperations;
	}
	public void setInvestmentCommonOperations(double investmentCommonOperations) {
		this.investmentCommonOperations = investmentCommonOperations;
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
	public boolean isWithoutActivity() {
		return withoutActivity;
	}
	public void setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
	}
	public String getBankEntity() {
		return bankEntity;
	}
	public void setBankEntity(String bankEntity) {
		this.bankEntity = bankEntity;
	}
	public String getBankOffice() {
		return bankOffice;
	}
	public void setBankOffice(String bankOffice) {
		this.bankOffice = bankOffice;
	}
	public String getBankControl() {
		return bankControl;
	}
	public void setBankControl(String bankControl) {
		this.bankControl = bankControl;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
}
