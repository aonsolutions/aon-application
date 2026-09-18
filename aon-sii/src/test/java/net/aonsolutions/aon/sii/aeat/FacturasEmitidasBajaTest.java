package net.aonsolutions.aon.sii.aeat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;

/**
 * Comprueba el XML de baja de facturas emitidas que se envia al SII
 * contrastandolo con el XML de referencia.
 */
class FacturasEmitidasBajaTest {

	private static final String NIF_EMPRESA = "B00000000";
	private static final String NOMBRE_EMPRESA = "AON SOLUTIONS SL";
	private static final String SERIE_FACTURA = "FE/2026/0001";

	private static final Date FECHA_EMISION = AonDateUtils.getDate(2026, 2, 18); // 18-03-2026
	private static final Date FECHA_DEVENGO = AonDateUtils.getDate(2026, 3, 5); // 05-04-2026
	private static final Date FECHA_EXPEDICION = AonDateUtils.getDate(2026, 2, 20); // 20-03-2026

	/**
	 * Periodo de liquidacion 2026/04, tomado de la fecha de devengo, y RefExterna
	 * con el id de la factura.
	 */
	private static final String XML_ESPERADO = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<ns2:BajaLRFacturasEmitidas xmlns="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd" xmlns:ns2="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd" xmlns:ns3="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd">
			    <Cabecera>
			        <IDVersionSii>1.1</IDVersionSii>
			        <Titular>
			            <NombreRazon>AON SOLUTIONS SL</NombreRazon>
			            <NIF>B00000000</NIF>
			        </Titular>
			    </Cabecera>
			    <ns2:RegistroLRBajaExpedidas>
			        <PeriodoLiquidacion>
			            <Ejercicio>2026</Ejercicio>
			            <Periodo>04</Periodo>
			        </PeriodoLiquidacion>
			        <ns2:IDFactura>
			            <IDEmisorFactura>
			                <NIF>B00000000</NIF>
			            </IDEmisorFactura>
			            <NumSerieFacturaEmisor>FE/2026/0001</NumSerieFacturaEmisor>
			            <FechaExpedicionFacturaEmisor>18-03-2026</FechaExpedicionFacturaEmisor>
			        </ns2:IDFactura>
			        <ns2:RefExterna>1234</ns2:RefExterna>
			    </ns2:RegistroLRBajaExpedidas>
			</ns2:BajaLRFacturasEmitidas>
			""";

	private Company company() {
		Company company = new Company();
		company.setDocument(NIF_EMPRESA);
		company.setName(NOMBRE_EMPRESA);
		return company;
	}

	private Invoice invoice() {
		return new Invoice()
			.setId(1234)
			.setReferenceCode(SERIE_FACTURA)
			.setIssueDate(FECHA_EMISION)
			.setTaxDate(FECHA_DEVENGO);
	}

	private String xmlBaja(Company company, Invoice invoice) {
		FacturasEmitidasBaja emitidasBaja = FacturasEmitidasBaja.getInstance();
		BajaLRFacturasEmitidas baja = emitidasBaja.bajaFacturasEmitidas(company, invoice);
		return new String(emitidasBaja.getBajaFacturasEmitidas(baja), StandardCharsets.UTF_8);
	}

	@Test
	void bajaDeUnaFacturaEmitida() {
		assertEquals(XML_ESPERADO, xmlBaja(company(), invoice()));
	}

	/**
	 * Cuando la factura tiene fecha de expedicion fiscal, la baja usa esa fecha
	 * y no la de emision, porque es la que envia el alta
	 * (FacturasEmitidas.suministroFacturasEmitidas). Si no coincidiera, la AEAT
	 * no localizaria el registro que se quiere anular.
	 */
	@Test
	void usaLaFechaDeExpedicionFiscalCuandoExiste() {
		Invoice invoice = invoice();
		invoice.ensureFiscal().setExpDate(FECHA_EXPEDICION);

		assertEquals(XML_ESPERADO.replace("18-03-2026", "20-03-2026"), xmlBaja(company(), invoice));
	}
}
