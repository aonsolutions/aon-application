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
					item.setPrice(getPrice(purchasePrice, item.getProfitPercent(), false));
				}
			}
			item.setSalesProfitPercent(getSalesProfit(item.getPurchasePrice(), item.getPrice()));
		} else {
			item.setPurchasePrice(0);
		}
	}

	public void onProfitChanged(IPriceable priceable, Object value) {
		if (value != null && !value.toString().equals("")) {
			if (priceable.getProfitablePrice() != 0) {
				double profit = CommonUtil.round(((Double)value).doubleValue(), 3);
				priceable.setPrice(getPrice(priceable.getProfitablePrice(), profit, false));
				priceable.setSalesProfitPercent(getSalesProfit(priceable.getProfitablePrice(), priceable.getPrice()));
			}
		} else {
			priceable.setProfitPercent(0);
		}
	}

	private double getPrice(double profitablePrice, double profit, boolean overSales) {
		double price = 0;
		if (!overSales) {
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
		} else {
			for (int i=2; i<=4; i++) {
				price = CommonUtil.round(profitablePrice / (1 - profit / 100), i);
				if (profit == CommonUtil.round(1 - profitablePrice / price * 100, 3)) {
					return price;
				} else {
					price = CommonUtil.floor(profitablePrice / (1 - profit / 100), i);
					if (profit == CommonUtil.round(1 - profitablePrice / price * 100, 3)) {
						return price;
					}
				}
			}
		}
		return price;
	}

	public void onPriceChanged(IPriceable priceable, Object value) {
		if (value != null && !value.toString().equals("")) {
			double price = CommonUtil.round(((Double)value).doubleValue(), 4);
			priceable.setProfitPercent(getProfit(priceable.getProfitablePrice(), price));
			priceable.setSalesProfitPercent(getSalesProfit(priceable.getProfitablePrice(), priceable.getPrice()));
		} else {
			priceable.setPrice(0);
		}
	}

	private double getProfit(double purchasePrice, double price) {
		return (purchasePrice == 0) ? 0 : CommonUtil.round((price / purchasePrice - 1) * 100, 3);
	}

	public double getSalesProfit(double purchasePrice, double price) {
		return (price == 0) ? 0 : CommonUtil.round((1 - purchasePrice / price) * 100, 3);
	}

	public void onSalesProfitChanged(IPriceable priceable, Object value) {
		if (value != null && !value.toString().equals("")) {
			if (priceable.getProfitablePrice() != 0) {
				double salesProfit = CommonUtil.round(((Double)value).doubleValue(), 3);
				priceable.setPrice(getPrice(priceable.getProfitablePrice(), salesProfit, true));
				priceable.setProfitPercent(getProfit(priceable.getProfitablePrice(), priceable.getPrice()));
			}
		} else {
			priceable.setSalesProfitPercent(0);
		}
	}

	public void onSalesPriceChanged(IPriceable priceable, Object value) {
		onSalesPriceChanged(priceable, value, true);
	}

	public void onSalesPriceChanged(IPriceable priceable, Object value, boolean calculateProfit) {
		if (value != null && !value.toString().equals("")) {
			double salesPrice = CommonUtil.round(((Double)value).doubleValue());
			priceable.setPrice(getPrice(priceable, salesPrice, 4));
			if (calculateProfit) {
				priceable.setProfitPercent(getProfit(priceable.getProfitablePrice(), priceable.getPrice()));
				priceable.setSalesProfitPercent(getSalesProfit(priceable.getProfitablePrice(), priceable.getPrice()));
			}
		} else {
			priceable.setSalesPrice(0);
		}
	}

	public double getPrice(IPriceable priceable, double salesPrice, int precision) {
		double vatPercent = (priceable.getVat() != null) ? priceable.getVat().getPercentage() : 0;
		double retentionPercent = (priceable.getRetention() != null) ? priceable.getRetention().getPercentage()	: 0;
		return getPrice(vatPercent, retentionPercent, salesPrice, precision);
	}

	public double getPrice(double vatPercent, double retentionPercent, double salesPrice, int minPrecision) {
		return getPrice(vatPercent, retentionPercent, salesPrice, minPrecision, 4);
	}

	public double getPrice(double vatPercent, double retentionPercent, double salesPrice, int minPrecision, int maxPrecision) {
		double price = 0;
		for (int i=minPrecision; i<=maxPrecision; i++) {
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
					} else if (i < maxPrecision && i < 4) {
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
