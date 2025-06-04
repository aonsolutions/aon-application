package net.aonsolutions.aon.gwt.ccaa.server.xbrl;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Constantes {
	
	public static final String I_ACTUAL = "I.ACTUAL";
	public static final String D_ACTUAL = "D.ACTUAL";
	public static final String I_ANTERIOR = "I.ANTERIOR";
	public static final String D_ANTERIOR = "D.ANTERIOR";

// ----------------------------------------------------------------------------------------------------    
    // ESTOS DATOS SON SOLO PARA PROBAR, ES COMO SI FUESE EL ESQUEMA XML QUE ESTA GUARDADO EN BASE DE DATOS
    // LAS REFETENCIAS A ESTE MAP, SERAN BUSQUEDAS EN EL XML QUE ESTA GUARDADO EN LA BD
	public static final Map<String,String> datosIdentificacion = new LinkedHashMap<>();  	
	static {
		datosIdentificacion.put("1010", "B50111111"); // NIF
		datosIdentificacion.put("1011", "001"); // Forma juridica SA
//		datosIdentificacion.put("1012", "023"); // Forma juridica SL 
//		datosIdentificacion.put("1013", "SOC.COOP."); // Forma juridida OTRAS
		datosIdentificacion.put("1009", "CODIGO LEI"); // LEI
		datosIdentificacion.put("1020", "DENOMINACION SOCIAL"); // Denominación social
		datosIdentificacion.put("1022", "DOMICILIO SOCIAL"); // Domicilio social
		datosIdentificacion.put("1023", "MUNICIPIO"); // Municipio
		datosIdentificacion.put("1025", "ZARAGOZA"); // Provincia
		datosIdentificacion.put("1024", "50001"); // Código postal
		datosIdentificacion.put("1031", "976000000"); // Teléfono
		datosIdentificacion.put("1037", "info@mail.com"); // Direccion email
//		datosIdentificacion.put("2009", "EDICION DE LIBROS"); // Actividad principal (nombre)
//		datosIdentificacion.put("2001", "5811"); // Actividad principal (codigo CNAE)
		datosIdentificacion.put("2009", "ELABORACION DE ALIMENTOS"); // Actividad principal (nombre)
		datosIdentificacion.put("2001", "1086"); // Actividad principal (codigo CNAE)
		
		datosIdentificacion.put("1901", "35");
		datosIdentificacion.put("1903", "CAUSAS DE NO CONSIGNAR CIFRAS");
		
        datosIdentificacion.put("4001", "4001"); 
        datosIdentificacion.put("4002", "4002");  
        datosIdentificacion.put("4010", "4010");
        datosIdentificacion.put("4120", "4120");
        datosIdentificacion.put("4121", "4121");
        datosIdentificacion.put("4122", "4122");
        datosIdentificacion.put("4123", "4123");
        datosIdentificacion.put("40019", "40019");
        datosIdentificacion.put("40029", "40029");
        datosIdentificacion.put("40109", "40109");
        datosIdentificacion.put("41209", "41209");
        datosIdentificacion.put("41219", "41219");
        datosIdentificacion.put("41229", "41229");
        datosIdentificacion.put("41239", "41239");
        
        datosIdentificacion.put("4212", "4212");
        datosIdentificacion.put("4213", "4213");
        datosIdentificacion.put("42129", "42129");
        datosIdentificacion.put("42139", "42139");
        
        datosIdentificacion.put("1902","1");  // Micropymes 0-false, 1-true 
        
        datosIdentificacion.put("91000", "91000");
        datosIdentificacion.put("91001", "91001");
        datosIdentificacion.put("91002", "91002");
        datosIdentificacion.put("91003", "91003");
        datosIdentificacion.put("91004", "91004");
        datosIdentificacion.put("91005", "91005");
        datosIdentificacion.put("91007", "91007");
        datosIdentificacion.put("91008", "91008");
        datosIdentificacion.put("91009", "91009");
        datosIdentificacion.put("91010", "91010");
        datosIdentificacion.put("91011", "91011");
        datosIdentificacion.put("91012", "91012");
        datosIdentificacion.put("94705", "94705");
        datosIdentificacion.put("910009", "91000.9");
        datosIdentificacion.put("910019", "91001.9");
        datosIdentificacion.put("910029", "91002.9");
        datosIdentificacion.put("910039", "91003.9");
        datosIdentificacion.put("910049", "91004.9");
        datosIdentificacion.put("910059", "91005.9");
        datosIdentificacion.put("910079", "91007.9");
        datosIdentificacion.put("910089", "91008.9");
        datosIdentificacion.put("910099", "91009.9");
        datosIdentificacion.put("910109", "91010.9");
        datosIdentificacion.put("910119", "91011.9");
        datosIdentificacion.put("910129", "91012.9");
        datosIdentificacion.put("947059", "947059");
        
        // Memoria apartados texto libre
        datosIdentificacion.put("9019001", "Actividad de la empresa");
        datosIdentificacion.put("9029001", "Bases de presentación de las cuentas anuales");
        datosIdentificacion.put("9049001", "Normas de registro y valoración");
        datosIdentificacion.put("9059001", "Inmovilizado material, intangible e inversiones inmobiliarias");
        datosIdentificacion.put("9069001", "Activos financieros");
        datosIdentificacion.put("9079001", "Pasivos financieros");
        datosIdentificacion.put("9089001", "Fondos propios");
        datosIdentificacion.put("9099001", "Situación fiscal");
        datosIdentificacion.put("9129001", "Operaciones con partes vinculadas");
        datosIdentificacion.put("9139001", "Otra información");
        
        // Memoria cuadro normalizado apartado 10
//        datosIdentificacion.put("98007", "98007");
//        datosIdentificacion.put("980079", "98007.9");

        // Abreviado datos pertenencia a grupo de sociedades
        datosIdentificacion.put("1041", "CASILLA 1041");
        datosIdentificacion.put("1040", "CASILLA 1040");
        datosIdentificacion.put("1061", "CASILLA 1061");
        datosIdentificacion.put("1060", "CASILLA 1060");

        
	}
// ----------------------------------------------------------------------------------------------------	
		

}
