package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public interface IMod115KeyDAO {
	
	Mod115Key getKey();
	boolean acceptValue(Mod115 mod,IrpfBreakdown  br);
	void initialize(AONContext ctx,Mod115 mod,Map<Mod115Key,Set<String>> docs,Map<Mod115Key,Set<String>> pdocs,IrpfBreakdown  br);
	void uniqueInitialize(AONContext ctx,Mod115 mod);
	String getExpression();
	String getTemplate();
	
}

