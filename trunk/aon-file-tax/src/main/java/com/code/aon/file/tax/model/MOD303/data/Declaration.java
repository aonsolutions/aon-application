package com.code.aon.file.tax.model.MOD303.data;

import org.apache.commons.lang.StringUtils;


public class Declaration {

	private Integer year;
	private String period;
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
	private double baseIntracommunitary;
	private double quotaIntracommunitary;
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
	private double agriculturalRegimeCompensation;
	private double investmentNormalization;
	private double prorataNormalization;
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
	private String depositBankEntity;
	private String depositBankOffice;
	private String depositBankControl;
	private String depositBankAccount;
	private String payBackBankEntity;
	private String payBackBankOffice;
	private String payBackBankControl;
	private String payBackBankAccount;

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getPeriod() {
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
		return intracommunitaryCommonOperationsBase + + intracommunitaryExpensesOperationsBase + intracommunitaryInvestmentOperationsBase;
	}
	public double getIntracommunitaryOperationsTotalQuota() {
		return intracommunitaryCommonOperationsQuota + intracommunitaryExpensesOperationsQuota + intracommunitaryInvestmentOperationsQuota;
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
	public int getWithoutActivityNumber() {
		return isWithoutActivity()?1:0;
	}
	public String getWithoutActivityString() {
		return isWithoutActivity()?"1":" ";
	}
	
	public String getDepositBankEntity() {
		return depositBankEntity;
	}
	public void setDepositBankEntity(String depositBankEntity) {
		this.depositBankEntity = depositBankEntity;
	}
	public String getDepositBankOffice() {
		return depositBankOffice;
	}
	public void setDepositBankOffice(String depositBankOffice) {
		this.depositBankOffice = depositBankOffice;
	}
	public String getDepositBankControl() {
		return depositBankControl;
	}
	public void setDepositBankControl(String depositBankControl) {
		this.depositBankControl = depositBankControl;
	}
	public String getDepositBankAccount() {
		return depositBankAccount;
	}
	public void setDepositBankAccount(String depositBankAccount) {
		this.depositBankAccount = depositBankAccount;
	}

	public String getPayBackBankEntity() {
		return payBackBankEntity;
	}
	public void setPayBackBankEntity(String payBackBankEntity) {
		this.payBackBankEntity = payBackBankEntity;
	}
	public String getPayBackBankOffice() {
		return payBackBankOffice;
	}
	public void setPayBackBankOffice(String payBackBankOffice) {
		this.payBackBankOffice = payBackBankOffice;
	}
	public String getPayBackBankControl() {
		return payBackBankControl;
	}
	public void setPayBackBankControl(String payBackBankControl) {
		this.payBackBankControl = payBackBankControl;
	}
	public String getPayBackBankAccount() {
		return payBackBankAccount;
	}
	public void setPayBackBankAccount(String payBackBankAccount) {
		this.payBackBankAccount = payBackBankAccount;
	}
	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
}
