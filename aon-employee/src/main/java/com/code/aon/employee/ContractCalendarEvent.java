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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.employee.enumeration.ContractCalendarEventType;

@Entity
@Table(name = "contract_calendar_event")
public class ContractCalendarEvent implements ITransferObject {

	private static final long serialVersionUID = -2564874457439900595L;

	private Integer id;
	private Contract contract;
	private Date date;
	private ContractCalendarEventType type;
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
	@ForeignKey(name = "FK_CONTRACT_CALENDAR_EVENT_CONTRACT")
	@Index(name = "IDX_CONTRACT_CALENDAR_EVENT_CONTRACT")
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
	public ContractCalendarEventType getType() {
		return type;
	}

	public void setType(ContractCalendarEventType type) {
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
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ContractCalendarEvent o = (ContractCalendarEvent) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.date, o.date)
				.append(this.type, o.type)
				.append(this.duration, o.duration)
				.isEquals();	
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(contract)
			.append(date)
			.append(type)
			.append(duration)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}