package net.aonsolutions.aon.tbai.lroe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistroFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RegistrosFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.SituacionRegistroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altarespuesta_v1_0_2.LROEPF140IngresosConFacturaConSGAltaRespuesta;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altarespuesta_v1_0_1.LROEPJ240FacturasEmitidasConSGAltaRespuesta;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

/**
 * Lectura de la situacion de los registros de la respuesta del servicio de
 * entradas.
 *
 * Los envios de alta de los subcapitulos 1.1 la leen del objeto de respuesta
 * (ver {@link LROE#readRegistros}); el resto de subcapitulos siguen usando el
 * respaldo generico sobre el documento XML (ver
 * {@link LROE#readSituacionRegistro}).
 *
 * Los cuerpos de respuesta de src/test/resources/.../respuestas son los ejemplos
 * publicados por Batuz para el subcapitulo 1.1.
 */
class LroeSituacionRegistroTest {

	/** Cabeceras de una respuesta rechazada, que es lo que devuelve el servicio de entradas. */
	private JSONObject cabeceras() {
		JSONObject json = new JSONObject();
		json.put(LROEResponse.LROE_RESPONSE_TYPE, "Incorrecto");
		json.put(LROEResponse.LROE_RESPONSE_CODE, "B4_1000005");
		json.put(LROEResponse.LROE_RESPONSE_MESSAGE, "El formato del fichero no es correcto.");
		return json;
	}

	// *****************************************************************
	// ************ [OBJETO DE RESPUESTA - SUBCAPITULOS 1.1] ***********
	// *****************************************************************

	private byte[] cuerpo(String resource) throws IOException {
		try (InputStream is = LroeSituacionRegistroTest.class.getResourceAsStream("respuestas/" + resource)) {
			assertNotNull(is, "No se encuentra la respuesta de ejemplo " + resource);
			return is.readAllBytes();
		}
	}

	private JSONObject registros140(String resource) throws Exception {
		byte[] cuerpo = cuerpo(resource);
		LROEPF140IngresosConFacturaConSGAltaRespuesta respuesta = (LROEPF140IngresosConFacturaConSGAltaRespuesta)
			XMLUtils.unmarshal(cuerpo, LROEPF140IngresosConFacturaConSGAltaRespuesta.class);
		return LROE.readRegistros(new LROEResponse(cabeceras(), cuerpo), respuesta.getRegistros()).getJson();
	}

	private JSONObject registros240(String resource) throws Exception {
		byte[] cuerpo = cuerpo(resource);
		LROEPJ240FacturasEmitidasConSGAltaRespuesta respuesta = (LROEPJ240FacturasEmitidasConSGAltaRespuesta)
			XMLUtils.unmarshal(cuerpo, LROEPJ240FacturasEmitidasConSGAltaRespuesta.class);
		return LROE.readRegistros(new LROEResponse(cabeceras(), cuerpo), respuesta.getRegistros()).getJson();
	}

	@Test
	void alta140Correcta_Test() throws Exception {
		assertFalse(registros140("pf_140_1_1_alta_correcta.xml").getBoolean("error"));
	}

	@Test
	void alta240Correcta_Test() throws Exception {
		assertFalse(registros240("pj_240_1_1_alta_correcta.xml").getBoolean("error"));
	}

	@Test
	void alta140Incorrecta_Test() throws Exception {
		JSONObject json = registros140("pf_140_1_1_alta_incorrecta.xml");

		assertTrue(json.getBoolean("error"));
		assertEquals("B4_2000003", json.getString("errorCode"));
		assertEquals("B4_2000003 - Registro duplicado.", json.getString("errorMessage"));
	}

	@Test
	void alta240Incorrecta_Test() throws Exception {
		JSONObject json = registros240("pj_240_1_1_alta_incorrecta.xml");

		assertTrue(json.getBoolean("error"));
		assertEquals("B4_2000003", json.getString("errorCode"));
		assertEquals("B4_2000003 - Registro duplicado.", json.getString("errorMessage"));
	}

	/**
	 * En este ejemplo el registro rechazado es el primero, por lo que el respaldo
	 * sobre el documento XML tambien lo detectaba.
	 */
	@Test
	void alta140ParcialmenteCorrecta_Test() throws Exception {
		JSONObject json = registros140("pf_140_1_1_alta_parcialmente_correcta.xml");

		assertTrue(json.getBoolean("error"));
		assertEquals("B4_2000003", json.getString("errorCode"));
	}

	/**
	 * En este ejemplo el registro rechazado es el segundo. El respaldo sobre el
	 * documento XML solo mira el primer EstadoRegistro del documento, con lo que daba
	 * el envio por correcto pese a haber una factura sin anotar.
	 */
	@Test
	void alta240ParcialmenteCorrecta_Test() throws Exception {
		JSONObject json = registros240("pj_240_1_1_alta_parcialmente_correcta.xml");

		assertTrue(json.getBoolean("error"), "El envio tiene un registro rechazado");
		assertEquals("B4_2000003", json.getString("errorCode"));
		assertEquals("B4_2000003 - Registro duplicado.", json.getString("errorMessage"));
	}

	/**
	 * Un registro aceptado con errores si queda anotado, por lo que no invalida el
	 * envio, pero su error se conserva para poder mostrarlo y trazarlo.
	 */
	@Test
	void altaAceptadaConErrores_Test() {
		JSONObject json = LROE.readRegistros(new LROEResponse(new JSONObject(), new byte[0]),
			registros(situacion(EstadoRegistroEnum.ACEPTADO_CON_ERRORES, "B4_2000116", "Epigrafe incorrecto."))).getJson();

		assertFalse(json.getBoolean("error"), "El registro se ha anotado");
		assertEquals("B4_2000116", json.getString("errorCode"));
		assertEquals("B4_2000116 - Epigrafe incorrecto.", json.getString("errorMessage"));
	}

	/**
	 * Rechazo completo del envio: la respuesta no lleva registros, por lo que el
	 * estado global es el de la cabecera eus-bizkaia-n3-tipo-respuesta.
	 */
	@Test
	void altaSinRegistros_Test() {
		LROEResponse response = new LROEResponse(cabeceras(), new byte[0]);

		assertDoesNotThrow(() -> LROE.readRegistros(response, null));
		assertDoesNotThrow(() -> LROE.readRegistros(response, new RegistrosFacturaConSGType()));

		assertFalse(response.getJson().has("error"), "El estado del envio no lo determina ningun registro");
		assertTrue(response.isError(), "El envio esta rechazado por la cabecera de la respuesta");
		assertEquals("B4_1000005 - El formato del fichero no es correcto.", response.getErrorMessage());
	}

	private RegistrosFacturaConSGType registros(SituacionRegistroType... situaciones) {
		RegistrosFacturaConSGType registros = new RegistrosFacturaConSGType();
		for (SituacionRegistroType situacion : situaciones) {
			RegistroFacturaConSGType registro = new RegistroFacturaConSGType();
			registro.setSituacionRegistro(situacion);
			registros.getRegistro().add(registro);
		}
		return registros;
	}

	private SituacionRegistroType situacion(EstadoRegistroEnum estado, String codigo, String descripcion) {
		SituacionRegistroType situacion = new SituacionRegistroType();
		situacion.setEstadoRegistro(estado);
		situacion.setCodigoErrorRegistro(codigo);
		situacion.setDescripcionErrorRegistroES(descripcion);
		return situacion;
	}

	// *****************************************************************
	// ***** [DOCUMENTO XML - RESPALDO DEL RESTO DE SUBCAPITULOS] ******
	// *****************************************************************

	private static final String CABECERA = """
		<Cabecera>
			<Modelo>240</Modelo>
			<Capitulo>1</Capitulo>
			<Subcapitulo>1.1</Subcapitulo>
			<Operacion>A00</Operacion>
			<Version>1.0</Version>
			<Ejercicio>2022</Ejercicio>
			<ObligadoTributario>
				<NIF>B00000034</NIF>
				<ApellidosNombreRazonSocial>HOTEL ADIBIDEZ</ApellidosNombreRazonSocial>
			</ObligadoTributario>
		</Cabecera>""";

	private static final String IDENTIFICADOR = """
		<Identificador>
			<IDFactura>
				<SerieFactura>B2022</SerieFactura>
				<NumFactura>0100</NumFactura>
				<FechaExpedicionFactura>30-01-2022</FechaExpedicionFactura>
			</IDFactura>
		</Identificador>""";

	private JSONObject situacion(String cuerpo) throws Exception {
		JSONObject json = cabeceras();
		LROE.readSituacionRegistro(XMLUtils.getDocument(cuerpo.getBytes(StandardCharsets.UTF_8)), json);
		return json;
	}

	private String respuesta(String situacionRegistro) {
		return "<LROEPJ240FacturasEmitidasConSGAltaRespuesta>"
			+ CABECERA
			+ "<DatosPresentacion>"
			+ "<FechaPresentacion>28-04-2020 16:56:36</FechaPresentacion>"
			+ "<NIFPresentador>B00000034</NIFPresentador>"
			+ "</DatosPresentacion>"
			+ "<Registros><Registro>" + IDENTIFICADOR + situacionRegistro + "</Registro></Registros>"
			+ "</LROEPJ240FacturasEmitidasConSGAltaRespuesta>";
	}

	@Test
	void registroCorrecto_Test() throws Exception {
		JSONObject json = situacion(respuesta("""
			<SituacionRegistro>
				<EstadoRegistro>Correcto</EstadoRegistro>
			</SituacionRegistro>"""));

		assertFalse(json.getBoolean("error"));
	}

	@Test
	void registroIncorrecto_Test() throws Exception {
		JSONObject json = situacion(respuesta("""
			<SituacionRegistro>
				<EstadoRegistro>Incorrecto</EstadoRegistro>
				<CodigoErrorRegistro>B4_2000003</CodigoErrorRegistro>
				<DescripcionErrorRegistroES>Registro duplicado.</DescripcionErrorRegistroES>
				<DescripcionErrorRegistroEU>Erregistro bikoiztua.</DescripcionErrorRegistroEU>
			</SituacionRegistro>"""));

		assertTrue(json.getBoolean("error"));
		assertEquals("B4_2000003", json.getString("errorCode"));
		assertEquals("B4_2000003 - Registro duplicado.", json.getString("errorMessage"));
	}

	/**
	 * Si el registro se rechaza sin informar el codigo ni la descripcion, el mensaje
	 * lo resuelve {@link LROEResponse#getErrorMessage()} con las cabeceras de la
	 * respuesta.
	 */
	@Test
	void registroIncorrectoSinDetalleDelError_Test() throws Exception {
		JSONObject json = situacion(respuesta("""
			<SituacionRegistro>
				<EstadoRegistro>Incorrecto</EstadoRegistro>
			</SituacionRegistro>"""));

		assertTrue(json.getBoolean("error"));
		assertNull(json.optString("errorCode", null));
		assertEquals("B4_1000005 - El formato del fichero no es correcto.",
			new LROEResponse(json, new byte[0]).getErrorMessage());
	}

	/**
	 * Rechazo completo del envio: la respuesta no lleva datos de presentacion ni
	 * registros, por lo que el estado global es el de la cabecera
	 * eus-bizkaia-n3-tipo-respuesta.
	 */
	@Test
	void rechazoCompletoSinRegistros_Test() throws Exception {
		String cuerpo = "<LROEPJ240FacturasEmitidasConSGAltaRespuesta>" + CABECERA
			+ "</LROEPJ240FacturasEmitidasConSGAltaRespuesta>";

		JSONObject json = assertDoesNotThrow(() -> situacion(cuerpo));

		assertFalse(json.has("error"), "El estado del envio no lo determina ningun registro");
		LROEResponse response = new LROEResponse(json, cuerpo.getBytes(StandardCharsets.UTF_8));
		assertTrue(response.isError(), "El envio esta rechazado por la cabecera de la respuesta");
		assertEquals("B4_1000005 - El formato del fichero no es correcto.", response.getErrorMessage());
		assertEquals(cuerpo, new String(response.getData(), StandardCharsets.UTF_8),
			"El cuerpo de la respuesta no se debe perder");
	}
}
