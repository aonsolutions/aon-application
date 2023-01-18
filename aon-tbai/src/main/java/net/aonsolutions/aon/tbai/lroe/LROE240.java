package net.aonsolutions.aon.tbai.lroe;

import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.server.AonDateUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;

public class LROE240 extends LROE{
	
	private static final long serialVersionUID = 1L;
	
	protected final static String MODEL_240 = "240";
	private final static String TEST_NIF_240 = "A99802019";
	private final static String TEST_NAME_240 = "4wbLGzaHUvHzMkJm9Z5knRPBKpLKr7"; 
	
	protected static Cabecera240Type buildCabecera(Company company, LROEInfo info, Invoice invoice) {
		Cabecera240Type cabecera = new Cabecera240Type();
		cabecera.setModelo(MODEL_240);
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(company.getDocument().replace(" ", ""));
		nif.setApellidosNombreRazonSocial(company.getName());
		cabecera.setObligadoTributario(nif);
		Date ejercicioDate = invoice.getFiscal().getExpDate() != null ? invoice.getFiscal().getExpDate() : invoice.getIssueDate(); 
		cabecera.setEjercicio(AonDateUtils.getYear(ejercicioDate));
		cabecera.setCapitulo(info.getCapitulo());
		if(info.getSubcapitulo() != null)
			cabecera.setSubcapitulo(info.getSubcapitulo());
		cabecera.setOperacion(info.getOperacion());
		cabecera.setVersion(info.getVersion());
		return cabecera;
	}
	
	protected static JSONObject buildJSON(Company company, LROEInfo info, Invoice invoice) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, LROE);
		json.put(IJsonNames.APA, info.getSubcapitulo() != null
				? info.getSubcapitulo() : info.getCapitulo());
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, company.getDocument().replace(" ", "")); 
		json2.put(IJsonNames.NRS, company.getName());
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, info.getModelo());
		Date ejercicioDate = invoice.getFiscal().getExpDate() != null ? invoice.getFiscal().getExpDate() : invoice.getIssueDate(); 
		drs.put(IJsonNames.EJER, AonDateUtils.getYear(ejercicioDate));
		json.put(IJsonNames.DRS, drs);
		return json;
	}	

}
