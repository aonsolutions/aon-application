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

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaEmitidaSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionGastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionIngresoSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesGastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaConEmisorType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_2_ingresos_confacturasinsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaSinSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_anulacionpeticion_v1_0_0.LROEPF140GastosConFacturaAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_2_facturasemitidas_sinsg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasSinSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_anulacionpeticion_v1_0_0.LROEPJ240FacturasRecibidasAnulacionPeticion;
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

	/** NIF del emisor de las facturas recibidas que se anulan, que es el proveedor. */
	private static final String NIF_PROVEEDOR = "04437365K";

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
	// ************ [LROE_PF_140_2_1 GASTOS CON FACTURA] ***************
	// *****************************************************************

	/**
	 * En el subcapitulo de gastos con factura la factura recibida que se anula se
	 * identifica con su serie, su numero, su fecha de expedicion y su emisor, que es
	 * el proveedor y no el obligado tributario.
	 */
	@Test
	void peticionGastosObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionGastos((LROEPF140GastosConFacturaAnulacionPeticion) null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticionGastosValida_Test() {
		assertValidaGastos(peticionGastos(idGasto()));
	}

	/** Los gastos y las facturas recibidas son el capitulo 2. */
	@Test
	void capituloGastosErroneo_Test() {
		LROEPF140GastosConFacturaAnulacionPeticion lroe = peticionGastos(idGasto());
		lroe.getCabecera().setCapitulo("1");
		assertErroresGastos(lroe, InvoiceCommunicationError.LROE_1000023);
	}

	/** Los gastos con factura son el subcapitulo 2.1. */
	@Test
	void subcapituloGastosErroneo_Test() {
		LROEPF140GastosConFacturaAnulacionPeticion lroe = peticionGastos(idGasto());
		lroe.getCabecera().setSubcapitulo("2.2");
		assertErroresGastos(lroe, InvoiceCommunicationError.LROE_1000024);
	}

	@Test
	void gastosObligatorios_Test() {
		LROEPF140GastosConFacturaAnulacionPeticion lroe = peticionGastos(idGasto());
		lroe.setGastos(null);
		assertErroresGastos(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void gastosVacios_Test() {
		assertErroresGastos(peticionGastos(), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void idGastoObligatorio_Test() {
		assertErroresGastos(peticionGastos((IDFacturaConEmisorType) null),
			InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void numFacturaGastoObligatorio_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setNumFactura(null);
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000000);
	}

	/** La serie y el numero de la factura son de tipo TextMax20Type. */
	@Test
	void numFacturaGastoDemasiadoLargo_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setNumFactura(texto(21));
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void serieFacturaGastoDemasiadoLarga_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setSerieFactura(texto(21));
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_1000001);
	}

	/** La serie es el unico campo opcional del bloque. */
	@Test
	void serieFacturaGastoOpcional_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setSerieFactura(null);
		assertValidaGastos(peticionGastos(idGasto));
	}

	@Test
	void fechaExpedicionGastoObligatoria_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setFechaExpedicionFactura(null);
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void fechaExpedicionGastoPosteriorAHoy_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setFechaExpedicionFactura(fecha(1));
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000005);
	}

	@Test
	void fechaExpedicionGastoConFormatoErroneo_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setFechaExpedicionFactura("2026-01-31");
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000005);
	}

	/**
	 * El ejercicio de un gasto es el de su fecha de recepcion (ver
	 * LROE.getEjercicio), que no se informa en la anulacion, por lo que no tiene que
	 * coincidir con el de la fecha de expedicion de la factura.
	 */
	@Test
	void ejercicioGastoDistintoDeLaFechaExpedicion_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setFechaExpedicionFactura(
			AonDateUtils.format(AonDateUtils.addYears(AonDateUtils.today(), -1), "dd-MM-yyyy"));
		assertValidaGastos(peticionGastos(idGasto));
	}

	@Test
	void emisorGastoObligatorio_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setEmisorFacturaRecibida(null);
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000000);
	}

	/** El emisor se identifica con su NIF o con el bloque IDOtro. */
	@Test
	void emisorGastoSinDocumento_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setEmisorFacturaRecibida(new DocumentoType());
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000000);
	}

	/** El NIF y el bloque IDOtro son una eleccion, por lo que se excluyen. */
	@Test
	void emisorGastoConNifYConIdOtro_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.getEmisorFacturaRecibida().setIDOtro(idOtro(CountryEnum.FR, "02", "FR12345678"));
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void emisorGastoConNifDeFormatoErroneo_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.getEmisorFacturaRecibida().setNIF(NIF_FORMATO_ERRONEO);
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000011);
	}

	@Test
	void emisorGastoConNifDeLetraErronea_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.getEmisorFacturaRecibida().setNIF(NIF_LETRA_ERRONEA);
		assertErroresGastos(peticionGastos(idGasto), InvoiceCommunicationError.LROE_2000011);
	}

	/** El NIF del proveedor no tiene que coincidir con el del obligado tributario. */
	@Test
	void emisorGastoDistintoDelObligadoTributario_Test() {
		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.getEmisorFacturaRecibida().setNIF(OTRO_NIF);
		assertValidaGastos(peticionGastos(idGasto));
	}

	@Test
	void emisorGastoConIdOtro_Test() {
		assertValidaGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.US, "03", "123456789"))));
	}

	@Test
	void idTypeGastoObligatorio_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.US, null, "123456789"))),
			InvoiceCommunicationError.LROE_2000000);
	}

	/** El esquema solo admite los tipos de documento 02, 03, 04, 05 y 06. */
	@Test
	void idTypeGastoNoAdmitido_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.US, "07", "123456789"))),
			InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void idGastoIdOtroObligatorio_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.US, "03", null))),
			InvoiceCommunicationError.LROE_2000000);
	}

	/** El ID del bloque IDOtro es de tipo TextMax20Type. */
	@Test
	void idGastoIdOtroDemasiadoLargo_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.US, "03", texto(21)))),
			InvoiceCommunicationError.LROE_1000001);
	}

	/** El codigo de pais es obligatorio con los tipos de documento 03, 04, 05 y 06. */
	@Test
	void codigoPaisGastoObligatorio_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(null, "03", "123456789"))),
			InvoiceCommunicationError.LROE_2000012);
	}

	/** El codigo de pais no es obligatorio cuando el emisor se identifica con un NIF-IVA. */
	@Test
	void codigoPaisGastoOpcionalConNifIva_Test() {
		assertValidaGastos(peticionGastos(idGastoConIdOtro(idOtro(null, "02", "FR12345678"))));
	}

	@Test
	void nifIvaGastoConPrefijoDeOtroPais_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.FR, "02", "PT123456789"))),
			InvoiceCommunicationError.LROE_2000013);
	}

	@Test
	void nifIvaGastoSinPrefijo_Test() {
		assertErroresGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.FR, "02", "12345678"))),
			InvoiceCommunicationError.LROE_2000013);
	}

	/** Grecia asigna los NIF-IVA con el prefijo EL y no con su codigo de pais. */
	@Test
	void nifIvaGastoDeGrecia_Test() {
		assertValidaGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.GR, "02", "EL123456789"))));
	}

	/** Irlanda del Norte asigna los NIF-IVA con el prefijo XI y el codigo de pais GB. */
	@Test
	void nifIvaGastoDeIrlandaDelNorte_Test() {
		assertValidaGastos(peticionGastos(idGastoConIdOtro(idOtro(CountryEnum.GB, "02", "XI123456789"))));
	}

	@Test
	void gastosDuplicados_Test() {
		assertErroresGastos(peticionGastos(idGasto(), idGasto()), InvoiceCommunicationError.LROE_1000005);
	}

	@Test
	void gastosDistintos_Test() {
		IDFacturaConEmisorType otro = idGasto();
		otro.setNumFactura("20");
		assertValidaGastos(peticionGastos(idGasto(), otro));
	}

	// *****************************************************************
	// ************ [LROE_PJ_240_2 FACTURAS RECIBIDAS] *****************
	// *****************************************************************

	/**
	 * Las facturas recibidas del modelo 240 se anulan con el mismo bloque
	 * IDFacturaConEmisorType que los gastos con factura del modelo 140, por lo que
	 * las validaciones son las mismas. La unica diferencia es que el capitulo 2 del
	 * modelo 240 no se divide en subcapitulos.
	 */
	@Test
	void peticion240RecibidasObligatoria_Test() {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionGastos((LROEPJ240FacturasRecibidasAnulacionPeticion) null));
		assertEquals(List.of(InvoiceCommunicationError.LROE_1000001), e.getMessages());
	}

	@Test
	void peticion240RecibidasValida_Test() {
		assertValida240Recibidas(peticion240Recibidas(idGasto()));
	}

	@Test
	void modelo240RecibidasErroneo_Test() {
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = peticion240Recibidas(idGasto());
		lroe.getCabecera().setModelo("140");
		assertErrores240Recibidas(lroe, InvoiceCommunicationError.LROE_1000020);
	}

	@Test
	void capitulo240RecibidasErroneo_Test() {
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = peticion240Recibidas(idGasto());
		lroe.getCabecera().setCapitulo("1");
		assertErrores240Recibidas(lroe, InvoiceCommunicationError.LROE_1000023);
	}

	/** El capitulo 2 del modelo 240 no se divide en subcapitulos. */
	@Test
	void subcapitulo240RecibidasInformado_Test() {
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = peticion240Recibidas(idGasto());
		lroe.getCabecera().setSubcapitulo("2.1");
		assertErrores240Recibidas(lroe, InvoiceCommunicationError.LROE_1000024);
	}

	@Test
	void facturasRecibidasObligatorias_Test() {
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = peticion240Recibidas(idGasto());
		lroe.setFacturasRecibidas(null);
		assertErrores240Recibidas(lroe, InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void facturasRecibidasVacias_Test() {
		assertErrores240Recibidas(peticion240Recibidas(), InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void idRecibidaObligatorio_Test() {
		assertErrores240Recibidas(peticion240Recibidas((IDFacturaConEmisorType) null),
			InvoiceCommunicationError.LROE_1000001);
	}

	@Test
	void numFacturaRecibidaObligatorio_Test() {
		IDFacturaConEmisorType idRecibida = idGasto();
		idRecibida.setNumFactura(null);
		assertErrores240Recibidas(peticion240Recibidas(idRecibida),
			InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void fechaExpedicionRecibidaPosteriorAHoy_Test() {
		IDFacturaConEmisorType idRecibida = idGasto();
		idRecibida.setFechaExpedicionFactura(fecha(1));
		assertErrores240Recibidas(peticion240Recibidas(idRecibida),
			InvoiceCommunicationError.LROE_2000005);
	}

	@Test
	void emisorRecibidaObligatorio_Test() {
		IDFacturaConEmisorType idRecibida = idGasto();
		idRecibida.setEmisorFacturaRecibida(null);
		assertErrores240Recibidas(peticion240Recibidas(idRecibida),
			InvoiceCommunicationError.LROE_2000000);
	}

	@Test
	void emisorRecibidaConNifDeLetraErronea_Test() {
		IDFacturaConEmisorType idRecibida = idGasto();
		idRecibida.getEmisorFacturaRecibida().setNIF(NIF_LETRA_ERRONEA);
		assertErrores240Recibidas(peticion240Recibidas(idRecibida),
			InvoiceCommunicationError.LROE_2000011);
	}

	@Test
	void codigoPaisRecibidaObligatorio_Test() {
		assertErrores240Recibidas(peticion240Recibidas(idGastoConIdOtro(idOtro(null, "03", "123456789"))),
			InvoiceCommunicationError.LROE_2000012);
	}

	@Test
	void nifIvaRecibidaConPrefijoDeOtroPais_Test() {
		assertErrores240Recibidas(
			peticion240Recibidas(idGastoConIdOtro(idOtro(CountryEnum.FR, "02", "PT123456789"))),
			InvoiceCommunicationError.LROE_2000013);
	}

	@Test
	void facturasRecibidasDuplicadas_Test() {
		assertErrores240Recibidas(peticion240Recibidas(idGasto(), idGasto()),
			InvoiceCommunicationError.LROE_1000005);
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

	private void assertValidaGastos(LROEPF140GastosConFacturaAnulacionPeticion lroe) {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionGastos(lroe));
	}

	private void assertErroresGastos(LROEPF140GastosConFacturaAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionGastos(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	/** Peticion de anulacion del subcapitulo 2.1 del modelo 140. */
	private static LROEPF140GastosConFacturaAnulacionPeticion peticionGastos(
			IDFacturaConEmisorType... idGastos) {
		LROEPF140GastosConFacturaAnulacionPeticion lroe = new LROEPF140GastosConFacturaAnulacionPeticion();
		lroe.setCabecera(cabeceraGastos());

		AnulacionesGastosConFacturaType gastos = new AnulacionesGastosConFacturaType();
		for (IDFacturaConEmisorType idGasto : idGastos) {
			AnulacionGastoConFacturaType gasto = new AnulacionGastoConFacturaType();
			gasto.setIDGasto(idGasto);
			gastos.getGasto().add(gasto);
		}
		lroe.setGastos(gastos);
		return lroe;
	}

	private static Cabecera140Type cabeceraGastos() {
		Cabecera140Type cabecera = cabecera();
		cabecera.setCapitulo("2");
		cabecera.setSubcapitulo("2.1");
		return cabecera;
	}

	/** Identificador valido de la factura recibida que se anula. */
	private static IDFacturaConEmisorType idGasto() {
		DocumentoType emisor = new DocumentoType();
		emisor.setNIF(NIF_PROVEEDOR);

		IDFacturaConEmisorType idGasto = new IDFacturaConEmisorType();
		idGasto.setSerieFactura("A");
		idGasto.setNumFactura("10");
		idGasto.setFechaExpedicionFactura(fecha(0));
		idGasto.setEmisorFacturaRecibida(emisor);
		return idGasto;
	}

	/** Identificador de la factura recibida de un emisor sin NIF. */
	private static IDFacturaConEmisorType idGastoConIdOtro(IDOtroType idOtro) {
		DocumentoType emisor = new DocumentoType();
		emisor.setIDOtro(idOtro);

		IDFacturaConEmisorType idGasto = idGasto();
		idGasto.setEmisorFacturaRecibida(emisor);
		return idGasto;
	}

	private static IDOtroType idOtro(CountryEnum codigoPais, String idType, String id) {
		IDOtroType idOtro = new IDOtroType();
		idOtro.setCodigoPais(codigoPais);
		idOtro.setIDType(idType);
		idOtro.setID(id);
		return idOtro;
	}

	private void assertValida240Recibidas(LROEPJ240FacturasRecibidasAnulacionPeticion lroe) {
		assertDoesNotThrow(() -> LroeValidation.validateAnulacionGastos(lroe));
	}

	private void assertErrores240Recibidas(LROEPJ240FacturasRecibidasAnulacionPeticion lroe,
			InvoiceCommunicationError... errores) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> LroeValidation.validateAnulacionGastos(lroe));
		assertEquals(List.of(errores), e.getMessages());
	}

	/** Peticion de anulacion del capitulo 2 del modelo 240. */
	private static LROEPJ240FacturasRecibidasAnulacionPeticion peticion240Recibidas(
			IDFacturaConEmisorType... idRecibidas) {
		LROEPJ240FacturasRecibidasAnulacionPeticion lroe = new LROEPJ240FacturasRecibidasAnulacionPeticion();
		lroe.setCabecera(cabecera240Recibidas());

		AnulacionesFacturasRecibidasType facturas = new AnulacionesFacturasRecibidasType();
		for (IDFacturaConEmisorType idRecibida : idRecibidas) {
			AnulacionFacturaRecibidaType facturaRecibida = new AnulacionFacturaRecibidaType();
			facturaRecibida.setIDRecibida(idRecibida);
			facturas.getFacturaRecibida().add(facturaRecibida);
		}
		lroe.setFacturasRecibidas(facturas);
		return lroe;
	}

	private static Cabecera240Type cabecera240Recibidas() {
		Cabecera240Type cabecera = cabecera240();
		cabecera.setCapitulo("2");
		cabecera.setSubcapitulo(null);
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
