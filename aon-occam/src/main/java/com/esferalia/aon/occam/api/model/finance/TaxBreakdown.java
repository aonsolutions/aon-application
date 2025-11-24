package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType.InvoiceTransactionTypeVisitor;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class TaxBreakdown implements Serializable {
	
	private static final long serialVersionUID = -5156527112729566231L;
	
	private LinkedList<InvoiceBreakdown> ibs = new LinkedList<>();
	
	public boolean isEmpty() {
		 return ibs == null || ibs.isEmpty();
	}
	public boolean isNotEmpty() {
		return !isEmpty();
	}
	public List<InvoiceBreakdown> getBreakdown() {
		return ibs;
	}
	public Stream<InvoiceBreakdown> stream() {
		return ibs == null ? Stream.empty() : ibs.stream();
	}
	public Stream<InvoiceBreakdown> vatStream() {
		return ibs.stream()
			.filter(ib -> ib.isVat() )
			.sorted((ib,ib1) -> AonNumberUtils.compare(ib.getPercentage(),ib1.getPercentage()))
		;
	}
	public Stream<InvoiceBreakdown> withholdingStream() {
		return ibs.stream()
			.filter(ib -> ib.isWithholding() )
			.sorted((ib,ib1) -> AonNumberUtils.compare(ib.getPercentage(),ib1.getPercentage()))
		;
	}
	
	public TaxBreakdown add(InvoiceTax it) {
		return add(InvoiceBreakdown.from(it));
	}
	
	public TaxBreakdown add(InvoiceBreakdown ib) {
		check( ib );
		Optional<InvoiceBreakdown> oib = get( ib );
		if (oib.isPresent()) {
			oib.get().add(ib);
		} else {
			ibs.add( ib );
		}
		return this;
	}
	
	private void check(InvoiceBreakdown ib) {
		if (ib == null) throw new AonCoreException("No se puede agregar un desglose nulo" );
		if (ib.isVat() ) {
			ib.setWithholdingType(null);
			if (ib.getVatDeductionType() == null) {
				ib.setVatDeductionType( VatDeductionType.WITH_RIGHT );
			}
		}
		if (ib.isWithholding() ) {
			ib.setVatDeductionType( null );	
		}
	}
	private Optional<InvoiceBreakdown> get(InvoiceBreakdown b) {
		return ibs.stream().filter(a -> a.isSameGroup(b)).findFirst();
	}
	
	public List<InvoiceBreakdown> getVats() {
		return vatStream().collect(Collectors.toCollection(LinkedList::new));
	}

	public Optional<InvoiceWithholding> getInvoiceWithholding() {
		return ibs.stream()
			.filter(ib -> ib.isWithholding() )
			.map( ib -> new InvoiceWithholding()
				.setBase(ib.getBase())
				.setQuota(ib.getQuota())
				.setPercentage(ib.getPercentage())
				.setWithholdingType(ib.getWithholdingType()))
			.findFirst();
	}
	
	public double getVatBase() {
		return AonMathUtils.round( AonCollectionUtils.stream( getVats() ).mapToDouble( t -> t.getBase() ).sum() , 4); 
	}

	public double getVatQuota() {
		return AonMathUtils.round( AonCollectionUtils.stream( getVats() ).mapToDouble( t -> t.getQuota() ).sum() , 2); 
	}
	
	public double getSurchargeQuota() {
		return AonMathUtils.round( AonCollectionUtils.stream( getVats() ).mapToDouble( t -> t.getSurchargeQuota() ).sum() , 2); 
	}
	
	public double getRetentionBase() {
		Optional<InvoiceWithholding> oiw = getInvoiceWithholding();
		if (oiw.isPresent()) {
			return AonMathUtils.round(oiw.get().getBase(), 4);
		}
		return 0.0;
	}
	public double getRetentionQuota() {
		Optional<InvoiceWithholding> oiw = getInvoiceWithholding();
		if (oiw.isPresent()) {
			return AonMathUtils.round(oiw.get().getQuota(), 2);
		}
		return 0.0;
	}
	public double getResult() {
		return AonMathUtils.round(getVatQuota() - getRetentionQuota());
	}
	
	public void refresh(Invoice invoice) {
		ibs = new LinkedList<>();
		
		// Se añaden los suplidos como NO SUJETOS
		invoice.detailStream()
			// ... que sean suplidos
			.filter(d -> d.isPrepayment())
			// ... que tengan base imponible
			.filter(d -> AonMathUtils.isNotZero(d.getTaxableBase()))
			.map( d ->  new InvoiceBreakdown()
				.setTaxType( TaxType.VAT )
				.setBase(d.getTaxableBase())
				.setVatDeductionType( VatDeductionType.NON_TAXABLE )
				.setPrepayment(true)
			)
			.forEach( this::add );
			
		// Se añaden las líneas de facturas 
		invoice.detailStream()
			// ... que no sean suplidos
			.filter(d -> !d.isPrepayment())
			// ... que tengan base imponible
			.filter(d -> AonMathUtils.isNotZero(d.getTaxableBase()))
			// ... que no tengan impuestos definidos
			.filter(d -> !d.hasTaxes())
			.map( d ->  { 
				InvoiceBreakdown ib = new InvoiceBreakdown()
					.setTaxType( TaxType.VAT )
					.setBase(d.getTaxableBase())
				;
				return ensureData(invoice,ib, true);
			})
			.forEach( this::add );
			
		// Se añaden los invoice_tax "normales" de la factura 
		invoice.detailStream()
			// ... que no sean suplidos
			.filter(d -> !d.isPrepayment())
			.flatMap(d -> d.taxStream())
			.map( InvoiceBreakdown::from )
			.map( ib -> ensureData(invoice,ib, false))
			.forEach( this::add );
		
		calculateTaxBreakdown(invoice);
	}
	
	private InvoiceBreakdown ensureData(Invoice invoice, InvoiceBreakdown ib, boolean forceExemption) {
		if (forceExemption || invoice.isExempt()) {
			ib.setVatDeductionType( VatDeductionType.WITHOUT_RIGHT )
				.setPrepayment(false)
				.setVatExemptionCause( invoice.getTransaction().visit(new InvoiceTransactionTypeVisitor<VATExemptionCause>() {
					@Override public VATExemptionCause visitNational() {return VATExemptionCause.E1;}
					@Override public VATExemptionCause visitIntracommunity() {return VATExemptionCause.E5;}
					@Override public VATExemptionCause visitExtracommunity() {return VATExemptionCause.E2;}
					@Override public VATExemptionCause visitCanCeuMel() {return VATExemptionCause.E2;}
					@Override public VATExemptionCause visitOtherISP() {return VATExemptionCause.E1;}
				})
			);
		}
		return ib;
	}
	
	public Invoice calculateTaxBreakdown(Invoice inv) {
		stream().forEach(ib -> calculateBreakdown(inv, ib));
		return inv;
	}
	
	private static void calculateBreakdown(Invoice inv, InvoiceBreakdown ib) {
		if(ib.getPercentage() != 0.0 && ib.getQuota() == 0.0) {
			ib.setQuota(AonMathUtils.round(ib.getBase() * ib.getPercentage() / 100 ));
			if (inv.isSurcharge() && ib.getSurchargeQuota() != 0.0 && ib.getSurchargeQuota() == 0.0) {
				ib.setSurchargeQuota( AonMathUtils.round(ib.getBase() * ib.getSurcharge() / 100 ));	
			} else {
				ib.setSurcharge( 0.0);
				ib.setSurchargeQuota( 0.0);
			}
		}
	}
}
