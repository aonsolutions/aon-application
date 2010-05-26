package com.esferalia.aon.ui.payroll.controller;

import com.esferalia.aon.payroll.core.IPercepcion;

public class PercepcionEmpleado {
	
	private IPercepcion percepcion;
	private Double importeNuevo;
//	private Double diferencia;
	private String formula;

	public IPercepcion getPercepcion() {
		return percepcion;
	}
	public void setPercepcion(IPercepcion percepcion) {
		this.percepcion = percepcion;
	}
	public Double getImporteNuevo() {
		return importeNuevo;
	}
	public void setImporteNuevo(Double importeNuevo) {
		this.importeNuevo = importeNuevo;
	}
	public Double getDiferencia() {
		return getImporteNuevo()-getPercepcion().getImporte();
//		return diferencia;
	}
//	public void setDiferencia(Double diferencia) {
//		this.diferencia = diferencia;
//	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}

}
