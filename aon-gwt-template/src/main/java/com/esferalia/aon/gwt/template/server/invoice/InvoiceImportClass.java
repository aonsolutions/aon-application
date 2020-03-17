package com.esferalia.aon.gwt.template.server.invoice;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;


public class InvoiceImportClass {
	InvoiceOpType type;
	Date date;
	String ref;
	String nif;
	String name;
	String third;
	String concept;
	String zip;
	Country country;
	String account;
	Double base;
	Double percentage;
	Double quota;
	Double rePercentage;
	Double reQuota;
	Double retentionPercentage;
	Double retentionQuota;
	Double total;
	InvoiceClaveRetencion retentionKey;
	InvoiceSubClaveRetencion retentionSubKey;
	
	public InvoiceImportClass() {
		// TODO Auto-generated constructor stub
	}

	public InvoiceOpType getType() {
		return type;
	}

	public void setType(InvoiceOpType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getThird() {
		return third;
	}

	public void setThird(String third) {
		this.third = third;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Double getBase() {
		return base;
	}

	public void setBase(Double base) {
		this.base = base;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getQuota() {
		return quota;
	}

	public void setQuota(Double quota) {
		this.quota = quota;
	}

	public Double getRePercentage() {
		return rePercentage;
	}

	public void setRePercentage(Double rePercentage) {
		this.rePercentage = rePercentage;
	}

	public Double getReQuota() {
		return reQuota;
	}

	public void setReQuota(Double reQuota) {
		this.reQuota = reQuota;
	}

	public Double getRetentionPercentage() {
		return retentionPercentage;
	}

	public void setRetentionPercentage(Double retentionPercentage) {
		this.retentionPercentage = retentionPercentage;
	}

	public Double getRetentionQuota() {
		return retentionQuota;
	}

	public void setRetentionQuota(Double retentionQuota) {
		this.retentionQuota = retentionQuota;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public InvoiceClaveRetencion getRetentionKey() {
		return retentionKey;
	}

	public void setRetentionKey(InvoiceClaveRetencion retentionKey) {
		this.retentionKey = retentionKey;
	}

	public InvoiceSubClaveRetencion getRetentionSubKey() {
		return retentionSubKey;
	}

	public void setRetentionSubKey(InvoiceSubClaveRetencion retentionSubKey) {
		this.retentionSubKey = retentionSubKey;
	}
	
	public enum InvoiceOpType {
		EX,
		PIS,
		ISP,
		EIB,
		VI,
		AI,
		AIS,
		AIB,
		GISP,
		GE,
		NAC,
		INT,
		EXT,
		CCM;
		
		public static InvoiceOpType safeValueOf(String value) {
			if(value == null) return null;
			return valueOf(value);
		}
	}
	
	public enum InvoiceClaveRetencion {
		A,
		B,
		C,
		D,
		E,
		F,
		G,
		H,
		I,
		J,
		K,
		M,
		N,
		O,
		PR,
		AR,
		CM,
		AG,
		TA;
		
		public static InvoiceClaveRetencion safeValueOf(String value) {
			if(value == null) return null;
			return valueOf(value);
		}
	}	
	
	public enum InvoiceSubClaveRetencion {
		O1,
		O2,
		O3,
		O4;
		
		public static InvoiceSubClaveRetencion safeValueOf(Integer value) {
			if(value != null && value == 1) {
				 return O1;
			} else if(value != null && value == 2) {
				 return O2;
			} else if(value != null && value == 3) {
				 return O3;
			} else if(value != null && value == 4) {
				 return O4;
			}
			return null;
		}
		
		public static InvoiceSubClaveRetencion safeValueOf(String value) {
			if(value != null && ("01".equals(value) || "1".equals(value))) {
				 return O1;
			} else if(value != null && ("02".equals(value) || "2".equals(value))) {
				 return O2;
			} else if(value != null && ("03".equals(value) || "3".equals(value))) {
				 return O3;
			} else if(value != null && ("04".equals(value) || "4".equals(value))) {
				 return O4;
			}
			return null;
		}
	}
}
