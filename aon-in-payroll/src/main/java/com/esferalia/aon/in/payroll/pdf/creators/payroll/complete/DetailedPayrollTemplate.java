package com.esferalia.aon.in.payroll.pdf.creators.payroll.complete;

import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.DeductionType.NullDeductionException;
import com.esferalia.aon.occam.api.model.type.DeductionType.UnknownDeductionException;

public class DetailedPayrollTemplate {
	
	public static void main(String[] args) {
		try {System.out.println(DeductionType.deductionTypeOf((byte) 3));} 
		catch (NullDeductionException | UnknownDeductionException e) {e.printStackTrace();}
	}
	
}
