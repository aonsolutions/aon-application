package com.esferalia.aon.occam.test.finance.invoice;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceCommunicationDAOValidationTest extends AbstractOccamTest {

	@Test(expected = AonCoreException.class)
	public void saveRequest_nullCommunicationType_throwsException() {
		InvoiceCommunicationDAO.saveRequest(ctx, null, null, new byte[0]);
	}

	@Test(expected = AonCoreException.class)
	public void saveResponse_nullCommunicationType_throwsException() {
		InvoiceCommunicationDAO.saveResponse(ctx, null, null, null, new byte[0]);
	}

	@Test
	public void saveRequest_exceptionMessageIsNotNull() {
		try {
			InvoiceCommunicationDAO.saveRequest(ctx, null, null, new byte[0]);
		} catch (AonCoreException e) {
			assertNotNull(e.getMessage());
		}
	}

	@Test
	public void saveResponse_exceptionMessageIsNotNull() {
		try {
			InvoiceCommunicationDAO.saveResponse(ctx, null, null, null, new byte[0]);
		} catch (AonCoreException e) {
			assertNotNull(e.getMessage());
		}
	}
}
