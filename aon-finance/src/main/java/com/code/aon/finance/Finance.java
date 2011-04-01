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
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IScopable;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;

@Entity
@Table(name = "finance")
public class Finance implements ITransferObject, IBankAccountContainer, IConfidentialable, IScopable {
	
	private static final long serialVersionUID = 8289553641190577845L;

	private Integer id;
	private boolean payment;
	private Registry registry;
    private String registryName;
    private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private double amount;
	private double expenses;
	private String concept;
	private Invoice invoice;
	private Date dueDate;
	private PayMethod payMethod;
	private Bank bank;
	private BankAccount bankAccount;
	private FinanceStatus financeStatus;
	private SecurityLevel securityLevel;
    private Scope scope;

	private RegistryDocument registryFullDocument;

    public Finance() {
		this.dueDate = new Date();
	}

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

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable=false)
    @ForeignKey(name="FK_FINANCE_REGISTRY")
    @Index(name="IDX_FINANCE_REGISTRY")
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

    @Column(name="rname", length=128)
    public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	@Column(name="rdocument", length=16)
    public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}

	@Column(name="rdocument_type")
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public void setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
	}
	
	@Column(name="rdocument_country")
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public void setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
	}

	@Column(precision=15,scale=2)
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(precision=15,scale=2)
	public double getExpenses() {
		return expenses;
	}
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

	@Column(length=32)
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	@ManyToOne
    @JoinColumn(name="invoice")
    @ForeignKey(name="FK_FINANCE_INVOICE")
    @Index(name="IDX_FINANCE_INVOICE")            
	public Invoice getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}
	
	@Column(name="due_date")
	@Temporal(TemporalType.DATE)
	@Index(name="IDX_FINANCE_DUE_DATE")
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@ManyToOne
    @JoinColumn(name="pay_method")
    @ForeignKey(name="FK_FINANCE_PAY_METHOD")
    @Index(name="IDX_FINANCE_PAY_METHOD")    
	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	@ManyToOne
    @JoinColumn(name="bank")
    @ForeignKey(name="FK_FINANCE_BANK")
    @Index(name="IDX_FINANCE_BANK")        
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

	@Column(name = "status")
	public FinanceStatus getFinanceStatus() {
		return financeStatus;
	}
	public void setFinanceStatus(FinanceStatus financeStatus) {
		this.financeStatus = financeStatus;
	}

	@Column(name = "security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="scope", nullable = false)
    @ForeignKey(name="FK_FINANCE_SCOPE")
    @Index(name="IDX_FINANCE_SCOPE") 
    public Scope getScope() {
        return scope;
    }
    public void setScope(Scope scope) {
        this.scope = scope;
    }

	@Transient
	public double getTotalAmount(){
		return getAmount() + getExpenses();
	}
	
	@Transient
	public RegistryDocument getRegistryFullDocument() {
		if (registryFullDocument == null) {
			registryFullDocument = new RegistryDocument();
		}
		registryFullDocument.setDocument(getRegistryDocument());
		registryFullDocument.setType(getRegistryDocumentType());
		registryFullDocument.setCountry(getRegistryDocumentCountry());
		return registryFullDocument;
	}
	@Transient
	public boolean isValidRegistryDocument() {
		return getRegistryFullDocument().isValid();
	}
	@Transient
	public boolean isRegistryDocumentValidable() {
		return getRegistryFullDocument().isValidable();
	}
	
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Transient
	public boolean isEmptyInvoice() {
		return (getInvoice() == null || getInvoice().getId() == null);
	}

	@Transient
	public String getReferenceCode() {
		return (!isEmptyInvoice()) ? getInvoice().getReferenceCode() : null;
	}

	@Transient
	public String getDocumentNumber() {
		return (!isEmptyInvoice()) ? getInvoice().getDocumentNumber() : getConcept();
	}

	@Transient
	public boolean isNegotiableDocument(){
		return (getPayMethod() != null && getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT);
	}

	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Finance o = (Finance) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.amount,o.amount)
			.append(this.bank,o.bank)
			.append(this.bankAccount,o.bankAccount)
			.append(this.concept,o.concept)
			.append(this.dueDate,o.dueDate)
			.append(this.expenses,o.expenses)
			.append(this.financeStatus,o.financeStatus)
			.append(this.invoice,o.invoice)
			.append(this.payment,o.payment)
			.append(this.payMethod,o.payMethod)
			.append(this.registry,o.registry)
			.append(this.registryDocument,o.registryDocument)
			.append(this.registryDocumentType,o.registryDocumentType)
			.append(this.registryDocumentCountry,o.registryDocumentCountry)
			.append(this.registryName,o.registryName)
			.append(this.securityLevel,o.securityLevel)
			.append(this.scope,o.scope)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.amount)
			.append(this.bank)
			.append(this.bankAccount)
			.append(this.concept)
			.append(this.dueDate)
			.append(this.expenses)
			.append(this.financeStatus)
			.append(this.invoice)
			.append(this.payment)
			.append(this.payMethod)
			.append(this.registry)
			.append(this.registryDocument)
			.append(this.registryDocumentType)
			.append(this.registryDocumentCountry)
			.append(this.registryName)
			.append(this.securityLevel)
			.append(this.scope)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}