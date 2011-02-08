package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.empresa.IRemesaCertificadoEmpresa;

public class RemesableCertificate implements Serializable {
	
	private static final long serialVersionUID = 4515899308824088082L;

	private boolean selected;
	private IRemesaCertificadoEmpresa remesa;
	private IEmpresa empresa;
	private boolean showEmployees;
	private List<RemesableEmpleadoCertificate> empleadosList;
	
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
		empresa = remesa.getEmpresa();
	}
	public IEmpresa getEmpresa() {
		return empresa;
	}
	public boolean isShowEmployees() {
		return showEmployees;
	}
	public void setShowEmployees(boolean showEmployees) {
		this.showEmployees = showEmployees;
	}
	public List<RemesableEmpleadoCertificate> getEmpleadosList() {
		return empleadosList;
	}
	public void setEmpleadosList(List<RemesableEmpleadoCertificate> empleadosList) {
		this.empleadosList = empleadosList;
	}
	
	public void addEmpleado(RemesableEmpleadoCertificate empleado, boolean addFirst){
		if(getEmpleadosList()==null){
			setEmpleadosList(new LinkedList<RemesableEmpleadoCertificate>());
		}
		if(addFirst){
			getEmpleadosList().add(0,empleado);
		} else {
			getEmpleadosList().add(empleado);
		}
	}
	
	public boolean existEmpleado(RemesableEmpleadoCertificate empleado){
		if(getEmpleadosList()==null){
			return false;
		}
		Integer personId = empleado.getEmpleado().getPersona().getId();
		for(RemesableEmpleadoCertificate e: getEmpleadosList()){
			if(personId.equals(e.getEmpleado().getPersona().getId())){
				return true;
			}
		}
		return false;
	}
	
		
}
