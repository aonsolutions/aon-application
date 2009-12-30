package com.code.aon.finance;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents a Finance Batch.
 * 
 * @author Consulting & Development. Inigo Gayarre - 05-oct-2005
 * @since 1.0
 */
@Entity
@Table(name = "fbatch")
public class FinanceBatch implements ITransferObject {
	
	private static final Logger LOGGER = Logger.getLogger(FinanceBatch.class.getName());

    /** The id. */
    private Integer id;

    /** The description. */
    private String description;
    
    /** The issue date. */
    private Date issueDate;
    
    /** The finance batch type. */
    private FinanceBatchType financeBatchType;

    /** The finance batch status. */
    private FinanceBatchStatus financeBatchStatus;

    /** The registry bank. */
    private RegistryBank registryBank;
    
    /** The payment. */
    private boolean payment;

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
	 * Gets the registry bank.
	 * 
	 * @return the registry bank
	 */
    @ManyToOne
    @JoinColumn(name="rbank")
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	/**
	 * Sets the registry bank.
	 * 
	 * @param registryBank the registry bank
	 */
	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the finance batch status.
	 * 
	 * @return the finance batch status
	 */
	@Column(name = "status")
	public FinanceBatchStatus getFinanceBatchStatus() {
		return financeBatchStatus;
	}

	/**
	 * Sets the finance batch status.
	 * 
	 * @param financeBatchStatus the finance batch status
	 */
	public void setFinanceBatchStatus(FinanceBatchStatus financeBatchStatus) {
		this.financeBatchStatus = financeBatchStatus;
	}

	/**
	 * Gets the finance batch type.
	 * 
	 * @return the finance batch type
	 */
	@Column(name = "type")
	public FinanceBatchType getFinanceBatchType() {
		return financeBatchType;
	}

	/**
	 * Sets the finance batch type.
	 * 
	 * @param financeBatchType the finance batch type
	 */
	public void setFinanceBatchType(FinanceBatchType financeBatchType) {
		this.financeBatchType = financeBatchType;
	}

	/**
	 * Gets the issue date.
	 * 
	 * @return the issue date
	 */
	@Column(name="issue_date")
	public Date getIssueDate() {
		return issueDate;
	}

	/**
	 * Sets the issue date.
	 * 
	 * @param issueDate the issue date
	 */
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

    /**
     * Checks if is payment.
     * 
     * @return true, if is payment
     */
    @Column(nullable = false)
	public boolean isPayment() {
		return payment;
	}

	/**
	 * Sets the payment.
	 * 
	 * @param payment the payment
	 */
	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
	@Transient
	public List getDetailList() {
		try {
			IManagerBean financeBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), getId());
			return financeBatchDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining financeBatchDetail list", e);
		}
		return null;
	}
}