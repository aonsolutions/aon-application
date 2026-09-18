package net.aonsolutions.aon.tbai.lroe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistroFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altapeticion_v1_0_2.LROEPF140IngresosConFacturaConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altarespuesta_v1_0_2.LROEPF140IngresosConFacturaConSGAltaRespuesta;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

/**
 * Alta de ingresos con facturas emitidas con software garante de una persona
 * fisica en el entorno de pruebas del LROE (modelo 140, capitulo 1, subcapitulo
 * 1.1).
 *
 * El obligado tributario es el titular del certificado de persona fisica del kit
 * de certificados de pruebas de Batuz, y el software garante es el de pruebas
 * (licencia TBAIBI00000000PRUEBA).
 *
 * El test etiquetado como "envio" comunica realmente con
 * https://pruesarrerak.bizkaia.eus/N3B4000M/aurkezpena, por lo que necesita
 * acceso a la red. Para excluirlo:
 * mvn test -DexcludedGroups=envio
 */
class Lroe140_1_1Test extends AbstractLroeConSGTest {

	private static final String XSD_ALTA = "LROE_PF_140_1_1_Ingresos_ConfacturaConSG_AltaPeticion_V1_0_2.xsd";

	/** El obligado tributario del modelo 140 es el titular del certificado de persona fisica. */
	private Person person() {
		return (Person) new Person()
			.setFirstName("NUEVOCIUD")
			.setFirstSurname("FICTICIO")
			.setSecondSurname("ACTIVO")
			.setName("FICTICIO ACTIVO NUEVOCIUD")
			.setDocument(NIF_PERSONA_FISICA);
	}

	/**
	 * El fichero TicketBAI se genera a partir de la empresa, que para una persona
	 * fisica lleva su mismo NIF y nombre.
	 */
	private Company company() {
		return company(NIF_PERSONA_FISICA, "FICTICIO ACTIVO NUEVOCIUD");
	}

	private LROEInfo info() {
		return LROE140_1_1.buildInfo(OperacionEnum.A_00, java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
	}

	// *****************************************************************
	// ******************* [PETICION - SIN ENVIO] **********************
	// *****************************************************************

	@Test
	void cabeceraPeticion_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_PERSONA_FISICA);
		Invoice invoice = invoice();
		LROEInfo info = info();

		LROEPF140IngresosConFacturaConSGAltaPeticion peticion =
			LROE140_1_1.buildAlta(person(), invoice, info, ticketBai(company(), invoice, icc));

		assertEquals("140", peticion.getCabecera().getModelo());
		assertEquals("1", peticion.getCabecera().getCapitulo());
		assertEquals("1.1", peticion.getCabecera().getSubcapitulo());
		assertEquals(OperacionEnum.A_00, peticion.getCabecera().getOperacion());
		assertEquals(NIF_PERSONA_FISICA, peticion.getCabecera().getObligadoTributario().getNIF());
		assertEquals(1, peticion.getIngresos().getIngreso().size());
		assertEquals(EPIGRAFE, peticion.getIngresos().getIngreso().get(0).getRenta().getDetalleRenta().get(0).getEpigrafe());
	}

	@Test
	void peticionCumpleEsquema_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_PERSONA_FISICA);
		Invoice invoice = invoice();

		byte[] xml = LROE140_1_1.buildAltaXml(person(), invoice, info(), ticketBai(company(), invoice, icc));

		assertCumpleEsquema(xml, XSD_ALTA);
	}

	@Test
	void ticketBaiConSoftwareGarantePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_PERSONA_FISICA);
		Invoice invoice = invoice();

		byte[] ticketBai = ticketBai(company(), invoice, icc);

		assertTicketBaiConSoftwareGarantePruebas(ticketBai);
		// El nodo TicketBai del LROE lleva el fichero firmado tal cual, en Base64.
		LROEPF140IngresosConFacturaConSGAltaPeticion peticion =
			LROE140_1_1.buildAlta(person(), invoice, info(), ticketBai);
		org.junit.jupiter.api.Assertions.assertArrayEquals(ticketBai,
			peticion.getIngresos().getIngreso().get(0).getTicketBai());
	}

	@Test
	void cabeceraJsonDelEnvio_Test() throws Exception {
		assertCabeceraJson(LROE140.buildJSON(person(), info()), "140", NIF_PERSONA_FISICA);
	}

	// *****************************************************************
	// ***************** [ENVIO AL ENTORNO DE PRUEBAS] *****************
	// *****************************************************************

	@Test
	@Tag("envio")
	void altaEnEntornoDePruebas_Test() throws Exception {
		InvoiceCommunicationConfiguration icc = config(CERT_PERSONA_FISICA);
		Invoice invoice = invoice();
		LROEInfo info = info();
		byte[] xml = LROE140_1_1.buildAltaXml(person(), invoice, info, ticketBai(company(), invoice, icc));
		assertCumpleEsquema(xml, XSD_ALTA);

		LROEResponse response = new LROE140_1_1().sendAlta(icc, person(), info, xml);

		assertEnvioCorrecto(response);

		LROEPF140IngresosConFacturaConSGAltaRespuesta respuesta =
			(LROEPF140IngresosConFacturaConSGAltaRespuesta) XMLUtils.unmarshal(
				response.getData(), LROEPF140IngresosConFacturaConSGAltaRespuesta.class);

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
