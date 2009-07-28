package com.code.aon.finance.invoicing.pricing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.strategy.TaxKey;
import com.code.aon.registry.ITaxInfo;

public class InvoicePriceStrategy extends BasicPriceStrategy {
	
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

	@SuppressWarnings("unchecked")
	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		Iterator iter = getTaxBreakDowns(icc, iti).iterator();
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				total += taxBreakDown.getTaxQuota();
				total += taxBreakDown.getSurchargeQuota();
			}
		}
		return CommonUtil.round(total);
	}
	
	@SuppressWarnings("unchecked")
	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		double total = 0;
		Iterator iter = getTaxBreakDowns(icc, iti).iterator();
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				total = total - taxBreakDown.getTaxQuota();
			}
		}
		return CommonUtil.round(total);
	}
}