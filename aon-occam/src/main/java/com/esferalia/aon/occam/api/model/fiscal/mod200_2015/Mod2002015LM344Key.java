package com.esferalia.aon.occam.api.model.fiscal.mod200_2015;

import java.io.Serializable;

// DOTACIONES POR DETERIORO DE CRÉDITOS U OTROS ACTIVOS DERIVADOS DE LAS POSIBLES INSOLVENCIAS
// DE LOS DEUDORES NO VINCULADOS CON EL CONTRIBUYENTE Y OTRAS DEL ART. 11.12 LIS. CONVERSIÓN DE
// ACTIVOS POR IMPUESTO DIFERIDO EN CRÉDITO EXIGIBLE FRENTE A LA ADMÓN. TRIBUTARIA (ART. 130 LIS) 
public enum Mod2002015LM344Key implements Serializable, IMod200KeysProvider {

	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	  C01(new Mod2002015Key[]{Mod2002015Key.LM893 ,Mod2002015Key.LM173 ,Mod2002015Key.LM958 ,Mod2002015Key.LM898 },"2011")
	 ,C02(new Mod2002015Key[]{Mod2002015Key.LM899 ,Mod2002015Key.LM227 ,Mod2002015Key.LM959 ,Mod2002015Key.LM917 },"2012")
	 ,C03(new Mod2002015Key[]{Mod2002015Key.LM948 ,Mod2002015Key.LM291 ,Mod2002015Key.LM979 ,Mod2002015Key.LM949 },"2013")
	 ,C04(new Mod2002015Key[]{Mod2002015Key.LM950 ,Mod2002015Key.LM951 ,Mod2002015Key.LM980 ,Mod2002015Key.LM952 },"2014")
	 ,C05(new Mod2002015Key[]{Mod2002015Key.LM1220,Mod2002015Key.LM1221,Mod2002015Key.LM1222,Mod2002015Key.LM1223},"2015(*)")
	 ,C06(new Mod2002015Key[]{Mod2002015Key.LM981 ,Mod2002015Key.LM982 ,Mod2002015Key.LM983 ,Mod2002015Key.LM984 },"2015")
	 ,C07(new Mod2002015Key[]{Mod2002015Key.LM953 ,Mod2002015Key.LM344 ,Mod2002015Key.LM985 ,Mod2002015Key.LM954 },"Total")
	 ,C08(new Mod2002015Key[]{Mod2002015Key.LM393 ,null, null, null },"Importe del cr\u00E9dito exigible")
	 ,C09(new Mod2002015Key[]{Mod2002015Key.LM150 ,null, null, null },"Opciones - Abono")
	 ,C10(new Mod2002015Key[]{Mod2002015Key.LM506 ,null, null, null },"Opciones - Compensaci\u00F3n")
     ;
	 
    private String description;
    
    private Mod2002015Key[] keys;
    
	private Mod2002015LM344Key(Mod2002015Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002015Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

