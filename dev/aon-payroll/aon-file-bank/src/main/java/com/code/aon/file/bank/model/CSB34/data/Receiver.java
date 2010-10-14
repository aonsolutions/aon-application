package com.code.aon.file.bank.model.CSB34.data;

/**
 * The receiver
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Receiver{
	
	/**
	 * Code
	 */
	private String code;
	
	/**
	 * Name
	 */
	private String name;
	
	/**
	 * Address
	 */
	private String address;
	
	/**
	 * County
	 */
	private String county;
	
	/**
	 * Alternate code
	 */
	private String codeAlt;
	
	/**
	 * The document as NIF...
	 */
	private String document;

	/**
	 * Country
	 */
	private String country;

	/**
	 * Postal code
	 */
	private String postalcode;
	
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
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the address substring
	 */
	public String getAddressPart1() {
		if (address.length()>36)return address.substring(0, 36);
		return address;
	}

	/**
	 * @return the address substring
	 */
	public String getAddressPart2() {
		if (address.length()>36){
			if (address.length()>72)return address.substring(36,72);
			return address.substring(36);
		}
		return null;
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
	 * @return the postalcode
	 */
	public String getPostalcode() {
		return postalcode;
	}

	/**
	 * @param postalcode the postalcode to set
	 */
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the codeAlt
	 */
	public String getCodeAlt() {
		return codeAlt;
	}

	/**
	 * @param codeAlt the codeAlt to set
	 */
	public void setCodeAlt(String codeAlt) {
		this.codeAlt = codeAlt;
	}

	/**
	 * @return the document
	 */
	public String getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(String document) {
		this.document = document;
	}
	
	/**
	 * @return the string of postal code and county fixed
	 */
	public String getPostalAndCounty(){
		return postalcode + " " + county;
	}
}
