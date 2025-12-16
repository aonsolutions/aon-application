package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedList;
import java.util.List;

import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
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

class InvoiceCommunicationSaveWrongTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }

	private List<Invoice> getInvoices() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		return AonCollectionUtils.toList( invoice);
	}
	
	@Test
	void noContextTest() {
		InvoiceCommunicatorContext cc = null;
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		assertThat(InvoiceCommunicationError.AON_0001).isIn(e.getMessages());
	}

	@Test
	void domainNullTest() {
		Domain domain = null;
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(configWithCertificate()).setCompany(company());
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		assertThat(InvoiceCommunicationError.AON_0003).isIn(e.getMessages());
	}

	@Test
	void domainEmptyTest() {
		Domain domain = new Domain();
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(configWithCertificate()).setCompany(company());
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		assertThat(InvoiceCommunicationError.AON_0003).isIn(e.getMessages());
	}

	@Test
	void userNullTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = null;
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(configWithCertificate()).setCompany(company());
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		assertThat(InvoiceCommunicationError.AON_0004).isIn(e.getMessages());
	}

	@Test
	void userEmptyTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = new User();
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(configWithCertificate()).setCompany(company());
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		assertThat(InvoiceCommunicationError.AON_0004).isIn(e.getMessages());
	}
	
	@Test
	void noConfigTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(null).setCompany(company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0006).isIn(ice.getMessages());
	}
	
	@Test
	void nullCompanyTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(config()).setCompany(null);
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0002).isIn(ice.getMessages());
	}

	@Test
	void emptyCompanyTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		cc.setConfig(config()).setCompany(new Company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0002).isIn(ice.getMessages());
	}

	@Test
	void noDocumentCompanyTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		Company company = company();
		company.setDocument(null);
		cc.setConfig(config()).setCompany(company);
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0002).isIn(ice.getMessages());
	}

	@Test
	void noNameCompanyTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getDomainName()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, getInvoices());
		Company company = company();
		company.setName(null);
		cc.setConfig(config()).setCompany(company);
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0002).isIn(ice.getMessages());
	}

	@Test
	void noInvoicesTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, null);
		cc.setConfig(config()).setCompany(company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0005).isIn(ice.getMessages());
	}


	@Test
	void emptyInvoicesTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		List<Invoice> invoices = new LinkedList<>();
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, invoices);
		cc.setConfig(config()).setCompany(company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_0005).isIn(ice.getMessages());
	}

	@Test
	void moreThanOneInvoicesTest() {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		List<Invoice> invoices = new LinkedList<>();
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment().getCtx(), getEnvironment().getDomainId());
		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment().getCtx(), getEnvironment().getDomainId());
		invoices.add(invoice1);
		invoices.add(invoice2);
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, invoices);
		cc.setConfig(configWithCertificate(	)).setCompany(company());
		DataAccessException e = assertThrows(DataAccessException.class, () -> InvoiceCommunicator.acceptInvoice(cc));
		InvoiceCommunicationException ice = e.getCause(InvoiceCommunicationException.class);
		assertNotNull(ice);
		assertThat(InvoiceCommunicationError.AON_9007).isIn(ice.getMessages());
	}

	
	
	
}
