package com.code.aon.ui.loader.pojo;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.enumeration.DocumentType;


public class LoadedRegistry  implements ILoadedPojo, ILoadedDocumentHolder{

	public Integer id;
	public String razonSocial;
	public String alias;
	public Integer tipoDocumento;
	public String paisDocumento;
	public String documento;
	public String nacionalidad;
	
	public String tipoVia;
	public String direccion;
	public String numero;
	public String direccion2;
	public String direccion3;
	public String cp;
	public String ciudad;
	public String provincia;
	public String nombreProvincia;
	public String pais;
	
	public String telefono1;
	public String telefono2;
	public String fax;
	public String email;
	public String web;
	
	public String banco;
	public String cuentaBanco;
	
	public String formaPago;
	public Integer numeroVtos;
	public Integer diasAlPrimerVto;
	public Integer diasEntreVtos;
	public String diasPago;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Integer getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getPaisDocumento() {
		return paisDocumento;
	}
	public void setPaisDocumento(String paisDocumento) {
		this.paisDocumento = paisDocumento;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public String getNacionalidad() {
		return nacionalidad;
	}
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	public String getTipoVia() {
		return tipoVia;
	}
	public void setTipoVia(String tipoVia) {
		this.tipoVia = tipoVia;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getDireccion2() {
		return direccion2;
	}
	public void setDireccion2(String direccion2) {
		this.direccion2 = direccion2;
	}
	public String getDireccion3() {
		return direccion3;
	}
	public void setDireccion3(String direccion3) {
		this.direccion3 = direccion3;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getNombreProvincia() {
		return nombreProvincia;
	}
	public void setNombreProvincia(String nombreProvincia) {
		this.nombreProvincia = nombreProvincia;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getTelefono1() {
		return telefono1;
	}
	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}
	public String getTelefono2() {
		return telefono2;
	}
	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getCuentaBanco() {
		return cuentaBanco;
	}
	public void setCuentaBanco(String cuentaBanco) {
		this.cuentaBanco = cuentaBanco;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public Integer getNumeroVtos() {
		return numeroVtos;
	}
	public void setNumeroVtos(Integer numeroVtos) {
		this.numeroVtos = numeroVtos;
	}
	public Integer getDiasAlPrimerVto() {
		return diasAlPrimerVto;
	}
	public void setDiasAlPrimerVto(Integer diasAlPrimerVto) {
		this.diasAlPrimerVto = diasAlPrimerVto;
	}
	public Integer getDiasEntreVtos() {
		return diasEntreVtos;
	}
	public void setDiasEntreVtos(Integer diasEntreVtos) {
		this.diasEntreVtos = diasEntreVtos;
	}
	public String getDiasPago() {
		return diasPago;
	}
	public void setDiasPago(String diasPago) {
		this.diasPago = diasPago;
	}
	
	public DocumentType getDocumentType() {
		if (getTipoDocumento() != null) {
			return DocumentType.values()[getTipoDocumento()];	
		}
		return null;
	}
	public Country getDocumentCountry() {
		if (StringUtils.isNotBlank(getPaisDocumento())) {
			return Country.valueOf( getPaisDocumento());	
		}
		return null;
	}
	public Country getNationality() {
		if (StringUtils.isNotBlank(getNacionalidad())) {
			return Country.valueOf( getNacionalidad());
		}
		return null;
	}
	@Override
	public String getIdentifier() {
		return getId() == null?null:getId().toString();
	}
	
}
