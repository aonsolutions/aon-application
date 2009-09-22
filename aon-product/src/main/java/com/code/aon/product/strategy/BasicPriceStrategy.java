package com.code.aon.product.strategy;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.TariffCatalogue;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.ITaxInfo;

/**
 * Class that implements IPriceStrategy, 
 * to calculate prices.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class BasicPriceStrategy implements IPriceStrategy {
	
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = Logger.getLogger(BasicPriceStrategy.class.getName());

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getBasePrice(com.code.aon.product.strategy.ICalculable, boolean)
	 */
	public double getBasePrice(ICalculable calc, boolean forceUnitPrice) {
		double price = 0;
		try {
			if(forceUnitPrice) {
				price = getUnitPrice(calc);
			} else {
				price = calc.getPrice();
			}
			price = (price + calc.getTaxes()) * calc.getQuantity();
			if(calc.getDiscountExpression().getDiscounts() != null){
				for(int i = 0;i<calc.getDiscountExpression().getDiscounts().length;i++){
					price = price * ( 1 - calc.getDiscountExpression().getDiscounts()[i] /100);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining basePrice", e);
		}
		return CommonUtil.round(price);
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getBasePrice(com.code.aon.product.strategy.ICalculable)
	 */
	public double getBasePrice(ICalculable calc) {
		return getBasePrice(calc, false);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getTaxBreakDowns(com.code.aon.product.strategy.ICalculableContainer, com.code.aon.registry.ITaxInfo)
	 */
	@SuppressWarnings("unchecked")
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if(!iti.isTaxFree()){
			Iterator iter = icc.getDetailList().iterator();
			Map<Integer,TaxBreakDown> map = new HashMap<Integer, TaxBreakDown>();
			while(iter.hasNext()){
				ICalculable calc = (ICalculable)iter.next();
				if (calc.getItem() != null && calc.getItem().getId() != null) {
					double percent = 0;
					double surcharge = 0;
					TaxType taxType = calc.getItem().getProduct().getVat().getType();
					if(icc.getDate().before(calc.getItem().getProduct().getVat().getStartDate())){
						TaxDetail taxDetail = obtainTaxDetail(calc.getItem().getProduct().getVat(),icc.getDate());
						if(taxDetail != null){
							percent  = taxDetail.getValue();
							surcharge = taxDetail.getSurcharge();
						}
					}else{
						percent = calc.getItem().getProduct().getVat().getPercentage();
						surcharge = calc.getItem().getProduct().getVat().getSurcharge();
					}
					TaxBreakDown taxBreakDown;
					if(map.containsKey(calc.getItem().getProduct().getVat().getId())){
						taxBreakDown = map.get(calc.getItem().getProduct().getVat().getId());
						taxBreakDown.setBase(taxBreakDown.getBase() + getBasePrice(calc)); 
					}else{
						taxBreakDown = new TaxBreakDown();
						taxBreakDown.setTaxType(taxType);
						taxBreakDown.setTaxPercent(percent);
						taxBreakDown.setSurchargePercent(surcharge);
						taxBreakDown.setBase(getBasePrice(calc));
					}
					map.put(calc.getItem().getProduct().getVat().getId(), taxBreakDown);
				}
			}
			Iterator<TaxBreakDown> iterator = map.values().iterator();
			while(iterator.hasNext()){
				TaxBreakDown tbd = iterator.next();
				tbd.setTaxQuota(CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100));
				if(iti.isSurcharge()){
					tbd.setSurchargeQuota(CommonUtil.round(tbd.getBase() * tbd.getSurchargePercent()/100));
				}else{
					tbd.setSurchargeQuota(0.0);
					tbd.setSurchargePercent(0.0);
				}
				taxBreakDowns.add(tbd);
			}
		}
		return taxBreakDowns;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getTaxableBase(com.code.aon.product.strategy.ICalculableContainer)
	 */
	@SuppressWarnings("unchecked")
	public double getTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		Iterator iter = icc.getDetailList().iterator();
		while(iter.hasNext()){
			ICalculable calc = (ICalculable)iter.next();
			if (calc.getItem() != null && calc.getItem().getId() != null) {
				taxableBase += getBasePrice(calc);
			}
		}
		if(icc.getDiscountExpression().getDiscounts() != null){
			for(int i = 0;i<icc.getDiscountExpression().getDiscounts().length;i++){
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return taxableBase;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getUnitPrice(com.code.aon.product.strategy.ICalculable)
	 */
	public double getUnitPrice(ICalculable calc) {
		double price = 0;
		if (calc.getItem() != null && calc.getItem().getId() != null) {
			price = calc.getItem().getPrice();
		}
		return price;
	}

	@SuppressWarnings("unchecked")
	public double getUnitPrice(ICalculable calc, Date date, Tariff tariff) {
		try {
			if (calc.getItem() != null && calc.getItem().getId() != null && tariff != null && tariff.getId() != null) {
				IManagerBean tariffCatalogueBean = BeanManager.getManagerBean(TariffCatalogue.class);
				IManagerBean catalogueItemBean = BeanManager.getManagerBean(CatalogueItem.class);
				IManagerBean catalogueCategoryBean = BeanManager.getManagerBean(CatalogueCategory.class);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(tariffCatalogueBean.getFieldName(IProductAlias.TARIFF_CATALOGUE_TARIFF_ID), tariff.getId());
				criteria.addLessThanOrEqualExpression(tariffCatalogueBean.getFieldName(IProductAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), date);
				Expression dateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(tariffCatalogueBean.getFieldName(IProductAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE), date);
				Expression nullExpr = ExpressionUtilities.getNullExpression(tariffCatalogueBean.getFieldName(IProductAlias.TARIFF_CATALOGUE_CATALOGUE_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(dateExpr, nullExpr));
				criteria.addOrder(tariffCatalogueBean.getFieldName(IProductAlias.TARIFF_CATALOGUE_CATALOGUE_START_DATE), false);
				Iterator iterator = tariffCatalogueBean.getList(criteria).iterator();
				while (iterator.hasNext()) {
					TariffCatalogue tariffCatalogue = (TariffCatalogue)iterator.next();

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_ITEM_ID), calc.getItem().getId());
					criteria.addLessThanOrEqualExpression(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueItemBean.getFieldName(IProductAlias.CATALOGUE_ITEM_QUANTITY), false);
					Iterator itemIterator = catalogueItemBean.getList(criteria, 0, 1).iterator();
					if (itemIterator.hasNext()) {
						CatalogueItem catalogueItem = (CatalogueItem)itemIterator.next();
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueItem.getDiscount()));
						return (catalogueItem.getPrice() > 0)?catalogueItem.getPrice():getUnitPrice(calc);
					}

					criteria = new Criteria();
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IProductAlias.CATALOGUE_CATEGORY_CATALOGUE_ID), tariffCatalogue.getCatalogue().getId());
					criteria.addEqualExpression(catalogueCategoryBean.getFieldName(IProductAlias.CATALOGUE_CATEGORY_CATEGORY_ID), calc.getItem().getProduct().getCategory().getId());
					criteria.addLessThanOrEqualExpression(catalogueCategoryBean.getFieldName(IProductAlias.CATALOGUE_CATEGORY_QUANTITY), calc.getQuantity());
					criteria.addOrder(catalogueCategoryBean.getFieldName(IProductAlias.CATALOGUE_CATEGORY_QUANTITY), false);
					Iterator categoryIterator = catalogueCategoryBean.getList(criteria, 0, 1).iterator();
					if (categoryIterator.hasNext()) {
						CatalogueCategory catalogueCategory = (CatalogueCategory)categoryIterator.next();
						calc.getDiscountExpression().setDiscountExpr(Double.toString(catalogueCategory.getDiscount()));
						return getUnitPrice(calc);
					}
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining unitPrice for tariff = " + tariff.getName(), e);
		}
		return getUnitPrice(calc);
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getTotalPrice(com.code.aon.product.strategy.ICalculableContainer, com.code.aon.registry.ITaxInfo)
	 */
	@SuppressWarnings("unchecked")
	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti) {
		double total = getTaxableBase(icc);
		Iterator iter = getTaxBreakDowns(icc, iti).iterator();
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				total += taxBreakDown.getTaxQuota();
				total += taxBreakDown.getSurchargeQuota();
			}
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				total = total - taxBreakDown.getTaxQuota();
			}
		}
		return CommonUtil.round(total);
	}
	
	/**
	 * Returns the tax detail for this tax and date
	 * 
	 * @param vat the tax to be applied
	 * @param date the date to be applied
	 * @return the tax detail
	 */
	@SuppressWarnings("unchecked")
	private TaxDetail obtainTaxDetail(Tax vat, Date date) {
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID), vat.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_START_DATE), date);
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_END_DATE), date);
			Iterator iter = taxDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (TaxDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining taxDetail for tax with id= " + vat.getId(), e);
		}
		return null;
	}
	
}
