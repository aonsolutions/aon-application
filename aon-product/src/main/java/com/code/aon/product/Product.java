package com.code.aon.product;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.ql.Criteria;

/**
 * Transfer Object that represents a product.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="product")
public class Product implements ITransferObject,ILookupObject {
	
	private static final Logger LOGGER = Logger.getLogger(Product.class.getName());

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Description of the product.
     */
    private String name;

    /**
     * Internal code of the product.
     */
    private String code;

    /**
     * Brand of the product.
     */
    private Brand brand;

    /**
     * The category of the product.
     */
    private ProductCategory category;

    /**
     * Is the product inventoriable?
     */
    private boolean inventoriable;

    /**
     * The status of the product.
     */
    private ProductStatus status;
    
    /**
     * The V.A.T. tax. 
     */
    private Tax vat;
    
    /**
    * The RETENTION tax. 
    */
    private Tax retention;
    
    /**
     * Product type.
     */
    private ProductType type;

    /**
     * Is the product a composition?
     * 0: is not a composition. 1: is a composition
     */
    private boolean composition;
    
    /**
     * The set of items linked to this product. 
     */
	private Set<Item> items = new HashSet<Item>();

    /**
     * Void contructor.
     * 
     */
    public Product() {
    }

    /**
     * Contructor for this unique key.
     * 
     * @param pk
     *            unique key.
     */
    public Product(Integer pk) {
        this.id = pk;
    }

    /**
     * Returns the unique key.
     * 
     * @return unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param id
     *            unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns this product's name.
     * 
     * @return product's name.
     */
    @Column(length=64, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the product's name
     * 
     * @param name
     *            product's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the product's brand.
     * 
     * @return product's brand.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="brand", nullable=true)
    public Brand getBrand() {
        return brand;
    }

    /**
     * Assigns product's brand.
     * 
     * @param brand
     *            product's brand.
     */
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    /**
     * Returns the product's category.
     * 
     * @return product's category.
     */
    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="category", nullable=true)
    public ProductCategory getCategory() {
        return category;
    }

    /**
     * Assigns the product's category.
     * 
     * @param category
     *            product's category.
     */
    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    /**
     * Returns the internal code of this product.
     * 
     * @return the internal code of this product.
     */
    @Column(nullable=false, length=15)
    public String getCode() {
        return code;
    }

    /**
     * Assigns the internal code of this product.
     * 
     * @param code
     *            internal code of this product.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns if the product is inventariable.
     * 
     * @return True if the product is inventariable.
     */
    public boolean isInventoriable() {
        return inventoriable;
    }

    /**
     * Assigns if the product is inventariable.
     * 
     * @param inventoriable
     *            True if the product is inventariable.
     */
    public void setInventoriable(boolean inventoriable) {
        this.inventoriable = inventoriable;
    }

    /**
     * Returns the product status.
     * 
     * @return product status.
     */
    @Column(name="status")
    public ProductStatus getStatus() {
        return status;
    }

    /**
     * Assigns product status.
     * 
     * @param status
     *            product status.
     */
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    /**
     * Returns the V.A.T. to be applied in this product
     * 
     * @return V.A.T. of this product
     */
    @ManyToOne
    @JoinColumn(name="vat", nullable=false)
    public Tax getVat() {
		return vat;
	}

    /**
     * Assigns the V.A.T. to be applied in this product
     * 
     * @param vat
     * 		V.A.T. of this product
     */
	public void setVat(Tax vat) {
		this.vat = vat;
	}

	/**
     * Returns the RETENTION to be applied in this product
     * 
     * @return RETENTION of this product
     */
    @ManyToOne
    @JoinColumn(name="retention")
	public Tax getRetention() {
		return retention;
	}

    /**
     * Assigns the RETENTION to be applied in this product
     * 
     * @param retention
     * 		RETENTION of this product
     */
	public void setRetention(Tax retention) {
		this.retention = retention;
	}

	/**
	 * Returns product tipo
	 * 
	 * @return
	 * 		the product type
	 */
	@Column(name="type")
	public ProductType getType() {
		return type;
	}

	/**
	 * Assigns product type
	 * 
	 * @param type
	 * 	 the product type
	 */
	public void setType(ProductType type) {
		this.type = type;
	}

    /**
	 * Returns if the product is a composition o not
	 * 
     * @return True if the product is a composition.
     * 
     */
    @Column(name="composition")
    public boolean isComposition() {
        return composition;
    }

    /**
     * Assigns if the product is a composition.
     * 
     * @param composition
     *            True if the product is a composition.
     */
    public void setComposition(boolean composition) {
        this.composition = composition;
    }
    
	/**
	 * Returns the items linked to this product.
	 *
	 * @return Set with all the items. 
	 *  
	 */
    @OneToMany(mappedBy="product")
	public Set<Item> getItems() {
		return this.items;
	}
	
	/**
	 * Assigns the items linked to this product.
	 * 
	 * @param items Set with all the items. 
	 */
	public void setItems( Set<Item> items ) {
		this.items = items;
	}
	
	/**
	 * Returns the V.A.T. to be applied for this product in a concrete date
	 * 
	 * @param date the date to check. 
	 * @return the Tax to be applied  
	 * 
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public Tax getVat(Date date){
        if(date.after(getVat().getStartDate())){
        	return getVat();
        }
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
        	criteria.addEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_TAX_ID), getVat().getId());
        	criteria.addLessThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_START_DATE),date);
        	criteria.addGreaterThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_END_DATE),date);
        	Iterator iter = taxDetailBean.getList(criteria).iterator();
        	while(iter.hasNext()){
        		TaxDetail taxDetail = (TaxDetail)iter.next();
        		if(taxDetail.getTax().getType().equals(TaxType.VAT)){
        			Tax tax = new Tax();
            		tax.setId(taxDetail.getTax().getId());
            		tax.setPercentage(taxDetail.getValue());
            		tax.setSurcharge(taxDetail.getSurcharge());
            		tax.setType(taxDetail.getTax().getType());
            		return tax;
        		}
        	}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining VAT for product with id= " + getId() + "and date= " + date, e);
		}
		return getVat();
	}

	/**
	 * Assigns the item to this product and add it to the items set.
	 * 
	 * @param item the item to add. 
	 */
	@Transient
	public void addItems(Item item) {
		item.setProduct( this );
		this.items.add( item );
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.common.ILookupObject#lookups()
     */
    @Transient
    public Map<String,Object> getLookups() {
    	Map<String,Object> map = new HashMap<String,Object>();
        map.put(IProductAlias.PRODUCT_ID , getId());
        map.put(IProductAlias.PRODUCT_NAME, getName());
        return map;
    }
    
}