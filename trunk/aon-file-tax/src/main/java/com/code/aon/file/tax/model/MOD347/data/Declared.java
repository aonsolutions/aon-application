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

	public String toString(){
		String description = "Reg. Declarado ";
		description += code == null?"NULL ":"'"+code+"''";
		description += "NAME ";
		description += name == null?"NULL ":"'"+name+"'; ";
		return description;
	}

}
