package net.aonsolutions.occam.api.model;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculator	 {
	// private static final Logger LOGGER = Logger.getLogger(InvoiceCalculator.class.getName());
	
	private InvoiceCalculator() {
	}

	public static Invoice calculate(Invoice inv) {
		if (inv.getHeader().isWithholding() && inv.getWithholding().isEmpty()) {
			throw new AonCoreException(AonError.INVOICE_CALC_NO_WITHHOLDING_INFO.getMessage());
		}
		MutableDouble prepayments = new MutableDouble(0.0);
		inv.getHeader().setTaxableBase(0.0);
		inv.getHeader().setVatQuota(0.0);
		inv.getHeader().setRetentionQuota(0.0);
		
		inv.detailStream()
			.filter(det -> !det.isDeleted() )
			.forEach(det -> {
				calculateDetail(inv, det );
				if ( det.isPrepayment() ) {
					prepayments.setValue(AonMathUtils.round(AonMathUtils.round( prepayments.getValue() + det.getTaxableBase(), 4)));
				} else {
					inv.getHeader().setTaxableBase(AonMathUtils.round(AonMathUtils.round( inv.getHeader().getTaxableBase() + det.getTaxableBase(), 4)));
					
				}
		});
		inv.refreshTaxBreakdown();
		calculateTaxBreakdown( inv );

		settleVatAmounts(inv);
		settleWithholdingAmounts(inv);
		
		inv.getHeader().setVatQuota(inv.getTaxBreakdown().map( b -> b.getVatQuota() ).orElse(0.0));
		inv.getHeader().setRetentionQuota(inv.getTaxBreakdown()
			.flatMap( b -> b.getInvoiceWithholding())
			.map( w -> w.getQuota())
			.orElse(0.0)

		);
		
		inv.getHeader().setTotal(
				AonMathUtils.round(inv.getHeader().getTaxableBase() 
					+ prepayments.getValue() 
					+ inv.getTaxBreakdown().map( b -> b.getResult()).orElse(0.0)
				)
			);
		
		if (inv.hasFinances() && inv.getFinancesSize() == 1) {
			inv.financeStream().findFirst().ifPresent( f-> f.setAmount( inv.getHeader().getTotal()));
		}
		
		return inv;
	}
	
	private static void calculateTaxBreakdown(Invoice inv) {
		inv.getTaxBreakdown().ifPresent(tb -> tb.getVats().forEach(ib -> calculateVatBreakdown(inv, ib)));
		inv.getWithholding().ifPresent(iw -> calculateWithholdingBreakdown(inv, iw));
	}

	private static void settleVatAmounts(Invoice inv) {
		inv.getVats()
		.forEach(ib -> {
			LinkedList<InvoiceTax> stack = new LinkedList<>();
			MutableDouble quotaSum = new MutableDouble(0.0);
			MutableDouble surchargeQuotaSum = new MutableDouble(0.0);
			MutableDouble deductibleQuotaSum = new MutableDouble(0.0);
			inv.detailStream()
				.filter(det -> det.getVatTax().isPresent())
				.map(det -> det.getVatTax().get())
				.filter(ib::isSameGroup)
				.forEach( it -> {
					stack.push(it);
					quotaSum.setValue(AonMathUtils.round(quotaSum.getValue() + it.getQuota()));							
					surchargeQuotaSum.setValue(AonMathUtils.round(surchargeQuotaSum.getValue() + it.getSurchargeQuota()));
					deductibleQuotaSum.setValue(AonMathUtils.round(deductibleQuotaSum.getValue() + it.getDeductibleQuota()));
				}
			);
			try {
				InvoiceTax last = stack.pop();
				double gap = AonMathUtils.round( ib.getQuota() - quotaSum.getValue() );
				if ( AonMathUtils.isNotZero( gap )) {
					last.setQuota( AonMathUtils.round( last.getQuota() + gap));	
					last.setDeductibleQuota( AonMathUtils.round( last.getQuota() + last.getSurchargeQuota() ));
				}
				if (inv.getHeader().isSurcharge()) {
					double surchargeGap = AonMathUtils.round( ib.getSurchargeQuota() - surchargeQuotaSum.getValue() );
					if ( AonMathUtils.isNotZero( surchargeGap )) {
						last.setSurchargeQuota( AonMathUtils.round( last.getSurchargeQuota() + surchargeGap));	
						last.setDeductibleQuota( AonMathUtils.round( last.getQuota() + last.getSurchargeQuota() ));
					}
				}
			} catch (NoSuchElementException e) {
				// No debería dar
				e.printStackTrace();
			}
			ib.setDeductibleQuota( deductibleQuotaSum.getValue() );
		});
	}

	private static void settleWithholdingAmounts(Invoice inv) {
		if (inv.getHeader().isWithholding() && inv.getWithholding().isPresent()) {
			InvoiceWithholding iw = inv.getWithholding()
				.orElseThrow( () -> new AonCoreException(AonError.INVOICE_CALC_NO_WITHHOLDING_INFO.getMessage()));
			LinkedList<InvoiceTax> stack = new LinkedList<>();
			MutableDouble quotaSum = new MutableDouble(0.0);
			MutableDouble deductibleQuotaSum = new MutableDouble(0.0);
			inv.detailStream()
				.filter(det -> det.getWithholding().isPresent())
				.map(det -> det.getWithholding().get())
				.forEach( it -> {
					stack.push(it);
					quotaSum.setValue(AonMathUtils.round(quotaSum.getValue() + it.getQuota()));							
					deductibleQuotaSum.setValue(AonMathUtils.round(deductibleQuotaSum.getValue() + it.getDeductibleQuota()));
				}
			);
			if ( AonMathUtils.isNotZero( iw.getQuota() )) {
				try {
					InvoiceTax last = stack.pop();
					double gap = AonMathUtils.round( iw.getQuota() - quotaSum.getValue() );
					if ( AonMathUtils.isNotZero( gap )) {
						last.setQuota( AonMathUtils.round( last.getQuota() + gap));	
					}
				} catch (NoSuchElementException e) {
					// No debería dar
					e.printStackTrace();
				}
			} else {
				iw.setQuota( quotaSum.getValue() );	
			}
			iw.setDeductibleQuota( deductibleQuotaSum.getValue() );
		}
	}

	public static void calculateDetail(Invoice inv, InvoiceDetail detail) {
		MutableDouble mbase = new MutableDouble((detail.getPrice() + detail.getTaxes()) * detail.getQuantity());
		detail.getDiscountExpression().ifPresent( d ->
			AonCollectionUtils.stream( d.getDiscounts() )
				.forEach( dd -> mbase.setValue( mbase.getValue() * ( 1 - dd / 100)))
		);
		
		detail.setTaxableBase(AonMathUtils.round(mbase.getValue(), 4));
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
		detail.disableVat( inv );
		detail.disableWithholding(inv);
	}
	
	private static void calculateVatDetail(Invoice inv, InvoiceDetail detail) {
		InvoiceTax vat = detail.enableVatTax();
		vat.setBase(detail.getTaxableBase() );
		if (!vat.isQuotaEdited()) {
			vat.setQuota(AonMathUtils.round(vat.getBase() * vat.getPercentage() / 100 ));
		}
		if (inv.getHeader().isSurcharge()) {
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
			vat.setDeductibleQuota( AonMathUtils.round( vat.getQuota() + vat.getSurchargeQuota()) );
			vat.setDirectTaxPercent(0.0);
			vat.setDeductibleQuotaEdited(false);
		}
	}
	
	private static void calculateVatBreakdown(Invoice inv, InvoiceBreakdown ib) {
		if (!ib.isQuotaEdited()) {
			ib.setQuota(AonMathUtils.round(ib.getBase() * ib.getPercentage() / 100 ));
		}
		if (inv.getHeader().isSurcharge()) {
			if (!ib.isSurchargeQuotaEdited()) {
				ib.setSurchargeQuota( AonMathUtils.round(ib.getBase() * ib.getSurcharge() / 100 ));	
			}
		} else {
			ib.setSurcharge( 0.0);
			ib.setSurchargeQuota( 0.0);
			ib.setSurchargeQuotaEdited( false );
		}
	}
	private static void calculateWithholdingBreakdown(Invoice inv, InvoiceWithholding iw) {
		if (inv.getHeader().isWithholding()) {
			if (!iw.isQuotaEdited()) {
				iw.setQuota( AonMathUtils.round( iw.getBase() * iw.getPercentage() / 100, 2) );
			}
			iw.setDeductibleQuota( iw.getQuota() );
		}
	}

	
	private static void calculateWithholdingDetail(Invoice inv, InvoiceDetail detail) {
		if (inv.getHeader().isWithholding() && !inv.getHeader().isWithholdingFarmer()) {
			InvoiceWithholding wd = inv.getWithholding()
				.orElseThrow( () -> new AonCoreException(AonError.INVOICE_CALC_NO_WITHHOLDING_INFO.getMessage()));
			InvoiceTax irpf = detail.enableWithholding(inv);
			irpf.setBase(detail.getTaxableBase());
			irpf.setPercentage( wd.getPercentage() );
			irpf.setQuota( AonMathUtils.round( irpf.getBase() * irpf.getPercentage() / 100, 2) );
			irpf.setWithholdingType(wd.getWithholdingType());
			if ( detail.getInvestAsset().isPresent()) {
				if (!irpf.isDeductibleQuotaEdited()) {
					irpf.setDeductibleQuota( AonMathUtils.round( irpf.getQuota() * irpf.getDirectTaxPercent() / 100 ));
				}
			} else {
				irpf.setDeductiblePercent(100.0);
				irpf.setDeductibleQuota( irpf.getQuota() );
				irpf.setDirectTaxPercent(0.0);
				irpf.setDeductibleQuotaEdited(false);
			}
			
		}
	}
	
	private static void calculateWithholdingFarmerDetail(Invoice inv, InvoiceDetail detail) {
		if (inv.getHeader().isWithholding() && inv.getHeader().isWithholdingFarmer()) {
			InvoiceWithholding wd = inv.getWithholding()
					.orElseThrow( () -> new AonCoreException(AonError.INVOICE_CALC_NO_WITHHOLDING_INFO.getMessage()));
			InvoiceTax irpf = detail.enableWithholding(inv);
			InvoiceTax vat = detail.enableVatTax();
				
			irpf.setBase(detail.getTaxableBase() + vat.getQuota() + (inv.getHeader().isSurcharge()?vat.getSurchargeQuota():0.0));
			irpf.setPercentage( wd.getPercentage() );
			irpf.setQuota( AonMathUtils.round( irpf.getBase() * irpf.getPercentage() / 100, 2) );
			irpf.setWithholdingType(wd.getWithholdingType());
			
			irpf.setDeductiblePercent(100.0);
			irpf.setDeductibleQuota( irpf.getQuota() );
			irpf.setDirectTaxPercent(0.0);
			irpf.setDeductibleQuotaEdited(false);
				
		}
	}

	public static void reverseCalculate(Invoice inv, double total) {
		if (inv.getHeader().isWithholdingFarmer()) throw new AonCoreException(AonError.INVOICE_CALC_REV_FARMER.getMessage());
		long details = inv.detailStream().count();
		if (details <= 0) throw new AonCoreException(AonError.INVOICE_CALC_REV_ZERO.getMessage());
		if (details > 1) throw new AonCoreException(AonError.INVOICE_CALC_REV_MORE.getMessage());
		if (inv.detailStream().count() == 1) {
			InvoiceDetail det = inv.detailStream()
				.findFirst()
				.orElseThrow( () -> new AonCoreException(AonError.INVOICE_CALC_REV_ZERO.getMessage()) ); 
			reverseCalculate(inv, det, total);
		}
	}
	
	private static void reverseCalculate(Invoice inv, InvoiceDetail det, double total) {
		if ( det.isPrepayment() || !inv.isVatEnabled() ) {
			det.setPrice(total);
			det.setQuantity(1.0);
			det.setDiscount(0.0);
			det.disableVat(inv);
			det.disableWithholding(inv);
		} else {
			InvoiceTax it = det.enableVatTax();
			double vatPerc = it.getPercentage();
			double surchargePerc = it.getSurcharge();
			double withholdingPerc = 0.0;
			if (inv.getHeader().isWithholding()) {
				withholdingPerc = inv.getWithholding().map( w -> w.getPercentage()).orElse(0.0);
			}
			double tb = reverseCalculate(vatPerc, surchargePerc, withholdingPerc, total);
			det.setPrice(tb);
			det.setTaxes(0.0);
			det.setQuantity(1.0);
			det.setDiscount(0.0);
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
}
