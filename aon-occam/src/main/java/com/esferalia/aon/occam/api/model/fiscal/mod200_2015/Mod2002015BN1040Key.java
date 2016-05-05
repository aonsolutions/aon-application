package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// Deducción por reversión de medidas temporales (D.T. 37ª.1 LIS)
public enum Mod2002015BN1040Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// K --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002015Key[]{Mod2002015Key.BN1162,Mod2002015Key.BN1163,Mod2002015Key.BN1164,Mod2002015Key.BN1165},"2015(*)")
	,C02(new Mod2002015Key[]{Mod2002015Key.BN1166,Mod2002015Key.BN1167,Mod2002015Key.BN1168,Mod2002015Key.BN1169},"2015")
	,C03(new Mod2002015Key[]{Mod2002015Key.BN1170,Mod2002015Key.BN1171,null /* BN1040 */   ,Mod2002015Key.BN1173},"Total")
	;
	 
    private String description;
    private Mod2002015Key[] keys;

	private Mod2002015BN1040Key(Mod2002015Key[] keys, String description) {
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

