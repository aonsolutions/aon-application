package com.code.aon.accounting;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.common.ITransferObject;

/**
 * Entity class for representing an account.
 * 
 * @author Consulting & Development. ecastellano - 22/01/2007
 * 
 */
@Entity
@Table(name = "amortization")
public class Amortization implements ITransferObject {

	private static final long serialVersionUID = 7370145682918674752L;

	private Integer id;
	private String description;
    private AmortizationType amortizationType;
    private Date initialDate;
    private Date deadline;
    private Double amount;
    private AmortizationPeriod feePeriod;
    private Double saleAmount;
	private String comments;
	
	private List<AmortizationDetail> details;

	@Id
    @GeneratedValue	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(nullable = false, length = 64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="amortization_type", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_AMORTIZATION_TYPE")
	@Index(name = "IDX_AMORTIZATION_AMORTIZATION_TYPE")	
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}

	@Column(name = "initial_date", nullable = false)
	@Temporal(TemporalType.DATE)	
	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	@Column
	@Temporal(TemporalType.DATE)
	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	@Column(nullable = false)
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
	
	@Column(name = "fee_period", nullable = false)
    public AmortizationPeriod getFeePeriod() {
        return feePeriod;
    }

    public void setFeePeriod(AmortizationPeriod feePeriod) {
        this.feePeriod = feePeriod;
    }
	
	@Column(name = "sale_amount")
    public Double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        this.saleAmount = saleAmount;
    }
	
	
	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@OneToMany(mappedBy = "amortization", cascade={CascadeType.REMOVE})
	public List<AmortizationDetail> getDetails() {
		return details;
	}

	public void setDetails(List<AmortizationDetail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Amortization o = (Amortization) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getAmortizationType(), o.getAmortizationType())
			.append(this.getAmount(), o.getAmount())
			.append(this.getDescription(), o.getDescription())
			.append(this.getInitialDate(), o.getInitialDate())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getAmortizationType())
			.append(this.getAmount())
			.append(this.getDescription())
			.append(this.getInitialDate())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
		.append("id",this.getId())
		.append("description",this.getDescription())
		.append("amortizationType",this.getAmortizationType())
		.append("initialDate",this.getInitialDate()).toString();
	}

}