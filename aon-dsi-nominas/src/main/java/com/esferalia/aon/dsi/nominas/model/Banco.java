package com.esferalia.aon.dsi.nominas.model;

public class Banco {

	private String codigo;
	private String nombre;
	private String iban;
	private String bic;
	private String sufijo;
	private String cuentaContable;
	
	public String getCodigo() {
		return codigo;
	}
	public Banco setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	public String getNombre() {
		return nombre;
	}
	public Banco setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public Banco setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getBic() {
		return bic;
	}
	public Banco setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public String getSufijo() {
		return sufijo;
	}
	public Banco setSufijo(String sufijo) {
		this.sufijo = sufijo;
		return this;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public Banco setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
		return this;
	}
	
	@Override
	public String toString() {
		return "Banco [codigo=" + codigo + ", nombre=" + nombre + ", iban=" + iban + ", bic=" + bic + ", sufijo="
				+ sufijo + ", cuentaContable=" + cuentaContable + "]";
	}
	
}
