package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.Person;
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
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresoConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresosConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.CabeceraFacturaConsultaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FechaDesdeHastaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposconsulta.FiltroConsultaIngresosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altapeticion_v1_0_2.LROEPF140IngresosConFacturaConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_consultapeticion_v1_0_0.LROEPF140IngresosConFacturaConSGConsultaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_consultarespuesta_v1_0_1.LROEPF140IngresosConFacturaConSGConsultaRespuesta;
import net.aonsolutions.aon.tbai.CRC8;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.TbaiBlockchain;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.TbaiUri;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.sign.TbaiSign;

public class LROE140_1_1 extends LROE140 {

	private static final long serialVersionUID = 1L;

	private static final String CAPITULO = "1";
	private static final String SUBCAPITULO = "1.1";
	
	private LROEPF140IngresosConFacturaConSGAltaPeticion build(Person person, Invoice invoice, LROEInfo info, byte[] data) {
		LROEPF140IngresosConFacturaConSGAltaPeticion proba = new LROEPF140IngresosConFacturaConSGAltaPeticion();
		proba.setCabecera(buildCabecera(person, info));

		IngresosConSGCodificadoType ingresos = new IngresosConSGCodificadoType();
		IngresoConSGCodificadoType ingreso = new IngresoConSGCodificadoType();
		ingreso.setTicketBai(data);
		
		RentaIngresosType renta = new RentaIngresosType();
		DetalleRentaIngresosType detalleRenta = new DetalleRentaIngresosType();
		detalleRenta.setCriterioCobrosYPagos(invoice.isVatAccrualPayment() ? SiNoEnum.S : SiNoEnum.N);
		if(invoice.getEpigraph().equals("183320")) invoice.setEpigraph("183321");
		if(invoice.getEpigraph().equals("183310")) invoice.setEpigraph("183311");
		if(invoice.getEpigraph().equals("184950")) invoice.setEpigraph("1849501"); // o 1849502 ??
		
		detalleRenta.setEpigrafe(invoice.getEpigraph());
		detalleRenta.setIngresoAComputarIRPFDiferenteBaseImpoIVA(SiNoEnum.N);
		//detalleRenta.setImporteIngresoIRPF();
		renta.getDetalleRenta().add(detalleRenta);
		ingreso.setRenta(renta);

		ingresos.getIngreso().add(ingreso);
		proba.setIngresos(ingresos);
		return proba;
	}
	
	public LROEResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] tbai) throws StatusCodeException {
		try {
			LROEInfo info = buildInfo(OperacionEnum.A_00, getEjercicio(tbaiConfiguration, invoice));
			final LROEPF140IngresosConFacturaConSGAltaPeticion p140 = build(person, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGAltaPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public LROEInfo buildInfo(OperacionEnum operacion, Integer ejercicio) {
		return new LROEInfo(MODEL_140, CAPITULO, SUBCAPITULO, operacion, ejercicio);
	}
	
	private LROEPF140IngresosConFacturaConSGAnulacionPeticion buildBaja(Person person, Invoice invoice, LROEInfo info, byte[] data) {	
		LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe = new LROEPF140IngresosConFacturaConSGAnulacionPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		AnulacionesIngresosConSGType anulaciones = new AnulacionesIngresosConSGType();
		
		AnulacionFacturaConSGType anulacion = new AnulacionFacturaConSGType();
		anulacion.setAnulacionTicketBai(data);
		anulaciones.getIngreso().add(anulacion);
		lroe.setIngresos(anulaciones);
		return lroe;
	}
	
	public LROEResponse anulacion(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] tbai)  {
		try {
			LROEInfo info = buildInfo(OperacionEnum.AN_0, getEjercicio(tbaiConfiguration, invoice));
			final LROEPF140IngresosConFacturaConSGAnulacionPeticion p140 = buildBaja(person, invoice, info, tbai); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGAnulacionPeticion.class );
			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] xml = bos.toByteArray();
			DataRequest dataRequest = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, xml);
			byte[] data = toGzip(xml);
			return send(tbaiConfiguration, buildJSON(person, info), data).setDataRequest(dataRequest);
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public boolean consulta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice) {
		try {
			LROEInfo info = buildInfo(OperacionEnum.C_00, getEjercicio(tbaiConfiguration, invoice));
			LROEPF140IngresosConFacturaConSGConsultaPeticion lroe = buildConsulta(person, invoice, info);
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGConsultaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( lroe, bos );
			byte[] xml = bos.toByteArray();
			byte[] data = toGzip(xml);
			LROEResponse response = sendConsulta(tbaiConfiguration, buildJSON(person, info), data);
			
			LROEPF140IngresosConFacturaConSGConsultaRespuesta resp = (LROEPF140IngresosConFacturaConSGConsultaRespuesta) 
					unmarshall(LROEPF140IngresosConFacturaConSGConsultaRespuesta.class, response.getResponseDataStr());
			
			
			if(SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros())) {
				saveTbai(tbaiConfiguration, person, invoice, resp);
				DataRequest request = LroeData.saveRequest(person.getDomain(), new User().setLogin(""), invoice, info, data);
				response.setDataRequest(request);
				LroeData.saveResponse(person.getDomain(), new User().setLogin(""), invoice, response, info);
			}
			
			return SiNoEnum.S.equals(resp.getResultadoConsulta().getExistenRegistros());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveTbai(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, LROEPF140IngresosConFacturaConSGConsultaRespuesta resp) {
		TbaiSign tbaiSign = new TbaiSign();
		String signature = resp.getIngresos().getIngreso().get(0).getTicketBai().getSignature();
		String date = resp.getIngresos().getIngreso().get(0).getTicketBai().getFactura().getCabeceraFactura().getFechaExpedicionFactura();
		String emisor = resp.getIngresos().getIngreso().get(0).getTicketBai().getSujetos().getEmisor().getNIF();
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
	
		String total = resp.getIngresos().getIngreso().get(0).getTicketBai().getFactura().getDatosFactura().getImporteTotalFactura();

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
			.saveResponsePending(person.getDomain(), new User().setLogin(""), invoice, tresp,
			bc, new DataRequest(), qrUrl);
	}

	private LROEPF140IngresosConFacturaConSGConsultaPeticion buildConsulta(Person person, Invoice invoice, LROEInfo info) {
		LROEPF140IngresosConFacturaConSGConsultaPeticion lroe = new LROEPF140IngresosConFacturaConSGConsultaPeticion();
		lroe.setCabecera(buildCabecera(person, info));
		
		FiltroConsultaIngresosConFacturaType filtro = new FiltroConsultaIngresosConFacturaType(); 
		filtro.setCabeceraFactura(buildCabeceraFactura(invoice));
		filtro.setDestinatario(buildDestinatario(invoice));
		if(invoice.getEpigraph().equals("183320")) invoice.setEpigraph("183321");
		if(invoice.getEpigraph().equals("183310")) invoice.setEpigraph("183311");
		if(invoice.getEpigraph().equals("184950")) invoice.setEpigraph("1849501"); // o 1849502 ??
		filtro.setEpigrafe(invoice.getEpigraph());
		filtro.setEstado(EstadoRegistroConsultaEnum.CORRECTO);
		filtro.setNumPaginaConsulta(1);
		lroe.setFiltroConsultaIngresosConSG(filtro);
		return lroe;
	}

	private DocumentoType buildDestinatario(Invoice invoice) {
		DocumentoType destinatario = new DocumentoType();
		if (invoice.getRegistryDocumentCountry().equals(Country.ES)) {
			destinatario.setNIF(invoice.getRegistryDocument().replace(" ", ""));
		} else if(invoice.isIntracommunity()){
			IDOtroType otro = new IDOtroType();

			if(invoice.getRegistryDocumentCountry().equals(Country.XI)) {
				otro.setCodigoPais(CountryEnum.GB);
			} else otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			
			String document = invoice.getRegistryDocument().replace(" ", "");
			if(!document.substring(0,2).equals(invoice.getRegistryDocumentCountry().getIso2())) {
				document = invoice.getRegistryDocumentCountry().getIso2() + document;
			}
			otro.setID(document);
			otro.setIDType(IDType.NIF_IVA.getName());
			destinatario.setIDOtro(otro);
		} else {
			IDOtroType otro = new IDOtroType();
			otro.setCodigoPais(CountryEnum.valueOf(invoice.getRegistryDocumentCountry().getIso2()));
			otro.setID(invoice.getRegistryDocument().replace(" ", ""));
			otro.setIDType(IDType.valueOf(invoice.getRegistryDocumentType()).getName());
			destinatario.setIDOtro(otro);
		}
		return destinatario;
	}
	
	private CabeceraFacturaConsultaType buildCabeceraFactura(Invoice invoice) {
		CabeceraFacturaConsultaType cabecera = new CabeceraFacturaConsultaType();
		FechaDesdeHastaType fecha = new FechaDesdeHastaType();
		fecha.setDesde(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		fecha.setHasta(AonDateUtils.format(invoice.getIssueDate(), DATE_FORMAT));
		cabecera.setFechaExpedicionFactura(fecha);
		
		FechaDesdeHastaType fechaRec = new FechaDesdeHastaType();
		fechaRec.setDesde(AonDateUtils.format(invoice.getCreationDate(), DATE_FORMAT));
		fechaRec.setDesde(AonDateUtils.format(invoice.getCreationDate(), DATE_FORMAT));
		
		if(!AonStringUtils.isBlank(invoice.getSeries()))
		    cabecera.setSerieFactura(invoice.getSeries());
		cabecera.setNumFactura(Integer.toString(invoice.getNumber()));
		return cabecera;
	}
}
