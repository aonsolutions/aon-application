package com.esferalia.aon.in.payroll.pdf.creators.payroll._default.bundles;

import java.util.ListResourceBundle;

public class PayrollBundle  extends ListResourceBundle{

	 public Object[][] getContents() {
	        return contents;
	    }

	    private Object[][] contents = {
	    	{ "MONEDA", "\u20ac"},
	    	{ "FORMATO FECHA", "dd/MM/yyyy"},
	        { "TITULO","Recibo individual justificativo del pago de *" },
	        { "EMPRESA", "Empresa" },
	        { "DOMICILIO", "Domicilio" },
	        { "CIF", "CCC" },
	        { "CCC", "CIF" },
	        { "TRABAJADOR", "Trabajador" },
	        { "NIF", "NIF" },
	        { "NSS", "NSS" },
	        { "FECHA ANTIGUEDAD", "Fecha de antiguedad" },
	        { "G.COTIZ", "G.Cotización" },
	        { "G.PROFESIONAL", "G.Profesional" },
	        { "PERIODO LIQUIDACION", "Periodo Liquidacion" },
	        { "TOTAL DIAS", "Total dias" },
	        { "DEVENGOS", "Devengos" },
	        { "TOTALES", "Totales" },
	        { "TOTAL DEVENGADO", "Total devengado" },
	        { "DEDUCCIONES", "Deducciones" },
	        { "TOTAL DEDUCIR", "Total deducir" },
	        { "TOTAL PERCIBIR", "Liquido total a percibir" },
	        { "FIRMA EMPRESA", "Sello y firma de la empresa" },
	        { "FIRMA TRABAJADOR", "'recibí'" },
	        { "TITULO PIE", "Determinación de las bases de cotización a la Seguridad Social y conceptos de recaudación conjunta" },
	        { "TITULO PIE 2", "y de la base sujeta a retención del IRPF y aportación de la empresa" },
	        { "BASE", "Base" },
	        { "TIPO", "Tipo" },
	        { "AP EMPRESA", "AP.Empresa" },
	        { "CONTINGENCIAS COMUNES", "Contingencias comunes" },
	        { "IMPORTE DE REMUNERACION MENSUAL", "Importe de remuneración mensual" },
	        { "IMPORTE PRORRATA DE PAGA EXTRAORDINARIA", "Importe prorrata de paga extraordinaria" },
	        { "CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA", "Contingencias profesionales y conceptos recaudación conjunta" },
	        { "AT Y EP", "AT y EP" },
	        { "DESEMPLEO", "Desempleo" },
	        { "FORMACION PROFESIONAL", "Formación profesional" },
	        { "FONDO DE GARANTIA SALARIAL", "Fondo de garantia salarial" },
	        { "COTIZACION ADICIONAL POR HORAS EXTRAS", "Cotización adicional por horas extraordinarias" },
	        { "FUERZA MAYOR O", "Fuerza mayor o" },
	        { "NO ESTRUCTURALES", "No estructurales" },
	        { "BASE SUJETA A RETENCION IRPF", "Base sujeta a retención del IRPF" },
	        { "EN ESPECIE", "en especie" },
	        { "EN RETRIBUCIONES DINERARIAS", "en retribuciones dinerarias" },
	        { "TOTAL APORTACIONES", "Total aportaciones" }
	    };
}
