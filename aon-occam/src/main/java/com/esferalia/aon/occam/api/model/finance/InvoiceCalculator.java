package com.esferalia.aon.occam.api.model.finance;

import java.util.stream.Collector;

import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceCalculator {
	
	private InvoiceCalculator() {
	}

	public static Invoice generateTaxBreakdown(Invoice invoice) {
		if (invoice == null) throw new AonCoreException("Invoice is NULL");
		invoice.setTaxBreakdown( 
			AonCollectionUtils.stream( invoice.getDetails() )
				.flatMap( det -> det.getInvoiceTaxes().stream() )
				.collect( Collector.of(
					TaxBreakdown::new
					,TaxBreakdown::add
					,(left, right) -> { throw new AonCoreException("Should not be used in parallel"); }
					,ibm -> ibm
					,Collector.Characteristics.IDENTITY_FINISH
				))
		);
		return invoice;
	}
	public static double getTaxableBase(Invoice invoice) {
		double tb = AonCollectionUtils.stream( invoice.getDetails() )
			.filter( det -> !det.isPrepayment() ) 
			.mapToDouble( det -> det.getTaxableBase() )
			.sum(); 
		return AonMathUtils.round( tb , 4);
	}

	public static double getTotal(Invoice invoice) {
		return AonMathUtils.round( getTaxableBase(invoice) + invoice.getTaxBreakdown().getResult() , 2);
	}
}
