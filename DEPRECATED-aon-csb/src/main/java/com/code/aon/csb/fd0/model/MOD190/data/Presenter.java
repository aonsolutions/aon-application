package com.code.aon.csb.fd0.model.MOD190.data;

/**
 * The presenter
 *  
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Presenter {

	/**
	 * The year
	 */
	private Integer year;
	/**
	 * The code
	 */
	private String code;
	/**
	 * The name
	 */
	private String name;
	/**
	 * The street type
	 */
	private String streetType;
	/**
	 * The street name
	 */
	private String streetName;
	/**
	 * The street number
	 */
	private Integer streetNumber;
	/**
	 * The street stair
	 */
	private String streetStair;
	/**
	 * The street storey
	 */
	private String streetStorey;
	/**
	 * The street door
	 */
	private String streetDoor;
	/**
	 * The postal code
	 */
	private Integer postalCode;
	/**
	 * The county
	 */
	private String county;
	/**
	 * The province
	 */
	private Integer province;
	/**
	 * The relationship phone
	 */
	private Integer relPhone;
	/**
	 * The relationship person name 
	 */
	private String relName;
	
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
	 * @return the county
	 */
	public String getCounty() {
		return county;
	}
	/**
	 * @param county the county to set
	 */
	public void setCounty(String county) {
		this.county = county;
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
	 * @return the postalCode
	 */
	public Integer getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(Integer postalCode) {
		this.postalCode = postalCode;
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
	 * @return the relName
	 */
	public String getRelName() {
		return relName;
	}
	/**
	 * @param relName the relName to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}
	/**
	 * @return the relPhone
	 */
	public Integer getRelPhone() {
		return relPhone;
	}
	/**
	 * @param relPhone the relPhone to set
	 */
	public void setRelPhone(Integer relPhone) {
		this.relPhone = relPhone;
	}
	/**
	 * @return the streetDoor
	 */
	public String getStreetDoor() {
		return streetDoor;
	}
	/**
	 * @param streetDoor the streetDoor to set
	 */
	public void setStreetDoor(String streetDoor) {
		this.streetDoor = streetDoor;
	}
	/**
	 * @return the streetName
	 */
	public String getStreetName() {
		return streetName;
	}
	/**
	 * @param streetName the streetName to set
	 */
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	/**
	 * @return the streetNumber
	 */
	public Integer getStreetNumber() {
		return streetNumber;
	}
	/**
	 * @param streetNumber the streetNumber to set
	 */
	public void setStreetNumber(Integer streetNumber) {
		this.streetNumber = streetNumber;
	}
	/**
	 * @return the streetStair
	 */
	public String getStreetStair() {
		return streetStair;
	}
	/**
	 * @param streetStair the streetStair to set
	 */
	public void setStreetStair(String streetStair) {
		this.streetStair = streetStair;
	}
	/**
	 * @return the streetStorey
	 */
	public String getStreetStorey() {
		return streetStorey;
	}
	/**
	 * @param streetStorey the streetStorey to set
	 */
	public void setStreetStorey(String streetStorey) {
		this.streetStorey = streetStorey;
	}
	/**
	 * @return the streetType
	 */
	public String getStreetType() {
		return streetType;
	}
	/**
	 * @param streetType the streetType to set
	 */
	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}
	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String description = "PRESENTER ";
		description += "YEAR ";
		description += year == null ? "NULL " : "'" + year.toString() + "'";
		description += "CODE ";
		description += code == null ? "NULL " : "'" + code + "''";
		description += "NAME ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}
	
	
}
