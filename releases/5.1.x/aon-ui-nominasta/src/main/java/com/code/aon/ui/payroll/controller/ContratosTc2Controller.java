package com.code.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.payroll.auxiliares.contratos.ContratosTc2;


public class ContratosTc2Controller extends PayrollBasicController {
	
	private List<ContratosTc2> reportList;
	public List<ContratosTc2> getReportList() {
		reportList = new LinkedList<ContratosTc2>();
		reportList.add((ContratosTc2) getTo());
		return reportList;
	}

	public void setReportList(List<ContratosTc2> reportList) {
		this.reportList = reportList;
	}

}
