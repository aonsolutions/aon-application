package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public interface IMod130KeyDAO {
	
	Mod130Key getKey();
	boolean acceptValue(Mod130 mod,IrpfBreakdown  br);
	void initialize(AONContext ctx,Mod130 mod);
	String getExpression();
	String info(AONContext ctx,Mod130 mod);
	
	
}

