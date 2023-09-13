package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Deducción donativos a entidades sin fines de lucro. Ley 49/2002
// Fila Totales [00598] [00565] [00895] 
public enum Mod2002022BN565Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	// Total deducciones a entidades sin fines de lucro (Ley 49/2002) 	
    C01(new Mod2002022Key[]{Mod2002022Key.BN598 ,Mod2002022Key.BN565 ,Mod2002022Key.BN895 },"Total")
	;	
	 
    private String description;
    private Mod2002022Key[] keys;
    
	private Mod2002022BN565Key(Mod2002022Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}

	public Mod2002022Key[] getKeys() {
		return keys;
	}
	
	public String getDescription() {
		return description;
	}

}

