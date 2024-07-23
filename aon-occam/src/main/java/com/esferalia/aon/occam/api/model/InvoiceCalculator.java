package com.esferalia.aon.occam.api.model;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculator {
	private static final Logger LOGGER = Logger.getLogger(InvoiceCalculator.class.getName());
	

	private static boolean mustAddVatToTotal(AccountingInvoice ai) {
		return isVatEnabled(ai);
	}
	private static boolean isVatEnabled(AccountingInvoice ai) {
		return (ai.isInputVatEnabled() != ai.isOutputVatEnabled());
	}
	public static void calculate(AccountingInvoice ai) {
		double vt = 0.0;
		double tb = 0.0;
		double wb = 0.0; 
		if (ai.getVats() != null) {
			for (InvoiceVAT vat : ai.getVats()) {
				tb = tb + vat.getBase();
				if (mustAddVatToTotal(ai)) {
					vt = vt + (vat.getQuota() + vat.getSurchargeQuota());
				}
				if (ai.isWithholding() && vat.isWithholding()) {
					if (ai.isWithholdingFarmer()) {
						wb = wb + vat.getBase() + vat.getQuota() + (ai.isSurcharge()?vat.getSurchargeQuota():0.0);
					} else {
						wb = wb + vat.getBase();
					}
				}
			}
		}
		double wp = ai.getWithholdingData().getPercentage();
		double rt =  (ai.getWithholdingData().isQuotaEdited())
			?ai.getWithholdingData().getQuota()
			:AonMathUtils.round(wb * wp / 100);
		double t = AonMathUtils.round(tb + vt - rt);
		ai.getWithholdingData().setBase(wb);
		ai.getWithholdingData().setQuota(rt);
		ai.getInvoice().setTotal(t);
		ai.getInvoice().setVatQuota(AonMathUtils.round(vt));
		ai.getInvoice().setRetentionQuota(AonMathUtils.round(rt));
		ai.getInvoice().setTaxableBase(AonMathUtils.round(tb));
		if (ai.getInvoice().hasFinances() && ai.getInvoice().getFinances().size() == 1 && ai.getInvoice().getFinances().get(0).isPending()) {
			ai.getInvoice().getFinances().get(0).setAmount(t);
		}
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

		if ( vat.getInvestAsset() != null ) {
			if (!vat.isDeductibleQuotaEdited()) {
				vat.setDeductibleQuota( AonMathUtils.round( vat.getQuota() * vat.getDeductiblePercent() / 100 ));
			}
		} else {
			vat.setDeductiblePercent(100.0);
			vat.setDeductibleQuota( vat.getQuota() );
			vat.setDirectTaxPercent(0.0);
		}
		
		vat.syncChangesToWrappedDetail();
		
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
		double tb = (isVatEnabled( ai ) || ai.isWithholding()) 
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

	public static double getQuotaGap(InvoiceWithholding invoiceWitholding, Double quota) {
		if (quota == null) quota = 0.0;
		return AonMathUtils.round(quota - getQuota(invoiceWitholding)); 		
	}
	public static double getQuota(InvoiceWithholding invoiceWitholding) {
		return AonMathUtils.round(invoiceWitholding.getBase() * invoiceWitholding.getPercentage() / 100 );		
	}
	public static double getQuota(InvoiceVAT vat) {
		return AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 );		
	}
	public static double getQuotaGap(InvoiceVAT vat, Double quota) {
		if (quota == null) quota = 0.0;
		return AonMathUtils.round(quota - getQuota(vat)); 		
	}
	public static double getSurchargeQuota(InvoiceVAT vat) {
		return AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 );		
	}
	public static double getSurchargeQuotaGap(InvoiceVAT vat, Double surchargeQuota) {
		if (surchargeQuota == null) surchargeQuota = 0.0;
		return AonMathUtils.round(surchargeQuota - getSurchargeQuota(vat));
	}
	public static double getDeductibleQuota(InvoiceVAT vat) {
		return AonMathUtils.round( (vat.getBase() * vat.getPercentage() / 100) * vat.getDeductiblePercent() / 100);		
	}
	public static double getDeductibleQuotaGap(InvoiceVAT vat, Double deductibleQuota) {
		if (deductibleQuota == null) deductibleQuota = 0.0;
		return AonMathUtils.round(deductibleQuota - AonMathUtils.round(vat.getQuota() * vat.getDeductiblePercent() / 100));		
	}

	public static double getDirectTaxNoDedExpenses(InvoiceVAT vat) {
		double percent = AonMathUtils.round(100 - vat.getDirectTaxPercent());
		return AonMathUtils.round(vat.getBase() *  percent / 100);		
	}

	public static double getDirectTaxNoDedExpensesGap(InvoiceVAT vat, Double directTaxQuota) {
		if (directTaxQuota == null) directTaxQuota = 0.0;
		return AonMathUtils.round(directTaxQuota - getDirectTaxNoDedExpenses(vat));		
	}
	
	public static void calculateDUAInfo( AccountingInvoice ai ) {
		if ( ai.getDuaInvoice() != null && ai.getDuaInvoice().getInfo() != null) {
			AccountingDUAInfo info = ai.getDuaInvoice().getInfo();
			double prc = info.getPrice();
			double adj = info.getAdjust();
			double stv = AonMathUtils.round(prc + adj);
			info.setStatisticalValue(stv);
			info.setDutyBase(stv);
			double bas = info.getDutyBase();
			double per = info.getDutyPercent();
			double tot = AonMathUtils.round(bas * per / 100 );
			info.setDutyTotal(tot);
		}
	}
	
	public static double getDutyTotal(AccountingDUAInfo info) {
		return AonMathUtils.round(info.getDutyBase() * info.getDutyPercent() / 100 );		
	}
	public static double getDutyTotalGap(AccountingDUAInfo info, Double total) {
		if (total == null) total = 0.0;
		return AonMathUtils.round(total - getDutyTotal(info)); 		
	}
	
	public static void calculateViaDUA(AccountingInvoice ai) {
		LinkedList<InvoiceVAT> vats = new LinkedList<InvoiceVAT>();  
		for (InvoiceVAT vat : ai.getVats()) {
			if ( !vat.isPrepayment() ) {
				vats.add(vat);		
			}
		}
		AccountingDUAInvoice duaInvoice = ai.getDuaInvoice(); 
		if (duaInvoice != null) {
			ai.setPrepayments(true);
			double totalQuota = 0;
			if (duaInvoice.getInfo() != null && duaInvoice.getInfo().isAuthCalcEnabled()) {
				// (1) Se hace un mapa con las bases y los porcentajes de iva.
				LinkedList<InvoiceVAT> duaVats = new LinkedList<InvoiceVAT>();
				for (InvoiceVAT ori : duaInvoice.getAccountingInvoice().getVats()) {
					InvoiceVAT vat = ori.clone();
					vat.setAutoGenerated(true);
					duaVats.add(vat);
					
					double factor = ori.getBase() / duaInvoice.getAccountingInvoice().getTotalInvoice();  
					
					vat.setBase((duaInvoice.getInfo().getStatisticalValue() * factor) + (duaInvoice.getInfo().getDutyTotal() * factor));
					LOGGER.info("(" + duaInvoice.getInfo().getStatisticalValue()+" * "+ factor+") + ("+duaInvoice.getInfo().getDutyTotal()+" * "+factor+")");
					double vatQuota = AonMathUtils.round( vat.getBase() * vat.getPercentage() / 100 );
					vat.setQuota(vatQuota);
					double vatSurchargeQuota = AonMathUtils.round( vat.getBase() * vat.getSurcharge() / 100 );
					vat.setSurchargeQuota(vatSurchargeQuota);
					vat.setDeductibleQuota(AonMathUtils.round(vatQuota + vatSurchargeQuota));
					totalQuota = AonMathUtils.round(totalQuota + vat.getDeductibleQuota());
				}
				duaInvoice.getInfo().setDuaVats(duaVats);
			} else {
				for (InvoiceVAT vat : duaInvoice.getInfo().getDuaVats() ) {
					double vatQuota = vat.getQuota();
					double vatSurchargeQuota = vat.getSurchargeQuota();
					vat.setDeductibleQuota(AonMathUtils.round(vatQuota + vatSurchargeQuota));
					totalQuota = AonMathUtils.round(totalQuota + vat.getDeductibleQuota());
				}
			}
			if (AonMathUtils.isNotZero( totalQuota )) {
				Account vatAccount = duaInvoice.getInfo().getVatAccount(); 
				vats.add( new InvoiceVAT()
						.setPrepayment(true)
						.setAutoGenerated(true)
						.setExpAccount(vatAccount)
						.setBase( totalQuota  )
						);
			}
			if ( AonMathUtils.isNotZero( duaInvoice.getInfo().getDutyTotal() )) {
				Account dutyAccount = duaInvoice.getInfo().getDutyAccount();
				vats.add( new InvoiceVAT()
						.setPrepayment(true)
						.setAutoGenerated(true)
						.setExpAccount(dutyAccount)
						.setBase(duaInvoice.getInfo().getDutyTotal())
						);
			}
		}
		ai.setVats(vats);
	}
}
