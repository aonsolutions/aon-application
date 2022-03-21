package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import java.io.IOException;
import java.io.OutputStream;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;

public interface IPayrollTemplate {

	/**
	 * Print the PDF file from a collection
	 * 
	 * @param os
	 * @param payrolld
	 * @param logo
	 * @param language
	 * @throws CanNotCreatePdfException
	 * @throws IOException
	 */
	void print(OutputStream os) throws CanNotCreatePdfException;

}