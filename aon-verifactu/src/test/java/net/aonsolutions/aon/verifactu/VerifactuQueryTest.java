package net.aonsolutions.aon.verifactu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationQuery;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestaconsultalr.RespuestaConsultaFactuSistemaFacturacionType;

class VerifactuQueryTest extends AbstractVerifactuTest {
	
	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }
	
	@Test
	void queryNullFilterTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate( null );
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc, getEnvironment().getEnablerData(icc.getConfig())));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0028).isIn(e.getMessages());
	}
	
	@Test
	void queryNullYearTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(null);
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icc.setCommunicationQuery(icq);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc, getEnvironment().getEnablerData(icc.getConfig())));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0029).isIn(e.getMessages());
	}
	
	@Test
	void queryNullMonthTest() throws InvoiceCommunicationException {
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(null);
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icq.setYear(2025);
		icc.setCommunicationQuery(icq);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class, () -> VERIFACTU.query(icc, getEnvironment().getEnablerData(icc.getConfig())));
		assertNotNull(e);
		assertNotNull(e.getMessages());
		assertThat(InvoiceCommunicationError.AON_0030).isIn(e.getMessages());
	}
	
	@Test
	void queryTest() throws InvoiceCommunicationException, IOException {
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(null);
		InvoiceCommunicationQuery icq = new InvoiceCommunicationQuery();
		icq.setYear(2025);
		icq.setMonth(Month.DECEMBER);
		Date today = new Date();
		icq.setDate(today);
		icc.setCommunicationQuery(icq);
		VerifactuContext vc = VERIFACTU.query(icc, getEnvironment().getEnablerData(icc.getConfig()));
		assertNotNull(vc);
		assertNotNull(vc.getResponse());
		assertNull(vc.getResponse().getResponse());
		assertNotNull(vc.getResponse().getQueryResponse());
		RespuestaConsultaFactuSistemaFacturacionType r = vc.getResponse().getQueryResponse();
		AonCollectionUtils.stream( r.getRegistroRespuestaConsultaFactuSistemaFacturacion())
		 	.map( Verifactu2Invoice::to )
		 	.forEach( i -> {
		 		// TODO asserts
		 	});
	}
	
}
