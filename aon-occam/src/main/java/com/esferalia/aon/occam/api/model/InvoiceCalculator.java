package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculator {
	
	public static void calculate(AccountingInvoice ai) {
		double vt = 0.0;
		double tb = 0.0;
		double wb = 0.0; 
		if (ai.getVats() != null) {
			for (InvoiceVAT vat : ai.getVats()) {
				tb = tb + vat.getBase();
				if (ai.isVatEnabled()) {
					vt = vt + (vat.getQuota() + vat.getSurchargeQuota());
				}
				if (ai.isWithholding() && vat.isWithholding()) {
					if (ai.isWithholdingFarmer()) {
						wb = wb + vat.getBase() + vat.getQuota();
					} else {
						wb = wb + vat.getBase();
					}
				}
			}
		}
		double wp = ai.getWithholdingData().getPercentage();
		double rt = AonMathUtils.round(wb * wp / 100);
		double t = AonMathUtils.round(tb + vt - rt);
		ai.getWithholdingData().setBase(wb);
		ai.getWithholdingData().setQuota(rt);
		ai.getInvoice().setTotal(t);
		ai.getInvoice().setVatQuota(AonMathUtils.round(vt));
		ai.getInvoice().setRetentionQuota(AonMathUtils.round(rt));
		ai.getInvoice().setTaxableBase(AonMathUtils.round(tb));
	}
	
	public static void calculate(AccountingInvoice ai, InvoiceVAT vat) {
		vat.setQuota(AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 ));
		vat.setSurchargeQuota( ai.isSurcharge()
				?AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 )
				:0.0);
		vat.setDeductiblePercent(vat.getInvestAsset() != null
				?vat.getDeductiblePercent()
				:100.0);
		vat.setDeductibleQuota(vat.getInvestAsset() != null
				?AonMathUtils.round( vat.getQuota() * vat.getDeductiblePercent() / 100 )
				:vat.getQuota());
	}
	
	public static void reverseCalculate(AccountingInvoice ai, double total) {
		InvoiceVAT vat = ai.getFirstVat();
		double vatPerc = vat.getPercentage();
		double surchargePerc = vat.getSurcharge();
		double withHoldingPerc = 0.0;
		if (ai.isWithholding()) {
			withHoldingPerc = ai.getWithholdingData().getPercentage();
		}
		double tb = (ai.isVatEnabled()) 
				//? ((total * 100) / (100 + vatPerc + surchargePerc - withHoldingPerc))
				? reverseCalculate(vatPerc, surchargePerc, withHoldingPerc, total, 2, 2)
				: total;
		vat.setBase(tb);
		calculate(ai,vat);
		calculate(ai);
	}
	
	
	private static double reverseCalculate(double vatPercent, double surchargePercent, double retentionPercent, double total, int minPrecision, int maxPrecision) {
		total = AonMathUtils.round(total); 
		double base = 0;
		for (int i=minPrecision; i<=maxPrecision; i++) {
			base = AonMathUtils.round(total / (1 + vatPercent / 100 + surchargePercent / 100 - retentionPercent / 100), i);
			if (total == getTotal(vatPercent, surchargePercent , retentionPercent, base)) {
				break;
			} else {
				base = AonMathUtils.ceil(total / (1 + vatPercent / 100 - retentionPercent / 100), i);
				if (total == getTotal(vatPercent, surchargePercent ,retentionPercent, base)) {
					break;
				} else {
					base = AonMathUtils.floor(total / (1 + vatPercent / 100 - retentionPercent / 100), i);
					if (total == getTotal(vatPercent, surchargePercent ,retentionPercent, base)) {
						break;
					} else if (i < maxPrecision && i < 4) {
						base = AonMathUtils.round(base + 5 / Math.pow(10, i+1), i+1);
						if (total == getTotal(vatPercent, surchargePercent ,retentionPercent, base)) {
							break;
						}
					}
				}
			}
		}
		return base;
	}
	
	private static double getTotal(double vatPercent, double surchargePercent ,double retentionPercent, double price) {
		return AonMathUtils.round(price * (1 + vatPercent / 100 + surchargePercent / 100 - retentionPercent / 100));
	}
	

}
