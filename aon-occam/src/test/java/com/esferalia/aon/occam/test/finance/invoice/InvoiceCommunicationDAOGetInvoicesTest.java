package com.esferalia.aon.occam.test.finance.invoice;

import static org.junit.Assert.assertNotNull;

import java.util.stream.Stream;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

public class InvoiceCommunicationDAOGetInvoicesTest extends AbstractOccamTest {

	@Test(expected = AonCoreException.class)
	public void getInvoices_nullParams_throwsException() {
		InvoiceCommunicationDAO.getInvoices(ctx, null);
	}

	@Test(expected = AonCoreException.class)
	public void getInvoices_nullDomain_throwsException() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setCommunicationType(InvoiceCommunicationType.SII);
		InvoiceCommunicationDAO.getInvoices(ctx, params);
	}

	@Test(expected = AonCoreException.class)
	public void getInvoices_nullCommunicationType_throwsException() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID);
		InvoiceCommunicationDAO.getInvoices(ctx, params);
	}

	@Test
	public void getInvoices_validParams_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.SII);

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}

	@Test
	public void getInvoices_withPendingStatusFilter_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.SII)
			.setCommunicationStatus(InvoiceCommunicationStatus.PENDING);

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}

	@Test
	public void getInvoices_withSalesTypeFilter_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.SII)
			.setType(InvoiceType.SALES);

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}

	@Test
	public void getInvoices_withDateRange_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.SII)
			.setFrom(AonDateUtils.getYearFirstDay(2024))
			.setTo(AonDateUtils.getYearLastDay(2024));

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}

	@Test
	public void getInvoices_withQueryFilter_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.SII)
			.setQuery("TEST");

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}

	@Test
	public void getInvoices_verifactuType_returnsNonNullStream() {
		InvoiceCommunicationParams params = new InvoiceCommunicationParams()
			.setDomain(DOMAIN_ID)
			.setCommunicationType(InvoiceCommunicationType.VERIFACTU);

		Stream<Invoice> result = InvoiceCommunicationDAO.getInvoices(ctx, params);

		assertNotNull(result);
	}
}
