package com.esferalia.aon.occam.api.model.fiscal.mod200_2013;

import java.io.Serializable;


public enum Mod2002013BN590Key implements Serializable, IMod200KeysProvider {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	  C0001(new Mod2002013Key[]{Mod2002013Key.BN854,Mod2002013Key.BN855,null            },"Activos fijos (Ley 20/1991) 2008")
	 ,C0002(new Mod2002013Key[]{Mod2002013Key.BN857,Mod2002013Key.BN858,Mod2002013Key.BN859},"Activos fijos (Ley 20/1991) 2009")
	 ,C0003(new Mod2002013Key[]{Mod2002013Key.BN860,Mod2002013Key.BN861,Mod2002013Key.BN862},"Activos fijos (Ley 20/1991) 2010")
	 ,C0004(new Mod2002013Key[]{Mod2002013Key.BN863,Mod2002013Key.BN864,Mod2002013Key.BN865},"Activos fijos (Ley 20/1991) 2011")
	 ,C0005(new Mod2002013Key[]{Mod2002013Key.BN883,Mod2002013Key.BN884,Mod2002013Key.BN885},"Activos fijos (Ley 20/1991) 2012")
	 ,C0006(new Mod2002013Key[]{Mod2002013Key.BN088,Mod2002013Key.BN564,Mod2002013Key.BN801},"Inversiones en Canarias (Ley 20/1991) 1997")
	 ,C0007(new Mod2002013Key[]{Mod2002013Key.BN194,Mod2002013Key.BN195,Mod2002013Key.BN196},"Inversiones en Canarias (Ley 20/1991) 1998")
	 ,C0008(new Mod2002013Key[]{Mod2002013Key.BN868,Mod2002013Key.BN869,Mod2002013Key.BN834},"Inversiones en Canarias (Ley 20/1991) 1999")
	 ,C0009(new Mod2002013Key[]{Mod2002013Key.BN871,Mod2002013Key.BN872,Mod2002013Key.BN873},"Inversiones en Canarias (Ley 20/1991) 2000")
	 ,C0010(new Mod2002013Key[]{Mod2002013Key.BN874,Mod2002013Key.BN875,Mod2002013Key.BN876},"Inversiones en Canarias (Ley 20/1991) 2001")
	 ,C0011(new Mod2002013Key[]{Mod2002013Key.BN877,Mod2002013Key.BN878,Mod2002013Key.BN879},"Inversiones en Canarias (Ley 20/1991) 2002")
	 ,C0012(new Mod2002013Key[]{Mod2002013Key.BN880,Mod2002013Key.BN881,Mod2002013Key.BN882},"Inversiones en Canarias (Ley 20/1991) 2003")
	 ,C0013(new Mod2002013Key[]{Mod2002013Key.BN866,Mod2002013Key.BN867,Mod2002013Key.BN870},"Inversiones en Canarias (Ley 20/1991) 2004")
	 ,C0014(new Mod2002013Key[]{Mod2002013Key.BN939,Mod2002013Key.BN940,Mod2002013Key.BN941},"Inversiones en Canarias (Ley 20/1991) 2005")
	 ,C0015(new Mod2002013Key[]{Mod2002013Key.BN191,Mod2002013Key.BN192,Mod2002013Key.BN193},"Inversiones en Canarias (Ley 20/1991) 2006") 
	 ,C0016(new Mod2002013Key[]{Mod2002013Key.BN613,Mod2002013Key.BN614,Mod2002013Key.BN701},"Inversiones en Canarias (Ley 20/1991) 2007")
	 ,C0017(new Mod2002013Key[]{Mod2002013Key.BN200,Mod2002013Key.BN257,Mod2002013Key.BN011},"Inversiones en Canarias (Ley 20/1991) 2008")
	 ,C0018(new Mod2002013Key[]{Mod2002013Key.BN037,Mod2002013Key.BN038,Mod2002013Key.BN039},"Inversiones en Canarias (Ley 20/1991) 2009")
	 ,C0019(new Mod2002013Key[]{Mod2002013Key.BN044,Mod2002013Key.BN045,Mod2002013Key.BN046},"Inversiones en Canarias (Ley 20/1991) 2010")
	 ,C0020(new Mod2002013Key[]{Mod2002013Key.BN528,Mod2002013Key.BN529,Mod2002013Key.BN530},"Inversiones en Canarias (Ley 20/1991) 2011")
	 ,C0021(new Mod2002013Key[]{Mod2002013Key.BN144,Mod2002013Key.BN145,Mod2002013Key.BN146},"Inversiones en Canarias (Ley 20/1991) 2012")
	 ,C0022(new Mod2002013Key[]{Mod2002013Key.BN147,Mod2002013Key.BN148,Mod2002013Key.BN149},"Inversiones en Canarias (Ley 20/1991) 2013")
	 ,C0023(new Mod2002013Key[]{Mod2002013Key.BN852,Mod2002013Key.BN853,Mod2002013Key.BN856},"Activos fijos (Ley 20/1991) 2013")
	 ,C0024(new Mod2002013Key[]{Mod2002013Key.BN886,null           ,Mod2002013Key.BN887},"Total deducciones inversiones en Canarias (Ley 20/1991)")
	;
	 
    private String description;
    
    private Mod2002013Key[] keys;
    
	private Mod2002013BN590Key(Mod2002013Key[] keys, String description) {
	    this.keys = keys;
		this.description = description;
	}
	
	public Mod2002013Key[] getKeys() {
		return keys;
	}
	public String getDescription() {
		return description;
	}
}

