package net.aonsolutions.aon.tbai.lroe;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.json.JSONObject;
import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.SiNoEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DetalleRentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresoConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IngresosConSGCodificadoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.RentaIngresosType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_altapeticion_v1_0_2.LROEPF140IngresosConFacturaConSGAltaPeticion;
import net.aonsolutions.aon.tbai.exceptions.http.StatusCodeException;
import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import net.aonsolutions.aon.tbai.utils.XMLUtils;

public class LROE140 {

	private final static String MODEL_140 = "140";
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	private static LROEPF140IngresosConFacturaConSGAltaPeticion build(Person person, Invoice invoice, byte[] data) {
		LROEPF140IngresosConFacturaConSGAltaPeticion proba = new LROEPF140IngresosConFacturaConSGAltaPeticion();
		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo(MODEL_140);
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(person.getDocument());
		nif.setApellidosNombreRazonSocial(person.getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(2021);
		cabecera.setCapitulo("1");
		cabecera.setSubcapitulo("1.1");
		cabecera.setOperacion(OperacionEnum.A_00);
		cabecera.setVersion("1.0");
		proba.setCabecera(cabecera);

		IngresosConSGCodificadoType ingresos = new IngresosConSGCodificadoType();
		IngresoConSGCodificadoType ingreso = new IngresoConSGCodificadoType();
		RentaIngresosType renta = new RentaIngresosType();
		DetalleRentaIngresosType detalleRenta = new DetalleRentaIngresosType();
		detalleRenta.setCriterioCobrosYPagos(SiNoEnum.N);
		detalleRenta.setEpigrafe(invoice.getEpigraph());
		detalleRenta.setIngresoAComputarIRPFDiferenteBaseImpoIVA(SiNoEnum.N);
		//detalleRenta.setImporteIngresoIRPF();
		renta.getDetalleRenta().add(detalleRenta);
		ingreso.setRenta(renta);
		ingreso.setTicketBai(data);
		ingresos.getIngreso().add(ingreso);
		proba.setIngresos(ingresos);
		return proba;
	}
	
	private static JSONObject buildJSON(Person person) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, "LROE");
		json.put(IJsonNames.APA, "1.1");
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, person.getDocument()); // TEST_NIF_140);
		json2.put(IJsonNames.NRS, person.getFirstName()); // TEST_NAME_140);
		json2.put(IJsonNames.AP1, person.getFirstSurname()); // TEST_SURNAME1_140);
		json2.put(IJsonNames.AP2, person.getSecondSurname()); // TEST_SURNAME2_140);
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, MODEL_140);
		drs.put(IJsonNames.EJER, AonDateUtils.getYear(new Date()));
		json.put(IJsonNames.DRS, drs);
		return json;
	}
	
	public static TbaiResponse alta(TbaiConfiguration tbaiConfiguration, Person person, Invoice invoice, byte[] xml) throws StatusCodeException {
		try {
			Document doc = XMLUtils.getDocument(xml);
			String sign = doc.getElementsByTagName("ds:SignatureValue").item(0).getTextContent();
			
			final LROEPF140IngresosConFacturaConSGAltaPeticion p140 = build(person, invoice, xml); 
			final JAXBContext jaxbContext = JAXBContext.newInstance( LROEPF140IngresosConFacturaConSGAltaPeticion.class );
			final Marshaller jaxbMarshaller   = jaxbContext.createMarshaller();	

			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal( p140, bos );
			byte[] data = bos.toByteArray();
			return LROE.send(tbaiConfiguration, buildJSON(person), data, sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void modificacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
	
	public static void anulacion(TbaiConfiguration tbaiConfiguration, Invoice invoice, byte[] xml)  {

	}
}
