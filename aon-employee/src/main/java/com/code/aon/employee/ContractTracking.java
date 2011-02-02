package com.code.aon.employee;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.employee.enumeration.ContractTrackingType;


@Entity
@Table(name = "contract_tracking")
public class ContractTracking implements ITransferObject {

	private static final long serialVersionUID = -2564874457439900595L;

	private Integer id;
	private Contract contract;
	private Date date;
	private ContractTrackingType type;
	private Double duration;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_CONTRACT_TRACKING_CONTRACT")
	@Index(name = "IDX_CONTRACT_TRACKING_CONTRACT")
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="type")
	public ContractTrackingType getType() {
		return type;
	}

	public void setType(ContractTrackingType type) {
		this.type = type;
	}

	@Column(name="duration")
	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof ContractTracking) {
			ContractTracking o = (ContractTracking) obj;
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