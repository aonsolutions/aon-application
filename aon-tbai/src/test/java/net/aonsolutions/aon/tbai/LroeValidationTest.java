package net.aonsolutions.aon.tbai;

import static net.aonsolutions.aon.tbai.TbaiValidationTest.anulacion;
import static net.aonsolutions.aon.tbai.TbaiValidationTest.fecha;
import static net.aonsolutions.aon.tbai.TbaiValidationTest.texto;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;

/**
 * Tests de {@link LroeValidation}.
 *
 * Cada test parte de una peticion de anulacion valida (ver {@link #peticion})
 * con un unico ingreso, modifica unicamente el campo que se quiere comprobar y
 * verifica los errores que se acumulan en la excepcion, en el mismo orden en el
 * que se ejecutan las validaciones.
 *
 * El fichero de anulacion TicketBAI de cada ingreso se construye con el mismo
 * fichero valido que utiliza {@link TbaiValidationTest} y se serializa igual que
 * en el envio real (ver LROE140_1_1.buildBaja).
 */
class LroeValidationTest {

	/** NIF del obligado tributario, que es el emisor de las facturas que se anulan. */
	private static final String NIF_OBLIGADO = "99980200M";

	/** Otro NIF valido, distinto del obligado tributario. */
	private static final String OTRO_NIF = "11111111H";

	/** NIF con el formato del tipo NIFType pero con la letra de control erronea. */
	private static final String NIF_LETRA_ERRONEA = "12345678A";

	/** Cadena de nueve caracteres que no cumple el formato del tipo NIFType. */
	private static final String NIF_FORMATO_ERRONEO = "AAAAAAAAA";

	// *****************************************************************
	// ************************ [1. CABECERA] **************************
	// *****************************************************************

	@Test
	void peticionObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionConSG(null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticionValida_Test() {
		assertValida(peticion(anulacion()));
	}

	@Test
	void cabeceraObligatoria_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.setCabecera(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void modeloObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setModelo(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void modeloIncorrecto_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setModelo("240");
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000020);
	}

	@Test
	void capituloObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setCapitulo(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void capituloIncorrecto_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setCapitulo("2");
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000023);
	}

	@Test
	void subcapituloObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setSubcapitulo(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void subcapituloIncorrecto_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setSubcapitulo("1.2");
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000024);
	}

	@Test
	void operacionObligatoria_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setOperacion(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void operacionIncorrecta_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setOperacion(OperacionEnum.A_00);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000025);
	}

	@Test
	void versionObligatoria_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setVersion(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void versionIncorrecta_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setVersion("2.0");
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void ejercicioObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setEjercicio(0);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	/** El ejercicio de la cabecera no puede ser posterior al ejercicio en curso. */
	@Test
	void ejercicioPosterior_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setEjercicio(ejercicio() + 1);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000021);
	}

	@Test
	void obligadoTributarioObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().setObligadoTributario(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void obligadoTributarioNifObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setNIF(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_2000000);
	}

	/**
	 * Un NIF erroneo tampoco coincide con el del emisor de la factura que se anula.
	 */
	@Test
	void obligadoTributarioNifFormatoErroneo_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setNIF(NIF_FORMATO_ERRONEO);
		assertErrores(lroe
			, InvoiceCommunicationError.LROE_2000011
			, InvoiceCommunicationError.LROE_2000002
		);
	}

	@Test
	void obligadoTributarioNifNoValido_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setNIF(NIF_LETRA_ERRONEA);
		assertErrores(lroe
			, InvoiceCommunicationError.LROE_2000011
			, InvoiceCommunicationError.LROE_2000002
		);
	}

	@Test
	void obligadoTributarioNombreRazonSocialObligatorio_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setApellidosNombreRazonSocial(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void obligadoTributarioNombreRazonSocialDemasiadoLargo_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setApellidosNombreRazonSocial(texto(120));
		assertValida(lroe);

		lroe = peticion(anulacion());
		lroe.getCabecera().getObligadoTributario().setApellidosNombreRazonSocial(texto(121));
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	// *****************************************************************
	// ************************ [11. INGRESOS] *************************
	// *****************************************************************

	@Test
	void ingresosObligatorios_Test() {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion());
		lroe.setIngresos(null);
		assertErrores(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void ingresosVacios_Test() {
		assertErrores(peticion(), InvoiceCommunicationError.LROE_1000001);
	}

	/** No se admiten mas de 1.000 anulaciones en la misma peticion. */
	@Test
	void ingresosMaximo_Test() {
		byte[][] ingresos = new byte[1001][];
		assertErrores(peticionBytes(ingresos)
			, InvoiceCommunicationError.LROE_1000001
			, InvoiceCommunicationError.LROE_2000000
		);
	}

	@Test
	void anulacionTicketBaiObligatoria_Test() {
		assertErrores(peticionBytes(new byte[][] { null }), InvoiceCommunicationError.LROE_2000000);
		assertErrores(peticionBytes(new byte[][] { new byte[0] }), InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void anulacionTicketBaiNoEsXml_Test() {
		assertErrores(peticionBytes(bytes("no es un fichero XML")), InvoiceCommunicationError.LROE_2000001);
	}

	@Test
	void anulacionTicketBaiOtroEsquema_Test() {
		assertErrores(peticionBytes(bytes("<Otro><Cabecera/></Otro>")), InvoiceCommunicationError.LROE_2000001);
	}

	/** El fichero TicketBAI tiene que cumplir tambien sus propias validaciones. */
	@Test
	void anulacionTicketBaiNoValida_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getCabeceraFactura().setNumFactura(null);
		assertErrores(peticion(anulacion)
			, InvoiceCommunicationError.LROE_2000001
			, InvoiceCommunicationError.TBAI_004
		);
	}

	@Test
	void emisorDistintoDelObligadoTributario_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getEmisor().setNIF(OTRO_NIF);
		assertErrores(peticion(anulacion), InvoiceCommunicationError.LROE_2000002);
	}

	/**
	 * La fecha de expedicion tambien la valida el fichero TicketBAI, por lo que
	 * una fecha posterior a hoy incumple las dos validaciones.
	 */
	@Test
	void fechaExpedicionPosteriorAHoy_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getCabeceraFactura().setFechaExpedicionFactura(fecha(1));
		assertErrores(peticion(anulacion)
			, InvoiceCommunicationError.LROE_2000001
			, InvoiceCommunicationError.TBAI_004
			, InvoiceCommunicationError.LROE_2000005
		);
	}

	/** El ejercicio de la cabecera es el de la fecha de expedicion de las facturas. */
	@Test
	void ejercicioDistintoDelCuerpo_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getCabeceraFactura()
			.setFechaExpedicionFactura(AonDateUtils.format(AonDateUtils.addYears(AonDateUtils.today(), -1), "dd-MM-yyyy"));
		assertErrores(peticion(anulacion), InvoiceCommunicationError.LROE_1000021);
	}

	@Test
	void facturasDuplicadas_Test() {
		assertErrores(peticion(anulacion(), anulacion()), InvoiceCommunicationError.LROE_1000005);
	}

	@Test
	void facturasDistintas_Test() {
		AnulaTicketBai otra = anulacion();
		otra.getIDFactura().getCabeceraFactura().setNumFactura("20");
		assertValida(peticion(anulacion(), otra));
	}

	// *****************************************************************
	// ***************** [ACUMULACION DE LOS ERRORES] ******************
	// *****************************************************************

	/** Se acumulan los errores de la cabecera y los de todos los ingresos. */
	@Test
	void variosErrores_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getEmisor().setNIF(OTRO_NIF);

		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = peticion(anulacion);
		lroe.getCabecera().setModelo("240");
		lroe.getCabecera().setSubcapitulo("1.2");

		assertErrores(lroe
			, InvoiceCommunicationError.LROE_1000020
			, InvoiceCommunicationError.LROE_1000024
			, InvoiceCommunicationError.LROE_2000002
		);
	}

	/** El mismo error no se repite cuando varios ingresos incumplen la misma validacion. */
	@Test
	void erroresNoDuplicados_Test() {
		AnulaTicketBai primera = anulacion();
		primera.getIDFactura().getEmisor().setNIF(OTRO_NIF);

		AnulaTicketBai segunda = anulacion();
		segunda.getIDFactura().getEmisor().setNIF(OTRO_NIF);
		segunda.getIDFactura().getCabeceraFactura().setNumFactura("20");

		assertErrores(peticion(primera, segunda), InvoiceCommunicationError.LROE_2000002);
	}

	// *****************************************************************
	// *************************** [UTILES] ****************************
	// *****************************************************************

	private void assertValida(LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe) {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionConSG(lroe));
	}

	private void assertErrores(LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionConSG(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	/** Peticion de anulacion con un ingreso por cada fichero TicketBAI. */
	private static LROEPF140IngresosConFacturaConSGAnulacionPeticion peticion(AnulaTicketBai... anulaciones) {
		byte[][] ingresos = new byte[anulaciones.length][];
		for (int i = 0; i < anulaciones.length; i++) {
			ingresos[i] = marshal(anulaciones[i]);
		}
		return peticionBytes(ingresos);
	}

	/** Peticion de anulacion con el contenido de cada ingreso ya serializado. */
	private static LROEPF140IngresosConFacturaConSGAnulacionPeticion peticionBytes(byte[][] anulaciones) {
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe =
			new LROEPF140IngresosConFacturaConSGAnulacionPeticion();
		lroe.setCabecera(cabecera());

		AnulacionesIngresosConSGType ingresos = new AnulacionesIngresosConSGType();
		for (byte[] anulacion : anulaciones) {
			AnulacionFacturaConSGType ingreso = new AnulacionFacturaConSGType();
			ingreso.setAnulacionTicketBai(anulacion);
			ingresos.getIngreso().add(ingreso);
		}
		lroe.setIngresos(ingresos);
		return lroe;
	}

	private static Cabecera140Type cabecera() {
		NIFPersonaType obligadoTributario = new NIFPersonaType();
		obligadoTributario.setNIF(NIF_OBLIGADO);
		obligadoTributario.setApellidosNombreRazonSocial("PRUEBA AAA BBB");

		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo("140");
		cabecera.setCapitulo("1");
		cabecera.setSubcapitulo("1.1");
		cabecera.setOperacion(OperacionEnum.AN_0);
		cabecera.setVersion("1.0");
		cabecera.setEjercicio(ejercicio());
		cabecera.setObligadoTributario(obligadoTributario);
		return cabecera;
	}

	private static byte[] marshal(AnulaTicketBai anulacion) {
		return assertDoesNotThrow(() -> XMLUtils.marshal(anulacion, AnulaTicketBai.class));
	}

	private static byte[][] bytes(String xml) {
		return new byte[][] { xml.getBytes(StandardCharsets.UTF_8) };
	}

	private static int ejercicio() {
		return AonDateUtils.getYear(AonDateUtils.today());
	}
}
