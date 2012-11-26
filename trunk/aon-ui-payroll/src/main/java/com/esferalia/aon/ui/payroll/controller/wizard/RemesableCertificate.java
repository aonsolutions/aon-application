package com.esferalia.aon.ui.payroll.controller.wizard;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.company.Enterprise;
import com.esferalia.aon.payroll.Certifica2Batch;

public class RemesableCertificate implements Serializable {
	
	private static final long serialVersionUID = -9172369329057959309L;

	private static final long DAY_IN_MILISECONDS = 1*24*60*60*1000;

	private boolean selected;
	private Certifica2Batch certificate;
	private Enterprise enterprise;
	private boolean showEmployees;
	private List<RemesableEmpleadoCertificate> remesableList;
	
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Certifica2Batch getCertificate() {
		return certificate;
	}
	public void setCertificate(Certifica2Batch certificate) {
		this.certificate = certificate;
		enterprise = certificate.getEnterprise();
	}
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	public boolean isShowEmployees() {
		return showEmployees;
	}
	public void setShowEmployees(boolean showEmployees) {
		this.showEmployees = showEmployees;
	}
	public List<RemesableEmpleadoCertificate> getRemesableList() {
		return remesableList;
	}
	public void setRemesableList(List<RemesableEmpleadoCertificate> remesableList) {
		this.remesableList = remesableList;
	}
		
	public void addEmployee(RemesableEmpleadoCertificate remesable, boolean addFirst){
		if(getRemesableList()==null){
			setRemesableList(new LinkedList<RemesableEmpleadoCertificate>());
		}
		if(!existEmployee(remesable)){
			if(addFirst){
				getRemesableList().add(0,remesable);
			} else {
				getRemesableList().add(remesable);
			}
		}
		if(getCertificate().getDate()==null || remesable.getContract().getEndDate().after(getCertificate().getDate())){
			if(remesable.getContract().getEndDate().before(Calendar.getInstance().getTime())){
				getCertificate().setDate(remesable.getContract().getEndDate());
			} else {
				getCertificate().setDate(new Date(remesable.getContract().getEndDate().getTime()+DAY_IN_MILISECONDS));
			}
		}
	}
	
	public boolean existEmployee(RemesableEmpleadoCertificate remesable){
		if(getRemesableList()==null){
			return false;
		}
		Integer personId = remesable.getContract().getPerson().getId();
		for(RemesableEmpleadoCertificate e: getRemesableList()){
			if(personId.equals(e.getContract().getPerson().getId())){
				return true;
			}
		}
		return false;
	}
	
		
}
