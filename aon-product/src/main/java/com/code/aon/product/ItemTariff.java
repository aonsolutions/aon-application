package com.code.aon.product;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.pricing.IPriceable;
import com.esferalia.aon.entity.master.ItemTariffDB;

@Entity
@Table(name="item_tariff")
public class ItemTariff extends ItemTariffDB implements IPriceable {

	private static final long serialVersionUID = 1L;

	public void setProfitPercent(double profitPercent) {
		setProfitPercent( CommonUtil.round(profitPercent, 3));
	}

    public void setPrice(double price) {
        setPrice( CommonUtil.round(price, 4));
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
}