package com.code.aon.desktop.report;

import com.code.aon.company.Company;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAddress;

public class IdentityReport {

	// COMMON
	
	private Company company;

	private RecordData recordData;

	private RegistryAddress address;

	private String addressStr;

	private String addressLine1;
	
	private String addressLine2;

	private String phone;
	
	private String fax;
	
	private String cellular;

	private String email;
	
	private String web;

	// FAX

	private String fax_to;
	
	private String fax_from;
	
	private String fax_subject;
	
	private String fax_content;

	private String fax_number;

	private String fax_phone_number;

	private String fax_page_number;

	// LETTER

	private String letter_date;
	
	private String letter_salutation;
	
	private String letter_content;
	
	private String letter_goodbye;
	
	private String letter_signature;
	
	private boolean printRegistryData;

	// PAGARE

	private String pagare_num;

	private String pagare_de;

	private String pagare_cantidad;
	
	private String pagare_cantidad_num;
	
	private String pagare_fecha_dia;

	private String pagare_fecha_mes;

	private String pagare_fecha_ano;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public RecordData getRecordData() {
		return recordData;
	}

	public void setRecordData(RecordData recordData) {
		this.recordData = recordData;
	}

	public RegistryAddress getAddress() {
		return address;
	}

	public void setAddress(RegistryAddress address) {
		this.address = address;
		addressStr = "";
		if (address.getStreetType()!=null)
			addressStr += address.getStreetType().toString()+" ";
		if (address.getAddress()!=null)
			addressStr += "" + address.getAddress()+"";
		if (address.getAddress2()!=null)
			addressStr += " " + address.getAddress2()+"";
		if (address.getAddress3()!=null)
			addressStr += " " + address.getAddress3()+"";
		if (address.getZip()!=null)
			addressStr += " " + address.getZip().trim()+"";
		if (address.getCity()!=null)
			addressStr += " " + address.getCity().trim()+"";
		if (address.getGeozone() != null)
			addressStr += " - " + address.getGeozone().getName().trim() + "";
		
		addressLine1 = "";
		addressLine2 = "";
		if (address.getStreetType()!=null)
			addressLine1 += address.getStreetType().toString().trim() +" ";
		if (address.getAddress()!=null)
			addressLine1 += "" + address.getAddress().trim()+"";
		if (address.getAddress2()!=null)
			addressLine1 += " " + address.getAddress2().trim()+"";
		if (address.getAddress3()!=null)
			addressLine1 += " " + address.getAddress3().trim()+"";
		if (address.getZip()!=null)
			addressLine2 += address.getZip().trim()+"";
		if (address.getCity()!=null)
			addressLine2 += " " + address.getCity().trim()+"";
		if (address.getGeozone() != null)
			addressLine2 += " - " + address.getGeozone().getName().trim() + "";
	}

	public String getAddressStr() {
		return addressStr;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getCellular() {
		return cellular;
	}

	public void setCellular(String cellular) {
		this.cellular = cellular;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	// FAX
	
	public String getFax_to() {
		return fax_to;
	}

	public void setFax_to(String fax_to) {
		this.fax_to = fax_to;
	}

	public String getFax_from() {
		return fax_from;
	}

	public void setFax_from(String fax_from) {
		this.fax_from = fax_from;
	}

	public String getFax_subject() {
		return fax_subject;
	}

	public void setFax_subject(String fax_subject) {
		this.fax_subject = fax_subject;
	}

	public String getFax_content() {
		return fax_content;
	}

	public void setFax_content(String fax_content) {
		this.fax_content = fax_content;
	}

	public String getFax_number() {
		return fax_number;
	}

	public void setFax_number(String fax_number) {
		this.fax_number = fax_number;
	}

	public String getFax_phone_number() {
		return fax_phone_number;
	}

	public void setFax_phone_number(String fax_phone_number) {
		this.fax_phone_number = fax_phone_number;
	}

	public String getFax_page_number() {
		return fax_page_number;
	}

	public void setFax_page_number(String fax_page_number) {
		this.fax_page_number = fax_page_number;
	}

	// LETTER

	public String getLetter_date() {
		return letter_date;
	}

	public void setLetter_date(String letter_date) {
		this.letter_date = letter_date;
	}

	public String getLetter_salutation() {
		return letter_salutation;
	}

	public void setLetter_salutation(String letter_salutation) {
		this.letter_salutation = letter_salutation;
	}

	public String getLetter_content() {
		return letter_content;
	}

	public void setLetter_content(String letter_content) {
		letter_content = letter_content.trim();
		this.letter_content = "     " + letter_content;
	}

	public String getLetter_goodbye() {
		return letter_goodbye;
	}

	public void setLetter_goodbye(String letter_goodbye) {
		this.letter_goodbye = letter_goodbye;
	}

	public String getLetter_signature() {
		return letter_signature;
	}

	public void setLetter_signature(String letter_signature) {
		this.letter_signature = letter_signature;
	}

	// PAGARE
	
	public String getPagare_num() {
		return pagare_num;
	}

	public void setPagare_num(String pagare_num) {
		this.pagare_num = pagare_num;
	}

	public String getPagare_de() {
		return pagare_de;
	}

	public void setPagare_de(String pagare_de) {
		this.pagare_de = pagare_de;
	}

	public String getPagare_cantidad() {
		return pagare_cantidad;
	}

	public void setPagare_cantidad(String pagare_cantidad) {
		this.pagare_cantidad = pagare_cantidad;
	}

	public String getPagare_cantidad_line1() {
		if (pagare_cantidad.length()>90)
			return pagare_cantidad.substring(0,90);
		return pagare_cantidad;
	}

	public String getPagare_cantidad_line2() {
		if (pagare_cantidad.length()>90)
			return pagare_cantidad.substring(90, pagare_cantidad.length());
		return "";
	}

	public String getPagare_cantidad_num() {
		return pagare_cantidad_num;
	}

	public void setPagare_cantidad_num(String pagare_cantidad_num) {
		this.pagare_cantidad_num = pagare_cantidad_num;
	}

	public String getPagare_fecha_dia() {
		return pagare_fecha_dia;
	}

	public void setPagare_fecha_dia(String pagare_fecha_dia) {
		this.pagare_fecha_dia = pagare_fecha_dia;
	}

	public String getPagare_fecha_mes() {
		return pagare_fecha_mes;
	}

	public void setPagare_fecha_mes(String pagare_fecha_mes) {
		this.pagare_fecha_mes = pagare_fecha_mes;
	}

	public String getPagare_fecha_ano() {
		return pagare_fecha_ano;
	}

	public void setPagare_fecha_ano(String pagare_fecha_ano) {
		this.pagare_fecha_ano = pagare_fecha_ano;
	}

	public boolean isPrintRegistryData() {
		return printRegistryData;
	}

	public void setPrintRegistryData(boolean printRegistryData) {
		this.printRegistryData = printRegistryData;
	}

}
