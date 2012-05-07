package com.code.aon.product.strategy;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.ItemTariff;
import com.code.aon.product.TariffCatalogue;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.ITaxInfo;
import com.esferalia.aon.entity.IEntityAlias;

public class BasicPriceStrategy implements IPriceStrategy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicPriceStrategy.class.getName());

	public double getUnitPrice(ICalculable calc) {
		double price = 0;
		if (calc.getItem() != null && calc.getItem().getId() != null) {
			price = calc.getItem().getPrice();
		}
		return price;
	}

	public double getUnitPrice(ICalculable calc, Date date, Tariff tariff) {
		try {
			if (calc.getItem() != null && calc.getItem().getId() != null && tariff != null && tariff.getId() != null) {
				IManagerBean tariffCatalogueBean = BeanManager.getManagerBean(TariffCatalogue.class);
				IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
				IManagerBean catalogueCategoryBean = BeanManager.getManagerBean(CatalogueCategory.class);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_TARIFF_ID), tariff.getId());
				criteria.addLessThanOrEqualExpression(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), date);
				Expression dateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE), date);
				Expression nullExpr = ExpressionUtilities.getNullExpression(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(dateExpr, nullExpr));
				criteria.addOrder(tariffCatalogueBean.getFieldName(IEntityAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), false);
				Iterator<ITransferObject> iterator = tariffCatalogueBean.getList(criteria).iterator();
				while (iterator.hasNext()) {
					TariffCatalogue tariffCatalogue = (TariffCatalogue)iterator.next();

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), calc.getItem().getId());
					criteria.addLessThanOrEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), false);
					Iterator<ITransferObject> itemIterator = catalogueItemBean.getList(criteria, 0, 1).iterator();
					if (itemIterator.hasNext()) {
						CatalogueItem catalogueItem = (CatalogueItem)itemIterator.next();
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueItem.getDiscount()));
						return (catalogueItem.getPrice() > 0)?catalogueItem.getPrice():getUnitPrice(calc);
					}

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATEGORY_ID), calc.getItem().getProduct().getCategory().getId());
					criteria.addLessThanOrEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), false);
					Iterator<ITransferObject> categoryIterator = catalogueCategoryBean.getList(criteria, 0, 1).iterator();
					if (categoryIterator.hasNext()) {
						CatalogueCategory catalogueCategory = (CatalogueCategory)categoryIterator.next();
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueCategory.getDiscount()));
						return getUnitPrice(calc);
					}
				}

				IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
				criteria = new Criteria();
				criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_ID), calc.getItem().getId());
				criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_TARIFF_ID), tariff.getId());
				iterator = itemTariffBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					ItemTariff itemTariff = (ItemTariff)iterator.next();
					return itemTariff.getPrice();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining unitPrice for tariff = " + tariff.getName(), e);
		}
		return getUnitPrice(calc);
	}
	
	public double getBasePrice(ICalculable calc) {
		return getBasePrice(calc, false);
	}

	public double getBasePrice(ICalculable calc, boolean forceUnitPrice) {
		double price = 0;
		try {
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
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining basePrice", e);
		}
		return CommonUtil.round(price, 4);
	}
	
	@SuppressWarnings("unchecked")
	public double getTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		Iterator<ITransferObject> iterator = icc.getDetailList().iterator();
		while (iterator.hasNext()) {
			ICalculable calc = (ICalculable)iterator.next();
			taxableBase += getBasePrice(calc);
		}
		if (icc.getDiscountExpression().getDiscounts() != null) {
			for (int i = 0;i<icc.getDiscountExpression().getDiscounts().length;i++) {
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(taxableBase);
	}

	@SuppressWarnings("unchecked")
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if (!iti.isTaxFree()) {
			Iterator<ITransferObject> iter = icc.getDetailList().iterator();
			Map<Integer, TaxBreakDown> map = new HashMap<Integer, TaxBreakDown>();
			while (iter.hasNext()) {
				ICalculable calc = (ICalculable)iter.next();
				if (calc.getItem() != null && calc.getItem().getId() != null) {
					Tax vat = calc.getItem().getProduct().getVat();
					if (vat != null && vat.getId() != null) {
						TaxBreakDown vatBreakDown;
						if (map.containsKey(vat.getId())) {
							vatBreakDown = map.get(vat.getId());
						} else {
							vatBreakDown = getTaxBreakDownObject(icc.getDate(), vat);
						}
						vatBreakDown.setBase(CommonUtil.round(vatBreakDown.getBase() + getBasePrice(calc), 4)); 
						map.put(vat.getId(), vatBreakDown);
					}

					Tax retention = calc.getItem().getProduct().getRetention();
					if (iti.isWithholding() && retention != null && retention.getId() != null) {
						TaxBreakDown retentionBreakDown;
						if (map.containsKey(retention.getId())) {
							retentionBreakDown = map.get(retention.getId());
						} else {
							retentionBreakDown = getTaxBreakDownObject(icc.getDate(), retention);
						}
						retentionBreakDown.setBase(CommonUtil.round(retentionBreakDown.getBase() + getBasePrice(calc), 4)); 
						map.put(retention.getId(), retentionBreakDown);
					}
				}
			}

			Iterator<TaxBreakDown> iterator = map.values().iterator();
			while (iterator.hasNext()) {
				TaxBreakDown tbd = iterator.next();
				tbd.setTaxQuota(CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100));
				if (iti.isSurcharge()) {
					tbd.setSurchargeQuota(CommonUtil.round(tbd.getBase() * tbd.getSurchargePercent() / 100));
				} else{
					tbd.setSurchargeQuota(0.0);
					tbd.setSurchargePercent(0.0);
				}
				taxBreakDowns.add(tbd);
			}
		}
		return taxBreakDowns;
	}

	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		Iterator<TaxBreakDown> iterator = getTaxBreakDowns(icc, iti).iterator();
		while (iterator.hasNext()) {
			TaxBreakDown taxBreakDown = iterator.next();
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				total += taxBreakDown.getTaxQuota();
				total += taxBreakDown.getSurchargeQuota();
			}
		}
		return CommonUtil.round(total);
	}
	
	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		Iterator<TaxBreakDown> iterator = getTaxBreakDowns(icc, iti).iterator();
		while (iterator.hasNext()) {
			TaxBreakDown taxBreakDown = iterator.next();
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				total += taxBreakDown.getTaxQuota();
			}
		}
		return CommonUtil.round(total);
	}

	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti) {
		double total = getTaxableBase(icc);
		Iterator<TaxBreakDown> iterator = getTaxBreakDowns(icc, iti).iterator();
		while (iterator.hasNext()) {
			TaxBreakDown taxBreakDown = iterator.next();
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
	
	private TaxBreakDown getTaxBreakDownObject(Date valueDate, Tax tax) {
		double percent = 0;
		double surcharge = 0;
		if (valueDate.before(tax.getStartDate())) {
			TaxDetail taxDetail = obtainTaxDetail(tax, valueDate);
			if (taxDetail != null) {
				percent = taxDetail.getValue();
				surcharge = taxDetail.getSurcharge();
			}
		} else{
			percent = tax.getPercentage();
			surcharge = tax.getSurcharge();
		}

		TaxBreakDown taxBreakDown = new TaxBreakDown();
		taxBreakDown.setTaxType(tax.getType());
		taxBreakDown.setTaxPercent(percent);
		taxBreakDown.setSurchargePercent(surcharge);
		taxBreakDown.setBase(0);
		return taxBreakDown;
	}

	private TaxDetail obtainTaxDetail(Tax tax, Date date) {
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), tax.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
			Iterator<ITransferObject> iterator = taxDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (TaxDetail)iterator.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining taxDetail for tax with id= " + tax.getId(), e);
		}
		return null;
	}
	
}
