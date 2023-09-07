package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen de cooperativas - Determinación de la base imponible
public enum Mod2002022LQ554Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.CP0C1,Mod2002022Key.CP0E1},"1. Ingresos computables")
	,C02(new Mod2002022Key[]{Mod2002022Key.CP0C2,Mod2002022Key.CP0E2},"2. Gastos espec\u00EDficos")
	,C03(new Mod2002022Key[]{Mod2002022Key.CP0C3,Mod2002022Key.CP0E3},"3. Gastos generales imputados")
	,C04(new Mod2002022Key[]{Mod2002022Key.CP0C4,Mod2002022Key.CP0E4},"4. Gastos Fondo de Educaci\u00F3n y Promoci\u00F3n") 
	,C05(new Mod2002022Key[]{null               ,Mod2002022Key.CP0E5},"5. Incrementos y disminuciones patrimoniales")
	,C06(new Mod2002022Key[]{Mod2002022Key.CP0C6,Mod2002022Key.CP0E6},"6. Resultado (1 - 2 - 3 - 4 + 5)")
	,C07(new Mod2002022Key[]{Mod2002022Key.CP0C7,Mod2002022Key.CP0E7},"7. Aumentos (ajustes positivos)")
	,C08(new Mod2002022Key[]{Mod2002022Key.CP0C8,Mod2002022Key.CP0E8},"8. Disminuciones (ajustes negativos)")
	,C09(new Mod2002022Key[]{Mod2002022Key.CP0C9,Mod2002022Key.CP0E9},"9. 50% Dotaci\u00F3n obligatoria F.R.O. (art. 16.5 Ley 20/1990)")
	,C10(new Mod2002022Key[]{Mod2002022Key.CPC10,Mod2002022Key.CPE10},"10. Reserva para inversiones en Canarias (Ley 19/1994)")
	,C11(new Mod2002022Key[]{Mod2002022Key.CPC11,Mod2002022Key.CPE11},"11. Factor de agotamiento")
	,C12(new Mod2002022Key[]{Mod2002022Key.CPC12,Mod2002022Key.CPE12},"12. Base imponible (6 + 7 - 8 - 9 + 10 + 11)")
	;
	 
    private String description;
    private Mod2002022Key[] keys;    

	private Mod2002022LQ554Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;		
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}

}