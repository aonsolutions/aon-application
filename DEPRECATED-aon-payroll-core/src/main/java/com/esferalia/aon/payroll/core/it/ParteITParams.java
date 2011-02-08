package com.esferalia.aon.payroll.core.it;

public class ParteITParams {

	private String empleadoID;
	private boolean alta;
	private boolean baja;
	private boolean confirmacion;

	public String getEmpleadoID() {
		return empleadoID;
	}

	public void setEmpleadoID(String empleadoID) {
		this.empleadoID = empleadoID;
	}

	public boolean isAlta() {
		return alta;
	}
	public void setAlta(boolean alta) {
		this.alta = alta;
	}

	public boolean isBaja() {
		return baja;
	}
	public void setBaja(boolean baja) {
		this.baja = baja;
	}

	public boolean isConfirmacion() {
		return confirmacion;
	}
	public void setConfirmacion(boolean confirmacion) {
		this.confirmacion = confirmacion;
	}
	
}
