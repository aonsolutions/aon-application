package net.aonsolutions.aon.sii.aeat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasRecibidas;

/**
 * Comprueba el XML de baja de facturas recibidas que se envia al SII
 * contrastandolo con el XML de referencia.
 */
class FacturasRecibidasBajaTest {

	private static final String NIF_EMPRESA = "B00000000";
	private static final String NOMBRE_EMPRESA = "AON SOLUTIONS SL";
	private static final String SERIE_FACTURA = "FR/2026/0001";
	private static final String NOMBRE_PROVEEDOR = "PROVEEDOR SL";

	private static final Date FECHA_EMISION = AonDateUtils.getDate(2026, 2, 18); // 18-03-2026
	private static final Date FECHA_DEVENGO = AonDateUtils.getDate(2026, 3, 5); // 05-04-2026

	/** Proveedor nacional; periodo de liquidacion 2026/04, tomado de la fecha de devengo. */
	private static final String XML_ESPERADO = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<ns2:BajaLRFacturasRecibidas xmlns="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd" xmlns:ns2="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd" xmlns:ns3="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd">
			    <Cabecera>
			        <IDVersionSii>1.1</IDVersionSii>
			        <Titular>
			            <NombreRazon>AON SOLUTIONS SL</NombreRazon>
			            <NIF>B00000000</NIF>
			        </Titular>
			    </Cabecera>
			    <ns2:RegistroLRBajaRecibidas>
			        <PeriodoLiquidacion>
			            <Ejercicio>2026</Ejercicio>
			            <Periodo>04</Periodo>
			        </PeriodoLiquidacion>
			        <ns2:IDFactura>
			            <IDEmisorFactura>
			                <NombreRazon>PROVEEDOR SL</NombreRazon>
			                <NIF>B11111111</NIF>
			            </IDEmisorFactura>
			            <NumSerieFacturaEmisor>FR/2026/0001</NumSerieFacturaEmisor>
			            <FechaExpedicionFacturaEmisor>18-03-2026</FechaExpedicionFacturaEmisor>
			        </ns2:IDFactura>
			    </ns2:RegistroLRBajaRecibidas>
			</ns2:BajaLRFacturasRecibidas>
			""";

	/** Proveedor intracomunitario: NIF-IVA (02) con el codigo de pais delante del documento. */
	private static final String XML_ESPERADO_INTRACOMUNITARIA = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<ns2:BajaLRFacturasRecibidas xmlns="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd" xmlns:ns2="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd" xmlns:ns3="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd">
			    <Cabecera>
			        <IDVersionSii>1.1</IDVersionSii>
			        <Titular>
			            <NombreRazon>AON SOLUTIONS SL</NombreRazon>
			            <NIF>B00000000</NIF>
			        </Titular>
			    </Cabecera>
			    <ns2:RegistroLRBajaRecibidas>
			        <PeriodoLiquidacion>
			            <Ejercicio>2026</Ejercicio>
			            <Periodo>04</Periodo>
			        </PeriodoLiquidacion>
			        <ns2:IDFactura>
			            <IDEmisorFactura>
			                <NombreRazon>PROVEEDOR SL</NombreRazon>
			                <IDOtro>
			                    <CodigoPais>FR</CodigoPais>
			                    <IDType>02</IDType>
			                    <ID>FR12345678901</ID>
			                </IDOtro>
			            </IDEmisorFactura>
			            <NumSerieFacturaEmisor>FR/2026/0001</NumSerieFacturaEmisor>
			            <FechaExpedicionFacturaEmisor>18-03-2026</FechaExpedicionFacturaEmisor>
			        </ns2:IDFactura>
			    </ns2:RegistroLRBajaRecibidas>
			</ns2:BajaLRFacturasRecibidas>
			""";

	/** Proveedor extracomunitario: el IDType sale del tipo de documento (pasaporte, 03). */
	private static final String XML_ESPERADO_EXTRACOMUNITARIA = """
			<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
			<ns2:BajaLRFacturasRecibidas xmlns="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd" xmlns:ns2="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd" xmlns:ns3="https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/ConsultaLR.xsd">
			    <Cabecera>
			        <IDVersionSii>1.1</IDVersionSii>
			        <Titular>
			            <NombreRazon>AON SOLUTIONS SL</NombreRazon>
			            <NIF>B00000000</NIF>
			        </Titular>
			    </Cabecera>
			    <ns2:RegistroLRBajaRecibidas>
			        <PeriodoLiquidacion>
			            <Ejercicio>2026</Ejercicio>
			            <Periodo>04</Periodo>
			        </PeriodoLiquidacion>
			        <ns2:IDFactura>
			            <IDEmisorFactura>
			                <NombreRazon>PROVEEDOR SL</NombreRazon>
			                <IDOtro>
			                    <CodigoPais>US</CodigoPais>
			                    <IDType>03</IDType>
			                    <ID>X-99999</ID>
			                </IDOtro>
			            </IDEmisorFactura>
			            <NumSerieFacturaEmisor>FR/2026/0001</NumSerieFacturaEmisor>
			            <FechaExpedicionFacturaEmisor>18-03-2026</FechaExpedicionFacturaEmisor>
			        </ns2:IDFactura>
			    </ns2:RegistroLRBajaRecibidas>
			</ns2:BajaLRFacturasRecibidas>
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
			.setTaxDate(FECHA_DEVENGO)
			.setRegistryName(NOMBRE_PROVEEDOR)
			.setTransaction(InvoiceTransactionType.NATIONAL)
			.setRegistryDocument("B11111111")
			.setRegistryDocumentType(DocumentType.CIF)
			.setRegistryDocumentCountry(Country.ES);
	}

	private String xmlBaja(Company company, Invoice invoice) {
		FacturasRecibidasBaja recibidasBaja = FacturasRecibidasBaja.getInstance();
		BajaLRFacturasRecibidas baja = recibidasBaja.bajaFacturasRecibidas(company, invoice);
		return new String(recibidasBaja.getBajaFacturasRecibidas(baja), StandardCharsets.UTF_8);
	}

	@Test
	void bajaDeUnaFacturaRecibida() {
		assertEquals(XML_ESPERADO, xmlBaja(company(), invoice()));
	}

	@Test
	void bajaDeUnaFacturaIntracomunitaria() {
		Invoice invoice = invoice()
			.setTransaction(InvoiceTransactionType.INTRACOMMUNITY)
			.setRegistryDocument("12345678901")
			.setRegistryDocumentType(DocumentType.OTHER)
			.setRegistryDocumentCountry(Country.FR);

		assertEquals(XML_ESPERADO_INTRACOMUNITARIA, xmlBaja(company(), invoice));
	}

	@Test
	void bajaDeUnaFacturaExtracomunitaria() {
		Invoice invoice = invoice()
			.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY)
			.setRegistryDocument("X-99999")
			.setRegistryDocumentType(DocumentType.PASSPORT)
			.setRegistryDocumentCountry(Country.US);

		assertEquals(XML_ESPERADO_EXTRACOMUNITARIA, xmlBaja(company(), invoice));
	}
}
