package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresaDetalle;

public class RemesableCertificate implements Serializable {
	
	private static final long serialVersionUID = 4515899308824088082L;

	private boolean selected;
	private IRemesaCertificadoEmpresa remesa;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public IRemesaCertificadoEmpresa getRemesa() {
		return remesa;
	}
	public void setRemesa(IRemesaCertificadoEmpresa remesa) {
		this.remesa = remesa;
	}
		
}
