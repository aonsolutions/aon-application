package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public interface IMod202KeyDAO {
	
	Mod202Key getKey();
	boolean accept(Mod202 mod);
	void initialize(AONContext ctx,Mod202 mod);
	String getExpression();
	String info(AONContext ctx,Mod202 mod);
	
}

