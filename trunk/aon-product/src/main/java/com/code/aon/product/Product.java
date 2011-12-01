package com.code.aon.product;

import java.util.HashSet;
import java.util.Set;

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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;

@Entity
@Table(name="product")
public class Product implements ITransferObject {
	
	private static final long serialVersionUID = -2151513399907107131L;

    private Integer id;
    private String name;
    private String code;
    private Brand brand;
    private ProductCategory category;
    private boolean inventoriable;
    private ProductStatus status;
    private Tax vat;
    private Tax retention;
    private ProductType type;
    private boolean composition;
    private boolean compositionPrice;
    private Account salesAccount;
    private Account purchaseAccount;
	private Set<Item> items = new HashSet<Item>();

    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable=false, length=64)
    @Index(name = "IDX_PRODUCT_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable=false, length=15)
    @Index(name = "IDX_PRODUCT_CODE")    	    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="brand")
    @ForeignKey(name = "FK_PRODUCT_BRAND")
    @Index(name = "IDX_PRODUCT_BRAND")    	        
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="category")
    @ForeignKey(name = "FK_PRODUCT_CATEGORY")
    @Index(name = "IDX_PRODUCT_CATEGORY")    	        
    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public boolean isInventoriable() {
        return inventoriable;
    }

    public void setInventoriable(boolean inventoriable) {
        this.inventoriable = inventoriable;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    @ManyToOne
    @JoinColumn(name="vat")
    @ForeignKey(name = "FK_PRODUCT_VAT")
    @Index(name = "IDX_PRODUCT_VAT")    	        
    public Tax getVat() {
		return vat;
	}

	public void setVat(Tax vat) {
		this.vat = vat;
	}

    @ManyToOne
    @JoinColumn(name="retention")
    @ForeignKey(name = "FK_PRODUCT_RETENTION")
    @Index(name = "IDX_PRODUCT_RETENTION")    	    
	public Tax getRetention() {
		return retention;
	}

	public void setRetention(Tax retention) {
		this.retention = retention;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

    public boolean isComposition() {
        return composition;
    }

    public void setComposition(boolean composition) {
        this.composition = composition;
    }

    @Column(name="composition_price")
    public boolean isCompositionPrice() {
        return compositionPrice;
    }

    public void setCompositionPrice(boolean compositionPrice) {
        this.compositionPrice = compositionPrice;
    }

	@Transient
	public Account getSalesAccount() {
		return salesAccount;
	}

	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}

	@Transient
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

    @OneToMany(mappedBy="product")
	public Set<Item> getItems() {
		return this.items;
	}
	
	public void setItems( Set<Item> items ) {
		this.items = items;
	}
	
	@Transient
	public void addItems(Item item) {
		item.setProduct( this );
		this.items.add( item );
	}
    
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Product o = (Product) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.brand, o.brand)			
				.append(this.category, o.category)								
				.append(this.code, o.code)				
				.append(this.composition, o.composition)								
				.append(this.compositionPrice, o.compositionPrice)								
				.append(this.inventoriable, o.inventoriable)				
				.append(this.name, o.name)			
				.append(this.purchaseAccount, o.purchaseAccount)								
				.append(this.retention, o.retention)				
				.append(this.salesAccount, o.salesAccount)								
				.append(this.status, o.status)				
				.append(this.type, o.type)				
				.append(this.vat, o.vat)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)						
			.append(this.brand)
			.append(this.category)		
			.append(this.code)						
			.append(this.composition)						
			.append(this.compositionPrice)						
			.append(this.inventoriable)			
			.append(this.name)
			.append(this.purchaseAccount)		
			.append(this.retention)						
			.append(this.salesAccount)						
			.append(this.status)						
			.append(this.type)			
			.append(this.vat)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}