package net.aonsolutions.aon.tbai;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;

import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.anulacion.Cabecera;
import ticketbai.anulacion.CabeceraFacturaType;
import ticketbai.anulacion.Emisor;
import ticketbai.anulacion.EntidadDesarrolladoraType;
import ticketbai.anulacion.HuellaTBAI;
import ticketbai.anulacion.IDFactura;
import ticketbai.anulacion.IDOtro;
import ticketbai.anulacion.SoftwareFacturacionType;

/**
 * Tests de {@link TbaiValidation}.
 *
 * Cada test parte de un fichero de anulacion valido (ver {@link #anulacion()}),
 * modifica unicamente el campo que se quiere comprobar y verifica los errores
 * que se acumulan en la excepcion, en el mismo orden en el que se ejecutan las
 * validaciones.
 */
class TbaiValidationTest {

	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	/** NIF valido del emisor de la factura que se anula. */
	private static final String NIF_EMISOR = "99980200M";

	/** NIF valido de la entidad desarrolladora (ver Invoice2tbai). */
	private static final String NIF_ENTIDAD = "B01487271";

	/** NIF con el formato del tipo NIFType pero con la letra de control erronea. */
	private static final String NIF_LETRA_ERRONEA = "12345678A";

	/** Cadena de nueve caracteres que no cumple el formato del tipo NIFType. */
	private static final String NIF_FORMATO_ERRONEO = "AAAAAAAAA";

	// *****************************************************************
	// ************************ [1. CABECERA] **************************
	// *****************************************************************

	@Test
	void anulacionObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> TbaiValidation.validateAnulacion(null));
		assertEquals(List.of(InvoiceCommunicationError.TBAI_002), e.getMessages());
	}

	@Test
	void anulacionValida_Test() {
		assertValida(a -> { });
	}

	@Test
	void cabeceraObligatoria_Test() {
		assertErrores(a -> a.setCabecera(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void idVersionTbaiObligatorio_Test() {
		assertErrores(a -> a.getCabecera().setIDVersionTBAI(null), InvoiceCommunicationError.TBAI_004);
		assertErrores(a -> a.getCabecera().setIDVersionTBAI("  "), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void idVersionTbaiIncorrecto_Test() {
		assertErrores(a -> a.getCabecera().setIDVersionTBAI("1.1"), InvoiceCommunicationError.TBAI_002);
	}

	// *****************************************************************
	// *********************** [3. ID FACTURA] *************************
	// *****************************************************************

	@Test
	void idFacturaObligatorio_Test() {
		assertErrores(a -> a.setIDFactura(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorObligatorio_Test() {
		assertErrores(a -> a.getIDFactura().setEmisor(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorNifObligatorio_Test() {
		assertErrores(a -> a.getIDFactura().getEmisor().setNIF(null), InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNifFormatoErroneo_Test() {
		assertErrores(a -> a.getIDFactura().getEmisor().setNIF(NIF_FORMATO_ERRONEO),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void emisorNifNoValido_Test() {
		assertErrores(a -> a.getIDFactura().getEmisor().setNIF(NIF_LETRA_ERRONEA),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNombreRazonSocialObligatorio_Test() {
		assertErrores(a -> a.getIDFactura().getEmisor().setApellidosNombreRazonSocial(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void emisorNombreRazonSocialDemasiadoLargo_Test() {
		assertValida(a -> a.getIDFactura().getEmisor().setApellidosNombreRazonSocial(texto(120)));
		assertErrores(a -> a.getIDFactura().getEmisor().setApellidosNombreRazonSocial(texto(121)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void cabeceraFacturaObligatoria_Test() {
		assertErrores(a -> a.getIDFactura().setCabeceraFactura(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void serieFacturaOpcional_Test() {
		assertValida(a -> a.getIDFactura().getCabeceraFactura().setSerieFactura(null));
	}

	@Test
	void serieFacturaDemasiadoLarga_Test() {
		assertValida(a -> a.getIDFactura().getCabeceraFactura().setSerieFactura(texto(20)));
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setSerieFactura(texto(21)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void numFacturaObligatorio_Test() {
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setNumFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void numFacturaDemasiadoLargo_Test() {
		assertValida(a -> a.getIDFactura().getCabeceraFactura().setNumFactura(texto(20)));
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setNumFactura(texto(21)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void fechaExpedicionObligatoria_Test() {
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void fechaExpedicionFormatoErroneo_Test() {
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura("2025-01-01"),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void fechaExpedicionInexistente_Test() {
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura("31-02-2025"),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void fechaExpedicionPosteriorAHoy_Test() {
		assertValida(a -> a.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura(fecha(0)));
		assertErrores(a -> a.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura(fecha(1)),
			InvoiceCommunicationError.TBAI_004);
	}

	// *****************************************************************
	// ********************** [11. HUELLA TBAI] ************************
	// *****************************************************************

	@Test
	void huellaObligatoria_Test() {
		assertErrores(a -> a.setHuellaTBAI(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void numSerieDispositivoOpcional_Test() {
		assertValida(a -> a.getHuellaTBAI().setNumSerieDispositivo(null));
	}

	@Test
	void numSerieDispositivoDemasiadoLargo_Test() {
		assertValida(a -> a.getHuellaTBAI().setNumSerieDispositivo(texto(30)));
		assertErrores(a -> a.getHuellaTBAI().setNumSerieDispositivo(texto(31)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void softwareObligatorio_Test() {
		assertErrores(a -> a.getHuellaTBAI().setSoftware(null), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void licenciaObligatoria_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setLicenciaTBAI(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void licenciaDemasiadoLarga_Test() {
		assertValida(a -> a.getHuellaTBAI().getSoftware().setLicenciaTBAI(texto(20)));
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setLicenciaTBAI(texto(21)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void entidadDesarrolladoraObligatoria_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setEntidadDesarrolladora(null),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void entidadDesarrolladoraSinIdentificacion_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().setNIF(null),
			InvoiceCommunicationError.TBAI_016);
	}

	@Test
	void entidadDesarrolladoraConNifYIdOtro_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().setIDOtro(idOtro("02", "1")),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void entidadDesarrolladoraNifFormatoErroneo_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().setNIF(NIF_FORMATO_ERRONEO),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void entidadDesarrolladoraNifNoValido_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().getEntidadDesarrolladora().setNIF(NIF_LETRA_ERRONEA),
			InvoiceCommunicationError.TBAI_011);
	}

	@Test
	void entidadDesarrolladoraIdOtro_Test() {
		assertValida(a -> setIdOtro(a, idOtro("02", "1")));
	}

	@Test
	void idOtroTipoObligatorio_Test() {
		assertErrores(a -> setIdOtro(a, idOtro(null, "1")), InvoiceCommunicationError.TBAI_016);
	}

	@Test
	void idOtroTipoNoAdmitido_Test() {
		assertErrores(a -> setIdOtro(a, idOtro("01", "1")), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void idOtroIdObligatorio_Test() {
		assertErrores(a -> setIdOtro(a, idOtro("02", null)), InvoiceCommunicationError.TBAI_016);
	}

	@Test
	void idOtroIdDemasiadoLargo_Test() {
		assertValida(a -> setIdOtro(a, idOtro("02", texto(20))));
		assertErrores(a -> setIdOtro(a, idOtro("02", texto(21))), InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void softwareNombreObligatorio_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setNombre(null),
			InvoiceCommunicationError.TBAI_011);
	}

	@Test
	void softwareNombreDemasiadoLargo_Test() {
		assertValida(a -> a.getHuellaTBAI().getSoftware().setNombre(texto(120)));
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setNombre(texto(121)),
			InvoiceCommunicationError.TBAI_002);
	}

	@Test
	void softwareVersionObligatoria_Test() {
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setVersion(null),
			InvoiceCommunicationError.TBAI_004);
	}

	@Test
	void softwareVersionDemasiadoLarga_Test() {
		assertValida(a -> a.getHuellaTBAI().getSoftware().setVersion(texto(20)));
		assertErrores(a -> a.getHuellaTBAI().getSoftware().setVersion(texto(21)),
			InvoiceCommunicationError.TBAI_002);
	}

	// *****************************************************************
	// ***************** [ACUMULACION DE LOS ERRORES] ******************
	// *****************************************************************

	/** Se acumulan los errores de todos los campos que incumplen alguna validacion. */
	@Test
	void variosErrores_Test() {
		assertErrores(a -> {
				a.getIDFactura().getEmisor().setNIF(NIF_FORMATO_ERRONEO);
				a.getHuellaTBAI().getSoftware().setNombre(null);
				a.getHuellaTBAI().getSoftware().setVersion(null);
			}
			, InvoiceCommunicationError.TBAI_002
			, InvoiceCommunicationError.TBAI_011
			, InvoiceCommunicationError.TBAI_004
		);
	}

	/** El mismo error no se repite cuando varios campos incumplen la misma validacion. */
	@Test
	void erroresNoDuplicados_Test() {
		assertErrores(a -> {
				a.getIDFactura().getCabeceraFactura().setNumFactura(null);
				a.getHuellaTBAI().getSoftware().setLicenciaTBAI(null);
				a.getHuellaTBAI().getSoftware().setVersion(null);
			}
			, InvoiceCommunicationError.TBAI_004
		);
	}

	// *****************************************************************
	// *************************** [UTILES] ****************************
	// *****************************************************************

	private void assertValida(Consumer<AnulaTicketBai> completa) {
		AnulaTicketBai anulacion = anulacion();
		completa.accept(anulacion);
		assertDoesNotThrow(() -> TbaiValidation.validateAnulacion(anulacion));
	}

	private void assertErrores(Consumer<AnulaTicketBai> completa, InvoiceCommunicationError... errores) {
		AnulaTicketBai anulacion = anulacion();
		completa.accept(anulacion);
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> TbaiValidation.validateAnulacion(anulacion));
		assertEquals(List.of(errores), e.getMessages());
	}

	/**
	 * Fichero de anulacion valido, sin el bloque Signature, que se incorpora al
	 * firmarlo despues de serializarlo.
	 */
	static AnulaTicketBai anulacion() {
		AnulaTicketBai anulacion = new AnulaTicketBai();

		Cabecera cabecera = new Cabecera();
		cabecera.setIDVersionTBAI("1.2");
		anulacion.setCabecera(cabecera);

		Emisor emisor = new Emisor();
		emisor.setNIF(NIF_EMISOR);
		emisor.setApellidosNombreRazonSocial("PRUEBA AAA BBB");

		CabeceraFacturaType cabeceraFactura = new CabeceraFacturaType();
		cabeceraFactura.setSerieFactura("TEST1");
		cabeceraFactura.setNumFactura("19");
		cabeceraFactura.setFechaExpedicionFactura(fecha(0));

		IDFactura idFactura = new IDFactura();
		idFactura.setEmisor(emisor);
		idFactura.setCabeceraFactura(cabeceraFactura);
		anulacion.setIDFactura(idFactura);

		EntidadDesarrolladoraType entidad = new EntidadDesarrolladoraType();
		entidad.setNIF(NIF_ENTIDAD);

		SoftwareFacturacionType software = new SoftwareFacturacionType();
		software.setLicenciaTBAI("TBAIBI00000000PRUEBA");
		software.setEntidadDesarrolladora(entidad);
		software.setNombre("aonSolutions");
		software.setVersion("9.23");

		HuellaTBAI huella = new HuellaTBAI();
		huella.setNumSerieDispositivo("1");
		huella.setSoftware(software);
		anulacion.setHuellaTBAI(huella);

		return anulacion;
	}

	/** Identifica a la entidad desarrolladora con el bloque IDOtro en lugar del NIF. */
	private static void setIdOtro(AnulaTicketBai anulacion, IDOtro idOtro) {
		EntidadDesarrolladoraType entidad = anulacion.getHuellaTBAI().getSoftware().getEntidadDesarrolladora();
		entidad.setNIF(null);
		entidad.setIDOtro(idOtro);
	}

	private static IDOtro idOtro(String tipo, String id) {
		IDOtro idOtro = new IDOtro();
		idOtro.setIDType(tipo);
		idOtro.setID(id);
		return idOtro;
	}

	/** Fecha con formato dd-mm-yyyy de dentro de {@code dias} dias. */
	static String fecha(int dias) {
		return AonDateUtils.format(AonDateUtils.addDays(AonDateUtils.today(), dias), FORMATO_FECHA);
	}

	static String texto(int longitud) {
		return "A".repeat(longitud);
	}
}
