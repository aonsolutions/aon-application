package com.code.aon.employee;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
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
public class Salary implements ITransferObject, ISalary {
	
	private static final long serialVersionUID = 628669216993025202L;

	private Integer id;
	
	private Contract contract;
	
	private Date startDate;
	
	private Date endDate;
	
	private String address;
	
	private String employee;
	
	private String category;
	
	private Integer registration;
	
	private Integer totalDaysHours;
	
	private Double totalPayment;
	
	private Double totalDeduction;
	
	private Double totalLiquid;
	
	private Date broadcastDate;
	
	private Double remuneration;
	
	private Double extraPayProration;
	
	private Double total;
	
	private Double commonBase;
	
	private Double professionalBase;
	
	private Double overtimeBase;
	
	private Double irpfBase;
	
	private String quoteGroup;
	
	private Set<SalaryPayment> payments = new HashSet<SalaryPayment>();
	
	private Set<SalaryDeduction> deductions = new HashSet<SalaryDeduction>();

	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	@ManyToOne
	@JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_SALARY_RECCEIPT_CONTRACT")
	@Index(name = "FK_SALARY_RECCEIPT_CONTRACT")
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_date", nullable = false )
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_date", nullable = false )
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	@Column(length=64)
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	@Column(length=64)
	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	@Override
	@Column(length=64)
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	@Column( nullable = false)
	public Integer getRegistration() {
		return registration;
	}

	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	@Override
	@Column(name = "total_days_hours",  nullable = false)
	public Integer getTotalDaysHours() {
		return totalDaysHours;
	}

	public void setTotalDaysHours(Integer totalDaysHours) {
		this.totalDaysHours = totalDaysHours;
	}

	@Override
	@Column(name = "total_payment", precision = 15, scale = 3, nullable = false)
	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	@Override
	@Column(name = "total_deduction", precision = 15, scale = 3, nullable = false)
	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	@Override
	@Column(name = "total_liquid", precision = 15, scale = 3, nullable = false)
	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "broadcast_date", nullable = false )
	public Date getBroadcastDate() {
		return broadcastDate;
	}

	public void setBroadcastDate(Date broadcastDate) {
		this.broadcastDate = broadcastDate;
	}

	@Override
	@Column( precision = 15, scale = 3, nullable = false)
	public Double getRemuneration() {
		return remuneration;
	}

	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	@Override
	@Column(name = "extra_pay_proration", precision = 15, scale = 3, nullable = false)
	public Double getExtraPayProration() {
		return extraPayProration;
	}

	public void setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
	}

	@Override
	@Column(precision = 15, scale = 3, nullable = false)
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
	@Column(name = "common_base", precision = 15, scale = 3, nullable = false)
	public Double getCommonBase() {
		return commonBase;
	}

	public void setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
	}

	@Override
	@Column(name = "professional_base", precision = 15, scale = 3, nullable = false)
	public Double getProfessionalBase() {
		return professionalBase;
	}

	public void setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
	}

	@Override
	@Column(name = "overtime_base", precision = 15, scale = 3, nullable = false)
	public Double getOvertimeBase() {
		return overtimeBase;
	}

	public void setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
	}

	@Override
	@Column(name = "irpf_base", precision = 15, scale = 3, nullable = false)
	public Double getIrpfBase() {
		return irpfBase;
	}

	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}
	
	@Override
	@Column(name = "quote_group",length=2)
	public String getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryPayment> getSalaryPayments() {
		return payments;
	}

	public void setSalaryPayments(Set<SalaryPayment> payments) {
		this.payments = payments;
	}

	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	public Set<SalaryDeduction> getSalaryDeductions() {
		return deductions;
	}

	public void setSalaryDeductions(Set<SalaryDeduction> deductions) {
		this.deductions = deductions;
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
	
	@Override
	@Transient
	public ISalary getSalary(){
		return this;
	}
	
	@Transient
	public String getStartDateDay(Locale locale){
		return DateFormatUtils.format(startDate, "dd", locale);
	}
	@Transient
	public String getStartDateMonth(Locale locale){
		return DateFormatUtils.format(startDate, "MMMMM", locale);
	}
	@Transient
	public String getStartDateYear(Locale locale){
		return DateFormatUtils.format(startDate, "yyyy", locale);
	}
	@Transient
	public String getEndDateDay(Locale locale){
		return DateFormatUtils.format(endDate, "dd", locale);
	}
	@Transient
	public String getEndDateMonth(Locale locale){
		return DateFormatUtils.format(endDate, "MMMMM", locale);
	}
	@Transient
	public String getEndDateYear(Locale locale){
		return DateFormatUtils.format(endDate, "yyyy", locale);
	}

	@Override
	@Transient
	public Deductions getDeductions() {
		Deductions d = new Deductions( getSalaryDeductions());
		return d;
	}

	@Override
	@Transient
	public Payments getPayments() {
		Payments p = new Payments( getSalaryPayments());
		return p;
	}

}
