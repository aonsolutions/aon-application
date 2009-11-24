package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.supplier.Supplier;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

@Entity
@Table(name="income")
public class Income implements ITransferObject, IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod {
	
	private static final long serialVersionUID = -2473825467680303195L;
	private static final Logger LOGGER = Logger.getLogger(Income.class.getName());
    private static final String DELIM = " ";
    
	public Income() {
		this.issueTime = new Date();
	}

	private Integer id;
    private String series;
    private int number;
	private Supplier supplier;
	private RegistryAddress registryAddress;
	private Date issueTime;
	private PayMethod payMethod;
	private SecurityLevel securityLevel;
	private IncomeStatus status;
	private WorkPlace workPlace;
	private Scope scope;
    private int numberOfPayments;
    private int daysToFirstPayment;
    private int daysBetweenPayments;
    private String paymentDays;
    private int[] paymentDaysArray;
	private Bank bank;
	private BankAccount bankAccount;
	private Set<IncomeDetail> lines = new HashSet<IncomeDetail>();

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length=5)
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	
	@Column(nullable = false)
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = "" + getNumber();
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	@ManyToOne
    @JoinColumn( name="supplier", nullable = false )
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	@ManyToOne
	@JoinColumn( name="address" )
	public RegistryAddress getRegistryAddress() {
		return registryAddress;
	}
	public void setRegistryAddress(RegistryAddress registryAddress) {
		this.registryAddress = registryAddress;
	}

	@Column(name="issue_time")
	public Date getIssueTime() {
		return issueTime;
	}
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	@ManyToOne
	@JoinColumn( name="pay_method" )
	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public IncomeStatus getStatus() {
		return status;
	}
	public void setStatus(IncomeStatus status) {
		this.status = status;
	}

	@ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

    @ManyToOne
    @JoinColumn(name="scope", nullable = false)
	public Scope getScope() {
		return scope;
	}
	public void setScope(Scope scope) {
		this.scope = scope;
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

	@OneToMany(mappedBy = "income", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<IncomeDetail> getLines() {
		return this.lines;
	}
	public void setLines( Set<IncomeDetail> lines ) {
		this.lines = lines;
	}
	
	@Transient
	public Date getDate() {
		return this.issueTime;
	}

	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Transient
	public PayMethod getPayment() {
		return payMethod;
	}

	@Transient
	@SuppressWarnings("unchecked")
	public List getDetailList() {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), getId());
			return incomeDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining incomeDetail list", e);
		}
		return null;
	}
	
	@Transient
	@SuppressWarnings("unchecked")
	public List getOrderedDetailList() {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), getId());
			criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
			return incomeDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining offerDetail list", e);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Income o = (Income) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.series, o.series)
			.append(this.number, o.number)
			.append(this.supplier, o.supplier)
			.append(this.registryAddress, o.registryAddress)
			.append(this.issueTime, o.issueTime)
			.append(this.payMethod, o.payMethod)
			.append(this.securityLevel, o.securityLevel)
			.append(this.status, o.status)
			.append(this.workPlace, o.workPlace)
			.append(this.scope, o.scope)
			.append(this.numberOfPayments, o.numberOfPayments)
			.append(this.daysToFirstPayment, o.daysToFirstPayment)
			.append(this.daysBetweenPayments, o.daysBetweenPayments)
			.append(this.paymentDays, o.paymentDays)
			.append(this.bank, o.bank)
			.append(this.bankAccount, o.bankAccount)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(series)
			.append(number)
			.append(supplier)
			.append(registryAddress)
			.append(issueTime)
			.append(payMethod)
			.append(securityLevel)
			.append(status)
			.append(workPlace)
			.append(scope)
			.append(numberOfPayments)
			.append(daysToFirstPayment)
			.append(daysBetweenPayments)
			.append(paymentDays)
			.append(bank)
			.append(bankAccount)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}