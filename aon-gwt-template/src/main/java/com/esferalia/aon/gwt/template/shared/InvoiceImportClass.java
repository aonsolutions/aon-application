package com.esferalia.aon.gwt.template.shared;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.google.gwt.user.client.rpc.IsSerializable;


public class InvoiceImportClass implements IsSerializable {
	
	InvoiceOpType type;
	InvoiceType invoiceType;
	String activity;
	Date date;
	Date taxDate;
	String serie;
	Integer number;
	String ref;
	boolean service;
	String nif;
	String name;
	String registryAccount;
	String third;
	String concept;
	String address;
	String city;
	String province;
	String zip;
	Country country;
	String account;
	String accountDescription;
	String productCode;
	String productName;
	String productSerialNumber;
	String productBarcode;
	boolean suplido;
	Double quantity;
	Double price;
	Double discount;
	Double amount;
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
	Integer line;
	Date financeDate;
	String financeAccount;
	Boolean investment;
	String conceptDetail;
	LinkedList<InvoiceImportClass> lines;
	LinkedList<Finance> finances;
	
	public InvoiceImportClass() {

	}
	
	public String getActivity() {
		return activity;
	}
	
	public InvoiceImportClass setActivity(String activity) {
		this.activity = activity;
		return this;		
	}

	public InvoiceOpType getType() {
		return type;
	}

	public InvoiceImportClass setType(InvoiceOpType type) {
		this.type = type;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public InvoiceImportClass setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public Date getTaxDate() {
		return this.taxDate;
	}
	
	public InvoiceImportClass setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}

	public String getRef() {
		return ref;
	}

	public InvoiceImportClass setRef(String ref) {
		this.ref = ref;
		return this;
	}
	
	public boolean isService() {
		return service;
	}
	
	public InvoiceImportClass setService(boolean service) {
		this.service = service;
		return this;
	}

	public String getNif() {
		return nif;
	}

	public InvoiceImportClass setNif(String nif) {
		this.nif = nif;
		return this;
	}

	public String getName() {
		return name;
	}

	public InvoiceImportClass setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getRegistryAccount() {
		return registryAccount;
	}
	
	public InvoiceImportClass setRegistryAccount(String registryAccount) {
		this.registryAccount = registryAccount;
		return this;
	}

	public String getThird() {
		return third;
	}

	public InvoiceImportClass setThird(String third) {
		this.third = third;
		return this;
	}

	public String getConcept() {
		return concept;
	}

	public InvoiceImportClass setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public String getZip() {
		return zip;
	}

	public InvoiceImportClass setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public InvoiceImportClass setCountry(Country country) {
		this.country = country;
		return this;
	}

	public String getAccount() {
		return account;
	}

	public InvoiceImportClass setAccount(String account) {
		this.account = account;
		return this;
	}

	public Double getBase() {
		return base;
	}

	public InvoiceImportClass setBase(Double base) {
		this.base = base;
		return this;
	}

	public Double getPercentage() {
		return percentage;
	}

	public InvoiceImportClass setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}

	public Double getQuota() {
		return quota;
	}

	public InvoiceImportClass setQuota(Double quota) {
		this.quota = quota;
		return this;
	}

	public Double getRePercentage() {
		return rePercentage;
	}

	public InvoiceImportClass setRePercentage(Double rePercentage) {
		this.rePercentage = rePercentage;
		return this;
	}

	public Double getReQuota() {
		return reQuota;
	}

	public InvoiceImportClass setReQuota(Double reQuota) {
		this.reQuota = reQuota;
		return this;
	}

	public Double getRetentionPercentage() {
		return retentionPercentage;
	}

	public InvoiceImportClass setRetentionPercentage(Double retentionPercentage) {
		this.retentionPercentage = retentionPercentage;
		return this;
	}

	public Double getRetentionQuota() {
		return retentionQuota;
	}

	public InvoiceImportClass setRetentionQuota(Double retentionQuota) {
		this.retentionQuota = retentionQuota;
		return this;
	}

	public Double getTotal() {
		return total;
	}

	public InvoiceImportClass setTotal(Double total) {
		this.total = total;
		return this;
	}

	public InvoiceClaveRetencion getRetentionKey() {
		return retentionKey;
	}

	public InvoiceImportClass setRetentionKey(InvoiceClaveRetencion retentionKey) {
		this.retentionKey = retentionKey;
		return this;
	}

	public InvoiceSubClaveRetencion getRetentionSubKey() {
		return retentionSubKey;
	}

	public InvoiceImportClass setRetentionSubKey(InvoiceSubClaveRetencion retentionSubKey) {
		this.retentionSubKey = retentionSubKey;
		return this;
	}
	
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public InvoiceImportClass setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public InvoiceImportClass setAddress(String address) {
		this.address = address;
		return this;
	}

	public String getCity() {
		return city;
	}

	public InvoiceImportClass setCity(String city) {
		this.city = city;
		return this;
	}

	public String getProvince() {
		return province;
	}

	public InvoiceImportClass setProvince(String province) {
		this.province = province;
		return this;
	}

	public String getSerie() {
		return serie;
	}

	public InvoiceImportClass setSerie(String serie) {
		this.serie = serie;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public InvoiceImportClass setNumber(Integer number) {
		this.number = number;
		return this;
	}
	
	public String getAccountDescription() {
		return accountDescription;
	}

	public InvoiceImportClass setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}
	
	public Integer getLine() {
		return line;
	}

	public InvoiceImportClass setLine(Integer line) {
		this.line = line;
		return this;
	}

	public Date getFinanceDate() {
		return financeDate;
	}

	public InvoiceImportClass setFinanceDate(Date financeDate) {
		this.financeDate = financeDate;
		return this;
	}

	public String getFinanceAccount() {
		return financeAccount;
	}

	public InvoiceImportClass setFinanceAccount(String financeAccount) {
		this.financeAccount = financeAccount;
		return this;
	}
	
	public Boolean isInvestment() {
		return investment;
	}

	public InvoiceImportClass setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	
	public LinkedList<InvoiceImportClass> getLines() {
		if(lines == null)
			lines = new LinkedList<>();
		return lines;
	}
	
	public InvoiceImportClass setLines(LinkedList<InvoiceImportClass> lines) {
		this.lines = lines;
		return this;
	}
	
	
	public LinkedList<Finance> getFinances() {
		if(finances == null) {
			finances = new LinkedList<>();
		}
		return finances;
	}
	
	public InvoiceImportClass setFinances(LinkedList<Finance> finances) {
		this.finances = finances;
		return this;
	}
	
	public String getProductCode() {
		return productCode;
	}

	public InvoiceImportClass setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}

	public String getProductName() {
		return productName;
	}

	public InvoiceImportClass setProductName(String productName) {
		this.productName = productName;
		return this;
	}

	public String getProductSerialNumber() {
		return productSerialNumber;
	}

	public InvoiceImportClass setProductSerialNumber(String productSerialNumber) {
		this.productSerialNumber = productSerialNumber;
		return this;
	}

	public String getProductBarcode() {
		return productBarcode;
	}

	public InvoiceImportClass setProductBarcode(String productBarcode) {
		this.productBarcode = productBarcode;
		return this;
	}

	public boolean isSuplido() {
		return suplido;
	}

	public InvoiceImportClass setSuplido(boolean suplido) {
		this.suplido = suplido;
		return this;
	}
	
	public Double getQuantity() {
		return quantity;
	}
	
	public InvoiceImportClass setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public InvoiceImportClass setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public Double getDiscount() {
		return discount;
	}
	
	public InvoiceImportClass setDiscount(Double discount) {
		this.discount = discount;
		return this;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public InvoiceImportClass setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	
	public String getConceptDetail() {
		return conceptDetail;
	}
	
	public InvoiceImportClass setConceptDetail(String conceptDetail) {
		this.conceptDetail = conceptDetail;
		return this;
	}

	public boolean isEmpty() {
		return getType() == null && getDate() == null && getRef() == null
			&& getSerie() == null && getNumber() == null && getName() == null
			&& getNif() == null && getThird() == null && getConcept() == null
			&& getAddress() == null && getCity() == null && getProvince() == null
			&& getZip() == null && getCountry() == null && getAccount() == null
			&& getAccountDescription() == null && getBase() == null && getPercentage() == null
			&& getQuota() == null && getRePercentage() == null && getReQuota() == null
			&& getRetentionPercentage() == null && getRetentionQuota() == null
			&& getTotal() == null && getRetentionKey() == null && getRetentionSubKey() == null
			&& getFinanceDate() == null && getFinanceAccount() == null;
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
			try {
				return valueOf(value);
			} catch (Exception e) {
				return null;
			}
		}
		
		public static InvoiceOpType safeValueOf(InvoiceTransactionType value) {
			if(value == null) return null;
			try {
				if(InvoiceTransactionType.NATIONAL.equals(value))
					return NAC;
				else if(InvoiceTransactionType.INTRACOMMUNITY.equals(value))
					return INT;
				else if(InvoiceTransactionType.EXTRACOMMUNITY.equals(value))
					return EXT;
				else if(InvoiceTransactionType.CAN_CEU_MEL.equals(value))
					return CCM;
				else if(InvoiceTransactionType.OTHER_ISP.equals(value))
					return ISP;
			} catch (Exception e) {
				return null;
			}
			return null;
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
			try {
				return valueOf(value);
			} catch (Exception e) {
				return null;
			}
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
