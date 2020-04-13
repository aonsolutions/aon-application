package com.esferalia.aon.in.payroll.pdf;

import java.io.IOException;

import com.esferalia.aon.salary.ISalaryBuilder;

public interface SalaryPDFTemplate {
	
	public  SalaryPDFTemplate parse( String text, ISalaryBuilder<?> handler) throws IOException, UnknownPDFException ;

}
