package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.code.aon.company.WorkPlace;
import com.esferalia.aon.calendar.Calendar;

@Entity
@Table(name = "payroll_workplace")
public class PayrollWorkPlace implements ITransferObject {

	private static final long serialVersionUID = 5071920992681713461L;

	private Integer id;
	private WorkPlace workPlace;
	private Agreement agreement;
	private Calendar calendar;
	private EnterpriseActivity enterpriseActivity;
	
	@Id     
	@GeneratedValue
    @Column(name="id", unique=true, nullable=false, length=4)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn( name="workplace", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_PAYROLL_WORKPLACE_WORKPLACE")
	@Index(name = "IDX_PAYROLL_WORKPLACE_WORKPLACE")
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@ManyToOne
    @JoinColumn( name="agreement", updatable = false )	
	@ForeignKey(name = "FK_PAYROLL_WORKPLACE_AGREEMENT")
	@Index(name = "IDX_PAYROLL_WORKPLACE_AGREEMENT")
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	@ManyToOne
	@JoinColumn( name="calendar", updatable = false )	
	@ForeignKey(name = "FK_PAYROLL_WORKPLACE_CALENDAR")
	@Index(name = "IDX_PAYROLL_WORKPLACE_CALENDAR")
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	
	@ManyToOne
	@JoinColumn( name="enterprise_activity", updatable = false )	
	@ForeignKey(name = "FK_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY")
	@Index(name = "IDX_PAYROLL_WORKPLACE_ENTERPRISE_ACTIVITY")
	public EnterpriseActivity getEnterpriseActivity() {
		return enterpriseActivity;
	}
	public void setEnterpriseActivity(EnterpriseActivity enterpriseActivity) {
		this.enterpriseActivity = enterpriseActivity;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final PayrollWorkPlace o = (PayrollWorkPlace) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.workPlace, o.workPlace)	
				.append(this.agreement, o.agreement)
				.append(this.calendar, o.calendar)
				.append(this.enterpriseActivity, o.enterpriseActivity)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(id)
				.append(this.workPlace)
				.append(this.agreement)
				.append(this.calendar)
				.append(this.enterpriseActivity)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	

	
	
}
