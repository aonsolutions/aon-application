package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	public List<InvoiceBreakdown> clear() {
		ibs.clear();
		return ibs; 
	}

	public List<InvoiceBreakdown> getBreakdown() {
		return ibs;
	}
	public TaxBreakdown add(InvoiceTax it) {
		return add(InvoiceBreakdown.from(it));
	}
	
	public TaxBreakdown add(InvoiceBreakdown ib) {
		Optional<InvoiceBreakdown> oib = get( ib );
		if (oib.isPresent()) {
			oib.get().add(ib);
		} else {
			ibs.add( ib );
		}
		return this;
	}
	
	private Optional<InvoiceBreakdown> get(InvoiceBreakdown b) {
		return ibs.stream().filter(a -> a.isSameGroup(b)).findFirst();
	}
	
	public List<InvoiceBreakdown> getVats() {
		return ibs.stream().filter(ib -> ib.isVat() )
			.sorted((ib,ib1) -> AonNumberUtils.compare(ib.getPercentage(),ib1.getPercentage()))
			.collect(Collectors.toCollection(LinkedList::new));
				
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
