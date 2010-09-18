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
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name="finance_tracking")
public class FinanceTracking implements ITransferObject {
	
	private static final long serialVersionUID = 6433514583468654955L;

	private Integer id;
	private Finance finance;
	private Date trackingDate;
	private FinanceTrackingType type;
    private String description;
	private PayMethodTypeDetail payMethodTypeDetail;
    private RegistryBank registryBank;
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof FinanceTracking) {
			FinanceTracking o = (FinanceTracking) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}