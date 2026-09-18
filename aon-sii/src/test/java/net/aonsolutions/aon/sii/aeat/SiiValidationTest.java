package net.aonsolutions.aon.sii.aeat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.RegistroSii.PeriodoLiquidacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;

/**
 * Comprueba las validaciones previas al envio del mensaje de baja del libro
 * registro de facturas expedidas (SiiValidation).
 *
 * Cada test parte de un mensaje valido y altera un unico dato para provocar el
 * error esperado. Cuando el dato alterado hace fallar mas de una validacion se
 * comprueban todos los errores, porque el mensaje de baja acumula todos los que
 * encuentra antes de lanzar la excepcion.
 */
class SiiValidationTest {

	private static final String NIF_TITULAR = "B00000000";
	private static final String NIF_TITULAR_INVALIDO = "B00000001";
	private static final String NIF_OTRO_TITULAR = "12345678Z";
	private static final String NIF_MENOR = "K1234567L";
	private static final String NIF_REPRESENTANTE = "12345678Z";
	private static final String NIF_REPRESENTANTE_INVALIDO = "B00000001";

	private static final String NOMBRE_TITULAR = "AON SOLUTIONS SL";
	private static final String SERIE_FACTURA = "FE/2020/0001";
	private static final String EJERCICIO = "2020";
	private static final String PERIODO = "01";
	private static final String FECHA_EXPEDICION = "15-01-2020";
	private static final String REF_EXTERNA = "1234";

	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	// *********************************************************
	// ********************** [CABECERA] ***********************
	// *********************************************************

	@Test
	void mensajeNulo() {
		assertErrors(null, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void bajaValida() {
		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja()));
	}

	/**
	 * Sin cabecera el registro tampoco puede comprobar que su NIF emisor sea el
	 * del titular del libro registro.
	 */
	@Test
	void sinCabecera() {
		BajaLRFacturasEmitidas baja = baja();
		baja.setCabecera(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102, InvoiceCommunicationError.SII_1112);
	}

	@Test
	void versionSiiVacia() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().setIDVersionSii(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void versionSiiNoAdmitida() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().setIDVersionSii("1.0");

		assertErrors(baja, InvoiceCommunicationError.SII_4100);
	}

	@Test
	void sinTitular() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().setTitular(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102, InvoiceCommunicationError.SII_1112);
	}

	@Test
	void nombreRazonDelTitularVacio() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNombreRazon("  ");

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void nombreRazonDelTitularEnElLimite() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNombreRazon("A".repeat(120));

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void nombreRazonDelTitularDemasiadoLargo() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNombreRazon("A".repeat(121));

		assertErrors(baja, InvoiceCommunicationError.SII_3004);
	}

	@Test
	void nifDelTitularVacio() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNIF(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4104, InvoiceCommunicationError.SII_1112);
	}

	/** El mismo NIF erroneo se informa en la cabecera y en el registro. */
	@Test
	void nifDelTitularConFormatoErroneo() {
		BajaLRFacturasEmitidas baja = baja(NIF_TITULAR_INVALIDO);

		assertErrors(baja, InvoiceCommunicationError.SII_4122, InvoiceCommunicationError.SII_4111);
	}

	@Test
	void nifDelRepresentanteConFormatoErroneo() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNIFRepresentante(NIF_REPRESENTANTE_INVALIDO);

		assertErrors(baja, InvoiceCommunicationError.SII_4123);
	}

	@Test
	void nifDelRepresentanteAusenteNoSeValida() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().getTitular().setNIFRepresentante(null);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void titularMenorSinRepresentante() {
		BajaLRFacturasEmitidas baja = baja(NIF_MENOR);

		assertErrors(baja, InvoiceCommunicationError.SII_1122);
	}

	@Test
	void titularMenorConRepresentanteIgualAlTitular() {
		BajaLRFacturasEmitidas baja = baja(NIF_MENOR);
		baja.getCabecera().getTitular().setNIFRepresentante(NIF_MENOR);

		assertErrors(baja, InvoiceCommunicationError.SII_1123);
	}

	@Test
	void titularMenorConRepresentante() {
		BajaLRFacturasEmitidas baja = baja(NIF_MENOR);
		baja.getCabecera().getTitular().setNIFRepresentante(NIF_REPRESENTANTE);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	// *********************************************************
	// ***************** [NUMERO DE REGISTROS] *****************
	// *********************************************************

	@Test
	void sinRegistros() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getRegistroLRBajaExpedidas().clear();

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void enElLimiteDeRegistros() {
		BajaLRFacturasEmitidas baja = baja(10000);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void demasiadosRegistros() {
		BajaLRFacturasEmitidas baja = baja(10001);

		assertErrors(baja, InvoiceCommunicationError.SII_4117);
	}

	/** Los registros nulos de la lista se ignoran. */
	@Test
	void registroNuloIgnorado() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getRegistroLRBajaExpedidas().add(null);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	/** El mismo error de dos registros distintos se informa una sola vez. */
	@Test
	void elMismoErrorNoSeRepitePorRegistro() {
		BajaLRFacturasEmitidas baja = baja(2);
		baja.getRegistroLRBajaExpedidas().forEach(r -> r.getIDFactura().setNumSerieFacturaEmisor(null));

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	// *********************************************************
	// ***************** [PERIODO LIQUIDACION] *****************
	// *********************************************************

	@Test
	void sinPeriodoLiquidacion() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).setPeriodoLiquidacion(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void ejercicioVacio() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void ejercicioSinCuatroDigitos() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio("20");

		assertErrors(baja, InvoiceCommunicationError.SII_3015);
	}

	@Test
	void ejercicioNoNumerico() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio("20X0");

		assertErrors(baja, InvoiceCommunicationError.SII_3015);
	}

	@Test
	void ejercicioPosteriorAlActual() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio(String.valueOf(AonDateUtils.getCurrentYear() + 1));

		assertErrors(baja, InvoiceCommunicationError.SII_3015);
	}

	@Test
	void ejercicioYPeriodoActuales() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio(String.valueOf(AonDateUtils.getCurrentYear()));
		registro(baja).getPeriodoLiquidacion().setPeriodo(periodoActual());
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(hoy());

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void periodoVacio() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setPeriodo(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void periodoInexistente() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setPeriodo("13");

		assertErrors(baja, InvoiceCommunicationError.SII_3017);
	}

	@Test
	void periodoSinCeroALaIzquierda() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setPeriodo("1");

		assertErrors(baja, InvoiceCommunicationError.SII_3017);
	}

	@Test
	void periodoTrimestralAnteriorA2018() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio("2017");
		registro(baja).getPeriodoLiquidacion().setPeriodo("1T");
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor("15-01-2017");

		assertErrors(baja, InvoiceCommunicationError.SII_1211);
	}

	@Test
	void periodoTrimestralDesde2018() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio("2018");
		registro(baja).getPeriodoLiquidacion().setPeriodo("4T");
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor("15-01-2018");

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	/**
	 * En diciembre no existe ningun periodo posterior al actual dentro del
	 * ejercicio, por lo que la validacion no puede llegar a fallar.
	 */
	@Test
	void periodoPosteriorAlActualDelEjercicioEnCurso() {
		int mesSiguiente = mesActual() + 1;
		Assumptions.assumeTrue(mesSiguiente <= 12, "En diciembre no hay periodo posterior");

		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio(String.valueOf(AonDateUtils.getCurrentYear()));
		registro(baja).getPeriodoLiquidacion().setPeriodo(String.format("%02d", mesSiguiente));
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(hoy());

		assertErrors(baja, InvoiceCommunicationError.SII_3016);
	}

	/** El primer mes del trimestre es el que se compara con el mes actual. */
	@Test
	void periodoTrimestralPosteriorAlActualDelEjercicioEnCurso() {
		int trimestreSiguiente = ((mesActual() - 1) / 3) + 2;
		Assumptions.assumeTrue(trimestreSiguiente <= 4, "En el cuarto trimestre no hay trimestre posterior");

		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setEjercicio(String.valueOf(AonDateUtils.getCurrentYear()));
		registro(baja).getPeriodoLiquidacion().setPeriodo(trimestreSiguiente + "T");
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(hoy());

		assertErrors(baja, InvoiceCommunicationError.SII_3016);
	}

	// *********************************************************
	// ********************* [ID FACTURA] **********************
	// *********************************************************

	@Test
	void sinIdFactura() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).setIDFactura(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void sinEmisorDeLaFactura() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setIDEmisorFactura(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void nifDelEmisorVacio() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().getIDEmisorFactura().setNIF("  ");

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	/** Un NIF invalido en el registro tampoco puede coincidir con el del titular. */
	@Test
	void nifDelEmisorConFormatoErroneo() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().getIDEmisorFactura().setNIF(NIF_TITULAR_INVALIDO);

		assertErrors(baja, InvoiceCommunicationError.SII_4111, InvoiceCommunicationError.SII_1112);
	}

	@Test
	void nifDelEmisorDistintoDelTitular() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().getIDEmisorFactura().setNIF(NIF_OTRO_TITULAR);

		assertErrors(baja, InvoiceCommunicationError.SII_1112);
	}

	@Test
	void numeroDeSerieVacio() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setNumSerieFacturaEmisor(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void numeroDeSerieEnElLimite() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setNumSerieFacturaEmisor("F".repeat(60));

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void numeroDeSerieDemasiadoLargo() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setNumSerieFacturaEmisor("F".repeat(61));

		assertErrors(baja, InvoiceCommunicationError.SII_3004);
	}

	@Test
	void numeroDeSerieConCaracterDeControl() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setNumSerieFacturaEmisor("FE/2020\n0001");

		assertErrors(baja, InvoiceCommunicationError.SII_1105);
	}

	// *********************************************************
	// ****************** [FECHA EXPEDICION] *******************
	// *********************************************************

	@Test
	void fechaDeExpedicionVacia() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void fechaDeExpedicionConOtroFormato() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor("2020-01-15");

		assertErrors(baja, InvoiceCommunicationError.SII_4106);
	}

	/** La fecha tiene el formato correcto pero el dia no existe. */
	@Test
	void fechaDeExpedicionInexistente() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor("31-02-2020");

		assertErrors(baja, InvoiceCommunicationError.SII_1106);
	}

	@Test
	void fechaDeExpedicionDeHoy() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(hoy());

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void fechaDeExpedicionPosteriorAHoy() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(fecha(AonDateUtils.addDays(AonDateUtils.today(), 1)));

		assertErrors(baja, InvoiceCommunicationError.SII_1125);
	}

	@Test
	void fechaDeExpedicionDeHaceVeinteAnios() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(fecha(AonDateUtils.addYears(AonDateUtils.today(), -20)));

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void fechaDeExpedicionDeMasDeVeinteAnios() {
		Date limite = AonDateUtils.addDays(AonDateUtils.addYears(AonDateUtils.today(), -20), -1);

		BajaLRFacturasEmitidas baja = baja();
		registro(baja).getIDFactura().setFechaExpedicionFacturaEmisor(fecha(limite));

		assertErrors(baja, InvoiceCommunicationError.SII_1196);
	}

	// *********************************************************
	// ********************* [REF EXTERNA] *********************
	// *********************************************************

	@Test
	void refExternaAusente() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).setRefExterna(null);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void refExternaEnElLimite() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).setRefExterna("1".repeat(60));

		assertDoesNotThrow(() -> SiiValidation.validateFacturasEmitidasBaja(baja));
	}

	@Test
	void refExternaDemasiadoLarga() {
		BajaLRFacturasEmitidas baja = baja();
		registro(baja).setRefExterna("1".repeat(61));

		assertErrors(baja, InvoiceCommunicationError.SII_1210);
	}

	// *********************************************************
	// ****************** [VARIOS ERRORES] *********************
	// *********************************************************

	/** Se acumulan los errores de la cabecera y de los registros. */
	@Test
	void seAcumulanTodosLosErrores() {
		BajaLRFacturasEmitidas baja = baja();
		baja.getCabecera().setIDVersionSii("1.0");
		registro(baja).getPeriodoLiquidacion().setPeriodo("13");
		registro(baja).getIDFactura().setNumSerieFacturaEmisor(null);
		registro(baja).setRefExterna("1".repeat(61));

		assertErrors(baja,
			InvoiceCommunicationError.SII_4100,
			InvoiceCommunicationError.SII_3017,
			InvoiceCommunicationError.SII_4102,
			InvoiceCommunicationError.SII_1210);
	}

	// *********************************************************
	// *********************** [UTILES] ************************
	// *********************************************************

	private BajaLRFacturasEmitidas baja() {
		return baja(NIF_TITULAR, 1);
	}

	private BajaLRFacturasEmitidas baja(String nifTitular) {
		return baja(nifTitular, 1);
	}

	private BajaLRFacturasEmitidas baja(int registros) {
		return baja(NIF_TITULAR, registros);
	}

	/**
	 * Mensaje de baja valido con el NIF y el numero de registros indicados. El
	 * NIF del emisor de cada registro es el del titular, como exige el SII.
	 */
	private BajaLRFacturasEmitidas baja(String nifTitular, int registros) {
		BajaLRFacturasEmitidas baja = new BajaLRFacturasEmitidas();
		baja.setCabecera(cabecera(nifTitular));
		for (int i = 0; i < registros; i++) {
			baja.getRegistroLRBajaExpedidas().add(registro(nifTitular));
		}
		return baja;
	}

	private CabeceraSiiBaja cabecera(String nifTitular) {
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNombreRazon(NOMBRE_TITULAR);
		titular.setNIF(nifTitular);

		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("1.1");
		cabecera.setTitular(titular);
		return cabecera;
	}

	private LRBajaExpedidasType registro(String nifEmisor) {
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNIF(nifEmisor);

		IDFacturaExpedidaBCType idFactura = new IDFacturaExpedidaBCType();
		idFactura.setIDEmisorFactura(emisor);
		idFactura.setNumSerieFacturaEmisor(SERIE_FACTURA);
		idFactura.setFechaExpedicionFacturaEmisor(FECHA_EXPEDICION);

		PeriodoLiquidacion periodoLiquidacion = new PeriodoLiquidacion();
		periodoLiquidacion.setEjercicio(EJERCICIO);
		periodoLiquidacion.setPeriodo(PERIODO);

		LRBajaExpedidasType registro = new LRBajaExpedidasType();
		registro.setIDFactura(idFactura);
		registro.setPeriodoLiquidacion(periodoLiquidacion);
		registro.setRefExterna(REF_EXTERNA);
		return registro;
	}

	private LRBajaExpedidasType registro(BajaLRFacturasEmitidas baja) {
		return baja.getRegistroLRBajaExpedidas().get(0);
	}

	private String hoy() {
		return fecha(AonDateUtils.today());
	}

	private String fecha(Date date) {
		return AonDateUtils.format(date, FORMATO_FECHA);
	}

	private int mesActual() {
		return AonDateUtils.getMonth(AonDateUtils.today()) + 1;
	}

	private String periodoActual() {
		return String.format("%02d", mesActual());
	}

	/**
	 * Comprueba que la validacion falla con exactamente los errores indicados y
	 * en ese orden.
	 */
	private void assertErrors(BajaLRFacturasEmitidas baja, InvoiceCommunicationError... expected) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> SiiValidation.validateFacturasEmitidasBaja(baja));

		List<InvoiceCommunicationError> esperados = Arrays.asList(expected);
		assertEquals(esperados, e.getMessages(), "Errores de validacion");
	}
}
