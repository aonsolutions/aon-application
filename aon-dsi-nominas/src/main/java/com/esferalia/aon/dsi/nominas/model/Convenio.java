package com.esferalia.aon.dsi.nominas.model;

import java.util.Date;
import java.util.LinkedList;

public class Convenio {
	
	private String codigo;
	private String nombre;
	private String tc2conv;
	private Date fecha;
	private double horsem;
	private String tipoAntig;  // 1-Acumulativa, 2-Fija
	private String tipoComple; // 1-Complemento, 2-Compensacion

	private LinkedList<Concepto> conceptos;
	private LinkedList<Paga> pagas;
	private LinkedList<Antiguedad> antiguedades;
	private LinkedList<Complemento> complementos;
	
	private LinkedList<Categoria> categorias;
	
	public String getCodigo() {
		return codigo;
	}
	public Convenio setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	public String getNombre() {
		return nombre;
	}
	public Convenio setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public String getTc2conv() {
		return tc2conv;
	}
	public Convenio setTc2conv(String numero) {
		this.tc2conv = numero;
		return this;
	}
	public Date getFecha() {
		return fecha;
	}
	public Convenio setFecha(Date fecha) {
		this.fecha = fecha;
		return this;
	}
	public double getHorsem() {
		return horsem;
	}
	public Convenio setHorsem(double horas) {
		this.horsem = horas;
		return this;
	}	
	public String getTipoAntig() {
		return tipoAntig;
	}
	public Convenio setTipoAntig(String tipoAntig) {
		this.tipoAntig = tipoAntig;
		return this;
	}
	public String getTipoComple() {
		return tipoComple;
	}
	public Convenio setTipoComple(String tipoComple) {
		this.tipoComple = tipoComple;
		return this;
	}
	
	public LinkedList<Concepto> getConceptos() {
		if (conceptos == null) {
			conceptos = new LinkedList<Concepto>();
		}
		return conceptos;
	}
	public Convenio setConceptos(LinkedList<Concepto> conceptos) {
		this.conceptos = conceptos;
		return this;
	}
	public LinkedList<Paga> getPagas() {
		if (pagas == null) {
			pagas = new LinkedList<Paga>();
		}
		return pagas;
	}
	public Convenio setPagas(LinkedList<Paga> pagas) {
		this.pagas = pagas;
		return this;
	}	
	public LinkedList<Antiguedad> getAntiguedades() {
		if (antiguedades == null) {
			antiguedades = new LinkedList<Antiguedad>();
		}
		return antiguedades;
	}
	public Convenio setAntiguedades(LinkedList<Antiguedad> antiguedades) {
		this.antiguedades = antiguedades;
		return this;
	}	
	public LinkedList<Complemento> getComplementos() {
		if (complementos == null) {
			complementos = new LinkedList<Complemento>();		
		}		
		return complementos;
	}
	public Convenio setComplementos(LinkedList<Complemento> complementos) {
		this.complementos = complementos;
		return this;
	}
	
	public LinkedList<Categoria> getCategorias() {
		return categorias;
	}
	public Convenio setCategorias(LinkedList<Categoria> categorias) {
		this.categorias = categorias;
		return this;
	}
	
	// Comprueba si existe el concepto (según su clave), en los conceptos salariales del convenio
	public boolean existeConcepto(String clave) {		
		for(Concepto concepto : this.conceptos) {
			if (clave.equals(concepto.getClave()))
				return true;
		}
		return false;
	}
	
	// Comprueba si existe la paga extra (según el mes), en las pagas extras del convenio
	public boolean existePaga(String mes) {		
		for(Paga paga : this.pagas) {
			if (mes.equals(paga.getMes()))
				return true;
		}
		return false;
	}
	
	// Comprueba si la paga que se le pasa es distinta (dias o importe / prorrateada o no) en las categorias del convenio
	public boolean esPagaDistinta(String mes) {		
		double importeControl = 0;
		String tipoControl = null;
		for (Categoria categoria : this.getCategorias()) {
			Paga pagaCat = categoria.buscarPaga(mes);
			// Comprobar si la paga es por dias o importe, por defecto se asume que es por importe
			double impPaga = 100;  			
			if (pagaCat != null && pagaCat.esPorDias()) {				
				impPaga = pagaCat.getImporte();
			}			
			if (importeControl == 0)
				importeControl = impPaga;
			String tipo = "";
			if (pagaCat != null && "P".equals(pagaCat.getTipo()))			
			   tipo = "P";
			if (tipoControl == null) 
				tipoControl = tipo;
			// Comprobar si la paga es distinta de la misma de las anteriores categorias 
			if (importeControl != impPaga || !tipoControl.equals(tipo))
				return true;
		}
		return false;
	}
	
	// Comprueba si la tabla de antiguedad es distinta en las categorias del convenio
	public boolean esAntiguedadDistinta() {
		String formulaAntiguedad = null;
		for (Categoria categoria : getCategorias()) {
				if (formulaAntiguedad == null)
					formulaAntiguedad = categoria.getAonFormulaAntiguedad();
				else if (!formulaAntiguedad.equals(categoria.getAonFormulaAntiguedad()))
					return true;
				formulaAntiguedad = categoria.getAonFormulaAntiguedad();
		}
		return false;
	}
	
	// Devuelve la longitud máxima de la formula de la antiguedad de las categorias
	public int maximaLongitudAntiguedad() {
		int longitud = 0;
		for (Categoria categoria : getCategorias()) {			
			String formulaAntiguedad = categoria.getAonFormulaAntiguedad();
			if (formulaAntiguedad.length() > 128)
				formulaAntiguedad = formulaAntiguedad.replace(" ", "");
			if (formulaAntiguedad.length() > longitud)
				longitud = formulaAntiguedad.length();			
		}
		return longitud;		
	}
	
	@Override
	public String toString() {
		return "Convenio [codigo=" + codigo + ", nombre=" + nombre + "]";
//		return "Convenio [codigo=" + codigo + ", nombre=" + nombre + ", tc2conv=" + tc2conv + ", fecha=" + fecha
//				+ ", horsem=" + horsem + ", tipoAntig=" + tipoAntig + ", tipoComple=" + tipoComple + ", conceptos="
//				+ conceptos + ", pagas=" + pagas + ", antiguedades=" + antiguedades + ", complementos=" + complementos
//				+ "]";
	}
	
}
