package com.code.aon.file.tax.model.MOD347.data;

public class Declared {

	private String code;
	private String managerCode;
	private String name;
	private Integer province;
	private String country;
	private String key;
	private Double quantity;
	private Double quantityQuarter1;	
	private Double quantityQuarter2;	
	private Double quantityQuarter3;	
	private Double quantityQuarter4;
	
	private double assetAmount;
	private double assetFirstQuarterAmount;
	private double assetSecondQuarterAmount;
	private double assetThirdQuarterAmount;
	private double assetFourthQuarterAmount;
	private double cashAmount;
	private Integer cashYear;
	
	private String operatorNif;
	private boolean vatAccrual = false;
	private boolean isp = false;
	private boolean depositRegime = false;
	private double vatAccrualAmount;
	
	private boolean insurance = false;
	private boolean renting = false;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	public String getInsurance() {
		return insurance?"X":null;
	}
	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getManagerCode() {
		return managerCode;
	}
	public void setManagerCode(String managerCode) {
		this.managerCode = managerCode;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getProvince() {
		return province;
	}
	public void setProvince(Integer province) {
		this.province = province;
	}

	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public String getRenting() {
		return renting?"X":null;
	}
	public void setRenting(boolean renting) {
		this.renting = renting;
	}

	public Double getQuantityQuarter1() {
		return quantityQuarter1;
	}
	public void setQuantityQuarter1(Double quantityQuarter1) {
		this.quantityQuarter1 = quantityQuarter1;
	}

	public Double getQuantityQuarter2() {
		return quantityQuarter2;
	}
	public void setQuantityQuarter2(Double quantityQuarter2) {
		this.quantityQuarter2 = quantityQuarter2;
	}
	
	public Double getQuantityQuarter3() {
		return quantityQuarter3;
	}
	public void setQuantityQuarter3(Double quantityQuarter3) {
		this.quantityQuarter3 = quantityQuarter3;
	}
	
	public Double getQuantityQuarter4() {
		return quantityQuarter4;
	}
	public void setQuantityQuarter4(Double quantityQuarter4) {
		this.quantityQuarter4 = quantityQuarter4;
	}
	
	public double getAssetAmount() {
		return assetAmount;
	}
	public void setAssetAmount(double assetAmount) {
		this.assetAmount = assetAmount;
	}
	public double getAssetFirstQuarterAmount() {
		return assetFirstQuarterAmount;
	}
	public void setAssetFirstQuarterAmount(double assetFirstQuarterAmount) {
		this.assetFirstQuarterAmount = assetFirstQuarterAmount;
	}
	public double getAssetSecondQuarterAmount() {
		return assetSecondQuarterAmount;
	}
	public void setAssetSecondQuarterAmount(double assetSecondQuarterAmount) {
		this.assetSecondQuarterAmount = assetSecondQuarterAmount;
	}
	public double getAssetThirdQuarterAmount() {
		return assetThirdQuarterAmount;
	}
	public void setAssetThirdQuarterAmount(double assetThirdQuarterAmount) {
		this.assetThirdQuarterAmount = assetThirdQuarterAmount;
	}
	public double getAssetFourthQuarterAmount() {
		return assetFourthQuarterAmount;
	}
	public void setAssetFourthQuarterAmount(double assetFourthQuarterAmount) {
		this.assetFourthQuarterAmount = assetFourthQuarterAmount;
	}
	public double getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(double cashAmount) {
		this.cashAmount = cashAmount;
	}
	public Integer getCashYear() {
		return cashYear;
	}
	public void setCashYear(Integer cashYear) {
		this.cashYear = cashYear;
	}
	
	public String getOperatorNif() {
		return operatorNif;
	}
	public void setOperatorNif(String operatorNif) {
		this.operatorNif = operatorNif;
	}
	public String getVatAccrual() {
		return vatAccrual?"X":"";
	}
	public void setVatAccrual(boolean vatAccrual) {
		this.vatAccrual = vatAccrual;
	}
	public String getIsp() {
		return isp?"X":"";
	}
	public void setIsp(boolean isp) {
		this.isp = isp;
	}
	public String getDepositRegime() {
		return depositRegime?"X":"";
	}
	public void setDepositRegime(boolean depositRegime) {
		this.depositRegime = depositRegime;
	}
	public double getVatAccrualAmount() {
		return vatAccrualAmount;
	}
	public void setVatAccrualAmount(double vatAccrualAmount) {
		this.vatAccrualAmount = vatAccrualAmount;
	}
	public String toString(){
		String description = "Reg. Declarado ";
		description += code == null?"NULL ":"'"+code+"''";
		description += "NAME ";
		description += name == null?"NULL ":"'"+name+"'; ";
		return description;
	}

}
