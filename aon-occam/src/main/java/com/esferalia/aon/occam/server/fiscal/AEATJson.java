package com.esferalia.aon.occam.server.fiscal;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;

public class AEATJson {
	private AEATJson() {
	}
	
	private static final String RESPUESTA = "respuesta";
	private static final String RESPUESTA_ERRORES = "errores";
	private static final String RESPUESTA_CORRECTA = "correcta";
	private static final String FORMA_PAGO = "FormaPago";
	private static final String CODIGO_SEGURO_VERIFICACION = "CodigoSeguroVerificacion";
	private static final String FECHA = "Fecha";
	private static final String HORA = "Hora";
	private static final String EXPEDIENTE = "Expediente";
	private static final String NIF_PRESENTADOR = "NIFPresentador";
	private static final String APELLIDOS_NOMBRE_PRESENTADOR = "ApellidosNombrePresentador";
	private static final String TIPO_REPRESENTACION = "TipoRepresentacion";
	private static final String NIF_DECLARANTE = "NIFDeclarante";
	private static final String APELLIDOS_NOMBRE_DECLARANTE = "ApellidosNombreDeclarante";
	private static final String MODELO = "Modelo";
	private static final String EJERCICIO = "Ejercicio";
	private static final String PERIODO = "Periodo";
	private static final String JUSTIFICANTE = "Justificante";
	private static final String NRC_PAGO = "NRCPago";
	private static final String IMPORTE_A_INGRESAR = "ImporteAIngresar";
	private static final String IDIOMA = "Idioma";
	private static final String URL_PDF = "urlPdf";
	private static final String PRESENTACION_LOTES = "PresentacionLotes";
	
	
	public static AEATResponse toJSON(byte[] body) throws JSONException {
		// FALTA
		//JSONObject json = new JSONObject( new String(body) );
		JSONObject json;
		try {
			json = new JSONObject( new String(body,"UTF-8") );
		}
		catch (UnsupportedEncodingException e) {
			json = new JSONObject( new String(body) );
		}
		System.out.println( " ------ AEAT Response ----" );
		System.out.println( json.toString(1) );
		System.out.println( " -------------------------" );
		AEATResponse aeatResponse = new AEATResponse();
		JSONObject jsonRespuesta = json.optJSONObject(RESPUESTA);
		if (jsonRespuesta.has(RESPUESTA_CORRECTA)) {
			JSONObject jsonCorrecta = jsonRespuesta.optJSONObject(RESPUESTA_CORRECTA);
			aeatResponse.setFormaPago(jsonCorrecta.optString(FORMA_PAGO));
			aeatResponse.setCodigoSeguroVerificacion(jsonCorrecta.optString(CODIGO_SEGURO_VERIFICACION));
			aeatResponse.setFecha(jsonCorrecta.optString(FECHA));
			aeatResponse.setHora(jsonCorrecta.optString(HORA));
			aeatResponse.setExpediente(jsonCorrecta.optString(EXPEDIENTE));
			aeatResponse.setnIFPresentador(jsonCorrecta.optString(NIF_PRESENTADOR));
			aeatResponse.setApellidosNombrePresentador(jsonCorrecta.optString(APELLIDOS_NOMBRE_PRESENTADOR));
			aeatResponse.setTipoRepresentacion(jsonCorrecta.optString(TIPO_REPRESENTACION));
			aeatResponse.setNifDeclarante(jsonCorrecta.optString(NIF_DECLARANTE));
			aeatResponse.setApellidosNombreDeclarante(jsonCorrecta.optString(APELLIDOS_NOMBRE_DECLARANTE));
			aeatResponse.setModelo(jsonCorrecta.optString(MODELO));
			aeatResponse.setEjercicio(jsonCorrecta.optString(EJERCICIO));
			aeatResponse.setPeriodo(jsonCorrecta.optString(PERIODO));
			aeatResponse.setJustificante(jsonCorrecta.optString(JUSTIFICANTE));
			aeatResponse.setNrcPago(jsonCorrecta.optString(NRC_PAGO));
			aeatResponse.setImporteAIngresar(jsonCorrecta.optString(IMPORTE_A_INGRESAR));
			aeatResponse.setIdioma(jsonCorrecta.optString(IDIOMA));
			aeatResponse.setUrlPdf(jsonCorrecta.optString(URL_PDF));
			aeatResponse.setPresentacionLotes(jsonCorrecta.optString(PRESENTACION_LOTES));
		} else if (jsonRespuesta.has(RESPUESTA_ERRORES)) {
			JSONArray jsonErrores = jsonRespuesta.optJSONArray(RESPUESTA_ERRORES);
			for (int i = 0; i < jsonErrores.length(); i++ ) {
				aeatResponse.addError(jsonErrores.getString(i));
			}
		} else {
			aeatResponse.addError("No se ha encontrado una respuesta válida por parte de la Agencia Tributaria. (JSON)");
		}
		return aeatResponse;
	}
	
}
/*

*/