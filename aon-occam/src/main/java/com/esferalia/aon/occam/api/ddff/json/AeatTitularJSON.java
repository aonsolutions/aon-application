package com.esferalia.aon.occam.api.ddff.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ddff.AeatComunidadAutonoma;
import com.esferalia.aon.occam.api.model.ddff.AeatDiscapacidad;
import com.esferalia.aon.occam.api.model.ddff.AeatSexo;
import com.esferalia.aon.occam.api.model.ddff.AeatTitular;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AeatTitularJSON {
	
	private AeatTitularJSON() {
		
	}
	
	public static AeatTitular from(String text) {
		return from(new JSONObject(text));
	}
	
	public static List<AeatTitular> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AeatJSONUtils.stream(array)
			.map(AeatTitularJSON::from)
			.collect(Collectors.toCollection(LinkedList::new));		
	}
	
	public static AeatTitular from(JSONObject json) {
		if (json == null) return null; 
		return new AeatTitular()
			.setNif( AeatJSONUtils.getString(json, AeatJSONConstants.NIF))
			.setApellidosNombre( AeatJSONUtils.getString(json, AeatJSONConstants.APELLIDOS_NOMBRE))
			.setDiscapacidadIRPF(AeatDiscapacidad.getByValue(json.optString(AeatJSONConstants.DISCAPACIDAD_IRPF)))
			.setDiscapacidad990(AeatDiscapacidad.getByValue(json.optString(AeatJSONConstants.DISCAPACIDAD_990)))
			.setFechaNacimiento(AeatJSONUtils.getDate(json, AeatJSONConstants.FECHA_NACIMIENTO ))
			.setSexo(AeatSexo.getByValue(json.optString(AeatJSONConstants.SEXO)))
			.setFechaFallecimiento(AeatJSONUtils.getDate(json, AeatJSONConstants.FECHA_FALLECIMIENTO ))
			.setComunidadAutonoma(AeatComunidadAutonoma.getByValue(json.optString(AeatJSONConstants.COMUNIDAD_AUTONOMA)))
			.setIBAN( AeatJSONUtils.getString(json, AeatJSONConstants.IBAN))
			.setSWIFT( AeatJSONUtils.getString(json, AeatJSONConstants.SWIFT))
			.setFechaAdquisicionViviendaHabitual(AeatJSONUtils.getDate(json, AeatJSONConstants.FECHA_ADQUISICION_VIVIENDA_HABITUAL))
			.setNumeroPrestamoHipotecario( AeatJSONUtils.getString(json, AeatJSONConstants.NUMERO_PRESTAMO_HIPOTECARIO))
			.setPorcentajePrestamo( AeatJSONUtils.getDouble(json, AeatJSONConstants.PORCENTAJE_PRESTAMO))
			.setDeduccionViviendaEjercicioAnterior( AeatJSONUtils.getBoolean(json, AeatJSONConstants.DEDUCCION_VIVIENDA_EJERCICIO_ANTERIOR))
			.setIglesiaCatolica( AeatJSONUtils.getBoolean(json, AeatJSONConstants.IGLESIA_CATOLICA))
			.setFinesSociales( AeatJSONUtils.getBoolean(json, AeatJSONConstants.FINES_SOCIALES))
			;
	}
	
	public static JSONArray to(List<AeatTitular> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}

	public static JSONArray to(Stream<AeatTitular> stream) {
		return stream
			.map(AeatTitularJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}

	public static JSONObject to(AeatTitular data) {
		if (data == null) return null;
		return new JSONObject()
			.putOpt(AeatJSONConstants.NIF, data.getNif())
			.putOpt(AeatJSONConstants.APELLIDOS_NOMBRE, data.getApellidosNombre())
			.putOpt(AeatJSONConstants.DISCAPACIDAD_IRPF, data.getDiscapacidadIRPF() != null ? data.getDiscapacidadIRPF().getCode() : null)
			.putOpt(AeatJSONConstants.DISCAPACIDAD_990, data.getDiscapacidad990() != null ? data.getDiscapacidad990().getCode() : null)
			.putOpt(AeatJSONConstants.FECHA_NACIMIENTO, AeatJSONUtils.formatDate(data.getFechaNacimiento()))
			.putOpt(AeatJSONConstants.SEXO, data.getSexo() != null ? data.getSexo().getCode() : null)
			.putOpt(AeatJSONConstants.FECHA_FALLECIMIENTO, AeatJSONUtils.formatDate(data.getFechaFallecimiento()))
			.putOpt(AeatJSONConstants.COMUNIDAD_AUTONOMA, data.getComunidadAutonoma() != null ? data.getComunidadAutonoma().getCode() : null)
			.putOpt(AeatJSONConstants.IBAN, data.getIBAN())
			.putOpt(AeatJSONConstants.SWIFT, data.getSWIFT())
			.putOpt(AeatJSONConstants.FECHA_ADQUISICION_VIVIENDA_HABITUAL, AeatJSONUtils.formatDate(data.getFechaAdquisicionViviendaHabitual()))
			.putOpt(AeatJSONConstants.NUMERO_PRESTAMO_HIPOTECARIO, data.getNumeroPrestamoHipotecario())
			.putOpt(AeatJSONConstants.PORCENTAJE_PRESTAMO, data.getPorcentajePrestamo())
			.putOpt(AeatJSONConstants.DEDUCCION_VIVIENDA_EJERCICIO_ANTERIOR, data.isDeduccionViviendaEjercicioAnterior() )
			.putOpt(AeatJSONConstants.IGLESIA_CATOLICA, data.isIglesiaCatolica() )
			.putOpt(AeatJSONConstants.FINES_SOCIALES, data.isFinesSociales() )
			;
	}
	
}