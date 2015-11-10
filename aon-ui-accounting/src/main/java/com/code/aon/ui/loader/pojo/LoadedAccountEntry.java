package com.code.aon.ui.loader.pojo;

import java.util.Date;

import com.code.aon.accounting.enumeration.AccountEntryType;

public class LoadedAccountEntry implements ILoadedPojo{

	private Integer id;
	private Integer diario;
	private Date fecha;
	private Integer tipoAsiento;
	private String comentario;
	
	private Integer enlaceFactura;
	private String enlaceSerie;
	private Integer enlaceNumero;
	private Integer enlaceTipoFactura;
	
	
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
	public Integer getDiario() {
		return diario;
	}
	public void setDiario(Integer diario) {
		this.diario = diario;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Integer getTipoAsiento() {
		return tipoAsiento;
	}
	public AccountEntryType getEntryType() {
		return tipoAsiento==null?AccountEntryType.MANUAL:AccountEntryType.values()[tipoAsiento];
	}
	public void setTipoAsiento(Integer tipoAsiento) {
		this.tipoAsiento = tipoAsiento;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public Integer getEnlaceFactura() {
		return enlaceFactura;
	}

	public void setEnlaceFactura(Integer enlaceFactura) {
		this.enlaceFactura = enlaceFactura;
	}

	public String getEnlaceSerie() {
		return enlaceSerie;
	}

	public void setEnlaceSerie(String enlaceSerie) {
		this.enlaceSerie = enlaceSerie;
	}

	public Integer getEnlaceNumero() {
		return enlaceNumero;
	}

	public void setEnlaceNumero(Integer enlaceNumero) {
		this.enlaceNumero = enlaceNumero;
	}

	public Integer getEnlaceTipoFactura() {
		return enlaceTipoFactura;
	}

	public void setEnlaceTipoFactura(Integer enlaceTipoFactura) {
		this.enlaceTipoFactura = enlaceTipoFactura;
	}
	
}
