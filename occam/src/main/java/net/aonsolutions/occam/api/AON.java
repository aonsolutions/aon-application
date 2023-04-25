package net.aonsolutions.occam.api;

import java.util.Optional;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.InvoiceFacade.InvoiceFilter;
import net.aonsolutions.occam.api.invoice.Invoice;
import net.aonsolutions.occam.dao.DomainDAO;
import net.aonsolutions.occam.dao.InvoiceDAO;

public class AON {

	private AON() {
	}
	// ******************************** [DOMAIN]
	public static Optional<Domain> getDomain(Occam occam, DomainFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return DomainDAO.get(ctx, filter);
		}
	}
	
	// ******************************** [INVOICE]
	public static Stream<Invoice> getInvoices(Occam occam, InvoiceFilter filter) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return InvoiceDAO.getStream(ctx, filter);
		}
	}
}
