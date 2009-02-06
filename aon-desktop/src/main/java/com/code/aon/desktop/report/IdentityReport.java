package com.code.aon.desktop.report;

import java.util.Date;

import com.code.aon.company.Company;
import com.code.aon.registry.RecordData;
import com.code.aon.registry.RegistryAddress;

public class IdentityReport {

	// COMMON
	
	private Company company;

	private RecordData recordData;

	private RegistryAddress address;

	private String addressStr;

	private String addressStr2;

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
	
	private Date fax_date;

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

	private String pagare_concepto;

	private String pagare_fecha;

	private String pagare_para;

	private String pagare_info;

	private String label_to;

	private String label_att;
	
	private String label_to_address;

	private String label_to_address2;

	private String label_to_phone;
	
	private String label_to_fax;
	
	private String label_to_obs;
	
	private String label_to_bultos;

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
		addressStr2 = "";
		if (address.getStreetType()!=null)
			addressStr += address.getStreetType().toString()+" ";
		if (address.getAddress()!=null)
			addressStr += "" + address.getAddress()+"";
		if (address.getAddress2()!=null)
			addressStr += " " + address.getAddress2()+"";
		if (address.getAddress3()!=null)
			addressStr += " " + address.getAddress3()+"";
		if (address.getZip()!=null)
			addressStr2 += "" + address.getZip().trim()+"";
		if (address.getCity()!=null)
			addressStr2 += " " + address.getCity().trim()+"";
		if (address.getGeozone() != null)
			addressStr2 += " - " + address.getGeozone().getName().trim() + "";
		
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

	public String getAddressStr2() {
		return addressStr2;
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

	public Date getFax_date() {
		return fax_date;
	}

	public void setFax_date(Date fax_date) {
		this.fax_date = fax_date;
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
		if (pagare_cantidad.length()>55)
			return pagare_cantidad.substring(0,pagare_cantidad.indexOf(" ", 55));
		return pagare_cantidad;
	}

	public String getPagare_cantidad_line2() {
		if (pagare_cantidad.length()>55)
			return pagare_cantidad.substring(pagare_cantidad.indexOf(" ", 55), pagare_cantidad.length());
		return "";
	}

	public String getPagare_cantidad_num() {
		return pagare_cantidad_num;
	}

	public void setPagare_cantidad_num(String pagare_cantidad_num) {
		this.pagare_cantidad_num = pagare_cantidad_num;
	}

	public void setPagare_concepto(String pagare_concepto) {
		this.pagare_concepto = pagare_concepto;
	}

	public String getPagare_concepto() {
		return this.pagare_concepto;
	}

	public String getPagare_concepto_line1() {
		if (pagare_concepto.length()>75)
			return pagare_concepto.substring(0, pagare_concepto.indexOf(" ", 75));
		return pagare_concepto;
	}

	public String getPagare_concepto_line2() {
		if (pagare_concepto.length()>75)
			return pagare_concepto.substring(pagare_concepto.indexOf(" ", 75), pagare_concepto.length());
		return "";
	}

	public String getPagare_fecha() {
		return pagare_fecha;
	}

	public void setPagare_fecha(String pagare_fecha) {
		this.pagare_fecha = pagare_fecha;
	}

	public String getPagare_para() {
		return pagare_para;
	}

	public void setPagare_para(String pagare_para) {
		this.pagare_para = pagare_para;
	}

	public String getPagare_info() {
		return pagare_info;
	}

	public void setPagare_info(String pagare_info) {
		this.pagare_info = pagare_info;
	}

	public boolean isPrintRegistryData() {
		return printRegistryData;
	}

	public void setPrintRegistryData(boolean printRegistryData) {
		this.printRegistryData = printRegistryData;
	}

	public String getLabel_to() {
		return label_to;
	}

	public void setLabel_to(String label_to) {
		this.label_to = label_to;
	}

	public String getLabel_att() {
		return label_att;
	}

	public void setLabel_att(String label_att) {
		this.label_att = label_att;
	}

	public String getLabel_to_address() {
		return label_to_address;
	}

	public void setLabel_to_address(String label_to_address) {
		this.label_to_address = label_to_address;
	}

	public String getLabel_to_phone() {
		return label_to_phone;
	}

	public void setLabel_to_phone(String label_to_phone) {
		this.label_to_phone = label_to_phone;
	}

	public String getLabel_to_fax() {
		return label_to_fax;
	}

	public void setLabel_to_fax(String label_to_fax) {
		this.label_to_fax = label_to_fax;
	}

	public String getLabel_to_obs() {
		return label_to_obs;
	}

	public void setLabel_to_obs(String label_to_obs) {
		this.label_to_obs = label_to_obs;
	}

	public String getLabel_to_bultos() {
		return label_to_bultos;
	}

	public void setLabel_to_bultos(String label_to_bultos) {
		this.label_to_bultos = label_to_bultos;
	}

	public String getLabel_to_address2() {
		return label_to_address2;
	}

	public void setLabel_to_address2(String label_to_address2) {
		this.label_to_address2 = label_to_address2;
	}

}
