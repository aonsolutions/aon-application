package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public interface IMod123KeyDAO {
	
	Mod123Key getKey();
	boolean acceptValue(Mod123 mod,IrpfBreakdown  br);
	void initialize(AONContext ctx,Mod123 mod,Map<Mod123Key,Set<String>> docs,IrpfBreakdown  br);
	void uniqueInitialize(AONContext ctx,Mod123 mod);
	String getExpression();
	String getTemplate();
	
}

