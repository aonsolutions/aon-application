package com.code.aon.ui.loader.pojo;

import java.util.Date;

import com.code.aon.accounting.enumeration.AccountEntryType;

public class LoadedAccountEntry implements ILoadedPojo{

	private Integer id;
	private Integer diario;
	private Date fecha;
	private Integer tipoAsiento;
	private String comentario;
	
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
	
}
