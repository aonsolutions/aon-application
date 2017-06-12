package com.code.aon.file.tax.model.MOD303;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.file.tax.FileTaxUtil;

public class Declaration {

	private Integer year;
	private String period;
	private boolean replacement;
	private boolean complementary;
	private String replacedNumber;
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
	private Double result0;
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
	
	private double intracommunitaryDeliveries;
	private double intracommunitaryServiceDeliveries;
	private double exportationTotal;
	private double nonTaxableTotal;
	private double invSujPasNotIncluded;
	private double presIntraServices;
	private double difference;
	
	private int iae1Key;
	private String iae1Epigraph;
	private int iae2Key;
	private String iae2Epigraph;
	private int iae3Key;
	private String iae3Epigraph;
	private int iae4Key;
	private String iae4Epigraph;
	private int iae5Key;
	private String iae5Epigraph;
	private int iae6Key;
	private String iae6Epigraph;
	
	private String mod347;
	
	private double c80;
	private double c81;
	private double c82;
	private double c83;
	private double c84;
	private double c85;
	private double c86;
	private double c87;
	private double c88;

	private GeneralRegime generalRegime;
	private SimplifiedRegime simplifiedRegime;

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
	public String getPeriodForGipuzkoa() {
		if ("01".equals(period)) return "01";
		if ("02".equals(period)) return "02";
		if ("03".equals(period)) return "03";
		if ("04".equals(period)) return "04";
		if ("05".equals(period)) return "05";
		if ("06".equals(period)) return "06";
		if ("07".equals(period)) return "07";
		if ("08".equals(period)) return "08";
		if ("09".equals(period)) return "09";
		if ("10".equals(period)) return "10";
		if ("11".equals(period)) return "11";
		if ("12".equals(period)) return "12";
		if ("T1".equals(period)) return "01";
		if ("T2".equals(period)) return "02";
		if ("T3".equals(period)) return "03";
		if ("T4".equals(period)) return "04"; // ¿?
		if ("An".equals(period)) return "  "; // ¿?
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
	public String getReplacedNumber() {
		return this.replacedNumber;
	}
	public void setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
	}
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}
	public String getComplementaryLetter() {
		return isComplementary()?"X":" ";
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
		return isSimplRegimeOnly()?1:3;
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
	}
	public int getVatAccrualRegimeNumber() {
		return isVatAccrualRegime()?1:2;
	}
	public boolean isVatAccrualRegimeReceiver() {
		return vatAccrualRegimeReceiver;
	}
	public void setVatAccrualRegimeReceiver(boolean vatAccrualRegimeReceiver) {
		this.vatAccrualRegimeReceiver = vatAccrualRegimeReceiver;
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
	public Double getResult() {
		return result;
	}
	public void setResult(Double result) {
		this.result = result;
	}
	public Double getResult0() {
		return result0;
	}
	public void setResult0(Double result0) {
		this.result0 = result0;
	}
	public Double getResultSum() {
		return CommonUtil.round(
			(getGeneralRegime() != null?getGeneralRegime().getResult():0) +
			(getSimplifiedRegime() != null?getSimplifiedRegime().getResult():0));
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

	public String getDeclarationType() {
		if (getResult() == 0  ) {
			return "N";
		} else if (getResult() > 0  ) {
			return (StringUtils.isNotBlank(getBankAccount()))?"U":"I";
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
	public String getWithoutActivityLetter() {
		return isWithoutActivity()?"X":" ";
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
	public String getGipuzkoaBankAccount() {
		if (getDeposit() > 0) {
			return depositBankAccount;	
		} 
		if (getPayBack() > 0) {
			return payBackBankAccount;
		}
		return "00000000000000000000";
	}
	

	public String getPayBackBankAccount() {
		return payBackBankAccount;
	}
	public void setPayBackBankAccount(String payBackBankAccount) {
		this.payBackBankAccount = payBackBankAccount;
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
	public double getDifference() {
		return difference;
	}
	public void setDifference(double difference) {
		this.difference = difference;
	}

	public GeneralRegime getGeneralRegime() {
		return generalRegime;
	}

	public void setGeneralRegime(GeneralRegime generalRegime) {
		this.generalRegime = generalRegime;
	}

	public SimplifiedRegime getSimplifiedRegime() {
		return simplifiedRegime;
	}

	public void setSimplifiedRegime(SimplifiedRegime simplifiedRegime) {
		this.simplifiedRegime = simplifiedRegime;
	}
	public String getVector() {
		String vector = "0010001";
		if (getSimplifiedRegime() != null) {
			vector += "0020001";
		}
		vector += "0030001FIN";
		return vector;
	}
	
	public int getIae1Key() {
		return iae1Key;
	}
	public void setIae1Key(int iae1Key) {
		this.iae1Key = iae1Key;
	}
	public String getIae1Epigraph() {
		return iae1Epigraph;
	}
	public void setIae1Epigraph(String iae1Epigraph) {
		this.iae1Epigraph = iae1Epigraph;
	}
	public int getIae2Key() {
		return iae2Key;
	}
	public void setIae2Key(int iae2Key) {
		this.iae2Key = iae2Key;
	}
	public String getIae2Epigraph() {
		return iae2Epigraph;
	}
	public void setIae2Epigraph(String iae2Epigraph) {
		this.iae2Epigraph = iae2Epigraph;
	}
	public int getIae3Key() {
		return iae3Key;
	}
	public void setIae3Key(int iae3Key) {
		this.iae3Key = iae3Key;
	}
	public String getIae3Epigraph() {
		return iae3Epigraph;
	}
	public void setIae3Epigraph(String iae3Epigraph) {
		this.iae3Epigraph = iae3Epigraph;
	}
	public int getIae4Key() {
		return iae4Key;
	}
	public void setIae4Key(int iae4Key) {
		this.iae4Key = iae4Key;
	}
	public String getIae4Epigraph() {
		return iae4Epigraph;
	}
	public void setIae4Epigraph(String iae4Epigraph) {
		this.iae4Epigraph = iae4Epigraph;
	}
	public int getIae5Key() {
		return iae5Key;
	}
	public void setIae5Key(int iae5Key) {
		this.iae5Key = iae5Key;
	}
	public String getIae5Epigraph() {
		return iae5Epigraph;
	}
	public void setIae5Epigraph(String iae5Epigraph) {
		this.iae5Epigraph = iae5Epigraph;
	}
	public int getIae6Key() {
		return iae6Key;
	}
	public void setIae6Key(int iae6Key) {
		this.iae6Key = iae6Key;
	}
	public String getIae6Epigraph() {
		return iae6Epigraph;
	}
	public void setIae6Epigraph(String iae6Epigraph) {
		this.iae6Epigraph = iae6Epigraph;
	}
	public String getMod347() {
		return mod347;
	}
	public void setMod347(String mod347) {
		this.mod347 = mod347;
	}
	public double getC80() {
		return c80;
	}
	public void setC80(double c80) {
		this.c80 = c80;
	}
	public double getC81() {
		return c81;
	}
	public void setC81(double c81) {
		this.c81 = c81;
	}
	public double getC82() {
		return c82;
	}
	public void setC82(double c82) {
		this.c82 = c82;
	}
	public double getC83() {
		return c83;
	}
	public void setC83(double c83) {
		this.c83 = c83;
	}
	public double getC84() {
		return c84;
	}
	public void setC84(double c84) {
		this.c84 = c84;
	}
	public double getC85() {
		return c85;
	}
	public void setC85(double c85) {
		this.c85 = c85;
	}
	public double getC86() {
		return c86;
	}
	public void setC86(double c86) {
		this.c86 = c86;
	}
	public double getC87() {
		return c87;
	}
	public void setC87(double c87) {
		this.c87 = c87;
	}
	public double getC88() {
		return c88;
	}
	public void setC88(double c88) {
		this.c88 = c88;
	}

	public void changeInvalidCharacters() {
		setPeriod(FileTaxUtil.changeInvalidCharacters(getPeriod()));
		setBankName(FileTaxUtil.changeInvalidCharacters(getBankName()));
		setCcc(FileTaxUtil.changeInvalidCharacters(getCcc()));	
		setDocument(FileTaxUtil.changeInvalidCharacters(getDocument()));
		setName(FileTaxUtil.changeInvalidCharacters(getName()));
		setSurname(FileTaxUtil.changeInvalidCharacters(getSurname()));
		setAddress(FileTaxUtil.changeInvalidCharacters(getAddress()));
		setEntity(FileTaxUtil.changeInvalidCharacters(getEntity()));
		setCity(FileTaxUtil.changeInvalidCharacters(getCity()));
		setProvinceID(FileTaxUtil.changeInvalidCharacters(getProvinceID()));
		setProvince(FileTaxUtil.changeInvalidCharacters(getProvince()));
		setTelephone(FileTaxUtil.changeInvalidCharacters(getTelephone()));
		setFax(FileTaxUtil.changeInvalidCharacters(getFax()));
		setEmail(FileTaxUtil.changeInvalidCharacters(getEmail()));
		setTodayMonth(FileTaxUtil.changeInvalidCharacters(getTodayMonth()));
		setBankAccount(FileTaxUtil.changeInvalidCharacters(getBankAccount()));
		setDepositBankAccount(FileTaxUtil.changeInvalidCharacters(getDepositBankAccount()));
		setPayBackBankAccount(FileTaxUtil.changeInvalidCharacters(getPayBackBankAccount()));
	}

	@Override
	public String toString() {
		return getDocument() + " " + getName();
	}
}
