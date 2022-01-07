package com.esferalia.aon.ui.payroll.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.person.Person;

public class EnterpriseSalaryReportController implements Serializable {

	private Date endDate;
	private Date startDate;
	private Person person;
	private String[] salaryTypes;
	private boolean groupByPerson;
	
	
	
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String[] getSalaryTypes() {
		return salaryTypes;
	}

	public void setSalaryTypes(String[] salaryTypes) {
		this.salaryTypes = salaryTypes;
	}

	public boolean isGroupByPerson() {
		return groupByPerson;
	}

	public void setGroupByPerson(boolean groupByPerson) {
		this.groupByPerson = groupByPerson;
	}

	public String onPDF() throws IOException {
		HttpServletResponse response = DownloadUtil.getResponse();
		try ( OutputStream out = DownloadUtil.initDownload(response, "helloWorld.txt", MimeType.MIME_TXT) ) {
			new PrintStream(out).printf("Fecha Inicio: %s\r\n Fecha Fin: %s", startDate.toLocaleString(), endDate.toLocaleString() );
			out.flush();
		}
		return null;
	}
	
	
	public String onExcel() throws IOException {
		return null;
	}
	
}
