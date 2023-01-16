package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryMiembroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoDeclaradoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.TipoOperacionIntracomunitariaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionBienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesBienesInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.BienesInversionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleOpIntracomunitariaTransfPericialesOtrosPJType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.EntregaBienesInversionPeriodoRegularizacionType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDBienInversionModelo240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFIVAType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFNIFIVAPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.OtraInformacionTrascendenciaTributariaOpIntraType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.TransferenciaPericialOtroPJType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.TransferenciasPericialesOtrosPJType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_3_bienes_altamodifpeticion_v1_0_1.LROEPJ240BienesAltaModifPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_3_bienes_anulacionpeticion_v1_0_0.LROEPJ240BienesAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_4_1_determinadasopintracomunitarias_transfpericialesotros_altamodifpeticion_v1_0_2.LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LROE140_5_2 extends LROE240 {
	
	private static final long serialVersionUID = 1L;
	
	private static final String CAPITULO = "3";
	
	private LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion build(Company company, List<Invoice> invoices, LROEInfo info) {
		LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion lroe =  new LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion();
		lroe.setCabecera(buildCabecera(company, info, invoices.get(0)));
		
		TransferenciasPericialesOtrosPJType transferencias = new TransferenciasPericialesOtrosPJType();
		invoices.stream().forEach(invoice -> {
			transferencias.getTransferenciaPericialOtro().add(buildTransferencia(company, invoice));
		});
		lroe.setTransferenciasPericialesOtros(transferencias);
		return lroe;
	}
	
	private TransferenciaPericialOtroPJType buildTransferencia(Company company, Invoice invoice) {
		TransferenciaPericialOtroPJType transferencia = new TransferenciaPericialOtroPJType();
		transferencia.setDeclarado(buildContraparte(invoice));
		transferencia.setDetalleOperacion(buildDetalleOperacion(invoice));
		transferencia.setEmisorFactura(invoice.isSales() ? buildMe(company) : buildContraparte(invoice));
		transferencia.setFechaExpedicionFactura(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		if(!AonStringUtils.isBlank(invoice.getSeries()))
			transferencia.setSerieFactura(invoice.getSeries());
		transferencia.setNumFactura(Integer.toString(invoice.getNumber()));

		return transferencia;
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
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		LinkedList<Invoice> invoices = new LinkedList<>();
		invoices.add(invoice);
		return alta(tbaiConfiguration, company, invoices);
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Company company, List<Invoice> invoices) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00);
			 
			final LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion p240 = build(company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance(LROEPJ240TransferenciasPericialesOtrosAltaModifPeticion.class);
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info, invoices.get(0)), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_240, CAPITULO, null, operacion);
	}
	
	private LROEPJ240BienesAnulacionPeticion buildBaja(Company company, List<Invoice> invoices, LROEInfo info) {	
		LROEPJ240BienesAnulacionPeticion lroe = new LROEPJ240BienesAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info, invoices.get(0)));
		
		AnulacionesBienesInversionType bienes = new AnulacionesBienesInversionType();		
		invoices.stream().forEach(invoice -> {
			bienes.getBien().addAll(buildAnulacionBienes(invoice));
		});
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
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0);
			final LROEPJ240BienesAnulacionPeticion p240 = buildBaja(company, invoices, info); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240BienesAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoices, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info, invoices.get(0)), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
}
