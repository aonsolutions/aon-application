package com.code.aon.employee;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents the salary.
 * 
 */
@Entity
@Table(name="salary")
public class Salary implements ITransferObject {
	
	private static final long serialVersionUID = 628669216993025202L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SALARY_RECCEIPT_CONTRACT")
	@Index(name = "FK_SALARY_RECCEIPT_CONTRACT")
	private Contract contract;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date", nullable = false )
	private Date endDate;
	
	@Column(length=64)
	private String address;
	
	@Column(length=64)
	private String employee;
	
	@Column(length=64)
	private String category;
	
	@Column( nullable = false)
	private Integer registration;
	
	@Column(name = "total_days_hours",  nullable = false)
	private Integer totalDaysHours;
	
	@Column(name = "total_payment", precision = 15, scale = 3, nullable = false)
	private Double totalPayment;
	
	@Column(name = "total_deduction", precision = 15, scale = 3, nullable = false)
	private Double totalDeduction;
	
	@Column(name = "total_liquid", precision = 15, scale = 3, nullable = false)
	private Double totalLiquid;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "broadcast_date", nullable = false )
	private Date broadcastDate;
	
	@Column( precision = 15, scale = 3, nullable = false)
	private Double remuneration;
	
	@Column(name = "extra_pay_proration", precision = 15, scale = 3, nullable = false)
	private Double extraPayProration;
	
	@Column(precision = 15, scale = 3, nullable = false)
	private Double total;
	
	@Column(name = "common_base", precision = 15, scale = 3, nullable = false)
	private Double commonBase;
	
	@Column(name = "professional_base", precision = 15, scale = 3, nullable = false)
	private Double professionalBase;
	
	@Column(name = "overtime_base", precision = 15, scale = 3, nullable = false)
	private Double overtimeBase;
	
	@Column(name = "irpf_base", precision = 15, scale = 3, nullable = false)
	private Double irpfBase;

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getRegistration() {
		return registration;
	}

	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	public Integer getTotalDaysHours() {
		return totalDaysHours;
	}

	public void setTotalDaysHours(Integer totalDaysHours) {
		this.totalDaysHours = totalDaysHours;
	}

	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	public Date getBroadcastDate() {
		return broadcastDate;
	}

	public void setBroadcastDate(Date broadcastDate) {
		this.broadcastDate = broadcastDate;
	}

	public Double getRemuneration() {
		return remuneration;
	}

	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	public Double getExtraPayProration() {
		return extraPayProration;
	}

	public void setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public Double getCommonBase() {
		return commonBase;
	}

	public void setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
	}

	public Double getProfessionalBase() {
		return professionalBase;
	}

	public void setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
	}

	public Double getOvertimeBase() {
		return overtimeBase;
	}

	public void setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
	}

	public Double getIrpfBase() {
		return irpfBase;
	}

	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}
	
	@Transient
	public String getStartDateDay(){
		return DateFormatUtils.format(startDate, "dd", Locale.getDefault());
	}
	@Transient
	public String getStartDateMonth(){
		return DateFormatUtils.format(startDate, "MM", Locale.getDefault());
	}
	@Transient
	public String getStartDateYear(){
		return DateFormatUtils.format(startDate, "yyyy", Locale.getDefault());
	}
	@Transient
	public String getEndDateDay(){
		return DateFormatUtils.format(endDate, "dd", Locale.getDefault());
	}
	@Transient
	public String getEndDateMonth(){
		return DateFormatUtils.format(endDate, "MM", Locale.getDefault());
	}
	@Transient
	public String getEndDateYear(){
		return DateFormatUtils.format(endDate, "yyyy", Locale.getDefault());
	}

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Salary o = (Salary) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)
				.append(this.startDate, o.startDate)
				.append(this.endDate, o.endDate)
				.append(this.address, o.address)
				.append(this.employee, o.employee)
				.append(this.category, o.category)
				.append(this.registration, o.registration)
				.append(this.totalDaysHours, o.totalDaysHours)
				.append(this.totalPayment, o.totalPayment)
				.append(this.totalDeduction, o.totalDeduction)
				.append(this.totalLiquid, o.totalLiquid)
				.append(this.broadcastDate, o.broadcastDate)
				.append(this.remuneration, o.remuneration)
				.append(this.extraPayProration, o.extraPayProration)
				.append(this.total, o.total)
				.append(this.commonBase, o.commonBase)
				.append(this.professionalBase, o.professionalBase)
				.append(this.overtimeBase, o.overtimeBase)
				.append(this.irpfBase, o.irpfBase)
				.isEquals();			
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(contract)
			.append(startDate)
			.append(endDate)
			.append(address)
			.append(employee)
			.append(category)
			.append(registration)
			.append(totalDaysHours)
			.append(totalPayment)
			.append(totalDeduction)
			.append(totalLiquid)
			.append(broadcastDate)
			.append(remuneration)
			.append(extraPayProration)
			.append(total)
			.append(commonBase)
			.append(professionalBase)
			.append(overtimeBase)
			.append(irpfBase)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	public Salary getSalary(){
		return this;
	}

}
