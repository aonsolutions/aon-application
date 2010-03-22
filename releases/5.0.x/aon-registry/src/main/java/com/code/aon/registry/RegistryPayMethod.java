package com.code.aon.registry;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;

/**
 * Transfer Object that represents the payMethod of a Registry.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 */
@Entity
@Table(name = "rpaymethod")
public class RegistryPayMethod implements ITransferObject, IPayMethod {

	private static final long serialVersionUID = -8346359421638707402L;

	/** The id. */
    private Integer id;

    /** The registry. */
    private Registry registry;

    /** The payment. */
    private PayMethod payment;

    /** The payment. */
    private RegistryBank rBank;

    /** The number of payments. */
    private int numberOfPayments;

    /** The days to first payment. */
    private int daysToFirstPayment;

    /** The days between payments. */
    private int daysBetweenPayments;

    /** The payment days. */
    private String paymentDays;

    private int[] paymentDaysArray;

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id.
     * 
     * @param id the id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the registry.
     * 
     * @return the registry
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name = "FK_RPAYMETHOD_REGISTRY")
    @Index(name = "IDX_RPAYMETHOD_REGISTRY")
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Sets the registry.
     * 
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * Gets the days between payments.
     * 
     * @return the days between payments
     */
    @Column(name = "days_between_pymnts")
    public int getDaysBetweenPayments() {
        return daysBetweenPayments;
    }

    /**
     * Sets the days between payments.
     * 
     * @param daysBetweenPayment the days between payments
     */
    public void setDaysBetweenPayments(int daysBetweenPayment) {
        this.daysBetweenPayments = daysBetweenPayment;
    }

    /**
     * Gets the days to first payment.
     * 
     * @return the days to first payment
     */
    @Column(name = "days_to_first_pymnt")
    public int getDaysToFirstPayment() {
        return daysToFirstPayment;
    }

    /**
     * Sets the days to first payment.
     * 
     * @param daysToFirstPayment the days to first payment
     */
    public void setDaysToFirstPayment(int daysToFirstPayment) {
        this.daysToFirstPayment = daysToFirstPayment;
    }

    /**
     * Gets the payment.
     * 
     * @return the payment
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pay_method", nullable = false)
    @ForeignKey(name = "FK_RPAYMETHOD_PAY_METHOD")
    @Index(name = "IDX_RPAYMETHOD_PAY_METHOD")
    public PayMethod getPayment() {
        return payment;
    }

    /**
     * Sets the payment.
     * 
     * @param payment the payment
     */
    public void setPayment(PayMethod payment) {
        this.payment = payment;
    }

    /**
     * Gets the bank.
     * 
     * @return the payment
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rbank")
    @ForeignKey(name = "FK_RPAYMETHOD_RBANK")
    @Index(name = "IDX_RPAYMETHOD_RBANK")
    public RegistryBank getRegistryBank() {
        return rBank;
    }

    /**
     * Sets the bank.
     * 
     * @param payment the payment
     */
    public void setRegistryBank(RegistryBank rBank) {
        this.rBank = rBank;
    }

    /**
     * Gets the number of payments.
     * 
     * @return the number of payments
     */
    @Column(name = "number_of_pymnts")
    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    /**
     * Sets the number of payments.
     * 
     * @param numberOfPayments the number of payments
     */
    public void setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }
    
    /**
     * Gets the payment days.
     * 
     * @return the payment days
     */
    @Column(name="pymnt_days", length=8)
    public String getPaymentDays() {
        return paymentDays;
    }
    
    /** The DELIM. */
    private final String DELIM = " ";
    
    /**
     * Sets the payment days.
     * 
     * @param paymentDays the payment days
     */
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryPayMethod o = (RegistryPayMethod) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.daysBetweenPayments, o.daysBetweenPayments)
				.append(this.daysToFirstPayment, o.daysToFirstPayment)				
				.append(this.numberOfPayments, o.numberOfPayments)
				.append(this.payment, o.payment)
				.append(this.paymentDays, o.paymentDays)
				.append(this.rBank, o.rBank)
				.append(this.registry, o.registry)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(daysBetweenPayments)
			.append(daysToFirstPayment)
			.append(id)
			.append(numberOfPayments)
			.append(payment)	
			.append(paymentDays)
			.append(rBank)
			.append(registry)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

	@Override
	@Transient
	public Bank getBank() {
		return getRegistryBank()==null?null:getRegistryBank().getBank();
	}

	@Transient
	@Override
	public BankAccount getBankAccount() {
		return getRegistryBank()==null?null:getRegistryBank().getBankAccount();
	} 
    
}