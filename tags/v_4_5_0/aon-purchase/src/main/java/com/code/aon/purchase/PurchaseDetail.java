package com.code.aon.purchase;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.ItemSupplier;
import com.code.aon.supplier.dao.ISupplierAlias;

@Entity
@Table(name="purchase_detail")
public class PurchaseDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -8755960049183396137L;
	private final static Logger LOGGER = LoggerFactory.getLogger(PurchaseDetail.class);

	private Integer id;
	private Purchase purchase;
	private Integer line;
	private Item item;
    private String description;
	private double quantity;
	private double price;
    private DiscountExpression discountExpression;
    private double taxes;
    private PurchaseDetailStatus status;
    private double delivered;
    private double transfered;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

	@ManyToOne
	@JoinColumn( name="purchase", nullable = false, updatable=false )
	public Purchase getPurchase() {
		return purchase;
	}
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	
    public Integer getLine() {
        return line;
    }
    public void setLine(Integer line) {
        this.line = line;
    }

	@ManyToOne
	@JoinColumn( name="item", nullable=false )
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

	@Column(length=1024)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

	@Column(name="discount_expr")
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}

	public PurchaseDetailStatus getStatus() {
		return status;
	}
	public void setStatus(PurchaseDetailStatus status) {
		this.status = status;
	}

	public double getDelivered() {
		return delivered;
	}
	public void setDelivered(double delivered) {
		this.delivered = delivered;
	}

	@Transient
	public double getTransfered() {
		transfered = transfered > (quantity - delivered) ? (quantity - delivered) : transfered;
		return transfered;
	}
	public void setTransfered(double transfered) {
		this.transfered = transfered;
	}
	
	@Transient
    public String getItemSupplierCode() {
    	try {
			IManagerBean itemSupplierBean = BeanManager.getManagerBean(ItemSupplier.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemSupplierBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_ITEM_ID),getItem().getId());
			criteria.addEqualExpression(itemSupplierBean.getFieldName(ISupplierAlias.ITEM_SUPPLIER_SUPPLIER_ID),getPurchase().getSupplier().getId());
			List<ITransferObject> lista;
			lista = itemSupplierBean.getList(criteria);
			if (lista.size() > 0) {
				String code = ((ItemSupplier) lista.get(0)).getCode();
				return (StringUtils.isNotEmpty(code)) ? code : getItem()
						.getProduct().getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("can't get ItemSupplier.code",e);
		}
		return getItem().getProduct().getCode();
	}   		
   
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final PurchaseDetail o = (PurchaseDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(purchase, o.purchase)
				.append(line, o.line)			
				.append(item, o.item)			
				.append(description, o.description)
				.append(quantity, o.quantity)
				.append(price, o.price)
				.append(discountExpression, o.discountExpression)
				.append(taxes, o.taxes)
				.append(status, o.status)
				.append(delivered, o.delivered)
				.append(transfered, o.transfered)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(purchase)
			.append(line)			
			.append(item)			
			.append(description)
			.append(quantity)
			.append(price)
			.append(discountExpression)
			.append(taxes)
			.append(status)
			.append(delivered)
			.append(transfered)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}
