package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class InvoiceConsoleParams implements Serializable {
	
	public static enum OrderBy {
		 ISSUE_DATE("Fecha de emisi\u00F3n") { @Override public <T> T visit(InvoiceConsoleParamsOrderVisitor<T> visitor) {return visitor.issueDate();}}
		,REGISTRY("Raz\u00F3n Social") { @Override public <T> T visit(InvoiceConsoleParamsOrderVisitor<T> visitor) {return visitor.registry();}}
		,SERIES_NUMBER("Serie/N\u00FAmero") { @Override public <T> T visit(InvoiceConsoleParamsOrderVisitor<T> visitor) {return visitor.seriesNumber();}}
		,REFERENCE_CODE("C\u00F3digo de referencia") { @Override public <T> T visit(InvoiceConsoleParamsOrderVisitor<T> visitor) {return visitor.referenceCode();}}
		,ID("Orden creaci\u00F3n") { @Override public <T> T visit(InvoiceConsoleParamsOrderVisitor<T> visitor) {return visitor.id();}}
		;
		
		private String label;

		private OrderBy(String label) {
			this.label = label;
		}
		public String getLabel() {
			return label;
		}

		public abstract <T> T visit( InvoiceConsoleParamsOrderVisitor<T> visitor);
		
		public interface InvoiceConsoleParamsOrderVisitor<T> {
			T issueDate();
			T registry();
			T seriesNumber();
			T referenceCode();
			T id();
		}
	}
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private Integer id;
	private Integer[] ids;
	private Integer fromId;
	private Integer toId;
	private Integer domain;
	private Date fromDate;
	private Date toDate;
	private Integer activity;
	
	private String series;
	private Integer fromNumber;
	private Integer toNumber;
	private String referenceCode;
	
	private Integer registry;
	private Boolean output;
	private Boolean annulled;
	private InvoiceTransactionType transactionType;
	private RectificationType rectified;
	private Boolean surcharge;
	private Boolean farmerRegime;
	private Boolean accrualRegime;
	private Boolean investment;
	private Boolean withholding;
	private Boolean service;
	private Boolean recorded;
	private Boolean proforma;
	private Boolean amortizationBinded;
	private InvoiceSource source;

	private SecurityLevel securityLevel;
	
	private InvoiceCommunicationType communicationType;
	private InvoiceCommunicationStatus communicationStatus;
	
	private OrderBy orderBy;
	private boolean descending = true;

	private int offset;
	private int limit;


	// FOR DAO ONLY. Not serializable to/from JSON. 
	private boolean communicationExcluded;
	private boolean attachExcluded;
	// --------------------------------------------

	public Integer getId() {
		return id;
	}
	public InvoiceConsoleParams setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer[] getIds() {
		return ids;
	}
	public InvoiceConsoleParams setIds(Integer[] ids) {
		this.ids = ids;
		return this;
	}
	
	public Integer getFromId() {
		return fromId;
	}
	public InvoiceConsoleParams setFromId(Integer fromId) {
		this.fromId = fromId;
		return this;
	}
	
	public Integer getToId() {
		return toId;
	}
	public InvoiceConsoleParams setToId(Integer toId) {
		this.toId = toId;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceConsoleParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	public InvoiceConsoleParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public InvoiceConsoleParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

	public Integer getActivity() {
		return activity;
	}
	public InvoiceConsoleParams setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public InvoiceConsoleParams setSeries(String series) {
		this.series = series;
		return this;
	}
	
	public Integer getFromNumber() {
		return fromNumber;
	}
	public InvoiceConsoleParams setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
		return this;
	}
	
	public Integer getToNumber() {
		return toNumber;
	}
	public InvoiceConsoleParams setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public InvoiceConsoleParams setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public InvoiceConsoleParams setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public InvoiceConsoleParams setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	
	public Boolean getOutput() {
		return output;
	}
	public InvoiceConsoleParams setOutput(Boolean output) {
		this.output = output;
		return this;
	}
	public boolean isOutput() {
		return output != null && output.booleanValue();
	}
	public boolean isInput() {
		return output != null && !output.booleanValue();
	}
	
	public InvoiceTransactionType getTransactionType() {
		return transactionType;
	}
	public InvoiceConsoleParams setTransactionType(InvoiceTransactionType transactionType) {
		this.transactionType = transactionType;
		return this;
	}
	
	public RectificationType getRectificationType() {
		return rectified;
	}
	public InvoiceConsoleParams setRectificationType(RectificationType rectified) {
		this.rectified = rectified;
		return this;
	}
	
	public Boolean getSurcharge() {
		return surcharge;
	}
	public InvoiceConsoleParams setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
		return this;
	}
	
	public Boolean getFarmerRegime() {
		return farmerRegime;
	}
	public InvoiceConsoleParams setFarmerRegime(Boolean farmerRegime) {
		this.farmerRegime = farmerRegime;
		return this;
	}
	
	public Boolean getAccrualRegime() {
		return accrualRegime;
	}
	public InvoiceConsoleParams setAccrualRegime(Boolean accrualRegime) {
		this.accrualRegime = accrualRegime;
		return this;
	}
	
	public Boolean getWithholding() {
		return withholding;
	}
	public InvoiceConsoleParams setWithholding(Boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	
	public Boolean getInvestment() {
		return investment;
	}
	public InvoiceConsoleParams setInvestment(Boolean investment) {
		this.investment = investment;
		return this;
	}
	
	public Boolean getService() {
		return service;
	}
	public InvoiceConsoleParams setService(Boolean service) {
		this.service = service;
		return this;
	}
	
	public Boolean getRecorded() {
		return recorded;
	}
	public InvoiceConsoleParams setRecorded(Boolean recorded) {
		this.recorded = recorded;
		return this;
	}
	
	public Boolean getProforma() {
		return proforma;
	}
	public InvoiceConsoleParams setProforma(Boolean proforma) {
		this.proforma = proforma;
		return this;
	}

	public Boolean getAmortizationBinded() {
		return amortizationBinded;
	}
	public InvoiceConsoleParams setAmortizationBinded(Boolean amortizationBinded) {
		this.amortizationBinded = amortizationBinded;
		return this;
	}
	
	public InvoiceSource getSource() {
		return source;
	}
	public InvoiceConsoleParams setSource(InvoiceSource source) {
		this.source = source;
		return this;
	}

	public InvoiceCommunicationType getCommunicationType() {
		return communicationType;
	}
	public InvoiceConsoleParams setCommunicationType(InvoiceCommunicationType communicationType) {
		this.communicationType = communicationType;
		return this;
	}
	
	public InvoiceCommunicationStatus getCommunicationStatus() {
		return communicationStatus;
	}
	public InvoiceConsoleParams setCommunicationStatus(InvoiceCommunicationStatus communicationStatus) {
		this.communicationStatus = communicationStatus;
		return this;
	}
	
	public Boolean getAnnulled() {
		return annulled;
	}
	public InvoiceConsoleParams setAnnulled(Boolean annulled) {
		this.annulled = annulled;
		return this;
	}
	
	public OrderBy getOrderBy() {
		return orderBy;
	}
	public InvoiceConsoleParams setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public boolean isDescending() {
		return descending;
	}
	public InvoiceConsoleParams setDescending(boolean descending) {
		this.descending = descending;
		return this;
	}
	
	public int getOffset() {
		return offset;
	}
	public InvoiceConsoleParams setOffset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public int getLimit() {
		return limit;
	}
	public InvoiceConsoleParams setLimit(int limit) {
		this.limit = limit;
		return this;
	}

	// FOR DAO ONLY. Not serializable to/from JSON.
	public boolean isCommunicationExcluded() {
		return communicationExcluded;
	}
	public InvoiceConsoleParams setCommunicationExcluded(boolean communicationExcluded) {
		this.communicationExcluded = communicationExcluded;
		return this;
	}
	
	public boolean isAttachExcluded() {
		return attachExcluded;
	}
	public InvoiceConsoleParams setAttachExcluded( boolean attachExcluded) {
		this.attachExcluded = attachExcluded;
		return this;
	}
	
}
