package com.code.aon.accounting.report;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;

public class OperationReport implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Integer id;
	private Date entryDate;
	private String concept;
	private String account;
	private Double balance;
	private String documentNumber;
	private String referenceCode;
	private String rdocument;
	private String rname;
	private List<OperationReportTax> ivaTypes;
	private List<OperationReportTax> taxes;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public String getRdocument() {
		return rdocument;
	}
	public void setRdocument(String rdocument) {
		this.rdocument = rdocument;
	}
	public String getRname() {
		return rname;
	}
	public void setRname(String rname) {
		this.rname = rname;
	}
	public List<OperationReportTax> getIvaTypes() {
		return ivaTypes;
	}
	public void setIvaTypes(List<OperationReportTax> ivaTypes) {
		this.ivaTypes = ivaTypes;
	}
	public String getInvoiceRegistry() {
		return StringUtils.trimToEmpty(getRdocument()) 
			+ ((StringUtils.isBlank(getRdocument()) || StringUtils.isBlank(getRname()))?"":" - ")
			+ StringUtils.trimToEmpty(getRname());
	}
	public boolean isRepeated() {
		return (getId() == null);
	}
	public List<OperationReportTax> getTaxes() {
		return taxes;
	}
	public void setTaxes(List<OperationReportTax> taxes) {
		this.taxes = taxes;
	}
	public String getAbbreviatedAccount() {
		return StringUtils.abbreviate(getAccount(), 60);
	}
	public Double getTotalBase(){
		return taxes==null?0.0:taxes.stream().filter(o -> o.getBase()!=null).mapToDouble(OperationReportTax::getBase).sum();
	}
	public Double getTotalQuota(){
		return taxes==null?0.0:taxes.stream().filter(o -> o.getQuota()!=null).mapToDouble(OperationReportTax::getQuota).sum();
	}
	public Double getTotalSurchargeQuota(){
		return taxes==null?0.0:taxes.stream().filter((o) -> o.getSurchargeQuota()!=null).mapToDouble(OperationReportTax::getSurchargeQuota).sum();
	}
	public Double getTotalIvaQuota(){
		return taxes==null?0.0:taxes.stream().filter(o -> o.getQuota()!=null && o.getTaxType().equals("IVA")).mapToDouble(OperationReportTax::getQuota).sum();
	}
	public Double getTotalIrpfQuota(){
		return taxes==null?0.0:taxes.stream().filter(o -> o.getQuota()!=null && o.getTaxType().equals("IRPF")).mapToDouble(OperationReportTax::getQuota).sum();
	}

}

