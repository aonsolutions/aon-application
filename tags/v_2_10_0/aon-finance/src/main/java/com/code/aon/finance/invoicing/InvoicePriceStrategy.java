package com.code.aon.finance.invoicing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.enumeration.TaxType;
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
					TaxKey key = new TaxKey();
					key.setType(breakDown.getTaxType());
					key.setPercent(breakDown.getTaxPercent());
					TaxBreakDown mapBreakDown;
					if(map.containsKey(key)){
						mapBreakDown  = (TaxBreakDown)map.get(key);
						mapBreakDown.setBase(mapBreakDown.getBase() + breakDown.getBase());
					} else {
						mapBreakDown = breakDown;
					}
					map.put(key, mapBreakDown);
				}
			}
			Iterator iterator = map.values().iterator();
			while(iterator.hasNext()){
				TaxBreakDown tbd = (TaxBreakDown)iterator.next();
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
		return round(total,2);
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
		return round(total,2);
	}
}