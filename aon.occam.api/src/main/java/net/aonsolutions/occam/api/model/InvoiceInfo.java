package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceInfoMetadata;
import net.aonsolutions.occam.api.model.type.InvoiceCommunicationStatus;
import net.aonsolutions.occam.api.model.type.InvoiceCommunicationType;

public class InvoiceInfo extends AonEntity<InvoiceInfoMetadata> implements HasAudit {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private InvoiceCommunicationType type;
	private InvoiceCommunicationStatus status;
	
	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceInfo markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public InvoiceInfo setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public InvoiceInfo setDeleted( boolean deleted) {
		super.setDeleted(deleted);
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public InvoiceInfo setId(Integer id) {
		checkIfDirty( this.id,id, InvoiceInfoMetadata.ID);
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoiceInfo setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, InvoiceInfoMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}

	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceInfo setInvoice(Integer invoice) {
		checkIfDirty( this.invoice,invoice, InvoiceInfoMetadata.INVOICE);
		this.invoice = invoice;
		return this;
	}

	public InvoiceCommunicationType getType() {
		return type;
	}
	public InvoiceInfo setType(InvoiceCommunicationType type) {
		checkIfDirty( this.type,type, InvoiceInfoMetadata.TYPE);
		this.type = type;
		return this;
	}
	
	public boolean isAccepted() {
		return getStatus() == InvoiceCommunicationStatus.ACCEPTED;
	}
	
	public boolean isAcceptedWithErrors() {
		return getStatus() == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS;
	}

	public boolean isWrong() {
		return getStatus() == InvoiceCommunicationStatus.WRONG;
	}
	
	public boolean isAnnuled() {
		return getStatus() == InvoiceCommunicationStatus.CANCELLED;
	}

	public InvoiceCommunicationStatus getStatus() {
		if (status == null) setStatus( InvoiceCommunicationStatus.PENDING );
		return status;
	}
	public InvoiceInfo setStatus(InvoiceCommunicationStatus status) {
		checkIfDirty( this.status,status, InvoiceInfoMetadata.STATUS);
		this.status = status;
		return this;
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public InvoiceInfo setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public InvoiceInfo setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public InvoiceInfo setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public InvoiceInfo setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceInfo) {
			return AonObjectUtils.equals( this.getUuid(),((InvoiceInfo) obj).getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + AonObjectUtils.requireNonNullElse(getUuid(), 0).hashCode();
	}

}
