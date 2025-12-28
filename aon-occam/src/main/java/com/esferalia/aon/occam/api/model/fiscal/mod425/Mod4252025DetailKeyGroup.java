package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;

public enum Mod4252025DetailKeyGroup implements Serializable {

	  DEV_001 ("R\u00E9gimen ordinario"                                                                                      ,Mod4252025DetailKey.C003,Mod4252025DetailKey.C006,Mod4252025DetailKey.C009,Mod4252025DetailKey.C012,Mod4252025DetailKey.C015,Mod4252025DetailKey.C018,Mod4252025DetailKey.C018B)
	 ,DEV_002 ("R\u00E9gimen especial de bienes usados"                                                                      ,Mod4252025DetailKey.C021,Mod4252025DetailKey.C024,Mod4252025DetailKey.C027,Mod4252025DetailKey.C030,Mod4252025DetailKey.C033)
	 ,DEV_003 ("R\u00E9gimen especial de objetos de arte, antig\u00fcedades y objetos de colecci\u00F3n"                     ,Mod4252025DetailKey.C036,Mod4252025DetailKey.C039,Mod4252025DetailKey.C042,Mod4252025DetailKey.C045,Mod4252025DetailKey.C048)
	 ,DEV_004 ("R\u00E9gimen especial del criterio de caja"                                                                  ,Mod4252025DetailKey.C051,Mod4252025DetailKey.C054,Mod4252025DetailKey.C057,Mod4252025DetailKey.C060,Mod4252025DetailKey.C063,Mod4252025DetailKey.C066,Mod4252025DetailKey.C066B)
	 ,DEV_005 ("R\u00E9gimen especial de agencias de viaje"                                                                  ,Mod4252025DetailKey.C069)  
	 ,DEV_006 ("Modificaci\u00F3n de bases y rectificaci\u00F3n de cuotas impositivas repercutidas"                          ,Mod4252025DetailKey.C071)
	 ,DEV_007 ("Modificaci\u00F3n de bases y cuotas por procedimientos de concurso de acreedores o cr\u00E9ditos incobrables",Mod4252025DetailKey.C073)
	 ,DEV_008 ("Total bases I.G.I.C."                                                                                        ,Mod4252025DetailKey.C074)  
	 ,DEV_009 ("Operaciones con inversi\u00F3n del sujeto pasivo"                                                            ,Mod4252025DetailKey.C076)  
	 ,DEV_010 ("Cuotas devueltas en R\u00E9gimen de viajeros"                                                                ,Mod4252025DetailKey.C078)  
	 ,DEV_011 ("Total cuotas devengadas"                                                                                     ,Mod4252025DetailKey.C079)  
	 ,DED_001 ("IGIC deducible en operaciones interiores corrientes"                                                         ,Mod4252025DetailKey.C081)
	 ,DED_002 ("IGIC deducible en operaciones interiores con bienes de inversi\u00F3n"                                       ,Mod4252025DetailKey.C083)  
	 ,DED_003 ("IGIC deducible por importaciones de bienes corrientes"                                                       ,Mod4252025DetailKey.C085)
	 ,DED_004 ("IGIC deducible por importaciones de bienes de inversi\u00F3n"                                                ,Mod4252025DetailKey.C087)  
	 ,DED_005 ("Rectificaci\u00F3n de deducciones"                                                                           ,Mod4252025DetailKey.C089)
	 ,DED_006 ("Compensaci\u00F3n en r\u00E9gimen especial de la agricultura, ganaderia y pesca"                             ,Mod4252025DetailKey.C090)  
	 ,DED_007 ("Regularizaci\u00F3n de cuotas soportadas por bienes de inversi\u00F3n"                                       ,Mod4252025DetailKey.C091)
	 ,DED_008 ("Regularizaci\u00F3n de cuotas soportadas antes del inicio de la actividad"                                   ,Mod4252025DetailKey.C092)  
	 ,DED_009 ("Regularizaci\u00F3n por aplicaci\u00F3n del porcentaje definitivo de prorrata"                               ,Mod4252025DetailKey.C093)
	 ,DED_010 ("Total cuotas deducibles"                                                                                     ,Mod4252025DetailKey.C094)
	 ,TOT_001 ("Resultado r\u00E9gimen general"                                                                              ,Mod4252025DetailKey.C095)
	 ;
	
	private String label;
	private Mod4252025DetailKey[] keys;
	
	private Mod4252025DetailKeyGroup(String label, Mod4252025DetailKey... keys) {
		this.label = label;
		this.keys = keys;
	}
	public Mod4252025DetailKey[] getKeys() {
		return keys;
	}
	public String getLabel() {
		return label;
	}
	
}