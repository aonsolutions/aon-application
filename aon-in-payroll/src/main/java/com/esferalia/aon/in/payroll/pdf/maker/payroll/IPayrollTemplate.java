package com.esferalia.aon.in.payroll.pdf.maker.payroll;

import java.io.IOException;
import java.io.OutputStream;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;

public interface IPayrollTemplate {
    	
		public static final int PPE = Integer.MAX_VALUE -3; 

		public static final int NOTE = Integer.MAX_VALUE -2; 
    	public static final int INFO = Integer.MAX_VALUE -1 ; 
    	public static final int WARNING = Integer.MAX_VALUE ; 
	
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