package solutions.aon.in.invoice.templates;

import java.util.List;

import solutions.aon.in.invoice.InvoiceBuilder;

public class InvoiceTaxParser {
	
	private static final double IVA_PERCENTS [] = {21.0, 10.0, 4.0};

	public static void getTaxes(List<Double> amounts, InvoiceBuilder<?> handler) {
		simpleVATInvoice(amounts,handler);	
	}
	
	private static int indexOf(Double amounts [], double amount, int start ) {
		double delta =(amount > 0.1 ? 0.019 : 0.0019);
		return indexOf(amounts, amount, start, delta);
	}

	private static int indexOf(Double amounts [], double amount, int start , double delta ) {
		int indexOf = -1;
		double diffOf = 0.5;
		for (int i = start; i < amounts.length; i++) {
			double diff = Math.abs(amounts[i] - amount);
			if ( diff < delta && diff < diffOf ) {
				indexOf = i;
				diffOf = diff;
			}
		}
		return indexOf;
	}
	
	
	/**
	 * Intento de parseo de una factura con una sola base de IVA, siendo el  
	 * total factura el número más alto de la factura.  
	 */
	private static boolean simpleVATInvoice(List<Double> collection, InvoiceBuilder<?> handler) {
		Double amounts [] = collection.stream().sorted((a1,a2)-> Double.compare(Math.abs(a2), Math.abs(a1))).toArray(Double[]::new);
		boolean retValue = false;
		for (int i = 0; i < amounts.length; i++) {
			double total = amounts[i];
			for (double percentage : IVA_PERCENTS ) {
				double base = total / (1 + percentage / 100.0);
				int indexOfBase = indexOf(amounts, base, i + 1);
				if ( indexOfBase >= 0 ) {
					double quota = total - base;
					int indexOfQuota = indexOf(amounts, quota, i+1);
					if ( indexOfQuota >= 0 ) {
						retValue = true;
						handler.setTotal(total);
						handler.setTax(
								new InvoiceTax()
								.setType( TaxType.IVA )
								.setBase(amounts[indexOfBase])
								.setPercent(percentage )
								.setQuota(amounts[indexOfQuota])
							);
					}
				}
			}
		}
		return retValue;
	}
	
}
