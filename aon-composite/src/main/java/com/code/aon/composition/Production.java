package com.code.aon.composition;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents a production of a item.
 * 
 * @author Consulting & Development. Gorka Irazu - 23-oct-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="production")
public class Production implements ITransferObject {

	private static final long serialVersionUID = 8787519895610996362L;

	private static final Logger LOGGER = Logger.getLogger(Production.class.getName());

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Production description.
     */
    private String description;

    /**
     * Production lot code.
     */
    private String lotCode;

    /**
     * Production date.
     */
    private Date productionDate;

    /**
     * Item to produce.
     */
    private Item item;

    /**
     * Initial quantity in composition.
     */
    private double initialQuantity;

    /**
     * Quantity to produce.
     */
    private double quantity;

    /**
     * Production price.
     */
    private double price;

    /**
	 * Set of production lines.
	 */
	private Set<ProductionDetail> lines = new HashSet<ProductionDetail>();

    /**
     * Set of production expenses.
     */
    private Set<ProductionExpense> expenses = new HashSet<ProductionExpense>();

    /**
     * Constructor.
     * 
     */
    public Production() {
        this.productionDate = new Date();
    }

    /**
     * Returns the unique key.
     * 
     * @return Integer
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param id
     *          Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the production description.
     * 
     * @return String
     */
    @Column(length=64)
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the production description.
     * 
     * @param description
     *          Production description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the production lot code.
     * 
     * @return String
     */
    @Column(name="lot_code", length=25)
    public String getLotCode() {
        return lotCode;
    }

    /**
     * Assigns the production lot code.
     * 
     * @param lotCode
     *          Production lot code.
     */
    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    /**
     * Returns the production date.
     * 
     * @return String
     */
    @Column(name="production_date")
    public Date getProductionDate() {
        return productionDate;
    }

    /**
     * Assigns the production date.
     * 
     * @param productionDate
     *          Production date.
     */
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    /**
     * Returns the item to produce.
     * 
     * @return Item
     *      cascade = "none"
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item", nullable=false)
    public Item getItem() {
        return item;
    }

    /**
     * Assigns the item to produce.
     * 
     * @param item
     *          Item to produce.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the initial quantity in composition.
     * 
     * @return double
     */
    @Column(name="initial_quantity")
    public double getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * Assigns the initial quantity in composition.
     * 
     * @param initialQuantity
     *          Initial quantity in composition.
     */
    public void setInitialQuantity(double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * Returns the quantity to produce.
     * 
     * @return double
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Assigns the quantity to produce.
     * 
     * @param quantity
     *          Quantity to produce.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the production price.
     * 
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Assigns the production price.
     * 
     * @param price
     *          Production price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns a Set containing the details of the production.
     *
     * @return Set<ProductionDetail>
     */
    @OneToMany(mappedBy = "production", cascade={CascadeType.REMOVE})
	public Set<ProductionDetail> getLines() {
		return lines;
	}

    /**
     * Assigns a Set containing the details of the production.
     *
     * @param lines
     *          A Set containing the details of the production.
     */
	public void setLines(Set<ProductionDetail> lines) {
		this.lines = lines;
	}

    /**
     * Returns a Set containing the expenses of the production.
     *
     * @return Set<ProductionExpense>
     */
    @OneToMany(mappedBy = "production", cascade={CascadeType.REMOVE})
    public Set<ProductionExpense> getExpenses() {
        return expenses;
    }

    /**
     * Assigns a Set containing the expenses of the production.
     *
     * @param expenses
     *          A Set containing the expenses of the production.
     */
    public void setExpenses(Set<ProductionExpense> expenses) {
        this.expenses = expenses;
    }
    
    /**
     * Returns a List containing the details of the production.
     *
     * @return List
     */
    @Transient
    @SuppressWarnings("unchecked")
	public List getDetailList() {
		try {
			IManagerBean productionDetailBean = BeanManager.getManagerBean(ProductionDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(productionDetailBean.getFieldName(ICompositionAlias.PRODUCTION_DETAIL_PRODUCTION_ID), getId());
			return productionDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining productionDetail list", e);
		}
		return null;
	}

    /**
     * Returns a List containing the expenses of the production.
     *
     * @return List
     */
	@Transient
    @SuppressWarnings("unchecked")
    public List getExpensesList() {
        try {
            IManagerBean productionDetailBean = BeanManager.getManagerBean(ProductionExpense.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(productionDetailBean.getFieldName(ICompositionAlias.PRODUCTION_EXPENSE_PRODUCTION_ID), getId());
            return productionDetailBean.getList(criteria);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining productionExpense list", e);
        }
        return null;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Production) {
			Production o = (Production) obj;
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