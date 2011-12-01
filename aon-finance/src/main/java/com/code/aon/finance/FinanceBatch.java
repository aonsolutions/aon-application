package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "fbatch")
public class FinanceBatch implements ITransferObject,IConfidentialable {
	
	private static final long serialVersionUID = 804673961013565165L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceBatch.class.getName());

    private Integer id;
    private String description;
    private Date issueDate;
    private FinanceBatchType financeBatchType;
    private FinanceBatchStatus financeBatchStatus;
    private RegistryBank registryBank;
    private BankStatementLink bankStatementLink;
    private boolean payment;
    private SecurityLevel securityLevel;
	private Set<FinanceBatchDetail> lines = new HashSet<FinanceBatchDetail>();

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
	@Column(length=32)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name="issue_date")
	@Temporal(TemporalType.DATE)
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	@Column(name = "type")
	public FinanceBatchType getFinanceBatchType() {
		return financeBatchType;
	}
	public void setFinanceBatchType(FinanceBatchType financeBatchType) {
		this.financeBatchType = financeBatchType;
	}

	@Column(name = "status")
	public FinanceBatchStatus getFinanceBatchStatus() {
		return financeBatchStatus;
	}
	public void setFinanceBatchStatus(FinanceBatchStatus financeBatchStatus) {
		this.financeBatchStatus = financeBatchStatus;
	}

    @ManyToOne
    @JoinColumn(name="rbank")
    @ForeignKey(name="FK_FBATCH_RBANK")
    @Index(name="IDX_FBATCH_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
    @ManyToOne
    @JoinColumn(name="bank_statement_link")
    @ForeignKey(name="FK_FBATCH_BANK_STATEMENT_LINK")
    @Index(name="IDX_FBATCH_BANK_STATEMENT_LINK")            
	public BankStatementLink getBankStatementLink() {
		return bankStatementLink;
	}
	public void setBankStatementLink(BankStatementLink bankStatementLink) {
		this.bankStatementLink = bankStatementLink;
	}
	
    @Column(nullable = false)
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
    @Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    
	@Transient
	@Override
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	@Override
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

    @OneToMany(mappedBy = "financeBatch", cascade={CascadeType.REMOVE})
	public Set<FinanceBatchDetail> getLines() {
		return this.lines;
	}
	public void setLines( Set<FinanceBatchDetail> lines ) {
		this.lines = lines;
	}

	@Transient
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
	        criteria.addOrder(financeBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE));
	        criteria.addOrder(financeBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_CONCEPT));
			return financeBatchDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining financeBatchDetail list", e);
		}
		return null;
	}

	@Transient
	public Integer getFinanceBatchTotalDetails(){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
			return fBatchDetailBean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining fbatch total details", e);
		}
		return new Integer(0);
	}

	@Transient
	public Double getFinanceBatchTotalAmount(){
		try {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
			Projection projection = Projection.sum(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_AMOUNT));
			Object value = fBatchDetailBean.getUniqueResult(projection, criteria);
			if (value != null) {
				return (Double)value;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining fbatch total amount", e);
		}
		return new Double(0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FinanceBatch o = (FinanceBatch) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description,o.description)
				.append(this.issueDate,o.issueDate)		
				.append(this.financeBatchType,o.financeBatchType)
				.append(this.financeBatchStatus,o.financeBatchStatus)		
				.append(this.registryBank,o.registryBank)		
				.append(this.bankStatementLink,o.bankStatementLink)		
				.append(this.payment,o.payment)		
				.append(this.securityLevel,o.securityLevel)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.description)
			.append(this.issueDate)		
			.append(this.financeBatchType)
			.append(this.financeBatchStatus)		
			.append(this.registryBank)		
			.append(this.bankStatementLink)		
			.append(this.payment)		
			.append(this.securityLevel)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}