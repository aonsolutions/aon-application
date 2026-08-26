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
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaEmitidaSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionIngresoSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_2_ingresos_confacturasinsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaSinSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_2_facturasemitidas_sinsg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasSinSGAnulacionPeticion;
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
			() -> LroeValidation.validateAnulacionConSG((LROEPF140IngresosConFacturaConSGAnulacionPeticion) null));
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
	// ********************** [LROE_PJ_240_1_1] ************************
	// *****************************************************************

	/**
	 * Las validaciones del subcapitulo LROE_PJ_240_1_1 son las mismas que las del
	 * LROE_PF_140_1_1, por lo que solo se comprueba lo que diferencia a los dos
	 * subcapitulos: el modelo de la cabecera y el bloque que agrupa las anulaciones.
	 */
	@Test
	void peticion240Obligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionConSG((LROEPJ240FacturasEmitidasConSGAnulacionPeticion) null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticion240Valida_Test() {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionConSG(peticion240(anulacion())));
	}

	/** El modelo de las personas juridicas es el 240 y no el 140. */
	@Test
	void modelo240Erroneo_Test() {
		LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe = peticion240(anulacion());
		lroe.getCabecera().setModelo("140");
		assertErrores240(lroe, InvoiceCommunicationError.LROE_1000020);
	}

	@Test
	void facturasEmitidasObligatorias_Test() {
		LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe = peticion240(anulacion());
		lroe.setFacturasEmitidas(null);
		assertErrores240(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void emisor240DistintoDelObligadoTributario_Test() {
		AnulaTicketBai anulacion = anulacion();
		anulacion.getIDFactura().getEmisor().setNIF(OTRO_NIF);
		assertErrores240(peticion240(anulacion), InvoiceCommunicationError.LROE_2000002);
	}

	// *****************************************************************
	// **************** [LROE_PF_140_1_2 SIN SOFTWARE] *****************
	// *****************************************************************

	/**
	 * En los subcapitulos sin software garante la factura que se anula no viene en
	 * un fichero TicketBAI, sino que se identifica con su serie, su numero y su
	 * fecha de expedicion. Las validaciones de la cabecera son las mismas que en los
	 * subcapitulos con software garante.
	 */
	@Test
	void peticionSinSGObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionSinSG((LROEPF140IngresosConFacturaSinSGAnulacionPeticion) null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticionSinSGValida_Test() {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionSinSG(peticionSinSG(idFactura())));
	}

	/** Las facturas emitidas sin software garante son el subcapitulo 1.2. */
	@Test
	void subcapituloSinSGErroneo_Test() {
		LROEPF140IngresosConFacturaSinSGAnulacionPeticion lroe = peticionSinSG(idFactura());
		lroe.getCabecera().setSubcapitulo("1.1");
		assertErroresSinSG(lroe, InvoiceCommunicationError.LROE_1000024);
	}

	@Test
	void ingresosSinSGObligatorios_Test() {
		LROEPF140IngresosConFacturaSinSGAnulacionPeticion lroe = peticionSinSG(idFactura());
		lroe.setIngresos(null);
		assertErroresSinSG(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void ingresosSinSGVacios_Test() {
		assertErroresSinSG(peticionSinSG(), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void idIngresoObligatorio_Test() {
		assertErroresSinSG(peticionSinSG((IDFacturaType) null), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void numFacturaSinSGObligatorio_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setNumFactura(null);
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_2000000);
	}

	/** La serie y el numero de la factura son de tipo TextMax20Type. */
	@Test
	void numFacturaSinSGDemasiadoLargo_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setNumFactura(texto(21));
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void serieFacturaSinSGDemasiadoLarga_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setSerieFactura(texto(21));
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_1000001);
	}

	/** La serie es el unico campo opcional del bloque. */
	@Test
	void serieFacturaSinSGOpcional_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setSerieFactura(null);
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionSinSG(peticionSinSG(idFactura)));
	}

	@Test
	void fechaExpedicionSinSGObligatoria_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setFechaExpedicionFactura(null);
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void fechaExpedicionSinSGPosteriorAHoy_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setFechaExpedicionFactura(fecha(1));
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_2000005);
	}

	@Test
	void fechaExpedicionSinSGConFormatoErroneo_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setFechaExpedicionFactura("2026-01-31");
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_2000005);
	}

	@Test
	void ejercicioSinSGDistintoDelCuerpo_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setFechaExpedicionFactura(
			AonDateUtils.format(AonDateUtils.addYears(AonDateUtils.today(), -1), "dd-MM-yyyy"));
		assertErroresSinSG(peticionSinSG(idFactura), InvoiceCommunicationError.LROE_1000021);
	}

	@Test
	void facturasSinSGDuplicadas_Test() {
		assertErroresSinSG(peticionSinSG(idFactura(), idFactura()), InvoiceCommunicationError.LROE_1000005);
	}

	@Test
	void facturasSinSGDistintas_Test() {
		IDFacturaType otra = idFactura();
		otra.setNumFactura("20");
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionSinSG(peticionSinSG(idFactura(), otra)));
	}

	// *****************************************************************
	// **************** [LROE_PJ_240_1_2 SIN SOFTWARE] *****************
	// *****************************************************************

	@Test
	void peticion240SinSGObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionSinSG((LROEPJ240FacturasEmitidasSinSGAnulacionPeticion) null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticion240SinSGValida_Test() {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionSinSG(peticion240SinSG(idFactura())));
	}

	@Test
	void modelo240SinSGErroneo_Test() {
		LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe = peticion240SinSG(idFactura());
		lroe.getCabecera().setModelo("140");
		assertErrores240SinSG(lroe, InvoiceCommunicationError.LROE_1000020);
	}

	@Test
	void facturasEmitidasSinSGObligatorias_Test() {
		LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe = peticion240SinSG(idFactura());
		lroe.setFacturasEmitidas(null);
		assertErrores240SinSG(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void idFacturaObligatorio_Test() {
		assertErrores240SinSG(peticion240SinSG((IDFacturaType) null), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void fechaExpedicion240SinSGPosteriorAHoy_Test() {
		IDFacturaType idFactura = idFactura();
		idFactura.setFechaExpedicionFactura(fecha(1));
		assertErrores240SinSG(peticion240SinSG(idFactura), InvoiceCommunicationError.LROE_2000005);
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

	private void assertErrores240(LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionConSG(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	/** Peticion de anulacion del modelo 240 con una factura emitida por cada fichero TicketBAI. */
	private static LROEPJ240FacturasEmitidasConSGAnulacionPeticion peticion240(AnulaTicketBai... anulaciones) {
		LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe =
			new LROEPJ240FacturasEmitidasConSGAnulacionPeticion();
		lroe.setCabecera(cabecera240());

		AnulacionesFacturasEmitidasConSGType facturas = new AnulacionesFacturasEmitidasConSGType();
		for (AnulaTicketBai anulacion : anulaciones) {
			AnulacionFacturaConSGType factura = new AnulacionFacturaConSGType();
			factura.setAnulacionTicketBai(marshal(anulacion));
			facturas.getFacturaEmitida().add(factura);
		}
		lroe.setFacturasEmitidas(facturas);
		return lroe;
	}

	private void assertErroresSinSG(LROEPF140IngresosConFacturaSinSGAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionSinSG(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	private void assertErrores240SinSG(LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionSinSG(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	/** Peticion de anulacion del subcapitulo 1.2 del modelo 140. */
	private static LROEPF140IngresosConFacturaSinSGAnulacionPeticion peticionSinSG(IDFacturaType... idFacturas) {
		LROEPF140IngresosConFacturaSinSGAnulacionPeticion lroe =
			new LROEPF140IngresosConFacturaSinSGAnulacionPeticion();
		lroe.setCabecera(cabeceraSinSG());

		AnulacionesIngresosSinSGType ingresos = new AnulacionesIngresosSinSGType();
		for (IDFacturaType idFactura : idFacturas) {
			AnulacionIngresoSinSGType ingreso = new AnulacionIngresoSinSGType();
			ingreso.setIDIngreso(idFactura);
			ingresos.getIngreso().add(ingreso);
		}
		lroe.setIngresos(ingresos);
		return lroe;
	}

	/** Peticion de anulacion del subcapitulo 1.2 del modelo 240. */
	private static LROEPJ240FacturasEmitidasSinSGAnulacionPeticion peticion240SinSG(IDFacturaType... idFacturas) {
		LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe =
			new LROEPJ240FacturasEmitidasSinSGAnulacionPeticion();
		Cabecera240Type cabecera = cabecera240();
		cabecera.setSubcapitulo("1.2");
		lroe.setCabecera(cabecera);

		AnulacionesFacturasEmitidasSinSGType facturas = new AnulacionesFacturasEmitidasSinSGType();
		for (IDFacturaType idFactura : idFacturas) {
			AnulacionFacturaEmitidaSinSGType facturaEmitida = new AnulacionFacturaEmitidaSinSGType();
			facturaEmitida.setIDFactura(idFactura);
			facturas.getFacturaEmitida().add(facturaEmitida);
		}
		lroe.setFacturasEmitidas(facturas);
		return lroe;
	}

	/** Identificador valido de la factura que se anula. */
	private static IDFacturaType idFactura() {
		IDFacturaType idFactura = new IDFacturaType();
		idFactura.setSerieFactura("A");
		idFactura.setNumFactura("10");
		idFactura.setFechaExpedicionFactura(fecha(0));
		return idFactura;
	}

	private static Cabecera140Type cabeceraSinSG() {
		Cabecera140Type cabecera = cabecera();
		cabecera.setSubcapitulo("1.2");
		return cabecera;
	}

	private static Cabecera240Type cabecera240() {
		NIFPersonaType obligadoTributario = new NIFPersonaType();
		obligadoTributario.setNIF(NIF_OBLIGADO);
		obligadoTributario.setApellidosNombreRazonSocial("PRUEBA AAA BBB");

		Cabecera240Type cabecera = new Cabecera240Type();
		cabecera.setModelo("240");
		cabecera.setCapitulo("1");
		cabecera.setSubcapitulo("1.1");
		cabecera.setOperacion(OperacionEnum.AN_0);
		cabecera.setVersion("1.0");
		cabecera.setEjercicio(ejercicio());
		cabecera.setObligadoTributario(obligadoTributario);
		return cabecera;
	}
}
