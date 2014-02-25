package com.code.aon.product.strategy;

import java.util.Date;
import java.util.HashMap;
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
import com.code.aon.config.TariffCatalogue;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.ItemTariff;
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
				for (ITransferObject ito : tariffCatalogueBean.getList(criteria)) {
					TariffCatalogue tariffCatalogue = (TariffCatalogue)ito;

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_ITEM_ID), calc.getItem().getId());
					criteria.addLessThanOrEqualExpression(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueItemBean.getFieldName(IEntityAlias.CATALOGUE_ITEM_QUANTITY), false);
					for (ITransferObject itr : catalogueItemBean.getList(criteria, 0, 1)) {
						CatalogueItem catalogueItem = (CatalogueItem)itr;
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueItem.getDiscount()));
						return (catalogueItem.getPrice() > 0)?catalogueItem.getPrice():getUnitPrice(calc);
					}

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_CATEGORY_ID), calc.getItem().getProduct().getCategory().getId());
					criteria.addLessThanOrEqualExpression(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueCategoryBean.getFieldName(IEntityAlias.CATALOGUE_CATEGORY_QUANTITY), false);
					for (ITransferObject itr : catalogueCategoryBean.getList(criteria, 0, 1)) {
						CatalogueCategory catalogueCategory = (CatalogueCategory)itr;
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueCategory.getDiscount()));
						return getUnitPrice(calc);
					}
				}

				IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
				criteria = new Criteria();
				criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_ID), calc.getItem().getId());
				criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_TARIFF_ID), tariff.getId());
				for (ITransferObject itr : itemTariffBean.getList(criteria)) {
					ItemTariff itemTariff = (ItemTariff)itr;
					return itemTariff.getPrice();
				}

				calc.getDiscountExpression().setDiscountExpr(Double.toString(tariff.getDiscount()));
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
						vatBreakDown = getTaxBreakDownObject(icc.getDate(), vat);
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
						retentionBreakDown = getTaxBreakDownObject(icc.getDate(), retention);
					}
					retentionBreakDown.setBase(CommonUtil.round(retentionBreakDown.getBase() + getBasePrice(calc), 4)); 
					map.put(retention.getId(), retentionBreakDown);
				}
			}
		}

		for (TaxBreakDown taxBreakDown : map.values()) {
			taxBreakDown.setTaxQuota(CommonUtil.round(taxBreakDown.getBase() * taxBreakDown.getTaxPercent() / 100));
			if (iti.isSurcharge()) {
				taxBreakDown.setSurchargeQuota(CommonUtil.round(taxBreakDown.getBase() * taxBreakDown.getSurchargePercent() / 100));
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
			for (ITransferObject ito : taxDetailBean.getList(criteria)) {
				return (TaxDetail)ito;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining taxDetail for tax with id= " + tax.getId(), e);
		}
		return null;
	}
	
}
