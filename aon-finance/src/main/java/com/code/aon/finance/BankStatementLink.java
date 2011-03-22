package com.code.aon.finance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.enumeration.StatementLinkSource;
import com.code.aon.finance.enumeration.StatementLinkStatus;

@Entity
@Table(name = "bank_statement_link")
public class BankStatementLink implements ITransferObject {

	private static final long serialVersionUID = -3251977952823406742L;

	private Integer id;
	private BankStatement bankStatement;
    private StatementLinkSource source;
    private int sourceId;
    private Date sourceDate;
    private double amount;
    private StatementLinkStatus status;
    private BankStatementLink linkedBankStatementLink;

    private ITransferObject sourceTo;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bank_statement", nullable = false)
    @ForeignKey(name="FK_BANK_STATEMENT_LINK_BANK_STATEMENT")
    @Index(name="IDX_BANK_STATEMENT_LINK_BANK_STATEMENT")            
	public BankStatement getBankStatement() {
		return bankStatement;
	}
	public void setBankStatement(BankStatement bankStatement) {
		this.bankStatement = bankStatement;
	}
	
    @Column(nullable = false)
	public StatementLinkSource getSource() {
		return source;
	}
	public void setSource(StatementLinkSource source) {
		this.source = source;
	}

    @Column(name="source_id", nullable = false)
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	@Column(name="source_date")
	@Temporal(TemporalType.DATE)
	public Date getSourceDate() {
		return sourceDate;
	}
	public void setSourceDate(Date sourceDate) {
		this.sourceDate = sourceDate;
	}

	@Column(precision=15, scale=2)
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public StatementLinkStatus getStatus() {
		return status;
	}
	public void setStatus(StatementLinkStatus status) {
		this.status = status;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="linked_bank_statement_link")
    @ForeignKey(name="FK_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK")
    @Index(name="IDX_BANK_STATEMENT_LINK_BANK_STATEMENT_LINK")
	public BankStatementLink getLinkedBankStatementLink() {
		return linkedBankStatementLink;
	}
	public void setLinkedBankStatementLink(BankStatementLink bankStatementLink) {
		this.linkedBankStatementLink = bankStatementLink;
	}
	
	@Transient
	public boolean isFinanceTracking() {
		return getSource() == StatementLinkSource.FINANCE_TRACKING;
	}
	@Transient
	public boolean isFinanceBatch() {
		return getSource() == StatementLinkSource.FINANCE_BATCH;
	}
	@Transient
	public boolean isBankConcept() {
		return getSource() == StatementLinkSource.BANK_CONCEPT;
	}
	@Transient
	public boolean isAccount() {
		return getSource() == StatementLinkSource.ACCOUNT;
	}

	@Transient
	public ITransferObject getSourceTo() throws ManagerBeanException {
		if (sourceTo == null) {
			if (isFinanceTracking()) {
				IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
				setSourceTo(trackingBean.get(getSourceId()));
			} else if (isFinanceBatch()) {
				IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
				setSourceTo(fBatchBean.get(getSourceId()));
			} else if (isBankConcept()) {
				IManagerBean conceptBean = BeanManager.getManagerBean(BankConcept.class);
				setSourceTo(conceptBean.get(getSourceId()));
			} else if (isAccount()) {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				setSourceTo(accountBean.get(Integer.toString(getSourceId())));
			}
		}

		return sourceTo;
	}
	public void setSourceTo(ITransferObject sourceTo) {
		this.sourceTo = sourceTo;
	}

	@Transient
	public Date getDate() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().getDueDate();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).getIssueDate();
		} else if (isBankConcept()) {
			return (getLinkedBankStatementLink() == null) ? getBankStatement().getOperationDate() : getLinkedBankStatementLink().getSourceDate();
		} else if (isAccount()) {
			return (getLinkedBankStatementLink() == null) ? getBankStatement().getOperationDate() : getLinkedBankStatementLink().getSourceDate();
		}
		return null;
	}
	@Transient
	public boolean isConfidential() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().isConfidential();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).isConfidential();
		}
		return getBankStatement().isConfidential();
	}
	@Transient
	public String getConcept() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().getDocumentNumber();
		} else if (isFinanceBatch()) {
			return Integer.toString(((FinanceBatch)getSourceTo()).getId());
		} else if (isBankConcept()) {
			Account bankConceptAccount = ((BankConcept)getSourceTo()).getAccount();
			return (bankConceptAccount != null) ? bankConceptAccount.getId() : "";
		} else if (isAccount()) {
			return ((Account)getSourceTo()).getId();
		}
		return null;
	}
	@Transient
	public String getDescription() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().getRegistryName();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).getDescription();
		} else if (isBankConcept()) {
			return ((BankConcept)getSourceTo()).getName();
		} else if (isAccount()) {
			return ((Account)getSourceTo()).getDescription();
		}
		return null;
	}
	@Transient
	public boolean isPayment() throws ManagerBeanException {
		if (isFinanceTracking()) {
			return ((FinanceTracking)getSourceTo()).getFinance().isPayment();
		} else if (isFinanceBatch()) {
			return ((FinanceBatch)getSourceTo()).isPayment();
		}
		return getBankStatement().isPayment();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final BankStatementLink o = (BankStatementLink) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.amount,o.amount)
				.append(this.bankStatement,o.bankStatement)
				.append(this.linkedBankStatementLink,o.linkedBankStatementLink)
				.append(this.source,o.source)
				.append(this.sourceDate,o.sourceDate)
				.append(this.sourceId,o.sourceId)
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
			.append(this.bankStatement)
			.append(this.linkedBankStatementLink)
			.append(this.source)		
			.append(this.sourceDate)		
			.append(this.sourceId)		
			.append(this.status)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}