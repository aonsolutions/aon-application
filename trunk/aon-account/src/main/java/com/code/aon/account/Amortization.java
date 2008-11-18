package com.code.aon.account;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.account.enumeration.AmortizationPeriod;
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
	public AmortizationType getAmortizationType() {
		return amortizationType;
	}

	public void setAmortizationType(AmortizationType amortizationType) {
		this.amortizationType = amortizationType;
	}

	@Column(name = "initial_date", nullable = false)
	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	@Column
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
	
	
	@Column(name="comments",length=65535)
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Amortization) {
			Amortization account = (Amortization) obj;
			if (ObjectUtils.equals(getId(), account.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}