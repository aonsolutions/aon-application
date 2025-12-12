package net.aonsolutions.aon.verifactu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationQuery;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.watson.server.io.AonIOUtils;

class QueryTest extends AbstractVerifactuTest {
	
	@Test
	void queryNullFilterTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContextWithCertificate();
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0028).isIn(e.getMessages());
	}
	
	@Test
	void queryNullYearTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContextWithCertificate();
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icc.setCommunicationQuery(icq);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0029).isIn(e.getMessages());
	}
	
	@Test
	void queryNullMonthTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContextWithCertificate();
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icq.setYear(2025);
		icc.setCommunicationQuery(icq);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0030).isIn(e.getMessages());
	}
	
	@Test
	void queryTest() throws InvoiceCommunicationException, IOException {
		InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContextWithCertificate();
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icq.setYear(2025);
		icq.setMonth(Month.DECEMBER);
		icq.setId(10);
		icc.setCommunicationQuery(icq);
		VerifactuContext vc = VERIFACTU.query(icc);
		System.out.println( "--------------- VERIFACTU Query Response ---------------" );
		AonIOUtils.write(vc.getResponse().getBytes(), System.out);
		System.out.println();
		System.out.println("---------------------------------------------------------");
		
	}
	
}
