package com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle;

import java.util.ListResourceBundle;

public class PayrollBundle  extends ListResourceBundle{

	 public Object[][] getContents() {
	        return contents;
	    }

	    private Object[][] contents = {
	    	{ "MONEDA", "\u20ac"},
	    	{ "FORMATO FECHA", "dd/MM/yyyy"},
	        { "TITULO","Recibo individual justificativo del pago de *" },
	        { "CIF", "NIF" },
	        { "CCC", "CCC" },
	        { "NIF", "NIF" },
	        { "NSS", "NSS" },
	        { "FECHA ANTIGUEDAD", "Fecha de antigüedad" },
	        { "G.COTIZ", "G.Cotización" },
	        { "G.PROFESIONAL", "G.Profesional" },
	        { "PERIODO LIQUIDACION", "Periodo de liquidación" },
	        { "TOTAL DIAS", "Total días" },
	        { "DEVENGOS", "Devengos" },
	        { "TOTALES", "Totales" },
	        { "TOTAL DEVENGADO", "Total devengado" },
	        { "DEDUCCIONES", "Deducciones" },
	        { "TOTAL DEDUCIR", "Total deducir" },
	        { "TOTAL PERCIBIR", "Líquido total a percibir" },
	        { "FIRMA EMPRESA", "Sello y firma de la empresa" },
	        { "FIRMA TRABAJADOR", "'Recibí, el trabajador'" },
	        { "TITULO PIE", "Determinación de las bases de cotización a la S.S. y del IRPF, conceptos de recaudación conjunta y aportación de la empresa" },
	        { "TITULO PIE 2", "" },
	        { "BASE", "Base" },
	        { "TIPO", "Tipo" },
	        { "AP EMPRESA", "AP.Empresa" },
	        { "CONTINGENCIAS COMUNES", "Contingencias comunes" },
	        { "MECANISMO DE EQUIDAD INTERGENERACIONAL", "Mecanismo de equidad intergeneracional" },
	        { "IMPORTE DE REMUNERACION MENSUAL", "Importe de remuneración mensual" },
	        { "IMPORTE PRORRATA DE PAGA EXTRAORDINARIA", "Importe prorrata de paga extraordinaria" },
	        { "CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA", "Contingencias profesionales y conceptos recaudación conjunta" },
	        { "AT Y EP", "AT y EP" },
	        { "DESEMPLEO", "Desempleo" },
	        { "FORMACION PROFESIONAL", "Formación profesional" },
	        { "FONDO DE GARANTIA SALARIAL", "Fondo de garantía salarial" },
	        { "COTIZACION ADICIONAL POR HORAS EXTRAS", "Cotización adicional por horas extraordinarias" },
	        { "FUERZA MAYOR O", "Fuerza mayor" },
	        { "NO ESTRUCTURALES", "No estructurales" },
			{ "SOLIDARIDAD", "Solidaridad"},
	        { "BASE SUJETA A RETENCION IRPF", "Base sujeta a retención del IRPF" },
	        { "EN ESPECIE", "en especie" },
	        { "EN RETRIBUCIONES DINERARIAS", "en retribuciones dinerarias" },
	        { "TOTAL APORTACIONES", "Total aportaciones" },
	        { "TOTAL COSTES", "Importe acumulado" }
	    };
}
