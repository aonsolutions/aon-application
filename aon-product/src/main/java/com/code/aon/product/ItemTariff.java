package com.code.aon.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.pricing.IPriceable;
import com.code.aon.product.pricing.ItemPricesManager;
import com.esferalia.aon.entity.master.ItemTariffDB;

@Entity
@Table(name="item_tariff")
@Heritable
public class ItemTariff extends ItemTariffDB implements IPriceable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void setProfitPercent(double profitPercent) {
		super.setProfitPercent(CommonUtil.round(profitPercent, 3));
	}

    public void setPrice(double price) {
        super.setPrice( CommonUtil.round(price, 4));
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
	public double getSalesProfitPercent() {
		return 0;
	}
	public double getSalesProfitPercent(double purchasePrice, double price) {
		ItemPricesManager pricesManager = new ItemPricesManager();
		return pricesManager.getSalesProfit(purchasePrice, price);
	}
	public void setSalesProfitPercent(double salesProfitPercent) {
	}

	@Transient
	public double getSalesPrice() {
		return getSalesPrice(this.getPrice());
	}
	public double getSalesPrice(double price) {
		double vatQuota = (getVat() != null) ? CommonUtil.round(price * getVat().getPercentage() / 100) : 0;
		double retentionQuota = (getItem().getProduct().isWithholding()) ? CommonUtil.round(price * getRetention().getPercentage() / 100) : 0;
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
		return (getItem().getProduct().isWithholding()) ? getItem().getProduct().getRetention() : null;
	}

}