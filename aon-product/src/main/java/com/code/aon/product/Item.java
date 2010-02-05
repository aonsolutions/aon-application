package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.enumeration.ProductStatus;

@Entity
@Table(name="item")
public class Item implements ITransferObject {

	private static final long serialVersionUID = -2720748805321005422L;
	
    private Integer id;
    private Product product;
    private String detail;
    private String description;
    private String barcode;
    private double price;
    private ProductStatus status;
    private double expensesPercent;
    private double expensesFixed;
    private double profitPercent;
    private double purchasePrice;
    private boolean internet;

    @Id
    @GeneratedValue
    @Column(nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="product", nullable=false)
    @org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @AonPOJOInitializationInvalidateRestoreNull
    @ForeignKey(name = "FK_ITEM_PRODUCT")
    @Index(name = "IDX_ITEM_PRODUCT")
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Column(length=64)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Lob
    @Type(type="stringClob")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(length=32)
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Column(nullable=true)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
    @Column(name="expenses_percent")
	public double getExpensesPercent() {
		return expensesPercent;
	}

	public void setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
	}

    @Column(name="expenses_fixed")
	public double getExpensesFixed() {
		return expensesFixed;
	}

	public void setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
	}

	@Column(name="profit_percent")
	public double getProfitPercent() {
		return profitPercent;
	}

	public void setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
	}

	@Column(name="purchase_price")
	public double getPurchasePrice() {
		return purchasePrice;
	}
	
    public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
      
	public boolean isInternet() {
		return internet;
	}

	public void setInternet(boolean internet) {
		this.internet = internet;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Item o = (Item) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.barcode, o.barcode)				
				.append(this.description, o.description)				
				.append(this.detail, o.detail)				
				.append(this.expensesFixed, o.expensesFixed)				
				.append(this.expensesPercent, o.expensesPercent)
				.append(this.internet, o.internet)
				.append(this.price, o.price)				
				.append(this.product, o.product)				
				.append(this.profitPercent, o.profitPercent)				
				.append(this.purchasePrice, o.purchasePrice)				
				.append(this.status, o.status)												
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(description)
			.append(barcode)
			.append(detail)
			.append(expensesFixed)			
			.append(expensesPercent)						
			.append(id)
			.append(internet)	
			.append(price)						
			.append(product)			
			.append(profitPercent)						
			.append(purchasePrice)						
			.append(status)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}