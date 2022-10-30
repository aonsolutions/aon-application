package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.EstadoRegistroConsultaEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleEmitidaConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasEmitidasConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.CabeceraFacturaConsultaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FechaDesdeHastaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FiltroConsultaFacturasEmitidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altapeticion_v1_0_2.LROEPJ240FacturasEmitidasConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_consultapeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGConsultaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_consultarespuesta_v1_0_1.LROEPJ240FacturasEmitidasConSGConsultaRespuesta;
import net.aonsolutions.aon.tbai.CRC8;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.TbaiBlockchain;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.sign.TbaiSign;

public class LROE240_1_1 extends LROE240 {
	
	private static final String CAPITULO = "1";
	private static final String SUBCAPITULO = "1.1";
	
	private static LROEPJ240FacturasEmitidasConSGAltaPeticion build(Company company, Invoice invoice, LROEInfo info, byte[] data) {
		LROEPJ240FacturasEmitidasConSGAltaPeticion proba = new LROEPJ240FacturasEmitidasConSGAltaPeticion();
		proba.setCabecera(buildCabecera(company, info));

		FacturasEmitidasConSGCodificadoType facturas = new FacturasEmitidasConSGCodificadoType();
		DetalleEmitidaConSGCodificadoType factura = new DetalleEmitidaConSGCodificadoType();
		
//		OtraInformacionTrascendenciaTributariaType otra = new OtraInformacionTrascendenciaTributariaType();
//		otra.setCupon();
//		otra.setEntidadSucedida();
//		otra.setFacturacionDispAdicionalSegundaYQuinta();
//		otra.setImporteTransmisionInmueblesSujetoAIVA();
//		otra.setInmuebles();
//		otra.setNIFRepresentanteDeclarado();
//		otra.setNumRegistroAcuerdoFacturacion();
//		otra.setReferenciaExterna();
//
//		factura.setOtraInformacionTrascendenciaTributaria(otra);
		factura.setTicketBai(data);
		facturas.getFacturaEmitida().add(factura);
		
		proba.setFacturasEmitidas(facturas);
		return proba;
	} 
	
	public LROEResponse alta(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] tbai) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00);
			final LROEPJ240FacturasEmitidasConSGAltaPeticion p240 = build(company, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasConSGAltaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_240, CAPITULO, SUBCAPITULO, operacion);
	}
	
	private LROEPJ240FacturasEmitidasConSGAnulacionPeticion buildBaja(Company company, Invoice invoice, LROEInfo info, byte[] data) {	
		LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe = new LROEPJ240FacturasEmitidasConSGAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		AnulacionesFacturasEmitidasConSGType anulaciones = new AnulacionesFacturasEmitidasConSGType();
		AnulacionFacturaConSGType anulacion = new AnulacionFacturaConSGType();
		anulacion.setAnulacionTicketBai(data);
		anulaciones.getFacturaEmitida().add(anulacion);
		lroe.setFacturasEmitidas(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] tbai) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0);
			final LROEPJ240FacturasEmitidasConSGAnulacionPeticion p240 = buildBaja(company, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasConSGAnulacionPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(company, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public boolean consulta(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.C_00);
			LROEPJ240FacturasEmitidasConSGConsultaPeticion lroe = buildConsulta(company, invoice, info);
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasConSGConsultaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( lroe, bos );
			byte[] xml = bos.toByteArray();
			byte[] data = toGzip(xml);
			LROEResponse response = sendConsulta(tbaiConfiguration, buildJSON(company, info), data);
		
			LROEPJ240FacturasEmitidasConSGConsultaRespuesta resp = (LROEPJ240FacturasEmitidasConSGConsultaRespuesta) 
					unmarshall(LROEPJ240FacturasEmitidasConSGConsultaRespuesta.class, response.getResponseDataStr());
			
			if(SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros())) {
				saveTbai(tbaiConfiguration, company, invoice, resp);
				DataRequest request = LroeData.saveRequest(company.getDomain(), new User().setLogin(""), invoice, info, data);
				response.setDataRequest(request);
				LroeData.saveResponse(company.getDomain(), new User().setLogin(""), invoice, response, info);
			}
			
			return SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveTbai(TbaiConfiguration tbaiConfiguration, Company company, Invoice invoice, LROEPJ240FacturasEmitidasConSGConsultaRespuesta resp) {
		TbaiSign tbaiSign = new TbaiSign();
		String signature = resp.getFacturasEmitidas().getFacturaEmitida().get(0).getTicketBai().getSignature();
		String date = resp.getFacturasEmitidas().getFacturaEmitida().get(0).getTicketBai().getFactura().getCabeceraFactura().getFechaExpedicionFactura();
		String emisor = resp.getFacturasEmitidas().getFacturaEmitida().get(0).getTicketBai().getSujetos().getEmisor().getNIF();
		String tbai = "";
		try {
			tbai = tbaiSign.buildTbaiId(date, emisor, signature);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		TbaiResponse tresp = new TbaiResponse().setResponseStatus("pending").setSign(signature)
			.setTbaiId(tbai);

		TbaiBlockchain bc = new TbaiBlockchain().setDate(AonDateUtils.format(new Date(), "dd-MM-yyyy"))
				.setNumber(Integer.toString(invoice.getNumber())).setSerie(invoice.getSeries())
				.setSignature(signature);

		String tbaiId = URLEncoder.encode(tresp.getTbaiId());
	
		String total = resp.getFacturasEmitidas().getFacturaEmitida().get(0).getTicketBai().getFactura().getDatosFactura().getImporteTotalFactura();

		String qrUrl = TbaiUri.getUrlQr(tbaiConfiguration) + "?id=" + tbaiId + "&s="
			+ (invoice.getSeries() != null ? invoice.getSeries() : "") + "&nf=" + invoice.getNumber() + "&i="
			+ total;
		
		try {
			String crc = CRC8.calculate(qrUrl);
			qrUrl = qrUrl + "&cr=" + crc;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		TbaiData.getInstance(tbaiConfiguration)
			.saveResponsePending(company.getDomain(), new User().setLogin(""), invoice, tresp,
			bc, new DataRequest(), qrUrl);
	}
	
	private LROEPJ240FacturasEmitidasConSGConsultaPeticion buildConsulta(Company company, Invoice invoice, LROEInfo info) {
		LROEPJ240FacturasEmitidasConSGConsultaPeticion lroe = new LROEPJ240FacturasEmitidasConSGConsultaPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		
		FiltroConsultaFacturasEmitidasType filtro = new FiltroConsultaFacturasEmitidasType(); 
		filtro.setCabeceraFactura(buildCabeceraFactura(invoice));
		filtro.setDestinatario(buildDestinatario(invoice));
		filtro.setEstado(EstadoRegistroConsultaEnum.CORRECTO);
		filtro.setNumPaginaConsulta(1);
		lroe.setFiltroConsultaFacturasEmitidasConSG(filtro);
		return lroe;
	}
	
	private CabeceraFacturaConsultaType buildCabeceraFactura(Invoice invoice) {
		CabeceraFacturaConsultaType cabecera = new CabeceraFacturaConsultaType();
		FechaDesdeHastaType fecha = new FechaDesdeHastaType();
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		cabecera.setFechaExpedicionFactura(fecha);
		
		FechaDesdeHastaType fechaRec = new FechaDesdeHastaType();
		fechaRec.setDesde(AonDateUtils.format(invoice.getCreationDate(), DATE_FORMAT));
		fechaRec.setDesde(AonDateUtils.format(invoice.getCreationDate(), DATE_FORMAT));
	
		if(!AonStringUtils.isBlank(invoice.getSeries()))
		    cabecera.setSerieFactura(invoice.getSeries());
        cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		return cabecera;
	}
	
	private DocumentoType buildDestinatario(Invoice invoice) {
		DocumentoType destinatario = new DocumentoType();
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			destinatario.setNIF(invoice.getRegistryDocument());
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			String document = invoice.getRegistryDocument();
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			destinatario.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument());
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			destinatario.setIDOtro(otro);
		}
		return destinatario;
	}
}
