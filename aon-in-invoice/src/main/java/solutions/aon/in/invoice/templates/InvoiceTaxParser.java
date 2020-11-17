package solutions.aon.in.invoice.templates;

import java.util.List;

import solutions.aon.in.invoice.InvoiceBuilder;

public class InvoiceTaxParser {
	
	private static final double IVA_PERCENTS [] = {21.0, 10.0, 4.0};
	private static final double IRPF_PERCENTS [] = {19.0};

	public static void setTaxes(List<Double> collection, InvoiceBuilder<?> handler) {
		Double[] amounts = collection.stream().sorted((a1,a2)-> Double.compare(Math.abs(a2), Math.abs(a1))).toArray(Double[]::new);
		
		@SuppressWarnings("unused")
		boolean something = simpleVATInvoice(amounts,handler);
		if ( !something) {
			complexVATInvoice(amounts,handler);
			simpleIRPFInvoice(amounts, handler);
			if ( handler.hasTaxes() ) {
				double total = 0.0;
				for ( InvoiceTax tax : handler.getTaxes() ) {
					if (tax.getType() == TaxType.IVA) {
						total = total + tax.getBase() + tax.getQuota();
					}
					if (tax.getType() == TaxType.IRPF) {
						total = total - tax.getQuota();
					}
				}
				int indexOfTotal = indexOf(amounts, total, 0);
				if ( indexOfTotal >= 0 ) {
					handler.setTotal( amounts[indexOfTotal]);
				} else {
	
					//TODO
					// Identificarcar aquellas lineas de IVA, únicas por porcentaje, que, sumadas 
					// entre si den un numero (total) que exista en la factura
					
					
				}
			}
		}
		
//		if (taxAdded) {
//		}
		
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
	private static boolean simpleVATInvoice(Double[] amounts, InvoiceBuilder<?> handler) {
		boolean matched = false;
		iva: {
			for (int i = 0; i < amounts.length; i++) {
				double total = amounts[i];
				if (amounts[i] != 0.0) {
					for (double percentage : IVA_PERCENTS ) {
						double base = total / (1 + percentage / 100.0);
						if (base != 0.0) {
							int indexOfBase = indexOf(amounts, base, i + 1);
							if ( indexOfBase >= 0 ) {
								double quota = total - base;
								int indexOfQuota = indexOf(amounts, quota, i+1);
								if ( indexOfQuota >= 0 ) {
									matched = true;
									handler.setTotal(total);
									handler.setTax(
											new InvoiceTax()
											.setType( TaxType.IVA )
											.setBase(amounts[indexOfBase])
											.setPercent(percentage )
											.setQuota(amounts[indexOfQuota])
											);
									break iva;
								}
							}
						}
					}
				}
			}
		}
		return matched;
	}
	
	private static void complexVATInvoice(Double[] amounts, InvoiceBuilder<?> handler) {
		for (int i = 0; i < amounts.length; i++) {
			if (amounts[i] != 0.0) {
				double base = amounts[i];
				for (double percentage : IVA_PERCENTS ) {
					double quota = base * percentage / 100.0;
					int indexOfQuota = indexOf(amounts, quota, i+1);
					if ( indexOfQuota >= 0 ) {
						handler.setTax(
								new InvoiceTax()
								.setType( TaxType.IVA )
								.setBase(amounts[i])
								.setPercent(percentage )
								.setQuota(amounts[indexOfQuota])
								);
					}
				}
			}
		}
	}

	private static void simpleIRPFInvoice(Double[] amounts, InvoiceBuilder<?> handler) {
		for (int i = 0; i < amounts.length; i++) {
			if (amounts[i] != 0.0) {
				double base = amounts[i];
				for (double percentage : IRPF_PERCENTS ) {
					double quota = base * percentage / 100.0;
					int indexOfQuota = indexOf(amounts, quota, i+1);
					if ( indexOfQuota >= 0 ) {
						handler.setTax(
								new InvoiceTax()
								.setType( TaxType.IRPF )
								.setBase(amounts[i])
								.setPercent(percentage )
								.setQuota(amounts[indexOfQuota])
								);
					}
				}
			}
		}
	}
}
