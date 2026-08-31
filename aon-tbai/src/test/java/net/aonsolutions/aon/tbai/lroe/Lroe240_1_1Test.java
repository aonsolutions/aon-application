package net.aonsolutions.aon.tbai.lroe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Calendar;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistroFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altapeticion_v1_0_2.LROEPJ240FacturasEmitidasConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altarespuesta_v1_0_1.LROEPJ240FacturasEmitidasConSGAltaRespuesta;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

/**
 * Alta de facturas emitidas con software garante de una persona juridica en el
 * entorno de pruebas del LROE (modelo 240, capitulo 1, subcapitulo 1.1).
 *
 * El obligado tributario es la entidad titular del certificado de sello de
 * empresa del kit de certificados de pruebas de Batuz, y el software garante es
 * el de pruebas (licencia TBAIBI00000000PRUEBA).
 *
 * El test etiquetado como "envio" comunica realmente con
 * https://pruesarrerak.bizkaia.eus/N3B4000M/aurkezpena, por lo que necesita
 * acceso a la red. Para excluirlo:
 * mvn test -DexcludedGroups=envio
 */
class Lroe240_1_1Test extends AbstractLroeConSGTest {

	private static final String XSD_ALTA = "LROE_PJ_240_1_1_FacturasEmitidas_ConSG_AltaPeticion_V1_0_2.xsd";

	/** El obligado tributario del modelo 240 es la entidad titular del certificado de sello. */
	private Company company() {
		return company(NIF_PERSONA_JURIDICA, NOMBRE_PERSONA_JURIDICA);
	}

	private LROEInfo info() {
		return LROE240_1_1.buildInfo(OperacionEnum.A_00, Calendar.getInstance().get(Calendar.YEAR));
	}

	// *****************************************************************
	// ******************* [PETICION - SIN ENVIO] **********************
	// *****************************************************************

	@Test
	void cabeceraPeticion_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Invoice invoice = invoice();

		LROEPJ240FacturasEmitidasConSGAltaPeticion peticion =
			LROE240_1_1.buildAlta(company(), invoice, info(), ticketBai(company(), invoice, icc));

		assertEquals("240", peticion.getCabecera().getModelo());
		assertEquals("1", peticion.getCabecera().getCapitulo());
		assertEquals("1.1", peticion.getCabecera().getSubcapitulo());
		assertEquals(OperacionEnum.A_00, peticion.getCabecera().getOperacion());
		assertEquals(NIF_PERSONA_JURIDICA, peticion.getCabecera().getObligadoTributario().getNIF());
		assertEquals(1, peticion.getFacturasEmitidas().getFacturaEmitida().size());
	}

	@Test
	void peticionCumpleEsquema_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Invoice invoice = invoice();

		byte[] xml = LROE240_1_1.buildAltaXml(company(), invoice, info(), ticketBai(company(), invoice, icc));

		assertCumpleEsquema(xml, XSD_ALTA);
	}

	@Test
	void ticketBaiConSoftwareGarantePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Invoice invoice = invoice();

		byte[] ticketBai = ticketBai(company(), invoice, icc);

		assertTicketBaiConSoftwareGarantePruebas(ticketBai);
		// El nodo TicketBai del LROE lleva el fichero firmado tal cual, en Base64.
		LROEPJ240FacturasEmitidasConSGAltaPeticion peticion =
			LROE240_1_1.buildAlta(company(), invoice, info(), ticketBai);
		org.junit.jupiter.api.Assertions.assertArrayEquals(ticketBai,
			peticion.getFacturasEmitidas().getFacturaEmitida().get(0).getTicketBai());
	}

	@Test
	void cabeceraJsonDelEnvio_Test() {
		assertCabeceraJson(LROE240.buildJSON(company(), info()), "240", NIF_PERSONA_JURIDICA);
	}

	// *****************************************************************
	// ***************** [ENVIO AL ENTORNO DE PRUEBAS] *****************
	// *****************************************************************

	/**
	 * El mismo alta, presentada con el certificado de representante de entidad en
	 * lugar del sello de empresa: el obligado tributario sigue siendo la entidad y
	 * el presentador es el representante.
	 */
	@Test
	@Tag("envio")
	void altaConCertificadoDeRepresentante_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_REPRESENTANTE_ENTIDAD);
		Company company = company();
		Invoice invoice = invoice();
		LROEInfo info = info();
		byte[] xml = LROE240_1_1.buildAltaXml(company, invoice, info, ticketBai(company, invoice, icc));

		LROEResponse response = new LROE240_1_1().sendAlta(icc, company, info, xml);

		assertEnvioCorrecto(response);
		assertRegistroCorrecto(response, invoice);
	}

	@Test
	@Tag("envio")
	void altaEnEntornoDePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_SELLO_EMPRESA);
		Company company = company();
		Invoice invoice = invoice();
		LROEInfo info = info();
		byte[] xml = LROE240_1_1.buildAltaXml(company, invoice, info, ticketBai(company, invoice, icc));
		assertCumpleEsquema(xml, XSD_ALTA);

		LROEResponse response = new LROE240_1_1().sendAlta(icc, company, info, xml);

		assertEnvioCorrecto(response);
		assertRegistroCorrecto(response, invoice);
	}

	/** La respuesta del alta trae los datos de presentacion y un registro por factura enviada. */
	private void assertRegistroCorrecto(LROEResponse response, Invoice invoice) throws JAXBException {
		LROEPJ240FacturasEmitidasConSGAltaRespuesta respuesta =
			(LROEPJ240FacturasEmitidasConSGAltaRespuesta) XMLUtils.unmarshal(
				response.getData(), LROEPJ240FacturasEmitidasConSGAltaRespuesta.class);

		assertNotNull(respuesta.getDatosPresentacion(), () -> "Sin datos de presentacion: " + describe(response));
		assertNotNull(respuesta.getRegistros(), () -> "Sin registros: " + describe(response));
		assertEquals(1, respuesta.getRegistros().getRegistro().size(), () -> describe(response));

		RegistroFacturaConSGType registro = respuesta.getRegistros().getRegistro().get(0);
		assertEquals(EstadoRegistroEnum.CORRECTO, registro.getSituacionRegistro().getEstadoRegistro(),
			() -> "Registro rechazado: " + describe(response));
		assertEquals(invoice.getSeries(), registro.getIdentificador().getIDFactura().getSerieFactura());
		assertEquals(Integer.toString(invoice.getNumber()), registro.getIdentificador().getIDFactura().getNumFactura());
		assertFalse(response.isError(), () -> describe(response));
	}
}
