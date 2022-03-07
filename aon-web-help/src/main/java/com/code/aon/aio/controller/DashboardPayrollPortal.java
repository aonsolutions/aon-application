package com.code.aon.aio.controller;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class DashboardPayrollPortal implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	Double ss;
	Double irpf;
	Double otros;
	Double deduction;

	Double neto;
	
	Double total;
	String mes;
	Integer ano;
	public DashboardPayrollPortal(){
		//Constructor
	}
	public DashboardPayrollPortal(double ss,double irpf, double otros, double neto, double total, String mes, int ano) {
		this.ss= ss;
		this.irpf=irpf;
		this.otros= otros;
		this.neto=neto;
		this.total=total;
		this.mes= mes;
		this.ano=ano;
	}
	
	public Double getSs() {
		return ss;
	}
	public void setSs(Double ss) {
		this.ss = ss;
	}
	public Double getIrpf() {
		return irpf;
	}
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}
	public Double getOtros() {
		return otros;
	}
	public void setOtros(Double otros) {
		this.otros = otros;
	}
	public Double getNeto() {
		return neto;
	}
	public void setNeto(Double neto) {
		this.neto = neto;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public double getDeduction() {
		return deduction;
	}
	public void setDeduction(double deduction) {
		this.deduction = deduction;
	}

}
