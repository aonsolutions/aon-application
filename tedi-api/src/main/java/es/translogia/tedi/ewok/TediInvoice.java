package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class TediInvoice implements Serializable{

	private static final long serialVersionUID = 4073286910695517187L;
	
	private String uuid;
	private String company;
	private TediInvoiceType type;
	private String series;
	private Integer number;
	private String reference;
	private Date date;
	private TediInvoiceTransaction transaction;
	private TediInvoiceCategory category;
	private Double total;
	private TediRegistry sender;
	private TediRegistry receiver;
	private LinkedList<TediInvoiceDetail> details;
	private LinkedList<TediInvoiceTax> taxes;
	private LinkedList<TediFinance> finances;
	private TediInvoiceFile file;
	private TediInvoiceStatus status;
	private TediInvoiceStatus oldStatus;
	private String source;
	private LinkedList<TediComments> comments;
	private TediEmailInfo email;

	public String getUuid() {
		return uuid;
	}

	public TediInvoice setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public String getCompany() {
		return company;
	}

	public TediInvoice setCompany(String company) {
		this.company = company;
		return this;
	}

	public TediInvoiceType getType() {
		return type;
	}

	public TediInvoice setType(TediInvoiceType type) {
		this.type = type;
		return this;
	}

	public String getSeries() {
		return series;
	}

	public TediInvoice setSeries(String series) {
		this.series = series;
		return this;
	}

	public Integer getNumber() {
		return number;
	}

	public TediInvoice setNumber(Integer number) {
		this.number = number;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public TediInvoice setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public TediInvoice setDate(Date date) {
		this.date = date;
		return this;
	}

	public TediInvoiceTransaction getTransaction() {
		return transaction;
	}

	public TediInvoice setTransaction(TediInvoiceTransaction transaction) {
		this.transaction = transaction;
		return this;
	}

	public TediInvoiceCategory getCategory() {
		return category;
	}

	public TediInvoice setCategory(TediInvoiceCategory category) {
		this.category = category;
		return this;
	}

	public Double getTotal() {
		return total;
	}

	public TediInvoice setTotal(Double total) {
		this.total = total;
		return this;
	}

	public TediRegistry getSender() {
		return sender;
	}

	public TediInvoice setSender(TediRegistry sender) {
		this.sender = sender;
		return this;
	}

	public TediRegistry getReceiver() {
		return receiver;
	}

	public TediInvoice setReceiver(TediRegistry receiver) {
		this.receiver = receiver;
		return this;
	}

	public LinkedList<TediInvoiceDetail> getDetails() {
		return details;
	}

	public TediInvoice setDetails(LinkedList<TediInvoiceDetail> details) {
		this.details = details;
		return this;
	}

	public LinkedList<TediInvoiceTax> getTaxes() {
		return taxes;
	}

	public TediInvoice setTaxes(LinkedList<TediInvoiceTax> taxes) {
		this.taxes = taxes;
		return this;
	}

	public LinkedList<TediFinance> getFinances() {
		return finances;
	}

	public TediInvoice setFinances(LinkedList<TediFinance> finances) {
		this.finances = finances;
		return this;
	}

	public TediInvoiceFile getFile() {
		return file;
	}

	public TediInvoice setFile(TediInvoiceFile file) {
		this.file = file;
		return this;
	}

	public TediInvoiceStatus getStatus() {
		return status;
	}

	public TediInvoice setStatus(TediInvoiceStatus status) {
		this.status = status;
		return this;
	}

	public TediInvoiceStatus getOldStatus() {
		return oldStatus;
	}

	public TediInvoice setOldStatus(TediInvoiceStatus oldStatus) {
		this.oldStatus = oldStatus;
		return this;
	}

	public String getSource() {
		return source;
	}

	public TediInvoice setSource(String source) {
		this.source = source;
		return this;
	}

	public LinkedList<TediComments> getComments() {
		return comments;
	}
	public TediInvoice setComments(LinkedList<TediComments> comments) {
		this.comments = comments;
		return this;
	}
	
	public TediEmailInfo getEmail() {
		return email;
	}

	public TediInvoice setEmail(TediEmailInfo email) {
		this.email = email;
		return this;
	}

	public TediRegistry getRegistry() {
		if (getType() != null) {
			return isEmitida() ? getReceiver() : getSender();
		}
		return null;
	}

	public TediInvoice setRegistry(TediRegistry registry) {
		if (getType() != null && isEmitida()) {
			setReceiver(registry);
		} else {
			setSender(registry);
		}
		return this;
	}

	public boolean isEmitida() {
		return (getType() == TediInvoiceType.EMITIDA);
	}
	public boolean isRecibida() {
		return (getType() == TediInvoiceType.RECIBIDA);
	}
	public boolean isTicket() {
		return (getType() == TediInvoiceType.TICKET);
	}
	public boolean isExpense() {
		return isRecibida() 
			&& getCategory() != null
			&& getCategory() != TediInvoiceCategory.C6000
			&& getCategory() != TediInvoiceCategory.C6070
			;
	}
	public boolean isPurchase() {
		return isRecibida() && !isExpense(); 
	}

}
