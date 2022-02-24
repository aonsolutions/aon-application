package com.esferalia.aon.dsi.nominas.model;

import java.util.LinkedList;

import com.esferalia.aon.dsi.nominas.Traspaso;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Categoria {
	
	private String codcat;
	private String nomcat;
	private double horsem;
	private String tipoAntig;  // 1-Acumulativa, 2-Fija
	
	private LinkedList<Concepto> conceptos;
	private LinkedList<Paga> pagas;
	private LinkedList<Antiguedad> antiguedades;	
	
	public String getCodcat() {
		return codcat;
	}
	public Categoria setCodcat(String codigo) {
		this.codcat = codigo;
		return this;
	}
	public String getNomcat() {
		return nomcat;
	}
	public Categoria setNomcat(String nombre) {
		this.nomcat = nombre;
		return this;
	}
	public double getHorsem() {
		return horsem;
	}
	public Categoria setHorsem(double horas) {
		this.horsem = horas;
		return this;
	}
	public String getTipoAntig() {
		return tipoAntig;
	}
	public Categoria setTipoAntig(String tipoAntig) {
		this.tipoAntig = tipoAntig;
		return this;
	}
	public LinkedList<Concepto> getConceptos() {
		if (conceptos == null) {
			conceptos = new LinkedList<Concepto>();
		}
		return conceptos;
	}
	public Categoria setConceptos(LinkedList<Concepto> conceptos) {
		this.conceptos = conceptos;
		return this;
	}
	public LinkedList<Paga> getPagas() {
		if (pagas == null) {
			pagas = new LinkedList<Paga>();
		}
		return pagas;
	}
	public Categoria setPagas(LinkedList<Paga> pagas) {
		this.pagas = pagas;
		return this;
	}
	public LinkedList<Antiguedad> getAntiguedades() {
		if (antiguedades == null) {
			antiguedades = new LinkedList<Antiguedad>();
		}
		return antiguedades;
	}
	public Categoria setAntiguedades(LinkedList<Antiguedad> antiguedades) {
		this.antiguedades = antiguedades;
		return this;
	}
	
	// Busca la paga extra del mes que se le pasa
	public Paga buscarPaga(String mes) {		
		for(Paga paga : this.pagas) {
			if (mes.equals(paga.getMes()))
				return paga;
		}
		return null;
	}
	
	// Comprueba si existe el concepto (según su clave), en los conceptos salariales de la categoria
	public boolean existeConcepto(String clave) {		
		for(Concepto concepto : this.conceptos) {
			if (clave.equals(concepto.getClave()))
				return true;
		}
		return false;
	}
	
	// Formula para la antiguedad en AON, segun la tabla de antiguedad de Omega
	// - Si la tabla tiene una sola linea, formula = ANTIGÜEDAD(IMPORTE_ANTIGUEDAD, periodo)
	// - En caso contrario formula if else años e importe, formula = (AÑOS_ANTIGUEDAD >= año_n ? importe_n : AÑOS_ANTIGUEDAD >= año_n-1 ? importe_n-1 : ... : AÑOS_ANTIGUEDAD >= año1 ? importe1 : 0.0)                
	public String getAonFormulaAntiguedad() {		
		String formula = "";
		if (getAntiguedades().size() == 1) {
			if (AonStringUtils.isNotBlank(getAntiguedades().get(0).getAnos()) && AonStringUtils.isNotBlank(getAntiguedades().get(0).getTipo()))
				formula = "ANTIGÜEDAD(IMPORTE_ANTIGUEDAD, " + Traspaso.getPeriod(getAntiguedades().get(0).getAnos()) + ")"; 
		} else if (getAntiguedades().size() > 1) {

			// Se hace la formula con if else AÑOS_ANTIGUEDAD, leyendo la tabla en orden inverso
			// AÑOS_ANTIGUEDAD >= años_n ? importe_n : AÑOS_ANTIGUEDAD >= años_n-1 ? importe_n-1 :, ... : 0
			formula = "";
			for (int i = getAntiguedades().size()-1; i >= 0; i--) {
				Antiguedad antig = getAntiguedades().get(i);
				formula = formula + "AÑOS_ANTIGUEDAD >= " + Integer.parseInt(antig.getAnos()) + " ? ";
				if ("P".equals(antig.getTipo())) 
					formula = formula + (antig.getImporte() / 100);
				else
					formula = formula + antig.getImporte();
				formula = formula + " : ";				
			}			
			if (!formula.isEmpty())
				formula = "(" + formula + "0.0)";
			
		}
		return formula.trim();
	}
	
	// Expresion para el concepto de la tabla de antigüedad en AON
	public String getAonSeniorityExpression() {
		String expression = getAonFormulaAntiguedad();
		if (!expression.isEmpty()) {
			if (expression.contains("IMPORTE_ANTIGUEDAD")) {
				// Tabla de antiguedad de una sola línea				
				switch (getTipoCobroAntiguedad()) {
					case "M":
						expression = expression.replace("IMPORTE_ANTIGUEDAD", getAonSeniorityVariable()) + " * DIAS_TRABAJADOS / DIAS_MES";	
						break;
					case "D":
						expression = expression.replace("IMPORTE_ANTIGUEDAD", getAonSeniorityVariable()) + " * DIAS_TRABAJADOS";	
						break;
					case "P":
						expression = expression.replace("IMPORTE_ANTIGUEDAD", getAonSeniorityVariable() + " * SALARIO_BASE");	
						break;
					default:
						break;
				}
			}
			else {
				// Tabla de antiguedad de más de una línea				
				switch (getTipoCobroAntiguedad()) {
					case "M":
						expression = getAonSeniorityVariable() + " * DIAS_TRABAJADOS / DIAS_MES";	
						break;
					case "D":
						expression = getAonSeniorityVariable() + " * DIAS_TRABAJADOS";	
						break;
					case "P":
						expression = getAonSeniorityVariable() + " * SALARIO_BASE";	
						break;
					default:
						break;
				}							
			}
		}
		return expression;
	}
	
	// Variable para la tabla de antiguedad en AON
	// - Si tabla de una sola linea, variable = "IMPORTE_ANTIGUEDAD_" + tipoCobro + "_" + años
	// - En caso contrario, variable = "IMPORTE_ANTIGUEDAD_" + tipoCobro
	public String getAonSeniorityVariable() {
		String variable = "";
		String formula = getAonFormulaAntiguedad();
		if (!formula.isEmpty()) {		
			if (formula.contains("IMPORTE_ANTIGUEDAD")) {
				variable = "IMPORTE_ANTIGUEDAD_" + getTipoCobroAntiguedad() + "_" + getAntiguedades().get(0).getAnos(); 
			}
			else {
				variable = "IMPORTE_ANTIGUEDAD_" + getTipoCobroAntiguedad();
			}
		}
		return variable;
	}
	
	// Tipo de Cobro de la tabla de antiguedad, se asume que todas las líneas de la tabla de antiguedad tienen el mismo tipo de cobro (P, D o M)
	private String getTipoCobroAntiguedad() {
		for (Antiguedad antig : getAntiguedades()) {
			if (AonStringUtils.isNotBlank(antig.getAnos()) && AonStringUtils.isNotBlank(antig.getTipo())) {
				return antig.getTipo();				
			}
		}
		return "";
	}
	
	@Override
	public String toString() {
		return "Categoria [codcat=" + codcat + ", nomcat=" + nomcat + "]";
//		return "Categoria [codcat=" + codcat + ", nomcat=" + nomcat + ", horsem=" + horsem + ", tipoAntig=" + tipoAntig
//				+ ", conceptos=" + conceptos + ", pagas=" + pagas + ", antiguedades=" + antiguedades + "]";
	}
	
}
