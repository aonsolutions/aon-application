package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

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
	public void setInvoiceWithholding(InvoiceWithholding iw) {
		this.iw = iw;		
	}

	public void refresh(Invoice invoice) {
		iw = null;
		vats = new LinkedList<>();
		invoice.detailStream()
			.flatMap(d -> d.taxStream()) 
			.filter( it -> it.isNotDeleted() )
			.forEach( this::add );
	}
	
	TaxBreakdown add(InvoiceTax it) {
		return add(InvoiceBreakdown.from(it));
	}
	TaxBreakdown add(InvoiceBreakdown ib) {
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
					.setAccount( ib.getWithholdingAccount().orElse(null) )
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
		return AonMathUtils.round( getVats()
				.mapToDouble( t -> AonMathUtils.round(t.getQuota() + t.getSurchargeQuota()))
				.sum() , 2); 
	}
	public double getRetentionBase() {
		return getInvoiceWithholding()
			.map( inw -> AonMathUtils.round(inw.getBase(), 4))
			.orElse(0.0)
		;
	}
	public double getRetentionQuota() {
		return getInvoiceWithholding()
			.map( inw -> AonMathUtils.round(inw.getQuota(), 2))
			.orElse(0.0)
		;
	}
	public double getResult() {
		return AonMathUtils.round(getVatQuota() - getRetentionQuota());
	}
}
