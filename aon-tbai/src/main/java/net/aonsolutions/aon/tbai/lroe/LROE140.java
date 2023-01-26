package net.aonsolutions.aon.tbai.lroe;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;

public class LROE140 extends LROE {
	
	private static final long serialVersionUID = 1L;
	
	protected final static String MODEL_140 = "140";
	private final static String TEST_NIF_140 = "99980200M";
	private final static String TEST_NAME_140 = "8FVCxNbMNm"; 
	private final static String TEST_SURNAME1_140 = "Vux9anjAES"; 
	private final static String TEST_SURNAME2_140 = "EMPTmw3fmi";
	
	protected Cabecera140Type buildCabecera(Person person, LROEInfo info) {
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
	
	protected JSONObject buildJSON(Person person, LROEInfo info) {
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
