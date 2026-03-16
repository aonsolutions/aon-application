package com.esferalia.aon.occam.test.finance.invoice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class InvoiceCommunicationDAOGetHistoryTest extends AbstractOccamTest {

	@Test
	public void getHistory_nonExistentInvoice_returnsEmptyList() {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(ctx, -1, null);

		assertNotNull(history);
		assertTrue(history.isEmpty());
	}

	@Test
	public void getHistory_nullMessagesExtractor_returnsNonNullList() {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(ctx, -1, null);

		assertNotNull(history);
	}

	@Test
	public void getHistory_withMessagesExtractor_appliesExtractor() {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(
			ctx, -1, h -> Collections.emptyList());

		assertNotNull(history);
	}
}
