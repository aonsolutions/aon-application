package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		
		// Se añaden los suplidos (invoiceDeetail.prepaymet=true)
		// como NO SUJETOS
		invoice.detailStream()
			.filter(d -> d.isPrepayment())
			.map( d ->  new InvoiceBreakdown()
				.setTaxType( TaxType.VAT )
				.setBase(d.getTaxableBase())
				.setVatDeductionType( VatDeductionType.NON_TAXABLE ))
			.forEach( this::add );
			
		
		// Se añaden los invoice_tax de la factura 
		invoice.detailStream()
			.flatMap(d -> d.taxStream()) 
			.forEach( this::add );
		
		
	}
	
	public Invoice calculateTaxBreakdown(Invoice inv) {
		stream().forEach(ib -> calculateBreakdown(inv, ib));
		return inv;
	}
	
	private static void calculateBreakdown(Invoice inv, InvoiceBreakdown ib) {
		ib.setQuota(AonMathUtils.round(ib.getBase() * ib.getPercentage() / 100 ));
		if (inv.isSurcharge()) {
			ib.setSurchargeQuota( AonMathUtils.round(ib.getBase() * ib.getSurcharge() / 100 ));	
		} else {
			ib.setSurcharge( 0.0);
			ib.setSurchargeQuota( 0.0);
		}
	}
}
