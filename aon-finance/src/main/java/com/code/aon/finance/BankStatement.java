package com.code.aon.finance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "bank_statement")
public class BankStatement implements ITransferObject {

	private static final long serialVersionUID = 6904628009460137531L;

    private Integer id;
    private RegistryBank registryBank;
    private int lotNumber;
    private Date operationDate;
    private StatementConcept commonConcept;
    private String ownConcept;
    private boolean payment;
    private double amount;
    private int document;
    private String reference1;
    private String reference2;
    private String description;
    private StatementReliability reliability;
    private StatementStatus status;
	private String comments;

	private boolean showBankStatementLink;
	private boolean showAccountEntry;

	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rbank", nullable = false)
    @ForeignKey(name="FK_BANK_STATEMENT_RBANK")
    @Index(name="IDX_BANK_STATEMENT_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
    @Column(name="lot_number")
    public int getLotNumber() {
        return lotNumber;
    }
    public void setLotNumber(int lotNumber) {
        this.lotNumber = lotNumber;
    }
    
	@Column(name="operation_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

    @Column(name="common_concept", nullable = false)
	public StatementConcept getCommonConcept() {
		return commonConcept;
	}
	public void setCommonConcept(StatementConcept commonConcept) {
		this.commonConcept = commonConcept;
	}

    @Column(name="own_concept", length=5)
	public String getOwnConcept() {
		return ownConcept;
	}
	public void setOwnConcept(String ownConcept) {
		this.ownConcept = ownConcept;
	}

    @Column(nullable = false)
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
    @Column(precision=15, scale=2)
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
    @Column
    public int getDocument() {
        return document;
    }
    public void setDocument(int document) {
        this.document = document;
    }
    
	@Column(length=12)
	public String getReference1() {
		return reference1;
	}
	public void setReference1(String reference1) {
		this.reference1 = reference1;
	}

	@Column(length=16)
	public String getReference2() {
		return reference2;
	}
	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	@Column(length=80)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public StatementReliability getReliability() {
		return reliability;
	}
	public void setReliability(StatementReliability reliability) {
		this.reliability = reliability;
	}

	public StatementStatus getStatus() {
		return status;
	}
	public void setStatus(StatementStatus status) {
		this.status = status;
	}

	@Column(name="comments")
	@Lob
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Transient
	public boolean isReturned() {
		return commonConcept == StatementConcept.RETURNED;
	}
	@Transient
	public boolean isCollectionBatch() {
		return commonConcept == StatementConcept.COLLECTION_BATCH;
	}

	@Transient
	public boolean isExact() {
		return reliability == StatementReliability.VERY_HIGH;
	}
	@Transient
	public boolean isApproximate() {
		return reliability == StatementReliability.HIGH;
	}
	@Transient
	public boolean isAmbiguous() {
		return reliability == StatementReliability.MEDIUM;
	}
	@Transient
	public boolean isInexact() {
		return reliability == StatementReliability.LOW;
	}

	@Transient
	public boolean isPending() {
		return status == StatementStatus.PENDING;
	}
	@Transient
	public boolean isChecked() {
		return status == StatementStatus.CHECKED;
	}
	@Transient
	public boolean isRecorded() {
		return status == StatementStatus.RECORDED;
	}

	@Transient
	public boolean isShowBankStatementLink() {
		return showBankStatementLink;
	}
	public void setShowBankStatementLink(boolean value) {
		this.showBankStatementLink = value;
	}

	@Transient
	public boolean isShowAccountEntry() {
		return showAccountEntry;
	}
	public void setShowAccountEntry(boolean value) {
		this.showAccountEntry = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final BankStatement o = (BankStatement) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.amount,o.amount)
				.append(this.comments,o.comments)
				.append(this.commonConcept,o.commonConcept)
				.append(this.description,o.description)
				.append(this.document,o.document)
				.append(this.lotNumber,o.lotNumber)
				.append(this.operationDate,o.operationDate)		
				.append(this.ownConcept,o.ownConcept)
				.append(this.payment,o.payment)		
				.append(this.registryBank,o.registryBank)		
				.append(this.reference1,o.reference1)
				.append(this.reference2,o.reference2)
				.append(this.reliability,o.reliability)		
				.append(this.status,o.status)		
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.amount)
			.append(this.comments)
			.append(this.commonConcept)
			.append(this.description)
			.append(this.document)
			.append(this.lotNumber)
			.append(this.operationDate)		
			.append(this.ownConcept)
			.append(this.payment)		
			.append(this.registryBank)		
			.append(this.reference1)
			.append(this.reference2)
			.append(this.reliability)		
			.append(this.status)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}