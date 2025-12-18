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
import net.aonsolutions.aon.verifactu.Environment;
import net.aonsolutions.aon.verifactu.InvoiceTypes;

class InvoiceCommunicationCertTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }

	private List<Invoice> getInvoices() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		return AonCollectionUtils.toList(invoice);
	}
	
	@Test
	void noCertTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(getEnvironment().configuration()).setCompany(getEnvironment().company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0023).isIn(ice.getMessages());
	}

	@Test
	void invalidCertIdTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, Integer.MIN_VALUE, getInvoices());
		cc.setConfig(getEnvironment().configuration()).setCompany(getEnvironment().company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0021).isIn(ice.getMessages());
	}

}
