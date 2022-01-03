package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.json.JSONObject;
import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleEmitidaConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.FacturasEmitidasConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_altapeticion_v1_0_2.LROEPJ240FacturasEmitidasConSGAltaPeticion;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE240 {
	
	private final static String TEST_NIF_240 = "A99802019";
	private final static String TEST_NAME_240 = "4wbLGzaHUvHzMkJm9Z5knRPBKpLKr7"; 

	private static LROEPJ240FacturasEmitidasConSGAltaPeticion build(Company company, Invoice invoice, byte[] data) {
		LROEPJ240FacturasEmitidasConSGAltaPeticion proba = new LROEPJ240FacturasEmitidasConSGAltaPeticion();
		
		Cabecera240Type cabecera = new Cabecera240Type();
		cabecera.setModelo("240");
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(company.getDocument()); // TEST_NIF_240); // company.getDocument());
		nif.setApellidosNombreRazonSocial(company.getName()); // TEST_NAME_240);// company.getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(AonDateUtils.getYear(new Date()));
		cabecera.setCapitulo("1");
		cabecera.setSubcapitulo("1.1");
		cabecera.setOperacion(OperacionEnum.A_00);
		cabecera.setVersion("1.0");
		proba.setCabecera(cabecera);

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

	private static JSONObject buildJSON(Company company) {
		JSONObject json = new JSONObject();
		json.put("con", "LROE");
		json.put("apa", "1.1");
		JSONObject json2 = new JSONObject();
		json2.put("nif", company.getDocument()); // TEST_NIF_240); // company.getDocument());
		json2.put("nrs", company.getName()); //TEST_NAME_240); // company.getName());
		json.put("inte", json2);

		JSONObject drs = new JSONObject();
		drs.put("mode", "240");
		drs.put("ejer", AonDateUtils.getYear(new Date()));
		json.put("drs", drs);
		return json;
	}
	
	public static TbaiResponse alta(Company company, TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml) throws StatusCodeException {
		try {
			Document doc = XMLUtils.getDocument(xml);
			String sign = doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
			
			final LROEPJ240FacturasEmitidasConSGAltaPeticion p240 = build(company, invoice, xml); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPJ240FacturasEmitidasConSGAltaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p240, bos );
			byte[] data = bos.toByteArray();
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
			GZIPOutputStream gzipStream = new GZIPOutputStream(baos);
			try {
				gzipStream.write(data);
			} finally {
				baos.close();
				gzipStream.close();
			}
			byte[] data2 = baos.toByteArray();
			
			
			return LROE.send(tbaiConfiguration, buildJSON(company), data2, sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
