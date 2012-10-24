package com.code.aon.finance.invoicing.pricing;

import java.util.HashMap;
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

	public double getCalculatedTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		for (Object obj : icc.getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)obj;
			taxableBase += invoiceDetail.getTaxableBase();
		}
		if (icc.getDiscountExpression().getDiscounts() != null) {
			for(int i = 0;i<icc.getDiscountExpression().getDiscounts().length;i++) {
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(taxableBase);
	}

	@Override
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		if (!iti.isTaxFree()) {
			Map<TaxKey, TaxBreakDown> map = new HashMap<TaxKey, TaxBreakDown>();
			for (Object obj : icc.getDetailList()) {
				InvoiceDetail invoiceDetail = (InvoiceDetail)obj;
				for (TaxBreakDown breakDown : invoiceDetail.getTaxBreakDowns()) {
					setTaxBreakDownAddInfo(breakDown, invoiceDetail);
					TaxKey key = new TaxKey();
					key.setType(breakDown.getTaxType());
					key.setPercent(breakDown.getTaxPercent());
					TaxBreakDown mapBreakDown;
					if (map.containsKey(key)) {
						mapBreakDown  = map.get(key);
						mapBreakDown.setBase(mapBreakDown.getBase() + breakDown.getBase());
						mapBreakDown.setTaxQuota(mapBreakDown.getTaxQuota() + breakDown.getTaxQuota());
						mapBreakDown.setSurchargeQuota(mapBreakDown.getSurchargeQuota() + breakDown.getSurchargeQuota());
					} else {
						mapBreakDown = breakDown;
					}
					map.put(key, mapBreakDown);
				}
			}
			for (TaxBreakDown breakDown : map.values()) {
				if (breakDown.getTaxQuota() == 0) {
					breakDown.setTaxQuota(CommonUtil.round(breakDown.getBase() * breakDown.getTaxPercent()/100));
				}
				if (iti.isSurcharge()) {
					if (breakDown.getSurchargeQuota() == 0) {
						breakDown.setSurchargeQuota(CommonUtil.round(breakDown.getBase() * breakDown.getSurchargePercent()/100));
					}
				} else {
					breakDown.setSurchargeQuota(0.0);
					breakDown.setSurchargePercent(0.0);
				}
				taxBreakDowns.add(breakDown);
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

	public double getVatPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

	public double getRetentionPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

}