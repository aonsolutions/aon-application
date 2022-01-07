package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceTax;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.BienAfectoIRPFYOIVAEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveCodigoFacturaRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoFacturaGastosEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.ClaveTipoRectificativaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionRecargoEquivalenciaORegimenSimplificadoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CabeceraFacturaGastosRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.ClavesGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DatosFacturaGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturaRectificativaImporteType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasRectificadasSustituidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.GastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDClaveGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIVAGastoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_altamodifpeticion_v1_0_2.LROEPF140GastosConFacturaAltaModifPeticion;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_2_1 extends LROE140 {
	
	private static final String CAPITULO = "2";
	private static final String SUBCAPITULO = "2.1";
	
	private static LROEPF140GastosConFacturaAltaModifPeticion build(Person person, List<Invoice> invoices, LROEInfo info) {
		LROEPF140GastosConFacturaAltaModifPeticion lroe =  new LROEPF140GastosConFacturaAltaModifPeticion();
		lroe.setCabecera(buildCabecera(person, info));

		GastosConFacturaType gastos = new GastosConFacturaType();
		invoices.stream().forEach(invoice -> {
			gastos.getGasto().add(buildGasto(invoice));
		});
		lroe.setGastos(gastos);
		return lroe;
	}
		
	private static GastoConFacturaType buildGasto(Invoice invoice) {
		GastoConFacturaType gasto = new GastoConFacturaType();
		gasto.setEmisorFacturaRecibida(buildEmisor(invoice));
		gasto.setCabeceraFactura(buildInvoiceCabecera(invoice));
		gasto.setDatosFactura(buildFactura(invoice));
		gasto.setRentaIVA(buildRenta(invoice));
		return gasto;
	}
	
	private static DocumentoPersonaType buildEmisor(Invoice invoice) {
		DocumentoPersonaType emisor = new DocumentoPersonaType();
		emisor.setApellidosNombreRazonSocial(invoice.getRegistryName());
		
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			emisor.setNIF(invoice.getRegistryDocument());
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			emisor.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument());
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			emisor.setIDOtro(otro);
		}
		return emisor;
	}
	
	private static CabeceraFacturaGastosRecibidasType buildInvoiceCabecera(Invoice invoice) {
		CabeceraFacturaGastosRecibidasType cabecera = new CabeceraFacturaGastosRecibidasType();
		cabecera.setTipoFactura(ClaveTipoFacturaGastosEnum.F_1);
		cabecera.setNumFactura(invoice.getReferenceCode());
		cabecera.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		cabecera.setFechaRecepcion(AonDateUtils.format(invoice.getCreationDate(), "dd-MM-yyyy"));
		if(invoice.isRectified()) {
			FacturaRectificativaImporteType rectificativa = new FacturaRectificativaImporteType(); 
			rectificativa.setCodigo(ClaveCodigoFacturaRectificativaEnum.R_1); 
			rectificativa.setTipo(ClaveTipoRectificativaEnum.I); // por diferencia o por sustitucion
			cabecera.setFacturaRectificativa(rectificativa);
				
			FacturasRectificadasSustituidasType rectificadas = new FacturasRectificadasSustituidasType();
			IDFacturaType rectificada = new IDFacturaType();
			rectificada.setSerieFactura(invoice.getRectificationInvoiceSeries());
			rectificada.setNumFactura(invoice.getRectificationInvoiceNumber().toString());
			rectificada.setFechaExpedicionFactura(AonDateUtils.format(invoice.getRectificationInvoiceDate(), "dd-MM-yyyy"));
			rectificadas.getIDFacturaRectificadaSustituida().add(rectificada);
			cabecera.setFacturasRectificadasSustituidas(rectificadas);
		}
		return cabecera;
	}
	
	private static DatosFacturaGastoType buildFactura(Invoice invoice) {
		DatosFacturaGastoType factura = new DatosFacturaGastoType();
		Double total = invoice.getBreakdown().stream().filter(f -> TaxType.VAT.equals(f.getTaxType()))
		.mapToDouble(r -> {
			if(r.getPercentage() > 0 && r.getQuota() == 0.0) {
				r.setQuota(AonMathUtils.round(r.getBase() * r.getPercentage() / 100));
			}
			return AonMathUtils.round(r.getBase() + r.getQuota() + r.getSurchargeQuota());
		}).sum();
		
		factura.setDescripcionOperacion("Factura " + invoice.getReferenceCode());
		factura.setImporteTotalFactura(total.toString());
		ClavesGastoType claves = new ClavesGastoType();
		IDClaveGastoType clave = new IDClaveGastoType();
		clave.setClaveRegimenIvaOpTrascendencia("01");
		claves.getIDClave().add(clave);
		factura.setClaves(claves);
		return factura;
	}
	
	private static RentaIVAGastoType buildRenta(Invoice invoice) {
		RentaIVAGastoType renta = new RentaIVAGastoType();
		for (InvoiceDetail detail : invoice.getDetails()) {
			InvoiceTax tax = detail.getInvoiceTaxes().stream().filter(e -> TaxType.VAT.equals(e.getTaxType())).findFirst().get();
			InvoiceTax irpf = detail.getInvoiceTaxes().stream().filter(e -> TaxType.RETENTION.equals(e.getTaxType())).findFirst().get();
			DetalleRentaIVAGastoType r = new DetalleRentaIVAGastoType();
			r.setEpigrafe(invoice.getEpigraph());
			r.setConcepto(detail.getDescription());
			r.setBaseImponible(Double.toString(tax.getBase()));	
			r.setTipoImpositivo(Double.toString(tax.getPercentage()));
			r.setCuotaIVADeducible(Double.toString(tax.getDeductibleQuota()));
			r.setCuotaIVASoportada(Double.toString(tax.getQuota()));

			r.setCriterioCobrosYPagos(invoice.isVatAccrualPayment() ? SiNoEnum.S : SiNoEnum.N);
			if(irpf != null)
				r.setImporteGastoIRPF(Double.toString(irpf.getQuota()));
			
			r.setInversionSujetoPasivo(invoice.isIsp() ? SiNoEnum.S : SiNoEnum.N);
			if(invoice.isSurcharge()) {
				r.setOperacionEnRecargoDeEquivalenciaORegimenSimplificado(OperacionRecargoEquivalenciaORegimenSimplificadoEnum.E);
				r.setTipoRecargoEquivalencia(Double.toString(tax.getSurcharge()));
				r.setCuotaRecargoEquivalencia(Double.toString(tax.getSurchargeQuota()));				
			}

//			r.setPorcentajeCompensacionREAGYP("");
//			r.setImporteCompensacionREAGYP("");
			if(invoice.getInvestAsset() != null) {
				r.setBienAfectoIRPFYOIVA(BienAfectoIRPFYOIVAEnum.I);
				r.setReferenciaBien(Integer.toString(detail.getInvestAssetData().getId()));
			}
			
			renta.getDetalleRentaIVA().add(r);
		}

		return renta;
	}

	public static LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return alta(tbaiConfiguration, person, invoices);
	}
	
	public static LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, List<Invoice> invoices) {
		try {
			LROEInfo info = new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, OperacionEnum.A_00);
			final LROEPF140GastosConFacturaAltaModifPeticion p140 = build(person, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140GastosConFacturaAltaModifPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] data = toGzip(bos.toByteArray());
			return send(tbaiConfiguration, buildJSON(person, info), data);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public static void modificacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
	
	public static void anulacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
}
