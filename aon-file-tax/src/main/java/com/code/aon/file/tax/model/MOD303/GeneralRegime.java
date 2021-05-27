package com.code.aon.file.tax.model.MOD303;

import java.util.Map;
import java.util.TreeMap;

public class GeneralRegime {

	
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
	private double result;


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
	public double getResult() {
		return result;
	}
	public void setResult(double result) {
		this.result = result;
	}
	
}
