package com.code.aon.finance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "bank_statement")
public class BankStatement implements ITransferObject {

	private static final long serialVersionUID = 6904628009460137531L;

    private Integer id;
    private RegistryBank registryBank;
    private Date operationDate;
    private StatementConcept concept;
    private boolean payment;
    private double amount;
    private int document;
    private String reference1;
    private String reference2;
    private String description;
    private StatementStatus status;

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
    
    @ManyToOne
    @JoinColumn(name="rbank", nullable = false)
    @ForeignKey(name="FK_BANK_STATEMENT_RBANK")
    @Index(name="IDX_BANK_STATEMENT_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	@Column(name="operation_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

    @Column(nullable = false)
	public StatementConcept getConcept() {
		return concept;
	}
	public void setConcept(StatementConcept concept) {
		this.concept = concept;
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

	@Column(name = "status")
	public StatementStatus getStatus() {
		return status;
	}
	public void setStatus(StatementStatus status) {
		this.status = status;
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
				.append(this.concept,o.concept)
				.append(this.description,o.description)
				.append(this.document,o.document)
				.append(this.operationDate,o.operationDate)		
				.append(this.registryBank,o.registryBank)		
				.append(this.reference1,o.reference1)
				.append(this.reference2,o.reference2)
				.append(this.payment,o.payment)		
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
			.append(this.concept)
			.append(this.description)
			.append(this.document)
			.append(this.operationDate)		
			.append(this.registryBank)		
			.append(this.reference1)
			.append(this.reference2)
			.append(this.payment)		
			.append(this.status)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}