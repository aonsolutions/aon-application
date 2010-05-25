package com.esferalia.aon.ui.payroll.controller;

import com.esferalia.aon.payroll.core.IPercepcion;

public class PercepcionEmpleado {
	
	private IPercepcion percepcion;
	private Integer importeNuevo;
	private Integer diferencia;
	private String formula;

	public IPercepcion getPercepcion() {
		return percepcion;
	}
	public void setPercepcion(IPercepcion percepcion) {
		this.percepcion = percepcion;
	}
	public Integer getImporteNuevo() {
		return importeNuevo;
	}
	public void setImporteNuevo(Integer importeNuevo) {
		this.importeNuevo = importeNuevo;
	}
	public Integer getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(Integer diferencia) {
		this.diferencia = diferencia;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}

}
