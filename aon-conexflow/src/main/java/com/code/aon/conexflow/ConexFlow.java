package com.code.aon.conexflow;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "PE")
public class ConexFlow {

    @XmlElement(name = "Respuesta", required = true)
    protected ConexFlow.Respuesta respuesta;
    protected ConexFlow.Query query;    
    protected ConexFlowStatus status;
    protected Integer id; 
    protected byte[] data;
    protected Date date;
    protected Integer project;
    protected Double amount;
    protected Boolean antTnr;
    protected String description;

    public ConexFlow.Respuesta getRespuesta() {
        return respuesta;
    }
    
    public void setCabecera(ConexFlow.Respuesta value) {
        this.respuesta = value;
    }

    public ConexFlow.Query getQuery() {
    	return query;
    }
    
    public ConexFlow setQuery(ConexFlow.Query value) {
    	this.query = value;
    	return this;
    }
    
    public ConexFlowStatus getStatus(){
    	return status;
    }
    
    public ConexFlow setStatus(ConexFlowStatus status){
    	this.status = status;
    	return this;
    }

    public byte[] getData() {
		return data;
	}

	public ConexFlow setData(byte[] data) {
		this.data = data;
		return this;
	}

	public Date getDate() {
		return date;
	}

	public ConexFlow setDate(Date date) {
		this.date = date;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public ConexFlow setId(Integer id) {
		this.id = id;
		return this;
	}
	
	
	public Integer getProject() {
		return project;
	}

	public ConexFlow setProject(Integer project) {
		this.project = project;
		return this;
	}

	public Double getAmount() {
		return amount;
	}

	public ConexFlow setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public Boolean isAntTnr() {
		return antTnr;
	}

	public ConexFlow setAntTnr(Boolean antTnr) {
		this.antTnr = antTnr;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public ConexFlow setDescription(String description) {
		setStatus(ConexFlowStatus.valueOfDescriptionName(description));
		Integer index = description.indexOf("#");
		String s = description.substring(index + 1);
		if(s.contains("#")){
			index = s.indexOf("#");
			s = s.substring(0, index);
		}
		setAmount(!ConexFlowStatus.CREATE_TOKEN.equals(getStatus()) && !ConexFlowStatus.CREATE_TOKEN_FAIL.equals(getStatus())
				? Double.parseDouble(s) : null);
		setAntTnr(description.contains("ANT_TNR"));
		this.description = description;
		return this;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Respuesta {

    	@XmlAttribute(name="Operacion", required = true)
    	protected String operacion;
    	@XmlAttribute(name="Empresa", required = true)
    	protected String empresa;
    	@XmlAttribute(name="TPV", required = true)
    	protected String tpv;
    	@XmlAttribute(name="Centro", required = true)
    	protected String centro;
    	@XmlAttribute(name="Fecha")
    	protected String fecha;
    	@XmlAttribute(name="Hora")
    	protected String hora;
    	@XmlAttribute(name="ID_Operacion")
    	protected String idOperacion;
    	
        @XmlElement(name = "Resultado", required = true)
        protected String resultado;
        @XmlElement(name = "Ref_Cliente")
        protected String refClient;
        @XmlElement(name = "Des_Resultado")
        protected String desResultado;
        @XmlElement(name = "Des_Tipo_Doc")
        protected String desTipoDoc;
        @XmlElement(name = "Des_CA")
        protected String desCA;
        @XmlElement(name = "Autorizacion")
        protected String autorizacion;
        @XmlElement(name = "Comercio")
        protected String comercio;
        @XmlElement(name = "Terminal")
        protected String teminal;
        @XmlElement(name = "Referencia")
        protected String referencia;
        @XmlElement(name = "Ticket")
        protected String ticket;
        @XmlElement(name = "Voucher")
        protected String voucher;
        @XmlElement(name = "InfoAdicionalSalida")
        protected String infoAdicionalSalida;
        @XmlElement(name = "TipoAutenticacion")
        protected String tipoAutenticacion;
        @XmlElement(name = "TipoAutorizacion")
        protected String tipoAutorizacion;
        @XmlElement(name = "CF_AuthURL")
        protected String CF_AuthURL;
        @XmlElement(name = "CF_MAC")
        protected String CF_MAC;
        @XmlElement(name = "CF_BankID")
        protected String CF_BankID;
        @XmlElement(name = "CF_ReplyURL")
        protected String CF_ReplyURL;
        @XmlElement(name = "CF_PAN")
        protected String CF_PAN;
        @XmlElement(name = "CF_DocumentNumber")
        protected String CF_DocumentNumber;
        @XmlElement(name = "CF_ExpDateTime")
        protected String CF_ExpDateTime;
        @XmlElement(name = "CF_ExpirationDate")
        protected String CF_ExpirationDate;
        @XmlElement(name = "Cod_Res_CA")
        protected String codResCA;
        @XmlElement(name = "Fecha_Original")
        protected String fechaOriginal;
        @XmlElement(name = "Hora_Original")
        protected String horaOriginal;
        @XmlElement(name = "Ref_Cliente_Original")
        protected String refClienteOriginal;
        @XmlElement(name = "Centro_Original")
        protected String centroOriginal;
        @XmlElement(name = "Tpv_Original")
        protected String tpvOriginal;
        @XmlElement(name = "ID_Operacion_Original")
        protected String idOperacionOriginal;
        @XmlElement(name = "OperacionOriginal")
        protected String operacionOriginal;
        @XmlElement(name = "Importe")
        protected String importe;
        @XmlElement(name = "Token")
        protected String token;
        
		public String getRefClient() {
			return refClient;
		}

		public void setRefClient(String refClient) {
			this.refClient = refClient;
		}

		public String getDesResultado() {
			return desResultado;
		}

		public void setDesResultado(String desResultado) {
			this.desResultado = desResultado;
		}

		public String getDesTipoDoc() {
			return desTipoDoc;
		}

		public void setDesTipoDoc(String desTipoDoc) {
			this.desTipoDoc = desTipoDoc;
		}

		public String getDesCA() {
			return desCA;
		}

		public void setDesCA(String desCA) {
			this.desCA = desCA;
		}

		public String getAutorizacion() {
			return autorizacion;
		}

		public void setAutorizacion(String autorizacion) {
			this.autorizacion = autorizacion;
		}

		public String getComercio() {
			return comercio;
		}

		public void setComercio(String comercio) {
			this.comercio = comercio;
		}

		public String getTeminal() {
			return teminal;
		}

		public void setTeminal(String teminal) {
			this.teminal = teminal;
		}

		public String getReferencia() {
			return referencia;
		}

		public void setReferencia(String referencia) {
			this.referencia = referencia;
		}

		public String getTicket() {
			return ticket;
		}

		public void setTicket(String ticket) {
			this.ticket = ticket;
		}

		public String getInfoAdicionalSalida() {
			return infoAdicionalSalida;
		}

		public void setInfoAdicionalSalida(String infoAdicionalSalida) {
			this.infoAdicionalSalida = infoAdicionalSalida;
		}

		public String getTipoAutenticacion() {
			return tipoAutenticacion;
		}

		public void setTipoAutenticacion(String tipoAutenticacion) {
			this.tipoAutenticacion = tipoAutenticacion;
		}

		public String getTipoAutorizacion() {
			return tipoAutorizacion;
		}

		public void setTipoAutorizacion(String tipoAutorizacion) {
			this.tipoAutorizacion = tipoAutorizacion;
		}

		public String getCF_AuthURL() {
			return CF_AuthURL;
		}

		public void setCF_AuthURL(String cF_AuthURL) {
			CF_AuthURL = cF_AuthURL;
		}

		public String getCF_MAC() {
			return CF_MAC;
		}

		public void setCF_MAC(String cF_MAC) {
			CF_MAC = cF_MAC;
		}

		public String getCF_BankID() {
			return CF_BankID;
		}

		public void setCF_BankID(String cF_BankID) {
			CF_BankID = cF_BankID;
		}

		public String getCF_ReplyURL() {
			return CF_ReplyURL;
		}

		public void setCF_ReplyURL(String cF_ReplyURL) {
			CF_ReplyURL = cF_ReplyURL;
		}

		public String getCF_PAN() {
			return CF_PAN;
		}

		public void setCF_PAN(String cF_PAN) {
			CF_PAN = cF_PAN;
		}

		public String getCF_DocumentNumber() {
			return CF_DocumentNumber;
		}

		public void setCF_DocumentNumber(String cF_DocumentNumber) {
			CF_DocumentNumber = cF_DocumentNumber;
		}

		public String getCF_ExpDateTime() {
			return CF_ExpDateTime;
		}

		public void setCF_ExpDateTime(String cF_ExpDateTime) {
			CF_ExpDateTime = cF_ExpDateTime;
		}
		
		
		public String getCF_ExpirationDate(){
			return CF_ExpirationDate;
		}
		
		public void setCF_ExpirationDate(String CF_ExpirationDate){
			this.CF_ExpirationDate = CF_ExpirationDate;
		}

		public String getCodResCA() {
			return codResCA;
		}

		public void setCodResCA(String codResCA) {
			this.codResCA = codResCA;
		}

		public String getFechaOriginal() {
			return fechaOriginal;
		}

		public void setFechaOriginal(String fechaOriginal) {
			this.fechaOriginal = fechaOriginal;
		}

		public String getHoraOriginal() {
			return horaOriginal;
		}

		public void setHoraOriginal(String horaOriginal) {
			this.horaOriginal = horaOriginal;
		}

		public String getRefClienteOriginal() {
			return refClienteOriginal;
		}

		public void setRefClienteOriginal(String refClienteOriginal) {
			this.refClienteOriginal = refClienteOriginal;
		}

		public String getCentroOriginal() {
			return centroOriginal;
		}

		public void setCentroOriginal(String centroOriginal) {
			this.centroOriginal = centroOriginal;
		}

		public String getIdOperacionOriginal() {
			return idOperacionOriginal;
		}

		public void setIdOperacionOriginal(String idOperacionOriginal) {
			this.idOperacionOriginal = idOperacionOriginal;
		}

		public String getOperacionOriginal() {
			return operacionOriginal;
		}

		public void setOperacionOriginal(String operacionOriginal) {
			this.operacionOriginal = operacionOriginal;
		}

		public String getImporte() {
			return importe;
		}

		public void setImporte(String importe) {
			this.importe = importe;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getTpv() {
			return tpv;
		}
		
		public void setTpv(String tpv) {
			this.tpv = tpv;
		}
		
		public String getResultado() {
			return resultado;
		}

		public void setResultado(String resultado) {
			this.resultado = resultado;
		}

		public String getOperacion() {
			return operacion;
		}

		public void setOperacion(String operacion) {
			this.operacion = operacion;
		}

		public String getEmpresa() {
			return empresa;
		}

		public void setEmpresa(String empresa) {
			this.empresa = empresa;
		}

		public String getCentro() {
			return centro;
		}

		public void setCentro(String centro) {
			this.centro = centro;
		}

		public String getFecha() {
			return fecha;
		}

		public void setFecha(String fecha) {
			this.fecha = fecha;
		}

		public String getHora() {
			return hora;
		}

		public void setHora(String hora) {
			this.hora = hora;
		}

		public String getIdOperacion() {
			return idOperacion;
		}

		public void setIdOperacion(String idOperacion) {
			this.idOperacion = idOperacion;
		}

		public String getVoucher() {
			return voucher;
		}

		public void setVoucher(String voucher) {
			this.voucher = voucher;
		}

		public String getTpvOriginal() {
			return tpvOriginal;
		}

		public void setTpvOriginal(String tpvOriginal) {
			this.tpvOriginal = tpvOriginal;
		}
		
		
    }
    
    public static class Query {
    	protected String operacion;
    	protected String empresa;
    	protected String centro;
    	protected String tpv;
    	protected String fecha;
    	protected String hora;
    	protected String soporte;
    	protected String documento;
    	protected String fechaCad;
    	protected String importe;
    	protected String moneda;
    	protected String plazos;
    	protected String securityCode;
    	protected String refCliente;
    	protected String infoAdicionalEntrada;
    	protected String infoAdicionalSalida;
    	protected String CF_ReplyURL;
    	protected String CF_ReplyURLAuth;
    	protected String CF_MAC;
    	protected String flagAltaToken;
    	protected String flagTestSaldo;
    	protected String observaciones;
    	protected String refTokenCliente;
    	protected String centroOriginal;
    	protected String tpvOriginal;
    	protected String fechaOriginal;
    	protected String idOperacionOriginal;
    	protected String autOriginal;
    	protected String operador;
    	protected String tipoReferencia;
    	protected String refClienteOriginal;
    	protected String horaOriginal;
    	protected String operacionOriginal;
    	protected String importeOriginal;
    	protected String valorDocumento;
    	protected String idOperacion;
    	protected String ACK;
    	protected String token;
    	
		public String getOperacion() {
			return operacion;
		}
		
		public void setOperacion(String operacion) {
			this.operacion = operacion;
		}
		
		public String getEmpresa() {
			return empresa;
		}
		
		public void setEmpresa(String empresa) {
			this.empresa = empresa;
		}
		
		public String getCentro() {
			return centro;
		}
		
		public void setCentro(String centro) {
			this.centro = centro;
		}
		
		public String getTpv() {
			return tpv;
		}
		
		public void setTpv(String tpv) {
			this.tpv = tpv;
		}
		
		public String getFecha() {
			return fecha;
		}
		
		public void setFecha(String fecha) {
			this.fecha = fecha;
		}
		
		public String getHora() {
			return hora;
		}
		
		public void setHora(String hora) {
			this.hora = hora;
		}
		
		public String getSoporte() {
			return soporte;
		}
		
		public void setSoporte(String soporte) {
			this.soporte = soporte;
		}
		
		public String getDocumento() {
			return documento;
		}
		
		public void setDocumento(String documento) {
			this.documento = documento;
		}
		
		public String getFechaCad() {
			return fechaCad;
		}
		
		public void setFechaCad(String fechaCad) {
			this.fechaCad = fechaCad;
		}
		
		public String getImporte() {
			return importe;
		}
		
		public void setImporte(String importe) {
			this.importe = importe;
		}
		
		public String getMoneda() {
			return moneda;
		}
		
		public void setMoneda(String moneda) {
			this.moneda = moneda;
		}
		
		public String getPlazos() {
			return plazos;
		}
		
		public void setPlazos(String plazos) {
			this.plazos = plazos;
		}
		
		public String getSecurityCode() {
			return securityCode;
		}
		
		public void setSecurityCode(String securityCode) {
			this.securityCode = securityCode;
		}
		
		public String getRefCliente() {
			return refCliente;
		}
		
		public void setRefCliente(String refCliente) {
			this.refCliente = refCliente;
		}
		
		public String getInfoAdicionalSalida() {
			return infoAdicionalSalida;
		}
		
		public void setInfoAdicionalSalida(String infoAdicionalSalida) {
			this.infoAdicionalSalida = infoAdicionalSalida;
		}
		
		public String getCF_ReplyURL() {
			return CF_ReplyURL;
		}
		
		public void setCF_ReplyURL(String cF_ReplyURL) {
			CF_ReplyURL = cF_ReplyURL;
		}
		
		public String getCF_ReplyURLAuth() {
			return CF_ReplyURLAuth;
		}
		
		public void setCF_ReplyURLAuth(String cF_ReplyURLAuth) {
			CF_ReplyURLAuth = cF_ReplyURLAuth;
		}
		
		public String getCF_MAC() {
			return CF_MAC;
		}
		
		public void setCF_MAC(String cF_MAC) {
			CF_MAC = cF_MAC;
		}
		
		public String getFlagAltaToken() {
			return flagAltaToken;
		}
		
		public void setFlagAltaToken(String flagAltaToken) {
			this.flagAltaToken = flagAltaToken;
		}
		
		public String getFlagTestSaldo() {
			return flagTestSaldo;
		}
		
		public void setFlagTestSaldo(String flagTestSaldo) {
			this.flagTestSaldo = flagTestSaldo;
		}
		
		public String getObservaciones() {
			return observaciones;
		}
		
		public void setObservaciones(String observaciones) {
			this.observaciones = observaciones;
		}
		
		public String getRefTokenCliente() {
			return refTokenCliente;
		}
		
		public void setRefTokenCliente(String refTokenCliente) {
			this.refTokenCliente = refTokenCliente;
		}
		
		public String getCentroOriginal() {
			return centroOriginal;
		}
		
		public void setCentroOriginal(String centroOriginal) {
			this.centroOriginal = centroOriginal;
		}
		
		public String getFechaOriginal() {
			return fechaOriginal;
		}
		
		public void setFechaOriginal(String fechaOriginal) {
			this.fechaOriginal = fechaOriginal;
		}
		public String getIdOperacionOriginal() {
			return idOperacionOriginal;
		}
		
		public void setIdOperacionOriginal(String idOperacionOriginal) {
			this.idOperacionOriginal = idOperacionOriginal;
		}
		
		public String getAutOriginal() {
			return autOriginal;
		}
		
		public void setAutOriginal(String autOriginal) {
			this.autOriginal = autOriginal;
		}
		
		public String getOperador() {
			return operador;
		}
		
		public void setOperador(String operador) {
			this.operador = operador;
		}
		
		public String getTipoReferencia() {
			return tipoReferencia;
		}
		
		public void setTipoReferencia(String tipoReferencia) {
			this.tipoReferencia = tipoReferencia;
		}
		
		public String getRefClienteOriginal() {
			return refClienteOriginal;
		}
		
		public void setRefClienteOriginal(String refClienteOriginal) {
			this.refClienteOriginal = refClienteOriginal;
		}
		
		public String getHoraOriginal() {
			return horaOriginal;
		}
		
		public void setHoraOriginal(String horaOriginal) {
			this.horaOriginal = horaOriginal;
		}
		
		public String getOperacionOriginal() {
			return operacionOriginal;
		}
		
		public void setOperacionOriginal(String operacionOriginal) {
			this.operacionOriginal = operacionOriginal;
		}
		
		public String getImporteOriginal() {
			return importeOriginal;
		}
		
		public void setImporteOriginal(String importeOriginal) {
			this.importeOriginal = importeOriginal;
		}
		
		public String getIdOperacion() {
			return idOperacion;
		}
		
		public void setIdOperacion(String idOperacion) {
			this.idOperacion = idOperacion;
		}
		
		public String getACK() {
			return ACK;
		}
		
		public void setACK(String aCK) {
			ACK = aCK;
		}
		
		public String getToken() {
			return token;
		}
		
		public void setToken(String token) {
			this.token = token;
		}

		public String getInfoAdicionalEntrada() {
			return infoAdicionalEntrada;
		}

		public void setInfoAdicionalEntrada(String infoAdicionalEntrada) {
			this.infoAdicionalEntrada = infoAdicionalEntrada;
		}

		public String getTpvOriginal() {
			return tpvOriginal;
		}

		public void setTpvOriginal(String tpvOriginal) {
			this.tpvOriginal = tpvOriginal;
		}
		
		public String getValorDocumento() {
			return valorDocumento;
		}
		
		public void setValorDocumento(String valorDocumento) {
			this.valorDocumento = valorDocumento;
		}	
    }
}
