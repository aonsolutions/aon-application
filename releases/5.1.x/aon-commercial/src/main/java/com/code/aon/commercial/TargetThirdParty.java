package com.code.aon.commercial;

import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Tariff;

@Entity
@Table(name="target_third_party")
public class TargetThirdParty implements ITransferObject, IBankAccountContainer, IPayMethod {

	private static final long serialVersionUID = -9181775363113298274L;

    private static final String DELIM = " ";

    private Integer id;
	private Target target;
	private Target thirdParty;
	private String targetExternalCode;
    private Tariff tariff;
    private PayMethod payMethod;
    private int numberOfPayments;
    private int daysToFirstPayment;
    private int daysBetweenPayments;
    private String paymentDays;
    private int[] paymentDaysArray;
	private Bank bank;
	private BankAccount bankAccount;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="target", nullable=false, updatable=false)
	@ForeignKey(name = "FK_TARGET_THIRD_PARTY_TARGET")
	@Index(name = "IDX_TARGET_THIRD_PARTY_TARGET")
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="third_party", nullable=false)
	@ForeignKey(name = "FK_TARGET_THIRD_PARTY_THIRD_PARTY")
	@Index(name = "IDX_TARGET_THIRD_PARTY_THIRD_PARTY")
	public Target getThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(Target thirdParty) {
		this.thirdParty = thirdParty;
	}

	@Column(name="target_external_code",length=15)
	public String getTargetExternalCode() {
		return targetExternalCode;
	}

	public void setTargetExternalCode(String targetExternalCode) {
		this.targetExternalCode = targetExternalCode;
	}

	@ManyToOne
	@JoinColumn(name="tariff")
	@ForeignKey(name = "FK_TARGET_THIRD_PARTY_TARIFF")
	@Index(name = "IDX_TARGET_THIRD_PARTY_TARIFF")
	public Tariff getTariff() {
		return tariff;
	}

	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	@ManyToOne
	@JoinColumn(name="pay_method")
	@ForeignKey(name = "FK_TARGET_THIRD_PARTY_PAY_METHOD")
	@Index(name = "IDX_TARGET_THIRD_PARTY_PAY_METHOD")
	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

    @Column(name = "number_of_pymnts")
    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    public void setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }
    
    @Column(name = "days_to_first_pymnt")
    public int getDaysToFirstPayment() {
        return daysToFirstPayment;
    }

    public void setDaysToFirstPayment(int daysToFirstPayment) {
        this.daysToFirstPayment = daysToFirstPayment;
    }

    @Column(name = "days_between_pymnts")
    public int getDaysBetweenPayments() {
        return daysBetweenPayments;
    }

    public void setDaysBetweenPayments(int daysBetweenPayment) {
        this.daysBetweenPayments = daysBetweenPayment;
    }

    @Column(name="pymnt_days", length=8)
    public String getPaymentDays() {
        return paymentDays;
    }
    
    public void setPaymentDays(String paymentDays) {
        this.paymentDays = paymentDays;
        StringTokenizer strTknzr = new StringTokenizer(this.paymentDays,DELIM);
    	int[] values = new int[strTknzr.countTokens()];
    	for (int i = 0; i < values.length; i++){
    		values[i] = Integer.parseInt(strTknzr.nextToken());
    	}    	
        this.paymentDaysArray = values;
    }

    @Transient
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	@ManyToOne
    @JoinColumn(name="bank")
	@ForeignKey(name = "FK_TARGET_THIRD_PARTY_BANK")
	@Index(name = "IDX_TARGET_THIRD_PARTY_BANK")
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	@Column(name="bank_account", length=30)
	@Type(type="com.code.aon.config.hibernate.BankAccountType")
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Transient
	public PayMethod getPayment() {
		return payMethod;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TargetThirdParty o = (TargetThirdParty) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.bank, o.bank)
				.append(this.bankAccount, o.bankAccount)				
				.append(this.daysBetweenPayments, o.daysBetweenPayments)				
				.append(this.daysToFirstPayment, o.daysToFirstPayment)
				.append(this.numberOfPayments, o.numberOfPayments)				
				.append(this.paymentDays, o.paymentDays)
				.append(this.payMethod, o.payMethod)				
				.append(this.thirdParty, o.thirdParty)				
				.append(this.targetExternalCode, o.targetExternalCode)				
				.append(this.target, o.target)				
				.append(this.tariff, o.tariff)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(bank)
			.append(bankAccount)
			.append(daysBetweenPayments)
			.append(daysToFirstPayment)
			.append(numberOfPayments)
			.append(paymentDays)
			.append(payMethod)
			.append(id)			
			.append(thirdParty)
			.append(targetExternalCode)
			.append(target)
			.append(tariff)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}