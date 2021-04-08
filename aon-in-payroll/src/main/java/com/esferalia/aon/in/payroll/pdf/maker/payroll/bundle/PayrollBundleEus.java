package com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle;

import java.util.ListResourceBundle;

public class PayrollBundleEus  extends ListResourceBundle{

	 public Object[][] getContents() {
	        return contents;
	    }

	    private Object[][] contents = {
	    	{ "MONEDA", "\u20ac"},
	        { "TITULO","* ordainketa justifikazioaren bakarkako ordainagiria" },
	        { "EMPRESA", "Konpainia" },
	        { "DOMICILIO", "Helbidea" },
	        { "CIF", "IFZ" },
	        { "CCC", "KKK" },
	        { "TRABAJADOR", "Langilea" },
	        { "NIF", "IFZ" },
	        { "NSS", "GSZ" },
	        { "FECHA ANTIGUEDAD", "Fecha de antiguedad" },
	        { "G.COTIZ", "Kotizazio taldea" },
	        { "G.PROFESIONAL", "Lanpostua" },
	        { "PERIODO LIQUIDACION", "Kitapen aldia" },
	        { "TOTAL DIAS", "Egun totalak" },
	        { "DEVENGOS", "Sortzapenak" },
	        { "TOTALES", "Totalak" },
	        { "TOTAL DEVENGADO", "Sortzapen totala" },
	        { "DEDUCCIONES", "Kenkariak" },
	        { "TOTAL DEDUCIR", "Total deducir" },
	        { "TOTAL PERCIBIR", "Liquido total a percibir" },
	        { "FIRMA EMPRESA", "Sello y firma de la empresa" },
	        { "FIRMA TRABAJADOR", "'recibí'" },
	        { "TITULO PIE", "Langileak Gizarte Segurantzara egindako ekarpenak eta zerga-bilketa" },
	        { "TITULO PIE 2", "bateratuko kontzeptuak" },
	        { "BASE", "Oinarri" },
	        { "TIPO", "Portzentaia" },
	        { "AP EMPRESA", "AP." },
	        { "CONTINGENCIAS COMUNES", "Kontingentzia arruntak" },
	        { "IMPORTE DE REMUNERACION MENSUAL", "Hileko soldata" },
	        { "IMPORTE PRORRATA DE PAGA EXTRAORDINARIA", "Aparteko pagen hainbanapena" },
	        { "CONTINGENCIAS PROFESIONALES Y CONCEPTOS DE RECAUDACION CONJUNTA", "Kontingentzia  profesionalengatik" },
	        { "AT Y EP", "LI eta LG" },
	        { "DESEMPLEO", "Langabezia" },
	        { "FORMACION PROFESIONAL", "Lanbide heziketa" },
	        { "FONDO DE GARANTIA SALARIAL", "FOGASA" },
	        { "COTIZACION ADICIONAL POR HORAS EXTRAS", "Aparteko orduengatik" },
	        { "FUERZA MAYOR O", "Ezinbesteko kasukoak" },
	        { "NO ESTRUCTURALES", "Ez egiturazkoak" },
	        { "BASE SUJETA A RETENCION IRPF", "PFEZ-aren Atxikipenari lotutako oinarria" },
	        { "EN ESPECIE", "Gauzaz" },
	        { "EN RETRIBUCIONES DINERARIAS", "Diru ordainketak" },
	        { "TOTAL APORTACIONES", "Ekarpen totala" }
	    };
}
