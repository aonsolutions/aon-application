package com.code.aon.ui.loader.pojo;

import java.util.Date;

import com.code.aon.common.enumeration.Country;
import com.code.aon.registry.enumeration.DocumentType;

public class LoadedFinance implements ILoadedPojo, ILoadedDocumentHolder{

	public Integer id;
	public Integer factura;
	public Integer idTitular;
	public String cuenta;
	public String documento;
	public Integer tipoDocumento;
	public String paisDocumento;
	public String razonSocial;
	public String concepto;
	public Date fechaVto;
	public Integer pago;
	public String formaPago;
	public String cuentaBanco;
	public Double importe;
	
	@Override
	public String getIdentifier() {
		return id==null?null:id.toString();
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getFactura() {
		return factura;
	}
	public void setFactura(Integer factura) {
		this.factura = factura;
	}
	public Integer getIdTitular() {
		return idTitular;
	}
	public void setIdTitular(Integer idTitular) {
		this.idTitular = idTitular;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getDocumento() {
		return documento;
	}
	public void setDocumento(String documento) {
		this.documento = documento;
	}
	public Integer getTipoDocumento() {
		return tipoDocumento;
	}
	public DocumentType getDocumentType() {
		return DocumentType.values()[getTipoDocumento()];
	}
	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getPaisDocumento() {
		return paisDocumento;
	}
	public Country getDocumentCountry() {
		return Country.valueOf( getPaisDocumento());
	}
	public void setPaisDocumento(String paisDocumento) {
		this.paisDocumento = paisDocumento;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public Integer getPago() {
		return pago;
	}
	public void setPago(Integer pago) {
		this.pago = pago;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public Date getFechaVto() {
		return fechaVto;
	}
	public void setFechaVto(Date fechaVto) {
		this.fechaVto = fechaVto;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getCuentaBanco() {
		return cuentaBanco;
	}
	public void setCuentaBanco(String cuentaBanco) {
		this.cuentaBanco = cuentaBanco;
	}
	public Double getImporte() {
		return importe;
	}
	public void setImporte(Double importe) {
		this.importe = importe;
	}
	
	public LoadedCustomer getLoadedCustomer() {
		LoadedCustomer loadedCustomer = new LoadedCustomer();
		fillLoadedRegistry(loadedCustomer);
		loadedCustomer.setCuenta(getCuenta());
		return loadedCustomer;
	}
	public LoadedSupplier getLoadedSupplier() {
		LoadedSupplier loadedSupplier = new LoadedSupplier();
		fillLoadedRegistry(loadedSupplier);
		loadedSupplier.setCuenta(getCuenta());
		return loadedSupplier;
	}
	public LoadedCreditor getLoadedCreditor() {
		LoadedCreditor loadedCreditor = new LoadedCreditor();
		fillLoadedRegistry(loadedCreditor);
		loadedCreditor.setCuenta(getCuenta());
		return loadedCreditor;
	}
	private void fillLoadedRegistry(LoadedRegistry loadedRegistry) {
		loadedRegistry.setDocumento(getDocumento());
		loadedRegistry.setPaisDocumento(getPaisDocumento());
		loadedRegistry.setTipoDocumento(getTipoDocumento());
		loadedRegistry.setRazonSocial(getRazonSocial());
		loadedRegistry.setFormaPago(getFormaPago());
		loadedRegistry.setCuentaBanco(getCuentaBanco());
		loadedRegistry.setId(getIdTitular());
	}
}
