package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.supplier.Supplier;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Transfer Object that represents a Income.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name="income")
public class Income implements ITransferObject, ICalculableContainer, IHeaderObject {
	
	private static final long serialVersionUID = -2473825467680303195L;

	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(Income.class.getName());
	
	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * The series of the income
	 */
	private String series;

    /**
     * The number linked to this series
     */
    private int number;
	
	/**
	 * Supplier od the Income
	 */
	private Supplier supplier;
	
	/**
	 * The address of this income
	 */
	private RegistryAddress registryAddress;
	
	/**
	 * Date and time of the income
	 */
	private Date issueTime;
	
	/**
	 * The securiry level, if is confidential or official
	 */
	private SecurityLevel securityLevel;
	
	/**
	 * Current status of this income
	 */
	private IncomeStatus incomeStatus;
	
	/**
	 * All the lines of the income
	 */
	private Set<IncomeDetail> lines = new HashSet<IncomeDetail>();

	/**
	 * Constructor that assigns current date
	 */
	public Income() {
		this.issueTime = new Date();
	}
	
	/**
	 * Returns the unique key
	 * 
	 * @return unique key
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Assigns the unique key
	 * 
	 * @param primaryKey
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Returns the series
	 * 
	 * @return series
	 * @see com.code.aon.common.IHeaderObject#getSeries()
	 */
	@Column(name="series", nullable=false, length=5)
	public String getSeries() {
		return series;
	}

	/**
	 * Assigns the series
	 * 
	 * @param series
	 * @see com.code.aon.common.IHeaderObject#setSeries(java.lang.String)
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	
	/**
	 * Returns the number
	 * 
	 * @return number
	 * @see com.code.aon.common.IHeaderObject#getNumber()
	 */
	@Column(name="number", nullable=false)
	public int getNumber() {
		return number;
	}

	/**
	 * Assigns the number
	 * 
	 * @param number
	 * @see com.code.aon.common.IHeaderObject#setNumber(int)
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the income status
	 * 
	 * @return income status
	 */
	@Column(name="status")
	public IncomeStatus getIncomeStatus() {
		return incomeStatus;
	}

	/**
	 * Assigns the income status
	 * 
	 * @param incomeStatus income status
	 */
	public void setIncomeStatus(IncomeStatus incomeStatus) {
		this.incomeStatus = incomeStatus;
	}

	/**
	 * Returns the issue time
	 * 
	 * @return issue time
	 */
	@Column(name="issue_time")
	public Date getIssueTime() {
		return issueTime;
	}

	/**
	 * Assigns the issue time
	 * 
	 * @param issueTime issue time
	 */
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	/**
	 * Returns the address referenced
	 * 
	 * @return the registry address
	 */
	@ManyToOne
    @JoinColumn(name="address", updatable = false)
	public RegistryAddress getRegistryAddress() {
		return registryAddress;
	}

	/**
	 * Assigns an address  
	 * 
	 * @param registryAddress registry address
	 */
	public void setRegistryAddress(RegistryAddress registryAddress) {
		this.registryAddress = registryAddress;
	}

	/**
	 * Returns the securyty level
	 * 
	 * @return security level
	 * @see com.code.aon.common.IHeaderObject#getSecurityLevel()
	 */
	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * Assigns the security level
	 * 
	 * @param securityLevel security level
	 */
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	
	/**
	 * Returns the supplier
	 * 
	 * @return supplier
	 */
	@ManyToOne
    @JoinColumn(name="supplier", nullable = false)
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * Assigns the supplier
	 * 
	 * @param supplier
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	/**
	 * Returns a Set containing all the Income Delatil
	 * 
	 * @return Set<IncomeDetail> all lines
	 */
	@OneToMany(mappedBy = "income", cascade={CascadeType.REMOVE})
	public Set<IncomeDetail> getLines() {
		return this.lines;
	}

	/**
	 * Assigns a Set of IncomeDetail to this header
	 * 
	 * @param lines Set<IncomeDetail> lines 
	 */
	public void setLines( Set<IncomeDetail> lines ) {
		this.lines = lines;
	}
	
	/**
	 * Returns the date
	 * 
	 * @return issue time
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDate()
	 */
	@Transient
	public Date getDate() {
		return this.issueTime;
	}

	/**
	 * Returns all the Income Detail in a list
	 * 
	 * @return List of IncomeDetail
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDetailList()
	 */
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
	
	/**
	 * Returns all the Income Detail in a ordered list
	 * 
	 * @return List of IncomeDetail ordered
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public List getOrderedDetailList() {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), getId());
			// criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ITEM_PRODUCT_TYPE));
			criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_ID));
			return incomeDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining offerDetail list", e);
		}
		return null;
	}

	/**
	 * Returns a discount expression
	 * 
	 * @return discount expression
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDiscountExpression()
	 */
	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

    @Override
    public boolean equals(Object obj) {
    	if(id == null){
    		return super.equals(obj);
    	}
        if (obj instanceof Income) {
            return (this.id.equals(((Income)obj).getId()));
        }
        return false;
    }

}