package com.esferalia.aon.gwt.template.server.delivery;

public class AlbvDet {
	
	Integer albv;
	Integer linea;
	String articulo;
	String detalle;
	String detalle2;
	String detalle3;
	String concepto;
	Double cantidad;
	Double precio;
	String descuentos;
	
	public Integer getAlbv() {
		return albv;
	}
	public AlbvDet setAlbv(Integer albv) {
		this.albv = albv;
		return this;
	}
	public Integer getLinea() {
		return linea;
	}
	public AlbvDet setLinea(Integer linea) {
		this.linea = linea;
		return this;
	}
	public String getArticulo() {
		return articulo;
	}
	public AlbvDet setArticulo(String articulo) {
		this.articulo = articulo;
		return this;
	}
	public String getConcepto() {
		return concepto;
	}
	public AlbvDet setConcepto(String concepto) {
		this.concepto = concepto;
		return this;
	}
	public Double getCantidad() {
		return cantidad;
	}
	public AlbvDet setCantidad(Double cantidad) {
		this.cantidad = cantidad;
		return this;
	}
	public Double getPrecio() {
		return precio;
	}
	public AlbvDet setPrecio(Double precio) {
		this.precio = precio;
		return this;
	}
	public String getDescuentos() {
		return descuentos;
	}
	public AlbvDet setDescuentos(String descuentos) {
		this.descuentos = descuentos;
		return this;
	}
	public String getDetalle() {
		return detalle;
	}
	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}
	public String getDetalle2() {
		return detalle2;
	}
	public void setDetalle2(String detalle2) {
		this.detalle2 = detalle2;
	}
	public String getDetalle3() {
		return detalle3;
	}
	public void setDetalle3(String detalle3) {
		this.detalle3 = detalle3;
	}
}
