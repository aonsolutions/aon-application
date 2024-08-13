package com.esferalia.aon.occam.api.model;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
// import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculator	 {
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceCalculator.class.getName());
	
	private InvoiceCalculator() {
	}

	public static void calculate(Invoice inv) {
		AonCollectionUtils.stream(inv.getDetails())
			.forEach(det -> calculateDetail(inv, det ));

		settleVatAmounts(inv);
		settleWithholdingAmounts(inv);
		
		for (InvoiceDetail det :  inv.getDetails() ) {
			inv.setTaxableBase(AonMathUtils.round(AonMathUtils.round( inv.getTaxableBase() + det.getTaxableBase(), 4)));
		}
		inv.setVatQuota(inv.getTaxBreakdown().map( b -> b.getVatQuota() ).orElse(0.0));
		inv.setRetentionQuota(inv.getTaxBreakdown()
			.flatMap( b -> b.getInvoiceWithholding())
			.map( w -> w.getQuota())
			.orElse(0.0)
		);
		inv.setTotal(inv.getTaxableBase() + inv.getTaxBreakdown().map( b -> b.getResult() ).orElse(0.0));
	}
	
	private static void settleVatAmounts(Invoice inv) {
		for (InvoiceBreakdown ib :  inv.getVats() ) {
			MutableObject<InvoiceTax> lastInvoiceTax = new MutableObject<>();
			MutableDouble quotaSum = new MutableDouble(0.0);
			MutableDouble surchargeQuotaSum = new MutableDouble(0.0);
			MutableDouble deductibleQuotaSum = new MutableDouble(0.0);
			for (InvoiceDetail det :  inv.getDetails() ) {
				det.getVatTax().ifPresent( it -> {
					if (ib.isSameGroup(it)) {
						lastInvoiceTax.setValue(it);
						quotaSum.setValue(AonMathUtils.round(quotaSum.getValue() + it.getQuota()));							
						surchargeQuotaSum.setValue(AonMathUtils.round(surchargeQuotaSum.getValue() + it.getSurchargeQuota()));
						deductibleQuotaSum.setValue(AonMathUtils.round(deductibleQuotaSum.getValue() + it.getDeductibleQuota()));
					}
				});
			}
			InvoiceTax last = lastInvoiceTax.getValue();
			if (last != null) {
				double gap = AonMathUtils.round( ib.getQuota() - quotaSum.getValue() );
				if ( AonMathUtils.isNotZero( gap )) {
					last.setQuota( AonMathUtils.round( last.getQuota() - gap));	
				}
				
				double surchargeGap = AonMathUtils.round( ib.getSurchargeQuota() - surchargeQuotaSum.getValue() );
				if ( AonMathUtils.isNotZero( surchargeGap )) {
					last.setSurchargeQuota( AonMathUtils.round( last.getSurchargeQuota() - surchargeGap));	
				}
			}
			ib.setDeductibleQuota( deductibleQuotaSum.getValue() );
		};
	}

	private static void settleWithholdingAmounts(Invoice inv) {
		if (inv.isWithholding()
		 && inv.getWithholding().isPresent()) {
			InvoiceWithholding iw = inv.getWithholding().get(); 
			MutableObject<InvoiceTax> lastInvoiceTax = new MutableObject<>();
			MutableDouble quotaSum = new MutableDouble(0.0);
			MutableDouble deductibleQuotaSum = new MutableDouble(0.0);
			for (InvoiceDetail det :  inv.getDetails() ) {
				det.getWithholdingTax().ifPresent( it -> {
					lastInvoiceTax.setValue(it);
					quotaSum.setValue(AonMathUtils.round(quotaSum.getValue() + it.getQuota()));							
					deductibleQuotaSum.setValue(AonMathUtils.round(deductibleQuotaSum.getValue() + it.getDeductibleQuota()));
				});
			}
			InvoiceTax last = lastInvoiceTax.getValue();
			if (last != null) {
				double gap = AonMathUtils.round( iw.getQuota() - quotaSum.getValue() );
				if ( AonMathUtils.isNotZero( gap )) {
					last.setQuota( AonMathUtils.round( last.getQuota() - gap));	
				}
			}
			iw.setDeductibleQuota( deductibleQuotaSum.getValue() );
		}
	}

	public static void calculateDetail(Invoice inv, InvoiceDetail detail) {
		double base = (detail.getPrice() + detail.getTaxes()) * detail.getQuantity();
		if (detail.getDiscountExpression().getDiscounts() != null) {
			for (int i = 0;i<detail.getDiscountExpression().getDiscounts().length;i++) {
				base = base * ( 1 - detail.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		detail.setTaxableBase(AonMathUtils.round(base, 4));
		
		if (detail.isPrepayment()) {
			calculatePrepaymentDetail(inv, detail);			
		} else {
			calculateVatDetail(inv, detail);
			calculateWithholdingDetail(inv, detail);
			calculateWithholdingFarmerDetail(inv, detail);
		}
	}

	private static void calculatePrepaymentDetail(Invoice inv, InvoiceDetail detail) {
		detail.setInvestAsset(null);
		detail.deleteVatTax();
		detail.deleteWithholdingTax();
	}
	
	private static void calculateVatDetail(Invoice inv, InvoiceDetail detail) {
		InvoiceTax vat = detail.ensureVatTax();
		if (!vat.isQuotaEdited()) {
			vat.setQuota(AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 ));
		}
		 // Recargo de equivalencia.
		if (inv.isSurcharge()) {
			if (!vat.isSurchargeQuotaEdited()) {
				vat.setSurchargeQuota( AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 ));	
			}
		} else {
			vat.setSurcharge( 0.0);
			vat.setSurchargeQuota( 0.0);
			vat.setSurchargeQuotaEdited( false );
		}
		
		if ( detail.getInvestAsset().isPresent()) {
			if (!vat.isDeductibleQuotaEdited()) {
				vat.setDeductibleQuota( AonMathUtils.round( vat.getQuota() * vat.getDeductiblePercent() / 100 ));
			}
		} else {
			vat.setDeductiblePercent(100.0);
			vat.setDeductibleQuota( vat.getQuota() );
			vat.setDirectTaxPercent(0.0);
			vat.setDeductibleQuotaEdited(false);
		}
	}
	
	private static void calculateWithholdingDetail(Invoice inv, InvoiceDetail detail) {
		if (inv.isWithholding() 
			&& !inv.isWithholdingFarmer() 
			&& detail.getWithholdingTax().isPresent()
			&& inv.getWithholding().isPresent()) {
			
			double base = detail.getTaxableBase();
			double percentage = inv.getWithholding().get().getPercentage();
			double quota = AonMathUtils.round( base * percentage / 100, 2);
			WithholdingType wt = inv.getWithholding().get().getWithholdingType();
			detail.ensureWithholdingTax(inv).setBase(base);
			detail.ensureWithholdingTax(inv).setPercentage( percentage );
			detail.ensureWithholdingTax(inv).setPercentage( quota );
			detail.ensureWithholdingTax(inv).setWithholdingType(wt);
			
		}
	}
	
	private static void calculateWithholdingFarmerDetail(Invoice inv, InvoiceDetail detail) {
		if (inv.isWithholding() 
			&& inv.isWithholdingFarmer() 
			&& detail.getWithholdingTax().isPresent()) {
				InvoiceTax vat = detail.ensureVatTax();	
				detail.ensureVatTax().setBase(detail.getTaxableBase());
				detail.ensureVatTax().setBase(detail.getTaxableBase() + vat.getQuota() + (inv.isSurcharge()?vat.getSurchargeQuota():0.0));
			}
	}

	public static void reverseCalculate(Invoice inv, double total) {
		InvoiceDetail det = AonCollectionUtils.stream(inv.getDetails())
			.findFirst()
			.orElseThrow( () -> new AonCoreException("No hay detalles de fatura") ); 
		reverseCalculate(inv, det, total);
	}
	
	public static void reverseCalculate(Invoice inv, InvoiceDetail det, double total) {
		if ( det.isPrepayment() || !inv.isVatEnabled() ) {
			det.setTaxableBase(total);
			det.deleteVatTax();
			det.deleteWithholdingTax();
		} else {
			InvoiceTax vat = det.ensureVatTax();
			double vatPerc = vat.getPercentage();
			double surchargePerc = vat.getSurcharge();
			double withholdingPerc = 0.0;
			if (inv.isWithholding()) {
				withholdingPerc = inv.getWithholding().map( w -> w.getPercentage()).orElse(0.0);
			}
			double tb = reverseCalculate(vatPerc, surchargePerc, withholdingPerc, total);
			vat.setBase(tb);
			calculateDetail(inv,det);
			calculate(inv);
		}
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
	
	public static boolean isQuotaEdited(InvoiceTax tax) {
		return (AonMathUtils.isNotZero(tax.getQuota()) 
			&& AonMathUtils.isNotZero(getQuotaGap(tax, tax.getQuota())));
	}
	public static double getQuota(InvoiceTax vat) {
		return AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 );		
	}
	public static double getQuotaGap(InvoiceTax tax, Double quota) {
		if (quota == null) quota = 0.0;
		return AonMathUtils.round(quota - getQuota(tax)); 		
	}
	public static boolean isSurchargeQuotaEdited(InvoiceTax tax) {
		return (AonMathUtils.isNotZero(tax.getSurchargeQuota()) 
			&& AonMathUtils.isNotZero(getSurchargeQuotaGap(tax, tax.getSurchargeQuota())));
	}
	public static double getSurchargeQuota(InvoiceTax tax) {
		return AonMathUtils.round(tax.getBase() * tax.getSurcharge() / 100 );		
	}
	public static double getSurchargeQuotaGap(InvoiceTax tax, Double surchargeQuota) {
		if (surchargeQuota == null) surchargeQuota = 0.0;
		return AonMathUtils.round(surchargeQuota - getSurchargeQuota(tax));
	}
	public static boolean isDeductibleQuotaEdited(InvoiceTax tax) {
		return (AonMathUtils.isNotZero(tax.getDeductibleQuota()) 
			&& AonMathUtils.isNotZero(getDeductibleQuotaGap(tax, tax.getDeductibleQuota())));
	}
	public static double getDeductibleQuota(InvoiceTax tax) {
		return AonMathUtils.round( (tax.getBase() * tax.getPercentage() / 100) * tax.getDeductiblePercent() / 100);		
	}
	public static double getDeductibleQuotaGap(InvoiceTax tax, Double deductibleQuota) {
		if (deductibleQuota == null) deductibleQuota = 0.0;
		return AonMathUtils.round(deductibleQuota - AonMathUtils.round(tax.getQuota() * tax.getDeductiblePercent() / 100));		
	}
	
	public static double getDirectTaxNoDedExpenses(InvoiceTax vat) {
		double percent = AonMathUtils.round(100 - vat.getDirectTaxPercent());
		return AonMathUtils.round(vat.getBase() *  percent / 100);		
	}
	
	public static double getDirectTaxNoDedExpensesGap(InvoiceTax vat, Double directTaxQuota) {
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

	public static void calculateViaDUA(AccountingInvoice ai) {
		LinkedList<InvoiceDetail> vats = new LinkedList<>();
		for (InvoiceDetail vat : ai.getInvoice().getDetails()) {
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
				LinkedList<InvoiceDetail> duaVats = new LinkedList<>();
				for (InvoiceDetail ori : duaInvoice.getAccountingInvoice().getInvoice().getDetails()) {
					InvoiceDetail vat = ori.copy();
					vat.setAutoGenerated(true);
					duaVats.add(vat);
					
					double factor = ori.getTaxableBase() / duaInvoice.getAccountingInvoice().getTotalInvoice();  
					
					vat.setTaxableBase((duaInvoice.getInfo().getStatisticalValue() * factor) + (duaInvoice.getInfo().getDutyTotal() * factor));
					LOGGER.info("(" + duaInvoice.getInfo().getStatisticalValue()+" * "+ factor+") + ("+duaInvoice.getInfo().getDutyTotal()+" * "+factor+")");
					double vatQuota = AonMathUtils.round( vat.getTaxableBase() * vat.ensureVatTax().getPercentage() / 100 );
					vat.ensureVatTax().setBase(vat.getTaxableBase());
					vat.ensureVatTax().setQuota(vatQuota);
					double vatSurchargeQuota = AonMathUtils.round( vat.getTaxableBase() * vat.ensureVatTax().getSurcharge() / 100 );
					vat.ensureVatTax().setSurchargeQuota(vatSurchargeQuota);
					vat.ensureVatTax().setDeductibleQuota(AonMathUtils.round(vatQuota + vatSurchargeQuota));
					totalQuota = AonMathUtils.round(totalQuota + vat.ensureVatTax().getDeductibleQuota());
				}
				duaInvoice.getInfo().setDuaDetails(duaVats);
			} else {
				for (InvoiceDetail vat : duaInvoice.getInfo().getDuaDetails() ) {
					double vatQuota = vat.ensureVatTax().getQuota();
					double vatSurchargeQuota = vat.ensureVatTax().getSurchargeQuota();
					vat.ensureVatTax().setDeductibleQuota(AonMathUtils.round(vatQuota + vatSurchargeQuota));
					totalQuota = AonMathUtils.round(totalQuota + vat.ensureVatTax().getDeductibleQuota());
				}
			}
			if (AonMathUtils.isNotZero( totalQuota )) {
				Account vatAccount = duaInvoice.getInfo().getVatAccount(); 
				vats.add( new InvoiceDetail()
					.setPrepayment(true)
					.setExpAccount(vatAccount)
					.setQuantity( 1 )
					.setPrice( totalQuota  )
					.setAutoGenerated(true)
				);
			}
			if ( AonMathUtils.isNotZero( duaInvoice.getInfo().getDutyTotal() )) {
				Account dutyAccount = duaInvoice.getInfo().getDutyAccount();
				vats.add( new InvoiceDetail()
					.setPrepayment(true)
					.setExpAccount(dutyAccount)
					.setQuantity( 1 )
					.setPrice(duaInvoice.getInfo().getDutyTotal())
					.setAutoGenerated(true)
				);
			}
		}
		ai.getInvoice().setDetails(vats);
	}

	public static double getQuotaGap(InvoiceWithholding invoiceWitholding, Double quota) {
		if (quota == null) quota = 0.0;
		return AonMathUtils.round(quota - getQuota(invoiceWitholding)); 		
	}
	public static double getQuota(InvoiceWithholding invoiceWitholding) {
		return AonMathUtils.round(invoiceWitholding.getBase() * invoiceWitholding.getPercentage() / 100 );		
	}
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	// ***************************************************************
	
	

//	public static double getQuota(InvoiceVAT vat) {
//		return AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 );		
//	}
//	public static double getQuotaGap(InvoiceVAT vat, Double quota) {
//		if (quota == null) quota = 0.0;
//		return AonMathUtils.round(quota - getQuota(vat)); 		
//	}
//	public static double getSurchargeQuota(InvoiceVAT vat) {
//		return AonMathUtils.round(vat.getBase() * vat.getSurcharge() / 100 );		
//	}
//	public static double getSurchargeQuotaGap(InvoiceVAT vat, Double surchargeQuota) {
//		if (surchargeQuota == null) surchargeQuota = 0.0;
//		return AonMathUtils.round(surchargeQuota - getSurchargeQuota(vat));
//	}
//	public static double getDeductibleQuota(InvoiceVAT vat) {
//		return AonMathUtils.round( (vat.getBase() * vat.getPercentage() / 100) * vat.getDeductiblePercent() / 100);		
//	}
//	public static double getDeductibleQuotaGap(InvoiceVAT vat, Double deductibleQuota) {
//		if (deductibleQuota == null) deductibleQuota = 0.0;
//		return AonMathUtils.round(deductibleQuota - AonMathUtils.round(vat.getQuota() * vat.getDeductiblePercent() / 100));		
//	}
//
//	
//	
//	public static double getDutyTotal(AccountingDUAInfo info) {
//		return AonMathUtils.round(info.getDutyBase() * info.getDutyPercent() / 100 );		
//	}
//	public static double getDutyTotalGap(AccountingDUAInfo info, Double total) {
//		if (total == null) total = 0.0;
//		return AonMathUtils.round(total - getDutyTotal(info)); 		
//	}
//	
//
}
