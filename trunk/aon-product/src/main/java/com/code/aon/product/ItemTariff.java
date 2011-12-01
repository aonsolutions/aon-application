package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.pricing.IPriceable;

@Entity
@Table(name="item_tariff")
public class ItemTariff implements ITransferObject, IPriceable {

	private static final long serialVersionUID = -6617476462179856145L;

	private Integer id;
	private Item item;
	private Tariff tariff;
	private ItemTariffType type;
    private double profitPercent;
    private double price;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	@ManyToOne
	@JoinColumn(name="item",nullable=false)
    @ForeignKey(name = "FK_ITEM_TARIFF_ITEM")
    @Index(name = "IDX_ITEM_TARIFF_ITEM")
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
    @ManyToOne
    @JoinColumn(name="tariff",nullable=false)
    @ForeignKey(name = "FK_ITEM_TARIFF_TARIFF")
    @Index(name = "IDX_ITEM_TARIFF_TARIFF")
    public Tariff getTariff() {
        return tariff;
    }
    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public ItemTariffType getType() {
		return type;
	}
	public void setType(ItemTariffType type) {
		this.type = type;
	}
	
	@Column(name="profit_percent")
	public double getProfitPercent() {
		return profitPercent;
	}

	public void setProfitPercent(double profitPercent) {
		this.profitPercent = CommonUtil.round(profitPercent, 3);
	}

    @Column(precision=15, scale=4)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = CommonUtil.round(price, 4);
    }

    @Transient
    public boolean isFixed() {
    	return getType() == ItemTariffType.FIXED;
    }

	@Transient
	public double getProfitablePrice() {
		if (getType() == ItemTariffType.PRICE) {
			return getItem().getPrice();
		} else if (getType() == ItemTariffType.SALES_PRICE) {
			return getItem().getSalesPrice();
		} else {
			return getItem().getPurchasePrice();
		}
	}

	@Transient
	public double getSalesPrice() {
		return getSalesPrice(this.getPrice());
	}
	public double getSalesPrice(double price) {
		double vatQuota = (getVat() != null) ? CommonUtil.round(price * getVat().getPercentage() / 100) : 0;
		double retentionQuota = (getRetention() != null) ? CommonUtil.round(price * getRetention().getPercentage() / 100) : 0;
		return CommonUtil.round(CommonUtil.round(price) + vatQuota - retentionQuota);
	}
	public void setSalesPrice(double salesPrice) {
	}

	@Transient
	public Tax getVat() {
		return getItem().getProduct().getVat();
	}

	@Transient
	public Tax getRetention() {
		return getItem().getProduct().getRetention();
	}

    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemTariff o = (ItemTariff) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(this.price, o.price)
				.append(this.profitPercent, o.profitPercent)
				.append(this.tariff, o.tariff)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(this.item)			
			.append(this.price)
			.append(this.profitPercent)
			.append(this.tariff)
			.append(this.type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}