package com.esferalia.aon.in.payroll.pdf.maker.settlement.bundles;

import java.util.ListResourceBundle;

public class SettlementBundle extends ListResourceBundle {
	 @Override
	public Object[][] getContents() {return contents;}
	 private Object[][] contents = {
	   	{ "TITLE", "DOCUMENTO  DE LIQUIDACIÓN Y FINIQUITO"},
	   	{ "ENTERPRISE", "De una parte, $enterprise_name, como empleador, con NIF $enterprise_nif y domicilio en $enterprise_address."},
	   	{ "EMPLOYEE", "De otra parte, $employee_name, con NIF: $employee_nif, que viene prestando sus servicios como empleado/a con la categoría de $employee_category, desde el $employee_antiquity."},
	   	{ "DECLARE", "DECLARAN:"},
	   	{ "DECLARATION", "Que causa baja en la misma con fecha $end_date, por el motivo de $end_cause y percibe en este momento la cantidad de $text_total EUROS ($total_amount\u20AC) por los servicios prestados y por la totalidad de los que le puedan corresponder derivados de esta relación laboral hasta el día que causó baja en la misma, y por los conceptos que a continuación se detallan."},
	   	{ "LEGAL DATA", "Con el percibo de dicha cantidad el empleado declara hallarse completamente saldado y finiquitado por todos y cuantos devengos salariales y derechos le pudieran corresponder por razón del trabajo realizado para el empleador, quedando totalmente rescindida la relación laboral que unía a las partes, sin que tenga derecho a posterior reclamación o indemnización por concepto alguno, y renuncia expresamente a cualquier acción procesal (civil, penal o de otra índole) contra el empleador."},
	   	{ "LEGAL ADVICE", "Se pone en su conocimiento el derecho que le asiste a solicitar la presencia de un representante legal de los trabajadores en el acto de la firma del recibo de finiquito."},
	   	{ "DATE", "'En $location a' dd 'de' MMMM ', RECIBÍ:'"},
	   	{ "EMPLOYEE SIGN", "Firma trabajador"},
	   	{ "ENTERPRISE SIGN", "Firma empresa"},
	   	{ "REPRESENTATIVE SIGN", "Firma representante"},	   	
	   	{ "DATE FORMAT", "dd/MM/yyyy"},	   	
	   	{ "CURRENCY", "\u20AC"},	   	
	   	{ "ACCRUALS", "DEVENGOS"},	   	
	   	{ "ACCRUAL TOTAL", "A. TOTAL DEVENGADO:"},	   	
	   	{ "DEDUCTIONS", "DEDUCCIONES"},	  
		{ "DEDUCTION TOTAL", "B. TOTAL DEDUCIR:"},	   	
		{ "TOTAL", "TOTAL A PERCIBIR (A-B):"},	   	
		{ "TOTALS", "TOTALES"},	   	
	 };
}
