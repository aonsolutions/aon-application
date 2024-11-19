package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class TaxBreakdown implements Serializable {
	
	private static final long serialVersionUID = -5156527112729566231L;
	
	private InvoiceWithholding iw;
	private LinkedList<InvoiceBreakdown> vats = new LinkedList<>();
	
	public Stream<InvoiceBreakdown> getVats() {
		return vats.stream()
			.sorted((ib,ib1) -> AonNumberUtils.compare(ib.getPercentage(),ib1.getPercentage()));
	}
	
	public Optional<InvoiceWithholding> getInvoiceWithholding() {
		return Optional.ofNullable(iw);
	}
	
	public void setInvoiceWithholding(Invoice invoice,InvoiceWithholding iw) {
		if (invoice.isWithholding()) {
			if (iw == null) {
				throw new AonCoreException("No hay datos para el cálculo de la retención" );
			}
			this.iw = iw;		
		} else {
			this.iw = null;
		}
	}

	public void refresh(Invoice invoice) {
		invoice.getDetails()
			.flatMap(d -> d.getInvoiceTaxes()) 
			.filter( it -> it.isNotDeleted() )
			.forEach( this::add );
	}
	
	private TaxBreakdown add(InvoiceTax it) {
		return add(InvoiceBreakdown.from(it));
	}
	private TaxBreakdown add(InvoiceBreakdown ib) {
		if (ib.isVat()) {
			Optional<InvoiceBreakdown> oib = get( ib );
			if (oib.isPresent()) {
				oib.get().add(ib);
			} else {
				vats.add( ib );
			}
		}
		if (ib.isWithholding()) {
			if (iw == null) {
				iw = new InvoiceWithholding()
					.setWithholdingType( ib.getWithholdingType() )
					.setPercentage( ib.getPercentage())
					.setAccount( ib.getWithholdingAccount() )
				;
			}
			iw.setBase( AonMathUtils.round( iw.getBase() + ib.getBase()) );
			iw.setQuota( AonMathUtils.round( iw.getQuota() + ib.getQuota()) );
			iw.setDeductibleQuota( AonMathUtils.round( iw.getDeductibleQuota() + ib.getDeductibleQuota()) );
		}
		return this;
	}
	
	private Optional<InvoiceBreakdown> get(InvoiceBreakdown b) {
		return vats.stream().filter(a -> a.isSameGroup(b)).findFirst();
	}

	public double getVatBase() {
		return AonMathUtils.round( getVats().mapToDouble( t -> t.getBase() ).sum() , 4); 
	}
	public double getVatQuota() {
		return AonMathUtils.round( getVats().mapToDouble( t -> t.getQuota() ).sum() , 2); 
	}
	public double getRetentionBase() {
		return getInvoiceWithholding()
			.map( iw -> AonMathUtils.round(iw.getBase(), 4))
			.orElse(0.0)
		;
	}
	public double getRetentionQuota() {
		return getInvoiceWithholding()
			.map( iw -> AonMathUtils.round(iw.getQuota(), 2))
			.orElse(0.0)
		;
	}
	public double getResult() {
		return AonMathUtils.round(getVatQuota() - getRetentionQuota());
	}
}
