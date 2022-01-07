package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleEmitidaConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasEmitidasConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altapeticion_v1_0_2.LROEPJ240FacturasEmitidasConSGAltaPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

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
	
	public static LROEResponse alta(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] tbai) throws StatusCodeException {
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
	
	public static LROEInfo buildInfo(OperacionEnum operacion) {
		return new LROEInfo(MODEL_240, CAPITULO, SUBCAPITULO, operacion);
	}
	
	private static LROEPJ240FacturasEmitidasConSGAnulacionPeticion buildBaja(Company company, Invoice invoice, LROEInfo info, byte[] data) {	
		LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe = new LROEPJ240FacturasEmitidasConSGAnulacionPeticion();
		lroe.setCabecera(buildCabecera(company, info));
		AnulacionesFacturasEmitidasConSGType anulaciones = new AnulacionesFacturasEmitidasConSGType();
		AnulacionFacturaConSGType anulacion = new AnulacionFacturaConSGType();
		anulacion.setAnulacionTicketBai(data);
		anulaciones.getFacturaEmitida().add(anulacion);
		lroe.setFacturasEmitidas(anulaciones);
		return lroe;
	}
	
	public static LROEResponse anulacion(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] tbai) throws StatusCodeException {
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
}
