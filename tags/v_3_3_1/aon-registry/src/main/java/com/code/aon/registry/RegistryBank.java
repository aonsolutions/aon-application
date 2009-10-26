package com.code.aon.registry;

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
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a union between a Registry and a Bank.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 */
@Entity
@Table(name ="rbank")
public class RegistryBank implements ITransferObject,IBankAccountContainer {

	private static final long serialVersionUID = -8532648329534533542L;

	/** The id. */
    private Integer id;

    /** The registry. */
    private Registry registry;

    /** The bank. */
    private Bank bank;

    /** The bank account. */
    private BankAccount bankAccount;

    /** The sufix. */
    private String sufix;

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
     * Gets the bank account.
     * 
     * @return the bank account
     */
    @Column(name = "bank_account", length = 30)
    @Type(type="com.code.aon.config.hibernate.BankAccountType")
    public BankAccount getBankAccount() {
        return bankAccount;
    }

    /**
     * Sets the bank account.
     * 
     * @param bankAccount the bank account
     */
    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * Gets the bank.
     * 
     * @return the bank
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="bank", nullable = false)
    @ForeignKey(name = "FK_RBANK_BANK")
    @Index(name = "IDX_RBANK_BANK")
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank.
     * 
     * @param bank the bank
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Gets the registry.
     * 
     * @return the registry
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name = "FK_RBANK_REGISTRY")
    @Index(name = "IDX_RBANK_REGISTRY")
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Sets the registry.
     * 
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry ;
    }

	/**
	 * Gets the sufix
	 * 
	 * @return the sufix
	 */
    @Column(length=3)
	public String getSufix() {
		return sufix;
	}

	/**
	 * Sets the sufix
	 * 
	 * @param sufix the sufix to set
	 */
	public void setSufix(String sufix) {
		this.sufix = sufix;
	}
    
	@Transient
	public String getFullName() {
		StringBuilder sb = new StringBuilder();
		if (getBank() != null && !StringUtils.isEmpty(getBank().getName()))  {
			sb.append(StringUtils.abbreviate(getBank().getName(), 30));
			sb.append(" ");
		}
		if (getBankAccount() != null) {
			sb.append("[");
			sb.append(getBankAccount().toString());
			sb.append("]");
		}
		return sb.toString(); 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof RegistryBank) {
			RegistryBank o = (RegistryBank) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }
    
}