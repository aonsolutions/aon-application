package com.esferalia.aon.dsi.nominas.model;

// Complementos por Enfermedad y Accidente
public class Complemento {
	
	private String tipo;
	private int porcentaje;
	private int diaDesde;
	private int diaHasta;
	private String calculo;
	
	public String getTipo() {
		return tipo;
	}
	public Complemento setTipo(String tipo) {
		this.tipo = tipo;
		return this;
	}
	public int getPorcentaje() {
		return porcentaje;
	}
	public Complemento setPorcentaje(int porcentaje) {
		this.porcentaje = porcentaje;
		return this;
	}
	public int getDiaDesde() {
		return diaDesde;
	}
	public Complemento setDiaDesde(int diaDesde) {
		this.diaDesde = diaDesde;
		return this;
	}
	public int getDiaHasta() {
		return diaHasta;
	}
	public Complemento setDiaHasta(int diaHasta) {
		this.diaHasta = diaHasta;
		return this;
	}
	public String getCalculo() {
		return calculo;
	}
	public Complemento setCalculo(String calculo) {
		this.calculo = calculo;
		return this;
	}
	
	// Code para payment_concept de AON, siempre "GARANTIZADO"
	public String getAonCode() {
		return "GARANTIZADO";
	}
	
	// Descripcion para los conceptos de AON de los complementos enf/acc de Omega
	public String getAonDescription() {
		if ("E".equals(this.tipo))
			return "MEJORAS PREST.SS.ENFERMEDAD COMUN";
		else if ("A".equals(this.tipo))
			return "MEJORAS PREST.SS.ENFERMEDAD PROFESIONAL";
		else return "";
	}
	
	// Clave CRA en formato numérico para los conceptos de los complementos E/A (siempre 55)
	public byte getAonClaveCRA() {
		return 55;
	}
	
	// Expresion para los conceptos de AON de los complementos enf/acc de Omega
	public String getAonExpression(String conceptosEnf, String conceptosAcc) {
		
		int diaDesde = this.diaDesde;
		if (diaDesde == 0)
			diaDesde = 1;
		
		int diaHasta = this.diaHasta;
		if (diaHasta == 0)
			diaHasta = 999;
		
		String formulaCalculo = "";
		if ("S".equals(this.calculo)) {
			formulaCalculo = "(" + ("A".equals(this.tipo) ? conceptosAcc : conceptosEnf) + ")" + (this.porcentaje < 100 ? " * " + this.porcentaje/100.0 : "");
		}
		else if ("B".equals(this.calculo)) {
			formulaCalculo = "BASE_REGULADORA" + (this.porcentaje < 100 ? " * " + this.porcentaje/100.0 : "");
		}		
		
		// Complemento por Enfermedad
		if ("E".equals(this.tipo)) {
			return "isdef DIAS_ENFERMEDAD_COMUN ? /*user*/GTZDO(" + formulaCalculo + ", " + diaDesde + ", " + diaHasta + ")/**/ : HIDE()";
		} 
		// Complemento por Accidente
		else if ("A".equals(this.tipo)) {
			return "isdef DIAS_ENFERMEDAD_PROFESIONAL ? /*user*/GTZDO(" + formulaCalculo + ", " + diaDesde + ", " + diaHasta + ")/**/ : HIDE()";
		}
		else return "";
		
	}
	
	
	@Override
	public String toString() {
		return "Complemento [tipo=" + tipo + ", porcentaje=" + porcentaje + ", diaDesde=" + diaDesde + ", diaHasta="
				+ diaHasta + ", calculo=" + calculo + "]";
	}

}
