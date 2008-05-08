package com.code.aon.product;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents an item.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="item")
public class Item implements ITransferObject, ILookupObject {

    private static final String ITEM_FULL_NAME = "Item_full_name";
    private static final String ITEM_VAT = "Item_vat";
    private static final String ITEM_SURCHARGE = "Item_surcharge";
	
    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Product that references this item.
     */
    private Product product;

    /**
     * Item detail.
     */
    private String detail;

    /**
     * Item description.
     */
    private String description;

    /**
     * Item price.
     */
    private double price;

    /**
     * Item status.
     */
    private ProductStatus status;

    /**
     * Expenses percent.
     */
    private double expensesPercent;

    /**
     * Sure expenses.
     */
    private double expensesFixed;
    
    /**
     * Sales's profit percent.
     */
    private double profitPercent;
    
    /**
	 * Last purchase price.
	 */
    private double purchasePrice;

    /**
     * Default contructor.
     * 
     */
    public Item() {
    }

    /**
     * Constructor for this unique key and product.
     * 
     * @param pk
     *            Unique key.
     * @param product
     *            Product that references this item.
     */
    public Item(Integer pk, Product product) {
        this.id = pk;
        this.setProduct(product);
    }

    /**
     * Returns the unique key.
     * 
     * @return Unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Asigns the unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns article's detail.
     * 
     * @return article's detail.
     */
    @Column(length=64)
    public String getDetail() {
        return detail;
    }

    /**
     * Asigns article's detail.
     * 
     * @param detail
     *            article's detail.
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Returns this item's description.
     * 
     * @return item's description.
     */
    @Column(length=65535)
    public String getDescription() {
        return description;
    }

    /**
     * Asign this item's description.
     * 
     * @param description
     *            item's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns item's price.
     * 
     * @return item's price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Asigns item's price.
     * 
     * @param price
     *            item's price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns item's product.
     * 
     * @return item's product.
     * 
     */
    @ManyToOne
    @JoinColumn(name="product", nullable=false)
    @AonPOJOInitializationInvalidateRestoreNull
    public Product getProduct() {
        return product;
    }

    /**
     * Asigns this item's product.
     * 
     * @param product
     *            item's product.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Returns item's status.
     * 
     * @return item's status.
     */
    public ProductStatus getStatus() {
        return status;
    }

	/**
     * Asigns item's status.
     * 
     * @param status
     *            item's status.
     */
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
	/**
	 * Returns sure expenses 
	 * 
	 * @return Returns sure expenses.
	 */
    @Column(name="expenses_fixed")
	public double getExpensesFixed() {
		return expensesFixed;
	}

	/**
	 * Asigns sure expenses
	 * 
	 * @param expensesFixed sure expenses.
	 */
	public void setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
	}

	/**
	 * Returns the expenses percent
	 * 
	 * @return Returns expenses percent.
	 */
    @Column(name="expenses_percent")
	public double getExpensesPercent() {
		return expensesPercent;
	}

	/**
	 * Asigns the expenses percent
	 * 
	 * @param expensesPercent expenses percent.
	 */
	public void setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
	}

	/**
	 * Returns the profit percent
	 * 
	 * @return Returns profit percent.
	 */
	@Column(name="profit_percent")
	public double getProfitPercent() {
		return profitPercent;
	}

	/**
	 * Asigns the profit percent
	 * 
	 * @param profitPercent profit percent.
	 */
	public void setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
	}

	/**
	 * Returns the purchase price
	 * 
	 * @return Returns purchase price.
	 */
	@Column(name="purchase_price")
	public double getPurchasePrice() {
		return purchasePrice;
	}
	
	/**
	 * Asigns the purchase price
	 * 
	 * @param purchasePrice purchase price.
	 */
    public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.common.ILookupObject#lookups()
     */
	@Transient
    public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(IProductAlias.ITEM_ID, getId());
        map.put(IProductAlias.ITEM_PRODUCT_ID, getProduct().getId());
        map.put(IProductAlias.ITEM_PRODUCT_CODE, getProduct().getCode());
        map.put(IProductAlias.ITEM_PRODUCT_NAME, getProduct().getName());
        map.put(IProductAlias.ITEM_PRODUCT_CATEGORY_ID, getProduct().getCategory().getId());
        map.put(IProductAlias.ITEM_DETAIL, getDetail());
        map.put(IProductAlias.ITEM_PRICE, new Double(getPrice()));
        map.put(IProductAlias.ITEM_PURCHASE_PRICE, new Double(getPurchasePrice()));
        map.put(IProductAlias.ITEM_EXPENSES_PERCENT, new Double(getExpensesPercent()));
        map.put(IProductAlias.ITEM_EXPENSES_FIXED, new Double(getExpensesFixed()));
        map.put(ITEM_FULL_NAME, getProduct().getName() + " " + ((getDetail() == null) ? "" : getDetail()));
        map.put(ITEM_VAT, new Double(getProduct().getVat().getPercentage()));
        map.put(ITEM_SURCHARGE, new Double(getProduct().getVat().getSurcharge()));
        return map;
    }
	
	/**
	 * Returns all the Income Detail in a ordered list
	 * 
	 * @return List of IncomeDetail ordered
	 */
	@Transient
	public ItemPos getItemPost() {
		try {
			IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID), getId());
			Iterator iter = itemPosBean.getList(criteria).iterator();
			if (iter.hasNext())
				return (ItemPos)iter.next();
		} catch (ManagerBeanException e) {
		}
		return null;
	}

}