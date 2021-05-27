package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014LQ561Key implements Serializable, IMod200KeysProvider  {

	 C0001(new Mod2002014Key[]{Mod2002014Key.LQ673,Mod2002014Key.LQ674,null               },Mod2002014Key.LQ673.getDescription())
	,C0002(new Mod2002014Key[]{Mod2002014Key.LQ676,Mod2002014Key.LQ677,Mod2002014Key.LQ678},Mod2002014Key.LQ676.getDescription())
	,C0003(new Mod2002014Key[]{Mod2002014Key.LQ679,Mod2002014Key.LQ680,Mod2002014Key.LQ681},Mod2002014Key.LQ679.getDescription())
	,C0004(new Mod2002014Key[]{Mod2002014Key.LQ682,Mod2002014Key.LQ683,Mod2002014Key.LQ684},Mod2002014Key.LQ682.getDescription())
	,C0005(new Mod2002014Key[]{Mod2002014Key.LQ685,Mod2002014Key.LQ686,Mod2002014Key.LQ687},Mod2002014Key.LQ685.getDescription())
	,C0006(new Mod2002014Key[]{Mod2002014Key.LQ688,Mod2002014Key.LQ689,Mod2002014Key.LQ690},Mod2002014Key.LQ688.getDescription())
	,C0007(new Mod2002014Key[]{Mod2002014Key.LQ691,Mod2002014Key.LQ692,Mod2002014Key.LQ693},Mod2002014Key.LQ691.getDescription())
	,C0008(new Mod2002014Key[]{Mod2002014Key.LQ623,Mod2002014Key.LQ624,Mod2002014Key.LQ672},Mod2002014Key.LQ623.getDescription())
	,C0009(new Mod2002014Key[]{Mod2002014Key.LQ279,Mod2002014Key.LQ280,Mod2002014Key.LQ281},Mod2002014Key.LQ279.getDescription())
	,C0010(new Mod2002014Key[]{Mod2002014Key.LQ587,Mod2002014Key.LQ515,Mod2002014Key.LQ900},Mod2002014Key.LQ587.getDescription())
	,C0011(new Mod2002014Key[]{Mod2002014Key.LQ059,Mod2002014Key.LQ099,Mod2002014Key.LQ100},Mod2002014Key.LQ059.getDescription())
	,C0012(new Mod2002014Key[]{Mod2002014Key.LQ017,Mod2002014Key.LQ018,Mod2002014Key.LQ019},Mod2002014Key.LQ017.getDescription())
	,C0013(new Mod2002014Key[]{Mod2002014Key.LQ772,Mod2002014Key.LQ773,Mod2002014Key.LQ777},Mod2002014Key.LQ772.getDescription())
	,C0014(new Mod2002014Key[]{Mod2002014Key.LQ907,Mod2002014Key.LQ908,Mod2002014Key.LQ909},Mod2002014Key.LQ907.getDescription())
	,C0015(new Mod2002014Key[]{Mod2002014Key.LQ910,Mod2002014Key.LQ911,Mod2002014Key.LQ912},Mod2002014Key.LQ910.getDescription())
	,C0016(new Mod2002014Key[]{Mod2002014Key.LQ935,Mod2002014Key.LQ936,Mod2002014Key.LQ937},Mod2002014Key.LQ935.getDescription())
	,C0017(new Mod2002014Key[]{Mod2002014Key.LQ694,null 			  ,Mod2002014Key.LQ695},Mod2002014Key.LQ694.getDescription())
	;
	 
    private String description;
    private Mod2002014Key[] keys;

	private Mod2002014LQ561Key(Mod2002014Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002014Key[] getKeys() {
		return keys; 
	}
}

