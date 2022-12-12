package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
public enum Mod2002022LM1535Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002022Key[]{Mod2002022Key.LM1524 ,null                 ,Mod2002022Key.LM1525 ,Mod2002022Key.LM1526 ,null                 ,Mod2002022Key.LM1527 ,null                 ,Mod2002022Key.LM1528},"2007 y anteriores")
	,C02(new Mod2002022Key[]{Mod2002022Key.LM1529 ,Mod2002022Key.LM1530 ,Mod2002022Key.LM1590 ,Mod2002022Key.LM1591 ,Mod2002022Key.LM1531 ,Mod2002022Key.LM1532 ,Mod2002022Key.LM1533 ,Mod2002022Key.LM1534},"2008 a 2015")
	,C03(new Mod2002022Key[]{Mod2002022Key.LM1535 ,null                 ,Mod2002022Key.LM1536 ,Mod2002022Key.LM1537 ,Mod2002022Key.LM1538 ,Mod2002022Key.LM1539 ,Mod2002022Key.LM1540 ,Mod2002022Key.LM1541},"Total")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LM1535Key(Mod2002022Key[] keys, String description) {
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

