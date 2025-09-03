package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.verifactu.AbstractVerifactuTest;
import net.aonsolutions.aon.verifactu.InvoiceTypes;

class InvoiceCommunicationCertTest extends AbstractVerifactuTest {

	private List<Invoice> getInvoices() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(ctx, DOMAIN_ID);
		return AonCollectionUtils.toList(invoice);
	}
	
	@Test
	void noCertTest() throws InvoiceCommunicationException {
		Domain domain = DomainDAO.getDomain(ctx, DOMAIN_ID);
		User user = UserDAO.get(ctx, DOMAIN_ID, USER)
			.orElseThrow(() -> new IllegalStateException("User not found: " + USER));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(config()).setCompany(company());
		InvoiceCommunicatorContext a = InvoiceCommunicator.acceptInvoice(cc);
		assertNotNull(a);
	}

	@Test
	void invalidCertIdTest() {
		Domain domain = DomainDAO.getDomain(ctx, DOMAIN_ID);
		User user = UserDAO.get(ctx, DOMAIN_ID, USER)
			.orElseThrow(() -> new IllegalStateException("User not found: " + USER));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, Integer.MIN_VALUE, getInvoices());
		cc.setConfig(config()).setCompany(company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0023).isIn(ice.getMessages());
	}

}
