package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryMiembroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoDeclaradoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoOperacionIntracomunitariaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionBienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CriterioCajaCobroPFType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.CriterioCajaCobrosPFType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleOpIntracomunitariaTransfPericialesOtrosPJType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.EntregaBienesInversionPeriodoRegularizacionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDBienInversionModelo240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFIVAType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFNIFIVAPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.OperacionOriginalCobroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_5_1_criteriocajacriteriocobrosypagos_cobros_altamodifpeticion_v1_0_0.LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_3_bienes_anulacionpeticion_v1_0_0.LROEPJ240BienesAnulacionPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_5_1 extends LROE140 {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CAPITULO = "5";
	private static final String SUBCAPITULO = "5.1";
	
	private LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion build(Person person, List<Invoice> invoices, LROEInfo info) {
		LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion lroe =  new LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion();
		lroe.setCabecera(buildCabecera(person, info, invoices.get(0)));
		CriterioCajaCobrosPFType cobros = new CriterioCajaCobrosPFType();
		invoices.stream().forEach(invoice -> cobros.getCobro().addAll(buildCobros(invoice)));
		lroe.setCobros(cobros);
		return lroe;
	}
	
	private List<CriterioCajaCobroPFType> buildCobros(Invoice invoice) {
		LinkedList<CriterioCajaCobroPFType> cobros = new LinkedList<>();
		invoice.getFinances().stream().forEach(finance -> {
			cobros.add(buildCobro(invoice, finance));
		});
		return cobros;
	}

	private CriterioCajaCobroPFType buildCobro(Invoice invoice, Finance finance) {
		CriterioCajaCobroPFType cobro = new CriterioCajaCobroPFType();
		cobro.setCuotaIVADevengada("");
		cobro.setEpigrafe("");
		cobro.setFechaCobro("");
		cobro.setImporteIngresoIRPF("");
		cobro.setMedioDeCobro(getMedioCobro(finance.getPayMethodType()));
		cobro.setDescripcionMedio(finance.getPayMethodName());
		cobro.setOperacionOriginal(buildOperacion(invoice));
		return cobro;
	}
	
	private String getMedioCobro(PayMethodType type) {
		if(PayMethodType.BANK_TRANSFER.equals(type)) {
			return "01"; 
		} else if(PayMethodType.CHEQUE.equals(type)) {
			return "02";
		} else return "04";
	}
	
	private OperacionOriginalCobroType buildOperacion(Invoice invoice) {
		OperacionOriginalCobroType op = new OperacionOriginalCobroType();
		IDFacturaType factura = new IDFacturaType();
		factura.setSerieFactura(invoice.getSeries());
		factura.setNumFactura(Integer.toString(invoice.getNumber()));
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		op.setConFactura(factura);
		return op;
	}
	
	private NIFNIFIVAPersonaType buildMe(Company company) {
		NIFNIFIVAPersonaType persona = new NIFNIFIVAPersonaType();
		persona.setApellidosNombreRazonSocial(company.getName());
		persona.setNIF(company.getDocument());		
		return persona;
	}
	
	private NIFNIFIVAPersonaType buildContraparte(Invoice invoice) {
		NIFNIFIVAPersonaType persona = new NIFNIFIVAPersonaType();
		persona.setApellidosNombreRazonSocial(invoice.getRegistryName());

		NIFIVAType otro = new NIFIVAType();
		otro.setCodigoPais(CountryMiembroType.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
		String document = invoice.getRegistryDocument();
		if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
			document = invoice.getRegistryDocumentCountry().getIso2() + document;
		}
		otro.setID(document);
		otro.setIDType(IDType.NIF_IVA.getName());
		persona.setIDOtro(otro);
		return persona;
	}
	
	private void buildDestinatario() {

	}
	
	private DetalleOpIntracomunitariaTransfPericialesOtrosPJType buildDetalleOperacion(Invoice invoice) {
		DetalleOpIntracomunitariaTransfPericialesOtrosPJType detalle = new DetalleOpIntracomunitariaTransfPericialesOtrosPJType();
		detalle.setClaveDeclarado(invoice.isSales() ? TipoDeclaradoEnum.D : TipoDeclaradoEnum.R); // D (destinatario) O R (remitente)
		detalle.setCodigoEstadoMiembroOrigenODestino(CountryMiembroType.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
		detalle.setDescripcionBienes("");
		detalle.setDireccionOperador("");
		detalle.setOtrasFacturasDocumentacion("");
		detalle.setPlazoOperacion(0);
		detalle.setTipoOperacion(TipoOperacionIntracomunitariaEnum.A); // TODO
		return detalle;
	}
	
	private BienInversionType buildBien(Invoice invoice, InvestAsset investAsset) {
		BienInversionType bien = new BienInversionType();
		EntregaBienesInversionPeriodoRegularizacionType entrega = new EntregaBienesInversionPeriodoRegularizacionType();
		entrega.setFacturaEntrega(buildFactura(invoice));
		entrega.setRegularizacionDeduccionEfectuada("");
		bien.setEntregaBienesInversionPeriodoRegularizacion(entrega);
		bien.setFechaInicioUtilizacion(AonDateUtils.format(investAsset.getStartDate(), DATE_FORMAT));
		bien.setIdentificacionBien(investAsset.getDescription());
		bien.setProrrataAnualDefinitiva(0);
		bien.setRegularizacionAnualDeduccion("");
		return bien;
	}
	
	private IDFacturaType buildFactura(Invoice invoice) {
		IDFacturaType factura = new IDFacturaType();
		factura.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		factura.setSerieFactura(invoice.getSeries());
		factura.setNumFactura(Integer.toString(invoice.getNumber()));
		return factura;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return alta(tbaiConfiguration, person, invoices);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, List<Invoice> invoices) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00);
			final LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion p140 = build(person, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance(LROEPF140CriterioCajaCriterioCobrosYPagosCobrosAltaModifPeticion.class);
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, operacion);
	}
	
	private LROEPJ240BienesAnulacionPeticion buildBaja(Person person, List<Invoice> invoices, LROEInfo info) {	
		LROEPJ240BienesAnulacionPeticion lroe = new LROEPJ240BienesAnulacionPeticion();
//		lroe.setCabecera(buildCabecera(person, info));
//		
//		AnulacionesBienesInversionType bienes = new AnulacionesBienesInversionType();		
//		invoices.stream().forEach(invoice -> {
//			bienes.getBien().addAll(buildAnulacionBienes(invoice));
//		});
		return lroe;
	}
	
	private List<AnulacionBienInversionType> buildAnulacionBienes(Invoice invoice) {
		LinkedList<AnulacionBienInversionType> bienes = new LinkedList<>();
		invoice.getDetails().stream().forEach(detail -> {
			if(detail.getInvestAsset() != null && detail.getInvestAssetData() != null && detail.getInvestAssetData().getId() != null) {
				bienes.add(buildAnulacionBien(invoice, detail.getInvestAssetData()));
			}
		});
		return bienes;
	}
	
	private AnulacionBienInversionType buildAnulacionBien(Invoice invoice, InvestAsset investAsset) {
		AnulacionBienInversionType bien = new AnulacionBienInversionType();
		IDBienInversionModelo240Type  bienID= new IDBienInversionModelo240Type();
		bienID.setFechaInicioUtilizacion(AonDateUtils.format(investAsset.getStartDate(), DATE_FORMAT));
		bienID.setIdentificacionBien(investAsset.getDescription());
		bien.setIDBien(bienID);
		return bien;
	}
	
	
	public LROEResponse anulacion(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return anulacion(company, tbaiConfiguration, invoices);
	}
	
	public LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, List<Invoice> invoices) {
		return new LROEResponse(new JSONObject());
	//		try {
//			LROEInfo info = buildInfo(OperacionEnum.AN_0);
//			final LROEPJ240BienesAnulacionPeticion p240 = buildBaja(company, invoices, info); 
//			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240BienesAnulacionPeticion.class );
//			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	
//
//			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//			jaxbMarshaller.marshal( p240, bos );
//			byte[] xml = bos.toByteArray();
//			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
//			byte[] data = toGzip(xml);
//			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
//		} catch (Exception e) {
//			return error(e);
//		}
	}
}
