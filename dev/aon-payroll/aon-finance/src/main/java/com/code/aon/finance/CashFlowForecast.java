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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.registry.RegistryBank;

@Entity
@Table(name = "cashflow_forecast")
public class CashFlowForecast implements ITransferObject {

	private static final long serialVersionUID = -7024476800714103863L;
	
    private Integer id;
	private boolean payment;
    private Date startDate;
    private Date dueDate;
    private String description;
    private RegistryBank registryBank;
    private double amount;
    private int paymentDay;
	private boolean january;
	private boolean february;
	private boolean march;
	private boolean april;
	private boolean may;
	private boolean june;
	private boolean july;
	private boolean august;
	private boolean september;
	private boolean october;
	private boolean november;
	private boolean december;

	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

	@Column(nullable = false)
	public boolean isPayment() {
		return payment;
	}
	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	@Column(name="start_date", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

    @Column(name="due_date")
	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
    @Column(name="description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rbank")
    @ForeignKey(name="FK_BANK_STATEMENT_RBANK")
    @Index(name="IDX_BANK_STATEMENT_RBANK")            
	public RegistryBank getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

    @Column(precision=15, scale=2, nullable = false)
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(name="payment_day", nullable = false)
	public int getPaymentDay() {
		return paymentDay;
	}
	public void setPaymentDay(int paymentDay) {
		this.paymentDay = paymentDay;
	}

	@Column(nullable = false)
	public boolean isJanuary() {
		return january;
	}
	public void setJanuary(boolean january) {
		this.january = january;
	}

	@Column(nullable = false)
	public boolean isFebruary() {
		return february;
	}
	public void setFebruary(boolean february) {
		this.february = february;
	}

	@Column(nullable = false)
	public boolean isMarch() {
		return march;
	}
	public void setMarch(boolean march) {
		this.march = march;
	}

	@Column(nullable = false)
	public boolean isApril() {
		return april;
	}
	public void setApril(boolean april) {
		this.april = april;
	}

	@Column(nullable = false)
	public boolean isMay() {
		return may;
	}
	public void setMay(boolean may) {
		this.may = may;
	}

	@Column(nullable = false)
	public boolean isJune() {
		return june;
	}
	public void setJune(boolean june) {
		this.june = june;
	}

	@Column(nullable = false)
	public boolean isJuly() {
		return july;
	}
	public void setJuly(boolean july) {
		this.july = july;
	}

	@Column(nullable = false)
	public boolean isAugust() {
		return august;
	}
	public void setAugust(boolean august) {
		this.august = august;
	}

	@Column(nullable = false)
	public boolean isSeptember() {
		return september;
	}
	public void setSeptember(boolean september) {
		this.september = september;
	}

	@Column(nullable = false)
	public boolean isOctober() {
		return october;
	}
	public void setOctober(boolean october) {
		this.october = october;
	}

	@Column(nullable = false)
	public boolean isNovember() {
		return november;
	}
	public void setNovember(boolean november) {
		this.november = november;
	}

	@Column(nullable = false)
	public boolean isDecember() {
		return december;
	}
	public void setDecember(boolean december) {
		this.december = december;
	}
	
	@Transient
	public boolean isUndated() {
		return (!january && !february && !march && !april && !may && !june && !july && 
				!august && !september && !october && !november && !december);
	}

	@Transient
	public boolean[] getMonths() {
		return new boolean[]{january,february,march,april,may,june,july,august,september,october,november,december};
	}
	@Transient
	public void setMonths(boolean[] months) {
		setJanuary(months[0]);
		setFebruary(months[1]);
		setMarch(months[2]);
		setApril(months[3]);
		setMay(months[4]);
		setJune(months[5]);
		setJuly(months[6]);
		setAugust(months[7]);
		setSeptember(months[8]);
		setOctober(months[9]);
		setNovember(months[10]);
		setDecember(months[11]);
	}

	@Transient
	public void initializeMonths() {
		setJanuary(false);
		setFebruary(false);
		setMarch(false);
		setApril(false);
		setMay(false);
		setJune(false);
		setJuly(false);
		setAugust(false);
		setSeptember(false);
		setOctober(false);
		setNovember(false);
		setDecember(false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CashFlowForecast o = (CashFlowForecast) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.payment,o.payment)
				.append(this.startDate,o.startDate)
				.append(this.dueDate,o.dueDate)
				.append(this.description,o.description)
				.append(this.registryBank,o.registryBank)
				.append(this.amount,o.amount)
				.append(this.paymentDay,o.paymentDay)
				.append(this.january,o.january)
				.append(this.february,o.february)
				.append(this.march,o.march)
				.append(this.april,o.april)
				.append(this.may,o.may)
				.append(this.june,o.june)
				.append(this.july,o.july)
				.append(this.august,o.august)
				.append(this.september,o.september)
				.append(this.october,o.october)
				.append(this.november,o.november)
				.append(this.december,o.december)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.payment)
			.append(this.startDate)
			.append(this.dueDate)
			.append(this.description)
			.append(this.registryBank)
			.append(this.amount)
			.append(this.paymentDay)
			.append(this.january)
			.append(this.february)
			.append(this.march)
			.append(this.april)
			.append(this.may)
			.append(this.june)
			.append(this.july)
			.append(this.august)
			.append(this.september)
			.append(this.october)
			.append(this.november)
			.append(this.december)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}
