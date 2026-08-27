package net.aonsolutions.aon.sii.aeat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType.IDEmisorFactura;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.RegistroSii.PeriodoLiquidacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasRecibidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaRecibidasType;

/**
 * Comprueba las validaciones previas al envio del mensaje de baja del libro
 * registro de facturas recibidas (SiiValidation).
 *
 * La cabecera y el periodo de liquidacion se validan igual que en las facturas
 * expedidas (SiiValidationTest), por lo que aqui solo se comprueba lo propio
 * de las recibidas: la identificacion del emisor, que es el proveedor y no el
 * titular del libro registro.
 */
class SiiValidationRecibidasTest {

	private static final String NIF_TITULAR = "B00000000";
	private static final String NIF_PROVEEDOR = "B11111119";
	private static final String NIF_PROVEEDOR_INVALIDO = "B11111111";

	private static final String NOMBRE_TITULAR = "AON SOLUTIONS SL";
	private static final String NOMBRE_PROVEEDOR = "PROVEEDOR SL";
	private static final String SERIE_FACTURA = "FR/2020/0001";
	private static final String EJERCICIO = "2020";
	private static final String PERIODO = "01";
	private static final String FECHA_EXPEDICION = "15-01-2020";
	private static final String REF_EXTERNA = "1234";

	// *********************************************************
	// *********************** [MENSAJE] ***********************
	// *********************************************************

	@Test
	void mensajeNulo() {
		assertErrors(null, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void bajaValida() {
		assertDoesNotThrow(() -> SiiValidation.validateFacturasRecibidasBaja(baja()));
	}

	@Test
	void sinRegistros() {
		BajaLRFacturasRecibidas baja = baja();
		baja.getRegistroLRBajaRecibidas().clear();

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	/** La cabecera se valida igual que en las facturas expedidas. */
	@Test
	void versionSiiNoAdmitida() {
		BajaLRFacturasRecibidas baja = baja();
		baja.getCabecera().setIDVersionSii("1.0");

		assertErrors(baja, InvoiceCommunicationError.SII_4100);
	}

	@Test
	void periodoInexistente() {
		BajaLRFacturasRecibidas baja = baja();
		registro(baja).getPeriodoLiquidacion().setPeriodo("13");

		assertErrors(baja, InvoiceCommunicationError.SII_3017);
	}

	// *********************************************************
	// ****************** [EMISOR DE LA FACTURA] ***************
	// *********************************************************

	@Test
	void sinIdFactura() {
		BajaLRFacturasRecibidas baja = baja();
		registro(baja).setIDFactura(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void sinEmisorDeLaFactura() {
		BajaLRFacturasRecibidas baja = baja();
		registro(baja).getIDFactura().setIDEmisorFactura(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void nombreRazonDelEmisorVacio() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNombreRazon("  ");

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void nombreRazonDelEmisorEnElLimite() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNombreRazon("A".repeat(120));

		assertDoesNotThrow(() -> SiiValidation.validateFacturasRecibidasBaja(baja));
	}

	@Test
	void nombreRazonDelEmisorDemasiadoLargo() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNombreRazon("A".repeat(121));

		assertErrors(baja, InvoiceCommunicationError.SII_3004);
	}

	/** El emisor se identifica con el NIF o con el bloque IDOtro. */
	@Test
	void sinIdentificacionDelEmisor() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNIF(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void conNifYConIdOtro() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setIDOtro(idOtro());

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void nifDelEmisorConFormatoErroneo() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNIF(NIF_PROVEEDOR_INVALIDO);

		assertErrors(baja, InvoiceCommunicationError.SII_4111);
	}

	/**
	 * El emisor es el proveedor, por lo que su NIF no tiene que coincidir con el
	 * del titular del libro registro.
	 */
	@Test
	void nifDelEmisorDistintoDelTitular() {
		BajaLRFacturasRecibidas baja = baja();
		emisor(baja).setNIF(NIF_TITULAR);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasRecibidasBaja(baja));
	}

	// *********************************************************
	// *********************** [ID OTRO] ***********************
	// *********************************************************

	@Test
	void emisorIdentificadoConIdOtro() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();

		assertDoesNotThrow(() -> SiiValidation.validateFacturasRecibidasBaja(baja));
	}

	@Test
	void idTypeVacio() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setIDType(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void idTypeNoAdmitido() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setIDType("01");

		assertErrors(baja, InvoiceCommunicationError.SII_1103);
	}

	@Test
	void idVacio() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setID(null);

		assertErrors(baja, InvoiceCommunicationError.SII_4102);
	}

	@Test
	void idDemasiadoLargo() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setID("1".repeat(21));

		assertErrors(baja, InvoiceCommunicationError.SII_3004);
	}

	/** Con NIF-IVA (02) el codigo de pais no es exigible. */
	@Test
	void sinCodigoPaisConNifIva() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setCodigoPais(null);

		assertDoesNotThrow(() -> SiiValidation.validateFacturasRecibidasBaja(baja));
	}

	@Test
	void sinCodigoPaisConPasaporte() {
		BajaLRFacturasRecibidas baja = bajaConIdOtro();
		emisor(baja).getIDOtro().setCodigoPais(null);
		emisor(baja).getIDOtro().setIDType("03");

		assertErrors(baja, InvoiceCommunicationError.SII_1124);
	}

	// *********************************************************
	// *********************** [UTILES] ************************
	// *********************************************************

	private BajaLRFacturasRecibidas baja() {
		return baja(emisorConNif());
	}

	private BajaLRFacturasRecibidas bajaConIdOtro() {
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNombreRazon(NOMBRE_PROVEEDOR);
		emisor.setIDOtro(idOtro());
		return baja(emisor);
	}

	private BajaLRFacturasRecibidas baja(IDEmisorFactura emisor) {
		BajaLRFacturasRecibidas baja = new BajaLRFacturasRecibidas();
		baja.setCabecera(cabecera());
		baja.getRegistroLRBajaRecibidas().add(registro(emisor));
		return baja;
	}

	private CabeceraSiiBaja cabecera() {
		PersonaFisicaJuridicaESType titular = new PersonaFisicaJuridicaESType();
		titular.setNombreRazon(NOMBRE_TITULAR);
		titular.setNIF(NIF_TITULAR);

		CabeceraSiiBaja cabecera = new CabeceraSiiBaja();
		cabecera.setIDVersionSii("1.1");
		cabecera.setTitular(titular);
		return cabecera;
	}

	private IDEmisorFactura emisorConNif() {
		IDEmisorFactura emisor = new IDEmisorFactura();
		emisor.setNombreRazon(NOMBRE_PROVEEDOR);
		emisor.setNIF(NIF_PROVEEDOR);
		return emisor;
	}

	/** Proveedor intracomunitario identificado con su NIF-IVA. */
	private IDOtroType idOtro() {
		IDOtroType idOtro = new IDOtroType();
		idOtro.setCodigoPais(CountryType2.FR);
		idOtro.setIDType("02");
		idOtro.setID("FR12345678901");
		return idOtro;
	}

	private LRBajaRecibidasType registro(IDEmisorFactura emisor) {
		IDFacturaRecibidaNombreBCType idFactura = new IDFacturaRecibidaNombreBCType();
		idFactura.setIDEmisorFactura(emisor);
		idFactura.setNumSerieFacturaEmisor(SERIE_FACTURA);
		idFactura.setFechaExpedicionFacturaEmisor(FECHA_EXPEDICION);

		PeriodoLiquidacion periodoLiquidacion = new PeriodoLiquidacion();
		periodoLiquidacion.setEjercicio(EJERCICIO);
		periodoLiquidacion.setPeriodo(PERIODO);

		LRBajaRecibidasType registro = new LRBajaRecibidasType();
		registro.setIDFactura(idFactura);
		registro.setPeriodoLiquidacion(periodoLiquidacion);
		registro.setRefExterna(REF_EXTERNA);
		return registro;
	}

	private LRBajaRecibidasType registro(BajaLRFacturasRecibidas baja) {
		return baja.getRegistroLRBajaRecibidas().get(0);
	}

	private IDEmisorFactura emisor(BajaLRFacturasRecibidas baja) {
		return registro(baja).getIDFactura().getIDEmisorFactura();
	}

	/**
	 * Comprueba que la validacion falla con exactamente los errores indicados y
	 * en ese orden.
	 */
	private void assertErrors(BajaLRFacturasRecibidas baja, InvoiceCommunicationError... expected) {
		InvoiceCommunicationException e = assertThrows(InvoiceCommunicationException.class,
			() -> SiiValidation.validateFacturasRecibidasBaja(baja));

		List<InvoiceCommunicationError> esperados = Arrays.asList(expected);
		assertEquals(esperados, e.getMessages(), "Errores de validacion");
	}
}
