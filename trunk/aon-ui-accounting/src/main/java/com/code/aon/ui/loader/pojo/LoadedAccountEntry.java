package com.code.aon.ui.loader.pojo;

import java.util.Date;

import com.code.aon.accounting.enumeration.AccountEntryType;

public class LoadedAccountEntry implements ILoadedPojo{

	private Integer id;
	private Integer diario;
	private Date fecha;
	private Integer tipo;
	private String comentario;
	
	// Sin soporte desde fichero. Todos los apuntes cargados directamente serán manuales.
	private AccountEntryType entryType;
	
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
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public AccountEntryType getEntryType() {
		return entryType;
	}
	public void setEntryType(AccountEntryType entryType) {
		this.entryType = entryType;
	}
	
}
