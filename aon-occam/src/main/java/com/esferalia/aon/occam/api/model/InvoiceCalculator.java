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
		if (vat.isPrepayment()) {
			vat.setPercentage(0.0);
			vat.setSurcharge(0.0);
			vat.setDeductiblePercent(0.0);
			vat.setWithholding(false);
			vat.setQuotaEdited(false);
			vat.setSurchargeQuotaEdited(false);
			vat.setDeductibleQuotaEdited(false);
		}
		if (!vat.isQuotaEdited()) {
			vat.setQuota(AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 ));
		}
		if (ai.isSurcharge()) {
			if (!vat.isSurchargeQuotaEdited()) {
				vat.setSurchargeQuota( AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 ));	
			}
		} else {
			vat.setSurchargeQuota( 0.0);	
		}
		vat.setDeductiblePercent(vat.getInvestAsset() != null?vat.getDeductiblePercent():100.0);
		if ( vat.getInvestAsset() != null ) {
			if (!vat.isDeductibleQuotaEdited()) {
				vat.setDeductibleQuota( AonMathUtils.round( vat.getQuota() * vat.getDeductiblePercent() / 100 ));
			}
		} else {
			vat.setDeductibleQuota( vat.getQuota() );
		}
	}
	
	public static void reverseCalculate(AccountingInvoice ai, double total) {
		reverseCalculate(ai, ai.getFirstVat(),total);
	}
	
	public static void reverseCalculate(AccountingInvoice ai, InvoiceVAT vat, double total) {
		
		double vatPerc = vat.getPercentage();
		double surchargePerc = vat.getSurcharge();
		double withholdingPerc = 0.0;
		if (ai.isWithholding()) {
			withholdingPerc = ai.getWithholdingData().getPercentage();
		}
		double tb = (ai.isVatEnabled() || ai.isWithholding()) 
				? reverseCalculate(vatPerc, surchargePerc, withholdingPerc, total)
				: total;
		vat.setBase(tb);
		calculate(ai,vat);
		calculate(ai);
	}
	
	private static double reverseCalculate(double vatPercent, double surchargePercent, double withholdingPerc, double total) {
		total = AonMathUtils.round(total);
		double base = 0;
		for (int i=2; i<=4; i++) {
			base = AonMathUtils.round(total / ( 1 + (vatPercent / 100) + (surchargePercent / 100) - (withholdingPerc / 100)), i);
			if (total == getTotal(vatPercent, surchargePercent , withholdingPerc, base)) {
				break;
			} else {
				base = AonMathUtils.ceil(total / (1 + (vatPercent / 100) + (surchargePercent / 100) - (withholdingPerc / 100)), i);
				if (total == getTotal(vatPercent, surchargePercent ,withholdingPerc, base)) {
					break;
				} else {
					base = AonMathUtils.floor(total / (1 + (vatPercent / 100) + (surchargePercent / 100) - (withholdingPerc / 100)), i);
					if (total == getTotal(vatPercent, surchargePercent ,withholdingPerc, base)) {
						break;
					} else if (i < 4) {
						base = AonMathUtils.round(base + 5 / Math.pow(10, i+1), i+1);
						if (total == getTotal(vatPercent, surchargePercent ,withholdingPerc, base)) {
							break;
						}
					}
				}
			}
		}
		return base;
	}
	
	private static double getTotal(double vatPercent, double surchargePercent ,double retentionPercent, double base) {
		return AonMathUtils.round(base 
				+ AonMathUtils.round(base * vatPercent / 100) 
				+ AonMathUtils.round(base * surchargePercent / 100) 
				- AonMathUtils.round(base * retentionPercent / 100)
				);
	}

	public static double getQuota(InvoiceVAT vat) {
		return AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 );		
	}
	public static double getQuotaGap(InvoiceVAT vat, Double quota) {
		if (quota == null) quota = 0.0;
		return AonMathUtils.round(quota - AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 )); 		
	}
	public static double getSurchargeQuota(InvoiceVAT vat) {
		return AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 );		
	}
	public static double getSurchargeQuotaGap(InvoiceVAT vat, Double surchargeQuota) {
		if (surchargeQuota == null) surchargeQuota = 0.0;
		return AonMathUtils.round(surchargeQuota - AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100));		
	}
	public static double getDeductibleQuota(InvoiceVAT vat) {
		return AonMathUtils.round( (vat.getBase() * vat.getPercentage() / 100) * vat.getDeductiblePercent() / 100 );		
	}
	public static double getDeductibleQuotaGap(InvoiceVAT vat, Double deductibleQuota) {
		if (deductibleQuota == null) deductibleQuota = 0.0;
		return AonMathUtils.round(deductibleQuota - AonMathUtils.round(vat.getQuota() * vat.getDeductiblePercent() / 100));		
	}
	

}
