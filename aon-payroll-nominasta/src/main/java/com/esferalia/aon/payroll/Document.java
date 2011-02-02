package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.esferalia.aon.core.IDocument;

@Embeddable
public class Document implements IDocument {

	private static final long serialVersionUID = 8951268005779548912L;
	
	String tipo;
	String pais;
	String value;
	
	@Override
	@Column(name="inddoc", length=1)
	public String getTipo() {
		return tipo;
	}
	@Override
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	@Override
	@Column(name="paiemi", length=3)
	public String getPais() {
		return pais;
	}
	@Override
	public void setPais(String pais) {
		this.pais = pais;
	}
	
	@Override
	@Column(name="numdoc", nullable=false, length=10)
	public String getValue() {
		return value;
	}
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean validate() {
		return true;
	}

}
