package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;


public enum Mod200BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	  C0001(new Mod200Key[]{Mod200Key.BN854,Mod200Key.BN855,null            },"Activos fijos (Ley 20/1991) 2008")
	 ,C0002(new Mod200Key[]{Mod200Key.BN857,Mod200Key.BN858,Mod200Key.BN859},"Activos fijos (Ley 20/1991) 2009")
	 ,C0003(new Mod200Key[]{Mod200Key.BN860,Mod200Key.BN861,Mod200Key.BN862},"Activos fijos (Ley 20/1991) 2010")
	 ,C0004(new Mod200Key[]{Mod200Key.BN863,Mod200Key.BN864,Mod200Key.BN865},"Activos fijos (Ley 20/1991) 2011")
	 ,C0005(new Mod200Key[]{Mod200Key.BN883,Mod200Key.BN884,Mod200Key.BN885},"Activos fijos (Ley 20/1991) 2012")
	 ,C0006(new Mod200Key[]{Mod200Key.BN088,Mod200Key.BN564,Mod200Key.BN801},"Inversiones en Canarias (Ley 20/1991) 1997")
	 ,C0007(new Mod200Key[]{Mod200Key.BN194,Mod200Key.BN195,Mod200Key.BN196},"Inversiones en Canarias (Ley 20/1991) 1998")
	 ,C0008(new Mod200Key[]{Mod200Key.BN868,Mod200Key.BN869,Mod200Key.BN834},"Inversiones en Canarias (Ley 20/1991) 1999")
	 ,C0009(new Mod200Key[]{Mod200Key.BN871,Mod200Key.BN872,Mod200Key.BN873},"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C0010(new Mod200Key[]{Mod200Key.BN874,Mod200Key.BN875,Mod200Key.BN876},"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C0011(new Mod200Key[]{Mod200Key.BN877,Mod200Key.BN878,Mod200Key.BN879},"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C0012(new Mod200Key[]{Mod200Key.BN880,Mod200Key.BN881,Mod200Key.BN882},"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C0013(new Mod200Key[]{Mod200Key.BN866,Mod200Key.BN867,Mod200Key.BN870},"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C0014(new Mod200Key[]{Mod200Key.BN939,Mod200Key.BN940,Mod200Key.BN941},"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C0015(new Mod200Key[]{Mod200Key.BN191,Mod200Key.BN192,Mod200Key.BN193},"Inversiones en Canarias (Ley 20/1991) 2006") 
	 ,C0016(new Mod200Key[]{Mod200Key.BN613,Mod200Key.BN614,Mod200Key.BN701},"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C0017(new Mod200Key[]{Mod200Key.BN200,Mod200Key.BN257,Mod200Key.BN011},"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C0018(new Mod200Key[]{Mod200Key.BN037,Mod200Key.BN038,Mod200Key.BN039},"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C0019(new Mod200Key[]{Mod200Key.BN044,Mod200Key.BN045,Mod200Key.BN046},"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C0020(new Mod200Key[]{Mod200Key.BN528,Mod200Key.BN529,Mod200Key.BN530},"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C0021(new Mod200Key[]{Mod200Key.BN144,Mod200Key.BN145,Mod200Key.BN146},"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C0022(new Mod200Key[]{Mod200Key.BN147,Mod200Key.BN148,Mod200Key.BN149},"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C0023(new Mod200Key[]{Mod200Key.BN852,Mod200Key.BN853,Mod200Key.BN856},"Activos fijos (Ley 20/1991) 2013")
	 ,C0024(new Mod200Key[]{Mod200Key.BN886,null           ,Mod200Key.BN887},"Total deducciones inversiones en Canarias (Ley 20/1991)")
	;
	 
    private String description;
    
    private Mod200Key[] keys;
    
	private Mod200BN590Key(Mod200Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod200Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

