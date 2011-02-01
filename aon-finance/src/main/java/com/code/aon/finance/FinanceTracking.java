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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name="finance_tracking")
public class FinanceTracking implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = 6433514583468654955L;

	private Integer id;
	private Finance finance;
	private Date trackingDate;
	private FinanceTrackingType type;
    private String description;
	private PayMethodTypeDetail payMethodTypeDetail;
    private RegistryBank registryBank;
    private BankStatementLink bankStatementLink;
	private double amount;
	private boolean recorded;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="finance", nullable=false)
    @ForeignKey(name="FK_FINANCE_TRACKING_FINANCE")
    @Index(name="IDX_FINANCE_TRACKING_FINANCE")                    
	public Finance getFinance() {
		return finance;
	}
	public void setFinance(Finance finance) {
		this.finance = finance;
	}

	@Column(name="tracking_date", nullable=false)
	@Temporal(TemporalType.DATE)
	public Date getTrackingDate() {
		return trackingDate;
	}
	public void setTrackingDate(Date trackingDate) {
		this.trackingDate = trackingDate;
	}

	@Column(nullable=false)
	public FinanceTrackingType getType() {
		return type;
	}
	public void setType(FinanceTrackingType type) {
		this.type = type;
	}

	@Column(length=64)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

	@ManyToOne
	@JoinColumn( name="pm_type_detail")
	@ForeignKey(name="FK_FINANCE_TRACKING_PM_TYPE_DETAIL_")
	@Index(name="IDX_FINANCE_TRACKING_PM_TYPE_DETAIL_")							
	public PayMethodTypeDetail getPayMethodTypeDetail() {
		return payMethodTypeDetail;
	}
	public void setPayMethodTypeDetail(PayMethodTypeDetail payMethodTypeDetail) {
		this.payMethodTypeDetail = payMethodTypeDetail;
	}

    @ManyToOne
    @JoinColumn(name="rbank")
    @ForeignKey(name="FK_FINANCE_TRACKING_RBANK")
    @Index(name="IDX_FINANCE_TRACKING_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

    @ManyToOne
    @JoinColumn(name="bank_statement_link")
    @ForeignKey(name="FK_FINANCE_TRACKING_BANK_STATEMENT_LINK")
    @Index(name="IDX_FINANCE_TRACKING_BANK_STATEMENT_LINK")            
	public BankStatementLink getBankStatementLink() {
		return bankStatementLink;
	}
	public void setBankStatementLink(BankStatementLink bankStatementLink) {
		this.bankStatementLink = bankStatementLink;
	}
	
	@Column(nullable=true, precision=15, scale=3)
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

	@Column(nullable = false)
	public boolean isRecorded() {
		return recorded;
	}
	public void setRecorded(boolean recorded) {
		this.recorded = recorded;
	}

	@Transient
	public boolean isRecordable() {
		return (type == FinanceTrackingType.PAID || type == FinanceTrackingType.RETURNED);
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getFinance().getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FinanceTracking o = (FinanceTracking) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.amount,o.amount)
			.append(this.description,o.description)
			.append(this.finance,o.finance)
			.append(this.payMethodTypeDetail,o.payMethodTypeDetail)
			.append(this.recorded,o.recorded)
			.append(this.registryBank,o.registryBank)
			.append(this.bankStatementLink,o.bankStatementLink)
			.append(this.trackingDate,o.trackingDate)
			.append(this.type,o.type)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.amount)
			.append(this.description)
			.append(this.finance)
			.append(this.payMethodTypeDetail)
			.append(this.recorded)
			.append(this.registryBank)
			.append(this.bankStatementLink)
			.append(this.trackingDate)
			.append(this.type)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}