package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	  C0001(new Mod2002014Key[]{Mod2002014Key.BN854,Mod2002014Key.BN855,null               },Mod2002014Key.BN854.getDescription())
	 ,C0002(new Mod2002014Key[]{Mod2002014Key.BN857,Mod2002014Key.BN858,Mod2002014Key.BN859},Mod2002014Key.BN857.getDescription())
	 ,C0003(new Mod2002014Key[]{Mod2002014Key.BN860,Mod2002014Key.BN861,Mod2002014Key.BN862},Mod2002014Key.BN860.getDescription())
	 ,C0004(new Mod2002014Key[]{Mod2002014Key.BN863,Mod2002014Key.BN864,Mod2002014Key.BN865},Mod2002014Key.BN863.getDescription())
	 ,C0005(new Mod2002014Key[]{Mod2002014Key.BN883,Mod2002014Key.BN884,Mod2002014Key.BN885},Mod2002014Key.BN883.getDescription())
	 ,C0006(new Mod2002014Key[]{Mod2002014Key.BN785,Mod2002014Key.BN789,Mod2002014Key.BN790},Mod2002014Key.BN785.getDescription())
	 ,C0007(new Mod2002014Key[]{Mod2002014Key.BN088,Mod2002014Key.BN564,Mod2002014Key.BN801},Mod2002014Key.BN088.getDescription())
	 ,C0008(new Mod2002014Key[]{Mod2002014Key.BN194,Mod2002014Key.BN195,Mod2002014Key.BN196},Mod2002014Key.BN194.getDescription())
	 ,C0009(new Mod2002014Key[]{Mod2002014Key.BN868,Mod2002014Key.BN869,Mod2002014Key.BN834},Mod2002014Key.BN868.getDescription())
	 ,C0010(new Mod2002014Key[]{Mod2002014Key.BN871,Mod2002014Key.BN872,Mod2002014Key.BN873},Mod2002014Key.BN871.getDescription())
	 ,C0011(new Mod2002014Key[]{Mod2002014Key.BN874,Mod2002014Key.BN875,Mod2002014Key.BN876},Mod2002014Key.BN874.getDescription())
	 ,C0012(new Mod2002014Key[]{Mod2002014Key.BN877,Mod2002014Key.BN878,Mod2002014Key.BN879},Mod2002014Key.BN877.getDescription())
	 ,C0013(new Mod2002014Key[]{Mod2002014Key.BN880,Mod2002014Key.BN881,Mod2002014Key.BN882},Mod2002014Key.BN880.getDescription())
	 ,C0014(new Mod2002014Key[]{Mod2002014Key.BN866,Mod2002014Key.BN867,Mod2002014Key.BN870},Mod2002014Key.BN866.getDescription())
	 ,C0015(new Mod2002014Key[]{Mod2002014Key.BN939,Mod2002014Key.BN940,Mod2002014Key.BN941},Mod2002014Key.BN939.getDescription())
	 ,C0016(new Mod2002014Key[]{Mod2002014Key.BN191,Mod2002014Key.BN192,Mod2002014Key.BN193},Mod2002014Key.BN191.getDescription())
	 ,C0017(new Mod2002014Key[]{Mod2002014Key.BN613,Mod2002014Key.BN614,Mod2002014Key.BN701},Mod2002014Key.BN613.getDescription())
	 ,C0018(new Mod2002014Key[]{Mod2002014Key.BN200,Mod2002014Key.BN257,Mod2002014Key.BN011},Mod2002014Key.BN200.getDescription())
	 ,C0019(new Mod2002014Key[]{Mod2002014Key.BN037,Mod2002014Key.BN038,Mod2002014Key.BN039},Mod2002014Key.BN037.getDescription())
	 ,C0020(new Mod2002014Key[]{Mod2002014Key.BN044,Mod2002014Key.BN045,Mod2002014Key.BN046},Mod2002014Key.BN044.getDescription())
	 ,C0021(new Mod2002014Key[]{Mod2002014Key.BN528,Mod2002014Key.BN529,Mod2002014Key.BN530},Mod2002014Key.BN528.getDescription())
	 ,C0022(new Mod2002014Key[]{Mod2002014Key.BN144,Mod2002014Key.BN145,Mod2002014Key.BN146},Mod2002014Key.BN144.getDescription())
	 ,C0023(new Mod2002014Key[]{Mod2002014Key.BN147,Mod2002014Key.BN148,Mod2002014Key.BN149},Mod2002014Key.BN147.getDescription())
	 ,C0024(new Mod2002014Key[]{Mod2002014Key.BN240,Mod2002014Key.BN241,Mod2002014Key.BN242},Mod2002014Key.BN240.getDescription())
	 ,C0025(new Mod2002014Key[]{Mod2002014Key.BN791,Mod2002014Key.BN802,Mod2002014Key.BN806},Mod2002014Key.BN791.getDescription())
	 ,C0026(new Mod2002014Key[]{Mod2002014Key.BN852,Mod2002014Key.BN853,Mod2002014Key.BN856},"Activos fijos (Ley 20/1991) 2013")
	 ,C0027(new Mod2002014Key[]{Mod2002014Key.BN886,null           ,Mod2002014Key.BN887},"Total deducciones inversiones en Canarias (Ley 20/1991)")
	;
	 
    private String description;
    
    private Mod2002014Key[] keys;
    
	private Mod2002014BN590Key(Mod2002014Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002014Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

