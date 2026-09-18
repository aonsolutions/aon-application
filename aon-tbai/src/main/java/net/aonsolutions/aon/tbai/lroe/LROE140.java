package net.aonsolutions.aon.tbai.lroe;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import net.aonsolutions.aon.tbai.InvoiceCommunication;

public class LROE140 extends LROE {
	
	private static final long serialVersionUID = 1L;
	
	protected final static String MODEL_140 = "140";
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	protected static Cabecera140Type buildCabecera(InvoiceCommunicatorContext ic, LROEInfo info) {
		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo(info.getModelo());
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(!ic.getPerson().isEmpty() ? ic.getPerson().getDocument() : ic.getCompany().getDocument());
		nif.setApellidosNombreRazonSocial(!ic.getPerson().isEmpty() ? ic.getPerson().getName() : ic.getCompany().getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(info.getEjercicio());
		cabecera.setCapitulo(info.getCapitulo());
		cabecera.setSubcapitulo(info.getSubcapitulo());
		cabecera.setOperacion(info.getOperacion());
		cabecera.setVersion(info.getVersion());
		return cabecera;
	}
	
	@Deprecated
	protected Cabecera140Type buildCabecera(InvoiceCommunication ic, LROEInfo info) {
		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo(info.getModelo());
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(!ic.getPerson().isEmpty() ? ic.getPerson().getDocument() : ic.getCompany().getDocument());
		nif.setApellidosNombreRazonSocial(!ic.getPerson().isEmpty() ? ic.getPerson().getName() : ic.getCompany().getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(info.getEjercicio());
		cabecera.setCapitulo(info.getCapitulo());
		cabecera.setSubcapitulo(info.getSubcapitulo());
		cabecera.setOperacion(info.getOperacion());
		cabecera.setVersion(info.getVersion());
		return cabecera;
	}
	
	@Deprecated
	protected static Cabecera140Type buildCabecera(Person person, LROEInfo info) {
		Cabecera140Type cabecera = new Cabecera140Type();
		cabecera.setModelo(info.getModelo());
		NIFPersonaType nif = new NIFPersonaType();
		nif.setNIF(person.getDocument());
		nif.setApellidosNombreRazonSocial(person.getName());
		cabecera.setObligadoTributario(nif);
		cabecera.setEjercicio(info.getEjercicio());
		cabecera.setCapitulo(info.getCapitulo());
		cabecera.setSubcapitulo(info.getSubcapitulo());
		cabecera.setOperacion(info.getOperacion());
		cabecera.setVersion(info.getVersion());
		return cabecera;
	}
	
	protected static JSONObject buildJSON(InvoiceCommunication ic, LROEInfo info) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, "LROE");
		json.put(IJsonNames.APA, info.getSubcapitulo());
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, !ic.getPerson().isEmpty() ? ic.getPerson().getDocument().replace(" ", "") : ic.getCompany().getDocument().replace(" ", "")); 
		json2.put(IJsonNames.NRS, !ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getFirstName()) 
				? ic.getPerson().getFirstName() : ic.getCompany().getName());
		if(!ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getFirstSurname())) json2.put(IJsonNames.AP1, ic.getPerson().getFirstSurname()); 
		if(!ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getSecondSurname())) json2.put(IJsonNames.AP2, ic.getPerson().getSecondSurname());
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, info.getModelo());
				
		drs.put(IJsonNames.EJER, info.getEjercicio());
		json.put(IJsonNames.DRS, drs);
		return json;
	}
	
	protected static JSONObject buildJSON(InvoiceCommunicatorContext ic, LROEInfo info) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, "LROE");
		json.put(IJsonNames.APA, info.getSubcapitulo());
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, !ic.getPerson().isEmpty() ? ic.getPerson().getDocument().replace(" ", "") : ic.getCompany().getDocument().replace(" ", "")); 
		json2.put(IJsonNames.NRS, !ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getFirstName()) 
				? ic.getPerson().getFirstName() : ic.getCompany().getName());
		if(!ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getFirstSurname())) json2.put(IJsonNames.AP1, ic.getPerson().getFirstSurname()); 
		if(!ic.getPerson().isEmpty() && AonStringUtils.isNotBlank(ic.getPerson().getSecondSurname())) json2.put(IJsonNames.AP2, ic.getPerson().getSecondSurname());
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, info.getModelo());
				
		drs.put(IJsonNames.EJER, info.getEjercicio());
		json.put(IJsonNames.DRS, drs);
		return json;
	}
	
	@Deprecated
	protected static JSONObject buildJSON(Person person, LROEInfo info) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.CON, "LROE");
		json.put(IJsonNames.APA, info.getSubcapitulo());
		JSONObject json2 = new JSONObject();
		json2.put(IJsonNames.NIF, person.getDocument().replace(" ", "")); 
		json2.put(IJsonNames.NRS, AonStringUtils.isNotBlank(person.getFirstName()) ? person.getFirstName() : person.getName());
		if(AonStringUtils.isNotBlank(person.getFirstSurname())) json2.put(IJsonNames.AP1, person.getFirstSurname()); 
		if(AonStringUtils.isNotBlank(person.getSecondSurname())) json2.put(IJsonNames.AP2, person.getSecondSurname());
		json.put(IJsonNames.INTE, json2);

		JSONObject drs = new JSONObject();
		drs.put(IJsonNames.MODE, info.getModelo());
				
		drs.put(IJsonNames.EJER, info.getEjercicio());
		json.put(IJsonNames.DRS, drs);
		return json;
	}
}
