package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Detalle de las correcciones al resultado de la cuenta de pérdidas y ganancias
// (excluida la corrección por Impuesto Sociedades) (cumplimentación voluntaria)
public enum Mod2002015CorrectionDetailKey implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	
	// FALTA - En el 2014 este enumerado estaba en Page08 de aon.gwt.fiscal.client.tree.content.mod200_2014, con algún campo más
	// para el 2015 se pone aqui para seguir el mismo criterio que el resto de desgloses
	// Habra que tenerlo en cuenta cuando se haga el formulario de pantalla para el 2015
	
	 C01(new Mod2002015Key[]{Mod2002015Key.DC001,Mod2002015Key.DC002,null               ,null               },"Correcciones Permanentes")
	,C02(new Mod2002015Key[]{Mod2002015Key.DC003,Mod2002015Key.DC004,Mod2002015Key.DC005,Mod2002015Key.DC006},"Correcciones temporarias con origen en el ejercicio")
	,C03(new Mod2002015Key[]{Mod2002015Key.DC007,Mod2002015Key.DC008,Mod2002015Key.DC009,Mod2002015Key.DC010},"Por amortizaciones")                           
	,C04(new Mod2002015Key[]{Mod2002015Key.DC011,Mod2002015Key.DC012,Mod2002015Key.DC013,Mod2002015Key.DC014},"Por deterioros de valor")                      
	,C05(new Mod2002015Key[]{Mod2002015Key.DC015,Mod2002015Key.DC016,Mod2002015Key.DC017,Mod2002015Key.DC018},"Por pensiones")                                
	,C06(new Mod2002015Key[]{Mod2002015Key.DC019,Mod2002015Key.DC020,Mod2002015Key.DC021,Mod2002015Key.DC022},"Por fondo de comercio")                        
	,C07(new Mod2002015Key[]{Mod2002015Key.DC023,Mod2002015Key.DC024,Mod2002015Key.DC025,Mod2002015Key.DC026},"Resto")                                        
	,C08(new Mod2002015Key[]{Mod2002015Key.DC027,Mod2002015Key.DC028,Mod2002015Key.DC029,Mod2002015Key.DC030},"Correcc. temporarias con origen en ejerc. anteriores")
	,C09(new Mod2002015Key[]{Mod2002015Key.DC031,Mod2002015Key.DC032,Mod2002015Key.DC033,Mod2002015Key.DC034},"Por amortizaciones")                               
	,C10(new Mod2002015Key[]{Mod2002015Key.DC035,Mod2002015Key.DC036,Mod2002015Key.DC037,Mod2002015Key.DC038},"Por deterioros de valor")                          
	,C11(new Mod2002015Key[]{Mod2002015Key.DC039,Mod2002015Key.DC040,Mod2002015Key.DC041,Mod2002015Key.DC042},"Por pensiones")                                    
	,C12(new Mod2002015Key[]{Mod2002015Key.DC043,Mod2002015Key.DC044,Mod2002015Key.DC045,Mod2002015Key.DC046},"Por fondo de comercio")                            
	,C13(new Mod2002015Key[]{Mod2002015Key.DC047,Mod2002015Key.DC048,Mod2002015Key.DC049,Mod2002015Key.DC050},"Resto")                                            
	,C14(new Mod2002015Key[]{Mod2002015Key.DC051,Mod2002015Key.DC052,Mod2002015Key.DC053,Mod2002015Key.DC054},"TOTAL correcc. al resultado de la cuenta de p\u00E9rdidas y ganancias (excluida correcc. I. Soc.)")                      
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015CorrectionDetailKey(Mod2002015Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002015Key[] getKeys() {
		return keys; 
	}
}

