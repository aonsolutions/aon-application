package com.code.aon.file.tax.model.MOD347.data;

/**
 * A building
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Building {

	/**
	 * The code
	 */
	private String code;
	/**
	 * The  managers code
	 */
	private String managerCode;
	/**
	 * The name
	 */
	private String name;
	/**
	 * The quantity
	 */
	private Double quantity;
	/**
	 * The cadastre code
	 */
	private String cadastre;
	/**
	 * The province code
	 */
	private Integer province;
	/**
	 * The country
	 */
	private String county;
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
	 * The stair
	 */
	private String streetStair;
	/**
	 * The storey
	 */
	private String streetStorey;
	/**
	 * The door
	 */
	private String streetDoor;
	
	/**
	 * @return the cadastre
	 */
	public String getCadastre() {
		return cadastre;
	}
	/**
	 * @param cadastre the cadastre to set
	 */
	public void setCadastre(String cadastre) {
		this.cadastre = cadastre;
	}
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "BUILDING CODE ";
		description += code == null?"NULL ":"'"+code+"''";
		description += "NAME ";
		description += name == null?"NULL ":"'"+name+"'; ";
		return description;
	}
	
	

}
