package net.aonsolutions.aon.tbai.lroe;

import org.json.JSONObject;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;

public class LROEInfo {

	private String modelo;
	private String capitulo;
	private String subcapitulo;
	private String version;
	private OperacionEnum operacion;
	
	public LROEInfo(String modelo, String capitulo, String subcapitulo, OperacionEnum operacion) {
		this.modelo = modelo;
		this.capitulo = capitulo;
		this.subcapitulo = subcapitulo;
		this.operacion = operacion;
		this.version = "1.0";
	}
	
	public String getModelo() {
		return modelo;
	}
	
	public LROEInfo setModelo(String modelo) {
		this.modelo = modelo;
		return this;
	}
	
	public String getCapitulo() {
		return capitulo;
	}
	
	public LROEInfo setCapitulo(String capitulo) {
		this.capitulo = capitulo;
		return this;
	}
	
	public String getSubcapitulo() {
		return subcapitulo;
	}
	
	public LROEInfo setSubcapitulo(String subcapitulo) {
		this.subcapitulo = subcapitulo;
		return this;
	}
	
	public OperacionEnum getOperacion() {
		return operacion;
	}
	
	public LROEInfo setOperacion(OperacionEnum operacion) {
		this.operacion = operacion;
		return this;
	}

	public String getVersion() {
		return version;
	}
	
	public LROEInfo setVersion(String version) {
		this.version = version;
		return this;
	}
	
	public JSONObject toJSON() {
		return new JSONObject()
			.put("modelo", getModelo())
			.put("capitulo", getCapitulo())
			.put("subcapitulo", getSubcapitulo())
			.put("operacion", operacion.name())
			.put("version", getVersion());
	}
}
