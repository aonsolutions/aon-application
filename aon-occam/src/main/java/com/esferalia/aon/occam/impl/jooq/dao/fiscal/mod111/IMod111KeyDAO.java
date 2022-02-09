package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public interface IMod111KeyDAO {
	
	Mod111Key getKey();
	boolean acceptValue(Mod111 mod,IrpfBreakdown  br);
	void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br);
	void uniqueInitialize(AONContext ctx,Mod111 mod);
	String getExpression();
	
}

