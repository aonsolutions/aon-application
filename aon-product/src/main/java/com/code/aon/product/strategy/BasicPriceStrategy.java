package com.code.aon.product.strategy;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Tariff;
import com.code.aon.config.TariffCatalogue;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.ItemTariff;
import com.code.aon.product.Product;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.RegistryTax;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.IEntityAlias;

public class BasicPriceStrategy implements IPriceStrategy, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicPriceStrategy.class.getName());

	public double getUnitPurchasePrice(ICalculable calc) {
		double purchasePrice = 0;
		if (calc.getItem() != null && calc.getItem().getId() != null) {
			purchasePrice = calc.getItem().getPurchasePrice();
		}
		return purchasePrice;
	}

	public double getUnitPurchasePrice(ICalculable calc, Date date, ITariffable iTariffable) {
		double purchasePrice = 0;
		if (iTariffable != null) {
			purchasePrice = getUnitPrice(calc, date, iTariffable, RegistryMode.SUPPLIER);
		}
		return (purchasePrice > 0) ? purchasePrice : getUnitPurchasePrice(calc);
	}

	public double getUnitPrice(ICalculable calc) {
		double price = 0;
		if (calc.getItem() != null && calc.getItem().getId() != null) {
			price = calc.getItem().getPrice();
		}
		return price;
	}

	public double getUnitPrice(ICalculable calc, Date date, ITariffable iTariffable) {
		double price = 0;
		if (iTariffable != null) {
			price = getUnitPrice(calc, date, iTariffable, RegistryMode.CUSTOMER);
		}
		return (price > 0) ? price : getUnitPrice(calc);
	}

	private double getUnitPrice(ICalculable calc, Date date, ITariffable iTariffable, RegistryMode rMode) {
		Registry registry = iTariffable.getRegistry();
		Tariff tariff = iTariffable.getTariff();
		Item item = calc.getItem();
		Product product = item.getProduct();
		try {
			if (item != null && item.getId() != null && iTariffable != null) {
				if (registry != null && registry.getId() != null) {
					IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), registry.getId());
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_STATUS), RegistryItemStatus.ACTIVE);
					criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), rMode);
					if (calc.getWorkPlace() != null && calc.getWorkPlace().getId() != null) {
						Expression wpExpr = ExpressionUtilities.getEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE_ID), calc.getWorkPlace().getId());
						Expression wpNullExpr = ExpressionUtilities.getNullExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE));
						criteria.addExpression(ExpressionUtilities.getOrExpression(wpExpr, wpNullExpr));
					}
					criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE), false);
					criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
					Projection prjWorkplace = Projection.property(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE));
					Projection prjDiscount = Projection.property(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_DISCOUNT_EXPRESSION));
					Projection prjPrice = Projection.property(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRICE));
					for (Object obj : rItemBean.getList(new ProjectionList(prjWorkplace, prjDiscount, prjPrice), criteria)) {
						Object[] objs = (Object[])obj;
						if (objs[0] == null || ((WorkPlace)objs[0]).equals(calc.getWorkPlace())) {
							calc.getDiscountExpression().setDiscountExpr(((DiscountExpression)objs[1]).getDiscountExpr());
							return (Double)objs[2];
						}
					}
				}
				if (tariff != null && tariff.getId() != null) {
					IManagerBean tCatalogueBean = BeanManager.getManagerBean(TariffCatalogue.class);
					IManagerBean cItemBean = BeanManager.getManagerBean(CatalogueItem.class);
					IManagerBean cCategoryBean = BeanManager.getManagerBean(CatalogueCategory.class);

					Criteria criteria = new Criteria();
					criteria.addEqualExpression(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_TARIFF_ID), tariff.getId());
					criteria.addLessThanOrEqualExpression(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), date);
					Expression dateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE), date);
					Expression nullExpr = ExpressionUtilities.getNullExpression(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE));
					criteria.addExpression(ExpressionUtilities.getOrExpression(dateExpr, nullExpr));
					criteria.addOrder(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), false);
					Projection prjCatalogue = Projection.property(tCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_ID));
					for (Object obj : tCatalogueBean.getList(new ProjectionList(prjCatalogue), criteria)) {
						Integer catalogueId = (Integer)obj;

						criteria = new Criteria();
						criteria.addEqualExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), catalogueId);
						Expression itemExpr = ExpressionUtilities.getEqualExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), item.getId());
						Expression itemNotNullExpr = ExpressionUtilities.getNotNullExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM));
						itemExpr = ExpressionUtilities.getAndExpression(itemExpr, itemNotNullExpr);
						Expression productExpr = ExpressionUtilities.getEqualExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRODUCT_ID), product.getId());
						Expression itemNullExpr = ExpressionUtilities.getNullExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM));
						productExpr = ExpressionUtilities.getAndExpression(productExpr, itemNullExpr);
						criteria.addExpression(ExpressionUtilities.getOrExpression(itemExpr, productExpr));
						criteria.addLessThanOrEqualExpression(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), calc.getQuantity());
						criteria.addOrder(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), false);
						criteria.addOrder(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), false);
						Projection prjDiscount = Projection.property(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_DISCOUNT));
						Projection prjPrice = Projection.property(cItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_PRICE));
						for (Object obe : cItemBean.getList(new ProjectionList(prjDiscount, prjPrice), criteria)) {
							Object[] objs = (Object[])obe;
							calc.getDiscountExpression().setDiscountExpr(Double.toString((Double)objs[0]));
							return (Double)objs[1];
						}

						if (product.getCategory() != null && product.getCategory().getId() != null) {
							criteria = new Criteria();
							criteria.addEqualExpression(cCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATALOGUE_ID), catalogueId);
							criteria.addEqualExpression(cCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATEGORY_ID), product.getCategory().getId());
							criteria.addLessThanOrEqualExpression(cCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), calc.getQuantity());
							criteria.addOrder(cCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), false);
							prjDiscount = Projection.property(cCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_DISCOUNT));
							for (Object obt : cCategoryBean.getList(new ProjectionList(prjDiscount), criteria)) {
								calc.getDiscountExpression().setDiscountExpr(Double.toString((Double)obt));
								return 0;
							}
						}
					}

					IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
					criteria = new Criteria();
					criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_ID), item.getId());
					criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_TARIFF_ID), tariff.getId());
					Projection prjPrice = Projection.property(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_PRICE));
					for (Object obj : itemTariffBean.getList(new ProjectionList(prjPrice), criteria)) {
						return (Double)obj;
					}

					if (item.getProduct().isSerializable() && StringUtils.isNotBlank(item.getSerialNumber())) {
						criteria = new Criteria();
						criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_PRODUCT_ID), item.getProduct().getId());
						criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_TARIFF_ID), tariff.getId());
						criteria.addOrder(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_SERIAL_NUMBER));
						for (Object obj : itemTariffBean.getList(new ProjectionList(prjPrice), criteria)) {
							return (Double)obj;
						}
					}

					calc.getDiscountExpression().setDiscountExpr(Double.toString(tariff.getDiscount()));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining unitPrice for tariff = " + tariff.getName(), e);
		}
		return 0;
	}
	
	public double getBasePrice(ICalculable calc) {
		return getBasePrice(calc, false);
	}

	public double getBasePrice(ICalculable calc, boolean forceUnitPrice) {
		double price = 0;
		if (forceUnitPrice) {
			price = getUnitPrice(calc);
		} else {
			price = calc.getPrice();
		}
		price = (price + calc.getTaxes()) * calc.getQuantity();
		if (calc.getDiscountExpression().getDiscounts() != null) {
			for (int i = 0;i<calc.getDiscountExpression().getDiscounts().length;i++) {
				price = price * ( 1 - calc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(price, 4);
	}
	
	public double getTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		for (Object obj : icc.getDetailList()) {
			ICalculable calc = (ICalculable)obj;
			taxableBase = CommonUtil.round(taxableBase + getBasePrice(calc), 4);
		}
		if (icc.getDiscountExpression() != null && icc.getDiscountExpression().getDiscounts() != null) {
			for (int i = 0;i<icc.getDiscountExpression().getDiscounts().length;i++) {
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(taxableBase);
	}

	@Override
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti, boolean ignoreTaxFree) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		Map<Integer, TaxBreakDown> map = new HashMap<Integer, TaxBreakDown>();
		for (Object obj : icc.getDetailList()) {
			ICalculable calc = (ICalculable)obj;
			if (calc.getItem() != null && calc.getItem().getId() != null) {
				Tax vat = calc.getItem().getProduct().getVat();
				if ((ignoreTaxFree || !iti.isVatFree()) && vat != null && vat.getId() != null) {
					TaxBreakDown vatBreakDown;
					if (map.containsKey(vat.getId())) {
						vatBreakDown = map.get(vat.getId());
					} else {
						vatBreakDown = getTaxBreakDownObject(icc.getRegistry(), icc.getDate(), vat);
					}
					vatBreakDown.setBase(CommonUtil.round(vatBreakDown.getBase() + getBasePrice(calc), 4)); 
					map.put(vat.getId(), vatBreakDown);
				}

				if ((ignoreTaxFree || !iti.isRetentionFree()) && iti.isWithholding() && calc.getItem().getProduct().isWithholding()) {
					Tax retention = calc.getItem().getProduct().getRetention();
					TaxBreakDown retentionBreakDown;
					if (map.containsKey(retention.getId())) {
						retentionBreakDown = map.get(retention.getId());
					} else {
						retentionBreakDown = getTaxBreakDownObject(icc.getRegistry(), icc.getDate(), retention);
					}
					retentionBreakDown.setBase(CommonUtil.round(retentionBreakDown.getBase() + getBasePrice(calc), 4)); 
					map.put(retention.getId(), retentionBreakDown);
				}
			}
		}

		for (TaxBreakDown taxBreakDown : map.values()) {
			taxBreakDown.setTaxQuota(obtainQuota(taxBreakDown.getBase(), taxBreakDown.getTaxPercent()));
			if (iti.isSurcharge()) {
				taxBreakDown.setSurchargeQuota(obtainQuota(taxBreakDown.getBase(), taxBreakDown.getSurchargePercent()));
			} else{
				taxBreakDown.setSurchargeQuota(0.0);
				taxBreakDown.setSurchargePercent(0.0);
			}
			taxBreakDowns.add(taxBreakDown);
		}
		return taxBreakDowns;
	}
	
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		return getTaxBreakDowns(icc,iti,false);
	}

	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				total += taxBreakDown.getTaxQuota();
				total += taxBreakDown.getSurchargeQuota();
			}
		}
		return CommonUtil.round(total);
	}
	
	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				total += taxBreakDown.getTaxQuota();
			}
		}
		return CommonUtil.round(total);
	}

	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti) {
		double total = getTaxableBase(icc);
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				total += taxBreakDown.getTaxQuota();
				total += taxBreakDown.getSurchargeQuota();
			}
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				total = total - taxBreakDown.getTaxQuota();
			}
		}
		return CommonUtil.round(total);
	}
	
	private TaxBreakDown getTaxBreakDownObject(Registry registry, Date valueDate, Tax tax) {
		double percent = 0;
		double surcharge = 0;
		try {
			RegistryTax rTax = obtainRegistryTax(registry, tax.getId(), valueDate);
			if (rTax != null) {
				percent = rTax.getPercentage();
				surcharge = rTax.getSurcharge();
			} else {
				if (valueDate.before(tax.getStartDate())) {
					TaxDetail taxDetail = obtainTax(tax.getId(), valueDate);
					if (taxDetail != null) {
			    		percent = taxDetail.getValue();
			    		surcharge = taxDetail.getSurcharge();
					}
				} else {
					percent = tax.getPercentage();
					surcharge = tax.getSurcharge();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining tax info", e);
		}

		TaxBreakDown taxBreakDown = new TaxBreakDown();
		taxBreakDown.setTaxType(tax.getType());
		taxBreakDown.setBase(0);
		taxBreakDown.setTaxPercent(percent);
		taxBreakDown.setSurchargePercent(surcharge);
		return taxBreakDown;
	}

	private RegistryTax obtainRegistryTax(Registry registry, Integer taxId, Date date) throws ManagerBeanException {
		if (registry != null && registry.getId() != null && taxId != null) {
			return registry.getTax(taxId, date);
		}
		return null;
	}

	private TaxDetail obtainTax(Integer taxId, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), taxId);
		criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
		criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
		for (ITransferObject ito : taxDetailBean.getList(criteria)) {
    		return (TaxDetail)ito;
		}
		return null;
	}

	protected double obtainQuota(double base, double percentage) {
		double quota = CommonUtil.round(base * percentage / 100);
		if (base % 0.125 == 0 && base % 0.250 != 0  && percentage == 4) {
			quota = CommonUtil.round(quota - 0.01);
		}
		return quota;
	}

	protected double obtainDeductibleQuota(TaxBreakDown taxBreakDown) {
		return obtainDeductibleQuota(taxBreakDown.getBase(), taxBreakDown.getTaxPercent(), taxBreakDown.getDeductiblePercent());
	}

	protected double obtainDeductibleQuota(double base, double percentage, double deductiblePercentage) {
		double quota = obtainQuota(base, percentage);
		return CommonUtil.round(quota * deductiblePercentage / 100);
	}

}
