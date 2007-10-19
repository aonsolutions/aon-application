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
import com.code.aon.product.ItemTariff;
import com.code.aon.product.Tariff;
import com.code.aon.product.Tax;
import com.code.aon.product.TaxDetail;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.ql.Criteria;
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
			if(forceUnitPrice){
				price = getUnitPrice(calc);
			}else{
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
		return round(price,2);
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
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if(!iti.isTaxFree()){
			Iterator iter = icc.getDetailList().iterator();
			Map<Integer,TaxBreakDown> map = new HashMap<Integer, TaxBreakDown>();
			while(iter.hasNext()){
				ICalculable calc = (ICalculable)iter.next();
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
			Iterator<TaxBreakDown> iterator = map.values().iterator();
			while(iterator.hasNext()){
				TaxBreakDown tbd = iterator.next();
				tbd.setTaxQuota(round(tbd.getBase() * tbd.getTaxPercent()/100 , 2));
				if(iti.isSurcharge()){
					tbd.setSurchargeQuota(round(tbd.getBase() * tbd.getSurchargePercent()/100 , 2));
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
	public double getTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		Iterator iter = icc.getDetailList().iterator();
		while(iter.hasNext()){
			ICalculable calc = (ICalculable)iter.next();
			taxableBase += getBasePrice(calc);
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
		return calc.getItem().getPrice();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getUnitPrice(com.code.aon.product.strategy.ICalculable, com.code.aon.product.Tariff)
	 */
	public double getUnitPrice(ICalculable calc, Tariff tariff) {
		double unitPrice = calc.getItem().getPrice();
		try {
			IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemTariffBean.getFieldName(IProductAlias.ITEM_TARIFF_TARIFF_ID), tariff.getId());
			Iterator iter = itemTariffBean.getList(criteria).iterator();
			if(iter.hasNext()){
				ItemTariff itemTariff = (ItemTariff)iter.next();
				unitPrice *= round(1 - itemTariff.getPercentage()/100, 2);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining unit price for item with id= " + calc.getItem().getId(), e);
		}
		return unitPrice;
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.IPriceStrategy#getTotalPrice(com.code.aon.product.strategy.ICalculableContainer, com.code.aon.registry.ITaxInfo)
	 */
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
		return round(total,2);
	}
	
	/**
	 * Returns the tax detail for this tax and date
	 * 
	 * @param vat the tax to be applied
	 * @param date the date to be applied
	 * @return the tax detail
	 */
	private TaxDetail obtainTaxDetail(Tax vat, Date date) {
		try {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_TAX_ID), vat.getId());
			criteria.addGreaterThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_START_DATE), date);
			criteria.addLessThanExpression(taxDetailBean.getFieldName(IProductAlias.TAX_DETAIL_END_DATE), date);
			Iterator iter = taxDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (TaxDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining taxDetail for tax with id= " + vat.getId(), e);
		}
		return null;
	}
	
	/**
	 * Return the value rounded with this precision
	 * 
	 * @param value the value to round
	 * @param precision the number of decimals
	 * @return the rounded value
	 */
	protected double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}
