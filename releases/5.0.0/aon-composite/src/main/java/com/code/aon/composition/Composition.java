package com.code.aon.composition;

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
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents a product composition.
 * 
 * @author Consulting & Development. Gorka Irazu - 25-may-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="composition")
public class Composition implements ITransferObject {

	private static final long serialVersionUID = 3620812340370625862L;

	private static final Logger LOGGER = Logger.getLogger(Composition.class.getName());
    
    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Composition type.
     */
    private Short type;

    /**
     * Composition description.
     */
    private String description;

    /**
     * Item to compose.
     */
    private Item item;

    /**
     * Quantity to compose.
     */
    private double quantity;

    /**
     * Composition price.
     */
    private double price;

    /**
     * Composition expenses percentage.
     */
    private double expensesPercent;

    /**
     * Composition expenses fixed.
     */
    private double expensesFixed;
    
    /**
     * TRUE: composition price is the sum of all composition lines, FALSE: composition price is header price.
     */
    private boolean priceInDetails;
    
    /**
	 * Set of composition lines.
	 */
	private Set<CompositionDetail> lines = new HashSet<CompositionDetail>();
	
	/**
	 * Set of composition expenses.
	 */
	private Set<CompositionExpense> expenses = new HashSet<CompositionExpense>();

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
     * Returns the composition type.
     * 
     * @return Short
     */
    public Short getType() {
        return type;
    }

    /**
     * Assigns the composition type.
     * 
     * @param type
     *          Composition type.
     */
    public void setType(Short type) {
        this.type = type;
    }

    /**
     * Returns the composition description.
     * 
     * @return String
     */
    @Column(length=64)
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the composition description.
     * 
     * @param description
     *          Composition description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the item to compose.
     * 
     * @return Item
     *      cascade = "none"
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item", nullable=false)
    @AonPOJOInitializationInvalidateRestoreNull
    public Item getItem() {
        return item;
    }

    /**
     * Assigns the item to compose.
     * 
     * @param item
     *          Item to compose.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the quantity of item to compose.
     * 
     * @return double
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Assigns the quantity of item to compose.
     * 
     * @param quantity
     *          Quantity of item to compose.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the composition price.
     * 
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Assigns the composition price.
     * 
     * @param price
     *          Composition price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the composition expenses percentage.
     * 
     * @return double
     */
    @Column(name="expenses_percent")
    public double getExpensesPercent() {
        return expensesPercent;
    }

    /**
     * Assigns the composition expenses percentage.
     * 
     * @param expensesPercent
     *          Composition expenses percentage.
     */
    public void setExpensesPercent(double expensesPercent) {
        this.expensesPercent = expensesPercent;
    }

    /**
     * Returns the composition expenses fixed.
     * 
     * @return double
     */
    @Column(name="expenses_fixed")
    public double getExpensesFixed() {
        return expensesFixed;
    }

    /**
     * Assigns the composition expenses fixed.
     * 
     * @param expensesFixed
     *          Composition expenses fixed.
     */
    public void setExpensesFixed(double expensesFixed) {
        this.expensesFixed = expensesFixed;
    }

    /**
     * Returns if the price is indicated by header or by lines.
     *
     * @return boolean
     */
    @Column(name="price_in_details")
    public boolean isPriceInDetails() {
		return priceInDetails;
	}

    /**
     * Assigns if the price is indicated by header or by lines.
     * 
     * @param priceInDetails
     *          If the price is indicated by header or by lines.
     */
	public void setPriceInDetails(boolean priceInDetails) {
		this.priceInDetails = priceInDetails;
	}

    /**
     * Returns a Set containing the details of the composition.
     *
     * @return Set<CompositionDetail>
     */
    @OneToMany(mappedBy = "composition", cascade={CascadeType.REMOVE})
	public Set<CompositionDetail> getLines() {
		return lines;
	}

    /**
     * Assigns a Set containing the details of the composition.
     *
     * @param lines
     *          A Set containing the details of the composition.
     */
	public void setLines(Set<CompositionDetail> lines) {
		this.lines = lines;
	}

    /**
     * Returns a Set containing the expenses of the composition.
     *
     * @return Set<CompositionExpense>
     */
	@OneToMany(mappedBy = "composition", cascade={CascadeType.REMOVE})
	public Set<CompositionExpense> getExpenses() {
		return expenses;
	}

    /**
     * Assigns a Set containing the expenses of the composition.
     *
     * @param expenses
     *          A Set containing the expenses of the composition.
     */
	public void setExpenses(Set<CompositionExpense> expenses) {
		this.expenses = expenses;
	}
	
    /**
     * Returns a List containing the details of the composition.
     *
     * @return List
     */
	@Transient
	@SuppressWarnings("unchecked")
	public List getDetailList() {
		try {
			IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), getId());
			return compositionDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining compositionDetail list", e);
		}
		return null;
	}
	
    /**
     * Returns a List containing the expenses of the composition.
     *
     * @return List
     */
	@Transient
	@SuppressWarnings("unchecked")
	public List getExpensesList() {
		try {
			IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionExpense.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_EXPENSE_COMPOSITION_ID), getId());
			return compositionDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining compositionExpense list", e);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Composition) {
			Composition o = (Composition) obj;
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