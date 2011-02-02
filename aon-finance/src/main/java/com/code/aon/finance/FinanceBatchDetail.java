package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.enumeration.FinanceStatus;

@Entity
@Table(name = "fbatch_detail")
public class FinanceBatchDetail implements ITransferObject {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private FinanceBatch financeBatch;
    private Finance finance;
    private double amount;
    private FinanceStatus status;

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
	public void setId(Integer id) {
		this.id = id;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fbatch", nullable = false)
    @ForeignKey(name="FK_FBATCH_DETAIL_FBATCH")
    @Index(name="IDX_FBATCH_DETAIL_FBATCH")                    
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="finance", nullable = false)
    @ForeignKey(name="FK_FBATCH_DETAIL_FINANCE")
    @Index(name="IDX_FBATCH_DETAIL_FINANCE")                
	public Finance getFinance() {
		return finance;
	}
	public void setFinance(Finance finance) {
		this.finance = finance;
	}

	@Column(nullable=true, precision=15, scale=3)
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public FinanceStatus getStatus() {
        return status;
    }
    public void setStatus(FinanceStatus status) {
        this.status = status;
    }
    
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final FinanceBatchDetail o = (FinanceBatchDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.amount,o.amount)
			.append(this.finance,o.finance)
			.append(this.financeBatch,o.financeBatch)
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
			.append(this.finance)
			.append(this.financeBatch)
			.append(this.status)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}