package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.it.IParteConfirmacionIT;
import com.esferalia.aon.payroll.core.it.IParteIT;

public class RemesableIT implements Serializable {
	
	private static final long serialVersionUID = 422693852102440685L;
	
	private boolean selected;
	private IParteIT parteIT;
	private IParteConfirmacionIT confirmacionIT;

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public IParteIT getParteIT() {
		return parteIT;
	}
	public void setParteIT(IParteIT parteIT) {
		this.parteIT = parteIT;
	}
	
	public IParteConfirmacionIT getConfirmacionIT() {
		return confirmacionIT;
	}
	public void setConfirmacionIT(IParteConfirmacionIT confirmacionIT) {
		this.confirmacionIT = confirmacionIT;
	}
	
	public boolean isBaja() {
		return (getParteIT().getFechaAlta() == null);
	}
	public boolean isAlta() {
		return (getParteIT().getFechaAlta() != null && getConfirmacionIT() == null);
	}
	public boolean isConfirmacion() {
		return (getParteIT().getFechaAlta() != null && getConfirmacionIT() != null);
	}
	
}
