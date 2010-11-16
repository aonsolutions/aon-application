package com.code.aon.employee;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
import com.code.aon.employee.enumeration.DeductionType;
import com.code.aon.employee.enumeration.PaymentType;

/**
 * Transfer Object that represents the salary.
 * 
 */
@Entity
@Table(name="salary")
public class Salary implements ITransferObject, ISalary {
	
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
	
	@Column(name = "quote_group",length=2)
	private String quoteGroup;
	
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	private Set<SalaryPayment> payments = new HashSet<SalaryPayment>();
	
	@OneToMany(mappedBy = "salary", cascade={CascadeType.REMOVE})
	private Set<SalaryDeduction> deductions = new HashSet<SalaryDeduction>();

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Override
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	@Override
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public Integer getRegistration() {
		return registration;
	}

	public void setRegistration(Integer registration) {
		this.registration = registration;
	}

	@Override
	public Integer getTotalDaysHours() {
		return totalDaysHours;
	}

	public void setTotalDaysHours(Integer totalDaysHours) {
		this.totalDaysHours = totalDaysHours;
	}

	@Override
	public Double getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}

	@Override
	public Double getTotalDeduction() {
		return totalDeduction;
	}

	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}

	@Override
	public Double getTotalLiquid() {
		return totalLiquid;
	}

	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}

	@Override
	public Date getBroadcastDate() {
		return broadcastDate;
	}

	public void setBroadcastDate(Date broadcastDate) {
		this.broadcastDate = broadcastDate;
	}

	@Override
	public Double getRemuneration() {
		return remuneration;
	}

	public void setRemuneration(Double remuneration) {
		this.remuneration = remuneration;
	}

	@Override
	public Double getExtraPayProration() {
		return extraPayProration;
	}

	public void setExtraPayProration(Double extraPayProration) {
		this.extraPayProration = extraPayProration;
	}

	@Override
	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	@Override
	public Double getCommonBase() {
		return commonBase;
	}

	public void setCommonBase(Double commonBase) {
		this.commonBase = commonBase;
	}

	@Override
	public Double getProfessionalBase() {
		return professionalBase;
	}

	public void setProfessionalBase(Double professionalBase) {
		this.professionalBase = professionalBase;
	}

	@Override
	public Double getOvertimeBase() {
		return overtimeBase;
	}

	public void setOvertimeBase(Double overtimeBase) {
		this.overtimeBase = overtimeBase;
	}

	@Override
	public Double getIrpfBase() {
		return irpfBase;
	}

	public void setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
	}
	
	@Override
	public String getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public Set<SalaryPayment> getSalaryPayments() {
		return payments;
	}

	public void setSalaryPayments(Set<SalaryPayment> payments) {
		this.payments = payments;
	}

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
	public Deductions getDeductions() {
		Deductions d = new Deductions();
		for(Object o: getSalaryDeductions().toArray()){
			SalaryDeduction sd = (SalaryDeduction) o;
			if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
				d.setCommonContingency(sd);
			} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
				d.setUnemployment(sd);
			} else if (sd.getType() == DeductionType.JOB_TRAINING) {
				d.setJobTraining(sd);
			} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
				d.setStructuralOvertime(sd);
			} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
				d.setNonStructuralOvertime(sd);
			} else if (sd.getType() == DeductionType.IRPF) {
				d.setIrpf(sd);
			} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
				d.setAdvancePayment(sd);
			} else if (sd.getType() == DeductionType.IN_KIND) {
				d.setInKid(sd);
			} else if (sd.getType() == DeductionType.OTHER) {
				d.setOther(sd);
			}
		}
		return d;
	}

	@Override
	public Payments getPayments() {
		Payments p = new Payments();
		p.setSalarySupplements(new LinkedList<IPayment>());
		p.setComplementarySuply(new LinkedList<IPayment>());
		for(Object o: getSalaryPayments().toArray()){
			SalaryPayment sp = (SalaryPayment) o;
			if (sp.getType() == PaymentType.BASE_SALARY) {
				p.setBaseSalary(sp);
			} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
				 p.getSalarySupplements().add(sp);
			} else if (sp.getType() == PaymentType.OVERTIME_HOURS) {
				p.setOvertimeHours(sp);
			} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
				p.setSpecialBonuses(sp);
			} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
				p.setSalaryInKid(sp);
			} else if (sp.getType() == PaymentType.COMPENSATION_SUPLY) {
				p.getComplementarySuply().add(sp);
			} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
				p.setSpecialSecurityBenefits(sp);
			} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
				p.setMovingCompensation(sp);
			} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
				p.setOtherNonWage(sp);
			}
		}
		return p;
	}

}
