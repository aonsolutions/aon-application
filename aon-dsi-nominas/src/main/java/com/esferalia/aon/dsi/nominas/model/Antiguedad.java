package com.esferalia.aon.dsi.nominas.model;

public class Antiguedad {
	
	private String anos;
	private double importe;
	private String tipo;
	
	public String getAnos() {
		return anos;
	}
	public Antiguedad setAnos(String anos) {
		this.anos = anos;
		return this;
	}
	public double getImporte() {
		return importe;
	}
	public Antiguedad setImporte(double importe) {
		this.importe = importe;
		return this;
	}
	public String getTipo() {
		return tipo;
	}
	public Antiguedad setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}
	
	@Override
	public String toString() {
		return "Antiguedad [anos=" + anos + ", importe=" + importe + ", tipo=" + tipo + "]";
	}

}
