package net.aonsolutions.aon.tbai.lroe;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;

public class LROEInfo {

	private String modelo;
	private String capitulo;
	private String subcapitulo;
	private String version;
	private OperacionEnum operacion;
	
	public LROEInfo(JSONObject json) {
		setModelo(JsonUtils.getString(json, "modelo"));
		setCapitulo(JsonUtils.getString(json, "capitulo"));
		setSubcapitulo(JsonUtils.getString(json, "subcapitulo"));
		setOperacion(OperacionEnum.valueOf(JsonUtils.getString(json, "operacion")));
		setVersion(JsonUtils.getString(json, "version"));
	}
	
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
	
	public InvoiceCommunicationType getCommunicationType() {
		if("1.1".equals(getSubcapitulo())) {
			return InvoiceCommunicationType.LROE_1_1;
		} else if("1.2".equals(getSubcapitulo())) {
			return InvoiceCommunicationType.LROE_1_2;
		} else if("1.3".equals(getSubcapitulo())) {
			return InvoiceCommunicationType.LROE_1_3;
		} else if("2.1".equals(getSubcapitulo())) {
			return InvoiceCommunicationType.LROE_2_1;
		} else if("2.2".equals(getSubcapitulo())) {
			return InvoiceCommunicationType.LROE_2_2;
		} else if("2".equals(getCapitulo())) {
			return InvoiceCommunicationType.LROE_2;
		}
		return null;
	}
	
	public InvoiceCommunicationOperation getCommunicationOperation() {
		if(OperacionEnum.A_00.equals(getOperacion()) || OperacionEnum.A_01.equals(getOperacion())){
			return InvoiceCommunicationOperation.REGISTER;
		} else if(OperacionEnum.M_00.equals(getOperacion()) || OperacionEnum.M_01.equals(getOperacion())) {
			return InvoiceCommunicationOperation.MODIFICATION;
		} else if(OperacionEnum.AN_0.equals(getOperacion())) {
			return InvoiceCommunicationOperation.ANNULMENT;
		} else if(OperacionEnum.C_00.equals(getOperacion())) {
			return InvoiceCommunicationOperation.CONSULTATION;
		}
		return null;
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
	
	public String getOperacionStr() {
		if(OperacionEnum.A_00.equals(getOperacion())
				|| OperacionEnum.A_01.equals(getOperacion())) {
			return "Alta";
		} else if(OperacionEnum.AN_0.equals(getOperacion())) {
			return "Anulación";
		} else if(OperacionEnum.M_00.equals(getOperacion())
				|| OperacionEnum.M_01.equals(getOperacion())) {
			return "Modificación";
		} else if(OperacionEnum.C_00.equals(getOperacion())) {
			return "Consulta";
		}
		return "";
	}
	
	public boolean isAlta() {
		return OperacionEnum.A_00.equals(getOperacion())
				|| OperacionEnum.A_01.equals(getOperacion());
	}
	
	public boolean isConsulta() {
		return OperacionEnum.C_00.equals(getOperacion());
	}
	
	public boolean isAnulacion() {
		return OperacionEnum.AN_0.equals(getOperacion());
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
