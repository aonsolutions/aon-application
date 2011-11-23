package com.esferalia.tgc.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.esferalia.tgc.ui.payroll.enumeration.ReportName;


public class TgcReportController {
	private ReportName reportName;

	public ReportName getReportName() {
		return reportName;
	}

	public void setReportName(ReportName reportName) {
		this.reportName = reportName;
	}
	
	public void initialize(ActionEvent event){
		setReportName(null);
	}
}