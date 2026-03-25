package com.esferalia.aon.occam.mod200.api.model.mod200_2025;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen de cooperativas - Determinación de la base imponible
public enum Mod2002025LQ554Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
 	
	 C01(new Mod2002025Key[]{Mod2002025Key.CP2827,Mod2002025Key.CP2828},"1. Ingresos computables")
	,C02(new Mod2002025Key[]{Mod2002025Key.CP2829,Mod2002025Key.CP2830},"2. Gastos espec\u00EDficos")
	,C03(new Mod2002025Key[]{Mod2002025Key.CP2832,Mod2002025Key.CP2833},"3. Gastos generales imputados")
	,C04(new Mod2002025Key[]{Mod2002025Key.CP2834,Mod2002025Key.CP2835},"4. Gastos Fondo de Educaci\u00F3n y Promoci\u00F3n")
	,C05(new Mod2002025Key[]{null                ,Mod2002025Key.CP2836},"5. Incrementos y disminuciones patrimoniales")
	,C06(new Mod2002025Key[]{Mod2002025Key.CP2837,Mod2002025Key.CP2838},"6. Resultado (1 - 2 - 3 - 4 + 5)")
	,C07(new Mod2002025Key[]{Mod2002025Key.CP2839,Mod2002025Key.CP2840},"7. Aumentos (ajustes positivos)")
	,C08(new Mod2002025Key[]{Mod2002025Key.CP2841,Mod2002025Key.CP2842},"8. Disminuciones (ajustes negativos)")
	,C09(new Mod2002025Key[]{Mod2002025Key.CP2843,Mod2002025Key.CP2845},"9. 50% Dotaci\u00F3n obligatoria F.R.O. (art. 16.5 Ley 20/1990)")
	,C10(new Mod2002025Key[]{Mod2002025Key.CP2846,Mod2002025Key.CP2847},"10. Reserva para inversiones en Canarias (Ley 19/1994)")
	,C11(new Mod2002025Key[]{Mod2002025Key.CP0012,Mod2002025Key.CP0016},"11. Reserva para inversiones en Illes Balears")
	,C12(new Mod2002025Key[]{Mod2002025Key.CP2848,Mod2002025Key.CP2849},"12. Factor de agotamiento")
	,C13(new Mod2002025Key[]{Mod2002025Key.CP553 ,Mod2002025Key.CP554 },"13. Base imponible (6 + 7 - 8 - 9 + 10 + 11 + 12)")	
	;
	 
    private String description;
    private Mod2002025Key[] keys;    

	private Mod2002025LQ554Key(Mod2002025Key[] keys, String description) {
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
