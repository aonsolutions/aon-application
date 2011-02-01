package com.code.aon.csb.fd0.model.MOD347.data;

/**
 * Declared 
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Declared {

	/**
	 * The code
	 */
	private String code;
	/**
	 * The managers code
	 */
	private String managerCode;
	/**
	 * The anme
	 */
	private String name;
	/**
	 * The province
	 */
	private Integer province;
	/**
	 * The country
	 */
	private Integer country;
	/**
	 * The key
	 */
	private String key;
	/**
	 * The quantity
	 */
	private Double quantity;
	/**
	 * Is insurance
	 */
	private boolean insurance = false;
	/**
	 * Is renting
	 */
	private boolean renting = false;
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the country
	 */
	public Integer getCountry() {
		return country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(Integer country) {
		this.country = country;
	}
	/**
	 * @return the insurance code
	 */
	public String getInsurance() {
		return insurance?"X":null;
	}
	/**
	 * @param insurance the insurance to set
	 */
	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the managerCode
	 */
	public String getManagerCode() {
		return managerCode;
	}
	/**
	 * @param managerCode the managerCode to set
	 */
	public void setManagerCode(String managerCode) {
		this.managerCode = managerCode;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the province
	 */
	public Integer getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(Integer province) {
		this.province = province;
	}
	/**
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the renting code
	 */
	public String getRenting() {
		return renting?"X":null;
	}
	/**
	 * @param renting the renting to set
	 */
	public void setRenting(boolean renting) {
		this.renting = renting;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "DECLARED CODE ";
		description += code == null?"NULL ":"'"+code+"''";
		description += "NAME ";
		description += name == null?"NULL ":"'"+name+"'; ";
		return description;
	}
	

}
