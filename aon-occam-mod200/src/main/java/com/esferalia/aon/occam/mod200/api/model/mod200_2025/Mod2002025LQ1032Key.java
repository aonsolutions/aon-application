package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Reserva de capitalización
public enum Mod2002025LQ1032Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002025Key[]{Mod2002025Key.LQ1401,Mod2002025Key.LQ1402,null				   },"2023")
	,C02(new Mod2002025Key[]{Mod2002025Key.LQ2773,Mod2002025Key.LQ2774,Mod2002025Key.LQ2775},"2024")
	,C03(new Mod2002025Key[]{Mod2002025Key.LQ441 ,Mod2002025Key.LQ452 ,Mod2002025Key.LQ453 },"2025(*)")
	,C04(new Mod2002025Key[]{Mod2002025Key.LQ3591,Mod2002025Key.LQ3592,Mod2002025Key.LQ3593},"2025")
	,C05(new Mod2002025Key[]{Mod2002025Key.LQ1137,Mod2002025Key.LQ1032,Mod2002025Key.LQ1139},"Total")
	
	,C06(new Mod2002025Key[]{Mod2002025Key.LQ1140,null                ,null                },"Reserva de capitalizaci\u00F3n dotada en el ejercicio")
	// FALTA - CASILLAS 3594 Y MILLON NUEVAS ESTE AÑO, NO SE SI LAS PONDRE AQUI JUNTO CON LA 1140 O APARTE LAS TRES (1140, 3594 Y MILLON) TENER EN CUENTA QUE MILLON ES UN CHECK
	,C07(new Mod2002025Key[]{Mod2002025Key.LQ3594,null                ,null                },"Incremento porcentual de la plantilla media total (**)")
	,C08(new Mod2002025Key[]{Mod2002025Key.MILLON,null                ,null                },"Entidad cuyo importe neto de la cifra de negocios durante los 12 meses anteriores a la fecha en que se inicie el per\u00EDodo impositivo al que corresponda esta reducci\u00F3n es inferior a 1 mill\u00F3n de euros")
	;
	 
    private String description;
    private Mod2002025Key[] keys;

	private Mod2002025LQ1032Key(Mod2002025Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002025Key[] getKeys() {
		return keys; 
	}

}

