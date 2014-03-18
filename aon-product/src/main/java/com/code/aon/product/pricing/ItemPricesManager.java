package com.code.aon.product.pricing;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;

public class ItemPricesManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onPurchasePriceChanged(Item item, Object value) {
		if (value != null && !value.toString().equals("")) {
			double purchasePrice = CommonUtil.round(((Double)value).doubleValue(), 4);
			if (purchasePrice == 0) {
				item.setProfitPercent(0);
			} else if (item.getProfitPercent() != 0 || item.getPrice() != 0) {
				if (item.getProfitPercent() == 0) {
					item.setProfitPercent(getProfit(purchasePrice, item.getPrice()));
				} else {
					item.setPrice(getPrice(purchasePrice, item.getProfitPercent()));
				}
			}
		} else {
			item.setPurchasePrice(0);
			item.setProfitPercent(0);
		}
	}

	public void onProfitChanged(IPriceable priceable, Object value) {
		if (value != null && !value.toString().equals("")) {
			double profit = CommonUtil.round(((Double)value).doubleValue(), 3);
			priceable.setPrice(getPrice(priceable.getProfitablePrice(), profit));
		} else {
			priceable.setProfitPercent(0);
			if (priceable.getProfitablePrice() != 0) {
				priceable.setPrice(priceable.getProfitablePrice());
			}
		}
	}

	private double getPrice(double profitablePrice, double profit) {
		double price = 0;
		for (int i=2; i<=4; i++) {
			price = CommonUtil.round(profitablePrice * (1 + profit / 100), i);
			if (profit == CommonUtil.round((price / profitablePrice - 1) * 100, 3)) {
				return price;
			} else {
				price = CommonUtil.floor(profitablePrice * (1 + profit / 100), i);
				if (profit == CommonUtil.round((price / profitablePrice - 1) * 100, 3)) {
					return price;
				}
			}
		}
		return price;
	}

	public void onPriceChanged(IPriceable priceable, Object value) {
		if (value != null && !value.toString().equals("")) {
			double price = CommonUtil.round(((Double)value).doubleValue(), 4);
			if (price == 0) {
				priceable.setProfitPercent(0);
			} else {
				priceable.setProfitPercent(getProfit(priceable.getProfitablePrice(), price));
			}
		} else {
			priceable.setPrice(0);
			priceable.setProfitPercent(0);
		}
	}

	private double getProfit(double purchasePrice, double price) {
		if (purchasePrice == 0) {
			return 0;
		}
		return CommonUtil.round((price / purchasePrice - 1) * 100, 3);
	}

	public void onSalesPriceChanged(IPriceable priceable, Object value) {
		onSalesPriceChanged(priceable, value, true);
	}

	public void onSalesPriceChanged(IPriceable priceable, Object value, boolean calculateProfit) {
		if (value != null && !value.toString().equals("")) {
			double salesPrice = CommonUtil.round(((Double)value).doubleValue());
			if (salesPrice == 0) {
				priceable.setPrice(0);
				priceable.setProfitPercent(0);
			} else {
				priceable.setPrice(getPrice(priceable, salesPrice, 2));
				if (calculateProfit) {
					priceable.setProfitPercent(getProfit(priceable.getProfitablePrice(), priceable.getPrice()));
				}
			}
		} else {
			priceable.setPrice(0);
			priceable.setProfitPercent(0);
		}
	}

	public double getPrice(IPriceable priceable, double salesPrice, int precision) {
		double vatPercent = (priceable.getVat() != null) ? priceable.getVat().getPercentage() : 0;
		double retentionPercent = (priceable.getRetention() != null) ? priceable.getRetention().getPercentage()	: 0;
		return getPrice(vatPercent, retentionPercent, salesPrice, precision);
	}

	public double getPrice(double vatPercent, double retentionPercent, double salesPrice, int precision) {
		double price = 0;
		for (int i=precision; i<=4; i++) {
			price = CommonUtil.round(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100), i);
			if (salesPrice == getSalesPrice(vatPercent, retentionPercent, price)) {
				break;
			} else {
				price = CommonUtil.ceil(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100), i);
				if (salesPrice == getSalesPrice(vatPercent, retentionPercent, price)) {
					break;
				} else {
					price = CommonUtil.floor(salesPrice / (1 + vatPercent / 100 - retentionPercent / 100), i);
					if (salesPrice == getSalesPrice(vatPercent, retentionPercent, price)) {
						break;
					} else if (i < 4) {
						price = CommonUtil.round(price + 5 / Math.pow(10, i+1), i+1);
						if (salesPrice == getSalesPrice(vatPercent, retentionPercent, price)) {
							break;
						}
					}
				}
			}
		}
		return price;
		
	}

	public double getSalesPrice(IPriceable priceable, double price) {
		double vatPercent = (priceable.getVat() != null) ? priceable.getVat().getPercentage() : 0;
		double retentionPercent = (priceable.getRetention() != null) ? priceable.getRetention().getPercentage()	: 0;
		return getSalesPrice(vatPercent, retentionPercent, price);
	}

	public double getSalesPrice(double vatPercent, double retentionPercent, double price) {
		return CommonUtil.round(price * (1 + vatPercent / 100 - retentionPercent / 100));
	}

}
