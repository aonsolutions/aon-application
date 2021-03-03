package net.aonsolutions.aon.tbai.emision.gipuzkoa;

import net.aonsolutions.aon.tbai.emision.EmisionInvoice;
import net.aonsolutions.aon.tbai.exceptions.validation.ValidationException;
import ticketbai.emision.Cabecera;
import ticketbai.emision.Factura;
import ticketbai.emision.HuellaTBAI;
import ticketbai.emision.Sujetos;

public class GipuzkoaEmisionValidator {

	public static void validate(EmisionInvoice invoice) throws ValidationException{
		if(invoice == null) throw new ValidationException("Invoice not found");
		try {
			validateHeader    (invoice.getCabecera());
			validateSubjects  (invoice.getSujetos());
			validateInvoice	  (invoice.getFactura());
			validateTbaiPrint (invoice.getHuellaTbai());		
		}
		catch(Exception e) {
			if(e.getCause()   != null && e.getMessage() != null) 	
				throw new ValidationException(e.getMessage(), e.getCause());
			if(e.getCause()   != null) 								
				throw new ValidationException(e.getCause());
			if(e.getMessage() != null) 								
				throw new ValidationException(e.getMessage());	
			throw new ValidationException("Unexpected exception ocurred",e);
		}
		
	}
	
	private static void validateHeader(Cabecera cabecera) throws Exception{
		
		if(cabecera == null) throw new Exception("Header not found");
		
	}
	
	private static void validateSubjects(Sujetos sujetos) throws Exception{
		
		if(sujetos == null) throw new Exception("Subjects not found");
		
	}
	
	private static void validateInvoice(Factura factura)   throws Exception{
	
		if(factura == null) throw new ValidationException("Invoice data not found");
		
	}
	
	private static void validateTbaiPrint(HuellaTBAI huellaTBAI) throws Exception{
		
		if(huellaTBAI == null) throw new ValidationException("TBAI print not found");
		
	}
}
