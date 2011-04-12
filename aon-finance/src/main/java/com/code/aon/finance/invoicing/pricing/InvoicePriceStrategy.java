package com.code.aon.finance.invoicing.pricing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.strategy.TaxKey;
import com.code.aon.registry.ITaxInfo;

public class InvoicePriceStrategy extends BasicPriceStrategy {

	@Override
	public double getTaxableBase(ICalculableContainer icc) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getTaxableBase() != 0) ? invoice.getTaxableBase() : getCalculatedTaxableBase(icc);
	}

	@SuppressWarnings("unchecked")
	public double getCalculatedTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		Iterator iter = icc.getDetailList().iterator();
		while(iter.hasNext()){
			InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
			taxableBase += invoiceDetail.getTaxableBase();
		}
		if(icc.getDiscountExpression().getDiscounts() != null){
			for(int i = 0;i<icc.getDiscountExpression().getDiscounts().length;i++){
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(taxableBase);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if(!iti.isTaxFree()){
			Iterator iter = icc.getDetailList().iterator();
			Map map = new HashMap();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				Iterator breakDownIter =  invoiceDetail.getTaxBreakDowns().iterator();
				while(breakDownIter.hasNext()){
					TaxBreakDown breakDown = (TaxBreakDown)breakDownIter.next();
					setTaxBreakDownAddInfo(breakDown, invoiceDetail);
					TaxKey key = new TaxKey();
					key.setType(breakDown.getTaxType());
					key.setPercent(breakDown.getTaxPercent());
					TaxBreakDown mapBreakDown;
					if(map.containsKey(key)){
						mapBreakDown  = (TaxBreakDown)map.get(key);
						mapBreakDown.setBase(mapBreakDown.getBase() + breakDown.getBase());
						mapBreakDown.setTaxQuota(mapBreakDown.getTaxQuota() + breakDown.getTaxQuota());
						mapBreakDown.setSurchargeQuota(mapBreakDown.getSurchargeQuota() + breakDown.getSurchargeQuota());
					} else {
						mapBreakDown = breakDown;
					}
					map.put(key, mapBreakDown);
				}
			}
			Iterator iterator = map.values().iterator();
			while(iterator.hasNext()){
				TaxBreakDown tbd = (TaxBreakDown)iterator.next();
				if(tbd.getTaxQuota() == 0){
					tbd.setTaxQuota(CommonUtil.round(tbd.getBase() * tbd.getTaxPercent()/100));
				}
				if(iti.isSurcharge()){
					if(tbd.getSurchargeQuota() == 0){
						tbd.setSurchargeQuota(CommonUtil.round(tbd.getBase() * tbd.getSurchargePercent()/100));
					}
				}else{
					tbd.setSurchargeQuota(0.0);
					tbd.setSurchargePercent(0.0);
				}
				taxBreakDowns.add(tbd);
			}
		}
		return taxBreakDowns;
	}

	protected void setTaxBreakDownAddInfo(TaxBreakDown breakDown, InvoiceDetail invoiceDetail) {
		//Para redefinir en los hijos.
	}

	@Override
	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getVatQuota() != 0) ? invoice.getVatQuota() : getCalculatedTotalVatQuota(icc, iti);
	}

	public double getCalculatedTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		return super.getTotalVatQuota(icc, iti);
	}
	
	@Override
	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getRetentionQuota() != 0) ? invoice.getRetentionQuota() : getCalculatedTotalRetentionQuota(icc, iti);
	}

	public double getCalculatedTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		return super.getTotalRetentionQuota(icc, iti);
	}
	
	@Override
	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getTotal() != 0) ? invoice.getTotal() : super.getTotalPrice(icc, iti);
	}

	@SuppressWarnings("unchecked")
	public double getVatPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		Iterator iter = getTaxBreakDowns(icc, iti).iterator();
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

	@SuppressWarnings("unchecked")
	public double getRetentionPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		Iterator iter = getTaxBreakDowns(icc, iti).iterator();
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

}